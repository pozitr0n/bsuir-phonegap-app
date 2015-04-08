package org.apache.cordova;

import android.net.Uri;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Whitelist
{
  public static final String TAG = "Whitelist";
  private ArrayList<URLPattern> whiteList = new ArrayList();
  
  public void addWhiteListEntry(String paramString, boolean paramBoolean)
  {
    if (this.whiteList != null) {}
    for (;;)
    {
      String str1;
      String str2;
      String str3;
      String str4;
      try
      {
        if (paramString.compareTo("*") == 0)
        {
          LOG.d("Whitelist", "Unlimited access to network resources");
          this.whiteList = null;
          return;
        }
        Matcher localMatcher = Pattern.compile("^((\\*|[a-z-]+)://)?(\\*|((\\*\\.)?[^*/:]+))?(:(\\d+))?(/.*)?").matcher(paramString);
        if (!localMatcher.matches()) {
          break label197;
        }
        str1 = localMatcher.group(2);
        str2 = localMatcher.group(3);
        if (("file".equals(str1)) || ("content".equals(str1))) {
          break label198;
        }
        str3 = localMatcher.group(7);
        str4 = localMatcher.group(8);
        if (str1 == null)
        {
          this.whiteList.add(new URLPattern("http", str2, str3, str4));
          this.whiteList.add(new URLPattern("https", str2, str3, str4));
          return;
        }
      }
      catch (Exception localException)
      {
        LOG.d("Whitelist", "Failed to add origin %s", new Object[] { paramString });
        return;
      }
      this.whiteList.add(new URLPattern(str1, str2, str3, str4));
      label197:
      return;
      label198:
      if (str2 == null) {
        str2 = "*";
      }
    }
  }
  
  public boolean isUrlWhiteListed(String paramString)
  {
    if (this.whiteList == null) {
      return true;
    }
    Uri localUri = Uri.parse(paramString);
    Iterator localIterator = this.whiteList.iterator();
    while (localIterator.hasNext()) {
      if (((URLPattern)localIterator.next()).matches(localUri)) {
        return true;
      }
    }
    return false;
  }
  
  private static class URLPattern
  {
    public Pattern host;
    public Pattern path;
    public Integer port;
    public Pattern scheme;
    
    public URLPattern(String paramString1, String paramString2, String paramString3, String paramString4)
      throws MalformedURLException
    {
      if (paramString1 != null) {}
      for (;;)
      {
        try
        {
          if ("*".equals(paramString1))
          {
            this.scheme = null;
            if ("*".equals(paramString2))
            {
              this.host = null;
              if ((paramString3 != null) && (!"*".equals(paramString3))) {
                break label166;
              }
              this.port = null;
              if ((paramString4 != null) && (!"/*".equals(paramString4))) {
                break;
              }
              this.path = null;
            }
          }
          else
          {
            this.scheme = Pattern.compile(regexFromPattern(paramString1, false));
            continue;
          }
          if (!paramString2.startsWith("*.")) {
            break label150;
          }
        }
        catch (NumberFormatException localNumberFormatException)
        {
          throw new MalformedURLException("Port must be a number");
        }
        this.host = Pattern.compile("([a-z0-9.-]*\\.)?" + regexFromPattern(paramString2.substring(2), false));
        continue;
        label150:
        this.host = Pattern.compile(regexFromPattern(paramString2, false));
        continue;
        label166:
        this.port = Integer.valueOf(Integer.parseInt(paramString3, 10));
      }
      this.path = Pattern.compile(regexFromPattern(paramString4, true));
    }
    
    private String regexFromPattern(String paramString, boolean paramBoolean)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      int i = 0;
      if (i < paramString.length())
      {
        char c = paramString.charAt(i);
        if ((c == '*') && (paramBoolean)) {
          localStringBuilder.append(".");
        }
        for (;;)
        {
          localStringBuilder.append(c);
          i++;
          break;
          if ("\\.[]{}()^$?+|".indexOf(c) > -1) {
            localStringBuilder.append('\\');
          }
        }
      }
      return localStringBuilder.toString();
    }
    
    public boolean matches(Uri paramUri)
    {
      try
      {
        boolean bool1;
        if (this.scheme != null)
        {
          boolean bool5 = this.scheme.matcher(paramUri.getScheme()).matches();
          bool1 = false;
          if (!bool5) {}
        }
        else if (this.host != null)
        {
          boolean bool4 = this.host.matcher(paramUri.getHost()).matches();
          bool1 = false;
          if (!bool4) {}
        }
        else if (this.port != null)
        {
          boolean bool3 = this.port.equals(Integer.valueOf(paramUri.getPort()));
          bool1 = false;
          if (!bool3) {}
        }
        else if (this.path != null)
        {
          boolean bool2 = this.path.matcher(paramUri.getPath()).matches();
          bool1 = false;
          if (!bool2) {}
        }
        else
        {
          bool1 = true;
        }
        return bool1;
      }
      catch (Exception localException)
      {
        LOG.d("Whitelist", localException.toString());
      }
      return false;
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.Whitelist
 * JD-Core Version:    0.7.0.1
 */