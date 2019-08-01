// Source File Name:   VarSetEventCommand.java

package command;

import command.BridgeEventCommand;
import command.Command;
import command.updaters.VarSetEventUpdater;
import exceptions.UpdateDataException;
import org.apache.log4j.Logger;
import org.asteriskjava.manager.event.ManagerEvent;
import org.asteriskjava.manager.event.VarSetEvent;

public class VarSetEventCommand
implements Command {
    private static final Logger logger = Logger.getLogger(BridgeEventCommand.class);

    public void execute(ManagerEvent event) {
        VarSetEventUpdater eventUpdater = new VarSetEventUpdater((VarSetEvent)event);
        try {
            eventUpdater.update();
        }
        catch (UpdateDataException ex) {
            logger.error((Object)"Error on update data on VarSet Event", (Throwable)ex);
        }
    }
}
