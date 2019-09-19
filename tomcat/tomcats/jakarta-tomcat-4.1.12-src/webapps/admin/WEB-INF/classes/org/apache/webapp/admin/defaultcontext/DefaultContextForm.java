/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/defaultcontext/DefaultContextForm.java,v 1.1 2002/09/11 14:20:00 amyroh Exp $
 * $Revision: 1.1 $
 * $Date: 2002/09/11 14:20:00 $
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


package org.apache.webapp.admin.defaultcontext;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import java.net.InetAddress;
import java.util.List;

/**
 * Form bean for the default context page.
 *
 * @author Amy Roh
 * @version $Revision: 1.1 $ $Date: 2002/09/11 14:20:00 $
 */

public final class DefaultContextForm extends ActionForm {
    
    // ----------------------------------------------------- Instance Variables
    
   /**
     * The administrative action represented by this form.
     */
    private String adminAction = "Edit";

    /**
     * The object name of the DefaultContext this bean refers to.
     */
    private String objectName = null;
   
    /**
     * The object name of the parent of this DefaultContext.
     */
    private String parentObjectName = null;
   
    /**
     * The object name of the loader of this DefaultContext.
     */
    private String loaderObjectName = null;
   
    /**
     * The object name of the manager of this DefaultContext.
     */
    private String managerObjectName = null;
   
    /**
     * The text for the node label.
     */
    private String nodeLabel = null;
    
    /**
     * The value of cookies.
     */
    private String cookies = "true";
    
    /**
     * The value of cross context.
     */
    private String crossContext = "true";
    
    /**
     * The text for reloadable boolean.
     */
    private String reloadable = "false";

    /**
     * The text for swallowOutput boolean.
     */
    private String swallowOutput = "false";

    /**
     * The text for use naming boolean.
     */
    private String useNaming = "true";
    
    /**
     * The text for the loader check interval.
     */
    private String ldrCheckInterval = "15";
    
    /**
     * The text for the loader Debug level.
     */
    private String ldrDebugLvl = "0";
    
    /**
     * The text for the boolean value of loader reloadable.
     */
    private String ldrReloadable = "false";
    
    /**
     * The text for the session manager check interval.
     */
    private String mgrCheckInterval = "60";
    
    /**
     * The text for the session manager Debug level.
     */
    private String mgrDebugLvl = "0";
    
    /**
     * The text for the session mgr session ID initializer.
     */
    private String mgrSessionIDInit = "";
    
    /**
     * The text for the session mgr max active sessions.
     */
    private String mgrMaxSessions = "0";
    
    /**
     * Set of valid values for debug level.
     */
    private List debugLvlVals = null;
    
    /*
     * Represent boolean (true, false) values for cookies etc.
     */
    private List booleanVals = null;
    
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
     * Return the object name of the DefaultContext this bean refers to.
     */
    public String getObjectName() {

        return this.objectName;

    }

    /**
     * Set the object name of the DefaultContext this bean refers to.
     */
    public void setObjectName(String objectName) {

        this.objectName = objectName;

    }    
    
    /**
     * Return the parent object name of the DefaultContext this bean refers to.
     */
    public String getParentObjectName() {

        return this.parentObjectName;

    }

    /**
     * Set the parent object name of the DefaultContext this bean refers to.
     */
    public void setParentObjectName(String parentObjectName) {

        this.parentObjectName = parentObjectName;

    }
    
      /**
     * Return the loader object name of the DefaultContext this bean refers to.
     */
    public String getLoaderObjectName() {

        return this.loaderObjectName;

    }

    /**
     * Set the loader object name of the DefaultContext this bean refers to.
     */
    public void setLoaderObjectName(String loaderObjectName) {

        this.loaderObjectName = loaderObjectName;

    }
    
      /**
     * Return the manager object name of the DefaultContext this bean refers to.
     */
    public String getManagerObjectName() {

        return this.managerObjectName;

    }

