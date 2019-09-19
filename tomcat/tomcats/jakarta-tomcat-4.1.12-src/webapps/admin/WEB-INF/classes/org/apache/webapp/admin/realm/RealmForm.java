/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/realm/RealmForm.java,v 1.2 2002/07/19 00:23:18 amyroh Exp $
 * $Revision: 1.2 $
 * $Date: 2002/07/19 00:23:18 $
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

package org.apache.webapp.admin.realm;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import java.net.InetAddress;
import java.util.List;

import org.apache.webapp.admin.ApplicationServlet;
import org.apache.webapp.admin.LabelValueBean;

/**
 * Form bean for the generic realm page.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.2 $ $Date: 2002/07/19 00:23:18 $
 */

public class RealmForm extends ActionForm {
    
    // ----------------------------------------------------- Instance Variables
    
   /**
     * The administrative action represented by this form.
     */
    private String adminAction = "Edit";

    /**
     * The object name of the realm this bean refers to.
     */
    private String objectName = null;
    
    /**
     * The text for the realm type.
     */
    private String realmType = null;
        
    /**
     * The text for the node label.
     */
    private String nodeLabel = null;
    
    /**
     * The text for the debug level.
     */
    private String debugLvl = "0";

    /**
     * The object name of the parent of this realm.
     */
    private String parentObjectName = null;
        
    /**
     * Set of valid values for debug level.
     */
    private List debugLvlVals = null;

    /**
     * Set of valid values for realms.
     */
    private List realmTypeVals = null;

    /**
     * The text for whether "delete this realm" operation is allowed
     * on the realm or not.
     */
    private String allowDeletion = null;

    // ------------------------------------------------------------- Properties

   /**
     * Return the administrative action represented by this form.
     */
    public String getAdminAction() {

        return this.adminAction;

    }

    /**
     * Set the administrative action represented by this form.
     */
    public void setAdminAction(String adminAction) {

        this.adminAction = adminAction;

    }

    /**
     * Return the Object Name.
     */
    public String getObjectName() {
        
        return this.objectName;
        
    }
    
    /**
     * Set the Object Name.
     */
    public void setObjectName(String objectName) {
        
        this.objectName = objectName;
        
    }
    
    /**
     * Return the realm type.
     */
    public String getRealmType() {
        
        return this.realmType;
        
    }
    
    /**
     * Set the realm type.
     */
    public void setRealmType(String realmType) {
        
        this.realmType = realmType;
        
    }
    
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
     * Return the parent object name of the realm this bean refers to.
     */
    public String getParentObjectName() {

        return this.parentObjectName;

    }


    /**
     * Set the parent object name of the realm this bean refers to.
     */
    public void setParentObjectName(String parentObjectName) {

        this.parentObjectName = parentObjectName;

    }
    
        
   /**
     * Return the realmTypeVals.
     */
    public List getRealmTypeVals() {
        
        return this.realmTypeVals;
        
    }
    
    /**
     * Set the realmTypeVals.
     */
    public void setRealmTypeVals(List realmTypeVals) {
        
        this.realmTypeVals = realmTypeVals;
        
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
     * Return the allow deletion value.
     */
    public String getAllowDeletion() {
        
        return this.allowDeletion;
        
    }
    
    /**
     * Set the allow Deletion value.
     */
    public void setAllowDeletion(String allowDeletion) {
        
        this.allowDeletion = allowDeletion;
        
    }
   
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        objectName = null;
        debugLvl = "0";
        adminAction = "Edit";
        
    }

}
