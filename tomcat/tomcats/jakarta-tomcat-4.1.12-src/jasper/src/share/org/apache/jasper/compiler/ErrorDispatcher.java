/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/compiler/ErrorDispatcher.java,v 1.2 2002/05/15 20:42:03 kinman Exp $
 * $Revision: 1.2 $
 * $Date: 2002/05/15 20:42:03 $
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
import java.text.MessageFormat;
import org.xml.sax.*;
import org.apache.jasper.JasperException;

/**
 * Class responsible for dispatching JSP parse and javac compilation errors
 * to the configured error handler.
 *
 * This class is also responsible for localizing any error codes before they
 * are passed on to the configured error handler.
 * 
 * In the case of a Java compilation error, the compiler error message is
 * parsed into an array of JavacErrorDetail instances, which is passed on to 
 * the configured error handler.
 *
 * @author Jan Luehe
 * @author Kin-man Chung
 */
public class ErrorDispatcher {

    private static final ResourceBundle bundle = ResourceBundle.getBundle(
        "org.apache.jasper.resources.messages");

    // Custom error handler
    private ErrorHandler errHandler;

    /*
     * Constructor.
     */
    ErrorDispatcher() {
	// XXX check web.xml for custom error handler
	errHandler = new DefaultErrorHandler(this);
    }

