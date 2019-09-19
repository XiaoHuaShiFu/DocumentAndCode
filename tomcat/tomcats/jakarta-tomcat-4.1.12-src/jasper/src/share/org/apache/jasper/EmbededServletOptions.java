/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/EmbededServletOptions.java,v 1.8.2.1 2002/09/08 05:45:59 glenn Exp $
 * $Revision: 1.8.2.1 $
 * $Date: 2002/09/08 05:45:59 $
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

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.jasper.logging.Logger;

import org.apache.jasper.compiler.TldLocationsCache;
import org.apache.jasper.xmlparser.ParserUtils;

import java.util.*;

/**
 * A class to hold all init parameters specific to the JSP engine. 
 *
 * @author Anil K. Vijendran
 * @author Hans Bergsten
 * @author Pierre Delisle
 */
public final class EmbededServletOptions implements Options {
    private Properties settings=new Properties();
    
    /**
     * Is Jasper being used in development mode?
     */
    public boolean development = true;

    /**
     * Do you want to keep the generated Java files around?
     */
    public boolean keepGenerated = true;

    /**
     * Do you want support for "large" files? What this essentially
     * means is that we generated code so that the HTML data in a JSP
     * file is stored separately as opposed to those constant string
     * data being used literally in the generated servlet. 
     */
    public boolean largeFile = false;

    /**
     * Determines whether tag handler pooling is enabled.
     */
    public boolean poolingEnabled = true;

    /**
     * Do you want support for "mapped" files? This will generate
     * servlet that has a print statement per line of the JSP file.
     * This seems like a really nice feature to have for debugging.
     */
    public boolean mappedFile = false;
    
    /**
     * Do you want stack traces and such displayed in the client's
     * browser? If this is false, such messages go to the standard
     * error or a log file if the standard error is redirected. 
     */
    public boolean sendErrorToClient = false;

    /**
     * Do we want to include debugging information in the class file?
     */
    public boolean classDebugInfo = true;

    /**
     * Background compile thread check interval in seconds.
     */
    public int checkInterval = 300;

    /**
     * JSP reloading check ?
     */
    public boolean reloading = true;

    /**
     * I want to see my generated servlets. Which directory are they
     * in?
     */
    public File scratchDir;
    
    /**
     * Need to have this as is for versions 4 and 5 of IE. Can be set from
     * the initParams so if it changes in the future all that is needed is
     * to have a jsp initParam of type ieClassId="<value>"
     */
    public String ieClassId = "clsid:8AD9C840-044E-11D1-B3E9-00805F499D93";

    /**
     * What classpath should I use while compiling generated servlets?
     */
    public String classpath = null;
    
    /**
     * Compiler to use.
     */
    public String compiler = null;

    /**
     * Cache for the TLD locations
     */
    private TldLocationsCache tldLocationsCache = null;

    /**
     * Java platform encoding to generate the JSP
     * page servlet.
     */
    private String javaEncoding = "UTF8";

    public String getProperty(String name ) {
        return settings.getProperty( name );
    }

    public void setProperty(String name, String value ) {
        settings.setProperty( name, value );
    }
    
    /**
     * Are we keeping generated code around?
     */
    public boolean getKeepGenerated() {
        return keepGenerated;
    }
    
    /**
     * Are we supporting large files?
     */
    public boolean getLargeFile() {
        return largeFile;
    }

    public boolean isPoolingEnabled() {
	return poolingEnabled;
    }
    
    /**
     * Are we supporting HTML mapped servlets?
     */
    public boolean getMappedFile() {
        return mappedFile;
    }
    
    /**
     * Should errors be sent to client or thrown into stderr?
     */
    public boolean getSendErrorToClient() {
        return sendErrorToClient;
    }
 
    /**
     * Should class files be compiled with debug information?
     */
    public boolean getClassDebugInfo() {
        return classDebugInfo;
    }

    /**
     * Background JSP compile thread check intervall
     */
    public int getCheckInterval() {
        return checkInterval;
    }

    /**
     * Is Jasper being used in development mode?
     */
    public boolean getDevelopment() {
        return development;
    }

    /**
     * JSP reloading check ?
     */
    public boolean getReloading() {
        return reloading;
    }

    /**
     * Class ID for use in the plugin tag when the browser is IE. 
     */
    public String getIeClassId() {
        return ieClassId;
    }
    
    /**
     * What is my scratch dir?
     */
    public File getScratchDir() {
        return scratchDir;
    }

    /**
     * What classpath should I use while compiling the servlets
     * generated from JSP files?
     */
    public String getClassPath() {
        return classpath;
    }

    /**
     * Compiler to use.
     */
    public String getCompiler() {
        return compiler;
    }


    public TldLocationsCache getTldLocationsCache() {
	return tldLocationsCache;
    }

    public void setTldLocationsCache( TldLocationsCache tldC ) {
        tldLocationsCache=tldC;
    }

    public String getJavaEncoding() {
	return javaEncoding;
    }

