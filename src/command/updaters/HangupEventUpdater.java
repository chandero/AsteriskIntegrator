// Source File Name:   HangupEventUpdater.java

package command.updaters;

import exceptions.UpdateDataException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.asteriskjava.manager.event.HangupEvent;
import utils.CommonUtils;
import utils.SQLiteDatabase;

public class HangupEventUpdater
  extends AbstractEventUpdater
{
  private static final Logger logger = Logger.getLogger(HangupEventUpdater.class);
  private static final String NORMAL_CAUSE_TEXT = "Normal Clearing";
  private final HangupEvent event;
  
  public HangupEventUpdater(HangupEvent event) {
    this.event = event;
  }
  


  public List<BasicNameValuePair> getRequestParams()
  {
    String callId = getCallIdByEventUid(CommonUtils.getFormattedUniqueId(event.getUniqueId()));
    List<BasicNameValuePair> eventParams = super.getRequestParamsWithCustomVariables(callId);
    eventParams.add(new BasicNameValuePair("causetxt", event.getCauseTxt()));
    eventParams.add(new BasicNameValuePair("callUUID", callId));
    eventParams.add(new BasicNameValuePair("callstatus", "Hangup"));
    
    String callerId = getCallerNumberFromChannel(event.getChannel());
    if (callerId == null) {
      callerId = event.getCallerIdNum();
    }
    
    eventParams.add(new BasicNameValuePair("callerIdNum", callerId));
    addHangUpCause(callId, eventParams);
    
    return eventParams;
  }
  
  public void update() throws UpdateDataException
  {
    List<String> updateParams = new ArrayList();
    updateParams.add(event.getCauseTxt());
    updateParams.add(event.getClass().getSimpleName());
    updateParams.add(CommonUtils.getFormattedUniqueId(event.getUniqueId()));
    updateParams.add(CommonUtils.getFormattedUniqueId(event.getUniqueId()));
    
    try
    {
      database.updateSql("UPDATE " + SQLiteDatabase.callsTable + " SET callcause = ?, event = ? WHERE srcuid = ? OR destuid = ?;", updateParams);
    }
    catch (SQLException ex)
    {
      throw new UpdateDataException("Cannot update on HangUp event", ex);
    }
  }
  
  private void addHangUpCause(String callUUID, List<BasicNameValuePair> eventParams)
  {
    if ("Normal Clearing".equals(event.getCauseTxt())) {
      List<String> selectParams = new ArrayList();
      selectParams.add(callUUID);
      try
      {
        ResultSet result = database.selectSql("SELECT bridged FROM " + SQLiteDatabase.callsTable + " WHERE uid = ? ;", selectParams);Throwable localThrowable3 = null;
        try
        {
          if ((result.next()) && (result.getString(1) == null)) {
            eventParams.add(new BasicNameValuePair("HangupCause", "NO ANSWER"));
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
        } } catch (SQLException ex) { logger.fatal("Cannot select HangUp details", ex);
      }
    }
  }
}