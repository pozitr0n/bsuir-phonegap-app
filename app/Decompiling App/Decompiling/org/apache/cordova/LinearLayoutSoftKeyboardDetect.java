package org.apache.cordova;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;

public class LinearLayoutSoftKeyboardDetect
  extends LinearLayout
{
  private static final String TAG = "SoftKeyboardDetect";
  private CordovaActivity app = null;
  private int oldHeight = 0;
  private int oldWidth = 0;
  private int screenHeight = 0;
  private int screenWidth = 0;
  
  public LinearLayoutSoftKeyboardDetect(Context paramContext, int paramInt1, int paramInt2)
  {
    super(paramContext);
    this.screenWidth = paramInt1;
    this.screenHeight = paramInt2;
    this.app = ((CordovaActivity)paramContext);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    LOG.v("SoftKeyboardDetect", "We are in our onMeasure method");
    int i = View.MeasureSpec.getSize(paramInt2);
    int j = View.MeasureSpec.getSize(paramInt1);
    Object[] arrayOfObject1 = new Object[1];
    arrayOfObject1[0] = Integer.valueOf(this.oldHeight);
    LOG.v("SoftKeyboardDetect", "Old Height = %d", arrayOfObject1);
    Object[] arrayOfObject2 = new Object[1];
    arrayOfObject2[0] = Integer.valueOf(i);
    LOG.v("SoftKeyboardDetect", "Height = %d", arrayOfObject2);
    Object[] arrayOfObject3 = new Object[1];
    arrayOfObject3[0] = Integer.valueOf(this.oldWidth);
    LOG.v("SoftKeyboardDetect", "Old Width = %d", arrayOfObject3);
    Object[] arrayOfObject4 = new Object[1];
    arrayOfObject4[0] = Integer.valueOf(j);
    LOG.v("SoftKeyboardDetect", "Width = %d", arrayOfObject4);
    if ((this.oldHeight == 0) || (this.oldHeight == i)) {
      LOG.d("SoftKeyboardDetect", "Ignore this event");
    }
    for (;;)
    {
      this.oldHeight = i;
      this.oldWidth = j;
      return;
      if (this.screenHeight == j)
      {
        int k = this.screenHeight;
        this.screenHeight = this.screenWidth;
        this.screenWidth = k;
        LOG.v("SoftKeyboardDetect", "Orientation Change");
      }
      else if (i > this.oldHeight)
      {
        if (this.app != null) {
          this.app.appView.sendJavascript("cordova.fireDocumentEvent('hidekeyboard');");
        }
      }
      else if ((i < this.oldHeight) && (this.app != null))
      {
        this.app.appView.sendJavascript("cordova.fireDocumentEvent('showkeyboard');");
      }
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.LinearLayoutSoftKeyboardDetect
 * JD-Core Version:    0.7.0.1
 */