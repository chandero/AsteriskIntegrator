// Source File Name:   AbstractEventUpdater.java

package command.updaters;

import exceptions.UpdateDataException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import utils.PropertiesReader;
import utils.SQLiteDatabase;

public abstract class AbstractEventUpdater
{
  private static final Logger logger = Logger.getLogger(AbstractEventUpdater.class);
  protected SQLiteDatabase database = SQLiteDatabase.getInstance();
  


  public AbstractEventUpdater() {}
  

  public List<String> getCallIdBySource(String sourceId)
  {
    return getCallIdByField("srcuid", sourceId);
  }
  
  public String getCallIdByEventUid(String uniqueId)
  {
    List<String> params = new ArrayList();
    params.add(uniqueId);
    params.add(uniqueId);
    
    String callerId = null;
    try { ResultSet result = database.selectSql("SELECT uid FROM " + SQLiteDatabase.callsTable + " WHERE srcuid=? OR destuid=? ORDER BY uid DESC limit 1;", params);Throwable localThrowable3 = null;
      try {
        if (result.next()) {
          callerId = result.getString(1);
        }
        closeStatement(result);
      }
      catch (Throwable localThrowable1)
      {
        localThrowable3 = localThrowable1;throw localThrowable1;

      }
      finally
      {

        if (result != null) if (localThrowable3 != null) try { result.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else result.close();
      } } catch (SQLException ex) { logger.fatal("Cannot get call UID", ex);
    }
    
    return callerId;
  }
  
  public List<BasicNameValuePair> getRequestParams()
  {
    return getBaseRequestParams();
  }
  
  public abstract void update() throws UpdateDataException;
  
  protected List<BasicNameValuePair> getRequestParamsWithCustomVariables(String callId) {
    List<BasicNameValuePair> eventParams = getBaseRequestParams();
    List<String> customVariablesNames = PropertiesReader.getAsteriskLookUpVariablesNames();
    if (!customVariablesNames.isEmpty()) {
      List<String> params = new ArrayList();
      params.add(callId);
      try { ResultSet result = database.selectSql("SELECT " + join(customVariablesNames, ",") + " FROM " + SQLiteDatabase.callsTable + " WHERE uid = ?;", params);Throwable localThrowable3 = null;
        try {
          if (result.next()) {
            for (String variableName : customVariablesNames) {
              eventParams.add(new BasicNameValuePair(variableName, result.getString(variableName)));
            }
          }
          
          closeStatement(result);
        }
        catch (Throwable localThrowable5)
        {
          localThrowable3 = localThrowable5;throw localThrowable5;
        }
        finally
        {
          if (result != null) if (localThrowable3 != null) try { result.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else result.close();
        } } catch (SQLException ex) { logger.fatal("Cannot get call custom variables", ex);
      }
    }
    
    return eventParams;
  }
  
  protected String getCallerNumberFromChannel(String channel) {
    if (channel != null) {
      String[] channelParts = channel.split("/");
      if (channelParts.length >= 2) {
        String numberPart = channelParts[1];
        String[] callerNumberParts = numberPart.split("-");
        
        return callerNumberParts[0];
      }
    }
    
    return null;
  }
  
  protected void closeStatement(ResultSet result) throws SQLException {
    Statement stmt = result.getStatement();
    if (stmt != null) {
      stmt.close();
    }
  }
  
  private List<BasicNameValuePair> getBaseRequestParams() {
    List<BasicNameValuePair> eventParams = new ArrayList();
    // eventParams.add(new BasicNameValuePair("vtigersignature", PropertiesReader.getProperty("VtigerSecretKey")));
    
    return eventParams;
  }
  
  private List<String> getCallIdByField(String fieldName, String fieldValue)
  {
    List<String> callerIds = new ArrayList();
    List<String> params = new ArrayList();
    params.add(fieldValue);
    try { ResultSet result = database.selectSql("SELECT uid FROM " + SQLiteDatabase.callsTable + " WHERE " + fieldName + " = ?;", params);Throwable localThrowable3 = null;
      try {
        while (result.next()) {
          callerIds.add(result.getString("uid"));
        }
        closeStatement(result);
      }
      catch (Throwable localThrowable1)
      {
        localThrowable3 = localThrowable1;throw localThrowable1;
      }
      finally
      {
        if (result != null) if (localThrowable3 != null) try { result.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else result.close();
      } } catch (SQLException ex) { logger.fatal("Cannot get call UID", ex);
    }
    
    return callerIds;
  }
  
  private String join(List<String> list, String conjunction) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (String item : list) {
      if (first) {
        first = false;
      } else {
        sb.append(conjunction);
      }
      
      sb.append(item);
    }
    
    return sb.toString();
  }
}