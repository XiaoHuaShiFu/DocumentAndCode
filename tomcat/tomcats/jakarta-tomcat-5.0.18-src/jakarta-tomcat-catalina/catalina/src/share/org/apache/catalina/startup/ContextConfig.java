/*
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
 * [Additional notices, if required by prior licensing conditions]
 *
 */


package org.apache.catalina.startup;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.catalina.Authenticator;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Logger;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.deploy.ErrorPage;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.util.StringManager;
import org.apache.commons.digester.Digester;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

/**
 * Startup event listener for a <b>Context</b> that configures the properties
 * of that Context, and the associated defined servlets.
 *
 * @author Craig R. McClanahan
 * @author Jean-Francois Arcand
 * @version $Revision: 1.39 $ $Date: 2003/12/20 23:04:02 $
 */

public final class ContextConfig
    implements LifecycleListener {

    private static org.apache.commons.logging.Log log=
        org.apache.commons.logging.LogFactory.getLog( ContextConfig.class );

    // ----------------------------------------------------- Instance Variables

    /**
     * The set of Authenticators that we know how to configure.  The key is
     * the name of the implemented authentication method, and the value is
     * the fully qualified Java class name of the corresponding Valve.
     */
    private static Properties authenticators = null;


    /**
     * The Context we are associated with.
     */
    private Context context = null;


    /**
     * The debugging detail level for this component.
     */
    private int debug = 0;

    
    /**
     * The default web application's deployment descriptor location.
     */
    private String defaultWebXml = null;
    
    
    /**
     * Track any fatal errors during startup configuration processing.
     */
    private boolean ok = false;


    /**
     * The string resources for this package.
     */
    private static final StringManager sm =
        StringManager.getManager(Constants.Package);


    /**
     * The <code>Digester</code> we will use to process web application
     * deployment descriptor files.
     */
    private static Digester webDigester = null;
    
    
    /**
     * The <code>Rule</code> used to parse the web.xml
     */
    private static WebRuleSet webRuleSet = new WebRuleSet();

    /**
     * Attribute value used to turn on/off XML validation
     */
     private static boolean xmlValidation = false;


    /**
     * Attribute value used to turn on/off XML namespace awarenes.
     */
    private static boolean xmlNamespaceAware = false;

        
    // ------------------------------------------------------------- Properties


    /**
     * Return the debugging detail level for this component.
     */
    public int getDebug() {

        return (this.debug);

    }


    /**
     * Set the debugging detail level for this component.
     *
     * @param debug The new debugging detail level
     */
    public void setDebug(int debug) {

        this.debug = debug;

    }
    
    
    /**
     * Return the location of the default deployment descriptor
     */
    public String getDefaultWebXml() {
        if( defaultWebXml == null ) defaultWebXml=Constants.DefaultWebXml;
        return (this.defaultWebXml);

    }


    /**
     * Set the location of the default deployment descriptor
     *
     * @param path Absolute/relative path to the default web.xml
     */
    public void setDefaultWebXml(String path) {

        this.defaultWebXml = path;

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Process the START event for an associated Context.
     *
     * @param event The lifecycle event that has occurred
     */
    public void lifecycleEvent(LifecycleEvent event) {

        // Identify the context we are associated with
        try {
            context = (Context) event.getLifecycle();
//             if (context instanceof StandardContext) {
//                 int contextDebug = ((StandardContext) context).getDebug();
//                 if (contextDebug > this.debug)
//                     this.debug = contextDebug;
//             }
        } catch (ClassCastException e) {
            log.error(sm.getString("contextConfig.cce", event.getLifecycle()), e);
            return;
        }

        // Called from ContainerBase.addChild() -> StandardContext.start()
        // Process the event that has occurred
        if (event.getType().equals(Lifecycle.START_EVENT))
            start();
        else if (event.getType().equals(Lifecycle.STOP_EVENT))
            stop();

    }


    // -------------------------------------------------------- Private Methods


    /**
     * Process the application configuration file, if it exists.
     */
    private void applicationConfig() {

        // Open the application web.xml file, if it exists
        InputStream stream = null;
        ServletContext servletContext = context.getServletContext();
        if (servletContext != null)
            stream = servletContext.getResourceAsStream
                (Constants.ApplicationWebXml);
        if (stream == null) {
            log.info(sm.getString("contextConfig.applicationMissing") + " " + context);
            return;
        }
        
        long t1=System.currentTimeMillis();

        if (webDigester == null){
            webDigester = createWebDigester();
        }
        
        URL url=null;
        // Process the application web.xml file
        synchronized (webDigester) {
            try {
                url =
                    servletContext.getResource(Constants.ApplicationWebXml);
                if( url!=null ) {
                    InputSource is = new InputSource(url.toExternalForm());
                    is.setByteStream(stream);
                    webDigester.clear();
                    webDigester.setDebug(getDebug());
                    if (context instanceof StandardContext) {
                        ((StandardContext) context).setReplaceWelcomeFiles(true);
                    }
                    webDigester.setUseContextClassLoader(false);
                    webDigester.push(context);
                    webDigester.parse(is);
                } else {
                    log.info("No web.xml, using defaults " + context );
                }
            } catch (SAXParseException e) {
                log.error(sm.getString("contextConfig.applicationParse"), e);
                log.error(sm.getString("contextConfig.applicationPosition",
                                 "" + e.getLineNumber(),
                                 "" + e.getColumnNumber()));
                ok = false;
            } catch (Exception e) {
                log.error(sm.getString("contextConfig.applicationParse"), e);
                ok = false;
            } finally {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException e) {
                    log.error(sm.getString("contextConfig.applicationClose"), e);
                }
                webDigester.push(null);
            }
        }
        webRuleSet.recycle();

        long t2=System.currentTimeMillis();
        if (context instanceof StandardContext) {
            ((StandardContext) context).setStartupTime(t2-t1);
        }
    }


    /**
     * Set up a manager.
     */
    private synchronized void managerConfig() {

        if (context.getManager() == null) {
            if ((context.getCluster() != null) && context.getDistributable()) {
                try {
                    context.setManager(context.getCluster().createManager
                                       (context.getName()));
                } catch (Exception ex) {
                    log.error("contextConfig.clusteringInit", ex);
                    ok = false;
                }
            } else {
                context.setManager(new StandardManager());
            }
        }

    }


    /**
     * Set up an Authenticator automatically if required, and one has not
     * already been configured.
     */
    private synchronized void authenticatorConfig() {

        // Does this Context require an Authenticator?
        SecurityConstraint constraints[] = context.findConstraints();
        if ((constraints == null) || (constraints.length == 0))
            return;
        LoginConfig loginConfig = context.getLoginConfig();
        if (loginConfig == null) {
            loginConfig = new LoginConfig("NONE", null, null, null);
            context.setLoginConfig(loginConfig);
        }

        // Has an authenticator been configured already?
        if (context instanceof Authenticator)
            return;
        if (context instanceof ContainerBase) {
            Pipeline pipeline = ((ContainerBase) context).getPipeline();
            if (pipeline != null) {
                Valve basic = pipeline.getBasic();
                if ((basic != null) && (basic instanceof Authenticator))
                    return;
                Valve valves[] = pipeline.getValves();
                for (int i = 0; i < valves.length; i++) {
                    if (valves[i] instanceof Authenticator)
                        return;
                }
            }
        } else {
            return;     // Cannot install a Valve even if it would be needed
        }

        // Has a Realm been configured for us to authenticate against?
        if (context.getRealm() == null) {
            log.error(sm.getString("contextConfig.missingRealm"));
            ok = false;
            return;
        }

        // Load our mapping properties if necessary
        if (authenticators == null) {
            try {
                InputStream is=this.getClass().getClassLoader().getResourceAsStream("org/apache/catalina/startup/Authenticators.properties");
                if( is!=null ) {
                    authenticators = new Properties();
                    authenticators.load(is);
                } else {
                    log.error(sm.getString("contextConfig.authenticatorResources"));
                    ok=false;
                    return;
                }
            } catch (IOException e) {
                log.error(sm.getString("contextConfig.authenticatorResources"), e);
                ok = false;
                return;
            }
        }

        // Identify the class name of the Valve we should configure
        String authenticatorName = null;
        authenticatorName =
                authenticators.getProperty(loginConfig.getAuthMethod());
        if (authenticatorName == null) {
            log.error(sm.getString("contextConfig.authenticatorMissing",
                             loginConfig.getAuthMethod()));
            ok = false;
            return;
        }

        // Instantiate and install an Authenticator of the requested class
        Valve authenticator = null;
        try {
            Class authenticatorClass = Class.forName(authenticatorName);
            authenticator = (Valve) authenticatorClass.newInstance();
            if (context instanceof ContainerBase) {
                Pipeline pipeline = ((ContainerBase) context).getPipeline();
                if (pipeline != null) {
                    ((ContainerBase) context).addValve(authenticator);
                    if( log.isDebugEnabled() )
                        log.debug(sm.getString("contextConfig.authenticatorConfigured",
                                     loginConfig.getAuthMethod()));
                }
            }
        } catch (Throwable t) {
            log.error(sm.getString("contextConfig.authenticatorInstantiate",
                             authenticatorName), t);
            ok = false;
        }

    }


    /**
     * Create (if necessary) and return a Digester configured to process the
     * web application deployment descriptor (web.xml).
     */
    private static Digester createWebDigester() {
        Digester webDigester =
            createWebXmlDigester(xmlNamespaceAware, xmlValidation);
        return webDigester;
    }


    /*
     * Create (if necessary) and return a Digester configured to process the
     * web application deployment descriptor (web.xml).
     */
    public static Digester createWebXmlDigester(boolean namespaceAware,
                                                boolean validation) {
        
        Digester webDigester =  DigesterFactory.newDigester(xmlValidation,
                                                            xmlNamespaceAware,
                                                            webRuleSet);
        return webDigester;
    }

    private String getBaseDir() {
        Container engineC=context.getParent().getParent();
        if( engineC instanceof StandardEngine ) {
            return ((StandardEngine)engineC).getBaseDir();
        }
        return System.getProperty("catalina.base");
    }

    /**
     * Process the default configuration file, if it exists.
     * The default config must be read with the container loader - so
     * container servlets can be loaded
     */
    private void defaultConfig() {
        long t1=System.currentTimeMillis();

        // Open the default web.xml file, if it exists
        if( defaultWebXml==null && context instanceof StandardContext ) {
            defaultWebXml=((StandardContext)context).getDefaultWebXml();
        }
        // set the default if we don't have any overrides
        if( defaultWebXml==null ) getDefaultWebXml();

        File file = new File(this.defaultWebXml);
        if (!file.isAbsolute()) {
            file = new File(getBaseDir(),
                            this.defaultWebXml);
        }

        InputStream stream = null;
        InputSource source = null;

        try {
            if ( ! file.exists() ) {
                // Use getResource and getResourceAsStream
                stream = getClass().getClassLoader()
                    .getResourceAsStream(defaultWebXml);
                if( stream != null ) {
                    source = new InputSource
                            (getClass().getClassLoader()
                            .getResource(defaultWebXml).toString());
                } else {
                    log.info("No default web.xml");
                    // no default web.xml
                    return;
                }
            } else {
                source =
                    new InputSource("file://" + file.getAbsolutePath());
                stream = new FileInputStream(file);
            }
        } catch (Exception e) {
            log.error(sm.getString("contextConfig.defaultMissing") 
                      + " " + defaultWebXml + " " + file , e);
            return;
        }

        if (webDigester == null){
            webDigester = createWebDigester();
        }
        
        // Process the default web.xml file
        synchronized (webDigester) {
            try {
                source.setByteStream(stream);
                webDigester.setDebug(getDebug());
                
                if (context instanceof StandardContext)
                    ((StandardContext) context).setReplaceWelcomeFiles(true);
                webDigester.clear();
                webDigester.setClassLoader(this.getClass().getClassLoader());
                //log.info( "Using cl: " + webDigester.getClassLoader());
                webDigester.setUseContextClassLoader(false);
                webDigester.push(context);
                webDigester.parse(source);
            } catch (SAXParseException e) {
                log.error(sm.getString("contextConfig.defaultParse"), e);
                log.error(sm.getString("contextConfig.defaultPosition",
                                 "" + e.getLineNumber(),
                                 "" + e.getColumnNumber()));
                ok = false;
            } catch (Exception e) {
                log.error(sm.getString("contextConfig.defaultParse"), e);
                ok = false;
            } finally {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException e) {
                    log.error(sm.getString("contextConfig.defaultClose"), e);
                }
            }
        }
        webRuleSet.recycle();
        
        long t2=System.currentTimeMillis();
        if( (t2-t1) > 200 )
            log.debug("Processed default web.xml " + file + " "  + ( t2-t1));
    }


    /**
     * Log a message on the Logger associated with our Context (if any)
     *
     * @param message Message to be logged
     */
    private void log(String message) {

        Logger logger = null;
        if (context != null)
            logger = context.getLogger();
        if (logger != null)
            logger.log("ContextConfig[" + context.getName() + "]: " + message);
        else
            log.info( message );
    }


    /**
     * Log a message on the Logger associated with our Context (if any)
     *
     * @param message Message to be logged
     * @param throwable Associated exception
     */
    private void log(String message, Throwable throwable) {

        Logger logger = null;
        if (context != null)
            logger = context.getLogger();
        if (logger != null)
            logger.log("ContextConfig[" + context.getName() + "] "
                       + message, throwable);
        else {
            log.error( message, throwable );
        }

    }


    /**
     * Process a "start" event for this Context - in background
     */
    private synchronized void start() {
        // Called from StandardContext.start()

        if (log.isDebugEnabled())
            log.debug(sm.getString("contextConfig.start"));
        context.setConfigured(false);
        ok = true;

        // Set properties based on DefaultContext
        Container container = context.getParent();
        if( !context.getOverride() ) {
            if( container instanceof Host ) {
                ((Host)container).importDefaultContext(context);
                xmlValidation = ((Host)container).getXmlValidation();
                xmlNamespaceAware = ((Host)container).getXmlNamespaceAware();

                container = container.getParent();
            }
            if( container instanceof Engine ) {
                ((Engine)container).importDefaultContext(context);
            }
        }

        // Process the default and application web.xml files
        defaultConfig();
        applicationConfig();
        if (ok) {
            validateSecurityRoles();
        }

        // Configure an authenticator if we need one
        if (ok)
            authenticatorConfig();

        // Configure a manager
        if (ok)
            managerConfig();

        // Dump the contents of this pipeline if requested
        if ((log.isDebugEnabled()) && (context instanceof ContainerBase)) {
            log.debug("Pipline Configuration:");
            Pipeline pipeline = ((ContainerBase) context).getPipeline();
            Valve valves[] = null;
            if (pipeline != null)
                valves = pipeline.getValves();
            if (valves != null) {
                for (int i = 0; i < valves.length; i++) {
                    log.debug("  " + valves[i].getInfo());
                }
            }
            log.debug("======================");
        }

        // Make our application available if no problems were encountered
        if (ok)
            context.setConfigured(true);
        else {
            log.error(sm.getString("contextConfig.unavailable"));
            context.setConfigured(false);
        }

    }


    /**
     * Process a "stop" event for this Context.
     */
    private synchronized void stop() {

        if (log.isDebugEnabled())
            log.debug(sm.getString("contextConfig.stop"));

        int i;

        // Removing children
        Container[] children = context.findChildren();
        for (i = 0; i < children.length; i++) {
            context.removeChild(children[i]);
        }

        // Removing application listeners
        String[] applicationListeners = context.findApplicationListeners();
        for (i = 0; i < applicationListeners.length; i++) {
            context.removeApplicationListener(applicationListeners[i]);
        }

        // Removing application parameters
        /*
        ApplicationParameter[] applicationParameters =
            context.findApplicationParameters();
        for (i = 0; i < applicationParameters.length; i++) {
            context.removeApplicationParameter
                (applicationParameters[i].getName());
        }
        */

        // Removing security constraints
        SecurityConstraint[] securityConstraints = context.findConstraints();
        for (i = 0; i < securityConstraints.length; i++) {
            context.removeConstraint(securityConstraints[i]);
        }

        // Removing Ejbs
        /*
        ContextEjb[] contextEjbs = context.findEjbs();
        for (i = 0; i < contextEjbs.length; i++) {
            context.removeEjb(contextEjbs[i].getName());
        }
        */

        // Removing environments
        /*
        ContextEnvironment[] contextEnvironments = context.findEnvironments();
        for (i = 0; i < contextEnvironments.length; i++) {
            context.removeEnvironment(contextEnvironments[i].getName());
        }
        */

        // Removing errors pages
        ErrorPage[] errorPages = context.findErrorPages();
        for (i = 0; i < errorPages.length; i++) {
            context.removeErrorPage(errorPages[i]);
        }

        // Removing filter defs
        FilterDef[] filterDefs = context.findFilterDefs();
        for (i = 0; i < filterDefs.length; i++) {
            context.removeFilterDef(filterDefs[i]);
        }

        // Removing filter maps
        FilterMap[] filterMaps = context.findFilterMaps();
        for (i = 0; i < filterMaps.length; i++) {
            context.removeFilterMap(filterMaps[i]);
        }

        // Removing instance listeners
        String[] instanceListeners = context.findInstanceListeners();
        for (i = 0; i < instanceListeners.length; i++) {
            context.removeInstanceListener(instanceListeners[i]);
        }

        // Removing local ejbs
        /*
        ContextLocalEjb[] contextLocalEjbs = context.findLocalEjbs();
        for (i = 0; i < contextLocalEjbs.length; i++) {
            context.removeLocalEjb(contextLocalEjbs[i].getName());
        }
        */

        // Removing Mime mappings
        String[] mimeMappings = context.findMimeMappings();
        for (i = 0; i < mimeMappings.length; i++) {
            context.removeMimeMapping(mimeMappings[i]);
        }

        // Removing parameters
        String[] parameters = context.findParameters();
        for (i = 0; i < parameters.length; i++) {
            context.removeParameter(parameters[i]);
        }

        // Removing resource env refs
        /*
        String[] resourceEnvRefs = context.findResourceEnvRefs();
        for (i = 0; i < resourceEnvRefs.length; i++) {
            context.removeResourceEnvRef(resourceEnvRefs[i]);
        }
        */

        // Removing resource links
        /*
        ContextResourceLink[] contextResourceLinks =
            context.findResourceLinks();
        for (i = 0; i < contextResourceLinks.length; i++) {
            context.removeResourceLink(contextResourceLinks[i].getName());
        }
        */

        // Removing resources
        /*
        ContextResource[] contextResources = context.findResources();
        for (i = 0; i < contextResources.length; i++) {
            context.removeResource(contextResources[i].getName());
        }
        */

        // Removing sercurity role
        String[] securityRoles = context.findSecurityRoles();
        for (i = 0; i < securityRoles.length; i++) {
            context.removeSecurityRole(securityRoles[i]);
        }

        // Removing servlet mappings
        String[] servletMappings = context.findServletMappings();
        for (i = 0; i < servletMappings.length; i++) {
            context.removeServletMapping(servletMappings[i]);
        }

        // FIXME : Removing status pages

        // Removing taglibs
        String[] taglibs = context.findTaglibs();
        for (i = 0; i < taglibs.length; i++) {
            context.removeTaglib(taglibs[i]);
        }

        // Removing welcome files
        String[] welcomeFiles = context.findWelcomeFiles();
        for (i = 0; i < welcomeFiles.length; i++) {
            context.removeWelcomeFile(welcomeFiles[i]);
        }

        // Removing wrapper lifecycles
        String[] wrapperLifecycles = context.findWrapperLifecycles();
        for (i = 0; i < wrapperLifecycles.length; i++) {
            context.removeWrapperLifecycle(wrapperLifecycles[i]);
        }

        // Removing wrapper listeners
        String[] wrapperListeners = context.findWrapperListeners();
        for (i = 0; i < wrapperListeners.length; i++) {
            context.removeWrapperListener(wrapperListeners[i]);
        }

        ok = true;

    }

    /**
     * Validate the usage of security role names in the web application
     * deployment descriptor.  If any problems are found, issue warning
     * messages (for backwards compatibility) and add the missing roles.
     * (To make these problems fatal instead, simply set the <code>ok</code>
     * instance variable to <code>false</code> as well).
     */
    private void validateSecurityRoles() {

        // Check role names used in <security-constraint> elements
        SecurityConstraint constraints[] = context.findConstraints();
        for (int i = 0; i < constraints.length; i++) {
            String roles[] = constraints[i].findAuthRoles();
            for (int j = 0; j < roles.length; j++) {
                if (!"*".equals(roles[j]) &&
                    !context.findSecurityRole(roles[j])) {
                    log.info(sm.getString("contextConfig.role.auth", roles[j]));
                    context.addSecurityRole(roles[j]);
                }
            }
        }

        // Check role names used in <servlet> elements
        Container wrappers[] = context.findChildren();
        for (int i = 0; i < wrappers.length; i++) {
            Wrapper wrapper = (Wrapper) wrappers[i];
            String runAs = wrapper.getRunAs();
            if ((runAs != null) && !context.findSecurityRole(runAs)) {
                log.info(sm.getString("contextConfig.role.runas", runAs));
                context.addSecurityRole(runAs);
            }
            String names[] = wrapper.findSecurityReferences();
            for (int j = 0; j < names.length; j++) {
                String link = wrapper.findSecurityReference(names[j]);
                if ((link != null) && !context.findSecurityRole(link)) {
                    log.info(sm.getString("contextConfig.role.link", link));
                    context.addSecurityRole(link);
                }
            }
        }

    }

}
