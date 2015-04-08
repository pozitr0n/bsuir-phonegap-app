package org.apache.cordova;

import android.app.Activity;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;

public class NativeToJsMessageQueue
{
  private static final int DEFAULT_BRIDGE_MODE = 2;
  static final boolean DISABLE_EXEC_CHAINING = false;
  static final boolean ENABLE_LOCATION_CHANGE_EXEC_MODE = false;
  private static final boolean FORCE_ENCODE_USING_EVAL = false;
  private static final String LOG_TAG = "JsMessageQueue";
  private static int MAX_PAYLOAD_SIZE = 524288000;
  private int activeListenerIndex;
  private final CordovaInterface cordova;
  private boolean paused;
  private final LinkedList<JsMessage> queue = new LinkedList();
  private final BridgeMode[] registeredListeners;
  private final CordovaWebView webView;
  
  public NativeToJsMessageQueue(CordovaWebView paramCordovaWebView, CordovaInterface paramCordovaInterface)
  {
    this.cordova = paramCordovaInterface;
    this.webView = paramCordovaWebView;
    this.registeredListeners = new BridgeMode[4];
    this.registeredListeners[0] = null;
    this.registeredListeners[1] = new LoadUrlBridgeMode(null);
    this.registeredListeners[2] = new OnlineEventsBridgeMode();
    this.registeredListeners[3] = new PrivateApiBridgeMode(null);
    reset();
  }
  
  private int calculatePackedMessageLength(JsMessage paramJsMessage)
  {
    int i = paramJsMessage.calculateEncodedLength();
    return 1 + (i + String.valueOf(i).length());
  }
  
  private void enqueueMessage(JsMessage paramJsMessage)
  {
    try
    {
      this.queue.add(paramJsMessage);
      if ((!this.paused) && (this.registeredListeners[this.activeListenerIndex] != null)) {
        this.registeredListeners[this.activeListenerIndex].onNativeToJsMessageAvailable();
      }
      return;
    }
    finally {}
  }
  
  private void packMessage(JsMessage paramJsMessage, StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append(paramJsMessage.calculateEncodedLength()).append(' ');
    paramJsMessage.encodeAsMessage(paramStringBuilder);
  }
  
  private String popAndEncodeAsJs()
  {
    int i;
    int j;
    int i2;
    int k;
    label91:
    int m;
    label99:
    StringBuilder localStringBuilder;
    int n;
    try
    {
      if (this.queue.size() == 0) {
        return null;
      }
      i = 0;
      j = 0;
      Iterator localIterator = this.queue.iterator();
      if (localIterator.hasNext())
      {
        i2 = 50 + ((JsMessage)localIterator.next()).calculateEncodedLength();
        if ((j <= 0) || (i + i2 <= MAX_PAYLOAD_SIZE) || (MAX_PAYLOAD_SIZE <= 0)) {
          break label241;
        }
      }
      if (j != this.queue.size()) {
        break label252;
      }
      k = 1;
      if (k == 0) {
        break label258;
      }
      m = 0;
      localStringBuilder = new StringBuilder(m + i);
      n = 0;
      label115:
      if (n < j)
      {
        JsMessage localJsMessage = (JsMessage)this.queue.removeFirst();
        if ((k != 0) && (n + 1 == j))
        {
          localJsMessage.encodeAsJsMessage(localStringBuilder);
        }
        else
        {
          localStringBuilder.append("try{");
          localJsMessage.encodeAsJsMessage(localStringBuilder);
          localStringBuilder.append("}finally{");
        }
      }
    }
    finally {}
    if (k == 0) {
      localStringBuilder.append("window.setTimeout(function(){cordova.require('cordova/plugin/android/polling').pollOnce();},0);");
    }
    for (;;)
    {
      int i1;
      if (i1 < j)
      {
        localStringBuilder.append('}');
        i1++;
      }
      else
      {
        String str = localStringBuilder.toString();
        return str;
        n++;
        break label115;
        label241:
        i += i2;
        j++;
        break;
        label252:
        k = 0;
        break label91;
        label258:
        m = 100;
        break label99;
        if (k != 0) {
          i1 = 1;
        } else {
          i1 = 0;
        }
      }
    }
  }
  
