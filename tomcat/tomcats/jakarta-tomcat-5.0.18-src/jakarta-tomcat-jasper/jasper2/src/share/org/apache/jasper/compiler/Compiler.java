/*
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
package org.apache.jasper.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.Options;
import org.apache.jasper.servlet.JspServletWrapper;
import org.apache.jasper.util.SystemLogHandler;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PatternSet;

/**
 * Main JSP compiler class. This class uses Ant for compiling.
 *
 * @author Anil K. Vijendran
 * @author Mandar Raje
 * @author Pierre Delisle
 * @author Kin-man Chung
 * @author Remy Maucherat
 * @author Mark Roth
 */
public class Compiler {
    private static org.apache.commons.logging.Log log=
        org.apache.commons.logging.LogFactory.getLog( Compiler.class );

    // ----------------------------------------------------------------- Static


    // Some javac are not thread safe; use a lock to serialize compilation, 
    static Object javacLock = new Object();


    // ----------------------------------------------------- Instance Variables


    protected JspCompilationContext ctxt;

    private ErrorDispatcher errDispatcher;
    private PageInfo pageInfo;
    private JspServletWrapper jsw;
    private JasperAntLogger logger;
    private TagFileProcessor tfp;

    protected Project project=null;

    protected Options options;

    protected Node.Nodes pageNodes;
    // ------------------------------------------------------------ Constructor


    public Compiler(JspCompilationContext ctxt) {
        this(ctxt, null);
    }


    public Compiler(JspCompilationContext ctxt, JspServletWrapper jsw) {
        this.jsw = jsw;
        this.ctxt = ctxt;
        this.options = ctxt.getOptions();
    }

    // Lazy eval - if we don't need to compile we probably don't need the project
    private Project getProject() {

        if( project!=null ) return project;

        // Initializing project
        project = new Project();
        logger = new JasperAntLogger();
        logger.setOutputPrintStream(System.out);
        logger.setErrorPrintStream(System.err);
	logger.setMessageOutputLevel(Project.MSG_INFO);
        project.addBuildListener( logger);
	if (System.getProperty("catalina.home") != null) {
            project.setBasedir( System.getProperty("catalina.home"));
        }
        
        if( options.getCompiler() != null ) {
            if( log.isDebugEnabled() )
                log.debug("Compiler " + options.getCompiler() );
            project.setProperty("build.compiler", options.getCompiler() );
        }
        project.init();
        return project;
    }

    class JasperAntLogger extends DefaultLogger {

        private StringBuffer reportBuf = new StringBuffer();

        protected void printMessage(final String message,
                                    final PrintStream stream,
                                    final int priority) {
        }

        protected void log(String message) {
            reportBuf.append(message);
            reportBuf.append(System.getProperty("line.separator"));
        }

        protected String getReport() {
            String report = reportBuf.toString();
            reportBuf.setLength(0);
            return report;
        }
    }

    // --------------------------------------------------------- Public Methods


