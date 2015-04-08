package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.internal.Util;
import java.io.IOException;
import java.io.InputStream;
import java.net.CacheRequest;

final class UnknownLengthHttpInputStream
  extends AbstractHttpInputStream
{
  private boolean inputExhausted;
  
  UnknownLengthHttpInputStream(InputStream paramInputStream, CacheRequest paramCacheRequest, HttpEngine paramHttpEngine)
    throws IOException
  {
    super(paramInputStream, paramHttpEngine, paramCacheRequest);
  }
  
  public int available()
    throws IOException
  {
    checkNotClosed();
    if (this.in == null) {
      return 0;
    }
    return this.in.available();
  }
  
  public void close()
    throws IOException
  {
    if (this.closed) {}
    do
    {
      return;
      this.closed = true;
    } while (this.inputExhausted);
    unexpectedEndOfInput();
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    Util.checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
    checkNotClosed();
    if ((this.in == null) || (this.inputExhausted)) {
      return -1;
    }
    int i = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i == -1)
    {
      this.inputExhausted = true;
      endOfInput(false);
      return -1;
    }
    cacheWrite(paramArrayOfByte, paramInt1, i);
    return i;
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.http.UnknownLengthHttpInputStream
 * JD-Core Version:    0.7.0.1
 */