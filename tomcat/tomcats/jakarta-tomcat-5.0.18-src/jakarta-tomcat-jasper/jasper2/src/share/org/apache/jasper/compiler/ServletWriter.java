/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/compiler/ServletWriter.java,v 1.4 2003/09/02 21:39:59 remm Exp $
 * $Revision: 1.4 $
 * $Date: 2003/09/02 21:39:59 $
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

import java.io.IOException;
import java.io.PrintWriter;

/**
 * This is what is used to generate servlets. 
 *
 * @author Anil K. Vijendran
 * @author Kin-man Chung
 */
public class ServletWriter {
    public static int TAB_WIDTH = 2;
    public static String SPACES = "                              ";

    // Current indent level:
    private int indent = 0;
    private int virtual_indent = 0;

    // The sink writer:
    PrintWriter writer;
    
    // servlet line numbers start from 1
    private int javaLine = 1;


    public ServletWriter(PrintWriter writer) {
	this.writer = writer;
    }

    public void close() throws IOException {
	writer.close();
    }

    
    // -------------------- Access informations --------------------

    public int getJavaLine() {
        return javaLine;
    }


    // -------------------- Formatting --------------------

    public void pushIndent() {
	virtual_indent += TAB_WIDTH;
	if (virtual_indent >= 0 && virtual_indent <= SPACES.length())
	    indent = virtual_indent;
    }

    public void popIndent() {
	virtual_indent -= TAB_WIDTH;
	if (virtual_indent >= 0 && virtual_indent <= SPACES.length())
	    indent = virtual_indent;
    }

    /**
     * Print a standard comment for echo outputed chunk.
     * @param start The starting position of the JSP chunk being processed. 
     * @param stop  The ending position of the JSP chunk being processed. 
     */
    public void printComment(Mark start, Mark stop, char[] chars) {
        if (start != null && stop != null) {
            println("// from="+start);
            println("//   to="+stop);
        }
        
        if (chars != null)
            for(int i = 0; i < chars.length;) {
                printin();
                print("// ");
                while (chars[i] != '\n' && i < chars.length)
                    writer.print(chars[i++]);
            }
    }

    /**
     * Prints the given string followed by '\n'
     */
    public void println(String s) {
        javaLine++;
	writer.println(s);
    }

    /**
     * Prints a '\n'
     */
    public void println() {
        javaLine++;
	writer.println("");
    }

    /**
     * Prints the current indention
     */
    public void printin() {
	writer.print(SPACES.substring(0, indent));
    }

    /**
     * Prints the current indention, followed by the given string
     */
    public void printin(String s) {
	writer.print(SPACES.substring(0, indent));
	writer.print(s);
    }

    /**
     * Prints the current indention, and then the string, and a '\n'.
     */
    public void printil(String s) {
        javaLine++;
	writer.print(SPACES.substring(0, indent));
	writer.println(s);
    }

    /**
     * Prints the given char.
     *
     * Use println() to print a '\n'.
     */
    public void print(char c) {
	writer.print(c);
    }

    /**
     * Prints the given int.
     */
    public void print(int i) {
	writer.print(i);
    }

    /**
     * Prints the given string.
     *
     * The string must not contain any '\n', otherwise the line count will be
     * off.
     */
    public void print(String s) {
	writer.print(s);
    }

    /**
     * Prints the given string.
     *
     * If the string spans multiple lines, the line count will be adjusted
     * accordingly.
     */
    public void printMultiLn(String s) {
        int index = 0;

        // look for hidden newlines inside strings
        while ((index=s.indexOf('\n',index)) > -1 ) {
            javaLine++;
            index++;
        }

	writer.print(s);
    }
}
