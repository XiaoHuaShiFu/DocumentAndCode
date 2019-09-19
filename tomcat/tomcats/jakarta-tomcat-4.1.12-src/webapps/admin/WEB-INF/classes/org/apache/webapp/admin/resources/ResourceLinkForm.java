/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/resources/ResourceLinkForm.java,v 1.2 2002/06/14 13:29:29 amyroh Exp $
 * $Revision: 1.2 $
 * $Date: 2002/06/14 13:29:29 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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

package org.apache.webapp.admin.resources;

import java.util.List;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.webapp.admin.LabelValueBean;

/**
 * Form bean for the individual resource link page.
 *
 * @author Amy Roh
 * @version $Revision: 1.2 $ $Date: 2002/06/14 13:29:29 $
 * @since 4.1
 */

public final class ResourceLinkForm extends BaseForm {


    // ----------------------------------------------------- Instance Variables


    // ------------------------------------------------------------- Properties


    /**
     * The name of the resource link.
     */
    private String name = null;

    public String getName() {
        return (this.name);
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The global name of the resource link.
     */
    private String global = null;

    public String getGlobal() {
        return (this.global);
    }

    public void setGlobal(String global) {
        this.global = global;
    }
    
    /**
     * The resource type of this resource link.
     */
    private String resourcetype = null;
    
    /**
     * Return the resource type of the resource link this bean refers to.
     */
    public String getResourcetype() {
        return this.resourcetype;
    }

    /**
     * Set the resource type of the resource link this bean refers to.
     */
    public void setResourcetype(String resourcetype) {
        this.resourcetype = resourcetype;
    }
       
    /**
     * The path of this resource link.
     */
    private String path = null;
    
    /**
     * Return the path of the resource link this bean refers to.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Set the path of the resource link this bean refers to.
     */
    public void setPath(String path) {
        this.path = path;
    }
       
    /**
     * The host of this resource link.
     */
    private String host = null;
    
    /**
     * Return the host of the resource link this bean refers to.
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Set the host of the resource link this bean refers to.
     */
    public void setHost(String host) {
        this.host = host;
    }    
    
       
    /**
     * The service of this resource link.
     */
    private String service = null;
    
    /**
     * Return the service of the resource link this bean refers to.
     */
    public String getService() {
        return this.service;
    }

    /**
     * Set the service of the resource link this bean refers to.
     */
    public void setService(String service) {
        this.service = service;
    }
    
    /**
     * The type of the resource link link.
     */
    private String type = null;

    public String getType() {
        return (this.type);
    }

    public void setType(String type) {
        this.type = type;
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
        name = null;
        global = null;
        type = null;
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
    
    private ActionErrors errors = null;
    
    public ActionErrors validate(ActionMapping mapping,
    HttpServletRequest request) {
        errors = new ActionErrors();

        String submit = request.getParameter("submit");

        if (submit != null) {

            // name is a required field
            if ((name == null) || (name.length() < 1)) {
                errors.add("name",
                           new ActionError("resources.error.name.required"));
            }

            // global is a required field
            if (( global == null) || (global.length() < 1)) {
                errors.add("global",
                           new ActionError("resources.error.global.required"));
            }
            
            // type is a required field
            if ((type == null) || (type.length() < 1)) {
                errors.add("type",
                           new ActionError("resources.error.type.required"));
            }
            
         }

        return (errors);

    }
 
    
}
