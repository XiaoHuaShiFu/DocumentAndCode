/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/host/AliasForm.java,v 1.1 2002/04/16 18:54:46 manveen Exp $
 * $Revision: 1.1 $
 * $Date: 2002/04/16 18:54:46 $
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
 * Form bean for the alias page.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.1 $ $Date: 2002/04/16 18:54:46 $
 */

public final class AliasForm extends ActionForm {
    
    // ----------------------------------------------------- Instance Variables

    /**
     * The text for the hostName.
     */
    private String hostName = null;

    /**
     * The text for the aliasName.
     */
    private String aliasName = null;

    /*
     * Represent aliases as a List.
     */    
    private List aliasVals = null;
   
    // ------------------------------------------------------------- Properties
    
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
     * Return the alias name.
     */
    public String getAliasName() {
        
        return this.aliasName;
        
    }
    
    /**
     * Set the alias name.
     */
    public void setAliasName(String aliasName) {
        
        this.aliasName = aliasName;
        
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
        
        this.aliasName = null;
        this.hostName = null;

    }
    
     /**
     * Render this object as a String.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("AliasForm[hostName=");
        sb.append(hostName);
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
            if ((aliasName== null) || (aliasName.length() < 1)) {
                errors.add("aliasName", new ActionError("error.aliasName.required"));
            }
                        
        }        
        return errors;       
    }
    
}
