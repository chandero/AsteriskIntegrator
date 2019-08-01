// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ChannelProtocolResolver.java

package utils;


// Referenced classes of package utils:
//            PropertiesReader

public class ChannelProtocolResolver
{

    public ChannelProtocolResolver()
    {
    }

    public static String getOriginateChannelProtocol(String number)
    {
        String channelProtocol = PropertiesReader.getProperty(number);
        if(channelProtocol.equals(""))
        {
            channelProtocol = PropertiesReader.getProperty("DefaultOriginateChannelProtocol");
            if(channelProtocol.equals(""))
                channelProtocol = "SIP";
        }
        return channelProtocol;
    }

    private static final String DEFAULT_CHANNEL_PROTOCOL = "SIP";
}
