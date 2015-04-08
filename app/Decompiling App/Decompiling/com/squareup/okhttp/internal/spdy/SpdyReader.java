package com.squareup.okhttp.internal.spdy;

import com.squareup.okhttp.internal.Util;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ProtocolException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

final class SpdyReader
  implements Closeable
{
  static final byte[] DICTIONARY;
  private int compressedLimit;
  private final DataInputStream in;
  private final DataInputStream nameValueBlockIn;
  
  static
  {
    try
    {
      DICTIONARY = "".getBytes(Util.UTF_8.name());
      return;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new AssertionError();
    }
  }
  
  SpdyReader(InputStream paramInputStream)
  {
    this.in = new DataInputStream(paramInputStream);
    this.nameValueBlockIn = newNameValueBlockStream();
  }
  
  private static IOException ioException(String paramString, Object... paramVarArgs)
    throws IOException
  {
    throw new IOException(String.format(paramString, paramVarArgs));
  }
  
  private DataInputStream newNameValueBlockStream()
  {
    new DataInputStream(new InflaterInputStream(new InputStream()new Inflater
    {
      public void close()
        throws IOException
      {
        SpdyReader.this.in.close();
      }
      
      public int read()
        throws IOException
      {
        return Util.readSingleByte(this);
      }
      
      public int read(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
        throws IOException
      {
        int i = Math.min(paramAnonymousInt2, SpdyReader.this.compressedLimit);
        int j = SpdyReader.this.in.read(paramAnonymousArrayOfByte, paramAnonymousInt1, i);
        SpdyReader.access$020(SpdyReader.this, j);
        return j;
      }
    }, new Inflater()
    {
      public int inflate(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
        throws DataFormatException
      {
        int i = super.inflate(paramAnonymousArrayOfByte, paramAnonymousInt1, paramAnonymousInt2);
        if ((i == 0) && (needsDictionary()))
        {
          setDictionary(SpdyReader.DICTIONARY);
          i = super.inflate(paramAnonymousArrayOfByte, paramAnonymousInt1, paramAnonymousInt2);
        }
        return i;
      }
    }));
  }
  
  private void readGoAway(Handler paramHandler, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 != 8)
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Integer.valueOf(paramInt2);
      throw ioException("TYPE_GOAWAY length: %d != 8", arrayOfObject);
    }
    paramHandler.goAway(paramInt1, 0x7FFFFFFF & this.in.readInt(), this.in.readInt());
  }
  
  private void readHeaders(Handler paramHandler, int paramInt1, int paramInt2)
    throws IOException
  {
    paramHandler.headers(paramInt1, 0x7FFFFFFF & this.in.readInt(), readNameValueBlock(paramInt2 - 4));
  }
  
  private List<String> readNameValueBlock(int paramInt)
    throws IOException
  {
    this.compressedLimit = (paramInt + this.compressedLimit);
    int i;
    try
    {
      i = this.nameValueBlockIn.readInt();
      if (i < 0)
      {
        Logger.getLogger(getClass().getName()).warning("numberOfPairs < 0: " + i);
        throw ioException("numberOfPairs < 0", new Object[0]);
      }
    }
    catch (DataFormatException localDataFormatException)
    {
      throw new IOException(localDataFormatException.getMessage());
    }
    ArrayList localArrayList = new ArrayList(i * 2);
    for (int j = 0; j < i; j++)
    {
      String str1 = readString();
      String str2 = readString();
      if (str1.length() == 0) {
        throw ioException("name.length == 0", new Object[0]);
      }
      if (str2.length() == 0) {
        throw ioException("values.length == 0", new Object[0]);
      }
      localArrayList.add(str1);
      localArrayList.add(str2);
    }
    if (this.compressedLimit != 0) {
      Logger.getLogger(getClass().getName()).warning("compressedLimit > 0: " + this.compressedLimit);
    }
    return localArrayList;
  }
  
  private void readPing(Handler paramHandler, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 != 4)
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Integer.valueOf(paramInt2);
      throw ioException("TYPE_PING length: %d != 4", arrayOfObject);
    }
    paramHandler.ping(paramInt1, this.in.readInt());
  }
  
  private void readRstStream(Handler paramHandler, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 != 8)
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Integer.valueOf(paramInt2);
      throw ioException("TYPE_RST_STREAM length: %d != 8", arrayOfObject);
    }
    paramHandler.rstStream(paramInt1, 0x7FFFFFFF & this.in.readInt(), this.in.readInt());
  }
  
  private void readSettings(Handler paramHandler, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = this.in.readInt();
    if (paramInt2 != 4 + i * 8)
    {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = Integer.valueOf(paramInt2);
      arrayOfObject[1] = Integer.valueOf(i);
      throw ioException("TYPE_SETTINGS length: %d != 4 + 8 * %d", arrayOfObject);
    }
    Settings localSettings = new Settings();
    for (int j = 0; j < i; j++)
    {
      int k = this.in.readInt();
      int m = this.in.readInt();
      int n = (0xFF000000 & k) >>> 24;
      localSettings.set(k & 0xFFFFFF, n, m);
    }
    paramHandler.settings(paramInt1, localSettings);
  }
  
  private String readString()
    throws DataFormatException, IOException
  {
    int i = this.nameValueBlockIn.readInt();
    byte[] arrayOfByte = new byte[i];
    Util.readFully(this.nameValueBlockIn, arrayOfByte);
    return new String(arrayOfByte, 0, i, "UTF-8");
  }
  
  private void readSynReply(Handler paramHandler, int paramInt1, int paramInt2)
    throws IOException
  {
    paramHandler.synReply(paramInt1, 0x7FFFFFFF & this.in.readInt(), readNameValueBlock(paramInt2 - 4));
  }
  
  private void readSynStream(Handler paramHandler, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = this.in.readInt();
    int j = this.in.readInt();
    int k = this.in.readShort();
    paramHandler.synStream(paramInt1, i & 0x7FFFFFFF, j & 0x7FFFFFFF, (0xE000 & k) >>> 13, k & 0xFF, readNameValueBlock(paramInt2 - 10));
  }
  
  private void readWindowUpdate(Handler paramHandler, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 != 8)
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Integer.valueOf(paramInt2);
      throw ioException("TYPE_WINDOW_UPDATE length: %d != 8", arrayOfObject);
    }
    int i = this.in.readInt();
    int j = this.in.readInt();
    paramHandler.windowUpdate(paramInt1, i & 0x7FFFFFFF, j & 0x7FFFFFFF);
  }
  
  public void close()
    throws IOException
  {
    Util.closeAll(this.in, this.nameValueBlockIn);
  }
  
  public boolean nextFrame(Handler paramHandler)
    throws IOException
  {
    int i;
    int m;
    int n;
    int i2;
    for (;;)
    {
      try
      {
        i = this.in.readInt();
        int j = this.in.readInt();
        if ((0x80000000 & i) != 0)
        {
          k = 1;
          m = (0xFF000000 & j) >>> 24;
          n = j & 0xFFFFFF;
          if (k == 0) {
            break label346;
          }
          int i1 = (0x7FFF0000 & i) >>> 16;
          i2 = i & 0xFFFF;
          if (i1 == 3) {
            break;
          }
          throw new ProtocolException("version != 3: " + i1);
        }
      }
      catch (IOException localIOException)
      {
        return false;
      }
      int k = 0;
    }
    switch (i2)
    {
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 14: 
    case 15: 
    default: 
      throw new IOException("Unexpected frame");
    case 1: 
      readSynStream(paramHandler, m, n);
      return true;
    case 2: 
      readSynReply(paramHandler, m, n);
      return true;
    case 3: 
      readRstStream(paramHandler, m, n);
      return true;
    case 4: 
      readSettings(paramHandler, m, n);
      return true;
    case 5: 
      if (n != 0)
      {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Integer.valueOf(n);
        throw ioException("TYPE_NOOP length: %d != 0", arrayOfObject);
      }
      paramHandler.noop();
      return true;
    case 6: 
      readPing(paramHandler, m, n);
      return true;
    case 7: 
      readGoAway(paramHandler, m, n);
      return true;
    case 8: 
      readHeaders(paramHandler, m, n);
      return true;
    case 9: 
      readWindowUpdate(paramHandler, m, n);
      return true;
    }
    Util.skipByReading(this.in, n);
    throw new UnsupportedOperationException("TODO");
    label346:
    paramHandler.data(m, i & 0x7FFFFFFF, this.in, n);
    return true;
  }
  
  public static abstract interface Handler
  {
    public abstract void data(int paramInt1, int paramInt2, InputStream paramInputStream, int paramInt3)
      throws IOException;
    
    public abstract void goAway(int paramInt1, int paramInt2, int paramInt3);
    
    public abstract void headers(int paramInt1, int paramInt2, List<String> paramList)
      throws IOException;
    
    public abstract void noop();
    
    public abstract void ping(int paramInt1, int paramInt2);
    
    public abstract void rstStream(int paramInt1, int paramInt2, int paramInt3);
    
    public abstract void settings(int paramInt, Settings paramSettings);
    
    public abstract void synReply(int paramInt1, int paramInt2, List<String> paramList)
      throws IOException;
    
    public abstract void synStream(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, List<String> paramList);
    
    public abstract void windowUpdate(int paramInt1, int paramInt2, int paramInt3);
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.spdy.SpdyReader
 * JD-Core Version:    0.7.0.1
 */