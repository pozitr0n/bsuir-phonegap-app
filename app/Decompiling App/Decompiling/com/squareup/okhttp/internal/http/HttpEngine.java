package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.Address;
import com.squareup.okhttp.Connection;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.ResponseSource;
import com.squareup.okhttp.Route;
import com.squareup.okhttp.TunnelRequest;
import com.squareup.okhttp.internal.Dns;
import com.squareup.okhttp.internal.Platform;
import com.squareup.okhttp.internal.Util;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.CookieHandler;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

public class HttpEngine
{
  private static final CacheResponse GATEWAY_TIMEOUT_RESPONSE = new CacheResponse()
  {
    public InputStream getBody()
      throws IOException
    {
      return new ByteArrayInputStream(Util.EMPTY_BYTE_ARRAY);
    }
    
    public Map<String, List<String>> getHeaders()
      throws IOException
    {
      HashMap localHashMap = new HashMap();
      localHashMap.put(null, Collections.singletonList("HTTP/1.1 504 Gateway Timeout"));
      return localHashMap;
    }
  };
  public static final int HTTP_CONTINUE = 100;
  private boolean automaticallyReleaseConnectionToPool;
  private CacheRequest cacheRequest;
  private CacheResponse cacheResponse;
  private InputStream cachedResponseBody;
  private ResponseHeaders cachedResponseHeaders;
  protected Connection connection;
  private boolean connectionReleased;
  protected final String method;
  protected final HttpURLConnectionImpl policy;
  private OutputStream requestBodyOut;
  final RequestHeaders requestHeaders;
  private InputStream responseBodyIn;
  ResponseHeaders responseHeaders;
  private ResponseSource responseSource;
  private InputStream responseTransferIn;
  protected RouteSelector routeSelector;
  long sentRequestMillis = -1L;
  private boolean transparentGzip;
  private Transport transport;
  final URI uri;
  
  public HttpEngine(HttpURLConnectionImpl paramHttpURLConnectionImpl, String paramString, RawHeaders paramRawHeaders, Connection paramConnection, RetryableOutputStream paramRetryableOutputStream)
    throws IOException
  {
    this.policy = paramHttpURLConnectionImpl;
    this.method = paramString;
    this.connection = paramConnection;
    this.requestBodyOut = paramRetryableOutputStream;
    try
    {
      this.uri = Platform.get().toUriLenient(paramHttpURLConnectionImpl.getURL());
      this.requestHeaders = new RequestHeaders(this.uri, new RawHeaders(paramRawHeaders));
      return;
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new IOException(localURISyntaxException.getMessage());
    }
  }
  
  public static String getDefaultUserAgent()
  {
    String str = System.getProperty("http.agent");
    if (str != null) {
      return str;
    }
    return "Java" + System.getProperty("java.version");
  }
  
  public static String getOriginAddress(URL paramURL)
  {
    int i = paramURL.getPort();
    String str = paramURL.getHost();
    if ((i > 0) && (i != Util.getDefaultPort(paramURL.getProtocol()))) {
      str = str + ":" + i;
    }
    return str;
  }
  
  private void initContentStream(InputStream paramInputStream)
    throws IOException
  {
    this.responseTransferIn = paramInputStream;
    if ((this.transparentGzip) && (this.responseHeaders.isContentEncodingGzip()))
    {
      this.responseHeaders.stripContentEncoding();
      this.responseHeaders.stripContentLength();
      this.responseBodyIn = new GZIPInputStream(paramInputStream);
      return;
    }
    this.responseBodyIn = paramInputStream;
  }
  
