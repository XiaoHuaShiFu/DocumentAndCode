/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/tcp/Jdk13ReplicationListener.java,v 1.1 2003/12/18 04:20:15 fhanik Exp $
 * $Revision: 1.1 $
 * $Date: 2003/12/18 04:20:15 $
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




import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.apache.catalina.cluster.io.ListenCallback;
import org.apache.catalina.cluster.io.Jdk13ObjectReader;
import org.apache.catalina.cluster.io.XByteBuffer;
/**
 */
public class Jdk13ReplicationListener implements Runnable
{

    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( SimpleTcpCluster.class );
    private ThreadPool pool = null;
    private boolean doListen = false;
    private ListenCallback callback;
    private java.net.InetAddress bind;
    private int port;
    private long timeout = 0;
    private boolean synchronous = false;
    ServerSocket serverSocket = null;

    public Jdk13ReplicationListener(ListenCallback callback,
                               int poolSize,
                               java.net.InetAddress bind,
                               int port,
                               long timeout,
                               boolean synchronous)
    {
        this.synchronous=synchronous;
        this.callback = callback;
        this.bind = bind;
        this.port = port;
        this.timeout = timeout;
    }

    public void run()
    {
        try
        {
            listen();
        }
        catch ( Exception x )
        {
            log.fatal("Unable to start cluster listener.",x);
        }
    }

    public void listen ()
        throws Exception
    {
        doListen = true;
        // Get the associated ServerSocket to bind it with
        serverSocket = new ServerSocket();
        serverSocket.bind (new InetSocketAddress (bind,port));
        while (doListen) {
            Socket socket = serverSocket.accept();
            ClusterListenThread t = new ClusterListenThread(socket,new Jdk13ObjectReader(socket,callback));
            t.setDaemon(true);
            t.start();
        }//while
        serverSocket.close();
    }

    public void stopListening(){
        doListen = false;
        try {
            serverSocket.close();
        } catch ( Exception x ) {
            log.error("Unable to stop the replication listen socket",x);
        }
    }

    protected static class ClusterListenThread extends Thread {
        private Socket socket;
        private Jdk13ObjectReader reader;
        private boolean keepRunning = true;
        private static byte[] ackMsg = new byte[] {6,2,3};
        ClusterListenThread(Socket socket, Jdk13ObjectReader reader) {
            this.socket = socket;
            this.reader = reader;
        }

        public void run() {
            try {
                byte[] buffer = new byte[1024];
                while (keepRunning) {
                    java.io.InputStream in = socket.getInputStream();
                    int cnt = in.read(buffer);
                    int ack = 0;
                    if ( cnt > 0 ) {
                        ack = reader.append(buffer, 0, cnt);
                    }
                    while ( ack > 0 ) {
                        sendAck();
                        ack--;
                    }
                }
            } catch ( Exception x ) {
                keepRunning = false;
                log.error("Unable to read data from client, disconnecting.",x);
                try { socket.close(); } catch ( Exception ignore ) {}
            }
        }

        private void sendAck() throws java.io.IOException {
            //send a reply-acknowledgement
            socket.getOutputStream().write(ackMsg);
        }

    }
}
