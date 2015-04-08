package org.apache.cordova;

import android.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

public class PluginResult
{
  public static final int MESSAGE_TYPE_ARRAYBUFFER = 6;
  public static final int MESSAGE_TYPE_BINARYSTRING = 7;
  public static final int MESSAGE_TYPE_BOOLEAN = 4;
  public static final int MESSAGE_TYPE_JSON = 2;
  public static final int MESSAGE_TYPE_NULL = 5;
  public static final int MESSAGE_TYPE_NUMBER = 3;
  public static final int MESSAGE_TYPE_STRING = 1;
  public static String[] StatusMessages = { "No result", "OK", "Class not found", "Illegal access", "Instantiation error", "Malformed url", "IO error", "Invalid action", "JSON error", "Error" };
  private String encodedMessage;
  private boolean keepCallback = false;
  private final int messageType;
  private final int status;
  private String strMessage;
  
  public PluginResult(Status paramStatus)
  {
    this(paramStatus, StatusMessages[paramStatus.ordinal()]);
  }
  
  public PluginResult(Status paramStatus, float paramFloat)
  {
    this.status = paramStatus.ordinal();
    this.messageType = 3;
    this.encodedMessage = ("" + paramFloat);
  }
  
  public PluginResult(Status paramStatus, int paramInt)
  {
    this.status = paramStatus.ordinal();
    this.messageType = 3;
    this.encodedMessage = ("" + paramInt);
  }
  
  public PluginResult(Status paramStatus, String paramString)
  {
    this.status = paramStatus.ordinal();
    if (paramString == null) {}
    for (int i = 5;; i = 1)
    {
      this.messageType = i;
      this.strMessage = paramString;
      return;
    }
  }
  
  public PluginResult(Status paramStatus, JSONArray paramJSONArray)
  {
    this.status = paramStatus.ordinal();
    this.messageType = 2;
    this.encodedMessage = paramJSONArray.toString();
  }
  
  public PluginResult(Status paramStatus, JSONObject paramJSONObject)
  {
    this.status = paramStatus.ordinal();
    this.messageType = 2;
    this.encodedMessage = paramJSONObject.toString();
  }
  
  public PluginResult(Status paramStatus, boolean paramBoolean)
  {
    this.status = paramStatus.ordinal();
    this.messageType = 4;
    this.encodedMessage = Boolean.toString(paramBoolean);
  }
  
  public PluginResult(Status paramStatus, byte[] paramArrayOfByte)
  {
    this(paramStatus, paramArrayOfByte, false);
  }
  
  public PluginResult(Status paramStatus, byte[] paramArrayOfByte, boolean paramBoolean)
  {
    this.status = paramStatus.ordinal();
    if (paramBoolean) {}
    for (int i = 7;; i = 6)
    {
      this.messageType = i;
      this.encodedMessage = Base64.encodeToString(paramArrayOfByte, 2);
      return;
    }
  }
  
  @Deprecated
  public String getJSONString()
  {
    return "{\"status\":" + this.status + ",\"message\":" + getMessage() + ",\"keepCallback\":" + this.keepCallback + "}";
  }
  
  public boolean getKeepCallback()
  {
    return this.keepCallback;
  }
  
  public String getMessage()
  {
    if (this.encodedMessage == null) {
      this.encodedMessage = JSONObject.quote(this.strMessage);
    }
    return this.encodedMessage;
  }
  
  public int getMessageType()
  {
    return this.messageType;
  }
  
  public int getStatus()
  {
    return this.status;
  }
  
  public String getStrMessage()
  {
    return this.strMessage;
  }
  
  public void setKeepCallback(boolean paramBoolean)
  {
    this.keepCallback = paramBoolean;
  }
  
  @Deprecated
  public String toCallbackString(String paramString)
  {
    if ((this.status == Status.NO_RESULT.ordinal()) && (this.keepCallback)) {
      return null;
    }
    if ((this.status == Status.OK.ordinal()) || (this.status == Status.NO_RESULT.ordinal())) {
      return toSuccessCallbackString(paramString);
    }
    return toErrorCallbackString(paramString);
  }
  
  @Deprecated
  public String toErrorCallbackString(String paramString)
  {
    return "cordova.callbackError('" + paramString + "', " + getJSONString() + ");";
  }
  
  @Deprecated
  public String toSuccessCallbackString(String paramString)
  {
    return "cordova.callbackSuccess('" + paramString + "'," + getJSONString() + ");";
  }
  
  public static enum Status
  {
    static
    {
      CLASS_NOT_FOUND_EXCEPTION = new Status("CLASS_NOT_FOUND_EXCEPTION", 2);
      ILLEGAL_ACCESS_EXCEPTION = new Status("ILLEGAL_ACCESS_EXCEPTION", 3);
      INSTANTIATION_EXCEPTION = new Status("INSTANTIATION_EXCEPTION", 4);
      MALFORMED_URL_EXCEPTION = new Status("MALFORMED_URL_EXCEPTION", 5);
      IO_EXCEPTION = new Status("IO_EXCEPTION", 6);
      INVALID_ACTION = new Status("INVALID_ACTION", 7);
      JSON_EXCEPTION = new Status("JSON_EXCEPTION", 8);
      ERROR = new Status("ERROR", 9);
      Status[] arrayOfStatus = new Status[10];
      arrayOfStatus[0] = NO_RESULT;
      arrayOfStatus[1] = OK;
      arrayOfStatus[2] = CLASS_NOT_FOUND_EXCEPTION;
      arrayOfStatus[3] = ILLEGAL_ACCESS_EXCEPTION;
      arrayOfStatus[4] = INSTANTIATION_EXCEPTION;
      arrayOfStatus[5] = MALFORMED_URL_EXCEPTION;
      arrayOfStatus[6] = IO_EXCEPTION;
      arrayOfStatus[7] = INVALID_ACTION;
      arrayOfStatus[8] = JSON_EXCEPTION;
      arrayOfStatus[9] = ERROR;
      $VALUES = arrayOfStatus;
    }
    
    private Status() {}
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.PluginResult
 * JD-Core Version:    0.7.0.1
 */