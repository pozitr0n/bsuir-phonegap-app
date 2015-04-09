package com.squareup.okhttp;

import com.squareup.okhttp.internal.Platform;
import com.squareup.okhttp.internal.http.HttpAuthenticator;
import com.squareup.okhttp.internal.http.HttpEngine;
import com.squareup.okhttp.internal.http.HttpTransport;
import com.squareup.okhttp.internal.http.RawHeaders;
import com.squareup.okhttp.internal.http.SpdyTransport;
import com.squareup.okhttp.internal.spdy.SpdyConnection;
import com.squareup.okhttp.internal.spdy.SpdyConnection.Builder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public final class Connection
  implements Closeable
{
  private static final byte[] HTTP_11 = { 104, 116, 116, 112, 47, 49, 46, 49 };
  private static final byte[] NPN_PROTOCOLS = { 6, 115, 112, 100, 121, 47, 51, 8, 104, 116, 116, 112, 47, 49, 46, 49 };
  private static final byte[] SPDY3 = { 115, 112, 100, 121, 47, 51 };
  private boolean connected = false;
  private int httpMinorVersion = 1;
  private long idleStartTimeNs;
  private InputStream in;
  private OutputStream out;
  private final Route route;
  private Socket socket;
  private SpdyConnection spdyConnection;
  
  public Connection(Route paramRoute)
  {
    this.route = paramRoute;
  }
  
  private void makeTunnel(TunnelRequest paramTunnelRequest)
    throws IOException
  {
    RawHeaders localRawHeaders2;
    for (Object localObject = paramTunnelRequest.getRequestHeaders();; localObject = localRawHeaders2)
    {
      this.out.write(((RawHeaders)localObject).toBytes());
      RawHeaders localRawHeaders1 = RawHeaders.fromBytes(this.in);
      switch (localRawHeaders1.getResponseCode())
      {
      default: 
        throw new IOException("Unexpected response code for CONNECT: " + localRawHeaders1.getResponseCode());
      case 407: 
        localRawHeaders2 = new RawHeaders((RawHeaders)localObject);
        URL localURL = new URL("https", paramTunnelRequest.host, paramTunnelRequest.port, "/");
        if (!HttpAuthenticator.processAuthHeader(407, localRawHeaders1, localRawHeaders2, this.route.proxy, localURL)) {
          break label144;
        }
      }
    }
    label144:
    throw new IOException("Failed to authenticate with proxy");
  }
  
  private void upgradeToTls(TunnelRequest paramTunnelRequest)
    throws IOException
  {
    Platform localPlatform = Platform.get();
    if (requiresTunnel()) {
      makeTunnel(paramTunnelRequest);
    }
    this.socket = this.route.address.sslSocketFactory.createSocket(this.socket, this.route.address.uriHost, this.route.address.uriPort, true);
    SSLSocket localSSLSocket = (SSLSocket)this.socket;
    if (this.route.modernTls) {
      localPlatform.enableTlsExtensions(localSSLSocket, this.route.address.uriHost);
    }
    for (;;)
    {
      if (this.route.modernTls) {
        localPlatform.setNpnProtocols(localSSLSocket, NPN_PROTOCOLS);
      }
      localSSLSocket.startHandshake();
      if (this.route.address.hostnameVerifier.verify(this.route.address.uriHost, localSSLSocket.getSession())) {
        break;
      }
      throw new IOException("Hostname '" + this.route.address.uriHost + "' was not verified");
      localPlatform.supportTlsIntolerantServer(localSSLSocket);
    }
    this.out = localSSLSocket.getOutputStream();
    this.in = localSSLSocket.getInputStream();
    byte[] arrayOfByte;
    if (this.route.modernTls)
    {
      arrayOfByte = localPlatform.getNpnSelectedProtocol(localSSLSocket);
      if (arrayOfByte != null)
      {
        if (!Arrays.equals(arrayOfByte, SPDY3)) {
          break label282;
        }
        localSSLSocket.setSoTimeout(0);
        this.spdyConnection = new SpdyConnection.Builder(this.route.address.getUriHost(), true, this.in, this.out).build();
      }
    }
    label282:
    while (Arrays.equals(arrayOfByte, HTTP_11)) {
      return;
    }
    throw new IOException("Unexpected NPN transport " + new String(arrayOfByte, "ISO-8859-1"));
  }
  
  public void close()
    throws IOException
  {
    this.socket.close();
  }
  
  public void connect(int paramInt1, int paramInt2, TunnelRequest paramTunnelRequest)
    throws IOException
  {
    if (this.connected) {
      throw new IllegalStateException("already connected");
    }
    this.connected = true;
    if (this.route.proxy.type() != Proxy.Type.HTTP) {}
    for (Socket localSocket = new Socket(this.route.proxy);; localSocket = new Socket())
    {
      this.socket = localSocket;
      this.socket.connect(this.route.inetSocketAddress, paramInt1);
      this.socket.setSoTimeout(paramInt2);
      this.in = this.socket.getInputStream();
      this.out = this.socket.getOutputStream();
      if (this.route.address.sslSocketFactory != null) {
        upgradeToTls(paramTunnelRequest);
      }
      int i = Platform.get().getMtu(this.socket);
      this.in = new BufferedInputStream(this.in, i);
      this.out = new BufferedOutputStream(this.out, i);
      return;
    }
  }
  
  public int getHttpMinorVersion()
  {
    return this.httpMinorVersion;
  }
  
  public long getIdleStartTimeNs()
  {
    if (this.spdyConnection == null) {
      return this.idleStartTimeNs;
    }
    return this.spdyConnection.getIdleStartTimeNs();
  }
  
  public Route getRoute()
  {
    return this.route;
  }
  
  public Socket getSocket()
  {
    return this.socket;
  }
  
  public SpdyConnection getSpdyConnection()
  {
    return this.spdyConnection;
  }
  
  public boolean isAlive()
  {
    return (!this.socket.isClosed()) && (!this.socket.isInputShutdown()) && (!this.socket.isOutputShutdown());
  }
  
  public boolean isConnected()
  {
    return this.connected;
  }
  
  public boolean isExpired(long paramLong)
  {
    return (isIdle()) && (System.nanoTime() - getIdleStartTimeNs() > paramLong);
  }
  
  public boolean isIdle()
  {
    return (this.spdyConnection == null) || (this.spdyConnection.isIdle());
  }
  
  public boolean isSpdy()
  {
    return this.spdyConnection != null;
  }
  
  public Object newTransport(HttpEngine paramHttpEngine)
    throws IOException
  {
    if (this.spdyConnection != null) {
      return new SpdyTransport(paramHttpEngine, this.spdyConnection);
    }
    return new HttpTransport(paramHttpEngine, this.out, this.in);
  }
  
  public boolean requiresTunnel()
  {
    return (this.route.address.sslSocketFactory != null) && (this.route.proxy.type() == Proxy.Type.HTTP);
  }
  
  public void resetIdleStartTime()
  {
    if (this.spdyConnection != null) {
      throw new IllegalStateException("spdyConnection != null");
    }
    this.idleStartTimeNs = System.nanoTime();
  }
  
  public void setHttpMinorVersion(int paramInt)
  {
    this.httpMinorVersion = paramInt;
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.Connection
 * JD-Core Version:    0.7.0.1
 */