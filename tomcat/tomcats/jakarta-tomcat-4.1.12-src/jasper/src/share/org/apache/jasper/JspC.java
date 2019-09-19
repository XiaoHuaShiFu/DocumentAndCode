/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/JspC.java,v 1.12.2.1 2002/08/21 17:54:24 kinman Exp $
 * $Revision: 1.12.2.1 $
 * $Date: 2002/08/21 17:54:24 $
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

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.jasper.compiler.JspReader;
import org.apache.jasper.compiler.ServletWriter;
import org.apache.jasper.compiler.Compiler;
import org.apache.jasper.compiler.TldLocationsCache;

import org.apache.jasper.servlet.JasperLoader;
import org.apache.jasper.servlet.JspCServletContext;

import org.apache.jasper.logging.Logger;
import org.apache.jasper.logging.JasperLogger;

/**
 * Shell for the jspc compiler.  Handles all options associated with the 
 * command line and creates compilation contexts which it then compiles
 * according to the specified options.
 *
 * This version can process files from a _single_ webapp at once, i.e.
 * a single docbase can be specified.
 *
 * It can be used as a Ant task using:
 <pre>
     &lt;taskdef classname="org.apache.jasper.JspC" name="jasper2" &gt;
        &lt;classpath&gt;
            &lt;pathelement location="${java.home}/../lib/tools.jar"/&gt;
            &lt;fileset dir="${ENV.CATALINA_HOME}/server/lib"&gt;
                &lt;include name="*.jar"/&gt;
            &lt;/fileset&gt;
            &lt;fileset dir="${ENV.CATALINA_HOME}/common/lib"&gt;
                &lt;include name="*.jar"/&gt;
            &lt;/fileset&gt;
            &lt;path refid="myjars"/&gt;
         &lt;/classpath&gt;
    &lt;/taskdef&gt;
    
    &lt;jasper2 verbose="0" 
             package="my.package"
             uriroot="${webapps.dir}/${webapp.name}"
             webXmlFragment="${build.dir}/generated_web.xml" 
             outputDir="${webapp.dir}/${webapp.name}/WEB-INF/src/my/package" /&gt;
 </pre>
 *
 * @author Danno Ferrin
 * @author Pierre Delisle
 * @author Costin Manolache
 */
public class JspC implements Options {

    public static final String DEFAULT_IE_CLASS_ID = 
            "clsid:8AD9C840-044E-11D1-B3E9-00805F499D93";
    
    public static final String SWITCH_VERBOSE = "-v";
    public static final String SWITCH_QUIET = "-q";
    public static final String SWITCH_OUTPUT_DIR = "-d";
    public static final String SWITCH_OUTPUT_SIMPLE_DIR = "-dd";
    public static final String SWITCH_IE_CLASS_ID = "-ieplugin";
    public static final String SWITCH_PACKAGE_NAME = "-p";
    public static final String SWITCH_CLASS_NAME = "-c";
    public static final String SWITCH_FULL_STOP = "--";
    public static final String SWITCH_COMPILE = "--compile";
    public static final String SWITCH_URI_BASE = "-uribase";
    public static final String SWITCH_URI_ROOT = "-uriroot";
    public static final String SWITCH_FILE_WEBAPP = "-webapp";
    public static final String SWITCH_WEBAPP_INC = "-webinc";
    public static final String SWITCH_WEBAPP_XML = "-webxml";
    public static final String SWITCH_MAPPED = "-mapped";
    public static final String SWITCH_DIE = "-die";
    public static final String SHOW_SUCCESS ="-s";
    public static final String LIST_ERRORS = "-l";

    public static final int NO_WEBXML = 0;
    public static final int INC_WEBXML = 10;
    public static final int ALL_WEBXML = 20;

    public static final int DEFAULT_DIE_LEVEL = 1;
    public static final int NO_DIE_LEVEL = 0;


    String classPath=null;
    URLClassLoader loader=null;

    // future direction
    //public static final String SWITCH_XML_OUTPUT = "-xml";
  
    
    boolean largeFile = false;
    boolean mappedFile = false;

    int jspVerbosityLevel = Logger.INFORMATION;

    File scratchDir;

    String ieClassId = DEFAULT_IE_CLASS_ID;

    String targetPackage;
    
    String targetClassName;

    String uriBase;

    String uriRoot;

    int dieLevel;
    boolean dieOnExit = false;
    static int die; // I realize it is duplication, but this is for
                    // the static main catch

