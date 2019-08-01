// Source File Name:   DialEventCommand.java

package command;

import command.updaters.DialEventUpdater;
import exceptions.UpdateDataException;
import org.apache.log4j.Logger;
import org.asteriskjava.manager.event.*;
import utils.VtigerConnector;

// Referenced classes of package command:
//            Command

public class DialEventCommand
    implements Command
{

    public DialEventCommand()
    {
    }

    public void execute(ManagerEvent event)
    {
        DialEventUpdater eventUpdater;
        if(event.getClass() == DialEvent.class)
            eventUpdater = new DialEventUpdater((DialEvent)event);
        else
            eventUpdater = new DialEventUpdater((DialBeginEvent)event);
        try
        {
            eventUpdater.update();
            VtigerConnector connector = new VtigerConnector();
            java.util.List requestParams = eventUpdater.getRequestParams();
            if(requestParams != null)
                connector.sendCommand(eventUpdater.getRequestParams());
        }
        catch(UpdateDataException ex)
        {
            logger.error("Error on update data on Dial Event", ex);
        }
    }

    private static final Logger logger = Logger.getLogger(DialEventCommand.class);

}
