package org.apache.cordova;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import java.io.File;

@Deprecated
public class DirectoryManager
{
  private static final String LOG_TAG = "DirectoryManager";
  
  private static File constructFilePaths(String paramString1, String paramString2)
  {
    if (paramString2.startsWith(paramString1)) {
      return new File(paramString2);
    }
    return new File(paramString1 + "/" + paramString2);
  }
  
  private static long freeSpaceCalculation(String paramString)
  {
    StatFs localStatFs = new StatFs(paramString);
    return localStatFs.getBlockSize() * localStatFs.getAvailableBlocks() / 1024L;
  }
  
  public static long getFreeDiskSpace(boolean paramBoolean)
  {
    if (Environment.getExternalStorageState().equals("mounted")) {}
    for (long l = freeSpaceCalculation(Environment.getExternalStorageDirectory().getPath());; l = freeSpaceCalculation("/"))
    {
      return l;
      if (!paramBoolean) {
        break;
      }
    }
    return -1L;
  }
  
  public static String getTempDirectoryPath(Context paramContext)
  {
    if (Environment.getExternalStorageState().equals("mounted")) {}
    for (File localFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + paramContext.getPackageName() + "/cache/");; localFile = paramContext.getCacheDir())
    {
      if (!localFile.exists()) {
        localFile.mkdirs();
      }
      return localFile.getAbsolutePath();
    }
  }
  
  public static boolean testFileExists(String paramString)
  {
    if ((testSaveLocationExists()) && (!paramString.equals(""))) {
      return constructFilePaths(Environment.getExternalStorageDirectory().toString(), paramString).exists();
    }
    return false;
  }
  
  public static boolean testSaveLocationExists()
  {
    return Environment.getExternalStorageState().equals("mounted");
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.DirectoryManager
 * JD-Core Version:    0.7.0.1
 */