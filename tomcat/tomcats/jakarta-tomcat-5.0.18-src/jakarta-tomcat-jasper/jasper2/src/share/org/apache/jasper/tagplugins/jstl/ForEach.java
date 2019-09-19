/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/tagplugins/jstl/ForEach.java,v 1.5 2003/02/13 19:46:11 kinman Exp $
 * $Revision: 1.5 $
 * $Date: 2003/02/13 19:46:11 $
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

package org.apache.jasper.tagplugins.jstl;

import org.apache.jasper.compiler.tagplugin.*;

public final class ForEach implements TagPlugin {

    private boolean hasVar, hasBegin, hasEnd, hasStep;

    public void doTag(TagPluginContext ctxt) {

	String index = null;

	boolean hasVarStatus = ctxt.isAttributeSpecified("varStatus");
	if (hasVarStatus) {
	    ctxt.dontUseTagPlugin();
	    return;
	}

	hasVar = ctxt.isAttributeSpecified("var");
	hasBegin = ctxt.isAttributeSpecified("begin");
	hasEnd = ctxt.isAttributeSpecified("end");
	hasStep = ctxt.isAttributeSpecified("step");

	boolean hasItems = ctxt.isAttributeSpecified("items");
	if (hasItems) {
	    doCollection(ctxt);
	    return;
	}

	// We must have a begin and end attributes
	index = ctxt.getTemporaryVariableName();
	ctxt.generateJavaSource("for (int " + index + " = ");
	ctxt.generateAttribute("begin");
	ctxt.generateJavaSource("; " + index + " <= ");
	ctxt.generateAttribute("end");
	if (hasStep) {
	    ctxt.generateJavaSource("; " + index + "+=");
	    ctxt.generateAttribute("step");
	    ctxt.generateJavaSource(") {");
	}
	else {
	    ctxt.generateJavaSource("; " + index + "++) {");
	}

	// If var is specified and the body contains an EL, then sycn
	// the var attribute
	if (hasVar /* && ctxt.hasEL() */) {
	    ctxt.generateJavaSource("pageContext.setAttribute(");
	    ctxt.generateAttribute("var");
	    ctxt.generateJavaSource(", String.valueOf(" + index + "));");
	}
	ctxt.generateBody();
	ctxt.generateJavaSource("}");
    }

