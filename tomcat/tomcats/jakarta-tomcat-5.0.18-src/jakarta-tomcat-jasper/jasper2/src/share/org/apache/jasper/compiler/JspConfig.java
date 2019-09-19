/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/compiler/JspConfig.java,v 1.13 2003/09/02 21:39:58 remm Exp $
 * $Revision: 1.13 $
 * $Date: 2003/09/02 21:39:58 $
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

import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jasper.JasperException;
import org.apache.jasper.xmlparser.ParserUtils;
import org.apache.jasper.xmlparser.TreeNode;

/**
 * Handles the jsp-config element in WEB_INF/web.xml.  This is used
 * for specifying the JSP configuration information on a JSP page
 *
 * @author Kin-man Chung
 */

public class JspConfig {

    private static final String WEB_XML = "/WEB-INF/web.xml";

    // Logger
    private static Log log = LogFactory.getLog(JspConfig.class);

    private Vector jspProperties = null;
    private ServletContext ctxt;
    private boolean initialized = false;

    private String defaultIsXml = null;		// unspecified
    private String defaultIsELIgnored = null;	// unspecified
    private String defaultIsScriptingInvalid = "false";
    private JspProperty defaultJspProperty;

    public JspConfig(ServletContext ctxt) {
	this.ctxt = ctxt;
    }

    private void processWebDotXml(ServletContext ctxt) throws JasperException {

	InputStream is = ctxt.getResourceAsStream(WEB_XML);
	if (is == null) {
	    // no web.xml
	    return;
	}

	ParserUtils pu = new ParserUtils();
	TreeNode webApp = pu.parseXMLDocument(WEB_XML, is);
	if (webApp == null || !"2.4".equals(webApp.findAttribute("version"))) {
	    defaultIsELIgnored = "true";
	    return;
	}
	TreeNode jspConfig = webApp.findChild("jsp-config");
	if (jspConfig == null) {
	    return;
	}

	jspProperties = new Vector();
	Iterator jspPropertyList = jspConfig.findChildren("jsp-property-group");
	while (jspPropertyList.hasNext()) {

	    TreeNode element = (TreeNode) jspPropertyList.next();
	    Iterator list = element.findChildren();

            Vector urlPatterns = new Vector();
	    String pageEncoding = null;
	    String scriptingInvalid = null;
	    String elIgnored = null;
	    String isXml = null;
	    Vector includePrelude = new Vector();
	    Vector includeCoda = new Vector();

	    while (list.hasNext()) {

		element = (TreeNode) list.next();
		String tname = element.getName();

		if ("url-pattern".equals(tname))
                    urlPatterns.addElement( element.getBody() );
		else if ("page-encoding".equals(tname))
		    pageEncoding = element.getBody();
		else if ("is-xml".equals(tname))
		    isXml = element.getBody();
		else if ("el-ignored".equals(tname))
		    elIgnored = element.getBody();
		else if ("scripting-invalid".equals(tname))
		    scriptingInvalid = element.getBody();
		else if ("include-prelude".equals(tname))
		    includePrelude.addElement(element.getBody());
		else if ("include-coda".equals(tname))
		    includeCoda.addElement(element.getBody());
	    }

             if (urlPatterns.size() == 0) {
                 continue;
             }
 
             // Add one JspPropertyGroup for each URL Pattern.  This makes
             // the matching logic easier.
             for( int p = 0; p < urlPatterns.size(); p++ ) {
                 String urlPattern = (String)urlPatterns.elementAt( p );
                 String path = null;
                 String extension = null;
 
                 if (urlPattern.indexOf('*') < 0) {
                     // Exact match
                     path = urlPattern;
                 } else {
                     int i = urlPattern.lastIndexOf('/');
                     String file;
                     if (i >= 0) {
                         path = urlPattern.substring(0,i+1);
                         file = urlPattern.substring(i+1);
                     } else {
                         file = urlPattern;
                     }
 
                     // pattern must be "*", or of the form "*.jsp"
                     if (file.equals("*")) {
                         extension = "*";
                     } else if (file.startsWith("*.")) {
                         extension = file.substring(file.indexOf('.')+1);
                     } else {
                         if (log.isWarnEnabled()) {
			     log.warn(Localizer.getMessage("jsp.warning.bad.urlpattern.propertygroup",
							   urlPattern));
			 }
                         continue;
                     }
                 }
 
                 JspProperty property = new JspProperty(isXml,
                                                        elIgnored,
                                                        scriptingInvalid,
                                                        pageEncoding,
                                                        includePrelude,
                                                        includeCoda);
                 JspPropertyGroup propertyGroup =
                     new JspPropertyGroup(path, extension, property);

                 jspProperties.addElement(propertyGroup);
             }
	}
    }

