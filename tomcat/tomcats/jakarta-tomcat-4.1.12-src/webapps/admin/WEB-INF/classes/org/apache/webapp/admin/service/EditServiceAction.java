/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/service/EditServiceAction.java,v 1.5 2002/04/27 01:05:03 manveen Exp $
 * $Revision: 1.5 $
 * $Date: 2002/04/27 01:05:03 $
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

package org.apache.webapp.admin.service;

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
 * The <code>Action</code> that sets up <em>Edit Service</em> transactions.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.5 $ $Date: 2002/04/27 01:05:03 $
 */

public class EditServiceAction extends Action {
    

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
        
        // Set up the object names of the MBeans we are manipulating
        ObjectName sname = null;
        ObjectName ename = null;
        StringBuffer sb = null;
        try {
            sname = new ObjectName(request.getParameter("select"));
        } catch (Exception e) {
            String message =
                resources.getMessage("error.serviceName.bad",
                                     request.getParameter("select"));
            getServlet().log(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return (null);
        }
        try {
            sb = new StringBuffer(sname.getDomain());
            sb.append(":type=Engine,service=");
            sb.append(sname.getKeyProperty("name"));
            ename = new ObjectName(sb.toString());
        } catch (Exception e) {
            String message =
                resources.getMessage("error.engineName.bad",
                                     sb.toString());
            getServlet().log(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return (null);
        }

        String adminService = null;
        // Get the service name the admin app runs on
        // this service cannot be deleted from the admin tool
        try {
            adminService = Lists.getAdminAppService(
                                  mBServer, sname.getDomain(),request);
         } catch (Exception e) {
            String message =
                resources.getMessage("error.serviceName.bad",
                                 adminService);
            getServlet().log(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return (null);
        }

        
        // Fill in the form values for display and editing
        ServiceForm serviceFm = new ServiceForm();
        session.setAttribute("serviceForm", serviceFm);
        serviceFm.setAdminAction("Edit");
        serviceFm.setObjectName(sname.toString());
        serviceFm.setEngineObjectName(ename.toString());
        sb = new StringBuffer("Service (");
        sb.append(sname.getKeyProperty("name"));
        sb.append(")");
        serviceFm.setNodeLabel(sb.toString());
        serviceFm.setAdminServiceName(adminService);
        serviceFm.setDebugLvlVals(Lists.getDebugLevels());
        String attribute = null;
        try {

            // Copy scalar properties
            attribute = "name";
            serviceFm.setServiceName
                ((String) mBServer.getAttribute(sname, attribute));
            attribute = "name";
            serviceFm.setEngineName
                ((String) mBServer.getAttribute(ename, attribute));
            attribute = "debug";
            serviceFm.setDebugLvl
                (((Integer) mBServer.getAttribute(ename, attribute)).toString());
            attribute = "defaultHost";
            serviceFm.setDefaultHost
                ((String) mBServer.getAttribute(ename, attribute));

            // Build the list of available hosts
            attribute = "hosts";
            ArrayList hosts = new ArrayList();
            hosts.add(new LabelValueBean
                      (resources.getMessage("list.none"), ""));
            Iterator items = Lists.getHosts(mBServer, sname).iterator();
            while (items.hasNext()) {
                ObjectName hname = new ObjectName((String) items.next());
                String name = hname.getKeyProperty("host");
                if (name!=null)
                    hosts.add(new LabelValueBean(name, name));
            }
            serviceFm.setHostNameVals(hosts);

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
        
        // Forward to the service display page
        return (mapping.findForward("Service"));
        
    }


}
