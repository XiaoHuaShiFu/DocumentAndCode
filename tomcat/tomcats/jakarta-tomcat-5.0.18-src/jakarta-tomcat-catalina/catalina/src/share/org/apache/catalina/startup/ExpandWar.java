/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/catalina/src/share/org/apache/catalina/startup/ExpandWar.java,v 1.4 2003/07/26 14:24:52 remm Exp $
 * $Revision: 1.4 $
 * $Date: 2003/07/26 14:24:52 $
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


package org.apache.catalina.startup;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.catalina.Host;
import org.apache.catalina.Logger;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.util.StringManager;

/**
 * Expand out a WAR in a Host's appBase.
 *
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 * @author Glenn L. Nielsen
 * @version $Revision: 1.4 $
 */

public class ExpandWar {


    /**
     * The string resources for this package.
     */
    protected static final StringManager sm =
        StringManager.getManager(Constants.Package);


    /**
     * Expand the WAR file found at the specified URL into an unpacked
     * directory structure, and return the absolute pathname to the expanded
     * directory.
     *
     * @param host Host war is being installed for
     * @param war URL of the web application archive to be expanded
     *  (must start with "jar:")
     *
     * @exception IllegalArgumentException if this is not a "jar:" URL
     * @exception IOException if an input/output error was encountered
     *  during expansion
     */
    public static String expand(Host host, URL war)
        throws IOException {

        int debug = 0;
        Logger logger = host.getLogger();

        if (host instanceof StandardHost) {
            debug = ((StandardHost) host).getDebug();
        }

        // Calculate the directory name of the expanded directory
        if (debug >= 1) {
            logger.log("expand(" + war.toString() + ")");
        }
        String pathname = war.toString().replace('\\', '/');
        if (pathname.endsWith("!/")) {
            pathname = pathname.substring(0, pathname.length() - 2);
        }
        int period = pathname.lastIndexOf('.');
        if (period >= pathname.length() - 4)
            pathname = pathname.substring(0, period);
        int slash = pathname.lastIndexOf('/');
        if (slash >= 0) {
            pathname = pathname.substring(slash + 1);
        }
        if (debug >= 1) {
            logger.log("  Proposed directory name: " + pathname);
        }
        return expand(host, war, pathname);

    }


    /**
     * Expand the WAR file found at the specified URL into an unpacked
     * directory structure, and return the absolute pathname to the expanded
     * directory.
     *
     * @param host Host war is being installed for
     * @param war URL of the web application archive to be expanded
     *  (must start with "jar:")
     * @param pathname Context path name for web application
     *
     * @exception IllegalArgumentException if this is not a "jar:" URL
     * @exception IOException if an input/output error was encountered
     *  during expansion
     */
    public static String expand(Host host, URL war, String pathname)
        throws IOException {

        int debug = 0;
        Logger logger = host.getLogger();

        if (host instanceof StandardHost) {
            debug = ((StandardHost) host).getDebug();
        }

        // Make sure that there is no such directory already existing
        File appBase = new File(host.getAppBase());
        if (!appBase.isAbsolute()) {
            appBase = new File(System.getProperty("catalina.base"),
                               host.getAppBase());
        }
        if (!appBase.exists() || !appBase.isDirectory()) {
            throw new IOException
                (sm.getString("hostConfig.appBase",
                              appBase.getAbsolutePath()));
        }
        File docBase = new File(appBase, pathname);
        if (docBase.exists()) {
            // War file is already installed
            return (docBase.getAbsolutePath());
        }

        // Create the new document base directory
        docBase.mkdir();
        if (debug >= 2) {
            logger.log("  Have created expansion directory " +
                docBase.getAbsolutePath());
        }

        // Expand the WAR into the new document base directory
        JarURLConnection juc = (JarURLConnection) war.openConnection();
        juc.setUseCaches(false);
        JarFile jarFile = null;
        InputStream input = null;
        try {
            jarFile = juc.getJarFile();
            if (debug >= 2) {
                logger.log("  Have opened JAR file successfully");
            }
            Enumeration jarEntries = jarFile.entries();
            if (debug >= 2) {
                logger.log("  Have retrieved entries enumeration");
            }
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) jarEntries.nextElement();
                String name = jarEntry.getName();
                if (debug >= 2) {
                    logger.log("  Am processing entry " + name);
                }
                int last = name.lastIndexOf('/');
                if (last >= 0) {
                    File parent = new File(docBase,
                                           name.substring(0, last));
                    if (debug >= 2) {
                        logger.log("  Creating parent directory " + parent);
                    }
                    parent.mkdirs();
                }
                if (name.endsWith("/")) {
                    continue;
                }
                if (debug >= 2) {
                    logger.log("  Creating expanded file " + name);
                }
                input = jarFile.getInputStream(jarEntry);
                expand(input, docBase, name);
                input.close();
                input = null;
            }
        } catch (IOException e) {
            // If something went wrong, delete expanded dir to keep things 
            // clean
            deleteDir(docBase);
            throw e;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Throwable t) {
                    ;
                }
                input = null;
            }
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (Throwable t) {
                    ;
                }
                jarFile = null;
            }
        }

        // Return the absolute path to our new document base directory
        return (docBase.getAbsolutePath());

    }


    /**
     * Delete the specified directory, including all of its contents and
     * subdirectories recursively.
     *
     * @param dir File object representing the directory to be deleted
     */
    public static void deleteDir(File dir) {

        String files[] = dir.list();
        if (files == null) {
            files = new String[0];
        }
        for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            if (file.isDirectory()) {
                deleteDir(file);
            } else {
                file.delete();
            }
        }
        dir.delete();

    }


    /**
     * Expand the specified input stream into the specified directory, creating
     * a file named from the specified relative path.
     *
     * @param input InputStream to be copied
     * @param docBase Document base directory into which we are expanding
     * @param name Relative pathname of the file to be created
     *
     * @exception IOException if an input/output error occurs
     */
    protected static void expand(InputStream input, File docBase, String name)
        throws IOException {

        File file = new File(docBase, name);
        BufferedOutputStream output = null;
        try {
            output = 
                new BufferedOutputStream(new FileOutputStream(file));
            byte buffer[] = new byte[2048];
            while (true) {
                int n = input.read(buffer);
                if (n <= 0)
                    break;
                output.write(buffer, 0, n);
            }
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

    }


}
