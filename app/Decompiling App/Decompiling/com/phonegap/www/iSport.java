package com.phonegap.www;

import android.os.Bundle;
import org.apache.cordova.Config;
import org.apache.cordova.CordovaActivity;

public class iSport
  extends CordovaActivity
{
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    super.init();
    super.loadUrl(Config.getStartUrl());
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.phonegap.www.iSport
 * JD-Core Version:    0.7.0.1
 */