    /**
     * Generate codes for Collections
     * The pseudo code is:
     */
    private void doCollection(TagPluginContext ctxt) {

	ctxt.generateImport("java.util.*");
	generateIterators(ctxt);

        String itemsV = ctxt.getTemporaryVariableName();
        ctxt.generateJavaSource("Object " + itemsV + "= ");
        ctxt.generateAttribute("items");
        ctxt.generateJavaSource(";");
	
	String indexV=null, beginV=null, endV=null, stepV=null;
	if (hasBegin) {
	    beginV = ctxt.getTemporaryVariableName();
	    ctxt.generateJavaSource("int " + beginV + " = ");
	    ctxt.generateAttribute("begin");
	    ctxt.generateJavaSource(";");
	}
	if (hasEnd) {
	    indexV = ctxt.getTemporaryVariableName();
	    ctxt.generateJavaSource("int " + indexV + " = 0;");
	    endV = ctxt.getTemporaryVariableName();
	    ctxt.generateJavaSource("int " + endV + " = ");
	    ctxt.generateAttribute("end");
	    ctxt.generateJavaSource(";");
	}
	if (hasStep) {
	    stepV = ctxt.getTemporaryVariableName();
	    ctxt.generateJavaSource("int " + stepV + " = ");
	    ctxt.generateAttribute("step");
	    ctxt.generateJavaSource(";");
	}

        String iterV = ctxt.getTemporaryVariableName();
        ctxt.generateJavaSource("Iterator " + iterV + " = null;");
	// Object[]
	ctxt.generateJavaSource("if (" + itemsV + " instanceof Object[])");
	ctxt.generateJavaSource(iterV + "=toIterator((Object[])" + itemsV + ");");
	// boolean[]
	ctxt.generateJavaSource("else if (" + itemsV + " instanceof boolean[])");
	ctxt.generateJavaSource(iterV + "=toIterator((boolean[])" + itemsV + ");");
	// byte[]
	ctxt.generateJavaSource("else if (" + itemsV + " instanceof byte[])");
	ctxt.generateJavaSource(iterV + "=toIterator((byte[])" + itemsV + ");");
	// char[]
	ctxt.generateJavaSource("else if (" + itemsV + " instanceof char[])");
	ctxt.generateJavaSource(iterV + "=toIterator((char[])" + itemsV + ");");
	// short[]
	ctxt.generateJavaSource("else if (" + itemsV + " instanceof short[])");
	ctxt.generateJavaSource(iterV + "=toIterator((short[])" + itemsV + ");");
	// int[]
	ctxt.generateJavaSource("else if (" + itemsV + " instanceof int[])");
	ctxt.generateJavaSource(iterV + "=toIterator((int[])" + itemsV + ");");
	// long[]
	ctxt.generateJavaSource("else if (" + itemsV + " instanceof long[])");
	ctxt.generateJavaSource(iterV + "=toIterator((long[])" + itemsV + ");");
	// float[]
	ctxt.generateJavaSource("else if (" + itemsV + " instanceof float[])");
	ctxt.generateJavaSource(iterV + "=toIterator((float[])" + itemsV + ");");
	// double[]
	ctxt.generateJavaSource("else if (" + itemsV + " instanceof double[])");
	ctxt.generateJavaSource(iterV + "=toIterator((double[])" + itemsV + ");");

        ctxt.generateJavaSource("else if (" + itemsV + " instanceof Collection)");
        ctxt.generateJavaSource(iterV + "=((Collection)" + itemsV + ").iterator();");

	if (hasBegin) {
            String tV = ctxt.getTemporaryVariableName();
	    ctxt.generateJavaSource("for (int " + tV + "=" + beginV + ";" +
			tV + ">0 && " + iterV + ".hasNext(); " +
			tV + "--)");
	    ctxt.generateJavaSource(iterV + ".next();");
	}

	ctxt.generateJavaSource("while (" + iterV + ".hasNext()){");
	if (hasVar) {
	    ctxt.generateJavaSource("pageContext.setAttribute(");
	    ctxt.generateAttribute("var");
	    ctxt.generateJavaSource(", " + iterV + ".next());");
	}

	ctxt.generateBody();

	if (hasStep) {
	    String tV = ctxt.getTemporaryVariableName();
	    ctxt.generateJavaSource("for (int " + tV + "=" + stepV + "-1;" +
			tV + ">0 && " + iterV + ".hasNext(); " +
			tV + "--)");
	    ctxt.generateJavaSource(iterV + ".next();");
	}
	if (hasEnd) {
	    if (hasStep) {
		ctxt.generateJavaSource(indexV + "+=" + stepV + ";");
	    }
	    else {
		ctxt.generateJavaSource(indexV + "++;");
	    }
	    if (hasBegin) {
		ctxt.generateJavaSource("if(" + beginV + "+" + indexV +
			">"+ endV + ")");
	    }
	    else {
		ctxt.generateJavaSource("if(" + indexV + ">" + endV + ")");
	    }
	    ctxt.generateJavaSource("break;");
	}
	ctxt.generateJavaSource("}");	// while
    }

