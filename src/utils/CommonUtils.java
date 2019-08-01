// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CommonUtils.java
package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommonUtils {

    public CommonUtils() {
    }

    public static String getFormattedUniqueId(String rawUniqueId) {
        String idParts[] = rawUniqueId.split("\\.");
        return idParts[0];
    }

    public static List<String> executeCommand(String command) {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        List<String> output = new ArrayList<String>();
        if (OS.indexOf("win") >= 0) {
            //TODO Quitar comentario o agregar para modo desarrollo
            try {
                //System.out.println("UtilsService - Line 42 comando >> "+command);
                Runtime runtime = Runtime.getRuntime();
                String[] cmd = {"cmd", "/c", " " + command};
                Process child = runtime.exec(cmd);
                InputStreamReader irs = new InputStreamReader(child.getInputStream());
                BufferedReader br = new BufferedReader(irs);
                String line;
                while ((line = br.readLine()) != null) {
                    output.add(line);
                }
                //System.out.println("salida del comando:"+command+"\n Salida:"+output.toString());
                irs.close();
                br.close();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        } else {
            //TODO Quitar comentario o agregar para modo desarrollo
            try {
                Runtime runtime = Runtime.getRuntime();
                String[] cmd = {"/bin/bash", "-c", command};
                Process child = runtime.exec(cmd);
                child.waitFor();
                InputStreamReader irs = new InputStreamReader(child.getInputStream());
                BufferedReader br = new BufferedReader(irs);
                //System.out.println("UtilsService - Line 65 comando >> "+command);
                String line;
                while ((line = br.readLine()) != null) {
                    //System.out.println("UtilsService - Line 76 linea buffer >> "+line);
                    output.add(line);
                    // fw.write(line + "\n");
                }
                //System.out.println("salida del comando:"+command+"\n Salida:"+output.toString());
                irs.close();
                br.close();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        //System.out.println("UtilsService - Line 87 retorno comando >> "+output.toString());
        return output;
    }

}
