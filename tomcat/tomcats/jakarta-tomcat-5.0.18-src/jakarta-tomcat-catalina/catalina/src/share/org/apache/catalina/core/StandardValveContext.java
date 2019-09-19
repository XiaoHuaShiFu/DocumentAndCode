/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/catalina/src/share/org/apache/catalina/core/StandardValveContext.java,v 1.2 2003/05/26 22:03:59 remm Exp $
 * $Revision: 1.2 $
 * $Date: 2003/05/26 22:03:59 $
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


package org.apache.catalina.core;


import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;
import org.apache.catalina.util.StringManager;


/**
 * Standard implementation of a <code>ValveContext</code>.
 *
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 */

public final class StandardValveContext
    implements ValveContext {


    // ----------------------------------------------------- Instance Variables


    /**
     * The string manager for this package.
     */
    protected static StringManager sm =
        StringManager.getManager(Constants.Package);


    protected String info = 
        "org.apache.catalina.core.StandardValveContext/1.0";
    protected int stage = 0;
    protected Valve basic = null;
    protected Valve valves[] = null;


    // ------------------------------------------------------------- Properties


    /**
     * Return descriptive information about this ValveContext 
     * implementation.
     */
    public String getInfo() {
        return info;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Cause the <code>invoke()</code> method of the next Valve that is 
     * part of the Pipeline currently being processed (if any) to be 
     * executed, passing on the specified request and response objects 
     * plus this <code>ValveContext</code> instance.  Exceptions thrown by
     * a subsequently executed Valve (or a Filter or Servlet at the 
     * application level) will be passed on to our caller.
     *
     * If there are no more Valves to be executed, an appropriate
     * ServletException will be thrown by this ValveContext.
     *
     * @param request The request currently being processed
     * @param response The response currently being created
     *
     * @exception IOException if thrown by a subsequent Valve, Filter, or
     *  Servlet
     * @exception ServletException if thrown by a subsequent Valve, Filter,
     *  or Servlet
     * @exception ServletException if there are no further Valves 
     *  configured in the Pipeline currently being processed
     */
    public final void invokeNext(Request request, Response response)
        throws IOException, ServletException {

        int subscript = stage;
        stage = stage + 1;

        // Invoke the requested Valve for the current request thread
        if (subscript < valves.length) {
            valves[subscript].invoke(request, response, this);
        } else if ((subscript == valves.length) && (basic != null)) {
            basic.invoke(request, response, this);
        } else {
            throw new ServletException
                (sm.getString("standardPipeline.noValve"));
        }

    }


    // -------------------------------------------------------- Package Methods


    /**
     * Reset state.
     */
    void set(Valve basic, Valve valves[]) {
        stage = 0;
        this.basic = basic;
        this.valves = valves;
    }


}

