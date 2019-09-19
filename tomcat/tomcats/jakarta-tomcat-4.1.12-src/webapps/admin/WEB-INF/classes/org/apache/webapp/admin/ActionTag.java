/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/ActionTag.java,v 1.4 2002/03/14 08:49:40 manveen Exp $
 * $Revision: 1.4 $
 * $Date: 2002/03/14 08:49:40 $
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;


/**
 * <p>Nested tag that represents an individual "instant action".  This tag
 * is valid <strong>only</strong> when nested within an ActoinsTag tag.
 * This tag has the following user-settable attributes:</p>
 * <ul>
 * <li><strong>selected</strong> - Set to <code>true</code> if this action
 *     should be selected when the control is initially displayed.</li>
 * <li><strong>url</strong> - URL to which control should be transferred
 *     (in the current frame or window) if this action is selected.</li>
 * </ul>
 *
 * <p>In addition, the body content of this tag is used as the user-visible
 * label for the action, so that it may be conveniently localized.</p>
 *
 * <strong>FIXME</strong> - Internationalize the exception messages!
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.4 $ $Date: 2002/03/14 08:49:40 $
 */

public class ActionTag extends BodyTagSupport {


    // ----------------------------------------------------- Instance Variables


    /**
     * The label that will be rendered for this action.
     */
    protected String label = null;


    // ------------------------------------------------------------- Properties


    /**
     * Should this action be selected when the control is initially displayed?
     */
    protected boolean selected = false;

    public boolean getSelected() {
        return (this.selected);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Should this action selection be disabled? 
     * e.g. Action separators should be disabled.
     */
    protected boolean disabled = false;

    public boolean getDisabled() {
        return (this.disabled);
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * The URL to which control is transferred if this action is selected.
     */
    protected String url = null;

    public String getUrl() {
        return (this.url);
    }

    public void setUrl(String url) {
        this.url = url;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Process the start of this tag.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {

        // Initialize the holder for our label text
        this.label = null;

        // Do no further processing for now
        return (EVAL_BODY_TAG);

    }


    /**
     * Process the body text of this tag (if any).
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doAfterBody() throws JspException {

        String label = bodyContent.getString();
        if (label != null) {
            label = label.trim();
            if (label.length() > 0)
                this.label = label;
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
        while ((parent != null) && !(parent instanceof ActionsTag)) {
            parent = parent.getParent();
        }
        if ((parent == null) || !(parent instanceof ActionsTag))
            throw new JspException("Must be nested in an ActionsTag isntance");
        ActionsTag actions = (ActionsTag) parent;

        // Register the information for the action represented by
        // this action
        HttpServletRequest request =
            (HttpServletRequest) pageContext.getRequest();
        HttpServletResponse response =
            (HttpServletResponse) pageContext.getResponse();
        String path = null;
        if ((url != null) && (url.startsWith("/"))) {
            path = request.getContextPath() + url;
        } else {
            path = url;
        }
        actions.addAction(label, selected, disabled,
                          response.encodeURL(path));

        return (EVAL_PAGE);

    }


    /**
     * Release all state information set by this tag.
     */
    public void release() {

        this.label = null;
        this.selected = false;
        this.disabled = false;
        this.url = null;

    }


}
