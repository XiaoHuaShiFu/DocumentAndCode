/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/service/ServiceForm.java,v 1.6 2002/04/26 19:05:12 manveen Exp $
 * $Revision: 1.6 $
 * $Date: 2002/04/26 19:05:12 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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


package org.apache.webapp.admin.service;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import java.util.List;

/**
 * Form bean for the service page.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.6 $ $Date: 2002/04/26 19:05:12 $
 */

public final class ServiceForm extends ActionForm {
    
    // ----------------------------------------------------- Instance Variables
    

    /**
     * The administrative action represented by this form.
     */
    private String adminAction = "Edit";


    /**
     * The object name of the Engine this bean refers to.
     */
    private String engineObjectName = null;


    /**
     * The object name of the Service this bean refers to.
     */
    private String objectName = null;


    /**
     * The text for the serviceName.
     */
    private String serviceName = null;    
    
   /**
     * The text for the node label.
    */
    private String nodeLabel = null; 
    
    /**
     * The text for the engine Name.
     */
    private String engineName = null;
    
    
    /**
     * The text for the debug level.
     */
    private String debugLvl = "0";
    
    /**
     * The name of the service the admin app runs on.
     */
    private String adminServiceName = null;    

    /**
     * The text for the defaultHost Name.
     */
    private String defaultHost = null;
    
    private List debugLvlVals = null;
    private List hostNameVals = null;


    // ------------------------------------------------------------- Properties
    

    /**
     * Return the administrative action represented by this form.
     */
    public String getAdminAction() {

        return this.adminAction;

    }


    /**
     * Set the administrative action represented by this form.
     */
    public void setAdminAction(String adminAction) {

        this.adminAction = adminAction;

    }


    /**
     * Return the object name of the Engine this bean refers to.
     */
    public String getEngineObjectName() {

        return this.engineObjectName;

    }


    /**
     * Set the object name of the Engine this bean refers to.
     */
    public void setEngineObjectName(String engineObjectName) {

        this.engineObjectName = engineObjectName;

    }


    /**
     * Return the object name of the Service this bean refers to.
     */
    public String getObjectName() {

        return this.objectName;

    }


    /**
     * Set the object name of the Service this bean refers to.
     */
    public void setObjectName(String objectName) {

        this.objectName = objectName;

    }


    /**
     * Return the label of the node that was clicked.
     */
    public String getNodeLabel() {
        
        return this.nodeLabel;
        
    }
    
    /**
     * Set the node label.
     */
    public void setNodeLabel(String nodeLabel) {
        
        this.nodeLabel = nodeLabel;
        
    }
        
    /**
     * Return the debugVals.
     */
    public List getDebugLvlVals() {
        
        return this.debugLvlVals;
        
    }
    
    /**
     * Set the debugVals.
     */
    public void setDebugLvlVals(List debugLvlVals) {
        
        this.debugLvlVals = debugLvlVals;
        
    }
    
    
    /**
     * Return the host name values.
     */
    public List getHostNameVals() {
        
        return this.hostNameVals;
        
    }
    
    /**
     * Set the hostName values.
     */
    public void setHostNameVals(List hostNameVals) {
        
        this.hostNameVals = hostNameVals;
        
    }
    
    /**
     * Set the engineName.
     */
    
    public void setEngineName(String engineName) {
        
        this.engineName = engineName;
        
    }
    
    
    /**
     * Return the engineName.
     */
    
    public String getEngineName() {
        
        return this.engineName;
        
    }
    
    /**
     * Return the Debug Level Text.
     */
    
    public String getDebugLvl() {
        
        return this.debugLvl;
        
    }
    
    /**
     * Set the Debug Level Text.
     */
    public void setDebugLvl(String debugLvl) {
        
        this.debugLvl = debugLvl;
        
    }
    
    /**
     * Return the Service Name.
     */
    public String getServiceName() {
        
        return this.serviceName;
        
    }
    
    /**
     * Set the Service Name.
     */
    public void setServiceName(String serviceName) {
        
        this.serviceName = serviceName;
        
    }

    /**
     * Return the name of the service the admin app runs on.
     */
    public String getAdminServiceName() {

        return this.adminServiceName;

    }

    /**
     * Set the name of the service the admin app runs on.
     */
    public void setAdminServiceName(String adminServiceName) {

        this.adminServiceName = adminServiceName;

    }

    /**
     * Return the default Host.
     */
    public String getDefaultHost() {
        
        return this.defaultHost;
        
    }
    
    /**
     * Set the default Host.
     */
    public void setDefaultHost(String defaultHost) {

        this.defaultHost = defaultHost;

    }


    // --------------------------------------------------------- Public Methods
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        this.engineObjectName = null;
        this.objectName = null;
        this.serviceName = null;
        this.engineName = null;
        this.adminServiceName = null;
        this.debugLvl = "0";
        this.defaultHost = null;
    }
    

    /**
     * Render this object as a String.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("ServiceForm[adminAction=");
        sb.append(adminAction);
        sb.append(",debugLvl=");
        sb.append(debugLvl);
        sb.append(",defaultHost=");
        sb.append(defaultHost);
        sb.append(",engineName=");
        sb.append(engineName);
        sb.append(",engineObjectName='");
        sb.append(engineObjectName);
        sb.append("',objectName='");
        sb.append(objectName);
        sb.append("',serviceName=");
        sb.append(serviceName);
        sb.append("',adminServiceName=");
        sb.append(adminServiceName);
        sb.append("]");
        return (sb.toString());

    }


    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public ActionErrors validate(ActionMapping mapping,
    HttpServletRequest request) {
        
        ActionErrors errors = new ActionErrors();
        String submit = request.getParameter("submit");
        
        if (submit != null) {

            if ((serviceName == null) || (serviceName.length() < 1)) {
                errors.add("serviceName",
                           new ActionError("error.serviceName.required"));
            }
            
            if ((engineName == null) || (engineName.length() < 1)) {
                errors.add("engineName",
                           new ActionError("error.engineName.required"));
            }

        }
        
        return errors;
    }
    
}