    /** 
     * Compile the jsp file into equivalent servlet in .java file
     * @return a smap for the current JSP page, if one is generated,
     *         null otherwise
     */
    private String generateJava()
        throws Exception
    {
	String smapStr = null;

        long t1=System.currentTimeMillis();

	// Setup page info area
	pageInfo = new PageInfo(new BeanRepository(ctxt.getClassLoader(),
						   errDispatcher));

	JspConfig jspConfig = options.getJspConfig();
	JspConfig.JspProperty jspProperty =
			jspConfig.findJspProperty(ctxt.getJspFile());

        /*
         * If the current uri is matched by a pattern specified in
         * a jsp-property-group in web.xml, initialize pageInfo with
         * those properties.
         */
        pageInfo.setELIgnored(JspUtil.booleanValue(jspProperty.isELIgnored()));
        pageInfo.setScriptingInvalid(JspUtil.booleanValue(jspProperty.isScriptingInvalid()));
        if (jspProperty.getIncludePrelude() != null) {
            pageInfo.setIncludePrelude(jspProperty.getIncludePrelude());
        }
	if (jspProperty.getIncludeCoda() != null) {
	    pageInfo.setIncludeCoda(jspProperty.getIncludeCoda());
	}
        String javaFileName = ctxt.getServletJavaFileName();

        // Setup the ServletWriter
        String javaEncoding = ctxt.getOptions().getJavaEncoding();
	OutputStreamWriter osw = null; 
	try {
	    osw = new OutputStreamWriter(new FileOutputStream(javaFileName),
					 javaEncoding);
	} catch (UnsupportedEncodingException ex) {
            errDispatcher.jspError("jsp.error.needAlternateJavaEncoding", javaEncoding);
	}

	ServletWriter writer = new ServletWriter(new PrintWriter(osw));
        ctxt.setWriter(writer);

        // Reset the temporary variable counter for the generator.
        JspUtil.resetTemporaryVariableName();

	// Parse the file
	ParserController parserCtl = new ParserController(ctxt, this);
	pageNodes = parserCtl.parse(ctxt.getJspFile());

	if (ctxt.isPrototypeMode()) {
	    // generate prototype .java file for the tag file
	    Generator.generate(writer, this, pageNodes);
            writer.close();
	    return null;
	}

	// Validate and process attributes
	Validator.validate(this, pageNodes);

        long t2=System.currentTimeMillis();
	// Dump out the page (for debugging)
	// Dumper.dump(pageNodes);

	// Collect page info
	Collector.collect(this, pageNodes);

	// Compile (if necessary) and load the tag files referenced in
	// this compilation unit.
	tfp = new TagFileProcessor();
	tfp.loadTagFiles(this, pageNodes);

        long t3=System.currentTimeMillis();
        
	// Determine which custom tag needs to declare which scripting vars
	ScriptingVariabler.set(pageNodes, errDispatcher);

	// Optimizations by Tag Plugins
	TagPluginManager tagPluginManager = options.getTagPluginManager();
	tagPluginManager.apply(pageNodes, errDispatcher, pageInfo);

        // Optimization: concatenate contiguous template texts.
        TextOptimizer.concatenate(this, pageNodes);

	// Generate static function mapper codes.
	ELFunctionMapper.map(this, pageNodes);

	// generate servlet .java file
	Generator.generate(writer, this, pageNodes);
        writer.close();
        // The writer is only used during the compile, dereference
        // it in the JspCompilationContext when done to allow it
        // to be GC'd and save memory.
        ctxt.setWriter(null);

        long t4=System.currentTimeMillis();
        if( t4-t1 > 500 ) {
            log.debug("Generated "+ javaFileName + " total=" +
                      (t4-t1) + " generate=" + ( t4-t3 ) + " validate=" + ( t2-t1 ));
        }
        
        // JSR45 Support
        if (! options.isSmapSuppressed()) {
            smapStr = SmapUtil.generateSmap(ctxt, pageNodes);
        }

	// If any proto type .java and .class files was generated,
	// the prototype .java may have been replaced by the current
	// compilation (if the tag file is self referencing), but the
	// .class file need to be removed, to make sure that javac would
	// generate .class again from the new .java file just generated.

	tfp.removeProtoTypeFiles(ctxt.getClassFileName());

	return smapStr;
    }

