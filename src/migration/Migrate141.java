// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Migrate141.java

package migration;

import exceptions.MigrationException;
import java.sql.SQLException;
import utils.SQLiteDatabase;

// Referenced classes of package migration:
//            Migration

public class Migrate141 extends Migration
{

    public Migrate141()
    {
    }

    public void migrate()
        throws MigrationException
    {
        try
        {
            database.updateSql((new StringBuilder()).append("CREATE INDEX idx_srcuid ON ").append(SQLiteDatabase.callsTable).append("(srcuid)").toString());
            database.updateSql((new StringBuilder()).append("CREATE INDEX idx_destuid ON ").append(SQLiteDatabase.callsTable).append("(destuid)").toString());
        }
        catch(SQLException ex)
        {
            throw new MigrationException("Error on create indexes", ex);
        }
    }

    public String getVersion()
    {
        return "1.4.1";
    }
}
