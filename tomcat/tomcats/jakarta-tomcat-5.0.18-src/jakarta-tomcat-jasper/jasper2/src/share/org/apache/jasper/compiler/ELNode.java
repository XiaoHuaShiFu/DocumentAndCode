/* ========================================================================= *
 *                                                                           *
 *                 The Apache Software License,  Version 1.1                 *
 *                                                                           *
 *      Copyright (c) 1999, 2000, 2001  The Apache Software Foundation.      *
 *                           All rights reserved.                            *
 *                                                                           *
 * ========================================================================= *
 *                                                                           *
 * Redistribution and use in source and binary forms,  with or without modi- *
 * fication, are permitted provided that the following conditions are met:   *
 *                                                                           *
 * 1. Redistributions of source code  must retain the above copyright notice *
 *    notice, this list of conditions and the following disclaimer.          *
 *                                                                           *
 * 2. Redistributions  in binary  form  must  reproduce the  above copyright *
 *    notice,  this list of conditions  and the following  disclaimer in the *
 *    documentation and/or other materials provided with the distribution.   *
 *                                                                           *
 * 3. The end-user documentation  included with the redistribution,  if any, *
 *    must include the following acknowlegement:                             *
 *                                                                           *
 *       "This product includes  software developed  by the Apache  Software *
 *        Foundation <http://www.apache.org/>."                              *
 *                                                                           *
 *    Alternately, this acknowlegement may appear in the software itself, if *
 *    and wherever such third-party acknowlegements normally appear.         *
 *                                                                           *
 * 4. The names  "The  Jakarta  Project",  "Tomcat",  and  "Apache  Software *
 *    Foundation"  must not be used  to endorse or promote  products derived *
 *    from this  software without  prior  written  permission.  For  written *
 *    permission, please contact <apache@apache.org>.                        *
 *                                                                           *
 * 5. Products derived from this software may not be called "Apache" nor may *
 *    "Apache" appear in their names without prior written permission of the *
 *    Apache Software Foundation.                                            *
 *                                                                           *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES *
 * INCLUDING, BUT NOT LIMITED TO,  THE IMPLIED WARRANTIES OF MERCHANTABILITY *
 * AND FITNESS FOR  A PARTICULAR PURPOSE  ARE DISCLAIMED.  IN NO EVENT SHALL *
 * THE APACHE  SOFTWARE  FOUNDATION OR  ITS CONTRIBUTORS  BE LIABLE  FOR ANY *
 * DIRECT,  INDIRECT,   INCIDENTAL,  SPECIAL,  EXEMPLARY,  OR  CONSEQUENTIAL *
 * DAMAGES (INCLUDING,  BUT NOT LIMITED TO,  PROCUREMENT OF SUBSTITUTE GOODS *
 * OR SERVICES;  LOSS OF USE,  DATA,  OR PROFITS;  OR BUSINESS INTERRUPTION) *
 * HOWEVER CAUSED AND  ON ANY  THEORY  OF  LIABILITY,  WHETHER IN  CONTRACT, *
 * STRICT LIABILITY, OR TORT  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN *
 * ANY  WAY  OUT OF  THE  USE OF  THIS  SOFTWARE,  EVEN  IF  ADVISED  OF THE *
 * POSSIBILITY OF SUCH DAMAGE.                                               *
 *                                                                           *
 * ========================================================================= *
 *                                                                           *
 * This software  consists of voluntary  contributions made  by many indivi- *
 * duals on behalf of the  Apache Software Foundation.  For more information *
 * on the Apache Software Foundation, please see <http://www.apache.org/>.   *
 *                                                                           *
 * ========================================================================= */

package org.apache.jasper.compiler;

import java.util.*;
import javax.servlet.jsp.tagext.FunctionInfo;
import org.apache.jasper.JasperException;

/**
 * This class defines internal representation for an EL Expression
 *
 * It currently only defines functions.  It can be expanded to define
 * all the components of an EL expression, if need to.
 *
 * @author Kin-man Chung
 */

