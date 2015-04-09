package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.Connection;
import com.squareup.okhttp.internal.AbstractOutputStream;
import com.squareup.okhttp.internal.Util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CacheRequest;
import java.net.ProtocolException;
import java.net.Socket;

public final class HttpTransport
  implements Transport
{
  public static final int DEFAULT_CHUNK_LENGTH = 1024;
  private static final int DISCARD_STREAM_TIMEOUT_MILLIS = 100;
  private final HttpEngine httpEngine;
  private OutputStream requestOut;
  private final InputStream socketIn;
  private final OutputStream socketOut;
  
  public HttpTransport(HttpEngine paramHttpEngine, OutputStream paramOutputStream, InputStream paramInputStream)
  {
    this.httpEngine = paramHttpEngine;
    this.socketOut = paramOutputStream;
    this.requestOut = paramOutputStream;
    this.socketIn = paramInputStream;
  }
  
  private static boolean discardStream(HttpEngine paramHttpEngine, InputStream paramInputStream)
  {
    Connection localConnection = paramHttpEngine.connection;
    if (localConnection == null) {}
    Socket localSocket;
    do
    {
      return false;
      localSocket = localConnection.getSocket();
    } while (localSocket == null);
    try
    {
      int i = localSocket.getSoTimeout();
      localSocket.setSoTimeout(100);
      try
      {
        Util.skipAll(paramInputStream);
        return true;
      }
      finally
      {
        localSocket.setSoTimeout(i);
      }
      return false;
    }
    catch (IOException localIOException) {}
  }
  
  public OutputStream createRequestBody()
    throws IOException
  {
    boolean bool = this.httpEngine.requestHeaders.isChunked();
    if ((!bool) && (this.httpEngine.policy.getChunkLength() > 0) && (this.httpEngine.connection.getHttpMinorVersion() != 0))
    {
      this.httpEngine.requestHeaders.setChunked();
      bool = true;
    }
    if (bool)
    {
      int k = this.httpEngine.policy.getChunkLength();
      if (k == -1) {
        k = 1024;
      }
      writeRequestHeaders();
      return new ChunkedOutputStream(this.requestOut, k, null);
    }
    int i = this.httpEngine.policy.getFixedContentLength();
    if (i != -1)
    {
      this.httpEngine.requestHeaders.setContentLength(i);
      writeRequestHeaders();
      return new FixedLengthOutputStream(this.requestOut, i, null);
    }
    int j = this.httpEngine.requestHeaders.getContentLength();
    if (j != -1)
    {
      writeRequestHeaders();
      return new RetryableOutputStream(j);
    }
    return new RetryableOutputStream();
  }
  
  public void flushRequest()
    throws IOException
  {
    this.requestOut.flush();
    this.requestOut = this.socketOut;
  }
  
  public InputStream getTransferStream(CacheRequest paramCacheRequest)
    throws IOException
  {
    if (!this.httpEngine.hasResponseBody()) {
      return new FixedLengthInputStream(this.socketIn, paramCacheRequest, this.httpEngine, 0);
    }
    if (this.httpEngine.responseHeaders.isChunked()) {
      return new ChunkedInputStream(this.socketIn, paramCacheRequest, this);
    }
    if (this.httpEngine.responseHeaders.getContentLength() != -1) {
      return new FixedLengthInputStream(this.socketIn, paramCacheRequest, this.httpEngine, this.httpEngine.responseHeaders.getContentLength());
    }
    return new UnknownLengthHttpInputStream(this.socketIn, paramCacheRequest, this.httpEngine);
  }
  
  public boolean makeReusable(boolean paramBoolean, OutputStream paramOutputStream, InputStream paramInputStream)
  {
    if (paramBoolean) {}
    while (((paramOutputStream != null) && (!((AbstractOutputStream)paramOutputStream).isClosed())) || (this.httpEngine.requestHeaders.hasConnectionClose()) || ((this.httpEngine.responseHeaders != null) && (this.httpEngine.responseHeaders.hasConnectionClose())) || ((paramInputStream instanceof UnknownLengthHttpInputStream))) {
      return false;
    }
    if (paramInputStream != null) {
      return discardStream(this.httpEngine, paramInputStream);
    }
    return true;
  }
  
  public ResponseHeaders readResponseHeaders()
    throws IOException
  {
    RawHeaders localRawHeaders = RawHeaders.fromBytes(this.socketIn);
    this.httpEngine.connection.setHttpMinorVersion(localRawHeaders.getHttpMinorVersion());
    this.httpEngine.receiveHeaders(localRawHeaders);
    return new ResponseHeaders(this.httpEngine.uri, localRawHeaders);
  }
  
  public void writeRequestBody(RetryableOutputStream paramRetryableOutputStream)
    throws IOException
  {
    paramRetryableOutputStream.writeToSocket(this.requestOut);
  }
  
  public void writeRequestHeaders()
    throws IOException
  {
    this.httpEngine.writingRequestHeaders();
    byte[] arrayOfByte = this.httpEngine.requestHeaders.getHeaders().toBytes();
    this.requestOut.write(arrayOfByte);
  }
  
  private static class ChunkedInputStream
    extends AbstractHttpInputStream
  {
    private static final int NO_CHUNK_YET = -1;
    private int bytesRemainingInChunk = -1;
    private boolean hasMoreChunks = true;
    private final HttpTransport transport;
    
    ChunkedInputStream(InputStream paramInputStream, CacheRequest paramCacheRequest, HttpTransport paramHttpTransport)
      throws IOException
    {
      super(paramHttpTransport.httpEngine, paramCacheRequest);
      this.transport = paramHttpTransport;
    }
    
    private void readChunkSize()
      throws IOException
    {
      if (this.bytesRemainingInChunk != -1) {
        Util.readAsciiLine(this.in);
      }
      String str = Util.readAsciiLine(this.in);
      int i = str.indexOf(";");
      if (i != -1) {
        str = str.substring(0, i);
      }
      try
      {
        this.bytesRemainingInChunk = Integer.parseInt(str.trim(), 16);
        if (this.bytesRemainingInChunk == 0)
        {
          this.hasMoreChunks = false;
          RawHeaders localRawHeaders = this.httpEngine.responseHeaders.getHeaders();
          RawHeaders.readHeaders(this.transport.socketIn, localRawHeaders);
          this.httpEngine.receiveHeaders(localRawHeaders);
          endOfInput(false);
        }
        return;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new ProtocolException("Expected a hex chunk size but was " + str);
      }
    }
    
    public int available()
      throws IOException
    {
      checkNotClosed();
      if ((!this.hasMoreChunks) || (this.bytesRemainingInChunk == -1)) {
        return 0;
      }
      return Math.min(this.in.available(), this.bytesRemainingInChunk);
    }
    
    public void close()
      throws IOException
    {
      if (this.closed) {
        return;
      }
      if ((this.hasMoreChunks) && (!HttpTransport.discardStream(this.httpEngine, this))) {
        unexpectedEndOfInput();
      }
      this.closed = true;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      Util.checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
      checkNotClosed();
      if (!this.hasMoreChunks) {
        return -1;
      }
      if ((this.bytesRemainingInChunk == 0) || (this.bytesRemainingInChunk == -1))
      {
        readChunkSize();
        if (!this.hasMoreChunks) {
          return -1;
        }
      }
      int i = this.in.read(paramArrayOfByte, paramInt1, Math.min(paramInt2, this.bytesRemainingInChunk));
      if (i == -1)
      {
        unexpectedEndOfInput();
        throw new IOException("unexpected end of stream");
      }
      this.bytesRemainingInChunk -= i;
      cacheWrite(paramArrayOfByte, paramInt1, i);
      return i;
    }
  }
  
  private static final class ChunkedOutputStream
    extends AbstractOutputStream
  {
    private static final byte[] CRLF = { 13, 10 };
    private static final byte[] FINAL_CHUNK = { 48, 13, 10, 13, 10 };
    private static final byte[] HEX_DIGITS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
    private final ByteArrayOutputStream bufferedChunk;
    private final byte[] hex = { 0, 0, 0, 0, 0, 0, 0, 0, 13, 10 };
    private final int maxChunkLength;
    private final OutputStream socketOut;
    
    private ChunkedOutputStream(OutputStream paramOutputStream, int paramInt)
    {
      this.socketOut = paramOutputStream;
      this.maxChunkLength = Math.max(1, dataLength(paramInt));
      this.bufferedChunk = new ByteArrayOutputStream(paramInt);
    }
    
    private int dataLength(int paramInt)
    {
      int i = 4;
      int j = paramInt - i;
      while (j > 0)
      {
        i++;
        j >>= 4;
      }
      return paramInt - i;
    }
    
    private void writeBufferedChunkToSocket()
      throws IOException
    {
      int i = this.bufferedChunk.size();
      if (i <= 0) {
        return;
      }
      writeHex(i);
      this.bufferedChunk.writeTo(this.socketOut);
      this.bufferedChunk.reset();
      this.socketOut.write(CRLF);
    }
    
    private void writeHex(int paramInt)
      throws IOException
    {
      int i = 8;
      do
      {
        byte[] arrayOfByte = this.hex;
        i--;
        arrayOfByte[i] = HEX_DIGITS[(paramInt & 0xF)];
        paramInt >>>= 4;
      } while (paramInt != 0);
      this.socketOut.write(this.hex, i, this.hex.length - i);
    }
    
    /* Error */
    public void close()
      throws IOException
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 103	com/squareup/okhttp/internal/http/HttpTransport$ChunkedOutputStream:closed	Z
      //   6: istore_2
      //   7: iload_2
      //   8: ifeq +6 -> 14
      //   11: aload_0
      //   12: monitorexit
      //   13: return
      //   14: aload_0
      //   15: iconst_1
      //   16: putfield 103	com/squareup/okhttp/internal/http/HttpTransport$ChunkedOutputStream:closed	Z
      //   19: aload_0
      //   20: invokespecial 105	com/squareup/okhttp/internal/http/HttpTransport$ChunkedOutputStream:writeBufferedChunkToSocket	()V
      //   23: aload_0
      //   24: getfield 50	com/squareup/okhttp/internal/http/HttpTransport$ChunkedOutputStream:socketOut	Ljava/io/OutputStream;
      //   27: getstatic 41	com/squareup/okhttp/internal/http/HttpTransport$ChunkedOutputStream:FINAL_CHUNK	[B
      //   30: invokevirtual 95	java/io/OutputStream:write	([B)V
      //   33: goto -22 -> 11
      //   36: astore_1
      //   37: aload_0
      //   38: monitorexit
      //   39: aload_1
      //   40: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	41	0	this	ChunkedOutputStream
      //   36	4	1	localObject	Object
      //   6	2	2	bool	boolean
      // Exception table:
      //   from	to	target	type
      //   2	7	36	finally
      //   14	33	36	finally
    }
    
    /* Error */
    public void flush()
      throws IOException
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 103	com/squareup/okhttp/internal/http/HttpTransport$ChunkedOutputStream:closed	Z
      //   6: istore_2
      //   7: iload_2
      //   8: ifeq +6 -> 14
      //   11: aload_0
      //   12: monitorexit
      //   13: return
      //   14: aload_0
      //   15: invokespecial 105	com/squareup/okhttp/internal/http/HttpTransport$ChunkedOutputStream:writeBufferedChunkToSocket	()V
      //   18: aload_0
      //   19: getfield 50	com/squareup/okhttp/internal/http/HttpTransport$ChunkedOutputStream:socketOut	Ljava/io/OutputStream;
      //   22: invokevirtual 108	java/io/OutputStream:flush	()V
      //   25: goto -14 -> 11
      //   28: astore_1
      //   29: aload_0
      //   30: monitorexit
      //   31: aload_1
      //   32: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	33	0	this	ChunkedOutputStream
      //   28	4	1	localObject	Object
      //   6	2	2	bool	boolean
      // Exception table:
      //   from	to	target	type
      //   2	7	28	finally
      //   14	25	28	finally
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      for (;;)
      {
        int i;
        try
        {
          checkNotClosed();
          Util.checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
          if (paramInt2 > 0) {
            if ((this.bufferedChunk.size() > 0) || (paramInt2 < this.maxChunkLength))
            {
              i = Math.min(paramInt2, this.maxChunkLength - this.bufferedChunk.size());
              this.bufferedChunk.write(paramArrayOfByte, paramInt1, i);
              if (this.bufferedChunk.size() == this.maxChunkLength) {
                writeBufferedChunkToSocket();
              }
            }
            else
            {
              i = this.maxChunkLength;
              writeHex(i);
              this.socketOut.write(paramArrayOfByte, paramInt1, i);
              this.socketOut.write(CRLF);
            }
          }
        }
        finally {}
        return;
        paramInt1 += i;
        paramInt2 -= i;
      }
    }
  }
  
  private static class FixedLengthInputStream
    extends AbstractHttpInputStream
  {
    private int bytesRemaining;
    
    public FixedLengthInputStream(InputStream paramInputStream, CacheRequest paramCacheRequest, HttpEngine paramHttpEngine, int paramInt)
      throws IOException
    {
      super(paramHttpEngine, paramCacheRequest);
      this.bytesRemaining = paramInt;
      if (this.bytesRemaining == 0) {
        endOfInput(false);
      }
    }
    
    public int available()
      throws IOException
    {
      checkNotClosed();
      if (this.bytesRemaining == 0) {
        return 0;
      }
      return Math.min(this.in.available(), this.bytesRemaining);
    }
    
    public void close()
      throws IOException
    {
      if (this.closed) {
        return;
      }
      if ((this.bytesRemaining != 0) && (!HttpTransport.discardStream(this.httpEngine, this))) {
        unexpectedEndOfInput();
      }
      this.closed = true;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      Util.checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
      checkNotClosed();
      int i;
      if (this.bytesRemaining == 0) {
        i = -1;
      }
      do
      {
        return i;
        i = this.in.read(paramArrayOfByte, paramInt1, Math.min(paramInt2, this.bytesRemaining));
        if (i == -1)
        {
          unexpectedEndOfInput();
          throw new ProtocolException("unexpected end of stream");
        }
        this.bytesRemaining -= i;
        cacheWrite(paramArrayOfByte, paramInt1, i);
      } while (this.bytesRemaining != 0);
      endOfInput(false);
      return i;
    }
  }
  
  private static final class FixedLengthOutputStream
    extends AbstractOutputStream
  {
    private int bytesRemaining;
    private final OutputStream socketOut;
    
    private FixedLengthOutputStream(OutputStream paramOutputStream, int paramInt)
    {
      this.socketOut = paramOutputStream;
      this.bytesRemaining = paramInt;
    }
    
    public void close()
      throws IOException
    {
      if (this.closed) {}
      do
      {
        return;
        this.closed = true;
      } while (this.bytesRemaining <= 0);
      throw new ProtocolException("unexpected end of stream");
    }
    
    public void flush()
      throws IOException
    {
      if (this.closed) {
        return;
      }
      this.socketOut.flush();
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      checkNotClosed();
      Util.checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
      if (paramInt2 > this.bytesRemaining) {
        throw new ProtocolException("expected " + this.bytesRemaining + " bytes but received " + paramInt2);
      }
      this.socketOut.write(paramArrayOfByte, paramInt1, paramInt2);
      this.bytesRemaining -= paramInt2;
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.http.HttpTransport
 * JD-Core Version:    0.7.0.1
 */