package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.internal.Util;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public final class RawHeaders
{
  private static final Comparator<String> FIELD_NAME_COMPARATOR = new Comparator()
  {
    public int compare(String paramAnonymousString1, String paramAnonymousString2)
    {
      if (paramAnonymousString1 == paramAnonymousString2) {
        return 0;
      }
      if (paramAnonymousString1 == null) {
        return -1;
      }
      if (paramAnonymousString2 == null) {
        return 1;
      }
      return String.CASE_INSENSITIVE_ORDER.compare(paramAnonymousString1, paramAnonymousString2);
    }
  };
  private int httpMinorVersion = 1;
  private final List<String> namesAndValues = new ArrayList(20);
  private String requestLine;
  private int responseCode = -1;
  private String responseMessage;
  private String statusLine;
  
  public RawHeaders() {}
  
  public RawHeaders(RawHeaders paramRawHeaders)
  {
    this.namesAndValues.addAll(paramRawHeaders.namesAndValues);
    this.requestLine = paramRawHeaders.requestLine;
    this.statusLine = paramRawHeaders.statusLine;
    this.httpMinorVersion = paramRawHeaders.httpMinorVersion;
    this.responseCode = paramRawHeaders.responseCode;
    this.responseMessage = paramRawHeaders.responseMessage;
  }
  
  private void addLenient(String paramString1, String paramString2)
  {
    this.namesAndValues.add(paramString1);
    this.namesAndValues.add(paramString2.trim());
  }
  
  public static RawHeaders fromBytes(InputStream paramInputStream)
    throws IOException
  {
    RawHeaders localRawHeaders;
    do
    {
      localRawHeaders = new RawHeaders();
      localRawHeaders.setStatusLine(Util.readAsciiLine(paramInputStream));
      readHeaders(paramInputStream, localRawHeaders);
    } while (localRawHeaders.getResponseCode() == 100);
    return localRawHeaders;
  }
  
  public static RawHeaders fromMultimap(Map<String, List<String>> paramMap, boolean paramBoolean)
    throws IOException
  {
    if (!paramBoolean) {
      throw new UnsupportedOperationException();
    }
    RawHeaders localRawHeaders = new RawHeaders();
    Iterator localIterator1 = paramMap.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      String str = (String)localEntry.getKey();
      List localList = (List)localEntry.getValue();
      if (str != null)
      {
        Iterator localIterator2 = localList.iterator();
        while (localIterator2.hasNext()) {
          localRawHeaders.addLenient(str, (String)localIterator2.next());
        }
      }
      else if (!localList.isEmpty())
      {
        localRawHeaders.setStatusLine((String)localList.get(-1 + localList.size()));
      }
    }
    return localRawHeaders;
  }
  
  public static RawHeaders fromNameValueBlock(List<String> paramList)
  {
    if (paramList.size() % 2 != 0) {
      throw new IllegalArgumentException("Unexpected name value block: " + paramList);
    }
    RawHeaders localRawHeaders = new RawHeaders();
    for (int i = 0; i < paramList.size(); i += 2)
    {
      String str1 = (String)paramList.get(i);
      String str2 = (String)paramList.get(i + 1);
      int k;
      for (int j = 0; j < str2.length(); j = k + 1)
      {
        k = str2.indexOf(0, j);
        if (k == -1) {
          k = str2.length();
        }
        localRawHeaders.namesAndValues.add(str1);
        localRawHeaders.namesAndValues.add(str2.substring(j, k));
      }
    }
    return localRawHeaders;
  }
  
  public static void readHeaders(InputStream paramInputStream, RawHeaders paramRawHeaders)
    throws IOException
  {
    for (;;)
    {
      String str = Util.readAsciiLine(paramInputStream);
      if (str.length() == 0) {
        break;
      }
      paramRawHeaders.addLine(str);
    }
  }
  
  public void add(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      throw new IllegalArgumentException("fieldname == null");
    }
    if (paramString2 == null) {
      throw new IllegalArgumentException("value == null");
    }
    if ((paramString1.length() == 0) || (paramString1.indexOf(0) != -1) || (paramString2.indexOf(0) != -1)) {
      throw new IllegalArgumentException("Unexpected header: " + paramString1 + ": " + paramString2);
    }
    addLenient(paramString1, paramString2);
  }
  
  public void addAll(String paramString, List<String> paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext()) {
      add(paramString, (String)localIterator.next());
    }
  }
  
  public void addLine(String paramString)
  {
    int i = paramString.indexOf(":");
    if (i == -1)
    {
      addLenient("", paramString);
      return;
    }
    addLenient(paramString.substring(0, i), paramString.substring(i + 1));
  }
  
  public void addSpdyRequestHeaders(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    add(":method", paramString1);
    add(":scheme", paramString5);
    add(":path", paramString2);
    add(":version", paramString3);
    add(":host", paramString4);
  }
  
  public void computeResponseStatusLineFromSpdyHeaders()
    throws IOException
  {
    String str1 = null;
    String str2 = null;
    int i = 0;
    if (i < this.namesAndValues.size())
    {
      String str3 = (String)this.namesAndValues.get(i);
      if (":status".equals(str3)) {
        str1 = (String)this.namesAndValues.get(i + 1);
      }
      for (;;)
      {
        i += 2;
        break;
        if (":version".equals(str3)) {
          str2 = (String)this.namesAndValues.get(i + 1);
        }
      }
    }
    if ((str1 == null) || (str2 == null)) {
      throw new ProtocolException("Expected ':status' and ':version' headers not present");
    }
    setStatusLine(str2 + " " + str1);
  }
  
  public String get(String paramString)
  {
    for (int i = -2 + this.namesAndValues.size(); i >= 0; i -= 2) {
      if (paramString.equalsIgnoreCase((String)this.namesAndValues.get(i))) {
        return (String)this.namesAndValues.get(i + 1);
      }
    }
    return null;
  }
  
  public RawHeaders getAll(Set<String> paramSet)
  {
    RawHeaders localRawHeaders = new RawHeaders();
    for (int i = 0; i < this.namesAndValues.size(); i += 2)
    {
      String str = (String)this.namesAndValues.get(i);
      if (paramSet.contains(str)) {
        localRawHeaders.add(str, (String)this.namesAndValues.get(i + 1));
      }
    }
    return localRawHeaders;
  }
  
  public String getFieldName(int paramInt)
  {
    int i = paramInt * 2;
    if ((i < 0) || (i >= this.namesAndValues.size())) {
      return null;
    }
    return (String)this.namesAndValues.get(i);
  }
  
  public int getHttpMinorVersion()
  {
    if (this.httpMinorVersion != -1) {
      return this.httpMinorVersion;
    }
    return 1;
  }
  
  public int getResponseCode()
  {
    return this.responseCode;
  }
  
  public String getResponseMessage()
  {
    return this.responseMessage;
  }
  
  public String getStatusLine()
  {
    return this.statusLine;
  }
  
  public String getValue(int paramInt)
  {
    int i = 1 + paramInt * 2;
    if ((i < 0) || (i >= this.namesAndValues.size())) {
      return null;
    }
    return (String)this.namesAndValues.get(i);
  }
  
  public int length()
  {
    return this.namesAndValues.size() / 2;
  }
  
  public void removeAll(String paramString)
  {
    for (int i = 0; i < this.namesAndValues.size(); i += 2) {
      if (paramString.equalsIgnoreCase((String)this.namesAndValues.get(i)))
      {
        this.namesAndValues.remove(i);
        this.namesAndValues.remove(i);
      }
    }
  }
  
  public void set(String paramString1, String paramString2)
  {
    removeAll(paramString1);
    add(paramString1, paramString2);
  }
  
  public void setRequestLine(String paramString)
  {
    this.requestLine = paramString.trim();
  }
  
  public void setStatusLine(String paramString)
    throws IOException
  {
    if (this.responseMessage != null) {
      throw new IllegalStateException("statusLine is already set");
    }
    if (paramString.length() > 13) {}
    for (int i = 1; (!paramString.startsWith("HTTP/1.")) || (paramString.length() < 12) || (paramString.charAt(8) != ' ') || ((i != 0) && (paramString.charAt(12) != ' ')); i = 0) {
      throw new ProtocolException("Unexpected status line: " + paramString);
    }
    int j = '￐' + paramString.charAt(7);
    if ((j < 0) || (j > 9)) {
      throw new ProtocolException("Unexpected status line: " + paramString);
    }
    for (;;)
    {
      try
      {
        int k = Integer.parseInt(paramString.substring(9, 12));
        if (i != 0)
        {
          str = paramString.substring(13);
          this.responseMessage = str;
          this.responseCode = k;
          this.statusLine = paramString;
          this.httpMinorVersion = j;
          return;
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new ProtocolException("Unexpected status line: " + paramString);
      }
      String str = "";
    }
  }
  
  public byte[] toBytes()
    throws UnsupportedEncodingException
  {
    StringBuilder localStringBuilder = new StringBuilder(256);
    localStringBuilder.append(this.requestLine).append("\r\n");
    for (int i = 0; i < this.namesAndValues.size(); i += 2) {
      localStringBuilder.append((String)this.namesAndValues.get(i)).append(": ").append((String)this.namesAndValues.get(i + 1)).append("\r\n");
    }
    localStringBuilder.append("\r\n");
    return localStringBuilder.toString().getBytes("ISO-8859-1");
  }
  
  public Map<String, List<String>> toMultimap(boolean paramBoolean)
  {
    TreeMap localTreeMap = new TreeMap(FIELD_NAME_COMPARATOR);
    for (int i = 0; i < this.namesAndValues.size(); i += 2)
    {
      String str1 = (String)this.namesAndValues.get(i);
      String str2 = (String)this.namesAndValues.get(i + 1);
      ArrayList localArrayList = new ArrayList();
      List localList = (List)localTreeMap.get(str1);
      if (localList != null) {
        localArrayList.addAll(localList);
      }
      localArrayList.add(str2);
      localTreeMap.put(str1, Collections.unmodifiableList(localArrayList));
    }
    if ((paramBoolean) && (this.statusLine != null)) {
      localTreeMap.put(null, Collections.unmodifiableList(Collections.singletonList(this.statusLine)));
    }
    for (;;)
    {
      return Collections.unmodifiableMap(localTreeMap);
      if (this.requestLine != null) {
        localTreeMap.put(null, Collections.unmodifiableList(Collections.singletonList(this.requestLine)));
      }
    }
  }
  
  public List<String> toNameValueBlock()
  {
    HashSet localHashSet = new HashSet();
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    if (i < this.namesAndValues.size())
    {
      String str1 = ((String)this.namesAndValues.get(i)).toLowerCase(Locale.US);
      String str2 = (String)this.namesAndValues.get(i + 1);
      if ((str1.equals("connection")) || (str1.equals("host")) || (str1.equals("keep-alive")) || (str1.equals("proxy-connection")) || (str1.equals("transfer-encoding"))) {}
      label247:
      for (;;)
      {
        i += 2;
        break;
        if (localHashSet.add(str1))
        {
          localArrayList.add(str1);
          localArrayList.add(str2);
        }
        else
        {
          for (int j = 0;; j += 2)
          {
            if (j >= localArrayList.size()) {
              break label247;
            }
            if (str1.equals(localArrayList.get(j)))
            {
              localArrayList.set(j + 1, (String)localArrayList.get(j + 1) + "" + str2);
              break;
            }
          }
        }
      }
    }
    return localArrayList;
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     com.squareup.okhttp.internal.http.RawHeaders
 * JD-Core Version:    0.7.0.1
 */