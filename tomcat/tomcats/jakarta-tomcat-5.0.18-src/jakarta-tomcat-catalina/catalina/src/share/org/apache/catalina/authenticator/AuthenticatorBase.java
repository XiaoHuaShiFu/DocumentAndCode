/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/catalina/src/share/org/apache/catalina/authenticator/AuthenticatorBase.java,v 1.15 2003/12/11 05:50:39 billbarker Exp $
 * $Revision: 1.15 $
 * $Date: 2003/12/11 05:50:39 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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


package org.apache.catalina.authenticator;


import java.io.IOException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Authenticator;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.HttpResponse;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Logger;
import org.apache.catalina.Manager;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Session;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.util.DateTool;
import org.apache.catalina.util.LifecycleSupport;
import org.apache.catalina.util.StringManager;
import org.apache.catalina.valves.ValveBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Basic implementation of the <b>Valve</b> interface that enforces the
 * <code>&lt;security-constraint&gt;</code> elements in the web application
 * deployment descriptor.  This functionality is implemented as a Valve
 * so that it can be ommitted in environments that do not require these
 * features.  Individual implementations of each supported authentication
 * method can subclass this base class as required.
 * <p>
 * <b>USAGE CONSTRAINT</b>:  When this class is utilized, the Context to
 * which it is attached (or a parent Container in a hierarchy) must have an
 * associated Realm that can be used for authenticating users and enumerating
 * the roles to which they have been assigned.
 * <p>
 * <b>USAGE CONSTRAINT</b>:  This Valve is only useful when processing HTTP
 * requests.  Requests of any other type will simply be passed through.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.15 $ $Date: 2003/12/11 05:50:39 $
 */


