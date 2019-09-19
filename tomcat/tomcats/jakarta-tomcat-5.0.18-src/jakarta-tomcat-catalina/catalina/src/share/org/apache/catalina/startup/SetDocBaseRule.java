/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/catalina/src/share/org/apache/catalina/startup/SetDocBaseRule.java,v 1.10 2004/01/11 10:05:35 remm Exp $
 * $Revision: 1.10 $
 * $Date: 2004/01/11 10:05:35 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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


package org.apache.catalina.startup;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

import org.apache.catalina.Context;
import org.apache.catalina.Deployer;
import org.apache.catalina.Host;
import org.apache.catalina.core.StandardHost;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

/**
 * <p>Rule that modifies the docBase of the host, setting it appropriately,
 * before adding the Context to the parent Host.</p>
 * 
 * @author Remy Maucherat
 */
public class SetDocBaseRule extends Rule {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this Rule.
     *
     * @param digester Digester we are associated with
     */
    public SetDocBaseRule(Digester digester) {

        super(digester);

    }


    // ----------------------------------------------------- Instance Variables


    // --------------------------------------------------------- Public Methods


    /**
     * Handle the beginning of an XML element.
     *
     * @param attributes The attributes of this element
     *
     * @exception Exception if a processing error occurs
     */
    public void begin(Attributes attributes) throws Exception {

        Context child = (Context) digester.peek(0);
        Deployer parent = (Deployer) digester.peek(1);
        Host host = null;
        if (!(parent instanceof StandardHost)) {
            Method method = parent.getClass().getMethod("getHost", null);
            host = (Host) method.invoke(parent, null);
        } else {
            host = (Host) parent;
        }
        String appBase = host.getAppBase();

        boolean unpackWARs = true;
        if (host instanceof StandardHost) {
            unpackWARs = ((StandardHost) host).isUnpackWARs();
        }
        if (!unpackWARs 
            && !("true".equals(attributes.getValue("unpackWAR")))) {
            return;
        }
        if ("false".equals(attributes.getValue("unpackWAR"))) {
            return;
        }

        File canonicalAppBase = new File(appBase);
        if (canonicalAppBase.isAbsolute()) {
            canonicalAppBase = canonicalAppBase.getCanonicalFile();
        } else {
            canonicalAppBase = 
                new File(System.getProperty("catalina.base"), appBase)
                .getCanonicalFile();
        }

        String docBase = child.getDocBase();
        if (docBase == null) {
            // Trying to guess the docBase according to the path
            String path = child.getPath();
            if (path == null) {
                return;
            }
            if (path.equals("")) {
                docBase = "ROOT";
            } else {
                if (path.startsWith("/")) {
                    docBase = path.substring(1);
                } else {
                    docBase = path;
                }
            }
        }

        File file = new File(docBase);
        if (!file.isAbsolute()) {
            docBase = (new File(canonicalAppBase, docBase)).getPath();
        } else {
            docBase = file.getCanonicalPath();
        }

        if (docBase.toLowerCase().endsWith(".war")) {
            URL war = new URL("jar:" + (new File(docBase)).toURL() + "!/");
            String contextPath = child.getPath();
            if (contextPath.equals("")) {
                contextPath = "ROOT";
            }
            docBase = ExpandWar.expand(host, war, contextPath);
            file = new File(docBase);
            docBase = file.getCanonicalPath();
        } else {
            File docDir = new File(docBase);
            if (!docDir.exists()) {
                File warFile = new File(docBase + ".war");
                if (warFile.exists()) {
                    URL war = new URL("jar:" + warFile.toURL() + "!/");
                    docBase = ExpandWar.expand(host, war, child.getPath());
                    file = new File(docBase);
                    docBase = file.getCanonicalPath();
                }
            }
        }

        if (docBase.startsWith(canonicalAppBase.getPath())) {
            docBase = docBase.substring(canonicalAppBase.getPath().length());
            docBase = docBase.replace(File.separatorChar, '/');
            if (docBase.startsWith("/")) {
                docBase = docBase.substring(1);
            }
        } else {
            docBase = docBase.replace(File.separatorChar, '/');
        }

        child.setDocBase(docBase);

    }


}
