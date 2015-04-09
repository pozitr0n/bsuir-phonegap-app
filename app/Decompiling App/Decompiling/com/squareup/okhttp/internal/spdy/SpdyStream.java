package com.squareup.okhttp.internal.spdy;

import com.squareup.okhttp.internal.Util;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.nio.ByteOrder;
import java.util.List;

public final class SpdyStream
{
  private static final int DATA_FRAME_HEADER_LENGTH = 8;
  public static final int RST_CANCEL = 5;
  public static final int RST_FLOW_CONTROL_ERROR = 7;
  public static final int RST_FRAME_TOO_LARGE = 11;
  public static final int RST_INTERNAL_ERROR = 6;
  public static final int RST_INVALID_CREDENTIALS = 10;
  public static final int RST_INVALID_STREAM = 2;
  public static final int RST_PROTOCOL_ERROR = 1;
  public static final int RST_REFUSED_STREAM = 3;
  public static final int RST_STREAM_ALREADY_CLOSED = 9;
  public static final int RST_STREAM_IN_USE = 8;
  public static final int RST_UNSUPPORTED_VERSION = 4;
  private static final String[] STATUS_CODE_NAMES;
  public static final int WINDOW_UPDATE_THRESHOLD = 32768;
  private final SpdyConnection connection;
  private final int id;
  private final SpdyDataInputStream in = new SpdyDataInputStream(null);
  private final SpdyDataOutputStream out = new SpdyDataOutputStream(null);
  private final int priority;
  private long readTimeoutMillis = 0L;
  private final List<String> requestHeaders;
  private List<String> responseHeaders;
  private int rstStatusCode = -1;
  private final int slot;
  private int writeWindowSize;
  
  static
  {
    if (!SpdyStream.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      STATUS_CODE_NAMES = new String[] { null, "PROTOCOL_ERROR", "INVALID_STREAM", "REFUSED_STREAM", "UNSUPPORTED_VERSION", "CANCEL", "INTERNAL_ERROR", "FLOW_CONTROL_ERROR", "STREAM_IN_USE", "STREAM_ALREADY_CLOSED", "INVALID_CREDENTIALS", "FRAME_TOO_LARGE" };
      return;
    }
  }
  
  SpdyStream(int paramInt1, SpdyConnection paramSpdyConnection, int paramInt2, int paramInt3, int paramInt4, List<String> paramList, Settings paramSettings)
  {
    if (paramSpdyConnection == null) {
      throw new NullPointerException("connection == null");
    }
    if (paramList == null) {
      throw new NullPointerException("requestHeaders == null");
    }
    this.id = paramInt1;
    this.connection = paramSpdyConnection;
    this.priority = paramInt3;
    this.slot = paramInt4;
    this.requestHeaders = paramList;
    if (isLocallyInitiated())
    {
      SpdyDataInputStream localSpdyDataInputStream2 = this.in;
      boolean bool3;
      SpdyDataOutputStream localSpdyDataOutputStream2;
      if ((paramInt2 & 0x2) != 0)
      {
        bool3 = bool1;
        SpdyDataInputStream.access$202(localSpdyDataInputStream2, bool3);
        localSpdyDataOutputStream2 = this.out;
        if ((paramInt2 & 0x1) == 0) {
          break label164;
        }
      }
      for (;;)
      {
        SpdyDataOutputStream.access$302(localSpdyDataOutputStream2, bool1);
        setSettings(paramSettings);
        return;
        bool3 = false;
        break;
        label164:
        bool1 = false;
      }
    }
    SpdyDataInputStream localSpdyDataInputStream1 = this.in;
    boolean bool2;
    label186:
    SpdyDataOutputStream localSpdyDataOutputStream1;
    if ((paramInt2 & 0x1) != 0)
    {
      bool2 = bool1;
      SpdyDataInputStream.access$202(localSpdyDataInputStream1, bool2);
      localSpdyDataOutputStream1 = this.out;
      if ((paramInt2 & 0x2) == 0) {
        break label223;
      }
    }
    for (;;)
    {
      SpdyDataOutputStream.access$302(localSpdyDataOutputStream1, bool1);
      break;
      bool2 = false;
      break label186;
      label223:
      bool1 = false;
    }
  }
  
