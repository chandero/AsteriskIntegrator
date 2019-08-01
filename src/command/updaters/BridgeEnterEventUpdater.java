// Source File Name:   BridgeEnterEventUpdater.java

package command.updaters;

import exceptions.UpdateDataException;
import java.sql.SQLException;
import java.util.*;
import org.apache.http.message.BasicNameValuePair;
import org.asteriskjava.manager.event.BridgeEnterEvent;
import utils.CommonUtils;
import utils.SQLiteDatabase;

// Referenced classes of package command.updaters:
//            AbstractEventUpdater

public class BridgeEnterEventUpdater extends AbstractEventUpdater
{

    public BridgeEnterEventUpdater(BridgeEnterEvent event)
    {
        this.event = event;
    }

    public List getRequestParams()
    {
        List eventParams = null;
        String callId = getBridgeCallId();
        if(callId != null)
        {
            eventParams = super.getRequestParamsWithCustomVariables(callId);
            eventParams.add(new BasicNameValuePair("uniqueid1", CommonUtils.getFormattedUniqueId(event.getUniqueId())));
            eventParams.add(new BasicNameValuePair("callerid1", getCallerId1Number()));
            eventParams.add(new BasicNameValuePair("dateReceived", event.getDateReceived().toString()));
            eventParams.add(new BasicNameValuePair("callUUID", callId));
            eventParams.add(new BasicNameValuePair("callstatus", "BridgeEnter"));
        }
        return eventParams;
    }

    public void update()
        throws UpdateDataException
    {
        String uniqueid = CommonUtils.getFormattedUniqueId(event.getUniqueId());
        String callId = getBridgeCallId();
        try
        {
            if(callId == null)
            {
                List createParams = new ArrayList();
                createParams.add(uniqueid);
                createParams.add("BridgeEnterEvent");
                createParams.add(getCallerId1Number());
                database.updateSql((new StringBuilder()).append("INSERT INTO ").append(SQLiteDatabase.callsTable).append("(srcuid, destuid, event, from_number, to_number, bridged) VALUES(?,?,?,?,?,?);").toString(), createParams);
            } else
            {
                List updateParams = new ArrayList();
                updateParams.add(getCallerId1Number());
                updateParams.add("BridgeEnterEvent");
                updateParams.add(callId);
                database.updateSql((new StringBuilder()).append("UPDATE ").append(SQLiteDatabase.callsTable).append(" SET from_number = ?, to_number = ?, event = ?, bridged = ? WHERE uid = ?;").toString(), updateParams);
            }
        }
        catch(SQLException ex)
        {
            throw new UpdateDataException("Cannot update Bridge Enter Event", ex);
        }
    }

    private String getBridgeCallId()
    {
        String callId = null;
        String uniqueid1 = CommonUtils.getFormattedUniqueId(event.getUniqueId());
        String uniqueid2 = CommonUtils.getFormattedUniqueId(event.getBridgeUniqueId());
        List callUUIDs = getCallIdBySource(uniqueid1);
        if(callUUIDs.isEmpty())
            callUUIDs = getCallIdBySource(uniqueid2);
        if(!callUUIDs.isEmpty())
            callId = (String)callUUIDs.get(callUUIDs.size() - 1);
        return callId;
    }

    private String getCallerId1Number()
    {
        String number = getCallerNumberFromChannel(event.getCallerIdNum());
        if(number == null)
            number = event.getCallerIdNum();
        return number;
    }

    private final BridgeEnterEvent event;
}
