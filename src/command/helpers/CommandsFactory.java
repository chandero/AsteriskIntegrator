// Source File Name:   CommandsFactory.java

package command.helpers;

import command.*;
import java.util.HashMap;
import java.util.Map;
import org.asteriskjava.manager.event.ManagerEvent;

public class CommandsFactory
{

    public CommandsFactory()
    {
    }

    public static Command createCommand(ManagerEvent event)
        throws IllegalAccessException, InstantiationException, ClassNotFoundException
    {
        String simpleEventName = event.getClass().getSimpleName();
        if(eventProcessors.containsKey(simpleEventName))
            return (Command)((Class)eventProcessors.get(simpleEventName)).newInstance();
        else
            return null;
    }

    private static final Map eventProcessors;

    static 
    {
        eventProcessors = new HashMap();
        eventProcessors.put("BridgeEvent", BridgeEventCommand.class);
        eventProcessors.put("BridgeEnterEvent", BridgeEnterEventCommand.class);        
        eventProcessors.put("CdrEvent", CdrEventCommand.class);
        eventProcessors.put("DialEvent", DialEventCommand.class);
        eventProcessors.put("DialBeginEvent", DialEventCommand.class);
        eventProcessors.put("NewChannelEvent", NewChannelEventCommand.class);
        eventProcessors.put("HangupEvent", HangupEventCommand.class);
        eventProcessors.put("VarSetEvent", VarSetEventCommand.class);
    }
}
