/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/compiler/PageInfo.java,v 1.5 2002/06/05 22:01:33 kinman Exp $
 * $Revision: 1.5 $
 * $Date: 2002/06/05 22:01:33 $
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
package org.apache.jasper.compiler;

import java.util.*;

import org.apache.jasper.Constants;

/**
 * A repository for various info about the page under compilation
 *
 * @author Kin-man Chung
 */

class PageInfo {

    private Vector imports;
    private Vector includes;

    private BeanRepository beanRepository;
    private Hashtable tagLibraries;

    private String language = "java";
    private String xtends = Constants.JSP_SERVLET_BASE;
    private String contentType = null;
    private boolean session = true;
    private int buffer = 8*1024;	// XXX confirm
    private boolean autoFlush = true;
    private boolean threadSafe = true;
    private boolean isErrorPage = false;
    private String errorPage = null;
    private String pageEncoding = null;
    private int maxTagNesting = 0;
    private boolean scriptless = false;

    PageInfo(BeanRepository beanRepository) {
	this.beanRepository = beanRepository;
	this.tagLibraries = new Hashtable();
	this.imports = new Vector();
        this.includes = new Vector();

	// Enter standard imports
	for(int i = 0; i < Constants.STANDARD_IMPORTS.length; i++)
	    imports.add(Constants.STANDARD_IMPORTS[i]);
    }

    public void addImports(List imports) {
	this.imports.addAll(imports);
    }

    public List getImports() {
	return imports;
    }

    public void addInclude(String include) {
        this.includes.add(include);
    }
     
    public List getIncludes() {
        return includes;
    }

    public BeanRepository getBeanRepository() {
	return beanRepository;
    }

    public Hashtable getTagLibraries() {
	return tagLibraries;
    }

    public String getLanguage() {
	return language;
    }

    public void setLanguage(String language) {
	this.language = language;
    }

    public String getExtends() {
	return xtends;
    }

    public void setExtends(String xtends) {
	this.xtends = xtends;
    }

    public String getContentType() {
	return contentType;
    }

    public void setContentType(String contentType) {
	this.contentType = contentType;
    }

    public String getErrorPage() {
	return errorPage;
    }

    public void setErrorPage(String errorPage) {
	this.errorPage = errorPage;
    }

    public int getBuffer() {
	return buffer;
    }

    public void setBuffer(int buffer) {
	this.buffer = buffer;
    }

    public boolean isSession() {
	return session;
    }

    public void setSession(boolean session) {
	this.session = session;
    }

    public boolean isAutoFlush() {
	return autoFlush;
    }

    public void setAutoFlush(boolean autoFlush) {
	this.autoFlush = autoFlush;
    }

    public boolean isThreadSafe() {
	return threadSafe;
    }

    public void setThreadSafe(boolean threadSafe) {
	this.threadSafe = threadSafe;
    }

    public boolean isIsErrorPage() {
	return isErrorPage;
    }

    public void setIsErrorPage(boolean isErrorPage) {
	this.isErrorPage = isErrorPage;
    }

    public void setPageEncoding(String pageEncoding) {
	this.pageEncoding = pageEncoding;
    }

    public String getPageEncoding() {
	return pageEncoding;
    }

    public int getMaxTagNesting() {
        return maxTagNesting;
    }

    public void setMaxTagNesting(int maxTagNesting) {
        this.maxTagNesting = maxTagNesting;
    }

    public void setScriptless(boolean s) {
	scriptless = s;
    }

    public boolean isScriptless() {
	return scriptless;
    }

}
