/*
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


package org.apache.catalina.core;


import java.io.IOException;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.Logger;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.ValveContext;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.catalina.util.StringManager;
import org.apache.catalina.valves.ValveBase;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.buf.MessageBytes;

/**
 * Valve that implements the default basic behavior for the
 * <code>StandardWrapper</code> container implementation.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.24 $ $Date: 2003/12/22 17:47:43 $
 */

final class StandardWrapperValve
    extends ValveBase {

    private static Log log = LogFactory.getLog(StandardWrapperValve.class);

    // ----------------------------------------------------- Instance Variables


    // Some JMX statistics. This vavle is associated with a StandardWrapper.
    // We exponse the StandardWrapper as JMX ( j2eeType=Servlet ). The fields
    // are here for performance.
    private long processingTime;
    private long maxTime;
    private long minTime = Long.MAX_VALUE;
    private int requestCount;
    private int errorCount;


    /**
     * The string manager for this package.
     */
    private static final StringManager sm =
        StringManager.getManager(Constants.Package);


    // --------------------------------------------------------- Public Methods


    /**
     * Invoke the servlet we are managing, respecting the rules regarding
     * servlet lifecycle and SingleThreadModel support.
     *
     * @param request Request to be processed
     * @param response Response to be produced
     * @param valveContext Valve context used to forward to the next Valve
     *
     * @exception IOException if an input/output error occurred
     * @exception ServletException if a servlet error occurred
     */
    public final void invoke(Request request, Response response,
                             ValveContext valveContext)
        throws IOException, ServletException {

        // Initialize local variables we may need
        boolean unavailable = false;
        Throwable throwable = null;
        // This should be a Request attribute...
        long t1=System.currentTimeMillis();
        requestCount++;
        StandardWrapper wrapper = (StandardWrapper) getContainer();
        HttpRequest hrequest = (HttpRequest) request;
        Servlet servlet = null;
        HttpServletRequest hreq = (HttpServletRequest) request.getRequest();
        HttpServletResponse hres =
            (HttpServletResponse) response.getResponse();

        // Check for the application being marked unavailable
        if (!((Context) wrapper.getParent()).getAvailable()) {
            hres.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                           sm.getString("standardContext.isUnavailable"));
            unavailable = true;
        }

        // Check for the servlet being marked unavailable
        if (!unavailable && wrapper.isUnavailable()) {
            log(sm.getString("standardWrapper.isUnavailable",
                             wrapper.getName()));
            if (hres == null) {
                ;       // NOTE - Not much we can do generically
            } else {
                long available = wrapper.getAvailable();
                if ((available > 0L) && (available < Long.MAX_VALUE)) {
                    hres.setDateHeader("Retry-After", available);
                    hres.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                               sm.getString("standardWrapper.isUnavailable",
                                            wrapper.getName()));
                } else if (available == Long.MAX_VALUE) {
                    hres.sendError(HttpServletResponse.SC_NOT_FOUND,
                               sm.getString("standardWrapper.notFound",
                                            wrapper.getName()));
                }
            }
            unavailable = true;
        }

        // Allocate a servlet instance to process this request
        try {
            if (!unavailable) {
                servlet = wrapper.allocate();
            }
        } catch (UnavailableException e) {
            long available = wrapper.getAvailable();
            if ((available > 0L) && (available < Long.MAX_VALUE)) {
                hres.setDateHeader("Retry-After", available);
                hres.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                           sm.getString("standardWrapper.isUnavailable",
                                        wrapper.getName()));
            } else if (available == Long.MAX_VALUE) {
                hres.sendError(HttpServletResponse.SC_NOT_FOUND,
                           sm.getString("standardWrapper.notFound",
                                        wrapper.getName()));
            }
        } catch (ServletException e) {
            log(sm.getString("standardWrapper.allocateException",
                             wrapper.getName()), e);
            throwable = e;
            exception(request, response, e);
            servlet = null;
        } catch (Throwable e) {
            log(sm.getString("standardWrapper.allocateException",
                             wrapper.getName()), e);
            throwable = e;
            exception(request, response, e);
            servlet = null;
        }

        // Acknowlege the request
        try {
            response.sendAcknowledgement();
        } catch (IOException e) {
            hreq.removeAttribute(Globals.JSP_FILE_ATTR);
            log(sm.getString("standardWrapper.acknowledgeException",
                             wrapper.getName()), e);
            throwable = e;
            exception(request, response, e);
        } catch (Throwable e) {
            log(sm.getString("standardWrapper.acknowledgeException",
                             wrapper.getName()), e);
            throwable = e;
            exception(request, response, e);
            servlet = null;
        }
        MessageBytes requestPathMB = null;
        if (hreq != null) {
            requestPathMB = hrequest.getRequestPathMB();
        }
        hreq.setAttribute
            (ApplicationFilterFactory.DISPATCHER_TYPE_ATTR,
             ApplicationFilterFactory.REQUEST_INTEGER);
        hreq.setAttribute
            (ApplicationFilterFactory.DISPATCHER_REQUEST_PATH_ATTR,
             requestPathMB);
        // Create the filter chain for this request
        ApplicationFilterFactory factory =
            ApplicationFilterFactory.getInstance();
        ApplicationFilterChain filterChain =
            factory.createFilterChain((ServletRequest) request,
                                      wrapper, servlet);

        // Call the filter chain for this request
        // NOTE: This also calls the servlet's service() method
        try {
            String jspFile = wrapper.getJspFile();
            if (jspFile != null)
                hreq.setAttribute(Globals.JSP_FILE_ATTR, jspFile);
            else
                hreq.removeAttribute(Globals.JSP_FILE_ATTR);
            if ((servlet != null) && (filterChain != null)) {
                filterChain.doFilter(hreq, hres);
            }
            hreq.removeAttribute(Globals.JSP_FILE_ATTR);
        } catch (ClientAbortException e) {
            hreq.removeAttribute(Globals.JSP_FILE_ATTR);
            throwable = e;
            exception(request, response, e);
        } catch (IOException e) {
            hreq.removeAttribute(Globals.JSP_FILE_ATTR);
            log(sm.getString("standardWrapper.serviceException",
                             wrapper.getName()), e);
            throwable = e;
            exception(request, response, e);
        } catch (UnavailableException e) {
            hreq.removeAttribute(Globals.JSP_FILE_ATTR);
            log(sm.getString("standardWrapper.serviceException",
                             wrapper.getName()), e);
            //            throwable = e;
            //            exception(request, response, e);
            wrapper.unavailable(e);
            long available = wrapper.getAvailable();
            if ((available > 0L) && (available < Long.MAX_VALUE)) {
                hres.setDateHeader("Retry-After", available);
                hres.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                           sm.getString("standardWrapper.isUnavailable",
                                        wrapper.getName()));
            } else if (available == Long.MAX_VALUE) {
                hres.sendError(HttpServletResponse.SC_NOT_FOUND,
                            sm.getString("standardWrapper.notFound",
                                        wrapper.getName()));
            }
            // Do not save exception in 'throwable', because we
            // do not want to do exception(request, response, e) processing
        } catch (ServletException e) {
            hreq.removeAttribute(Globals.JSP_FILE_ATTR);
            Throwable rootCause = e;
            Throwable rootCauseCheck = null;

            // Extra aggressive rootCause finding
            do {
                try {
                    rootCauseCheck = (Throwable)PropertyUtils.getProperty
                                                (rootCause, "rootCause");
                    if (rootCauseCheck!=null)
                        rootCause = rootCauseCheck;

                } catch (ClassCastException ex) {
                    rootCauseCheck = null;
                } catch (IllegalAccessException ex) {
                    rootCauseCheck = null;
                } catch (NoSuchMethodException ex) {
                    rootCauseCheck = null;
                } catch (java.lang.reflect.InvocationTargetException ex) {
                    rootCauseCheck = null;
                }
            } while (rootCauseCheck != null);

            if (!(rootCause instanceof ClientAbortException)) {
                log(sm.getString("standardWrapper.serviceException",
                                 wrapper.getName()), rootCause);
            }
            throwable = e;
            exception(request, response, e);
        } catch (Throwable e) {
            hreq.removeAttribute(Globals.JSP_FILE_ATTR);
            log(sm.getString("standardWrapper.serviceException",
                             wrapper.getName()), e);
            throwable = e;
            exception(request, response, e);
        }

        // Release the filter chain (if any) for this request
        try {
            if (filterChain != null)
                filterChain.release();
        } catch (Throwable e) {
            log(sm.getString("standardWrapper.releaseFilters",
                             wrapper.getName()), e);
            if (throwable == null) {
                throwable = e;
                exception(request, response, e);
            }
        }

        // Deallocate the allocated servlet instance
        try {
            if (servlet != null) {
                wrapper.deallocate(servlet);
            }
        } catch (Throwable e) {
            log(sm.getString("standardWrapper.deallocateException",
                             wrapper.getName()), e);
            if (throwable == null) {
                throwable = e;
                exception(request, response, e);
            }
        }

        // If this servlet has been marked permanently unavailable,
        // unload it and release this instance
        try {
            if ((servlet != null) &&
                (wrapper.getAvailable() == Long.MAX_VALUE)) {
                wrapper.unload();
            }
        } catch (Throwable e) {
            log(sm.getString("standardWrapper.unloadException",
                             wrapper.getName()), e);
            if (throwable == null) {
                throwable = e;
                exception(request, response, e);
            }
        }
        long t2=System.currentTimeMillis();

        long time=t2-t1;
        processingTime += time;
        if( time > maxTime) maxTime=time;
        if( time < minTime) minTime=time;

    }


    // -------------------------------------------------------- Private Methods


    /**
     * Log a message on the Logger associated with our Container (if any)
     *
     * @param message Message to be logged
     */
    private void log(String message) {

        Logger logger = null;
        if (container != null)
            logger = container.getLogger();
        if (logger != null)
            logger.log("StandardWrapperValve[" + container.getName() + "]: "
                       + message);
        else {
            String containerName = null;
            if (container != null)
                containerName = container.getName();
            System.out.println("StandardWrapperValve[" + containerName
                               + "]: " + message);
        }

    }


    /**
     * Log a message on the Logger associated with our Container (if any)
     *
     * @param message Message to be logged
     * @param throwable Associated exception
     */
    private void log(String message, Throwable throwable) {

        Logger logger = null;
        if (container != null)
            logger = container.getLogger();
        if (logger != null)
            logger.log("StandardWrapperValve[" + container.getName() + "]: "
                       + message, throwable);
        else {
            String containerName = null;
            if (container != null)
                containerName = container.getName();
            System.out.println("StandardWrapperValve[" + containerName
                               + "]: " + message);
            System.out.println("" + throwable);
            throwable.printStackTrace(System.out);
        }

    }


    /**
     * Handle the specified ServletException encountered while processing
     * the specified Request to produce the specified Response.  Any
     * exceptions that occur during generation of the exception report are
     * logged and swallowed.
     *
     * @param request The request being processed
     * @param response The response being generated
     * @param exception The exception that occurred (which possibly wraps
     *  a root cause exception
     */
    private void exception(Request request, Response response,
                           Throwable exception) {
        ServletRequest sreq = request.getRequest();
        sreq.setAttribute(Globals.EXCEPTION_ATTR, exception);

        ServletResponse sresponse = response.getResponse();
        if (sresponse instanceof HttpServletResponse)
            ((HttpServletResponse) sresponse).setStatus
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

    }

    public long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public long getMinTime() {
        return minTime;
    }

    public void setMinTime(long minTime) {
        this.minTime = minTime;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    // Don't register in JMX

    public ObjectName createObjectName(String domain, ObjectName parent)
            throws MalformedObjectNameException
    {
        return null;
    }
}
