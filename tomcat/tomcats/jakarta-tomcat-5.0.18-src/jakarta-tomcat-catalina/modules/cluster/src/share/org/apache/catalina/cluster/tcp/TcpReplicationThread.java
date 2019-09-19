/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/tcp/TcpReplicationThread.java,v 1.7 2004/01/09 23:24:09 fhanik Exp $
 * $Revision: 1.7 $
 * $Date: 2004/01/09 23:24:09 $
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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.List;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import org.apache.catalina.cluster.io.ObjectReader;
import java.util.LinkedList;
/**
     * A worker thread class which can drain channels and echo-back
     * the input.  Each instance is constructed with a reference to
     * the owning thread pool object. When started, the thread loops
     * forever waiting to be awakened to service the channel associated
     * with a SelectionKey object.
     * The worker is tasked by calling its serviceChannel() method
     * with a SelectionKey object.  The serviceChannel() method stores
     * the key reference in the thread object then calls notify()
     * to wake it up.  When the channel has been drained, the worker
     * thread returns itself to its parent pool.
     */
public class TcpReplicationThread extends WorkerThread
{
    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( SimpleTcpCluster.class );
    private ByteBuffer buffer = ByteBuffer.allocate (1024);
    private SelectionKey key;
    private boolean synchronous=false;

    TcpReplicationThread ()
    {
    }

    // loop forever waiting for work to do
    public synchronized void run()
    {
        while (doRun) {
            try {
                // sleep and release object lock
                this.wait();
            } catch (InterruptedException e) {
                log.info("TCP worker thread interrupted in cluster",e);
                // clear interrupt status
                this.interrupted();
            }
            if (key == null) {
                continue;	// just in case
            }
            try {
                drainChannel (key);
            } catch (Exception e) {
                log.error ("TCP Worker thread in cluster caught '"
                    + e + "' closing channel", e);

                // close channel and nudge selector
                try {
                    key.channel().close();
                } catch (IOException ex) {
                    log.error("Unable to close channel.",ex);
                }
                key.selector().wakeup();
            }
            key = null;
            // done, ready for more, return to pool
            this.pool.returnWorker (this);
        }
    }

    /**
     * Called to initiate a unit of work by this worker thread
     * on the provided SelectionKey object.  This method is
     * synchronized, as is the run() method, so only one key
     * can be serviced at a given time.
     * Before waking the worker thread, and before returning
     * to the main selection loop, this key's interest set is
     * updated to remove OP_READ.  This will cause the selector
     * to ignore read-readiness for this channel while the
     * worker thread is servicing it.
     */
    synchronized void serviceChannel (SelectionKey key, boolean synchronous)
    {
        this.key = key;
        this.synchronous=synchronous;
        key.interestOps (key.interestOps() & (~SelectionKey.OP_READ));
        key.interestOps (key.interestOps() & (~SelectionKey.OP_WRITE));
        this.notify();		// awaken the thread
    }

    /**
     * The actual code which drains the channel associated with
     * the given key.  This method assumes the key has been
     * modified prior to invocation to turn off selection
     * interest in OP_READ.  When this method completes it
     * re-enables OP_READ and calls wakeup() on the selector
     * so the selector will resume watching this channel.
     */
    private void drainChannel (SelectionKey key)
        throws Exception
    {
        boolean packetReceived=false;
        SocketChannel channel = (SocketChannel) key.channel();
        int count;
        buffer.clear();			// make buffer empty
        ObjectReader reader = (ObjectReader)key.attachment();
        // loop while data available, channel is non-blocking
        while ((count = channel.read (buffer)) > 0) {
            buffer.flip();		// make buffer readable
            int pkgcnt = reader.append(buffer.array(),0,count);
            buffer.clear();		// make buffer empty
        }
        //check to see if any data is available
        int pkgcnt = reader.execute();
        while ( pkgcnt > 0 ) {
            if (synchronous) {
                sendAck(key,channel);
            } //end if
            pkgcnt--;
        }
        if (count < 0) {
            // close channel on EOF, invalidates the key
            channel.close();
            return;
        }
        // resume interest in OP_READ, OP_WRITE
        key.interestOps (key.interestOps() | SelectionKey.OP_READ);
        // cycle the selector so this key is active again
        key.selector().wakeup();
    }

    private void sendAck(SelectionKey key, SocketChannel channel) {
        //send a reply-acknowledgement
        try {
            channel.write(ByteBuffer.wrap(new byte[] {6, 2, 3}));
        } catch ( java.io.IOException x ) {
            log.warn("Unable to send ACK back through channel, channel disconnected?: "+x.getMessage());
        }
    }
}
