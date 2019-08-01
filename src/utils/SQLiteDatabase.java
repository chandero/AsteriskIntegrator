// Source File Name:   SQLiteDatabase.java

package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import utils.PropertiesReader;

public class SQLiteDatabase {
    public static String callsTable = "crm_calls_info";
    public static String versionTable = "version";
    private static SQLiteDatabase database = null;
    private static final String dbName = "CrmAsterisk";
    private Connection connection;

    public static synchronized SQLiteDatabase getInstance() {
        if (database == null) {
            database = new SQLiteDatabase();
        }
        return database;
    }

    public synchronized void updateSql(String sql, List<String> params) throws SQLException {
        try (PreparedStatement stmt = this.prepareSql(sql, params);){
            stmt.execute();
        }
    }

    public synchronized void updateSql(String sql) throws SQLException {
        this.updateSql(sql, new ArrayList<String>());
    }

    public synchronized ResultSet selectSql(String sql, List<String> params) throws SQLException {
        PreparedStatement stmt = this.prepareSql(sql, params);
        return stmt.executeQuery();
    }

    public void close() throws SQLException {
        if (this.connection != null) {
            this.connection.close();
            this.connection = null;
        }
        database = null;
    }

    private SQLiteDatabase() {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + PropertiesReader.getProperty((String)"AsteriskAppDBPath") + "/" + dbName + ".db");
        }
        catch (SQLException ex) {
            throw new RuntimeException("Unable connect to Database" + ex.getMessage());
        }
        if (!this.checkStructure()) {
            this.createStructure();
        }
        this.createVariablesFieldsStructure();
    }

    private boolean checkStructure() {
        try {
            int tablesCount;
            try (Statement stmt = this.connection.createStatement();
                 ResultSet result = stmt.executeQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='" + callsTable + "';");){
                result.next();
                tablesCount = result.getInt(1);
            }
            return tablesCount != 0;
        }
        catch (SQLException ex) {
            throw new RuntimeException("Unable to check database structure" + ex.getMessage());
        }
    }

    private void createVariablesFieldsStructure() {
        for (String variableName : PropertiesReader.getAsteriskLookUpVariablesNames()) {
            if (this.isColumnExists(variableName)) continue;
            this.createDefaultColumn(variableName);
        }
    }

    private boolean isColumnExists(String columnName) {
        boolean columnExists;
        columnExists = true;
        try {
            try (Statement stmt = this.connection.createStatement();){
                stmt.executeQuery("SELECT " + columnName + " FROM " + callsTable + ";");
            }
        }
        catch (SQLException ex) {
            columnExists = false;
        }
        return columnExists;
    }

    private void createDefaultColumn(String columnName) {
        try {
            try (Statement stmt = this.connection.createStatement();){
                stmt.executeUpdate("ALTER TABLE " + callsTable + " ADD COLUMN " + columnName + " varchar(255) DEFAULT NULL;");
            }
        }
        catch (SQLException ex) {
            throw new RuntimeException("Unable to create column " + columnName, ex);
        }
    }

    private void createStructure() {
        try {
            try (Statement stmt = this.connection.createStatement();){
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + callsTable + " (uid INTEGER PRIMARY KEY AUTOINCREMENT,srcuid varchar(255) NOT NULL,destuid varchar(255) DEFAULT NULL,event varchar(50) DEFAULT NULL,direction varchar(50) DEFAULT NULL,channel varchar(255) DEFAULT NULL,from_number varchar(255) DEFAULT NULL,to_number varchar(255) DEFAULT NULL,starttime datetime DEFAULT NULL,endtime datetime DEFAULT NULL,totalduration int DEFAULT NULL,bridged varchar(20) DEFAULT NULL,callcause varchar(50) DEFAULT NULL,recordingpath varchar(255) DEFAULT NULL,recordingurl varchar(255) DEFAULT NULL);");
            }
        }
        catch (SQLException ex) {
            throw new RuntimeException("Cannot create database stucture" + ex.getMessage());
        }
    }

    private PreparedStatement prepareSql(String sql, List<String> params) throws SQLException {
        PreparedStatement stmt = this.connection.prepareStatement(sql);
        int paramIndex = 1;
        for (String currentParam : params) {
            stmt.setString(paramIndex, currentParam);
            ++paramIndex;
        }
        return stmt;
    }
}