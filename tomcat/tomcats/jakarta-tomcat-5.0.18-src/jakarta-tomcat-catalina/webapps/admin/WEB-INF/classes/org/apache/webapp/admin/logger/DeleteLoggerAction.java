/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/logger/DeleteLoggerAction.java,v 1.6 2003/04/24 07:56:34 amyroh Exp $
 * $Revision: 1.6 $
 * $Date: 2003/04/24 07:56:34 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */


package org.apache.webapp.admin.logger;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.Set;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.QueryExp;
import javax.management.Query;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.JMException;
import org.apache.struts.util.MessageResources;

import org.apache.webapp.admin.ApplicationServlet;
import org.apache.webapp.admin.TomcatTreeBuilder;

/**
 * The <code>Action</code> that sets up <em>Delete Loggers</em> transactions.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.6 $ $Date: 2003/04/24 07:56:34 $
 */

public class DeleteLoggerAction extends Action {
    
    
    /**
     * The MBeanServer we will be interacting with.
     */
    private MBeanServer mBServer = null;
    
    
    /**
     * The MessageResources we will be retrieving messages from.
     */
    private MessageResources resources = null;
    
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param actionForm The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public ActionForward perform(ActionMapping mapping,
    ActionForm form,
    HttpServletRequest request,
    HttpServletResponse response)
    throws IOException, ServletException {
        
        
        // Acquire the resources that we need
        HttpSession session = request.getSession();
        Locale locale = (Locale) session.getAttribute(Action.LOCALE_KEY);
        if (resources == null) {
            resources = getServlet().getResources();
        }
        
        // Acquire a reference to the MBeanServer containing our MBeans
        try {
            mBServer = ((ApplicationServlet) getServlet()).getServer();
        } catch (Throwable t) {
            throw new ServletException
            ("Cannot acquire MBeanServer reference", t);
        }
        
        String pattern = null;
        // Set up a form bean containing the currently selected
        // objects to be deleted
        LoggersForm loggersForm = new LoggersForm();
        String select = request.getParameter("select");
        if (select != null) {
            String loggers[] = new String[1];
            loggers[0] = select;
            loggersForm.setLoggers(loggers);
            pattern = select;
        }
        request.setAttribute("loggersForm", loggersForm);
        
        // Accumulate a list of all available loggers
        ArrayList list = new ArrayList();
        String parent = request.getParameter("parent");
        
        if (parent != null) {
            try {
                pattern = getObjectName(parent, TomcatTreeBuilder.LOGGER_TYPE);
            } catch (Exception e) {
                getServlet().log
                (resources.getMessage(locale, "users.error.select"));
                response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                resources.getMessage(locale, "users.error.select"));
                return (null);
            }
        }
        
        try {
            Iterator items =
            mBServer.queryNames(new ObjectName(pattern), null).iterator();
            while (items.hasNext()) {
                list.add(items.next().toString());
            }
        } catch (Exception e) {
            getServlet().log
            (resources.getMessage(locale, "users.error.select"));
            response.sendError
            (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            resources.getMessage(locale, "users.error.select"));
            return (null);
        }
        
        Collections.sort(list);
        request.setAttribute("loggersList", list);
        
        // Forward to the list display page
        return (mapping.findForward("Loggers"));
        
    }
    
    public static String getObjectName(String parent, String MBeanType)
    throws Exception{
        
        // Form the pattern that gets the logger for this particular
        // service, host or context.
        ObjectName poname = new ObjectName(parent);
        String domain = poname.getDomain();
        StringBuffer sb = new StringBuffer(domain+MBeanType);
        String type = poname.getKeyProperty("type");
        String j2eeType = poname.getKeyProperty("j2eeType");
        String path = "";
        String host = "";
        String name = poname.getKeyProperty("name");
        if ((name != null) && (name.length() > 0)) {
            name = name.substring(2);
            int i = name.indexOf("/");
            host = name.substring(0,i);
            path = name.substring(i); 
        }
        if ("WebModule".equalsIgnoreCase(j2eeType)) { // container is context            
            sb.append(",path="+path);
            sb.append(",host="+host);
        }
        if ("Host".equalsIgnoreCase(type)) {    // container is host
            sb.append(",host=");
            sb.append(poname.getKeyProperty("host"));
        }
        if ("Service".equalsIgnoreCase(type)) {  // container is service
        }
        return sb.toString();  
    }
}
