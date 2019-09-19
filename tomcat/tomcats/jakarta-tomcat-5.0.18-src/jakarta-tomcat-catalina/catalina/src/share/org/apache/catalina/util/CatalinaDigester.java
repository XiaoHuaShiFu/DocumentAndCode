/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/catalina/src/share/org/apache/catalina/util/CatalinaDigester.java,v 1.1 2003/11/14 10:00:39 remm Exp $
 * $Revision: 1.1 $
 * $Date: 2003/11/14 10:00:39 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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
 * [Additional notices, if required by prior licensing conditions]
 *
 */


package org.apache.catalina.util;


import org.apache.commons.digester.Digester;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.SAXException;

import org.apache.tomcat.util.IntrospectionUtils;

/**
 * This extended digester filters out ${...} tokens to replace them with
 * matching system properties.
 * 
 * @author Simon Kitching
 * @author Remy Maucherat
 */
public class CatalinaDigester extends Digester {


    // ---------------------------------------------------------- Static Fields


    private static class SystemPropertySource 
        implements IntrospectionUtils.PropertySource {
        public String getProperty( String key ) {
            return System.getProperty(key);
        }
    }

    protected static IntrospectionUtils.PropertySource source[] = 
        new IntrospectionUtils.PropertySource[] { new SystemPropertySource() };


    // ---------------------------------------------------------------- Methods


    /**
     * Invoke inherited implementation after applying variable
     * substitution to any attribute values containing variable
     * references. 
     */
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes list)
        throws SAXException {
        list = updateAttributes(list);
        super.startElement(namespaceURI, localName, qName, list);
    }


    /**
     * Invoke inherited implementation after applying variable substitution
     * to the character data contained in the current element.
     */
    public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException  {
        bodyText = updateBodyText(bodyText);
        super.endElement(namespaceURI, localName, qName);
    }


    /**
     * Returns an attributes list which contains all the attributes
     * passed in, with any text of form "${xxx}" in an attribute value
     * replaced by the appropriate value from the system property.
     */
    private Attributes updateAttributes(Attributes list) {

        if (list.getLength() == 0) {
            return list;
        }
        
        AttributesImpl newAttrs = new AttributesImpl(list);
        int nAttributes = newAttrs.getLength();
        for (int i = 0; i < nAttributes; ++i) {
            String value = newAttrs.getValue(i);
            try {
                String newValue = 
                    IntrospectionUtils.replaceProperties(value, null, source);
                if (value != newValue) {
                    newAttrs.setValue(i, newValue);
                }
            }
            catch (Exception e) {
                // ignore - let the attribute have its original value
            }
        }

        return newAttrs;

    }


    /**
     * Return a new StringBuffer containing the same contents as the
     * input buffer, except that data of form ${varname} have been
     * replaced by the value of that var as defined in the system property.
     */
    private StringBuffer updateBodyText(StringBuffer bodyText) {
        String in = bodyText.toString();
        String out;
        try {
            out = IntrospectionUtils.replaceProperties(in, null, source);
        } catch(Exception e) {
            return bodyText; // return unchanged data
        }

        if (out == in)  {
            // No substitutions required. Don't waste memory creating
            // a new buffer
            return bodyText;
        } else {
            return new StringBuffer(out);
        }
    }


}
