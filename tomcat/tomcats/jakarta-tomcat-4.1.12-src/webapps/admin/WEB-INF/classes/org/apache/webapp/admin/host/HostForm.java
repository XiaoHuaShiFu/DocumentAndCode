/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/host/HostForm.java,v 1.5 2002/09/09 20:10:15 amyroh Exp $
 * $Revision: 1.5 $
 * $Date: 2002/09/09 20:10:15 $
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


package org.apache.webapp.admin.host;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import java.util.List;

/**
 * Form bean for the host page.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.5 $ $Date: 2002/09/09 20:10:15 $
 */

public final class HostForm extends ActionForm {
    
    // ----------------------------------------------------- Instance Variables
        
    /**
     * The administrative action represented by this form.
     */
    private String adminAction = "Edit";

    /**
     * The object name of the Service this bean refers to.
     */
    private String objectName = null;

    /**
     * The text for the node label. This is of the form 'Host(name)'
     * and is picked up from the node of the tree that is clicked on.
     */
    private String nodeLabel = null;
    
    /**
     * The text for the hostName.
     */
    private String hostName = null;
    
    /**
     * The name of the service this host belongs to.
     */
    private String serviceName = null;
    
    /**
     * The directory for the appBase.
     */
    private String appBase = null;
    
    /**
     * The text for the debug level.
     */
    private String debugLvl = "0";

    /**
     * Boolean for autoDeploy.
     */
    private String autoDeploy = "true";

    /**
     * Boolean for deployXML.
     */
    private String deployXML = "true";

    /**
     * Boolean for liveDeploy.
     */
    private String liveDeploy = "true";
    
    /**
     * Boolean for unpack WARs.
     */
    private String unpackWARs = "true";
    
    /**
     * The text for the port. -- TBD
     */
    private String findAliases = null;
    
    /**
     * Set of valid values for debug level.
     */
    private List debugLvlVals = null;
    
    /*
     * Represent boolean (true, false) values for unpackWARs etc.
     */
    private List booleanVals = null;
    
    /*
     * Represent aliases as a List.
     */    
    private List aliasVals = null;
   
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
     * Return the object name of the Host this bean refers to.
     */
    public String getObjectName() {

        return this.objectName;

    }


    /**
     * Set the object name of the Host this bean refers to.
     */
    public void setObjectName(String objectName) {

        this.objectName = objectName;

    }

    
    /**
     * Return the object name of the service this host belongs to.
     */
    public String getServiceName() {

        return this.serviceName;

    }


    /**
     * Set the object name of the Service this host belongs to.
     */
    public void setServiceName(String serviceName) {

        this.serviceName = serviceName;

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
     * Return the host name.
     */
    public String getHostName() {
        
        return this.hostName;
        
    }
    
    /**
     * Set the host name.
     */
    public void setHostName(String hostName) {
        
        this.hostName = hostName;
        
    }
    
    /**
     * Return the appBase.
     */
    public String getAppBase() {
        
        return this.appBase;
        
    }
    
    
    /**
     * Set the appBase.
     */
    
    public void setAppBase(String appBase) {
        
        this.appBase = appBase;
        
    }

    /**
     * Return the autoDeploy.
     */
    public String getAutoDeploy() {
        
        return this.autoDeploy;
        
    }
    
    /**
     * Set the autoDeploy.
     */
    
    public void setAutoDeploy(String autoDeploy) {
        
        this.autoDeploy = autoDeploy;
        
    }

    /**
     * Return the deployXML.
     */
    public String getDeployXML() {
        
        return this.deployXML;
        
    }
    
    /**
     * Set the deployXML.
     */
    
    public void setDeployXML(String deployXML) {
        
        this.deployXML = deployXML;
        
    }

    /**
     * Return the liveDeploy.
     */
    public String getLiveDeploy() {
        
        return this.liveDeploy;
        
    }
    
    /**
     * Set the liveDeploy.
     */
    
    public void setLiveDeploy(String liveDeploy) {
        
        this.liveDeploy = liveDeploy;
        
    }
    
    /**
     * Return the unpackWARs.
     */
    public String getUnpackWARs() {
        
        return this.unpackWARs;
        
    }
    
    /**
     * Set the unpackWARs.
     */
    
    public void setUnpackWARs(String unpackWARs) {
        
        this.unpackWARs = unpackWARs;
        
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
     * Return the booleanVals.
     */
    public List getBooleanVals() {
        
        return this.booleanVals;
        
    }
    
    /**
     * Set the booleanVals.
     */
    public void setBooleanVals(List booleanVals) {
        
        this.booleanVals = booleanVals;
        
    }
    
    /**
     * Return the List of alias Vals.
     */
    public List getAliasVals() {
        
        return this.aliasVals;
        
    }
    
    /**
     * Set the alias Vals.
     */
    public void setAliasVals(List aliasVals) {
        
        this.aliasVals = aliasVals;
        
    }
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        this.objectName = null;        
        this.serviceName = null;
        this.hostName = null;
        this.appBase = null;
        this.autoDeploy = "true";
        this.deployXML = "true";
        this.liveDeploy = "true";
        this.debugLvl = "0";
        this.unpackWARs = "true";
        
    }
    
     /**
     * Render this object as a String.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("HostForm[adminAction=");
        sb.append(adminAction);
        sb.append(",debugLvl=");
        sb.append(debugLvl);
        sb.append(",appBase=");
        sb.append(appBase);
        sb.append(",autoDeploy=");
        sb.append(autoDeploy);
        sb.append(",deployXML=");
        sb.append(deployXML);
        sb.append(",liveDeploy=");
        sb.append(liveDeploy);
        sb.append(",unpackWARs=");
        sb.append(unpackWARs);
        sb.append("',objectName='");
        sb.append(objectName);
        sb.append("',hostName=");
        sb.append(hostName);
        sb.append("',serviceName=");
        sb.append(serviceName);
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
        
        // front end validation when save is clicked.
        if (submit != null) {
            
            // hostName cannot be null
            if ((hostName== null) || (hostName.length() < 1)) {
                errors.add("hostName", new ActionError("error.hostName.required"));
            }
            
            // appBase cannot be null
            if ((appBase == null) || (appBase.length() < 1)) {
                errors.add("appBase", new ActionError("error.appBase.required"));
            }
            
        }        
        return errors;
        
    }
    
}