  private void initResponseSource()
    throws IOException
  {
    this.responseSource = ResponseSource.NETWORK;
    if ((!this.policy.getUseCaches()) || (this.policy.responseCache == null)) {}
    CacheResponse localCacheResponse;
    do
    {
      return;
      localCacheResponse = this.policy.responseCache.get(this.uri, this.method, this.requestHeaders.getHeaders().toMultimap(false));
    } while (localCacheResponse == null);
    Map localMap = localCacheResponse.getHeaders();
    this.cachedResponseBody = localCacheResponse.getBody();
    if ((!acceptCacheResponseType(localCacheResponse)) || (localMap == null) || (this.cachedResponseBody == null))
    {
      Util.closeQuietly(this.cachedResponseBody);
      return;
    }
    RawHeaders localRawHeaders = RawHeaders.fromMultimap(localMap, true);
    this.cachedResponseHeaders = new ResponseHeaders(this.uri, localRawHeaders);
    long l = System.currentTimeMillis();
    this.responseSource = this.cachedResponseHeaders.chooseResponseSource(l, this.requestHeaders);
    if (this.responseSource == ResponseSource.CACHE)
    {
      this.cacheResponse = localCacheResponse;
      setResponse(this.cachedResponseHeaders, this.cachedResponseBody);
      return;
    }
    if (this.responseSource == ResponseSource.CONDITIONAL_CACHE)
    {
      this.cacheResponse = localCacheResponse;
      return;
    }
    if (this.responseSource == ResponseSource.NETWORK)
    {
      Util.closeQuietly(this.cachedResponseBody);
      return;
    }
    throw new AssertionError();
  }
  
  private void maybeCache()
    throws IOException
  {
    if ((!this.policy.getUseCaches()) || (this.policy.responseCache == null)) {}
    while (!this.responseHeaders.isCacheable(this.requestHeaders)) {
      return;
    }
    this.cacheRequest = this.policy.responseCache.put(this.uri, this.policy.getHttpConnectionToCache());
  }
  
  private void prepareRawRequestHeaders()
    throws IOException
  {
    this.requestHeaders.getHeaders().setRequestLine(getRequestLine());
    if (this.requestHeaders.getUserAgent() == null) {
      this.requestHeaders.setUserAgent(getDefaultUserAgent());
    }
    if (this.requestHeaders.getHost() == null) {
      this.requestHeaders.setHost(getOriginAddress(this.policy.getURL()));
    }
    if (((this.connection == null) || (this.connection.getHttpMinorVersion() != 0)) && (this.requestHeaders.getConnection() == null)) {
      this.requestHeaders.setConnection("Keep-Alive");
    }
    if (this.requestHeaders.getAcceptEncoding() == null)
    {
      this.transparentGzip = true;
      this.requestHeaders.setAcceptEncoding("gzip");
    }
    if ((hasRequestBody()) && (this.requestHeaders.getContentType() == null)) {
      this.requestHeaders.setContentType("application/x-www-form-urlencoded");
    }
    long l = this.policy.getIfModifiedSince();
    if (l != 0L) {
      this.requestHeaders.setIfModifiedSince(new Date(l));
    }
    CookieHandler localCookieHandler = this.policy.cookieHandler;
    if (localCookieHandler != null) {
      this.requestHeaders.addCookies(localCookieHandler.get(this.uri, this.requestHeaders.getHeaders().toMultimap(false)));
    }
  }
  
  public static String requestPath(URL paramURL)
  {
    String str = paramURL.getFile();
    if (str == null) {
      str = "/";
    }
    while (str.startsWith("/")) {
      return str;
    }
    return "/" + str;
  }
  
  private String requestString()
  {
    URL localURL = this.policy.getURL();
    if (includeAuthorityInRequestLine()) {
      return localURL.toString();
    }
    return requestPath(localURL);
  }
  
  private void sendSocketRequest()
    throws IOException
  {
    if (this.connection == null) {
      connect();
    }
    if (this.transport != null) {
      throw new IllegalStateException();
    }
    this.transport = ((Transport)this.connection.newTransport(this));
    if ((hasRequestBody()) && (this.requestBodyOut == null)) {
      this.requestBodyOut = this.transport.createRequestBody();
    }
  }
  
  private void setResponse(ResponseHeaders paramResponseHeaders, InputStream paramInputStream)
    throws IOException
  {
    if (this.responseBodyIn != null) {
      throw new IllegalStateException();
    }
    this.responseHeaders = paramResponseHeaders;
    if (paramInputStream != null) {
      initContentStream(paramInputStream);
    }
  }
  
  protected boolean acceptCacheResponseType(CacheResponse paramCacheResponse)
  {
    return true;
  }
  
