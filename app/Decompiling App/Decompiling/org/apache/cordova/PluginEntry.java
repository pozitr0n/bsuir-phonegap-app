package org.apache.cordova;

import java.io.PrintStream;

public class PluginEntry
{
  public boolean onload = false;
  public CordovaPlugin plugin = null;
  public String pluginClass = "";
  public String service = "";
  
  public PluginEntry(String paramString1, String paramString2, boolean paramBoolean)
  {
    this.service = paramString1;
    this.pluginClass = paramString2;
    this.onload = paramBoolean;
  }
  
  public PluginEntry(String paramString, CordovaPlugin paramCordovaPlugin)
  {
    this.service = paramString;
    this.plugin = paramCordovaPlugin;
    this.pluginClass = paramCordovaPlugin.getClass().getName();
    this.onload = false;
  }
  
  private Class getClassByName(String paramString)
    throws ClassNotFoundException
  {
    Class localClass = null;
    if (paramString != null)
    {
      boolean bool = "".equals(paramString);
      localClass = null;
      if (!bool) {
        localClass = Class.forName(paramString);
      }
    }
    return localClass;
  }
  
  private boolean isCordovaPlugin(Class paramClass)
  {
    if (paramClass != null) {
      return CordovaPlugin.class.isAssignableFrom(paramClass);
    }
    return false;
  }
  
  public CordovaPlugin createPlugin(CordovaWebView paramCordovaWebView, CordovaInterface paramCordovaInterface)
  {
    if (this.plugin != null) {
      return this.plugin;
    }
    try
    {
      Class localClass = getClassByName(this.pluginClass);
      if (isCordovaPlugin(localClass))
      {
        this.plugin = ((CordovaPlugin)localClass.newInstance());
        this.plugin.initialize(paramCordovaInterface, paramCordovaWebView);
        CordovaPlugin localCordovaPlugin = this.plugin;
        return localCordovaPlugin;
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      System.out.println("Error adding plugin " + this.pluginClass + ".");
    }
    return null;
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.PluginEntry
 * JD-Core Version:    0.7.0.1
 */