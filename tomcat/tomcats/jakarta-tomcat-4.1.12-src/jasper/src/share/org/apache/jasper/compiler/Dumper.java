/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/compiler/Dumper.java,v 1.1.1.1 2002/03/28 18:46:15 kinman Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/03/28 18:46:15 $
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

import org.xml.sax.Attributes;
import org.apache.jasper.JasperException;

public class Dumper {

    static class DumpVisitor extends Node.Visitor {
	private int indent = 0;

	private String getAttributes(Attributes attrs) {
	    if (attrs == null)
		return "";

	    StringBuffer buf = new StringBuffer();
	    for (int i=0; i < attrs.getLength(); i++) {
		buf.append(" " + attrs.getQName(i) + "=\""
			   + attrs.getValue(i) + "\"");
	    }
	    return buf.toString();
	}

	private void printString(String str) {
	    printIndent();
	    System.out.print(str);
	}

	private void printString(String prefix, char[] chars, String suffix) {
	    String str = null;
	    if (chars != null) {
		str = new String(chars);
	    }
	    printString(prefix, str, suffix);
	}
	     
	private void printString(String prefix, String str, String suffix) {
	    printIndent();
	    if (str != null) {
		System.out.print(prefix + str + suffix);
	    } else {
		System.out.print(prefix + suffix);
	    }
	}

	private void printAttributes(String prefix, Attributes attrs,
				     String suffix) {
	    printString(prefix, getAttributes(attrs), suffix);
	}

	private void dumpBody(Node n) throws JasperException {
	    Node.Nodes page = n.getBody();
	    if (page != null) {
//		indent++;
		page.visit(this);
//		indent--;
	    }
        }

        public void visit(Node.PageDirective n) throws JasperException {
	    printAttributes("<%@ page", n.getAttributes(), "%>");
        }

        public void visit(Node.TaglibDirective n) throws JasperException {
	    printAttributes("<%@ taglib", n.getAttributes(), "%>");
        }

        public void visit(Node.IncludeDirective n) throws JasperException {
	    printAttributes("<%@ include", n.getAttributes(), "%>");
	    dumpBody(n);
        }

        public void visit(Node.Comment n) throws JasperException {
	    printString("<%--", n.getText(), "--%>");
        }

        public void visit(Node.Declaration n) throws JasperException {
	    printString("<%!", n.getText(), "%>");
        }

        public void visit(Node.Expression n) throws JasperException {
	    printString("<%=", n.getText(), "%>");
        }

        public void visit(Node.Scriptlet n) throws JasperException {
	    printString("<%", n.getText(), "%>");
        }

        public void visit(Node.IncludeAction n) throws JasperException {
	    printAttributes("<jsp:include", n.getAttributes(), "/>");
	    dumpBody(n);
        }

        public void visit(Node.ForwardAction n) throws JasperException {
	    printAttributes("<jsp:forward", n.getAttributes(), ">");
	    dumpBody(n);
	    printString("</jsp:forward>");
        }

        public void visit(Node.GetProperty n) throws JasperException {
	    printAttributes("<jsp:getProperty", n.getAttributes(), "/>");
        }

        public void visit(Node.SetProperty n) throws JasperException {
	    printAttributes("<jsp:setProperty", n.getAttributes(), "/>");
        }

        public void visit(Node.UseBean n) throws JasperException {
	    printAttributes("<jsp:usebean", n.getAttributes(), ">");
	    dumpBody(n);
	    printString("</jsp:usebean>");
        }
	
        public void visit(Node.PlugIn n) throws JasperException {
	    printAttributes("<jsp:plugin", n.getAttributes(), ">");
	    dumpBody(n);
	    printString("</jsp:plugin>");
	}

        public void visit(Node.CustomTag n) throws JasperException {
	    printAttributes("<" + n.getName(), n.getAttributes(), ">");
	    dumpBody(n);
	    printString("</" + n.getName() + ">");
        }

	public void visit(Node.UninterpretedTag n) throws JasperException {
	    String tag = n.getName();
	    printAttributes("<"+tag, n.getAttributes(), ">");
	    dumpBody(n);
	    printString("</" + tag + ">");
        }

	public void visit(Node.TemplateText n) throws JasperException {
	    printString(new String(n.getText()));
	}

	private void printIndent() {
	    for (int i=0; i < indent; i++) {
		System.out.print("  ");
	    }
	}
    }

    public static void dump(Node n) {
	try {
	    n.accept(new DumpVisitor());	
	} catch (JasperException e) {
	    e.printStackTrace();
	}
    }

    public static void dump(Node.Nodes page) {
	try {
	    page.visit(new DumpVisitor());
	} catch (JasperException e) {
	    e.printStackTrace();
	}
    }
}

