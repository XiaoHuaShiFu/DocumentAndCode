/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/util/SmartQueue.java,v 1.1 2003/04/18 02:51:24 fhanik Exp $
 * $Revision: 1.1 $
 * $Date: 2003/04/18 02:51:24 $
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

package org.apache.catalina.cluster.util;

/**
 * A smart queue, used for async replication<BR>
 * the "smart" part of this queue is that if the session is already queued for replication,
 * and it is updated again, the session will simply be replaced, hence we don't 
 * replicate stuff that is obsolete.
 * Put this into util, since it is quite  generic.
 * 
 * @author Filip Hanik
 * @version 1.0
 */
 

import java.util.LinkedList;
import java.util.HashMap;

public class SmartQueue {
    /**
     * This is the actual queue
     */
    private LinkedList queue = new LinkedList();
    /**
     * And this is only for performance, fast lookups
     */
    private HashMap queueMap = new HashMap();
    
    private Object mutex = new Object();
    public static int debug = 0;
    
    public SmartQueue() {
    }
    
    /**
     * Add an object to the queue
     * @param entry - the smart entry
     */
    public void add(SmartEntry entry) {
        /*make sure we are within a synchronized block since we are dealing with two
          unsync collections*/
        synchronized (mutex) {
            /*check to see if this object has already been queued*/
            SmartEntry current = (SmartEntry)queueMap.get(entry.getKey());
            if ( current == null ) {
                /*the object has not been queued, at it to the end of the queue*/
                if ( debug != 0 ) System.out.println("["+Thread.currentThread().getName()+"][SmartQueue] Adding new object="+entry);
                queue.addLast(entry);
                queueMap.put(entry.getKey(),entry);
            }else {
                /*the object has been queued, replace the value*/
                if ( debug != 0 ) System.out.print("["+Thread.currentThread().getName()+"][SmartQueue] Replacing old object="+current);
                current.setValue(entry.getValue());
                if ( debug != 0 ) System.out.println("with new object="+current);
            }
            /*wake up all the threads that are waiting for the lock to be released*/
            mutex.notifyAll();
        }
    }
    
    public int size() {
        synchronized (mutex) {
            return queue.size();            
        }
    }
    
    /**
     * Blocks forever until an element has been added to the queue
     * @return
     */
    public SmartEntry remove() {
        SmartEntry result = null;        
        synchronized (mutex) {
            while ( size() == 0 ) {
                try {
                    if ( debug != 0 ) System.out.println("["+Thread.currentThread().getName()+"][SmartQueue] Queue sleeping until object added size="+size()+".");
                    mutex.wait();
                    if ( debug != 0 ) System.out.println("["+Thread.currentThread().getName()+"][SmartQueue] Queue woke up or interrupted size="+size()+".");
                }
                catch(IllegalMonitorStateException ex) {
                    throw ex;
                }
                catch(InterruptedException ex) {
                }//catch
            }//while
            /*guaranteed that we are not empty by now*/
            result = (SmartEntry)queue.removeFirst();
            queueMap.remove(result.getKey());
            if ( debug != 0 ) System.out.println("["+Thread.currentThread().getName()+"][SmartQueue] Returning="+result);
        }
        return result;
    }
    
    
    
    public static class SmartEntry {
        protected Object key;
        protected Object value;
        public SmartEntry(Object key,
                               Object value) {
            if ( key == null ) throw new IllegalArgumentException("SmartEntry key can not be null.");
            if ( value == null ) throw new IllegalArgumentException("SmartEntry value can not be null.");
            this.key = key;
            this.value = value;
        }
        
        public Object getKey() {
            return key;
        }
        
        public Object getValue() {
            return value;
        }
        
        public void setValue(Object value) {
            if ( value == null ) throw new IllegalArgumentException("SmartEntry value can not be null.");
            this.value = value;
        }
        
        public int hashCode() {
            return key.hashCode();
        }
        
        public boolean equals(Object o) {
            if (!(o instanceof SmartEntry)) return false;
            SmartEntry other = (SmartEntry)o;
            return other.getKey().equals(getKey());
        }
        
        public String toString() {
            return "[SmartyEntry key="+key+" value="+value+"]";
        }
    }
    

}