    /** 
     * Compile the servlet from .java file to .class file
     */
    private void generateClass(String smap)
        throws FileNotFoundException, JasperException, Exception {

        long t1=System.currentTimeMillis();
        String javaEncoding = ctxt.getOptions().getJavaEncoding();
        String javaFileName = ctxt.getServletJavaFileName();
        String classpath = ctxt.getClassPath(); 

        String sep = System.getProperty("path.separator");

        StringBuffer errorReport = new StringBuffer();
        boolean success = true;

        StringBuffer info=new StringBuffer();
        info.append("Compile: javaFileName=" + javaFileName + "\n" );
        info.append("    classpath=" + classpath + "\n" );

        // Start capturing the System.err output for this thread
        SystemLogHandler.setThread();

        // Initializing javac task
        getProject();
        Javac javac = (Javac) project.createTask("javac");

        // Initializing classpath
        Path path = new Path(project);
        path.setPath(System.getProperty("java.class.path"));
        info.append("    cp=" + System.getProperty("java.class.path") + "\n");
        StringTokenizer tokenizer = new StringTokenizer(classpath, sep);
        while (tokenizer.hasMoreElements()) {
            String pathElement = tokenizer.nextToken();
            File repository = new File(pathElement);
            path.setLocation(repository);
            info.append("    cp=" + repository + "\n");
        }

        if( log.isDebugEnabled() )
            log.debug( "Using classpath: " + System.getProperty("java.class.path") + sep
                       + classpath);
        
        // Initializing sourcepath
        Path srcPath = new Path(project);
        srcPath.setLocation(options.getScratchDir());

        info.append("    work dir=" + options.getScratchDir() + "\n");

        // Initialize and set java extensions
        String exts = System.getProperty("java.ext.dirs");
        if (exts != null) {
            Path extdirs = new Path(project);
            extdirs.setPath(exts);
            javac.setExtdirs(extdirs);
            info.append("    extension dir=" + exts + "\n");
        }

        // Configure the compiler object
        javac.setEncoding(javaEncoding);
        javac.setClasspath(path);
        javac.setDebug(ctxt.getOptions().getClassDebugInfo());
        javac.setSrcdir(srcPath);
        javac.setOptimize(! ctxt.getOptions().getClassDebugInfo() );
        javac.setFork(ctxt.getOptions().getFork());
        info.append("    srcDir=" + srcPath + "\n" );

        // Set the Java compiler to use
        if (options.getCompiler() != null) {
            javac.setCompiler(options.getCompiler());
            info.append("    compiler=" + options.getCompiler() + "\n");
        }

        // Build includes path
        PatternSet.NameEntry includes = javac.createInclude();

        includes.setName(ctxt.getJavaPath());
        info.append("    include="+ ctxt.getJavaPath() + "\n" );

        try {
            if (ctxt.getOptions().getFork()) {
                javac.execute();
            } else {
                synchronized(javacLock) {
                    javac.execute();
                }
            }
        } catch (BuildException e) {
            log.error( "Javac exception ", e);
            log.error( "Env: " + info.toString());
            success = false;
        }

        errorReport.append(logger.getReport());

        // Stop capturing the System.err output for this thread
        String errorCapture = SystemLogHandler.unsetThread();
        if (errorCapture != null) {
            errorReport.append(System.getProperty("line.separator"));
            errorReport.append(errorCapture);
        }

        if (!ctxt.keepGenerated()) {
            File javaFile = new File(javaFileName);
            javaFile.delete();
        }

        if (!success) {
            log.error( "Error compiling file: " + javaFileName + " " + errorReport);
            errDispatcher.javacError(errorReport.toString(), javaFileName, pageNodes);
        }

        long t2=System.currentTimeMillis();
        if( t2-t1 > 500 ) {
            log.debug( "Compiled " + javaFileName + " " + (t2-t1));
        }

	if (ctxt.isPrototypeMode()) {
	    return;
	}

        // JSR45 Support
        if (! options.isSmapSuppressed()) {
            SmapUtil.installSmap(ctxt.getClassFileName(), smap);
        }
    }

    /** 
     * Compile the jsp file from the current engine context
     */
    public void compile()
        throws FileNotFoundException, JasperException, Exception
    {
        compile(true);
    }

    /**
     * Compile the jsp file from the current engine context.  As an side-
     * effect, tag files that are referenced by this page are also compiled.
     * @param compileClass If true, generate both .java and .class file
     *                     If false, generate only .java file
     */
    public void compile(boolean compileClass)
        throws FileNotFoundException, JasperException, Exception
    {
        if (errDispatcher == null) {
            this.errDispatcher = new ErrorDispatcher();
        }

        try {
            String smap = generateJava();
            if (compileClass) {
                generateClass(smap);
            }
        } finally {
            if (tfp != null) {
                tfp.removeProtoTypeFiles(null);
            }
            // Make sure these object which are only used during the
            // generation and compilation of the JSP page get
            // dereferenced so that they can be GC'd and reduce the
            // memory footprint.
            tfp = null;
            errDispatcher = null;
            logger = null;
            project = null;
            pageInfo = null;
            pageNodes = null;
            if (ctxt.getWriter() != null) {
                ctxt.getWriter().close();
                ctxt.setWriter(null);
            }
        }
    }

