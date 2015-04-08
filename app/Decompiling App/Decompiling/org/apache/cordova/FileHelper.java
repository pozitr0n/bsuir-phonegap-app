package org.apache.cordova;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

@Deprecated
public class FileHelper
{
  private static final String LOG_TAG = "FileUtils";
  private static final String _DATA = "_data";
  
  public static InputStream getInputStreamFromUriString(String paramString, CordovaInterface paramCordovaInterface)
    throws IOException
  {
    if (paramString.startsWith("content"))
    {
      Uri localUri = Uri.parse(paramString);
      return paramCordovaInterface.getActivity().getContentResolver().openInputStream(localUri);
    }
    if (paramString.startsWith("file://"))
    {
      int i = paramString.indexOf("?");
      if (i > -1) {
        paramString = paramString.substring(0, i);
      }
      if (paramString.startsWith("file:///android_asset/"))
      {
        String str = Uri.parse(paramString).getPath().substring(15);
        return paramCordovaInterface.getActivity().getAssets().open(str);
      }
      return new FileInputStream(getRealPath(paramString, paramCordovaInterface));
    }
    return new FileInputStream(getRealPath(paramString, paramCordovaInterface));
  }
  
  public static String getMimeType(String paramString, CordovaInterface paramCordovaInterface)
  {
    Uri localUri = Uri.parse(paramString);
    if (paramString.startsWith("content://")) {
      return paramCordovaInterface.getActivity().getContentResolver().getType(localUri);
    }
    return getMimeTypeForExtension(localUri.getPath());
  }
  
  public static String getMimeTypeForExtension(String paramString)
  {
    String str1 = paramString;
    int i = str1.lastIndexOf('.');
    if (i != -1) {
      str1 = str1.substring(i + 1);
    }
    String str2 = str1.toLowerCase(Locale.getDefault());
    if (str2.equals("3ga")) {
      return "audio/3gpp";
    }
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(str2);
  }
  
  public static String getRealPath(Uri paramUri, CordovaInterface paramCordovaInterface)
  {
    return getRealPath(paramUri.toString(), paramCordovaInterface);
  }
  
  public static String getRealPath(String paramString, CordovaInterface paramCordovaInterface)
  {
    String str;
    if (paramString.startsWith("content://"))
    {
      String[] arrayOfString = { "_data" };
      Cursor localCursor = paramCordovaInterface.getActivity().managedQuery(Uri.parse(paramString), arrayOfString, null, null, null);
      int i = localCursor.getColumnIndexOrThrow("_data");
      localCursor.moveToFirst();
      str = localCursor.getString(i);
      if (str == null) {
        LOG.e("FileUtils", "Could get real path for URI string %s", new Object[] { paramString });
      }
    }
    do
    {
      return str;
      if (!paramString.startsWith("file://")) {
        break;
      }
      str = paramString.substring(7);
    } while (!str.startsWith("/android_asset/"));
    LOG.e("FileUtils", "Cannot get real path for URI string %s because it is a file:///android_asset/ URI.", new Object[] { paramString });
    return null;
    return paramString;
  }
  
  public static String stripFileProtocol(String paramString)
  {
    if (paramString.startsWith("file://")) {
      paramString = paramString.substring(7);
    }
    return paramString;
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.FileHelper
 * JD-Core Version:    0.7.0.1
 */