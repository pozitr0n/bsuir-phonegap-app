package org.apache.cordova;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Debug;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

public class PluginManager
{
  private static final int SLOW_EXEC_WARNING_THRESHOLD;
  private static String TAG = "PluginManager";
  private final CordovaWebView app;
  private final CordovaInterface ctx;
  private final HashMap<String, PluginEntry> entries = new HashMap();
  private boolean firstRun;
  private AtomicInteger numPendingUiExecs;
  protected HashMap<String, List<String>> urlMap = new HashMap();
  
  static
  {
    if (Debug.isDebuggerConnected()) {}
    for (int i = 60;; i = 16)
    {
      SLOW_EXEC_WARNING_THRESHOLD = i;
      return;
    }
  }
  
  public PluginManager(CordovaWebView paramCordovaWebView, CordovaInterface paramCordovaInterface)
  {
    this.ctx = paramCordovaInterface;
    this.app = paramCordovaWebView;
    this.firstRun = true;
    this.numPendingUiExecs = new AtomicInteger(0);
  }
  
  private void execHelper(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    CordovaPlugin localCordovaPlugin = getPlugin(paramString1);
    if (localCordovaPlugin == null)
    {
      Log.d(TAG, "exec() call to unknown plugin: " + paramString1);
      PluginResult localPluginResult3 = new PluginResult(PluginResult.Status.CLASS_NOT_FOUND_EXCEPTION);
      this.app.sendPluginResult(localPluginResult3, paramString3);
    }
    for (;;)
    {
      return;
      try
      {
        CallbackContext localCallbackContext = new CallbackContext(paramString3, this.app);
        long l1 = System.currentTimeMillis();
        boolean bool = localCordovaPlugin.execute(paramString2, paramString4, localCallbackContext);
        long l2 = System.currentTimeMillis() - l1;
        if (l2 > SLOW_EXEC_WARNING_THRESHOLD) {
          Log.w(TAG, "THREAD WARNING: exec() call to " + paramString1 + "." + paramString2 + " blocked the main thread for " + l2 + "ms. Plugin should use CordovaInterface.getThreadPool().");
        }
        if (!bool)
        {
          PluginResult localPluginResult2 = new PluginResult(PluginResult.Status.INVALID_ACTION);
          this.app.sendPluginResult(localPluginResult2, paramString3);
          return;
        }
      }
      catch (JSONException localJSONException)
      {
        PluginResult localPluginResult1 = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
        this.app.sendPluginResult(localPluginResult1, paramString3);
      }
    }
  }
  
  private void pluginConfigurationMissing()
  {
    LOG.e(TAG, "=====================================================================================");
    LOG.e(TAG, "ERROR: config.xml is missing.  Add res/xml/config.xml to your project.");
    LOG.e(TAG, "https://git-wip-us.apache.org/repos/asf?p=cordova-android.git;a=blob;f=framework/res/xml/config.xml");
    LOG.e(TAG, "=====================================================================================");
  }
  
  public void addService(String paramString1, String paramString2)
  {
    addService(new PluginEntry(paramString1, paramString2, false));
  }
  
  public void addService(PluginEntry paramPluginEntry)
  {
    this.entries.put(paramPluginEntry.service, paramPluginEntry);
  }
  
  public void clearPluginObjects()
  {
    Iterator localIterator = this.entries.values().iterator();
    while (localIterator.hasNext()) {
      ((PluginEntry)localIterator.next()).plugin = null;
    }
  }
  
  public void exec(final String paramString1, final String paramString2, final String paramString3, final String paramString4)
  {
    if (this.numPendingUiExecs.get() > 0)
    {
      this.numPendingUiExecs.getAndIncrement();
      this.ctx.getActivity().runOnUiThread(new Runnable()
      {
        public void run()
        {
          PluginManager.this.execHelper(paramString1, paramString2, paramString3, paramString4);
          PluginManager.this.numPendingUiExecs.getAndDecrement();
        }
      });
      return;
    }
    execHelper(paramString1, paramString2, paramString3, paramString4);
  }
  
  @Deprecated
  public void exec(String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean)
  {
    exec(paramString1, paramString2, paramString3, paramString4);
  }
  
