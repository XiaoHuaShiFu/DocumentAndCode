/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/valve/RemoteAddrValveForm.java,v 1.4 2003/03/25 08:29:05 amyroh Exp $
 * $Revision: 1.4 $
 * $Date: 2003/03/25 08:29:05 $
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

import java.lang.IllegalArgumentException;
import java.net.InetAddress;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.regexp.RE;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


import org.apache.webapp.admin.ApplicationServlet;
import org.apache.webapp.admin.LabelValueBean;

/**
 * Form bean for the remote addr valve page.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.4 $ $Date: 2003/03/25 08:29:05 $
 */

public final class RemoteAddrValveForm extends ValveForm {
    
    // ----------------------------------------------------- Instance Variables
    
    
    /**
     * The text for the allow IP addresses.
     * A comma-separated list of regular expression patterns
     * that the remote client's IP address is compared to. 
     */
    private String allow = "";

    /**
     * The text for the deny IP addresses.
     */
    private String deny = "";
    
    /**
     * The set of <code>allow</code> regular expressions we will evaluate.
     */
    private RE allows[] = new RE[0];

    /**
     * The set of <code>deny</code> regular expressions we will evaluate.
     */
    private RE denies[] = new RE[0];


    // ------------------------------------------------------------- Properties

    /**
     * Return the allow hosts IP adddresses.
     */
    public String getAllow() {
        
        return this.allow;
        
    }
    
    /**
     * Set the allow hosts.
     */
    public void setAllow(String allow) {
        
        this.allow = allow;
        
    }
    
    /**
     * Return the deny hosts IP adddresses.
     */
    public String getDeny() {
        
        return this.deny;
        
    }
    
    /**
     * Set the deny hosts IP addresses.
     */
    public void setDeny(String deny) {
        
        this.deny = deny;
        
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
        this.allow = null;
        this.deny = null;
        this.allows = null;
        this.denies = null;
        
    }
    
    /**
     * Render this object as a String.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("RemoteAddrValveForm[adminAction=");
        sb.append(getAdminAction());
        sb.append("',valveType=");
        sb.append(getValveType());
        sb.append(",allow=");
        sb.append(getAllow());
        sb.append(",deny=");
        sb.append(getDeny());        
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
        //if (submit != null) {
            // validate allow/deny patterns
            if ((allow == null) || (allow.length() < 1)) {
                if ((deny == null) || (deny.length() < 1)) {
                    errors.add("allow",
                    new ActionError("error.allow.deny.required"));
                }
            }                
        //}
        
        try {
            allows = ValveUtil.precalculate(allow);            
        } catch (IllegalArgumentException e) {
            errors.add("allow", new ActionError("error.syntax"));
            return errors;
        }
         
        try {   
            denies = ValveUtil.precalculate(deny);
        } catch (IllegalArgumentException e) {
            errors.add("allow", new ActionError("error.syntax"));
            return errors;
        }
        
        String ip = request.getRemoteAddr();
        
        if (ip == null) {
            return errors;
        }
        
        for (int i = 0; i < denies.length; i++) {
            if (denies[i].match(ip)) {
                if (allows.length < 1) {
                    errors.add("deny",
                        new ActionError("error.denyIP"));
                }
                for (int j = 0; j < allows.length; j++) {
                    if (!allows[j].match(ip)) { 
                        errors.add("deny",
                        new ActionError("error.denyIP"));
                    }
                }
            }    
        }
        
        boolean allowMatch = true;
        if (allows.length > 0) {
            allowMatch = false;
        }
        for (int i = 0; i < allows.length; i++) {
            if (allows[i].match(ip)) {
                allowMatch = true;       
            }
        }       
        if (!allowMatch) {
            errors.add("allow", new ActionError("error.allowIP"));
        }
        
        return errors;
    }
}
