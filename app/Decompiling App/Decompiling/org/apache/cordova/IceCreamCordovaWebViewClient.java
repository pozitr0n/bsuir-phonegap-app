package org.apache.cordova;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build.VERSION;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import java.io.FileNotFoundException;
import java.io.IOException;

@TargetApi(11)
public class IceCreamCordovaWebViewClient
  extends CordovaWebViewClient
{
  private static final String TAG = "IceCreamCordovaWebViewClient";
  
  public IceCreamCordovaWebViewClient(CordovaInterface paramCordovaInterface)
  {
    super(paramCordovaInterface);
  }
  
  public IceCreamCordovaWebViewClient(CordovaInterface paramCordovaInterface, CordovaWebView paramCordovaWebView)
  {
    super(paramCordovaInterface, paramCordovaWebView);
  }
  
  private static boolean needsSpecialsInAssetUrlFix(Uri paramUri)
  {
    if (CordovaResourceApi.getUriType(paramUri) != 1) {}
    do
    {
      return false;
      if ((paramUri.getQuery() != null) || (paramUri.getFragment() != null)) {
        return true;
      }
    } while (!paramUri.toString().contains("%"));
    switch (Build.VERSION.SDK_INT)
    {
    default: 
      return false;
    }
    return true;
  }
  
  public WebResourceResponse shouldInterceptRequest(WebView paramWebView, String paramString)
  {
    WebResourceResponse localWebResourceResponse1;
    try
    {
      if (((paramString.startsWith("http:")) || (paramString.startsWith("https:"))) && (!Config.isUrlWhiteListed(paramString)))
      {
        LOG.w("IceCreamCordovaWebViewClient", "URL blocked by whitelist: " + paramString);
        return new WebResourceResponse("text/plain", "UTF-8", null);
      }
      CordovaResourceApi localCordovaResourceApi = this.appView.getResourceApi();
      Uri localUri1 = Uri.parse(paramString);
      Uri localUri2 = localCordovaResourceApi.remapUri(localUri1);
      if (localUri1.equals(localUri2))
      {
        boolean bool = needsSpecialsInAssetUrlFix(localUri1);
        localWebResourceResponse1 = null;
        if (!bool) {}
      }
      else
      {
        CordovaResourceApi.OpenForReadResult localOpenForReadResult = localCordovaResourceApi.openForRead(localUri2, true);
        WebResourceResponse localWebResourceResponse2 = new WebResourceResponse(localOpenForReadResult.mimeType, "UTF-8", localOpenForReadResult.inputStream);
        return localWebResourceResponse2;
      }
    }
    catch (IOException localIOException)
    {
      if (!(localIOException instanceof FileNotFoundException)) {
        LOG.e("IceCreamCordovaWebViewClient", "Error occurred while loading a file (returning a 404).", localIOException);
      }
      localWebResourceResponse1 = new WebResourceResponse("text/plain", "UTF-8", null);
    }
    return localWebResourceResponse1;
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.IceCreamCordovaWebViewClient
 * JD-Core Version:    0.7.0.1
 */