    private void init() throws JasperException {

	if (!initialized) {
	    processWebDotXml(ctxt);
	    defaultJspProperty = new JspProperty(defaultIsXml,
						 defaultIsELIgnored,
						 defaultIsScriptingInvalid,
						 null, null, null);
	    initialized = true;
	}
    }

    /**
     * Find a property that best matches the supplied resource.
     * @param uri the resource supplied.
     * @return a JspProperty indicating the best match, or some default.
     */
    public JspProperty findJspProperty(String uri) throws JasperException {

	init();

	// JSP Configuration settings do not apply to tag files	    
	if (jspProperties == null || uri.endsWith(".tag")
	        || uri.endsWith(".tagx")) {
	    return defaultJspProperty;
	}

	String uriPath = null;
	int index = uri.lastIndexOf('/');
	if (index >=0 ) {
	    uriPath = uri.substring(0, index+1);
	}
	String uriExtension = null;
	index = uri.lastIndexOf('.');
	if (index >=0) {
	    uriExtension = uri.substring(index+1);
	}

	Vector includePreludes = new Vector();
	Vector includeCodas = new Vector();

	JspPropertyGroup isXmlMatch = null;
	JspPropertyGroup elIgnoredMatch = null;
	JspPropertyGroup scriptingInvalidMatch = null;
	JspPropertyGroup pageEncodingMatch = null;

	Iterator iter = jspProperties.iterator();
	while (iter.hasNext()) {

	    JspPropertyGroup jpg = (JspPropertyGroup) iter.next();
	    JspProperty jp = jpg.getJspProperty();

             // (arrays will be the same length)
             String extension = jpg.getExtension();
             String path = jpg.getPath();
 
             if (extension == null) {
 
                 // exact match pattern: /a/foo.jsp
                 if (!uri.equals(path)) {
                     // not matched;
                     continue;
                 }
 
                 // Add include-preludes and include-codas
                 if (jp.getIncludePrelude() != null) {
                     includePreludes.addAll(jp.getIncludePrelude());
                 }
                 if (jp.getIncludeCoda() != null) {
                     includeCodas.addAll(jp.getIncludeCoda());
                 }
 
                 // For other attributes, keep the best match.
                 if (jp.isXml() != null) {
                     isXmlMatch = jpg;
                 }
                 if (jp.isELIgnored() != null) {
                     elIgnoredMatch = jpg;
                 }
                 if (jp.isScriptingInvalid() != null) {
                     scriptingInvalidMatch = jpg;
                 }
                 if (jp.getPageEncoding() != null) {
                     pageEncodingMatch = jpg;
                 }
             } else {
 
                 // Possible patterns are *, *.ext, /p/*, and /p/*.ext
 
                 if (path != null && !path.equals(uriPath)) {
                     // not matched
                     continue;
                 }
                 if (!extension.equals("*") &&
                                 !extension.equals(uriExtension)) {
                     // not matched
                     continue;
                 }
 
                 // We have a match
                 // Add include-preludes and include-codas
                 if (jp.getIncludePrelude() != null) {
                     includePreludes.addAll(jp.getIncludePrelude());
                 }
                 if (jp.getIncludeCoda() != null) {
                     includeCodas.addAll(jp.getIncludeCoda());
                 }
 
                 // If there is a previous match, and the current match is
                 // more restrictive, use the current match.
                 if (jp.isXml() != null &&
                         (isXmlMatch == null ||
                                 (isXmlMatch.getExtension() != null &&
                                  isXmlMatch.getExtension().equals("*")))) {
                         isXmlMatch = jpg;
                 }
                 if (jp.isELIgnored() != null &&
                         (elIgnoredMatch == null ||
                             (elIgnoredMatch.getExtension() != null &&
                              elIgnoredMatch.getExtension().equals("*")))) {
                     elIgnoredMatch = jpg;
                 }
                 if (jp.isScriptingInvalid() != null &&
                         (scriptingInvalidMatch == null ||
                             (scriptingInvalidMatch.getExtension() != null &&
                              scriptingInvalidMatch.getExtension().equals("*")))) {
                     scriptingInvalidMatch = jpg;
                 }
                 if (jp.getPageEncoding() != null &&
                         (pageEncodingMatch == null ||
                             (pageEncodingMatch.getExtension() != null &&
                              pageEncodingMatch.getExtension().equals("*")))) {
                     pageEncodingMatch = jpg;
                 }
             }
	}


	String isXml = defaultIsXml;
	String isELIgnored = defaultIsELIgnored;
	String isScriptingInvalid = defaultIsScriptingInvalid;
	String pageEncoding = null;

	if (isXmlMatch != null) {
	    isXml = isXmlMatch.getJspProperty().isXml();
	}
	if (elIgnoredMatch != null) {
	    isELIgnored = elIgnoredMatch.getJspProperty().isELIgnored();
	}
	if (scriptingInvalidMatch != null) {
	    isScriptingInvalid =
		scriptingInvalidMatch.getJspProperty().isScriptingInvalid();
	}
	if (pageEncodingMatch != null) {
	    pageEncoding = pageEncodingMatch.getJspProperty().getPageEncoding();
	}

	return new JspProperty(isXml, isELIgnored, isScriptingInvalid,
			       pageEncoding, includePreludes, includeCodas);
    }

