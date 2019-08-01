// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CdrEventUpdater.java

package command.updaters;

import exceptions.UpdateDataException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.asteriskjava.manager.event.CdrEvent;
import utils.CommonUtils;
import utils.PropertiesReader;
import utils.SQLiteDatabase;

public class CdrEventUpdater
  extends AbstractEventUpdater
{
  private static final Logger logger = Logger.getLogger(CdrEventUpdater.class);
  private final CdrEvent event;
  
  public CdrEventUpdater(CdrEvent event)
  {
    this.event = event;
  }
  
  public List<BasicNameValuePair> getRequestParams()
  {
    String callId = getCallIdByEventUid(CommonUtils.getFormattedUniqueId(event.getUniqueId()));
    List<BasicNameValuePair> eventParams = super.getRequestParamsWithCustomVariables(callId);
    
    eventParams.add(new BasicNameValuePair("starttime", event.getStartTime()));
    eventParams.add(new BasicNameValuePair("endtime", event.getEndTime()));
    eventParams.add(new BasicNameValuePair("duration", event.getDuration().toString()));
    eventParams.add(new BasicNameValuePair("billableseconds", event.getBillableSeconds().toString()));
    eventParams.add(new BasicNameValuePair("callerNumber", getCallerNumber()));
    eventParams.add(new BasicNameValuePair("callstatus", "EndCall"));
    eventParams.add(new BasicNameValuePair("callUUID", callId));
    
    return eventParams;
  }
  
  public List<BasicNameValuePair> getRecordingRequestParams() {
    List<BasicNameValuePair> requestParams = null;
    String callId = getCallIdByEventUid(CommonUtils.getFormattedUniqueId(event.getUniqueId()));
    String recordingPath = getRecordingPath(callId);
    if ((recordingPath != null) && (!recordingPath.equals(""))) {
      requestParams = super.getRequestParamsWithCustomVariables(callId);
      requestParams.add(new BasicNameValuePair("recordinglink", PropertiesReader.getSoundFileUrl(callId)));
      requestParams.add(new BasicNameValuePair("callerNumber", getCallerNumber()));
      requestParams.add(new BasicNameValuePair("callstatus", "Record"));
      requestParams.add(new BasicNameValuePair("callUUID", callId));
    }
    
    return requestParams;
  }
  
  private String getRecordingPath(String callId)
  {
    String recordingPath = null;
    ArrayList<String> params = new ArrayList();
    params.add(callId);
    try { ResultSet result = database.selectSql("SELECT recordingpath FROM " + SQLiteDatabase.callsTable + " WHERE uid=?", params);Throwable localThrowable3 = null;
      try { if (result.next()) {
          recordingPath = result.getString("recordingpath");
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
      } } catch (SQLException ex) { logger.fatal("Error on get recordingPath from CDR", ex);
    }
    
    return recordingPath;
  }
  
  public void update() throws UpdateDataException
  {
    String callId = getCallIdByEventUid(CommonUtils.getFormattedUniqueId(event.getUniqueId()));
    List<String> createParams = new ArrayList();
    
    createParams.add(event.getEndTime());
    createParams.add(event.getStartTime());
    createParams.add(event.getDuration().toString());
    createParams.add(event.getClass().getSimpleName());
    createParams.add(event.getSrc());
    createParams.add(event.getDestination());
    
    String recordingPath = event.getCustomField("recordingpath");
    if ((recordingPath != null) && (!recordingPath.equals(""))) {
      createParams.add(recordingPath);
      createParams.add(PropertiesReader.getSoundFileUrl(callId));
    }
    
    createParams.add(callId);
    
    try
    {
      if ((recordingPath != null) && (!recordingPath.equals(""))) {
        database.updateSql("UPDATE " + SQLiteDatabase.callsTable + " SET endtime = ?, starttime = ?, totalduration = ?, event = ?, from_number = ?, to_number = ?, recordingpath = ?, recordingurl = ? WHERE uid=?;", createParams);
      }
      else
      {
        database.updateSql("UPDATE " + SQLiteDatabase.callsTable + " SET endtime = ?, starttime = ?, totalduration = ?, event = ?, from_number = ?, to_number = ? WHERE uid=?;", createParams);
      }
    }
    catch (SQLException ex)
    {
      throw new UpdateDataException("Cannot update call details", ex);
    }
  }
  
  private String getCallerNumber() {
    String callerNumber = getCallerNumberFromChannel(event.getDestinationChannel());
    if (callerNumber == null) {
      callerNumber = event.getDestination();
    }
    
    return callerNumber;
  }
}