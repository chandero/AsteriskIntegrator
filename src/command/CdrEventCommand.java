// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CdrEventCommand.java

package command;

import command.updaters.CdrEventUpdater;
import exceptions.UpdateDataException;
import java.util.List;
import org.apache.log4j.Logger;
import org.asteriskjava.manager.event.CdrEvent;
import org.asteriskjava.manager.event.ManagerEvent;
import utils.VtigerConnector;

// Referenced classes of package command:
//            Command

public class CdrEventCommand
    implements Command
{

    public CdrEventCommand()
    {
    }

    public void execute(ManagerEvent event)
    {
        CdrEventUpdater eventUpdater = new CdrEventUpdater((CdrEvent)event);
        try
        {
            eventUpdater.update();
            VtigerConnector connector = new VtigerConnector();
            List requestParams = eventUpdater.getRequestParams();
            if(requestParams != null)
                connector.sendCommand(requestParams);
            requestParams = eventUpdater.getRecordingRequestParams();
            if(requestParams != null)
                connector.sendCommand(requestParams);
        }
        catch(UpdateDataException ex)
        {
            logger.error("Error on update data on Cdr Event", ex);
        }
    }

    private static final Logger logger = Logger.getLogger(CdrEventCommand.class);

}
