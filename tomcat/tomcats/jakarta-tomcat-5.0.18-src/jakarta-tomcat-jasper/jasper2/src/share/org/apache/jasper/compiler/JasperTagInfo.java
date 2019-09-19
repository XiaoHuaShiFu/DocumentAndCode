/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/compiler/JasperTagInfo.java,v 1.1 2003/03/28 23:16:24 luehe Exp $
 * $Revision: 1.1 $
 * $Date: 2003/03/28 23:16:24 $
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

import javax.servlet.jsp.tagext.*;

/**
 * TagInfo extension used by tag handlers that are implemented via tag files.
 * This class provides access to the name of the Map used to store the
 * dynamic attribute names and values passed to the custom action invocation.
 * This information is used by the code generator.
 */
class JasperTagInfo extends TagInfo {

    private String dynamicAttrsMapName;

    public JasperTagInfo(String tagName,
			 String tagClassName,
			 String bodyContent,
			 String infoString,
			 TagLibraryInfo taglib,
			 TagExtraInfo tagExtraInfo,
			 TagAttributeInfo[] attributeInfo,
			 String displayName,
			 String smallIcon,
			 String largeIcon,
			 TagVariableInfo[] tvi,
			 String mapName) {

	super(tagName, tagClassName, bodyContent, infoString, taglib,
	      tagExtraInfo, attributeInfo, displayName, smallIcon, largeIcon,
	      tvi);
	this.dynamicAttrsMapName = mapName;
    }

    public String getDynamicAttributesMapName() {
	return dynamicAttrsMapName;
    }

    public boolean hasDynamicAttributes() {
        return dynamicAttrsMapName != null;
    }
}
