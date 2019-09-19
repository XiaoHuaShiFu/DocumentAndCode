/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/valve/EditValveAction.java,v 1.4 2002/05/21 00:36:43 manveen Exp $
 * $Revision: 1.4 $
 * $Date: 2002/05/21 00:36:43 $
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

package org.apache.webapp.admin.valve;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
import org.apache.struts.util.MessageResources;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.JMException;

import org.apache.webapp.admin.ApplicationServlet;
import org.apache.webapp.admin.LabelValueBean;
import org.apache.webapp.admin.Lists;
import org.apache.webapp.admin.TomcatTreeBuilder;

/**
 * A generic <code>Action</code> that sets up <em>Edit 
 * Valve </em> transactions, based on the type of Valve.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.4 $ $Date: 2002/05/21 00:36:43 $
 */

public class EditValveAction extends Action {
    

    /**
     * The MBeanServer we will be interacting with.
     */
    private MBeanServer mBServer = null;
    

    /**
     * The MessageResources we will be retrieving messages from.
     */
    private MessageResources resources = null;
    
    private HttpSession session = null;
    private Locale locale = null;
    private String parent = null;
    
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
        session = request.getSession();
        locale = (Locale) session.getAttribute(Action.LOCALE_KEY);
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
        
        // Set up the object names of the MBeans we are manipulating
        ObjectName vname = null;
        StringBuffer sb = null;
        try {
            vname = new ObjectName(request.getParameter("select"));
        } catch (Exception e) {
            String message =
                resources.getMessage("error.valveName.bad",
                                     request.getParameter("select"));
            getServlet().log(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return (null);
        }
        
       parent = request.getParameter("parent");
       String valveType = null;
       String attribute = null;
       
       // Find what type of Valve this is
       try {    
            attribute = "className";
            String className = (String) 
                mBServer.getAttribute(vname, attribute);
            int period = className.lastIndexOf(".");
            if (period >= 0)
                valveType = className.substring(period + 1);
        } catch (Throwable t) {
          getServlet().log
                (resources.getMessage(locale, "users.error.attribute.get",
                                      attribute), t);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 resources.getMessage(locale, "users.error.attribute.get",
                                      attribute));
            return (null); 
        }

