// Source File Name:   BridgeEventCommand.java

package command;

import command.updaters.BridgeEventUpdater;
import exceptions.UpdateDataException;
import org.apache.log4j.Logger;
import org.asteriskjava.manager.event.BridgeEvent;
import org.asteriskjava.manager.event.ManagerEvent;
import utils.VtigerConnector;

// Referenced classes of package command:
//            Command

public class BridgeEventCommand
    implements Command
{

    public BridgeEventCommand()
    {
    }

    public void execute(ManagerEvent event)
    {
        BridgeEventUpdater eventUpdater = new BridgeEventUpdater((BridgeEvent)event);
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
            logger.error("Error on update data on Bridge Event", ex);
        }
    }

    private static final Logger logger = Logger.getLogger(BridgeEventCommand.class);

}
