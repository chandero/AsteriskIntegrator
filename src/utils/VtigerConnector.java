// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   VtigerConnector.java

package utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.*;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

// Referenced classes of package utils:
//            InsecureSSLSocketFactory, PropertiesReader

public class VtigerConnector
{
    
    private static final Logger logger = Logger.getLogger(VtigerConnector.class);
    private final String requestPrefix;
    private final String requestUri;

    public VtigerConnector()
    {
        requestPrefix = PropertiesReader.getProperty("CrmURLPrefix");
        requestUri = (new StringBuilder()).append(PropertiesReader.getProperty("CrmURL")).append(requestPrefix).toString();
    }

    public VtigerConnector(String requestUri)
    {
        this.requestUri = requestUri;
        this.requestPrefix = PropertiesReader.getProperty("CrmURLPrefix");
    }

    public void sendCommand(List eventParams)
    {
        HttpPost request = new HttpPost(requestUri);
        HttpClient client = getNewHttpClient();
        try
        {
            request.setEntity(new UrlEncodedFormEntity(eventParams, "UTF-8"));
            logger.info("Query a enviar: " + request.toString());
            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            // Getting the response body.
            String responseBody = EntityUtils.toString(response.getEntity());
            
            logger.info("Respuesta del CRM code: " + statusCode + ", body: " + responseBody);
        }
        catch(UnsupportedEncodingException ex)
        {
            logger.fatal("Error on send POST request to vtiger", ex);
        }
        catch(IOException ex)
        {
            logger.fatal("Error on send POST request to vtiger", ex);
        }
    }

    private HttpClient getNewHttpClient()
    {
        try
        {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            InsecureSSLSocketFactory sf = new InsecureSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            org.apache.http.params.HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, "UTF-8");
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            org.apache.http.conn.ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        }
        catch(Exception e)
        {
            return new DefaultHttpClient();
        }
    }



}