  private void cancelStreamIfNecessary()
    throws IOException
  {
    assert (!Thread.holdsLock(this));
    for (;;)
    {
      try
      {
        boolean bool;
        if ((!this.in.finished) && (this.in.closed))
        {
          if (this.out.finished) {
            break label110;
          }
          if (this.out.closed)
          {
            break label110;
            bool = isOpen();
            if (i == 0) {
              break label93;
            }
            close(5);
            return;
          }
        }
        i = 0;
        continue;
        if (bool) {
          continue;
        }
      }
      finally {}
      label93:
      this.connection.removeStream(this.id);
      return;
      label110:
      int i = 1;
    }
  }
  
  private boolean closeInternal(int paramInt)
  {
    assert (!Thread.holdsLock(this));
    try
    {
      if (this.rstStatusCode != -1) {
        return false;
      }
      if ((this.in.finished) && (this.out.finished)) {
        return false;
      }
    }
    finally {}
    this.rstStatusCode = paramInt;
    notifyAll();
    this.connection.removeStream(this.id);
    return true;
  }
  
  private String rstStatusString()
  {
    if ((this.rstStatusCode > 0) && (this.rstStatusCode < STATUS_CODE_NAMES.length)) {
      return STATUS_CODE_NAMES[this.rstStatusCode];
    }
    return Integer.toString(this.rstStatusCode);
  }
  
  private void setSettings(Settings paramSettings)
  {
    int i = 65536;
    assert (Thread.holdsLock(this.connection));
    if (paramSettings != null) {
      i = paramSettings.getInitialWindowSize(i);
    }
    this.writeWindowSize = i;
  }
  
  public void close(int paramInt)
    throws IOException
  {
    if (!closeInternal(paramInt)) {
      return;
    }
    this.connection.writeSynReset(this.id, paramInt);
  }
  
  public void closeLater(int paramInt)
  {
    if (!closeInternal(paramInt)) {
      return;
    }
    this.connection.writeSynResetLater(this.id, paramInt);
  }
  
  public SpdyConnection getConnection()
  {
    return this.connection;
  }
  
  public InputStream getInputStream()
  {
    return this.in;
  }
  
  public OutputStream getOutputStream()
  {
    try
    {
      if ((this.responseHeaders == null) && (!isLocallyInitiated())) {
        throw new IllegalStateException("reply before requesting the output stream");
      }
    }
    finally {}
    return this.out;
  }
  
  int getPriority()
  {
    return this.priority;
  }
  
  public long getReadTimeoutMillis()
  {
    return this.readTimeoutMillis;
  }
  
  public List<String> getRequestHeaders()
  {
    return this.requestHeaders;
  }
  
