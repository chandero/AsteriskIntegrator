// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ConnectionManager.java

package utils;

import org.asteriskjava.manager.DefaultManagerConnection;
import org.asteriskjava.manager.ManagerConnection;

// Referenced classes of package utils:
//            PropertiesReader

public class ConnectionManager
{

    public ConnectionManager()
    {
    }

    public static ManagerConnection getConnection()
    {
        return new DefaultManagerConnection(PropertiesReader.getProperty("AsteriskServerIP"), Integer.parseInt(PropertiesReader.getProperty("AsteriskServerPort")), PropertiesReader.getProperty("AsteriskUsername"), PropertiesReader.getProperty("AsteriskPassword"));
    }
}
