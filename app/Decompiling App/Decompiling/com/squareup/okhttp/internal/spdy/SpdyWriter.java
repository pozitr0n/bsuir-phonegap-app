package com.squareup.okhttp.internal.spdy;

import com.squareup.okhttp.internal.Platform;
import com.squareup.okhttp.internal.Util;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.zip.Deflater;

final class SpdyWriter
  implements Closeable
{
  private final ByteArrayOutputStream nameValueBlockBuffer;
  private final DataOutputStream nameValueBlockOut;
  final DataOutputStream out;
  
  SpdyWriter(OutputStream paramOutputStream)
  {
    this.out = new DataOutputStream(paramOutputStream);
    Deflater localDeflater = new Deflater();
    localDeflater.setDictionary(SpdyReader.DICTIONARY);
    this.nameValueBlockBuffer = new ByteArrayOutputStream();
    this.nameValueBlockOut = new DataOutputStream(Platform.get().newDeflaterOutputStream(this.nameValueBlockBuffer, localDeflater, true));
  }
  
  private void writeNameValueBlockToBuffer(List<String> paramList)
    throws IOException
  {
    this.nameValueBlockBuffer.reset();
    int i = paramList.size() / 2;
    this.nameValueBlockOut.writeInt(i);
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      this.nameValueBlockOut.writeInt(str.length());
      this.nameValueBlockOut.write(str.getBytes("UTF-8"));
    }
    this.nameValueBlockOut.flush();
  }
  
  public void close()
    throws IOException
  {
    Util.closeAll(this.out, this.nameValueBlockOut);
  }
  
  public void data(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    throws IOException
  {
    try
    {
      int i = paramArrayOfByte.length;
      this.out.writeInt(0x7FFFFFFF & paramInt2);
      this.out.writeInt((paramInt1 & 0xFF) << 24 | 0xFFFFFF & i);
      this.out.write(paramArrayOfByte);
      this.out.flush();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void goAway(int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    try
    {
      this.out.writeInt(-2147287033);
      this.out.writeInt(0x8 | (paramInt1 & 0xFF) << 24);
      this.out.writeInt(paramInt2);
      this.out.writeInt(paramInt3);
      this.out.flush();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void headers(int paramInt1, int paramInt2, List<String> paramList)
    throws IOException
  {
    try
    {
      writeNameValueBlockToBuffer(paramList);
      int i = 4 + this.nameValueBlockBuffer.size();
      this.out.writeInt(-2147287032);
      this.out.writeInt((paramInt1 & 0xFF) << 24 | 0xFFFFFF & i);
      this.out.writeInt(0x7FFFFFFF & paramInt2);
      this.nameValueBlockBuffer.writeTo(this.out);
      this.out.flush();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void noop()
    throws IOException
  {
    try
    {
      this.out.writeInt(-2147287035);
      this.out.writeInt(0);
      this.out.flush();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void ping(int paramInt1, int paramInt2)
    throws IOException
  {
    try
    {
      this.out.writeInt(-2147287034);
      this.out.writeInt(0x4 | (paramInt1 & 0xFF) << 24);
      this.out.writeInt(paramInt2);
      this.out.flush();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void rstStream(int paramInt1, int paramInt2)
    throws IOException
  {
    try
    {
      this.out.writeInt(-2147287037);
      this.out.writeInt(8);
      this.out.writeInt(0x7FFFFFFF & paramInt1);
      this.out.writeInt(paramInt2);
      this.out.flush();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void settings(int paramInt, Settings paramSettings)
    throws IOException
  {
    for (;;)
    {
      int k;
      try
      {
        int i = paramSettings.size();
        int j = 4 + i * 8;
        this.out.writeInt(-2147287036);
        this.out.writeInt((paramInt & 0xFF) << 24 | j & 0xFFFFFF);
        this.out.writeInt(i);
        k = 0;
        if (k <= 9)
        {
          if (!paramSettings.isSet(k)) {
            break label139;
          }
          int m = paramSettings.flags(k);
          this.out.writeInt((m & 0xFF) << 24 | k & 0xFFFFFF);
          this.out.writeInt(paramSettings.get(k));
        }
      }
      finally {}
      this.out.flush();
      return;
      label139:
      k++;
    }
  }
  
  public void synReply(int paramInt1, int paramInt2, List<String> paramList)
    throws IOException
  {
    try
    {
      writeNameValueBlockToBuffer(paramList);
      int i = 4 + this.nameValueBlockBuffer.size();
      this.out.writeInt(-2147287038);
      this.out.writeInt((paramInt1 & 0xFF) << 24 | 0xFFFFFF & i);
      this.out.writeInt(0x7FFFFFFF & paramInt2);
      this.nameValueBlockBuffer.writeTo(this.out);
      this.out.flush();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void synStream(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, List<String> paramList)
    throws IOException
  {
    try
    {
      writeNameValueBlockToBuffer(paramList);
      int i = 10 + this.nameValueBlockBuffer.size();
      this.out.writeInt(-2147287039);
      this.out.writeInt((paramInt1 & 0xFF) << 24 | 0xFFFFFF & i);
      this.out.writeInt(paramInt2 & 0x7FFFFFFF);
      this.out.writeInt(paramInt3 & 0x7FFFFFFF);
      this.out.writeShort(0x0 | (paramInt4 & 0x7) << 13 | paramInt5 & 0xFF);
      this.nameValueBlockBuffer.writeTo(this.out);
      this.out.flush();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void windowUpdate(int paramInt1, int paramInt2)
    throws IOException
  {
    try
    {
      this.out.writeInt(-2147287031);
      this.out.writeInt(8);
      this.out.writeInt(paramInt1);
      this.out.writeInt(paramInt2);
      this.out.flush();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.spdy.SpdyWriter
 * JD-Core Version:    0.7.0.1
 */