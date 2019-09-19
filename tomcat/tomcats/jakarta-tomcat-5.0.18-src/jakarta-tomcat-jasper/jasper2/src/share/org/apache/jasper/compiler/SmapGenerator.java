/*
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

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a source map (SMAP), which serves to associate lines
 * of the input JSP file(s) to lines in the generated servlet in the
 * final .class file, according to the JSR-045 spec.
 * 
 * @author Shawn Bayern
 */
public class SmapGenerator {

    //*********************************************************************
    // Overview

    /*
     * The SMAP syntax is reasonably straightforward.  The purpose of this
     * class is currently twofold:
     *  - to provide a simple but low-level Java interface to build
     *    a logical SMAP
     *  - to serialize this logical SMAP for eventual inclusion directly
     *    into a .class file.
     */


    //*********************************************************************
    // Private state

    private String outputFileName;
    private String defaultStratum = "Java";
    private List strata = new ArrayList();
    private List embedded = new ArrayList();
    private boolean doEmbedded = true;

    //*********************************************************************
    // Methods for adding mapping data

    /**
     * Sets the filename (without path information) for the generated
     * source file.  E.g., "foo$jsp.java".
     */
    public synchronized void setOutputFileName(String x) {
	outputFileName = x;
    }

    /**
     * Adds the given SmapStratum object, representing a Stratum with
     * logically associated FileSection and LineSection blocks, to
     * the current SmapGenerator.  If <tt>default</tt> is true, this
     * stratum is made the default stratum, overriding any previously
     * set default.
     *
     * @param stratum the SmapStratum object to add
     * @param defaultStratum if <tt>true</tt>, this SmapStratum is considered
     *                to represent the default SMAP stratum unless
     *                overwritten
     */
    public synchronized void addStratum(SmapStratum stratum,
					boolean defaultStratum) {
	strata.add(stratum);
	if (defaultStratum)
	    this.defaultStratum = stratum.getStratumName();
    }

    /**
     * Adds the given string as an embedded SMAP with the given stratum name.
     *
     * @param smap the SMAP to embed
     * @param stratumName the name of the stratum output by the compilation
     *                    that produced the <tt>smap</tt> to be embedded
     */
    public synchronized void addSmap(String smap, String stratumName) {
	embedded.add("*O " + stratumName + "\n"
		   + smap
		   + "*C " + stratumName + "\n");
    }

    /**
     * Instructs the SmapGenerator whether to actually print any embedded
     * SMAPs or not.  Intended for situations without an SMAP resolver.
     *
     * @param status If <tt>false</tt>, ignore any embedded SMAPs.
     */
    public void setDoEmbedded(boolean status) {
	doEmbedded = status;
    }

    //*********************************************************************
    // Methods for serializing the logical SMAP

    public synchronized String getString() {
	// check state and initialize buffer
	if (outputFileName == null)
	    throw new IllegalStateException();
        StringBuffer out = new StringBuffer();

	// start the SMAP
	out.append("SMAP\n");
	out.append(outputFileName + '\n');
	out.append(defaultStratum + '\n');

	// include embedded SMAPs
	if (doEmbedded) {
	    int nEmbedded = embedded.size();
	    for (int i = 0; i < nEmbedded; i++) {
	        out.append(embedded.get(i));
	    }
	}

	// print our StratumSections, FileSections, and LineSections
	int nStrata = strata.size();
	for (int i = 0; i < nStrata; i++) {
	    SmapStratum s = (SmapStratum) strata.get(i);
	    out.append(s.getString());
	}

	// end the SMAP
	out.append("*E\n");

	return out.toString();
    }

    public String toString() { return getString(); }

    //*********************************************************************
    // For testing (and as an example of use)...

    public static void main(String args[]) {
	SmapGenerator g = new SmapGenerator();
	g.setOutputFileName("foo.java");
	SmapStratum s = new SmapStratum("JSP");
	s.addFile("foo.jsp");
	s.addFile("bar.jsp", "/foo/foo/bar.jsp");
	s.addLineData(1, "foo.jsp", 1, 1, 1);
	s.addLineData(2, "foo.jsp", 1, 6, 1);
	s.addLineData(3, "foo.jsp", 2, 10, 5);
	s.addLineData(20, "bar.jsp", 1, 30, 1);
	g.addStratum(s, true);
	System.out.print(g);

	System.out.println("---");

	SmapGenerator embedded = new SmapGenerator();
	embedded.setOutputFileName("blargh.tier2");
	s = new SmapStratum("Tier2");
	s.addFile("1.tier2");
	s.addLineData(1, "1.tier2", 1, 1, 1);
	embedded.addStratum(s, true);
	g.addSmap(embedded.toString(), "JSP");
	System.out.println(g);
    }
}
