package org.apache.cordova;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;

@Deprecated
public class JSONUtils
{
  public static List<String> toStringList(JSONArray paramJSONArray)
    throws JSONException
  {
    Object localObject;
    if (paramJSONArray == null) {
      localObject = null;
    }
    for (;;)
    {
      return localObject;
      localObject = new ArrayList();
      for (int i = 0; i < paramJSONArray.length(); i++) {
        ((List)localObject).add(paramJSONArray.get(i).toString());
      }
    }
  }
}


/* Location:           C:\Users\user\Desktop\Decompiling of the PhoneGap App\Decompiling\iSport-v.0.1_dex2jar.jar
 * Qualified Name:     org.apache.cordova.JSONUtils
 * JD-Core Version:    0.7.0.1
 */