    /**
     * This is a protected method intended to be overridden by 
     * subclasses of Compiler. This is used by the compile method
     * to do all the compilation. 
     */
    public boolean isOutDated() {
        return isOutDated( true );
    }

    /**
     * Determine if a compilation is necessary by checking the time stamp
     * of the JSP page with that of the corresponding .class or .java file.
     * If the page has dependencies, the check is also extended to its
     * dependeants, and so on.
     * This method can by overidden by a subclasses of Compiler.
     * @param checkClass If true, check against .class file,
     *                   if false, check against .java file.
     */
    public boolean isOutDated(boolean checkClass) {

        String jsp = ctxt.getJspFile();

        long jspRealLastModified = 0;
        try {
            URL jspUrl = ctxt.getResource(jsp);
            if (jspUrl == null) {
                ctxt.incrementRemoved();
                return false;
            }
            jspRealLastModified = jspUrl.openConnection().getLastModified();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

        long targetLastModified;
        File targetFile;
        
        if( checkClass ) {
            targetFile = new File(ctxt.getClassFileName());
        } else {
            targetFile = new File(ctxt.getServletJavaFileName());
        }
        
        if (!targetFile.exists()) {
            return true;
        }

        targetLastModified = targetFile.lastModified();
        if (targetLastModified < jspRealLastModified) {
            if( log.isDebugEnabled() )
                log.debug("Compiler: outdated: " + targetFile + " " + targetLastModified );
            return true;
        }

        // determine if source dependent files (e.g. includes using include
	// directives) have been changed.
        if( jsw==null ) {
            return false;
        }

        List depends = jsw.getDependants();
        if (depends == null) {
            return false;
        }

        Iterator it = depends.iterator();
        boolean outDated = false;
        while (it.hasNext()) {
            String include = (String)it.next();
            try {
                URL includeUrl = ctxt.getResource(include);
                if (includeUrl == null) {
                    //System.out.println("Compiler: outdated, no includeUri " + include );
                    outDated = true;
                }
                if (includeUrl.openConnection().getLastModified() >
                    targetLastModified) {
                    //System.out.println("Compiler: outdated, include old " + include );
                    outDated = true;
                    // Remove any potential Wrappers for tag files
                    ctxt.getRuntimeContext().removeWrapper(include);
                }
            } catch (Exception e) {
                e.printStackTrace();
                outDated = true;
            }
        }
        return outDated;

    }

    
    /**
     * Gets the error dispatcher.
     */
    public ErrorDispatcher getErrorDispatcher() {
	return errDispatcher;
    }


    /**
     * Gets the info about the page under compilation
     */
    public PageInfo getPageInfo() {
	return pageInfo;
    }


    public JspCompilationContext getCompilationContext() {
	return ctxt;
    }


    /**
     * Remove generated files
     */
    public void removeGeneratedFiles() {
        try {
            String classFileName = ctxt.getClassFileName();
            if (classFileName != null) {
                File classFile = new File(classFileName);
                if( log.isDebugEnabled() )
                    log.debug( "Deleting " + classFile );
                classFile.delete();
            }
        } catch (Exception e) {
            // Remove as much as possible, ignore possible exceptions
        }
        try {
            String javaFileName = ctxt.getServletJavaFileName();
            if (javaFileName != null) {
                File javaFile = new File(javaFileName);
                if( log.isDebugEnabled() )
                    log.debug( "Deleting " + javaFile );
                javaFile.delete();
            }
        } catch (Exception e) {
            // Remove as much as possible, ignore possible exceptions
        }
    }

    public void removeGeneratedClassFiles() {
        try {
            String classFileName = ctxt.getClassFileName();
            if (classFileName != null) {
                File classFile = new File(classFileName);
                if( log.isDebugEnabled() )
                    log.debug( "Deleting " + classFile );
                classFile.delete();
            }
        } catch (Exception e) {
            // Remove as much as possible, ignore possible exceptions
        }
    }
}
