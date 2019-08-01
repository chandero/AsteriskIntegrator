// Source File Name:   PropertiesReader.java

package utils;

import java.io.*;
import java.util.*;

public class PropertiesReader
{

    public PropertiesReader()
    {
    }

    public static String getProperty(String name)
    {
        initPropertiesIfNeed();
        return props.getProperty(name, "");
    }

    public static boolean isNeedCheckKey()
    {
        initPropertiesIfNeed();
        return Boolean.parseBoolean(props.getProperty("CheckKeyOnListenRequest", "false"));
    }

    public static String getSoundFileUrl(String recordId)
    {
        return (new StringBuilder()).append("http://").append(getProperty("ServerIP")).append(":").append(getProperty("ServerPort")).append("/recording?id=").append(recordId).toString();
    }

    public static List<String> getAsteriskLookUpVariablesNames()
    {
        List lookupVariablesNames = new ArrayList();
        String rawConfigValue = getProperty("LookUpVariablesNames");
        if(!rawConfigValue.equals(""))
        {
            String variablesNames[] = rawConfigValue.split(",");
            lookupVariablesNames = Arrays.asList(variablesNames);
        }
        return lookupVariablesNames;
    }

    private static void initPropertiesIfNeed()
    {
        if(props == null)
        {
            props = new Properties();
            try
            {
                props.load(new FileInputStream("./../conf/AsteriskIntegrator.properties"));
            }
            catch(FileNotFoundException ex)
            {
                throw new RuntimeException(ex);
            }
            catch(IOException ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }

    private static final String configurationPath = "./../conf/AsteriskIntegrator.properties";
    private static Properties props = null;

}
