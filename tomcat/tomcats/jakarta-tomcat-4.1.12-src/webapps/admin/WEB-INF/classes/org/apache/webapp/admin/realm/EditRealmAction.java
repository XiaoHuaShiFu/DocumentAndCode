/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/realm/EditRealmAction.java,v 1.11 2002/08/13 22:35:20 amyroh Exp $
 * $Revision: 1.11 $
 * $Date: 2002/08/13 22:35:20 $
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

package org.apache.webapp.admin.realm;

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
 * Realm </em> transactions, based on the type of Realm.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.11 $ $Date: 2002/08/13 22:35:20 $
 */

public class EditRealmAction extends Action {


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
    private HttpServletRequest request = null;

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
        this.request = request;
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
        ObjectName rname = null;
        StringBuffer sb = null;
        try {
            rname = new ObjectName(request.getParameter("select"));
        } catch (Exception e) {
            String message =
                resources.getMessage("error.realmName.bad",
                                     request.getParameter("select"));
            getServlet().log(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return (null);
        }

       String realmType = null;
       String attribute = null;

       // Find what type of Realm this is
       try {
            attribute = "className";
            String className = (String)
                mBServer.getAttribute(rname, attribute);
            int period = className.lastIndexOf(".");
            if (period >= 0)
                realmType = className.substring(period + 1);
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

        // Forward to the appropriate realm display page

        if ("UserDatabaseRealm".equalsIgnoreCase(realmType)) {
               setUpUserDatabaseRealm(rname, response);
        } else if ("MemoryRealm".equalsIgnoreCase(realmType)) {
               setUpMemoryRealm(rname, response);
        } else if ("JDBCRealm".equalsIgnoreCase(realmType)) {
               setUpJDBCRealm(rname, response);
        } else {
               setUpJNDIRealm(rname, response);
        }

        return (mapping.findForward(realmType));

    }

    private void setUpUserDatabaseRealm(ObjectName rname,
                                        HttpServletResponse response)
    throws IOException {
        // Fill in the form values for display and editing
        UserDatabaseRealmForm realmFm = new UserDatabaseRealmForm();
        session.setAttribute("userDatabaseRealmForm", realmFm);
        realmFm.setAdminAction("Edit");
        realmFm.setObjectName(rname.toString());
        String realmType = "UserDatabaseRealm";
        StringBuffer sb = new StringBuffer("");
        String host = rname.getKeyProperty("host");
        String context = rname.getKeyProperty("path");
        if (host!=null) {
            sb.append("Host (" + host + ") > ");
        }
        if (context!=null) {
            sb.append("Context (" + context + ") > ");
        }
        sb.append("Realm");
        realmFm.setNodeLabel(sb.toString());
        realmFm.setRealmType(realmType);
        realmFm.setDebugLvlVals(Lists.getDebugLevels());
        realmFm.setAllowDeletion(allowDeletion(rname));

        String attribute = null;
        try {

            // Copy scalar properties
            attribute = "debug";
            realmFm.setDebugLvl
                (((Integer) mBServer.getAttribute(rname, attribute)).toString());
            attribute = "resourceName";
            realmFm.setResource
                ((String) mBServer.getAttribute(rname, attribute));

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

    private void setUpMemoryRealm(ObjectName rname,
                                        HttpServletResponse response)
    throws IOException {
        // Fill in the form values for display and editing
        MemoryRealmForm realmFm = new MemoryRealmForm();
        session.setAttribute("memoryRealmForm", realmFm);
        realmFm.setAdminAction("Edit");
        realmFm.setObjectName(rname.toString());
        String realmType = "MemoryRealm";
        StringBuffer sb = new StringBuffer("Realm (");
        sb.append(realmType);
        sb.append(")");
        realmFm.setNodeLabel(sb.toString());
        realmFm.setRealmType(realmType);
        realmFm.setDebugLvlVals(Lists.getDebugLevels());
        realmFm.setAllowDeletion(allowDeletion(rname));

        String attribute = null;
        try {

            // Copy scalar properties
            attribute = "debug";
            realmFm.setDebugLvl
                (((Integer) mBServer.getAttribute(rname, attribute)).toString());
            attribute = "pathname";
            realmFm.setPathName
                ((String) mBServer.getAttribute(rname, attribute));

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

    private void setUpJDBCRealm(ObjectName rname,
                                        HttpServletResponse response)
    throws IOException {
        // Fill in the form values for display and editing
        JDBCRealmForm realmFm = new JDBCRealmForm();
        session.setAttribute("jdbcRealmForm", realmFm);
        realmFm.setAdminAction("Edit");
        realmFm.setObjectName(rname.toString());
        String realmType = "JDBCRealm";
        StringBuffer sb = new StringBuffer("Realm (");
        sb.append(realmType);
        sb.append(")");
        realmFm.setNodeLabel(sb.toString());
        realmFm.setRealmType(realmType);
        realmFm.setDebugLvlVals(Lists.getDebugLevels());
        realmFm.setAllowDeletion(allowDeletion(rname));

        String attribute = null;
        try {

            // Copy scalar properties
            attribute = "debug";
            realmFm.setDebugLvl
                (((Integer) mBServer.getAttribute(rname, attribute)).toString());
            attribute = "digest";
            realmFm.setDigest
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "driverName";
            realmFm.setDriver
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "roleNameCol";
            realmFm.setRoleNameCol
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "userNameCol";
            realmFm.setUserNameCol
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "userCredCol";
            realmFm.setPasswordCol
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "userTable";
            realmFm.setUserTable
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "userRoleTable";
            realmFm.setRoleTable
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "connectionName";
            realmFm.setConnectionName
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "connectionPassword";
            realmFm.setConnectionPassword
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "connectionURL";
            realmFm.setConnectionURL
                ((String) mBServer.getAttribute(rname, attribute));

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

    private void setUpJNDIRealm(ObjectName rname,
                                        HttpServletResponse response)
    throws IOException {
        // Fill in the form values for display and editing
        JNDIRealmForm realmFm = new JNDIRealmForm();
        session.setAttribute("jndiRealmForm", realmFm);
        realmFm.setAdminAction("Edit");
        realmFm.setObjectName(rname.toString());
        String realmType = "JNDIRealm";
        StringBuffer sb = new StringBuffer("Realm (");
        sb.append(realmType);
        sb.append(")");
        realmFm.setNodeLabel(sb.toString());
        realmFm.setRealmType(realmType);
        realmFm.setDebugLvlVals(Lists.getDebugLevels());
        realmFm.setSearchVals(Lists.getBooleanValues());
        realmFm.setAllowDeletion(allowDeletion(rname));

        String attribute = null;
        try {

            // Copy scalar properties
            attribute = "debug";
            realmFm.setDebugLvl
                (((Integer) mBServer.getAttribute(rname, attribute)).toString());
            attribute = "digest";
            realmFm.setDigest
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "userSubtree";
            realmFm.setUserSubtree
                    (((Boolean) mBServer.getAttribute(rname, attribute)).toString());
            attribute = "roleSubtree";
            realmFm.setRoleSubtree
                    (((Boolean) mBServer.getAttribute(rname, attribute)).toString());
            attribute = "userRoleName";
            realmFm.setUserRoleName
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "roleName";
            realmFm.setRoleName
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "roleBase";
            realmFm.setRoleBase
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "roleSearch";
            realmFm.setRolePattern
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "contextFactory";
            realmFm.setContextFactory
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "userBase";
            realmFm.setUserBase
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "userPassword";
            realmFm.setUserPassword
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "userPattern";
            realmFm.setUserPattern
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "userSearch";
            realmFm.setUserSearch
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "connectionName";
            realmFm.setConnectionName
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "connectionPassword";
            realmFm.setConnectionPassword
                ((String) mBServer.getAttribute(rname, attribute));
            attribute = "connectionURL";
            realmFm.setConnectionURL
                ((String) mBServer.getAttribute(rname, attribute));

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

    /*
     * Check if "delete this realm" operation should be enabled.
     * this operation is not allowed in case the realm is under service,
     * host or context that the admin app runs on.
     * return "true" if deletion is allowed.
     */

    private String allowDeletion(ObjectName rname) {

     boolean retVal = true;
     try{
        // admin app's values
        String adminService = Lists.getAdminAppService(
                              mBServer, rname.getDomain(),request);
        String adminHost = request.getServerName();
        String adminContext = request.getContextPath();

        String thisService = rname.getKeyProperty("service");
        String thisHost = rname.getKeyProperty("host");
        String thisContext = rname.getKeyProperty("path");

        // realm is under context
        if (thisContext!=null) {
            retVal = !(thisContext.equalsIgnoreCase(adminContext));
        } else if (thisHost != null) {
            // realm is under host
            retVal = !(thisHost.equalsIgnoreCase(adminHost));
        } else {
            // realm is under service
            retVal = !(thisService.equalsIgnoreCase(adminService));
        }

     } catch (Exception e) {
           getServlet().log("Error getting admin service, host or context", e);
     }
        return new Boolean(retVal).toString();
    }
}