  public CordovaPlugin getPlugin(String paramString)
  {
    PluginEntry localPluginEntry = (PluginEntry)this.entries.get(paramString);
    CordovaPlugin localCordovaPlugin;
    if (localPluginEntry == null) {
      localCordovaPlugin = null;
    }
    do
    {
      return localCordovaPlugin;
      localCordovaPlugin = localPluginEntry.plugin;
    } while (localCordovaPlugin != null);
    return localPluginEntry.createPlugin(this.app, this.ctx);
  }
  
  public void init()
  {
    LOG.d(TAG, "init()");
    if (this.firstRun)
    {
      loadPlugins();
      this.firstRun = false;
    }
    for (;;)
    {
      addService(new PluginEntry("PluginManager", new PluginManagerService(null)));
      startupPlugins();
      return;
      onPause(false);
      onDestroy();
      clearPluginObjects();
    }
  }
  
  public void loadPlugins()
  {
    int i = this.ctx.getActivity().getResources().getIdentifier("config", "xml", this.ctx.getActivity().getClass().getPackage().getName());
    if (i == 0)
    {
      i = this.ctx.getActivity().getResources().getIdentifier("config", "xml", this.ctx.getActivity().getPackageName());
      if (i == 0) {
        pluginConfigurationMissing();
      }
    }
    for (;;)
    {
      return;
      XmlResourceParser localXmlResourceParser = this.ctx.getActivity().getResources().getXml(i);
      int j = -1;
      String str1 = "";
      String str2 = "";
      boolean bool = false;
      int k = 0;
      while (j != 1)
      {
        String str4;
        if (j == 2)
        {
          str4 = localXmlResourceParser.getName();
          if (str4.equals("url-filter"))
          {
            Log.w(TAG, "Plugin " + str1 + " is using deprecated tag <url-filter>");
            if (this.urlMap.get(str1) == null) {
              this.urlMap.put(str1, new ArrayList(2));
            }
            ((List)this.urlMap.get(str1)).add(localXmlResourceParser.getAttributeValue(null, "value"));
          }
        }
        try
        {
          for (;;)
          {
            int m = localXmlResourceParser.next();
            j = m;
            break;
            if (str4.equals("feature"))
            {
              k = 1;
              str1 = localXmlResourceParser.getAttributeValue(null, "name");
            }
            else if ((k != 0) && (str4.equals("param")))
            {
              String str5 = localXmlResourceParser.getAttributeValue(null, "name");
              if (str5.equals("service"))
              {
                str1 = localXmlResourceParser.getAttributeValue(null, "value");
              }
              else if ((str5.equals("package")) || (str5.equals("android-package")))
              {
                str2 = localXmlResourceParser.getAttributeValue(null, "value");
              }
              else if (str5.equals("onload"))
              {
                bool = "true".equals(localXmlResourceParser.getAttributeValue(null, "value"));
                continue;
                if (j == 3)
                {
                  String str3 = localXmlResourceParser.getName();
                  if ((str3.equals("feature")) || (str3.equals("plugin")))
                  {
                    addService(new PluginEntry(str1, str2, bool));
                    str1 = "";
                    str2 = "";
                    k = 0;
                  }
                }
              }
            }
          }
        }
        catch (XmlPullParserException localXmlPullParserException)
        {
          localXmlPullParserException.printStackTrace();
        }
        catch (IOException localIOException)
        {
          localIOException.printStackTrace();
        }
      }
    }
  }
  
  public void onDestroy()
  {
    Iterator localIterator = this.entries.values().iterator();
    while (localIterator.hasNext())
    {
      PluginEntry localPluginEntry = (PluginEntry)localIterator.next();
      if (localPluginEntry.plugin != null) {
        localPluginEntry.plugin.onDestroy();
      }
    }
  }
  
  public void onNewIntent(Intent paramIntent)
  {
    Iterator localIterator = this.entries.values().iterator();
    while (localIterator.hasNext())
    {
      PluginEntry localPluginEntry = (PluginEntry)localIterator.next();
      if (localPluginEntry.plugin != null) {
        localPluginEntry.plugin.onNewIntent(paramIntent);
      }
    }
  }
  
