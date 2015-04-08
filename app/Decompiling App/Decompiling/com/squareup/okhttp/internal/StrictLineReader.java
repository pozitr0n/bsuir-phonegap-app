package com.squareup.okhttp.internal;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class StrictLineReader
  implements Closeable
{
  private static final byte CR = 13;
  private static final byte LF = 10;
  private byte[] buf;
  private final Charset charset;
  private int end;
  private final InputStream in;
  private int pos;
  
  public StrictLineReader(InputStream paramInputStream, int paramInt, Charset paramCharset)
  {
    if ((paramInputStream == null) || (paramCharset == null)) {
      throw new NullPointerException();
    }
    if (paramInt < 0) {
      throw new IllegalArgumentException("capacity <= 0");
    }
    if (!paramCharset.equals(Util.US_ASCII)) {
      throw new IllegalArgumentException("Unsupported encoding");
    }
    this.in = paramInputStream;
    this.charset = paramCharset;
    this.buf = new byte[paramInt];
  }
  
  public StrictLineReader(InputStream paramInputStream, Charset paramCharset)
  {
    this(paramInputStream, 8192, paramCharset);
  }
  
  private void fillBuf()
    throws IOException
  {
    int i = this.in.read(this.buf, 0, this.buf.length);
    if (i == -1) {
      throw new EOFException();
    }
    this.pos = 0;
    this.end = i;
  }
  
  public void close()
    throws IOException
  {
    synchronized (this.in)
    {
      if (this.buf != null)
      {
        this.buf = null;
        this.in.close();
      }
      return;
    }
  }
  
  public int readInt()
    throws IOException
  {
    String str = readLine();
    try
    {
      int i = Integer.parseInt(str);
      return i;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new IOException("expected an int but was \"" + str + "\"");
    }
  }
  
  public String readLine()
    throws IOException
  {
    synchronized (this.in)
    {
      if (this.buf == null) {
        throw new IOException("LineReader is closed");
      }
    }
    if (this.pos >= this.end) {
      fillBuf();
    }
    for (int i = this.pos;; i++)
    {
      if (i != this.end)
      {
        if (this.buf[i] != 10) {
          continue;
        }
        if ((i == this.pos) || (this.buf[(i - 1)] != 13)) {
          break label272;
        }
      }
      label272:
      for (int k = i - 1;; k = i)
      {
        String str2 = new String(this.buf, this.pos, k - this.pos, this.charset.name());
        this.pos = (i + 1);
        return str2;
        ByteArrayOutputStream local1 = new ByteArrayOutputStream(80 + (this.end - this.pos))
        {
          public String toString()
          {
            if ((this.count > 0) && (this.buf[(-1 + this.count)] == 13)) {}
            for (int i = -1 + this.count;; i = this.count) {
              try
              {
                String str = new String(this.buf, 0, i, StrictLineReader.this.charset.name());
                return str;
              }
              catch (UnsupportedEncodingException localUnsupportedEncodingException)
              {
                throw new AssertionError(localUnsupportedEncodingException);
              }
            }
          }
        };
        for (;;)
        {
          local1.write(this.buf, this.pos, this.end - this.pos);
          this.end = -1;
          fillBuf();
          for (int j = this.pos; j != this.end; j++) {
            if (this.buf[j] == 10)
            {
              if (j != this.pos) {
                local1.write(this.buf, this.pos, j - this.pos);
              }
              this.pos = (j + 1);
              String str1 = local1.toString();
              return str1;
            }
          }
        }
      }
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.StrictLineReader
 * JD-Core Version:    0.7.0.1
 */