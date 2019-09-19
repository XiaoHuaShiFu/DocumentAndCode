/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/servlet/JspServlet.java,v 1.31 2003/09/10 01:47:24 jfarcand Exp $
 * $Revision: 1.31 $
 * $Date: 2003/09/10 01:47:24 $
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
 */ 

package org.apache.jasper.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jasper.Constants;
import org.apache.jasper.EmbeddedServletOptions;
import org.apache.jasper.Options;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.apache.jasper.compiler.Localizer;

/**
 * The JSP engine (a.k.a Jasper).
 *
 * The servlet container is responsible for providing a
 * URLClassLoader for the web application context Jasper
 * is being used in. Jasper will try get the Tomcat
 * ServletContext attribute for its ServletContext class
 * loader, if that fails, it uses the parent class loader.
 * In either case, it must be a URLClassLoader.
 *
 * @author Anil K. Vijendran
 * @author Harish Prabandham
 * @author Remy Maucherat
 * @author Kin-man Chung
 * @author Glenn Nielsen
 */
public class JspServlet extends HttpServlet {

    // Logger
    private static Log log = LogFactory.getLog(JspServlet.class);

    private ServletContext context;
    private ServletConfig config;
    private Options options;
    private JspRuntimeContext rctxt;

    public void init(ServletConfig config)
	throws ServletException {

	super.init(config);
	this.config = config;
	this.context = config.getServletContext();
        
        options = new EmbeddedServletOptions(config, context);

        // Initialize the JSP Runtime Context
        rctxt = new JspRuntimeContext(context,options);

	if (log.isDebugEnabled()) {
	    log.debug(Localizer.getMessage("jsp.message.scratch.dir.is",
					   options.getScratchDir().toString()));
	    log.debug(Localizer.getMessage("jsp.message.dont.modify.servlets"));
	}
    }


    /**
     * <p>Look for a <em>precompilation request</em> as described in
     * Section 8.4.2 of the JSP 1.2 Specification.  <strong>WARNING</strong> -
     * we cannot use <code>request.getParameter()</code> for this, because
     * that will trigger parsing all of the request parameters, and not give
     * a servlet the opportunity to call
     * <code>request.setCharacterEncoding()</code> first.</p>
     *
     * @param request The servlet requset we are processing
     *
     * @exception ServletException if an invalid parameter value for the
     *  <code>jsp_precompile</code> parameter name is specified
     */
    boolean preCompile(HttpServletRequest request) throws ServletException {

        String queryString = request.getQueryString();
        if (queryString == null) {
            return (false);
        }
        int start = queryString.indexOf(Constants.PRECOMPILE);
        if (start < 0) {
            return (false);
        }
        queryString =
            queryString.substring(start + Constants.PRECOMPILE.length());
        if (queryString.length() == 0) {
            return (true);             // ?jsp_precompile
        }
        if (queryString.startsWith("&")) {
            return (true);             // ?jsp_precompile&foo=bar...
        }
        if (!queryString.startsWith("=")) {
            return (false);            // part of some other name or value
        }
        int limit = queryString.length();
        int ampersand = queryString.indexOf("&");
        if (ampersand > 0) {
            limit = ampersand;
        }
        String value = queryString.substring(1, limit);
        if (value.equals("true")) {
            return (true);             // ?jsp_precompile=true
        } else if (value.equals("false")) {
	    // Spec says if jsp_precompile=false, the request should not
	    // be delivered to the JSP page; the easiest way to implement
	    // this is to set the flag to true, and precompile the page anyway.
	    // This still conforms to the spec, since it says the
	    // precompilation request can be ignored.
            return (true);             // ?jsp_precompile=false
        } else {
            throw new ServletException("Cannot have request parameter " +
                                       Constants.PRECOMPILE + " set to " +
                                       value);
        }

    }
    

    public void service (HttpServletRequest request, 
    			 HttpServletResponse response)
	throws ServletException, IOException {

        try {
            String includeUri 
                = (String) request.getAttribute(Constants.INC_SERVLET_PATH);
            String requestUri 
                = (String) request.getAttribute(Constants.INC_REQUEST_URI);
            
            String jspUri;
            
            // When jsp-property-group/url-matching is used, and when the 
            // jsp is not defined with <servlet-name>, the url
            // as to be passed as it is to the JSP container (since 
            // Catalina doesn't know anything about the requested JSP 
            
            // The first scenario occurs when the jsp is not directly under /
            // example: /utf16/foo.jsp
            if (requestUri != null){
                String currentIncludedUri 
                    = requestUri.substring(requestUri.indexOf(includeUri));

                if ( !includeUri.equals(currentIncludedUri) ) {
                    includeUri = currentIncludedUri;
                }
            }

            // The second scenario is when the includeUri is null but it 
            // is still possible to recreate the request.
            if (includeUri == null) {
                jspUri = request.getServletPath();
                if (request.getPathInfo() != null) {
                    jspUri = request.getServletPath() + request.getPathInfo();
                }
            } else {
                jspUri = includeUri;
            }
                                
            String jspFile = (String) request.getAttribute(Constants.JSP_FILE);
            if (jspFile != null) {
                jspUri = jspFile;
            }

            boolean precompile = preCompile(request);

            if (log.isDebugEnabled()) {	    
                log.debug("JspEngine --> " + jspUri);
                log.debug("\t     ServletPath: " + request.getServletPath());
                log.debug("\t        PathInfo: " + request.getPathInfo());
                log.debug("\t        RealPath: " + context.getRealPath(jspUri));
                log.debug("\t      RequestURI: " + request.getRequestURI());
                log.debug("\t     QueryString: " + request.getQueryString());
                log.debug("\t  Request Params: ");
                Enumeration e = request.getParameterNames();

                while (e.hasMoreElements()) {
                    String name = (String) e.nextElement();
                    log.info("\t\t " + name + " = " +
                             request.getParameter(name));
                }
            }

            serviceJspFile(request, response, jspUri, null, precompile);
        } catch (RuntimeException e) {
            throw e;
        } catch (ServletException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Throwable e) {
            throw new ServletException(e);
        }

    }

    public void destroy() {
        if (log.isDebugEnabled()) {
            log.debug("JspServlet.destroy()");
        }
        rctxt.destroy();
    }


    // -------------------------------------------------------- Private Methods

    private void serviceJspFile(HttpServletRequest request,
                                HttpServletResponse response, String jspUri,
                                Throwable exception, boolean precompile)
        throws ServletException, IOException {

        JspServletWrapper wrapper =
            (JspServletWrapper) rctxt.getWrapper(jspUri);
        if (wrapper == null) {
            // First check if the requested JSP page exists, to avoid
            // creating unnecessary directories and files.
            InputStream resourceStream = context.getResourceAsStream(jspUri);
            if (resourceStream == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, jspUri);
                return;
            } else {
                try {
                    resourceStream.close();
                } catch(IOException e) { /* ignore */ }
            }
            boolean isErrorPage = exception != null;
            synchronized(this) {
                wrapper = (JspServletWrapper) rctxt.getWrapper(jspUri);
                if (wrapper == null) {
                    wrapper = new JspServletWrapper(config, options, jspUri,
                                                    isErrorPage, rctxt);
                    rctxt.addWrapper(jspUri,wrapper);
                }
            }
        }

        wrapper.service(request, response, precompile);

    }

}
