// Source File Name:   DialEventUpdater.java

package command.updaters;

import exceptions.UpdateDataException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.http.message.BasicNameValuePair;
import org.asteriskjava.manager.event.NewChannelEvent;
import utils.CommonUtils;
import utils.SQLiteDatabase;

// Referenced classes of package command.updaters:
//            AbstractEventUpdater

public class NewChannelEventUpdater extends AbstractEventUpdater
{

    public NewChannelEventUpdater(NewChannelEvent event)
    {
        this.event = event;
    }


    public List getRequestParams()
    {
        List eventParams = null;
        if(event.getUniqueId() != null)
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
                eventParams.add(new BasicNameValuePair("StartTime", formatedDate));
                eventParams.add(new BasicNameValuePair("callUUID", callId));
                eventParams.add(new BasicNameValuePair("callstatus", "NewCall"));
            } 
        }
        return eventParams;
    }

    public void update()
        throws UpdateDataException
    {
        if(event.getUniqueId() != null)
        {
            List updateParams = new ArrayList();
            updateParams.add(CommonUtils.getFormattedUniqueId(event.getUniqueId()));
            updateParams.add(event.getClass().getSimpleName());
            updateParams.add(event.getChannel());
            updateParams.add(event.getCallerIdNum());
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

    private final  NewChannelEvent event;
}
