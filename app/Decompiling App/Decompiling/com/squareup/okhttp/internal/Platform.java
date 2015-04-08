package com.squareup.okhttp.internal;

import com.squareup.okhttp.OkHttpClient;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import javax.net.ssl.SSLSocket;

public class Platform
{
  private static final Platform PLATFORM = ;
  private Constructor<DeflaterOutputStream> deflaterConstructor;
  
  /* Error */
  private static Platform findPlatform()
  {
    // Byte code:
    //   0: ldc 26
    //   2: ldc 28
    //   4: iconst_0
    //   5: anewarray 30	java/lang/Class
    //   8: invokevirtual 34	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   11: astore_1
    //   12: ldc 36
    //   14: invokestatic 40	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   17: astore 11
    //   19: iconst_1
    //   20: anewarray 30	java/lang/Class
    //   23: astore 12
    //   25: aload 12
    //   27: iconst_0
    //   28: getstatic 46	java/lang/Boolean:TYPE	Ljava/lang/Class;
    //   31: aastore
    //   32: aload 11
    //   34: ldc 48
    //   36: aload 12
    //   38: invokevirtual 34	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   41: astore 13
    //   43: aload 11
    //   45: ldc 50
    //   47: iconst_1
    //   48: anewarray 30	java/lang/Class
    //   51: dup
    //   52: iconst_0
    //   53: ldc 52
    //   55: aastore
    //   56: invokevirtual 34	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   59: astore 14
    //   61: new 54	com/squareup/okhttp/internal/Platform$Android41
    //   64: dup
    //   65: aload_1
    //   66: aload 11
    //   68: aload 13
    //   70: aload 14
    //   72: aload 11
    //   74: ldc 56
    //   76: iconst_1
    //   77: anewarray 30	java/lang/Class
    //   80: dup
    //   81: iconst_0
    //   82: ldc 58
    //   84: aastore
    //   85: invokevirtual 34	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   88: aload 11
    //   90: ldc 60
    //   92: iconst_0
    //   93: anewarray 30	java/lang/Class
    //   96: invokevirtual 34	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   99: aconst_null
    //   100: invokespecial 63	com/squareup/okhttp/internal/Platform$Android41:<init>	(Ljava/lang/reflect/Method;Ljava/lang/Class;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Lcom/squareup/okhttp/internal/Platform$1;)V
    //   103: astore 15
    //   105: aload 15
    //   107: areturn
    //   108: astore_0
    //   109: new 2	com/squareup/okhttp/internal/Platform
    //   112: dup
    //   113: invokespecial 64	com/squareup/okhttp/internal/Platform:<init>	()V
    //   116: areturn
    //   117: astore 16
    //   119: new 66	com/squareup/okhttp/internal/Platform$Android23
    //   122: dup
    //   123: aload_1
    //   124: aload 11
    //   126: aload 13
    //   128: aload 14
    //   130: aconst_null
    //   131: invokespecial 69	com/squareup/okhttp/internal/Platform$Android23:<init>	(Ljava/lang/reflect/Method;Ljava/lang/Class;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Lcom/squareup/okhttp/internal/Platform$1;)V
    //   134: astore 17
    //   136: aload 17
    //   138: areturn
    //   139: astore 10
    //   141: ldc 71
    //   143: invokestatic 40	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   146: astore 5
    //   148: new 73	java/lang/StringBuilder
    //   151: dup
    //   152: invokespecial 74	java/lang/StringBuilder:<init>	()V
    //   155: ldc 71
    //   157: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   160: ldc 80
    //   162: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   165: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   168: invokestatic 40	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   171: astore 6
    //   173: new 73	java/lang/StringBuilder
    //   176: dup
    //   177: invokespecial 74	java/lang/StringBuilder:<init>	()V
    //   180: ldc 71
    //   182: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   185: ldc 86
    //   187: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   190: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   193: invokestatic 40	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   196: astore 7
    //   198: new 73	java/lang/StringBuilder
    //   201: dup
    //   202: invokespecial 74	java/lang/StringBuilder:<init>	()V
    //   205: ldc 71
    //   207: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   210: ldc 88
    //   212: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   215: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   218: invokestatic 40	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   221: astore 8
    //   223: new 90	com/squareup/okhttp/internal/Platform$JdkWithJettyNpnPlatform
    //   226: dup
    //   227: aload_1
    //   228: aload 5
    //   230: ldc 92
    //   232: iconst_2
    //   233: anewarray 30	java/lang/Class
    //   236: dup
    //   237: iconst_0
    //   238: ldc 94
    //   240: aastore
    //   241: dup
    //   242: iconst_1
    //   243: aload 6
    //   245: aastore
    //   246: invokevirtual 34	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   249: aload 5
    //   251: ldc 96
    //   253: iconst_1
    //   254: anewarray 30	java/lang/Class
    //   257: dup
    //   258: iconst_0
    //   259: ldc 94
    //   261: aastore
    //   262: invokevirtual 34	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   265: aload 7
    //   267: aload 8
    //   269: invokespecial 99	com/squareup/okhttp/internal/Platform$JdkWithJettyNpnPlatform:<init>	(Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;Ljava/lang/Class;Ljava/lang/Class;)V
    //   272: astore 9
    //   274: aload 9
    //   276: areturn
    //   277: astore 4
    //   279: aload_1
    //   280: ifnull +13 -> 293
    //   283: new 101	com/squareup/okhttp/internal/Platform$Java5
    //   286: dup
    //   287: aload_1
    //   288: aconst_null
    //   289: invokespecial 104	com/squareup/okhttp/internal/Platform$Java5:<init>	(Ljava/lang/reflect/Method;Lcom/squareup/okhttp/internal/Platform$1;)V
    //   292: areturn
    //   293: new 2	com/squareup/okhttp/internal/Platform
    //   296: dup
    //   297: invokespecial 64	com/squareup/okhttp/internal/Platform:<init>	()V
    //   300: areturn
    //   301: astore_3
    //   302: goto -23 -> 279
    //   305: astore_2
    //   306: goto -165 -> 141
    // Local variable table:
    //   start	length	slot	name	signature
    //   108	1	0	localNoSuchMethodException1	NoSuchMethodException
    //   11	277	1	localMethod1	Method
    //   305	1	2	localClassNotFoundException1	java.lang.ClassNotFoundException
    //   301	1	3	localClassNotFoundException2	java.lang.ClassNotFoundException
    //   277	1	4	localNoSuchMethodException2	NoSuchMethodException
    //   146	104	5	localClass1	Class
    //   171	73	6	localClass2	Class
    //   196	70	7	localClass3	Class
    //   221	47	8	localClass4	Class
    //   272	3	9	localJdkWithJettyNpnPlatform	JdkWithJettyNpnPlatform
    //   139	1	10	localNoSuchMethodException3	NoSuchMethodException
    //   17	108	11	localClass5	Class
    //   23	14	12	arrayOfClass	Class[]
    //   41	86	13	localMethod2	Method
    //   59	70	14	localMethod3	Method
    //   103	3	15	localAndroid41	Android41
    //   117	1	16	localNoSuchMethodException4	NoSuchMethodException
    //   134	3	17	localAndroid23	Android23
    // Exception table:
    //   from	to	target	type
    //   0	12	108	java/lang/NoSuchMethodException
    //   61	105	117	java/lang/NoSuchMethodException
    //   12	61	139	java/lang/NoSuchMethodException
    //   119	136	139	java/lang/NoSuchMethodException
    //   141	274	277	java/lang/NoSuchMethodException
    //   141	274	301	java/lang/ClassNotFoundException
    //   12	61	305	java/lang/ClassNotFoundException
    //   61	105	305	java/lang/ClassNotFoundException
    //   119	136	305	java/lang/ClassNotFoundException
  }
  
