package com.squareup.okhttp.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class FaultRecoveringOutputStream
  extends AbstractOutputStream
{
  private final int maxReplayBufferLength;
  private OutputStream out;
  private ByteArrayOutputStream replayBuffer;
  
  public FaultRecoveringOutputStream(int paramInt, OutputStream paramOutputStream)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    this.maxReplayBufferLength = paramInt;
    this.replayBuffer = new ByteArrayOutputStream(paramInt);
    this.out = paramOutputStream;
  }
  
  /* Error */
  private boolean recover(IOException paramIOException)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 27	com/squareup/okhttp/internal/FaultRecoveringOutputStream:replayBuffer	Ljava/io/ByteArrayOutputStream;
    //   4: ifnonnull +12 -> 16
    //   7: iconst_0
    //   8: ireturn
    //   9: astore_3
    //   10: aload_2
    //   11: invokestatic 39	com/squareup/okhttp/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   14: aload_3
    //   15: astore_1
    //   16: aconst_null
    //   17: astore_2
    //   18: aload_0
    //   19: aload_1
    //   20: invokevirtual 43	com/squareup/okhttp/internal/FaultRecoveringOutputStream:replacementStream	(Ljava/io/IOException;)Ljava/io/OutputStream;
    //   23: astore_2
    //   24: aload_2
    //   25: ifnull -18 -> 7
    //   28: aload_0
    //   29: aload_2
    //   30: invokevirtual 47	com/squareup/okhttp/internal/FaultRecoveringOutputStream:replaceStream	(Ljava/io/OutputStream;)V
    //   33: iconst_1
    //   34: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	35	0	this	FaultRecoveringOutputStream
    //   0	35	1	paramIOException	IOException
    //   10	20	2	localObject	java.lang.Object
    //   9	6	3	localIOException	IOException
    // Exception table:
    //   from	to	target	type
    //   18	24	9	java/io/IOException
    //   28	33	9	java/io/IOException
  }
  
  public final void close()
    throws IOException
  {
    if (this.closed) {
      return;
    }
    do
    {
      try
      {
        this.out.close();
        this.closed = true;
        return;
      }
      catch (IOException localIOException) {}
    } while (recover(localIOException));
    throw localIOException;
  }
  
  public final void flush()
    throws IOException
  {
    if (this.closed) {
      return;
    }
    do
    {
      try
      {
        this.out.flush();
        return;
      }
      catch (IOException localIOException) {}
    } while (recover(localIOException));
    throw localIOException;
  }
  
  public boolean isRecoverable()
  {
    return this.replayBuffer != null;
  }
  
  public final void replaceStream(OutputStream paramOutputStream)
    throws IOException
  {
    if (!isRecoverable()) {
      throw new IllegalStateException();
    }
    if (this.out == paramOutputStream) {
      return;
    }
    this.replayBuffer.writeTo(paramOutputStream);
    Util.closeQuietly(this.out);
    this.out = paramOutputStream;
  }
  
  protected abstract OutputStream replacementStream(IOException paramIOException)
    throws IOException;
  
  public final void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.closed) {
      throw new IOException("stream closed");
    }
    Util.checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
    for (;;)
    {
      try
      {
        this.out.write(paramArrayOfByte, paramInt1, paramInt2);
        if (this.replayBuffer == null) {
          break;
        }
        if (paramInt2 + this.replayBuffer.size() > this.maxReplayBufferLength)
        {
          this.replayBuffer = null;
          return;
        }
        this.replayBuffer.write(paramArrayOfByte, paramInt1, paramInt2);
        return;
      }
      catch (IOException localIOException) {}
      if (!recover(localIOException)) {
        throw localIOException;
      }
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.FaultRecoveringOutputStream
 * JD-Core Version:    0.7.0.1
 */