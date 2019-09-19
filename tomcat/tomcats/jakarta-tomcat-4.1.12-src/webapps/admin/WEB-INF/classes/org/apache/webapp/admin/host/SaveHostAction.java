/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/host/SaveHostAction.java,v 1.4 2002/09/09 19:57:23 amyroh Exp $
 * $Revision: 1.4 $
 * $Date: 2002/09/09 19:57:23 $
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

package org.apache.webapp.admin.host;


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



/**
 * The <code>Action</code> that completes <em>Add Host</em> and
 * <em>Edit Host</em> transactions.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.4 $ $Date: 2002/09/09 19:57:23 $
 */

public final class SaveHostAction extends Action {


    // ----------------------------------------------------- Instance Variables

    /**
     * Signature for the <code>createStandardHost</code> operation.
     */
    private String createStandardHostTypes[] =
    { "java.lang.String",     // parent
      "java.lang.String",     // name
      "java.lang.String",     // appBase
      "boolean",              // autoDeploy
      "boolean",              // deployXML
      "boolean",              // liveDeploy
      "boolean",              // unpackWARs
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
        HostForm hform = (HostForm) form;
        String adminAction = hform.getAdminAction();
        String hObjectName = hform.getObjectName();

        // Perform a "Create Host" transaction (if requested)
        if ("Create".equals(adminAction)) {

            String operation = null;
            Object values[] = null;
            
            try {
                
                String serviceName = hform.getServiceName();

                // Ensure that the requested host name is unique
                ObjectName oname =
                    new ObjectName(TomcatTreeBuilder.HOST_TYPE +
                                   ",host=" + hform.getHostName() +
                                   ",service=" + serviceName);
                if (mBServer.isRegistered(oname)) {
                    ActionErrors errors = new ActionErrors();
                    errors.add("hostName",
                               new ActionError("error.hostName.exists"));
                    saveErrors(request, errors);
                    return (new ActionForward(mapping.getInput()));
                }
                
                // Look up our MBeanFactory MBean
                ObjectName fname =
                    new ObjectName(TomcatTreeBuilder.FACTORY_TYPE);

                // Create a new StandardHost object
                values = new Object[7];
                values[0] = 
                    TomcatTreeBuilder.ENGINE_TYPE + ",service=" + serviceName;
                values[1] = hform.getHostName();
                values[2] = hform.getAppBase();
                values[3] = new Boolean(hform.getAutoDeploy());
                values[4] = new Boolean(hform.getDeployXML());
                values[5] = new Boolean(hform.getLiveDeploy());
                values[6] = new Boolean(hform.getUnpackWARs());
                
                operation = "createStandardHost";
                hObjectName = (String)
                    mBServer.invoke(fname, operation,
                                    values, createStandardHostTypes);

                // Add the new Host to our tree control node
                TreeControl control = (TreeControl)
                    session.getAttribute("treeControlTest");
                if (control != null) {
                    String parentName = 
                          TomcatTreeBuilder.SERVICE_TYPE + ",name=" + serviceName;
                    TreeControlNode parentNode = control.findNode(parentName);
                    if (parentNode != null) {
                        String nodeLabel =
                            "Host (" + hform.getHostName() + ")";
                        String encodedName =
                            URLEncoder.encode(hObjectName);
                        TreeControlNode childNode =
                            new TreeControlNode(hObjectName,
                                                "Host.gif",
                                                nodeLabel,
                                                "EditHost.do?select=" +
                                                encodedName,
                                                "content",
                                                true);
                        parentNode.addChild(childNode);
                        // FIXME - force a redisplay
                    } else {
                        getServlet().log
                            ("Cannot find parent node '" + parentName + "'");
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

            ObjectName honame = new ObjectName(hObjectName);

            attribute = "debug";
            int debug = 0;
            try {
                debug = Integer.parseInt(hform.getDebugLvl());
            } catch (Throwable t) {
                debug = 0;
            }
            mBServer.setAttribute(honame,
                                  new Attribute("debug", new Integer(debug)));

            attribute = "appBase";
            String appBase = "";
            try {
                appBase = hform.getAppBase();
            } catch (Throwable t) {
                appBase = "";
            }
            mBServer.setAttribute(honame,
                                  new Attribute("appBase", appBase));

            attribute = "autoDeploy";
            String autoDeploy = "true";
            try {
                autoDeploy = hform.getAutoDeploy();
            } catch (Throwable t) {
                autoDeploy = "true";
            }
            mBServer.setAttribute(honame,
                                  new Attribute("autoDeploy", new Boolean(autoDeploy)));

            attribute = "deployXML";
            String deployXML = "true";
            try {
                deployXML = hform.getDeployXML();
            } catch (Throwable t) {
                deployXML = "true";
            }
            mBServer.setAttribute(honame,
                                  new Attribute("deployXML", new Boolean(deployXML)));

            attribute = "liveDeploy";
            String liveDeploy = "true";
            try {
                liveDeploy = hform.getLiveDeploy();
            } catch (Throwable t) {
                liveDeploy = "true";
            }
            mBServer.setAttribute(honame,
                                  new Attribute("liveDeploy", new Boolean(liveDeploy)));

            attribute = "unpackWARs";
            String unpackWARs = "false";
            try {
                unpackWARs = hform.getUnpackWARs();
            } catch (Throwable t) {
                unpackWARs = "false";
            }
            mBServer.setAttribute(honame,
                                  new Attribute("unpackWARs", new Boolean(unpackWARs)));
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
