// Source File Name:   MakeCallServlet.java
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.SQLiteDatabase;

public class CdrServlet extends HttpServlet {

    public CdrServlet() {
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JSONObject listObject = new JSONObject();

        try {
            database = SQLiteDatabase.getInstance();

            JSONArray cdr = new JSONArray();

            List<String> params = new ArrayList<String>();

            ResultSet rs = database.selectSql("SELECT *"
                    + " FROM " + SQLiteDatabase.callsTable, params);
            ResultSetMetaData metadata = rs.getMetaData();
            int numberOfColumns = metadata.getColumnCount();
            
            while (rs.next()) {
                JSONObject o = new JSONObject();
                int i = 1;
                while(i <= numberOfColumns) {
                   String cn = metadata.getColumnName(i);
                   o.put(cn, rs.getString(i++));
                }
                cdr.add(o);
            }
            listObject.put("cdr", cdr);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(CdrServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        PrintWriter writer = response.getWriter();
        try {
            response.setContentType(
                "application/json");
            writer.println(listObject.toJSONString());
        } catch (Throwable localThrowable1) {
            throw localThrowable1;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Throwable localThrowable2) {
                    localThrowable2.addSuppressed(localThrowable2);
                }
            } else {
                writer.close();
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private static final Logger logger = Logger.getLogger(MakeCallServlet.class
    );
    protected SQLiteDatabase database;

}