    /**
     * Create an EmbededServletOptions object using data available from
     * ServletConfig and ServletContext. 
     */
    public EmbededServletOptions(ServletConfig config,
				 ServletContext context) {

        Enumeration enum=config.getInitParameterNames();
        while( enum.hasMoreElements() ) {
            String k=(String)enum.nextElement();
            String v=config.getInitParameter( k );

            setProperty( k, v);
        }

        // quick hack
        String validating=config.getInitParameter( "validating");
        if( "false".equals( validating )) ParserUtils.validating=false;
        
        String keepgen = config.getInitParameter("keepgenerated");
        if (keepgen != null) {
            if (keepgen.equalsIgnoreCase("true"))
                this.keepGenerated = true;
            else if (keepgen.equalsIgnoreCase("false"))
                this.keepGenerated = false;
            else Constants.message ("jsp.warning.keepgen", Logger.WARNING);
        }
            

        String largeFile = config.getInitParameter("largefile"); 
        if (largeFile != null) {
            if (largeFile.equalsIgnoreCase("true"))
                this.largeFile = true;
            else if (largeFile.equalsIgnoreCase("false"))
                this.largeFile = false;
            else Constants.message ("jsp.warning.largeFile", Logger.WARNING);
        }
	
        String mapFile = config.getInitParameter("mappedfile"); 
        if (mapFile != null) {
            if (mapFile.equalsIgnoreCase("true"))
                this.mappedFile = true;
            else if (mapFile.equalsIgnoreCase("false"))
                this.mappedFile = false;
            else Constants.message ("jsp.warning.mappedFile", Logger.WARNING);
        }

	poolingEnabled = true;
        String poolingEnabledParam
	    = config.getInitParameter("enablePooling"); 
        if (poolingEnabledParam != null
  	        && !poolingEnabledParam.equalsIgnoreCase("true")) {
            if (poolingEnabledParam.equalsIgnoreCase("false"))
                this.poolingEnabled = false;
            else Constants.message("jsp.warning.enablePooling",
				   Logger.WARNING);
        }
	
        String senderr = config.getInitParameter("sendErrToClient");
        if (senderr != null) {
            if (senderr.equalsIgnoreCase("true"))
                this.sendErrorToClient = true;
            else if (senderr.equalsIgnoreCase("false"))
                this.sendErrorToClient = false;
            else Constants.message ("jsp.warning.sendErrToClient", Logger.WARNING);
        }

        String debugInfo = config.getInitParameter("classdebuginfo");
        if (debugInfo != null) {
            if (debugInfo.equalsIgnoreCase("true"))
                this.classDebugInfo  = true;
            else if (debugInfo.equalsIgnoreCase("false"))
                this.classDebugInfo  = false;
            else Constants.message ("jsp.warning.classDebugInfo", Logger.WARNING);
        }

        String checkInterval = config.getInitParameter("checkInterval");
        if (checkInterval != null) {
            try {
                this.checkInterval = new Integer(checkInterval).intValue();
                if (this.checkInterval == 0) {
                    this.checkInterval = 300;
                    Constants.message("jsp.warning.checkInterval",
                                      Logger.WARNING);
                }
            } catch(NumberFormatException ex) {
                Constants.message ("jsp.warning.checkInterval", Logger.WARNING);
            }
        }

        String development = config.getInitParameter("development");
        if (development != null) {
            if (development.equalsIgnoreCase("true"))
                this.development = true;
            else if (development.equalsIgnoreCase("false"))
                this.development = false;
            else Constants.message ("jsp.warning.development", Logger.WARNING);
        }

        String reloading = config.getInitParameter("reloading");
        if (reloading != null) {
            if (reloading.equalsIgnoreCase("true"))
                this.reloading = true;
            else if (reloading.equalsIgnoreCase("false"))
                this.reloading = false;
            else Constants.message ("jsp.warning.reloading", Logger.WARNING);
        }

        String ieClassId = config.getInitParameter("ieClassId");
        if (ieClassId != null)
            this.ieClassId = ieClassId;

        String classpath = config.getInitParameter("classpath");
        if (classpath != null)
            this.classpath = classpath;

        String dir = config.getInitParameter("scratchdir"); 

        if (dir != null)
            scratchDir = new File(dir);
        else {
            // First we try the Servlet 2.2 javax.servlet.context.tempdir property
            scratchDir = (File) context.getAttribute(Constants.TMP_DIR);
            if (scratchDir == null) {
                // Not running in a Servlet 2.2 container.
                // Try to get the JDK 1.2 java.io.tmpdir property
                dir = System.getProperty("java.io.tmpdir");
                if (dir != null)
                    scratchDir = new File(dir);
            }
        }
                
        if (this.scratchDir == null) {
            Constants.message("jsp.error.no.scratch.dir", Logger.FATAL);
            return;
        }
            
        if (!(scratchDir.exists() && scratchDir.canRead() &&
              scratchDir.canWrite() && scratchDir.isDirectory()))
            Constants.message("jsp.error.bad.scratch.dir",
                              new Object[] {
                                  scratchDir.getAbsolutePath()
                              }, Logger.FATAL);
                                  
        this.compiler = config.getInitParameter("compiler");

        String javaEncoding = config.getInitParameter("javaEncoding");
        if (javaEncoding != null) {
            this.javaEncoding = javaEncoding;
        }

	// Setup the global Tag Libraries location cache for this
	// web-application.
	tldLocationsCache = new TldLocationsCache(context);

    }

}

