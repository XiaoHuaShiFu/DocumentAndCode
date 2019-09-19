/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/server/ServerForm.java,v 1.1 2002/04/04 21:52:25 manveen Exp $
 * $Revision: 1.1 $
 * $Date: 2002/04/04 21:52:25 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * 4. The names "The Jakarta Project", "Struts", and "Apache Software
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

package org.apache.webapp.admin.server;


import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.webapp.admin.ApplicationServlet;
import org.apache.webapp.admin.TomcatTreeBuilder;

import java.util.List;

/**
 * Form bean for the server form page.  
 * @author Patrick Luby
 * @author Manveen Kaur
 * @version $Revision: 1.1 $ $Date: 2002/04/04 21:52:25 $
 */

public final class ServerForm extends ActionForm {
    
    // ----------------------------------------------------- Instance Variables
    
    /**
     * The text for the node label.
     */
    private String nodeLabel = null;
    
    /**
     * The text for the port number.
     */    
    private String portNumberText = "8080";
    
    /**
     * The text for the debug level.
     */
    private String debugLvl = "0";

    /**
     * The text for the shutdown text.
     */    
    private String shutdownText = null;
    
    private List debugLvlVals = null;
    
    // ------------------------------------------------------------- Properties
    /**
     * Return the label of the node that was clicked.
     */
    public String getNodeLabel() {
        
        return this.nodeLabel;
        
    }
    
    /**
     * Set the node label.
     */
    public void setNodeLabel(String nodeLabel) {
        
        this.nodeLabel = nodeLabel;
        
    }    
    
    /**
     * Return the debugVals.
     */
    public List getDebugLvlVals() {
        
        return this.debugLvlVals;
        
    }
    
    /**
     * Set the debugVals.
     */
    public void setDebugLvlVals(List debugLvlVals) {
        
        this.debugLvlVals = debugLvlVals;
        
    }
        
    /**
     * Return the portNumberText.
     */
    public String getPortNumberText() {
        
        return this.portNumberText;
        
    }
    
    /**
     * Set the portNumberText.
     */
    public void setPortNumberText(String portNumberText) {
        
        this.portNumberText = portNumberText;
        
    }
    
    /**
     * Return the Debug Level Text.
     */
    public String getDebugLvl() {
        
        return this.debugLvl;
        
    }
    
    /**
     * Set the Debug Level Text.
     */
    public void setDebugLvl(String debugLvl) {
        
        this.debugLvl = debugLvl;
        
    }
    
    /**
     * Return the Shutdown Text.
     */
    public String getShutdownText() {
        
        return this.shutdownText;
        
    }
    
    /**
     * Set the Shut down  Text.
     */
    public void setShutdownText(String shutdownText) {
        
        this.shutdownText = shutdownText;
        
    }
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        this.portNumberText = null;
        this.debugLvl = "0";
        this.shutdownText = null;
        
    }
    
    
    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public ActionErrors validate(ActionMapping mapping,
    HttpServletRequest request) {
        
        ActionErrors errors = new ActionErrors();
        
        String submit = request.getParameter("submit");
        if (submit != null) {
            
            // check for portNumber -- must not be blank, must be in
            // the range 1 to 65535.
            
            if ((portNumberText == null) || (portNumberText.length() < 1)) {
                errors.add("portNumberText",
                new ActionError("error.portNumber.required"));
            } else {
                try {
                    int port = Integer.parseInt(portNumberText);
                    if ((port <= 0) || (port >65535 ))
                        errors.add("portNumberText", 
                            new ActionError("error.portNumber.range"));
                } catch (NumberFormatException e) {
                    errors.add("portNumberText", 
                        new ActionError("error.portNumber.format"));
                }
            }
        
            // shutdown text can be any non-empty string of atleast 6 characters.
            
            if ((shutdownText == null) || (shutdownText.length() < 7))
                errors.add("shutdownText",
                new ActionError("error.shutdownText.length"));
            
        }
        
        return errors;
        
    }
    
}
