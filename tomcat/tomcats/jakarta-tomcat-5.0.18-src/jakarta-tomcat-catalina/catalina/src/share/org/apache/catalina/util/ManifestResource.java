/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/catalina/src/share/org/apache/catalina/util/ManifestResource.java,v 1.4 2003/07/31 20:56:25 luehe Exp $
 * $Revision: 1.4 $
 * $Date: 2003/07/31 20:56:25 $
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
 */
package org.apache.catalina.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.ArrayList;

/**
 *  Representation of a Manifest file and its available extensions and
 *  required extensions
 *  
 * @author Greg Murray
 * @author Justyna Horwat
 * @version $Revision: 1.4 $ $Date: 2003/07/31 20:56:25 $
 * 
 */
public class ManifestResource {
    
    // ------------------------------------------------------------- Properties

    // These are the resource types for determining effect error messages
    public static final int SYSTEM = 1;
    public static final int WAR = 2;
    public static final int APPLICATION = 3;
    
    private HashMap availableExtensions = null;
    private ArrayList requiredExtensions = null;
    
    private String resourceName = null;
    private int resourceType = -1;
        
    public ManifestResource(String resourceName, Manifest manifest, 
                            int resourceType) {
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        processManifest(manifest);
    }
    
    /**
     * Gets the name of the resource
     *
     * @return The name of the resource
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Gets the map of available extensions
     *
     * @return Map of available extensions
     */
    public HashMap getAvailableExtensions() {
        return availableExtensions;
    }
    
    /**
     * Gets the list of required extensions
     *
     * @return List of required extensions
     */
    public ArrayList getRequiredExtensions() {
        return requiredExtensions;   
    }
    
    // --------------------------------------------------------- Public Methods

    /**
     * Gets the number of available extensions
     *
     * @return The number of available extensions
     */
    public int getAvailableExtensionCount() {
        return (availableExtensions != null) ? availableExtensions.size() : 0;
    }
    
    /**
     * Gets the number of required extensions
     *
     * @return The number of required extensions
     */
    public int getRequiredExtensionCount() {
        return (requiredExtensions != null) ? requiredExtensions.size() : 0;
    }
    
    /**
     * Convienience method to check if this <code>ManifestResource</code>
     * has an requires extensions.
     *
     * @return true if required extensions are present
     */
    public boolean requiresExtensions() {
        return (requiredExtensions != null) ? true : false;
    }
    
    /**
     * Convienience method to check if this <code>ManifestResource</code>
     * has an extension available.
     *
     * @param key extension identifier
     *
     * @return true if extension available
     */
    public boolean containsExtension(String key) {
        return (availableExtensions != null) ?
                availableExtensions.containsKey(key) : false;
    }
    
    /**
     * Returns <code>true</code> if all required extension dependencies
     * have been meet for this <code>ManifestResource</code> object.
     *
     * @return boolean true if all extension dependencies have been satisfied
     */
    public boolean isFulfilled() {
        if (requiredExtensions == null) {
            return false;
        }
        Iterator it = requiredExtensions.iterator();
        while (it.hasNext()) {
            Extension ext = (Extension)it.next();
            if (!ext.isFulfilled()) return false;            
        }
        return true;
    }
    
    public String toString() {

        StringBuffer sb = new StringBuffer("ManifestResource[");
        sb.append(resourceName);

        sb.append(", isFulfilled=");
        sb.append(isFulfilled() +"");
        sb.append(", requiredExtensionCount =");
        sb.append(getRequiredExtensionCount());
        sb.append(", availableExtensionCount=");
        sb.append(getAvailableExtensionCount());
        switch (resourceType) {
            case SYSTEM : sb.append(", resourceType=SYSTEM"); break;
            case WAR : sb.append(", resourceType=WAR"); break;
            case APPLICATION : sb.append(", resourceType=APPLICATION"); break;
        }
        sb.append("]");
        return (sb.toString());
    }


    // -------------------------------------------------------- Private Methods

    private void processManifest(Manifest manifest) {
        availableExtensions = getAvailableExtensions(manifest);
        requiredExtensions = getRequiredExtensions(manifest);
    }
    
    /**
     * Return the set of <code>Extension</code> objects representing optional
     * packages that are required by the application associated with the
     * specified <code>Manifest</code>.
     *
     * @param manifest Manifest to be parsed
     *
     * @return List of required extensions, or null if the application
     * does not require any extensions
     */
    private ArrayList getRequiredExtensions(Manifest manifest) {

        Attributes attributes = manifest.getMainAttributes();
        String names = attributes.getValue("Extension-List");
        if (names == null)
            return null;

        ArrayList extensionList = new ArrayList();
        names += " ";

        while (true) {

            int space = names.indexOf(' ');
            if (space < 0)
                break;
            String name = names.substring(0, space).trim();
            names = names.substring(space + 1);

            String value =
                attributes.getValue(name + "-Extension-Name");
            if (value == null)
                continue;
            Extension extension = new Extension();
            extension.setExtensionName(value);
            extension.setImplementationURL
                (attributes.getValue(name + "-Implementation-URL"));
            extension.setImplementationVendorId
                (attributes.getValue(name + "-Implementation-Vendor-Id"));
            String version = attributes.getValue(name + "-Implementation-Version");
            extension.setImplementationVersion(version);
            extension.setSpecificationVersion
                (attributes.getValue(name + "-Specification-Version"));
            extensionList.add(extension);
        }
        return extensionList;
    }
    
    /**
     * Return the set of <code>Extension</code> objects representing optional
     * packages that are bundled with the application associated with the
     * specified <code>Manifest</code>.
     *
     * @param manifest Manifest to be parsed
     *
     * @return Map of available extensions, or null if the web application
     * does not bundle any extensions
     */
    private HashMap getAvailableExtensions(Manifest manifest) {

        Attributes attributes = manifest.getMainAttributes();
        String name = attributes.getValue("Extension-Name");
        if (name == null)
            return null;

        HashMap extensionMap = new HashMap();

        Extension extension = new Extension();
        extension.setExtensionName(name);
        extension.setImplementationURL(
            attributes.getValue("Implementation-URL"));
        extension.setImplementationVendor(
            attributes.getValue("Implementation-Vendor"));
        extension.setImplementationVendorId(
            attributes.getValue("Implementation-Vendor-Id"));
        extension.setImplementationVersion(
            attributes.getValue("Implementation-Version"));
        extension.setSpecificationVersion(
            attributes.getValue("Specification-Version"));

        if (!extensionMap.containsKey(extension.getUniqueId())) {
            extensionMap.put(extension.getUniqueId(), extension);
        }

        return extensionMap;
    }
    
}
