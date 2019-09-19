/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/session/SimpleTcpReplicationManager.java,v 1.20 2004/01/12 07:50:06 fhanik Exp $
 * $Revision: 1.20 $
 * $Date: 2004/01/12 07:50:06 $
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
package org.apache.catalina.cluster.session;

import java.io.*;
import org.apache.catalina.Session;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.util.CustomObjectInputStream;
import org.apache.catalina.cluster.io.ListenCallback;
import org.apache.catalina.cluster.tcp.ReplicationListener;
import org.apache.catalina.cluster.tcp.ReplicationTransmitter;
import org.apache.catalina.cluster.tcp.IDataSender;
import org.apache.catalina.cluster.tcp.SocketSender;
import org.apache.catalina.cluster.Member;
import org.apache.catalina.cluster.tcp.SimpleTcpCluster;
import org.apache.catalina.cluster.SessionMessage;



import java.net.InetAddress;
/**
 * Title:        Tomcat Session Replication for Tomcat 4.0 <BR>
 * Description:  A very simple straight forward implementation of
 *               session replication of servers in a cluster.<BR>
 *               This session replication is implemented "live". By live
 *               I mean, when a session attribute is added into a session on Node A
 *               a message is broadcasted to other messages and setAttribute is called on the
 *               replicated sessions.<BR>
 *               A full description of this implementation can be found under
 *               <href="http://www.filip.net/tomcat/">Filip's Tomcat Page</a><BR>
 *
 * Copyright:    See apache license
 * Company:      www.filip.net
 * @author  <a href="mailto:mail@filip.net">Filip Hanik</a>
 * @author Bela Ban (modifications for synchronous replication)
 * @version 1.0 for TC 4.0
 * Description: The InMemoryReplicationManager is a session manager that replicated
 * session information in memory. It uses <a href="www.javagroups.com">JavaGroups</a> as
 * a communication protocol to ensure guaranteed and ordered message delivery.
 * JavaGroups also provides a very flexible protocol stack to ensure that the replication
 * can be used in any environment.
 * <BR><BR>
 * The InMemoryReplicationManager extends the StandardManager hence it allows for us
 * to inherit all the basic session management features like expiration, session listeners etc
 * <BR><BR>
 * To communicate with other nodes in the cluster, the InMemoryReplicationManager sends out 7 different type of multicast messages
 * all defined in the SessionMessage class.<BR>
 * When a session is replicated (not an attribute added/removed) the session is serialized into
 * a byte array using the StandardSession.readObjectData, StandardSession.writeObjectData methods.
 */
