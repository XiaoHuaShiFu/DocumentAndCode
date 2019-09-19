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
 */ 

package org.apache.jasper.servlet;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.apache.jasper.Constants;

/**
 * Class loader for loading servlet class files (corresponding to JSP files) 
 * and tag handler class files (corresponding to tag files).
 *
 * @author Anil K. Vijendran
 * @author Harish Prabandham
 * @author Jean-Francois Arcand
 */
public class JasperLoader extends URLClassLoader {

    private PermissionCollection permissionCollection;
    private CodeSource codeSource;
    private String className;
    private ClassLoader parent;
    private SecurityManager securityManager;
    private PrivilegedLoadClass privLoadClass;

    public JasperLoader(URL[] urls, ClassLoader parent,
			PermissionCollection permissionCollection,
			CodeSource codeSource) {
	super(urls, parent);
	this.permissionCollection = permissionCollection;
	this.codeSource = codeSource;
	this.parent = parent;
        this.privLoadClass = new PrivilegedLoadClass();
	this.securityManager = System.getSecurityManager();
    }

    /**
     * Load the class with the specified name.  This method searches for
     * classes in the same manner as <code>loadClass(String, boolean)</code>
     * with <code>false</code> as the second argument.
     *
     * @param name Name of the class to be loaded
     *
     * @exception ClassNotFoundException if the class was not found
     */
    public Class loadClass(String name) throws ClassNotFoundException {

        return (loadClass(name, false));
    }

    /**
     * Load the class with the specified name, searching using the following
     * algorithm until it finds and returns the class.  If the class cannot
     * be found, returns <code>ClassNotFoundException</code>.
     * <ul>
     * <li>Call <code>findLoadedClass(String)</code> to check if the
     *     class has already been loaded.  If it has, the same
     *     <code>Class</code> object is returned.</li>
     * <li>If the <code>delegate</code> property is set to <code>true</code>,
     *     call the <code>loadClass()</code> method of the parent class
     *     loader, if any.</li>            
     * <li>Call <code>findClass()</code> to find this class in our locally
     *     defined repositories.</li>      
     * <li>Call the <code>loadClass()</code> method of our parent
     *     class loader, if any.</li>      
     * </ul>
     * If the class was found using the above steps, and the
     * <code>resolve</code> flag is <code>true</code>, this method will then
     * call <code>resolveClass(Class)</code> on the resulting Class object.
     *                                     
     * @param name Name of the class to be loaded
     * @param resolve If <code>true</code> then resolve the class
     *                                     
     * @exception ClassNotFoundException if the class was not found
     */                                    
    public Class loadClass(final String name, boolean resolve)
        throws ClassNotFoundException {

        Class clazz = null;                
                                           
        // (0) Check our previously loaded class cache
        clazz = findLoadedClass(name);     
        if (clazz != null) {               
            if (resolve)                   
                resolveClass(clazz);       
            return (clazz);        
        }                          
                          
        // (.5) Permission to access this class when using a SecurityManager
        if (securityManager != null) {     
            int dot = name.lastIndexOf('.');
            if (dot >= 0) {                
                try {        
                    // Do not call the security manager since by default, we grant that package.
                    if (!"org.apache.jasper.runtime".equalsIgnoreCase(name.substring(0,dot))){
                        securityManager.checkPackageAccess(name.substring(0,dot));
                    }
                } catch (SecurityException se) {
                    String error = "Security Violation, attempt to use " +
                        "Restricted Class: " + name;
                    se.printStackTrace();
                    throw new ClassNotFoundException(error);
                }                          
            }                              
        }

	// Class is in a package, delegate to thread context class loader
	if( !name.startsWith(Constants.JSP_PACKAGE_NAME) ) {
	    if (securityManager != null) {
                final ClassLoader classLoader = (ClassLoader)AccessController.doPrivileged(privLoadClass);
                try{
                    clazz = (Class)AccessController.doPrivileged(new PrivilegedExceptionAction(){
                        public Object run() throws Exception{
                            return classLoader.loadClass(name);
                        }
                    });
                } catch(PrivilegedActionException ex){
                    ex.getException().printStackTrace();

                }
            } else {
                clazz = parent.loadClass(name);
            }
            
	    if( resolve )
		resolveClass(clazz);
	    return clazz;
	}

	return findClass(name);
    }

    /**
     * Get the Permissions for a CodeSource.
     *
     * Since this ClassLoader is only used for a JSP page in
     * a web application context, we just return our preset
     * PermissionCollection for the web app context.
     *
     * @param CodeSource where the code was loaded from
     * @return PermissionCollection for CodeSource
     */
    public final PermissionCollection getPermissions(CodeSource codeSource) {
        return permissionCollection;
    }

    private class PrivilegedLoadClass implements PrivilegedAction {

        public Object run() {
            return Thread.currentThread().getContextClassLoader();
        }
    }
}