public abstract class AuthenticatorBase
    extends ValveBase
    implements Authenticator, Lifecycle {
    private static Log log = LogFactory.getLog(AuthenticatorBase.class);


    // ----------------------------------------------------- Instance Variables


    /**
     * The default message digest algorithm to use if we cannot use
     * the requested one.
     */
    protected static final String DEFAULT_ALGORITHM = "MD5";


    /**
     * The number of random bytes to include when generating a
     * session identifier.
     */
    protected static final int SESSION_ID_BYTES = 16;


    /**
     * The message digest algorithm to be used when generating session
     * identifiers.  This must be an algorithm supported by the
     * <code>java.security.MessageDigest</code> class on your platform.
     */
    protected String algorithm = DEFAULT_ALGORITHM;


    /**
     * Should we cache authenticated Principals if the request is part of
     * an HTTP session?
     */
    protected boolean cache = true;


    /**
     * The Context to which this Valve is attached.
     */
    protected Context context = null;


    /**
     * The debugging detail level for this component.
     */
    protected int debug = 0;


    /**
     * Return the MessageDigest implementation to be used when
     * creating session identifiers.
     */
    protected MessageDigest digest = null;


    /**
     * A String initialization parameter used to increase the entropy of
     * the initialization of our random number generator.
     */
    protected String entropy = null;


    /**
     * Descriptive information about this implementation.
     */
    protected static final String info =
        "org.apache.catalina.authenticator.AuthenticatorBase/1.0";

    /**
     * Flag to determine if we disable proxy caching, or leave the issue
     * up to the webapp developer.
     */
    protected boolean disableProxyCaching = true;

    /**
     * The lifecycle event support for this component.
     */
    protected LifecycleSupport lifecycle = new LifecycleSupport(this);


    /**
     * A random number generator to use when generating session identifiers.
     */
    protected Random random = null;


    /**
     * The Java class name of the random number generator class to be used
     * when generating session identifiers.
     */
    protected String randomClass = "java.security.SecureRandom";


    /**
     * The string manager for this package.
     */
    protected static final StringManager sm =
        StringManager.getManager(Constants.Package);


    /**
     * The SingleSignOn implementation in our request processing chain,
     * if there is one.
     */
    protected SingleSignOn sso = null;


    /**
     * Has this component been started?
     */
    protected boolean started = false;


    /**
     * "Expires" header always set to Date(1), so generate once only
     */
    private static final String DATE_ONE =
        (new SimpleDateFormat(DateTool.HTTP_RESPONSE_DATE_HEADER,
                              Locale.US)).format(new Date(1));


    // ------------------------------------------------------------- Properties


    /**
     * Return the message digest algorithm for this Manager.
     */
    public String getAlgorithm() {

        return (this.algorithm);

    }


    /**
     * Set the message digest algorithm for this Manager.
     *
     * @param algorithm The new message digest algorithm
     */
    public void setAlgorithm(String algorithm) {

        this.algorithm = algorithm;

    }


    /**
     * Return the cache authenticated Principals flag.
     */
    public boolean getCache() {

        return (this.cache);

    }


    /**
     * Set the cache authenticated Principals flag.
     *
     * @param cache The new cache flag
     */
    public void setCache(boolean cache) {

        this.cache = cache;

    }


    /**
     * Return the Container to which this Valve is attached.
     */
    public Container getContainer() {

        return (this.context);

    }


    /**
     * Set the Container to which this Valve is attached.
     *
     * @param container The container to which we are attached
     */
    public void setContainer(Container container) {

        if (!(container instanceof Context))
            throw new IllegalArgumentException
                (sm.getString("authenticator.notContext"));

        super.setContainer(container);
        this.context = (Context) container;

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
     * Return the entropy increaser value, or compute a semi-useful value
     * if this String has not yet been set.
     */
    public String getEntropy() {

        // Calculate a semi-useful value if this has not been set
        if (this.entropy == null)
            setEntropy(this.toString());

        return (this.entropy);

    }


    /**
     * Set the entropy increaser value.
     *
     * @param entropy The new entropy increaser value
     */
    public void setEntropy(String entropy) {

        this.entropy = entropy;

    }


    /**
     * Return descriptive information about this Valve implementation.
     */
    public String getInfo() {

        return (this.info);

    }


    /**
     * Return the random number generator class name.
     */
    public String getRandomClass() {

        return (this.randomClass);

    }


    /**
     * Set the random number generator class name.
     *
     * @param randomClass The new random number generator class name
     */
    public void setRandomClass(String randomClass) {

        this.randomClass = randomClass;

    }

    /**
     * Return the flag that states if we add headers to disable caching by
     * proxies.
     */
    public boolean getDisableProxyCaching() {
        return disableProxyCaching;
    }

    /**
     * Set the value of the flag that states if we add headers to disable
     * caching by proxies.
     * @param nocache <code>true</code> if we add headers to disable proxy 
     *              caching, <code>false</code> if we leave the headers alone.
     */
    public void setDisableProxyCaching(boolean nocache) {
        disableProxyCaching = nocache;
    }

    // --------------------------------------------------------- Public Methods


    /**
     * Enforce the security restrictions in the web application deployment
     * descriptor of our associated Context.
     *
     * @param request Request to be processed
     * @param response Response to be processed
     * @param context The valve context used to invoke the next valve
     *  in the current processing pipeline
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if thrown by a processing element
     */
    public void invoke(Request request, Response response,
                       ValveContext context)
        throws IOException, ServletException {

        // If this is not an HTTP request, do nothing
        if (!(request instanceof HttpRequest) ||
            !(response instanceof HttpResponse)) {
            context.invokeNext(request, response);
            return;
        }
        if (!(request.getRequest() instanceof HttpServletRequest) ||
            !(response.getResponse() instanceof HttpServletResponse)) {
            context.invokeNext(request, response);
            return;
        }
        HttpRequest hrequest = (HttpRequest) request;
        HttpResponse hresponse = (HttpResponse) response;
        if (log.isDebugEnabled())
            log.debug("Security checking request " +
                ((HttpServletRequest) request.getRequest()).getMethod() + " " +
                ((HttpServletRequest) request.getRequest()).getRequestURI());
        LoginConfig config = this.context.getLoginConfig();

        // Have we got a cached authenticated Principal to record?
        if (cache) {
            Principal principal =
                ((HttpServletRequest) request.getRequest()).getUserPrincipal();
            if (principal == null) {
                Session session = getSession(hrequest);
                if (session != null) {
                    principal = session.getPrincipal();
                    if (principal != null) {
                        if (log.isDebugEnabled())
                            log.debug("We have cached auth type " +
                                session.getAuthType() +
                                " for principal " +
                                session.getPrincipal());
                        hrequest.setAuthType(session.getAuthType());
                        hrequest.setUserPrincipal(principal);
                    }
                }
            }
        }

        // Special handling for form-based logins to deal with the case
        // where the login form (and therefore the "j_security_check" URI
        // to which it submits) might be outside the secured area
        String contextPath = this.context.getPath();
        String requestURI = hrequest.getDecodedRequestURI();
        if (requestURI.startsWith(contextPath) &&
            requestURI.endsWith(Constants.FORM_ACTION)) {
            if (!authenticate(hrequest, hresponse, config)) {
                if (log.isDebugEnabled())
                    log.debug(" Failed authenticate() test ??" + requestURI );
                return;
            }
        }

        Realm realm = this.context.getRealm();
        // Is this request URI subject to a security constraint?
        SecurityConstraint [] constraints
            = realm.findSecurityConstraints(hrequest, this.context);
       
        if ((constraints == null) /* &&
            (!Constants.FORM_METHOD.equals(config.getAuthMethod())) */ ) {
            if (log.isDebugEnabled())
                log.debug(" Not subject to any constraint");
            context.invokeNext(request, response);
            return;
        }

        // Make sure that constrained resources are not cached by web proxies
        // or browsers as caching can provide a security hole
        HttpServletRequest hsrequest = (HttpServletRequest)hrequest.getRequest();
        if (disableProxyCaching && 
            // FIXME: Disabled for Mozilla FORM support over SSL 
            // (improper caching issue)
            //!hsrequest.isSecure() &&
            !"POST".equalsIgnoreCase(hsrequest.getMethod())) {
            HttpServletResponse sresponse = 
                (HttpServletResponse) response.getResponse();
            sresponse.setHeader("Pragma", "No-cache");
            sresponse.setHeader("Cache-Control", "no-cache");
            sresponse.setHeader("Expires", DATE_ONE);
        }

        int i;
        // Enforce any user data constraint for this security constraint
        if (log.isDebugEnabled()) {
            log.debug(" Calling hasUserDataPermission()");
        }
        if (!realm.hasUserDataPermission(hrequest, hresponse,
                                         constraints)) {
            if (log.isDebugEnabled()) {
                log.debug(" Failed hasUserDataPermission() test");
            }
            /*
             * ASSERT: Authenticator already set the appropriate
             * HTTP status code, so we do not have to do anything special
             */
            return;
        }
       
        for(i=0; i < constraints.length; i++) {
            // Authenticate based upon the specified login configuration
            if (constraints[i].getAuthConstraint()) {
                if (log.isDebugEnabled()) {
                    log.debug(" Calling authenticate()");
                }
                if (!authenticate(hrequest, hresponse, config)) {
                    if (log.isDebugEnabled()) {
                        log.debug(" Failed authenticate() test");
                    }
                    /*
                     * ASSERT: Authenticator already set the appropriate
                     * HTTP status code, so we do not have to do anything
                     * special
                     */
                    return;
                } else {
                    break;
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(" Calling accessControl()");
        }
        if (!realm.hasResourcePermission(hrequest, hresponse,
                                         constraints,
                                         this.context)) {
            if (log.isDebugEnabled()) {
                log.debug(" Failed accessControl() test");
            }
            /*
             * ASSERT: AccessControl method has already set the
             * appropriate HTTP status code, so we do not have to do
             * anything special
             */
            return;
        }
    
        // Any and all specified constraints have been satisfied
        if (log.isDebugEnabled()) {
            log.debug(" Successfully passed all security constraints");
        }
        context.invokeNext(request, response);

    }


    // ------------------------------------------------------ Protected Methods




    /**
     * Associate the specified single sign on identifier with the
     * specified Session.
     *
     * @param ssoId Single sign on identifier
     * @param session Session to be associated
     */
    protected void associate(String ssoId, Session session) {

        if (sso == null)
            return;
        sso.associate(ssoId, session);

    }


    /**
     * Authenticate the user making this request, based on the specified
     * login configuration.  Return <code>true</code> if any specified
     * constraint has been satisfied, or <code>false</code> if we have
     * created a response challenge already.
     *
     * @param request Request we are processing
     * @param response Response we are creating
     * @param config    Login configuration describing how authentication
     *              should be performed
     *
     * @exception IOException if an input/output error occurs
     */
    protected abstract boolean authenticate(HttpRequest request,
                                            HttpResponse response,
                                            LoginConfig config)
        throws IOException;


    /**
     * Generate and return a new session identifier for the cookie that
     * identifies an SSO principal.
     */
    protected synchronized String generateSessionId() {

        // Generate a byte array containing a session identifier
        byte bytes[] = new byte[SESSION_ID_BYTES];
        getRandom().nextBytes(bytes);
        bytes = getDigest().digest(bytes);

        // Render the result as a String of hexadecimal digits
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            byte b1 = (byte) ((bytes[i] & 0xf0) >> 4);
            byte b2 = (byte) (bytes[i] & 0x0f);
            if (b1 < 10)
                result.append((char) ('0' + b1));
            else
                result.append((char) ('A' + (b1 - 10)));
            if (b2 < 10)
                result.append((char) ('0' + b2));
            else
                result.append((char) ('A' + (b2 - 10)));
        }
        return (result.toString());

    }


    /**
     * Return the MessageDigest object to be used for calculating
     * session identifiers.  If none has been created yet, initialize
     * one the first time this method is called.
     */
    protected synchronized MessageDigest getDigest() {

        if (this.digest == null) {
            try {
                this.digest = MessageDigest.getInstance(algorithm);
            } catch (NoSuchAlgorithmException e) {
                try {
                    this.digest = MessageDigest.getInstance(DEFAULT_ALGORITHM);
                } catch (NoSuchAlgorithmException f) {
                    this.digest = null;
                }
            }
        }

        return (this.digest);

    }


    /**
     * Return the random number generator instance we should use for
     * generating session identifiers.  If there is no such generator
     * currently defined, construct and seed a new one.
     */
    protected synchronized Random getRandom() {

        if (this.random == null) {
            try {
                Class clazz = Class.forName(randomClass);
                this.random = (Random) clazz.newInstance();
                long seed = System.currentTimeMillis();
                char entropy[] = getEntropy().toCharArray();
                for (int i = 0; i < entropy.length; i++) {
                    long update = ((byte) entropy[i]) << ((i % 8) * 8);
                    seed ^= update;
                }
                this.random.setSeed(seed);
            } catch (Exception e) {
                this.random = new java.util.Random();
            }
        }

        return (this.random);

    }


    /**
     * Return the internal Session that is associated with this HttpRequest,
     * or <code>null</code> if there is no such Session.
     *
     * @param request The HttpRequest we are processing
     */
    protected Session getSession(HttpRequest request) {

        return (getSession(request, false));

    }


    /**
     * Return the internal Session that is associated with this HttpRequest,
     * possibly creating a new one if necessary, or <code>null</code> if
     * there is no such session and we did not create one.
     *
     * @param request The HttpRequest we are processing
     * @param create Should we create a session if needed?
     */
    protected Session getSession(HttpRequest request, boolean create) {

        HttpServletRequest hreq =
            (HttpServletRequest) request.getRequest();
        HttpSession hses = hreq.getSession(create);
        if (hses == null)
            return (null);
        Manager manager = context.getManager();
        if (manager == null)
            return (null);
        else {
            try {
                return (manager.findSession(hses.getId()));
            } catch (IOException e) {
                return (null);
            }
        }

    }


    /**
     * Log a message on the Logger associated with our Container (if any).
     *
     * @param message Message to be logged
     */
    protected void log(String message) {

        Logger logger = context.getLogger();
        if (logger != null)
            logger.log("Authenticator[" + context.getPath() + "]: " +
                       message);
        else
            System.out.println("Authenticator[" + context.getPath() +
                               "]: " + message);

    }


    /**
     * Log a message on the Logger associated with our Container (if any).
     *
     * @param message Message to be logged
     * @param throwable Associated exception
     */
    protected void log(String message, Throwable throwable) {

        Logger logger = context.getLogger();
        if (logger != null)
            logger.log("Authenticator[" + context.getPath() + "]: " +
                       message, throwable);
        else {
            System.out.println("Authenticator[" + context.getPath() +
                               "]: " + message);
            throwable.printStackTrace(System.out);
        }

    }


    /**
     * Attempts reauthentication to the <code>Realm</code> using
     * the credentials included in argument <code>entry</code>.
     *
     * @param ssoId identifier of SingleSignOn session with which the
     *              caller is associated
     * @param request   the request that needs to be authenticated
     */
    protected boolean reauthenticateFromSSO(String ssoId, HttpRequest request) {

        if (sso == null || ssoId == null)
            return false;

        boolean reauthenticated = false;

        SingleSignOnEntry entry = sso.lookup(ssoId);
        if (entry != null && entry.getCanReauthenticate()) {
            Principal reauthPrincipal = null;
            Container parent = getContainer();
            if (parent != null) {
                Realm realm = getContainer().getRealm();
                String username = entry.getUsername();
                if (realm != null && username != null) {
                    reauthPrincipal =
                        realm.authenticate(username, entry.getPassword());
                }
            }

            if (reauthPrincipal != null) {
                associate(ssoId, getSession(request, true));
                request.setAuthType(entry.getAuthType());
                request.setUserPrincipal(reauthPrincipal);

                reauthenticated = true;
                if (log.isDebugEnabled()) {
                    log.debug(" Reauthenticated cached principal '" +
                              entry.getPrincipal().getName() +
                              "' with auth type '" +
                              entry.getAuthType() + "'");
                }
            }
        }

        return reauthenticated;
    }


    /**
     * Register an authenticated Principal and authentication type in our
     * request, in the current session (if there is one), and with our
     * SingleSignOn valve, if there is one.  Set the appropriate cookie
     * to be returned.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are generating
     * @param principal The authenticated Principal to be registered
     * @param authType The authentication type to be registered
     * @param username Username used to authenticate (if any)
     * @param password Password used to authenticate (if any)
     */
    protected void register(HttpRequest request, HttpResponse response,
                            Principal principal, String authType,
                            String username, String password) {

        if (log.isDebugEnabled())
            log.debug("Authenticated '" + principal.getName() + "' with type '"
                + authType + "'");

        // Cache the authentication information in our request
        request.setAuthType(authType);
        request.setUserPrincipal(principal);

        Session session = getSession(request, false);
        // Cache the authentication information in our session, if any
        if (cache) {
            if (session != null) {
                session.setAuthType(authType);
                session.setPrincipal(principal);
                if (username != null)
                    session.setNote(Constants.SESS_USERNAME_NOTE, username);
                else
                    session.removeNote(Constants.SESS_USERNAME_NOTE);
                if (password != null)
                    session.setNote(Constants.SESS_PASSWORD_NOTE, password);
                else
                    session.removeNote(Constants.SESS_PASSWORD_NOTE);
            }
        }

        // Construct a cookie to be returned to the client
        if (sso == null)
            return;

        // Only create a new SSO entry if the SSO did not already set a note
        // for an existing entry (as it would do with subsequent requests
        // for DIGEST and SSL authenticated contexts)
        String ssoId = (String) request.getNote(Constants.REQ_SSOID_NOTE);
        if (ssoId == null) {
            // Construct a cookie to be returned to the client
            HttpServletResponse hres =
                (HttpServletResponse) response.getResponse();
            ssoId = generateSessionId();
            Cookie cookie = new Cookie(Constants.SINGLE_SIGN_ON_COOKIE, ssoId);
            cookie.setMaxAge(-1);
            cookie.setPath("/");
            hres.addCookie(cookie);

            // Register this principal with our SSO valve
            sso.register(ssoId, principal, authType, username, password);
            request.setNote(Constants.REQ_SSOID_NOTE, ssoId);

        } else {
            // Update the SSO session with the latest authentication data
            sso.update(ssoId, principal, authType, username, password);
        }

        // Fix for Bug 10040
        // Always associate a session with a new SSO reqistration.
        // SSO entries are only removed from the SSO registry map when
        // associated sessions are destroyed; if a new SSO entry is created
        // above for this request and the user never revisits the context, the
        // SSO entry will never be cleared if we don't associate the session
        if (session == null)
            session = getSession(request, true);
        sso.associate(ssoId, session);

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
     * component.  This method should be called after <code>configure()</code>,
     * and before any of the public methods of the component are utilized.
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents this component from being used
     */
    public void start() throws LifecycleException {

        // Validate and update our current component state
        if (started)
            throw new LifecycleException
                (sm.getString("authenticator.alreadyStarted"));
        lifecycle.fireLifecycleEvent(START_EVENT, null);
        if ("org.apache.catalina.core.StandardContext".equals
            (context.getClass().getName())) {
            try {
                // XXX What is this ???
                Class paramTypes[] = new Class[0];
                Object paramValues[] = new Object[0];
                Method method =
                    context.getClass().getMethod("getDebug", paramTypes);
                Integer result = (Integer) method.invoke(context, paramValues);
                setDebug(result.intValue());
            } catch (Exception e) {
                log.error("Exception getting debug value", e);
            }
        }
        started = true;

        // Look up the SingleSignOn implementation in our request processing
        // path, if there is one
        Container parent = context.getParent();
        while ((sso == null) && (parent != null)) {
            if (!(parent instanceof Pipeline)) {
                parent = parent.getParent();
                continue;
            }
            Valve valves[] = ((Pipeline) parent).getValves();
            for (int i = 0; i < valves.length; i++) {
                if (valves[i] instanceof SingleSignOn) {
                    sso = (SingleSignOn) valves[i];
                    break;
                }
            }
            if (sso == null)
                parent = parent.getParent();
        }
        if (log.isDebugEnabled()) {
            if (sso != null)
                log.debug("Found SingleSignOn Valve at " + sso);
            else
                log.debug("No SingleSignOn Valve is present");
        }

    }


    /**
     * Gracefully terminate the active use of the public methods of this
     * component.  This method should be the last one called on a given
     * instance of this component.
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that needs to be reported
     */
    public void stop() throws LifecycleException {

        // Validate and update our current component state
        if (!started)
            throw new LifecycleException
                (sm.getString("authenticator.notStarted"));
        lifecycle.fireLifecycleEvent(STOP_EVENT, null);
        started = false;

        sso = null;

    }


}
