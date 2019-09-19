/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/valve/AccessLogValveForm.java,v 1.2 2002/04/11 14:47:47 manveen Exp $
 * $Revision: 1.2 $
 * $Date: 2002/04/11 14:47:47 $
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

package org.apache.webapp.admin.valve;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import java.util.List;

/**
 * Form bean for the accesslog valve page.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.2 $ $Date: 2002/04/11 14:47:47 $
 */

public final class AccessLogValveForm extends ValveForm {
    
    // ----------------------------------------------------- Instance Variables
    /**
     * The text for the debug level.
     */
    private String debugLvl = "0";
  
    /**
     * Set of valid values for debug level.
     */
    private List debugLvlVals = null;
    
    /**
     * The text for the directory.
     */
    private String directory = null;
    
    /**
     * The text for the pattern.
     */
    private String pattern = null;
        
    /**
     * The text for the prefix.
     */
    private String prefix = null;
    
    /**
     * The text for the suffix.
     */
    private String suffix = null;
      
    /**
     * The text for the connection URL.
     */
    private String resolveHosts = "false";
       
    /**
     * Set of boolean values.
     */
    private List booleanVals = null;
 
    // ------------------------------------------------------------- Properties

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
     * Return the pattern.
     */
    public String getPattern() {
        
        return this.pattern;
        
    }
    
    /**
     * Set the pattern.
     */
    public void setPattern(String pattern) {
        
        this.pattern = pattern;
        
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
     * Return the resolve hosts.
     */
    public String getResolveHosts() {
        
        return this.resolveHosts;
        
    }
    
    /**
     * Set the resolveHosts.
     */
    public void setResolveHosts(String resolveHosts) {
        
        this.resolveHosts = resolveHosts;
        
    }
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
    
        super.reset(mapping, request);
        this.debugLvl = "0";
        
        this.directory = null;
        this.prefix = null;
        this.suffix = null;
        this.pattern = null;        
        this.resolveHosts = "false";
        
    }
    
    /**
     * Render this object as a String.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("AccessLogValveForm[adminAction=");
        sb.append(getAdminAction());
        sb.append("',valveType=");
        sb.append(getValveType());
        sb.append(",debugLvl=");
        sb.append(debugLvl);
        sb.append(",directory=");
        sb.append(directory);
        sb.append("',prefix='");
        sb.append(prefix);
        sb.append("',pattern=");
        sb.append(pattern);
        sb.append(",resolveHosts=");
        sb.append(resolveHosts);
        sb.append("',objectName='");
        sb.append(getObjectName());
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
            
             // if not specified, default is access_log.
             // to specify no prefix, specify a 0 length string...
            if ((prefix == null) || (prefix.length() == 0)){
                prefix = "access_log.";
            }
            
            // default is a 0 length string
            if ((suffix == null) || (suffix.length() < 1)) {
                suffix = "";
            }
                                    
            // If no directory attribute is specified, the default
            // value is "logs".
            if ((directory == null) || (directory.length() < 1)) {
                directory = "logs";
            }

            if ((pattern == null) || (pattern.length() < 1)) {
                errors.add("pattern",
                new ActionError("error.pattern.required"));
            }         
        }
                 
        return errors;
    }
}
