package org.apache.cordova;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout.LayoutParams;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class CordovaWebView
  extends WebView
{
  public static final String CORDOVA_VERSION = "3.3.0";
  static final FrameLayout.LayoutParams COVER_SCREEN_GRAVITY_CENTER = new FrameLayout.LayoutParams(-1, -1, 17);
  public static final String TAG = "CordovaWebView";
  private boolean bound;
  private CordovaChromeClient chromeClient;
  private CordovaInterface cordova;
  ExposedJsApi exposedJsApi;
  private boolean handleButton = false;
  NativeToJsMessageQueue jsMessageQueue;
  private ArrayList<Integer> keyDownCodes = new ArrayList();
  private ArrayList<Integer> keyUpCodes = new ArrayList();
  private long lastMenuEventTime = 0L;
  int loadUrlTimeout = 0;
  private View mCustomView;
  private WebChromeClient.CustomViewCallback mCustomViewCallback;
  private ActivityResult mResult = null;
  private boolean paused;
  public PluginManager pluginManager;
  private BroadcastReceiver receiver;
  private CordovaResourceApi resourceApi;
  private String url;
  CordovaWebViewClient viewClient;
  
  public CordovaWebView(Context paramContext)
  {
    super(paramContext);
    if (CordovaInterface.class.isInstance(paramContext)) {
      this.cordova = ((CordovaInterface)paramContext);
    }
    for (;;)
    {
      loadConfiguration();
      setup();
      return;
      Log.d("CordovaWebView", "Your activity must implement CordovaInterface to work");
    }
  }
  
  public CordovaWebView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    if (CordovaInterface.class.isInstance(paramContext)) {
      this.cordova = ((CordovaInterface)paramContext);
    }
    for (;;)
    {
      setWebChromeClient(new CordovaChromeClient(this.cordova, this));
      initWebViewClient(this.cordova);
      loadConfiguration();
      setup();
      return;
      Log.d("CordovaWebView", "Your activity must implement CordovaInterface to work");
    }
  }
  
  public CordovaWebView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    if (CordovaInterface.class.isInstance(paramContext)) {
      this.cordova = ((CordovaInterface)paramContext);
    }
    for (;;)
    {
      setWebChromeClient(new CordovaChromeClient(this.cordova, this));
      loadConfiguration();
      setup();
      return;
      Log.d("CordovaWebView", "Your activity must implement CordovaInterface to work");
    }
  }
  
  @TargetApi(11)
  public CordovaWebView(Context paramContext, AttributeSet paramAttributeSet, int paramInt, boolean paramBoolean)
  {
    super(paramContext, paramAttributeSet, paramInt, paramBoolean);
    if (CordovaInterface.class.isInstance(paramContext)) {
      this.cordova = ((CordovaInterface)paramContext);
    }
    for (;;)
    {
      setWebChromeClient(new CordovaChromeClient(this.cordova));
      initWebViewClient(this.cordova);
      loadConfiguration();
      setup();
      return;
      Log.d("CordovaWebView", "Your activity must implement CordovaInterface to work");
    }
  }
  
  private void exposeJsInterface()
  {
    int i = Build.VERSION.SDK_INT;
    if ((i >= 11) && (i <= 13)) {}
    for (int j = 1; (j != 0) || (i < 9); j = 0)
    {
      Log.i("CordovaWebView", "Disabled addJavascriptInterface() bridge since Android version is old.");
      return;
    }
    if ((i < 11) && (Build.MANUFACTURER.equals("unknown")))
    {
      Log.i("CordovaWebView", "Disabled addJavascriptInterface() bridge callback due to a bug on the 2.3 emulator");
      return;
    }
    addJavascriptInterface(this.exposedJsApi, "_cordovaNative");
  }
  
  private void initWebViewClient(CordovaInterface paramCordovaInterface)
  {
    if ((Build.VERSION.SDK_INT < 11) || (Build.VERSION.SDK_INT > 17))
    {
      setWebViewClient(new CordovaWebViewClient(this.cordova, this));
      return;
    }
    setWebViewClient(new IceCreamCordovaWebViewClient(this.cordova, this));
  }
  
  private void loadConfiguration()
  {
    if ("true".equals(getProperty("Fullscreen", "false")))
    {
      this.cordova.getActivity().getWindow().clearFlags(2048);
      this.cordova.getActivity().getWindow().setFlags(1024, 1024);
    }
  }
  
  /* Error */
  @android.annotation.SuppressLint({"NewApi"})
  private void setup()
  {
    // Byte code:
    //   0: aload_0
    //   1: iconst_0
    //   2: invokevirtual 223	org/apache/cordova/CordovaWebView:setInitialScale	(I)V
    //   5: aload_0
    //   6: iconst_0
    //   7: invokevirtual 227	org/apache/cordova/CordovaWebView:setVerticalScrollBarEnabled	(Z)V
    //   10: aload_0
    //   11: invokevirtual 231	org/apache/cordova/CordovaWebView:shouldRequestFocusOnInit	()Z
    //   14: ifeq +8 -> 22
    //   17: aload_0
    //   18: invokevirtual 234	org/apache/cordova/CordovaWebView:requestFocusFromTouch	()Z
    //   21: pop
    //   22: aload_0
    //   23: invokevirtual 238	org/apache/cordova/CordovaWebView:getSettings	()Landroid/webkit/WebSettings;
    //   26: astore_1
    //   27: aload_1
    //   28: iconst_1
    //   29: invokevirtual 243	android/webkit/WebSettings:setJavaScriptEnabled	(Z)V
    //   32: aload_1
    //   33: iconst_1
    //   34: invokevirtual 246	android/webkit/WebSettings:setJavaScriptCanOpenWindowsAutomatically	(Z)V
    //   37: aload_1
    //   38: getstatic 252	android/webkit/WebSettings$LayoutAlgorithm:NORMAL	Landroid/webkit/WebSettings$LayoutAlgorithm;
    //   41: invokevirtual 256	android/webkit/WebSettings:setLayoutAlgorithm	(Landroid/webkit/WebSettings$LayoutAlgorithm;)V
    //   44: iconst_1
    //   45: anewarray 81	java/lang/Class
    //   48: astore 18
    //   50: aload 18
    //   52: iconst_0
    //   53: getstatic 262	java/lang/Boolean:TYPE	Ljava/lang/Class;
    //   56: aastore
    //   57: ldc 240
    //   59: ldc_w 264
    //   62: aload 18
    //   64: invokevirtual 268	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   67: astore 19
    //   69: getstatic 151	android/os/Build:MANUFACTURER	Ljava/lang/String;
    //   72: astore 20
    //   74: ldc 13
    //   76: new 270	java/lang/StringBuilder
    //   79: dup
    //   80: invokespecial 271	java/lang/StringBuilder:<init>	()V
    //   83: ldc_w 273
    //   86: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   89: aload 20
    //   91: invokevirtual 277	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   94: invokevirtual 281	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   97: invokestatic 101	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   100: pop
    //   101: getstatic 141	android/os/Build$VERSION:SDK_INT	I
    //   104: bipush 11
    //   106: if_icmpge +38 -> 144
    //   109: getstatic 151	android/os/Build:MANUFACTURER	Ljava/lang/String;
    //   112: ldc_w 283
    //   115: invokevirtual 287	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   118: ifeq +26 -> 144
    //   121: iconst_1
    //   122: anewarray 289	java/lang/Object
    //   125: astore 22
    //   127: aload 22
    //   129: iconst_0
    //   130: iconst_1
    //   131: invokestatic 293	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   134: aastore
    //   135: aload 19
    //   137: aload_1
    //   138: aload 22
    //   140: invokevirtual 299	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   143: pop
    //   144: aload_1
    //   145: iconst_0
    //   146: invokevirtual 302	android/webkit/WebSettings:setSaveFormData	(Z)V
    //   149: aload_1
    //   150: iconst_0
    //   151: invokevirtual 305	android/webkit/WebSettings:setSavePassword	(Z)V
    //   154: getstatic 141	android/os/Build$VERSION:SDK_INT	I
    //   157: bipush 15
    //   159: if_icmple +7 -> 166
    //   162: aload_1
    //   163: invokestatic 311	org/apache/cordova/CordovaWebView$Level16Apis:enableUniversalAccess	(Landroid/webkit/WebSettings;)V
    //   166: aload_0
    //   167: getfield 87	org/apache/cordova/CordovaWebView:cordova	Lorg/apache/cordova/CordovaInterface;
    //   170: invokeinterface 192 1 0
    //   175: invokevirtual 315	android/app/Activity:getApplicationContext	()Landroid/content/Context;
    //   178: ldc_w 317
    //   181: iconst_0
    //   182: invokevirtual 323	android/content/Context:getDir	(Ljava/lang/String;I)Ljava/io/File;
    //   185: invokevirtual 328	java/io/File:getPath	()Ljava/lang/String;
    //   188: astore 4
    //   190: aload_1
    //   191: iconst_1
    //   192: invokevirtual 331	android/webkit/WebSettings:setDatabaseEnabled	(Z)V
    //   195: aload_1
    //   196: aload 4
    //   198: invokevirtual 335	android/webkit/WebSettings:setDatabasePath	(Ljava/lang/String;)V
    //   201: aload_0
    //   202: getfield 87	org/apache/cordova/CordovaWebView:cordova	Lorg/apache/cordova/CordovaInterface;
    //   205: invokeinterface 192 1 0
    //   210: invokevirtual 338	android/app/Activity:getPackageName	()Ljava/lang/String;
    //   213: astore 11
    //   215: iconst_2
    //   216: aload_0
    //   217: getfield 87	org/apache/cordova/CordovaWebView:cordova	Lorg/apache/cordova/CordovaInterface;
    //   220: invokeinterface 192 1 0
    //   225: invokevirtual 342	android/app/Activity:getPackageManager	()Landroid/content/pm/PackageManager;
    //   228: aload 11
    //   230: sipush 128
    //   233: invokevirtual 348	android/content/pm/PackageManager:getApplicationInfo	(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;
    //   236: getfield 353	android/content/pm/ApplicationInfo:flags	I
    //   239: iand
    //   240: ifeq +15 -> 255
    //   243: getstatic 141	android/os/Build$VERSION:SDK_INT	I
    //   246: bipush 19
    //   248: if_icmplt +7 -> 255
    //   251: iconst_1
    //   252: invokestatic 356	org/apache/cordova/CordovaWebView:setWebContentsDebuggingEnabled	(Z)V
    //   255: aload_1
    //   256: aload 4
    //   258: invokevirtual 359	android/webkit/WebSettings:setGeolocationDatabasePath	(Ljava/lang/String;)V
    //   261: aload_1
    //   262: iconst_1
    //   263: invokevirtual 362	android/webkit/WebSettings:setDomStorageEnabled	(Z)V
    //   266: aload_1
    //   267: iconst_1
    //   268: invokevirtual 365	android/webkit/WebSettings:setGeolocationEnabled	(Z)V
    //   271: aload_1
    //   272: ldc2_w 366
    //   275: invokevirtual 371	android/webkit/WebSettings:setAppCacheMaxSize	(J)V
    //   278: aload_1
    //   279: aload_0
    //   280: getfield 87	org/apache/cordova/CordovaWebView:cordova	Lorg/apache/cordova/CordovaInterface;
    //   283: invokeinterface 192 1 0
    //   288: invokevirtual 315	android/app/Activity:getApplicationContext	()Landroid/content/Context;
    //   291: ldc_w 317
    //   294: iconst_0
    //   295: invokevirtual 323	android/content/Context:getDir	(Ljava/lang/String;I)Ljava/io/File;
    //   298: invokevirtual 328	java/io/File:getPath	()Ljava/lang/String;
    //   301: invokevirtual 374	android/webkit/WebSettings:setAppCachePath	(Ljava/lang/String;)V
    //   304: aload_1
    //   305: iconst_1
    //   306: invokevirtual 377	android/webkit/WebSettings:setAppCacheEnabled	(Z)V
    //   309: aload_0
    //   310: invokespecial 133	org/apache/cordova/CordovaWebView:updateUserAgentString	()V
    //   313: new 379	android/content/IntentFilter
    //   316: dup
    //   317: invokespecial 380	android/content/IntentFilter:<init>	()V
    //   320: astore 7
    //   322: aload 7
    //   324: ldc_w 382
    //   327: invokevirtual 385	android/content/IntentFilter:addAction	(Ljava/lang/String;)V
    //   330: aload_0
    //   331: getfield 387	org/apache/cordova/CordovaWebView:receiver	Landroid/content/BroadcastReceiver;
    //   334: ifnonnull +34 -> 368
    //   337: aload_0
    //   338: new 389	org/apache/cordova/CordovaWebView$1
    //   341: dup
    //   342: aload_0
    //   343: invokespecial 391	org/apache/cordova/CordovaWebView$1:<init>	(Lorg/apache/cordova/CordovaWebView;)V
    //   346: putfield 387	org/apache/cordova/CordovaWebView:receiver	Landroid/content/BroadcastReceiver;
    //   349: aload_0
    //   350: getfield 87	org/apache/cordova/CordovaWebView:cordova	Lorg/apache/cordova/CordovaInterface;
    //   353: invokeinterface 192 1 0
    //   358: aload_0
    //   359: getfield 387	org/apache/cordova/CordovaWebView:receiver	Landroid/content/BroadcastReceiver;
    //   362: aload 7
    //   364: invokevirtual 395	android/app/Activity:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;)Landroid/content/Intent;
    //   367: pop
    //   368: aload_0
    //   369: new 397	org/apache/cordova/PluginManager
    //   372: dup
    //   373: aload_0
    //   374: aload_0
    //   375: getfield 87	org/apache/cordova/CordovaWebView:cordova	Lorg/apache/cordova/CordovaInterface;
    //   378: invokespecial 400	org/apache/cordova/PluginManager:<init>	(Lorg/apache/cordova/CordovaWebView;Lorg/apache/cordova/CordovaInterface;)V
    //   381: putfield 402	org/apache/cordova/CordovaWebView:pluginManager	Lorg/apache/cordova/PluginManager;
    //   384: aload_0
    //   385: new 404	org/apache/cordova/NativeToJsMessageQueue
    //   388: dup
    //   389: aload_0
    //   390: aload_0
    //   391: getfield 87	org/apache/cordova/CordovaWebView:cordova	Lorg/apache/cordova/CordovaInterface;
    //   394: invokespecial 405	org/apache/cordova/NativeToJsMessageQueue:<init>	(Lorg/apache/cordova/CordovaWebView;Lorg/apache/cordova/CordovaInterface;)V
    //   397: putfield 407	org/apache/cordova/CordovaWebView:jsMessageQueue	Lorg/apache/cordova/NativeToJsMessageQueue;
    //   400: aload_0
    //   401: new 409	org/apache/cordova/ExposedJsApi
    //   404: dup
    //   405: aload_0
    //   406: getfield 402	org/apache/cordova/CordovaWebView:pluginManager	Lorg/apache/cordova/PluginManager;
    //   409: aload_0
    //   410: getfield 407	org/apache/cordova/CordovaWebView:jsMessageQueue	Lorg/apache/cordova/NativeToJsMessageQueue;
    //   413: invokespecial 412	org/apache/cordova/ExposedJsApi:<init>	(Lorg/apache/cordova/PluginManager;Lorg/apache/cordova/NativeToJsMessageQueue;)V
    //   416: putfield 162	org/apache/cordova/CordovaWebView:exposedJsApi	Lorg/apache/cordova/ExposedJsApi;
    //   419: aload_0
    //   420: new 414	org/apache/cordova/CordovaResourceApi
    //   423: dup
    //   424: aload_0
    //   425: invokevirtual 417	org/apache/cordova/CordovaWebView:getContext	()Landroid/content/Context;
    //   428: aload_0
    //   429: getfield 402	org/apache/cordova/CordovaWebView:pluginManager	Lorg/apache/cordova/PluginManager;
    //   432: invokespecial 420	org/apache/cordova/CordovaResourceApi:<init>	(Landroid/content/Context;Lorg/apache/cordova/PluginManager;)V
    //   435: putfield 422	org/apache/cordova/CordovaWebView:resourceApi	Lorg/apache/cordova/CordovaResourceApi;
    //   438: aload_0
    //   439: invokespecial 424	org/apache/cordova/CordovaWebView:exposeJsInterface	()V
    //   442: return
    //   443: astore 16
    //   445: ldc 13
    //   447: ldc_w 426
    //   450: invokestatic 101	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   453: pop
    //   454: goto -310 -> 144
    //   457: astore 14
    //   459: ldc 13
    //   461: ldc_w 428
    //   464: invokestatic 101	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   467: pop
    //   468: goto -324 -> 144
    //   471: astore 12
    //   473: ldc 13
    //   475: ldc_w 430
    //   478: invokestatic 101	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   481: pop
    //   482: goto -338 -> 144
    //   485: astore_2
    //   486: ldc 13
    //   488: ldc_w 432
    //   491: invokestatic 101	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   494: pop
    //   495: goto -351 -> 144
    //   498: astore 9
    //   500: ldc 13
    //   502: ldc_w 434
    //   505: invokestatic 101	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   508: pop
    //   509: aload 9
    //   511: invokevirtual 437	java/lang/IllegalArgumentException:printStackTrace	()V
    //   514: goto -259 -> 255
    //   517: astore 5
    //   519: ldc 13
    //   521: ldc_w 439
    //   524: invokestatic 101	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   527: pop
    //   528: aload 5
    //   530: invokevirtual 440	android/content/pm/PackageManager$NameNotFoundException:printStackTrace	()V
    //   533: goto -278 -> 255
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	536	0	this	CordovaWebView
    //   26	279	1	localWebSettings	WebSettings
    //   485	1	2	localInvocationTargetException	java.lang.reflect.InvocationTargetException
    //   188	69	4	str1	String
    //   517	12	5	localNameNotFoundException	android.content.pm.PackageManager.NameNotFoundException
    //   320	43	7	localIntentFilter	android.content.IntentFilter
    //   498	12	9	localIllegalArgumentException1	java.lang.IllegalArgumentException
    //   213	16	11	str2	String
    //   471	1	12	localIllegalAccessException	java.lang.IllegalAccessException
    //   457	1	14	localIllegalArgumentException2	java.lang.IllegalArgumentException
    //   443	1	16	localNoSuchMethodException	java.lang.NoSuchMethodException
    //   48	15	18	arrayOfClass	Class[]
    //   67	69	19	localMethod	java.lang.reflect.Method
    //   72	18	20	str3	String
    //   125	14	22	arrayOfObject	Object[]
    // Exception table:
    //   from	to	target	type
    //   44	144	443	java/lang/NoSuchMethodException
    //   44	144	457	java/lang/IllegalArgumentException
    //   44	144	471	java/lang/IllegalAccessException
    //   44	144	485	java/lang/reflect/InvocationTargetException
    //   201	255	498	java/lang/IllegalArgumentException
    //   201	255	517	android/content/pm/PackageManager$NameNotFoundException
  }
  
  private void updateUserAgentString()
  {
    getSettings().getUserAgentString();
  }
  
  public boolean backHistory()
  {
    if (super.canGoBack())
    {
      printBackForwardList();
      super.goBack();
      return true;
    }
    return false;
  }
  
  public void bindButton(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      this.keyDownCodes.add(Integer.valueOf(paramInt));
      return;
    }
    this.keyUpCodes.add(Integer.valueOf(paramInt));
  }
  
  public void bindButton(String paramString, boolean paramBoolean)
  {
    if (paramString.compareTo("volumeup") == 0) {
      this.keyDownCodes.add(Integer.valueOf(24));
    }
    while (paramString.compareTo("volumedown") != 0) {
      return;
    }
    this.keyDownCodes.add(Integer.valueOf(25));
  }
  
  public void bindButton(boolean paramBoolean)
  {
    this.bound = paramBoolean;
  }
  
  public String getProperty(String paramString1, String paramString2)
  {
    Bundle localBundle = this.cordova.getActivity().getIntent().getExtras();
    if (localBundle == null) {}
    Object localObject;
    do
    {
      return paramString2;
      localObject = localBundle.get(paramString1.toLowerCase(Locale.getDefault()));
    } while (localObject == null);
    return localObject.toString();
  }
  
  public CordovaResourceApi getResourceApi()
  {
    return this.resourceApi;
  }
  
  public CordovaChromeClient getWebChromeClient()
  {
    return this.chromeClient;
  }
  
  public boolean hadKeyEvent()
  {
    return this.handleButton;
  }
  
  public void handleDestroy()
  {
    loadUrl("javascript:try{cordova.require('cordova/channel').onDestroy.fire();}catch(e){console.log('exception firing destroy event from native');};");
    loadUrl("about:blank");
    if (this.pluginManager != null) {
      this.pluginManager.onDestroy();
    }
    if (this.receiver != null) {}
    try
    {
      this.cordova.getActivity().unregisterReceiver(this.receiver);
      return;
    }
    catch (Exception localException)
    {
      Log.e("CordovaWebView", "Error unregistering configuration receiver: " + localException.getMessage(), localException);
    }
  }
  
  public void handlePause(boolean paramBoolean)
  {
    LOG.d("CordovaWebView", "Handle the pause");
    loadUrl("javascript:try{cordova.fireDocumentEvent('pause');}catch(e){console.log('exception firing pause event from native');};");
    if (this.pluginManager != null) {
      this.pluginManager.onPause(paramBoolean);
    }
    if (!paramBoolean) {
      pauseTimers();
    }
    this.paused = true;
  }
  
  public void handleResume(boolean paramBoolean1, boolean paramBoolean2)
  {
    loadUrl("javascript:try{cordova.fireDocumentEvent('resume');}catch(e){console.log('exception firing resume event from native');};");
    if (this.pluginManager != null) {
      this.pluginManager.onResume(paramBoolean1);
    }
    resumeTimers();
    this.paused = false;
  }
  
  public void hideCustomView()
  {
    Log.d("CordovaWebView", "Hidding Custom View");
    if (this.mCustomView == null) {
      return;
    }
    this.mCustomView.setVisibility(8);
    ((ViewGroup)getParent()).removeView(this.mCustomView);
    this.mCustomView = null;
    this.mCustomViewCallback.onCustomViewHidden();
    setVisibility(0);
  }
  
  public boolean isBackButtonBound()
  {
    return this.bound;
  }
  
  public boolean isCustomViewShowing()
  {
    return this.mCustomView != null;
  }
  
  public boolean isPaused()
  {
    return this.paused;
  }
  
  public void loadUrl(String paramString)
  {
    if ((paramString.equals("about:blank")) || (paramString.startsWith("javascript:")))
    {
      loadUrlNow(paramString);
      return;
    }
    String str = getProperty("url", null);
    if (str == null)
    {
      loadUrlIntoView(paramString);
      return;
    }
    loadUrlIntoView(str);
  }
  
  public void loadUrl(String paramString, int paramInt)
  {
    String str = getProperty("url", null);
    if (str == null)
    {
      loadUrlIntoView(paramString, paramInt);
      return;
    }
    loadUrlIntoView(str);
  }
  
  public void loadUrlIntoView(final String paramString)
  {
    LOG.d("CordovaWebView", ">>> loadUrl(" + paramString + ")");
    this.url = paramString;
    this.pluginManager.init();
    final int i = this.loadUrlTimeout;
    final Runnable local3 = new Runnable()
    {
      public void run()
      {
        jdField_this.stopLoading();
        LOG.e("CordovaWebView", "CordovaWebView: TIMEOUT ERROR!");
        if (CordovaWebView.this.viewClient != null) {
          CordovaWebView.this.viewClient.onReceivedError(jdField_this, -6, "The connection to the server was unsuccessful.", paramString);
        }
      }
    }
    {
      public void run()
      {
        for (;;)
        {
          try {}catch (InterruptedException localInterruptedException)
          {
            localInterruptedException.printStackTrace();
            continue;
          }
          try
          {
            wait(this.val$loadUrlTimeoutValue);
            if (jdField_this.loadUrlTimeout == i) {
              jdField_this.cordova.getActivity().runOnUiThread(this.val$loadError);
            }
            return;
          }
          finally {}
        }
      }
    };
    this.cordova.getActivity().runOnUiThread(new Runnable()
    {
      public void run()
      {
        new Thread(local3).start();
        jdField_this.loadUrlNow(paramString);
      }
    });
  }
  
  public void loadUrlIntoView(String paramString, int paramInt)
  {
    if ((paramString.startsWith("javascript:")) || (canGoBack())) {}
    for (;;)
    {
      loadUrlIntoView(paramString);
      return;
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramString;
      arrayOfObject[1] = Integer.valueOf(paramInt);
      LOG.d("CordovaWebView", "loadUrlIntoView(%s, %d)", arrayOfObject);
      postMessage("splashscreen", "show");
    }
  }
  
  void loadUrlNow(String paramString)
  {
    if ((LOG.isLoggable(3)) && (!paramString.startsWith("javascript:"))) {
      LOG.d("CordovaWebView", ">>> loadUrlNow()");
    }
    if ((paramString.startsWith("file://")) || (paramString.startsWith("javascript:")) || (Config.isUrlWhiteListed(paramString))) {
      super.loadUrl(paramString);
    }
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (this.keyDownCodes.contains(Integer.valueOf(paramInt)))
    {
      if (paramInt == 25)
      {
        LOG.d("CordovaWebView", "Down Key Hit");
        loadUrl("javascript:cordova.fireDocumentEvent('volumedownbutton');");
        return true;
      }
      if (paramInt == 24)
      {
        LOG.d("CordovaWebView", "Up Key Hit");
        loadUrl("javascript:cordova.fireDocumentEvent('volumeupbutton');");
        return true;
      }
      return super.onKeyDown(paramInt, paramKeyEvent);
    }
    if (paramInt == 4)
    {
      boolean bool1;
      if (startOfHistory())
      {
        boolean bool2 = this.bound;
        bool1 = false;
        if (!bool2) {}
      }
      else
      {
        bool1 = true;
      }
      return bool1;
    }
    if (paramInt == 82)
    {
      View localView = getFocusedChild();
      if (localView != null)
      {
        ((InputMethodManager)this.cordova.getActivity().getSystemService("input_method")).hideSoftInputFromWindow(localView.getWindowToken(), 0);
        this.cordova.getActivity().openOptionsMenu();
        return true;
      }
      return super.onKeyDown(paramInt, paramKeyEvent);
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool = true;
    if (paramInt == 4) {
      if (this.mCustomView != null) {
        hideCustomView();
      }
    }
    do
    {
      for (;;)
      {
        bool = super.onKeyUp(paramInt, paramKeyEvent);
        do
        {
          return bool;
          if (this.bound)
          {
            loadUrl("javascript:cordova.fireDocumentEvent('backbutton');");
            return bool;
          }
        } while (backHistory());
        this.cordova.getActivity().finish();
      }
      if (paramInt == 82)
      {
        if (this.lastMenuEventTime < paramKeyEvent.getEventTime()) {
          loadUrl("javascript:cordova.fireDocumentEvent('menubutton');");
        }
        this.lastMenuEventTime = paramKeyEvent.getEventTime();
        return super.onKeyUp(paramInt, paramKeyEvent);
      }
      if (paramInt == 84)
      {
        loadUrl("javascript:cordova.fireDocumentEvent('searchbutton');");
        return bool;
      }
    } while (!this.keyUpCodes.contains(Integer.valueOf(paramInt)));
    return super.onKeyUp(paramInt, paramKeyEvent);
  }
  
  public void onNewIntent(Intent paramIntent)
  {
    if (this.pluginManager != null) {
      this.pluginManager.onNewIntent(paramIntent);
    }
  }
  
  public void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    postMessage("onScrollChanged", new ScrollEvent(paramInt1, paramInt2, paramInt3, paramInt4, this));
  }
  
  public void postMessage(String paramString, Object paramObject)
  {
    if (this.pluginManager != null) {
      this.pluginManager.postMessage(paramString, paramObject);
    }
  }
  
  public void printBackForwardList()
  {
    WebBackForwardList localWebBackForwardList = copyBackForwardList();
    int i = localWebBackForwardList.getSize();
    for (int j = 0; j < i; j++)
    {
      String str = localWebBackForwardList.getItemAtIndex(j).getUrl();
      LOG.d("CordovaWebView", "The URL at index: " + Integer.toString(j) + " is " + str);
    }
  }
  
  public WebBackForwardList restoreState(Bundle paramBundle)
  {
    WebBackForwardList localWebBackForwardList = super.restoreState(paramBundle);
    Log.d("CordovaWebView", "WebView restoration crew now restoring!");
    this.pluginManager.init();
    return localWebBackForwardList;
  }
  
  public void sendJavascript(String paramString)
  {
    this.jsMessageQueue.addJavaScript(paramString);
  }
  
  public void sendPluginResult(PluginResult paramPluginResult, String paramString)
  {
    this.jsMessageQueue.addPluginResult(paramPluginResult, paramString);
  }
  
  public void setWebChromeClient(CordovaChromeClient paramCordovaChromeClient)
  {
    this.chromeClient = paramCordovaChromeClient;
    super.setWebChromeClient(paramCordovaChromeClient);
  }
  
  public void setWebViewClient(CordovaWebViewClient paramCordovaWebViewClient)
  {
    this.viewClient = paramCordovaWebViewClient;
    super.setWebViewClient(paramCordovaWebViewClient);
  }
  
  protected boolean shouldRequestFocusOnInit()
  {
    return true;
  }
  
  public void showCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
  {
    Log.d("CordovaWebView", "showing Custom View");
    if (this.mCustomView != null)
    {
      paramCustomViewCallback.onCustomViewHidden();
      return;
    }
    this.mCustomView = paramView;
    this.mCustomViewCallback = paramCustomViewCallback;
    ViewGroup localViewGroup = (ViewGroup)getParent();
    localViewGroup.addView(paramView, COVER_SCREEN_GRAVITY_CENTER);
    setVisibility(8);
    localViewGroup.setVisibility(0);
    localViewGroup.bringToFront();
  }
  
  public void showWebPage(String paramString, boolean paramBoolean1, boolean paramBoolean2, HashMap<String, Object> paramHashMap)
  {
    Object[] arrayOfObject = new Object[3];
    arrayOfObject[0] = paramString;
    arrayOfObject[1] = Boolean.valueOf(paramBoolean1);
    arrayOfObject[2] = Boolean.valueOf(paramBoolean2);
    LOG.d("CordovaWebView", "showWebPage(%s, %b, %b, HashMap", arrayOfObject);
    if (paramBoolean2) {
      clearHistory();
    }
    if (!paramBoolean1)
    {
      if ((paramString.startsWith("file://")) || (Config.isUrlWhiteListed(paramString)))
      {
        loadUrl(paramString);
        return;
      }
      LOG.w("CordovaWebView", "showWebPage: Cannot load URL into webview since it is not in white list.  Loading into browser instead. (URL=" + paramString + ")");
      try
      {
        Intent localIntent2 = new Intent("android.intent.action.VIEW");
        localIntent2.setData(Uri.parse(paramString));
        this.cordova.getActivity().startActivity(localIntent2);
        return;
      }
      catch (ActivityNotFoundException localActivityNotFoundException2)
      {
        LOG.e("CordovaWebView", "Error loading url " + paramString, localActivityNotFoundException2);
        return;
      }
    }
    try
    {
      Intent localIntent1 = new Intent("android.intent.action.VIEW");
      localIntent1.setData(Uri.parse(paramString));
      this.cordova.getActivity().startActivity(localIntent1);
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException1)
    {
      LOG.e("CordovaWebView", "Error loading url " + paramString, localActivityNotFoundException1);
    }
  }
  
  public boolean startOfHistory()
  {
    WebHistoryItem localWebHistoryItem = copyBackForwardList().getItemAtIndex(0);
    boolean bool = false;
    if (localWebHistoryItem != null)
    {
      String str1 = localWebHistoryItem.getUrl();
      String str2 = getUrl();
      LOG.d("CordovaWebView", "The current URL is: " + str2);
      LOG.d("CordovaWebView", "The URL at item 0 is: " + str1);
      bool = str2.equals(str1);
    }
    return bool;
  }
  
  public void storeResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.mResult = new ActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  class ActivityResult
  {
    Intent incoming;
    int request;
    int result;
    
    public ActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
      this.request = paramInt1;
      this.result = paramInt2;
      this.incoming = paramIntent;
    }
  }
  
  @TargetApi(16)
  private static class Level16Apis
  {
    static void enableUniversalAccess(WebSettings paramWebSettings)
    {
      paramWebSettings.setAllowUniversalAccessFromFileURLs(true);
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.CordovaWebView
 * JD-Core Version:    0.7.0.1
 */