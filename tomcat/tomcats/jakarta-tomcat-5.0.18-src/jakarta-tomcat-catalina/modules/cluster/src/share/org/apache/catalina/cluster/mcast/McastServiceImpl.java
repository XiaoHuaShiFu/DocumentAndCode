/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/mcast/McastServiceImpl.java,v 1.6 2004/01/13 00:07:18 fhanik Exp $
 * $Revision: 1.6 $
 * $Date: 2004/01/13 00:07:18 $
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

/**
 * A <b>membership</b> implementation using simple multicast.
 * This is the representation of a multicast membership service.
 * This class is responsible for maintaining a list of active cluster nodes in the cluster.
 * If a node fails to send out a heartbeat, the node will be dismissed.
 * This is the low level implementation that handles the multicasting sockets.
 * Need to fix this, could use java.nio and only need one thread to send and receive, or
 * just use a timeout on the receive
 * @author Filip Hanik
 * @version $Revision: 1.6 $, $Date: 2004/01/13 00:07:18 $
 */

import java.net.MulticastSocket;
import java.io.IOException;
import java.net.InetAddress ;
import java.net.DatagramPacket;
import org.apache.catalina.cluster.MembershipListener;
public class McastServiceImpl
{
    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( McastService.class );
    /**
     * Internal flag used for the listen thread that listens to the multicasting socket.
     */
    protected boolean doRun = false;
    /**
     * Socket that we intend to listen to
     */
    protected MulticastSocket socket;
    /**
     * The local member that we intend to broad cast over and over again
     */
    protected McastMember member;
    /**
     * The multicast address
     */
    protected InetAddress address;
    /**
     * The multicast port
     */
    protected int port;
    /**
     * The time it takes for a member to expire.
     */
    protected long timeToExpiration;
    /**
     * How often to we send out a broadcast saying we are alive, must be smaller than timeToExpiration
     */
    protected long sendFrequency;
    /**
     * Reuse the sendPacket, no need to create a new one everytime
     */
    protected DatagramPacket sendPacket;
    /**
     * Reuse the receivePacket, no need to create a new one everytime
     */
    protected DatagramPacket receivePacket;
    /**
     * The membership, used so that we calculate memberships when they arrive or don't arrive
     */
    protected McastMembership membership;
    /**
     * The actual listener, for callback when shits goes down
     */
    protected MembershipListener service;
    /**
     * Thread to listen for pings
     */
    protected ReceiverThread receiver;
    /**
     * Thread to send pings
     */
    protected SenderThread sender;

    /**
     * When was the service started
     */
    protected long serviceStartTime = System.currentTimeMillis();

    /**
     * Create a new mcast service impl
     * @param member - the local member
     * @param sendFrequency - the time (ms) in between pings sent out
     * @param expireTime - the time (ms) for a member to expire
     * @param port - the mcast port
     * @param bind - the bind address (not sure this is used yet)
     * @param mcastAddress - the mcast address
     * @param service - the callback service
     * @throws IOException
     */
    public McastServiceImpl(
        McastMember member,
        long sendFrequency,
        long expireTime,
        int port,
        InetAddress bind,
        InetAddress mcastAddress,
        MembershipListener service)
    throws IOException {
        if ( bind != null) socket = new MulticastSocket(new java.net.InetSocketAddress(bind,port));
        else socket = new MulticastSocket(port);
        this.member = member;
        address = mcastAddress;
        this.port = port;
        sendPacket = new DatagramPacket(new byte[1000],1000);
        sendPacket.setAddress(address);
        sendPacket.setPort(port);
        receivePacket = new DatagramPacket(new byte[1000],1000);
        receivePacket.setAddress(address);
        receivePacket.setPort(port);
        membership = new McastMembership(member.getName());
        timeToExpiration = expireTime;
        this.service = service;
        this.sendFrequency = sendFrequency;
    }

    /**
     * Start the service
     * @throws IOException if the service fails to start
     * @throws IllegalStateException if the service is already started
     */
    public synchronized void start() throws IOException {
        if ( doRun ) throw new IllegalStateException("Service already running.");
        serviceStartTime = System.currentTimeMillis();
        socket.joinGroup(address);
        doRun = true;
        sender = new SenderThread(sendFrequency);
        sender.setDaemon(true);
        receiver = new ReceiverThread();
        receiver.setDaemon(true);
        receiver.start();
        sender.start();

    }

    /**
     * Stops the service
     * @throws IOException if the service fails to disconnect from the sockets
     */
    public synchronized void stop() throws IOException {
        socket.leaveGroup(address);
        doRun = false;
        sender = null;
        receiver = null;
        serviceStartTime = Long.MAX_VALUE;
    }

    /**
     * Receive a datagram packet, locking wait
     * @throws IOException
     */
    public void receive() throws IOException {
        socket.receive(receivePacket);
        byte[] data = new byte[receivePacket.getLength()];
        System.arraycopy(receivePacket.getData(),receivePacket.getOffset(),data,0,data.length);
        McastMember m = McastMember.getMember(data);
        if ( membership.memberAlive(m) ) {
            service.memberAdded(m);
        }
        McastMember[] expired = membership.expire(timeToExpiration);
        for ( int i=0; i<expired.length; i++)
            service.memberDisappeared(expired[i]);
    }

    /**
     * Send a ping
     * @throws Exception
     */
    public void send() throws Exception{
        member.inc();
        byte[] data = member.getData(this.serviceStartTime);
        DatagramPacket p = new DatagramPacket(data,data.length);
        p.setAddress(address);
        p.setPort(port);
        socket.send(p);
    }

    public long getServiceStartTime() {
       return this.serviceStartTime;
    }


    public class ReceiverThread extends Thread {
        public ReceiverThread() {
            super();
            setName("Cluster-MembershipReceiver");
        }
        public void run() {
            while ( doRun ) {
                try {
                    receive();
                } catch ( Exception x ) {
                    log.warn("Error receiving mcast package.",x);
                }
            }
        }
    }//class ReceiverThread

    public class SenderThread extends Thread {
        long time;
        public SenderThread(long time) {
            this.time = time;
            setName("Cluster-MembershipSender");

        }
        public void run() {
            while ( doRun ) {
                try {
                    send();
                    this.sleep(time);
                } catch ( Exception x ) {
                    log.warn("Unable to send mcast message.",x);
                }
            }
        }
    }//class SenderThread
}
