/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/catalina/src/share/org/apache/catalina/ant/DeployTask.java,v 1.5 2003/09/02 21:22:00 remm Exp $
 * $Revision: 1.5 $
 * $Date: 2003/09/02 21:22:00 $
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
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.tools.ant.BuildException;


/**
 * Ant task that implements the <code>/deploy</code> command, supported by
 * the Tomcat manager application.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.5 $ $Date: 2003/09/02 21:22:00 $
 * @since 4.1
 */
public class DeployTask extends AbstractCatalinaTask {


    // ------------------------------------------------------------- Properties


    /**
     * URL of the context configuration file for this application, if any.
     */
    protected String config = null;

    public String getConfig() {
        return (this.config);
    }

    public void setConfig(String config) {
        this.config = config;
    }


    /**
     * URL of the server local web application archive (WAR) file 
     * to be deployed.
     */
    protected String localWar = null;

    public String getLocalWar() {
        return (this.localWar);
    }

    public void setLocalWar(String localWar) {
        this.localWar = localWar;
    }


    /**
     * The context path of the web application we are managing.
     */
    protected String path = null;

    public String getPath() {
        return (this.path);
    }

    public void setPath(String path) {
        this.path = path;
    }


    /**
     * Tag to associate with this to be deployed webapp.
     */
    protected String tag = null;

    public String getTag() {
        return (this.tag);
    }

    public void setTag(String tag) {
        this.tag = tag;
    }


    /**
     * Update existing webapps.
     */
    protected boolean update = false;

    public boolean getUpdate() {
        return (this.update);
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }


    /**
     * URL of the web application archive (WAR) file to be deployed.
     */
    protected String war = null;

    public String getWar() {
        return (this.war);
    }

    public void setWar(String war) {
        this.war = war;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Execute the requested operation.
     *
     * @exception BuildException if an error occurs
     */
    public void execute() throws BuildException {

        super.execute();
        if (path == null) {
            throw new BuildException
                ("Must specify 'path' attribute");
        }
        if ((war == null) && (localWar == null) && (tag == null)) {
            throw new BuildException
                ("Must specify either 'war', 'localWar', or 'tag' attribute");
        }

        // Building an input stream on the WAR to upload, if any
        BufferedInputStream stream = null;
        String contentType = null;
        int contentLength = -1;
        if (war != null) {
            if (war.startsWith("file:")) {
                try {
                    URL url = new URL(war);
                    URLConnection conn = url.openConnection();
                    contentLength = conn.getContentLength();
                    stream = new BufferedInputStream
                        (conn.getInputStream(), 1024);
                } catch (IOException e) {
                    throw new BuildException(e);
                }
            } else {
                try {
                    stream = new BufferedInputStream
                        (new FileInputStream(war), 1024);
                } catch (IOException e) {
                    throw new BuildException(e);
                }
            }
            contentType = "application/octet-stream";
        }

        // Building URL
        StringBuffer sb = new StringBuffer("/deploy?path=");
        sb.append(URLEncoder.encode(this.path));
        if ((war == null) && (config != null)) {
            sb.append("&config=");
            sb.append(URLEncoder.encode(config));
        }
        if ((war == null) && (localWar != null)) {
            sb.append("&war=");
            sb.append(URLEncoder.encode(localWar));
        }
        if (update) {
            sb.append("&update=true");
        }
        if (tag != null) {
            sb.append("&tag=");
            sb.append(URLEncoder.encode(tag));
        }

        execute(sb.toString(), stream, contentType, contentLength);

    }


}
