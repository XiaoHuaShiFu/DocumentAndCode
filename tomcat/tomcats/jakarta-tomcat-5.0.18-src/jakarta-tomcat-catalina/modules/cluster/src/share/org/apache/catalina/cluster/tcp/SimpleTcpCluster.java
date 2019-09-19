/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/tcp/SimpleTcpCluster.java,v 1.28 2004/01/13 05:58:25 fhanik Exp $
 * $Revision: 1.28 $
 * $Date: 2004/01/13 05:58:25 $
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

package org.apache.catalina.cluster.tcp;

import java.beans.PropertyChangeSupport;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.HashMap;

import org.apache.catalina.ServerFactory;
import org.apache.catalina.core.StandardServer;

import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Logger;
import org.apache.catalina.Manager;
import org.apache.catalina.util.LifecycleSupport;
import org.apache.catalina.util.StringManager;

import org.apache.catalina.cluster.Member;
import org.apache.catalina.cluster.MembershipFactory;
import org.apache.catalina.cluster.MembershipListener;
import org.apache.catalina.cluster.MembershipService;

import org.apache.catalina.cluster.tcp.ReplicationListener;
import org.apache.catalina.cluster.tcp.ReplicationTransmitter;
import org.apache.catalina.cluster.tcp.SocketSender;
import org.apache.catalina.cluster.io.ListenCallback;

import org.apache.catalina.cluster.SessionMessage;
import org.apache.catalina.cluster.session.ReplicationStream;
import org.apache.catalina.cluster.ClusterManager;
import org.apache.catalina.cluster.Constants;

import org.apache.commons.logging.Log;

import java.io.IOException;
import java.net.URL;
/**
 * A <b>Cluster</b> implementation using simple multicast.
 * Responsible for setting
 * up a cluster and provides callers with a valid multicast receiver/sender.
 *
 * @author Filip Hanik
 * @author Remy Maucherat
 * @version $Revision: 1.28 $, $Date: 2004/01/13 05:58:25 $
 */

