package com.squareup.okhttp.internal;

import java.io.UnsupportedEncodingException;

public final class Base64
{
  private static final byte[] MAP = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
  
  public static byte[] decode(byte[] paramArrayOfByte)
  {
    return decode(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  public static byte[] decode(byte[] paramArrayOfByte, int paramInt)
  {
    int i = 3 * (paramInt / 4);
    if (i == 0) {
      return Util.EMPTY_BYTE_ARRAY;
    }
    byte[] arrayOfByte1 = new byte[i];
    int j = 0;
    int k = paramArrayOfByte[(paramInt - 1)];
    if ((k == 10) || (k == 13) || (k == 32) || (k == 9)) {}
    for (;;)
    {
      paramInt--;
      break;
      if (k != 61) {
        break label75;
      }
      j++;
    }
    label75:
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i5;
    int i6;
    if (i1 < paramInt)
    {
      i5 = paramArrayOfByte[i1];
      if ((i5 == 10) || (i5 == 13) || (i5 == 32)) {
        break label392;
      }
      if (i5 == 9) {
        i6 = i2;
      }
    }
    for (;;)
    {
      i1++;
      i2 = i6;
      break;
      int i7;
      if ((i5 >= 65) && (i5 <= 90))
      {
        i7 = i5 - 65;
        label162:
        n = n << 6 | (byte)i7;
        if (m % 4 != 3) {
          break label385;
        }
        int i8 = i2 + 1;
        arrayOfByte1[i2] = ((byte)(n >> 16));
        int i9 = i8 + 1;
        arrayOfByte1[i8] = ((byte)(n >> 8));
        i6 = i9 + 1;
        arrayOfByte1[i9] = ((byte)n);
      }
      for (;;)
      {
        m++;
        break;
        if ((i5 >= 97) && (i5 <= 122))
        {
          i7 = i5 - 71;
          break label162;
        }
        if ((i5 >= 48) && (i5 <= 57))
        {
          i7 = i5 + 4;
          break label162;
        }
        if (i5 == 43)
        {
          i7 = 62;
          break label162;
        }
        if (i5 == 47)
        {
          i7 = 63;
          break label162;
        }
        return null;
        int i3;
        if (j > 0)
        {
          int i4 = n << j * 6;
          i3 = i2 + 1;
          arrayOfByte1[i2] = ((byte)(i4 >> 16));
          if (j == 1)
          {
            i2 = i3 + 1;
            arrayOfByte1[i3] = ((byte)(i4 >> 8));
          }
        }
        else
        {
          i3 = i2;
        }
        byte[] arrayOfByte2 = new byte[i3];
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i3);
        return arrayOfByte2;
        label385:
        i6 = i2;
      }
      label392:
      i6 = i2;
    }
  }
  
  public static String encode(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = new byte[4 * (2 + paramArrayOfByte.length) / 3];
    int i = paramArrayOfByte.length - paramArrayOfByte.length % 3;
    int j = 0;
    int k = 0;
    while (j < i)
    {
      int i7 = k + 1;
      arrayOfByte[k] = MAP[((0xFF & paramArrayOfByte[j]) >> 2)];
      int i8 = i7 + 1;
      arrayOfByte[i7] = MAP[((0x3 & paramArrayOfByte[j]) << 4 | (0xFF & paramArrayOfByte[(j + 1)]) >> 4)];
      int i9 = i8 + 1;
      arrayOfByte[i8] = MAP[((0xF & paramArrayOfByte[(j + 1)]) << 2 | (0xFF & paramArrayOfByte[(j + 2)]) >> 6)];
      k = i9 + 1;
      arrayOfByte[i9] = MAP[(0x3F & paramArrayOfByte[(j + 2)])];
      j += 3;
    }
    switch (paramArrayOfByte.length % 3)
    {
    }
    for (;;)
    {
      for (int i2 = k;; i2 = i6)
      {
        try
        {
          String str = new String(arrayOfByte, 0, i2, "US-ASCII");
          return str;
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
          int i3;
          int i4;
          int i5;
          int i6;
          int m;
          int n;
          int i1;
          throw new AssertionError(localUnsupportedEncodingException);
        }
        i3 = k + 1;
        arrayOfByte[k] = MAP[((0xFF & paramArrayOfByte[i]) >> 2)];
        i4 = i3 + 1;
        arrayOfByte[i3] = MAP[((0x3 & paramArrayOfByte[i]) << 4)];
        i5 = i4 + 1;
        arrayOfByte[i4] = 61;
        i6 = i5 + 1;
        arrayOfByte[i5] = 61;
      }
      m = k + 1;
      arrayOfByte[k] = MAP[((0xFF & paramArrayOfByte[i]) >> 2)];
      n = m + 1;
      arrayOfByte[m] = MAP[((0x3 & paramArrayOfByte[i]) << 4 | (0xFF & paramArrayOfByte[(i + 1)]) >> 4)];
      i1 = n + 1;
      arrayOfByte[n] = MAP[((0xF & paramArrayOfByte[(i + 1)]) << 2)];
      k = i1 + 1;
      arrayOfByte[i1] = 61;
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.Base64
 * JD-Core Version:    0.7.0.1
 */