  public final void automaticallyReleaseConnectionToPool()
  {
    this.automaticallyReleaseConnectionToPool = true;
    if ((this.connection != null) && (this.connectionReleased))
    {
      this.policy.connectionPool.recycle(this.connection);
      this.connection = null;
    }
  }
  
  protected final void connect()
    throws IOException
  {
    if (this.connection != null) {}
    do
    {
      return;
      if (this.routeSelector == null)
      {
        String str = this.uri.getHost();
        if (str == null) {
          throw new UnknownHostException(this.uri.toString());
        }
        boolean bool = this.uri.getScheme().equalsIgnoreCase("https");
        SSLSocketFactory localSSLSocketFactory = null;
        HostnameVerifier localHostnameVerifier = null;
        if (bool)
        {
          localSSLSocketFactory = this.policy.sslSocketFactory;
          localHostnameVerifier = this.policy.hostnameVerifier;
        }
        this.routeSelector = new RouteSelector(new Address(str, Util.getEffectivePort(this.uri), localSSLSocketFactory, localHostnameVerifier, this.policy.requestedProxy), this.uri, this.policy.proxySelector, this.policy.connectionPool, Dns.DEFAULT, this.policy.getFailedRoutes());
      }
      this.connection = this.routeSelector.next();
      if (!this.connection.isConnected())
      {
        this.connection.connect(this.policy.getConnectTimeout(), this.policy.getReadTimeout(), getTunnelConfig());
        this.policy.connectionPool.maybeShare(this.connection);
        this.policy.getFailedRoutes().remove(this.connection.getRoute());
      }
      connected(this.connection);
    } while (this.connection.getRoute().getProxy() == this.policy.requestedProxy);
    this.requestHeaders.getHeaders().setRequestLine(getRequestLine());
  }
  
  protected void connected(Connection paramConnection) {}
  
  public final CacheResponse getCacheResponse()
  {
    return this.cacheResponse;
  }
  
  public final Connection getConnection()
  {
    return this.connection;
  }
  
  public final OutputStream getRequestBody()
  {
    if (this.responseSource == null) {
      throw new IllegalStateException();
    }
    return this.requestBodyOut;
  }
  
  public final RequestHeaders getRequestHeaders()
  {
    return this.requestHeaders;
  }
  
  String getRequestLine()
  {
    if ((this.connection == null) || (this.connection.getHttpMinorVersion() != 0)) {}
    for (String str = "HTTP/1.1";; str = "HTTP/1.0") {
      return this.method + " " + requestString() + " " + str;
    }
  }
  
  public final InputStream getResponseBody()
  {
    if (this.responseHeaders == null) {
      throw new IllegalStateException();
    }
    return this.responseBodyIn;
  }
  
  public final int getResponseCode()
  {
    if (this.responseHeaders == null) {
      throw new IllegalStateException();
    }
    return this.responseHeaders.getHeaders().getResponseCode();
  }
  
  public final ResponseHeaders getResponseHeaders()
  {
    if (this.responseHeaders == null) {
      throw new IllegalStateException();
    }
    return this.responseHeaders;
  }
  
  protected TunnelRequest getTunnelConfig()
  {
    return null;
  }
  
  public URI getUri()
  {
    return this.uri;
  }
  
  boolean hasRequestBody()
  {
    return (this.method.equals("POST")) || (this.method.equals("PUT"));
  }
  
  public final boolean hasResponse()
  {
    return this.responseHeaders != null;
  }
  
  public final boolean hasResponseBody()
  {
    int i = this.responseHeaders.getHeaders().getResponseCode();
    if (this.method.equals("HEAD")) {}
    do
    {
      return false;
      if (((i < 100) || (i >= 200)) && (i != 204) && (i != 304)) {
        return true;
      }
    } while ((this.responseHeaders.getContentLength() == -1) && (!this.responseHeaders.isChunked()));
    return true;
  }
  
  protected boolean includeAuthorityInRequestLine()
  {
    if (this.connection == null) {
      return this.policy.usingProxy();
    }
    return this.connection.getRoute().getProxy().type() == Proxy.Type.HTTP;
  }
  
