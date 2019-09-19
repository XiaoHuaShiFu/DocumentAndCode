/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/catalina/src/share/org/apache/naming/factory/TyrexFactory.java,v 1.1 2002/06/28 12:44:41 remm Exp $
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

import java.net.URL;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import tyrex.tm.TransactionDomain;
import tyrex.tm.DomainConfigurationException;
import tyrex.tm.RecoveryException;

/**
 * Abstract superclass of any factory that creates objects from Tyrex.<br>
 *
 * Subclasses can use getTransactionDomain() to handle the retrieval and
 * creation of the TransactionDomain.
 *
 * Tyrex is an open-source transaction manager, developed by Assaf Arkin and
 * exolab.org. See the <a href="http://tyrex.exolab.org/">Tyrex homepage</a>
 * for more details about Tyrex and downloads.
 *
 * @author David Haraburda
 * @version $Revision: 1.1 $ $Date: 2002/06/28 12:44:41 $
 */

public abstract class TyrexFactory implements ObjectFactory {


    // ----------------------------------------------------------- Constructors


    // -------------------------------------------------------------- Constants


    // ----------------------------------------------------- Instance Variables


    // ------------------------------------------------------ Protected Methods


    /**
     * Get (and if necessary, create) the active TransactionDomain
     *
     * This class checks to see if there is already a TransactionDomain
     * setup and instantiated.  If so, it is returned, otherwise one is
     * created and initialized using properties obtained from JNDI.
     */
    protected TransactionDomain getTransactionDomain() throws NamingException {
        TransactionDomain domain = null;
        InitialContext initCtx = new InitialContext();
        String config = initCtx.lookup("java:comp/env/" +
            Constants.TYREX_DOMAIN_CONFIG).toString();
        String name = initCtx.lookup("java:comp/env/" +
            Constants.TYREX_DOMAIN_NAME).toString();
        if (config != null && name != null) {
            try {
                domain = TransactionDomain.getDomain(name);
            } catch(Throwable t) {
                // Tyrex throws exceptions if required classes aren't found.
                log("Error loading Tyrex TransactionDomain", t);
                throw new NamingException
                    ("Exception loading TransactionDomain: " + t.getMessage());
            }
            if ((domain == null)
                || (domain.getState() == TransactionDomain.TERMINATED)) {
                URL configURL = Thread.currentThread().getContextClassLoader()
                    .getResource(config);
                if (configURL == null)
                    throw new NamingException
                        ("Could not load Tyrex domain config file");
                try {
                    domain = 
                        TransactionDomain.createDomain(configURL.toString());
                } catch(DomainConfigurationException dce) {
                    throw new NamingException
                        ("Could not create TransactionDomain: " 
                         + dce.getMessage());
                }
            }

        } else {
            throw new NamingException
                ("Specified config file or domain name "
                 + "parameters are invalid.");
        }

        if (domain.getState() == TransactionDomain.READY) {
            try {
                domain.recover();
            } catch( RecoveryException re ) {
                throw new NamingException
                    ("Could not activate TransactionDomain: " 
                     + re.getMessage() );
            }
        }

        return domain;
    }



    // -------------------------------------------------------- Private Methods


    private void log(String message) {
        System.out.print("TyrexFactory:  ");
        System.out.println(message);
    }


    private void log(String message, Throwable exception) {
        log(message);
        exception.printStackTrace(System.out);
    }


}
