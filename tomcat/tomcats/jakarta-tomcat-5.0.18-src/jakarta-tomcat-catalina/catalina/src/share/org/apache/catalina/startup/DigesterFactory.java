/*
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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


package org.apache.catalina.startup;

import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.catalina.util.SchemaResolver;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSet;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * Wrapper class around the Digester that hide Digester's initialization details
 *
 * @author Jean-Francois Arcand
 */

public class DigesterFactory{

    /**
     * The XML entiry resolver used by the Digester.
     */
    private static SchemaResolver schemaResolver;


    /**
     * Create a <code>Digester</code> parser with no <code>Rule</code>
     * associated and XML validation turned off.
     */
    public static Digester newDigester(){
        return newDigester(false, false, null);
    }

    
    /**
     * Create a <code>Digester</code> parser with XML validation turned off.
    * @param rule an instance of <code>Rule</code↔ used for parsing the xml.
     */
    public static Digester newDigester(RuleSet rule){
        return newDigester(false,false,rule);
    }

    
    /**
     * Create a <code>Digester</code> parser.
     * @param xmlValidation turn on/off xml validation
     * @param xmlNamespaceAware turn on/off namespace validation
     * @param rule an instance of <code>Rule</code↔ used for parsing the xml.
     */
    public static Digester newDigester(boolean xmlValidation,
                                       boolean xmlNamespaceAware,
                                       RuleSet rule) {

        URL url = null;
        Digester digester = new Digester();
        digester.setNamespaceAware(xmlNamespaceAware);
        digester.setValidating(xmlValidation);
        
        String parserName = 
                digester.getFactory().getClass().getName();
        if (parserName.indexOf("xerces")!=-1) {
            digester = patchXerces(digester);
        }

        schemaResolver = new SchemaResolver(digester);
        if (xmlValidation) {
            // Xerces 2.3 and up has a special way to turn on validation
            // for both DTD and Schema
            if (parserName.indexOf("xerces")!=-1) {
                turnOnXercesValidation(digester);
            } else {
                turnOnValidation(digester);
            }
        }
        registerLocalSchema();
        
        digester.setEntityResolver(schemaResolver);
        if ( rule != null )
            digester.addRuleSet(rule);

        return (digester);
    }


    /**
     * Patch Xerces for backward compatibility.
     */
    private static Digester patchXerces(Digester digester){
        // This feature is needed for backward compatibility with old DDs
        // which used Java encoding names such as ISO8859_1 etc.
        // with Crimson (bug 4701993). By default, Xerces does not
        // support ISO8859_1.
        try{
            digester.setFeature(
                "http://apache.org/xml/features/allow-java-encodings", true);
        } catch(ParserConfigurationException e){
                // log("contextConfig.registerLocalSchema", e);
        } catch(SAXNotRecognizedException e){
                // log("contextConfig.registerLocalSchema", e);
        } catch(SAXNotSupportedException e){
                // log("contextConfig.registerLocalSchema", e);
        }
        return digester;
    }


    /**
     * Utilities used to force the parser to use local schema, when available,
     * instead of the <code>schemaLocation</code> XML element.
     * @param The instance on which properties are set.
     * @return an instance ready to parse XML schema.
     */
    protected static void registerLocalSchema(){
        // J2EE
        register(Constants.J2eeSchemaResourcePath_14,
                 Constants.J2eeSchemaPublicId_14);
        // W3C
        register(Constants.W3cSchemaResourcePath_10,
                 Constants.W3cSchemaPublicId_10);
        // JSP
        register(Constants.JspSchemaResourcePath_20,
                 Constants.JspSchemaPublicId_20);
        // TLD
        register(Constants.TldDtdResourcePath_11,  
                 Constants.TldDtdPublicId_11);
        
        register(Constants.TldDtdResourcePath_12,
                 Constants.TldDtdPublicId_12);

        register(Constants.TldSchemaResourcePath_20,
                 Constants.TldSchemaPublicId_20);

        // web.xml    
        register(Constants.WebDtdResourcePath_22,
                 Constants.WebDtdPublicId_22);

        register(Constants.WebDtdResourcePath_23,
                 Constants.WebDtdPublicId_23);

        register(Constants.WebSchemaResourcePath_24,
                 Constants.WebSchemaPublicId_24);

        // Web Service
        register(Constants.J2eeWebServiceSchemaResourcePath_11,
                 Constants.J2eeWebServiceSchemaPublicId_11);

        register(Constants.J2eeWebServiceClientSchemaResourcePath_11,
                 Constants.J2eeWebServiceClientSchemaPublicId_11);

    }


    /**
     * Load the resource and add it to the 
     */
    protected static void register(String resourceURL, String resourcePublicId){

        URL url = DigesterFactory.class.getResource(resourceURL);
        schemaResolver.register(resourcePublicId , url.toString() );

    }


    /**
     * Turn on DTD and/or validation (based on the parser implementation)
     */
    protected static void turnOnValidation(Digester digester){
        URL url = DigesterFactory.class
                        .getResource(Constants.WebSchemaResourcePath_24);
        digester.setSchema(url.toString());     
    }


    /** 
     * Turn on schema AND DTD validation on Xerces parser.
     */
    protected static void turnOnXercesValidation(Digester digester){
        try{
            digester.setFeature(
                "http://apache.org/xml/features/validation/dynamic",
                true);
            digester.setFeature(
                "http://apache.org/xml/features/validation/schema",
                true);
        } catch(ParserConfigurationException e){
                // log("contextConfig.registerLocalSchema", e);
        } catch(SAXNotRecognizedException e){
                // log("contextConfig.registerLocalSchema", e);
        } catch(SAXNotSupportedException e){
                // log("contextConfig.registerLocalSchema", e);
        }
    }
}
