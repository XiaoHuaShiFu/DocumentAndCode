/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/catalina/src/share/org/apache/naming/factory/TyrexResourceFactory.java,v 1.1 2002/06/28 12:44:41 remm Exp $
 * $Revision: 1.1 $
 * $Date: 2002/06/28 12:44:41 $
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


package org.apache.naming.factory;

import java.util.Hashtable;
import java.util.Iterator;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.RefAddr;
import javax.naming.spi.ObjectFactory;
import java.net.URL;
import org.apache.naming.ResourceRef;
import tyrex.tm.TransactionDomain;
import tyrex.resource.Resources;
import tyrex.resource.ResourceConfig;

/**
 * Object factory for Tyrex Resources.<br>
 *
 * This class retrieves Tyrex resources that are configured in the
 * TransactionDomain.  The type of Resource returned is specified in
 * Tyrex's domain configuration file.
 *
 * Tyrex is an open-source transaction manager, developed by Assaf Arkin and
 * exolab.org. See the <a href="http://tyrex.exolab.org/">Tyrex homepage</a>
 * for more details about Tyrex and downloads.
 *
 * @author David Haraburda
 * @version $Revision: 1.1 $ $Date: 2002/06/28 12:44:41 $
 */

public class TyrexResourceFactory
    extends TyrexFactory {


    // ----------------------------------------------------------- Constructors


    // -------------------------------------------------------------- Constants


    public static final String RESOURCE_NAME = "name";
    public static final String DEFAULT_RESOURCE_NAME = "tomcat";


    // ----------------------------------------------------- Instance Variables


    // --------------------------------------------------------- Public Methods


    // -------------------------------------------------- ObjectFactory Methods


    /**
     * Create a new Resource instance.  The type of Resource is dependant
     * upon Tyrex's domain configuration.
     *
     * @param obj The reference object describing the Resource
     */
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable environment)
        throws NamingException {

        if (obj instanceof ResourceRef) {
            Reference ref = (Reference) obj;

            if (ref.getClassName().equals("tyrex.resource.Resource")) {

                try {

                    Resources resources = 
                        getTransactionDomain().getResources();
                    RefAddr nameAddr = ref.get(RESOURCE_NAME);
                    if (nameAddr != null) {
                        return resources
                            .getResource(nameAddr.getContent().toString())
                            .getClientFactory();
                    } else {
                        return resources.getResource(DEFAULT_RESOURCE_NAME)
                            .getClientFactory();
                    }

                } catch (Throwable t) {
                    log("Cannot create Tyrex Resource, Exception", t);
                    throw new NamingException
                        ("Exception creating Tyrex Resource: " 
                         + t.getMessage());
                }

            }

        }

        return null;

    }


    // -------------------------------------------------------- Private Methods


    private void log(String message) {
        System.out.print("TyrexResourceFactory:  ");
        System.out.println(message);
    }


    private void log(String message, Throwable exception) {
        log(message);
        exception.printStackTrace(System.out);
    }


}


