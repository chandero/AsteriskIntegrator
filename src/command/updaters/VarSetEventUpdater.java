// Source File Name:   VarSetEventUpdater.java

package command.updaters;

import exceptions.UpdateDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.asteriskjava.manager.event.VarSetEvent;
import utils.*;

// Referenced classes of package command.updaters:
//            AbstractEventUpdater

public class VarSetEventUpdater extends AbstractEventUpdater
{

    public VarSetEventUpdater(VarSetEvent varSetEvent)
    {
        event = varSetEvent;
    }

    public void update()
        throws UpdateDataException
    {
        System.out.println("Almacenando variable: " + event.getVariable() + " , valor: " + event.getValue());
        if(lookupVariablesNames.contains(event.getVariable()))
        {
            String callId = null;
            List callIds = getCallIdBySource(CommonUtils.getFormattedUniqueId(event.getUniqueId()));
            if(callIds.size() > 0)
                callId = (String)callIds.get(callIds.size() - 1);
            System.out.println("Almacenando variable en callid: " + callId);
            List updateParams = new ArrayList();
            try
            {
                if(callId == null)
                {
                    System.out.println("Almacenando insertando la variable: " + event.getVariable() + ", en callid: " + callId);
                    updateParams.add(CommonUtils.getFormattedUniqueId(event.getUniqueId()));
                    updateParams.add(event.getChannel());
                    updateParams.add(event.getValue());
                    database.updateSql((new StringBuilder()).append("INSERT INTO ").append(SQLiteDatabase.callsTable).append("(srcuid, channel, ").append(event.getVariable()).append(") VALUES(?,?,?);").toString(), updateParams);
                } else
                {
                    System.out.println("Almacenando actualizado la variable: " + event.getVariable() + ", en callid: " + callId);
                    updateParams.clear();
                    updateParams.add(event.getValue());
                    updateParams.add(callId);
                    String query = (new StringBuilder()).append("UPDATE ").append(SQLiteDatabase.callsTable).append(" SET ").append(event.getVariable()).append(" = ? WHERE uid = ?;").toString();
                    database.updateSql(query, updateParams);
                }
            }
            catch(SQLException ex)
            {
                System.out.println("Almacenando Error guardando la variable :" + event.getVariable());
                throw new UpdateDataException("Cannot update on VarSet event", ex);
            }
        }
    }

    private final VarSetEvent event;
    private final List lookupVariablesNames = PropertiesReader.getAsteriskLookUpVariablesNames();
}
