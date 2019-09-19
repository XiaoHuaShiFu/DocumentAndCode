/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/realm/SaveJNDIRealmAction.java,v 1.9 2002/08/16 00:30:45 amyroh Exp $
 * $Revision: 1.9 $
 * $Date: 2002/08/16 00:30:45 $
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
 * 4. The names "The Jakarta Project", "Struts", and "Apache Software
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


import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Locale;
import java.io.IOException;
import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.QueryExp;
import javax.management.Query;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.JMException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.webapp.admin.ApplicationServlet;
import org.apache.webapp.admin.TomcatTreeBuilder;
import org.apache.webapp.admin.TreeControl;
import org.apache.webapp.admin.TreeControlNode;
import org.apache.webapp.admin.logger.DeleteLoggerAction;

/**
 * The <code>Action</code> that completes <em>Add Realm</em> and
 * <em>Edit Realm</em> transactions for JNDI realm.
 *
 * @author Manveen Kaur
 * @author Amy Roh
 * @version $Revision: 1.9 $ $Date: 2002/08/16 00:30:45 $
 */

public final class SaveJNDIRealmAction extends Action {


    // ----------------------------------------------------- Instance Variables

    /**
     * Signature for the <code>createStandardRealm</code> operation.
     */
    private String createStandardRealmTypes[] =
    { "java.lang.String",     // parent
    };


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

        // Identify the requested action
        JNDIRealmForm rform = (JNDIRealmForm) form;
        String adminAction = rform.getAdminAction();
        String rObjectName = rform.getObjectName();

        // Perform a "Create JNDI Realm" transaction (if requested)
        if ("Create".equals(adminAction)) {

            String operation = null;
            String values[] = null;

            try {

                String parent = rform.getParentObjectName();
                String objectName = DeleteLoggerAction.getObjectName(parent,
                                    TomcatTreeBuilder.REALM_TYPE);

                ObjectName pname = new ObjectName(parent);
                StringBuffer sb = new StringBuffer(pname.getDomain());

                // For service, create the corresponding Engine mBean
                // Parent in this case needs to be the container mBean for the service
                try {
                    if ("Service".equalsIgnoreCase(pname.getKeyProperty("type"))) {
                        sb.append(":type=Engine,service=");
                        sb.append(pname.getKeyProperty("name"));
                        parent = sb.toString();
                    }
                } catch (Exception e) {
                    String message =
                        resources.getMessage("error.engineName.bad",
                                         sb.toString());
                    getServlet().log(message);
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
                    return (null);
                }

                // Ensure that the requested user database name is unique
                ObjectName oname =
                    new ObjectName(objectName);
                if (mBServer.isRegistered(oname)) {
                    ActionErrors errors = new ActionErrors();
                    errors.add("realmName",
                               new ActionError("error.realmName.exists"));
                    saveErrors(request, errors);
                    return (new ActionForward(mapping.getInput()));
                }

                // Look up our MBeanFactory MBean
                ObjectName fname =
                    new ObjectName(TomcatTreeBuilder.FACTORY_TYPE);

                // Create a new StandardRealm object
                values = new String[1];
                values[0] = parent;
                operation = "createJNDIRealm";
                rObjectName = (String)
                    mBServer.invoke(fname, operation,
                                    values, createStandardRealmTypes);

                // Add the new Realm to our tree control node
                TreeControl control = (TreeControl)
                    session.getAttribute("treeControlTest");
                if (control != null) {
                    TreeControlNode parentNode = control.findNode(rform.getParentObjectName());
                    if (parentNode != null) {
                        String nodeLabel = rform.getNodeLabel();
                        String encodedName =
                            URLEncoder.encode(rObjectName);
                        TreeControlNode childNode =
                            new TreeControlNode(rObjectName,
                                                "Realm.gif",
                                                nodeLabel,
                                                "EditRealm.do?select=" +
                                                encodedName,
                                                "content",
                                                true);
                        parentNode.addChild(childNode);
                        // FIXME - force a redisplay
                    } else {
                        getServlet().log
                            ("Cannot find parent node '" + parent + "'");
                    }
                } else {
                    getServlet().log
                        ("Cannot find TreeControlNode!");
                }

            } catch (Exception e) {

                getServlet().log
                    (resources.getMessage(locale, "users.error.invoke",
                                          operation), e);
                response.sendError
                    (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                     resources.getMessage(locale, "users.error.invoke",
                                          operation));
                return (null);

            }

        }

