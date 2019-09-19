/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/compiler/tagplugin/TagPluginContext.java,v 1.12 2003/09/02 21:40:00 remm Exp $
 * $Revision: 1.12 $
 * $Date: 2003/09/02 21:40:00 $
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

package org.apache.jasper.compiler.tagplugin;


/**
 * This interface allows the plugin author to make inqueries about the
 * properties of the current tag, and to use Jasper resources to generate
 * direct Java codes in place of tag handler invocations.
 */

public interface TagPluginContext {
    /**
     * @return true if the body of the tag is scriptless.
     */
    boolean isScriptless();

    /**
     * @param attribute Name of the attribute
     * @return true if the attribute is specified in the tag
     */
    boolean isAttributeSpecified(String attribute);

    /**
     * @return An unique temporary variable name that the plugin can use.
     */
    String getTemporaryVariableName();

    /**
     * Generate an import statement
     * @param importName Name of the import class, '*' allowed.
     */
    void generateImport(String s);

    /**
     * Generate a declaration in the of the generated class.  This can be
     * used to declare an innter class, a method, or a class variable.
     * @param id An unique ID identifying the declaration.  It is not
     *           part of the declaration, and is used to ensure that the
     *           declaration will only appear once.  If this method is
     *           invoked with the same id more than once in the translation
     *           unit, only the first declaration will be taken.
     * @param text The text of the declaration.
     **/
    void generateDeclaration(String id, String text);

    /**
     * Generate Java source codes
     */
    void generateJavaSource(String s);

    /**
     * @return true if the attribute is specified and its value is a
     *         translation-time constant.
     */
    boolean isConstantAttribute(String attribute);

    /**
     * @return A string that is the value of a constant attribute.  Undefined
     *         if the attribute is not a (translation-time) constant.
     *         null if the attribute is not specified.
     */
    String getConstantAttribute(String attribute);

    /**
     * Generate codesto evaluate value of a attribute in the custom tag
     * The codes is a Java expression.
     * NOTE: Currently cannot handle attributes that are fragments.
     * @param attribute The specified attribute
     */
    void generateAttribute(String attribute);

    /*
     * Generate codes for the body of the custom tag
     */
    void generateBody();

    /**
     * Abandon optimization for this tag handler, and instruct
     * Jasper to generate the tag handler calls, as usual.
     * Should be invoked if errors are detected, or when the tag body
     * is deemed too compilicated for optimization.
     */
    void dontUseTagPlugin();

    /**
     * Get the PluginContext for the parent of this custom tag.  NOTE:
     * The operations available for PluginContext so obtained is limited
     * to getPluginAttribute and setPluginAttribute, and queries (e.g.
     * isScriptless().  There should be no calls to generate*().
     * @return The pluginContext for the parent node.
     *         null if the parent is not a custom tag, or if the pluginConxt
     *         if not available (because useTagPlugin is false, e.g).
     */
    TagPluginContext getParentContext();

    /**
     * Associate the attribute with a value in the current tagplugin context.
     * The plugin attributes can be used for communication among tags that
     * must work together as a group.  See <c:when> for an example.
     */
    void setPluginAttribute(String attr, Object value);

    /**
     * Get the value of an attribute in the current tagplugin context.
     */
    Object getPluginAttribute(String attr);
}

