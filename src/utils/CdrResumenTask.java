package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author chandero
 */
public class CdrResumenTask implements Runnable {

    private java.sql.Connection connection;
    private final String fileName = System.getProperty("user.dir") + "/CdrResumenLast.noborrar";

    public CdrResumenTask() {
    }

    @Override
    public void run() {
        System.out.println("Archivo CdrResumenLast: " + fileName);
        this.connection = MysqlConnectionFactory.getVoiceConnection();
        if (this.connection != null) {
            String query = "SELECT * FROM cdr_resumen cr1 WHERE cr1.uniqueid > ?";
            Boolean _isSendEvent = Boolean.valueOf(PropertiesReader.getProperty("CrmSendEvents"));
            System.out.println("Se estan enviando eventos:" + _isSendEvent.toString());
            System.out.println("Conexión Activa:" + this.connection.toString());
            while (_isSendEvent) {
                try {
                    String sLastUniqueId = new String(Files.readAllBytes(Paths.get(fileName)));
                    PreparedStatement stmt;
                    stmt = this.connection.prepareStatement(query);
                    stmt.setString(1, sLastUniqueId.trim());
                    ResultSet result = stmt.executeQuery();
                    VtigerConnector connector = new VtigerConnector();
                    while (result.next()) {
                        Date calldate = result.getDate("calldate");
                        String clid = result.getString("clid");
                        String src = result.getString("src");
                        String dst = result.getString("dst");
                        String dcontext = result.getString("dcontext");
                        String channel = result.getString("channel");
                        String dstchannel = result.getString("dstchannel");
                        String lastapp = result.getString("lastapp");
                        String lastdata = result.getString("lastdata");
                        Integer duration = result.getInt("duration");
                        Integer billsec = result.getInt("billsec");
                        String disposition = result.getString("disposition");
                        String amaflags = result.getString("amaflags");
                        String accountcode = result.getString("accountcode");
                        String uniqueid = result.getString("uniqueid");
                        String userfield = result.getString("userfield");

                        List<BasicNameValuePair> requestParams = new ArrayList();
                        requestParams.add(new BasicNameValuePair("calldate", String.valueOf(calldate.getTime())));
                        requestParams.add(new BasicNameValuePair("src", src));
                        requestParams.add(new BasicNameValuePair("dst", dst));
                        requestParams.add(new BasicNameValuePair("dcontext", dcontext));
                        requestParams.add(new BasicNameValuePair("channel", channel));
                        requestParams.add(new BasicNameValuePair("dstchannel", dstchannel));
                        requestParams.add(new BasicNameValuePair("duration", duration.toString()));
                        requestParams.add(new BasicNameValuePair("billableseconds", billsec.toString()));
                        requestParams.add(new BasicNameValuePair("disposition", disposition));
                        requestParams.add(new BasicNameValuePair("accountcode", accountcode));
                        requestParams.add(new BasicNameValuePair("callstatus", "Call"));
                        requestParams.add(new BasicNameValuePair("callUUID", uniqueid));
                        // Date fecha = new Date(encu_fecha*1000);
                        // String sFecha = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(fecha);
                        // requestParams.add(new BasicNameValuePair("encutime", sFecha));
                        // requestParams.add(new BasicNameValuePair("channel", encu_channel));
                        //  requestParams.add(new BasicNameValuePair("callerid", encu_callerid));
                        //  requestParams.add(new BasicNameValuePair("encuasknumber", String.valueOf(encu_pregunta + 1)));
                        //  requestParams.add(new BasicNameValuePair("encuansnumber", String.valueOf(encu_respuesta)));
                        connector.sendCommand(requestParams);
                        Files.writeString(Paths.get(fileName), String.valueOf(uniqueid), StandardOpenOption.WRITE);
                    }
                    stmt.close();
                    // Files.writeString(Paths.get(fileName), String.valueOf(lastTimeData), StandardOpenOption.WRITE);
                    TimeUnit.SECONDS.sleep(30);
                } catch (IOException | SQLException ex) {
                    java.util.logging.Logger.getLogger(CdrResumenTask.class.getName()).log(Level.SEVERE, ex.getMessage());
                } catch (InterruptedException ex) {
                    Logger.getLogger(CdrResumenTask.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            System.err.println("No se pudo establecer una conexion a IPPBX");
        }
    }
}