    /**
     * To find out if an uri matches an url pattern in jsp config.  If so,
     * then the uri is a JSP page.  This is used primarily for jspc.
     */
    public boolean isJspPage(String uri) throws JasperException {

        init();
        if (jspProperties == null) {
            return false;
        }

        String uriPath = null;
        int index = uri.lastIndexOf('/');
        if (index >=0 ) {
            uriPath = uri.substring(0, index+1);
        }
        String uriExtension = null;
        index = uri.lastIndexOf('.');
        if (index >=0) {
            uriExtension = uri.substring(index+1);
        }

        Iterator iter = jspProperties.iterator();
        while (iter.hasNext()) {

            JspPropertyGroup jpg = (JspPropertyGroup) iter.next();
            JspProperty jp = jpg.getJspProperty();

            String extension = jpg.getExtension();
            String path = jpg.getPath();

            if (extension == null) {
                if (uri.equals(path)) {
                    // There is an exact match
                    return true;
                }
            } else {
                if ((path == null || path.equals(uriPath)) &&
                    (extension.equals("*") || extension.equals(uriExtension))) {
                    // Matches *, *.ext, /p/*, or /p/*.ext
                    return true;
                }
            }
        }
        return false;
    }

    static class JspPropertyGroup {
	private String path;
	private String extension;
	private JspProperty jspProperty;

	JspPropertyGroup(String path, String extension,
			 JspProperty jspProperty) {
	    this.path = path;
	    this.extension = extension;
	    this.jspProperty = jspProperty;
	}

	public String getPath() {
	    return path;
	}

	public String getExtension() {
	    return extension;
	}

	public JspProperty getJspProperty() {
	    return jspProperty;
	}
    }

    static public class JspProperty {

	private String isXml;
	private String elIgnored;
	private String scriptingInvalid;
	private String pageEncoding;
	private Vector includePrelude;
	private Vector includeCoda;

	public JspProperty(String isXml, String elIgnored,
		    String scriptingInvalid, String pageEncoding,
		    Vector includePrelude, Vector includeCoda) {

	    this.isXml = isXml;
	    this.elIgnored = elIgnored;
	    this.scriptingInvalid = scriptingInvalid;
	    this.pageEncoding = pageEncoding;
	    this.includePrelude = includePrelude;
	    this.includeCoda = includeCoda;
	}

	public String isXml() {
	    return isXml;
	}

	public String isELIgnored() {
	    return elIgnored;
	}

	public String isScriptingInvalid() {
	    return scriptingInvalid;
	}

	public String getPageEncoding() {
	    return pageEncoding;
	}

	public Vector getIncludePrelude() {
	    return includePrelude;
	}

	public Vector getIncludeCoda() {
	    return includeCoda;
	}
    }
}
