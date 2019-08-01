// Source File Name:   RecordingAction.java

package utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;

// Referenced classes of package utils:
//            SQLiteDatabase

public class RecordingAction
{

    public RecordingAction(String id)
    {
        database = SQLiteDatabase.getInstance();
        this.id = id;
    }

    public File getFile()
        throws FileNotFoundException
    {
        String recordUniqueId = getRecordUniqueId(id);
        if(recordUniqueId == null) {
            return null;
        }
        File file = BuscarGrabacion(recordUniqueId);
        return file;
    }

    private String getRecordUniqueId(String id) {
        List selectParams = new ArrayList<>();
        selectParams.add(id);
        try
        {
            ResultSet result = database.selectSql((new StringBuilder()).append("SELECT srcuid FROM ").append(SQLiteDatabase.callsTable).append(" WHERE uid = ? ;").toString(), selectParams);
            String path = result.getString(1);
            result.getStatement().close();
            return path;
        }
        catch(SQLException ex)
        {
            logger.fatal("Error on select recording path", ex);
        }
        return null;        
    }
    
    private String getRecordPath(String id)
    {
        
        List selectParams = new ArrayList();
        selectParams.add(id);
        try
        {
            ResultSet result = database.selectSql((new StringBuilder()).append("SELECT recordingpath FROM ").append(SQLiteDatabase.callsTable).append(" WHERE uid = ? ;").toString(), selectParams);
            String path = result.getString(1);
            result.getStatement().close();
            return path;
        }
        catch(SQLException ex)
        {
            logger.fatal("Error on select recording path", ex);
        }
        return null;
    }
    
    private File BuscarGrabacion(String string) {
        File result = null;
        File dir = new File("C:\\opt\\audios\\");
        FileFilter fileFilter = new WildcardFileFilter("*" + string + "*");
        File[] files = dir.listFiles(fileFilter);
        if (files != null && files.length > 0) {
            return files[0];
        }
        dir = new File("/var/spool/asterisk/monitor/");
        fileFilter = new WildcardFileFilter("*" + string + "*");
        files = dir.listFiles(fileFilter);
        if (files != null && files.length > 0) {
            result = files[0];
        } else {
            dir = new File("/var/spool/asterisk/monitor/in/");
            fileFilter = new WildcardFileFilter("*" + string + "*");
            files = dir.listFiles(fileFilter);
            if (files != null && files.length > 0) {
                result = files[0];
            } else {
                dir = new File("/var/spool/asterisk/monitor/out/");
                fileFilter = new WildcardFileFilter("*" + string + "*");
                files = dir.listFiles(fileFilter);
                if (files != null && files.length > 0) {
                    result = files[0];
                } else {
                    dir = new File("/var/spool/asterisk/monitor/queue/");
                    fileFilter = new WildcardFileFilter("*" + string + "*");
                    files = dir.listFiles(fileFilter);
                    if (files != null && files.length > 0) {
                        result = files[0];
                    } else {
                        result = null;
                    }
                }
            }
        }
        return result;
    }
    

    private static final Logger logger = Logger.getLogger(RecordingAction.class);
    private final String id;
    protected SQLiteDatabase database;

}
