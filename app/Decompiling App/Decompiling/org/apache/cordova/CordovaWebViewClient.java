package org.apache.cordova;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.Hashtable;
import org.json.JSONException;
import org.json.JSONObject;

public class CordovaWebViewClient
  extends WebViewClient
{
  private static final String CORDOVA_EXEC_URL_PREFIX = "http://cdv_exec/";
  private static final String TAG = "CordovaWebViewClient";
  CordovaWebView appView;
  private Hashtable<String, AuthenticationToken> authenticationTokens = new Hashtable();
  CordovaInterface cordova;
  private boolean doClearHistory = false;
  
  public CordovaWebViewClient(CordovaInterface paramCordovaInterface)
  {
    this.cordova = paramCordovaInterface;
  }
  
  public CordovaWebViewClient(CordovaInterface paramCordovaInterface, CordovaWebView paramCordovaWebView)
  {
    this.cordova = paramCordovaInterface;
    this.appView = paramCordovaWebView;
  }
  
  private void handleExecUrl(String paramString)
  {
    int i = "http://cdv_exec/".length();
    int j = paramString.indexOf('#', i + 1);
    int k = paramString.indexOf('#', j + 1);
    int m = paramString.indexOf('#', k + 1);
    if ((i == -1) || (j == -1) || (k == -1) || (m == -1))
    {
      Log.e("CordovaWebViewClient", "Could not decode URL command: " + paramString);
      return;
    }
    String str1 = paramString.substring(i, j);
    String str2 = paramString.substring(j + 1, k);
    String str3 = paramString.substring(k + 1, m);
    String str4 = paramString.substring(m + 1);
    this.appView.pluginManager.exec(str1, str2, str3, str4);
  }
  
  public void clearAuthenticationTokens()
  {
    this.authenticationTokens.clear();
  }
  
  public AuthenticationToken getAuthenticationToken(String paramString1, String paramString2)
  {
    AuthenticationToken localAuthenticationToken = (AuthenticationToken)this.authenticationTokens.get(paramString1.concat(paramString2));
    if (localAuthenticationToken == null)
    {
      localAuthenticationToken = (AuthenticationToken)this.authenticationTokens.get(paramString1);
      if (localAuthenticationToken == null) {
        localAuthenticationToken = (AuthenticationToken)this.authenticationTokens.get(paramString2);
      }
      if (localAuthenticationToken == null) {
        localAuthenticationToken = (AuthenticationToken)this.authenticationTokens.get("");
      }
    }
    return localAuthenticationToken;
  }
  
  public void onPageFinished(WebView paramWebView, String paramString)
  {
    super.onPageFinished(paramWebView, paramString);
    LOG.d("CordovaWebViewClient", "onPageFinished(" + paramString + ")");
    if (this.doClearHistory)
    {
      paramWebView.clearHistory();
      this.doClearHistory = false;
    }
    CordovaWebView localCordovaWebView = this.appView;
    localCordovaWebView.loadUrlTimeout = (1 + localCordovaWebView.loadUrlTimeout);
    this.appView.postMessage("onPageFinished", paramString);
    if (this.appView.getVisibility() == 4) {
      new Thread(new Runnable()
      {
        public void run()
        {
          try
          {
            Thread.sleep(2000L);
            CordovaWebViewClient.this.cordova.getActivity().runOnUiThread(new Runnable()
            {
              public void run()
              {
                CordovaWebViewClient.this.appView.postMessage("spinner", "stop");
              }
            });
            return;
          }
          catch (InterruptedException localInterruptedException) {}
        }
      }).start();
    }
    if (paramString.equals("about:blank")) {
      this.appView.postMessage("exit", null);
    }
  }
  
  public void onPageStarted(WebView paramWebView, String paramString, Bitmap paramBitmap)
  {
    this.appView.jsMessageQueue.reset();
    this.appView.postMessage("onPageStarted", paramString);
    if (this.appView.pluginManager != null) {
      this.appView.pluginManager.onReset();
    }
  }
  
  public void onReceivedError(WebView paramWebView, int paramInt, String paramString1, String paramString2)
  {
    Object[] arrayOfObject = new Object[3];
    arrayOfObject[0] = Integer.valueOf(paramInt);
    arrayOfObject[1] = paramString1;
    arrayOfObject[2] = paramString2;
    LOG.d("CordovaWebViewClient", "CordovaWebViewClient.onReceivedError: Error code=%s Description=%s URL=%s", arrayOfObject);
    CordovaWebView localCordovaWebView = this.appView;
    localCordovaWebView.loadUrlTimeout = (1 + localCordovaWebView.loadUrlTimeout);
    JSONObject localJSONObject = new JSONObject();
    try
    {
      localJSONObject.put("errorCode", paramInt);
      localJSONObject.put("description", paramString1);
      localJSONObject.put("url", paramString2);
      this.appView.postMessage("onReceivedError", localJSONObject);
      return;
    }
    catch (JSONException localJSONException)
    {
      for (;;)
      {
        localJSONException.printStackTrace();
      }
    }
  }
  
  public void onReceivedHttpAuthRequest(WebView paramWebView, HttpAuthHandler paramHttpAuthHandler, String paramString1, String paramString2)
  {
    AuthenticationToken localAuthenticationToken = getAuthenticationToken(paramString1, paramString2);
    if (localAuthenticationToken != null)
    {
      paramHttpAuthHandler.proceed(localAuthenticationToken.getUserName(), localAuthenticationToken.getPassword());
      return;
    }
    super.onReceivedHttpAuthRequest(paramWebView, paramHttpAuthHandler, paramString1, paramString2);
  }
  
  @TargetApi(8)
  public void onReceivedSslError(WebView paramWebView, SslErrorHandler paramSslErrorHandler, SslError paramSslError)
  {
    String str = this.cordova.getActivity().getPackageName();
    PackageManager localPackageManager = this.cordova.getActivity().getPackageManager();
    try
    {
      if ((0x2 & localPackageManager.getApplicationInfo(str, 128).flags) != 0)
      {
        paramSslErrorHandler.proceed();
        return;
      }
      super.onReceivedSslError(paramWebView, paramSslErrorHandler, paramSslError);
      return;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      super.onReceivedSslError(paramWebView, paramSslErrorHandler, paramSslError);
    }
  }
  
  public AuthenticationToken removeAuthenticationToken(String paramString1, String paramString2)
  {
    return (AuthenticationToken)this.authenticationTokens.remove(paramString1.concat(paramString2));
  }
  
  public void setAuthenticationToken(AuthenticationToken paramAuthenticationToken, String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      paramString1 = "";
    }
    if (paramString2 == null) {
      paramString2 = "";
    }
    this.authenticationTokens.put(paramString1.concat(paramString2), paramAuthenticationToken);
  }
  
  public void setWebView(CordovaWebView paramCordovaWebView)
  {
    this.appView = paramCordovaWebView;
  }
  
  public boolean shouldOverrideUrlLoading(WebView paramWebView, String paramString)
  {
    if ((this.appView.pluginManager != null) && (this.appView.pluginManager.onOverrideUrlLoading(paramString))) {}
    for (;;)
    {
      return true;
      if (paramString.startsWith("tel:"))
      {
        try
        {
          Intent localIntent6 = new Intent("android.intent.action.DIAL");
          localIntent6.setData(Uri.parse(paramString));
          this.cordova.getActivity().startActivity(localIntent6);
        }
        catch (ActivityNotFoundException localActivityNotFoundException6)
        {
          LOG.e("CordovaWebViewClient", "Error dialing " + paramString + ": " + localActivityNotFoundException6.toString());
        }
      }
      else if (paramString.startsWith("geo:"))
      {
        try
        {
          Intent localIntent5 = new Intent("android.intent.action.VIEW");
          localIntent5.setData(Uri.parse(paramString));
          this.cordova.getActivity().startActivity(localIntent5);
        }
        catch (ActivityNotFoundException localActivityNotFoundException5)
        {
          LOG.e("CordovaWebViewClient", "Error showing map " + paramString + ": " + localActivityNotFoundException5.toString());
        }
      }
      else if (paramString.startsWith("mailto:"))
      {
        try
        {
          Intent localIntent4 = new Intent("android.intent.action.VIEW");
          localIntent4.setData(Uri.parse(paramString));
          this.cordova.getActivity().startActivity(localIntent4);
        }
        catch (ActivityNotFoundException localActivityNotFoundException4)
        {
          LOG.e("CordovaWebViewClient", "Error sending email " + paramString + ": " + localActivityNotFoundException4.toString());
        }
      }
      else
      {
        if (paramString.startsWith("sms:")) {
          for (;;)
          {
            Intent localIntent3;
            int i;
            try
            {
              localIntent3 = new Intent("android.intent.action.VIEW");
              i = paramString.indexOf('?');
              if (i != -1) {
                break label459;
              }
              str1 = paramString.substring(4);
              localIntent3.setData(Uri.parse("sms:" + str1));
              localIntent3.putExtra("address", str1);
              localIntent3.setType("vnd.android-dir/mms-sms");
              this.cordova.getActivity().startActivity(localIntent3);
            }
            catch (ActivityNotFoundException localActivityNotFoundException3)
            {
              LOG.e("CordovaWebViewClient", "Error sending sms " + paramString + ":" + localActivityNotFoundException3.toString());
            }
            break;
            label459:
            String str1 = paramString.substring(4, i);
            String str2 = Uri.parse(paramString).getQuery();
            if ((str2 != null) && (str2.startsWith("body="))) {
              localIntent3.putExtra("sms_body", str2.substring(5));
            }
          }
        }
        if (paramString.startsWith("market:"))
        {
          try
          {
            Intent localIntent2 = new Intent("android.intent.action.VIEW");
            localIntent2.setData(Uri.parse(paramString));
            this.cordova.getActivity().startActivity(localIntent2);
          }
          catch (ActivityNotFoundException localActivityNotFoundException2)
          {
            LOG.e("CordovaWebViewClient", "Error loading Google Play Store: " + paramString, localActivityNotFoundException2);
          }
        }
        else
        {
          if ((paramString.startsWith("file://")) || (paramString.startsWith("data:")) || (Config.isUrlWhiteListed(paramString))) {
            return false;
          }
          try
          {
            Intent localIntent1 = new Intent("android.intent.action.VIEW");
            localIntent1.setData(Uri.parse(paramString));
            this.cordova.getActivity().startActivity(localIntent1);
          }
          catch (ActivityNotFoundException localActivityNotFoundException1)
          {
            LOG.e("CordovaWebViewClient", "Error loading url " + paramString, localActivityNotFoundException1);
          }
        }
      }
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.CordovaWebViewClient
 * JD-Core Version:    0.7.0.1
 */