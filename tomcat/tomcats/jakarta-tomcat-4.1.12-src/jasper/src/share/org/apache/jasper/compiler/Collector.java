/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/
compiler/Generator.java,v 1.16 2002/05/24 23:57:42 kinman Exp $
 * $Revision: 1.2 $
 * $Date: 2002/06/13 22:56:11 $
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

import org.apache.jasper.JasperException;

/**
 * Collect info about the page and nodes, and make them availabe through
 * the PageInfo object.
 *
 * @author Kin-man Chung
 */

public class Collector {

    /**
     * A visitor for collection info on the page
     * Info collected so far:
     *   Maximum tag nestings.
     *   Whether a page or a tag element (and its body) contains any scripting
     *       elements.
     */
    static class CollectVisitor extends Node.Visitor {

        private int maxTagNesting = 0;
        private int curTagNesting = 0;
	private boolean scriptingElementSeen = false;
	private boolean usebeanSeen = false;
	private boolean includeActionSeen = false;
	private boolean setPropertySeen = false;
	private boolean hasScriptingVars = false;

	public void visit(Node.ParamAction n) throws JasperException {
	    if (n.getValue().isExpression()) {
		scriptingElementSeen = true;
	    }
	}

	public void visit(Node.IncludeAction n) throws JasperException {
	    if (n.getPage().isExpression()) {
		scriptingElementSeen = true;
	    }
	    includeActionSeen = true;
            visitBody(n);
	}

	public void visit(Node.ForwardAction n) throws JasperException {
	    if (n.getPage().isExpression()) {
		scriptingElementSeen = true;
	    }
            visitBody(n);
	}

	public void visit(Node.SetProperty n) throws JasperException {
	    if (n.getValue() != null && n.getValue().isExpression()) {
		scriptingElementSeen = true;
	    }
	    setPropertySeen = true;
	}

	public void visit(Node.UseBean n) throws JasperException {
	    if (n.getBeanName() != null && n.getBeanName().isExpression()) {
		scriptingElementSeen = true;
	    }
	    usebeanSeen = true;
            visitBody(n);
	}

	public void visit(Node.PlugIn n) throws JasperException {
	    if (n.getHeight() != null && n.getHeight().isExpression()) {
		scriptingElementSeen = true;
	    }
	    if (n.getWidth() != null && n.getWidth().isExpression()) {
		scriptingElementSeen = true;
	    }
            visitBody(n);
	}

        public void visit(Node.CustomTag n) throws JasperException {

            curTagNesting++;
            if (curTagNesting > maxTagNesting) {
                maxTagNesting = curTagNesting;
            }

	    // save values collected so far
	    boolean scriptingElementSeenSave = scriptingElementSeen;
	    scriptingElementSeen = false;
	    boolean usebeanSeenSave = usebeanSeen;
	    usebeanSeen = false;
	    boolean includeActionSeenSave = includeActionSeen;
	    includeActionSeen = false;
	    boolean setPropertySeenSave = setPropertySeen;
	    setPropertySeen = false;
	    boolean hasScriptingVarsSave = hasScriptingVars;
	    hasScriptingVars = false;

	    // Scan attribute list for expressions
	    Node.JspAttribute[] attrs = n.getJspAttributes();
	    for (int i = 0; i < attrs.length; i++) {
		if (attrs[i].isExpression()) {
		    scriptingElementSeen = true;
		    break;
		}
	    }

            visitBody(n);

	    if (!hasScriptingVars) {
		// For some reason, varInfos is null when var is not defined
		// in TEI, but tagVarInfos is empty array when var is not
		// defined in tld.
		hasScriptingVars = n.getVariableInfos() != null || 
			(n.getTagVariableInfos() != null
			 && n.getTagVariableInfos().length > 0);
	    }

	    // Record if the tag element and its body contains any scriptlet.
	    n.setScriptless(! scriptingElementSeen);
	    n.setHasUsebean(usebeanSeen);
	    n.setHasIncludeAction(includeActionSeen);
	    n.setHasSetProperty(setPropertySeen);
	    n.setHasScriptingVars(hasScriptingVars);

	    // Propagate value of scriptingElementSeen up.
	    scriptingElementSeen = scriptingElementSeen || scriptingElementSeenSave;
	    usebeanSeen = usebeanSeen || usebeanSeenSave;
	    setPropertySeen = setPropertySeen || setPropertySeenSave;
	    includeActionSeen = includeActionSeen || includeActionSeenSave;
	    hasScriptingVars = hasScriptingVars || hasScriptingVarsSave;

            curTagNesting--;
        }

	public void visit(Node.Declaration n) throws JasperException {
	    scriptingElementSeen = true;
	}

	public void visit(Node.Expression n) throws JasperException {
	    scriptingElementSeen = true;
	}

	public void visit(Node.Scriptlet n) throws JasperException {
	    scriptingElementSeen = true;
	}

        public void updatePageInfo(PageInfo pageInfo) {
            pageInfo.setMaxTagNesting(maxTagNesting);
	    pageInfo.setScriptless(! scriptingElementSeen);
        }
    }

    public static void collect(Compiler compiler, Node.Nodes page)
		throws JasperException {

	CollectVisitor collectVisitor = new CollectVisitor();
        page.visit(collectVisitor);
        collectVisitor.updatePageInfo(compiler.getPageInfo());

    }
}

