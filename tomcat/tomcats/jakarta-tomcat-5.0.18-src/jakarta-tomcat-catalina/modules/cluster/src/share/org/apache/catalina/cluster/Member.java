/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/Member.java,v 1.2 2003/11/16 22:22:45 fhanik Exp $
 * $Revision: 1.2 $
 * $Date: 2003/11/16 22:22:45 $
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
 * The Member interface, defines a member in the Cluster.
 * A member is a Tomcat process that participates in session replication.<BR>
 * Each member can carry a set of properties, defined by the actual implementation.<BR>
 * For TCP replication has been targeted for the first release, the hostname and listen port
 * of the member is defined as hardcoded stuff.<BR>
 * The Member interface together with MembershipListener, MembershipService are interfaces used to
 * switch out the service used to establish membership in between the cluster nodes.
 *
 * @author Filip Hanik
 * @version $Revision: 1.2 $, $Date: 2003/11/16 22:22:45 $
 */


public interface Member {
    /**
     * Return implementation specific properties about this cluster node.
     * @return
     */
    public java.util.HashMap getMemberProperties();
    /**
     * Returns the name of this node, should be unique within the cluster.
     * @return
     */
    public String getName();
    /**
     * Returns the TCP listen host for the TCP implementation
     * @return
     */
    public String getHost();
    /**
     * Returns the TCP listen portfor the TCP implementation
     * @return
     */
    public int getPort();

    /**
     * Contains information on how long this member has been online.
     * The result is the number of milli seconds this member has been
     * broadcasting its membership to the cluster.
     * @return nr of milliseconds since this member started.
     */
    public long getMemberAliveTime();
}