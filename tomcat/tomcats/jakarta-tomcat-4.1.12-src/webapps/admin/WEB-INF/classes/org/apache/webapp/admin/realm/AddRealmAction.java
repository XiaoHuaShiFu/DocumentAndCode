/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/realm/AddRealmAction.java,v 1.5 2002/08/13 22:35:20 amyroh Exp $
 * $Revision: 1.5 $
 * $Date: 2002/08/13 22:35:20 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
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

package org.apache.webapp.admin.realm;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.webapp.admin.LabelValueBean;
import org.apache.webapp.admin.Lists;

/**
 * The <code>Action</code> that sets up <em>Add Realm</em> transactions.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.5 $ $Date: 2002/08/13 22:35:20 $
 */

public class AddRealmAction extends Action {

    /**
     * The MessageResources we will be retrieving messages from.
     */
    private MessageResources resources = null;

    // the list for types of realms
    private ArrayList types = null;

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

        // Acquire the resources that we need
        HttpSession session = request.getSession();
        Locale locale = (Locale) session.getAttribute(Action.LOCALE_KEY);
        if (resources == null) {
            resources = getServlet().getResources();
        }

        // Fill in the form values for display and editing

        String realmTypes[] = new String[4];
        realmTypes[0] = "UserDatabaseRealm";
        realmTypes[1] = "JNDIRealm";
        realmTypes[2] = "MemoryRealm";
        realmTypes[3] = "JDBCRealm";

        String parent = request.getParameter("parent");
        String type = request.getParameter("type");
        if (type == null)
            type = "UserDatabaseRealm";    // default type is UserDatabaseRealm

        types = new ArrayList();
        // the first element in the select list should be the type selected
        types.add(new LabelValueBean(type,
                "/admin/AddRealm.do?parent=" + URLEncoder.encode(parent)
                + "&type=" + type));
        for (int i=0; i< realmTypes.length; i++) {
            if (!type.equalsIgnoreCase(realmTypes[i])) {
                types.add(new LabelValueBean(realmTypes[i],
                "/admin/AddRealm.do?parent=" + URLEncoder.encode(parent)
                + "&type=" + realmTypes[i]));
            }
        }

        if ("UserDatabaseRealm".equalsIgnoreCase(type)) {
            createUserDatabaseRealm(session, parent);
        } else if ("JNDIRealm".equalsIgnoreCase(type)) {
            createJNDIRealm(session, parent);
        } else if ("MemoryRealm".equalsIgnoreCase(type)) {
            createMemoryRealm(session, parent);
        } else {
            //JDBC
            createJDBCRealm(session, parent);
        }
        // Forward to the realm display page
        return (mapping.findForward(type));

    }

    private void createUserDatabaseRealm(HttpSession session, String parent) {

        UserDatabaseRealmForm realmFm = new UserDatabaseRealmForm();
        session.setAttribute("userDatabaseRealmForm", realmFm);
        realmFm.setAdminAction("Create");
        realmFm.setObjectName("");
        realmFm.setParentObjectName(parent);
        String realmType = "UserDatabaseRealm";
        realmFm.setNodeLabel("Realm (" + realmType + ")");
        realmFm.setRealmType(realmType);
        realmFm.setDebugLvl("0");
        realmFm.setResource("");
        realmFm.setDebugLvlVals(Lists.getDebugLevels());
        realmFm.setRealmTypeVals(types);
    }

    private void createJNDIRealm(HttpSession session, String parent) {

        JNDIRealmForm realmFm = new JNDIRealmForm();
        session.setAttribute("jndiRealmForm", realmFm);
        realmFm.setAdminAction("Create");
        realmFm.setObjectName("");
        realmFm.setParentObjectName(parent);
        String realmType = "JNDIRealm";
        realmFm.setNodeLabel("Realm (" + realmType + ")");
        realmFm.setRealmType(realmType);
        realmFm.setDebugLvl("0");
        realmFm.setDigest("");
        realmFm.setRoleBase("");
        realmFm.setUserSubtree("false");
        realmFm.setRoleSubtree("false");
        realmFm.setRolePattern("");
        realmFm.setUserRoleName("");
        realmFm.setRoleName("");
        realmFm.setRoleBase("");
        realmFm.setContextFactory("");
        realmFm.setUserBase("");
        realmFm.setUserPattern("");
        realmFm.setUserSearch("");
        realmFm.setUserPassword("");
        realmFm.setConnectionName("");
        realmFm.setConnectionPassword("");
        realmFm.setConnectionURL("");
        realmFm.setDebugLvlVals(Lists.getDebugLevels());
        realmFm.setSearchVals(Lists.getBooleanValues());
        realmFm.setRealmTypeVals(types);
    }

    private void createMemoryRealm(HttpSession session, String parent) {

        MemoryRealmForm realmFm = new MemoryRealmForm();
        session.setAttribute("memoryRealmForm", realmFm);
        realmFm.setAdminAction("Create");
        realmFm.setObjectName("");
        realmFm.setParentObjectName(parent);
        String realmType = "MemoryRealm";
        realmFm.setNodeLabel("Realm (" + realmType + ")");
        realmFm.setRealmType(realmType);
        realmFm.setDebugLvl("0");
        realmFm.setPathName("");
        realmFm.setDebugLvlVals(Lists.getDebugLevels());
        realmFm.setRealmTypeVals(types);
    }

    private void createJDBCRealm(HttpSession session, String parent) {

        JDBCRealmForm realmFm = new JDBCRealmForm();
        session.setAttribute("jdbcRealmForm", realmFm);
        realmFm.setAdminAction("Create");
        realmFm.setObjectName("");
        realmFm.setParentObjectName(parent);
        String realmType = "JDBCRealm";
        realmFm.setNodeLabel("Realm (" + realmType + ")");
        realmFm.setRealmType(realmType);
        realmFm.setDebugLvl("0");
        realmFm.setDigest("");
        realmFm.setDriver("");
        realmFm.setRoleNameCol("");
        realmFm.setPasswordCol("");
        realmFm.setUserTable("");
        realmFm.setRoleTable("");
        realmFm.setConnectionName("");
        realmFm.setConnectionPassword("");
        realmFm.setConnectionURL("");
        realmFm.setDebugLvlVals(Lists.getDebugLevels());
        realmFm.setRealmTypeVals(types);
    }



}
