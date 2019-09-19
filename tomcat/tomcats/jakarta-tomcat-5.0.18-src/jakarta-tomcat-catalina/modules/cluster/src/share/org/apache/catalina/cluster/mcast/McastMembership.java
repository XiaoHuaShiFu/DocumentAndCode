/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/mcast/McastMembership.java,v 1.2 2003/03/26 17:24:50 fhanik Exp $
 * $Revision: 1.2 $
 * $Date: 2003/03/26 17:24:50 $
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

package org.apache.catalina.cluster.mcast;


import java.util.HashMap;
import java.util.Iterator;
/**
 * A <b>membership</b> implementation using simple multicast.
 * This is the representation of a multicast membership.
 * This class is responsible for maintaining a list of active cluster nodes in the cluster.
 * If a node fails to send out a heartbeat, the node will be dismissed.
 *
 * @author Filip Hanik
 * @version $Revision: 1.2 $, $Date: 2003/03/26 17:24:50 $
 */


public class McastMembership
{
    /**
     * The name of this membership, has to be the same as the name for the local
     * member
     */
    protected String name;
    /**
     * A map of all the members in the cluster.
     */
    protected HashMap map = new java.util.HashMap();

    /**
     * Constructs a new membership
     * @param myName - has to be the name of the local member. Used to filter the local member from the cluster membership
     */
    public McastMembership(String myName) {
        name = myName;
    }

    /**
     * Reset the membership and start over fresh.
     * Ie, delete all the members and wait for them to ping again and join this membership
     */
    public synchronized void reset() {
        map.clear();
    }

    /**
     * Notify the membership that this member has announced itself.
     *
     * @param m - the member that just pinged us
     * @return - true if this member is new to the cluster, false otherwise.
     * @return - false if this member is the local member.
     */
    public synchronized boolean memberAlive(McastMember m) {
        boolean result = false;
        //ignore ourselves
        if ( m.getName().equals(name) ) return result;

        //return true if the membership has changed
        MbrEntry entry = (MbrEntry)map.get(m.getName());
        if ( entry == null ) {
            entry = new MbrEntry(m);
            map.put(m.getName(),entry);
            result = true;
        }//end if
        entry.accessed();
        return result;
    }

    /**
     * Runs a refresh cycle and returns a list of members that has expired.
     * This also removes the members from the membership, in such a way that
     * getMembers() = getMembers() - expire()
     * @param maxtime - the max time a member can remain unannounced before it is considered dead.
     * @return the list of expired members
     */
    public synchronized McastMember[] expire(long maxtime) {
        MbrEntry[] members = getMemberEntries();
        java.util.ArrayList list = new java.util.ArrayList();
        for (int i=0; i<members.length; i++) {
            MbrEntry entry = members[i];
            if ( entry.hasExpired(maxtime) ) {
                list.add(entry.getMember());
            }//end if
        }//while
        McastMember[] result = new McastMember[list.size()];
        list.toArray(result);
        for ( int j=0; j<result.length; j++) map.remove(result[j].getName());
        return result;

    }//expire

    /**
     * Returning a list of all the members in the membership
     * @return
     */
    public synchronized McastMember[] getMembers() {
        McastMember[] result = new McastMember[map.size()];
        java.util.Iterator i = map.entrySet().iterator();
        int pos = 0;
        while ( i.hasNext() )
            result[pos++] = ((MbrEntry)((java.util.Map.Entry)i.next()).getValue()).getMember();
        return result;
    }

    protected synchronized MbrEntry[] getMemberEntries()
    {
        MbrEntry[] result = new MbrEntry[map.size()];
        java.util.Iterator i = map.entrySet().iterator();
        int pos = 0;
        while ( i.hasNext() )
            result[pos++] = ((MbrEntry)((java.util.Map.Entry)i.next()).getValue());
        return result;
    }


    /**
     * Inner class that represents a member entry
     */
    protected static class MbrEntry
    {

        protected McastMember mbr;
        protected long lastHeardFrom;
        public MbrEntry(McastMember mbr) {
            this.mbr = mbr;
        }
        /**
         * Indicate that this member has been accessed.
         */
        public void accessed(){
            lastHeardFrom = System.currentTimeMillis();
        }
        /**
         * Return the actual McastMember object
         * @return
         */
        public McastMember getMember() {
            return mbr;
        }

        /**
         * Check if this dude has expired
         * @param maxtime
         * @return
         */
        public boolean hasExpired(long maxtime) {
            long delta = System.currentTimeMillis() - lastHeardFrom;
            return delta > maxtime;
        }
    }//MbrEntry
}