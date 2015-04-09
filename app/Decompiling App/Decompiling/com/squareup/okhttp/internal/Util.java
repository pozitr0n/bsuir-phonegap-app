package com.squareup.okhttp.internal;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicReference;

public final class Util
{
  public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
  public static final String[] EMPTY_STRING_ARRAY = new String[0];
  public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
  public static final Charset US_ASCII = Charset.forName("US-ASCII");
  public static final Charset UTF_8 = Charset.forName("UTF-8");
  private static AtomicReference<byte[]> skipBuffer = new AtomicReference();
  
  public static void checkOffsetAndCount(int paramInt1, int paramInt2, int paramInt3)
  {
    if (((paramInt2 | paramInt3) < 0) || (paramInt2 > paramInt1) || (paramInt1 - paramInt2 < paramInt3)) {
      throw new ArrayIndexOutOfBoundsException();
    }
  }
  
  public static void closeAll(Closeable paramCloseable1, Closeable paramCloseable2)
    throws IOException
  {
    Object localObject = null;
    try
    {
      paramCloseable1.close();
    }
    catch (Throwable localThrowable1)
    {
      try
      {
        for (;;)
        {
          paramCloseable2.close();
          if (localObject != null) {
            break;
          }
          return;
          localThrowable1 = localThrowable1;
          localObject = localThrowable1;
        }
      }
      catch (Throwable localThrowable2)
      {
        for (;;)
        {
          if (localObject == null) {
            localObject = localThrowable2;
          }
        }
        if ((localObject instanceof IOException)) {
          throw ((IOException)localObject);
        }
        if ((localObject instanceof RuntimeException)) {
          throw ((RuntimeException)localObject);
        }
        if ((localObject instanceof Error)) {
          throw ((Error)localObject);
        }
        throw new AssertionError(localObject);
      }
    }
  }
  
  public static void closeQuietly(Closeable paramCloseable)
  {
    if (paramCloseable != null) {}
    try
    {
      paramCloseable.close();
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Exception localException) {}
  }
  
  public static void closeQuietly(Socket paramSocket)
  {
    if (paramSocket != null) {}
    try
    {
      paramSocket.close();
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Exception localException) {}
  }
  
  public static int copy(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    int i = 0;
    byte[] arrayOfByte = new byte[8192];
    for (;;)
    {
      int j = paramInputStream.read(arrayOfByte);
      if (j == -1) {
        break;
      }
      i += j;
      paramOutputStream.write(arrayOfByte, 0, j);
    }
    return i;
  }
  
  public static void deleteContents(File paramFile)
    throws IOException
  {
    File[] arrayOfFile = paramFile.listFiles();
    if (arrayOfFile == null) {
      throw new IOException("not a readable directory: " + paramFile);
    }
    int i = arrayOfFile.length;
    for (int j = 0; j < i; j++)
    {
      File localFile = arrayOfFile[j];
      if (localFile.isDirectory()) {
        deleteContents(localFile);
      }
      if (!localFile.delete()) {
        throw new IOException("failed to delete file: " + localFile);
      }
    }
  }
  
  public static boolean equal(Object paramObject1, Object paramObject2)
  {
    return (paramObject1 == paramObject2) || ((paramObject1 != null) && (paramObject1.equals(paramObject2)));
  }
  
  public static int getDefaultPort(String paramString)
  {
    if ("http".equalsIgnoreCase(paramString)) {
      return 80;
    }
    if ("https".equalsIgnoreCase(paramString)) {
      return 443;
    }
    return -1;
  }
  
  private static int getEffectivePort(String paramString, int paramInt)
  {
    if (paramInt != -1) {
      return paramInt;
    }
    return getDefaultPort(paramString);
  }
  
  public static int getEffectivePort(URI paramURI)
  {
    return getEffectivePort(paramURI.getScheme(), paramURI.getPort());
  }
  
  public static int getEffectivePort(URL paramURL)
  {
    return getEffectivePort(paramURL.getProtocol(), paramURL.getPort());
  }
  
