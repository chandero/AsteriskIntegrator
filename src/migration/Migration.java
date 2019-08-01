// Source File Name:   Migration.java

package migration;

import exceptions.MigrationException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import utils.SQLiteDatabase;

public abstract class Migration
{
  private static ArrayList<Migration> migrationsList = new ArrayList();
  
  private static String migrateVersion = "1.4.2";
  protected SQLiteDatabase database;
  
  static { migrationsList.add(new Migrate141()); }
  
  protected void initializate(SQLiteDatabase database)
  {
    this.database = database;
  }
  
  public static void applyMigrations() throws MigrationException {
    SQLiteDatabase database = SQLiteDatabase.getInstance();
    String version = null;
    try {
      database.updateSql("CREATE TABLE IF NOT EXISTS sp_version(version VARCHAR(31) DEFAULT NULL);");
      
      ResultSet result = database.selectSql("SELECT version FROM sp_version", new ArrayList());Throwable localThrowable3 = null;
      try { if (result.next()) {
          version = result.getString("version");
        }
        
        Statement stmt = result.getStatement();
        if (stmt != null) {
          stmt.close();
        }
      }
      catch (Throwable localThrowable1)
      {
        localThrowable3 = localThrowable1;throw localThrowable1;
      }
      finally
      {
        if (result != null) if (localThrowable3 != null) try { result.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else result.close();
      }
    } catch (SQLException ex) { throw new RuntimeException("Error on migraion check", ex);
    }
    
    boolean isNeedAddMigrations = version == null;
    boolean migrationWas = false;
    
    for (Migration migration : migrationsList) {
      if (!isNeedAddMigrations) {
        isNeedAddMigrations = migration.getVersion().equals(version);
      }
      else if (isNeedAddMigrations) {
        migration.initializate(database);
        migration.migrate();
        migrationWas = true;
      }
    }
    
    if (migrationWas) {
      try {
        ArrayList<String> params = new ArrayList();
        params.add(migrateVersion);
        
        if (version != null) {
          database.updateSql("UPDATE sp_version SET version=?", params);
        }
        else
        {
          database.updateSql("INSERT INTO sp_version(version) VALUES(?)", params);
        }
        
      }
      catch (SQLException ex)
      {
        throw new RuntimeException("Error on migraion apply", ex);
      }
    }
  }
  
  public Migration() {}
  
  public abstract void migrate()
    throws MigrationException;
  
  public abstract String getVersion();
}

