/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/tcp/PooledSocketSender.java,v 1.4 2004/01/15 04:19:50 fhanik Exp $
 * $Revision: 1.4 $
 * $Date: 2004/01/15 04:19:50 $
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
import java.net.InetAddress ;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class PooledSocketSender implements IDataSender
{

    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( org.apache.catalina.cluster.tcp.SimpleTcpCluster.class );

    private InetAddress address;
    private int port;
    private Socket sc = null;
    private boolean isSocketConnected = true;
    private boolean suspect;
    private long ackTimeout = 15*1000;  //15 seconds socket read timeout (for acknowledgement)
    private long keepAliveTimeout = 60*1000; //keep socket open for no more than one min
    private int keepAliveMaxRequestCount = 100; //max 100 requests before reconnecting
    private long keepAliveConnectTime = 0;
    private int keepAliveCount = 0;
    private int maxPoolSocketLimit = 25;

    private SenderQueue senderQueue = null;

    public PooledSocketSender(InetAddress host, int port)
    {
        this.address = host;
        this.port = port;
        senderQueue = new SenderQueue(this,maxPoolSocketLimit);
    }

    public InetAddress getAddress()
    {
        return address;
    }

    public int getPort()
    {
        return port;
    }

    public void connect() throws java.io.IOException
    {
        //do nothing, happens in the socket sender itself
        senderQueue.open();
        isSocketConnected = true;
    }

    public void disconnect()
    {
        senderQueue.close();
        isSocketConnected = false;
    }

    public boolean isConnected()
    {
        return isSocketConnected;
    }

    public void setAckTimeout(long timeout) {
        this.ackTimeout = timeout;
    }

    public long getAckTimeout() {
        return ackTimeout;
    }

    public void setMaxPoolSocketLimit(int limit) {
        maxPoolSocketLimit = limit;
    }

    public int getMaxPoolSocketLimit() {
        return maxPoolSocketLimit;
    }


    /**
     * Blocking send
     * @param data
     * @throws java.io.IOException
     */
    public void sendMessage(String sessionId, byte[] data) throws java.io.IOException
    {
        //get a socket sender from the pool
        SocketSender sender = senderQueue.getSender(0);
        if ( sender == null ) {
            log.warn("No socket sender available for client="+this.getAddress()+":"+this.getPort()+" did it disappear?");
            return;
        }//end if
        //send the message
        sender.sendMessage(sessionId,data);
        //return the connection to the pool
        senderQueue.returnSender(sender);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("PooledSocketSender[");
        buf.append(getAddress()).append(":").append(getPort()).append("]");
        return buf.toString();
    }

    public boolean getSuspect() {
        return suspect;
    }

    public void setSuspect(boolean suspect) {
        this.suspect = suspect;
    }

    public long getKeepAliveTimeout() {
        return keepAliveTimeout;
    }
    public void setKeepAliveTimeout(long keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }
    public int getKeepAliveMaxRequestCount() {
        return keepAliveMaxRequestCount;
    }
    public void setKeepAliveMaxRequestCount(int keepAliveMaxRequestCount) {
        this.keepAliveMaxRequestCount = keepAliveMaxRequestCount;
    }

    private class SenderQueue {
        private int limit = 25;
        PooledSocketSender parent = null;
        private LinkedList queue = new LinkedList();
        private LinkedList inuse = new LinkedList();
        private Object mutex = new Object();
        private boolean isOpen = true;

        public SenderQueue(PooledSocketSender parent, int limit) {
            this.limit = limit;
            this.parent = parent;
        }

        public SocketSender getSender(long timeout) {
            SocketSender sender = null;
            long start = System.currentTimeMillis();
            long delta = 0;
            do {
                synchronized (mutex) {
                    if ( !isOpen ) throw new IllegalStateException("Socket pool is closed.");
                    if ( queue.size() > 0 ) {
                        sender = (SocketSender) queue.removeFirst();
                    } else if ( inuse.size() < limit ) {
                        sender = getNewSocketSender();
                    } else {
                        try {
                            mutex.wait(timeout);
                        }catch ( Exception x ) {
                            parent.log.warn("PoolSocketSender.senderQueue.getSender failed",x);
                        }//catch
                    }//end if
                    if ( sender != null ) {
                        inuse.add(sender);
                    }
                }//synchronized
                delta = System.currentTimeMillis() - start;
            } while ( (isOpen) && (sender == null) && (timeout==0?true:(delta<timeout)) );
            //to do
            return sender;
        }

        public void returnSender(SocketSender sender) {
            //to do
            synchronized (mutex) {
                queue.add(sender);
                inuse.remove(sender);
                mutex.notify();
            }
        }

        private SocketSender getNewSocketSender() {
            //new SocketSender(
            SocketSender sender = new SocketSender(parent.getAddress(),parent.getPort());
            sender.setKeepAliveMaxRequestCount(parent.getKeepAliveMaxRequestCount());
            sender.setKeepAliveTimeout(parent.getKeepAliveTimeout());
            sender.setAckTimeout(parent.getAckTimeout());
            return sender;

        }

        public void close() {
            synchronized (mutex) {
                for ( int i=0; i<queue.size(); i++ ) {
                    SocketSender sender = (SocketSender)queue.get(i);
                    sender.disconnect();
                }//for
                for ( int i=0; i<inuse.size(); i++ ) {
                    SocketSender sender = (SocketSender) inuse.get(i);
                    sender.disconnect();
                }//for
                queue.clear();
                inuse.clear();
                isOpen = false;
                mutex.notifyAll();
            }
        }
        
        public void open() {
            synchronized (mutex) {
                isOpen = true;
                mutex.notifyAll();
            }
        }
    }
}
