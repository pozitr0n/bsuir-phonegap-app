package org.apache.cordova;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.util.Base64;
import android.webkit.MimeTypeMap;
import com.squareup.okhttp.OkHttpClient;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Locale;
import org.apache.http.util.EncodingUtils;

public class CordovaResourceApi
{
  private static final String[] LOCAL_FILE_PROJECTION = { "_data" };
  private static final String LOG_TAG = "CordovaResourceApi";
  public static final int URI_TYPE_ASSET = 1;
  public static final int URI_TYPE_CONTENT = 2;
  public static final int URI_TYPE_DATA = 4;
  public static final int URI_TYPE_FILE = 0;
  public static final int URI_TYPE_HTTP = 5;
  public static final int URI_TYPE_HTTPS = 6;
  public static final int URI_TYPE_RESOURCE = 3;
  public static final int URI_TYPE_UNKNOWN = -1;
  private static OkHttpClient httpClient = new OkHttpClient();
  static Thread jsThread;
  private final AssetManager assetManager;
  private final ContentResolver contentResolver;
  private final PluginManager pluginManager;
  private boolean threadCheckingEnabled = true;
  
  public CordovaResourceApi(Context paramContext, PluginManager paramPluginManager)
  {
    this.contentResolver = paramContext.getContentResolver();
    this.assetManager = paramContext.getAssets();
    this.pluginManager = paramPluginManager;
  }
  
  private void assertBackgroundThread()
  {
    if (this.threadCheckingEnabled)
    {
      Thread localThread = Thread.currentThread();
      if (localThread == Looper.getMainLooper().getThread()) {
        throw new IllegalStateException("Do not perform IO operations on the UI thread. Use CordovaInterface.getThreadPool() instead.");
      }
      if (localThread == jsThread) {
        throw new IllegalStateException("Tried to perform an IO operation on the WebCore thread. Use CordovaInterface.getThreadPool() instead.");
      }
    }
  }
  
  private static void assertNonRelative(Uri paramUri)
  {
    if (!paramUri.isAbsolute()) {
      throw new IllegalArgumentException("Relative URIs are not supported.");
    }
  }
  
  private String getDataUriMimeType(Uri paramUri)
  {
    String str = paramUri.getSchemeSpecificPart();
    int i = str.indexOf(',');
    if (i == -1) {}
    String[] arrayOfString;
    do
    {
      return null;
      arrayOfString = str.substring(0, i).split(";");
    } while (arrayOfString.length <= 0);
    return arrayOfString[0];
  }
  
  private String getMimeTypeFromPath(String paramString)
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
  
  public static int getUriType(Uri paramUri)
  {
    assertNonRelative(paramUri);
    String str = paramUri.getScheme();
    if ("content".equals(str)) {
      return 2;
    }
    if ("android.resource".equals(str)) {
      return 3;
    }
    if ("file".equals(str))
    {
      if (paramUri.getPath().startsWith("/android_asset/")) {
        return 1;
      }
      return 0;
    }
    if ("data".equals(str)) {
      return 4;
    }
    if ("http".equals(str)) {
      return 5;
    }
    if ("https".equals(str)) {
      return 6;
    }
    return -1;
  }
  
  private OpenForReadResult readDataUri(Uri paramUri)
  {
    String str1 = paramUri.getSchemeSpecificPart();
    int i = str1.indexOf(',');
    if (i == -1) {
      return null;
    }
    String[] arrayOfString = str1.substring(0, i).split(";");
    int j = 0;
    int k = arrayOfString.length;
    String str2 = null;
    if (k > 0) {
      str2 = arrayOfString[0];
    }
    for (int m = 1; m < arrayOfString.length; m++) {
      if ("base64".equalsIgnoreCase(arrayOfString[m])) {
        j = 1;
      }
    }
    String str3 = str1.substring(i + 1);
    if (j != 0) {}
    for (byte[] arrayOfByte = Base64.decode(str3, 0);; arrayOfByte = EncodingUtils.getBytes(str3, "UTF-8")) {
      return new OpenForReadResult(paramUri, new ByteArrayInputStream(arrayOfByte), str2, arrayOfByte.length, null);
    }
  }
  
  public void copyResource(Uri paramUri, OutputStream paramOutputStream)
    throws IOException
  {
    copyResource(openForRead(paramUri), paramOutputStream);
  }
  
