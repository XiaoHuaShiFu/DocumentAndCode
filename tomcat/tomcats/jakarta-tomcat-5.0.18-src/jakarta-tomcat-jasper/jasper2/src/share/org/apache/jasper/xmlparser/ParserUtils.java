/*
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
package org.apache.jasper.xmlparser;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jasper.Constants;
import org.apache.jasper.JasperException;
import org.apache.jasper.compiler.Localizer;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * XML parsing utilities for processing web application deployment
 * descriptor and tag library descriptor files.  FIXME - make these
 * use a separate class loader for the parser to be used.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.9 $ $Date: 2003/09/02 21:40:00 $
 */

public class ParserUtils {

    // Logger
    static Log log = LogFactory.getLog(ParserUtils.class);

    /**
     * An error handler for use when parsing XML documents.
     */
    static ErrorHandler errorHandler = new MyErrorHandler();

    /**
     * An entity resolver for use when parsing XML documents.
     */
    static EntityResolver entityResolver = new MyEntityResolver();

    // Turn off for JSP 2.0 until switch over to using xschema.
    public static boolean validating = false;


    // --------------------------------------------------------- Public Methods

    /**
     * Parse the specified XML document, and return a <code>TreeNode</code>
     * that corresponds to the root node of the document tree.
     *
     * @param uri URI of the XML document being parsed
     * @param is Input stream containing the deployment descriptor
     *
     * @exception JasperException if an input/output error occurs
     * @exception JasperException if a parsing error occurs
     */
    public TreeNode parseXMLDocument(String uri, InputStream is)
        throws JasperException {

        Document document = null;

        // Perform an XML parse of this document, via JAXP
        try {
            DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(validating);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(entityResolver);
            builder.setErrorHandler(errorHandler);
            document = builder.parse(is);
	} catch (ParserConfigurationException ex) {
            throw new JasperException
                (Localizer.getMessage("jsp.error.parse.xml", uri), ex);
	} catch (SAXParseException ex) {
            throw new JasperException
                (Localizer.getMessage("jsp.error.parse.xml.line",
				      uri,
				      Integer.toString(ex.getLineNumber()),
				      Integer.toString(ex.getColumnNumber())),
		 ex);
	} catch (SAXException sx) {
            throw new JasperException
                (Localizer.getMessage("jsp.error.parse.xml", uri), sx);
        } catch (IOException io) {
            throw new JasperException
                (Localizer.getMessage("jsp.error.parse.xml", uri), io);
	}

        // Convert the resulting document to a graph of TreeNodes
        return (convert(null, document.getDocumentElement()));
    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Create and return a TreeNode that corresponds to the specified Node,
     * including processing all of the attributes and children nodes.
     *
     * @param parent The parent TreeNode (if any) for the new TreeNode
     * @param node The XML document Node to be converted
     */
    protected TreeNode convert(TreeNode parent, Node node) {

        // Construct a new TreeNode for this node
        TreeNode treeNode = new TreeNode(node.getNodeName(), parent);

        // Convert all attributes of this node
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            int n = attributes.getLength();
            for (int i = 0; i < n; i++) {
                Node attribute = attributes.item(i);
                treeNode.addAttribute(attribute.getNodeName(),
                                      attribute.getNodeValue());
            }
        }

        // Create and attach all children of this node
        NodeList children = node.getChildNodes();
        if (children != null) {
            int n = children.getLength();
            for (int i = 0; i < n; i++) {
                Node child = children.item(i);
                if (child instanceof Comment)
                    continue;
                if (child instanceof Text) {
                    String body = ((Text) child).getData();
                    if (body != null) {
                        body = body.trim();
                        if (body.length() > 0)
                            treeNode.setBody(body);
                    }
                } else {
                    TreeNode treeChild = convert(treeNode, child);
                }
            }
        }
        
        // Return the completed TreeNode graph
        return (treeNode);
    }
}


// ------------------------------------------------------------ Private Classes

class MyEntityResolver implements EntityResolver {
    public InputSource resolveEntity(String publicId, String systemId)
	throws SAXException
    {
	for (int i=0; i<Constants.CACHED_DTD_PUBLIC_IDS.length; i++) {
	    String cachedDtdPublicId = Constants.CACHED_DTD_PUBLIC_IDS[i];
	    if (cachedDtdPublicId.equals(publicId)) {
		String resourcePath = Constants.CACHED_DTD_RESOURCE_PATHS[i];
		InputStream input =
		    this.getClass().getResourceAsStream(resourcePath);
		if (input == null) {
		    throw new SAXException(
                        Localizer.getMessage("jsp.error.internal.filenotfound",
					     resourcePath));
		}
		InputSource isrc = new InputSource(input);
		return isrc;
	    }
	}
        System.out.println("Resolve entity failed"  + publicId + " "
			   + systemId );
	ParserUtils.log.error(Localizer.getMessage("jsp.error.parse.xml.invalidPublicId",
						   publicId));
        return null;
    }
}

class MyErrorHandler implements ErrorHandler {
    public void warning(SAXParseException ex)
	throws SAXException
    {
        System.out.println("ParserUtils: warning " + ex );
	// We ignore warnings
    }

    public void error(SAXParseException ex)
	throws SAXException
    {
	throw ex;
    }

    public void fatalError(SAXParseException ex)
	throws SAXException
    {
	throw ex;
    }
}
