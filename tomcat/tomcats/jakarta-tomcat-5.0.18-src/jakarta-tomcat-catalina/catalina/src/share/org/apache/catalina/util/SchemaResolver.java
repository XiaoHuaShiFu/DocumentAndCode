/* ====================================================================
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


import java.util.HashMap;

import org.apache.commons.digester.Digester;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

/**
 * This class implements a local SAX's <code>EntityResolver</code>. All
 * DTDs and schemas used to validate the web.xml file will re-directed
 * to a local file stored in the servlet-api.jar and jsp-api.jar.
 *
 * @author Jean-Francois Arcand
 */
public class SchemaResolver implements EntityResolver {

    /**
     * The disgester instance for which this class is the entity resolver.
     */
    protected Digester digester;


    /**
     * The URLs of dtds and schemas that have been registered, keyed by the
     * public identifier that corresponds.
     */
    protected HashMap entityValidator = new HashMap();


    /**
     * The public identifier of the DTD we are currently parsing under
     * (if any).
     */
    protected String publicId = null;


    /**
     * Extension to make the difference between DTD and Schema.
     */
    protected String schemaExtension = "xsd";


    /**
     * Create a new <code>EntityResolver</code> that will redirect
     * all remote dtds and schema to a locat destination.
     * @param schemaLocation the XML Schema used to validate xml instance.
     */
    public SchemaResolver(Digester digester) {
        this.digester = digester;
    }


    /**
     * Register the specified DTD/Schema URL for the specified public
     * identifier. This must be called before the first call to
     * <code>parse()</code>.
     *
     * When adding a schema file (*.xsd), only the name of the file
     * will get added. If two schemas with the same name are added,
     * only the last one will be stored.
     *
     * @param publicId Public identifier of the DTD to be resolved
     * @param entityURL The URL to use for reading this DTD
     */
     public void register(String publicId, String entityURL) {
         String key = publicId;
         if (publicId.indexOf(schemaExtension) != -1)
             key = publicId.substring(publicId.lastIndexOf('/')+1);
         entityValidator.put(key, entityURL);
     }


    /**
     * Resolve the requested external entity.
     *
     * @param publicId The public identifier of the entity being referenced
     * @param systemId The system identifier of the entity being referenced
     *
     * @exception SAXException if a parsing exception occurs
     *
     */
    public InputSource resolveEntity(String publicId, String systemId)
        throws SAXException {

        if (publicId != null) {
            this.publicId = publicId;
            digester.setPublicId(publicId);
        }

        // Has this system identifier been registered?
        String entityURL = null;
        if (publicId != null) {
            entityURL = (String) entityValidator.get(publicId);
        }

        // Redirect the schema location to a local destination
        String key = null;
        if (entityURL == null && systemId != null) {
            key = systemId.substring(systemId.lastIndexOf('/')+1);
            entityURL = (String)entityValidator.get(key);
        }

        if (entityURL == null) {
           return (null);
        }

        try {
            return (new InputSource(entityURL));
        } catch (Exception e) {
            throw new SAXException(e);
        }

    }

}
