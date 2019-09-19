/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/users/ListGroupsAction.java,v 1.3 2002/02/10 08:06:20 craigmcc Exp $
 * $Revision: 1.3 $
 * $Date: 2002/02/10 08:06:20 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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


package org.apache.webapp.admin.users;


import java.io.IOException;
import java.net.URLDecoder;
import java.util.Locale;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.webapp.admin.ApplicationServlet;


/**
 * <p>Retrieve the set of MBean names for all currently defined groups,
 * and expose them in a request attribute named "groupsForm".  This action
 * requires the following request parameters to be set:</p>
 * <ul>
 * <li><strong>databaseName</strong> - Object name of the UserDatabase
 *     MBean from which we should retrieve the groups list.</li>
 * <li><strong>forward</strong> - Global forward to which we should
 *     go after stashing the groups list.</li>
 * </ul>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.3 $ $Date: 2002/02/10 08:06:20 $
 * @since 4.1
 */

public class ListGroupsAction extends Action {


    // ----------------------------------------------------- Instance Variables


    /**
     * The MBeanServer we will be interacting with.
     */
    private MBeanServer mserver = null;


    /**
     * The MessageResources we will be retrieving messages from.
     */
    private MessageResources resources = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param actionForm The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public ActionForward perform(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
        throws IOException, ServletException {


        // Look up the components we will be using as needed
        if (mserver == null) {
            mserver = ((ApplicationServlet) getServlet()).getServer();
        }
        if (resources == null) {
            resources = getServlet().getResources();
        }
        HttpSession session = request.getSession();
        Locale locale = (Locale) session.getAttribute(Action.LOCALE_KEY);


        // Create a form bean containing the requested MBean Names
        String databaseName =
            URLDecoder.decode(request.getParameter("databaseName"));
        GroupsForm groupsForm = null;
        try {
            groupsForm = UserUtils.getGroupsForm(mserver, databaseName);
        } catch (Exception e) {
            getServlet().log(resources.getMessage
                             (locale,
                              "users.error.attribute.get", "groups"), e);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 resources.getMessage
                 (locale, "users.error.attribute.get", "groups"));
        }

        // Stash the results in request scope
        request.setAttribute("groupsForm", groupsForm);
        saveToken(request);
        String forward =
            URLDecoder.decode(request.getParameter("forward"));
        return (mapping.findForward(forward));

    }

}
