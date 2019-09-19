/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/ClusterManager.java,v 1.2 2004/01/12 07:50:06 fhanik Exp $
 * $Revision: 1.2 $
 * $Date: 2004/01/12 07:50:06 $
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

package org.apache.catalina.cluster;

/**
 * The common interface used by all cluster manager.
 * This is so that we can have a more pluggable way
 * of swapping session managers for different algorithms.
 *
 * @author Filip Hanik

 */

import org.apache.catalina.Manager;
import org.apache.catalina.cluster.tcp.SimpleTcpCluster;


public interface ClusterManager extends Manager {

   /**
    * A message was received from another node, this
    * is the callback method to implement if you are interested in
    * receiving replication messages.
    * @param msg - the message received.
    */
   public void messageDataReceived(SessionMessage msg);

   /**
    * When the request has been completed, the replication valve
    * will notify the manager, and the manager will decide whether
    * any replication is needed or not.
    * If there is a need for replication, the manager will
    * create a session message and that will be replicated.
    * The cluster determines where it gets sent.
    * @param sessionId - the sessionId that just completed.
    * @return a SessionMessage to be sent,
    */
   public SessionMessage requestCompleted(String sessionId);

   /**
    * When the manager expires session not tied to a request.
    * The cluster will periodically ask for a list of sessions
    * that should expire and that should be sent across the wire.
    * @return
    */
   public String[] getInvalidatedSessions();
   
   /**
    * Return the name of the manager, typically the context name such as /replicator
    * @return String
    */
   public String getName();
   
   public void setName(String name);
   
   public void setExpireSessionsOnShutdown(boolean expireSessionsOnShutdown);
   
   public void setUseDirtyFlag(boolean useDirtyFlag);
   
   public void setCluster(SimpleTcpCluster cluster);

}
