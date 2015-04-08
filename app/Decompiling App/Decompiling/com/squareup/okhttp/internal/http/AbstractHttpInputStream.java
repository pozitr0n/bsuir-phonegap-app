package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.internal.Util;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CacheRequest;

abstract class AbstractHttpInputStream
  extends InputStream
{
  private final OutputStream cacheBody;
  private final CacheRequest cacheRequest;
  protected boolean closed;
  protected final HttpEngine httpEngine;
  protected final InputStream in;
  
  AbstractHttpInputStream(InputStream paramInputStream, HttpEngine paramHttpEngine, CacheRequest paramCacheRequest)
    throws IOException
  {
    this.in = paramInputStream;
    this.httpEngine = paramHttpEngine;
    if (paramCacheRequest != null) {}
    for (OutputStream localOutputStream = paramCacheRequest.getBody();; localOutputStream = null)
    {
      if (localOutputStream == null) {
        paramCacheRequest = null;
      }
      this.cacheBody = localOutputStream;
      this.cacheRequest = paramCacheRequest;
      return;
    }
  }
  
  protected final void cacheWrite(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.cacheBody != null) {
      this.cacheBody.write(paramArrayOfByte, paramInt1, paramInt2);
    }
  }
  
  protected final void checkNotClosed()
    throws IOException
  {
    if (this.closed) {
      throw new IOException("stream closed");
    }
  }
  
  protected final void endOfInput(boolean paramBoolean)
    throws IOException
  {
    if (this.cacheRequest != null) {
      this.cacheBody.close();
    }
    this.httpEngine.release(paramBoolean);
  }
  
  public final int read()
    throws IOException
  {
    return Util.readSingleByte(this);
  }
  
  protected final void unexpectedEndOfInput()
  {
    if (this.cacheRequest != null) {
      this.cacheRequest.abort();
    }
    this.httpEngine.release(true);
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.http.AbstractHttpInputStream
 * JD-Core Version:    0.7.0.1
 */