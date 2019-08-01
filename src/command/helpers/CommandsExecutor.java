// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CommandsExecutor.java

package command.helpers;

import command.Command;
import command.helpers.CommandsFactory;
import org.apache.log4j.Logger;
import org.asteriskjava.manager.ManagerEventListener;
import org.asteriskjava.manager.event.ManagerEvent;

public class CommandsExecutor
implements ManagerEventListener {
    private static final Logger logger = Logger.getLogger(CommandsExecutor.class);

    public void onManagerEvent(ManagerEvent event) {
        try {
            logger.info((Object)event);
            Command executeCommand = CommandsFactory.createCommand((ManagerEvent)event);
            if (executeCommand != null) {
                executeCommand.execute(event);
            }
        }
        catch (IllegalAccessException | InstantiationException ex) {
            logger.fatal((Object)"Cannot acces to command method", (Throwable)ex);
        }
        catch (ClassNotFoundException ex) {
            logger.info((Object)ex);
        }
    }
}