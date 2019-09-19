/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/MembershipService.java,v 1.1 2003/02/19 20:57:17 fhanik Exp $
 * $Revision: 1.1 $
 * $Date: 2003/02/19 20:57:17 $
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
 * The membership service helps the cluster determine the membership
 * logic in the cluster.
 * @author Filip Hanik
 * @version $Revision: 1.1 $, $Date: 2003/02/19 20:57:17 $
 */


public interface MembershipService {

    /**
     * Sets the properties for the membership service. This must be called before
     * the <code>start()</code> method is called.
     * The properties are implementation specific.
     * @param properties - to be used to configure the membership service.
     */
    public void setProperties(java.util.Properties properties);
    /**
     * Returns the properties for the configuration used.
     * @return
     */
    public java.util.Properties getProperties();
    /**
     * Starts the membership service. If a membership listeners is added
     * the listener will start to receive membership events.
     * @throws java.lang.Exception if the service fails to start.
     */
    public void start() throws java.lang.Exception;
    /**
     * Stops the membership service
     */
    public void stop();
    /**
     * Returns a list of all the members in the cluster.
     * @return
     */
    public Member[] getMembers();
    /**
     * Returns the member object that defines this member
     * @return
     */
    public Member getLocalMember();
    /**
     * Sets the membership listener, only one listener can be added.
     * If you call this method twice, the last listener will be used.
     * @param listener
     */
    public void addMembershipListener(MembershipListener listener);
    /**
     * removes the membership listener.
     */
    public void removeMembershipListener();

}