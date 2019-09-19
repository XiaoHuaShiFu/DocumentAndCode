/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/connector/SaveConnectorAction.java,v 1.11 2002/05/16 20:05:50 amyroh Exp $
 * $Revision: 1.11 $
 * $Date: 2002/05/16 20:05:50 $
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

package org.apache.webapp.admin.connector;

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
 * The <code>Action</code> that completes <em>Add Connector</em> and
 * <em>Edit Connector</em> transactions.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.11 $ $Date: 2002/05/16 20:05:50 $
 */

public final class SaveConnectorAction extends Action {


    // ----------------------------------------------------- Instance Variables

    /**
     * Signature for the <code>createStandardConnector</code> operation.
     */
    private String createStandardConnectorTypes[] =
    { "java.lang.String",    // parent
      "java.lang.String",    // address
      "int"                  // port      
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
        ConnectorForm cform = (ConnectorForm) form;
        String adminAction = cform.getAdminAction();
        String cObjectName = cform.getObjectName();
        String connectorType = cform.getConnectorType();

        // Perform a "Create Connector" transaction (if requested)
        if ("Create".equals(adminAction)) {

            String operation = null;
            Object values[] = null;

            try {
   
                String serviceName = cform.getServiceName();
                
                ObjectName oname =
                    new ObjectName(TomcatTreeBuilder.CONNECTOR_TYPE +
                                   ",service=" + serviceName +
                                   ",port=" + cform.getPortText() +
                                   ",address=" + cform.getAddress());
                                                
                // Ensure that the requested connector name is unique
                if (mBServer.isRegistered(oname)) {
                    ActionErrors errors = new ActionErrors();
                    errors.add("connectorName",
                               new ActionError("error.connectorName.exists"));
                    saveErrors(request, errors);
                    return (new ActionForward(mapping.getInput()));
                }

                // Look up our MBeanFactory MBean
                ObjectName fname =
                    new ObjectName(TomcatTreeBuilder.FACTORY_TYPE);

                // Create a new Connector object
                values = new Object[3];                
                values[0] = // parent 
                    TomcatTreeBuilder.SERVICE_TYPE + ",name=" + serviceName;
                values[1] = cform.getAddress();
                values[2] = new Integer(cform.getPortText());

                if ("HTTP".equalsIgnoreCase(connectorType)) {
                        operation = "createHttpConnector"; // HTTP
                } else if ("HTTPS".equalsIgnoreCase(connectorType)) { 
                        operation = "createHttpsConnector";   // HTTPS
                } else {
                        operation = "createAjpConnector";   // AJP(HTTP)                  
                }
                
                cObjectName = (String)
                    mBServer.invoke(fname, operation,
                                    values, createStandardConnectorTypes);
                
                // Add the new Connector to our tree control node
                TreeControl control = (TreeControl)
                    session.getAttribute("treeControlTest");
                if (control != null) {
                    String parentName = 
                          TomcatTreeBuilder.SERVICE_TYPE + ",name=" + serviceName;
                    TreeControlNode parentNode = control.findNode(parentName);
                    if (parentNode != null) {
                        String nodeLabel =
                           "Connector (" + cform.getPortText() + ")";
                        String encodedName =
                            URLEncoder.encode(cObjectName);
                        TreeControlNode childNode =
                            new TreeControlNode(cObjectName,
                                                "Connector.gif",
                                                nodeLabel,
                                                "EditConnector.do?select=" +
                                                encodedName,
                                                "content",
                                                true);
                        // FIXME--the node should be next to the rest of 
                        // the Connector nodes..
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

            ObjectName coname = new ObjectName(cObjectName);

            attribute = "debug";
            int debug = 0;
            try {
                debug = Integer.parseInt(cform.getDebugLvl());
            } catch (Throwable t) {
                debug = 0;
            }
            mBServer.setAttribute(coname,
                                  new Attribute("debug", new Integer(debug)));
            attribute = "acceptCount";
            int acceptCount = 60000;
            try {
                acceptCount = Integer.parseInt(cform.getAcceptCountText());
            } catch (Throwable t) {
                acceptCount = 60000;
            }
            mBServer.setAttribute(coname,
                                  new Attribute("acceptCount", new Integer(acceptCount)));            
            attribute = "connectionTimeout";
            int connectionTimeout = 0;
            try {
                connectionTimeout = Integer.parseInt(cform.getConnTimeOutText());
            } catch (Throwable t) {
                connectionTimeout = 0;
            }
            mBServer.setAttribute(coname,
                                  new Attribute("connectionTimeout", new Integer(connectionTimeout)));            
             attribute = "bufferSize";
            int bufferSize = 2048;
            try {
                bufferSize = Integer.parseInt(cform.getBufferSizeText());
            } catch (Throwable t) {
                bufferSize = 2048;
            }
            mBServer.setAttribute(coname,
                                  new Attribute("bufferSize", new Integer(bufferSize)));            
            attribute = "enableLookups";
            mBServer.setAttribute(coname,
                                  new Attribute("enableLookups", new Boolean(cform.getEnableLookups())));                        

            attribute = "redirectPort";
            int redirectPort = 0;
            try {
                redirectPort = Integer.parseInt(cform.getRedirectPortText());
            } catch (Throwable t) {
                redirectPort = 0;
            }
            mBServer.setAttribute(coname,
                                  new Attribute("redirectPort", new Integer(redirectPort))); 
            attribute = "minProcessors";
            int minProcessors = 5;
            try {
                minProcessors = Integer.parseInt(cform.getMinProcessorsText());
            } catch (Throwable t) {
                minProcessors = 5;
            }
            mBServer.setAttribute(coname,
                                  new Attribute("minProcessors", new Integer(minProcessors))); 
            attribute = "maxProcessors";
            int maxProcessors = 20;
            try {
                maxProcessors = Integer.parseInt(cform.getMaxProcessorsText());
            } catch (Throwable t) {
                maxProcessors = 20;
            }
            mBServer.setAttribute(coname,
                                  new Attribute("maxProcessors", new Integer(maxProcessors))); 
      
            // proxy name and port do not exist for AJP connector
            if (!("AJP".equalsIgnoreCase(connectorType))) {
                attribute = "proxyName";  
                String proxyName = cform.getProxyName();
                if ((proxyName != null) && (proxyName.length()>0)) { 
                    mBServer.setAttribute(coname,
                                  new Attribute("proxyName", proxyName));
                }
                
                attribute = "proxyPort";
                int proxyPort = 0;
                try {
                    proxyPort = Integer.parseInt(cform.getProxyPortText());
                } catch (Throwable t) {
                    proxyPort = 0;
                }
                mBServer.setAttribute(coname,
                              new Attribute("proxyPort", new Integer(proxyPort))); 
            }
            
            // HTTPS specific properties
            if("HTTPS".equalsIgnoreCase(connectorType)) {
                attribute = "clientAuth";              
                mBServer.setAttribute(coname,
                              new Attribute("clientAuth", new Boolean(
                                             cform.getClientAuthentication())));            
                
                attribute = "keystoreFile";
                String keyFile = cform.getKeyStoreFileName();
                if ((keyFile != null) && (keyFile.length()>0)) 
                    mBServer.setAttribute(coname,
                              new Attribute("keystoreFile", keyFile));            
                
                attribute = "keystorePass";
                String keyPass = cform.getKeyStorePassword();
                if ((keyPass != null) && (keyPass.length()>0)) 
                    mBServer.setAttribute(coname,
                              new Attribute("keystorePass", keyPass));                 
                // request.setAttribute("warning", "connector.keyPass.warning");               
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