  public boolean onOverrideUrlLoading(String paramString)
  {
    Iterator localIterator1 = this.entries.values().iterator();
    for (;;)
    {
      if (localIterator1.hasNext())
      {
        PluginEntry localPluginEntry = (PluginEntry)localIterator1.next();
        List localList = (List)this.urlMap.get(localPluginEntry.service);
        if (localList != null)
        {
          Iterator localIterator2 = localList.iterator();
          if (!localIterator2.hasNext()) {
            continue;
          }
          if (!paramString.startsWith((String)localIterator2.next())) {
            break;
          }
          return getPlugin(localPluginEntry.service).onOverrideUrlLoading(paramString);
        }
        if ((localPluginEntry.plugin != null) && (localPluginEntry.plugin.onOverrideUrlLoading(paramString))) {
          return true;
        }
      }
    }
    return false;
  }
  
  public void onPause(boolean paramBoolean)
  {
    Iterator localIterator = this.entries.values().iterator();
    while (localIterator.hasNext())
    {
      PluginEntry localPluginEntry = (PluginEntry)localIterator.next();
      if (localPluginEntry.plugin != null) {
        localPluginEntry.plugin.onPause(paramBoolean);
      }
    }
  }
  
  public void onReset()
  {
    Iterator localIterator = this.entries.values().iterator();
    while (localIterator.hasNext())
    {
      CordovaPlugin localCordovaPlugin = ((PluginEntry)localIterator.next()).plugin;
      if (localCordovaPlugin != null) {
        localCordovaPlugin.onReset();
      }
    }
  }
  
  public void onResume(boolean paramBoolean)
  {
    Iterator localIterator = this.entries.values().iterator();
    while (localIterator.hasNext())
    {
      PluginEntry localPluginEntry = (PluginEntry)localIterator.next();
      if (localPluginEntry.plugin != null) {
        localPluginEntry.plugin.onResume(paramBoolean);
      }
    }
  }
  
  public Object postMessage(String paramString, Object paramObject)
  {
    Object localObject1 = this.ctx.onMessage(paramString, paramObject);
    if (localObject1 != null) {
      return localObject1;
    }
    Iterator localIterator = this.entries.values().iterator();
    while (localIterator.hasNext())
    {
      PluginEntry localPluginEntry = (PluginEntry)localIterator.next();
      if (localPluginEntry.plugin != null)
      {
        Object localObject2 = localPluginEntry.plugin.onMessage(paramString, paramObject);
        if (localObject2 != null) {
          return localObject2;
        }
      }
    }
    return null;
  }
  
  Uri remapUri(Uri paramUri)
  {
    Iterator localIterator = this.entries.values().iterator();
    while (localIterator.hasNext())
    {
      PluginEntry localPluginEntry = (PluginEntry)localIterator.next();
      if (localPluginEntry.plugin != null)
      {
        Uri localUri = localPluginEntry.plugin.remapUri(paramUri);
        if (localUri != null) {
          return localUri;
        }
      }
    }
    return null;
  }
  
  public void startupPlugins()
  {
    Iterator localIterator = this.entries.values().iterator();
    while (localIterator.hasNext())
    {
      PluginEntry localPluginEntry = (PluginEntry)localIterator.next();
      if (localPluginEntry.onload) {
        localPluginEntry.createPlugin(this.app, this.ctx);
      }
    }
  }
  
  private class PluginManagerService
    extends CordovaPlugin
  {
    private PluginManagerService() {}
    
    public boolean execute(String paramString, CordovaArgs paramCordovaArgs, CallbackContext paramCallbackContext)
      throws JSONException
    {
      if ("startup".equals(paramString))
      {
        PluginManager.this.numPendingUiExecs.getAndIncrement();
        PluginManager.this.ctx.getActivity().runOnUiThread(new Runnable()
        {
          public void run()
          {
            PluginManager.this.numPendingUiExecs.getAndDecrement();
          }
        });
        return true;
      }
      return false;
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.PluginManager
 * JD-Core Version:    0.7.0.1
 */