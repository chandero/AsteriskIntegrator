// Source File Name:   RootServlet.java

package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class RootServlet extends HttpServlet
{

    public RootServlet()
    {
        rootText = " Advanced Asterisk Integrator for CRM \n\nAdvanced Asterisk Integrator supports Asterisk.\n\n    Supports Asterisk queues and ring groups\n    Allows outgoing calls through Asterisk dialplan\n    Detects connected line for incoming calls \n\nCopyright \251 2019 \u200BAlexander Cruz";
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.println(rootText);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        processRequest(request, response);
    }

    private String rootText;
}
