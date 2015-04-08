package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.internal.AbstractOutputStream;
import com.squareup.okhttp.internal.Util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ProtocolException;

final class RetryableOutputStream
  extends AbstractOutputStream
{
  private final ByteArrayOutputStream content;
  private final int limit;
  
  public RetryableOutputStream()
  {
    this.limit = -1;
    this.content = new ByteArrayOutputStream();
  }
  
  public RetryableOutputStream(int paramInt)
  {
    this.limit = paramInt;
    this.content = new ByteArrayOutputStream(paramInt);
  }
  
  public void close()
    throws IOException
  {
    try
    {
      boolean bool = this.closed;
      if (bool) {}
      do
      {
        return;
        this.closed = true;
      } while (this.content.size() >= this.limit);
      throw new ProtocolException("content-length promised " + this.limit + " bytes, but received " + this.content.size());
    }
    finally {}
  }
  
  public int contentLength()
    throws IOException
  {
    try
    {
      close();
      int i = this.content.size();
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    try
    {
      checkNotClosed();
      Util.checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
      if ((this.limit != -1) && (this.content.size() > this.limit - paramInt2)) {
        throw new ProtocolException("exceeded content-length limit of " + this.limit + " bytes");
      }
    }
    finally {}
    this.content.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void writeToSocket(OutputStream paramOutputStream)
    throws IOException
  {
    this.content.writeTo(paramOutputStream);
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.http.RetryableOutputStream
 * JD-Core Version:    0.7.0.1
 */