  public static void pokeInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2, ByteOrder paramByteOrder)
  {
    if (paramByteOrder == ByteOrder.BIG_ENDIAN)
    {
      int m = paramInt1 + 1;
      paramArrayOfByte[paramInt1] = ((byte)(0xFF & paramInt2 >> 24));
      int n = m + 1;
      paramArrayOfByte[m] = ((byte)(0xFF & paramInt2 >> 16));
      int i1 = n + 1;
      paramArrayOfByte[n] = ((byte)(0xFF & paramInt2 >> 8));
      paramArrayOfByte[i1] = ((byte)(0xFF & paramInt2 >> 0));
      return;
    }
    int i = paramInt1 + 1;
    paramArrayOfByte[paramInt1] = ((byte)(0xFF & paramInt2 >> 0));
    int j = i + 1;
    paramArrayOfByte[i] = ((byte)(0xFF & paramInt2 >> 8));
    int k = j + 1;
    paramArrayOfByte[j] = ((byte)(0xFF & paramInt2 >> 16));
    paramArrayOfByte[k] = ((byte)(0xFF & paramInt2 >> 24));
  }
  
  public static String readAsciiLine(InputStream paramInputStream)
    throws IOException
  {
    StringBuilder localStringBuilder = new StringBuilder(80);
    for (;;)
    {
      int i = paramInputStream.read();
      if (i == -1) {
        throw new EOFException();
      }
      if (i == 10)
      {
        int j = localStringBuilder.length();
        if ((j > 0) && (localStringBuilder.charAt(j - 1) == '\r')) {
          localStringBuilder.setLength(j - 1);
        }
        return localStringBuilder.toString();
      }
      localStringBuilder.append((char)i);
    }
  }
  
  public static String readFully(Reader paramReader)
    throws IOException
  {
    try
    {
      StringWriter localStringWriter = new StringWriter();
      char[] arrayOfChar = new char[1024];
      for (;;)
      {
        int i = paramReader.read(arrayOfChar);
        if (i == -1) {
          break;
        }
        localStringWriter.write(arrayOfChar, 0, i);
      }
      str = localStringWriter.toString();
    }
    finally
    {
      paramReader.close();
    }
    String str;
    paramReader.close();
    return str;
  }
  
  public static void readFully(InputStream paramInputStream, byte[] paramArrayOfByte)
    throws IOException
  {
    readFully(paramInputStream, paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static void readFully(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 == 0) {}
    for (;;)
    {
      return;
      if (paramInputStream == null) {
        throw new NullPointerException("in == null");
      }
      if (paramArrayOfByte == null) {
        throw new NullPointerException("dst == null");
      }
      checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
      while (paramInt2 > 0)
      {
        int i = paramInputStream.read(paramArrayOfByte, paramInt1, paramInt2);
        if (i < 0) {
          throw new EOFException();
        }
        paramInt1 += i;
        paramInt2 -= i;
      }
    }
  }
  
  public static int readSingleByte(InputStream paramInputStream)
    throws IOException
  {
    int i = -1;
    byte[] arrayOfByte = new byte[1];
    if (paramInputStream.read(arrayOfByte, 0, 1) != i) {
      i = 0xFF & arrayOfByte[0];
    }
    return i;
  }
  
  public static void skipAll(InputStream paramInputStream)
    throws IOException
  {
    do
    {
      paramInputStream.skip(9223372036854775807L);
    } while (paramInputStream.read() != -1);
  }
  
  public static long skipByReading(InputStream paramInputStream, long paramLong)
    throws IOException
  {
    byte[] arrayOfByte = (byte[])skipBuffer.getAndSet(null);
    if (arrayOfByte == null) {
      arrayOfByte = new byte[4096];
    }
    long l = 0L;
    int i;
    int j;
    if (l < paramLong)
    {
      i = (int)Math.min(paramLong - l, arrayOfByte.length);
      j = paramInputStream.read(arrayOfByte, 0, i);
      if (j != -1) {
        break label70;
      }
    }
    for (;;)
    {
      skipBuffer.set(arrayOfByte);
      return l;
      label70:
      l += j;
      if (j >= i) {
        break;
      }
    }
  }
  
  public static void writeSingleByte(OutputStream paramOutputStream, int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = new byte[1];
    arrayOfByte[0] = ((byte)(paramInt & 0xFF));
    paramOutputStream.write(arrayOfByte);
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.Util
 * JD-Core Version:    0.7.0.1
 */