public class SimpleTcpCluster
    implements Cluster, Lifecycle,
               MembershipListener, ListenCallback,
               LifecycleListener {


    public static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( SimpleTcpCluster.class );


    // ----------------------------------------------------- Instance Variables


    /**
     * Descriptive information about this component implementation.
     */
    protected static final String info = "SimpleTcpCluster/1.0";


    /**
     * the service that provides the membership
     */
    protected MembershipService service = null;
    /**
     * the class name of the membership service
     */
    protected String serviceclass = null;
    /**
     * The properties for the
     */
    protected java.util.Properties svcproperties = new java.util.Properties();

    /**
     * Tcp address to listen for incoming changes
     */
    protected java.net.InetAddress tcpAddress = null;

    /**
     * The tcp port this instance listens to for incoming changes
     */
    protected int tcpPort = 1234;

    /**
     * number of thread we listen to tcp requests coming in on
     */
    protected int tcpThreadCount = 2;

    /**
     * ReplicationTransmitter to send data with
     */
    protected ReplicationTransmitter mReplicationTransmitter;

    /**
     * Name to register for the background thread.
     */
    protected String threadName = "SimpleTcpCluster";

    /**
     * Whether to expire sessions when shutting down
     */
    protected boolean expireSessionsOnShutdown = true;
    /**
     * Print debug to std.out?
     */
    protected boolean printToScreen = false;
    /**
     * Replicate only sessions that have been marked dirty
     * false=replicate sessions after each request
     */
    protected boolean useDirtyFlag = false;

    /**
     * Name for logging purpose
     */
    protected String clusterImpName = "SimpleTcpCluster";


    /**
     * The string manager for this package.
     */
    protected StringManager sm = StringManager.getManager(Constants.Package);


    /**
     * The background thread completion semaphore.
     */
    protected boolean threadDone = false;


    /**
     * The cluster name to join
     */
    protected String clusterName = null;


    /**
     * The Container associated with this Cluster.
     */
    protected Container container = null;


    /**
     * The lifecycle event support for this component.
     */
    protected LifecycleSupport lifecycle = new LifecycleSupport(this);


    /**
     * Has this component been started?
     */
    protected boolean started = false;


    /**
     * The property change support for this component.
     */
    protected PropertyChangeSupport support = new PropertyChangeSupport(this);


    /**
     * The debug level for this Container
     */
    protected int debug = 0;


    /**
     * The context name <-> manager association for distributed contexts.
     */
    protected HashMap managers = new HashMap();
    /**
     * The context name <-> manager association for all contexts.
     */
    protected HashMap allmanagers = new HashMap();

    /**
     * Nr of milliseconds between every heart beat
     */
    protected long msgFrequency = 500;
    /**
     * java.nio.Channels.Selector.select timeout in case the JDK has a
     * poor nio implementation
     */
    protected long tcpSelectorTimeout = 100;

    /**
     * The channel configuration.
     */
    protected String protocol = null;

    /**
     * The replication mode, can be either synchronous or asynchronous
     * defaults to synchronous
     */
    protected String replicationMode="synchronous";

    private long nrOfMsgsReceived = 0;
    private long msgSendTime = 0;
    private long lastChecked = System.currentTimeMillis();
    private boolean isJdk13 = false;
    private String managerClassName = "org.apache.catalina.cluster.session.DeltaManager";

    // ------------------------------------------------------------- Properties

    public SimpleTcpCluster() {
        try {
            tcpAddress = java.net.InetAddress.getLocalHost();
        }catch ( Exception x ) {
            log.error("In SimpleTcpCluster.constructor()",x);
        }

//        if ( ServerFactory.getServer() instanceof StandardServer ) {
//            StandardServer server = (StandardServer) ServerFactory.getServer();
//            server.addLifecycleListener(this);
//        }

    }
    /**
     * Return descriptive information about this Cluster implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() {
        return(this.info);
    }


    /**
     * Set the debug level for this component
     *
     * @param debug The debug level
     */
    public void setDebug(int debug) {
        this.debug = debug;
    }


    /**
     * Get the debug level for this component
     *
     * @return The debug level
     */
    public int getDebug() {
        return(this.debug);
    }

    public void setReplicationMode(String mode) {
        String msg = IDataSenderFactory.validateMode(mode);
        if ( msg == null ) {
            log.debug("Setting replcation mode to "+mode);
            this.replicationMode = mode;
        } else
            throw new IllegalArgumentException(msg);

    }
    /**
     * Set the name of the cluster to join, if no cluster with
     * this name is present create one.
     *
     * @param clusterName The clustername to join
     */
    public void setClusterName(String clusterName) {
        String oldClusterName = this.clusterName;
        this.clusterName = clusterName;
        support.firePropertyChange("clusterName",
                                   oldClusterName,
                                   this.clusterName);
    }


    /**
     * Return the name of the cluster that this Server is currently
     * configured to operate within.
     *
     * @return The name of the cluster associated with this server
     */
    public String getClusterName() {
        return(this.clusterName);
    }


    /**
     * Set the Container associated with our Cluster
     *
     * @param container The Container to use
     */
    public void setContainer(Container container) {
        Container oldContainer = this.container;
        this.container = container;
        support.firePropertyChange("container",
                                   oldContainer,
                                   this.container);
        //this.container.
    }


    /**
     * Get the Container associated with our Cluster
     *
     * @return The Container associated with our Cluster
     */
    public Container getContainer() {
        return(this.container);
    }


    /**
     * Sets the configurable protocol stack. This is a setting in server.xml
     * where you can configure your protocol.
     *
     * @param protocol the protocol stack - this method is called by
     * the server configuration at startup
     * @see <a href="www.javagroups.com">JavaGroups</a> for details
     */
    public void setProtocol(String protocol) {
        String oldProtocol = this.protocol;
        this.protocol = protocol;
        support.firePropertyChange("protocol", oldProtocol, this.protocol);
    }


    /**
     * Returns the protocol.
     */
    public String getProtocol() {
        return (this.protocol);
    }

    public Member[] getMembers() {
        return service.getMembers();
    }



    // --------------------------------------------------------- Public Methods


    public synchronized Manager createManager(String name) {
        ClusterManager manager = null;
        try {
            manager = (ClusterManager)getClass().getClassLoader().loadClass(getManagerClassName()).newInstance();
        } catch ( Exception x ) {
            log.error("Unable to load class for replication manager",x);
            manager = new org.apache.catalina.cluster.session.SimpleTcpReplicationManager();
        }
        manager.setName(name);
        manager.setCluster(this);
        manager.setDistributable(true);
        manager.setExpireSessionsOnShutdown(expireSessionsOnShutdown);
        manager.setUseDirtyFlag(useDirtyFlag);
        allmanagers.put(name, manager);
        managers.put(name,manager);
        return manager;
    }


    // ------------------------------------------------------ Lifecycle Methods


    /**
     * Add a lifecycle event listener to this component.
     *
     * @param listener The listener to add
     */
    public void addLifecycleListener(LifecycleListener listener) {
        lifecycle.addLifecycleListener(listener);
    }


    /**
     * Get the lifecycle listeners associated with this lifecycle. If this
     * Lifecycle has no listeners registered, a zero-length array is returned.
     */
    public LifecycleListener[] findLifecycleListeners() {

        return lifecycle.findLifecycleListeners();

    }


    /**
     * Remove a lifecycle event listener from this component.
     *
     * @param listener The listener to remove
     */
    public void removeLifecycleListener(LifecycleListener listener) {
        lifecycle.removeLifecycleListener(listener);
    }


    /**
     * Prepare for the beginning of active use of the public methods of this
     * component.  This method should be called after <code>configure()</code>,
     * and before any of the public methods of the component are utilized.<BR>
     * Starts the cluster communication channel, this will connect with the
     * other nodes in the cluster, and request the current session state to
     * be transferred to this node.
     *
     * @exception IllegalStateException if this component has already been
     *  started
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents this component from being used
     */
    public void start()
        throws LifecycleException {
        if (started)
            throw new LifecycleException
                (sm.getString("cluster.alreadyStarted"));
        log.info("Cluster is about to start");
        try {
            if ( isJdk13 ) {
                Jdk13ReplicationListener mReplicationListener =
                    new Jdk13ReplicationListener(this,
                                            this.tcpThreadCount,
                                            this.tcpAddress,
                                            this.tcpPort,
                                            this.tcpSelectorTimeout,
                                            "synchronous".equals(this.
                    replicationMode));
                Thread t = new Thread(mReplicationListener);
                t.setName("Cluster-TcpListener");
                t.setDaemon(true);
                t.start();
            } else {
                ReplicationListener mReplicationListener =
                    new ReplicationListener(this,
                                            this.tcpThreadCount,
                                            this.tcpAddress,
                                            this.tcpPort,
                                            this.tcpSelectorTimeout,
                                            IDataSenderFactory.SYNC_MODE.equals(replicationMode) ||
                                            IDataSenderFactory.POOLED_SYNC_MODE.equals(replicationMode));
                mReplicationListener.setName("Cluster-ReplicationListener");
                mReplicationListener.setDaemon(true);
                mReplicationListener.start();
            }

            mReplicationTransmitter = new ReplicationTransmitter(new IDataSender[0]);
            mReplicationTransmitter.start();

            //wait 5 seconds to establish the view membership
            log.info("Sleeping for "+(msgFrequency*4)+" secs to establish cluster membership");
            service = MembershipFactory.getMembershipService(serviceclass,svcproperties);
            service.addMembershipListener(this);
            service.start();
            Thread.currentThread().sleep((msgFrequency*4));
            this.started = true;
        } catch ( Exception x ) {
            log.error("Unable to start cluster.",x);
        }


    }


    public void send(SessionMessage msg, Member dest) {
        try
        {
            msg.setAddress(service.getLocalMember());
            Member destination = dest;
            if ( (destination == null) && (msg.getEventType() == SessionMessage.EVT_GET_ALL_SESSIONS) ) {
                if (service.getMembers().length > 0)
                    destination = service.getMembers()[0];
            }
            msg.setTimestamp(System.currentTimeMillis());
            java.io.ByteArrayOutputStream outs = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(outs);
            out.writeObject(msg);
            byte[] data = outs.toByteArray();
            if(destination != null) {
                  Member tcpdest = dest;
                  if ( (tcpdest != null) && (!service.getLocalMember().equals(tcpdest)))  {
                       mReplicationTransmitter.sendMessage(msg.getSessionID(),
                                                           data,
                                                           InetAddress.getByName(tcpdest.getHost()),
                                                           tcpdest.getPort());
                  }//end if
            }
            else {
                mReplicationTransmitter.sendMessage(msg.getSessionID(),data);
            }
        } catch ( Exception x ) {
            log.error("Unable to send message through tcp channel",x);
        }
    }

    public void send(SessionMessage msg) {
        send(msg,null);
    }

    /**
     * Gracefully terminate the active use of the public methods of this
     * component.  This method should be the last one called on a given
     * instance of this component.<BR>
     * This will disconnect the cluster communication channel and stop
     * the listener thread.
     *
     * @exception IllegalStateException if this component has not been started
     * @exception LifecycleException if this component detects a fatal error
     *  that needs to be reported
     */
    public void stop()
        throws LifecycleException {

        if (!started)
            throw new IllegalStateException
                (sm.getString("cluster.notStarted"));

    }


    public void memberAdded(Member member) {
        try  {
            log.info("Replication member added:" + member);
            Member mbr = member;
            mReplicationTransmitter.add(IDataSenderFactory.getIDataSender(replicationMode,mbr));
        } catch ( Exception x ) {
            log.error("Unable to connect to replication system.",x);
        }

    }

    public void memberDisappeared(Member member)
    {
        log.info("Received member disappeared:"+member);
        try
        {
            Member mbr = member;
            mReplicationTransmitter.remove(InetAddress.getByName(mbr.getHost()),
                                 mbr.getPort());
        }
        catch ( Exception x )
        {
            log.error("Unable remove cluster node from replication system.",x);
        }

    }

    public void setServiceclass(String clazz){
        this.serviceclass = clazz;
    }
    public void setMcastAddr(String addr) {
        svcproperties.setProperty("mcastAddress",addr);
    }


    public void setMcastBindAddress(String bindaddr) {
        svcproperties.setProperty("mcastBindAddress",bindaddr);
    }

    public void setMcastPort(int port) {
        svcproperties.setProperty("mcastPort",String.valueOf(port));
    }

    public void setMcastFrequency(long time) {
        svcproperties.setProperty("msgFrequency",String.valueOf(time));
        msgFrequency = time;
    }

    public void setMcastDropTime(long time) {
        svcproperties.setProperty("memberDropTime",String.valueOf(time));
    }

    public void setTcpThreadCount(int count) {
        this.tcpThreadCount = count;
    }

    public void setTcpListenAddress(String address)  {
        try {
            if ("auto".equals(address) )
            {
                address = java.net.InetAddress.getLocalHost().getHostAddress();
                tcpAddress = java.net.InetAddress.getByName(address);
            }
            else
            {
                tcpAddress = java.net.InetAddress.getByName(address);
            }//end if
            svcproperties.setProperty("tcpListenHost",address);
        }catch ( Exception x ){
            log.error("Unable to set listen address",x);
        }
    }

    public void setExpireSessionsOnShutdown(boolean expireSessionsOnShutdown){
        this.expireSessionsOnShutdown = expireSessionsOnShutdown;
    }

    public void setPrintToScreen(boolean printToScreen) {
        this.printToScreen = printToScreen;
    }
    public void setUseDirtyFlag(boolean useDirtyFlag) {
        this.useDirtyFlag = useDirtyFlag;
    }


    public void setTcpListenPort(int port) {
        this.tcpPort = port;
        svcproperties.setProperty("tcpListenPort",String.valueOf(port));
    }

    public void setTcpSelectorTimeout(long timeout) {
        this.tcpSelectorTimeout = timeout;
    }



    public void messageDataReceived(byte[] data) {
        try {
            ReplicationStream stream =
                new ReplicationStream(new java.io.ByteArrayInputStream(data),
                                      getClass().getClassLoader());
            Object myobj = stream.readObject();
            if ( myobj != null && myobj instanceof SessionMessage ) {
                SessionMessage msg = (SessionMessage)myobj;
                //remove when checking in
                perfMessageRecvd(msg.getTimestamp());
                String name = msg.getContextName();
                //check if the message is a EVT_GET_ALL_SESSIONS,
                //if so, wait until we are fully started up
                if ( name == null ) {
                    java.util.Iterator i = managers.keySet().iterator();
                    while ( i.hasNext() ) {
                        String key = (String)i.next();
                        ClusterManager mgr = (ClusterManager) managers.get(key);
                        if (mgr != null)
                            mgr.messageDataReceived(msg);
                        else {
                            //this happens a lot before the system has started up
                            log.debug("Context manager doesn't exist:" + key);
                        }
                    }//while
                } else {
                    ClusterManager mgr = (ClusterManager) managers.get(name);
                    if (mgr != null)
                        mgr.messageDataReceived(msg);
                    else
                        log.warn("Context manager doesn't exist:" + name);
                }//end if
            }  else
                log.warn("Received invalid message myobj="+myobj);
        } catch ( Exception x ) {
            log.error("Unable to deserialize session message.",x);
        }
    }

    public void lifecycleEvent(LifecycleEvent lifecycleEvent){
    }

    // --------------------------------------------------------- Cluster Wide Deployments
    /**
     * Start an existing web application, attached to the specified context
     * path in all the other nodes in the cluster.
     * Only starts a web application if it is not running.
     *
     * @param contextPath The context path of the application to be started
     *
     * @exception IllegalArgumentException if the specified context path
     *  is malformed (it must be "" or start with a slash)
     * @exception IllegalArgumentException if the specified context path does
     *  not identify a currently installed web application
     * @exception IOException if an input/output error occurs during
     *  startup
     */
    public void startContext(String contextPath) throws IOException {
        return;
    }


    /**
     * Install a new web application, whose web application archive is at the
     * specified URL, into this container with the specified context path.
     * A context path of "" (the empty string) should be used for the root
     * application for this container.  Otherwise, the context path must
     * start with a slash.
     * <p>
     * If this application is successfully installed, a ContainerEvent of type
     * <code>PRE_INSTALL_EVENT</code> will be sent to registered listeners
     * before the associated Context is started, and a ContainerEvent of type
     * <code>INSTALL_EVENT</code> will be sent to all registered listeners
     * after the associated Context is started, with the newly created
     * <code>Context</code> as an argument.
     *
     * @param contextPath The context path to which this application should
     *  be installed (must be unique)
     * @param war A URL of type "jar:" that points to a WAR file, or type
     *  "file:" that points to an unpacked directory structure containing
     *  the web application to be installed
     *
     * @exception IllegalArgumentException if the specified context path
     *  is malformed (it must be "" or start with a slash)
     * @exception IllegalStateException if the specified context path
     *  is already attached to an existing web application
     * @exception IOException if an input/output error was encountered
     *  during installation
     */
    public void installContext(String contextPath, URL war) {
        System.out.println("\n\n\n\nCluster Install called for context:"+contextPath+"\n\n\n\n");
    }


    /**
     * Stop an existing web application, attached to the specified context
     * path.  Only stops a web application if it is running.
     *
     * @param contextPath The context path of the application to be stopped
     *
     * @exception IllegalArgumentException if the specified context path
     *  is malformed (it must be "" or start with a slash)
     * @exception IllegalArgumentException if the specified context path does
     *  not identify a currently installed web application
     * @exception IOException if an input/output error occurs while stopping
     *  the web application
     */
    public void stop(String contextPath) throws IOException {
        return;
    }



    // ---------------------------------------------  Inner Class

    // ---------------------------------------------  Performance

    private void perfMessageRecvd(long timeSent) {
        nrOfMsgsReceived++;
        msgSendTime+=(System.currentTimeMillis()-timeSent);
        if ( (System.currentTimeMillis() - lastChecked) > 5000 ) {
            log.debug("Calc msg send time total="+msgSendTime+"ms num request="+nrOfMsgsReceived+" average per msg="+(msgSendTime/nrOfMsgsReceived)+"ms.");
        }
    }
    public boolean getIsJdk13() {
        return isJdk13;
    }
    public void setIsJdk13(boolean isJdk13) {
        this.isJdk13 = isJdk13;
    }
    public String getManagerClassName() {
        return managerClassName;
    }
    public void setManagerClassName(String managerClassName) {
        this.managerClassName = managerClassName;
    }

}
