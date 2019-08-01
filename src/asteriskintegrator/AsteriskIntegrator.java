// Source File Name:   AsteriskIntegrator.java

package asteriskintegrator;

import command.helpers.CommandsExecutor;
import migration.Migration;
import org.apache.log4j.Logger;
import org.asteriskjava.manager.ManagerConnection;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import servlets.*;
import utils.*;

public class AsteriskIntegrator
{

    public AsteriskIntegrator()
    {
    }

    public static void main(String args[])
    {
        try
        {
            ManagerConnection asteriskConnection = ConnectionManager.getConnection();
            asteriskConnection.addEventListener(new CommandsExecutor());
            WebAppContext context = new WebAppContext();
            context.setWar(".");
            context.addServlet(new ServletHolder(new RecordingServlet()), "/recording");
            context.addServlet(new ServletHolder(new MakeCallServlet()), "/makecall");
            context.addServlet(new ServletHolder(new CdrServlet()), "/cdrinfo");            
            context.addServlet(new ServletHolder(new RootServlet()), "");
            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[] {
                context, new DefaultHandler()
            });
            Migration.applyMigrations();
            (new Thread(new ConnectionTask(asteriskConnection))).start();
            Server server = new Server();
            SelectChannelConnector connector = new SelectChannelConnector();
            connector.setPort(Integer.parseInt(PropertiesReader.getProperty("ServerPort")));
            server.addConnector(connector);
            server.setHandler(handlers);
            server.start();
            server.join();
        }
        catch(Exception ex)
        {
            logger.fatal((new StringBuilder()).append("Critical error on starting connector: ").append(ex.getMessage()).toString(), ex);
            throw new RuntimeException("Critical error on starting connector. Stop running.");
        }
    }

    private static final Logger logger = Logger.getLogger(AsteriskIntegrator.class);
    public static int ASTERISK_VERSION = 0;

}
