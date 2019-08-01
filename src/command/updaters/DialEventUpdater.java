// Source File Name:   DialEventUpdater.java

package command.updaters;

import exceptions.UpdateDataException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.http.message.BasicNameValuePair;
import org.asteriskjava.manager.event.DialBeginEvent;
import org.asteriskjava.manager.event.DialEvent;
import utils.CommonUtils;
import utils.SQLiteDatabase;

// Referenced classes of package command.updaters:
//            AbstractEventUpdater

public class DialEventUpdater extends AbstractEventUpdater
{

    public DialEventUpdater(DialEvent event)
    {
        this.event = event;
    }

    public DialEventUpdater(DialBeginEvent event)
    {
        this.event = event;
    }

    public List getRequestParams()
    {
        List eventParams = null;
        if("Begin".equals(event.getSubEvent()) && event.getUniqueId() != null && event.getDestUniqueId() != null)
        {
            Date date = new Date();
            if(event.getTimestamp() != null)
                date.setTime(event.getTimestamp().longValue());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formatedDate = dateFormat.format(date);
            List callIds = getCallIdBySource(CommonUtils.getFormattedUniqueId(event.getUniqueId()));
            if(callIds.size() > 0)
            {
                String callId = (String)callIds.get(callIds.size() - 1);
                eventParams = super.getRequestParamsWithCustomVariables(callId);
                eventParams.add(new BasicNameValuePair("event", event.getClass().getSimpleName()));
                eventParams.add(new BasicNameValuePair("callerIdNumber", event.getCallerIdNum()));
                eventParams.add(new BasicNameValuePair("dialString", event.getDialString()));
                eventParams.add(new BasicNameValuePair("connectedLineName", event.getDestConnectedLineName()));
                eventParams.add(new BasicNameValuePair("StartTime", formatedDate));
                eventParams.add(new BasicNameValuePair("callUUID", callId));
                eventParams.add(new BasicNameValuePair("callstatus", "DialBegin"));
            }
        }
        return eventParams;
    }

    public void update()
        throws UpdateDataException
    {
        if("Begin".equals(event.getSubEvent()) && event.getUniqueId() != null && event.getDestUniqueId() != null)
        {
            List updateParams = new ArrayList();
            updateParams.add(CommonUtils.getFormattedUniqueId(event.getUniqueId()));
            updateParams.add(CommonUtils.getFormattedUniqueId(event.getDestUniqueId()));
            updateParams.add(event.getClass().getSimpleName());
            updateParams.add(event.getChannel());
            updateParams.add(event.getCallerIdNum());
            updateParams.add(event.getDialString());
            try
            {
                String callId = null;
                List callUUIDs = getCallIdBySource(CommonUtils.getFormattedUniqueId(event.getUniqueId()));
                if(callUUIDs.size() > 0)
                    callId = (String)callUUIDs.get(callUUIDs.size() - 1);
                if(callId == null)
                {
                    database.updateSql((new StringBuilder()).append("INSERT INTO ").append(SQLiteDatabase.callsTable).append("(srcuid, destuid, event, channel, from_number, to_number) VALUES(?,?,?,?,?,?);").toString(), updateParams);
                } else
                {
                    updateParams.add(callId);
                    database.updateSql((new StringBuilder()).append("UPDATE ").append(SQLiteDatabase.callsTable).append(" SET srcuid=?, destuid=?, event=?, channel=?, from_number=?, to_number=? WHERE uid=?").toString(), updateParams);
                }
            }
            catch(SQLException ex)
            {
                throw new UpdateDataException("Cannot add call to db", ex);
            }
        }
    }

    private static final String UPDATEBLE_SUBEVENT = "Begin";
    private final DialEvent event;
}
