package org.apache.cordova;

import android.webkit.JavascriptInterface;
import org.json.JSONException;

class ExposedJsApi
{
  private NativeToJsMessageQueue jsMessageQueue;
  private PluginManager pluginManager;
  
  public ExposedJsApi(PluginManager paramPluginManager, NativeToJsMessageQueue paramNativeToJsMessageQueue)
  {
    this.pluginManager = paramPluginManager;
    this.jsMessageQueue = paramNativeToJsMessageQueue;
  }
  
  @JavascriptInterface
  public String exec(String paramString1, String paramString2, String paramString3, String paramString4)
    throws JSONException
  {
    if (paramString4 == null) {
      return "@Null arguments.";
    }
    this.jsMessageQueue.setPaused(true);
    try
    {
      CordovaResourceApi.jsThread = Thread.currentThread();
      this.pluginManager.exec(paramString1, paramString2, paramString3, paramString4);
      String str = this.jsMessageQueue.popAndEncode(false);
      return str;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
      return "";
    }
    finally
    {
      this.jsMessageQueue.setPaused(false);
    }
  }
  
  @JavascriptInterface
  public String retrieveJsMessages(boolean paramBoolean)
  {
    return this.jsMessageQueue.popAndEncode(paramBoolean);
  }
  
  @JavascriptInterface
  public void setNativeToJsBridgeMode(int paramInt)
  {
    this.jsMessageQueue.setBridgeMode(paramInt);
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.ExposedJsApi
 * JD-Core Version:    0.7.0.1
 */