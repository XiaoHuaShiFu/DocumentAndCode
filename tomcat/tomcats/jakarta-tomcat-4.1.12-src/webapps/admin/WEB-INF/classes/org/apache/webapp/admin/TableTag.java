/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/TableTag.java,v 1.2 2002/06/09 19:10:22 remm Exp $
 * $Revision: 1.2 $
 * $Date: 2002/06/09 19:10:22 $
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
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;


/**
 * <p>JSP custom tag that renders an "instant table" control.  To the user,
 * it appears as an HTML &lt;table&gt; element
 * This tag has the following user-settable attributes:</p>
 * <ul>
 * <li><strong>columns</strong> - (Integer) number of columns in the table.
 * If not specified, one two columns will be created.</li>
 * <li><strong>table-class</strong> - The CSS style class to be applied to the
 *     entire rendered output of the entire table, if any.</li>
 * <li><strong>header-row-class</strong> - The CSS style class to be applied to the
 *     entire rendered output of the table header-row, if any.</li>
 *
 * </ul>
 *
 * <strong>FIXME</strong> - Internationalize the exception messages!
 *
 * @author Manveen Kaur
 * @version $Revision: 1.2 $ $Date: 2002/06/09 19:10:22 $
 */

public class TableTag extends BodyTagSupport {
    
    
    // ----------------------------------------------------- Manifest Constants
    
    
    /**
     * Attribute name used to indicate that we have generated the JavaScript
     * function already on the current page.  The data stored for this
     * attribute is arbitrary - only its existence is relevant.
     */
    //   protected static final String FUNCTION_TAG =
    //       "org.apache.webapp.admin.TableTag.FUNCTION_TAG";
    
    
    // ----------------------------------------------------- Instance Variables
    
    
    /**
     * The set of labels for the rows displayed by this control.
     */
    protected ArrayList labels = new ArrayList();
    
    
    /**
     * The set of datas for the rows displayed by this control.
     */
    protected ArrayList datas = new ArrayList();
    
    
    /**
     * The set of labelStyles for the rows displayed by this control.
     */
    protected ArrayList labelStyles = new ArrayList();
    
    
    /**
     * The set of dataStyles for the rows displayed by this control.
     */
    protected ArrayList dataStyles = new ArrayList();
    
    /**
     * The set of "headers" flags for rows displayed by this control.
     */
    protected ArrayList headers = new ArrayList();
    
    
    // ------------------------------------------------------------- Properties
    
    
    /**
     * The number of elements that will be displayed to the user.
     */
    protected int columns = 2;
    
    public int getColumns() {
        return (this.columns);
    }
    
    public void setColumns(int columns) {
        this.columns = columns;
    }
    
    
    /**
     * The CSS style class to be applied to the entire rendered output
     * of this "instant table" object.
     */
    protected String tableStyle = null;
    
    public String getTableStyle() {
        return (this.tableStyle);
    }
    
    public void setTableStyle(String tableStyle) {
        this.tableStyle = tableStyle;
    }
    
    /**
     * The CSS Style for the lines between table rows.
     */
    protected String lineStyle = null;
    
    public String getLineStyle() {
        return (this.lineStyle);
    }
    
    public void setLineStyle(String lineStyle) {
        this.lineStyle = lineStyle;
    }
    
    // --------------------------------------------------------- Public Methods


    public int doStartTag() throws JspException {

        this.headers.clear();
        this.labels.clear();
        this.datas.clear();
        this.labelStyles.clear();
        this.dataStyles.clear();

        return (EVAL_BODY_TAG);

    }


    /**
     * Render this instant actions control.
     *
     * @exception JspException if a processing error occurs
     */
    public int doEndTag() throws JspException {
        
        JspWriter out = pageContext.getOut();
        
        try {
            
            // Render the beginning of this element
            out.println();
            out.print("<table ");
            if (columns > 2) {
                out.print(" columns=\"");
                out.print(columns);
                out.print("\"");
            }
            if (tableStyle != null) {
                out.print(" class=\"");
                out.print(tableStyle);
                out.print("\"");
                out.print(" border=\"1\" cellspacing=\"0\" ");
                out.print(" cellpadding=\"0\" width=\"100%\" ");
            }
            out.println(">");
            
            
            // Render each defined row
            int n = labels.size();
            for (int i = 0; i < n; i++) {
                String label = (String) labels.get(i);
                boolean header = ((Boolean) headers.get(i)).booleanValue();
                String data = (String) datas.get(i);
                String labelStyle = (String) labelStyles.get(i);
                String dataStyle = (String) dataStyles.get(i);
                
                if (header)
                    out.println("<tr class=\"header-row\" >");
                else out.println("<tr>");
                
                out.println("  <td width=\"27%\"> ");
                
                out.print("    <div align=\"left\"");
                if (labelStyle != null)
                    out.print( " class=\"" + labelStyle +"\"");
                out.print(">");
                out.print(label);
                out.println("    </div>");
                out.println("  </td>");
                
                out.println("  <td width=\"73%\"> ");
                out.print("    <div align=\"left\"" );
                if (dataStyle != null)
                    out.print(" class=\"" + dataStyle + "\"");
                out.print(">");
                out.print(data);
                out.println("    </div>");
                out.print("  </td>");
                out.println("</tr>");
                
                /*
                if (!header) {
                    out.println("<tr height=\"1\">");
                    out.println("  <td class=\""+ lineStyle + "\" colspan=\"2\">");
                    out.println("    <img src=\"\" alt=\"\" width=\"1\" height=\"1\" border=\"0\">");
                    out.println("  </td>");
                    out.println("</tr>");
                }
                 */
            }
            
            // Render the end of this element
            out.println("</table>");
            out.println();
            
        } catch (IOException e) {
            throw new JspException(e);
        }
        
        return (EVAL_PAGE);
        
    }
    
    
    /**
     * Release all state information set by this tag.
     */
    public void release() {
        
        this.headers.clear();
        this.labels.clear();
        this.datas.clear();
        this.labelStyles.clear();
        this.dataStyles.clear();
        this.columns = 2;
        this.tableStyle = null;
        this.lineStyle = null;
        
    }
    
    
    // -------------------------------------------------------- Package Methods
    
    
    /**
     * Add a new Action to the set that will be rendered by this control.
     *
     * @param label Localized label visible to the user
     * @param selected Initial selected state of this option
     * @param url URL to which control should be transferred if selected
     */
    
    void addRow(boolean header, String label, String data,
    String labelStyle, String dataStyle) {
        
        headers.add(new Boolean(header));
        labels.add(label);
        datas.add(data);
        labelStyles.add(labelStyle);
        dataStyles.add(dataStyle);
        
    }
    
    // ------------------------------------------------------ Protected Methods
    
    
}