    boolean compile=false;
    String compiler=null;
    
    boolean dirset;
    
    boolean classDebugInfo=true;
    
    Vector extensions;

    Vector pages = new Vector();

    // Generation of web.xml fragments
    String webxmlFile;
    int webxmlLevel;
    Writer mapout;
    CharArrayWriter servletout;
    CharArrayWriter mappingout;

    static PrintStream log;

    JspCServletContext context;
    
    /**
     * Cache for the TLD locations
     */
    private TldLocationsCache tldLocationsCache = null;

    private boolean listErrors = false;
    private boolean showSuccess = false;

    public boolean getKeepGenerated() {
        // isn't this why we are running jspc?
        return true;
    }
    
    public boolean getLargeFile() {
        return largeFile;
    }

    public boolean isPoolingEnabled() {
        return true;
    }

    /**
     * Are we supporting HTML mapped servlets?
     */
    public boolean getMappedFile() {
		return mappedFile;
    }

    // Off-line compiler, no need for security manager
    public Object getProtectionDomain() {
	return null;
    }
    
    public boolean getSendErrorToClient() {
        // implied send to System.err
        return true;
    }

    public void setClassDebugInfo( boolean b ) {
        classDebugInfo=b;
    }
    
    public boolean getClassDebugInfo() {
        // compile with debug info
        return classDebugInfo;
    }

    /**
     * Background compilation check intervals in seconds
     */
    public int getCheckInterval() {
        return 300;
    }

    /**
     * Is Jasper being used in development mode?
     */
    public boolean getDevelopment() {
        return false;
    }

    /**
     * JSP reloading check ?
     */
    public boolean getReloading() {
        return true;
    }

    public String getIeClassId() {
        return ieClassId;
    }
    
    public int getJspVerbosityLevel() {
        return jspVerbosityLevel;
    }

    public File getScratchDir() {
        return scratchDir;
    }

    public Class getJspCompilerPlugin() {
       // we don't compile, so this is meanlingless
        return null;
    }

    public String getJspCompilerPath() {
       // we don't compile, so this is meanlingless
        return null;
    }

    /**
     * Compiler to use.
     */
    public String getCompiler() {
        return compiler;
    }

    public void setCompiler(String c) {
        compiler=c;
    }


    public TldLocationsCache getTldLocationsCache() {
	return tldLocationsCache;
    }

    public String getJavaEncoding() {
	return "UTF-8";
    }

    public String getClassPath() {
        if( classPath != null )
            return classPath;
        return System.getProperty("java.class.path");
    }

    public JspC() {
        Constants.jasperLog = new JasperLogger();
    }

    // -------------------- Options --------------------
    public void setClassPath(String s) {
        classPath=s;
    }
    
    /** Base dir for the webapp. Used to generate class names and
     *  resolve includes
     */
    public void setUriroot( String s ) {
        if( s==null ) {
            uriRoot=s;
            return;
        }
        try {
            uriRoot=new File( s ).getCanonicalPath();
        } catch( Exception ex ) {
            uriRoot=s;
        }
    }

    public void setVerbose( int level ) {
        Constants.jasperLog.setVerbosityLevel(level);
    }
    
    public void setCompile( boolean b ) {
        compile=b;
    }

    public void setValidateXml( boolean b ) {
        org.apache.jasper.xmlparser.ParserUtils.validating=b;
    }
    
    public void setOutputDir( String s ) {
        if( s!= null ) {
            scratchDir=new File(new File(s).getAbsolutePath());
            dirset=true;
        } else {
            scratchDir=null;
        }
    }

    public void setPackage( String p ) {
        targetPackage=p;
    }

    /** Class name of the generated file ( without package ).
     *  Can only be used if a single file is converted.
     *  XXX Do we need this feature ?
     */
    public void setClassName( String p ) {
        targetClassName=p;
    }

    /** File where we generate a web.xml fragment with the class definitions.
     */
    public void setWebXmlFragment( String s ) {
        webxmlFile=s;
        webxmlLevel=INC_WEBXML;
    }
    