  public List<String> getResponseHeaders()
    throws IOException
  {
    try
    {
      while ((this.responseHeaders == null) && (this.rstStatusCode == -1)) {
        wait();
      }
      InterruptedIOException localInterruptedIOException;
      if (this.responseHeaders == null) {
        break label64;
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      localInterruptedIOException = new InterruptedIOException();
      localInterruptedIOException.initCause(localInterruptedException);
      throw localInterruptedIOException;
    }
    finally {}
    List localList = this.responseHeaders;
    return localList;
    label64:
    throw new IOException("stream was reset: " + rstStatusString());
  }
  
  public int getRstStatusCode()
  {
    try
    {
      int i = this.rstStatusCode;
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  int getSlot()
  {
    return this.slot;
  }
  
  public boolean isLocallyInitiated()
  {
    if (this.id % 2 == 1) {}
    for (int i = 1; this.connection.client == i; i = 0) {
      return true;
    }
    return false;
  }
  
  /* Error */
  public boolean isOpen()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 108	com/squareup/okhttp/internal/spdy/SpdyStream:rstStatusCode	I
    //   6: istore_2
    //   7: iconst_0
    //   8: istore_3
    //   9: iload_2
    //   10: iconst_m1
    //   11: if_icmpeq +7 -> 18
    //   14: aload_0
    //   15: monitorexit
    //   16: iload_3
    //   17: ireturn
    //   18: aload_0
    //   19: getfield 101	com/squareup/okhttp/internal/spdy/SpdyStream:in	Lcom/squareup/okhttp/internal/spdy/SpdyStream$SpdyDataInputStream;
    //   22: invokestatic 178	com/squareup/okhttp/internal/spdy/SpdyStream$SpdyDataInputStream:access$200	(Lcom/squareup/okhttp/internal/spdy/SpdyStream$SpdyDataInputStream;)Z
    //   25: ifne +13 -> 38
    //   28: aload_0
    //   29: getfield 101	com/squareup/okhttp/internal/spdy/SpdyStream:in	Lcom/squareup/okhttp/internal/spdy/SpdyStream$SpdyDataInputStream;
    //   32: invokestatic 181	com/squareup/okhttp/internal/spdy/SpdyStream$SpdyDataInputStream:access$400	(Lcom/squareup/okhttp/internal/spdy/SpdyStream$SpdyDataInputStream;)Z
    //   35: ifeq +36 -> 71
    //   38: aload_0
    //   39: getfield 106	com/squareup/okhttp/internal/spdy/SpdyStream:out	Lcom/squareup/okhttp/internal/spdy/SpdyStream$SpdyDataOutputStream;
    //   42: invokestatic 185	com/squareup/okhttp/internal/spdy/SpdyStream$SpdyDataOutputStream:access$300	(Lcom/squareup/okhttp/internal/spdy/SpdyStream$SpdyDataOutputStream;)Z
    //   45: ifne +13 -> 58
    //   48: aload_0
    //   49: getfield 106	com/squareup/okhttp/internal/spdy/SpdyStream:out	Lcom/squareup/okhttp/internal/spdy/SpdyStream$SpdyDataOutputStream;
    //   52: invokestatic 188	com/squareup/okhttp/internal/spdy/SpdyStream$SpdyDataOutputStream:access$500	(Lcom/squareup/okhttp/internal/spdy/SpdyStream$SpdyDataOutputStream;)Z
    //   55: ifeq +16 -> 71
    //   58: aload_0
    //   59: getfield 237	com/squareup/okhttp/internal/spdy/SpdyStream:responseHeaders	Ljava/util/List;
    //   62: astore 4
    //   64: iconst_0
    //   65: istore_3
    //   66: aload 4
    //   68: ifnonnull -54 -> 14
    //   71: iconst_1
    //   72: istore_3
    //   73: goto -59 -> 14
    //   76: astore_1
    //   77: aload_0
    //   78: monitorexit
    //   79: aload_1
    //   80: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	81	0	this	SpdyStream
    //   76	4	1	localObject	Object
    //   6	6	2	i	int
    //   8	65	3	bool	boolean
    //   62	5	4	localList	List
    // Exception table:
    //   from	to	target	type
    //   2	7	76	finally
    //   18	38	76	finally
    //   38	58	76	finally
    //   58	64	76	finally
  }
  
  void receiveData(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    assert (!Thread.holdsLock(this));
    this.in.receive(paramInputStream, paramInt);
  }
  
  void receiveFin()
  {
    assert (!Thread.holdsLock(this));
    try
    {
      SpdyDataInputStream.access$202(this.in, true);
      boolean bool = isOpen();
      notifyAll();
      if (!bool) {
        this.connection.removeStream(this.id);
      }
      return;
    }
    finally {}
  }
  
  /* Error */
  void receiveHeaders(List<String> paramList)
    throws IOException
  {
    // Byte code:
    //   0: getstatic 62	com/squareup/okhttp/internal/spdy/SpdyStream:$assertionsDisabled	Z
    //   3: ifne +18 -> 21
    //   6: aload_0
    //   7: invokestatic 171	java/lang/Thread:holdsLock	(Ljava/lang/Object;)Z
    //   10: ifeq +11 -> 21
    //   13: new 173	java/lang/AssertionError
    //   16: dup
    //   17: invokespecial 174	java/lang/AssertionError:<init>	()V
    //   20: athrow
    //   21: iconst_0
    //   22: istore_2
    //   23: aload_0
    //   24: monitorenter
    //   25: aload_0
    //   26: getfield 237	com/squareup/okhttp/internal/spdy/SpdyStream:responseHeaders	Ljava/util/List;
    //   29: ifnull +51 -> 80
    //   32: new 288	java/util/ArrayList
    //   35: dup
    //   36: invokespecial 289	java/util/ArrayList:<init>	()V
    //   39: astore 4
    //   41: aload 4
    //   43: aload_0
    //   44: getfield 237	com/squareup/okhttp/internal/spdy/SpdyStream:responseHeaders	Ljava/util/List;
    //   47: invokeinterface 295 2 0
    //   52: pop
    //   53: aload 4
    //   55: aload_1
    //   56: invokeinterface 295 2 0
    //   61: pop
    //   62: aload_0
    //   63: aload 4
    //   65: putfield 237	com/squareup/okhttp/internal/spdy/SpdyStream:responseHeaders	Ljava/util/List;
    //   68: aload_0
    //   69: monitorexit
    //   70: iload_2
    //   71: ifeq +8 -> 79
    //   74: aload_0
    //   75: iconst_1
    //   76: invokevirtual 297	com/squareup/okhttp/internal/spdy/SpdyStream:closeLater	(I)V
    //   79: return
    //   80: iconst_1
    //   81: istore_2
    //   82: goto -14 -> 68
    //   85: astore_3
    //   86: aload_0
    //   87: monitorexit
    //   88: aload_3
    //   89: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	90	0	this	SpdyStream
    //   0	90	1	paramList	List<String>
    //   22	60	2	i	int
    //   85	4	3	localObject	Object
    //   39	25	4	localArrayList	java.util.ArrayList
    // Exception table:
    //   from	to	target	type
    //   25	68	85	finally
    //   68	70	85	finally
    //   86	88	85	finally
  }
  
  void receiveReply(List<String> paramList)
    throws IOException
  {
    assert (!Thread.holdsLock(this));
    int i = 0;
    boolean bool = true;
    try
    {
      if ((isLocallyInitiated()) && (this.responseHeaders == null))
      {
        this.responseHeaders = paramList;
        bool = isOpen();
        notifyAll();
        if (i == 0) {
          break label80;
        }
        closeLater(8);
      }
      while (bool)
      {
        return;
        i = 1;
        break;
      }
    }
    finally {}
    label80:
    this.connection.removeStream(this.id);
  }
  
  void receiveRstStream(int paramInt)
  {
    try
    {
      if (this.rstStatusCode == -1)
      {
        this.rstStatusCode = paramInt;
        notifyAll();
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  void receiveSettings(Settings paramSettings)
  {
    assert (Thread.holdsLock(this));
    setSettings(paramSettings);
    notifyAll();
  }
  
  void receiveWindowUpdate(int paramInt)
  {
    try
    {
      SpdyDataOutputStream.access$620(this.out, paramInt);
      notifyAll();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void reply(List<String> paramList, boolean paramBoolean)
    throws IOException
  {
    assert (!Thread.holdsLock(this));
    if (paramList == null) {
      try
      {
        throw new NullPointerException("responseHeaders == null");
      }
      finally {}
    }
    if (isLocallyInitiated()) {
      throw new IllegalStateException("cannot reply to a locally initiated stream");
    }
    if (this.responseHeaders != null) {
      throw new IllegalStateException("reply already sent");
    }
    this.responseHeaders = paramList;
    int i = 0;
    if (!paramBoolean)
    {
      SpdyDataOutputStream.access$302(this.out, true);
      i = 0x0 | 0x1;
    }
    this.connection.writeSynReply(this.id, i, paramList);
  }
  
  public void setReadTimeout(long paramLong)
  {
    this.readTimeoutMillis = paramLong;
  }
  
  private final class SpdyDataInputStream
    extends InputStream
  {
    private final byte[] buffer = new byte[65536];
    private boolean closed;
    private boolean finished;
    private int limit;
    private int pos = -1;
    private int unacknowledgedBytes = 0;
    
    static
    {
      if (!SpdyStream.class.desiredAssertionStatus()) {}
      for (boolean bool = true;; bool = false)
      {
        $assertionsDisabled = bool;
        return;
      }
    }
    
    private SpdyDataInputStream() {}
    
    private void checkNotClosed()
      throws IOException
    {
      if (this.closed) {
        throw new IOException("stream closed");
      }
      if (SpdyStream.this.rstStatusCode != -1) {
        throw new IOException("stream was reset: " + SpdyStream.this.rstStatusString());
      }
    }
    
    private void waitUntilReadable()
      throws IOException
    {
      long l1 = 0L;
      long l2 = 0L;
      if (SpdyStream.this.readTimeoutMillis != 0L) {
        l1 = System.nanoTime() / 1000000L;
      }
      for (l2 = SpdyStream.this.readTimeoutMillis;; l2 = l1 + SpdyStream.this.readTimeoutMillis - System.nanoTime() / 1000000L)
      {
        try
        {
          for (;;)
          {
            if ((this.pos != -1) || (this.finished) || (this.closed) || (SpdyStream.this.rstStatusCode != -1)) {
              return;
            }
            if (SpdyStream.this.readTimeoutMillis != 0L) {
              break;
            }
            SpdyStream.this.wait();
          }
          if (l2 <= 0L) {
            break;
          }
        }
        catch (InterruptedException localInterruptedException)
        {
          throw new InterruptedIOException();
        }
        SpdyStream.this.wait(l2);
      }
      throw new SocketTimeoutException();
    }
    
    public int available()
      throws IOException
    {
      synchronized (SpdyStream.this)
      {
        checkNotClosed();
        if (this.pos == -1) {
          return 0;
        }
        if (this.limit > this.pos)
        {
          int j = this.limit - this.pos;
          return j;
        }
      }
      int i = this.limit + (this.buffer.length - this.pos);
      return i;
    }
    
    public void close()
      throws IOException
    {
      synchronized (SpdyStream.this)
      {
        this.closed = true;
        SpdyStream.this.notifyAll();
        SpdyStream.this.cancelStreamIfNecessary();
        return;
      }
    }
    
    public int read()
      throws IOException
    {
      return Util.readSingleByte(this);
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      synchronized (SpdyStream.this)
      {
        Util.checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
        waitUntilReadable();
        checkNotClosed();
        if (this.pos == -1) {
          return -1;
        }
        int i = this.limit;
        int j = this.pos;
        int k = 0;
        if (i <= j)
        {
          int m = Math.min(paramInt2, this.buffer.length - this.pos);
          System.arraycopy(this.buffer, this.pos, paramArrayOfByte, paramInt1, m);
          this.pos = (m + this.pos);
          k = 0 + m;
          if (this.pos == this.buffer.length) {
            this.pos = 0;
          }
        }
        if (k < paramInt2)
        {
          int n = Math.min(this.limit - this.pos, paramInt2 - k);
          System.arraycopy(this.buffer, this.pos, paramArrayOfByte, paramInt1 + k, n);
          this.pos = (n + this.pos);
          k += n;
        }
        this.unacknowledgedBytes = (k + this.unacknowledgedBytes);
        if (this.unacknowledgedBytes >= 32768)
        {
          SpdyStream.this.connection.writeWindowUpdateLater(SpdyStream.this.id, this.unacknowledgedBytes);
          this.unacknowledgedBytes = 0;
        }
        if (this.pos == this.limit)
        {
          this.pos = -1;
          this.limit = 0;
        }
        return k;
      }
    }
    
    void receive(InputStream paramInputStream, int paramInt)
      throws IOException
    {
      assert (!Thread.holdsLock(SpdyStream.this));
      if (paramInt == 0) {
        return;
      }
      boolean bool;
      int i;
      int j;
      int k;
      synchronized (SpdyStream.this)
      {
        bool = this.finished;
        i = this.pos;
        j = this.limit;
        k = this.limit;
        int m;
        if (paramInt > this.buffer.length - available())
        {
          m = 1;
          if (m != 0)
          {
            Util.skipByReading(paramInputStream, paramInt);
            SpdyStream.this.closeLater(7);
          }
        }
        else
        {
          m = 0;
        }
      }
      if (bool)
      {
        Util.skipByReading(paramInputStream, paramInt);
        return;
      }
      if (i < k)
      {
        int n = Math.min(paramInt, this.buffer.length - k);
        Util.readFully(paramInputStream, this.buffer, k, n);
        k += n;
        paramInt -= n;
        if (k == this.buffer.length) {
          k = 0;
        }
      }
      if (paramInt > 0)
      {
        Util.readFully(paramInputStream, this.buffer, k, paramInt);
        k += paramInt;
      }
      synchronized (SpdyStream.this)
      {
        this.limit = k;
        if (this.pos == -1)
        {
          this.pos = j;
          SpdyStream.this.notifyAll();
        }
        return;
      }
    }
  }
  
  private final class SpdyDataOutputStream
    extends OutputStream
  {
    private final byte[] buffer = new byte[8192];
    private boolean closed;
    private boolean finished;
    private int pos = 8;
    private int unacknowledgedBytes = 0;
    
    static
    {
      if (!SpdyStream.class.desiredAssertionStatus()) {}
      for (boolean bool = true;; bool = false)
      {
        $assertionsDisabled = bool;
        return;
      }
    }
    
    private SpdyDataOutputStream() {}
    
    private void checkNotClosed()
      throws IOException
    {
      synchronized (SpdyStream.this)
      {
        if (this.closed) {
          throw new IOException("stream closed");
        }
      }
      if (this.finished) {
        throw new IOException("stream finished");
      }
      if (SpdyStream.this.rstStatusCode != -1) {
        throw new IOException("stream was reset: " + SpdyStream.this.rstStatusString());
      }
    }
    
    private void waitUntilWritable(int paramInt, boolean paramBoolean)
      throws IOException
    {
      for (;;)
      {
        try
        {
          if (paramInt + this.unacknowledgedBytes < SpdyStream.this.writeWindowSize) {
            break;
          }
          SpdyStream.this.wait();
          if ((!paramBoolean) && (this.closed)) {
            throw new IOException("stream closed");
          }
        }
        catch (InterruptedException localInterruptedException)
        {
          throw new InterruptedIOException();
        }
        if (this.finished) {
          throw new IOException("stream finished");
        }
        if (SpdyStream.this.rstStatusCode != -1) {
          throw new IOException("stream was reset: " + SpdyStream.this.rstStatusString());
        }
      }
    }
    
    private void writeFrame(boolean paramBoolean)
      throws IOException
    {
      assert (!Thread.holdsLock(SpdyStream.this));
      int i = -8 + this.pos;
      synchronized (SpdyStream.this)
      {
        waitUntilWritable(i, paramBoolean);
        this.unacknowledgedBytes = (i + this.unacknowledgedBytes);
        int j = 0;
        if (paramBoolean) {
          j = 0x0 | 0x1;
        }
        Util.pokeInt(this.buffer, 0, 0x7FFFFFFF & SpdyStream.this.id, ByteOrder.BIG_ENDIAN);
        Util.pokeInt(this.buffer, 4, (j & 0xFF) << 24 | 0xFFFFFF & i, ByteOrder.BIG_ENDIAN);
        SpdyStream.this.connection.writeFrame(this.buffer, 0, this.pos);
        this.pos = 8;
        return;
      }
    }
    
    public void close()
      throws IOException
    {
      assert (!Thread.holdsLock(SpdyStream.this));
      synchronized (SpdyStream.this)
      {
        if (this.closed) {
          return;
        }
        this.closed = true;
        writeFrame(true);
        SpdyStream.this.connection.flush();
        SpdyStream.this.cancelStreamIfNecessary();
        return;
      }
    }
    
    public void flush()
      throws IOException
    {
      assert (!Thread.holdsLock(SpdyStream.this));
      checkNotClosed();
      if (this.pos > 8)
      {
        writeFrame(false);
        SpdyStream.this.connection.flush();
      }
    }
    
    public void write(int paramInt)
      throws IOException
    {
      Util.writeSingleByte(this, paramInt);
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      assert (!Thread.holdsLock(SpdyStream.this));
      Util.checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
      checkNotClosed();
      while (paramInt2 > 0)
      {
        if (this.pos == this.buffer.length) {
          writeFrame(false);
        }
        int i = Math.min(paramInt2, this.buffer.length - this.pos);
        System.arraycopy(paramArrayOfByte, paramInt1, this.buffer, this.pos, i);
        this.pos = (i + this.pos);
        paramInt1 += i;
        paramInt2 -= i;
      }
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.spdy.SpdyStream
 * JD-Core Version:    0.7.0.1
 */