// Source File Name:   ConnectionTask.java

package utils;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.asteriskjava.AsteriskVersion;
import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.TimeoutException;

public class ConnectionTask
  implements Runnable
{
  private ManagerConnection asteriskConnection;
  private static final Logger logger = Logger.getLogger(ConnectionTask.class);
  
  public ConnectionTask(ManagerConnection asteriskConnection) {
    this.asteriskConnection = asteriskConnection;
  }
  
  public void run()
  {
    boolean isConnected = false;
    while (!isConnected) {
      try {
        asteriskConnection.login();
        AsteriskVersion astVersion = asteriskConnection.getVersion();
        asteriskintegrator.AsteriskIntegrator.ASTERISK_VERSION = astVersion.hashCode();
        isConnected = true;
      }
      catch (IllegalStateException|IOException|AuthenticationFailedException|TimeoutException ex) {
        logger.warn("Can not connect to Asterisk:", ex);
        try {
          Thread.sleep(1000L);
        }
        catch (InterruptedException localInterruptedException) {}
      }
    }
  }
}