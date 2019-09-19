/*
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


package org.apache.catalina.core;


import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.DefaultContext;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Realm;
import org.apache.catalina.Service;
import org.apache.catalina.realm.JAASRealm;
import org.apache.catalina.util.ServerInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.modeler.Registry;
import org.apache.commons.modeler.modules.MbeansSource;
import java.io.File;
import java.util.List;

/**
 * Standard implementation of the <b>Engine</b> interface.  Each
 * child container must be a Host implementation to process the specific
 * fully qualified host name of that virtual host.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.22 $ $Date: 2003/10/21 00:18:25 $
 */

public class StandardEngine
    extends ContainerBase
    implements Engine {

    private static Log log = LogFactory.getLog(StandardEngine.class);

    // ----------------------------------------------------------- Constructors


    /**
     * Create a new StandardEngine component with the default basic Valve.
     */
    public StandardEngine() {

        super();
        pipeline.setBasic(new StandardEngineValve());
        /* Set the jmvRoute using the system property jvmRoute */
        try {
            setJvmRoute(System.getProperty("jvmRoute"));
        } catch(Exception ex) {
        }
        // By default, the engine will hold the reloading thread
        backgroundProcessorDelay = 10;

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * Host name to use when no server host, or an unknown host,
     * is specified in the request.
     */
    private String defaultHost = null;


    /**
     * The descriptive information string for this implementation.
     */
    private static final String info =
        "org.apache.catalina.core.StandardEngine/1.0";


    /**
     * The <code>Service</code> that owns this Engine, if any.
     */
    private Service service = null;

    /** Allow the base dir to be specified explicitely for
     * each engine. In time we should stop using catalina.base property -
     * otherwise we loose some flexibility.
     */
    private String baseDir = null;

    /** Optional mbeans config file. This will replace the "hacks" in
     * jk and ServerListener. The mbeans file will support (transparent) 
     * persistence - soon. It'll probably replace jk2.properties and could
     * replace server.xml. Of course - the same beans could be loaded and 
     * managed by an external entity - like the embedding app - which
     *  can use a different persistence mechanism.
     */ 
    private String mbeansFile = null;
    
    /** Mbeans loaded by the engine.  
     */ 
    private List mbeans;
    
    /**
     * DefaultContext config
     */
    private DefaultContext defaultContext;


    /**
     * The JVM Route ID for this Tomcat instance. All Route ID's must be unique
     * across the cluster.
     */
    private String jvmRouteId;


    // ------------------------------------------------------------- Properties

    /** Provide a default in case no explicit configuration is set
     *
     * @return configured realm, or a JAAS realm by default
     */
    public Realm getRealm() {
        Realm configured=super.getRealm();
        // If no set realm has been called - default to JAAS
        // This can be overriden at engine, context and host level  
        if( configured==null ) {
            configured=new JAASRealm();
            this.setRealm( configured );
        }
        return configured;
    }


    /**
     * Return the default host.
     */
    public String getDefaultHost() {

        return (defaultHost);

    }


    /**
     * Set the default host.
     *
     * @param host The new default host
     */
    public void setDefaultHost(String host) {

        String oldDefaultHost = this.defaultHost;
        if (host == null) {
            this.defaultHost = null;
        } else {
            this.defaultHost = host.toLowerCase();
        }
        support.firePropertyChange("defaultHost", oldDefaultHost,
                                   this.defaultHost);

    }
    
    public void setName(String name ) {
        if( domain != null ) {
            // keep name==domain, ignore override
            // we are already registered
            super.setName( domain );
            return;
        }
        // The engine name is used as domain
        domain=name; // XXX should we set it in init() ? It shouldn't matter
        super.setName( name );
    }


    /**
     * Set the cluster-wide unique identifier for this Engine.
     * This value is only useful in a load-balancing scenario.
     * <p>
     * This property should not be changed once it is set.
     */
    public void setJvmRoute(String routeId) {
        jvmRouteId = routeId;
    }


    /**
     * Retrieve the cluster-wide unique identifier for this Engine.
     * This value is only useful in a load-balancing scenario.
     */
    public String getJvmRoute() {
        return jvmRouteId;
    }


    /**
     * Set the DefaultContext
     * for new web applications.
     *
     * @param defaultContext The new DefaultContext
     */
    public void addDefaultContext(DefaultContext defaultContext) {

        DefaultContext oldDefaultContext = this.defaultContext;
        this.defaultContext = defaultContext;
        support.firePropertyChange("defaultContext",
                                   oldDefaultContext, this.defaultContext);

    }


    /**
     * Retrieve the DefaultContext for new web applications.
     */
    public DefaultContext getDefaultContext() {
        return (this.defaultContext);
    }


    /**
     * Return the <code>Service</code> with which we are associated (if any).
     */
    public Service getService() {

        return (this.service);

    }


    /**
     * Set the <code>Service</code> with which we are associated (if any).
     *
     * @param service The service that owns this Engine
     */
    public void setService(Service service) {
        this.service = service;
    }

    public String getMbeansFile() {
        return mbeansFile;
    }

    public void setMbeansFile(String mbeansFile) {
        this.mbeansFile = mbeansFile;
    }

    public String getBaseDir() {
        if( baseDir==null ) {
            baseDir=System.getProperty("catalina.base");
        }
        if( baseDir==null ) {
            baseDir=System.getProperty("catalina.home");
        }
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    // --------------------------------------------------------- Public Methods


    /**
     * Install the StandardContext portion of the DefaultContext
     * configuration into current Context.
     *
     * @param context current web application context
     */
    public void installDefaultContext(Context context) {

        if (defaultContext != null &&
            defaultContext instanceof StandardDefaultContext) {

            ((StandardDefaultContext)defaultContext).installDefaultContext(context);
        }
    }


    /**
     * Import the DefaultContext config into a web application context.
     *
     * @param context web application context to import default context
     */
    public void importDefaultContext(Context context) {

        if ( this.defaultContext != null )
            this.defaultContext.importDefaultContext(context);

    }


    /**
     * Add a child Container, only if the proposed child is an implementation
     * of Host.
     *
     * @param child Child container to be added
     */
    public void addChild(Container child) {

        if (!(child instanceof Host))
            throw new IllegalArgumentException
                (sm.getString("standardEngine.notHost"));
        super.addChild(child);

    }


    /**
     * Return descriptive information about this Container implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() {

        return (info);

    }

    /**
     * Disallow any attempt to set a parent for this Container, since an
     * Engine is supposed to be at the top of the Container hierarchy.
     *
     * @param container Proposed parent Container
     */
    public void setParent(Container container) {

        throw new IllegalArgumentException
            (sm.getString("standardEngine.notParent"));

    }


    private boolean initialized=false;
    
    public void init() {
        if( initialized ) return;
        initialized=true;

        if( oname==null ) {
            // not registered in JMX yet - standalone mode
            try {
                if (domain==null) {
                    domain=getName();
                }
                log.debug( "Register " + domain );
                oname=new ObjectName(domain + ":type=Engine");
                controller=oname;
                Registry.getRegistry().registerComponent(this, oname, null);
            } catch( Throwable t ) {
                log.info("Error registering ", t );
            }
        }

        if( mbeansFile == null ) {
            String defaultMBeansFile=getBaseDir() + "/conf/tomcat5-mbeans.xml";
            File f=new File( defaultMBeansFile );
            if( f.exists() ) mbeansFile=f.getAbsolutePath();
        }
        if( mbeansFile != null ) {
            readEngineMbeans();
        }
        if( mbeans != null ) {
            try {
                Registry.getRegistry().invoke(mbeans, "init", false);
            } catch (Exception e) {
                log.error("Error in init() for " + mbeansFile, e);
            }
        }
        
        // not needed since the following if statement does the same thing the right way
        // remove later after checking
        //if( service==null ) {
        //    try {
        //        ObjectName serviceName=getParentName();        
        //        if( mserver.isRegistered( serviceName )) {
        //            log.info("Registering with the service ");
        //            try {
        //                mserver.invoke( serviceName, "setContainer",
        //                        new Object[] { this },
        //                        new String[] { "org.apache.catalina.Container" } );
        //            } catch( Exception ex ) {
        //               ex.printStackTrace();
        //            }
        //        }
        //    } catch( Exception ex ) {
        //        log.error("Error registering with service ");
        //    }
        //}
        
        if( service==null ) {
            // for consistency...: we are probably in embeded mode
            try {
                service=new StandardService();
                service.setContainer( this );
                service.initialize();
            } catch( Throwable t ) {
                t.printStackTrace();
            }
        }
        
    }
    
    public void destroy() throws LifecycleException {
        if( ! initialized ) return;
        initialized=false;
        
        // if we created it, make sure it's also destroyed
        ((StandardService)service).destroy();

        if( mbeans != null ) {
            try {
                Registry.getRegistry().invoke(mbeans, "destroy", false);
            } catch (Exception e) {
                log.error("Error in destroy() for " + mbeansFile, e);
            }
        }
        // 
        if( mbeans != null ) {
            try {
                for( int i=0; i<mbeans.size() ; i++ ) {
                    Registry.getRegistry().unregisterComponent((ObjectName)mbeans.get(i));
                }
            } catch (Exception e) {
                log.error("Error in destroy() for " + mbeansFile, e);
            }
        }
        
        // force all metadata to be reloaded.
        // That doesn't affect existing beans. We should make it per
        // registry - and stop using the static.
        Registry.getRegistry().resetMetadata();
        
                
    }
    
    /**
     * Start this Engine component.
     *
     * @exception LifecycleException if a startup error occurs
     */
    public void start() throws LifecycleException {
        if( started ) {
            return;
        }
        if( !initialized ) {
            init();
        }
        // Log our server identification information
        //System.out.println(ServerInfo.getServerInfo());
        log.info( "Starting Servlet Engine: " + ServerInfo.getServerInfo());
        if( mbeans != null ) {
            try {
                Registry.getRegistry().invoke(mbeans, "start", false);
            } catch (Exception e) {
                log.error("Error in start() for " + mbeansFile, e);
            }
        }

        // Standard container startup
        super.start();

    }
    
    public void stop() throws LifecycleException {
        super.stop();
        if( mbeans != null ) {
            try {
                Registry.getRegistry().invoke(mbeans, "stop", false);
            } catch (Exception e) {
                log.error("Error in stop() for " + mbeansFile, e);
            }
        }
    }


    /**
     * Return a String representation of this component.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("StandardEngine[");
        sb.append(getName());
        sb.append("]");
        return (sb.toString());

    }


    // ------------------------------------------------------ Protected Methods


    // -------------------- JMX registration  --------------------

    public ObjectName preRegister(MBeanServer server,
                                  ObjectName name) throws Exception
    {
        super.preRegister(server,name);

        this.setName( name.getDomain());

        return name;
    }

    // FIXME Remove -- not used 
    public ObjectName getParentName() throws MalformedObjectNameException {
        if (getService()==null) {
            return null;
        }
        String name = getService().getName();
        ObjectName serviceName=new ObjectName(domain +
                        ":type=Service,serviceName="+name);
        return serviceName;                
    }
    
    public ObjectName createObjectName(String domain, ObjectName parent)
        throws Exception
    {
        if( log.isDebugEnabled())
            log.debug("Create ObjectName " + domain + " " + parent );
        return new ObjectName( domain + ":type=Engine");
    }

    
    private void readEngineMbeans() {
        try {
            MbeansSource mbeansMB=new MbeansSource();
            File mbeansF=new File( mbeansFile );
            mbeansMB.setSource(mbeansF);
            
            Registry.getRegistry().registerComponent(mbeansMB, 
                    domain + ":type=MbeansFile", null);
            mbeansMB.load();
            mbeansMB.init();
            mbeansMB.setRegistry(Registry.getRegistry());
            mbeans=mbeansMB.getMBeans();
            
        } catch( Throwable t ) {
            log.error( "Error loading " + mbeansFile, t );
        }
        
    }
    
    public String getDomain() {
        if (domain!=null) {
            return domain;
        } else { 
            return getName();
        }
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
}
