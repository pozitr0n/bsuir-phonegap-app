package com.squareup.okhttp.internal.spdy;

import com.squareup.okhttp.internal.NamedRunnable;
import com.squareup.okhttp.internal.Util;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class SpdyConnection
  implements Closeable
{
  static final int FLAG_FIN = 1;
  static final int FLAG_UNIDIRECTIONAL = 2;
  static final int GOAWAY_INTERNAL_ERROR = 2;
  static final int GOAWAY_OK = 0;
  static final int GOAWAY_PROTOCOL_ERROR = 1;
  static final int TYPE_CREDENTIAL = 16;
  static final int TYPE_DATA = 0;
  static final int TYPE_GOAWAY = 7;
  static final int TYPE_HEADERS = 8;
  static final int TYPE_NOOP = 5;
  static final int TYPE_PING = 6;
  static final int TYPE_RST_STREAM = 3;
  static final int TYPE_SETTINGS = 4;
  static final int TYPE_SYN_REPLY = 2;
  static final int TYPE_SYN_STREAM = 1;
  static final int TYPE_WINDOW_UPDATE = 9;
  static final int VERSION = 3;
  private static final ExecutorService executor;
  final boolean client;
  private final IncomingStreamHandler handler;
  private final String hostName;
  private long idleStartTimeNs = System.nanoTime();
  private int lastGoodStreamId;
  private int nextPingId;
  private int nextStreamId;
  private Map<Integer, Ping> pings;
  Settings settings;
  private boolean shutdown;
  private final SpdyReader spdyReader;
  private final SpdyWriter spdyWriter;
  private final Map<Integer, SpdyStream> streams = new HashMap();
  
  static
  {
    if (!SpdyConnection.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      executor = new ThreadPoolExecutor(0, 2147483647, 60L, TimeUnit.SECONDS, new SynchronousQueue(), Executors.defaultThreadFactory());
      return;
    }
  }
  
  private SpdyConnection(Builder paramBuilder)
  {
    this.client = paramBuilder.client;
    this.handler = paramBuilder.handler;
    this.spdyReader = new SpdyReader(paramBuilder.in);
    this.spdyWriter = new SpdyWriter(paramBuilder.out);
    int j;
    if (paramBuilder.client)
    {
      j = i;
      this.nextStreamId = j;
      if (!paramBuilder.client) {
        break label151;
      }
    }
    for (;;)
    {
      this.nextPingId = i;
      this.hostName = paramBuilder.hostName;
      new Thread(new Reader(null), "Spdy Reader " + this.hostName).start();
      return;
      j = 2;
      break;
      label151:
      i = 2;
    }
  }
  
  /* Error */
  private void close(int paramInt1, int paramInt2)
    throws IOException
  {
    // Byte code:
    //   0: getstatic 71	com/squareup/okhttp/internal/spdy/SpdyConnection:$assertionsDisabled	Z
    //   3: ifne +18 -> 21
    //   6: aload_0
    //   7: invokestatic 239	java/lang/Thread:holdsLock	(Ljava/lang/Object;)Z
    //   10: ifeq +11 -> 21
    //   13: new 241	java/lang/AssertionError
    //   16: dup
    //   17: invokespecial 242	java/lang/AssertionError:<init>	()V
    //   20: athrow
    //   21: aconst_null
    //   22: astore_3
    //   23: aload_0
    //   24: iload_1
    //   25: invokevirtual 245	com/squareup/okhttp/internal/spdy/SpdyConnection:shutdown	(I)V
    //   28: aload_0
    //   29: monitorenter
    //   30: aload_0
    //   31: getfield 105	com/squareup/okhttp/internal/spdy/SpdyConnection:streams	Ljava/util/Map;
    //   34: invokeinterface 250 1 0
    //   39: istore 6
    //   41: aconst_null
    //   42: astore 7
    //   44: iload 6
    //   46: ifne +48 -> 94
    //   49: aload_0
    //   50: getfield 105	com/squareup/okhttp/internal/spdy/SpdyConnection:streams	Ljava/util/Map;
    //   53: invokeinterface 254 1 0
    //   58: aload_0
    //   59: getfield 105	com/squareup/okhttp/internal/spdy/SpdyConnection:streams	Ljava/util/Map;
    //   62: invokeinterface 258 1 0
    //   67: anewarray 260	com/squareup/okhttp/internal/spdy/SpdyStream
    //   70: invokeinterface 266 2 0
    //   75: checkcast 268	[Lcom/squareup/okhttp/internal/spdy/SpdyStream;
    //   78: astore 7
    //   80: aload_0
    //   81: getfield 105	com/squareup/okhttp/internal/spdy/SpdyConnection:streams	Ljava/util/Map;
    //   84: invokeinterface 271 1 0
    //   89: aload_0
    //   90: iconst_0
    //   91: invokespecial 275	com/squareup/okhttp/internal/spdy/SpdyConnection:setIdle	(Z)V
    //   94: aload_0
    //   95: getfield 277	com/squareup/okhttp/internal/spdy/SpdyConnection:pings	Ljava/util/Map;
    //   98: astore 8
    //   100: aconst_null
    //   101: astore 9
    //   103: aload 8
    //   105: ifnull +39 -> 144
    //   108: aload_0
    //   109: getfield 277	com/squareup/okhttp/internal/spdy/SpdyConnection:pings	Ljava/util/Map;
    //   112: invokeinterface 254 1 0
    //   117: aload_0
    //   118: getfield 277	com/squareup/okhttp/internal/spdy/SpdyConnection:pings	Ljava/util/Map;
    //   121: invokeinterface 258 1 0
    //   126: anewarray 279	com/squareup/okhttp/internal/spdy/Ping
    //   129: invokeinterface 266 2 0
    //   134: checkcast 281	[Lcom/squareup/okhttp/internal/spdy/Ping;
    //   137: astore 9
    //   139: aload_0
    //   140: aconst_null
    //   141: putfield 277	com/squareup/okhttp/internal/spdy/SpdyConnection:pings	Ljava/util/Map;
    //   144: aload_0
    //   145: monitorexit
    //   146: aload 7
    //   148: ifnull +68 -> 216
    //   151: aload 7
    //   153: astore 15
    //   155: aload 15
    //   157: arraylength
    //   158: istore 16
    //   160: iconst_0
    //   161: istore 17
    //   163: iload 17
    //   165: iload 16
    //   167: if_icmpge +49 -> 216
    //   170: aload 15
    //   172: iload 17
    //   174: aaload
    //   175: astore 18
    //   177: aload 18
    //   179: iload_2
    //   180: invokevirtual 283	com/squareup/okhttp/internal/spdy/SpdyStream:close	(I)V
    //   183: iinc 17 1
    //   186: goto -23 -> 163
    //   189: astore 4
    //   191: aload 4
    //   193: astore_3
    //   194: goto -166 -> 28
    //   197: astore 5
    //   199: aload_0
    //   200: monitorexit
    //   201: aload 5
    //   203: athrow
    //   204: astore 19
    //   206: aload_3
    //   207: ifnull -24 -> 183
    //   210: aload 19
    //   212: astore_3
    //   213: goto -30 -> 183
    //   216: aload 9
    //   218: ifnull +36 -> 254
    //   221: aload 9
    //   223: astore 12
    //   225: aload 12
    //   227: arraylength
    //   228: istore 13
    //   230: iconst_0
    //   231: istore 14
    //   233: iload 14
    //   235: iload 13
    //   237: if_icmpge +17 -> 254
    //   240: aload 12
    //   242: iload 14
    //   244: aaload
    //   245: invokevirtual 286	com/squareup/okhttp/internal/spdy/Ping:cancel	()V
    //   248: iinc 14 1
    //   251: goto -18 -> 233
    //   254: aload_0
    //   255: getfield 135	com/squareup/okhttp/internal/spdy/SpdyConnection:spdyReader	Lcom/squareup/okhttp/internal/spdy/SpdyReader;
    //   258: invokevirtual 288	com/squareup/okhttp/internal/spdy/SpdyReader:close	()V
    //   261: aload_0
    //   262: getfield 146	com/squareup/okhttp/internal/spdy/SpdyConnection:spdyWriter	Lcom/squareup/okhttp/internal/spdy/SpdyWriter;
    //   265: invokevirtual 289	com/squareup/okhttp/internal/spdy/SpdyWriter:close	()V
    //   268: aload_3
    //   269: ifnull +25 -> 294
    //   272: aload_3
    //   273: athrow
    //   274: astore 10
    //   276: aload 10
    //   278: astore_3
    //   279: goto -18 -> 261
    //   282: astore 11
    //   284: aload_3
    //   285: ifnonnull -17 -> 268
    //   288: aload 11
    //   290: astore_3
    //   291: goto -23 -> 268
    //   294: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	295	0	this	SpdyConnection
    //   0	295	1	paramInt1	int
    //   0	295	2	paramInt2	int
    //   22	269	3	localObject1	Object
    //   189	3	4	localIOException1	IOException
    //   197	5	5	localObject2	Object
    //   39	6	6	bool	boolean
    //   42	110	7	arrayOfSpdyStream1	SpdyStream[]
    //   98	6	8	localMap	Map
    //   101	121	9	arrayOfPing1	Ping[]
    //   274	3	10	localIOException2	IOException
    //   282	7	11	localIOException3	IOException
    //   223	18	12	arrayOfPing2	Ping[]
    //   228	10	13	i	int
    //   231	18	14	j	int
    //   153	18	15	arrayOfSpdyStream2	SpdyStream[]
    //   158	10	16	k	int
    //   161	23	17	m	int
    //   175	3	18	localSpdyStream	SpdyStream
    //   204	7	19	localIOException4	IOException
    // Exception table:
    //   from	to	target	type
    //   23	28	189	java/io/IOException
    //   30	41	197	finally
    //   49	94	197	finally
    //   94	100	197	finally
    //   108	144	197	finally
    //   144	146	197	finally
    //   199	201	197	finally
    //   177	183	204	java/io/IOException
    //   254	261	274	java/io/IOException
    //   261	268	282	java/io/IOException
  }
  
  private SpdyStream getStream(int paramInt)
  {
    try
    {
      SpdyStream localSpdyStream = (SpdyStream)this.streams.get(Integer.valueOf(paramInt));
      return localSpdyStream;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  /* Error */
  private Ping removePing(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 277	com/squareup/okhttp/internal/spdy/SpdyConnection:pings	Ljava/util/Map;
    //   6: ifnull +24 -> 30
    //   9: aload_0
    //   10: getfield 277	com/squareup/okhttp/internal/spdy/SpdyConnection:pings	Ljava/util/Map;
    //   13: iload_1
    //   14: invokestatic 295	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   17: invokeinterface 302 2 0
    //   22: checkcast 279	com/squareup/okhttp/internal/spdy/Ping
    //   25: astore_3
    //   26: aload_0
    //   27: monitorexit
    //   28: aload_3
    //   29: areturn
    //   30: aconst_null
    //   31: astore_3
    //   32: goto -6 -> 26
    //   35: astore_2
    //   36: aload_0
    //   37: monitorexit
    //   38: aload_2
    //   39: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	40	0	this	SpdyConnection
    //   0	40	1	paramInt	int
    //   35	4	2	localObject	Object
    //   25	7	3	localPing	Ping
    // Exception table:
    //   from	to	target	type
    //   2	26	35	finally
  }
  
  private void setIdle(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (;;)
    {
      try
      {
        l = System.nanoTime();
        this.idleStartTimeNs = l;
        return;
      }
      finally {}
      long l = 0L;
    }
  }
  
  private void writePing(int paramInt, Ping paramPing)
    throws IOException
  {
    SpdyWriter localSpdyWriter = this.spdyWriter;
    if (paramPing != null) {}
    try
    {
      paramPing.send();
      this.spdyWriter.ping(0, paramInt);
      return;
    }
    finally {}
  }
  
  private void writePingLater(final int paramInt, final Ping paramPing)
  {
    ExecutorService localExecutorService = executor;
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = this.hostName;
    arrayOfObject[1] = Integer.valueOf(paramInt);
    localExecutorService.submit(new NamedRunnable(String.format("Spdy Writer %s ping %d", arrayOfObject))
    {
      public void execute()
      {
        try
        {
          SpdyConnection.this.writePing(paramInt, paramPing);
          return;
        }
        catch (IOException localIOException) {}
      }
    });
  }
  
  public void close()
    throws IOException
  {
    close(0, 5);
  }
  
  public void flush()
    throws IOException
  {
    synchronized (this.spdyWriter)
    {
      this.spdyWriter.out.flush();
      return;
    }
  }
  
  public long getIdleStartTimeNs()
  {
    try
    {
      long l = this.idleStartTimeNs;
      return l;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  /* Error */
  public boolean isIdle()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 113	com/squareup/okhttp/internal/spdy/SpdyConnection:idleStartTimeNs	J
    //   6: lstore_2
    //   7: lload_2
    //   8: lconst_0
    //   9: lcmp
    //   10: ifeq +11 -> 21
    //   13: iconst_1
    //   14: istore 4
    //   16: aload_0
    //   17: monitorexit
    //   18: iload 4
    //   20: ireturn
    //   21: iconst_0
    //   22: istore 4
    //   24: goto -8 -> 16
    //   27: astore_1
    //   28: aload_0
    //   29: monitorexit
    //   30: aload_1
    //   31: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	32	0	this	SpdyConnection
    //   27	4	1	localObject	Object
    //   6	2	2	l	long
    //   14	9	4	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	7	27	finally
  }
  
  public SpdyStream newStream(List<String> paramList, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    int i;
    if (paramBoolean1)
    {
      i = 0;
      if (!paramBoolean2) {
        break label71;
      }
    }
    int k;
    label71:
    for (int j = 0;; j = 2)
    {
      k = i | j;
      synchronized (this.spdyWriter)
      {
        try
        {
          if (!this.shutdown) {
            break label77;
          }
          throw new IOException("shutdown");
        }
        finally {}
      }
      i = 1;
      break;
    }
    label77:
    int m = this.nextStreamId;
    this.nextStreamId = (2 + this.nextStreamId);
    SpdyStream localSpdyStream = new SpdyStream(m, this, k, 0, 0, paramList, this.settings);
    if (localSpdyStream.isOpen())
    {
      this.streams.put(Integer.valueOf(m), localSpdyStream);
      setIdle(false);
    }
    this.spdyWriter.synStream(k, m, 0, 0, 0, paramList);
    return localSpdyStream;
  }
  
  public void noop()
    throws IOException
  {
    this.spdyWriter.noop();
  }
  
  public int openStreamCount()
  {
    try
    {
      int i = this.streams.size();
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public Ping ping()
    throws IOException
  {
    Ping localPing = new Ping();
    try
    {
      if (this.shutdown) {
        throw new IOException("shutdown");
      }
    }
    finally {}
    int i = this.nextPingId;
    this.nextPingId = (2 + this.nextPingId);
    if (this.pings == null) {
      this.pings = new HashMap();
    }
    this.pings.put(Integer.valueOf(i), localPing);
    writePing(i, localPing);
    return localPing;
  }
  
  SpdyStream removeStream(int paramInt)
  {
    try
    {
      SpdyStream localSpdyStream = (SpdyStream)this.streams.remove(Integer.valueOf(paramInt));
      if ((localSpdyStream != null) && (this.streams.isEmpty())) {
        setIdle(true);
      }
      return localSpdyStream;
    }
    finally {}
  }
  
  public void shutdown(int paramInt)
    throws IOException
  {
    int i;
    synchronized (this.spdyWriter) {}
  }
  
  void writeFrame(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    synchronized (this.spdyWriter)
    {
      this.spdyWriter.out.write(paramArrayOfByte, paramInt1, paramInt2);
      return;
    }
  }
  
  void writeSynReply(int paramInt1, int paramInt2, List<String> paramList)
    throws IOException
  {
    this.spdyWriter.synReply(paramInt2, paramInt1, paramList);
  }
  
  void writeSynReset(int paramInt1, int paramInt2)
    throws IOException
  {
    this.spdyWriter.rstStream(paramInt1, paramInt2);
  }
  
  void writeSynResetLater(final int paramInt1, final int paramInt2)
  {
    ExecutorService localExecutorService = executor;
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = this.hostName;
    arrayOfObject[1] = Integer.valueOf(paramInt1);
    localExecutorService.submit(new NamedRunnable(String.format("Spdy Writer %s stream %d", arrayOfObject))
    {
      public void execute()
      {
        try
        {
          SpdyConnection.this.writeSynReset(paramInt1, paramInt2);
          return;
        }
        catch (IOException localIOException) {}
      }
    });
  }
  
  void writeWindowUpdate(int paramInt1, int paramInt2)
    throws IOException
  {
    this.spdyWriter.windowUpdate(paramInt1, paramInt2);
  }
  
  void writeWindowUpdateLater(final int paramInt1, final int paramInt2)
  {
    ExecutorService localExecutorService = executor;
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = this.hostName;
    arrayOfObject[1] = Integer.valueOf(paramInt1);
    localExecutorService.submit(new NamedRunnable(String.format("Spdy Writer %s stream %d", arrayOfObject))
    {
      public void execute()
      {
        try
        {
          SpdyConnection.this.writeWindowUpdate(paramInt1, paramInt2);
          return;
        }
        catch (IOException localIOException) {}
      }
    });
  }
  
  public static class Builder
  {
    public boolean client;
    private IncomingStreamHandler handler = IncomingStreamHandler.REFUSE_INCOMING_STREAMS;
    private String hostName;
    private InputStream in;
    private OutputStream out;
    
    public Builder(String paramString, boolean paramBoolean, InputStream paramInputStream, OutputStream paramOutputStream)
    {
      this.hostName = paramString;
      this.client = paramBoolean;
      this.in = paramInputStream;
      this.out = paramOutputStream;
    }
    
    public Builder(String paramString, boolean paramBoolean, Socket paramSocket)
      throws IOException
    {
      this(paramString, paramBoolean, paramSocket.getInputStream(), paramSocket.getOutputStream());
    }
    
    public Builder(boolean paramBoolean, InputStream paramInputStream, OutputStream paramOutputStream)
    {
      this("", paramBoolean, paramInputStream, paramOutputStream);
    }
    
    public Builder(boolean paramBoolean, Socket paramSocket)
      throws IOException
    {
      this("", paramBoolean, paramSocket.getInputStream(), paramSocket.getOutputStream());
    }
    
    public SpdyConnection build()
    {
      return new SpdyConnection(this, null);
    }
    
    public Builder handler(IncomingStreamHandler paramIncomingStreamHandler)
    {
      this.handler = paramIncomingStreamHandler;
      return this;
    }
  }
  
  private class Reader
    implements Runnable, SpdyReader.Handler
  {
    private Reader() {}
    
    public void data(int paramInt1, int paramInt2, InputStream paramInputStream, int paramInt3)
      throws IOException
    {
      SpdyStream localSpdyStream = SpdyConnection.this.getStream(paramInt2);
      if (localSpdyStream == null)
      {
        SpdyConnection.this.writeSynResetLater(paramInt2, 2);
        Util.skipByReading(paramInputStream, paramInt3);
      }
      do
      {
        return;
        localSpdyStream.receiveData(paramInputStream, paramInt3);
      } while ((paramInt1 & 0x1) == 0);
      localSpdyStream.receiveFin();
    }
    
    public void goAway(int paramInt1, int paramInt2, int paramInt3)
    {
      synchronized (SpdyConnection.this)
      {
        SpdyConnection.access$1002(SpdyConnection.this, true);
        Iterator localIterator = SpdyConnection.this.streams.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          if ((((Integer)localEntry.getKey()).intValue() > paramInt2) && (((SpdyStream)localEntry.getValue()).isLocallyInitiated()))
          {
            ((SpdyStream)localEntry.getValue()).receiveRstStream(3);
            localIterator.remove();
          }
        }
      }
    }
    
    public void headers(int paramInt1, int paramInt2, List<String> paramList)
      throws IOException
    {
      SpdyStream localSpdyStream = SpdyConnection.this.getStream(paramInt2);
      if (localSpdyStream != null) {
        localSpdyStream.receiveHeaders(paramList);
      }
    }
    
    public void noop() {}
    
    public void ping(int paramInt1, int paramInt2)
    {
      int i = 1;
      int j = SpdyConnection.this.client;
      if (paramInt2 % 2 == i)
      {
        if (j == i) {
          break label39;
        }
        SpdyConnection.this.writePingLater(paramInt2, null);
      }
      label39:
      Ping localPing;
      do
      {
        return;
        i = 0;
        break;
        localPing = SpdyConnection.this.removePing(paramInt2);
      } while (localPing == null);
      localPing.receive();
    }
    
    public void rstStream(int paramInt1, int paramInt2, int paramInt3)
    {
      SpdyStream localSpdyStream = SpdyConnection.this.removeStream(paramInt2);
      if (localSpdyStream != null) {
        localSpdyStream.receiveRstStream(paramInt3);
      }
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 14	com/squareup/okhttp/internal/spdy/SpdyConnection$Reader:this$0	Lcom/squareup/okhttp/internal/spdy/SpdyConnection;
      //   4: invokestatic 140	com/squareup/okhttp/internal/spdy/SpdyConnection:access$700	(Lcom/squareup/okhttp/internal/spdy/SpdyConnection;)Lcom/squareup/okhttp/internal/spdy/SpdyReader;
      //   7: aload_0
      //   8: invokevirtual 146	com/squareup/okhttp/internal/spdy/SpdyReader:nextFrame	(Lcom/squareup/okhttp/internal/spdy/SpdyReader$Handler;)Z
      //   11: istore 5
      //   13: iload 5
      //   15: ifne -15 -> 0
      //   18: aload_0
      //   19: getfield 14	com/squareup/okhttp/internal/spdy/SpdyConnection$Reader:this$0	Lcom/squareup/okhttp/internal/spdy/SpdyConnection;
      //   22: iconst_0
      //   23: iconst_5
      //   24: invokestatic 150	com/squareup/okhttp/internal/spdy/SpdyConnection:access$800	(Lcom/squareup/okhttp/internal/spdy/SpdyConnection;II)V
      //   27: return
      //   28: astore_3
      //   29: aload_0
      //   30: getfield 14	com/squareup/okhttp/internal/spdy/SpdyConnection$Reader:this$0	Lcom/squareup/okhttp/internal/spdy/SpdyConnection;
      //   33: iconst_1
      //   34: iconst_1
      //   35: invokestatic 150	com/squareup/okhttp/internal/spdy/SpdyConnection:access$800	(Lcom/squareup/okhttp/internal/spdy/SpdyConnection;II)V
      //   38: return
      //   39: astore 4
      //   41: return
      //   42: astore_1
      //   43: aload_0
      //   44: getfield 14	com/squareup/okhttp/internal/spdy/SpdyConnection$Reader:this$0	Lcom/squareup/okhttp/internal/spdy/SpdyConnection;
      //   47: iconst_2
      //   48: bipush 6
      //   50: invokestatic 150	com/squareup/okhttp/internal/spdy/SpdyConnection:access$800	(Lcom/squareup/okhttp/internal/spdy/SpdyConnection;II)V
      //   53: aload_1
      //   54: athrow
      //   55: astore_2
      //   56: goto -3 -> 53
      //   59: astore 6
      //   61: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	62	0	this	Reader
      //   42	12	1	localObject	Object
      //   55	1	2	localIOException1	IOException
      //   28	1	3	localIOException2	IOException
      //   39	1	4	localIOException3	IOException
      //   11	3	5	bool	boolean
      //   59	1	6	localIOException4	IOException
      // Exception table:
      //   from	to	target	type
      //   0	13	28	java/io/IOException
      //   29	38	39	java/io/IOException
      //   0	13	42	finally
      //   43	53	55	java/io/IOException
      //   18	27	59	java/io/IOException
    }
    
    public void settings(int paramInt, Settings paramSettings)
    {
      for (;;)
      {
        SpdyStream[] arrayOfSpdyStream2;
        int j;
        synchronized (SpdyConnection.this)
        {
          if ((SpdyConnection.this.settings == null) || ((paramInt & 0x1) != 0))
          {
            SpdyConnection.this.settings = paramSettings;
            boolean bool = SpdyConnection.this.streams.isEmpty();
            SpdyStream[] arrayOfSpdyStream1 = null;
            if (!bool) {
              arrayOfSpdyStream1 = (SpdyStream[])SpdyConnection.this.streams.values().toArray(new SpdyStream[SpdyConnection.this.streams.size()]);
            }
            if (arrayOfSpdyStream1 == null) {
              break;
            }
            arrayOfSpdyStream2 = arrayOfSpdyStream1;
            int i = arrayOfSpdyStream2.length;
            j = 0;
            if (j >= i) {
              break;
            }
          }
        }
        synchronized (arrayOfSpdyStream2[j])
        {
          try
          {
            ???.receiveSettings(SpdyConnection.this.settings);
            j++;
            continue;
          }
          finally {}
          SpdyConnection.this.settings.merge(paramSettings);
          continue;
          localObject1 = finally;
          throw localObject1;
        }
      }
    }
    
    public void synReply(int paramInt1, int paramInt2, List<String> paramList)
      throws IOException
    {
      SpdyStream localSpdyStream = SpdyConnection.this.getStream(paramInt2);
      if (localSpdyStream == null) {
        SpdyConnection.this.writeSynResetLater(paramInt2, 2);
      }
      do
      {
        return;
        localSpdyStream.receiveReply(paramList);
      } while ((paramInt1 & 0x1) == 0);
      localSpdyStream.receiveFin();
    }
    
    public void synStream(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, List<String> paramList)
    {
      final SpdyStream localSpdyStream1;
      synchronized (SpdyConnection.this)
      {
        localSpdyStream1 = new SpdyStream(paramInt2, SpdyConnection.this, paramInt1, paramInt4, paramInt5, paramList, SpdyConnection.this.settings);
        if (SpdyConnection.this.shutdown) {
          return;
        }
        SpdyConnection.access$1102(SpdyConnection.this, paramInt2);
        SpdyStream localSpdyStream2 = (SpdyStream)SpdyConnection.this.streams.put(Integer.valueOf(paramInt2), localSpdyStream1);
        if (localSpdyStream2 != null)
        {
          localSpdyStream2.closeLater(1);
          SpdyConnection.this.removeStream(paramInt2);
          return;
        }
      }
      ExecutorService localExecutorService = SpdyConnection.executor;
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = SpdyConnection.this.hostName;
      arrayOfObject[1] = Integer.valueOf(paramInt2);
      localExecutorService.submit(new NamedRunnable(String.format("Callback %s stream %d", arrayOfObject))
      {
        public void execute()
        {
          try
          {
            SpdyConnection.this.handler.receive(localSpdyStream1);
            return;
          }
          catch (IOException localIOException)
          {
            throw new RuntimeException(localIOException);
          }
        }
      });
    }
    
    public void windowUpdate(int paramInt1, int paramInt2, int paramInt3)
    {
      SpdyStream localSpdyStream = SpdyConnection.this.getStream(paramInt2);
      if (localSpdyStream != null) {
        localSpdyStream.receiveWindowUpdate(paramInt3);
      }
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.spdy.SpdyConnection
 * JD-Core Version:    0.7.0.1
 */