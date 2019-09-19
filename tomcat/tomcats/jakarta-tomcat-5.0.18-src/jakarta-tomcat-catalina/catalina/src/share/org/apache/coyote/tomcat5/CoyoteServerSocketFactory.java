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
 * [Additional notices, if required by prior licensing conditions]
 *
 */
package org.apache.coyote.tomcat5;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;


/**
 * This socket factory holds secure socket factory parameters. Besides the usual
 * configuration mechanism based on setting JavaBeans properties, this
 * component may also be configured by passing a series of attributes set
 * with calls to <code>setAttribute()</code>.  The following attribute
 * names are recognized, with default values in square brackets:
 * <ul>
 * <li><strong>algorithm</strong> - Certificate encoding algorithm
 *     to use. [SunX509]</li>
 * <li><strong>clientAuth</strong> - Require client authentication if
 *     set to <code>true</code>. [false]</li>
 * <li><strong>keystoreFile</strong> - Pathname to the Key Store file to be
 *     loaded.  This must be an absolute path, or a relative path that
 *     is resolved against the "catalina.base" system property.
 *     ["./keystore" in the user home directory]</li>
 * <li><strong>keystorePass</strong> - Password for the Key Store file to be
 *     loaded. ["changeit"]</li>
 * <li><strong>keystoreType</strong> - Type of the Key Store file to be
 *     loaded. ["JKS"]</li>
 * <li><strong>protocol</strong> - SSL protocol to use. [TLS]</li>
 * </ul>
 *
 * @author Harish Prabandham
 * @author Costin Manolache
 * @author Craig McClanahan
 */

