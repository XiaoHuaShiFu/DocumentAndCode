/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/compiler/TagConstants.java,v 1.1.1.1 2002/03/28 18:46:16 kinman Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/03/28 18:46:16 $
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

public interface TagConstants {
    public static final String JSP_ROOT_TAG = "jsp:root";
    public static final String JSP_ROOT_TAG_END = "</jsp:root>";
    public static final String JSP_PAGE_DIRECTIVE_TAG = "jsp:directive.page";
    public static final String JSP_INCLUDE_DIRECTIVE_TAG
	= "jsp:directive.include";
    public static final String JSP_DECLARATION_TAG = "jsp:declaration";
    public static final String JSP_DECLARATION_TAG_START = "<jsp:declaration>";
    public static final String JSP_DECLARATION_TAG_END = "</jsp:declaration>";
    public static final String JSP_SCRIPTLET_TAG = "jsp:scriptlet";
    public static final String JSP_SCRIPTLET_TAG_START = "<jsp:scriptlet>";
    public static final String JSP_SCRIPTLET_TAG_END = "</jsp:scriptlet>";
    public static final String JSP_EXPRESSION_TAG = "jsp:expression";
    public static final String JSP_EXPRESSION_TAG_START = "<jsp:expression>";
    public static final String JSP_EXPRESSION_TAG_END = "</jsp:expression>";
    public static final String JSP_USE_BEAN_TAG = "jsp:useBean";
    public static final String JSP_SET_PROPERTY_TAG = "jsp:setProperty";
    public static final String JSP_GET_PROPERTY_TAG = "jsp:getProperty";
    public static final String JSP_INCLUDE_TAG = "jsp:include";
    public static final String JSP_FORWARD_TAG = "jsp:forward";
    public static final String JSP_PARAM_TAG = "jsp:param";
    public static final String JSP_PARAMS_TAG = "jsp:params";
    public static final String JSP_PLUGIN_TAG = "jsp:plugin";
    public static final String JSP_FALLBACK_TAG = "jsp:fallback";
    public static final String JSP_TEXT_TAG = "jsp:text";
    public static final String JSP_TEXT_TAG_START = "<jsp:text>";
    public static final String JSP_TEXT_TAG_END = "</jsp:text>";
}