    /**
     * Resolve relative path, and create output directories.
     */
    void setupContext(JspCompilationContext clctxt) {
        // set up a scratch/output dir if none is provided
        if (scratchDir == null) {
            String temp = System.getProperty("java.io.tempdir");
            if (temp == null) {
                temp = "";
            }
            scratchDir = new File(new File(temp).getAbsolutePath());
        }

        String outputDir = scratchDir.getAbsolutePath();

        if (dirset) {
            int indexOfSlash = clctxt.getJspFile().lastIndexOf('/');
            
            /* String pathName = "";
            if (indexOfSlash != -1) {
                pathName = clctxt.getJspFile().substring(0, indexOfSlash);
            } */
            String tmpDir = outputDir + File.separatorChar; // + pathName;
            File f = new File(tmpDir);
            if (!f.exists()) {
                f.mkdirs();
            }
        }
        clctxt.setOutputDir( outputDir );
    }

    void initClassLoader( JspCompilationContext clctxt ) throws IOException {
        classPath = getClassPath();

        ClassLoader parent=this.getClass().getClassLoader();

        ArrayList urls = new ArrayList();
        File webappBase=new File(uriRoot);

        if( parent instanceof URLClassLoader ) {
            URLClassLoader uL=(URLClassLoader) parent;
            URL path[]=uL.getURLs();
            for( int i=0; i<path.length; i++ ) {
                urls.add( path[i] );
                classPath = classPath + File.pathSeparator +
                    path[i].getFile();
            }
        }

        if( parent instanceof org.apache.tools.ant.AntClassLoader ) {
            classPath= classPath + File.pathSeparator +
                ((org.apache.tools.ant.AntClassLoader)parent).getClasspath();
        }
        
        // Turn the classPath in URLs
        StringTokenizer tokenizer = new StringTokenizer(classPath, File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            String path = tokenizer.nextToken();
            try {
                File libFile = new File(path);
                urls.add(libFile.toURL());
            } catch (IOException ioe) {
                // Failing a toCanonicalPath on a file that
                // exists() should be a JVM regression test,
                // therefore we have permission to freak uot
                throw new RuntimeException(ioe.toString());
            }
        }

        if (webappBase.exists()) {
            File classes = new File(webappBase, "/WEB-INF/classes");
            try {
                if (classes.exists()) {
                    classPath = classPath + File.pathSeparator 
                        + classes.getCanonicalPath();
                    urls.add(classes.getCanonicalFile().toURL());
                }
            } catch (IOException ioe) {
                // failing a toCanonicalPath on a file that
                // exists() should be a JVM regression test,
                // therefore we have permission to freak out
                throw new RuntimeException(ioe.toString());
            }
            File lib = new File(webappBase, "/WEB-INF/lib");
            if (lib.exists() && lib.isDirectory()) {
                String[] libs = lib.list();
                for (int i = 0; i < libs.length; i++) {
                    if( libs[i].length() <5 ) continue;
                    String ext=libs[i].substring( libs[i].length() - 4 );
                    if( ! ".jar".equalsIgnoreCase( ext )) {
                        System.out.println("XXX bad jar " + libs[i]);
                        continue;
                    }
                    try {
                        File libFile = new File(lib, libs[i]);
                        classPath = classPath + File.pathSeparator 
                            + libFile.getCanonicalPath();
                        urls.add(libFile.getCanonicalFile().toURL());
                    } catch (IOException ioe) {
                        // failing a toCanonicalPath on a file that
                        // exists() should be a JVM regression test,
                        // therefore we have permission to freak out
                        throw new RuntimeException(ioe.toString());
                    }
                }
            }
        }

        // What is this ??
        urls.add(new File(clctxt.getRealPath("/")).getCanonicalFile().toURL());

        URL urlsA[]=new URL[urls.size()];
        urls.toArray(urlsA);
        loader = new URLClassLoader(urlsA, this.getClass().getClassLoader());
    }

    public void generateWebMapping( String file, JspCompilationContext clctxt )
        throws IOException 
    {
        String className = clctxt.getServletClassName();
        String packageName = clctxt.getServletPackageName();
        
        String thisServletName;
        if  ("".equals(packageName)) {
            thisServletName = className;
        } else {
            thisServletName = packageName + '.' + className;
        }
        
        if (servletout != null) {
            servletout.write("\n\t<servlet>\n\t\t<servlet-name>");
            servletout.write(thisServletName);
            servletout.write("</servlet-name>\n\t\t<servlet-class>");
            servletout.write(thisServletName);
            servletout.write("</servlet-class>\n\t</servlet>\n");
        }
        if (mappingout != null) {
            mappingout.write("\n\t<servlet-mapping>\n\t\t<servlet-name>");
            mappingout.write(thisServletName);
            mappingout.write("</servlet-name>\n\t\t<url-pattern>");
            mappingout.write(file.replace('\\', '/'));
            mappingout.write("</url-pattern>\n\t</servlet-mapping>\n");
            
        }
    }
    
