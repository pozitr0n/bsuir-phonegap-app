package org.apache.cordova;

import android.view.View;

public class ScrollEvent
{
  public int l;
  public int nl;
  public int nt;
  public int t;
  private View targetView;
  
  ScrollEvent(int paramInt1, int paramInt2, int paramInt3, int paramInt4, View paramView)
  {
    this.l = paramInt3;
    this.nl = paramInt1;
    this.nt = paramInt2;
    this.targetView = paramView;
  }
  
  public int dl()
  {
    return this.nl - this.l;
  }
  
  public int dt()
  {
    return this.nt - this.t;
  }
  
  public View getTargetView()
  {
    return this.targetView;
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.ScrollEvent
 * JD-Core Version:    0.7.0.1
 */