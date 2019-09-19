/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/catalina/src/share/org/apache/catalina/core/DummyResponse.java,v 1.2 2003/09/02 21:22:04 remm Exp $
 * $Revision: 1.2 $
 * $Date: 2003/09/02 21:22:04 $
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


package org.apache.catalina.core;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.HttpResponse;
import org.apache.catalina.Request;


/**
 * Dummy response object, used for JSP precompilation.
 *
 * @author Remy Maucherat
 * @version $Revision: 1.2 $ $Date: 2003/09/02 21:22:04 $
 */

public class DummyResponse
    implements HttpResponse, HttpServletResponse {

    public DummyResponse() {
    }


    public void setAppCommitted(boolean appCommitted) {}
    public boolean isAppCommitted() { return false; }
    public Connector getConnector() { return null; }
    public void setConnector(Connector connector) {}
    public int getContentCount() { return -1; }
    public Context getContext() { return null; }
    public void setContext(Context context) {}
    public boolean getIncluded() { return false; }
    public void setIncluded(boolean included) {}
    public String getInfo() { return null; }
    public Request getRequest() { return null; }
    public void setRequest(Request request) {}
    public ServletResponse getResponse() { return null; }
    public OutputStream getStream() { return null; }
    public void setStream(OutputStream stream) {}
    public void setSuspended(boolean suspended) {}
    public boolean isSuspended() { return false; }
    public void setError() {}
    public boolean isError() { return false; }
    public ServletOutputStream createOutputStream() throws IOException {
        return null;
    }
    public void finishResponse() throws IOException {}
    public int getContentLength() { return -1; }
    public String getContentType() { return null; }
    public PrintWriter getReporter() { return null; }
    public void recycle() {}
    public void write(int b) throws IOException {}
    public void write(byte b[]) throws IOException {}
    public void write(byte b[], int off, int len) throws IOException {}
    public void flushBuffer() throws IOException {}
    public int getBufferSize() { return -1; }
    public String getCharacterEncoding() { return null; }
    public void setCharacterEncoding(String charEncoding) {}
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }
    public Locale getLocale() { return null; }
    public PrintWriter getWriter() throws IOException { return null; }
    public boolean isCommitted() { return false; }
    public void reset() {}
    public void resetBuffer() {}
    public void setBufferSize(int size) {}
    public void setContentLength(int length) {}
    public void setContentType(String type) {}
    public void setLocale(Locale locale) {}

    public Cookie[] getCookies() { return null; }
    public String getHeader(String name) { return null; }
    public String[] getHeaderNames() { return null; }
    public String[] getHeaderValues(String name) { return null; }
    public String getMessage() { return null; }
    public int getStatus() { return -1; }
    public void reset(int status, String message) {}
    public void addCookie(Cookie cookie) {}
    public void addDateHeader(String name, long value) {}
    public void addHeader(String name, String value) {}
    public void addIntHeader(String name, int value) {}
    public boolean containsHeader(String name) { return false; }
    public String encodeRedirectURL(String url) { return null; }
    public String encodeRedirectUrl(String url) { return null; }
    public String encodeURL(String url) { return null; }
    public String encodeUrl(String url) { return null; }
    public void sendAcknowledgement() throws IOException {}
    public void sendError(int status) throws IOException {}
    public void sendError(int status, String message) throws IOException {}
    public void sendRedirect(String location) throws IOException {}
    public void setDateHeader(String name, long value) {}
    public void setHeader(String name, String value) {}
    public void setIntHeader(String name, int value) {}
    public void setStatus(int status) {}
    public void setStatus(int status, String message) {}


}
