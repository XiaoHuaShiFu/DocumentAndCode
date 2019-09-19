/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/RowTag.java,v 1.2 2002/01/23 23:06:54 craigmcc Exp $
 * $Revision: 1.2 $
 * $Date: 2002/01/23 23:06:54 $
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
 * <p>Nested tag that represents an individual "instant table".  This tag
 * is valid <strong>only</strong> when nested within an TableTag tag.
 * This tag has the following user-settable attributes:</p>
 * <ul>
 * <li><strong>header</strong> - Is this  a header row?</li>
 * <li><strong>label</strong> - label to be displayed.</li>
 * <li><strong>data</strong> - data of the table data element.</li>
 * <li><strong>labelStyle</strong> - Style to be applied to the
 * label table data element.</li>
 * <li><strong>dataStyle</strong> - Style to be applied to the data table
 * data element.</li>
 *
 * </ul>
 *
 * @author Manveen Kaur
 * @version $Revision: 1.2 $ $Date: 2002/01/23 23:06:54 $
 */

public class RowTag extends BodyTagSupport {
    
    /**
     * Is this the header row?
     */
    protected boolean header = false;
    
    public boolean getHeader() {
        return (this.header);
    }
    
    public void setHeader(boolean header) {
        this.header = header;
    }    
    
    /**
     * The label that will be rendered for this row's table data element.
     */
    protected String label = null;
   
    public void setLabel(String label) {
        this.label = label;
    }
    
    
    /**
     * The data of the table data element of this row.
     */
    protected String data = null;
    
    public void setData(String data) {
        this.data = data;
    }
    
    /**
     * The style of the label.
     */
    protected String labelStyle = null;
    
    public String getLabelStyle() {
        return (this.labelStyle);
    }
    
    public void setLabelStyle(String labelStyle) {
        this.labelStyle = labelStyle;
    }
    
    
    /**
     * The style of the data.
     */
    protected String dataStyle = null;
    
    public String getdataStyle() {
        return (this.dataStyle);
    }
    
    public void setdataStyle(String dataStyle) {
        this.dataStyle = dataStyle;
    }
    
    // --------------------------------------------------------- Public Methods
    
    
    /**
     * Process the start of this tag.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {
        
         // Do no further processing for now
        return (EVAL_BODY_TAG);
        
    }
    
    
    /**
     * Process the body text of this tag (if any).
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doAfterBody() throws JspException {
       
        return (SKIP_BODY);
        
    }
    
    
    /**
     * Record this action with our surrounding ActionsTag instance.
     *
     * @exception JspException if a processing error occurs
     */
    public int doEndTag() throws JspException {
        
        // Find our parent TableTag instance
        Tag parent = getParent();
        while ((parent != null) && !(parent instanceof TableTag)) {
            parent = parent.getParent();
        }
        if (parent == null) {
            throw new JspException("Must be nested in a TableTag instance");
        }
        TableTag table = (TableTag) parent;
        
        // Register the information for the row represented by
        // this row
        HttpServletResponse response =
        (HttpServletResponse) pageContext.getResponse();
        table.addRow(header, label, data, labelStyle, dataStyle);
        
        return (EVAL_PAGE);
        
    }
    
    
    /**
     * Release all state information set by this tag.
     */
    public void release() {
        
        //super.release();
        
        this.header= false;
        this.label = null;
        this.data = null;
        this.labelStyle = null;
        this.dataStyle = null;
        
    }
    
    
}
