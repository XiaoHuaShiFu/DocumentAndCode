/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/ApplicationServlet.java,v 1.4 2003/07/08 06:32:52 remm Exp $
 * $Revision: 1.4 $
 * $Date: 2003/07/08 06:32:52 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * 4. The names "The Jakarta Project", "Struts", and "Apache Software
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


package org.apache.webapp.admin;

import java.text.DateFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.management.MBeanServer;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import org.apache.commons.modeler.Registry;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;


/**
 * Subclass of ActionServlet that adds caching of the supported locales in the
 * ApplicationLocales class.
 *
 * @author Patrick Luby
 * @version $Revision: 1.4 $ $Date: 2003/07/08 06:32:52 $
 */

public class ApplicationServlet extends ActionServlet {


    // ----------------------------------------------------- Manifest Constants


    /**
     * The application scope key under which we store our
     * <code>ApplicationLocales</code> instance.
     */
    public static final String LOCALES_KEY = "applicationLocales";


    // ----------------------------------------------------- Instance Variables


    /**
     * The managed beans Registry used to look up metadata.
     */
    protected Registry registry = null;


    /**
     * The JMX MBeanServer we will use to look up management beans.
     */
    protected MBeanServer server = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Convenience method to make the managed beans Registry available.
     *
     * @exception ServletException if the Registry is not available
     */
    public Registry getRegistry() throws ServletException {

        if (registry == null)
            initRegistry();
        return (this.registry);

    }


    /**
     * Convenience method to make the JMX MBeanServer available.
     *
     * @exception ServletException if the MBeanServer is not available
     */
    public MBeanServer getServer() throws ServletException {

        if (server == null)
            initServer();
        return (this.server);

    }


    /**
     * Initialize this servlet.
     *
     * @exception ServletException if an initialization error occurs.
     */
    public void init() throws javax.servlet.ServletException {
        super.init();
        initApplicationLocales();
    }


    // ---------------------------------------------------- Protected Methods


    /**
     * Create and initialize the ApplicationLocales object, and make it
     * available as a servlet context attribute.
     */
    protected void initApplicationLocales() {

        ApplicationLocales locales = new ApplicationLocales(this);
        getServletContext().setAttribute(LOCALES_KEY, locales);

    }


    /**
     * Validate the existence of the Registry that should have been
     * provided to us by an instance of
     * <code>org.apache.catalina.mbean.ServerLifecycleListener</code>
     * enabled at startup time.
     *
     * @exception ServletException if we cannot find the Registry
     */
    protected void initRegistry() throws ServletException {

        registry = Registry.getRegistry();
        //(Registry) getServletContext().getAttribute
        //    ("org.apache.catalina.Registry");
        if (registry == null)
            throw new UnavailableException("Registry is not available");

    }


    /**
     * Validate the existence of the MBeanServer that should have been
     * provided to us by an instance of
     * <code>org.apache.catalina.mbean.ServerLifecycleListener</code>
     * enabled at startup time.
     *
     * @exception ServletException if we cannot find the MBeanServer
     */
    protected void initServer() throws ServletException {

        server = Registry.getRegistry().getMBeanServer();
        //(MBeanServer) getServletContext().getAttribute
        //    ("org.apache.catalina.MBeanServer");
        if (server == null)
            throw new UnavailableException("MBeanServer is not available");

    }


}
