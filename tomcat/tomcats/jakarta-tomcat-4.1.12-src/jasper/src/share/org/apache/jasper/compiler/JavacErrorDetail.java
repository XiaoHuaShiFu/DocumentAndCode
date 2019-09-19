/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/compiler/JavacErrorDetail.java,v 1.1.1.1 2002/03/28 18:46:18 kinman Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/03/28 18:46:18 $
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

/**
 * Class providing details about a javac compilation error.
 *
 * @author Jan Luehe
 * @author Kin-man Chung
 */
public class JavacErrorDetail {

    private String javaFileName;
    private int javaLineNum;
    private String jspFileName;
    private int jspBeginLineNum;
    private String errMsg;

    /**
     * Constructor.
     *
     * @param javaFileName The name of the Java file in which the 
     * compilation error occurred
     * @param javaLineNum The compilation error line number
     * @param jspFileName The name of the JSP file from which the Java source
     * file was generated
     * @param jspBeginLineNum The start line number of the JSP element
     * responsible for the compilation error
     * @param errMsg The compilation error message
     */
    public JavacErrorDetail(String javaFileName,
			    int javaLineNum,
			    String jspFileName,
			    int jspBeginLineNum,
			    String errMsg) {
	this.javaFileName = javaFileName;
	this.javaLineNum = javaLineNum;
	this.jspFileName = jspFileName;
	this.jspBeginLineNum = jspBeginLineNum;
	this.errMsg = errMsg;
    }

    /**
     * Gets the name of the Java source file in which the compilation error
     * occurred.
     *
     * @return Java source file name
     */
    public String getJavaFileName() {
	return this.javaFileName;
    }

    /**
     * Gets the compilation error line number.
     * 
     * @return Compilation error line number
     */
    public int getJavaLineNumber() {
	return this.javaLineNum;
    }

    /**
     * Gets the name of the JSP file from which the Java source file was
     * generated.
     *
     * @return JSP file from which the Java source file was generated.
     */
    public String getJspFileName() {
	return this.jspFileName;
    }

    /**
     * Gets the start line number (in the JSP file) of the JSP element
     * responsible for the compilation error.
     *
     * @return Start line number of the JSP element responsible for the
     * compilation error
     */
    public int getJspBeginLineNumber() {
	return this.jspBeginLineNum;
    }

    /**
     * Gets the compilation error message.
     *
     * @return Compilation error message
     */
    public String getErrorMessage() {
	return this.errMsg;
    }
}
