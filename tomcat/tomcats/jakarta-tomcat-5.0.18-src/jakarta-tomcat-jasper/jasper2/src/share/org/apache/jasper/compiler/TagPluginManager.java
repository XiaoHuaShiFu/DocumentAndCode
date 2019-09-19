/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/compiler/TagPluginManager.java,v 1.17 2003/05/01 16:42:12 luehe Exp $
 * $Revision: 1.17 $
 * $Date: 2003/05/01 16:42:12 $
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
import java.io.*;
import javax.servlet.ServletContext;

import org.apache.jasper.JasperException;
import org.apache.jasper.xmlparser.ParserUtils;
import org.apache.jasper.xmlparser.TreeNode;
import org.apache.jasper.compiler.tagplugin.TagPlugin;
import org.apache.jasper.compiler.tagplugin.TagPluginContext;

/**
 * Manages tag plugin optimizations.
 * @author Kin-man Chung
 */

public class TagPluginManager {

    private static final String TAG_PLUGINS_XML = "/WEB-INF/tagPlugins.xml";
    private static final String TAG_PLUGINS_ROOT_ELEM = "tag-plugins";

    private boolean initialized = false;
    private HashMap tagPlugins = null;
    private ServletContext ctxt;
    private PageInfo pageInfo;

    public TagPluginManager(ServletContext ctxt) {
	this.ctxt = ctxt;
    }

    public void apply(Node.Nodes page, ErrorDispatcher err, PageInfo pageInfo)
	    throws JasperException {

	init(err);
	if (tagPlugins == null || tagPlugins.size() == 0) {
	    return;
	}

	this.pageInfo = pageInfo;

        page.visit(new Node.Visitor() {
            public void visit(Node.CustomTag n)
                    throws JasperException {
                invokePlugin(n);
                visitBody(n);
            }
        });

    }
 
    private void init(ErrorDispatcher err) throws JasperException {
	if (initialized)
	    return;

	InputStream is = ctxt.getResourceAsStream(TAG_PLUGINS_XML);
	if (is == null)
	    return;

	TreeNode root = (new ParserUtils()).parseXMLDocument(TAG_PLUGINS_XML,
							     is);
	if (root == null) {
	    return;
	}

	if (!TAG_PLUGINS_ROOT_ELEM.equals(root.getName())) {
	    err.jspError("jsp.error.plugin.wrongRootElement", TAG_PLUGINS_XML,
			 TAG_PLUGINS_ROOT_ELEM);
	}

	tagPlugins = new HashMap();
	Iterator pluginList = root.findChildren("tag-plugin");
	while (pluginList.hasNext()) {
	    TreeNode pluginNode = (TreeNode) pluginList.next();
            TreeNode tagClassNode = pluginNode.findChild("tag-class");
	    if (tagClassNode == null) {
		// Error
		return;
	    }
	    String tagClass = tagClassNode.getBody().trim();
	    TreeNode pluginClassNode = pluginNode.findChild("plugin-class");
	    if (pluginClassNode == null) {
		// Error
		return;
	    }

	    String pluginClassStr = pluginClassNode.getBody();
	    TagPlugin tagPlugin = null;
	    try {
		Class pluginClass = Class.forName(pluginClassStr);
		tagPlugin = (TagPlugin) pluginClass.newInstance();
	    } catch (Exception e) {
		throw new JasperException(e);
	    }
	    if (tagPlugin == null) {
		return;
	    }
	    tagPlugins.put(tagClass, tagPlugin);
	}
	initialized = true;
    }

    /**
     * Invoke tag plugin for the given custom tag, if a plugin exists for 
     * the custom tag's tag handler.
     *
     * The given custom tag node will be manipulated by the plugin.
     */
    private void invokePlugin(Node.CustomTag n) {
	TagPlugin tagPlugin = (TagPlugin)
		tagPlugins.get(n.getTagHandlerClass().getName());
	if (tagPlugin == null) {
	    return;
	}

	TagPluginContext tagPluginContext = new TagPluginContextImpl(n, pageInfo);
	n.setTagPluginContext(tagPluginContext);
	tagPlugin.doTag(tagPluginContext);
    }

    static class TagPluginContextImpl implements TagPluginContext {
	private Node.CustomTag node;
	private Node.Nodes curNodes;
	private PageInfo pageInfo;
	private HashMap pluginAttributes;

	TagPluginContextImpl(Node.CustomTag n, PageInfo pageInfo) {
	    this.node = n;
	    this.pageInfo = pageInfo;
	    curNodes = new Node.Nodes();
	    n.setAtETag(curNodes);
	    curNodes = new Node.Nodes();
	    n.setAtSTag(curNodes);
	    n.setUseTagPlugin(true);
	    pluginAttributes = new HashMap();
	}

	public TagPluginContext getParentContext() {
	    Node parent = node.getParent();
	    if (! (parent instanceof Node.CustomTag)) {
		return null;
	    }
	    return ((Node.CustomTag) parent).getTagPluginContext();
	}

	public void setPluginAttribute(String key, Object value) {
	    pluginAttributes.put(key, value);
	}

	public Object getPluginAttribute(String key) {
	    return pluginAttributes.get(key);
	}

	public boolean isScriptless() {
	    return node.getChildInfo().isScriptless();
	}

	public boolean isConstantAttribute(String attribute) {
	    Node.JspAttribute attr = getNodeAttribute(attribute);
	    if (attr == null)
		return false;
	    return attr.isLiteral();
	}

	public String getConstantAttribute(String attribute) {
	    Node.JspAttribute attr = getNodeAttribute(attribute);
            if (attr == null)
		return null;
	    return attr.getValue();
	}

	public boolean isAttributeSpecified(String attribute) {
	    return getNodeAttribute(attribute) != null;
	}

	public String getTemporaryVariableName() {
	    return JspUtil.nextTemporaryVariableName();
	}

	public void generateImport(String imp) {
	    pageInfo.addImport(imp);
	}

	public void generateDeclaration(String id, String text) {
	    if (pageInfo.isPluginDeclared(id)) {
		return;
	    }
	    curNodes.add(new Node.Declaration(text, node.getStart(), null));
	}

	public void generateJavaSource(String sourceCode) {
	    curNodes.add(new Node.Scriptlet(sourceCode, node.getStart(),
					    null));
	}

	public void generateAttribute(String attributeName) {
	    curNodes.add(new Node.AttributeGenerator(node.getStart(),
						     attributeName,
						     node));
	}

	public void dontUseTagPlugin() {
	    node.setUseTagPlugin(false);
	}

	public void generateBody() {
	    // Since we'll generate the body anyway, this is really a nop, 
	    // except for the fact that it lets us put the Java sources the
	    // plugins produce in the correct order (w.r.t the body).
	    curNodes = node.getAtETag();
	}

	private Node.JspAttribute getNodeAttribute(String attribute) {
	    Node.JspAttribute[] attrs = node.getJspAttributes();
	    for (int i=0; attrs != null && i < attrs.length; i++) {
		if (attrs[i].getName().equals(attribute)) {
		    return attrs[i];
		}
	    }
	    return null;
	}
    }
}