public class SimpleTcpReplicationManager extends org.apache.catalina.session.StandardManager
implements org.apache.catalina.cluster.ClusterManager
{

    //the channel configuration
    protected String mChannelConfig = null;

    //the group name
    protected String mGroupName = "TomcatReplication";

    //somehow start() gets called more than once
    protected boolean mChannelStarted = false;

    //log to screen
    protected boolean mPrintToScreen = true;



    protected boolean mManagerRunning = false;

    /** Use synchronous rather than asynchronous replication. Every session modification (creation, change, removal etc)
     * will be sent to all members. The call will then wait for max milliseconds, or forever (if timeout is 0) for
     * all responses.
     */
    protected boolean synchronousReplication=true;

    /** Set to true if we don't want the sessions to expire on shutdown */
    protected boolean mExpireSessionsOnShutdown = true;

    protected boolean useDirtyFlag = false;

    protected String name;

    protected int debug = 0;

    protected boolean distributable = true;

    protected org.apache.catalina.cluster.tcp.SimpleTcpCluster cluster;

    protected java.util.HashMap invalidatedSessions = new java.util.HashMap();

    /**
     * Flag to keep track if the state has been transferred or not
     * Assumes false.
     */
    protected boolean stateTransferred = false;

    /**
     * Constructor, just calls super()
     *
     */
    public SimpleTcpReplicationManager()
    {
        super();
    }


    public boolean isManagerRunning()
    {
        return mManagerRunning;
    }

    public void setUseDirtyFlag(boolean usedirtyflag)
    {
        this.useDirtyFlag = usedirtyflag;
    }

    public void setExpireSessionsOnShutdown(boolean expireSessionsOnShutdown)
    {
        mExpireSessionsOnShutdown = expireSessionsOnShutdown;
    }

    public void setCluster(SimpleTcpCluster cluster) {
        this.log("Cluster associated with SimpleTcpReplicationManager");
        this.cluster = cluster;
    }

    public boolean getExpireSessionsOnShutdown()
    {
        return mExpireSessionsOnShutdown;
    }

    public void setPrintToScreen(boolean printtoscreen)
    {
        log("Setting screen debug to:"+printtoscreen);
        mPrintToScreen = printtoscreen;
    }

    public void setSynchronousReplication(boolean flag)
    {
        synchronousReplication=flag;
    }

    /**
     * Override persistence since they don't go hand in hand with replication for now.
     */
    public void unload() throws IOException {
        if ( !getDistributable() ) {
            super.unload();
        }
    }

    public int getDebug()
    {
        return this.debug;
    }

    public void setDebug(int debug) {
        this.debug = debug;
        //super.setDebug(debug);
    }
    /**
     * Creates a HTTP session.
     * Most of the code in here is copied from the StandardManager.
     * This is not pretty, yeah I know, but it was necessary since the
     * StandardManager had hard coded the session instantiation to the a
     * StandardSession, when we actually want to instantiate a ReplicatedSession<BR>
     * If the call comes from the Tomcat servlet engine, a SessionMessage goes out to the other
     * nodes in the cluster that this session has been created.
     * @param notify - if set to true the other nodes in the cluster will be notified.
     *                 This flag is needed so that we can create a session before we deserialize
     *                 a replicated one
     *
     * @see ReplicatedSession
     */
    protected Session createSession(boolean notify, boolean setId)
    {

        //inherited from the basic manager
        if ((getMaxActiveSessions() >= 0) &&
           (sessions.size() >= getMaxActiveSessions()))
            throw new IllegalStateException(sm.getString("standardManager.createSession.ise"));


        Session session = new ReplicatedSession(this);

        // Initialize the properties of the new session and return it
        session.setNew(true);
        session.setValid(true);
        session.setCreationTime(System.currentTimeMillis());
        session.setMaxInactiveInterval(this.maxInactiveInterval);
        String sessionId = generateSessionId();
        String jvmRoute = getJvmRoute();
        // @todo Move appending of jvmRoute generateSessionId()???
        if (jvmRoute != null) {
            sessionId += '.' + jvmRoute;
        }
        /*
        synchronized (sessions) {
        while (sessions.get(sessionId) != null)        // Guarantee uniqueness
        sessionId = generateSessionId();
        }
        */
        if ( setId ) session.setId(sessionId);

        if ( notify && (cluster!=null) )
        {
            //notify javagroups

            //SessionMessage msg = new SessionMessage(this.name,
            //                                        SessionMessage.EVT_SESSION_CREATED,
            //                                        writeSession(session),
            //                                        session.getId());
            //cluster.send(msg);
            ((ReplicatedSession)session).setIsDirty(true);
        }
        return (session);
    }//createSession

    //=========================================================================
    // OVERRIDE THESE METHODS TO IMPLEMENT THE REPLICATION
    //=========================================================================

    /**
     * Construct and return a new session object, based on the default
     * settings specified by this Manager's properties.  The session
     * id will be assigned by this method, and available via the getId()
     * method of the returned session.  If a new session cannot be created
     * for any reason, return <code>null</code>.
     *
     * @exception IllegalStateException if a new session cannot be
     *  instantiated for any reason
     */
    public Session createSession()
    {
        //create a session and notify the other nodes in the cluster
        Session session =  createSession(getDistributable(),true);
        add(session);
        return session;
    }

    public void sessionInvalidated(String sessionId) {
        synchronized ( invalidatedSessions ) {
            invalidatedSessions.put(sessionId, sessionId);
        }
    }

    public String[] getInvalidatedSessions() {
        synchronized ( invalidatedSessions ) {
            String[] result = new String[invalidatedSessions.size()];
            invalidatedSessions.values().toArray(result);
            return result;
        }

    }

    public SessionMessage requestCompleted(String sessionId)
    {
        if (  !getDistributable() ) {
            log.warn("Received requestCompleted message, although this context["+
                     getName()+"] is not distributable. Ignoring message");
            return null;
        }
        //notify javagroups
        try
        {
            if ( invalidatedSessions.get(sessionId) != null ) {
                synchronized ( invalidatedSessions ) {
                    invalidatedSessions.remove(sessionId);
                    SessionMessage msg = new SessionMessage(name,
                    SessionMessage.EVT_SESSION_EXPIRED,
                    null,
                    sessionId);
                return msg;
                }
            } else {
                ReplicatedSession session = (ReplicatedSession) findSession(
                    sessionId);
                if (session != null) {
                    //return immediately if the session is not dirty
                    if (useDirtyFlag && (!session.isDirty())) {
                        //but before we return doing nothing,
                        //see if we should send
                        //an updated last access message so that
                        //sessions across cluster dont expire
                        long interval = session.getMaxInactiveInterval();
                        long lastaccdist = System.currentTimeMillis() -
                            session.getLastAccessWasDistributed();
                        if ( ((interval*1000) / lastaccdist)< 3 ) {
                            SessionMessage accmsg = new SessionMessage(name,
                                SessionMessage.EVT_SESSION_ACCESSED,
                                null,
                                sessionId);
                            session.setLastAccessWasDistributed(System.currentTimeMillis());
                            return accmsg;
                        }
                        return null;
                    }

                    session.setIsDirty(false);
                    if (getDebug() > 5) {
                        try {
                            log.debug("Sending session to cluster=" + session);
                        }
                        catch (Exception ignore) {}
                    }
                    SessionMessage msg = new SessionMessage(name,
                        SessionMessage.EVT_SESSION_CREATED,
                        writeSession(session),
                        session.getId());
                    return msg;
                } //end if
            }//end if
        }
        catch (Exception x )
        {
            log("Unable to replicate session",x);
        }
        return null;
    }

    /**
     * Serialize a session into a byte array<BR>
     * This method simple calls the writeObjectData method on the session
     * and returns the byte data from that call
     * @param session - the session to be serialized
     * @return a byte array containing the session data, null if the serialization failed
     */
    protected byte[] writeSession( Session session )
    {
        try
        {
            java.io.ByteArrayOutputStream session_data = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream session_out = new java.io.ObjectOutputStream(session_data);
            session_out.flush();
            boolean hasPrincipal = session.getPrincipal() != null;
            session_out.writeBoolean(hasPrincipal);
            if ( hasPrincipal )
            {
                session_out.writeObject(SerializablePrincipal.createPrincipal((GenericPrincipal)session.getPrincipal()));
            }//end if
            ((ReplicatedSession)session).writeObjectData(session_out);
            return session_data.toByteArray();

        }
        catch ( Exception x )
        {
            log("Failed to serialize the session!",x,1);
        }
        return null;
    }

    /**
     * Reinstantiates a serialized session from the data passed in.
     * This will first call createSession() so that we get a fresh instance with all
     * the managers set and all the transient fields validated.
     * Then it calls Session.readObjectData(byte[]) to deserialize the object
     * @param data - a byte array containing session data
     * @return a valid Session object, null if an error occurs
     *
     */
    protected Session readSession( byte[] data, String sessionId )
    {
        try
        {
            java.io.ByteArrayInputStream session_data = new java.io.ByteArrayInputStream(data);
            ReplicationStream session_in = new ReplicationStream(session_data,container.getLoader().getClassLoader());

            Session session = sessionId!=null?this.findSession(sessionId):null;
            //clear the old values from the existing session
            if ( session!=null ) {
                ReplicatedSession rs = (ReplicatedSession)session;
                rs.expire(false);
                session = null;
            }//end if

            if (session==null) session = createSession(false,false);
            boolean hasPrincipal = session_in.readBoolean();
            SerializablePrincipal p = null;
            if ( hasPrincipal )
                p = (SerializablePrincipal)session_in.readObject();
            ((ReplicatedSession)session).readObjectData(session_in);
            if ( hasPrincipal )
                session.setPrincipal(p.getPrincipal(getContainer().getRealm()));
            return session;

        }
        catch ( Exception x )
        {
            log("Failed to deserialize the session!",x,1);
        }
        return null;
    }

    public String getName() {
        return this.name;
    }
    /**
     * Prepare for the beginning of active use of the public methods of this
     * component.  This method should be called after <code>configure()</code>,
     * and before any of the public methods of the component are utilized.<BR>
     * Starts the cluster communication channel, this will connect with the other nodes
     * in the cluster, and request the current session state to be transferred to this node.
     * @exception IllegalStateException if this component has already been
     *  started
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents this component from being used
     */
    public void start() throws LifecycleException {
        mManagerRunning = true;
        super.start();
        //start the javagroups channel
        try {
            //the channel is already running
            if ( mChannelStarted ) return;
            log("Starting clustering manager...:"+getName(),1);
            if ( cluster == null ) {
                log("Starting... no cluster associated with this context:"+getName(),1);
                return;
            }

            if (cluster.getMembers().length > 0) {
                Member mbr = cluster.getMembers()[0];
                SessionMessage msg =
                    new SessionMessage(this.getName(),
                                       SessionMessage.EVT_GET_ALL_SESSIONS,
                                       null,
                                       "GET-ALL");
                cluster.send(msg, mbr);
                log.warn("Manager["+getName()+"], requesting session state from "+mbr+
                         ". This operation will timeout if no session state has been received within "+
                         "60 seconds");
                long reqStart = System.currentTimeMillis();
                long reqNow = 0;
                boolean isTimeout=false;
                do {
                    try {
                        Thread.currentThread().sleep(100);
                    }catch ( Exception sleep) {}
                    reqNow = System.currentTimeMillis();
                    isTimeout=((reqNow-reqStart)>(1000*60));
                } while ( (!isStateTransferred()) && (!isTimeout));
                if ( isTimeout || (!isStateTransferred()) ) {
                    log.error("Manager["+getName()+"], No session state received, timing out.");
                }else {
                    log.info("Manager["+getName()+"], session state received in "+(reqNow-reqStart)+" ms.");
                }
            } else {
                log.info("Manager["+getName()+"], skipping state transfer. No members active in cluster group.");
            }//end if
            mChannelStarted = true;
        }  catch ( Exception x ) {
            log("Unable to start SimpleTcpReplicationManager",x,1);
        }
    }

    /**
     * Gracefully terminate the active use of the public methods of this
     * component.  This method should be the last one called on a given
     * instance of this component.<BR>
     * This will disconnect the cluster communication channel and stop the listener thread.
     * @exception IllegalStateException if this component has not been started
     * @exception LifecycleException if this component detects a fatal error
     *  that needs to be reported
     */
    public void stop() throws LifecycleException
    {
        mManagerRunning = false;
        mChannelStarted = false;
        super.stop();
        //stop the javagroup channel
        try
        {
//            mReplicationListener.stopListening();
//            mReplicationTransmitter.stop();
//            service.stop();
//            service = null;
        }
        catch ( Exception x )
        {
            log("Unable to stop javagroups channel",x,1);
        }
    }

    public void setDistributable(boolean dist) {
        this.distributable = dist;
    }

    public boolean getDistributable() {
        return distributable;
    }

    /**
     * This method is called by the received thread when a SessionMessage has
     * been received from one of the other nodes in the cluster.
     * @param msg - the message received
     * @param sender - the sender of the message, this is used if we receive a
     *                 EVT_GET_ALL_SESSION message, so that we only reply to
     *                 the requesting node
     */
    protected void messageReceived( SessionMessage msg, Member sender ) {
        try  {
            log("Received SessionMessage of type="+msg.getEventTypeString(),3);
            log("Received SessionMessage sender="+sender,3);
            switch ( msg.getEventType() ) {
                case SessionMessage.EVT_GET_ALL_SESSIONS: {
                    //get a list of all the session from this manager
                    Object[] sessions = findSessions();
                    java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
                    java.io.ObjectOutputStream oout = new java.io.ObjectOutputStream(bout);
                    oout.writeInt(sessions.length);
                    for (int i=0; i<sessions.length; i++){
                        ReplicatedSession ses = (ReplicatedSession)sessions[i];
                        oout.writeUTF(ses.getId());
                        byte[] data = writeSession(ses);
                        oout.writeObject(data);
                    }//for
                    //don't send a message if we don't have to
                    oout.flush();
                    oout.close();
                    byte[] data = bout.toByteArray();
                    SessionMessage newmsg = new SessionMessage(name,
                        SessionMessage.EVT_ALL_SESSION_DATA,
                        data, "");
                    cluster.send(newmsg, sender);
                    break;
                }
                case SessionMessage.EVT_ALL_SESSION_DATA: {
                    java.io.ByteArrayInputStream bin =
                        new java.io.ByteArrayInputStream(msg.getSession());
                    java.io.ObjectInputStream oin = new java.io.ObjectInputStream(bin);
                    int size = oin.readInt();
                    for ( int i=0; i<size; i++) {
                        String id = oin.readUTF();
                        byte[] data = (byte[])oin.readObject();
                        Session session = readSession(data,id);
                        session.setManager(this);
                        add(session);
                    }//for
                    stateTransferred=true;
                    break;
                }
                case SessionMessage.EVT_SESSION_CREATED: {
                    Session session = this.readSession(msg.getSession(),msg.getSessionID());
                    session.setManager(this);
                    add(session);
                    session.setValid(true);
                    session.access();
                    if ( getDebug()  > 5 ) log("Received replicated session="+session);
                    break;
                }
                case SessionMessage.EVT_SESSION_EXPIRED: {
                    Session session = findSession(msg.getSessionID());
                    if ( session != null ) {
                        session.expire();
                        this.remove(session);
                    }//end if
                    break;
                }
                case SessionMessage.EVT_SESSION_ACCESSED :{
                    Session session = findSession(msg.getSessionID());
                    if ( session != null ) {
                        session.access();
                    }
                    break;
                }
                default:  {
                    //we didn't recognize the message type, do nothing
                    break;
                }
            }//switch
        }
        catch ( Exception x )
        {
            log("Unable to receive message through TCP channel",x,1);
        }
    }

    public void messageDataReceived(SessionMessage msg) {
        try {
            messageReceived(msg, msg.getAddress()!=null?(Member)msg.getAddress():null);
        } catch(Throwable ex){
            log("InMemoryReplicationManager.messageDataReceived()", ex);
        }//catch
    }

    public boolean isStateTransferred() {
        return stateTransferred;
    }

    public void log(String msg)  {
        log(msg,3);
    }

    public void log(String msg, Throwable x) {
        log(msg,x,3);
    }
    public void log(String msg, int level) {
        if ( getDebug() >= level ) {
            String lmsg = msg;
            if ( mPrintToScreen ) System.out.println(lmsg);
            SimpleTcpCluster.log.info(lmsg);
        }
    }

    public void log(String msg, Throwable x, int level)  {
        if ( getDebug() >= level )  {
            String lmsg = msg;
            if ( mPrintToScreen ) {
                System.out.println(lmsg);
                x.printStackTrace();
            }
            SimpleTcpCluster.log.error(lmsg,x);
        }//end if
    }
    public void setName(String name) {
        this.name = name;
    }
}