    //*********************************************************************
    // Package-scoped utility methods

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param errCode Error code
     */
    void jspError(String errCode) throws JasperException {
	dispatch(null, errCode, null, null);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param where Error location
     * @param errCode Error code
     */
    void jspError(Mark where, String errCode) throws JasperException {
	dispatch(where, errCode, null, null);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param n Node that caused the error
     * @param errCode Error code
     */
    void jspError(Node n, String errCode) throws JasperException {
	dispatch(n.getStart(), errCode, null, null);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param errCode Error code
     * @param arg Argument for parametric replacement
     */
    void jspError(String errCode, String arg) throws JasperException {
	dispatch(null, errCode, new Object[] {arg}, null);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param where Error location
     * @param errCode Error code
     * @param arg Argument for parametric replacement
     */
    void jspError(Mark where, String errCode, String arg)
	        throws JasperException {
	dispatch(where, errCode, new Object[] {arg}, null);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param n Node that caused the error
     * @param errCode Error code
     * @param arg Argument for parametric replacement
     */
    void jspError(Node n, String errCode, String arg)
	        throws JasperException {
	dispatch(n.getStart(), errCode, new Object[] {arg}, null);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param errCode Error code
     * @param arg1 First argument for parametric replacement
     * @param arg2 Second argument for parametric replacement
     */
    void jspError(String errCode, String arg1, String arg2)
	        throws JasperException {
	dispatch(null, errCode, new Object[] {arg1, arg2}, null);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param where Error location
     * @param errCode Error code
     * @param arg1 First argument for parametric replacement
     * @param arg2 Second argument for parametric replacement
     */
    void jspError(Mark where, String errCode, String arg1, String arg2)
	        throws JasperException {
	dispatch(where, errCode, new Object[] {arg1, arg2}, null);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param n Node that caused the error
     * @param errCode Error code
     * @param arg1 First argument for parametric replacement
     * @param arg2 Second argument for parametric replacement
     */
    void jspError(Node n, String errCode, String arg1, String arg2)
	        throws JasperException {
	dispatch(n.getStart(), errCode, new Object[] {arg1, arg2}, null);
    }

    /*
     * Dispatches the given parsing exception to the configured error handler.
     *
     * @param e Parsing exception
     */
    void jspError(Exception e) throws JasperException {
	dispatch(null, null, null, e);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param errCode Error code
     * @param arg Argument for parametric replacement
     * @param e Parsing exception
     */
    void jspError(String errCode, String arg, Exception e)
	        throws JasperException {
	dispatch(null, errCode, new Object[] {arg}, e);
    }

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param n Node that caused the error
     * @param errCode Error code
     * @param arg Argument for parametric replacement
     * @param e Parsing exception
     */
    void jspError(Node n, String errCode, String arg, Exception e)
	        throws JasperException {
	dispatch(n.getStart(), errCode, new Object[] {arg}, e);
    }

    /*
     * Dispatches the given compilation error to the configured error handler.
     *
     * @param errMsg Compilation error message that was generated by the
     * javac compiler
     * @param fname Name of Java source file whose compilation failed
     * @param page Node representation of JSP page from which the Java source
     * file was generated
     */
    void javacError(String errMsg, String fname, Node.Nodes page)
	        throws JasperException, IOException {
	JavacErrorDetail[] errDetails = parseJavacMessage(errMsg, fname, page);
	errHandler.javacError(errDetails);
    }

    /*
     * Returns the localized error message corresponding to the given error
     * code.
     *
     * If the given error code is not defined in the resource bundle for
     * localized error messages, it is used as the error message.
     *
     * @param errCode Error code to localize
     * 
     * @return Localized error message
     */
    String getString(String errCode) {
	String errMsg = errCode;
	try {
	    errMsg = bundle.getString(errCode);
	} catch (MissingResourceException e) {
	}
	return errMsg;
    }

    /* 
     * Returns the localized error message corresponding to the given error
     * code.
     *
     * If the given error code is not defined in the resource bundle for
     * localized error messages, it is used as the error message.
     *
     * @param errCode Error code to localize
     * @param arg Argument for parametric replacement
     *
     * @return Localized error message
     */
    String getString(String errCode, String arg) {
	return getString(errCode, new Object[] {arg});
    }

    /* 
     * Returns the localized error message corresponding to the given error
     * code.
     *
     * If the given error code is not defined in the resource bundle for
     * localized error messages, it is used as the error message.
     *
     * @param errCode Error code to localize
     * @param arg1 First argument for parametric replacement
     * @param arg2 Second argument for parametric replacement
     *
     * @return Localized error message
     */
    String getString(String errCode, String arg1, String arg2) {
	return getString(errCode, new Object[] {arg1, arg2});
    }

    /*
     * Returns the localized error message corresponding to the given error
     * code.
     *
     * If the given error code is not defined in the resource bundle for
     * localized error messages, it is used as the error message.
     *
     * @param errCode Error code to localize
     * @param args Arguments for parametric replacement
     *
     * @return Localized error message
     */
    String getString(String errCode, Object[] args) {
	String errMsg = errCode;
	try {
	    errMsg = bundle.getString(errCode);
	    if (args != null) {
		MessageFormat formatter = new MessageFormat(errMsg);
		errMsg = formatter.format(args);
	    }
	} catch (MissingResourceException e) {
	}
	
	return errMsg;
    }


    //*********************************************************************
    // Private utility methods

    /*
     * Dispatches the given JSP parse error to the configured error handler.
     *
     * The given error code is localized. If it is not found in the
     * resource bundle for localized error messages, it is used as the error
     * message.
     *
     * @param where Error location
     * @param errCode Error code
     * @param args Arguments for parametric replacement
     * @param e Parsing exception
     */
    private void dispatch(Mark where, String errCode, Object[] args,
			  Exception e) throws JasperException {
	String file = null;
	int line = -1;
	int column = -1;

	// Localize
	String errMsg = getString(errCode, args);

	// Get error location
	if (where != null) {
	    file = where.getFile();
	    line = where.getLineNumber();
	    column = where.getColumnNumber();
	} else if (e instanceof SAXParseException) {
	    file = ((SAXParseException) e).getSystemId();
	    line = ((SAXParseException) e).getLineNumber();
	    column = ((SAXParseException) e).getColumnNumber();
	}

	// Get nested exception
	Exception nestedEx = e;
	if (e instanceof SAXException) {
	    nestedEx = ((SAXException) e).getException();
	}

	errHandler.jspError(file, line, column, errMsg, nestedEx);
    }

    /*
     * Parses the given Java compilation error message, which may contain one
     * or more compilation errors, into an array of JavacErrorDetail instances.
     *
     * Each JavacErrorDetail instance contains the information about a single
     * compilation error.
     *
     * @param errMsg Compilation error message that was generated by the
     * javac compiler
     * @param fname Name of Java source file whose compilation failed
     * @param page Node representation of JSP page from which the Java source
     * file was generated
     *
     * @return Array of JavacErrorDetail instances corresponding to the
     * compilation errors
     */
    private JavacErrorDetail[] parseJavacMessage(String errMsg, String fname,
						 Node.Nodes page)
	        throws IOException, JasperException {

	Vector errVec = new Vector();
	StringBuffer partialErrMsg = new StringBuffer();
	int lineNum = -1;
	Node errNode = null;

        BufferedReader reader = new BufferedReader(new StringReader(errMsg));

        while (true) {
            String line = reader.readLine();
            if (line == null) {
		break;
	    }

            /*
	     * Error line number is delimited by set of colons.
	     * (Ignore colon following drive letter on Windows.)
	     * XXX Handle deprecation warnings that don't have line info
	     */
            int beginColon = line.indexOf(':', 2); 
            int endColon = line.indexOf(':', beginColon + 1);
            if ((beginColon >= 0) && (endColon >= 0)) {
		if (errNode != null) {
		    // add previous error to error vector
		    errVec.add(new JavacErrorDetail(
		        fname,
			lineNum,
			errNode.getStart().getFile(),
			errNode.getStart().getLineNumber(),
			partialErrMsg.toString()));
		    partialErrMsg = new StringBuffer();
		}
		String lineNumStr = line.substring(beginColon + 1, endColon);
                try {
                    lineNum = Integer.parseInt(lineNumStr);
                } catch (NumberFormatException e) {
                    // XXX
                }

		// Map servlet line number to corresponding node in JSP page
		ErrorVisitor errVisitor = new ErrorVisitor(lineNum);
		page.visit(errVisitor);
		errNode = errVisitor.getJspSourceNode();
                /* XXX Supress map exception to display the original error
		if (errNode == null) {
		    jspError("jsp.error.source.map", lineNumStr);
		}
                */
            }
	    partialErrMsg.append(line);
	    partialErrMsg.append('\n');
        }

        reader.close();

	// add last error to error vector
	String pageFile = null;
	int pageLine = -1;
	if (errNode != null) {
	    pageFile = errNode.getStart().getFile();
	    pageLine = errNode.getStart().getLineNumber();
	}
	errVec.add(new JavacErrorDetail(fname,
					lineNum,
					pageFile,
					pageLine,
					partialErrMsg.toString()));

	JavacErrorDetail[] errDetails = null;
	if (errVec.size() > 0) {
	    errDetails = new JavacErrorDetail[errVec.size()];
	    errVec.copyInto(errDetails);
	}

	return errDetails;
    }


    /*
     * Visitor responsible for mapping a line number in the generated servlet
     * source code to the corresponding JSP node.
     */
    static class ErrorVisitor extends Node.Visitor {

	// Java source line number to be mapped
	private int lineNum;

	/*
	 * JSP node whose Java source code range in the generated servlet
	 * contains the Java source line number to be mapped
	 */
	Node found;

	/*
	 * Constructor.
	 *
	 * @param lineNum Source line number in the generated servlet code
	 */
	public ErrorVisitor(int lineNum) {
	    this.lineNum = lineNum;
	}

	public void doVisit(Node n) throws JasperException {
	    if ((lineNum >= n.getBeginJavaLine())
		    && (lineNum < n.getEndJavaLine())) {
		found = n;
	    }
        }

	/*
	 * Gets the JSP node to which the source line number in the generated
	 * servlet code was mapped.
	 *
	 * @return JSP node to which the source line number in the generated
	 * servlet code was mapped
	 */
	public Node getJspSourceNode() {
	    return found;
	}
    }
}