  public void addJavaScript(String paramString)
  {
    enqueueMessage(new JsMessage(paramString));
  }
  
  public void addPluginResult(PluginResult paramPluginResult, String paramString)
  {
    if (paramString == null)
    {
      Log.e("JsMessageQueue", "Got plugin result with no callbackId", new Throwable());
      return;
    }
    if (paramPluginResult.getStatus() == PluginResult.Status.NO_RESULT.ordinal()) {}
    for (int i = 1;; i = 0)
    {
      boolean bool = paramPluginResult.getKeepCallback();
      if ((i != 0) && (bool)) {
        break;
      }
      enqueueMessage(new JsMessage(paramPluginResult, paramString));
      return;
    }
  }
  
  public boolean getPaused()
  {
    return this.paused;
  }
  
  public String popAndEncode(boolean paramBoolean)
  {
    for (;;)
    {
      int i;
      int j;
      int m;
      try
      {
        this.registeredListeners[this.activeListenerIndex].notifyOfFlush(paramBoolean);
        if (this.queue.isEmpty()) {
          return null;
        }
        i = 0;
        j = 0;
        Iterator localIterator = this.queue.iterator();
        if (localIterator.hasNext())
        {
          m = calculatePackedMessageLength((JsMessage)localIterator.next());
          if ((j <= 0) || (i + m <= MAX_PAYLOAD_SIZE) || (MAX_PAYLOAD_SIZE <= 0)) {}
        }
        else
        {
          StringBuilder localStringBuilder = new StringBuilder(i);
          int k = 0;
          if (k < j)
          {
            packMessage((JsMessage)this.queue.removeFirst(), localStringBuilder);
            k++;
            continue;
          }
          if (!this.queue.isEmpty()) {
            localStringBuilder.append('*');
          }
          String str = localStringBuilder.toString();
          return str;
        }
      }
      finally {}
      i += m;
      j++;
    }
  }
  
  public void reset()
  {
    try
    {
      this.queue.clear();
      setBridgeMode(2);
      return;
    }
    finally {}
  }
  
