package com.squareup.okhttp.internal;

import java.io.IOException;
import java.io.OutputStream;

public abstract class AbstractOutputStream
  extends OutputStream
{
  protected boolean closed;
  
  protected final void checkNotClosed()
    throws IOException
  {
    if (this.closed) {
      throw new IOException("stream closed");
    }
  }
  
  public boolean isClosed()
  {
    return this.closed;
  }
  
  public final void write(int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = new byte[1];
    arrayOfByte[0] = ((byte)paramInt);
    write(arrayOfByte);
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.AbstractOutputStream
 * JD-Core Version:    0.7.0.1
 */