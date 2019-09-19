/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/tcp/ReplicationTransmitter.java,v 1.12 2004/01/13 04:22:28 fhanik Exp $
 * $Revision: 1.12 $
 * $Date: 2004/01/13 04:22:28 $
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

import org.apache.catalina.cluster.io.XByteBuffer;


public class ReplicationTransmitter
{
    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( SimpleTcpCluster.class );

    private java.util.HashMap map = new java.util.HashMap();
    public ReplicationTransmitter(IDataSender[] senders)
    {
        for ( int i=0; i<senders.length; i++)
            map.put(senders[i].getAddress().getHostAddress()+":"+senders[i].getPort(),senders[i]);
    }

    private static long nrOfRequests = 0;
    private static long totalBytes = 0;
    private static synchronized void addStats(int length) {
        nrOfRequests++;
        totalBytes+=length;
        if ( (nrOfRequests % 100) == 0 ) {
            log.info("Nr of bytes sent="+totalBytes+" over "+nrOfRequests+" =="+(totalBytes/nrOfRequests)+" bytes/request");
        }

    }

    public synchronized void add(IDataSender sender)
    {
        String key = sender.getAddress().getHostAddress()+":"+sender.getPort();
        if ( !map.containsKey(key) )
            map.put(sender.getAddress().getHostAddress()+":"+sender.getPort(),sender);
    }//add

    public synchronized void remove(java.net.InetAddress addr,int port)
    {
        String key = addr.getHostAddress()+":"+port;
        IDataSender sender = (IDataSender)map.get(key);
        if ( sender == null ) return;
        sender.disconnect();
        map.remove(key);
    }

    public void start() throws java.io.IOException
    {
        //don't have to do shit, we connect on demand
    }

    public synchronized void stop()
    {
        java.util.Iterator i = map.entrySet().iterator();
        while ( i.hasNext() )
        {
            IDataSender sender = (IDataSender)((java.util.Map.Entry)i.next()).getValue();
            if ( sender.isConnected() )
            {
                try { sender.disconnect(); } catch ( Exception x ){}
            }//end if
        }//while
    }//stop

    public IDataSender[] getSenders()
    {
        java.util.Iterator i = map.entrySet().iterator();
        java.util.Vector v = new java.util.Vector();
        while ( i.hasNext() )
        {
            IDataSender sender = (IDataSender)((java.util.Map.Entry)i.next()).getValue();
            if ( sender!=null) v.addElement(sender);
        }
        IDataSender[] result = new IDataSender[v.size()];
        v.copyInto(result);
        return result;
    }

    protected void sendMessageData(String sessionId, byte[] data, IDataSender sender) throws java.io.IOException  {
        if ( sender == null ) throw new java.io.IOException("Sender not available. Make sure sender information is available to the ReplicationTransmitter.");
        try
        {
            if (!sender.isConnected())
                sender.connect();
            sender.sendMessage(sessionId,data);
            sender.setSuspect(false);
            addStats(data.length);
        }catch ( Exception x)
        {
            if ( !sender.getSuspect() ) {
                log.warn("Unable to send replicated message, is server down?",
                         x);
            }
            sender.setSuspect(true);

        }

    }
    public void sendMessage(String sessionId, byte[] indata, java.net.InetAddress addr, int port) throws java.io.IOException
    {
        byte[] data = XByteBuffer.createDataPackage(indata);
        String key = addr.getHostAddress()+":"+port;
        IDataSender sender = (IDataSender)map.get(key);
        sendMessageData(sessionId,data,sender);
    }

    public void sendMessage(String sessionId, byte[] indata) throws java.io.IOException
    {
         IDataSender[] senders = getSenders();
        byte[] data = XByteBuffer.createDataPackage(indata);
        for ( int i=0; i<senders.length; i++ )
        {

            IDataSender sender = senders[i];
            try
            {
                sendMessageData(sessionId,data,sender);
            }catch ( Exception x)
            {

                if ( !sender.getSuspect()) log.warn("Unable to send replicated message to "+sender+", is server down?",x);
                sender.setSuspect(true);
            }
        }//while
    }



}