  public final void readResponse()
    throws IOException
  {
    if (hasResponse()) {
      this.responseHeaders.setResponseSource(this.responseSource);
    }
    do
    {
      return;
      if (this.responseSource == null) {
        throw new IllegalStateException("readResponse() without sendRequest()");
      }
    } while (!this.responseSource.requiresConnection());
    if (this.sentRequestMillis == -1L)
    {
      if ((this.requestBodyOut instanceof RetryableOutputStream))
      {
        int i = ((RetryableOutputStream)this.requestBodyOut).contentLength();
        this.requestHeaders.setContentLength(i);
      }
      this.transport.writeRequestHeaders();
    }
    if (this.requestBodyOut != null)
    {
      this.requestBodyOut.close();
      if ((this.requestBodyOut instanceof RetryableOutputStream)) {
        this.transport.writeRequestBody((RetryableOutputStream)this.requestBodyOut);
      }
    }
    this.transport.flushRequest();
    this.responseHeaders = this.transport.readResponseHeaders();
    this.responseHeaders.setLocalTimestamps(this.sentRequestMillis, System.currentTimeMillis());
    this.responseHeaders.setResponseSource(this.responseSource);
    if (this.responseSource == ResponseSource.CONDITIONAL_CACHE)
    {
      if (this.cachedResponseHeaders.validate(this.responseHeaders))
      {
        release(false);
        setResponse(this.cachedResponseHeaders.combine(this.responseHeaders), this.cachedResponseBody);
        this.policy.responseCache.trackConditionalCacheHit();
        this.policy.responseCache.update(this.cacheResponse, this.policy.getHttpConnectionToCache());
        return;
      }
      Util.closeQuietly(this.cachedResponseBody);
    }
    if (hasResponseBody()) {
      maybeCache();
    }
    initContentStream(this.transport.getTransferStream(this.cacheRequest));
  }
  
  public void receiveHeaders(RawHeaders paramRawHeaders)
    throws IOException
  {
    CookieHandler localCookieHandler = this.policy.cookieHandler;
    if (localCookieHandler != null) {
      localCookieHandler.put(this.uri, paramRawHeaders.toMultimap(true));
    }
  }
  
  public final void release(boolean paramBoolean)
  {
    if (this.responseBodyIn == this.cachedResponseBody) {
      Util.closeQuietly(this.responseBodyIn);
    }
    if ((!this.connectionReleased) && (this.connection != null))
    {
      this.connectionReleased = true;
      if ((this.transport != null) && (this.transport.makeReusable(paramBoolean, this.requestBodyOut, this.responseTransferIn))) {
        break label78;
      }
      Util.closeQuietly(this.connection);
      this.connection = null;
    }
    label78:
    while (!this.automaticallyReleaseConnectionToPool) {
      return;
    }
    this.policy.connectionPool.recycle(this.connection);
    this.connection = null;
  }
  
  public final void sendRequest()
    throws IOException
  {
    if (this.responseSource != null) {}
    do
    {
      return;
      prepareRawRequestHeaders();
      initResponseSource();
      if (this.policy.responseCache != null) {
        this.policy.responseCache.trackResponse(this.responseSource);
      }
      if ((this.requestHeaders.isOnlyIfCached()) && (this.responseSource.requiresConnection()))
      {
        if (this.responseSource == ResponseSource.CONDITIONAL_CACHE) {
          Util.closeQuietly(this.cachedResponseBody);
        }
        this.responseSource = ResponseSource.CACHE;
        this.cacheResponse = GATEWAY_TIMEOUT_RESPONSE;
        RawHeaders localRawHeaders = RawHeaders.fromMultimap(this.cacheResponse.getHeaders(), true);
        setResponse(new ResponseHeaders(this.uri, localRawHeaders), this.cacheResponse.getBody());
      }
      if (this.responseSource.requiresConnection())
      {
        sendSocketRequest();
        return;
      }
    } while (this.connection == null);
    this.policy.connectionPool.recycle(this.connection);
    this.policy.getFailedRoutes().remove(this.connection.getRoute());
    this.connection = null;
  }
  
  public void writingRequestHeaders()
  {
    if (this.sentRequestMillis != -1L) {
      throw new IllegalStateException();
    }
    this.sentRequestMillis = System.currentTimeMillis();
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.http.HttpEngine
 * JD-Core Version:    0.7.0.1
 */