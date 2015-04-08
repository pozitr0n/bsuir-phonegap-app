package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.OkResponseCache;
import com.squareup.okhttp.ResponseSource;
import com.squareup.okhttp.internal.DiskLruCache;
import com.squareup.okhttp.internal.DiskLruCache.Editor;
import com.squareup.okhttp.internal.DiskLruCache.Snapshot;
import com.squareup.okhttp.internal.StrictLineReader;
import com.squareup.okhttp.internal.Util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.HttpURLConnection;
import java.net.ResponseCache;
import java.net.SecureCacheResponse;
import java.net.URI;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public final class HttpResponseCache
  extends ResponseCache
  implements OkResponseCache
{
  private static final char[] DIGITS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
  private static final int ENTRY_BODY = 1;
  private static final int ENTRY_COUNT = 2;
  private static final int ENTRY_METADATA = 0;
  private static final int VERSION = 201105;
  private final DiskLruCache cache;
  private int hitCount;
  private int networkCount;
  private int requestCount;
  private int writeAbortCount;
  private int writeSuccessCount;
  
  public HttpResponseCache(File paramFile, long paramLong)
    throws IOException
  {
    this.cache = DiskLruCache.open(paramFile, 201105, 2, paramLong);
  }
  
  private void abortQuietly(DiskLruCache.Editor paramEditor)
  {
    if (paramEditor != null) {}
    try
    {
      paramEditor.abort();
      return;
    }
    catch (IOException localIOException) {}
  }
  
  private static String bytesToHexString(byte[] paramArrayOfByte)
  {
    char[] arrayOfChar1 = DIGITS;
    char[] arrayOfChar2 = new char[2 * paramArrayOfByte.length];
    int i = paramArrayOfByte.length;
    int j = 0;
    int k = 0;
    while (j < i)
    {
      int m = paramArrayOfByte[j];
      int n = k + 1;
      arrayOfChar2[k] = arrayOfChar1[(0xF & m >> 4)];
      k = n + 1;
      arrayOfChar2[n] = arrayOfChar1[(m & 0xF)];
      j++;
    }
    return new String(arrayOfChar2);
  }
  
  private HttpEngine getHttpEngine(URLConnection paramURLConnection)
  {
    if ((paramURLConnection instanceof HttpURLConnectionImpl)) {
      return ((HttpURLConnectionImpl)paramURLConnection).getHttpEngine();
    }
    if ((paramURLConnection instanceof HttpsURLConnectionImpl)) {
      return ((HttpsURLConnectionImpl)paramURLConnection).getHttpEngine();
    }
    return null;
  }
  
  private static InputStream newBodyInputStream(final DiskLruCache.Snapshot paramSnapshot)
  {
    new FilterInputStream(paramSnapshot.getInputStream(1))
    {
      public void close()
        throws IOException
      {
        paramSnapshot.close();
        super.close();
      }
    };
  }
  
  private String uriToKey(URI paramURI)
  {
    try
    {
      String str = bytesToHexString(MessageDigest.getInstance("MD5").digest(paramURI.toString().getBytes("UTF-8")));
      return str;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new AssertionError(localNoSuchAlgorithmException);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new AssertionError(localUnsupportedEncodingException);
    }
  }
  
  public CacheResponse get(URI paramURI, String paramString, Map<String, List<String>> paramMap)
  {
    String str = uriToKey(paramURI);
    DiskLruCache.Snapshot localSnapshot;
    Entry localEntry;
    try
    {
      localSnapshot = this.cache.get(str);
      if (localSnapshot == null) {
        return null;
      }
      localEntry = new Entry(localSnapshot.getInputStream(0));
      if (!localEntry.matches(paramURI, paramString, paramMap))
      {
        localSnapshot.close();
        return null;
      }
    }
    catch (IOException localIOException)
    {
      return null;
    }
    if (localEntry.isHttps()) {
      return new EntrySecureCacheResponse(localEntry, localSnapshot);
    }
    return new EntryCacheResponse(localEntry, localSnapshot);
  }
  
  public DiskLruCache getCache()
  {
    return this.cache;
  }
  
  public int getHitCount()
  {
    try
    {
      int i = this.hitCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public int getNetworkCount()
  {
    try
    {
      int i = this.networkCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public int getRequestCount()
  {
    try
    {
      int i = this.requestCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public int getWriteAbortCount()
  {
    try
    {
      int i = this.writeAbortCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public int getWriteSuccessCount()
  {
    try
    {
      int i = this.writeSuccessCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public CacheRequest put(URI paramURI, URLConnection paramURLConnection)
    throws IOException
  {
    if (!(paramURLConnection instanceof HttpURLConnection)) {}
    for (;;)
    {
      return null;
      HttpURLConnection localHttpURLConnection = (HttpURLConnection)paramURLConnection;
      String str1 = localHttpURLConnection.getRequestMethod();
      String str2 = uriToKey(paramURI);
      if ((str1.equals("POST")) || (str1.equals("PUT")) || (str1.equals("DELETE"))) {
        try
        {
          this.cache.remove(str2);
          return null;
        }
        catch (IOException localIOException1)
        {
          return null;
        }
      }
      if (str1.equals("GET"))
      {
        HttpEngine localHttpEngine = getHttpEngine(localHttpURLConnection);
        if (localHttpEngine != null)
        {
          ResponseHeaders localResponseHeaders = localHttpEngine.getResponseHeaders();
          if (!localResponseHeaders.hasVaryAll())
          {
            Entry localEntry = new Entry(paramURI, localHttpEngine.getRequestHeaders().getHeaders().getAll(localResponseHeaders.getVaryFields()), localHttpURLConnection);
            DiskLruCache.Editor localEditor = null;
            try
            {
              localEditor = this.cache.edit(str2);
              if (localEditor != null)
              {
                localEntry.writeTo(localEditor);
                CacheRequestImpl localCacheRequestImpl = new CacheRequestImpl(localEditor);
                return localCacheRequestImpl;
              }
            }
            catch (IOException localIOException2)
            {
              abortQuietly(localEditor);
            }
          }
        }
      }
    }
    return null;
  }
  
  public void trackConditionalCacheHit()
  {
    try
    {
      this.hitCount = (1 + this.hitCount);
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void trackResponse(ResponseSource paramResponseSource)
  {
    for (;;)
    {
      try
      {
        this.requestCount = (1 + this.requestCount);
        int i = 2.$SwitchMap$com$squareup$okhttp$ResponseSource[paramResponseSource.ordinal()];
        switch (i)
        {
        default: 
          return;
        }
      }
      finally {}
      this.hitCount = (1 + this.hitCount);
      continue;
      this.networkCount = (1 + this.networkCount);
    }
  }
  
  public void update(CacheResponse paramCacheResponse, HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    HttpEngine localHttpEngine = getHttpEngine(paramHttpURLConnection);
    URI localURI = localHttpEngine.getUri();
    ResponseHeaders localResponseHeaders = localHttpEngine.getResponseHeaders();
    Entry localEntry = new Entry(localURI, localHttpEngine.getRequestHeaders().getHeaders().getAll(localResponseHeaders.getVaryFields()), paramHttpURLConnection);
    if ((paramCacheResponse instanceof EntryCacheResponse)) {}
    for (DiskLruCache.Snapshot localSnapshot = ((EntryCacheResponse)paramCacheResponse).snapshot;; localSnapshot = ((EntrySecureCacheResponse)paramCacheResponse).snapshot)
    {
      DiskLruCache.Editor localEditor = null;
      try
      {
        localEditor = localSnapshot.edit();
        if (localEditor != null)
        {
          localEntry.writeTo(localEditor);
          localEditor.commit();
        }
        return;
      }
      catch (IOException localIOException)
      {
        abortQuietly(localEditor);
      }
    }
  }
  
  private final class CacheRequestImpl
    extends CacheRequest
  {
    private OutputStream body;
    private OutputStream cacheOut;
    private boolean done;
    private final DiskLruCache.Editor editor;
    
    public CacheRequestImpl(final DiskLruCache.Editor paramEditor)
      throws IOException
    {
      this.editor = paramEditor;
      this.cacheOut = paramEditor.newOutputStream(1);
      this.body = new FilterOutputStream(this.cacheOut)
      {
        public void close()
          throws IOException
        {
          synchronized (HttpResponseCache.this)
          {
            if (HttpResponseCache.CacheRequestImpl.this.done) {
              return;
            }
            HttpResponseCache.CacheRequestImpl.access$302(HttpResponseCache.CacheRequestImpl.this, true);
            HttpResponseCache.access$408(HttpResponseCache.this);
            super.close();
            paramEditor.commit();
            return;
          }
        }
        
        public void write(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
          throws IOException
        {
          this.out.write(paramAnonymousArrayOfByte, paramAnonymousInt1, paramAnonymousInt2);
        }
      };
    }
    
    public void abort()
    {
      synchronized (HttpResponseCache.this)
      {
        if (this.done) {
          return;
        }
        this.done = true;
        HttpResponseCache.access$508(HttpResponseCache.this);
        Util.closeQuietly(this.cacheOut);
        try
        {
          this.editor.abort();
          return;
        }
        catch (IOException localIOException) {}
      }
    }
    
    public OutputStream getBody()
      throws IOException
    {
      return this.body;
    }
  }
  
  private static final class Entry
  {
    private final String cipherSuite;
    private final Certificate[] localCertificates;
    private final Certificate[] peerCertificates;
    private final String requestMethod;
    private final RawHeaders responseHeaders;
    private final String uri;
    private final RawHeaders varyHeaders;
    
    public Entry(InputStream paramInputStream)
      throws IOException
    {
      StrictLineReader localStrictLineReader;
      try
      {
        localStrictLineReader = new StrictLineReader(paramInputStream, Util.US_ASCII);
        this.uri = localStrictLineReader.readLine();
        this.requestMethod = localStrictLineReader.readLine();
        this.varyHeaders = new RawHeaders();
        int i = localStrictLineReader.readInt();
        for (int j = 0; j < i; j++) {
          this.varyHeaders.addLine(localStrictLineReader.readLine());
        }
        this.responseHeaders = new RawHeaders();
        this.responseHeaders.setStatusLine(localStrictLineReader.readLine());
        int k = localStrictLineReader.readInt();
        for (int m = 0; m < k; m++) {
          this.responseHeaders.addLine(localStrictLineReader.readLine());
        }
        if (!isHttps()) {
          break label223;
        }
        String str = localStrictLineReader.readLine();
        if (!str.isEmpty()) {
          throw new IOException("expected \"\" but was \"" + str + "\"");
        }
      }
      finally
      {
        paramInputStream.close();
      }
      this.cipherSuite = localStrictLineReader.readLine();
      this.peerCertificates = readCertArray(localStrictLineReader);
      for (this.localCertificates = readCertArray(localStrictLineReader);; this.localCertificates = null)
      {
        paramInputStream.close();
        return;
        label223:
        this.cipherSuite = null;
        this.peerCertificates = null;
      }
    }
    
    public Entry(URI paramURI, RawHeaders paramRawHeaders, HttpURLConnection paramHttpURLConnection)
      throws IOException
    {
      this.uri = paramURI.toString();
      this.varyHeaders = paramRawHeaders;
      this.requestMethod = paramHttpURLConnection.getRequestMethod();
      this.responseHeaders = RawHeaders.fromMultimap(paramHttpURLConnection.getHeaderFields(), true);
      HttpsURLConnection localHttpsURLConnection;
      if (isHttps())
      {
        localHttpsURLConnection = (HttpsURLConnection)paramHttpURLConnection;
        this.cipherSuite = localHttpsURLConnection.getCipherSuite();
      }
      try
      {
        Certificate[] arrayOfCertificate2 = localHttpsURLConnection.getServerCertificates();
        arrayOfCertificate1 = arrayOfCertificate2;
      }
      catch (SSLPeerUnverifiedException localSSLPeerUnverifiedException)
      {
        for (;;)
        {
          Certificate[] arrayOfCertificate1 = null;
        }
      }
      this.peerCertificates = arrayOfCertificate1;
      this.localCertificates = localHttpsURLConnection.getLocalCertificates();
      return;
      this.cipherSuite = null;
      this.peerCertificates = null;
      this.localCertificates = null;
    }
    
    private boolean isHttps()
    {
      return this.uri.startsWith("https://");
    }
    
    /* Error */
    private Certificate[] readCertArray(StrictLineReader paramStrictLineReader)
      throws IOException
    {
      // Byte code:
      //   0: aload_1
      //   1: invokevirtual 49	com/squareup/okhttp/internal/StrictLineReader:readInt	()I
      //   4: istore_2
      //   5: iload_2
      //   6: iconst_m1
      //   7: if_icmpne +9 -> 16
      //   10: aconst_null
      //   11: astore 5
      //   13: aload 5
      //   15: areturn
      //   16: ldc 148
      //   18: invokestatic 154	java/security/cert/CertificateFactory:getInstance	(Ljava/lang/String;)Ljava/security/cert/CertificateFactory;
      //   21: astore 4
      //   23: iload_2
      //   24: anewarray 156	java/security/cert/Certificate
      //   27: astore 5
      //   29: iconst_0
      //   30: istore 6
      //   32: iload 6
      //   34: aload 5
      //   36: arraylength
      //   37: if_icmpge -24 -> 13
      //   40: aload 5
      //   42: iload 6
      //   44: aload 4
      //   46: new 158	java/io/ByteArrayInputStream
      //   49: dup
      //   50: aload_1
      //   51: invokevirtual 36	com/squareup/okhttp/internal/StrictLineReader:readLine	()Ljava/lang/String;
      //   54: ldc 160
      //   56: invokevirtual 164	java/lang/String:getBytes	(Ljava/lang/String;)[B
      //   59: invokestatic 170	com/squareup/okhttp/internal/Base64:decode	([B)[B
      //   62: invokespecial 173	java/io/ByteArrayInputStream:<init>	([B)V
      //   65: invokevirtual 177	java/security/cert/CertificateFactory:generateCertificate	(Ljava/io/InputStream;)Ljava/security/cert/Certificate;
      //   68: aastore
      //   69: iinc 6 1
      //   72: goto -40 -> 32
      //   75: astore_3
      //   76: new 18	java/io/IOException
      //   79: dup
      //   80: aload_3
      //   81: invokespecial 180	java/io/IOException:<init>	(Ljava/lang/Throwable;)V
      //   84: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	85	0	this	Entry
      //   0	85	1	paramStrictLineReader	StrictLineReader
      //   4	20	2	i	int
      //   75	6	3	localCertificateException	java.security.cert.CertificateException
      //   21	24	4	localCertificateFactory	java.security.cert.CertificateFactory
      //   11	30	5	arrayOfCertificate	Certificate[]
      //   30	40	6	j	int
      // Exception table:
      //   from	to	target	type
      //   16	29	75	java/security/cert/CertificateException
      //   32	69	75	java/security/cert/CertificateException
    }
    
    /* Error */
    private void writeCertArray(Writer paramWriter, Certificate[] paramArrayOfCertificate)
      throws IOException
    {
      // Byte code:
      //   0: aload_2
      //   1: ifnonnull +10 -> 11
      //   4: aload_1
      //   5: ldc 186
      //   7: invokevirtual 191	java/io/Writer:write	(Ljava/lang/String;)V
      //   10: return
      //   11: aload_1
      //   12: new 69	java/lang/StringBuilder
      //   15: dup
      //   16: invokespecial 70	java/lang/StringBuilder:<init>	()V
      //   19: aload_2
      //   20: arraylength
      //   21: invokestatic 196	java/lang/Integer:toString	(I)Ljava/lang/String;
      //   24: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   27: bipush 10
      //   29: invokevirtual 199	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
      //   32: invokevirtual 81	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   35: invokevirtual 191	java/io/Writer:write	(Ljava/lang/String;)V
      //   38: aload_2
      //   39: arraylength
      //   40: istore 4
      //   42: iconst_0
      //   43: istore 5
      //   45: iload 5
      //   47: iload 4
      //   49: if_icmpge -39 -> 10
      //   52: aload_2
      //   53: iload 5
      //   55: aaload
      //   56: invokevirtual 203	java/security/cert/Certificate:getEncoded	()[B
      //   59: invokestatic 207	com/squareup/okhttp/internal/Base64:encode	([B)Ljava/lang/String;
      //   62: astore 6
      //   64: aload_1
      //   65: new 69	java/lang/StringBuilder
      //   68: dup
      //   69: invokespecial 70	java/lang/StringBuilder:<init>	()V
      //   72: aload 6
      //   74: invokevirtual 76	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   77: bipush 10
      //   79: invokevirtual 199	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
      //   82: invokevirtual 81	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   85: invokevirtual 191	java/io/Writer:write	(Ljava/lang/String;)V
      //   88: iinc 5 1
      //   91: goto -46 -> 45
      //   94: astore_3
      //   95: new 18	java/io/IOException
      //   98: dup
      //   99: aload_3
      //   100: invokespecial 180	java/io/IOException:<init>	(Ljava/lang/Throwable;)V
      //   103: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	104	0	this	Entry
      //   0	104	1	paramWriter	Writer
      //   0	104	2	paramArrayOfCertificate	Certificate[]
      //   94	6	3	localCertificateEncodingException	java.security.cert.CertificateEncodingException
      //   40	10	4	i	int
      //   43	46	5	j	int
      //   62	11	6	str	String
      // Exception table:
      //   from	to	target	type
      //   11	42	94	java/security/cert/CertificateEncodingException
      //   52	88	94	java/security/cert/CertificateEncodingException
    }
    
    public boolean matches(URI paramURI, String paramString, Map<String, List<String>> paramMap)
    {
      boolean bool1 = this.uri.equals(paramURI.toString());
      boolean bool2 = false;
      if (bool1)
      {
        boolean bool3 = this.requestMethod.equals(paramString);
        bool2 = false;
        if (bool3)
        {
          boolean bool4 = new ResponseHeaders(paramURI, this.responseHeaders).varyMatches(this.varyHeaders.toMultimap(false), paramMap);
          bool2 = false;
          if (bool4) {
            bool2 = true;
          }
        }
      }
      return bool2;
    }
    
    public void writeTo(DiskLruCache.Editor paramEditor)
      throws IOException
    {
      BufferedWriter localBufferedWriter = new BufferedWriter(new OutputStreamWriter(paramEditor.newOutputStream(0), Util.UTF_8));
      localBufferedWriter.write(this.uri + '\n');
      localBufferedWriter.write(this.requestMethod + '\n');
      localBufferedWriter.write(Integer.toString(this.varyHeaders.length()) + '\n');
      for (int i = 0; i < this.varyHeaders.length(); i++) {
        localBufferedWriter.write(this.varyHeaders.getFieldName(i) + ": " + this.varyHeaders.getValue(i) + '\n');
      }
      localBufferedWriter.write(this.responseHeaders.getStatusLine() + '\n');
      localBufferedWriter.write(Integer.toString(this.responseHeaders.length()) + '\n');
      for (int j = 0; j < this.responseHeaders.length(); j++) {
        localBufferedWriter.write(this.responseHeaders.getFieldName(j) + ": " + this.responseHeaders.getValue(j) + '\n');
      }
      if (isHttps())
      {
        localBufferedWriter.write(10);
        localBufferedWriter.write(this.cipherSuite + '\n');
        writeCertArray(localBufferedWriter, this.peerCertificates);
        writeCertArray(localBufferedWriter, this.localCertificates);
      }
      localBufferedWriter.close();
    }
  }
  
  static class EntryCacheResponse
    extends CacheResponse
  {
    private final HttpResponseCache.Entry entry;
    private final InputStream in;
    private final DiskLruCache.Snapshot snapshot;
    
    public EntryCacheResponse(HttpResponseCache.Entry paramEntry, DiskLruCache.Snapshot paramSnapshot)
    {
      this.entry = paramEntry;
      this.snapshot = paramSnapshot;
      this.in = HttpResponseCache.newBodyInputStream(paramSnapshot);
    }
    
    public InputStream getBody()
    {
      return this.in;
    }
    
    public Map<String, List<String>> getHeaders()
    {
      return this.entry.responseHeaders.toMultimap(true);
    }
  }
  
  static class EntrySecureCacheResponse
    extends SecureCacheResponse
  {
    private final HttpResponseCache.Entry entry;
    private final InputStream in;
    private final DiskLruCache.Snapshot snapshot;
    
    public EntrySecureCacheResponse(HttpResponseCache.Entry paramEntry, DiskLruCache.Snapshot paramSnapshot)
    {
      this.entry = paramEntry;
      this.snapshot = paramSnapshot;
      this.in = HttpResponseCache.newBodyInputStream(paramSnapshot);
    }
    
    public InputStream getBody()
    {
      return this.in;
    }
    
    public String getCipherSuite()
    {
      return this.entry.cipherSuite;
    }
    
    public Map<String, List<String>> getHeaders()
    {
      return this.entry.responseHeaders.toMultimap(true);
    }
    
    public List<Certificate> getLocalCertificateChain()
    {
      if ((this.entry.localCertificates == null) || (this.entry.localCertificates.length == 0)) {
        return null;
      }
      return Arrays.asList((Object[])this.entry.localCertificates.clone());
    }
    
    public Principal getLocalPrincipal()
    {
      if ((this.entry.localCertificates == null) || (this.entry.localCertificates.length == 0)) {
        return null;
      }
      return ((X509Certificate)this.entry.localCertificates[0]).getSubjectX500Principal();
    }
    
    public Principal getPeerPrincipal()
      throws SSLPeerUnverifiedException
    {
      if ((this.entry.peerCertificates == null) || (this.entry.peerCertificates.length == 0)) {
        throw new SSLPeerUnverifiedException(null);
      }
      return ((X509Certificate)this.entry.peerCertificates[0]).getSubjectX500Principal();
    }
    
    public List<Certificate> getServerCertificateChain()
      throws SSLPeerUnverifiedException
    {
      if ((this.entry.peerCertificates == null) || (this.entry.peerCertificates.length == 0)) {
        throw new SSLPeerUnverifiedException(null);
      }
      return Arrays.asList((Object[])this.entry.peerCertificates.clone());
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.http.HttpResponseCache
 * JD-Core Version:    0.7.0.1
 */