        // Forward to the appropriate valve display page        
        if ("AccessLogValve".equalsIgnoreCase(valveType)) {
               setUpAccessLogValve(vname, response);
        } else if ("RemoteAddrValve".equalsIgnoreCase(valveType)) {
               setUpRemoteAddrValve(vname, response);
        } else if ("RemoteHostValve".equalsIgnoreCase(valveType)) {
                setUpRemoteHostValve(vname, response);
        } else if ("RequestDumperValve".equalsIgnoreCase(valveType)) {
               setUpRequestDumperValve(vname, response);
        } else if ("SingleSignOn".equalsIgnoreCase(valveType)) {
               setUpSingleSignOnValve(vname, response);
        }
       
        
        return (mapping.findForward(valveType));
                
    }

    private void setUpAccessLogValve(ObjectName vname,
                                        HttpServletResponse response) 
    throws IOException {
        // Fill in the form values for display and editing
        AccessLogValveForm valveFm = new AccessLogValveForm();
        session.setAttribute("accessLogValveForm", valveFm);
        valveFm.setAdminAction("Edit");
        valveFm.setObjectName(vname.toString()); 
        valveFm.setParentObjectName(parent);
        String valveType = "AccessLogValve";
        StringBuffer sb = new StringBuffer("");
        String host = vname.getKeyProperty("host");
        String context = vname.getKeyProperty("path");        
        if (host!=null) {
            sb.append("Host (" + host + ") > ");
        }
        if (context!=null) {
            sb.append("Context (" + context + ") > ");
        }
        sb.append("Valve");
        valveFm.setNodeLabel(sb.toString());
        valveFm.setValveType(valveType);
        valveFm.setDebugLvlVals(Lists.getDebugLevels());
        valveFm.setBooleanVals(Lists.getBooleanValues());
        String attribute = null;
        try {
            
            // Copy scalar properties
            attribute = "debug";
            valveFm.setDebugLvl
                (((Integer) mBServer.getAttribute(vname, attribute)).toString());
            attribute = "directory";
            valveFm.setDirectory
                ((String) mBServer.getAttribute(vname, attribute));
            attribute = "pattern";
            valveFm.setPattern
                ((String) mBServer.getAttribute(vname, attribute));
            attribute = "prefix";
            valveFm.setPrefix
                ((String) mBServer.getAttribute(vname, attribute));
            attribute = "suffix";
            valveFm.setSuffix
                ((String) mBServer.getAttribute(vname, attribute));
            attribute = "resolveHosts";
            valveFm.setResolveHosts
                (((Boolean) mBServer.getAttribute(vname, attribute)).toString());

        } catch (Throwable t) {
            getServlet().log
                (resources.getMessage(locale, "users.error.attribute.get",
                                      attribute), t);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 resources.getMessage(locale, "users.error.attribute.get",
                                      attribute));
        }     
    }

    private void setUpRequestDumperValve(ObjectName vname,
                                        HttpServletResponse response) 
    throws IOException {
        // Fill in the form values for display and editing
        RequestDumperValveForm valveFm = new RequestDumperValveForm();
        session.setAttribute("requestDumperValveForm", valveFm);
        valveFm.setAdminAction("Edit");
        valveFm.setObjectName(vname.toString()); 
        valveFm.setParentObjectName(parent);
        String valveType = "RequestDumperValve";
        StringBuffer sb = new StringBuffer("Valve (");
        sb.append(valveType);
        sb.append(")");
        valveFm.setNodeLabel(sb.toString());
        valveFm.setValveType(valveType);
    }

    private void setUpSingleSignOnValve(ObjectName vname,
                                        HttpServletResponse response) 
    throws IOException {
        // Fill in the form values for display and editing
        SingleSignOnValveForm valveFm = new SingleSignOnValveForm();
        session.setAttribute("singleSignOnValveForm", valveFm);
        valveFm.setAdminAction("Edit");
        valveFm.setObjectName(vname.toString()); 
        valveFm.setParentObjectName(parent);
        String valveType = "SingleSignOn";
        StringBuffer sb = new StringBuffer("Valve (");
        sb.append(valveType);
        sb.append(")");
        valveFm.setNodeLabel(sb.toString());
        valveFm.setValveType(valveType);
        valveFm.setDebugLvlVals(Lists.getDebugLevels());
        String attribute = null;
        try {
            
            // Copy scalar properties
            attribute = "debug";
            valveFm.setDebugLvl
                (((Integer) mBServer.getAttribute(vname, attribute)).toString());
        } catch (Throwable t) {
            getServlet().log
                (resources.getMessage(locale, "users.error.attribute.get",
                                      attribute), t);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 resources.getMessage(locale, "users.error.attribute.get",
                                      attribute));
        }     
    }


    private void setUpRemoteAddrValve(ObjectName vname,
                                        HttpServletResponse response) 
    throws IOException {
        // Fill in the form values for display and editing
        RemoteAddrValveForm valveFm = new RemoteAddrValveForm();
        session.setAttribute("remoteAddrValveForm", valveFm);
        valveFm.setAdminAction("Edit");
        valveFm.setObjectName(vname.toString()); 
        valveFm.setParentObjectName(parent);
        String valveType = "RemoteAddrValve";
        StringBuffer sb = new StringBuffer("Valve (");
        sb.append(valveType);
        sb.append(")");
        valveFm.setNodeLabel(sb.toString());
        valveFm.setValveType(valveType);
        String attribute = null;
        try {
            
            // Copy scalar properties
            attribute = "allow";
            valveFm.setAllow
                ((String) mBServer.getAttribute(vname, attribute));
            attribute = "deny";
            valveFm.setDeny
                ((String) mBServer.getAttribute(vname, attribute));
                        
        } catch (Throwable t) {
            getServlet().log
                (resources.getMessage(locale, "users.error.attribute.get",
                                      attribute), t);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 resources.getMessage(locale, "users.error.attribute.get",
                                      attribute));
        }     
    }

    private void setUpRemoteHostValve(ObjectName vname,
                                        HttpServletResponse response) 
    throws IOException {
        // Fill in the form values for display and editing
        RemoteHostValveForm valveFm = new RemoteHostValveForm();
        session.setAttribute("remoteHostValveForm", valveFm);
        valveFm.setAdminAction("Edit");
        valveFm.setObjectName(vname.toString()); 
        valveFm.setParentObjectName(parent);
        String valveType = "RemoteHostValve";
        StringBuffer sb = new StringBuffer("Valve (");
        sb.append(valveType);
        sb.append(")");
        valveFm.setNodeLabel(sb.toString());
        valveFm.setValveType(valveType);
        String attribute = null;
        try {
            
            // Copy scalar properties
            attribute = "allow";
            valveFm.setAllow
                ((String) mBServer.getAttribute(vname, attribute));
            attribute = "deny";
            valveFm.setDeny
                ((String) mBServer.getAttribute(vname, attribute));
                        
        } catch (Throwable t) {
            getServlet().log
                (resources.getMessage(locale, "users.error.attribute.get",
                                      attribute), t);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 resources.getMessage(locale, "users.error.attribute.get",
                                      attribute));
        }     
    }
    
}
