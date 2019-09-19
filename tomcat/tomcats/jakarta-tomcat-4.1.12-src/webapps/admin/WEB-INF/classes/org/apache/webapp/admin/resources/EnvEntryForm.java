/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/resources/EnvEntryForm.java,v 1.7 2002/06/14 13:29:29 amyroh Exp $
 * $Revision: 1.7 $
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

import java.lang.reflect.Constructor;

/**
 * Form bean for the individual environment entry page.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.7 $ $Date: 2002/06/14 13:29:29 $
 * @since 4.1
 */

public final class EnvEntryForm extends BaseForm {


    // ----------------------------------------------------- Instance Variables


    // ------------------------------------------------------------- Properties


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
     * The type of the associated entry.
     */
    private String entryType = null;

    public String getEntryType() {
        return (this.entryType);
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }


    /**
     * The value of the associated entry.
     */
    private String value = null;

    public String getValue() {
        return (this.value);
    }

    public void setValue(String value) {
        this.value = value;
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

    /**
     * The value of override appl level entries.
     */
    private boolean override = true;

    public boolean getOverride() {
        return (this.override);
    }

    public void setOverride(boolean override) {
        this.override = override;
    }
    
    /**
     * The resource type of this environment entry.
     */
    private String resourcetype = null;
    
    /**
     * Return the resource type of the environment entry this bean refers to.
     */
    public String getResourcetype() {
        return this.resourcetype;
    }

    /**
     * Set the resource type of the environment entry this bean refers to.
     */
    public void setResourcetype(String resourcetype) {
        this.resourcetype = resourcetype;
    }
       
    /**
     * The path of this environment entry.
     */
    private String path = null;
    
    /**
     * Return the path of the environment entry this bean refers to.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Set the path of the environment entry this bean refers to.
     */
    public void setPath(String path) {
        this.path = path;
    }
       
    /**
     * The host of this environment entry.
     */
    private String host = null;
    
    /**
     * Return the host of the environment entry this bean refers to.
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Set the host of the environment entry this bean refers to.
     */
    public void setHost(String host) {
        this.host = host;
    }    
    
       
    /**
     * The service of this environment entry.
     */
    private String service = null;
    
    /**
     * Return the service of the environment entry this bean refers to.
     */
    public String getService() {
        return this.service;
    }

    /**
     * Set the service of the environment entry this bean refers to.
     */
    public void setService(String service) {
        this.service = service;
    }
    
    /**
     * Precomputed list of entry type labels and values.
     */
    private static List typeVals = new ArrayList();

    static {
        typeVals.add(new LabelValueBean("java.lang.Boolean", "java.lang.Boolean"));
        typeVals.add(new LabelValueBean("java.lang.Byte", "java.lang.Byte"));
        typeVals.add(new LabelValueBean("java.lang.Character", "java.lang.Character"));
        typeVals.add(new LabelValueBean("java.lang.Double", "java.lang.Double"));
        typeVals.add(new LabelValueBean("java.lang.Float", "java.lang.Float"));
        typeVals.add(new LabelValueBean("java.lang.Integer", "java.lang.Integer"));    
        typeVals.add(new LabelValueBean("java.lang.Long", "java.lang.Long"));
        typeVals.add(new LabelValueBean("java.lang.Short", "java.lang.Short"));        
        typeVals.add(new LabelValueBean("java.lang.String", "java.lang.String"));           
        
    }

    /**
     * Return the typeVals.
     */
    public List getTypeVals() {
        
        return this.typeVals;
        
    }
    
    /**
     * Set the typeVals.
     */
    public void setTypeVals(List typeVals) {
        
        this.typeVals = typeVals;
        
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
        entryType = null;
        value = null;
        description = null;
        override = false;

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

            // value is a required field
            if ((value == null) || (value.length() < 1)) {
                errors.add("value",
                           new ActionError("resources.error.value.required"));
            }

            // Quotes not allowed in name
            if ((name != null) && (name.indexOf('"') >= 0)) {
                errors.add("name",
                           new ActionError("users.error.quotes"));
            }

            // Quotes not allowed in value
            if ((value != null) && (value.indexOf('"') > 0)) {
                errors.add("value",
                           new ActionError("users.error.quotes"));
            }

            // Quotes not allowed in description
            if ((description != null) && (description.indexOf('"') > 0)) {
                errors.add("description",
                           new ActionError("users.error.quotes"));
            }
            
            // if cehcked, override will be sent as a request parameter
            override = (request.getParameter("override") != null);
            
            if (validateType(entryType, value)) {
                   errors.add("value",
                           new ActionError("resources.error.value.mismatch"));
            }
        }
        return (errors);
    }

    /**
     * Entry type must match type of value.
     */
    private boolean validateType(String entryType, String value) {
        Class cls = null;
        boolean mismatch = false;
        try {
            cls = Class.forName(entryType);
            
            if (Character.class.isAssignableFrom(cls)) {
                // Special handling is needed because the UI returns
                // a string even if it is a character (single length string).
                if (value.length() != 1) {
                    mismatch = true;
                }
            } else if (Boolean.class.isAssignableFrom(cls)) {
                // Special handling is needed because Boolean
                // string constructor accepts anything other than
                // true to be false
                if (!("true".equalsIgnoreCase(value) ||
                "false".equalsIgnoreCase(value))) {
                    mismatch = true;
                }
            } else if (Number.class.isAssignableFrom(cls)) {
                // all numbers throw NumberFormatException if they are
                // constructed with an incorrect number string
                // We use the general string constructor to do this job
                try {
                    Class[] parameterTypes = {String.class};
                    Constructor ct = cls.getConstructor(parameterTypes);
                    Object arglist1[] = {value};
                    Object retobj = ct.newInstance(arglist1);
                } catch (Exception e) {
                    mismatch = true;
                }
            } else if (String.class.isAssignableFrom(cls)) {
                // all strings are allowed
            } else {
                // validation for other types not implemented yet
               errors.add("entryType",
                       new ActionError("resources.error.entryType.notimpl"));
            }
        } catch (ClassNotFoundException cnfe) {
            // entry type has an invalid entry
           errors.add("entryType",
                       new ActionError("resources.error.entryType.invalid"));
         }        
        return mismatch;
    }
    
}
