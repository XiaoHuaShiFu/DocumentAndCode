/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/compiler/ImplicitTagLibraryInfo.java,v 1.19 2003/02/13 20:58:42 luehe Exp $
 * $Revision: 1.19 $
 * $Date: 2003/02/13 20:58:42 $
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
import javax.servlet.jsp.tagext.FunctionInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagFileInfo;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.JasperException;

/**
 * Class responsible for generating an implicit tag library containing tag
 * handlers corresponding to the tag files in "/WEB-INF/tags/" or a 
 * subdirectory of it.
 *
 * @author Jan Luehe
 */
class ImplicitTagLibraryInfo extends TagLibraryInfo {

    private static final String WEB_INF_TAGS = "/WEB-INF/tags";
    private static final String TAG_FILE_SUFFIX = ".tag";
    private static final String TAGX_FILE_SUFFIX = ".tagx";
    private static final String TAGS_SHORTNAME = "tags";
    private static final String TLIB_VERSION = "1.0";
    private static final String JSP_VERSION = "2.0";

    // Maps tag names to tag file paths
    private Hashtable tagFileMap;

    private ParserController pc;
    private Vector vec;

    /**
     * Constructor.
     */
    public ImplicitTagLibraryInfo(JspCompilationContext ctxt,
				  ParserController pc,
				  String prefix,
				  String tagdir,
				  ErrorDispatcher err) throws JasperException {
        super(prefix, null);
	this.pc = pc;
	this.tagFileMap = new Hashtable();
	this.vec = new Vector();

        // Implicit tag libraries have no functions:
        this.functions = new FunctionInfo[0];

	tlibversion = TLIB_VERSION;
	jspversion = JSP_VERSION;

	if (!tagdir.startsWith(WEB_INF_TAGS)) {
	    err.jspError("jsp.error.invalid.tagdir", tagdir);
	}
	
	// Determine the value of the <short-name> subelement of the
	// "imaginary" <taglib> element
	if (tagdir.equals(WEB_INF_TAGS)
	        || tagdir.equals( WEB_INF_TAGS + "/")) {
	    shortname = TAGS_SHORTNAME;
	} else {
	    shortname = tagdir.substring(WEB_INF_TAGS.length());
	    shortname = shortname.replace('/', '-');
	}

	// Populate mapping of tag names to tag file paths
	Set dirList = ctxt.getResourcePaths(tagdir);
	if (dirList != null) {
	    Iterator it = dirList.iterator();
	    while (it.hasNext()) {
		String path = (String) it.next();
		if (path.endsWith(TAG_FILE_SUFFIX)
		        || path.endsWith(TAGX_FILE_SUFFIX)) {
		    /*
		     * Use the filename of the tag file, without the .tag or
		     * .tagx extension, respectively, as the <name> subelement
		     * of the "imaginary" <tag-file> element
		     */
		    String suffix = path.endsWith(TAG_FILE_SUFFIX) ?
			TAG_FILE_SUFFIX : TAGX_FILE_SUFFIX; 
		    String tagName = path.substring(path.lastIndexOf("/") + 1);
		    tagName = tagName.substring(0,
						tagName.lastIndexOf(suffix));
		    tagFileMap.put(tagName, path);
		}
	    }
	}
    }

    /**
     * Checks to see if the given tag name maps to a tag file path,
     * and if so, parses the corresponding tag file.
     *
     * @return The TagFileInfo corresponding to the given tag name, or null if
     * the given tag name is not implemented as a tag file
     */
    public TagFileInfo getTagFile(String shortName) {

	TagFileInfo tagFile = super.getTagFile(shortName);
	if (tagFile == null) {
	    String path = (String) tagFileMap.get(shortName);
	    if (path == null) {
		return null;
	    }

	    TagInfo tagInfo = null;
	    try {
		tagInfo = TagFileProcessor.parseTagFileDirectives(pc,
								  shortName,
								  path,
								  this);
	    } catch (JasperException je) {
		throw new RuntimeException(je.toString());
	    }

	    tagFile = new TagFileInfo(shortName, path, tagInfo);
	    vec.addElement(tagFile);

	    this.tagFiles = new TagFileInfo[vec.size()];
	    vec.copyInto(this.tagFiles);
	}

	return tagFile;
    }
}
