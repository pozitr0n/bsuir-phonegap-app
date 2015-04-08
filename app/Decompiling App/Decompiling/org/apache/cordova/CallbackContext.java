package org.apache.cordova;

import org.json.JSONArray;
import org.json.JSONObject;

public class CallbackContext
{
  private static final String LOG_TAG = "CordovaPlugin";
  private String callbackId;
  private int changingThreads;
  private boolean finished;
  private CordovaWebView webView;
  
  public CallbackContext(String paramString, CordovaWebView paramCordovaWebView)
  {
    this.callbackId = paramString;
    this.webView = paramCordovaWebView;
  }
  
  public void error(int paramInt)
  {
    sendPluginResult(new PluginResult(PluginResult.Status.ERROR, paramInt));
  }
  
  public void error(String paramString)
  {
    sendPluginResult(new PluginResult(PluginResult.Status.ERROR, paramString));
  }
  
  public void error(JSONObject paramJSONObject)
  {
    sendPluginResult(new PluginResult(PluginResult.Status.ERROR, paramJSONObject));
  }
  
  public String getCallbackId()
  {
    return this.callbackId;
  }
  
  public boolean isChangingThreads()
  {
    return this.changingThreads > 0;
  }
  
  public boolean isFinished()
  {
    return this.finished;
  }
  
  /* Error */
  public void sendPluginResult(PluginResult paramPluginResult)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 58	org/apache/cordova/CallbackContext:finished	Z
    //   6: ifeq +46 -> 52
    //   9: ldc 8
    //   11: new 60	java/lang/StringBuilder
    //   14: dup
    //   15: invokespecial 61	java/lang/StringBuilder:<init>	()V
    //   18: ldc 63
    //   20: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   23: aload_0
    //   24: getfield 22	org/apache/cordova/CallbackContext:callbackId	Ljava/lang/String;
    //   27: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   30: ldc 69
    //   32: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   35: aload_1
    //   36: invokevirtual 72	org/apache/cordova/PluginResult:getMessage	()Ljava/lang/String;
    //   39: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   42: invokevirtual 75	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   45: invokestatic 81	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   48: pop
    //   49: aload_0
    //   50: monitorexit
    //   51: return
    //   52: aload_1
    //   53: invokevirtual 84	org/apache/cordova/PluginResult:getKeepCallback	()Z
    //   56: ifne +25 -> 81
    //   59: iconst_1
    //   60: istore_3
    //   61: aload_0
    //   62: iload_3
    //   63: putfield 58	org/apache/cordova/CallbackContext:finished	Z
    //   66: aload_0
    //   67: monitorexit
    //   68: aload_0
    //   69: getfield 24	org/apache/cordova/CallbackContext:webView	Lorg/apache/cordova/CordovaWebView;
    //   72: aload_1
    //   73: aload_0
    //   74: getfield 22	org/apache/cordova/CallbackContext:callbackId	Ljava/lang/String;
    //   77: invokevirtual 89	org/apache/cordova/CordovaWebView:sendPluginResult	(Lorg/apache/cordova/PluginResult;Ljava/lang/String;)V
    //   80: return
    //   81: iconst_0
    //   82: istore_3
    //   83: goto -22 -> 61
    //   86: astore_2
    //   87: aload_0
    //   88: monitorexit
    //   89: aload_2
    //   90: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	91	0	this	CallbackContext
    //   0	91	1	paramPluginResult	PluginResult
    //   86	4	2	localObject	Object
    //   60	23	3	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	51	86	finally
    //   52	59	86	finally
    //   61	68	86	finally
    //   87	89	86	finally
  }
  
  public void success()
  {
    sendPluginResult(new PluginResult(PluginResult.Status.OK));
  }
  
  public void success(int paramInt)
  {
    sendPluginResult(new PluginResult(PluginResult.Status.OK, paramInt));
  }
  
  public void success(String paramString)
  {
    sendPluginResult(new PluginResult(PluginResult.Status.OK, paramString));
  }
  
  public void success(JSONArray paramJSONArray)
  {
    sendPluginResult(new PluginResult(PluginResult.Status.OK, paramJSONArray));
  }
  
  public void success(JSONObject paramJSONObject)
  {
    sendPluginResult(new PluginResult(PluginResult.Status.OK, paramJSONObject));
  }
  
  public void success(byte[] paramArrayOfByte)
  {
    sendPluginResult(new PluginResult(PluginResult.Status.OK, paramArrayOfByte));
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.CallbackContext
 * JD-Core Version:    0.7.0.1
 */