package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.internal.Base64;
import java.io.IOException;
import java.net.Authenticator;
import java.net.Authenticator.RequestorType;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class HttpAuthenticator
{
  private static InetAddress getConnectToInetAddress(Proxy paramProxy, URL paramURL)
    throws IOException
  {
    if ((paramProxy != null) && (paramProxy.type() != Proxy.Type.DIRECT)) {
      return ((InetSocketAddress)paramProxy.address()).getAddress();
    }
    return InetAddress.getByName(paramURL.getHost());
  }
  
  private static String getCredentials(RawHeaders paramRawHeaders, String paramString, Proxy paramProxy, URL paramURL)
    throws IOException
  {
    List localList = parseChallenges(paramRawHeaders, paramString);
    if (localList.isEmpty()) {
      return null;
    }
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      Challenge localChallenge = (Challenge)localIterator.next();
      InetSocketAddress localInetSocketAddress;
      if (paramRawHeaders.getResponseCode() == 407) {
        localInetSocketAddress = (InetSocketAddress)paramProxy.address();
      }
      for (PasswordAuthentication localPasswordAuthentication = Authenticator.requestPasswordAuthentication(localInetSocketAddress.getHostName(), getConnectToInetAddress(paramProxy, paramURL), localInetSocketAddress.getPort(), paramURL.getProtocol(), localChallenge.realm, localChallenge.scheme, paramURL, Authenticator.RequestorType.PROXY); localPasswordAuthentication != null; localPasswordAuthentication = Authenticator.requestPasswordAuthentication(paramURL.getHost(), getConnectToInetAddress(paramProxy, paramURL), paramURL.getPort(), paramURL.getProtocol(), localChallenge.realm, localChallenge.scheme, paramURL, Authenticator.RequestorType.SERVER))
      {
        String str = Base64.encode((localPasswordAuthentication.getUserName() + ":" + new String(localPasswordAuthentication.getPassword())).getBytes("ISO-8859-1"));
        return localChallenge.scheme + " " + str;
      }
    }
    return null;
  }
  
  private static List<Challenge> parseChallenges(RawHeaders paramRawHeaders, String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    if (i < paramRawHeaders.length())
    {
      if (!paramString.equalsIgnoreCase(paramRawHeaders.getFieldName(i))) {}
      label183:
      for (;;)
      {
        i++;
        break;
        String str1 = paramRawHeaders.getValue(i);
        int j = 0;
        for (;;)
        {
          if (j >= str1.length()) {
            break label183;
          }
          int k = j;
          int m = HeaderParser.skipUntil(str1, j, " ");
          String str2 = str1.substring(k, m).trim();
          int n = HeaderParser.skipWhitespace(str1, m);
          if (!str1.regionMatches(n, "realm=\"", 0, "realm=\"".length())) {
            break;
          }
          int i1 = n + "realm=\"".length();
          int i2 = HeaderParser.skipUntil(str1, i1, "\"");
          String str3 = str1.substring(i1, i2);
          j = HeaderParser.skipWhitespace(str1, 1 + HeaderParser.skipUntil(str1, i2 + 1, ","));
          localArrayList.add(new Challenge(str2, str3));
        }
      }
    }
    return localArrayList;
  }
  
  public static boolean processAuthHeader(int paramInt, RawHeaders paramRawHeaders1, RawHeaders paramRawHeaders2, Proxy paramProxy, URL paramURL)
    throws IOException
  {
    if ((paramInt != 407) && (paramInt != 401)) {
      throw new IllegalArgumentException();
    }
    if (paramInt == 407) {}
    String str2;
    for (String str1 = "Proxy-Authenticate";; str1 = "WWW-Authenticate")
    {
      str2 = getCredentials(paramRawHeaders1, str1, paramProxy, paramURL);
      if (str2 != null) {
        break;
      }
      return false;
    }
    if (paramInt == 407) {}
    for (String str3 = "Proxy-Authorization";; str3 = "Authorization")
    {
      paramRawHeaders2.set(str3, str2);
      return true;
    }
  }
  
  private static final class Challenge
  {
    final String realm;
    final String scheme;
    
    Challenge(String paramString1, String paramString2)
    {
      this.scheme = paramString1;
      this.realm = paramString2;
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof Challenge)) && (((Challenge)paramObject).scheme.equals(this.scheme)) && (((Challenge)paramObject).realm.equals(this.realm));
    }
    
    public int hashCode()
    {
      return this.scheme.hashCode() + 31 * this.realm.hashCode();
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.http.HttpAuthenticator
 * JD-Core Version:    0.7.0.1
 */