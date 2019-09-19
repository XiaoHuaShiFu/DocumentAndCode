/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/tcp/ThreadPool.java,v 1.5 2004/01/13 00:07:18 fhanik Exp $
 * $Revision: 1.5 $
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
import java.util.List;
import java.util.LinkedList;
import java.io.IOException;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ThreadPool
{
    /**
     * A very simple thread pool class.  The pool size is set at
     * construction time and remains fixed.  Threads are cycled
     * through a FIFO idle queue.
     */

    List idle = new LinkedList();
    Object mutex = new Object();

    ThreadPool (int poolSize, Class threadClass) throws Exception {
        // fill up the pool with worker threads
        for (int i = 0; i < poolSize; i++) {
            WorkerThread thread = (WorkerThread)threadClass.newInstance();
            thread.setPool(this);

            // set thread name for debugging, start it
            thread.setName (threadClass.getName()+"[" + (i + 1)+"]");
            thread.setDaemon(true);
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();

            idle.add (thread);
        }
    }

    /**
     * Find an idle worker thread, if any.  Could return null.
     */
    WorkerThread getWorker()
    {
        WorkerThread worker = null;

        
        synchronized (mutex) {
            while ( worker == null ) {
                if (idle.size() > 0) {
                    try {
                        worker = (WorkerThread) idle.remove(0);
                    } catch (java.util.NoSuchElementException x) {
                        //this means that there are no available workers
                        worker = null;
                    }
                } else {
                    try { mutex.wait(); } catch ( java.lang.InterruptedException x ) {}
                }
            }
        }

        return (worker);
    }

    /**
     * Called by the worker thread to return itself to the
     * idle pool.
     */
    void returnWorker (WorkerThread worker)
    {
        synchronized (mutex) {
            idle.add (worker);
            mutex.notify();
        }
    }
}
