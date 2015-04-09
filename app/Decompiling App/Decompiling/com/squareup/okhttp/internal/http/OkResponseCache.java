package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.ResponseSource;
import java.io.IOException;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public abstract interface OkResponseCache
{
  public abstract CacheResponse get(URI paramURI, String paramString, Map<String, List<String>> paramMap)
    throws IOException;
  
  public abstract CacheRequest put(URI paramURI, URLConnection paramURLConnection)
    throws IOException;
  
  public abstract void trackConditionalCacheHit();
  
  public abstract void trackResponse(ResponseSource paramResponseSource);
  
  public abstract void update(CacheResponse paramCacheResponse, HttpURLConnection paramHttpURLConnection)
    throws IOException;
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.http.OkResponseCache
 * JD-Core Version:    0.7.0.1
 */