        // Perform attribute updates as requested
        String attribute = null;
        try {

            ObjectName roname = new ObjectName(rObjectName);

            attribute = "connectionName";
            String connectionName = rform.getConnectionName();
            if ((connectionName != null) && (connectionName.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("connectionName",  connectionName));
            }

            attribute = "connectionPassword";
            String connectionPassword = rform.getConnectionPassword();
            if ((connectionPassword != null) && (connectionPassword.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("connectionPassword",  connectionPassword));
            }

            attribute = "connectionURL";
            String connectionURL = rform.getConnectionURL();
            if ((connectionURL != null) && (connectionURL.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("connectionURL",  connectionURL));
            }

            attribute = "contextFactory";
            String contextFactory = rform.getContextFactory();
            if ((contextFactory != null) && (contextFactory.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("contextFactory",  contextFactory));
            }

            attribute = "debug";
            int debug = 0;
            try {
                debug = Integer.parseInt(rform.getDebugLvl());
            } catch (Throwable t) {
                debug = 0;
            }
            mBServer.setAttribute(roname,
                            new Attribute("debug", new Integer(debug)));

            attribute = "digest";
            String digest = rform.getDigest();
            if ((digest != null) && (digest.length()>0)) {
                mBServer.setAttribute(roname,
                                        new Attribute("digest", digest));
            }

            attribute = "roleBase";
            String roleBase = rform.getRoleBase();
            if ((roleBase != null) && (roleBase.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("roleBase",  roleBase));
            }

            attribute = "roleName";
            String roleName = rform.getRoleName();
            if ((roleName != null) && (roleName.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("roleName",  roleName));
            }

            attribute = "rolePattern";
            String rolePattern = rform.getRolePattern();
            if ((rolePattern != null) && (rolePattern.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("rolePattern",  rolePattern));
            }

            attribute = "roleSubtree";
            String roleSubtree = rform.getRoleSubtree();
            if ((roleSubtree != null) && (roleSubtree.length()>0)) {
                mBServer.setAttribute(roname,
                    new Attribute("roleSubtree",  new Boolean(roleSubtree)));
            }

            attribute = "userBase";
            String userBase = rform.getUserBase();
            if ((userBase != null) && (userBase.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("userBase",  userBase));
            }

            attribute = "userPassword";
            String userPassword = rform.getUserPassword();
            if ((userPassword != null) && (userPassword.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("userPassword",  userPassword));
            }

            attribute = "userPattern";
            String userPattern = rform.getUserPattern();
            if ((userPattern != null) && (userPattern.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("userPattern",  userPattern));
            }

            attribute = "userRoleName";
            String userRoleName = rform.getUserRoleName();
            if ((userRoleName != null) && (userRoleName.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("userRoleName",  userRoleName));
            }

            attribute = "userSearch";
            String userSearch = rform.getUserSearch();
            if ((userSearch != null) && (userSearch.length()>0)) {
                mBServer.setAttribute(roname,
                        new Attribute("userSearch",  userSearch));
            }

            attribute = "userSubtree";
            String userSubtree = rform.getUserSubtree();
            if ((userSubtree != null) && (userSubtree.length()>0)) {
                mBServer.setAttribute(roname,
                    new Attribute("userSubtree",  new Boolean(userSubtree)));
            }

        } catch (Exception e) {

            getServlet().log
                (resources.getMessage(locale, "users.error.attribute.set",
                                      attribute), e);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 resources.getMessage(locale, "users.error.attribute.set",
                                      attribute));
            return (null);
        }

        // Forward to the success reporting page
        session.removeAttribute(mapping.getAttribute());
        return (mapping.findForward("Save Successful"));

    }

}