    public boolean processFile(String file)
        throws JasperException
    {
        try {
            String jspUri=file.replace('\\','/');
            String baseDir = scratchDir.getCanonicalPath();
            this.setOutputDir( baseDir + jspUri.substring( 0, jspUri.lastIndexOf( '/' ) ) );
            JspCompilationContext clctxt = new JspCompilationContext
                ( jspUri, false,  this, context, null, null );

            /* Override the defaults */
            if ((targetClassName != null) && (targetClassName.length() > 0)) {
                clctxt.setServletClassName(targetClassName);
                targetClassName = null;
            }
            if (targetPackage != null) {
                clctxt.setServletPackageName(targetPackage);
            }
            
            setupContext(clctxt);

            if( loader==null )
                initClassLoader( clctxt );
            
            clctxt.setClassLoader(loader);
            clctxt.setClassPath(classPath);

            Compiler clc = clctxt.createCompiler();
            this.setOutputDir( baseDir );

            if( compile ) {
                // Generate both .class and .java
                if( clc.isOutDated() ) {
                    clc.compile();
                }
            } else {
                // Only generate .java, compilation is separated 
                // Don't compile if the .class file is newer than the .jsp file
                if( clc.isOutDated(false) ) {
                    clc.generateJava();
                } 
            }

            // Generate mapping
            generateWebMapping( file, clctxt );
            if ( showSuccess ) {
                log.println( "Built File: " + file );
            }
            return true;
        } catch (FileNotFoundException fne) {
            Constants.message("jspc.error.fileDoesNotExist", 
                              new Object[] {fne.getMessage()}, Logger.WARNING);
            throw new JasperException( fne );
        } catch (Exception e) {
            Constants.message("jspc.error.generalException", 
                    new Object[] {file, e}, Logger.ERROR);
            if ( listErrors ) {
	        log.println( "Error in File: " + file );
		return true;
            } else if (dieLevel != NO_DIE_LEVEL) {
                dieOnExit = true;
            }
            throw new JasperException( e );
        }
    }

    /** Find the WEB-INF dir by looking up in the directory tree.
     *  This is used if no explicit docbase is set, but only files.
     *  XXX Maybe we should require the docbase.
     */
    private void locateUriRoot( File f ) {
        String tUriBase = uriBase;
        if (tUriBase == null) {
            tUriBase = "/";
        }
        try {
            if (f.exists()) {
                f = new File(f.getCanonicalPath());
                while (f != null) {
                    File g = new File(f, "WEB-INF");
                    if (g.exists() && g.isDirectory()) {
                        uriRoot = f.getCanonicalPath();
                        uriBase = tUriBase;
                        Constants.message("jspc.implicit.uriRoot",
                                          new Object[] { uriRoot },
                                              Logger.INFORMATION);
                        break;
                    }
                    if (f.exists() && f.isDirectory()) {
                        tUriBase = "/" + f.getName() + "/" + tUriBase;
                    }
                    
                    String fParent = f.getParent();
                    if (fParent == null) {
                        f = new File(args[argPos]);
                        fParent = f.getParent();
                        if (fParent == null) {
                            fParent = File.separator;
                        }
                        uriRoot = new File(fParent).getCanonicalPath();
                        uriBase = "/";
                        break;
                    } else {
                        f = new File(fParent);
                    }
                    
                    // If there is no acceptible candidate, uriRoot will
                    // remain null to indicate to the CompilerContext to
                    // use the current working/user dir.
                }

                try {
                    File froot = new File(uriRoot);
                    uriRoot = froot.getCanonicalPath();
                } catch (IOException ioe) {
                    // if we cannot get the base, leave it null
                }
            }
        } catch (IOException ioe) {
            // since this is an optional default and a null value
            // for uriRoot has a non-error meaning, we can just
            // pass straight through
        }
    }