  public static Platform get()
  {
    return PLATFORM;
  }
  
  public void enableTlsExtensions(SSLSocket paramSSLSocket, String paramString) {}
  
  public int getMtu(Socket paramSocket)
    throws IOException
  {
    return 1400;
  }
  
  public byte[] getNpnSelectedProtocol(SSLSocket paramSSLSocket)
  {
    return null;
  }
  
  public void logW(String paramString)
  {
    System.out.println(paramString);
  }
  
  public OutputStream newDeflaterOutputStream(OutputStream paramOutputStream, Deflater paramDeflater, boolean paramBoolean)
  {
    try
    {
      Constructor localConstructor = this.deflaterConstructor;
      if (localConstructor == null)
      {
        Class[] arrayOfClass = new Class[3];
        arrayOfClass[0] = OutputStream.class;
        arrayOfClass[1] = Deflater.class;
        arrayOfClass[2] = Boolean.TYPE;
        localConstructor = DeflaterOutputStream.class.getConstructor(arrayOfClass);
        this.deflaterConstructor = localConstructor;
      }
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramOutputStream;
      arrayOfObject[1] = paramDeflater;
      arrayOfObject[2] = Boolean.valueOf(paramBoolean);
      OutputStream localOutputStream = (OutputStream)localConstructor.newInstance(arrayOfObject);
      return localOutputStream;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new UnsupportedOperationException("Cannot SPDY; no SYNC_FLUSH available");
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      if ((localInvocationTargetException.getCause() instanceof RuntimeException)) {}
      for (RuntimeException localRuntimeException = (RuntimeException)localInvocationTargetException.getCause();; localRuntimeException = new RuntimeException(localInvocationTargetException.getCause())) {
        throw localRuntimeException;
      }
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new RuntimeException(localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new AssertionError();
    }
  }
  
  public void setNpnProtocols(SSLSocket paramSSLSocket, byte[] paramArrayOfByte) {}
  
  public void supportTlsIntolerantServer(SSLSocket paramSSLSocket)
  {
    paramSSLSocket.setEnabledProtocols(new String[] { "SSLv3" });
  }
  
  public void tagSocket(Socket paramSocket)
    throws SocketException
  {}
  
  public URI toUriLenient(URL paramURL)
    throws URISyntaxException
  {
    return paramURL.toURI();
  }
  
  public void untagSocket(Socket paramSocket)
    throws SocketException
  {}
  
  private static class Android23
    extends Platform.Java5
  {
    protected final Class<?> openSslSocketClass;
    private final Method setHostname;
    private final Method setUseSessionTickets;
    
    private Android23(Method paramMethod1, Class<?> paramClass, Method paramMethod2, Method paramMethod3)
    {
      super(null);
      this.openSslSocketClass = paramClass;
      this.setUseSessionTickets = paramMethod2;
      this.setHostname = paramMethod3;
    }
    
    public void enableTlsExtensions(SSLSocket paramSSLSocket, String paramString)
    {
      super.enableTlsExtensions(paramSSLSocket, paramString);
      if (this.openSslSocketClass.isInstance(paramSSLSocket)) {}
      try
      {
        Method localMethod = this.setUseSessionTickets;
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Boolean.valueOf(true);
        localMethod.invoke(paramSSLSocket, arrayOfObject);
        this.setHostname.invoke(paramSSLSocket, new Object[] { paramString });
        return;
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw new RuntimeException(localInvocationTargetException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
    }
  }
  
  private static class Android41
    extends Platform.Android23
  {
    private final Method getNpnSelectedProtocol;
    private final Method setNpnProtocols;
    
    private Android41(Method paramMethod1, Class<?> paramClass, Method paramMethod2, Method paramMethod3, Method paramMethod4, Method paramMethod5)
    {
      super(paramClass, paramMethod2, paramMethod3, null);
      this.setNpnProtocols = paramMethod4;
      this.getNpnSelectedProtocol = paramMethod5;
    }
    
    public byte[] getNpnSelectedProtocol(SSLSocket paramSSLSocket)
    {
      if (!this.openSslSocketClass.isInstance(paramSSLSocket)) {
        return null;
      }
      try
      {
        byte[] arrayOfByte = (byte[])this.getNpnSelectedProtocol.invoke(paramSSLSocket, new Object[0]);
        return arrayOfByte;
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw new RuntimeException(localInvocationTargetException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
    }
    
    public void setNpnProtocols(SSLSocket paramSSLSocket, byte[] paramArrayOfByte)
    {
      if (!this.openSslSocketClass.isInstance(paramSSLSocket)) {
        return;
      }
      try
      {
        this.setNpnProtocols.invoke(paramSSLSocket, new Object[] { paramArrayOfByte });
        return;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw new RuntimeException(localInvocationTargetException);
      }
    }
  }
  
  private static class Java5
    extends Platform
  {
    private final Method getMtu;
    
    private Java5(Method paramMethod)
    {
      this.getMtu = paramMethod;
    }
    
    public int getMtu(Socket paramSocket)
      throws IOException
    {
      try
      {
        NetworkInterface localNetworkInterface = NetworkInterface.getByInetAddress(paramSocket.getLocalAddress());
        int i = ((Integer)this.getMtu.invoke(localNetworkInterface, new Object[0])).intValue();
        return i;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        if ((localInvocationTargetException.getCause() instanceof IOException)) {
          throw ((IOException)localInvocationTargetException.getCause());
        }
        throw new RuntimeException(localInvocationTargetException.getCause());
      }
    }
  }
  
  private static class JdkWithJettyNpnPlatform
    extends Platform.Java5
  {
    private final Class<?> clientProviderClass;
    private final Method getMethod;
    private final Method putMethod;
    private final Class<?> serverProviderClass;
    
    public JdkWithJettyNpnPlatform(Method paramMethod1, Method paramMethod2, Method paramMethod3, Class<?> paramClass1, Class<?> paramClass2)
    {
      super(null);
      this.putMethod = paramMethod2;
      this.getMethod = paramMethod3;
      this.clientProviderClass = paramClass1;
      this.serverProviderClass = paramClass2;
    }
    
    public byte[] getNpnSelectedProtocol(SSLSocket paramSSLSocket)
    {
      try
      {
        Platform.JettyNpnProvider localJettyNpnProvider = (Platform.JettyNpnProvider)Proxy.getInvocationHandler(this.getMethod.invoke(null, new Object[] { paramSSLSocket }));
        if ((!Platform.JettyNpnProvider.access$300(localJettyNpnProvider)) && (Platform.JettyNpnProvider.access$400(localJettyNpnProvider) == null))
        {
          Logger.getLogger(OkHttpClient.class.getName()).log(Level.INFO, "NPN callback dropped so SPDY is disabled. Is npn-boot on the boot class path?");
          return null;
        }
        if (!Platform.JettyNpnProvider.access$300(localJettyNpnProvider))
        {
          byte[] arrayOfByte = Platform.JettyNpnProvider.access$400(localJettyNpnProvider).getBytes("US-ASCII");
          return arrayOfByte;
        }
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new AssertionError();
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw new AssertionError();
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError();
      }
      return null;
    }
    
    public void setNpnProtocols(SSLSocket paramSSLSocket, byte[] paramArrayOfByte)
    {
      try
      {
        ArrayList localArrayList = new ArrayList();
        int j;
        int k;
        for (int i = 0; i < paramArrayOfByte.length; i = j + k)
        {
          j = i + 1;
          k = paramArrayOfByte[i];
          localArrayList.add(new String(paramArrayOfByte, j, k, "US-ASCII"));
        }
        ClassLoader localClassLoader = Platform.class.getClassLoader();
        Class[] arrayOfClass = new Class[2];
        arrayOfClass[0] = this.clientProviderClass;
        arrayOfClass[1] = this.serverProviderClass;
        Object localObject = Proxy.newProxyInstance(localClassLoader, arrayOfClass, new Platform.JettyNpnProvider(localArrayList));
        this.putMethod.invoke(null, new Object[] { paramSSLSocket, localObject });
        return;
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new AssertionError(localUnsupportedEncodingException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw new AssertionError(localInvocationTargetException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
    }
  }
  
  private static class JettyNpnProvider
    implements InvocationHandler
  {
    private final List<String> protocols;
    private String selected;
    private boolean unsupported;
    
    public JettyNpnProvider(List<String> paramList)
    {
      this.protocols = paramList;
    }
    
    public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
      throws Throwable
    {
      String str = paramMethod.getName();
      Class localClass = paramMethod.getReturnType();
      if (paramArrayOfObject == null) {
        paramArrayOfObject = Util.EMPTY_STRING_ARRAY;
      }
      if ((str.equals("supports")) && (Boolean.TYPE == localClass)) {
        return Boolean.valueOf(true);
      }
      if ((str.equals("unsupported")) && (Void.TYPE == localClass))
      {
        this.unsupported = true;
        return null;
      }
      if ((str.equals("protocols")) && (paramArrayOfObject.length == 0)) {
        return this.protocols;
      }
      if ((str.equals("selectProtocol")) && (String.class == localClass) && (paramArrayOfObject.length == 1) && ((paramArrayOfObject[0] == null) || ((paramArrayOfObject[0] instanceof List))))
      {
        ((List)paramArrayOfObject[0]);
        this.selected = ((String)this.protocols.get(0));
        return this.selected;
      }
      if ((str.equals("protocolSelected")) && (paramArrayOfObject.length == 1))
      {
        this.selected = ((String)paramArrayOfObject[0]);
        return null;
      }
      return paramMethod.invoke(this, paramArrayOfObject);
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.Platform
 * JD-Core Version:    0.7.0.1
 */