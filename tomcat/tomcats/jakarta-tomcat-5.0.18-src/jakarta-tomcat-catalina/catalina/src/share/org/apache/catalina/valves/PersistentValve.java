/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/catalina/src/share/org/apache/catalina/valves/PersistentValve.java,v 1.4 2003/09/02 21:22:03 remm Exp $
 * $Revision: 1.4 $
 * $Date: 2003/09/02 21:22:03 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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


package org.apache.catalina.valves;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Context;
import org.apache.catalina.Logger;
import org.apache.catalina.Manager;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Session;
import org.apache.catalina.Store;
import org.apache.catalina.ValveContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.session.PersistentManager;
import org.apache.catalina.util.StringManager;


/**
 * Valve that implements the default basic behavior for the
 * <code>StandardHost</code> container implementation.
 * <p>
 * <b>USAGE CONSTRAINT</b>: To work correctly it requires a  PersistentManager.
 *
 * @author Jean-Frederic Clere
 * @version $Revision: 1.4 $ $Date: 2003/09/02 21:22:03 $
 */

public class PersistentValve
    extends ValveBase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The descriptive information related to this implementation.
     */
    private static final String info =
        "org.apache.catalina.valves.PersistentValve/1.0";


    /**
     * The string manager for this package.
     */
    private static final StringManager sm =
        StringManager.getManager(Constants.Package);


    // ------------------------------------------------------------- Properties


    /**
     * Return descriptive information about this Valve implementation.
     */
    public String getInfo() {

        return (info);

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Select the appropriate child Context to process this request,
     * based on the specified request URI.  If no matching Context can
     * be found, return an appropriate HTTP error.
     *
     * @param request Request to be processed
     * @param response Response to be produced
     * @param valveContext Valve context used to forward to the next Valve
     *
     * @exception IOException if an input/output error occurred
     * @exception ServletException if a servlet error occurred
     */
    public void invoke(Request request, Response response,
                       ValveContext valveContext)
        throws IOException, ServletException {

        // Select the Context to be used for this Request
        StandardHost host = (StandardHost) getContainer();
        Context context = request.getContext();
        if (context == null) {
            ((HttpServletResponse) response.getResponse()).sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 sm.getString("standardHost.noContext"));
            return;
        }

        // Bind the context CL to the current thread
        Thread.currentThread().setContextClassLoader
            (context.getLoader().getClassLoader());

        // Update the session last access time for our session (if any)
        HttpServletRequest hreq = (HttpServletRequest) request.getRequest();
        String sessionId = hreq.getRequestedSessionId();
        Manager manager = context.getManager();
        if (sessionId != null && manager != null) {
            if (manager instanceof PersistentManager) {
                Store store = ((PersistentManager) manager).getStore();
                if (store != null) {
                    Session session = null;
                    try {
                        session = store.load(sessionId);
                    } catch (Exception e) {
                        log("deserializeError");
                    }
                    if (session != null) {
                        if (!session.isValid() ||
                            isSessionStale(session, System.currentTimeMillis())) {
                            log("session swapped in is invalid or expired");
                            session.expire();
                            store.remove(sessionId);
                        } else {
                            session.setManager(manager);
                            // session.setId(sessionId); Only if new ???
                            manager.add(session);
                            // ((StandardSession)session).activate();
                            session.access();
                        }
                    }
                }
            }
        }
        log("sessionId: " + sessionId);

        // Ask the next valve to process the request.
        valveContext.invokeNext(request, response);

        // Read the sessionid after the response.
        // HttpSession hsess = hreq.getSession(false);
        HttpSession hsess;
        try {
            hsess = hreq.getSession();
        } catch (Exception ex) {
            hsess = null;
        }
        String newsessionId = null;
        if (hsess!=null)
            newsessionId = hsess.getId();

        log("newsessionId: " + newsessionId);
        if (newsessionId!=null) {
            /* store the session in the store and remove it from the manager */
            if (manager instanceof PersistentManager) {
                Session session = manager.findSession(newsessionId);
                Store store = ((PersistentManager) manager).getStore();
                if (store != null && session!=null &&
                    session.isValid() &&
                    !isSessionStale(session, System.currentTimeMillis())) {
                    // ((StandardSession)session).passivate();
                    store.save(session);
                    ((PersistentManager) manager).removeSuper(session);
                    session.recycle();
                } else {
                    log("newsessionId store: " + store + " session: " +
                        session + " valid: " + session.isValid() +
                        " Staled: " +
                        isSessionStale(session, System.currentTimeMillis()));

                }
            } else {
                log("newsessionId Manager: " + manager);
            }
        }
    }

    /**
     * Log a message on the Logger associated with our Container (if any).
     *
     * @param message Message to be logged
     */
    protected void log(String message) {
 
        Logger logger = container.getLogger();
        if (logger != null)
            logger.log(this.toString() + ": " + message);
        else
            System.out.println(this.toString() + ": " + message);
 
    }

    /**
     * Indicate whether the session has been idle for longer
     * than its expiration date as of the supplied time.
     *
     * FIXME: Probably belongs in the Session class.
     */
    protected boolean isSessionStale(Session session, long timeNow) {
 
        int maxInactiveInterval = session.getMaxInactiveInterval();
        if (maxInactiveInterval >= 0) {
            int timeIdle = // Truncate, do not round up
                (int) ((timeNow - session.getLastAccessedTime()) / 1000L);
            if (timeIdle >= maxInactiveInterval)
                return true;
        }
 
        return false;
 
    }

}
