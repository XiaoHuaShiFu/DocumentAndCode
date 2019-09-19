/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/catalina/src/share/org/apache/catalina/ant/ValidatorTask.java,v 1.6 2003/12/20 23:04:02 remm Exp $
 * $Revision: 1.6 $
 * $Date: 2003/12/20 23:04:02 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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


package org.apache.catalina.ant;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.catalina.startup.Constants;
import org.apache.catalina.startup.DigesterFactory;
import org.apache.commons.digester.Digester;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.xml.sax.InputSource;


/**
 * Task for validating a web application deployment descriptor, using XML 
 * schema validation.
 *
 * @author Remy Maucherat
 * @version $Revision: 1.6 $ $Date: 2003/12/20 23:04:02 $
 * @since 5.0
 */

public class ValidatorTask extends Task {


    // ----------------------------------------------------- Instance Variables


    // ------------------------------------------------------------- Properties


    /**
     * The path to the webapp directory.
     */
    protected String path = null;

    public String getPath() {
        return (this.path);
    }

    public void setPath(String path) {
        this.path = path;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Execute the specified command.  This logic only performs the common
     * attribute validation required by all subclasses; it does not perform
     * any functional logic directly.
     *
     * @exception BuildException if a validation error occurs
     */
    public void execute() throws BuildException {

        if (path == null) {
            throw new BuildException("Must specify 'path'");
        }

        File file = new File(path, Constants.ApplicationWebXml);
        if ((!file.exists()) || (!file.canRead())) {
            throw new BuildException("Cannot find web.xml");
        }

        // Commons-logging likes having the context classloader set
        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader
            (ValidatorTask.class.getClassLoader());

        Digester digester = DigesterFactory.newDigester(true, true, null);
        try {
            file = file.getCanonicalFile();
            InputStream stream = 
                new BufferedInputStream(new FileInputStream(file));
            InputSource is = new InputSource(file.toURL().toExternalForm());
            is.setByteStream(stream);
            digester.parse(is);
            System.out.println("web.xml validated");
        } catch (Throwable t) {
            throw new BuildException("Validation failure", t);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCL);
        }

    }


}
