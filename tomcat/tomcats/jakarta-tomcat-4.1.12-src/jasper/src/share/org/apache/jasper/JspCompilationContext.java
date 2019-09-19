/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/JspCompilationContext.java,v 1.6.2.6 2002/09/13 19:01:18 glenn Exp $
 * $Revision: 1.6.2.6 $
 * $Date: 2002/09/13 19:01:18 $
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
 */ 

package org.apache.jasper;

import org.apache.jasper.compiler.JspReader;
import org.apache.jasper.compiler.ServletWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import org.apache.jasper.compiler.Compiler;
import org.apache.jasper.servlet.JspServletWrapper;
import org.apache.jasper.servlet.JasperLoader;
import javax.servlet.ServletContext;
import org.apache.jasper.compiler.JspRuntimeContext;

/**
 * A place holder for various things that are used through out the JSP
 * engine. This is a per-request/per-context data structure. Some of
 * the instance variables are set at different points.
 *
 * Most of the path-related stuff is here - mangling names, versions, dirs,
 * loading resources and dealing with uris. 
 *
 * @author Anil K. Vijendran
 * @author Harish Prabandham
 * @author Pierre Delisle
 * @author Costin Manolache
 */
public class JspCompilationContext {

    protected String servletClassName;
    protected String jspUri;
    private boolean isErrPage;
    protected String servletPackageName = Constants.JSP_PACKAGE_NAME;
    protected String servletJavaFileName;
    protected String jspPath;
    protected String classFileName;
    protected String contentType;
    protected JspReader reader;
    protected ServletWriter writer;
    protected Options options;
    protected JspServletWrapper jsw;
    protected Compiler jspCompiler;
    protected String classPath;

    protected String baseURI;
    protected String outputDir;
    protected ServletContext context;
    protected URLClassLoader loader;

    protected JspRuntimeContext rctxt;

    protected int removed = 0;
    protected boolean reload = true;
    
    protected URLClassLoader jspLoader;
    protected URL [] outUrls = new URL[1];
    protected Class servletClass;

    // jspURI _must_ be relative to the context
    public JspCompilationContext(String jspUri, boolean isErrPage, Options options,
                                 ServletContext context, JspServletWrapper jsw,
                                 JspRuntimeContext rctxt) {
        this.jspUri = canonicalURI(jspUri);
        this.isErrPage = isErrPage;
        this.options=options;
        this.jsw=jsw;
        this.context=context;
        
        this.baseURI = jspUri.substring(0, jspUri.lastIndexOf('/') + 1);
        // hack fix for resolveRelativeURI
        if (baseURI == null) {
            baseURI = "/";
        } else if (baseURI.charAt(0) != '/') {
            // strip the basde slash since it will be combined with the
            // uriBase to generate a file
            baseURI = "/" + baseURI;
        }
        if (baseURI.charAt(baseURI.length() - 1) != '/') {
            baseURI += '/';
        }
        this.rctxt=rctxt;
    }

    /* ==================== Methods to override ==================== */
    
    /** ---------- Class path and loader ---------- */

    /**
     * The classpath that is passed off to the Java compiler. 
     */
    public String getClassPath() {
        if( classPath != null )
            return classPath;
        return rctxt.getClassPath();
    }

    /**
     * The classpath that is passed off to the Java compiler. 
     */
    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    /**
     * What class loader to use for loading classes while compiling
     * this JSP?
     */
    public ClassLoader getClassLoader() {
        if( loader != null )
            return loader;
        return rctxt.getParentClassLoader();
    }

    public void setClassLoader(URLClassLoader loader) {
	this.loader = loader;
    }

    /** ---------- Input/Output  ---------- */
    
    /**
     * The scratch directory to generate code into.
     */
    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir( String s ) {
        this.outputDir=s;
    }

    /**
     * Create a "Compiler" object based on some init param data. This
     * is not done yet. Right now we're just hardcoding the actual
     * compilers that are created. 
     */
    public Compiler createCompiler() throws JasperException {
        if (jspCompiler != null ) {
            return jspCompiler;
        }
        jspCompiler = new Compiler(this, jsw);
        return jspCompiler;
    }

    /** ---------- Access resources in the webapp ---------- */

    /** 
     * Get the full value of a URI relative to this compilations context
     * uses current file as the base.
     */
    public String resolveRelativeUri(String uri) {
        // sometimes we get uri's massaged from File(String), so check for
        // a root directory deperator char
        if (uri.startsWith("/") || uri.startsWith(File.separator)) {
            return uri;
        } else {
            return baseURI + uri;
        }
    }

    /**
     * Gets a resource as a stream, relative to the meanings of this
     * context's implementation.
     * @return a null if the resource cannot be found or represented 
     *         as an InputStream.
     */
    public java.io.InputStream getResourceAsStream(String res) {
        return context.getResourceAsStream(canonicalURI(res));
    }


    public URL getResource(String res) throws MalformedURLException {
        return context.getResource(canonicalURI(res));
    }

