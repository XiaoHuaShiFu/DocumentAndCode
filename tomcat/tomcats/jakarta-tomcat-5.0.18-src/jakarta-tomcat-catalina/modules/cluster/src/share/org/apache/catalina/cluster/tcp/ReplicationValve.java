/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/modules/cluster/src/share/org/apache/catalina/cluster/tcp/ReplicationValve.java,v 1.9 2004/01/13 05:26:59 fhanik Exp $
 * $Revision: 1.9 $
 * $Date: 2004/01/13 05:26:59 $
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





import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Container;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.HttpResponse;
import org.apache.catalina.Logger;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;
import org.apache.catalina.util.StringManager;
import org.apache.catalina.valves.*;
import org.apache.catalina.cluster.SessionMessage;
import org.apache.catalina.cluster.tcp.SimpleTcpCluster;
import org.apache.catalina.cluster.ClusterSession;
import org.apache.catalina.cluster.ClusterManager;

/**
 * <p>Implementation of a Valve that logs interesting contents from the
 * specified Request (before processing) and the corresponding Response
 * (after processing).  It is especially useful in debugging problems
 * related to headers and cookies.</p>
 *
 * <p>This Valve may be attached to any Container, depending on the granularity
 * of the logging you wish to perform.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.9 $ $Date: 2004/01/13 05:26:59 $
 */

public class ReplicationValve
    extends ValveBase {
    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( SimpleTcpCluster.class );

    // ----------------------------------------------------- Instance Variables


    /**
     * The descriptive information related to this implementation.
     */
    private static final String info =
        "org.apache.catalina.cluster.tcp.ReplicationValve/1.0";


    /**
     * The StringManager for this package.
     */
    protected static StringManager sm =
        StringManager.getManager(Constants.Package);

    /**
     * holds file endings to not call for like images and others
     */
    protected java.util.regex.Pattern[] reqFilters = new java.util.regex.Pattern[0];

    protected int debug = 0;
    // ------------------------------------------------------------- Properties

    public ReplicationValve() {
    }
    /**
     * Return descriptive information about this Valve implementation.
     */
    public String getInfo() {

        return (info);

    }


    // --------------------------------------------------------- Public Methods
    protected static long totalSendTime=0;
    protected static long nrOfRequests =0;
    protected static long lastSendTime =0;
    protected static synchronized void addClusterSendTime(long time) {
        totalSendTime+=time;
        nrOfRequests++;
        if ( (nrOfRequests % 100) == 0 ) {
            log.info("Average cluster serialize/send time="+(totalSendTime/nrOfRequests)+" ms for "+
                     nrOfRequests+" requests ("+totalSendTime+"ms).");
            lastSendTime=System.currentTimeMillis();
        }//end if
    }


    /**
     * Log the interesting request parameters, invoke the next Valve in the
     * sequence, and log the interesting response parameters.
     *
     * @param request The servlet request to be processed
     * @param response The servlet response to be created
     * @param context The valve context used to invoke the next valve
     *  in the current processing pipeline
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void invoke(Request request, Response response,
                       ValveContext context)
        throws IOException, ServletException
    {
        //this happens before the request
        context.invokeNext(request, response);
        //this happens after the request
        try
        {
            long start = System.currentTimeMillis();
            HttpRequest hrequest = (HttpRequest) request;
            HttpServletRequest hreq = (HttpServletRequest) hrequest.getRequest();
            HttpSession session = hreq.getSession(false);
            
            if (!( request.getContext().getManager() instanceof ClusterManager) ) return;
            
            ClusterManager manager = (ClusterManager)request.getContext().getManager();
            SimpleTcpCluster cluster = (SimpleTcpCluster)getContainer().getCluster();
            if ( cluster == null ) {
                log.warn("No cluster configured for this request.");
                return;
            }
            //first check for session invalidations
            String[] invalidIds=manager.getInvalidatedSessions();
            if ( invalidIds.length > 0 ) {
                for ( int i=0;i<invalidIds.length; i++ ) {
                    try {
                        SessionMessage imsg = manager.requestCompleted(
                            invalidIds[i]);
                        if (imsg != null)
                            cluster.send(imsg);
                    }catch ( Exception x ) {
                        log.error("Unable to send session invalid message over cluster.",x);
                    }
                }
            }

            String id = null;
            if ( session != null )
                id = session.getId();
            else
                return;

            if ( id == null )
                return;

            if ( (request.getContext().getManager()==null) ||
                 (!(request.getContext().getManager() instanceof ClusterManager)))
                return;



            String uri = hrequest.getDecodedRequestURI();
            boolean filterfound = false;

            for ( int i=0; (i<reqFilters.length) && (!filterfound); i++ )
            {
                java.util.regex.Matcher matcher = reqFilters[i].matcher(uri);
                filterfound = matcher.matches();
            }//for
            if ( filterfound )
                return;

            log.debug("Invoking replication request on "+uri);

            
            SessionMessage msg = manager.requestCompleted(id);

            if ( msg == null ) return;

            cluster.send(msg);
            long stop = System.currentTimeMillis();
            addClusterSendTime(stop-start);

        }catch (Exception x)
        {
            log.error("Unable to perform replication request.",x);
        }
    }


    /**
     * Return a String rendering of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("ReplicationValve[");
        if (container != null)
            sb.append(container.getName());
        sb.append("]");
        return (sb.toString());

    }

    public void setFilter(String filter)
    {
        log.debug("Loading request filters="+filter);
        java.util.StringTokenizer t = new java.util.StringTokenizer(filter,";");
        this.reqFilters = new java.util.regex.Pattern[t.countTokens()];
        int i = 0;
        while ( t.hasMoreTokens() )
        {
            String s = t.nextToken();
            log.debug("Request filter="+s);
            try
            {
                reqFilters[i++] = java.util.regex.Pattern.compile(s);
            }catch ( Exception x )
            {
                log.error("Unable to compile filter "+s,x);
            }
        }
    }

    public void setDebug(int debug)
    {
        this.debug = debug;
    }

    // ------------------------------------------------------ Protected Methods



}
