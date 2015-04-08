package com.squareup.okhttp;

import com.squareup.okhttp.internal.Platform;
import com.squareup.okhttp.internal.Util;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConnectionPool
{
  private static final long DEFAULT_KEEP_ALIVE_DURATION_MS = 300000L;
  private static final int MAX_CONNECTIONS_TO_CLEANUP = 2;
  private static final ConnectionPool systemDefault;
  private final LinkedList<Connection> connections = new LinkedList();
  private final Callable<Void> connectionsCleanupCallable = new Callable()
  {
    public Void call()
      throws Exception
    {
      ArrayList localArrayList = new ArrayList(2);
      int i = 0;
      synchronized (ConnectionPool.this)
      {
        ListIterator localListIterator1 = ConnectionPool.this.connections.listIterator(ConnectionPool.this.connections.size());
        for (;;)
        {
          Connection localConnection2;
          if (localListIterator1.hasPrevious())
          {
            localConnection2 = (Connection)localListIterator1.previous();
            if ((!localConnection2.isAlive()) || (localConnection2.isExpired(ConnectionPool.this.keepAliveDurationNs)))
            {
              localListIterator1.remove();
              localArrayList.add(localConnection2);
              if (localArrayList.size() != 2) {
                continue;
              }
            }
          }
          else
          {
            ListIterator localListIterator2 = ConnectionPool.this.connections.listIterator(ConnectionPool.this.connections.size());
            while ((localListIterator2.hasPrevious()) && (i > ConnectionPool.this.maxIdleConnections))
            {
              Connection localConnection1 = (Connection)localListIterator2.previous();
              if (localConnection1.isIdle())
              {
                localArrayList.add(localConnection1);
                localListIterator2.remove();
                i--;
              }
            }
          }
          if (localConnection2.isIdle()) {
            i++;
          }
        }
        Iterator localIterator = localArrayList.iterator();
        if (localIterator.hasNext()) {
          Util.closeQuietly((Connection)localIterator.next());
        }
      }
      return null;
    }
  };
  private final ExecutorService executorService = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
  private final long keepAliveDurationNs;
  private final int maxIdleConnections;
  
  static
  {
    String str1 = System.getProperty("http.keepAlive");
    String str2 = System.getProperty("http.keepAliveDuration");
    String str3 = System.getProperty("http.maxConnections");
    if (str2 != null) {}
    for (long l = Long.parseLong(str2); (str1 != null) && (!Boolean.parseBoolean(str1)); l = 300000L)
    {
      systemDefault = new ConnectionPool(0, l);
      return;
    }
    if (str3 != null)
    {
      systemDefault = new ConnectionPool(Integer.parseInt(str3), l);
      return;
    }
    systemDefault = new ConnectionPool(5, l);
  }
  
  public ConnectionPool(int paramInt, long paramLong)
  {
    this.maxIdleConnections = paramInt;
    this.keepAliveDurationNs = (1000L * (paramLong * 1000L));
  }
  
  public static ConnectionPool getDefault()
  {
    return systemDefault;
  }
  
  private void waitForCleanupCallableToRun()
  {
    try
    {
      this.executorService.submit(new Runnable()
      {
        public void run() {}
      }).get();
      return;
    }
    catch (Exception localException)
    {
      throw new AssertionError();
    }
  }
  
  public void evictAll()
  {
    try
    {
      ArrayList localArrayList = new ArrayList(this.connections);
      this.connections.clear();
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext()) {
        Util.closeQuietly((Connection)localIterator.next());
      }
      return;
    }
    finally {}
  }
  
  /* Error */
  public Connection get(Address paramAddress)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 68	com/squareup/okhttp/ConnectionPool:connections	Ljava/util/LinkedList;
    //   6: aload_0
    //   7: getfield 68	com/squareup/okhttp/ConnectionPool:connections	Ljava/util/LinkedList;
    //   10: invokevirtual 167	java/util/LinkedList:size	()I
    //   13: invokevirtual 171	java/util/LinkedList:listIterator	(I)Ljava/util/ListIterator;
    //   16: astore_3
    //   17: aload_3
    //   18: invokeinterface 176 1 0
    //   23: istore 4
    //   25: aconst_null
    //   26: astore 5
    //   28: iload 4
    //   30: ifeq +87 -> 117
    //   33: aload_3
    //   34: invokeinterface 179 1 0
    //   39: checkcast 154	com/squareup/okhttp/Connection
    //   42: astore 6
    //   44: aload 6
    //   46: invokevirtual 183	com/squareup/okhttp/Connection:getRoute	()Lcom/squareup/okhttp/Route;
    //   49: invokevirtual 189	com/squareup/okhttp/Route:getAddress	()Lcom/squareup/okhttp/Address;
    //   52: aload_1
    //   53: invokevirtual 195	com/squareup/okhttp/Address:equals	(Ljava/lang/Object;)Z
    //   56: ifeq -39 -> 17
    //   59: aload 6
    //   61: invokevirtual 198	com/squareup/okhttp/Connection:isAlive	()Z
    //   64: ifeq -47 -> 17
    //   67: invokestatic 202	java/lang/System:nanoTime	()J
    //   70: aload 6
    //   72: invokevirtual 205	com/squareup/okhttp/Connection:getIdleStartTimeNs	()J
    //   75: lsub
    //   76: aload_0
    //   77: getfield 99	com/squareup/okhttp/ConnectionPool:keepAliveDurationNs	J
    //   80: lcmp
    //   81: ifge -64 -> 17
    //   84: aload_3
    //   85: invokeinterface 208 1 0
    //   90: aload 6
    //   92: invokevirtual 211	com/squareup/okhttp/Connection:isSpdy	()Z
    //   95: istore 7
    //   97: iload 7
    //   99: ifne +14 -> 113
    //   102: invokestatic 216	com/squareup/okhttp/internal/Platform:get	()Lcom/squareup/okhttp/internal/Platform;
    //   105: aload 6
    //   107: invokevirtual 220	com/squareup/okhttp/Connection:getSocket	()Ljava/net/Socket;
    //   110: invokevirtual 224	com/squareup/okhttp/internal/Platform:tagSocket	(Ljava/net/Socket;)V
    //   113: aload 6
    //   115: astore 5
    //   117: aload 5
    //   119: ifnull +20 -> 139
    //   122: aload 5
    //   124: invokevirtual 211	com/squareup/okhttp/Connection:isSpdy	()Z
    //   127: ifeq +12 -> 139
    //   130: aload_0
    //   131: getfield 68	com/squareup/okhttp/ConnectionPool:connections	Ljava/util/LinkedList;
    //   134: aload 5
    //   136: invokevirtual 228	java/util/LinkedList:addFirst	(Ljava/lang/Object;)V
    //   139: aload_0
    //   140: getfield 86	com/squareup/okhttp/ConnectionPool:executorService	Ljava/util/concurrent/ExecutorService;
    //   143: aload_0
    //   144: getfield 93	com/squareup/okhttp/ConnectionPool:connectionsCleanupCallable	Ljava/util/concurrent/Callable;
    //   147: invokeinterface 231 2 0
    //   152: pop
    //   153: aload_0
    //   154: monitorexit
    //   155: aload 5
    //   157: areturn
    //   158: astore 9
    //   160: aload 6
    //   162: invokestatic 160	com/squareup/okhttp/internal/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   165: invokestatic 216	com/squareup/okhttp/internal/Platform:get	()Lcom/squareup/okhttp/internal/Platform;
    //   168: new 233	java/lang/StringBuilder
    //   171: dup
    //   172: invokespecial 234	java/lang/StringBuilder:<init>	()V
    //   175: ldc 236
    //   177: invokevirtual 240	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   180: aload 9
    //   182: invokevirtual 243	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   185: invokevirtual 247	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   188: invokevirtual 251	com/squareup/okhttp/internal/Platform:logW	(Ljava/lang/String;)V
    //   191: goto -174 -> 17
    //   194: astore_2
    //   195: aload_0
    //   196: monitorexit
    //   197: aload_2
    //   198: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	199	0	this	ConnectionPool
    //   0	199	1	paramAddress	Address
    //   194	4	2	localObject1	Object
    //   16	69	3	localListIterator	ListIterator
    //   23	6	4	bool1	boolean
    //   26	130	5	localObject2	Object
    //   42	119	6	localConnection	Connection
    //   95	3	7	bool2	boolean
    //   158	23	9	localSocketException	SocketException
    // Exception table:
    //   from	to	target	type
    //   102	113	158	java/net/SocketException
    //   2	17	194	finally
    //   17	25	194	finally
    //   33	97	194	finally
    //   102	113	194	finally
    //   122	139	194	finally
    //   139	153	194	finally
    //   160	191	194	finally
  }
  
  public int getConnectionCount()
  {
    try
    {
      int i = this.connections.size();
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  List<Connection> getConnections()
  {
    waitForCleanupCallableToRun();
    try
    {
      ArrayList localArrayList = new ArrayList(this.connections);
      return localArrayList;
    }
    finally {}
  }
  
  public int getHttpConnectionCount()
  {
    int i = 0;
    try
    {
      Iterator localIterator = this.connections.iterator();
      while (localIterator.hasNext())
      {
        boolean bool = ((Connection)localIterator.next()).isSpdy();
        if (!bool) {
          i++;
        }
      }
      return i;
    }
    finally {}
  }
  
  public int getSpdyConnectionCount()
  {
    int i = 0;
    try
    {
      Iterator localIterator = this.connections.iterator();
      while (localIterator.hasNext())
      {
        boolean bool = ((Connection)localIterator.next()).isSpdy();
        if (bool) {
          i++;
        }
      }
      return i;
    }
    finally {}
  }
  
  public void maybeShare(Connection paramConnection)
  {
    this.executorService.submit(this.connectionsCleanupCallable);
    if (!paramConnection.isSpdy()) {}
    while (!paramConnection.isAlive()) {
      return;
    }
    try
    {
      this.connections.addFirst(paramConnection);
      return;
    }
    finally {}
  }
  
  public void recycle(Connection paramConnection)
  {
    this.executorService.submit(this.connectionsCleanupCallable);
    if (paramConnection.isSpdy()) {
      return;
    }
    if (!paramConnection.isAlive())
    {
      Util.closeQuietly(paramConnection);
      return;
    }
    try
    {
      Platform.get().untagSocket(paramConnection.getSocket());
      try
      {
        this.connections.addFirst(paramConnection);
        paramConnection.resetIdleStartTime();
        return;
      }
      finally {}
      return;
    }
    catch (SocketException localSocketException)
    {
      Platform.get().logW("Unable to untagSocket(): " + localSocketException);
      Util.closeQuietly(paramConnection);
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.ConnectionPool
 * JD-Core Version:    0.7.0.1
 */