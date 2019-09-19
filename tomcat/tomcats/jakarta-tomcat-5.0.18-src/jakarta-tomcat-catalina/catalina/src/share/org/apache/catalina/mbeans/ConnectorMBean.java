/*l
 * $Header: /home/cvs/jakarta-tomcat-catalina/catalina/src/share/org/apache/catalina/mbeans/ConnectorMBean.java,v 1.3 2003/09/02 21:22:02 remm Exp $
 * $Revision: 1.3 $
 * $Date: 2003/09/02 21:22:02 $
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
 * [Additional notices, if required by prior licensing conditions]
 *
 */

package org.apache.catalina.mbeans;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.InvalidTargetObjectTypeException;

import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.tomcat5.CoyoteConnector;
import org.apache.tomcat.util.IntrospectionUtils;


/**
 * <p>A <strong>ModelMBean</strong> implementation for the
 * <code>org.apache.coyote.tomcat5.CoyoteConnector</code> component.</p>
 *
 * @author Amy Roh
 * @version $Revision: 1.3 $ $Date: 2003/09/02 21:22:02 $
 */

public class ConnectorMBean extends ClassNameMBean {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a <code>ModelMBean</code> with default
     * <code>ModelMBeanInfo</code> information.
     *
     * @exception MBeanException if the initializer of an object
     *  throws an exception
     * @exception RuntimeOperationsException if an IllegalArgumentException
     *  occurs
     */
    public ConnectorMBean()
        throws MBeanException, RuntimeOperationsException {

        super();

    }


    // ------------------------------------------------------------- Attributes


    /**
     * Obtain and return the value of a specific attribute of this MBean.
     *
     * @param name Name of the requested attribute
     *
     * @exception AttributeNotFoundException if this attribute is not
     *  supported by this MBean
     * @exception MBeanException if the initializer of an object
     *  throws an exception
     * @exception ReflectionException if a Java reflection exception
     *  occurs when invoking the getter
     */
    public Object getAttribute(String name)
        throws AttributeNotFoundException, MBeanException,
        ReflectionException {
		
 	Object attribute = null;
        // Validate the input parameters
        if (name == null)
            throw new RuntimeOperationsException
                (new IllegalArgumentException("Attribute name is null"),
                 "Attribute name is null");
		 
        CoyoteConnector connector = null;
	try {
	    connector = (CoyoteConnector) getManagedResource();
	} catch (InstanceNotFoundException e) {
	    throw new MBeanException(e);
	} catch (InvalidTargetObjectTypeException e) {
	   throw new MBeanException(e);
        } 	    
	
	if (("algorithm").equals(name) || ("keystoreType").equals(name) ||
            ("maxThreads").equals(name) || ("maxSpareThreads").equals(name) ||
            ("minSpareThreads").equals(name)) {
                
            if (("keystoreType").equals(name)) {
                name = "keyType";
            }
                
            ProtocolHandler protocolHandler = connector.getProtocolHandler();
	    /* check the Protocol first, since JkCoyote has an independent
             * configure method.
             */
            try {
                if( protocolHandler != null ) {
                    attribute = IntrospectionUtils.getAttribute(protocolHandler, name);
                }
            } catch (Exception e) {
                throw new MBeanException(e);
            }
            //if( attribute == null ) {
            //    attribute = connector.getProperty(name);
            //}
	} else {
	    attribute = super.getAttribute(name);
	}
	
        return attribute;

    }

    
    /**
     * Set the value of a specific attribute of this MBean.
     *
     * @param attribute The identification of the attribute to be set
     *  and the new value
     *
     * @exception AttributeNotFoundException if this attribute is not
     *  supported by this MBean
     * @exception MBeanException if the initializer of an object
     *  throws an exception
     * @exception ReflectionException if a Java reflection exception
     *  occurs when invoking the getter
     */
     public void setAttribute(Attribute attribute)
        throws AttributeNotFoundException, MBeanException,
        ReflectionException {

        // Validate the input parameters
        if (attribute == null)
            throw new RuntimeOperationsException
                (new IllegalArgumentException("Attribute is null"),
                 "Attribute is null");
        String name = attribute.getName();
        Object value = attribute.getValue();
        if (name == null)
            throw new RuntimeOperationsException
                (new IllegalArgumentException("Attribute name is null"),
                 "Attribute name is null"); 
		 
        CoyoteConnector connector = null;
	try {
	    connector = (CoyoteConnector) getManagedResource();
	} catch (InstanceNotFoundException e) {
	    throw new MBeanException(e);
	} catch (InvalidTargetObjectTypeException e) {
	   throw new MBeanException(e);
        } 	    
	
        if (("algorithm").equals(name) || ("keystoreType").equals(name) ||
            ("maxThreads").equals(name) || ("maxSpareThreads").equals(name) ||
            ("minSpareThreads").equals(name)) {
                
            if (("keystoreType").equals(name)) {
                name = "keyType";
            }
            
            ProtocolHandler protocolHandler = connector.getProtocolHandler();
	    /* check the Protocol first, since JkCoyote has an independent
             * configure method.
             */
            try {
                if( protocolHandler != null ) {
                    IntrospectionUtils.setAttribute(protocolHandler, name, value);
                }   
            } catch (Exception e) {
                throw new MBeanException(e);
            }
  
	} else {
	    super.setAttribute(attribute);
	}
	
    }


    // ------------------------------------------------------------- Operations
    
    
}
