/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/resources/MailSessionsForm.java,v 1.1 2002/06/20 02:31:05 amyroh Exp $
 * $Revision: 1.1 $
 * $Date: 2002/06/20 02:31:05 $
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

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


/**
 * Form bean for the delete mail sessions page.
 *
 * @author Amy Roh
 * @version $Revision: 1.1 $ $Date: 2002/06/20 02:31:05 $
 * @since 4.1
 */

public final class MailSessionsForm extends BaseForm {


    // ----------------------------------------------------- Instance Variables


    // ------------------------------------------------------------- Properties


    /**
     * The object names of the specified environment entries.
     */
    private String mailSessions[] = null;

    public String[] getMailSessions() {
        return (this.mailSessions);
    }

    public void setMailSessions(String mailSessions[]) {
        this.mailSessions = mailSessions;
    }

    /**
     * The resource type of this mail session.
     */
    private String resourcetype = null;
    
    /**
     * Return the resource type of the mail session this bean refers to.
     */
    public String getResourcetype() {
        return this.resourcetype;
    }

    /**
     * Set the resource type of the mail session this bean refers to.
     */
    public void setResourcetype(String resourcetype) {
        this.resourcetype = resourcetype;
    }
       
    /**
     * The path of this mail session.
     */
    private String path = null;
    
    /**
     * Return the path of the mail session this bean refers to.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Set the path of the mail session this bean refers to.
     */
    public void setPath(String path) {
        this.path = path;
    }
       
    /**
     * The host of this mail session.
     */
    private String host = null;
    
    /**
     * Return the host of the mail session this bean refers to.
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Set the host of the mail session this bean refers to.
     */
    public void setHost(String host) {
        this.host = host;
    }    
    
       
    /**
     * The service of this mail session.
     */
    private String service = null;
    
    /**
     * Return the service of the mail session this bean refers to.
     */
    public String getService() {
        return this.service;
    }

    /**
     * Set the service of the mail session this bean refers to.
     */
    public void setService(String service) {
        this.service = service;
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
        this.mailSessions = null;

    }


}
