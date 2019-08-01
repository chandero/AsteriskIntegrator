// Source File Name:   HangupEventCommand.java

package command;

import command.updaters.HangupEventUpdater;
import exceptions.UpdateDataException;
import org.apache.log4j.Logger;
import org.asteriskjava.manager.event.HangupEvent;
import org.asteriskjava.manager.event.ManagerEvent;
import utils.VtigerConnector;

// Referenced classes of package command:
//            BridgeEventCommand, Command

public class HangupEventCommand
    implements Command
{

    public HangupEventCommand()
    {
    }

    public void execute(ManagerEvent event)
    {
        HangupEventUpdater eventUpdater = new HangupEventUpdater((HangupEvent)event);
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
            logger.error("Error on update data on Hangup Event", ex);
        }
    }

    private static final Logger logger = Logger.getLogger(BridgeEventCommand.class);

}
