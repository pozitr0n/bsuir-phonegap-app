package com.squareup.okhttp.internal;

public abstract class NamedRunnable
  implements Runnable
{
  private String name;
  
  public NamedRunnable(String paramString)
  {
    this.name = paramString;
  }
  
  protected abstract void execute();
  
  public final void run()
  {
    String str = Thread.currentThread().getName();
    Thread.currentThread().setName(this.name);
    try
    {
      execute();
      return;
    }
    finally
    {
      Thread.currentThread().setName(str);
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.NamedRunnable
 * JD-Core Version:    0.7.0.1
 */