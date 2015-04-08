package com.squareup.okhttp.internal.spdy;

import java.io.IOException;

public abstract interface IncomingStreamHandler
{
  public static final IncomingStreamHandler REFUSE_INCOMING_STREAMS = new IncomingStreamHandler()
  {
    public void receive(SpdyStream paramAnonymousSpdyStream)
      throws IOException
    {
      paramAnonymousSpdyStream.close(3);
    }
  };
  
  public abstract void receive(SpdyStream paramSpdyStream)
    throws IOException;
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.spdy.IncomingStreamHandler
 * JD-Core Version:    0.7.0.1
 */