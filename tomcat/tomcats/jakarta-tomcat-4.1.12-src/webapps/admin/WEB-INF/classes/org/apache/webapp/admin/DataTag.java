/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/DataTag.java,v 1.1 2002/01/03 23:21:53 remm Exp $
 * $Revision: 1.1 $
 * $Date: 2002/01/03 23:21:53 $
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
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
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


package org.apache.webapp.admin;


import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;


/**
 * <p>Nested tag that represents an individual "data" for a row.  This tag
 * is valid <strong>only</strong> when nested within a RowTag tag.
 *
 * <p>In addition, the body content of this tag is used as the user-visible
 * data for the action, so that it may be conveniently localized.</p>
 *
 * <strong>FIXME</strong> - Internationalize the exception messages!
 *
 * @author Manveen Kaur
 * @version $Revision: 1.1 $
 */

public class DataTag extends BodyTagSupport {


    // ----------------------------------------------------- Instance Variables


    /**
     * The data that will be rendered for this table row.
     */
    protected String data = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Process the start of this tag.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {

        // Initialize the holder for our data text
        this.data = null;

        // Do no further processing for now
        return (EVAL_BODY_TAG);

    }


    /**
     * Process the body text of this tag (if any).
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doAfterBody() throws JspException {

        String data = bodyContent.getString();
        if (data != null) {
            data = data.trim();
            if (data.length() > 0)
                this.data = data;
        }
        return (SKIP_BODY);

    }


    /**
     * Record this action with our surrounding ActionsTag instance.
     *
     * @exception JspException if a processing error occurs
     */
    public int doEndTag() throws JspException {

        // Find our parent ActionsTag instance
        Tag parent = getParent();
        if ((parent == null) || !(parent instanceof RowTag))
            throw new JspException("Must be nested in a rowTag isntance");
        RowTag row = (RowTag) parent;

        // Register the information for the action represented by
        // this action
        HttpServletResponse response =
            (HttpServletResponse) pageContext.getResponse();
        row.setData(data);
        
        return (EVAL_PAGE);

    }


    /**
     * Release all state information set by this tag.
     */
    public void release() {

        this.data = null;
    }


}
