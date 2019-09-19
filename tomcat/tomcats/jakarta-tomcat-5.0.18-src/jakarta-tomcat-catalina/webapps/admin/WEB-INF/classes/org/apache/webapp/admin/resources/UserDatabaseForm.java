/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/resources/UserDatabaseForm.java,v 1.3 2003/03/26 08:05:19 amyroh Exp $
 * $Revision: 1.3 $
 * $Date: 2003/03/26 08:05:19 $
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

import java.lang.reflect.Constructor;

/**
 * Form bean for the individual user database page.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.3 $ $Date: 2003/03/26 08:05:19 $
 * @since 4.1
 */

public final class UserDatabaseForm extends BaseForm {


    // ----------------------------------------------------- Instance Variables


    // ------------------------------------------------------------- Properties

    /**
     * The domain of this data source.
     */
    private String domain = null;
    
    /**
     * Return the domain of the data source this bean refers to.
     */
    public String getDomain() {
        return this.domain;
    }

    /**
     * Set the domain of the data source this bean refers to.
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * The name of the associated entry.
     */
    private String name = null;

    public String getName() {
        return (this.name);
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The path of the associated user database entry.
     */
    private String path = null;

    public String getPath() {
        return (this.path);
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * The type of the resource.
     */
    private String type = null;

    public String getType() {
        return (this.type);
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * The factory that implements the user database entry.
     */
    private String factory = null;

    public String getFactory() {
        return (this.factory);
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    /**
     * The description of the associated entry.
     */
    private String description = null;

    public String getDescription() {
        return (this.description);
    }

    public void setDescription(String description) {
        this.description = description;
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
        type = null;
        path = null;
        factory = null;
        description = null;

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
        //if (submit != null) {

            // name is a required field
            if ((name == null) || (name.length() < 1)) {
                errors.add("name",
                           new ActionError("resources.error.name.required"));
            }

            // path is a required field
            if ((path == null) || (path.length() < 1)) {
                errors.add("path",
                           new ActionError("resources.error.path.required"));
            }

            // Quotes not allowed in name
            if ((name != null) && (name.indexOf('"') >= 0)) {
                errors.add("name",
                           new ActionError("users.error.quotes"));
            }

            // Quotes not allowed in path
            if ((path != null) && (path.indexOf('"') > 0)) {
                errors.add("path",
                           new ActionError("users.error.quotes"));
            }

            // Quotes not allowed in description
            if ((description != null) && (description.indexOf('"') > 0)) {
                errors.add("description",
                           new ActionError("users.error.quotes"));
            }
        //}
        return (errors);
    }
    
}