    /** Locate all jsp files in the webapp. Used if no explicit
     *  jsps are specified.
     */
    public void scanFiles( File base ) {
        Stack dirs = new Stack();
        dirs.push(base);
        if (extensions == null) {
            extensions = new Vector();
            extensions.addElement("jsp");
        }
        while (!dirs.isEmpty()) {
            String s = dirs.pop().toString();
            //System.out.println("--" + s);
            File f = new File(s);
            if (f.exists() && f.isDirectory()) {
                String[] files = f.list();
                String ext;
                for (int i = 0; i < files.length; i++) {
                    File f2 = new File(s, files[i]);
                    //System.out.println(":" + f2.getPath());
                    if (f2.isDirectory()) {
                        dirs.push(f2.getPath());
                        //System.out.println("++" + f2.getPath());
                    } else {
                        ext = files[i].substring(
                                                 files[i].lastIndexOf('.') + 1);
                        if (extensions.contains(ext)) {
                            //System.out.println(s + "?" + files[i]);
                            pages.addElement(
                                             s + File.separatorChar + files[i]);
                        } else {
                                    //System.out.println("not done:" + ext);
                        }
                    }
                }
            }
        }
    }

    private void initWebXml() {
        try {
            if (webxmlLevel >= INC_WEBXML) {
                File fmapings = new File(webxmlFile);
                mapout = new FileWriter(fmapings);
                servletout = new CharArrayWriter();
                mappingout = new CharArrayWriter();
            } else {
                mapout = null;
                servletout = null;
                mappingout = null;
            }
            if (webxmlLevel >= ALL_WEBXML) {
                mapout.write(Constants.getString("jspc.webxml.header"));
            } else if (webxmlLevel>= INC_WEBXML) {
                mapout.write(Constants.getString("jspc.webinc.header"));
            }
        } catch (IOException ioe) {
            mapout = null;
            servletout = null;
            mappingout = null;
        }
    }

    private void completeWebXml() {
        if (mapout != null) {
            try {
                servletout.writeTo(mapout);
                mappingout.writeTo(mapout);
                if (webxmlLevel >= ALL_WEBXML) {
                    mapout.write(Constants.getString("jspc.webxml.footer"));
                } else if (webxmlLevel >= INC_WEBXML) {
                    mapout.write(Constants.getString("jspc.webinc.footer"));
                }
                mapout.close();
            } catch (IOException ioe) {
                // noting to do if it fails since we are done with it
            }
        }
    }

    private void initServletContext() {
        try {
            context =new JspCServletContext
                (new PrintWriter(System.out),
                 new URL("file:" + uriRoot.replace('\\','/') + '/'));
            tldLocationsCache = new
                TldLocationsCache(context);
        } catch (MalformedURLException me) {
            System.out.println("**" + me);
        }
    }
    

    public void execute()  throws JasperException {

        if( uriRoot==null ) {
            if( pages.size() == 0 ) {
                throw new JasperException( "No uriRoot or files");
            }
            String firstJsp=(String)pages.elementAt( 0 );
            locateUriRoot( new File( firstJsp ) );
        }

        // No explicit page, we'll process all .jsp in the webapp
        if( pages.size() == 0 ) {
            scanFiles( new File( uriRoot ));
        }
            
        File uriRootF = new File(uriRoot);
        if (!uriRootF.exists() || !uriRootF.isDirectory()) {
            throw new JasperException(Constants.getString("jsp.error.jspc.uriroot_not_dir"));
        }
                
        if( context==null )
            initServletContext();
                                                                       
        initWebXml();

        Enumeration e = pages.elements();
        while (e.hasMoreElements()) {
            String nextjsp = e.nextElement().toString();
            try {
                File fjsp = new File(nextjsp);
                if (!fjsp.exists()) {
                    Constants.message("jspc.error.fileDoesNotExist", 
                                      new Object[] {fjsp}, Logger.WARNING);
                    continue;
                }
                String s = fjsp.getCanonicalPath();
                //System.out.println("**" + s);
                if (s.startsWith(uriRoot)) {
                    nextjsp = s.substring(uriRoot.length());
                }
            } catch (IOException ioe) {
                // if we got problems dont change the file name
            }

            if (nextjsp.startsWith("." + File.separatorChar)) {
                nextjsp = nextjsp.substring(2);
            }

            processFile(nextjsp);
        }

        completeWebXml();
    }


    // ==================== CLI support ==================== 
    
    int argPos;
    // value set by beutifully obsfucscated java
    boolean fullstop = false;
    String args[];

