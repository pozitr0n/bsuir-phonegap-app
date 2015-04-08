package com.squareup.okhttp.internal;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract interface Dns
{
  public static final Dns DEFAULT = new Dns()
  {
    public InetAddress[] getAllByName(String paramAnonymousString)
      throws UnknownHostException
    {
      return InetAddress.getAllByName(paramAnonymousString);
    }
  };
  
  public abstract InetAddress[] getAllByName(String paramString)
    throws UnknownHostException;
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.Dns
 * JD-Core Version:    0.7.0.1
 */