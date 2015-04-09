package com.squareup.okhttp;

import java.io.IOException;
import java.net.CacheResponse;
import java.net.HttpURLConnection;

public abstract interface OkResponseCache
{
  public abstract void trackConditionalCacheHit();
  
  public abstract void trackResponse(ResponseSource paramResponseSource);
  
  public abstract void update(CacheResponse paramCacheResponse, HttpURLConnection paramHttpURLConnection)
    throws IOException;
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.OkResponseCache
 * JD-Core Version:    0.7.0.1
 */