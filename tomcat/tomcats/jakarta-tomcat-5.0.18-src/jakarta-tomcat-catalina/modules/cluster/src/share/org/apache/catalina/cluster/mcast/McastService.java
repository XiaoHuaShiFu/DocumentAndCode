/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/mcast/McastService.java,v 1.5 2004/01/09 02:50:54 fhanik Exp $
 * $Revision: 1.5 $
 * $Date: 2004/01/09 02:50:54 $
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

package org.apache.catalina.cluster.mcast;

import org.apache.catalina.cluster.MembershipService;
import java.util.Properties;
import org.apache.catalina.cluster.Member;
import org.apache.catalina.cluster.MembershipListener;
import java.util.Properties;

/**
 * A <b>membership</b> implementation using simple multicast.
 * This is the representation of a multicast membership service.
 * This class is responsible for maintaining a list of active cluster nodes in the cluster.
 * If a node fails to send out a heartbeat, the node will be dismissed.
 *
 * @author Filip Hanik
 * @version $Revision: 1.5 $, $Date: 2004/01/09 02:50:54 $
 */


public class McastService implements MembershipService,MembershipListener {

    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( McastService.class );
    /**
     * The implementation specific properties
     */
    protected Properties properties;
    /**
     * A handle to the actual low level implementation
     */
    protected McastServiceImpl impl;
    /**
     * A membership listener delegate (should be the cluster :)
     */
    protected MembershipListener listener;
    /**
     * The local member
     */
    protected McastMember localMember;

    /**
     * Create a membership service.
     */
    public McastService() {
    }


    /**
     *
     * @param properties<BR>All are required<BR>
     * 1. mcastPort - the port to listen to<BR>
     * 2. mcastAddress - the mcast group address<BR>
     * 3. bindAddress - the bind address if any - only one that can be null<BR>
     * 4. memberDropTime - the time a member is gone before it is considered gone.<BR>
     * 5. msgFrequency - the frequency of sending messages<BR>
     * 6. tcpListenPort - the port this member listens to<BR>
     * 7. tcpListenHost - the bind address of this member<BR>
     * @exception throws a java.lang.IllegalArgumentException if a property is missing.
     */
    public void setProperties(Properties properties) {
        hasProperty(properties,"mcastPort");
        hasProperty(properties,"mcastAddress");
        hasProperty(properties,"memberDropTime");
        hasProperty(properties,"msgFrequency");
        hasProperty(properties,"tcpListenPort");
        hasProperty(properties,"tcpListenHost");
        this.properties = properties;
    }

    /**
     * Return the properties, see setProperties
     * @return
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Return the local member
     */
    public Member getLocalMember() {
        localMember.setMemberAliveTime(System.currentTimeMillis()-impl.getServiceStartTime());
        return localMember;
    }


    /**
     * Check if a required property is available.
     * @param properties
     * @param name
     */
    protected void hasProperty(Properties properties, String name){
        if ( properties.getProperty(name)==null) throw new IllegalArgumentException("Required property \""+name+"\" is missing.");
    }

    /**
     * Start broadcasting and listening to membership pings
     * @throws java.lang.Exception if a IO error occurs
     */
    public void start() throws java.lang.Exception {
        if ( impl != null ) return;
        String host = getProperties().getProperty("tcpListenHost");
        int port = Integer.parseInt(getProperties().getProperty("tcpListenPort"));
        String name = "tcp://"+host+":"+port;
        localMember = new McastMember(name,host,port,100);
        java.net.InetAddress bind = null;
        if ( properties.getProperty("mcastBindAddress")!= null ) {
            bind = java.net.InetAddress.getByName(properties.getProperty("mcastBindAddress"));
        }
        impl = new McastServiceImpl((McastMember)localMember,Long.parseLong(properties.getProperty("msgFrequency")),
                                    Long.parseLong(properties.getProperty("memberDropTime")),
                                    Integer.parseInt(properties.getProperty("mcastPort")),
                                    bind,
                                    java.net.InetAddress.getByName(properties.getProperty("mcastAddress")),
                                    this);

        impl.start();
    }

    /**
     * Stop broadcasting and listening to membership pings
     */
    public void stop() {
        try  {
            if ( impl != null) impl.stop();
        } catch ( Exception x)  {
            log.error("Unable to stop the mcast service.",x);
        }
        impl = null;
    }

    /**
     * Return all the members
     * @return
     */
    public Member[] getMembers() {
        if ( impl == null || impl.membership == null ) return null;
        return impl.membership.getMembers();
    }
    /**
     * Add a membership listener, this version only supports one listener per service,
     * so calling this method twice will result in only the second listener being active.
     * @param listener
     */
    public void addMembershipListener(MembershipListener listener) {
        this.listener = listener;
    }
    /**
     * Remove the membership listener
     */
    public void removeMembershipListener(){
        listener = null;
    }

    public void memberAdded(Member member) {
        if ( listener!=null ) listener.memberAdded(member);
    }

    /**
     * Callback from the impl when a new member has been received
     * @param member
     */
    public void memberDisappeared(Member member)
    {
        if ( listener!=null ) listener.memberDisappeared(member);
    }

    /**
     * Simple test program
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        System.out.println("Usage McastService hostname tcpport");
        McastService service = new McastService();
        java.util.Properties p = new java.util.Properties();
        p.setProperty("mcastPort","5555");
        p.setProperty("mcastAddress","224.10.10.10");
        p.setProperty("bindAddress","localhost");
        p.setProperty("memberDropTime","3000");
        p.setProperty("msgFrequency","500");
        p.setProperty("tcpListenPort",args[1]);
        p.setProperty("tcpListenHost",args[0]);
        service.setProperties(p);
        service.start();
        Thread.currentThread().sleep(60*1000*60);
    }
}
