/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/resources/ResourceLinksForm.java,v 1.2 2003/03/23 02:10:27 amyroh Exp $
 * $Revision: 1.2 $
 * $Date: 2003/03/23 02:10:27 $
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
 * Form bean for the delete resource links page.
 *
 * @author Amy Roh
 * @version $Revision: 1.2 $ $Date: 2003/03/23 02:10:27 $
 * @since 4.1
 */

public final class ResourceLinksForm extends BaseForm {


    // ----------------------------------------------------- Instance Variables


    // ------------------------------------------------------------- Properties


    /**
     * The object names of the specified resource links.
     */
    private String resourceLinks[] = null;

    public String[] getResourceLinks() {
        return (this.resourceLinks);
    }

    public void setResourceLinks(String resourceLinks[]) {
        this.resourceLinks = resourceLinks;
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
     * The domain of this resource link.
     */
    private String domain = null;
    
    /**
     * Return the domain of the resource link this bean refers to.
     */
    public String getDomain() {
        return this.domain;
    }

    /**
     * Set the domain of the resource link this bean refers to.
     */
    public void setDomain(String domain) {
        this.domain = domain;
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
        this.resourceLinks = null;

    }


}
