/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/session/ReplicatedSession.java,v 1.9 2004/01/12 05:23:10 fhanik Exp $
 * $Revision: 1.9 $
 * $Date: 2004/01/12 05:23:10 $
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

/**
 * Title:        Tomcat Session Replication for Tomcat 4.0 <BR>
 * Description:  A very simple straight forward implementation of
 *               session replication of servers in a cluster.<BR>
 *               This session replication is implemented "live". By live
 *               I mean, when a session attribute is added into a session on Node A
 *               a message is broadcasted to other messages and setAttribute is called on the replicated
 *               sessions.<BR>
 *               A full description of this implementation can be found under
 *               <href="http://www.filip.net/tomcat/">Filip's Tomcat Page</a><BR>
 *
 * Copyright:    See apache license
 * Company:      www.filip.net
 * @author  <a href="mailto:mail@filip.net">Filip Hanik</a>
 * @version 1.0 for TC 4.0
 * Description:<BR>
 * The ReplicatedSession class is a simple extension of the StandardSession class
 * It overrides a few methods (setAttribute, removeAttribute, expire, access) and has
 * hooks into the InMemoryReplicationManager to broadcast and receive events from the cluster.<BR>
 * This class inherits the readObjectData and writeObject data methods from the StandardSession
 * and does not contain any serializable elements in addition to the inherited ones from the StandardSession
 *
 */
import org.apache.catalina.Manager;
import org.apache.catalina.session.ManagerBase;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.SessionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Principal;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ReplicatedSession extends org.apache.catalina.session.StandardSession
implements org.apache.catalina.cluster.ClusterSession{

    private transient Manager mManager = null;
    protected boolean isDirty = false;
    private transient long lastAccessWasDistributed = System.currentTimeMillis();
    private boolean isPrimarySession=true;

    public ReplicatedSession(Manager manager) {
        super(manager);
        mManager = manager;
    }


    public boolean isDirty()
    {
        return isDirty;
    }

    public void setIsDirty(boolean dirty)
    {
        isDirty = dirty;
    }


    public void setLastAccessWasDistributed(long time) {
        lastAccessWasDistributed = time;
    }

    public long getLastAccessWasDistributed() {
        return lastAccessWasDistributed;
    }


    public void removeAttribute(String name) {
        setIsDirty(true);
        super.removeAttribute(name);
    }

    /**
     * see parent description,
     * plus we also notify other nodes in the cluster
     */
    public void removeAttribute(String name, boolean notify) {
        setIsDirty(true);
        super.removeAttribute(name,notify);
    }


    /**
     * Sets an attribute and notifies the other nodes in the cluster
     */
    public void setAttribute(String name, Object value)
    {
        if ( value == null ) {
          removeAttribute(name);
          return;
        }
        if (!(value instanceof java.io.Serializable))
            throw new java.lang.IllegalArgumentException("Value for attribute "+name+" is not serializable.");
        setIsDirty(true);
        super.setAttribute(name,value);
    }

    public void setMaxInactiveInterval(int interval) {
        setIsDirty(true);
        super.setMaxInactiveInterval(interval);
    }


    /**
     * Sets the manager for this session
     * @param mgr - the servers InMemoryReplicationManager
     */
    public void setManager(SimpleTcpReplicationManager mgr)
    {
        mManager = mgr;
        super.setManager(mgr);
    }


    /**
     * Set the authenticated Principal that is associated with this Session.
     * This provides an <code>Authenticator</code> with a means to cache a
     * previously authenticated Principal, and avoid potentially expensive
     * <code>Realm.authenticate()</code> calls on every request.
     *
     * @param principal The new Principal, or <code>null</code> if none
     * @param jgnotify notify the other nodes in the cluster? (true/false)
     */
    public void setPrincipal(Principal principal) {
        super.setPrincipal(principal);
        setIsDirty(true);
    }

    public void expire() {
        SimpleTcpReplicationManager mgr =(SimpleTcpReplicationManager)getManager();
        mgr.sessionInvalidated(getId());
        setIsDirty(true);
        super.expire();
    }

    public void invalidate() {
        SimpleTcpReplicationManager mgr =(SimpleTcpReplicationManager)getManager();
        mgr.sessionInvalidated(getId());
        setIsDirty(true);
        super.invalidate();
    }


    /**
     * Read a serialized version of the contents of this session object from
     * the specified object input stream, without requiring that the
     * StandardSession itself have been serialized.
     *
     * @param stream The object input stream to read from
     *
     * @exception ClassNotFoundException if an unknown class is specified
     * @exception IOException if an input/output error occurs
     */
    public void readObjectData(ObjectInputStream stream)
        throws ClassNotFoundException, IOException {

        super.readObjectData(stream);

    }


    /**
     * Write a serialized version of the contents of this session object to
     * the specified object output stream, without requiring that the
     * StandardSession itself have been serialized.
     *
     * @param stream The object output stream to write to
     *
     * @exception IOException if an input/output error occurs
     */
    public void writeObjectData(ObjectOutputStream stream)
        throws IOException {

        super.writeObjectData(stream);

    }





    /**
     * returns true if this session is the primary session, if that is the
     * case, the manager can expire it upon timeout.
     * @return
     */
    public boolean isPrimarySession() {
        return isPrimarySession;
    }

    /**
     * Sets whether this is the primary session or not.
     * @param primarySession
     */
    public void setPrimarySession(boolean primarySession) {
        this.isPrimarySession=primarySession;
    }




    /**
     * Implements a log method to log through the manager
     */
    protected void log(String message) {

        if ((mManager != null) && (mManager instanceof SimpleTcpReplicationManager)) {
            ((SimpleTcpReplicationManager) mManager).log(message);
        } else {
            System.out.println("ReplicatedSession: " + message);
        }

    }

    protected void log(String message, Throwable x) {

        if ((mManager != null) && (mManager instanceof SimpleTcpReplicationManager)) {
            ((SimpleTcpReplicationManager) mManager).log(message,x);
        } else {
            System.out.println("ReplicatedSession: " + message);
            x.printStackTrace();
        }

    }

    public String toString() {
        StringBuffer buf = new StringBuffer("ReplicatedSession id=");
        buf.append(getId()).append(" ref=").append(super.toString()).append("\n");
        java.util.Enumeration e = getAttributeNames();
        while ( e.hasMoreElements() ) {
            String name = (String)e.nextElement();
            Object value = getAttribute(name);
            buf.append("\tname=").append(name).append("; value=").append(value).append("\n");
        }
        buf.append("\tLastAccess=").append(getLastAccessedTime()).append("\n");
        return buf.toString();
    }

}
