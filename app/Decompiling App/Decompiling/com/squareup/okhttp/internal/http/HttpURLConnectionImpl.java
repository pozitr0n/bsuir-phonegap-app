package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.Connection;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Route;
import com.squareup.okhttp.internal.AbstractOutputStream;
import com.squareup.okhttp.internal.FaultRecoveringOutputStream;
import com.squareup.okhttp.internal.Util;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.SocketPermission;
import java.net.URL;
import java.security.Permission;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocketFactory;

public class HttpURLConnectionImpl
  extends HttpURLConnection
{
  static final int HTTP_TEMP_REDIRECT = 307;
  private static final int MAX_REDIRECTS = 20;
  private static final int MAX_REPLAY_BUFFER_LENGTH = 8192;
  final ConnectionPool connectionPool;
  final CookieHandler cookieHandler;
  final Set<Route> failedRoutes;
  private FaultRecoveringOutputStream faultRecoveringRequestBody;
  private final boolean followProtocolRedirects;
  HostnameVerifier hostnameVerifier;
  protected HttpEngine httpEngine;
  protected IOException httpEngineFailure;
  final ProxySelector proxySelector;
  private final RawHeaders rawRequestHeaders = new RawHeaders();
  private int redirectionCount;
  final Proxy requestedProxy;
  final OkResponseCache responseCache;
  SSLSocketFactory sslSocketFactory;
  
  public HttpURLConnectionImpl(URL paramURL, OkHttpClient paramOkHttpClient, OkResponseCache paramOkResponseCache, Set<Route> paramSet)
  {
    super(paramURL);
    this.followProtocolRedirects = paramOkHttpClient.getFollowProtocolRedirects();
    this.failedRoutes = paramSet;
    this.requestedProxy = paramOkHttpClient.getProxy();
    this.proxySelector = paramOkHttpClient.getProxySelector();
    this.cookieHandler = paramOkHttpClient.getCookieHandler();
    this.connectionPool = paramOkHttpClient.getConnectionPool();
    this.sslSocketFactory = paramOkHttpClient.getSslSocketFactory();
    this.hostnameVerifier = paramOkHttpClient.getHostnameVerifier();
    this.responseCache = paramOkResponseCache;
  }
  
  private boolean execute(boolean paramBoolean)
    throws IOException
  {
    try
    {
      this.httpEngine.sendRequest();
      if (paramBoolean) {
        this.httpEngine.readResponse();
      }
      return true;
    }
    catch (IOException localIOException)
    {
      if (handleFailure(localIOException)) {
        return false;
      }
      throw localIOException;
    }
  }
  
  private HttpEngine getResponse()
    throws IOException
  {
    initHttpEngine();
    if (this.httpEngine.hasResponse()) {
      return this.httpEngine;
    }
    for (;;)
    {
      if (execute(true))
      {
        Retry localRetry = processResponseHeaders();
        if (localRetry == Retry.NONE)
        {
          this.httpEngine.automaticallyReleaseConnectionToPool();
          return this.httpEngine;
        }
        String str = this.method;
        OutputStream localOutputStream = this.httpEngine.getRequestBody();
        int i = getResponseCode();
        if ((i == 300) || (i == 301) || (i == 302) || (i == 303))
        {
          str = "GET";
          localOutputStream = null;
        }
        if ((localOutputStream != null) && (!(localOutputStream instanceof RetryableOutputStream))) {
          throw new HttpRetryException("Cannot retry streamed HTTP body", this.httpEngine.getResponseCode());
        }
        if (localRetry == Retry.DIFFERENT_CONNECTION) {
          this.httpEngine.automaticallyReleaseConnectionToPool();
        }
        this.httpEngine.release(false);
        this.httpEngine = newHttpEngine(str, this.rawRequestHeaders, this.httpEngine.getConnection(), (RetryableOutputStream)localOutputStream);
      }
    }
  }
  
  private boolean handleFailure(IOException paramIOException)
    throws IOException
  {
    RouteSelector localRouteSelector = this.httpEngine.routeSelector;
    if ((localRouteSelector != null) && (this.httpEngine.connection != null)) {
      localRouteSelector.connectFailed(this.httpEngine.connection, paramIOException);
    }
    OutputStream localOutputStream = this.httpEngine.getRequestBody();
    if ((localOutputStream == null) || ((localOutputStream instanceof RetryableOutputStream)) || ((this.faultRecoveringRequestBody != null) && (this.faultRecoveringRequestBody.isRecoverable()))) {}
    for (int i = 1; ((localRouteSelector == null) && (this.httpEngine.connection == null)) || ((localRouteSelector != null) && (!localRouteSelector.hasNext())) || (!isRecoverable(paramIOException)) || (i == 0); i = 0)
    {
      this.httpEngineFailure = paramIOException;
      return false;
    }
    this.httpEngine.release(true);
    if ((localOutputStream instanceof RetryableOutputStream)) {}
    for (RetryableOutputStream localRetryableOutputStream = (RetryableOutputStream)localOutputStream;; localRetryableOutputStream = null)
    {
      this.httpEngine = newHttpEngine(this.method, this.rawRequestHeaders, null, localRetryableOutputStream);
      this.httpEngine.routeSelector = localRouteSelector;
      if ((this.faultRecoveringRequestBody != null) && (this.faultRecoveringRequestBody.isRecoverable()))
      {
        this.httpEngine.sendRequest();
        this.faultRecoveringRequestBody.replaceStream(this.httpEngine.getRequestBody());
      }
      return true;
    }
  }
  
  private void initHttpEngine()
    throws IOException
  {
    if (this.httpEngineFailure != null) {
      throw this.httpEngineFailure;
    }
    if (this.httpEngine != null) {
      return;
    }
    this.connected = true;
    do
    {
      try
      {
        if (this.doOutput)
        {
          if (this.method.equals("GET")) {
            this.method = "POST";
          }
        }
        else
        {
          this.httpEngine = newHttpEngine(this.method, this.rawRequestHeaders, null, null);
          return;
        }
      }
      catch (IOException localIOException)
      {
        this.httpEngineFailure = localIOException;
        throw localIOException;
      }
    } while ((this.method.equals("POST")) || (this.method.equals("PUT")));
    throw new ProtocolException(this.method + " does not support writing");
  }
  
  private boolean isRecoverable(IOException paramIOException)
  {
    if (((paramIOException instanceof SSLHandshakeException)) && ((paramIOException.getCause() instanceof CertificateException))) {}
    for (int i = 1;; i = 0)
    {
      boolean bool = paramIOException instanceof ProtocolException;
      if ((i != 0) || (bool)) {
        break;
      }
      return true;
    }
    return false;
  }
  
  private HttpEngine newHttpEngine(String paramString, RawHeaders paramRawHeaders, Connection paramConnection, RetryableOutputStream paramRetryableOutputStream)
    throws IOException
  {
    if (this.url.getProtocol().equals("http")) {
      return new HttpEngine(this, paramString, paramRawHeaders, paramConnection, paramRetryableOutputStream);
    }
    if (this.url.getProtocol().equals("https")) {
      return new HttpsURLConnectionImpl.HttpsEngine(this, paramString, paramRawHeaders, paramConnection, paramRetryableOutputStream);
    }
    throw new AssertionError();
  }
  
  private Retry processResponseHeaders()
    throws IOException
  {
    if (this.httpEngine.connection != null) {}
    int i;
    for (Proxy localProxy = this.httpEngine.connection.getRoute().getProxy();; localProxy = this.requestedProxy)
    {
      i = getResponseCode();
      switch (i)
      {
      default: 
        return Retry.NONE;
      }
    }
    if (localProxy.type() != Proxy.Type.HTTP) {
      throw new ProtocolException("Received HTTP_PROXY_AUTH (407) code while not using proxy");
    }
    if (HttpAuthenticator.processAuthHeader(getResponseCode(), this.httpEngine.getResponseHeaders().getHeaders(), this.rawRequestHeaders, localProxy, this.url)) {
      return Retry.SAME_CONNECTION;
    }
    return Retry.NONE;
    if (!getInstanceFollowRedirects()) {
      return Retry.NONE;
    }
    int j = 1 + this.redirectionCount;
    this.redirectionCount = j;
    if (j > 20) {
      throw new ProtocolException("Too many redirects: " + this.redirectionCount);
    }
    if ((i == 307) && (!this.method.equals("GET")) && (!this.method.equals("HEAD"))) {
      return Retry.NONE;
    }
    String str = getHeaderField("Location");
    if (str == null) {
      return Retry.NONE;
    }
    URL localURL = this.url;
    this.url = new URL(localURL, str);
    if ((!this.url.getProtocol().equals("https")) && (!this.url.getProtocol().equals("http"))) {
      return Retry.NONE;
    }
    boolean bool1 = localURL.getProtocol().equals(this.url.getProtocol());
    if ((!bool1) && (!this.followProtocolRedirects)) {
      return Retry.NONE;
    }
    boolean bool2 = localURL.getHost().equals(this.url.getHost());
    if (Util.getEffectivePort(localURL) == Util.getEffectivePort(this.url)) {}
    for (int k = 1; (bool2) && (k != 0) && (bool1); k = 0) {
      return Retry.SAME_CONNECTION;
    }
    return Retry.DIFFERENT_CONNECTION;
  }
  
  public final void addRequestProperty(String paramString1, String paramString2)
  {
    if (this.connected) {
      throw new IllegalStateException("Cannot add request property after connection is made");
    }
    if (paramString1 == null) {
      throw new NullPointerException("field == null");
    }
    this.rawRequestHeaders.add(paramString1, paramString2);
  }
  
  public final void connect()
    throws IOException
  {
    initHttpEngine();
    while (!execute(false)) {}
  }
  
  public final void disconnect()
  {
    if (this.httpEngine != null)
    {
      if (this.httpEngine.hasResponse()) {
        Util.closeQuietly(this.httpEngine.getResponseBody());
      }
      this.httpEngine.release(true);
    }
  }
  
  final int getChunkLength()
  {
    return this.chunkLength;
  }
  
  public final InputStream getErrorStream()
  {
    try
    {
      HttpEngine localHttpEngine = getResponse();
      boolean bool = localHttpEngine.hasResponseBody();
      Object localObject = null;
      if (bool)
      {
        int i = localHttpEngine.getResponseCode();
        localObject = null;
        if (i >= 400)
        {
          InputStream localInputStream = localHttpEngine.getResponseBody();
          localObject = localInputStream;
        }
      }
      return localObject;
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  Set<Route> getFailedRoutes()
  {
    return this.failedRoutes;
  }
  
  final int getFixedContentLength()
  {
    return this.fixedContentLength;
  }
  
  public final String getHeaderField(int paramInt)
  {
    try
    {
      String str = getResponse().getResponseHeaders().getHeaders().getValue(paramInt);
      return str;
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  public final String getHeaderField(String paramString)
  {
    try
    {
      RawHeaders localRawHeaders = getResponse().getResponseHeaders().getHeaders();
      if (paramString == null) {
        return localRawHeaders.getStatusLine();
      }
      String str = localRawHeaders.get(paramString);
      return str;
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  public final String getHeaderFieldKey(int paramInt)
  {
    try
    {
      String str = getResponse().getResponseHeaders().getHeaders().getFieldName(paramInt);
      return str;
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  public final Map<String, List<String>> getHeaderFields()
  {
    try
    {
      Map localMap = getResponse().getResponseHeaders().getHeaders().toMultimap(true);
      return localMap;
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  protected HttpURLConnection getHttpConnectionToCache()
  {
    return this;
  }
  
  public HttpEngine getHttpEngine()
  {
    return this.httpEngine;
  }
  
  public final InputStream getInputStream()
    throws IOException
  {
    if (!this.doInput) {
      throw new ProtocolException("This protocol does not support input");
    }
    HttpEngine localHttpEngine = getResponse();
    if (getResponseCode() >= 400) {
      throw new FileNotFoundException(this.url.toString());
    }
    InputStream localInputStream = localHttpEngine.getResponseBody();
    if (localInputStream == null) {
      throw new ProtocolException("No response body exists; responseCode=" + getResponseCode());
    }
    return localInputStream;
  }
  
  public final OutputStream getOutputStream()
    throws IOException
  {
    connect();
    OutputStream localOutputStream = this.httpEngine.getRequestBody();
    if (localOutputStream == null) {
      throw new ProtocolException("method does not support a request body: " + this.method);
    }
    if (this.httpEngine.hasResponse()) {
      throw new ProtocolException("cannot write request body after response has been read");
    }
    if (this.faultRecoveringRequestBody == null) {
      this.faultRecoveringRequestBody = new FaultRecoveringOutputStream(8192, localOutputStream)
      {
        protected OutputStream replacementStream(IOException paramAnonymousIOException)
          throws IOException
        {
          if (((HttpURLConnectionImpl.this.httpEngine.getRequestBody() instanceof AbstractOutputStream)) && (((AbstractOutputStream)HttpURLConnectionImpl.this.httpEngine.getRequestBody()).isClosed())) {
            return null;
          }
          if (HttpURLConnectionImpl.this.handleFailure(paramAnonymousIOException)) {
            return HttpURLConnectionImpl.this.httpEngine.getRequestBody();
          }
          return null;
        }
      };
    }
    return this.faultRecoveringRequestBody;
  }
  
  public final Permission getPermission()
    throws IOException
  {
    String str = getURL().getHost();
    int i = Util.getEffectivePort(getURL());
    if (usingProxy())
    {
      InetSocketAddress localInetSocketAddress = (InetSocketAddress)this.requestedProxy.address();
      str = localInetSocketAddress.getHostName();
      i = localInetSocketAddress.getPort();
    }
    return new SocketPermission(str + ":" + i, "connect, resolve");
  }
  
  public final Map<String, List<String>> getRequestProperties()
  {
    if (this.connected) {
      throw new IllegalStateException("Cannot access request header fields after connection is set");
    }
    return this.rawRequestHeaders.toMultimap(false);
  }
  
  public final String getRequestProperty(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return this.rawRequestHeaders.get(paramString);
  }
  
  public final int getResponseCode()
    throws IOException
  {
    return getResponse().getResponseCode();
  }
  
  public String getResponseMessage()
    throws IOException
  {
    return getResponse().getResponseHeaders().getHeaders().getResponseMessage();
  }
  
  public final void setRequestProperty(String paramString1, String paramString2)
  {
    if (this.connected) {
      throw new IllegalStateException("Cannot set request property after connection is made");
    }
    if (paramString1 == null) {
      throw new NullPointerException("field == null");
    }
    this.rawRequestHeaders.set(paramString1, paramString2);
  }
  
  public final boolean usingProxy()
  {
    return (this.requestedProxy != null) && (this.requestedProxy.type() != Proxy.Type.DIRECT);
  }
  
  static enum Retry
  {
    static
    {
      DIFFERENT_CONNECTION = new Retry("DIFFERENT_CONNECTION", 2);
      Retry[] arrayOfRetry = new Retry[3];
      arrayOfRetry[0] = NONE;
      arrayOfRetry[1] = SAME_CONNECTION;
      arrayOfRetry[2] = DIFFERENT_CONNECTION;
      $VALUES = arrayOfRetry;
    }
    
    private Retry() {}
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.http.HttpURLConnectionImpl
 * JD-Core Version:    0.7.0.1
 */