    public static void main(String arg[]) {
        if (arg.length == 0) {
           System.out.println(Constants.getString("jspc.usage"));
        } else {
            try {
                log=System.out;
                JspC jspc = new JspC();
                jspc.setArgs(arg);
                jspc.execute();
            } catch (JasperException je) {
                System.err.print("error:");
                System.err.println(je.getMessage());
                if (die != NO_DIE_LEVEL) {
                    System.exit(die);
                }
            }
        }
    }

    private String nextArg() {
        if ((argPos >= args.length)
            || (fullstop = SWITCH_FULL_STOP.equals(args[argPos]))) {
            return null;
        } else {
            return args[argPos++];
        }
    }
        
    private String nextFile() {
        if (fullstop) argPos++;
        if (argPos >= args.length) {
            return null;
        } else {
            return args[argPos++];
        }
    }

    void setArgs(String[] arg) {
        args = arg;
        String tok;

        int verbosityLevel = Logger.WARNING;
        dieLevel = NO_DIE_LEVEL;
        die = dieLevel;

        while ((tok = nextArg()) != null) {
            if (tok.equals(SWITCH_QUIET)) {
                verbosityLevel = Logger.WARNING;
            } else if (tok.equals(SWITCH_VERBOSE)) {
                verbosityLevel = Logger.INFORMATION;
            } else if (tok.startsWith(SWITCH_VERBOSE)) {
                try {
                    verbosityLevel
                     = Integer.parseInt(tok.substring(SWITCH_VERBOSE.length()));
                } catch (NumberFormatException nfe) {
                    log.println(
                        "Verbosity level " 
                        + tok.substring(SWITCH_VERBOSE.length()) 
                        + " is not valid.  Option ignored.");
                }
            } else if (tok.equals(SWITCH_OUTPUT_DIR)) {
                tok = nextArg();
                setOutputDir( tok );
            } else if (tok.equals(SWITCH_OUTPUT_SIMPLE_DIR)) {
                tok = nextArg();
                if (tok != null) {
                    scratchDir = new File(new File(tok).getAbsolutePath());
                    dirset = false;
                } else {
                    // either an in-java call with an explicit null
                    // or a "-d --" sequence should cause this,
                    // which would mean default handling
                    /* no-op */
                    scratchDir = null;
                }
            } else if (tok.equals(SWITCH_PACKAGE_NAME)) {
                targetPackage = nextArg();
            } else if (tok.equals(SWITCH_COMPILE)) {
                compile=true;
            } else if (tok.equals(SWITCH_CLASS_NAME)) {
                targetClassName = nextArg();
            } else if (tok.equals(SWITCH_URI_BASE)) {
                uriBase=nextArg();
            } else if (tok.equals(SWITCH_URI_ROOT)) {
                setUriroot( nextArg());
            } else if (tok.equals(SWITCH_FILE_WEBAPP)) {
                setUriroot( nextArg());
            } else if ( tok.equals( SHOW_SUCCESS ) ) {
                showSuccess = true;
            } else if ( tok.equals( LIST_ERRORS ) ) {
                listErrors = true;
            } else if (tok.equals(SWITCH_WEBAPP_INC)) {
                webxmlFile = nextArg();
                if (webxmlFile != null) {
                    webxmlLevel = INC_WEBXML;
                }
            } else if (tok.equals(SWITCH_WEBAPP_XML)) {
                webxmlFile = nextArg();
                if (webxmlFile != null) {
                    webxmlLevel = ALL_WEBXML;
                }
            } else if (tok.equals(SWITCH_MAPPED)) {
                mappedFile = true;
            } else if (tok.startsWith(SWITCH_DIE)) {
                try {
                    dieLevel = Integer.parseInt(
                        tok.substring(SWITCH_DIE.length()));
                } catch (NumberFormatException nfe) {
                    dieLevel = DEFAULT_DIE_LEVEL;
                }
                die = dieLevel;
            } else {
                //pushBackArg();
                if (!fullstop) {
                    argPos--;
                }
                // Not a recognized Option?  Start treting them as JSP Pages
                break;
            }
        }

        // Add all extra arguments to the list of files
        while( true ) {
            String file = nextFile();
            if( file==null ) break;
            pages.addElement( file );
        }
        
        Constants.jasperLog.setVerbosityLevel(verbosityLevel);
    }

    /**
     * allows user to set where the log goes other than System.out
     * @param log
     */
    public static void setLog( PrintStream log ) {
	    JspC.log = log;
    }
    
}

