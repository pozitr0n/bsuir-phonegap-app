

com.squareup.okhttp.internal.Util
java.net.Proxy
java.net.UnknownHostException
javax.net.ssl.HostnameVerifier
javax.net.ssl.SSLSocketFactory

Address

  hostnameVerifier
  proxy
  sslSocketFactory
  uriHost
  uriPort
  
  Address, , , , 
    
  
     (== {
      "uriHost == null"
    
     (<=0 {
      "uriPort <= 0: "
    
    proxy = paramProxy;
    this.uriHost = paramString;
    this.uriPort = paramInt;
    this.sslSocketFactory = paramSSLSocketFactory;
    this.hostnameVerifier = paramHostnameVerifier;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool1 = paramObject instanceof Address;
    boolean bool2 = false;
    if (bool1)
    {
      Address localAddress = (Address)paramObject;
      boolean bool3 = Util.equal(this.proxy, localAddress.proxy);
      bool2 = false;
      if (bool3)
      {
        boolean bool4 = this.uriHost.equals(localAddress.uriHost);
        bool2 = false;
        if (bool4)
        {
          int i = this.uriPort;
          int j = localAddress.uriPort;
          bool2 = false;
          if (i == j)
          {
            boolean bool5 = Util.equal(this.sslSocketFactory, localAddress.sslSocketFactory);
            bool2 = false;
            if (bool5)
            {
              boolean bool6 = Util.equal(this.hostnameVerifier, localAddress.hostnameVerifier);
              bool2 = false;
              if (bool6) {
                bool2 = true;
              }
            }
          }
        }
      }
    }
    return bool2;
  }
  
  public HostnameVerifier getHostnameVerifier()
  {
    return this.hostnameVerifier;
  }
  
  public Proxy getProxy()
  {
    return this.proxy;
  }
  
  public SSLSocketFactory getSslSocketFactory()
  {
    return this.sslSocketFactory;
  }
  
  public String getUriHost()
  {
    return this.uriHost;
  }
  
  public int getUriPort()
  {
    return this.uriPort;
  }
  
  public int hashCode()
  {
    int i = 31 * (31 * (527 + this.uriHost.hashCode()) + this.uriPort);
    int j;
    int k;
    if (this.sslSocketFactory != null)
    {
      j = this.sslSocketFactory.hashCode();
      k = 31 * (i + j);
      if (this.hostnameVerifier == null) {
        break label104;
      }
    }
    label104:
    for (int m = this.hostnameVerifier.hashCode();; m = 0)
    {
      int n = 31 * (k + m);
      Proxy localProxy = this.proxy;
      int i1 = 0;
      if (localProxy != null) {
        i1 = this.proxy.hashCode();
      }
      return n + i1;
      j = 0;
      break;
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.Address
 * JD-Core Version:    0.7.0.1
 */