    /**
     * Generate iterators for data types supported in items
     */
    private void generateIterators(TagPluginContext ctxt) {

	// Object[]
	ctxt.generateDeclaration("ObjectArrayIterator", 
	    "private Iterator toIterator(final Object[] a){\n" +
	    "  return (new Iterator() {\n" +
	    "    int index=0;\n" +
	    "    public boolean hasNext() {\n" +
	    "      return index < a.length;}\n" +
	    "    public Object next() {\n" +
	    "      return a[index++];}\n" +
	    "    public void remove() {}\n" +
	    "  });\n" +
	    "}"
	);

	// boolean[]
	ctxt.generateDeclaration("booleanArrayIterator", 
	    "private Iterator toIterator(final boolean[] a){\n" +
	    "  return (new Iterator() {\n" +
	    "    int index=0;\n" +
	    "    public boolean hasNext() {\n" +
	    "      return index < a.length;}\n" +
	    "    public Object next() {\n" +
	    "      return new Boolean(a[index++]);}\n" +
	    "    public void remove() {}\n" +
	    "  });\n" +
	    "}"
	);

	// byte[]
	ctxt.generateDeclaration("byteArrayIterator", 
	    "private Iterator toIterator(final byte[] a){\n" +
	    "  return (new Iterator() {\n" +
	    "    int index=0;\n" +
	    "    public boolean hasNext() {\n" +
	    "      return index < a.length;}\n" +
	    "    public Object next() {\n" +
	    "      return new Byte(a[index++]);}\n" +
	    "    public void remove() {}\n" +
	    "  });\n" +
	    "}"
	);

	// char[]
	ctxt.generateDeclaration("charArrayIterator", 
	    "private Iterator toIterator(final char[] a){\n" +
	    "  return (new Iterator() {\n" +
	    "    int index=0;\n" +
	    "    public boolean hasNext() {\n" +
	    "      return index < a.length;}\n" +
	    "    public Object next() {\n" +
	    "      return new Character(a[index++]);}\n" +
	    "    public void remove() {}\n" +
	    "  });\n" +
	    "}"
	);

	// short[]
	ctxt.generateDeclaration("shortArrayIterator", 
	    "private Iterator toIterator(final short[] a){\n" +
	    "  return (new Iterator() {\n" +
	    "    int index=0;\n" +
	    "    public boolean hasNext() {\n" +
	    "      return index < a.length;}\n" +
	    "    public Object next() {\n" +
	    "      return new Short(a[index++]);}\n" +
	    "    public void remove() {}\n" +
	    "  });\n" +
	    "}"
	);

	// int[]
	ctxt.generateDeclaration("intArrayIterator", 
	    "private Iterator toIterator(final int[] a){\n" +
	    "  return (new Iterator() {\n" +
	    "    int index=0;\n" +
	    "    public boolean hasNext() {\n" +
	    "      return index < a.length;}\n" +
	    "    public Object next() {\n" +
	    "      return new Integer(a[index++]);}\n" +
	    "    public void remove() {}\n" +
	    "  });\n" +
	    "}"
	);

	// long[]
	ctxt.generateDeclaration("longArrayIterator", 
	    "private Iterator toIterator(final long[] a){\n" +
	    "  return (new Iterator() {\n" +
	    "    int index=0;\n" +
	    "    public boolean hasNext() {\n" +
	    "      return index < a.length;}\n" +
	    "    public Object next() {\n" +
	    "      return new Long(a[index++]);}\n" +
	    "    public void remove() {}\n" +
	    "  });\n" +
	    "}"
	);

	// float[]
	ctxt.generateDeclaration("floatArrayIterator",
	    "private Iterator toIterator(final float[] a){\n" +
	    "  return (new Iterator() {\n" +
	    "    int index=0;\n" +
	    "    public boolean hasNext() {\n" +
	    "      return index < a.length;}\n" +
	    "    public Object next() {\n" +
	    "      return new Float(a[index++]);}\n" +
	    "    public void remove() {}\n" +
	    "  });\n" +
	    "}"
	);

	// double[]
	ctxt.generateDeclaration("doubleArrayIterator",
	    "private Iterator toIterator(final double[] a){\n" +
	    "  return (new Iterator() {\n" +
	    "    int index=0;\n" +
	    "    public boolean hasNext() {\n" +
	    "      return index < a.length;}\n" +
	    "    public Object next() {\n" +
	    "      return new Double(a[index++]);}\n" +
	    "    public void remove() {}\n" +
	    "  });\n" +
	    "}"
	);
    }
}
