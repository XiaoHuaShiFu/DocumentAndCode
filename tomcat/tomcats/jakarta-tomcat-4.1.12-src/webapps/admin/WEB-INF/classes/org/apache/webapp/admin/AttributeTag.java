/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/AttributeTag.java,v 1.2 2002/08/16 22:24:06 amyroh Exp $
 * $Revision: 1.2 $
 * $Date: 2002/08/16 22:24:06 $
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
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.commons.beanutils.PropertyUtils;



/**
 * Custom tag that retrieves a JMX MBean attribute value, and writes it
 * out to the current output stream.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2002/08/16 22:24:06 $
 */

public class AttributeTag extends TagSupport {


    // ------------------------------------------------------------- Properties


    /**
     * The attribute name on the JMX MBean to be retrieved.
     */
    protected String attribute = null;

    public String getAttribute() {
        return (this.attribute);
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }


    /**
     * The bean name to be retrieved.
     */
    protected String name = null;

    public String getName() {
        return (this.name);
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     * The property name to be retrieved.
     */
    protected String property = null;

    public String getProperty() {
        return (this.property);
    }

    public void setProperty(String property) {
        this.property = property;
    }


    /**
     * The scope in which the bean should be searched.
     */
    protected String scope = null;

    public String getScope() {
        return (this.scope);
    }

    public void setScope(String scope) {
        this.scope = scope;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Render the JMX MBean attribute identified by this tag
     *
     * @exception JspException if a processing exception occurs
     */
    public int doEndTag() throws JspException {

        // Retrieve the object name identified by our attributes
        Object bean = null;
        if (scope == null) {
            bean = pageContext.findAttribute(name);
        } else if ("page".equalsIgnoreCase(scope)) {
            bean = pageContext.getAttribute(name, PageContext.PAGE_SCOPE);
        } else if ("request".equalsIgnoreCase(scope)) {
            bean = pageContext.getAttribute(name, PageContext.REQUEST_SCOPE);
        } else if ("session".equalsIgnoreCase(scope)) {
            bean = pageContext.getAttribute(name, PageContext.SESSION_SCOPE);
        } else if ("application".equalsIgnoreCase(scope)) {
            bean = pageContext.getAttribute(name,
                                            PageContext.APPLICATION_SCOPE);
        } else {
            throw new JspException("Invalid scope value '" + scope + "'");
        }
        if (bean == null) {
            throw new JspException("No bean '" + name + "' found");
        }
        if (property != null) {
            try {
                bean = PropertyUtils.getProperty(bean, property);
            } catch (Throwable t) {
                throw new JspException
                    ("Exception retrieving property '" + property + "': " + t);
            }
            if (bean == null) {
                throw new JspException("No property '" + property + "' found");
            }
        }

        // Convert to an object name as necessary
        ObjectName oname = null;
        try {
            if (bean instanceof ObjectName) {
                oname = (ObjectName) bean;
            } else if (bean instanceof String) {
                oname = new ObjectName((String) bean);
            } else {
                oname = new ObjectName(bean.toString());
            }
        } catch (Throwable t) {
            throw new JspException("Exception creating object name for '" +
                                   bean + "': " + t);
        }

        // Acquire a reference to our MBeanServer
        MBeanServer mserver =
            (MBeanServer) pageContext.getAttribute
            ("org.apache.catalina.MBeanServer", PageContext.APPLICATION_SCOPE);
        if (mserver == null)
            throw new JspException("MBeanServer is not available");

        // Retrieve the specified attribute from the specified MBean
        Object value = null;
        try {
            value = mserver.getAttribute(oname, attribute);
        } catch (Throwable t) {
            throw new JspException("Exception retrieving attribute '" +
                                   attribute + "'");
        }

        // Render this value to our current output writer
        if (value != null) {
            JspWriter out = pageContext.getOut();
            try {
                out.print(value);
            } catch (IOException e) {
                throw new JspException("IOException: " + e);
            }
        }

        // Evaluate the remainder of this page
        return (EVAL_PAGE);

    }


    /**
     * Release all current state.
     */
    public void release() {

        attribute = null;
        name = null;
        property = null;
        scope = null;

    }


}
