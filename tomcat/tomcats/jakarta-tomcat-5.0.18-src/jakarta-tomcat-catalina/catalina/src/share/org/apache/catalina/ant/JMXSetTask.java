/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/catalina/src/share/org/apache/catalina/ant/JMXSetTask.java,v 1.2 2003/09/02 21:22:00 remm Exp $
 * $Revision: 1.2 $
 * $Date: 2003/09/02 21:22:00 $
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


package org.apache.catalina.ant;


import org.apache.tools.ant.BuildException;


/**
 * Ant task that implements the JMX Set command (<code>/jmxproxy/?set</code>)
 * supported by the Tomcat manager application.
 *
 * @author Vivek Chopra
 * @version $Revision: 1.2 $
 */
public class JMXSetTask extends AbstractCatalinaTask {

    // Properties

    /**
     * The full bean name
     */
    protected String bean      = null;

    /**
     * The attribute you wish to alter
     */
    protected String attribute = null;

    /**
     * The new value for the attribute
     */
    protected String value     = null;

    // Public Methods
    
    /**
     * Get method for the bean name
     * @return Bean name
     */
    public String getBean () {
        return this.bean;
    }

    /**
     * Set method for the bean name
     * @param bean Bean name
     */
    public void setBean (String bean) {
        this.bean = bean;
    }

    /**
     * Get method for the attribute name
     * @return Attribute name
     */
    public String getAttribute () {
        return this.attribute;
    }

    /**
     * Set method for the attribute name
     * @param attribute Attribute name
     */
    public void setAttribute (String attribute) {
        this.attribute = attribute;
    }

    /**
     * Get method for the attribute value
     * @return Attribute value
     */
    public String getValue () {
        return this.value;
    }

    /**
     * Set method for the attribute value
     * @param attribute Attribute value
     */
    public void setValue (String value) {
        this.value = value;
    }

    /**
     * Execute the requested operation.
     *
     * @exception BuildException if an error occurs
     */
    public void execute() throws BuildException {
        super.execute();
        if (bean == null || attribute == null || value == null) {
            throw new BuildException
                ("Must specify 'bean', 'attribute' and 'value' attributes");
        }
        System.out.println ("INFO: Setting attribute " + attribute +
                            " in bean " + bean +
                            " to " + value); 
        execute("/jmxproxy/?set=" + bean 
                + "&att=" + attribute 
                + "&val=" + value);
    }
}
