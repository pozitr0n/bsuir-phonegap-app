package com.squareup.okhttp;

import com.squareup.okhttp.internal.http.HttpURLConnectionImpl;
import com.squareup.okhttp.internal.http.HttpsURLConnectionImpl;
import com.squareup.okhttp.internal.http.OkResponseCache;
import com.squareup.okhttp.internal.http.OkResponseCacheAdapter;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.ResponseCache;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public final class OkHttpClient
{
  private ConnectionPool connectionPool;
  private CookieHandler cookieHandler;
  private Set<Route> failedRoutes = Collections.synchronizedSet(new LinkedHashSet());
  private boolean followProtocolRedirects = true;
  private HostnameVerifier hostnameVerifier;
  private Proxy proxy;
  private ProxySelector proxySelector;
  private ResponseCache responseCache;
  private SSLSocketFactory sslSocketFactory;
  
  private OkHttpClient copyWithDefaults()
  {
    OkHttpClient localOkHttpClient = new OkHttpClient();
    localOkHttpClient.proxy = this.proxy;
    localOkHttpClient.failedRoutes = this.failedRoutes;
    ProxySelector localProxySelector;
    CookieHandler localCookieHandler;
    label53:
    ResponseCache localResponseCache;
    label71:
    SSLSocketFactory localSSLSocketFactory;
    label90:
    HostnameVerifier localHostnameVerifier;
    if (this.proxySelector != null)
    {
      localProxySelector = this.proxySelector;
      localOkHttpClient.proxySelector = localProxySelector;
      if (this.cookieHandler == null) {
        break label151;
      }
      localCookieHandler = this.cookieHandler;
      localOkHttpClient.cookieHandler = localCookieHandler;
      if (this.responseCache == null) {
        break label158;
      }
      localResponseCache = this.responseCache;
      localOkHttpClient.responseCache = localResponseCache;
      if (this.sslSocketFactory == null) {
        break label166;
      }
      localSSLSocketFactory = this.sslSocketFactory;
      localOkHttpClient.sslSocketFactory = localSSLSocketFactory;
      if (this.hostnameVerifier == null) {
        break label174;
      }
      localHostnameVerifier = this.hostnameVerifier;
      label109:
      localOkHttpClient.hostnameVerifier = localHostnameVerifier;
      if (this.connectionPool == null) {
        break label182;
      }
    }
    label151:
    label158:
    label166:
    label174:
    label182:
    for (ConnectionPool localConnectionPool = this.connectionPool;; localConnectionPool = ConnectionPool.getDefault())
    {
      localOkHttpClient.connectionPool = localConnectionPool;
      localOkHttpClient.followProtocolRedirects = this.followProtocolRedirects;
      return localOkHttpClient;
      localProxySelector = ProxySelector.getDefault();
      break;
      localCookieHandler = CookieHandler.getDefault();
      break label53;
      localResponseCache = ResponseCache.getDefault();
      break label71;
      localSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
      break label90;
      localHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
      break label109;
    }
  }
  
  private OkResponseCache okResponseCache()
  {
    if ((this.responseCache instanceof HttpResponseCache)) {
      return ((HttpResponseCache)this.responseCache).okResponseCache;
    }
    if (this.responseCache != null) {
      return new OkResponseCacheAdapter(this.responseCache);
    }
    return null;
  }
  
  public ConnectionPool getConnectionPool()
  {
    return this.connectionPool;
  }
  
  public CookieHandler getCookieHandler()
  {
    return this.cookieHandler;
  }
  
  public boolean getFollowProtocolRedirects()
  {
    return this.followProtocolRedirects;
  }
  
  public HostnameVerifier getHostnameVerifier()
  {
    return this.hostnameVerifier;
  }
  
  public Proxy getProxy()
  {
    return this.proxy;
  }
  
  public ProxySelector getProxySelector()
  {
    return this.proxySelector;
  }
  
  public ResponseCache getResponseCache()
  {
    return this.responseCache;
  }
  
  public SSLSocketFactory getSslSocketFactory()
  {
    return this.sslSocketFactory;
  }
  
  public HttpURLConnection open(URL paramURL)
  {
    String str = paramURL.getProtocol();
    OkHttpClient localOkHttpClient = copyWithDefaults();
    if (str.equals("http")) {
      return new HttpURLConnectionImpl(paramURL, localOkHttpClient, localOkHttpClient.okResponseCache(), localOkHttpClient.failedRoutes);
    }
    if (str.equals("https")) {
      return new HttpsURLConnectionImpl(paramURL, localOkHttpClient, localOkHttpClient.okResponseCache(), localOkHttpClient.failedRoutes);
    }
    throw new IllegalArgumentException("Unexpected protocol: " + str);
  }
  
  public OkHttpClient setConnectionPool(ConnectionPool paramConnectionPool)
  {
    this.connectionPool = paramConnectionPool;
    return this;
  }
  
  public OkHttpClient setCookieHandler(CookieHandler paramCookieHandler)
  {
    this.cookieHandler = paramCookieHandler;
    return this;
  }
  
  public OkHttpClient setFollowProtocolRedirects(boolean paramBoolean)
  {
    this.followProtocolRedirects = paramBoolean;
    return this;
  }
  
  public OkHttpClient setHostnameVerifier(HostnameVerifier paramHostnameVerifier)
  {
    this.hostnameVerifier = paramHostnameVerifier;
    return this;
  }
  
  public OkHttpClient setProxy(Proxy paramProxy)
  {
    this.proxy = paramProxy;
    return this;
  }
  
  public OkHttpClient setProxySelector(ProxySelector paramProxySelector)
  {
    this.proxySelector = paramProxySelector;
    return this;
  }
  
  public OkHttpClient setResponseCache(ResponseCache paramResponseCache)
  {
    this.responseCache = paramResponseCache;
    return this;
  }
  
  public OkHttpClient setSSLSocketFactory(SSLSocketFactory paramSSLSocketFactory)
  {
    this.sslSocketFactory = paramSSLSocketFactory;
    return this;
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.OkHttpClient
 * JD-Core Version:    0.7.0.1
 */