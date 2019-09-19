/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/tcp/ReplicationListener.java,v 1.9 2004/01/09 02:53:08 fhanik Exp $
 * $Revision: 1.9 $
 * $Date: 2004/01/09 02:53:08 $
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


import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SelectableChannel;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.util.Iterator;
import org.apache.catalina.cluster.io.ListenCallback;
import org.apache.catalina.cluster.io.ObjectReader;
import org.apache.catalina.cluster.io.XByteBuffer;
/**
 */
public class ReplicationListener extends Thread
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
    public ReplicationListener(ListenCallback callback,
                               int poolSize,
                               java.net.InetAddress bind,
                               int port,
                               long timeout,
                               boolean synchronous)
    {
        try {
            this.synchronous=synchronous;
            pool = new ThreadPool(poolSize, TcpReplicationThread.class);
        }catch ( Exception x ) {
            log.fatal("Unable to start thread pool for TCP listeners, session replication will fail! msg="+x.getMessage());
        }
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
            log.error("Unable to start cluster listener.",x);
        }
    }

    public void listen ()
        throws Exception
    {
        doListen = true;
        // allocate an unbound server socket channel
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        // Get the associated ServerSocket to bind it with
        ServerSocket serverSocket = serverChannel.socket();
        // create a new Selector for use below
        Selector selector = Selector.open();
        // set the port the server channel will listen to
        serverSocket.bind (new InetSocketAddress (bind,port));
        // set non-blocking mode for the listening socket
        serverChannel.configureBlocking (false);
        // register the ServerSocketChannel with the Selector
        serverChannel.register (selector, SelectionKey.OP_ACCEPT);
        while (doListen) {
            // this may block for a long time, upon return the
            // selected set contains keys of the ready channels
            try {

                //System.out.println("Selecting with timeout="+timeout);
                int n = selector.select(timeout);
                //System.out.println("select returned="+n);
                if (n == 0) {
                    continue; // nothing to do
                }
                // get an iterator over the set of selected keys
                Iterator it = selector.selectedKeys().iterator();
                // look at each key in the selected set
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();
                    // Is a new connection coming in?
                    if (key.isAcceptable()) {
                        ServerSocketChannel server =
                            (ServerSocketChannel) key.channel();
                        SocketChannel channel = server.accept();
                        registerChannel(selector,
                                        channel,
                                        SelectionKey.OP_READ,
                                        new ObjectReader(channel, selector,
                            callback));
                    }
                    // is there data to read on this channel?
                    //System.out.println("key readable="+key.isReadable());
                    if (key.isReadable()) {
                        readDataFromSocket(key);
                    } else {
                        //System.out.println("This shouldn't get called");
                        key.interestOps(key.interestOps() & (~key.OP_WRITE));
                    }

                    // remove key from selected set, it's been handled
                    it.remove();
                }
                //System.out.println("Done with loop");
            }
            catch (java.nio.channels.CancelledKeyException nx) {
                log.warn(
                    "Replication client disconnected, error when polling key. Ignoring client.");
            }
            catch (Exception x) {
                log.error("Unable to process request in ReplicationListener", x);
            }

        } //while
        serverChannel.close();
        selector.close();
    }

    public void stopListening(){
        doListen = false;
    }


    // ----------------------------------------------------------

    /**
     * Register the given channel with the given selector for
     * the given operations of interest
     */
    protected void registerChannel (Selector selector,
                                    SelectableChannel channel,
                                    int ops,
                                    Object attach)
    throws Exception {
        if (channel == null) return; // could happen
        // set the new channel non-blocking
        channel.configureBlocking (false);
        // register it with the selector
        channel.register (selector, ops, attach);
    }

    // ----------------------------------------------------------

    /**
     * Sample data handler method for a channel with data ready to read.
     * @param key A SelectionKey object associated with a channel
     *  determined by the selector to be ready for reading.  If the
     *  channel returns an EOF condition, it is closed here, which
     *  automatically invalidates the associated key.  The selector
     *  will then de-register the channel on the next select call.
     */
    protected void readDataFromSocket (SelectionKey key)
        throws Exception
    {
        TcpReplicationThread worker = (TcpReplicationThread)pool.getWorker();
        if (worker == null) {
            // No threads available, do nothing, the selection
            // loop will keep calling this method until a
            // thread becomes available.  This design could
            // be improved.
            return;
        } else {
            // invoking this wakes up the worker thread then returns
            worker.serviceChannel(key, synchronous);
            return;
        }
    }

}
