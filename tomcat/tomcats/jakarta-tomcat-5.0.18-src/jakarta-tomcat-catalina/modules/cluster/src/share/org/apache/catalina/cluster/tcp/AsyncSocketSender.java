/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/tcp/AsyncSocketSender.java,v 1.3 2004/01/13 00:07:18 fhanik Exp $
 * $Revision: 1.3 $
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

package org.apache.catalina.cluster.tcp;

import java.net.InetAddress ;
import java.net.Socket;
import java.io.IOException;
import org.apache.catalina.cluster.util.SmartQueue;
public class AsyncSocketSender implements IDataSender {
    private static int threadCounter=1;
    private InetAddress address;
    private int port;
    private Socket sc = null;
    private boolean isSocketConnected = false;
    private SmartQueue queue = new SmartQueue();
    private boolean suspect;
    
    public AsyncSocketSender(InetAddress host, int port)  {
        this.address = host;
        this.port = port;
        QueueThread t = new QueueThread(this);
        t.setDaemon(true);
        t.start();
        SimpleTcpCluster.log.info("Started async sender thread for TCP replication.");
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void connect() throws java.io.IOException  {
        sc = new Socket(getAddress(),getPort());
        isSocketConnected = true;
    }

    public void disconnect()  {
        try
        {
            sc.close();
        }catch ( Exception x)
        {}
        isSocketConnected = false;
    }

    public boolean isConnected() {
        return isSocketConnected;
    }

    /**
     * Blocking send
     * @param data
     * @throws java.io.IOException
     */
    private synchronized void sendMessage(byte[] data) throws java.io.IOException  {
        if ( !isConnected() ) connect();
        try
        {
            sc.getOutputStream().write(data);
        }
        catch ( java.io.IOException x )
        {
            disconnect();
            connect();
            sc.getOutputStream().write(data);
        }
    }

    public synchronized void sendMessage(String sessionId, byte[] data) throws java.io.IOException {
        SmartQueue.SmartEntry entry = new SmartQueue.SmartEntry(sessionId,data);
        queue.add(entry);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("SocketSender[");
        buf.append(getAddress()).append(":").append(getPort()).append("]");
        return buf.toString();
    }
    public boolean isSuspect() {
        return suspect;
    }
    
    public boolean getSuspect() {
        return suspect;
    }
    
    public void setSuspect(boolean suspect) {
        this.suspect = suspect;
    }
    
    private class QueueThread extends Thread {
        AsyncSocketSender sender;

        public QueueThread(AsyncSocketSender sender) {
            this.sender = sender;
            setName("Cluster-AsyncSocketSender-"+(threadCounter++));
        }
        
        public void run() {
            while (true) {
                SmartQueue.SmartEntry entry = sender.queue.remove();
                if ( entry != null ) {
                    try {
                        byte[] data = (byte[]) entry.getValue();
                        sender.sendMessage(data);
                    }
                    catch (Exception x) {
                        SimpleTcpCluster.log.warn(
                            "Unable to asynchronously send session w/ id=" +
                            entry.getKey()+" message will be ignored.");
                    }
                }
            }
        }
    }
}
