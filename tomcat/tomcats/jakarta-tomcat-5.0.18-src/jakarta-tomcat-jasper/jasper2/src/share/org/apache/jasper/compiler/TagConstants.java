/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/compiler/TagConstants.java,v 1.10 2003/04/09 00:23:51 luehe Exp $
 * $Revision: 1.10 $
 * $Date: 2003/04/09 00:23:51 $
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

    public static final String JSP_URI = "http://java.sun.com/JSP/Page";

    public static final String DIRECTIVE_ACTION = "directive.";

    public static final String ROOT_ACTION = "root";
    public static final String JSP_ROOT_ACTION = "jsp:root";

    public static final String PAGE_DIRECTIVE_ACTION = "directive.page";
    public static final String JSP_PAGE_DIRECTIVE_ACTION = "jsp:directive.page";

    public static final String INCLUDE_DIRECTIVE_ACTION = "directive.include";
    public static final String JSP_INCLUDE_DIRECTIVE_ACTION = "jsp:directive.include";

    public static final String DECLARATION_ACTION = "declaration";
    public static final String JSP_DECLARATION_ACTION = "jsp:declaration";

    public static final String SCRIPTLET_ACTION = "scriptlet";
    public static final String JSP_SCRIPTLET_ACTION = "jsp:scriptlet";

    public static final String EXPRESSION_ACTION = "expression";
    public static final String JSP_EXPRESSION_ACTION = "jsp:expression";

    public static final String USE_BEAN_ACTION = "useBean";
    public static final String JSP_USE_BEAN_ACTION = "jsp:useBean";

    public static final String SET_PROPERTY_ACTION = "setProperty";
    public static final String JSP_SET_PROPERTY_ACTION = "jsp:setProperty";

    public static final String GET_PROPERTY_ACTION = "getProperty";
    public static final String JSP_GET_PROPERTY_ACTION = "jsp:getProperty";

    public static final String INCLUDE_ACTION = "include";
    public static final String JSP_INCLUDE_ACTION = "jsp:include";

    public static final String FORWARD_ACTION = "forward";
    public static final String JSP_FORWARD_ACTION = "jsp:forward";

    public static final String PARAM_ACTION = "param";
    public static final String JSP_PARAM_ACTION = "jsp:param";

    public static final String PARAMS_ACTION = "params";
    public static final String JSP_PARAMS_ACTION = "jsp:params";

    public static final String PLUGIN_ACTION = "plugin";
    public static final String JSP_PLUGIN_ACTION = "jsp:plugin";

    public static final String FALLBACK_ACTION = "fallback";
    public static final String JSP_FALLBACK_ACTION = "jsp:fallback";

    public static final String TEXT_ACTION = "text";
    public static final String JSP_TEXT_ACTION = "jsp:text";
    public static final String JSP_TEXT_ACTION_END = "</jsp:text>";

    public static final String ATTRIBUTE_ACTION = "attribute";
    public static final String JSP_ATTRIBUTE_ACTION = "jsp:attribute";

    public static final String BODY_ACTION = "body";
    public static final String JSP_BODY_ACTION = "jsp:body";

    public static final String ELEMENT_ACTION = "element";
    public static final String JSP_ELEMENT_ACTION = "jsp:element";

    public static final String OUTPUT_ACTION = "output";
    public static final String JSP_OUTPUT_ACTION = "jsp:output";

    public static final String TAGLIB_DIRECTIVE_ACTION = "taglib";
    public static final String JSP_TAGLIB_DIRECTIVE_ACTION = "jsp:taglib";

    /*
     * Tag Files
     */
    public static final String INVOKE_ACTION = "invoke";
    public static final String JSP_INVOKE_ACTION = "jsp:invoke";

    public static final String DOBODY_ACTION = "doBody";
    public static final String JSP_DOBODY_ACTION = "jsp:doBody";

    /*
     * Tag File Directives
     */
    public static final String TAG_DIRECTIVE_ACTION = "directive.tag";
    public static final String JSP_TAG_DIRECTIVE_ACTION = "jsp:directive.tag";

    public static final String ATTRIBUTE_DIRECTIVE_ACTION = "directive.attribute";
    public static final String JSP_ATTRIBUTE_DIRECTIVE_ACTION = "jsp:directive.attribute";

    public static final String VARIABLE_DIRECTIVE_ACTION = "directive.variable";
    public static final String JSP_VARIABLE_DIRECTIVE_ACTION = "jsp:directive.variable";

    /*
     * Directive attributes
     */
    public static final String URN_JSPTAGDIR = "urn:jsptagdir:";
    public static final String URN_JSPTLD = "urn:jsptld:";
}
