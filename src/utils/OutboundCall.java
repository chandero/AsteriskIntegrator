// Source File Name:   OutboundCall.java

package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.OriginateAction;
import org.asteriskjava.manager.response.ManagerResponse;

public class OutboundCall {

   private static final Logger logger = Logger.getLogger(OutboundCall.class);
   private static final List ignoreCallParamsNames = new ArrayList();
   private final String from;
   private final String to;
   private final String context;
   private final String secret;
   private final Map callParams;


   public OutboundCall(HttpServletRequest request) {
      this.from = request.getParameter("from");
      this.to = request.getParameter("to");
      this.context = request.getParameter("context");
      this.secret = request.getParameter("secret");
      this.callParams = new HashMap();
      Enumeration parameterNames = request.getParameterNames();

      while(parameterNames.hasMoreElements()) {
         String parameterName = (String)parameterNames.nextElement();
         if(!ignoreCallParamsNames.contains(parameterName)) {
            this.callParams.put(parameterName, request.getParameter(parameterName));
         }
      }
   }

   public String doCallFromRequest() {
      String secretKey = PropertiesReader.getProperty("CrmSecretKey");
      if(this.secret.equals(secretKey)) {
         OriginateAction action = new OriginateAction();
         action.setChannel(ChannelProtocolResolver.getOriginateChannelProtocol(this.from) + "/" + this.from);
         action.setContext(this.context);
         action.setExten(this.to);
         action.setCallerId(this.from);
         action.setPriority(Integer.valueOf(1));
         action.setVariables(this.callParams);
         ManagerConnection connection = ConnectionManager.getConnection();
         try {
            connection.login();
            ManagerResponse ex = connection.sendAction(action, 30000L);
            connection.logoff();
            return ex.getResponse();
         } catch (TimeoutException|IOException|AuthenticationFailedException var5) {
            logger.fatal("Error on send request to asterisk", var5);
            connection.logoff();
            return "Error: " + var5.getMessage();
         }
      } else {
         return "Authentication Failure";
      }
   }

   static {
      ignoreCallParamsNames.add("from");
      ignoreCallParamsNames.add("to");
      ignoreCallParamsNames.add("context");
      ignoreCallParamsNames.add("secret");
      ignoreCallParamsNames.add("event");
   }
}