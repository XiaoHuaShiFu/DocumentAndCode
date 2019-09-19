/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/logger/AddLoggerAction.java,v 1.1 2002/03/21 17:11:28 manveen Exp $
 * $Revision: 1.1 $
 * $Date: 2002/03/21 17:11:28 $
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

package org.apache.webapp.admin.logger;

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
 * The <code>Action</code> that sets up <em>Add Logger</em> transactions.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.1 $ $Date: 2002/03/21 17:11:28 $
 */

public class AddLoggerAction extends Action {
        
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

        // Acquire the resources that we need
        HttpSession session = request.getSession();
        Locale locale = (Locale) session.getAttribute(Action.LOCALE_KEY);
        if (resources == null) {
            resources = getServlet().getResources();
        }
        
        // Fill in the form values for display and editing
        LoggerForm loggerFm = new LoggerForm();
        session.setAttribute("loggerForm", loggerFm);
        loggerFm.setAdminAction("Create");
        loggerFm.setObjectName("");
        String parent = request.getParameter("parent");
        loggerFm.setParentObjectName(parent);
        String type = request.getParameter("type");
        if (type == null)
            type = "FileLogger";    // default type is FileLogger
        loggerFm.setLoggerType(type);
        loggerFm.setDebugLvl("0");
        loggerFm.setDebugLvlVals(Lists.getDebugLevels());
        loggerFm.setVerbosityLvlVals(Lists.getVerbosityLevels());        
        loggerFm.setBooleanVals(Lists.getBooleanValues());        
      
        String loggerTypes[] = new String[3];
        loggerTypes[0] = "FileLogger";
        loggerTypes[1] = "SystemErrLogger";
        loggerTypes[2] = "SystemOutLogger";
        
        ArrayList types = new ArrayList();    
        // the first element in the select list should be the type selected
        types.add(new LabelValueBean(type,
                "/admin/AddLogger.do?parent=" + URLEncoder.encode(parent) 
                + "&type=" + type));        
        for (int i=0; i< loggerTypes.length; i++) {
            if (!type.equalsIgnoreCase(loggerTypes[i])) {
                types.add(new LabelValueBean(loggerTypes[i],
                "/admin/AddLogger.do?parent=" + URLEncoder.encode(parent) 
                + "&type=" + loggerTypes[i]));        
            }
        }
        loggerFm.setLoggerTypeVals(types);
        
        // Forward to the logger display page
        return (mapping.findForward("Logger"));
        
    }
    
}
