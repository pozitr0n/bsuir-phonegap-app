package com.squareup.okhttp;

public enum ResponseSource
{
  static
  {
    ResponseSource[] arrayOfResponseSource = new ResponseSource[3];
    arrayOfResponseSource[0] = CACHE;
    arrayOfResponseSource[1] = CONDITIONAL_CACHE;
    arrayOfResponseSource[2] = NETWORK;
    $VALUES = arrayOfResponseSource;
  }
  
  private ResponseSource() {}
  
  public boolean requiresConnection()
  {
    return (this == CONDITIONAL_CACHE) || (this == NETWORK);
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.ResponseSource
 * JD-Core Version:    0.7.0.1
 */