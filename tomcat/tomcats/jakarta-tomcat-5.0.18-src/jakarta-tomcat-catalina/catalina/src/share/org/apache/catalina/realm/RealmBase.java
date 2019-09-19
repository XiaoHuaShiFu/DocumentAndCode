/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/catalina/src/share/org/apache/catalina/realm/RealmBase.java,v 1.25 2004/01/11 09:23:42 remm Exp $
 * $Revision: 1.25 $
 * $Date: 2004/01/11 09:23:42 $
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


package org.apache.catalina.realm;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.HttpResponse;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Logger;
import org.apache.catalina.Realm;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.deploy.SecurityCollection;
import org.apache.catalina.util.HexUtils;
import org.apache.catalina.util.LifecycleSupport;
import org.apache.catalina.util.MD5Encoder;
import org.apache.catalina.util.StringManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.modeler.Registry;

/**
 * Simple implementation of <b>Realm</b> that reads an XML file to configure
 * the valid users, passwords, and roles.  The file format (and default file
 * location) are identical to those currently supported by Tomcat 3.X.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.25 $ $Date: 2004/01/11 09:23:42 $
 */

public abstract class RealmBase
    implements Lifecycle, Realm, MBeanRegistration {

    private static Log log = LogFactory.getLog(RealmBase.class);

    // ----------------------------------------------------- Instance Variables


    /**
     * The Container with which this Realm is associated.
     */
    protected Container container = null;


    /**
     * The debugging detail level for this component.
     */
    protected int debug = 0;


    /**
     * Digest algorithm used in storing passwords in a non-plaintext format.
     * Valid values are those accepted for the algorithm name by the
     * MessageDigest class, or <code>null</code> if no digesting should
     * be performed.
     */
    protected String digest = null;


    /**
     * Descriptive information about this Realm implementation.
     */
    protected static final String info =
        "org.apache.catalina.realm.RealmBase/1.0";


    /**
     * The lifecycle event support for this component.
     */
    protected LifecycleSupport lifecycle = new LifecycleSupport(this);


    /**
     * The MessageDigest object for digesting user credentials (passwords).
     */
    protected MessageDigest md = null;


    /**
     * The MD5 helper object for this class.
     */
    protected static final MD5Encoder md5Encoder = new MD5Encoder();


    /**
     * MD5 message digest provider.
     */
    protected static MessageDigest md5Helper;


    /**
     * The string manager for this package.
     */
    protected static StringManager sm =
        StringManager.getManager(Constants.Package);


    /**
     * Has this component been started?
     */
    protected boolean started = false;


    /**
     * The property change support for this component.
     */
    protected PropertyChangeSupport support = new PropertyChangeSupport(this);


    /**
     * Should we validate client certificate chains when they are presented?
     */
    protected boolean validate = true;


    // ------------------------------------------------------------- Properties


    /**
     * Return the Container with which this Realm has been associated.
     */
    public Container getContainer() {

        return (container);

    }


    /**
     * Set the Container with which this Realm has been associated.
     *
     * @param container The associated Container
     */
    public void setContainer(Container container) {

        Container oldContainer = this.container;
        this.container = container;
        support.firePropertyChange("container", oldContainer, this.container);

    }

    /**
     * Return the debugging detail level for this component.
     */
    public int getDebug() {

        return (this.debug);

    }


    /**
     * Set the debugging detail level for this component.
     *
     * @param debug The new debugging detail level
     */
    public void setDebug(int debug) {

        this.debug = debug;

    }


    /**
     * Return the digest algorithm  used for storing credentials.
     */
    public String getDigest() {

        return digest;

    }


    /**
     * Set the digest algorithm used for storing credentials.
     *
     * @param digest The new digest algorithm
     */
    public void setDigest(String digest) {

        this.digest = digest;

    }


    /**
     * Return descriptive information about this Realm implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() {

        return info;

    }


    /**
     * Return the "validate certificate chains" flag.
     */
    public boolean getValidate() {

        return (this.validate);

    }


    /**
     * Set the "validate certificate chains" flag.
     *
     * @param validate The new validate certificate chains flag
     */
    public void setValidate(boolean validate) {

        this.validate = validate;

    }


    // --------------------------------------------------------- Public Methods


    
    /**
     * Add a property change listener to this component.
     *
     * @param listener The listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {

        support.addPropertyChangeListener(listener);

    }


    /**
     * Return the Principal associated with the specified username and
     * credentials, if there is one; otherwise return <code>null</code>.
     *
     * @param username Username of the Principal to look up
     * @param credentials Password or other credentials to use in
     *  authenticating this username
     */
    public Principal authenticate(String username, String credentials) {

        String serverCredentials = getPassword(username);

        if ( (serverCredentials == null)
             || (!serverCredentials.equals(credentials)) )
            return null;

        return getPrincipal(username);

    }


    /**
     * Return the Principal associated with the specified username and
     * credentials, if there is one; otherwise return <code>null</code>.
     *
     * @param username Username of the Principal to look up
     * @param credentials Password or other credentials to use in
     *  authenticating this username
     */
    public Principal authenticate(String username, byte[] credentials) {

        return (authenticate(username, credentials.toString()));

    }


    /**
     * Return the Principal associated with the specified username, which
     * matches the digest calculated using the given parameters using the
     * method described in RFC 2069; otherwise return <code>null</code>.
     *
     * @param username Username of the Principal to look up
     * @param clientDigest Digest which has been submitted by the client
     * @param nOnce Unique (or supposedly unique) token which has been used
     * for this request
     * @param realm Realm name
     * @param md5a2 Second MD5 digest used to calculate the digest :
     * MD5(Method + ":" + uri)
     */
    public Principal authenticate(String username, String clientDigest,
                                  String nOnce, String nc, String cnonce,
                                  String qop, String realm,
                                  String md5a2) {

        /*
          System.out.println("Digest : " + clientDigest);

          System.out.println("************ Digest info");
          System.out.println("Username:" + username);
          System.out.println("ClientSigest:" + clientDigest);
          System.out.println("nOnce:" + nOnce);
          System.out.println("nc:" + nc);
          System.out.println("cnonce:" + cnonce);
          System.out.println("qop:" + qop);
          System.out.println("realm:" + realm);
          System.out.println("md5a2:" + md5a2);
        */


        String md5a1 = getDigest(username, realm);
        if (md5a1 == null)
            return null;
        String serverDigestValue = md5a1 + ":" + nOnce + ":" + nc + ":"
            + cnonce + ":" + qop + ":" + md5a2;
        String serverDigest =
            md5Encoder.encode(md5Helper.digest(serverDigestValue.getBytes()));
        //System.out.println("Server digest : " + serverDigest);

        if (serverDigest.equals(clientDigest))
            return getPrincipal(username);
        else
            return null;
    }



    /**
     * Return the Principal associated with the specified chain of X509
     * client certificates.  If there is none, return <code>null</code>.
     *
     * @param certs Array of client certificates, with the first one in
     *  the array being the certificate of the client itself.
     */
    public Principal authenticate(X509Certificate certs[]) {

        if ((certs == null) || (certs.length < 1))
            return (null);

        // Check the validity of each certificate in the chain
        if (log.isDebugEnabled())
            log.debug("Authenticating client certificate chain");
        if (validate) {
            for (int i = 0; i < certs.length; i++) {
                if (log.isDebugEnabled())
                    log.debug(" Checking validity for '" +
                        certs[i].getSubjectDN().getName() + "'");
                try {
                    certs[i].checkValidity();
                } catch (Exception e) {
                    if (log.isDebugEnabled())
                        log.debug("  Validity exception", e);
                    return (null);
                }
            }
        }

        // Check the existence of the client Principal in our database
        return (getPrincipal(certs[0].getSubjectDN().getName()));

    }

    /**
     * Return the SecurityConstraints configured to guard the request URI for
     * this request, or <code>null</code> if there is no such constraint.
     *
     * @param request Request we are processing
     * @param context Context the Request is mapped to
     */
    public SecurityConstraint [] findSecurityConstraints(HttpRequest request,
                                                     Context context) {

        ArrayList results = null;
        // Are there any defined security constraints?
        SecurityConstraint constraints[] = context.findConstraints();
        if ((constraints == null) || (constraints.length == 0)) {
            if (log.isDebugEnabled())
                log.debug("  No applicable constraints defined");
            return (null);
        }

        // Check each defined security constraint
        HttpServletRequest hreq = (HttpServletRequest) request.getRequest();
        String uri = request.getRequestPathMB().toString();
        
        String method = hreq.getMethod();
        int i;
        boolean found = false;
        for (i = 0; i < constraints.length; i++) {
            SecurityCollection [] collection = constraints[i].findCollections();
                     
            if (log.isDebugEnabled())
                log.debug("  Checking constraint '" + constraints[i] +
                    "' against " + method + " " + uri + " --> " +
                    constraints[i].included(uri, method));
            for(int j=0; j < collection.length; j++){
                String [] patterns = collection[j].findPatterns();
                for(int k=0; k < patterns.length; k++) {
                    if(uri.equals(patterns[k])) {
                        found = true;
                        if(collection[j].findMethod(method)) {
                            if(results == null) {
                                results = new ArrayList();
                            }
                            results.add(constraints[i]);
                        }
                    }
                }
            }
        }

        if(found) {
            return resultsToArray(results);
        }

        int longest = -1;

        for (i = 0; i < constraints.length; i++) {
            SecurityCollection [] collection = constraints[i].findCollections();
            
            if (log.isDebugEnabled())
                log.debug("  Checking constraint '" + constraints[i] +
                    "' against " + method + " " + uri + " --> " +
                    constraints[i].included(uri, method));
            for(int j=0; j < collection.length; j++){
                String [] patterns = collection[j].findPatterns();
                boolean matched = false;
                int length = -1;
                for(int k=0; k < patterns.length; k++) {
                    String pattern = patterns[k];
                    if(pattern.startsWith("/") && pattern.endsWith("/*") && 
                       pattern.length() >= longest) {
                            
                        if(pattern.length() == 2) {
                            matched = true;
                            length = pattern.length();
                        } else if(pattern.regionMatches(0,uri,0,
                                                        pattern.length()-2)) {
                            matched = true;
                            length = pattern.length();
                        }
                    }
                }
                if(matched) {
                    found = true;
                    if(length > longest) {
                        if(results != null) {
                            results.clear();
                        }
                        longest = length;
                    }
                    if(collection[j].findMethod(method)) {
                        if(results == null) {
                            results = new ArrayList();
                        }
                        results.add(constraints[i]);
                    }
                }
            }
        }

        if(found) {
            return  resultsToArray(results);
        }

        for (i = 0; i < constraints.length; i++) {
            SecurityCollection [] collection = constraints[i].findCollections();
            
            if (log.isDebugEnabled())
                log.debug("  Checking constraint '" + constraints[i] +
                    "' against " + method + " " + uri + " --> " +
                    constraints[i].included(uri, method));
            boolean matched = false;
            int pos = -1;
            for(int j=0; j < collection.length; j++){
                String [] patterns = collection[j].findPatterns();
                for(int k=0; k < patterns.length && !matched; k++) {
                    String pattern = patterns[k];
                    if(pattern.startsWith("*.")){
                        int slash = uri.lastIndexOf("/");
                        int dot = uri.lastIndexOf(".");
                        if(slash >= 0 && dot > slash &&
                           dot != uri.length()-1 &&
                           uri.length()-dot == pattern.length()-1) {
                            if(pattern.regionMatches(1,uri,dot,uri.length()-dot)) {
                                matched = true;
                                pos = j;
                            }
                        }
                    }
                }
            }
            if(matched) {
                found = true;
                if(collection[pos].findMethod(method)) {
                    if(results == null) {
                        results = new ArrayList();
                    }
                    results.add(constraints[i]);
                }
            }
        }

        if(found) {
            return resultsToArray(results);
        }

        for (i = 0; i < constraints.length; i++) {
            SecurityCollection [] collection = constraints[i].findCollections();
            
            if (log.isDebugEnabled())
                log.debug("  Checking constraint '" + constraints[i] +
                    "' against " + method + " " + uri + " --> " +
                    constraints[i].included(uri, method));
            for(int j=0; j < collection.length; j++){
                String [] patterns = collection[j].findPatterns();
                boolean matched = false;
                for(int k=0; k < patterns.length && !matched; k++) {
                    String pattern = patterns[k];
                    if(pattern.equals("/")){
                        matched = true;
                    }
                }
                if(matched) {
                    if(results == null) {
                        results = new ArrayList();
                    }                    
                    results.add(constraints[i]);
                }
            }
        }

        if(results == null) {
            // No applicable security constraint was found
            if (log.isDebugEnabled())
                log.debug("  No applicable constraint located");
        }
        return resultsToArray(results);
    }
 
    /**
     * Convert an ArrayList to a SecurityContraint [].
     */
    private SecurityConstraint [] resultsToArray(ArrayList results) {
        if(results == null) {
            return null;
        }
        SecurityConstraint [] array = new SecurityConstraint[results.size()];
        results.toArray(array);
        return array;
    }

    
    /**
     * Perform access control based on the specified authorization constraint.
     * Return <code>true</code> if this constraint is satisfied and processing
     * should continue, or <code>false</code> otherwise.
     *
     * @param request Request we are processing
     * @param response Response we are creating
     * @param constraint Security constraint we are enforcing
     * @param The Context to which client of this class is attached.
     *
     * @exception IOException if an input/output error occurs
     */
    public boolean hasResourcePermission(HttpRequest request,
                                         HttpResponse response,
                                         SecurityConstraint []constraints,
                                         Context context)
        throws IOException {

        if (constraints == null || constraints.length == 0)
            return (true);

        // Specifically allow access to the form login and form error pages
        // and the "j_security_check" action
        LoginConfig config = context.getLoginConfig();
        if ((config != null) &&
            (Constants.FORM_METHOD.equals(config.getAuthMethod()))) {
            String requestURI = request.getDecodedRequestURI();
            String loginPage = context.getPath() + config.getLoginPage();
            if (loginPage.equals(requestURI)) {
                if (log.isDebugEnabled())
                    log.debug(" Allow access to login page " + loginPage);
                return (true);
            }
            String errorPage = context.getPath() + config.getErrorPage();
            if (errorPage.equals(requestURI)) {
                if (log.isDebugEnabled())
                    log.debug(" Allow access to error page " + errorPage);
                return (true);
            }
            if (requestURI.endsWith(Constants.FORM_ACTION)) {
                if (log.isDebugEnabled())
                    log.debug(" Allow access to username/password submission");
                return (true);
            }
        }

        // Which user principal have we already authenticated?
        Principal principal =
            ((HttpServletRequest) request.getRequest()).getUserPrincipal();
        for(int i=0; i < constraints.length; i++) {
            SecurityConstraint constraint = constraints[i];
            String roles[] = constraint.findAuthRoles();
            if (roles == null)
                roles = new String[0];

            if (constraint.getAllRoles())
                return (true);

            if (log.isDebugEnabled())
                log.debug("  Checking roles " + principal);

            if (roles.length == 0) {
                if(constraint.getAuthConstraint()) {
                    ((HttpServletResponse) response.getResponse()).sendError
                        (HttpServletResponse.SC_FORBIDDEN,
                         sm.getString("realmBase.forbidden"));
                    if( log.isDebugEnabled() ) log.debug("No roles ");
                    return (false); // No listed roles means no access at all
                } else {
                    log.debug("Passing all access");
                    return (true);
                }
            } else if (principal == null) {
                if (log.isDebugEnabled())
                    log.debug("  No user authenticated, cannot grant access");
                ((HttpServletResponse) response.getResponse()).sendError
                    (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                     sm.getString("realmBase.notAuthenticated"));
                return (false);
            }


            for (int j = 0; j < roles.length; j++) {
                if (hasRole(principal, roles[j]))
                    return (true);
                if( log.isDebugEnabled() )
                    log.debug( "No role found:  " + roles[j]);
            }
        }
        // Return a "Forbidden" message denying access to this resource
        ((HttpServletResponse) response.getResponse()).sendError
            (HttpServletResponse.SC_FORBIDDEN,
             sm.getString("realmBase.forbidden"));
        return (false);

    }
    
    
    /**
     * Return <code>true</code> if the specified Principal has the specified
     * security role, within the context of this Realm; otherwise return
     * <code>false</code>.  This method can be overridden by Realm
     * implementations, but the default is adequate when an instance of
     * <code>GenericPrincipal</code> is used to represent authenticated
     * Principals from this Realm.
     *
     * @param principal Principal for whom the role is to be checked
     * @param role Security role to be checked
     */
    public boolean hasRole(Principal principal, String role) {

        // Should be overriten in JAASRealm - to avoid pretty inefficient conversions
        if ((principal == null) || (role == null) ||
            !(principal instanceof GenericPrincipal))
            return (false);

        GenericPrincipal gp = (GenericPrincipal) principal;
        if (!(gp.getRealm() == this)) {
            log.debug("Different realm " + this + " " + gp.getRealm());//    return (false);
        }
        boolean result = gp.hasRole(role);
        if (log.isDebugEnabled()) {
            String name = principal.getName();
            if (result)
                log.debug(sm.getString("realmBase.hasRoleSuccess", name, role));
            else
                log.debug(sm.getString("realmBase.hasRoleFailure", name, role));
        }
        return (result);

    }

    
    /**
     * Enforce any user data constraint required by the security constraint
     * guarding this request URI.  Return <code>true</code> if this constraint
     * was not violated and processing should continue, or <code>false</code>
     * if we have created a response already.
     *
     * @param request Request we are processing
     * @param response Response we are creating
     * @param constraint Security constraint being checked
     *
     * @exception IOException if an input/output error occurs
     */
    public boolean hasUserDataPermission(HttpRequest request,
                                    HttpResponse response,
                                    SecurityConstraint []constraints)
        throws IOException {

        // Is there a relevant user data constraint?
        if (constraints == null || constraints.length == 0) {
            if (log.isDebugEnabled())
                log.debug("  No applicable security constraint defined");
            return (true);
        }
        for(int i=0; i < constraints.length; i++) {
            SecurityConstraint constraint = constraints[i];
            String userConstraint = constraint.getUserConstraint();
            if (userConstraint == null) {
                if (log.isDebugEnabled())
                    log.debug("  No applicable user data constraint defined");
                return (true);
            }
            if (userConstraint.equals(Constants.NONE_TRANSPORT)) {
                if (log.isDebugEnabled())
                    log.debug("  User data constraint has no restrictions");
                return (true);
            }

        }
        // Validate the request against the user data constraint
        if (request.getRequest().isSecure()) {
            if (log.isDebugEnabled())
                log.debug("  User data constraint already satisfied");
            return (true);
        }
        // Initialize variables we need to determine the appropriate action
        HttpServletRequest hrequest =
            (HttpServletRequest) request.getRequest();
        HttpServletResponse hresponse =
            (HttpServletResponse) response.getResponse();
        int redirectPort = request.getConnector().getRedirectPort();

        // Is redirecting disabled?
        if (redirectPort <= 0) {
            if (log.isDebugEnabled())
                log.debug("  SSL redirect is disabled");
            hresponse.sendError
                (HttpServletResponse.SC_FORBIDDEN,
                 hrequest.getRequestURI());
            return (false);
        }

        // Redirect to the corresponding SSL port
        StringBuffer file = new StringBuffer();
        String protocol = "https";
        String host = hrequest.getServerName();
        // Protocol
        file.append(protocol).append("://");
        // Host with port
        file.append(host).append(":").append(redirectPort);
        // URI
        file.append(hrequest.getRequestURI());
        String requestedSessionId = hrequest.getRequestedSessionId();
        if ((requestedSessionId != null) &&
            hrequest.isRequestedSessionIdFromURL()) {
            file.append(";jsessionid=");
            file.append(requestedSessionId);
        }
        String queryString = hrequest.getQueryString();
        if (queryString != null) {
            file.append('?');
            file.append(queryString);
        }
        if (log.isDebugEnabled())
            log.debug("  Redirecting to " + file.toString());
        hresponse.sendRedirect(file.toString());
        return (false);

    }
    
    
    /**
     * Remove a property change listener from this component.
     *
     * @param listener The listener to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {

        support.removePropertyChangeListener(listener);

    }


    // ------------------------------------------------------ Lifecycle Methods


    /**
     * Add a lifecycle event listener to this component.
     *
     * @param listener The listener to add
     */
    public void addLifecycleListener(LifecycleListener listener) {

        lifecycle.addLifecycleListener(listener);

    }


    /**
     * Get the lifecycle listeners associated with this lifecycle. If this 
     * Lifecycle has no listeners registered, a zero-length array is returned.
     */
    public LifecycleListener[] findLifecycleListeners() {

        return lifecycle.findLifecycleListeners();

    }


    /**
     * Remove a lifecycle event listener from this component.
     *
     * @param listener The listener to remove
     */
    public void removeLifecycleListener(LifecycleListener listener) {

        lifecycle.removeLifecycleListener(listener);

    }

    /**
     * Prepare for the beginning of active use of the public methods of this
     * component.  This method should be called before any of the public
     * methods of this component are utilized.  It should also send a
     * LifecycleEvent of type START_EVENT to any registered listeners.
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents this component from being used
     */
    public void start() throws LifecycleException {

        // Validate and update our current component state
        if (started) {
            log.info(sm.getString("realmBase.alreadyStarted"));
            return;
        }
        if( !initialized ) {
            init();
        }
        lifecycle.fireLifecycleEvent(START_EVENT, null);
        started = true;

        // Create a MessageDigest instance for credentials, if desired
        if (digest != null) {
            try {
                md = MessageDigest.getInstance(digest);
            } catch (NoSuchAlgorithmException e) {
                throw new LifecycleException
                    (sm.getString("realmBase.algorithm", digest), e);
            }
        }

    }


    /**
     * Gracefully terminate the active use of the public methods of this
     * component.  This method should be the last one called on a given
     * instance of this component.  It should also send a LifecycleEvent
     * of type STOP_EVENT to any registered listeners.
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that needs to be reported
     */
    public void stop()
        throws LifecycleException {

        // Validate and update our current component state
        if (!started) {
            log.info(sm.getString("realmBase.notStarted"));
            return;
        }
        lifecycle.fireLifecycleEvent(STOP_EVENT, null);
        started = false;

        // Clean up allocated resources
        md = null;
        
        destroy();
    
    }
    
    public void destroy() {
    
        // unregister this realm
        if ( oname!=null ) {   
            try {   
                Registry.getRegistry().unregisterComponent(oname); 
                log.debug( "unregistering realm " + oname );   
            } catch( Exception ex ) {   
                log.error( "Can't unregister realm " + oname, ex);   
            }      
        }
          
    }

    // ------------------------------------------------------ Protected Methods


    /**
     * Digest the password using the specified algorithm and
     * convert the result to a corresponding hexadecimal string.
     * If exception, the plain credentials string is returned.
     *
     * <strong>IMPLEMENTATION NOTE</strong> - This implementation is
     * synchronized because it reuses the MessageDigest instance.
     * This should be faster than cloning the instance on every request.
     *
     * @param credentials Password or other credentials to use in
     *  authenticating this username
     */
    protected String digest(String credentials)  {

        // If no MessageDigest instance is specified, return unchanged
        if (hasMessageDigest() == false)
            return (credentials);

        // Digest the user credentials and return as hexadecimal
        synchronized (this) {
            try {
                md.reset();
                md.update(credentials.getBytes());
                return (HexUtils.convert(md.digest()));
            } catch (Exception e) {
                log.error(sm.getString("realmBase.digest"), e);
                return (credentials);
            }
        }

    }

    protected boolean hasMessageDigest() {
        return !(md == null);
    }

    /**
     * Return the digest associated with given principal's user name.
     */
    protected String getDigest(String username, String realmName) {
        if (md5Helper == null) {
            try {
                md5Helper = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                throw new IllegalStateException();
            }
        }
        String digestValue = username + ":" + realmName + ":"
            + getPassword(username);
        byte[] digest =
            md5Helper.digest(digestValue.getBytes());
        return md5Encoder.encode(digest);
    }


    /**
     * Return a short name for this Realm implementation, for use in
     * log messages.
     */
    protected abstract String getName();


    /**
     * Return the password associated with the given principal's user name.
     */
    protected abstract String getPassword(String username);


    /**
     * Return the Principal associated with the given user name.
     */
    protected abstract Principal getPrincipal(String username);


    /**
     * Log a message on the Logger associated with our Container (if any)
     *
     * @param message Message to be logged
     */
    protected void log(String message) {

        Logger logger = null;
        String name = null;
        if (container != null) {
            logger = container.getLogger();
            name = container.getName();
        }

        if (logger != null) {
            logger.log(getName()+"[" + name + "]: " + message);
        } else {
            System.out.println(getName()+"[" + name + "]: " + message);
        }

    }


    /**
     * Log a message on the Logger associated with our Container (if any)
     *
     * @param message Message to be logged
     * @param throwable Associated exception
     */
    protected void log(String message, Throwable throwable) {

        Logger logger = null;
        String name = null;
        if (container != null) {
            logger = container.getLogger();
            name = container.getName();
        }

        if (logger != null) {
            logger.log(getName()+"[" + name + "]: " + message, throwable);
        } else {
            System.out.println(getName()+"[" + name + "]: " + message);
            throwable.printStackTrace(System.out);
        }
    }


    // --------------------------------------------------------- Static Methods


    /**
     * Digest password using the algorithm especificied and
     * convert the result to a corresponding hex string.
     * If exception, the plain credentials string is returned
     *
     * @param credentials Password or other credentials to use in
     *  authenticating this username
     * @param algorithm Algorithm used to do th digest
     */
    public final static String Digest(String credentials, String algorithm) {

        try {
            // Obtain a new message digest with "digest" encryption
            MessageDigest md =
                (MessageDigest) MessageDigest.getInstance(algorithm).clone();
            // encode the credentials
            md.update(credentials.getBytes());
            // Digest the credentials and return as hexadecimal
            return (HexUtils.convert(md.digest()));
        } catch(Exception ex) {
            ex.printStackTrace();
            return credentials;
        }

    }


    /**
     * Digest password using the algorithm especificied and
     * convert the result to a corresponding hex string.
     * If exception, the plain credentials string is returned
     */
    public static void main(String args[]) {

        if(args.length > 2 && args[0].equalsIgnoreCase("-a")) {
            for(int i=2; i < args.length ; i++){
                System.out.print(args[i]+":");
                System.out.println(Digest(args[i], args[1]));
            }
        } else {
            System.out.println
                ("Usage: RealmBase -a <algorithm> <credentials>");
        }

    }


    // -------------------- JMX and Registration  --------------------
    protected String type;
    protected String domain;
    protected String host;
    protected String path;
    protected ObjectName oname;
    protected ObjectName controller;
    protected MBeanServer mserver;

    public ObjectName getController() {
        return controller;
    }

    public void setController(ObjectName controller) {
        this.controller = controller;
    }

    public ObjectName getObjectName() {
        return oname;
    }

    public String getDomain() {
        return domain;
    }

    public String getType() {
        return type;
    }

    public ObjectName preRegister(MBeanServer server,
                                  ObjectName name) throws Exception {
        oname=name;
        mserver=server;
        domain=name.getDomain();

        type=name.getKeyProperty("type");
        host=name.getKeyProperty("host");
        path=name.getKeyProperty("path");

        return name;
    }

    public void postRegister(Boolean registrationDone) {
    }

    public void preDeregister() throws Exception {
    }

    public void postDeregister() {
    }

    protected boolean initialized=false;
    
    public void init() {
        if( initialized && container != null ) return;
        
        initialized=true;
        if( container== null ) {
            ObjectName parent=null;
            // Register with the parent
            try {
                if( host == null ) {
                    // global
                    parent=new ObjectName(domain +":type=Engine");
                } else if( path==null ) {
                    parent=new ObjectName(domain +
                            ":type=Host,host=" + host);
                } else {
                    parent=new ObjectName(domain +":j2eeType=WebModule,name=//" +
                            host + path);
                }
                if( mserver.isRegistered(parent ))  {
                    log.debug("Register with " + parent);
                    mserver.invoke(parent, "setRealm", new Object[] {this},
                            new String[] {"org.apache.catalina.Realm"});
                }
            } catch (Exception e) {
                log.info("Parent not available yet: " + parent);  
            }
        }
        
        if( oname==null ) {
            // register
            try {
                ContainerBase cb=(ContainerBase)container;
                oname=new ObjectName(cb.getDomain()+":type=Realm" + cb.getContainerSuffix());
                Registry.getRegistry().registerComponent(this, oname, null );
                log.debug("Register Realm "+oname);
            } catch (Throwable e) {
                log.error( "Can't register " + oname, e);
            }
        }

    }

}
