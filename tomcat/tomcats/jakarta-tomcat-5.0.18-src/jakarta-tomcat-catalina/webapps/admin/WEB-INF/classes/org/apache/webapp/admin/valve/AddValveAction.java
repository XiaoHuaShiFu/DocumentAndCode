/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/valve/AddValveAction.java,v 1.6 2003/05/13 08:33:14 amyroh Exp $
 * $Revision: 1.6 $
 * $Date: 2003/05/13 08:33:14 $
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

package org.apache.webapp.admin.valve;

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
 * The <code>Action</code> that sets up <em>Add Valve</em> transactions.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.6 $ $Date: 2003/05/13 08:33:14 $
 */

public class AddValveAction extends Action {
        
    /**
     * The MessageResources we will be retrieving messages from.
     */
    private MessageResources resources = null;

    // the list for types of valves
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
        
        String valveTypes[] = new String[5];
        valveTypes[0] = "AccessLogValve";
        valveTypes[1] = "RemoteAddrValve";
        valveTypes[2] = "RemoteHostValve";
        valveTypes[3] = "RequestDumperValve";       
        valveTypes[4] = "SingleSignOn";
                     
        String parent = request.getParameter("parent");
        String type = request.getParameter("type");        
        if (type == null) 
            type = "AccessLogValve";    // default type is AccessLog
        
        types = new ArrayList();    
        // the first element in the select list should be the type selected
        types.add(new LabelValueBean(type,
                "AddValve.do?parent=" + URLEncoder.encode(parent) 
                + "&type=" + type));        
        for (int i=0; i< valveTypes.length; i++) {
            if (!type.equalsIgnoreCase(valveTypes[i])) {
                types.add(new LabelValueBean(valveTypes[i],
                "AddValve.do?parent=" + URLEncoder.encode(parent) 
                + "&type=" + valveTypes[i]));        
            }
        }
       
        if ("AccessLogValve".equalsIgnoreCase(type)) {
            createAccessLogger(session, parent);
        } else if ("RemoteAddrValve".equalsIgnoreCase(type)) {
            createRemoteAddrValve(session, parent);
        } else if ("RemoteHostValve".equalsIgnoreCase(type)) {
            createRemoteHostValve(session, parent);
        } else if ("RequestDumperValve".equalsIgnoreCase(type)) {
            createRequestDumperValve(session, parent);
        } else {
            //SingleSignOn
            createSingleSignOnValve(session, parent);
        }
        // Forward to the valve display page
        return (mapping.findForward(type));
        
    }

    private void createAccessLogger(HttpSession session, String parent) {

        AccessLogValveForm valveFm = new AccessLogValveForm();
        session.setAttribute("accessLogValveForm", valveFm);
        valveFm.setAdminAction("Create");
        valveFm.setObjectName("");
        valveFm.setParentObjectName(parent);
        String valveType = "AccessLogValve";
        valveFm.setNodeLabel("Valve (" + valveType + ")");
        valveFm.setValveType(valveType);
        valveFm.setDebugLvl("0");
        valveFm.setPattern("");
        valveFm.setDirectory("logs");
        valveFm.setPrefix("access_log.");
        valveFm.setSuffix("");
        valveFm.setResolveHosts("false");
        valveFm.setRotatable("true");
        valveFm.setDebugLvlVals(Lists.getDebugLevels());
        valveFm.setBooleanVals(Lists.getBooleanValues());
        valveFm.setValveTypeVals(types);        
    }

    private void createRemoteAddrValve(HttpSession session, String parent) {

        RemoteAddrValveForm valveFm = new RemoteAddrValveForm();
        session.setAttribute("remoteAddrValveForm", valveFm);
        valveFm.setAdminAction("Create");
        valveFm.setObjectName("");
        valveFm.setParentObjectName(parent);
        String valveType = "RemoteAddrValve";
        valveFm.setNodeLabel("Valve (" + valveType + ")");
        valveFm.setValveType(valveType);
        valveFm.setAllow("");
        valveFm.setDeny("");
        valveFm.setValveTypeVals(types);        
    }

    private void createRemoteHostValve(HttpSession session, String parent) {

        RemoteHostValveForm valveFm = new RemoteHostValveForm();
        session.setAttribute("remoteHostValveForm", valveFm);
        valveFm.setAdminAction("Create");
        valveFm.setObjectName("");
        valveFm.setParentObjectName(parent);
        String valveType = "RemoteHostValve";
        valveFm.setNodeLabel("Valve (" + valveType + ")");
        valveFm.setValveType(valveType);
        valveFm.setAllow("");
        valveFm.setDeny("");
        valveFm.setValveTypeVals(types);        
    }

    private void createRequestDumperValve(HttpSession session, String parent) {

        RequestDumperValveForm valveFm = new RequestDumperValveForm();
        session.setAttribute("requestDumperValveForm", valveFm);
        valveFm.setAdminAction("Create");
        valveFm.setObjectName("");
        valveFm.setParentObjectName(parent);
        String valveType = "RequestDumperValve";
        valveFm.setNodeLabel("Valve (" + valveType + ")");
        valveFm.setValveType(valveType);
        valveFm.setDebugLvl("0");
        valveFm.setDebugLvlVals(Lists.getDebugLevels()); 
        valveFm.setValveTypeVals(types);        
    }

    private void createSingleSignOnValve(HttpSession session, String parent) {

        SingleSignOnValveForm valveFm = new SingleSignOnValveForm();
        session.setAttribute("singleSignOnValveForm", valveFm);
        valveFm.setAdminAction("Create");
        valveFm.setObjectName("");
        valveFm.setParentObjectName(parent);
        String valveType = "SingleSignOn";
        valveFm.setNodeLabel("Valve (" + valveType + ")");
        valveFm.setValveType(valveType);
        valveFm.setDebugLvl("0");
        valveFm.setDebugLvlVals(Lists.getDebugLevels());    
        valveFm.setValveTypeVals(types);        
    }

}