    /** 
     * Gets the actual path of a URI relative to the context of
     * the compilation.
     */
    public String getRealPath(String path) {
        if (context != null) {
            return context.getRealPath(path);
        }
        return path;
    }
    
    /* ==================== Common implementation ==================== */

    /**
     * Just the class name (does not include package name) of the
     * generated class. 
     */
    public String getServletClassName() {
        if (servletClassName != null) {
            return servletClassName;
        }
        int iSep = jspUri.lastIndexOf('/') + 1;
        int iEnd = jspUri.length();
        StringBuffer modifiedClassName = 
            new StringBuffer(jspUri.length() - iSep);
	if (!Character.isJavaIdentifierStart(jspUri.charAt(iSep))) {
	    // If the first char is not a legal Java letter or digit,
	    // prepend a '_'.
	    modifiedClassName.append('_');
	}
        for (int i = iSep; i < iEnd; i++) {
            char ch = jspUri.charAt(i);
            if (Character.isJavaIdentifierPart(ch)) {
                modifiedClassName.append(ch);
            } else if (ch == '.') {
                modifiedClassName.append('_');
            } else {
                modifiedClassName.append(mangleChar(ch));
            }
        }
        servletClassName = modifiedClassName.toString();
        return servletClassName;
    }

    public void setServletClassName(String servletClassName) {
        this.servletClassName = servletClassName;
    }
    
    /**
     * Path of the JSP URI. Note that this is not a file name. This is
     * the context rooted URI of the JSP file. 
     */
    public String getJspFile() {
        return jspUri;
    }

    /**
     * Are we processing something that has been declared as an
     * errorpage? 
     */
    public boolean isErrorPage() {
        return isErrPage;
    }

    public void setErrorPage(boolean isErrPage) {
        this.isErrPage = isErrPage;
    }

    /**
     * Package name for the generated class.
     */
    public String getServletPackageName() {
        return servletPackageName;
    }

    /**
     * The package name into which the servlet class is generated.
     */
    public void setServletPackageName(String servletPackageName) {
        this.servletPackageName = servletPackageName;
    }

    /**
     * Full path name of the Java file into which the servlet is being
     * generated. 
     */
    public String getServletJavaFileName() {

        if (servletJavaFileName != null) {
            return servletJavaFileName;
        }

        String outputDir = getOutputDir();
        servletJavaFileName = getServletClassName() + ".java";
 	if (outputDir != null && !outputDir.equals("")) {
            if( outputDir.endsWith("/" ) ) {
                servletJavaFileName = outputDir + servletJavaFileName;
            } else {
                servletJavaFileName = outputDir + "/" + servletJavaFileName;
            }
        }
	return servletJavaFileName;
    }

    public void setServletJavaFileName(String servletJavaFileName) {
        this.servletJavaFileName = servletJavaFileName;
    }

    /**
     * Get hold of the Options object for this context. 
     */
    public Options getOptions() {
        return options;
    }

    /**
     * Path of the JSP relative to the work directory.
     */
    public String getJspPath() {
        if (jspPath != null) {
            return jspPath;
        }
        String dirName = getJspFile();
        int pos = dirName.lastIndexOf('/');
        if (pos > 0) {
            dirName = dirName.substring(0, pos + 1);
        } else {
            dirName = "";
        }
        jspPath = dirName + getServletClassName() + ".java";
        if (jspPath.startsWith("/")) {
            jspPath = jspPath.substring(1);
        }
        return jspPath;
    }

    public String getClassFileName() {
        if (classFileName != null) {
            return classFileName;
        }

        String outputDir = getOutputDir();
        classFileName = getServletClassName() + ".class";
	if (outputDir != null && !outputDir.equals("")) {
	    classFileName = outputDir + File.separatorChar + classFileName;
        }
	return classFileName;
    }

    /**
     * Get the content type of this JSP.
     *
     * Content type includes content type and encoding.
     */
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Get the input reader for the JSP text. 
     */
    public JspReader getReader() { 
        return reader;
    }

    public void setReader(JspReader reader) {
        this.reader = reader;
    }

    /**
     * Where is the servlet being generated?
     */
    public ServletWriter getWriter() {
        return writer;
    }

    public void setWriter(ServletWriter writer) {
        this.writer = writer;
    }

    /**
     * Get the 'location' of the TLD associated with 
     * a given taglib 'uri'.
     * 
     * @return An array of two Strings. The first one is
     * real path to the TLD. If the path to the TLD points
     * to a jar file, then the second string is the
     * name of the entry for the TLD in the jar file.
     * Returns null if the uri is not associated to
     * a tag library 'exposed' in the web application.
     * A tag library is 'exposed' either explicitely in 
     * web.xml or implicitely via the uri tag in the TLD 
     * of a taglib deployed in a jar file (WEB-INF/lib).
     */
    public String[] getTldLocation(String uri) throws JasperException {
	String[] location = 
	    getOptions().getTldLocationsCache().getLocation(uri);
	return location;
    }