abstract class ELNode {

    abstract public void accept(Visitor v) throws JasperException;

    /**
     * Child classes
     */


    /**
     * Represents an EL expression: anything in ${ and }.
     */
    public static class Root extends ELNode {

	private ELNode.Nodes expr;

	Root(ELNode.Nodes expr) {
	    this.expr = expr;
	}

	public void accept(Visitor v) throws JasperException {
	    v.visit(this);
	}

	public ELNode.Nodes getExpression() {
	    return expr;
	}
    }

    /**
     * Represents text outside of EL expression.
     */
    public static class Text extends ELNode {

	private String text;

	Text(String text) {
	    this.text = text;
	}

	public void accept(Visitor v) throws JasperException {
	    v.visit(this);
	}

	public String getText() {
	    return text;
	}
    }

    /**
     * Represents anything in EL expression, other than functions, including
     * function arguments etc
     */
    public static class ELText extends ELNode {

	private String text;

	ELText(String text) {
	    this.text = text;
	}

	public void accept(Visitor v) throws JasperException {
	    v.visit(this);
	}

	public String getText() {
	    return text;
	}
    }

    /**
     * Represents a function
     * Currently only include the prefix and function name, but not its
     * arguments.
     */
    public static class Function extends ELNode {

	private String prefix;
	private String name;
	private String uri;
	private FunctionInfo functionInfo;
	private String methodName;
	private String[] parameters;

	Function(String prefix, String name) {
	    this.prefix = prefix;
	    this.name = name;
	}

	public void accept(Visitor v) throws JasperException {
	    v.visit(this);
	}

	public String getPrefix() {
	    return prefix;
	}

	public String getName() {
	    return name;
	}

	public void setUri(String uri) {
	    this.uri = uri;
	}

	public String getUri() {
	    return uri;
	}

	public void setFunctionInfo(FunctionInfo f) {
	    this.functionInfo = f;
	}

	public FunctionInfo getFunctionInfo() {
	    return functionInfo;
	}

	public void setMethodName(String methodName) {
	    this.methodName = methodName;
	}

	public String getMethodName() {
	    return methodName;
	}

	public void setParameters(String[] parameters) {
	    this.parameters = parameters;
	}

	public String[] getParameters() {
	    return parameters;
	}
    }

    /**
     * An ordered list of ELNode.
     */
    public static class Nodes {

	/* Name used for creating a map for the functions in this
	   EL expression, for communication to Generator.
	 */
	String mapName = null;	// The function map associated this EL
	private List list;

	public Nodes() {
	    list = new ArrayList();
	}

	public void add(ELNode en) {
	    list.add(en);
	}

	/**
	 * Visit the nodes in the list with the supplied visitor
	 * @param v The visitor used
	 */
	public void visit(Visitor v) throws JasperException {
	    Iterator iter = list.iterator();
	    while (iter.hasNext()) {
		ELNode n = (ELNode) iter.next();
		n.accept(v);
	    }
	}

	public Iterator iterator() {
	    return list.iterator();
	}

	public boolean isEmpty() {
	    return list.size() == 0;
	}

	/**
	 * @return true if the expression contains a ${...}
	 */
	public boolean containsEL() {
	    Iterator iter = list.iterator();
	    while (iter.hasNext()) {
		ELNode n = (ELNode) iter.next();
		if (n instanceof Root) {
		    return true;
		}
	    }
	    return false;
	}

	public void setMapName(String name) {
	    this.mapName = name;
	}

	public String getMapName() {
	    return mapName;
	}
    }

    /*
     * A visitor class for traversing ELNodes
     */
    public static class Visitor {

	public void visit(Root n) throws JasperException {
	    n.getExpression().visit(this);
	}

	public void visit(Function n) throws JasperException {
	}

	public void visit(Text n) throws JasperException {
	}

	public void visit(ELText n) throws JasperException {
	}
    }
}

