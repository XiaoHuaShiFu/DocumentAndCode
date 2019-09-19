/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/catalina/src/share/org/apache/catalina/authenticator/FormAuthenticator.java,v 1.5 2003/11/24 16:46:56 remm Exp $
 * $Revision: 1.5 $
 * $Date: 2003/11/24 16:46:56 $
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


package org.apache.catalina.authenticator;


import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.HttpRequest;
import org.apache.catalina.HttpResponse;
import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.MessageBytes;



/**
 * An <b>Authenticator</b> and <b>Valve</b> implementation of FORM BASED
 * Authentication, as described in the Servlet API Specification, Version 2.2.
 *
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 * @version $Revision: 1.5 $ $Date: 2003/11/24 16:46:56 $
 */

public class FormAuthenticator
    extends AuthenticatorBase {
    private static Log log = LogFactory.getLog(FormAuthenticator.class);



    // ----------------------------------------------------- Instance Variables


    /**
     * Descriptive information about this implementation.
     */
    protected static final String info =
        "org.apache.catalina.authenticator.FormAuthenticator/1.0";


    // ------------------------------------------------------------- Properties


    /**
     * Return descriptive information about this Valve implementation.
     */
    public String getInfo() {

        return (this.info);

    }


    // --------------------------------------------------------- Public Methods


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
    public boolean authenticate(HttpRequest request,
                                HttpResponse response,
                                LoginConfig config)
        throws IOException {

        // References to objects we will need later
        HttpServletRequest hreq =
          (HttpServletRequest) request.getRequest();
        HttpServletResponse hres =
          (HttpServletResponse) response.getResponse();
        Session session = null;

        // Have we already authenticated someone?
        Principal principal = hreq.getUserPrincipal();
        String ssoId = (String) request.getNote(Constants.REQ_SSOID_NOTE);
        if (principal != null) {
            if (log.isDebugEnabled())
                log.debug("Already authenticated '" +
                    principal.getName() + "'");
            // Associate the session with any existing SSO session
            if (ssoId != null)
                associate(ssoId, getSession(request, true));
            return (true);
        }

        // Is there an SSO session against which we can try to reauthenticate?
        if (ssoId != null) {
            if (log.isDebugEnabled())
                log.debug("SSO Id " + ssoId + " set; attempting " +
                          "reauthentication");
            // Try to reauthenticate using data cached by SSO.  If this fails,
            // either the original SSO logon was of DIGEST or SSL (which
            // we can't reauthenticate ourselves because there is no
            // cached username and password), or the realm denied
            // the user's reauthentication for some reason.
            // In either case we have to prompt the user for a logon */
            if (reauthenticateFromSSO(ssoId, request))
                return true;
        }

        // Have we authenticated this user before but have caching disabled?
        if (!cache) {
            session = getSession(request, true);
            if (log.isDebugEnabled())
                log.debug("Checking for reauthenticate in session " + session);
            String username =
                (String) session.getNote(Constants.SESS_USERNAME_NOTE);
            String password =
                (String) session.getNote(Constants.SESS_PASSWORD_NOTE);
            if ((username != null) && (password != null)) {
                if (log.isDebugEnabled())
                    log.debug("Reauthenticating username '" + username + "'");
                principal =
                    context.getRealm().authenticate(username, password);
                if (principal != null) {
                    session.setNote(Constants.FORM_PRINCIPAL_NOTE, principal);
                    register(request, response, principal,
                             Constants.FORM_METHOD,
                             username, password);
                    return (true);
                }
                if (log.isDebugEnabled())
                    log.debug("Reauthentication failed, proceed normally");
            }
        }

        // Is this the re-submit of the original request URI after successful
        // authentication?  If so, forward the *original* request instead.
        if (matchRequest(request)) {
            session = getSession(request, true);
            if (log.isDebugEnabled())
                log.debug("Restore request from session '" + session.getId() 
                          + "'");
            principal = (Principal)
                session.getNote(Constants.FORM_PRINCIPAL_NOTE);
            register(request, response, principal, Constants.FORM_METHOD,
                     (String) session.getNote(Constants.SESS_USERNAME_NOTE),
                     (String) session.getNote(Constants.SESS_PASSWORD_NOTE));
            if (restoreRequest(request, session)) {
                if (log.isDebugEnabled())
                    log.debug("Proceed to restored request");
                return (true);
            } else {
                if (log.isDebugEnabled())
                    log.debug("Restore of original request failed");
                hres.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return (false);
            }
        }

        // Acquire references to objects we will need to evaluate
        MessageBytes uriMB = MessageBytes.newInstance();
        CharChunk uriCC = uriMB.getCharChunk();
        uriCC.setLimit(-1);
        String contextPath = hreq.getContextPath();
        String requestURI = request.getDecodedRequestURI();
        response.setContext(request.getContext());

        // Is this the action request from the login page?
        boolean loginAction =
            requestURI.startsWith(contextPath) &&
            requestURI.endsWith(Constants.FORM_ACTION);

        // No -- Save this request and redirect to the form login page
        if (!loginAction) {
            session = getSession(request, true);
            if (log.isDebugEnabled())
                log.debug("Save request in session '" + session.getId() + "'");
            saveRequest(request, session);
            RequestDispatcher disp =
                context.getServletContext().getRequestDispatcher
                (config.getLoginPage());
            try {
                disp.forward(hreq, hres);
                response.finishResponse();
            } catch (Throwable t) {
                log.warn("Unexpected error forwarding to login page", t);
            }
            return (false);
        }

        // Yes -- Validate the specified credentials and redirect
        // to the error page if they are not correct
        Realm realm = context.getRealm();
        String username = hreq.getParameter(Constants.FORM_USERNAME);
        String password = hreq.getParameter(Constants.FORM_PASSWORD);
        if (log.isDebugEnabled())
            log.debug("Authenticating username '" + username + "'");
        principal = realm.authenticate(username, password);
        if (principal == null) {
            RequestDispatcher disp =
                context.getServletContext().getRequestDispatcher
                (config.getErrorPage());
            try {
                disp.forward(hreq, hres);
            } catch (Throwable t) {
                log.warn("Unexpected error forwarding to error page", t);
            }
            return (false);
        }

        // Save the authenticated Principal in our session
        if (log.isDebugEnabled())
            log.debug("Authentication of '" + username + "' was successful");
        if (session == null)
            session = getSession(request, true);
        session.setNote(Constants.FORM_PRINCIPAL_NOTE, principal);

        // If we are not caching, save the username and password as well
        if (!cache) {
            session.setNote(Constants.SESS_USERNAME_NOTE, username);
            session.setNote(Constants.SESS_PASSWORD_NOTE, password);
        }

        // Redirect the user to the original request URI (which will cause
        // the original request to be restored)
        requestURI = savedRequestURL(session);
        if (log.isDebugEnabled())
            log.debug("Redirecting to original '" + requestURI + "'");
        if (requestURI == null)
            hres.sendError(HttpServletResponse.SC_BAD_REQUEST,
                           sm.getString("authenticator.formlogin"));
        else
            hres.sendRedirect(hres.encodeRedirectURL(requestURI));
        return (false);

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Does this request match the saved one (so that it must be the redirect
     * we signalled after successful authentication?
     *
     * @param request The request to be verified
     */
    protected boolean matchRequest(HttpRequest request) {

      // Has a session been created?
      Session session = getSession(request, false);
      if (session == null)
          return (false);

      // Is there a saved request?
      SavedRequest sreq = (SavedRequest)
          session.getNote(Constants.FORM_REQUEST_NOTE);
      if (sreq == null)
          return (false);

      // Is there a saved principal?
      if (session.getNote(Constants.FORM_PRINCIPAL_NOTE) == null)
          return (false);

      // Does the request URI match?
      HttpServletRequest hreq = (HttpServletRequest) request.getRequest();
      String requestURI = hreq.getRequestURI();
      if (requestURI == null)
          return (false);
      return (requestURI.equals(sreq.getRequestURI()));

    }


    /**
     * Restore the original request from information stored in our session.
     * If the original request is no longer present (because the session
     * timed out), return <code>false</code>; otherwise, return
     * <code>true</code>.
     *
     * @param request The request to be restored
     * @param session The session containing the saved information
     */
    protected boolean restoreRequest(HttpRequest request, Session session) {

        // Retrieve and remove the SavedRequest object from our session
        SavedRequest saved = (SavedRequest)
            session.getNote(Constants.FORM_REQUEST_NOTE);
        session.removeNote(Constants.FORM_REQUEST_NOTE);
        session.removeNote(Constants.FORM_PRINCIPAL_NOTE);
        if (saved == null)
            return (false);

        // Modify our current request to reflect the original one
        request.clearCookies();
        Iterator cookies = saved.getCookies();
        while (cookies.hasNext()) {
            request.addCookie((Cookie) cookies.next());
        }
        request.clearHeaders();
        Iterator names = saved.getHeaderNames();
        while (names.hasNext()) {
            String name = (String) names.next();
            Iterator values = saved.getHeaderValues(name);
            while (values.hasNext()) {
                request.addHeader(name, (String) values.next());
            }
        }
        request.clearLocales();
        Iterator locales = saved.getLocales();
        while (locales.hasNext()) {
            request.addLocale((Locale) locales.next());
        }
        request.clearParameters();
        if ("POST".equalsIgnoreCase(saved.getMethod())) {
            Iterator paramNames = saved.getParameterNames();
            while (paramNames.hasNext()) {
                String paramName = (String) paramNames.next();
                String paramValues[] =
                    saved.getParameterValues(paramName);
                request.addParameter(paramName, paramValues);
            }
        }
        request.setMethod(saved.getMethod());
        request.setQueryString(saved.getQueryString());
        request.setRequestURI(saved.getRequestURI());
        return (true);

    }


    /**
     * Save the original request information into our session.
     *
     * @param request The request to be saved
     * @param session The session to contain the saved information
     */
    private void saveRequest(HttpRequest request, Session session) {

        // Create and populate a SavedRequest object for this request
        HttpServletRequest hreq = (HttpServletRequest) request.getRequest();
        SavedRequest saved = new SavedRequest();
        Cookie cookies[] = hreq.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++)
                saved.addCookie(cookies[i]);
        }
        Enumeration names = hreq.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            Enumeration values = hreq.getHeaders(name);
            while (values.hasMoreElements()) {
                String value = (String) values.nextElement();
                saved.addHeader(name, value);
            }
        }
        Enumeration locales = hreq.getLocales();
        while (locales.hasMoreElements()) {
            Locale locale = (Locale) locales.nextElement();
            saved.addLocale(locale);
        }
        Map parameters = hreq.getParameterMap();
        Iterator paramNames = parameters.keySet().iterator();
        while (paramNames.hasNext()) {
            String paramName = (String) paramNames.next();
            String paramValues[] = (String[]) parameters.get(paramName);
            saved.addParameter(paramName, paramValues);
        }
        saved.setMethod(hreq.getMethod());
        saved.setQueryString(hreq.getQueryString());
        saved.setRequestURI(hreq.getRequestURI());

        // Stash the SavedRequest in our session for later use
        session.setNote(Constants.FORM_REQUEST_NOTE, saved);

    }


    /**
     * Return the request URI (with the corresponding query string, if any)
     * from the saved request so that we can redirect to it.
     *
     * @param session Our current session
     */
    private String savedRequestURL(Session session) {

        SavedRequest saved =
            (SavedRequest) session.getNote(Constants.FORM_REQUEST_NOTE);
        if (saved == null)
            return (null);
        StringBuffer sb = new StringBuffer(saved.getRequestURI());
        if (saved.getQueryString() != null) {
            sb.append('?');
            sb.append(saved.getQueryString());
        }
        return (sb.toString());

    }


}