  /* Error */
  public void copyResource(OpenForReadResult paramOpenForReadResult, OutputStream paramOutputStream)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 240	org/apache/cordova/CordovaResourceApi:assertBackgroundThread	()V
    //   4: aload_1
    //   5: getfield 244	org/apache/cordova/CordovaResourceApi$OpenForReadResult:inputStream	Ljava/io/InputStream;
    //   8: astore 4
    //   10: aload 4
    //   12: instanceof 246
    //   15: ifeq +84 -> 99
    //   18: aload_2
    //   19: instanceof 248
    //   22: ifeq +77 -> 99
    //   25: aload_1
    //   26: getfield 244	org/apache/cordova/CordovaResourceApi$OpenForReadResult:inputStream	Ljava/io/InputStream;
    //   29: checkcast 246	java/io/FileInputStream
    //   32: invokevirtual 252	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   35: astore 7
    //   37: aload_2
    //   38: checkcast 248	java/io/FileOutputStream
    //   41: invokevirtual 253	java/io/FileOutputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   44: astore 8
    //   46: lconst_0
    //   47: lstore 9
    //   49: aload_1
    //   50: getfield 257	org/apache/cordova/CordovaResourceApi$OpenForReadResult:length	J
    //   53: lstore 11
    //   55: aload_1
    //   56: getfield 261	org/apache/cordova/CordovaResourceApi$OpenForReadResult:assetFd	Landroid/content/res/AssetFileDescriptor;
    //   59: ifnull +12 -> 71
    //   62: aload_1
    //   63: getfield 261	org/apache/cordova/CordovaResourceApi$OpenForReadResult:assetFd	Landroid/content/res/AssetFileDescriptor;
    //   66: invokevirtual 267	android/content/res/AssetFileDescriptor:getStartOffset	()J
    //   69: lstore 9
    //   71: aload 8
    //   73: aload 7
    //   75: lload 9
    //   77: lload 11
    //   79: invokevirtual 273	java/nio/channels/FileChannel:transferFrom	(Ljava/nio/channels/ReadableByteChannel;JJ)J
    //   82: pop2
    //   83: aload_1
    //   84: getfield 244	org/apache/cordova/CordovaResourceApi$OpenForReadResult:inputStream	Ljava/io/InputStream;
    //   87: invokevirtual 278	java/io/InputStream:close	()V
    //   90: aload_2
    //   91: ifnull +7 -> 98
    //   94: aload_2
    //   95: invokevirtual 281	java/io/OutputStream:close	()V
    //   98: return
    //   99: sipush 8192
    //   102: newarray byte
    //   104: astore 5
    //   106: aload 4
    //   108: aload 5
    //   110: iconst_0
    //   111: sipush 8192
    //   114: invokevirtual 285	java/io/InputStream:read	([BII)I
    //   117: istore 6
    //   119: iload 6
    //   121: ifle -38 -> 83
    //   124: aload_2
    //   125: aload 5
    //   127: iconst_0
    //   128: iload 6
    //   130: invokevirtual 289	java/io/OutputStream:write	([BII)V
    //   133: goto -27 -> 106
    //   136: astore_3
    //   137: aload_1
    //   138: getfield 244	org/apache/cordova/CordovaResourceApi$OpenForReadResult:inputStream	Ljava/io/InputStream;
    //   141: invokevirtual 278	java/io/InputStream:close	()V
    //   144: aload_2
    //   145: ifnull +7 -> 152
    //   148: aload_2
    //   149: invokevirtual 281	java/io/OutputStream:close	()V
    //   152: aload_3
    //   153: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	154	0	this	CordovaResourceApi
    //   0	154	1	paramOpenForReadResult	OpenForReadResult
    //   0	154	2	paramOutputStream	OutputStream
    //   136	17	3	localObject	Object
    //   8	99	4	localInputStream	InputStream
    //   104	22	5	arrayOfByte	byte[]
    //   117	12	6	i	int
    //   35	39	7	localFileChannel1	FileChannel
    //   44	28	8	localFileChannel2	FileChannel
    //   47	29	9	l1	long
    //   53	25	11	l2	long
    // Exception table:
    //   from	to	target	type
    //   4	46	136	finally
    //   49	71	136	finally
    //   71	83	136	finally
    //   99	106	136	finally
    //   106	119	136	finally
    //   124	133	136	finally
  }
  
  public HttpURLConnection createHttpConnection(Uri paramUri)
    throws IOException
  {
    assertBackgroundThread();
    return httpClient.open(new URL(paramUri.toString()));
  }
  
  public String getMimeType(Uri paramUri)
  {
    switch (getUriType(paramUri))
    {
    }
    for (;;)
    {
      return null;
      return getMimeTypeFromPath(paramUri.getPath());
      return this.contentResolver.getType(paramUri);
      return getDataUriMimeType(paramUri);
      try
      {
        HttpURLConnection localHttpURLConnection = httpClient.open(new URL(paramUri.toString()));
        localHttpURLConnection.setDoInput(false);
        localHttpURLConnection.setRequestMethod("HEAD");
        String str = localHttpURLConnection.getHeaderField("Content-Type");
        return str;
      }
      catch (IOException localIOException) {}
    }
  }
  
  public boolean isThreadCheckingEnabled()
  {
    return this.threadCheckingEnabled;
  }
  
  public File mapUriToFile(Uri paramUri)
  {
    assertBackgroundThread();
    switch (getUriType(paramUri))
    {
    }
    Cursor localCursor;
    do
    {
      return null;
      return new File(paramUri.getPath());
      localCursor = this.contentResolver.query(paramUri, LOCAL_FILE_PROJECTION, null, null, null);
    } while (localCursor == null);
    try
    {
      int i = localCursor.getColumnIndex(LOCAL_FILE_PROJECTION[0]);
      if ((i != -1) && (localCursor.getCount() > 0))
      {
        localCursor.moveToFirst();
        String str = localCursor.getString(i);
        if (str != null)
        {
          File localFile = new File(str);
          return localFile;
        }
      }
      return null;
    }
    finally
    {
      localCursor.close();
    }
  }
  
  public OpenForReadResult openForRead(Uri paramUri)
    throws IOException
  {
    return openForRead(paramUri, false);
  }
  
  public OpenForReadResult openForRead(Uri paramUri, boolean paramBoolean)
    throws IOException
  {
    if (!paramBoolean) {
      assertBackgroundThread();
    }
    switch (getUriType(paramUri))
    {
    default: 
    case 0: 
    case 1: 
    case 2: 
    case 3: 
    case 4: 
      OpenForReadResult localOpenForReadResult;
      do
      {
        throw new FileNotFoundException("URI not supported by CordovaResourceApi: " + paramUri);
        FileInputStream localFileInputStream = new FileInputStream(paramUri.getPath());
        return new OpenForReadResult(paramUri, localFileInputStream, getMimeTypeFromPath(paramUri.getPath()), localFileInputStream.getChannel().size(), null);
        String str3 = paramUri.getPath().substring(15);
        AssetFileDescriptor localAssetFileDescriptor2 = null;
        long l1 = -1L;
        try
        {
          localAssetFileDescriptor2 = this.assetManager.openFd(str3);
          localObject = localAssetFileDescriptor2.createInputStream();
          long l2 = localAssetFileDescriptor2.getLength();
          l1 = l2;
        }
        catch (FileNotFoundException localFileNotFoundException)
        {
          for (;;)
          {
            Object localObject = this.assetManager.open(str3);
          }
        }
        return new OpenForReadResult(paramUri, (InputStream)localObject, getMimeTypeFromPath(str3), l1, localAssetFileDescriptor2);
        String str2 = this.contentResolver.getType(paramUri);
        AssetFileDescriptor localAssetFileDescriptor1 = this.contentResolver.openAssetFileDescriptor(paramUri, "r");
        return new OpenForReadResult(paramUri, localAssetFileDescriptor1.createInputStream(), str2, localAssetFileDescriptor1.getLength(), localAssetFileDescriptor1);
        localOpenForReadResult = readDataUri(paramUri);
      } while (localOpenForReadResult == null);
      return localOpenForReadResult;
    }
    HttpURLConnection localHttpURLConnection = httpClient.open(new URL(paramUri.toString()));
    localHttpURLConnection.setDoInput(true);
    String str1 = localHttpURLConnection.getHeaderField("Content-Type");
    int i = localHttpURLConnection.getContentLength();
    return new OpenForReadResult(paramUri, localHttpURLConnection.getInputStream(), str1, i, null);
  }
  
  public OutputStream openOutputStream(Uri paramUri)
    throws IOException
  {
    return openOutputStream(paramUri, false);
  }
  
  public OutputStream openOutputStream(Uri paramUri, boolean paramBoolean)
    throws IOException
  {
    assertBackgroundThread();
    switch (getUriType(paramUri))
    {
    case 1: 
    default: 
      throw new FileNotFoundException("URI not supported by CordovaResourceApi: " + paramUri);
    case 0: 
      File localFile1 = new File(paramUri.getPath());
      File localFile2 = localFile1.getParentFile();
      if (localFile2 != null) {
        localFile2.mkdirs();
      }
      return new FileOutputStream(localFile1, paramBoolean);
    }
    ContentResolver localContentResolver = this.contentResolver;
    if (paramBoolean) {}
    for (String str = "wa";; str = "w") {
      return localContentResolver.openAssetFileDescriptor(paramUri, str).createOutputStream();
    }
  }
  
  public String remapPath(String paramString)
  {
    return remapUri(Uri.fromFile(new File(paramString))).getPath();
  }
  
  public Uri remapUri(Uri paramUri)
  {
    assertNonRelative(paramUri);
    Uri localUri = this.pluginManager.remapUri(paramUri);
    if (localUri != null) {
      return localUri;
    }
    return paramUri;
  }
  
  public void setThreadCheckingEnabled(boolean paramBoolean)
  {
    this.threadCheckingEnabled = paramBoolean;
  }
  
  public static final class OpenForReadResult
  {
    public final AssetFileDescriptor assetFd;
    public final InputStream inputStream;
    public final long length;
    public final String mimeType;
    public final Uri uri;
    
    OpenForReadResult(Uri paramUri, InputStream paramInputStream, String paramString, long paramLong, AssetFileDescriptor paramAssetFileDescriptor)
    {
      this.uri = paramUri;
      this.inputStream = paramInputStream;
      this.mimeType = paramString;
      this.length = paramLong;
      this.assetFd = paramAssetFileDescriptor;
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.CordovaResourceApi
 * JD-Core Version:    0.7.0.1
 */