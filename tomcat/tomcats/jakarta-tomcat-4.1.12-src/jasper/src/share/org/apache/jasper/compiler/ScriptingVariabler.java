/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/compiler/ScriptingVariabler.java,v 1.1.2.3 2002/09/10 00:36:24 luehe Exp $
 * $Revision: 1.1.2.3 $
 * $Date: 2002/09/10 00:36:24 $
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
import javax.servlet.jsp.tagext.*;
import org.apache.jasper.JasperException;

/**
 * Class responsible for determining the scripting variables that every
 * custom action needs to declare.
 *
 * @author Jan Luehe
 */
public class ScriptingVariabler {

    private static final Integer MAX_SCOPE = new Integer(Integer.MAX_VALUE);

    /*
     * Assigns an identifier (of type integer) to every custom tag, in order
     * to help identify, for every custom tag, the scripting variables that it
     * needs to declare.
     */
    static class CustomTagCounter extends Node.Visitor {

	private int count;
	private Node.CustomTag parent;

	public void visit(Node.CustomTag n) throws JasperException {
	    n.setCustomTagParent(parent);
	    Node.CustomTag tmpParent = parent;
	    parent = n;
	    visitBody(n);
	    parent = tmpParent;
	    n.setNumCount(new Integer(count++));
	}
    }

    /*
     * For every custom tag, determines the scripting variables it needs to
     * declare. 
     */
    static class ScriptingVariableVisitor extends Node.Visitor {

	private Hashtable scriptVars;

	public ScriptingVariableVisitor() {
	    scriptVars = new Hashtable();
	}

	public void visit(Node.CustomTag n) throws JasperException {
	    setScriptingVars(n, VariableInfo.AT_BEGIN);
	    setScriptingVars(n, VariableInfo.NESTED);
	    visitBody(n);
	    setScriptingVars(n, VariableInfo.AT_END);
	}

	private void setScriptingVars(Node.CustomTag n, int scope) {

	    TagVariableInfo[] tagVarInfos = n.getTagVariableInfos();
	    VariableInfo[] varInfos = n.getVariableInfos();
	    if (tagVarInfos == null && varInfos == null) {
		return;
	    }

	    Vector vec = new Vector();

	    Integer ownRange = null;
	    if (scope == VariableInfo.AT_BEGIN 
		    || scope == VariableInfo.AT_END) {
		Node.CustomTag parent = n.getCustomTagParent();
		if (parent == null)
		    ownRange = MAX_SCOPE;
		else
		    ownRange = parent.getNumCount();
	    } else {
		// NESTED
		ownRange = n.getNumCount();
	    }

	    if (varInfos != null) {
		for (int i=0; i<varInfos.length; i++) {
		    if (varInfos[i].getScope() != scope
			    || !varInfos[i].getDeclare()) {
			continue;
		    }
		    String varName = varInfos[i].getVarName();
		    
		    Integer currentRange = (Integer) scriptVars.get(varName);
		    if (currentRange == null
			    || ownRange.compareTo(currentRange) > 0) {
			scriptVars.put(varName, ownRange);
			vec.add(varInfos[i]);
		    }
		}
	    } else {
		for (int i=0; i<tagVarInfos.length; i++) {
		    if (tagVarInfos[i].getScope() != scope
			    || !tagVarInfos[i].getDeclare()) {
			continue;
		    }
		    String varName = tagVarInfos[i].getNameGiven();
		    if (varName == null) {
			varName = n.getTagData().getAttributeString(
		                        tagVarInfos[i].getNameFromAttribute());
		    }

		    Integer currentRange = (Integer) scriptVars.get(varName);
		    if (currentRange == null
			    || ownRange.compareTo(currentRange) > 0) {
			scriptVars.put(varName, ownRange);
			vec.add(tagVarInfos[i]);
		    }
		}
	    }

	    n.setScriptingVars(vec, scope);
	}
    }

    public static void set(Node.Nodes page) throws JasperException {
	page.visit(new CustomTagCounter());
	page.visit(new ScriptingVariableVisitor());
    }
}