  public void setBridgeMode(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.registeredListeners.length)) {
      Log.d("JsMessageQueue", "Invalid NativeToJsBridgeMode: " + paramInt);
    }
    while (paramInt == this.activeListenerIndex) {
      return;
    }
    Log.d("JsMessageQueue", "Set native->JS mode to " + paramInt);
    try
    {
      this.activeListenerIndex = paramInt;
      BridgeMode localBridgeMode = this.registeredListeners[paramInt];
      if ((!this.paused) && (!this.queue.isEmpty()) && (localBridgeMode != null)) {
        localBridgeMode.onNativeToJsMessageAvailable();
      }
      return;
    }
    finally {}
  }
  
  public void setPaused(boolean paramBoolean)
  {
    if ((this.paused) && (paramBoolean)) {
      Log.e("JsMessageQueue", "nested call to setPaused detected.", new Throwable());
    }
    this.paused = paramBoolean;
    if (!paramBoolean) {
      try
      {
        if ((!this.queue.isEmpty()) && (this.registeredListeners[this.activeListenerIndex] != null)) {
          this.registeredListeners[this.activeListenerIndex].onNativeToJsMessageAvailable();
        }
        return;
      }
      finally {}
    }
  }
  
  private abstract class BridgeMode
  {
    private BridgeMode() {}
    
    void notifyOfFlush(boolean paramBoolean) {}
    
    abstract void onNativeToJsMessageAvailable();
  }
  
  private static class JsMessage
  {
    final String jsPayloadOrCallbackId;
    final PluginResult pluginResult;
    
    JsMessage(String paramString)
    {
      if (paramString == null) {
        throw new NullPointerException();
      }
      this.jsPayloadOrCallbackId = paramString;
      this.pluginResult = null;
    }
    
    JsMessage(PluginResult paramPluginResult, String paramString)
    {
      if ((paramString == null) || (paramPluginResult == null)) {
        throw new NullPointerException();
      }
      this.jsPayloadOrCallbackId = paramString;
      this.pluginResult = paramPluginResult;
    }
    
    int calculateEncodedLength()
    {
      if (this.pluginResult == null) {
        return 1 + this.jsPayloadOrCallbackId.length();
      }
      int i = 1 + (1 + (2 + String.valueOf(this.pluginResult.getStatus()).length()) + this.jsPayloadOrCallbackId.length());
      switch (this.pluginResult.getMessageType())
      {
      case 2: 
      default: 
        return i + this.pluginResult.getMessage().length();
      case 4: 
      case 5: 
        return i + 1;
      case 3: 
        return i + (1 + this.pluginResult.getMessage().length());
      case 1: 
        return i + (1 + this.pluginResult.getStrMessage().length());
      case 7: 
        return i + (1 + this.pluginResult.getMessage().length());
      }
      return i + (1 + this.pluginResult.getMessage().length());
    }
    
    void encodeAsJsMessage(StringBuilder paramStringBuilder)
    {
      if (this.pluginResult == null)
      {
        paramStringBuilder.append(this.jsPayloadOrCallbackId);
        return;
      }
      int i = this.pluginResult.getStatus();
      if ((i == PluginResult.Status.OK.ordinal()) || (i == PluginResult.Status.NO_RESULT.ordinal())) {}
      for (boolean bool = true;; bool = false)
      {
        paramStringBuilder.append("cordova.callbackFromNative('").append(this.jsPayloadOrCallbackId).append("',").append(bool).append(",").append(i).append(",[").append(this.pluginResult.getMessage()).append("],").append(this.pluginResult.getKeepCallback()).append(");");
        return;
      }
    }
    
    void encodeAsMessage(StringBuilder paramStringBuilder)
    {
      if (this.pluginResult == null)
      {
        paramStringBuilder.append('J').append(this.jsPayloadOrCallbackId);
        return;
      }
      int i = this.pluginResult.getStatus();
      int j;
      label42:
      int k;
      label55:
      char c1;
      label77:
      StringBuilder localStringBuilder;
      if (i == PluginResult.Status.NO_RESULT.ordinal())
      {
        j = 1;
        if (i != PluginResult.Status.OK.ordinal()) {
          break label190;
        }
        k = 1;
        boolean bool = this.pluginResult.getKeepCallback();
        if ((j == 0) && (k == 0)) {
          break label196;
        }
        c1 = 'S';
        localStringBuilder = paramStringBuilder.append(c1);
        if (!bool) {
          break label203;
        }
      }
      label190:
      label196:
      label203:
      for (char c2 = '1';; c2 = '0')
      {
        localStringBuilder.append(c2).append(i).append(' ').append(this.jsPayloadOrCallbackId).append(' ');
        switch (this.pluginResult.getMessageType())
        {
        case 2: 
        default: 
          paramStringBuilder.append(this.pluginResult.getMessage());
          return;
          j = 0;
          break label42;
          k = 0;
          break label55;
          c1 = 'F';
          break label77;
        }
      }
      paramStringBuilder.append(this.pluginResult.getMessage().charAt(0));
      return;
      paramStringBuilder.append('N');
      return;
      paramStringBuilder.append('n').append(this.pluginResult.getMessage());
      return;
      paramStringBuilder.append('s');
      paramStringBuilder.append(this.pluginResult.getStrMessage());
      return;
      paramStringBuilder.append('S');
      paramStringBuilder.append(this.pluginResult.getMessage());
      return;
      paramStringBuilder.append('A');
      paramStringBuilder.append(this.pluginResult.getMessage());
    }
  }
  
  private class LoadUrlBridgeMode
    extends NativeToJsMessageQueue.BridgeMode
  {
    final Runnable runnable = new Runnable()
    {
      public void run()
      {
        String str = NativeToJsMessageQueue.this.popAndEncodeAsJs();
        if (str != null) {
          NativeToJsMessageQueue.this.webView.loadUrlNow("javascript:" + str);
        }
      }
    };
    
    private LoadUrlBridgeMode()
    {
      super(null);
    }
    
    void onNativeToJsMessageAvailable()
    {
      NativeToJsMessageQueue.this.cordova.getActivity().runOnUiThread(this.runnable);
    }
  }
  
  private class OnlineEventsBridgeMode
    extends NativeToJsMessageQueue.BridgeMode
  {
    boolean online = false;
    final Runnable runnable = new Runnable()
    {
      public void run()
      {
        if (!NativeToJsMessageQueue.this.queue.isEmpty()) {
          NativeToJsMessageQueue.this.webView.setNetworkAvailable(NativeToJsMessageQueue.OnlineEventsBridgeMode.this.online);
        }
      }
    };
    
    OnlineEventsBridgeMode()
    {
      super(null);
      NativeToJsMessageQueue.this.webView.setNetworkAvailable(true);
    }
    
    void notifyOfFlush(boolean paramBoolean)
    {
      if (paramBoolean) {
        if (this.online) {
          break label19;
        }
      }
      label19:
      for (boolean bool = true;; bool = false)
      {
        this.online = bool;
        return;
      }
    }
    
    void onNativeToJsMessageAvailable()
    {
      NativeToJsMessageQueue.this.cordova.getActivity().runOnUiThread(this.runnable);
    }
  }
  
  private class PrivateApiBridgeMode
    extends NativeToJsMessageQueue.BridgeMode
  {
    private static final int EXECUTE_JS = 194;
    boolean initFailed;
    Method sendMessageMethod;
    Object webViewCore;
    
    private PrivateApiBridgeMode()
    {
      super(null);
    }
    
    private void initReflection()
    {
      Object localObject1 = NativeToJsMessageQueue.this.webView;
      Object localObject2 = WebView.class;
      for (;;)
      {
        try
        {
          Field localField2 = ((Class)localObject2).getDeclaredField("mProvider");
          localField2.setAccessible(true);
          localObject1 = localField2.get(NativeToJsMessageQueue.this.webView);
          Class localClass = localObject1.getClass();
          localObject2 = localClass;
        }
        catch (Throwable localThrowable1)
        {
          Field localField1;
          continue;
        }
        try
        {
          localField1 = ((Class)localObject2).getDeclaredField("mWebViewCore");
          localField1.setAccessible(true);
          this.webViewCore = localField1.get(localObject1);
          if (this.webViewCore != null)
          {
            this.sendMessageMethod = this.webViewCore.getClass().getDeclaredMethod("sendMessage", new Class[] { Message.class });
            this.sendMessageMethod.setAccessible(true);
          }
          return;
        }
        catch (Throwable localThrowable2)
        {
          this.initFailed = true;
          Log.e("JsMessageQueue", "PrivateApiBridgeMode failed to find the expected APIs.", localThrowable2);
          return;
        }
      }
    }
    
    void onNativeToJsMessageAvailable()
    {
      if ((this.sendMessageMethod == null) && (!this.initFailed)) {
        initReflection();
      }
      Message localMessage;
      if (this.sendMessageMethod != null) {
        localMessage = Message.obtain(null, 194, NativeToJsMessageQueue.this.popAndEncodeAsJs());
      }
      try
      {
        this.sendMessageMethod.invoke(this.webViewCore, new Object[] { localMessage });
        return;
      }
      catch (Throwable localThrowable)
      {
        Log.e("JsMessageQueue", "Reflection message bridge failed.", localThrowable);
      }
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.NativeToJsMessageQueue
 * JD-Core Version:    0.7.0.1
 */