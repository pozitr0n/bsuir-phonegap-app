package com.squareup.okhttp.internal;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DiskLruCache
  implements Closeable
{
  static final long ANY_SEQUENCE_NUMBER = -1L;
  private static final String CLEAN = "CLEAN";
  private static final String DIRTY = "DIRTY";
  static final String JOURNAL_FILE = "journal";
  static final String JOURNAL_FILE_BACKUP = "journal.bkp";
  static final String JOURNAL_FILE_TEMP = "journal.tmp";
  static final Pattern LEGAL_KEY_PATTERN = Pattern.compile("[a-z0-9_-]{1,64}");
  static final String MAGIC = "libcore.io.DiskLruCache";
  private static final OutputStream NULL_OUTPUT_STREAM = new OutputStream()
  {
    public void write(int paramAnonymousInt)
      throws IOException
    {}
  };
  private static final String READ = "READ";
  private static final String REMOVE = "REMOVE";
  static final String VERSION_1 = "1";
  private final int appVersion;
  private final Callable<Void> cleanupCallable = new Callable()
  {
    public Void call()
      throws Exception
    {
      synchronized (DiskLruCache.this)
      {
        if (DiskLruCache.this.journalWriter == null) {
          return null;
        }
        DiskLruCache.this.trimToSize();
        if (DiskLruCache.this.journalRebuildRequired())
        {
          DiskLruCache.this.rebuildJournal();
          DiskLruCache.access$402(DiskLruCache.this, 0);
        }
        return null;
      }
    }
  };
  private final File directory;
  final ThreadPoolExecutor executorService = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
  private final File journalFile;
  private final File journalFileBackup;
  private final File journalFileTmp;
  private Writer journalWriter;
  private final LinkedHashMap<String, Entry> lruEntries = new LinkedHashMap(0, 0.75F, true);
  private long maxSize;
  private long nextSequenceNumber = 0L;
  private int redundantOpCount;
  private long size = 0L;
  private final int valueCount;
  
  private DiskLruCache(File paramFile, int paramInt1, int paramInt2, long paramLong)
  {
    this.directory = paramFile;
    this.appVersion = paramInt1;
    this.journalFile = new File(paramFile, "journal");
    this.journalFileTmp = new File(paramFile, "journal.tmp");
    this.journalFileBackup = new File(paramFile, "journal.bkp");
    this.valueCount = paramInt2;
    this.maxSize = paramLong;
  }
  
  private void checkNotClosed()
  {
    if (this.journalWriter == null) {
      throw new IllegalStateException("cache is closed");
    }
  }
  
  private void completeEdit(Editor paramEditor, boolean paramBoolean)
    throws IOException
  {
    Entry localEntry;
    try
    {
      localEntry = paramEditor.entry;
      if (localEntry.currentEditor != paramEditor) {
        throw new IllegalStateException();
      }
    }
    finally {}
    if ((paramBoolean) && (!localEntry.readable)) {
      for (int j = 0; j < this.valueCount; j++)
      {
        if (paramEditor.written[j] == 0)
        {
          paramEditor.abort();
          throw new IllegalStateException("Newly created entry didn't create value for index " + j);
        }
        if (!localEntry.getDirtyFile(j).exists())
        {
          paramEditor.abort();
          return;
        }
      }
    }
    for (int i = 0;; i++) {
      if (i < this.valueCount)
      {
        File localFile1 = localEntry.getDirtyFile(i);
        if (paramBoolean)
        {
          if (localFile1.exists())
          {
            File localFile2 = localEntry.getCleanFile(i);
            localFile1.renameTo(localFile2);
            long l2 = localEntry.lengths[i];
            long l3 = localFile2.length();
            localEntry.lengths[i] = l3;
            this.size = (l3 + (this.size - l2));
          }
        }
        else {
          deleteIfExists(localFile1);
        }
      }
      else
      {
        this.redundantOpCount = (1 + this.redundantOpCount);
        Entry.access$702(localEntry, null);
        if ((paramBoolean | localEntry.readable))
        {
          Entry.access$602(localEntry, true);
          this.journalWriter.write("CLEAN " + localEntry.key + localEntry.getLengths() + '\n');
          if (paramBoolean)
          {
            long l1 = this.nextSequenceNumber;
            this.nextSequenceNumber = (1L + l1);
            Entry.access$1202(localEntry, l1);
          }
        }
        for (;;)
        {
          this.journalWriter.flush();
          if ((this.size <= this.maxSize) && (!journalRebuildRequired())) {
            break;
          }
          this.executorService.submit(this.cleanupCallable);
          break;
          this.lruEntries.remove(localEntry.key);
          this.journalWriter.write("REMOVE " + localEntry.key + '\n');
        }
      }
    }
  }
  
  private static void deleteIfExists(File paramFile)
    throws IOException
  {
    if ((paramFile.exists()) && (!paramFile.delete())) {
      throw new IOException();
    }
  }
  
  private Editor edit(String paramString, long paramLong)
    throws IOException
  {
    for (;;)
    {
      Editor localEditor1;
      try
      {
        checkNotClosed();
        validateKey(paramString);
        Entry localEntry = (Entry)this.lruEntries.get(paramString);
        if (paramLong != -1L)
        {
          localEditor1 = null;
          if (localEntry != null)
          {
            long l = localEntry.sequenceNumber;
            boolean bool = l < paramLong;
            localEditor1 = null;
            if (!bool) {}
          }
          else
          {
            return localEditor1;
          }
        }
        if (localEntry == null)
        {
          localEntry = new Entry(paramString, null);
          this.lruEntries.put(paramString, localEntry);
          localEditor1 = new Editor(localEntry, null);
          Entry.access$702(localEntry, localEditor1);
          this.journalWriter.write("DIRTY " + paramString + '\n');
          this.journalWriter.flush();
          continue;
        }
        localEditor2 = localEntry.currentEditor;
      }
      finally {}
      Editor localEditor2;
      if (localEditor2 != null) {
        localEditor1 = null;
      }
    }
  }
  
  private static String inputStreamToString(InputStream paramInputStream)
    throws IOException
  {
    return Util.readFully(new InputStreamReader(paramInputStream, Util.UTF_8));
  }
  
  private boolean journalRebuildRequired()
  {
    return (this.redundantOpCount >= 2000) && (this.redundantOpCount >= this.lruEntries.size());
  }
  
  public static DiskLruCache open(File paramFile, int paramInt1, int paramInt2, long paramLong)
    throws IOException
  {
    if (paramLong <= 0L) {
      throw new IllegalArgumentException("maxSize <= 0");
    }
    if (paramInt2 <= 0) {
      throw new IllegalArgumentException("valueCount <= 0");
    }
    File localFile1 = new File(paramFile, "journal.bkp");
    File localFile2;
    if (localFile1.exists())
    {
      localFile2 = new File(paramFile, "journal");
      if (!localFile2.exists()) {
        break label150;
      }
      localFile1.delete();
    }
    DiskLruCache localDiskLruCache1;
    for (;;)
    {
      localDiskLruCache1 = new DiskLruCache(paramFile, paramInt1, paramInt2, paramLong);
      if (!localDiskLruCache1.journalFile.exists()) {
        break label214;
      }
      try
      {
        localDiskLruCache1.readJournal();
        localDiskLruCache1.processJournal();
        localDiskLruCache1.journalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(localDiskLruCache1.journalFile, true), Util.US_ASCII));
        return localDiskLruCache1;
      }
      catch (IOException localIOException)
      {
        label150:
        Platform.get().logW("DiskLruCache " + paramFile + " is corrupt: " + localIOException.getMessage() + ", removing");
        localDiskLruCache1.delete();
      }
      renameTo(localFile1, localFile2, false);
    }
    label214:
    paramFile.mkdirs();
    DiskLruCache localDiskLruCache2 = new DiskLruCache(paramFile, paramInt1, paramInt2, paramLong);
    localDiskLruCache2.rebuildJournal();
    return localDiskLruCache2;
  }
  
  private void processJournal()
    throws IOException
  {
    deleteIfExists(this.journalFileTmp);
    Iterator localIterator = this.lruEntries.values().iterator();
    while (localIterator.hasNext())
    {
      Entry localEntry = (Entry)localIterator.next();
      if (localEntry.currentEditor == null)
      {
        for (int j = 0; j < this.valueCount; j++) {
          this.size += localEntry.lengths[j];
        }
      }
      else
      {
        Entry.access$702(localEntry, null);
        for (int i = 0; i < this.valueCount; i++)
        {
          deleteIfExists(localEntry.getCleanFile(i));
          deleteIfExists(localEntry.getDirtyFile(i));
        }
        localIterator.remove();
      }
    }
  }
  
  /* Error */
  private void readJournal()
    throws IOException
  {
    // Byte code:
    //   0: new 434	com/squareup/okhttp/internal/StrictLineReader
    //   3: dup
    //   4: new 436	java/io/FileInputStream
    //   7: dup
    //   8: aload_0
    //   9: getfield 129	com/squareup/okhttp/internal/DiskLruCache:journalFile	Ljava/io/File;
    //   12: invokespecial 438	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   15: getstatic 375	com/squareup/okhttp/internal/Util:US_ASCII	Ljava/nio/charset/Charset;
    //   18: invokespecial 439	com/squareup/okhttp/internal/StrictLineReader:<init>	(Ljava/io/InputStream;Ljava/nio/charset/Charset;)V
    //   21: astore_1
    //   22: aload_1
    //   23: invokevirtual 442	com/squareup/okhttp/internal/StrictLineReader:readLine	()Ljava/lang/String;
    //   26: astore_3
    //   27: aload_1
    //   28: invokevirtual 442	com/squareup/okhttp/internal/StrictLineReader:readLine	()Ljava/lang/String;
    //   31: astore 4
    //   33: aload_1
    //   34: invokevirtual 442	com/squareup/okhttp/internal/StrictLineReader:readLine	()Ljava/lang/String;
    //   37: astore 5
    //   39: aload_1
    //   40: invokevirtual 442	com/squareup/okhttp/internal/StrictLineReader:readLine	()Ljava/lang/String;
    //   43: astore 6
    //   45: aload_1
    //   46: invokevirtual 442	com/squareup/okhttp/internal/StrictLineReader:readLine	()Ljava/lang/String;
    //   49: astore 7
    //   51: ldc 29
    //   53: aload_3
    //   54: invokevirtual 448	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   57: ifeq +54 -> 111
    //   60: ldc 38
    //   62: aload 4
    //   64: invokevirtual 448	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   67: ifeq +44 -> 111
    //   70: aload_0
    //   71: getfield 122	com/squareup/okhttp/internal/DiskLruCache:appVersion	I
    //   74: invokestatic 453	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   77: aload 5
    //   79: invokevirtual 448	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   82: ifeq +29 -> 111
    //   85: aload_0
    //   86: getfield 135	com/squareup/okhttp/internal/DiskLruCache:valueCount	I
    //   89: invokestatic 453	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   92: aload 6
    //   94: invokevirtual 448	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   97: ifeq +14 -> 111
    //   100: ldc_w 455
    //   103: aload 7
    //   105: invokevirtual 448	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   108: ifne +77 -> 185
    //   111: new 144	java/io/IOException
    //   114: dup
    //   115: new 218	java/lang/StringBuilder
    //   118: dup
    //   119: invokespecial 219	java/lang/StringBuilder:<init>	()V
    //   122: ldc_w 457
    //   125: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   128: aload_3
    //   129: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   132: ldc_w 459
    //   135: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   138: aload 4
    //   140: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   143: ldc_w 459
    //   146: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   149: aload 6
    //   151: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   154: ldc_w 459
    //   157: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   160: aload 7
    //   162: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   165: ldc_w 461
    //   168: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   171: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   174: invokespecial 462	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   177: athrow
    //   178: astore_2
    //   179: aload_1
    //   180: invokestatic 466	com/squareup/okhttp/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   183: aload_2
    //   184: athrow
    //   185: iconst_0
    //   186: istore 8
    //   188: aload_0
    //   189: aload_1
    //   190: invokevirtual 442	com/squareup/okhttp/internal/StrictLineReader:readLine	()Ljava/lang/String;
    //   193: invokespecial 469	com/squareup/okhttp/internal/DiskLruCache:readJournalLine	(Ljava/lang/String;)V
    //   196: iinc 8 1
    //   199: goto -11 -> 188
    //   202: astore 9
    //   204: aload_0
    //   205: iload 8
    //   207: aload_0
    //   208: getfield 91	com/squareup/okhttp/internal/DiskLruCache:lruEntries	Ljava/util/LinkedHashMap;
    //   211: invokevirtual 346	java/util/LinkedHashMap:size	()I
    //   214: isub
    //   215: putfield 184	com/squareup/okhttp/internal/DiskLruCache:redundantOpCount	I
    //   218: aload_1
    //   219: invokestatic 466	com/squareup/okhttp/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   222: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	223	0	this	DiskLruCache
    //   21	198	1	localStrictLineReader	StrictLineReader
    //   178	6	2	localObject	Object
    //   26	103	3	str1	String
    //   31	108	4	str2	String
    //   37	41	5	str3	String
    //   43	107	6	str4	String
    //   49	112	7	str5	String
    //   186	29	8	i	int
    //   202	1	9	localEOFException	java.io.EOFException
    // Exception table:
    //   from	to	target	type
    //   22	111	178	finally
    //   111	178	178	finally
    //   188	196	178	finally
    //   204	218	178	finally
    //   188	196	202	java/io/EOFException
  }
  
  private void readJournalLine(String paramString)
    throws IOException
  {
    int i = paramString.indexOf(' ');
    if (i == -1) {
      throw new IOException("unexpected journal line: " + paramString);
    }
    int j = i + 1;
    int k = paramString.indexOf(' ', j);
    String str;
    if (k == -1)
    {
      str = paramString.substring(j);
      if ((i != "REMOVE".length()) || (!paramString.startsWith("REMOVE"))) {
        break label104;
      }
      this.lruEntries.remove(str);
    }
    label104:
    do
    {
      return;
      str = paramString.substring(j, k);
      Entry localEntry = (Entry)this.lruEntries.get(str);
      if (localEntry == null)
      {
        localEntry = new Entry(str, null);
        this.lruEntries.put(str, localEntry);
      }
      if ((k != -1) && (i == "CLEAN".length()) && (paramString.startsWith("CLEAN")))
      {
        String[] arrayOfString = paramString.substring(k + 1).split(" ");
        Entry.access$602(localEntry, true);
        Entry.access$702(localEntry, null);
        localEntry.setLengths(arrayOfString);
        return;
      }
      if ((k == -1) && (i == "DIRTY".length()) && (paramString.startsWith("DIRTY")))
      {
        Entry.access$702(localEntry, new Editor(localEntry, null));
        return;
      }
    } while ((k == -1) && (i == "READ".length()) && (paramString.startsWith("READ")));
    throw new IOException("unexpected journal line: " + paramString);
  }
  
  private void rebuildJournal()
    throws IOException
  {
    BufferedWriter localBufferedWriter;
    for (;;)
    {
      try
      {
        if (this.journalWriter != null) {
          this.journalWriter.close();
        }
        localBufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.journalFileTmp), Util.US_ASCII));
        Entry localEntry;
        try
        {
          localBufferedWriter.write("libcore.io.DiskLruCache");
          localBufferedWriter.write("\n");
          localBufferedWriter.write("1");
          localBufferedWriter.write("\n");
          localBufferedWriter.write(Integer.toString(this.appVersion));
          localBufferedWriter.write("\n");
          localBufferedWriter.write(Integer.toString(this.valueCount));
          localBufferedWriter.write("\n");
          localBufferedWriter.write("\n");
          Iterator localIterator = this.lruEntries.values().iterator();
          if (!localIterator.hasNext()) {
            break;
          }
          localEntry = (Entry)localIterator.next();
          if (localEntry.currentEditor != null)
          {
            localBufferedWriter.write("DIRTY " + localEntry.key + '\n');
            continue;
            localObject1 = finally;
          }
        }
        finally
        {
          localBufferedWriter.close();
        }
        localBufferedWriter.write("CLEAN " + localEntry.key + localEntry.getLengths() + '\n');
      }
      finally {}
    }
    localBufferedWriter.close();
    if (this.journalFile.exists()) {
      renameTo(this.journalFile, this.journalFileBackup, true);
    }
    renameTo(this.journalFileTmp, this.journalFile, false);
    this.journalFileBackup.delete();
    this.journalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.journalFile, true), Util.US_ASCII));
  }
  
  private static void renameTo(File paramFile1, File paramFile2, boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean) {
      deleteIfExists(paramFile2);
    }
    if (!paramFile1.renameTo(paramFile2)) {
      throw new IOException();
    }
  }
  
  private void trimToSize()
    throws IOException
  {
    while (this.size > this.maxSize) {
      remove((String)((Map.Entry)this.lruEntries.entrySet().iterator().next()).getKey());
    }
  }
  
  private void validateKey(String paramString)
  {
    if (!LEGAL_KEY_PATTERN.matcher(paramString).matches()) {
      throw new IllegalArgumentException("keys must match regex [a-z0-9_-]{1,64}: \"" + paramString + "\"");
    }
  }
  
  public void close()
    throws IOException
  {
    for (;;)
    {
      try
      {
        Writer localWriter = this.journalWriter;
        if (localWriter == null) {
          return;
        }
        Iterator localIterator = new ArrayList(this.lruEntries.values()).iterator();
        if (localIterator.hasNext())
        {
          Entry localEntry = (Entry)localIterator.next();
          if (localEntry.currentEditor == null) {
            continue;
          }
          localEntry.currentEditor.abort();
          continue;
        }
        trimToSize();
      }
      finally {}
      this.journalWriter.close();
      this.journalWriter = null;
    }
  }
  
  public void delete()
    throws IOException
  {
    close();
    Util.deleteContents(this.directory);
  }
  
  public Editor edit(String paramString)
    throws IOException
  {
    return edit(paramString, -1L);
  }
  
  public void flush()
    throws IOException
  {
    try
    {
      checkNotClosed();
      trimToSize();
      this.journalWriter.flush();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  /* Error */
  public Snapshot get(String paramString)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokespecial 306	com/squareup/okhttp/internal/DiskLruCache:checkNotClosed	()V
    //   6: aload_0
    //   7: aload_1
    //   8: invokespecial 309	com/squareup/okhttp/internal/DiskLruCache:validateKey	(Ljava/lang/String;)V
    //   11: aload_0
    //   12: getfield 91	com/squareup/okhttp/internal/DiskLruCache:lruEntries	Ljava/util/LinkedHashMap;
    //   15: aload_1
    //   16: invokevirtual 312	java/util/LinkedHashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   19: checkcast 200	com/squareup/okhttp/internal/DiskLruCache$Entry
    //   22: astore_3
    //   23: aconst_null
    //   24: astore 4
    //   26: aload_3
    //   27: ifnonnull +8 -> 35
    //   30: aload_0
    //   31: monitorexit
    //   32: aload 4
    //   34: areturn
    //   35: aload_3
    //   36: invokestatic 209	com/squareup/okhttp/internal/DiskLruCache$Entry:access$600	(Lcom/squareup/okhttp/internal/DiskLruCache$Entry;)Z
    //   39: istore 5
    //   41: aconst_null
    //   42: astore 4
    //   44: iload 5
    //   46: ifeq -16 -> 30
    //   49: aload_0
    //   50: getfield 135	com/squareup/okhttp/internal/DiskLruCache:valueCount	I
    //   53: anewarray 549	java/io/InputStream
    //   56: astore 6
    //   58: iconst_0
    //   59: istore 7
    //   61: iload 7
    //   63: aload_0
    //   64: getfield 135	com/squareup/okhttp/internal/DiskLruCache:valueCount	I
    //   67: if_icmpge +77 -> 144
    //   70: aload 6
    //   72: iload 7
    //   74: new 436	java/io/FileInputStream
    //   77: dup
    //   78: aload_3
    //   79: iload 7
    //   81: invokevirtual 242	com/squareup/okhttp/internal/DiskLruCache$Entry:getCleanFile	(I)Ljava/io/File;
    //   84: invokespecial 438	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   87: aastore
    //   88: iinc 7 1
    //   91: goto -30 -> 61
    //   94: astore 8
    //   96: iconst_0
    //   97: istore 9
    //   99: aload_0
    //   100: getfield 135	com/squareup/okhttp/internal/DiskLruCache:valueCount	I
    //   103: istore 10
    //   105: aconst_null
    //   106: astore 4
    //   108: iload 9
    //   110: iload 10
    //   112: if_icmpge -82 -> 30
    //   115: aload 6
    //   117: iload 9
    //   119: aaload
    //   120: astore 11
    //   122: aconst_null
    //   123: astore 4
    //   125: aload 11
    //   127: ifnull -97 -> 30
    //   130: aload 6
    //   132: iload 9
    //   134: aaload
    //   135: invokestatic 466	com/squareup/okhttp/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   138: iinc 9 1
    //   141: goto -42 -> 99
    //   144: aload_0
    //   145: iconst_1
    //   146: aload_0
    //   147: getfield 184	com/squareup/okhttp/internal/DiskLruCache:redundantOpCount	I
    //   150: iadd
    //   151: putfield 184	com/squareup/okhttp/internal/DiskLruCache:redundantOpCount	I
    //   154: aload_0
    //   155: getfield 141	com/squareup/okhttp/internal/DiskLruCache:journalWriter	Ljava/io/Writer;
    //   158: new 218	java/lang/StringBuilder
    //   161: dup
    //   162: invokespecial 219	java/lang/StringBuilder:<init>	()V
    //   165: ldc_w 551
    //   168: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   171: aload_1
    //   172: invokevirtual 225	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   175: bipush 10
    //   177: invokevirtual 278	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   180: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   183: invokevirtual 554	java/io/Writer:append	(Ljava/lang/CharSequence;)Ljava/io/Writer;
    //   186: pop
    //   187: aload_0
    //   188: invokespecial 168	com/squareup/okhttp/internal/DiskLruCache:journalRebuildRequired	()Z
    //   191: ifeq +15 -> 206
    //   194: aload_0
    //   195: getfield 111	com/squareup/okhttp/internal/DiskLruCache:executorService	Ljava/util/concurrent/ThreadPoolExecutor;
    //   198: aload_0
    //   199: getfield 118	com/squareup/okhttp/internal/DiskLruCache:cleanupCallable	Ljava/util/concurrent/Callable;
    //   202: invokevirtual 294	java/util/concurrent/ThreadPoolExecutor:submit	(Ljava/util/concurrent/Callable;)Ljava/util/concurrent/Future;
    //   205: pop
    //   206: new 556	com/squareup/okhttp/internal/DiskLruCache$Snapshot
    //   209: dup
    //   210: aload_0
    //   211: aload_1
    //   212: aload_3
    //   213: invokestatic 316	com/squareup/okhttp/internal/DiskLruCache$Entry:access$1200	(Lcom/squareup/okhttp/internal/DiskLruCache$Entry;)J
    //   216: aload 6
    //   218: aload_3
    //   219: invokestatic 250	com/squareup/okhttp/internal/DiskLruCache$Entry:access$1000	(Lcom/squareup/okhttp/internal/DiskLruCache$Entry;)[J
    //   222: aconst_null
    //   223: invokespecial 559	com/squareup/okhttp/internal/DiskLruCache$Snapshot:<init>	(Lcom/squareup/okhttp/internal/DiskLruCache;Ljava/lang/String;J[Ljava/io/InputStream;[JLcom/squareup/okhttp/internal/DiskLruCache$1;)V
    //   226: astore 4
    //   228: goto -198 -> 30
    //   231: astore_2
    //   232: aload_0
    //   233: monitorexit
    //   234: aload_2
    //   235: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	236	0	this	DiskLruCache
    //   0	236	1	paramString	String
    //   231	4	2	localObject	Object
    //   22	197	3	localEntry	Entry
    //   24	203	4	localSnapshot	Snapshot
    //   39	6	5	bool	boolean
    //   56	161	6	arrayOfInputStream	InputStream[]
    //   59	30	7	i	int
    //   94	1	8	localFileNotFoundException	FileNotFoundException
    //   97	42	9	j	int
    //   103	10	10	k	int
    //   120	6	11	localInputStream	InputStream
    // Exception table:
    //   from	to	target	type
    //   61	88	94	java/io/FileNotFoundException
    //   2	23	231	finally
    //   35	41	231	finally
    //   49	58	231	finally
    //   61	88	231	finally
    //   99	105	231	finally
    //   115	122	231	finally
    //   130	138	231	finally
    //   144	206	231	finally
    //   206	228	231	finally
  }
  
  public File getDirectory()
  {
    return this.directory;
  }
  
  public long getMaxSize()
  {
    return this.maxSize;
  }
  
  public boolean isClosed()
  {
    return this.journalWriter == null;
  }
  
  public boolean remove(String paramString)
    throws IOException
  {
    for (;;)
    {
      Entry localEntry;
      int i;
      try
      {
        checkNotClosed();
        validateKey(paramString);
        localEntry = (Entry)this.lruEntries.get(paramString);
        if (localEntry != null)
        {
          Editor localEditor = localEntry.currentEditor;
          if (localEditor == null) {}
        }
        else
        {
          bool = false;
          return bool;
        }
        i = 0;
        if (i >= this.valueCount) {
          break label138;
        }
        File localFile = localEntry.getCleanFile(i);
        if (!localFile.delete()) {
          throw new IOException("failed to delete " + localFile);
        }
      }
      finally {}
      this.size -= localEntry.lengths[i];
      localEntry.lengths[i] = 0L;
      i++;
      continue;
      label138:
      this.redundantOpCount = (1 + this.redundantOpCount);
      this.journalWriter.append("REMOVE " + paramString + '\n');
      this.lruEntries.remove(paramString);
      if (journalRebuildRequired()) {
        this.executorService.submit(this.cleanupCallable);
      }
      boolean bool = true;
    }
  }
  
  public void setMaxSize(long paramLong)
  {
    try
    {
      this.maxSize = paramLong;
      this.executorService.submit(this.cleanupCallable);
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public long size()
  {
    try
    {
      long l = this.size;
      return l;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public final class Editor
  {
    private boolean committed;
    private final DiskLruCache.Entry entry;
    private boolean hasErrors;
    private final boolean[] written;
    
    private Editor(DiskLruCache.Entry paramEntry)
    {
      this.entry = paramEntry;
      if (DiskLruCache.Entry.access$600(paramEntry)) {}
      for (boolean[] arrayOfBoolean = null;; arrayOfBoolean = new boolean[DiskLruCache.this.valueCount])
      {
        this.written = arrayOfBoolean;
        return;
      }
    }
    
    public void abort()
      throws IOException
    {
      DiskLruCache.this.completeEdit(this, false);
    }
    
    public void abortUnlessCommitted()
    {
      if (!this.committed) {}
      try
      {
        abort();
        return;
      }
      catch (IOException localIOException) {}
    }
    
    public void commit()
      throws IOException
    {
      if (this.hasErrors)
      {
        DiskLruCache.this.completeEdit(this, false);
        DiskLruCache.this.remove(DiskLruCache.Entry.access$1100(this.entry));
      }
      for (;;)
      {
        this.committed = true;
        return;
        DiskLruCache.this.completeEdit(this, true);
      }
    }
    
    public String getString(int paramInt)
      throws IOException
    {
      InputStream localInputStream = newInputStream(paramInt);
      if (localInputStream != null) {
        return DiskLruCache.inputStreamToString(localInputStream);
      }
      return null;
    }
    
    public InputStream newInputStream(int paramInt)
      throws IOException
    {
      synchronized (DiskLruCache.this)
      {
        if (DiskLruCache.Entry.access$700(this.entry) != this) {
          throw new IllegalStateException();
        }
      }
      if (!DiskLruCache.Entry.access$600(this.entry)) {
        return null;
      }
      try
      {
        FileInputStream localFileInputStream = new FileInputStream(this.entry.getCleanFile(paramInt));
        return localFileInputStream;
      }
      catch (FileNotFoundException localFileNotFoundException) {}
      return null;
    }
    
    public OutputStream newOutputStream(int paramInt)
      throws IOException
    {
      synchronized (DiskLruCache.this)
      {
        if (DiskLruCache.Entry.access$700(this.entry) != this) {
          throw new IllegalStateException();
        }
      }
      if (!DiskLruCache.Entry.access$600(this.entry)) {
        this.written[paramInt] = true;
      }
      File localFile = this.entry.getDirtyFile(paramInt);
      try
      {
        localFileOutputStream = new FileOutputStream(localFile);
        FaultHidingOutputStream localFaultHidingOutputStream = new FaultHidingOutputStream(localFileOutputStream, null);
        return localFaultHidingOutputStream;
      }
      catch (FileNotFoundException localFileNotFoundException1)
      {
        for (;;)
        {
          FileOutputStream localFileOutputStream;
          DiskLruCache.this.directory.mkdirs();
          try
          {
            localFileOutputStream = new FileOutputStream(localFile);
          }
          catch (FileNotFoundException localFileNotFoundException2)
          {
            OutputStream localOutputStream = DiskLruCache.NULL_OUTPUT_STREAM;
            return localOutputStream;
          }
        }
      }
    }
    
    /* Error */
    public void set(int paramInt, String paramString)
      throws IOException
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore_3
      //   2: new 127	java/io/OutputStreamWriter
      //   5: dup
      //   6: aload_0
      //   7: iload_1
      //   8: invokevirtual 129	com/squareup/okhttp/internal/DiskLruCache$Editor:newOutputStream	(I)Ljava/io/OutputStream;
      //   11: getstatic 135	com/squareup/okhttp/internal/Util:UTF_8	Ljava/nio/charset/Charset;
      //   14: invokespecial 138	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;Ljava/nio/charset/Charset;)V
      //   17: astore 4
      //   19: aload 4
      //   21: aload_2
      //   22: invokevirtual 144	java/io/Writer:write	(Ljava/lang/String;)V
      //   25: aload 4
      //   27: invokestatic 148	com/squareup/okhttp/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
      //   30: return
      //   31: astore 5
      //   33: aload_3
      //   34: invokestatic 148	com/squareup/okhttp/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
      //   37: aload 5
      //   39: athrow
      //   40: astore 5
      //   42: aload 4
      //   44: astore_3
      //   45: goto -12 -> 33
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	48	0	this	Editor
      //   0	48	1	paramInt	int
      //   0	48	2	paramString	String
      //   1	44	3	localObject1	Object
      //   17	26	4	localOutputStreamWriter	OutputStreamWriter
      //   31	7	5	localObject2	Object
      //   40	1	5	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   2	19	31	finally
      //   19	25	40	finally
    }
    
    private class FaultHidingOutputStream
      extends FilterOutputStream
    {
      private FaultHidingOutputStream(OutputStream paramOutputStream)
      {
        super();
      }
      
      public void close()
      {
        try
        {
          this.out.close();
          return;
        }
        catch (IOException localIOException)
        {
          DiskLruCache.Editor.access$2302(DiskLruCache.Editor.this, true);
        }
      }
      
      public void flush()
      {
        try
        {
          this.out.flush();
          return;
        }
        catch (IOException localIOException)
        {
          DiskLruCache.Editor.access$2302(DiskLruCache.Editor.this, true);
        }
      }
      
      public void write(int paramInt)
      {
        try
        {
          this.out.write(paramInt);
          return;
        }
        catch (IOException localIOException)
        {
          DiskLruCache.Editor.access$2302(DiskLruCache.Editor.this, true);
        }
      }
      
      public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      {
        try
        {
          this.out.write(paramArrayOfByte, paramInt1, paramInt2);
          return;
        }
        catch (IOException localIOException)
        {
          DiskLruCache.Editor.access$2302(DiskLruCache.Editor.this, true);
        }
      }
    }
  }
  
  private final class Entry
  {
    private DiskLruCache.Editor currentEditor;
    private final String key;
    private final long[] lengths;
    private boolean readable;
    private long sequenceNumber;
    
    private Entry(String paramString)
    {
      this.key = paramString;
      this.lengths = new long[DiskLruCache.this.valueCount];
    }
    
    private IOException invalidLengths(String[] paramArrayOfString)
      throws IOException
    {
      throw new IOException("unexpected journal line: " + Arrays.toString(paramArrayOfString));
    }
    
    private void setLengths(String[] paramArrayOfString)
      throws IOException
    {
      if (paramArrayOfString.length != DiskLruCache.this.valueCount) {
        throw invalidLengths(paramArrayOfString);
      }
      int i = 0;
      try
      {
        while (i < paramArrayOfString.length)
        {
          this.lengths[i] = Long.parseLong(paramArrayOfString[i]);
          i++;
        }
        return;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw invalidLengths(paramArrayOfString);
      }
    }
    
    public File getCleanFile(int paramInt)
    {
      return new File(DiskLruCache.this.directory, this.key + "." + paramInt);
    }
    
    public File getDirtyFile(int paramInt)
    {
      return new File(DiskLruCache.this.directory, this.key + "." + paramInt + ".tmp");
    }
    
    public String getLengths()
      throws IOException
    {
      StringBuilder localStringBuilder = new StringBuilder();
      for (long l : this.lengths) {
        localStringBuilder.append(' ').append(l);
      }
      return localStringBuilder.toString();
    }
  }
  
  public final class Snapshot
    implements Closeable
  {
    private final InputStream[] ins;
    private final String key;
    private final long[] lengths;
    private final long sequenceNumber;
    
    private Snapshot(String paramString, long paramLong, InputStream[] paramArrayOfInputStream, long[] paramArrayOfLong)
    {
      this.key = paramString;
      this.sequenceNumber = paramLong;
      this.ins = paramArrayOfInputStream;
      this.lengths = paramArrayOfLong;
    }
    
    public void close()
    {
      InputStream[] arrayOfInputStream = this.ins;
      int i = arrayOfInputStream.length;
      for (int j = 0; j < i; j++) {
        Util.closeQuietly(arrayOfInputStream[j]);
      }
    }
    
    public DiskLruCache.Editor edit()
      throws IOException
    {
      return DiskLruCache.this.edit(this.key, this.sequenceNumber);
    }
    
    public InputStream getInputStream(int paramInt)
    {
      return this.ins[paramInt];
    }
    
    public long getLength(int paramInt)
    {
      return this.lengths[paramInt];
    }
    
    public String getString(int paramInt)
      throws IOException
    {
      return DiskLruCache.inputStreamToString(getInputStream(paramInt));
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.DiskLruCache
 * JD-Core Version:    0.7.0.1
 */