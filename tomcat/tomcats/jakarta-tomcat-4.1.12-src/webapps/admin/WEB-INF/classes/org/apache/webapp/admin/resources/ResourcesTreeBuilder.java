/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/resources/ResourcesTreeBuilder.java,v 1.10 2002/06/21 00:40:59 amyroh Exp $
 * $Revision: 1.10 $
 * $Date: 2002/06/21 00:40:59 $
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

package org.apache.webapp.admin.resources;

import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.Action;
import org.apache.struts.util.MessageResources;
import org.apache.webapp.admin.ApplicationServlet;
import org.apache.webapp.admin.TreeBuilder;
import org.apache.webapp.admin.TreeControl;
import org.apache.webapp.admin.TreeControlNode;


/**
 * Implementation of <code>TreeBuilder</code> that adds the nodes required
 * for administering the resources (data sources).
 *
 * @author Manveen Kaur
 * @version $Revision: 1.10 $ $Date: 2002/06/21 00:40:59 $
 * @since 4.1
 */

public class ResourcesTreeBuilder implements TreeBuilder {


    // ----------------------------------------------------- Instance Variables


    // ---------------------------------------------------- TreeBuilder Methods


    /**
     * Add the required nodes to the specified <code>treeControl</code>
     * instance.
     *
     * @param treeControl The <code>TreeControl</code> to which we should
     *  add our nodes
     * @param servlet The controller servlet for the admin application
     * @param request The servlet request we are processing
     */
    public void buildTree(TreeControl treeControl,
                          ApplicationServlet servlet,
                          HttpServletRequest request) {

        MessageResources resources = (MessageResources)
            servlet.getServletContext().getAttribute(Action.MESSAGES_KEY);
        addSubtree(treeControl.getRoot(), resources);

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Add the subtree of nodes required for user administration.
     *
     * @param root The root node of our tree control
     * @param resources The MessageResources for our localized messages
     *  messages
     */
    protected void addSubtree(TreeControlNode root,
                              MessageResources resources) {

        TreeControlNode subtree = new TreeControlNode
            ("Global Resource Administration",
             "folder_16_pad.gif",
             resources.getMessage("resources.treeBuilder.subtreeNode"),
             null,
             "content",
             true);        
        TreeControlNode datasources = new TreeControlNode
            ("Globally Administer Data Sources",
             "Datasource.gif",
             resources.getMessage("resources.treeBuilder.datasources"),
             "resources/listDataSources.do?resourcetype=Global&forward=" +
             URLEncoder.encode("DataSources List Setup"),
             "content",
             false);
        TreeControlNode mailsessions = new TreeControlNode
            ("Globally Administer Mail Sessions ",
            "Mailsession.gif",
            resources.getMessage("resources.treeBuilder.mailsessions"),
            "resources/listMailSessions.do?resourcetype=Global&forward=" +
            URLEncoder.encode("MailSessions List Setup"),
            "content",
            false);
        TreeControlNode userdbs = new TreeControlNode
            ("Globally Administer UserDatabase Entries",
             "Realm.gif",
             resources.getMessage("resources.treeBuilder.databases"),
             "resources/listUserDatabases.do?forward=" +
             URLEncoder.encode("UserDatabases List Setup"),
             "content",
             false);
        TreeControlNode envs = new TreeControlNode
            ("Globally Administer Environment Entries",
             "EnvironmentEntries.gif",
            resources.getMessage("resources.env.entries"),
            "resources/listEnvEntries.do?resourcetype=Global&forward=" +
            URLEncoder.encode("EnvEntries List Setup"),
            "content",
            false);
        root.addChild(subtree);
        subtree.addChild(datasources);
        subtree.addChild(mailsessions);
        subtree.addChild(envs);
        subtree.addChild(userdbs);
    }

}
