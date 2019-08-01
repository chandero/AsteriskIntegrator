// Source File Name:   NewChannelEventCommand.java

package command;

import command.updaters.NewChannelEventUpdater;
import exceptions.UpdateDataException;
import org.apache.log4j.Logger;
import org.asteriskjava.manager.event.*;
import utils.VtigerConnector;

// Referenced classes of package command:
//            Command

public class NewChannelEventCommand
    implements Command
{

    public NewChannelEventCommand()
    {
    }

    public void execute(ManagerEvent event)
    {
        NewChannelEventUpdater eventUpdater;
        eventUpdater = new NewChannelEventUpdater((NewChannelEvent)event);
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
            logger.error("Error on update data on NewChannel Event", ex);
        }
    }

    private static final Logger logger = Logger.getLogger(NewChannelEventCommand.class);

}
