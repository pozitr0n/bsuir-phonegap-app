package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.Address;
import com.squareup.okhttp.Connection;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.Route;
import com.squareup.okhttp.internal.Dns;
import com.squareup.okhttp.internal.Util;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.net.ssl.SSLHandshakeException;

public final class RouteSelector
{
  private static final int TLS_MODE_COMPATIBLE = 0;
  private static final int TLS_MODE_MODERN = 1;
  private static final int TLS_MODE_NULL = -1;
  private final Address address;
  private final Dns dns;
  private final Set<Route> failedRoutes;
  private boolean hasNextProxy;
  private InetSocketAddress lastInetSocketAddress;
  private Proxy lastProxy;
  private int nextSocketAddressIndex;
  private int nextTlsMode = -1;
  private final ConnectionPool pool;
  private final List<Route> postponedRoutes;
  private final ProxySelector proxySelector;
  private Iterator<Proxy> proxySelectorProxies;
  private InetAddress[] socketAddresses;
  private int socketPort;
  private final URI uri;
  private Proxy userSpecifiedProxy;
  
  public RouteSelector(Address paramAddress, URI paramURI, ProxySelector paramProxySelector, ConnectionPool paramConnectionPool, Dns paramDns, Set<Route> paramSet)
  {
    this.address = paramAddress;
    this.uri = paramURI;
    this.proxySelector = paramProxySelector;
    this.pool = paramConnectionPool;
    this.dns = paramDns;
    this.failedRoutes = paramSet;
    this.postponedRoutes = new LinkedList();
    resetNextProxy(paramURI, paramAddress.getProxy());
  }
  
  private boolean hasNextInetSocketAddress()
  {
    return this.socketAddresses != null;
  }
  
  private boolean hasNextPostponed()
  {
    return !this.postponedRoutes.isEmpty();
  }
  
  private boolean hasNextProxy()
  {
    return this.hasNextProxy;
  }
  
  private boolean hasNextTlsMode()
  {
    return this.nextTlsMode != -1;
  }
  
  private InetSocketAddress nextInetSocketAddress()
    throws UnknownHostException
  {
    InetAddress[] arrayOfInetAddress = this.socketAddresses;
    int i = this.nextSocketAddressIndex;
    this.nextSocketAddressIndex = (i + 1);
    InetSocketAddress localInetSocketAddress = new InetSocketAddress(arrayOfInetAddress[i], this.socketPort);
    if (this.nextSocketAddressIndex == this.socketAddresses.length)
    {
      this.socketAddresses = null;
      this.nextSocketAddressIndex = 0;
    }
    return localInetSocketAddress;
  }
  
  private Route nextPostponed()
  {
    return (Route)this.postponedRoutes.remove(0);
  }
  
  private Proxy nextProxy()
  {
    if (this.userSpecifiedProxy != null)
    {
      this.hasNextProxy = false;
      return this.userSpecifiedProxy;
    }
    if (this.proxySelectorProxies != null) {
      while (this.proxySelectorProxies.hasNext())
      {
        Proxy localProxy = (Proxy)this.proxySelectorProxies.next();
        if (localProxy.type() != Proxy.Type.DIRECT) {
          return localProxy;
        }
      }
    }
    this.hasNextProxy = false;
    return Proxy.NO_PROXY;
  }
  
  private int nextTlsMode()
  {
    if (this.nextTlsMode == 1)
    {
      this.nextTlsMode = 0;
      return 1;
    }
    if (this.nextTlsMode == 0)
    {
      this.nextTlsMode = -1;
      return 0;
    }
    throw new AssertionError();
  }
  
  private void resetNextInetSocketAddress(Proxy paramProxy)
    throws UnknownHostException
  {
    this.socketAddresses = null;
    String str;
    if (paramProxy.type() == Proxy.Type.DIRECT) {
      str = this.uri.getHost();
    }
    InetSocketAddress localInetSocketAddress;
    for (this.socketPort = Util.getEffectivePort(this.uri);; this.socketPort = localInetSocketAddress.getPort())
    {
      this.socketAddresses = this.dns.getAllByName(str);
      this.nextSocketAddressIndex = 0;
      return;
      SocketAddress localSocketAddress = paramProxy.address();
      if (!(localSocketAddress instanceof InetSocketAddress)) {
        throw new IllegalArgumentException("Proxy.address() is not an InetSocketAddress: " + localSocketAddress.getClass());
      }
      localInetSocketAddress = (InetSocketAddress)localSocketAddress;
      str = localInetSocketAddress.getHostName();
    }
  }
  
  private void resetNextProxy(URI paramURI, Proxy paramProxy)
  {
    this.hasNextProxy = true;
    if (paramProxy != null) {
      this.userSpecifiedProxy = paramProxy;
    }
    List localList;
    do
    {
      return;
      localList = this.proxySelector.select(paramURI);
    } while (localList == null);
    this.proxySelectorProxies = localList.iterator();
  }
  
  private void resetNextTlsMode()
  {
    if (this.address.getSslSocketFactory() != null) {}
    for (int i = 1;; i = 0)
    {
      this.nextTlsMode = i;
      return;
    }
  }
  
  public void connectFailed(Connection paramConnection, IOException paramIOException)
  {
    Route localRoute = paramConnection.getRoute();
    if ((localRoute.getProxy().type() != Proxy.Type.DIRECT) && (this.proxySelector != null)) {
      this.proxySelector.connectFailed(this.uri, localRoute.getProxy().address(), paramIOException);
    }
    this.failedRoutes.add(localRoute);
    if (!(paramIOException instanceof SSLHandshakeException)) {
      this.failedRoutes.add(localRoute.flipTlsMode());
    }
  }
  
  public boolean hasNext()
  {
    return (hasNextTlsMode()) || (hasNextInetSocketAddress()) || (hasNextProxy()) || (hasNextPostponed());
  }
  
  public Connection next()
    throws IOException
  {
    int i = 1;
    Connection localConnection = this.pool.get(this.address);
    if (localConnection != null) {
      return localConnection;
    }
    if (!hasNextTlsMode())
    {
      if (!hasNextInetSocketAddress())
      {
        if (!hasNextProxy())
        {
          if (!hasNextPostponed()) {
            throw new NoSuchElementException();
          }
          return new Connection(nextPostponed());
        }
        this.lastProxy = nextProxy();
        resetNextInetSocketAddress(this.lastProxy);
      }
      this.lastInetSocketAddress = nextInetSocketAddress();
      resetNextTlsMode();
    }
    if (nextTlsMode() == i) {}
    Route localRoute;
    for (;;)
    {
      localRoute = new Route(this.address, this.lastProxy, this.lastInetSocketAddress, i);
      if (!this.failedRoutes.contains(localRoute)) {
        break;
      }
      this.postponedRoutes.add(localRoute);
      return next();
      int j = 0;
    }
    return new Connection(localRoute);
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.http.RouteSelector
 * JD-Core Version:    0.7.0.1
 */