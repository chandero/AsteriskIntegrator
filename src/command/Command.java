// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Command.java

package command;

import org.asteriskjava.manager.event.ManagerEvent;

public interface Command
{

    public abstract void execute(ManagerEvent managerevent);
}
