/*
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
package org.apache.catalina.security;

import java.security.Security;
import org.apache.catalina.startup.CatalinaProperties;

/**
 * Util class to protect Catalina against package access and insertion.
 * The code are been moved from Catalina.java
 * @author the Catalina.java authors
 * @author Jean-Francois Arcand
 */
public final class SecurityConfig{
    private static SecurityConfig singleton = null;

    private static org.apache.commons.logging.Log log=
        org.apache.commons.logging.LogFactory.getLog( SecurityConfig.class );

    
    private final static String PACKAGE_ACCESS =  "sun.,"
                                                + "org.apache.catalina." 
                                                + ",org.apache.jasper."
                                                + ",org.apache.coyote."
                                                + ",org.apache.tomcat.";
    
    private final static String PACKAGE_DEFINITION= "java.,sun."
                                                + ",org.apache.catalina." 
                                                + ",org.apache.coyote."
                                                + ",org.apache.tomcat."
                                                + ",org.apache.jasper.";
    /**
     * List of protected package from conf/catalina.properties
     */
    private String packageDefinition;
    
    
    /**
     * List of protected package from conf/catalina.properties
     */
    private String packageAccess; 
    
    
    /**
     * Create a single instance of this class.
     */
    private SecurityConfig(){  
        try{
            packageDefinition = CatalinaProperties.getProperty("package.definition");
            packageAccess = CatalinaProperties.getProperty("package.access");
        } catch (java.lang.Exception ex){
            if (log.isDebugEnabled()){
                log.debug("Unable to load properties using CatalinaProperties", ex); 
            }            
        }
    }
    
    
    /**
     * Returns the singleton instance of that class.
     * @return an instance of that class.
     */
    public static SecurityConfig newInstance(){
        if (singleton == null){
            singleton = new SecurityConfig();
        }
        return singleton;
    }
    
    
    /**
     * Set the security package.access value.
     */
    public void setPackageAccess(){
        // If catalina.properties is missing, protect all by default.
        if (packageAccess == null){
            setSecurityProperty("package.access", PACKAGE_ACCESS);   
        } else {
            setSecurityProperty("package.access", packageAccess);   
        }
    }
    
    
    /**
     * Set the security package.definition value.
     */
     public void setPackageDefinition(){
        // If catalina.properties is missing, protect all by default.
         if (packageDefinition == null){
            setSecurityProperty("package.definition", PACKAGE_DEFINITION);
         } else {
            setSecurityProperty("package.definition", packageDefinition);
         }
    }
     
     
    /**
     * Set the proper security property
     * @param properties the package.* property.
     */
    private final void setSecurityProperty(String properties, String packageList){
        if (System.getSecurityManager() != null){
            String definition = Security.getProperty(properties);
            if( definition != null && definition.length() > 0 ){
                definition += ",";
            }

            Security.setProperty(properties,
                // FIX ME package "javax." was removed to prevent HotSpot
                // fatal internal errors
                definition + packageList);      
        }
    }
    
    
}




