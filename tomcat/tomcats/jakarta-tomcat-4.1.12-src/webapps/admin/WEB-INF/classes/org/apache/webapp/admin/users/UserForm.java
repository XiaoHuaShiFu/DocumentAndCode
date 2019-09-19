/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/users/UserForm.java,v 1.2 2002/02/10 08:06:20 craigmcc Exp $
 * $Revision: 1.2 $
 * $Date: 2002/02/10 08:06:20 $
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

package org.apache.webapp.admin.users;


import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


/**
 * Form bean for the individual user page.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2002/02/10 08:06:20 $
 * @since 4.1
 */

public final class UserForm extends BaseForm {


    // ----------------------------------------------------- Instance Variables


    // ------------------------------------------------------------- Properties


    /**
     * The full name of the associated user.
     */
    private String fullName = null;

    public String getFullName() {
        return (this.fullName);
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    /**
     * The MBean Names of the groups associated with this user.
     */
    private String groups[] = new String[0];

    public String[] getGroups() {
        return (this.groups);
    }

    public void setGroups(String groups[]) {
        if (groups == null) {
            groups = new String[0];
        }
        this.groups = groups;
    }


    /**
     * The password of the associated user.
     */
    private String password = null;

    public String getPassword() {
        return (this.password);
    }

    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * The MBean Names of the roles associated with this user.
     */
    private String roles[] = new String[0];

    public String[] getRoles() {
        return (this.roles);
    }

    public void setRoles(String roles[]) {
        if (roles == null) {
            roles = new String[0];
        }
        this.roles = roles;
    }


    /**
     * The username of the associated user.
     */
    private String username = null;

    public String getUsername() {
        return (this.username);
    }

    public void setUsername(String username) {
        this.username = username;
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
        fullName = null;
        groups = new String[0];
        password = null;
        roles = new String[0];
        username = null;

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

            // username is a required field
            if ((username == null) || (username.length() < 1)) {
                errors.add("username",
                           new ActionError("users.error.username.required"));
            }

            // uassword is a required field
            if ((password == null) || (username.length() < 1)) {
                errors.add("password",
                           new ActionError("users.error.password.required"));
            }

            // Quotes not allowed in username
            if ((username != null) && (username.indexOf('"') >= 0)) {
                errors.add("username",
                           new ActionError("users.error.quotes"));
            }

            // Quotes not allowed in password
            if ((password != null) && (password.indexOf('"') > 0)) {
                errors.add("description",
                           new ActionError("users.error.quotes"));
            }

            // Quotes not allowed in fullName
            if ((fullName != null) && (fullName.indexOf('"') > 0)) {
                errors.add("fullName",
                           new ActionError("users.error.quotes"));
            }

        }

        return (errors);

    }


}
