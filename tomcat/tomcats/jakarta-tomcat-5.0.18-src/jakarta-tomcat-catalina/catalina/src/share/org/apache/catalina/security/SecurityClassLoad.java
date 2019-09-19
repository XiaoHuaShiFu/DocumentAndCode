/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/catalina/src/share/org/apache/catalina/security/SecurityClassLoad.java,v 1.15 2004/01/07 05:33:28 billbarker Exp $
 * $Revision: 1.15 $
 * $Date: 2004/01/07 05:33:28 $
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


package org.apache.catalina.security;

/**
 * Static class used to preload java classes when using the
 * Java SecurityManager so that the defineClassInPackage
 * RuntimePermission does not trigger an AccessControlException.
 *
 * @author Glenn L. Nielsen
 * @author Jean-Francois Arcand
 * @version $Revision: 1.15 $ $Date: 2004/01/07 05:33:28 $
 */

public final class SecurityClassLoad {

    public static void securityClassLoad(ClassLoader loader)
        throws Exception {

        if( System.getSecurityManager() == null ){
            return;
        }
        
        loadCorePackage(loader);
        loadLoaderPackage(loader);
        loadSessionPackage(loader);
        loadUtilPackage(loader);
        loadJavaxPackage(loader);
        loadCoyotePackage(loader);        
        loadHttp11Package(loader);        
        loadJkPackage(loader);
    }
    
    
    private final static void loadCorePackage(ClassLoader loader)
        throws Exception {
        String basePackage = "org.apache.catalina.";
        loader.loadClass
            (basePackage +
             "core.ApplicationContextFacade$1");
        loader.loadClass
            (basePackage +
             "core.ApplicationDispatcher$PrivilegedForward");
        loader.loadClass
            (basePackage +
             "core.ApplicationDispatcher$PrivilegedInclude");
        loader.loadClass
            (basePackage +
             "core.ContainerBase$PrivilegedAddChild");
        loader.loadClass
            (basePackage +
             "core.StandardWrapper$1");
    }
    
    
    private final static void loadLoaderPackage(ClassLoader loader)
        throws Exception {
        String basePackage = "org.apache.catalina.";
        loader.loadClass
            (basePackage +
             "loader.WebappClassLoader$PrivilegedFindResource");
    }
    
    
    private final static void loadSessionPackage(ClassLoader loader)
        throws Exception {
        String basePackage = "org.apache.catalina.";
        loader.loadClass
            (basePackage + "session.StandardSession");
        loader.loadClass
            (basePackage +
             "session.StandardSession$1");
        loader.loadClass
            (basePackage +
             "session.StandardManager$PrivilegedDoUnload");
    }
    
    
    private final static void loadUtilPackage(ClassLoader loader)
        throws Exception {
        String basePackage = "org.apache.catalina.";
        loader.loadClass
            (basePackage + "util.URL");
        loader.loadClass(basePackage + "util.Enumerator");
    }
    
    
    private final static void loadJavaxPackage(ClassLoader loader)
        throws Exception {
        loader.loadClass("javax.servlet.http.Cookie");
    }
    

    private final static void loadHttp11Package(ClassLoader loader)
        throws Exception {
        String basePackage = "org.apache.coyote.http11.";
        loader.loadClass(basePackage + "Http11Processor$1");
        loader.loadClass(basePackage + "InternalOutputBuffer$1");
        loader.loadClass(basePackage + "InternalOutputBuffer$2");
    }
    
    
    private final static void loadCoyotePackage(ClassLoader loader)
        throws Exception {
        String basePackage = "org.apache.coyote.tomcat5.";
        loader.loadClass
            (basePackage +
             "CoyoteRequestFacade$GetAttributePrivilegedAction");
        loader.loadClass
            (basePackage +
             "CoyoteRequestFacade$GetParameterMapPrivilegedAction");
        loader.loadClass
            (basePackage +
             "CoyoteRequestFacade$GetRequestDispatcherPrivilegedAction");
        loader.loadClass
            (basePackage +
             "CoyoteRequestFacade$GetParameterPrivilegedAction");
        loader.loadClass
            (basePackage +
             "CoyoteRequestFacade$GetParameterNamesPrivilegedAction");
        loader.loadClass
            (basePackage +
             "CoyoteRequestFacade$GetParameterValuePrivilegedAction");
        loader.loadClass
            (basePackage +
             "CoyoteRequestFacade$GetCharacterEncodingPrivilegedAction");
        loader.loadClass
            (basePackage +
             "CoyoteRequestFacade$GetHeadersPrivilegedAction");
        loader.loadClass
            (basePackage +
             "CoyoteRequestFacade$GetHeaderNamesPrivilegedAction");  
        loader.loadClass
            (basePackage +
             "CoyoteRequestFacade$GetCookiesPrivilegedAction");
        loader.loadClass
            (basePackage +
             "CoyoteRequestFacade$GetLocalePrivilegedAction");
        loader.loadClass
            (basePackage +
             "CoyoteRequestFacade$GetLocalesPrivilegedAction");
        loader.loadClass
            (basePackage +
             "CoyoteResponseFacade$SetContentTypePrivilegedAction");
        loader.loadClass
            (basePackage + 
             "CoyoteResponseFacade$DateHeaderPrivilegedAction");
        loader.loadClass
            (basePackage +
             "CoyoteRequestFacade$GetSessionPrivilegedAction");
        loader.loadClass
            (basePackage +
             "CoyoteResponseFacade$1");
        loader.loadClass
            (basePackage +
             "OutputBuffer$1");
        loader.loadClass
            (basePackage +
             "CoyoteInputStream$1");
        loader.loadClass
            (basePackage +
             "CoyoteInputStream$2");
        loader.loadClass
            (basePackage +
             "CoyoteInputStream$3");
        loader.loadClass
            (basePackage +
             "CoyoteInputStream$4");
        loader.loadClass
            (basePackage +
             "CoyoteInputStream$5");
        loader.loadClass
            (basePackage +
             "InputBuffer$1");
        loader.loadClass
            (basePackage +
             "CoyoteResponse$1");
        loader.loadClass
            (basePackage +
             "CoyoteResponse$2");
        loader.loadClass
            (basePackage +
             "CoyoteResponse$3");
    }

    private final static void loadJkPackage(ClassLoader loader)
        throws Exception {
        String basePackage = "org.apache.jk.";
        loader.loadClass
            (basePackage +
             "server.JkCoyoteHandler$1");
        loader.loadClass
            (basePackage +
             "server.JkCoyoteHandler$StatusLinePrivilegedAction");
    }
}

