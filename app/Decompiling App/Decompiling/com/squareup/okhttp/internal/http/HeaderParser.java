package com.squareup.okhttp.internal.http;

final class HeaderParser
{
  public static void parseCacheControl(String paramString, CacheControlHandler paramCacheControlHandler)
  {
    int i = 0;
    while (i < paramString.length())
    {
      int j = i;
      int k = skipUntil(paramString, i, "=,");
      String str1 = paramString.substring(j, k).trim();
      if ((k == paramString.length()) || (paramString.charAt(k) == ','))
      {
        i = k + 1;
        paramCacheControlHandler.handle(str1, null);
      }
      else
      {
        int m = skipWhitespace(paramString, k + 1);
        String str2;
        if ((m < paramString.length()) && (paramString.charAt(m) == '"'))
        {
          int n = m + 1;
          int i1 = skipUntil(paramString, n, "\"");
          str2 = paramString.substring(n, i1);
          i = i1 + 1;
        }
        for (;;)
        {
          paramCacheControlHandler.handle(str1, str2);
          break;
          i = skipUntil(paramString, m, ",");
          str2 = paramString.substring(m, i).trim();
        }
      }
    }
  }
  
  public static int parseSeconds(String paramString)
  {
    try
    {
      long l = Long.parseLong(paramString);
      if (l > 2147483647L) {
        return 2147483647;
      }
      if (l < 0L) {
        return 0;
      }
      return (int)l;
    }
    catch (NumberFormatException localNumberFormatException) {}
    return -1;
  }
  
  public static int skipUntil(String paramString1, int paramInt, String paramString2)
  {
    for (;;)
    {
      if ((paramInt >= paramString1.length()) || (paramString2.indexOf(paramString1.charAt(paramInt)) != -1)) {
        return paramInt;
      }
      paramInt++;
    }
  }
  
  public static int skipWhitespace(String paramString, int paramInt)
  {
    for (;;)
    {
      if (paramInt < paramString.length())
      {
        int i = paramString.charAt(paramInt);
        if ((i == 32) || (i == 9)) {}
      }
      else
      {
        return paramInt;
      }
      paramInt++;
    }
  }
  
  public static abstract interface CacheControlHandler
  {
    public abstract void handle(String paramString1, String paramString2);
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.http.HeaderParser
 * JD-Core Version:    0.7.0.1
 */