package org.apache.cordova;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParserException;

public class Config
{
  public static final String TAG = "Config";
  private static Config self = null;
  private String startUrl;
  private Whitelist whitelist = new Whitelist();
  
  private Config() {}
  
  private Config(Activity paramActivity)
  {
    if (paramActivity == null) {
      LOG.i("CordovaLog", "There is no activity. Is this on the lock screen?");
    }
    for (;;)
    {
      return;
      int i = paramActivity.getResources().getIdentifier("config", "xml", paramActivity.getClass().getPackage().getName());
      if (i == 0)
      {
        i = paramActivity.getResources().getIdentifier("config", "xml", paramActivity.getPackageName());
        if (i == 0)
        {
          LOG.i("CordovaLog", "config.xml missing. Ignoring...");
          return;
        }
      }
      this.whitelist.addWhiteListEntry("file:///*", false);
      this.whitelist.addWhiteListEntry("content:///*", false);
      this.whitelist.addWhiteListEntry("data:*", false);
      XmlResourceParser localXmlResourceParser = paramActivity.getResources().getXml(i);
      int j = -1;
      while (j != 1)
      {
        String str1;
        boolean bool4;
        if (j == 2)
        {
          str1 = localXmlResourceParser.getName();
          if (!str1.equals("access")) {
            break label234;
          }
          String str7 = localXmlResourceParser.getAttributeValue(null, "origin");
          String str8 = localXmlResourceParser.getAttributeValue(null, "subdomains");
          if (str7 != null)
          {
            Whitelist localWhitelist = this.whitelist;
            if ((str8 == null) || (str8.compareToIgnoreCase("true") != 0)) {
              break label228;
            }
            bool4 = true;
            label204:
            localWhitelist.addWhiteListEntry(str7, bool4);
          }
        }
        try
        {
          for (;;)
          {
            int k = localXmlResourceParser.next();
            j = k;
            break;
            label228:
            bool4 = false;
            break label204;
            label234:
            if (str1.equals("log"))
            {
              String str6 = localXmlResourceParser.getAttributeValue(null, "level");
              Log.d("Config", "The <log> tags is deprecated. Use <preference name=\"loglevel\" value=\"" + str6 + "\"/> instead.");
              if (str6 != null) {
                LOG.setLogLevel(str6);
              }
            }
            else if (str1.equals("preference"))
            {
              String str3 = localXmlResourceParser.getAttributeValue(null, "name").toLowerCase(Locale.getDefault());
              if (str3.equalsIgnoreCase("LogLevel"))
              {
                LOG.setLogLevel(localXmlResourceParser.getAttributeValue(null, "value"));
              }
              else if (str3.equalsIgnoreCase("SplashScreen"))
              {
                String str5 = localXmlResourceParser.getAttributeValue(null, "value");
                if (str5 == null) {
                  str5 = "splash";
                }
                int i2 = paramActivity.getResources().getIdentifier(str5, "drawable", paramActivity.getClass().getPackage().getName());
                paramActivity.getIntent().putExtra(str3, i2);
              }
              else if (str3.equalsIgnoreCase("BackgroundColor"))
              {
                int i1 = localXmlResourceParser.getAttributeIntValue(null, "value", -16777216);
                paramActivity.getIntent().putExtra(str3, i1);
              }
              else if (str3.equalsIgnoreCase("LoadUrlTimeoutValue"))
              {
                int n = localXmlResourceParser.getAttributeIntValue(null, "value", 20000);
                paramActivity.getIntent().putExtra(str3, n);
              }
              else if (str3.equalsIgnoreCase("SplashScreenDelay"))
              {
                int m = localXmlResourceParser.getAttributeIntValue(null, "value", 3000);
                paramActivity.getIntent().putExtra(str3, m);
              }
              else if (str3.equalsIgnoreCase("KeepRunning"))
              {
                boolean bool3 = localXmlResourceParser.getAttributeValue(null, "value").equals("true");
                paramActivity.getIntent().putExtra(str3, bool3);
              }
              else if (str3.equalsIgnoreCase("InAppBrowserStorageEnabled"))
              {
                boolean bool2 = localXmlResourceParser.getAttributeValue(null, "value").equals("true");
                paramActivity.getIntent().putExtra(str3, bool2);
              }
              else if (str3.equalsIgnoreCase("DisallowOverscroll"))
              {
                boolean bool1 = localXmlResourceParser.getAttributeValue(null, "value").equals("true");
                paramActivity.getIntent().putExtra(str3, bool1);
              }
              else
              {
                String str4 = localXmlResourceParser.getAttributeValue(null, "value");
                paramActivity.getIntent().putExtra(str3, str4);
              }
            }
            else if (str1.equals("content"))
            {
              String str2 = localXmlResourceParser.getAttributeValue(null, "src");
              LOG.i("CordovaLog", "Found start page location: %s", new Object[] { str2 });
              if (str2 != null) {
                if (Pattern.compile("^[a-z-]+://").matcher(str2).find())
                {
                  this.startUrl = str2;
                }
                else
                {
                  if (str2.charAt(0) == '/') {
                    str2 = str2.substring(1);
                  }
                  this.startUrl = ("file:///android_asset/www/" + str2);
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
  
  public static void addWhiteListEntry(String paramString, boolean paramBoolean)
  {
    if (self == null) {
      return;
    }
    self.whitelist.addWhiteListEntry(paramString, paramBoolean);
  }
  
  public static String getStartUrl()
  {
    if ((self == null) || (self.startUrl == null)) {
      return "file:///android_asset/www/index.html";
    }
    return self.startUrl;
  }
  
  public static void init()
  {
    if (self == null) {
      self = new Config();
    }
  }
  
  public static void init(Activity paramActivity)
  {
    self = new Config(paramActivity);
  }
  
  public static boolean isUrlWhiteListed(String paramString)
  {
    if (self == null) {
      return false;
    }
    return self.whitelist.isUrlWhiteListed(paramString);
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.Config
 * JD-Core Version:    0.7.0.1
 */