    /**
     * Set the manager object name of the DefaultContext this bean refers to.
     */
    public void setManagerObjectName(String managerObjectName) {

        this.managerObjectName = managerObjectName;

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
     * Return the booleanVals.
     */
    public List getBooleanVals() {
        
        return this.booleanVals;
        
    }
    
    /**
     * Set the debugVals.
     */
    public void setBooleanVals(List booleanVals) {
        
        this.booleanVals = booleanVals;
        
    }
    
    
    /**
     * Return the Cookies.
     */
    
    public String getCookies() {
        
        return this.cookies;
        
    }
    
    /**
     * Set the Cookies.
     */
    public void setCookies(String cookies) {
        
        this.cookies = cookies;
        
    }
    
    /**
     * Return the Cross Context.
     */
    
    public String getCrossContext() {
        
        return this.crossContext;
        
    }
    
    /**
     * Set the Cross Context.
     */
    public void setCrossContext(String crossContext) {
        
        this.crossContext = crossContext;
        
    }
        
    /**
     * Return the reloadable boolean value.
     */

    public String getReloadable() {
        
        return this.reloadable;

    }

    /**
     * Set the reloadable value.
     */
    public void setReloadable(String reloadable) {

        this.reloadable = reloadable;

    }

    /**
     * Return the swallowOutput boolean value.
     */

    public String getSwallowOutput() {
        
        return this.swallowOutput;

    }

    /**
     * Set the swallowOutput value.
     */
    public void setSwallowOutput(String swallowOutput) {

        this.swallowOutput = swallowOutput;

    }

    /**
     * Return the use naming boolean value.
     */
    
    public String getUseNaming() {
        
        return this.useNaming;
        
    }
    
    /**
     * Set the useNaming value.
     */
    public void setUseNaming(String useNaming) {
        
        this.useNaming = useNaming;
        
    }
    
    /**
     * Return the loader check interval.
     */
    public String getLdrCheckInterval() {
        
        return this.ldrCheckInterval;
        
    }
    
    /**
     * Set the loader Check Interval.
     */
    public void setLdrCheckInterval(String ldrCheckInterval) {
        
        this.ldrCheckInterval = ldrCheckInterval;
        
    }
    
    /**
     * Return the Loader Debug Level Text.
     */
    
    public String getLdrDebugLvl() {
        
        return this.ldrDebugLvl;
        
    }
    
    /**
     * Set the Loader Debug Level Text.
     */
    public void setLdrDebugLvl(String ldrDebugLvl) {
        
        this.ldrDebugLvl = ldrDebugLvl;
        
    }
    
    /**
     * Return the loader reloadable boolean value.
     */
    public String getLdrReloadable() {
        
        return this.ldrReloadable;
        
    }
    
    /**
     * Set the loader reloadable value.
     */
    public void setLdrReloadable(String ldrReloadable) {
        
        this.ldrReloadable = ldrReloadable;
        
    }
    
    /**
     * Return the session manager check interval.
     */
    public String getMgrCheckInterval() {
        
        return this.mgrCheckInterval;
        
    }
    
    /**
     * Set the session manager Check Interval.
     */
    public void setMgrCheckInterval(String mgrCheckInterval) {
        
        this.mgrCheckInterval = mgrCheckInterval;
        
    }
    
    /**
     * Return the session mgr Debug Level Text.
     */
    
    public String getMgrDebugLvl() {
        
        return this.mgrDebugLvl;
        
    }
    
    /**
     * Set the session mgr Debug Level Text.
     */
    public void setMgrDebugLvl(String mgrDebugLvl) {
        
        this.mgrDebugLvl = mgrDebugLvl;
        
    }
    
    /**
     * Return the session ID initializer.
     */
    public String getMgrSessionIDInit() {
        
        return this.mgrSessionIDInit;
        
    }
    
    /**
     * Set the mgr Session ID Initizializer.
     */
    public void setMgrSessionIDInit(String mgrSessionIDInit) {
        
        this.mgrSessionIDInit = mgrSessionIDInit;
        
    }
    
    /**
     * Return the Session mgr maximum active sessions.
     */
    
    public String getMgrMaxSessions() {
        
        return this.mgrMaxSessions;
        
    }
    
    /**
     * Set the Session mgr maximum active sessions.
     */
    public void setMgrMaxSessions(String mgrMaxSessions) {
        
        this.mgrMaxSessions = mgrMaxSessions;
        
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
        this.parentObjectName = null;
        this.loaderObjectName = null;
        this.managerObjectName = null;
        
        // default context properties
        this.cookies = "true";
        this.crossContext = "true";
        this.reloadable = "false";
        this.swallowOutput = "false";
        this.useNaming = "true";
        
        // loader properties
        this.ldrCheckInterval = "15";
        this.ldrDebugLvl = "0";
        this.ldrReloadable = "true";
        
        // session manager properties
        this.mgrCheckInterval = "60";
        this.mgrDebugLvl = "0";
        this.mgrSessionIDInit = "0";
        this.mgrMaxSessions = "-1";
    }
    
    /**
     * Render this object as a String.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("DefaultContextForm[adminAction=");
        sb.append(adminAction);
        sb.append(",cookies=");
        sb.append(cookies);
        sb.append(",crossContext=");
        sb.append(crossContext);
        sb.append(",reloadable=");
        sb.append(reloadable);
        sb.append(",swallowOutput=");
        sb.append(swallowOutput);
        sb.append(",useNaming=");
        sb.append(useNaming);        
        // loader properties
        sb.append(",ldrCheckInterval=");
        sb.append(ldrCheckInterval);        
        sb.append(",ldrDebugLvl=");
        sb.append(ldrDebugLvl);
        sb.append(",ldrReloadable=");
        sb.append(ldrReloadable);
        // manager properties
        sb.append(",mgrDebugLvl=");
        sb.append(mgrDebugLvl);
        sb.append(",mgrCheckInterval=");
        sb.append(mgrCheckInterval);
        sb.append(",mgrSessionIDInit=");
        sb.append(mgrSessionIDInit);
        sb.append(",mgrMaxSessions=");
        sb.append(mgrMaxSessions);
        // object names
        sb.append("',objectName='");
        sb.append(objectName);
        sb.append("',parentObjectName=");
        sb.append(parentObjectName);
        sb.append("',loaderObjectName=");
        sb.append(loaderObjectName);
        sb.append("',managerObjectName=");
        sb.append(managerObjectName);
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
    
    private ActionErrors errors;
    
    public ActionErrors validate(ActionMapping mapping,
    HttpServletRequest request) {
        
        errors = new ActionErrors();
        
        String submit = request.getParameter("submit");
        
        // front end validation when save is clicked.
        if (submit != null) {
            
            // loader properties
            // FIXME-- verify if these ranges are ok.
            numberCheck("ldrCheckInterval", ldrCheckInterval  , true, 0, 10000);
            
            // session manager properties            
            numberCheck("mgrCheckInterval",  mgrCheckInterval, true, 0, 10000);
            numberCheck("mgrMaxSessions",  mgrMaxSessions, false, -1, 100);
            
            //if ((mgrSessionIDInit == null) || (mgrSessionIDInit.length() < 1)) {
            //    errors.add("mgrSessionIDInit", new ActionError("error.mgrSessionIDInit.required"));
            //}
        }
        
        return errors;
    }
    
    /*
     * Helper method to check that it is a required number and
     * is a valid integer within the given range. (min, max).
     *
     * @param  field  The field name in the form for which this error occured.
     * @param  numText  The string representation of the number.
     * @param rangeCheck  Boolean value set to true of reange check should be performed.
     *
     * @param  min  The lower limit of the range
     * @param  max  The upper limit of the range
     *
     */
    
    private void numberCheck(String field, String numText, boolean rangeCheck,
    int min, int max) {
        
        // Check for 'is required'
        if ((numText == null) || (numText.length() < 1)) {
            errors.add(field, new ActionError("error."+field+".required"));
        } else {
            
            // check for 'must be a number' in the 'valid range'
            try {
                int num = Integer.parseInt(numText);
                // perform range check only if required
                if (rangeCheck) {
                    if ((num < min) || (num > max ))
                        errors.add( field,
                        new ActionError("error."+ field +".range"));
                }
            } catch (NumberFormatException e) {
                errors.add(field,
                new ActionError("error."+ field + ".format"));
            }
        }
    }
    
}