    /**
     * Are we keeping generated code around?
     */
    public boolean keepGenerated() {
        return getOptions().getKeepGenerated();
    }

    // ==================== Removal ==================== 

    public void incrementRemoved() {
        if (removed > 1) {
            jspCompiler.removeGeneratedFiles();
            if( rctxt != null )
                rctxt.removeWrapper(jspUri);
        }
        removed++;
    }


    public boolean isRemoved() {
        if (removed > 1 ) {
            return true;
        }
        return false;
    }

    // ==================== Compile and reload ====================
    
    public void compile() throws JasperException, FileNotFoundException {
        createCompiler();
        if (jspCompiler.isOutDated()) {
            try {
                jspCompiler.compile();
                reload = true;
            } catch (JasperException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new JasperException(
                    Constants.getString("jsp.error.unable.compile"),ex);
            }
        }
    }

    /** True if the servlet needs loading
     */
    public boolean isReload() {
        return reload;
    }

    // ==================== Manipulating the class ====================

    public Class load() 
        throws JasperException, FileNotFoundException
    {
        try {
            jspLoader = new JasperLoader
                (outUrls,
                 getServletPackageName() + "." + getServletClassName(),
                 rctxt.getParentClassLoader(),
                 rctxt.getPermissionCollection(),
                 rctxt.getCodeSource());
            
            servletClass = jspLoader.loadClass(
                 getServletPackageName() + "." + getServletClassName());
        } catch (ClassNotFoundException cex) {
            throw new JasperException(
                Constants.getString("jsp.error.unable.load"),cex);
        } catch (Exception ex) {
            throw new JasperException
                (Constants.getString("jsp.error.unable.compile"), ex);
        }
        removed = 0;
        reload = false;
        return servletClass;
    }

    public void createOutdir() {
        File outDirF = null;
        try {
            URL outURL = options.getScratchDir().toURL();
            String outURI = outURL.toString();
            if (outURI.endsWith("/")) {
                outURI = outURI 
                    + jspUri.substring(1,jspUri.lastIndexOf("/")+1);
            } else {
                outURI = outURI 
                    + jspUri.substring(0,jspUri.lastIndexOf("/")+1);
            }
            outURL = new URL(outURI);
            outDirF = new File(outURL.getFile());
            if (!outDirF.exists()) {
                outDirF.mkdirs();
            }
            this.setOutputDir(  outDirF.toString() + File.separator );
            
            outUrls[0] = new URL(outDirF.toURL().toString() + File.separator);
        } catch (Exception e) {
            throw new IllegalStateException("No output directory: " +
                                            e.getMessage());
        }
    }
    
    // ==================== Private methods ==================== 
    // Mangling, etc.
    
    /**
     * Mangle the specified character to create a legal Java class name.
     */
    private static final String mangleChar(char ch) {

	String s = Integer.toHexString(ch);
	int nzeros = 5 - s.length();
	char[] result = new char[6];
	result[0] = '_';
	for (int i = 1; i <= nzeros; i++) {
	    result[i] = '0';
        }
	for (int i = nzeros+1, j = 0; i < 6; i++, j++) {
	    result[i] = s.charAt(j);
        }
	return new String(result);
    }

    private static final boolean isPathSeparator(char c) {
       return (c == '/' || c == '\\');
    }

    private static final String canonicalURI(String s) {
       if (s == null) return null;
       StringBuffer result = new StringBuffer();
       final int len = s.length();
       int pos = 0;
       while (pos < len) {
           char c = s.charAt(pos);
           if ( isPathSeparator(c) ) {
               /*
                * multiple path separators.
                * 'foo///bar' -> 'foo/bar'
                */
               while (pos+1 < len && isPathSeparator(s.charAt(pos+1))) {
                   ++pos;
               }

               if (pos+1 < len && s.charAt(pos+1) == '.') {
                   /*
                    * a single dot at the end of the path - we are done.
                    */
                   if (pos+2 >= len) break;

                   switch (s.charAt(pos+2)) {
                       /*
                        * self directory in path
                        * foo/./bar -> foo/bar
                        */
                   case '/':
                   case '\\':
                       pos += 2;
                       continue;

                       /*
                        * two dots in a path: go back one hierarchy.
                        * foo/bar/../baz -> foo/baz
                        */
                   case '.':
                       // only if we have exactly _two_ dots.
                       if (pos+3 < len && isPathSeparator(s.charAt(pos+3))) {
                           pos += 3;
                           int separatorPos = result.length()-1;
                           while (separatorPos >= 0 && 
                                  ! isPathSeparator(result
                                                    .charAt(separatorPos))) {
                               --separatorPos;
                           }
                           if (separatorPos >= 0)
                               result.setLength(separatorPos);
                           continue;
                       }
                   }
               }
           }
           result.append(c);
           ++pos;
       }
       return result.toString();
    }

}

