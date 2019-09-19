/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/logger/LoggerForm.java,v 1.4 2003/04/24 07:56:34 amyroh Exp $
 * $Revision: 1.4 $
 * $Date: 2003/04/24 07:56:34 $
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

package org.apache.webapp.admin.logger;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import java.net.InetAddress;
import java.util.List;

/**
 * Form bean for the logger page.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.4 $ $Date: 2003/04/24 07:56:34 $
 */

public final class LoggerForm extends ActionForm {
    
    // ----------------------------------------------------- Instance Variables
    
    /**
     * The administrative action represented by this form.
     */
    private String adminAction = "Edit";

  /**
     * The object name of the Logger this bean refers to.
     */
    private String objectName = null;
   
    /**
     * The object name of the parent of this Logger.
     */
    private String parentObjectName = null;
   
    /**
     * The text for the logger type. 
     * Specifies if it is a FileLogger, or SysErr or SysOut Logger.
     */
    private String loggerType = null;

    /**
     * The text for the debug level.
     */
    private String debugLvl = "0";
    
    /**
     * The text for the verbosity.
     */
    private String verbosityLvl = null;
    
   /**
     * The text for the directory.
     */
    private String directory = null;
    
    /**
     * The text for the prefix.
     */
    private String prefix = null;
    
    /**
     * The text for the timestamp.
     */
    private String timestamp = null;
    
    /**
     * The text for the suffix.
     */
    private String suffix = null;
    
    /**
     * The text for the node label.
     */
    private String nodeLabel = null;
    
    private List debugLvlVals = null;
    private List verbosityLvlVals = null;
    private List booleanVals = null;
    private List loggerTypeVals = null;

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
     * Return the object name of the Logger this bean refers to.
     */
    public String getObjectName() {

        return this.objectName;

    }


    /**
     * Set the object name of the Logger this bean refers to.
     */
    public void setObjectName(String objectName) {

        this.objectName = objectName;

    }
    
    
    /**
     * Return the parent object name of the Logger this bean refers to.
     */
    public String getParentObjectName() {

        return this.parentObjectName;

    }


    /**
     * Set the parent object name of the Logger this bean refers to.
     */
    public void setParentObjectName(String parentObjectName) {

        this.parentObjectName = parentObjectName;

    }
    
    /**
     * Return the Logger type.
     */
    public String getLoggerType() {
        
        return this.loggerType;
        
    }
    
    /**
     * Set the Logger type.
     */
    public void setLoggerType(String loggerType) {
        
        this.loggerType = loggerType;
        
    }
    
    /**
     * Return the verbosityLvl.
     */
    public String getVerbosityLvl() {
        
        return this.verbosityLvl;
        
    }
    
    /**
     * Set the verbosityLvl.
     */
    public void setVerbosityLvl(String verbosityLvl) {
        
        this.verbosityLvl = verbosityLvl;
        
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
     * Return the directory.
     */
    public String getDirectory() {
        
        return this.directory;
        
    }
    
    /**
     * Set the directory.
     */
    public void setDirectory(String directory) {
        
        this.directory = directory;
        
    }
    
    /**
     * Return the prefix.
     */
    public String getPrefix() {
        
        return this.prefix;
        
    }
    
    /**
     * Set the prefix.
     */
    public void setPrefix(String prefix) {
        
        this.prefix = prefix;
        
    }
    /**
     * Return the suffix.
     */
    public String getSuffix() {
        
        return this.suffix;
        
    }
    
    /**
     * Set the suffix.
     */
    public void setSuffix(String suffix) {
        
        this.suffix = suffix;
        
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
     * Return the timestamp.
     */
    public String getTimestamp() {
        
        return this.timestamp;
        
    }
    
    /**
     * Set the timestamp.
     */
    public void setTimestamp(String timestamp) {
        
        this.timestamp = timestamp;
        
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
     * Return the verbosity level values.
     */
    public List getVerbosityLvlVals() {
        
        return this.verbosityLvlVals;
        
    }
    
    /**
     * Set the verbosity level values.
     */
    public void setVerbosityLvlVals(List verbosityLvlVals) {
        
        this.verbosityLvlVals = verbosityLvlVals;
        
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
     * Return the loggerTypeVals.
     */
    public List getLoggerTypeVals() {
        
        return this.loggerTypeVals;
        
    }
    
    /**
     * Set the loggerTypeVals.
     */
    public void setLoggerTypeVals(List loggerTypeVals) {
        
        this.loggerTypeVals = loggerTypeVals;
        
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
        this.loggerType = null;
        this.parentObjectName = null;
        this.debugLvl = "0";
        this.verbosityLvl = "0";        
        this.directory = null;
        this.prefix = null;
        this.suffix = null;
        this.timestamp = "false";
        
    }
    
    /**
     * Render this object as a String.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("LoggerForm[adminAction=");
        sb.append(adminAction);
        sb.append(",debugLvl=");
        sb.append(debugLvl);
        sb.append(",verbosityLvl=");
        sb.append(verbosityLvl);
        sb.append(",directory=");
        sb.append(directory);
        sb.append(",prefix=");
        sb.append(prefix);
        sb.append(",suffix=");
        sb.append(suffix);
        sb.append(",loggerType=");
        sb.append(loggerType);
        sb.append(",objectName=");
        sb.append(objectName);
        sb.append(",parentObjectName=");
        sb.append(parentObjectName);
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
        String type = request.getParameter("loggerType");
        
        // front end validation when save is clicked.
        // these checks should be done only if it is FileLogger. 
        // No checks needed otherwise        
        //if ((submit != null) && 
        if (("FileLogger").equalsIgnoreCase(type)) {
             
            if ((directory == null) || (directory.length() < 1)) {
                errors.add("directory",
                new ActionError("error.directory.required"));
            }
                         
            if ((prefix == null) || (prefix.length() < 1)) {
                errors.add("prefix",
                new ActionError("error.prefix.required"));
            }
                         
            if ((suffix == null) || (suffix.length() < 1)) {
                errors.add("suffix",
                new ActionError("error.suffix.required"));
            }            
        }
        
        return errors;
    }
}
