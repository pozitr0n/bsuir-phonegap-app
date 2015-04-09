package org.apache.cordova;

import android.app.Activity;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class App
  extends CordovaPlugin
{
  public void backHistory()
  {
    this.cordova.getActivity().runOnUiThread(new Runnable()
    {
      public void run()
      {
        App.this.webView.backHistory();
      }
    });
  }
  
  public void clearCache()
  {
    this.cordova.getActivity().runOnUiThread(new Runnable()
    {
      public void run()
      {
        App.this.webView.clearCache(true);
      }
    });
  }
  
  public void clearHistory()
  {
    this.webView.clearHistory();
  }
  
  public boolean execute(String paramString, JSONArray paramJSONArray, CallbackContext paramCallbackContext)
    throws JSONException
  {
    PluginResult.Status localStatus = PluginResult.Status.OK;
    for (;;)
    {
      try
      {
        if (paramString.equals("clearCache"))
        {
          clearCache();
          paramCallbackContext.sendPluginResult(new PluginResult(localStatus, ""));
          return true;
        }
        if (paramString.equals("show"))
        {
          this.cordova.getActivity().runOnUiThread(new Runnable()
          {
            public void run()
            {
              App.this.webView.postMessage("spinner", "stop");
            }
          });
          continue;
        }
        if (!paramString.equals("loadUrl")) {
          break label111;
        }
      }
      catch (JSONException localJSONException)
      {
        paramCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
        return false;
      }
      loadUrl(paramJSONArray.getString(0), paramJSONArray.optJSONObject(1));
      continue;
      label111:
      if (!paramString.equals("cancelLoadUrl")) {
        if (paramString.equals("clearHistory")) {
          clearHistory();
        } else if (paramString.equals("backHistory")) {
          backHistory();
        } else if (paramString.equals("overrideButton")) {
          overrideButton(paramJSONArray.getString(0), paramJSONArray.getBoolean(1));
        } else if (paramString.equals("overrideBackbutton")) {
          overrideBackbutton(paramJSONArray.getBoolean(0));
        } else if (paramString.equals("exitApp")) {
          exitApp();
        }
      }
    }
  }
  
  public void exitApp()
  {
    this.webView.postMessage("exit", null);
  }
  
  public boolean isBackbuttonOverridden()
  {
    return this.webView.isBackButtonBound();
  }
  
  public void loadUrl(String paramString, JSONObject paramJSONObject)
    throws JSONException
  {
    LOG.d("App", "App.loadUrl(" + paramString + "," + paramJSONObject + ")");
    HashMap localHashMap = new HashMap();
    boolean bool1 = false;
    boolean bool2 = false;
    int i = 0;
    if (paramJSONObject != null)
    {
      JSONArray localJSONArray = paramJSONObject.names();
      int j = 0;
      if (j < localJSONArray.length())
      {
        String str = localJSONArray.getString(j);
        if (str.equals("wait")) {
          i = paramJSONObject.getInt(str);
        }
        for (;;)
        {
          j++;
          break;
          if (str.equalsIgnoreCase("openexternal"))
          {
            bool2 = paramJSONObject.getBoolean(str);
          }
          else if (str.equalsIgnoreCase("clearhistory"))
          {
            bool1 = paramJSONObject.getBoolean(str);
          }
          else
          {
            Object localObject2 = paramJSONObject.get(str);
            if (localObject2 != null) {
              if (localObject2.getClass().equals(String.class)) {
                localHashMap.put(str, (String)localObject2);
              } else if (localObject2.getClass().equals(Boolean.class)) {
                localHashMap.put(str, (Boolean)localObject2);
              } else if (localObject2.getClass().equals(Integer.class)) {
                localHashMap.put(str, (Integer)localObject2);
              }
            }
          }
        }
      }
    }
    if (i > 0) {}
    for (;;)
    {
      try
      {
        l = i;
      }
      catch (InterruptedException localInterruptedException)
      {
        long l;
        localInterruptedException.printStackTrace();
        continue;
      }
      try
      {
        wait(l);
        this.webView.showWebPage(paramString, bool2, bool1, localHashMap);
        return;
      }
      finally {}
    }
  }
  
  public void overrideBackbutton(boolean paramBoolean)
  {
    LOG.i("App", "WARNING: Back Button Default Behaviour will be overridden.  The backbutton event will be fired!");
    this.webView.bindButton(paramBoolean);
  }
  
  public void overrideButton(String paramString, boolean paramBoolean)
  {
    LOG.i("App", "WARNING: Volume Button Default Behaviour will be overridden.  The volume event will be fired!");
    this.webView.bindButton(paramString, paramBoolean);
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.App
 * JD-Core Version:    0.7.0.1
 */