public class CoyoteServerSocketFactory
    implements org.apache.catalina.net.ServerSocketFactory {

    private String algorithm = null;
    private boolean clientAuth = false;
    private String keystoreFile =
        System.getProperty("user.home") + File.separator + ".keystore";
    private String randomFile =
        System.getProperty("user.home") + File.separator + "random.pem";
    private String rootFile =
        System.getProperty("user.home") + File.separator + "root.pem";
    private String keystorePass = "changeit";
    private String keystoreType = "JKS";
    private String protocol = "TLS";
    private String protocols;
    private String sslImplementation = null;
    private String cipherSuites;
    private String keyAlias;

    // ------------------------------------------------------------- Properties

    /**
     * Gets the certificate encoding algorithm to be used.
     *
     * @return Certificate encoding algorithm
     */
    public String getAlgorithm() {
        return (this.algorithm);
    }

    /**
     * Sets the certificate encoding algorithm to be used.
     *
     * @param algorithm Certificate encoding algorithm
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Provides information about whether client authentication is enforced.
     *
     * @return true if client authentication is enforced, false otherwise
     */
    public boolean getClientAuth() {
        return (this.clientAuth);
    }

    /**
     * Sets the requirement of client authentication.
     *
     * @param clientAuth true if client authentication is enforced, false
     * otherwise
     */
    public void setClientAuth(boolean clientAuth) {
        this.clientAuth = clientAuth;
    }

    /**
     * Gets the pathname to the keystore file.
     *
     * @return Pathname to the keystore file
     */
    public String getKeystoreFile() {
        return (this.keystoreFile);
    }

    /**
     * Sets the pathname to the keystore file.
     *
     * @param keystoreFile Pathname to the keystore file
     */
    public void setKeystoreFile(String keystoreFile) {
      
        File file = new File(keystoreFile);
        if (!file.isAbsolute())
            file = new File(System.getProperty("catalina.base"),
                            keystoreFile);
        this.keystoreFile = file.getAbsolutePath();
    }

    /**
     * Gets the pathname to the random file.
     *
     * @return Pathname to the random file
     */
    public String getRandomFile() {
        return (this.randomFile);
    }

    /**
     * Sets the pathname to the random file.
     *
     * @param randomFile Pathname to the random file
     */
    public void setRandomFile(String randomFile) {
      
        File file = new File(randomFile);
        if (!file.isAbsolute())
            file = new File(System.getProperty("catalina.base"),
                            randomFile);
        this.randomFile = file.getAbsolutePath();
    }

    /**
     * Gets the pathname to the root list.
     *
     * @return Pathname to the root list
     */
    public String getRootFile() {
        return (this.rootFile);
    }

    /**
     * Sets the pathname to the root list.
     *
     * @param rootFile Pathname to the root list
     */
    public void setRootFile(String rootFile) {
      
        File file = new File(rootFile);
        if (!file.isAbsolute())
            file = new File(System.getProperty("catalina.base"),
                            rootFile);
        this.rootFile = file.getAbsolutePath();
    }
     
    /**
     * Gets the keystore password.
     *
     * @return Keystore password
     */
    public String getKeystorePass() {
        return (this.keystorePass);
    }

    /**
     * Sets the keystore password.
     *
     * @param keystorePass Keystore password
     */
    public void setKeystorePass(String keystorePass) {
        this.keystorePass = keystorePass;
    }

    /**
     * Gets the keystore type.
     *
     * @return Keystore type
     */
    public String getKeystoreType() {
        return (this.keystoreType);
    }

    /**
     * Sets the keystore type.
     *
     * @param keystoreType Keystore type
     */
    public void setKeystoreType(String keystoreType) {
        this.keystoreType = keystoreType;
    }

    /**
     * Gets the SSL protocol variant to be used.
     *
     * @return SSL protocol variant
     */
    public String getProtocol() {
        return (this.protocol);
    }

    /**
     * Sets the SSL protocol variant to be used.
     *
     * @param protocol SSL protocol variant
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Gets the SSL protocol variants to be enabled.
     *
     * @return Comma-separated list of SSL protocol variants
     */
    public String getProtocols() {
        return this.protocols;
    }

    /**
     * Sets the SSL protocol variants to be enabled.
     *
     * @param protocols Comma-separated list of SSL protocol variants
     */
    public void setProtocols(String protocols) {
        this.protocols = protocols;
    }

    /**
     * Gets the name of the SSL implementation to be used.
     *
     * @return SSL implementation name
     */
    public String getSSLImplementation() {
        return (this.sslImplementation);
    }

    /**
     * Sets the name of the SSL implementation to be used.
     *
     * @param sslImplementation SSL implementation name
     */
    public void setSSLImplementation(String sslImplementation) {
        this.sslImplementation = sslImplementation;
    }

    /**
     * Gets the alias name of the keypair and supporting certificate chain
     * used by the server to authenticate itself to SSL clients.
     *
     * @return The alias name of the keypair and supporting certificate chain
     */
    public String getKeyAlias() {
        return this.keyAlias;
    }

    /**
     * Sets the alias name of the keypair and supporting certificate chain
     * used by the server to authenticate itself to SSL clients.
     *
     * @param alias The alias name of the keypair and supporting certificate
     * chain
     */
    public void setKeyAlias(String alias) {
        this.keyAlias = alias;
    }

    /**
     * Gets the list of SSL cipher suites that are to be enabled
     *
     * @return Comma-separated list of SSL cipher suites, or null if all
     * cipher suites supported by the underlying SSL implementation are being
     * enabled
     */
    public String getCiphers() {
	return this.cipherSuites;
    }

    /**
     * Sets the SSL cipher suites that are to be enabled.
     *
     * Only those SSL cipher suites that are actually supported by
     * the underlying SSL implementation will be enabled.
     *
     * @param ciphers Comma-separated list of SSL cipher suites
     */
    public void setCiphers(String ciphers) {
	this.cipherSuites = ciphers;
    }


    // --------------------------------------------------------- Public Methods


    public ServerSocket createSocket(int port) {
        return (null);
    }


    public ServerSocket createSocket(int port, int backlog) {
        return (null);
    }


    public ServerSocket createSocket(int port, int backlog,
                                     InetAddress ifAddress) {
        return (null);
    }


}
