/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/runtime/JspFactoryImpl.java,v 1.4 2003/09/02 20:41:00 luehe Exp $
 * $Revision: 1.4 $
 * $Date: 2003/09/02 20:41:00 $
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
 */ 
package org.apache.jasper.runtime;

import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.JspEngineInfo;
import javax.servlet.jsp.PageContext;

import org.apache.jasper.util.SimplePool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of JspFactory.
 *
 * @author Anil K. Vijendran
 */
public class JspFactoryImpl extends JspFactory {

    // Logger
    private static Log log = LogFactory.getLog(JspFactoryImpl.class);

    private static final String SPEC_VERSION = "2.0";
    private static final boolean USE_POOL = true;

    private SimplePool pool = new SimplePool( 100 );
    
    public PageContext getPageContext(Servlet servlet,
				      ServletRequest request,
                                      ServletResponse response,
                                      String errorPageURL,                    
                                      boolean needsSession,
				      int bufferSize,
                                      boolean autoflush) {

	if( System.getSecurityManager() != null ) {
	    PrivilegedGetPageContext dp = new PrivilegedGetPageContext(
		(JspFactoryImpl)this, servlet, request, response, errorPageURL,
                needsSession, bufferSize, autoflush);
	    return (PageContext)AccessController.doPrivileged(dp);
	} else {
	    return internalGetPageContext(servlet, request, response,
					  errorPageURL, needsSession,
					  bufferSize, autoflush);
	}
    }

    public void releasePageContext(PageContext pc) {
	if( pc == null )
	    return;
        if( System.getSecurityManager() != null ) {
            PrivilegedReleasePageContext dp = new PrivilegedReleasePageContext(
                (JspFactoryImpl)this,pc);
            AccessController.doPrivileged(dp);
        } else {
            internalReleasePageContext(pc);
	}
    }

    public JspEngineInfo getEngineInfo() {
        return new JspEngineInfo() {
		public String getSpecificationVersion() {
		    return SPEC_VERSION;
		}
	    };
    }

    private PageContext internalGetPageContext(Servlet servlet,
					       ServletRequest request,
					       ServletResponse response, 
					       String errorPageURL, 
					       boolean needsSession,
					       int bufferSize, 
					       boolean autoflush) {
        try {
	    PageContext pc;
	    if( USE_POOL ) {
                pc = (PageContext) pool.get();
		if( pc == null ) {
		    pc= new PageContextImpl(this);
		}
	    } else {
		pc = new PageContextImpl(this);
	    }
	    pc.initialize(servlet, request, response, errorPageURL, 
                          needsSession, bufferSize, autoflush);
            return pc;
        } catch (Throwable ex) {
            /* FIXME: need to do something reasonable here!! */
            log.fatal("Exception initializing page context", ex);
            return null;
        }
    }

    private void internalReleasePageContext(PageContext pc) {
        pc.release();
	if (USE_POOL && (pc instanceof PageContextImpl)) {
	    pool.put( pc );
	}
    }

    private class PrivilegedGetPageContext implements PrivilegedAction {

	private JspFactoryImpl factory;
	private Servlet servlet;
	private ServletRequest request;
	private ServletResponse response;
	private String errorPageURL;
	private boolean needsSession;
	private int bufferSize;
	private boolean autoflush;

	PrivilegedGetPageContext(JspFactoryImpl factory,
				 Servlet servlet,
				 ServletRequest request,
				 ServletResponse response,
				 String errorPageURL,
				 boolean needsSession,
				 int bufferSize,
				 boolean autoflush) {
	    this.factory = factory;
	    this.servlet = servlet;
	    this.request = request;
	    this.response = response;
	    this.errorPageURL = errorPageURL;
	    this.needsSession = needsSession;
	    this.bufferSize = bufferSize;
	    this.autoflush = autoflush;
	}
 
	public Object run() {
	    return factory.internalGetPageContext(servlet,
						  request,
						  response,
						  errorPageURL,
						  needsSession,
						  bufferSize,
						  autoflush);
	}
    }

    private class PrivilegedReleasePageContext implements PrivilegedAction {

        private JspFactoryImpl factory;
	private PageContext pageContext;

        PrivilegedReleasePageContext(JspFactoryImpl factory,
				     PageContext pageContext) {
            this.factory = factory;
            this.pageContext = pageContext;
        }

        public Object run() {
            factory.internalReleasePageContext(pageContext);
	    return null;
        }
    }
}
