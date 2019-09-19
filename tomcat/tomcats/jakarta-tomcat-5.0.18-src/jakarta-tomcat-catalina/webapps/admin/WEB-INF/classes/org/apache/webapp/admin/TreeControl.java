/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/TreeControl.java,v 1.1.1.1 2002/07/18 16:48:22 remm Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/07/18 16:48:22 $
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


package org.apache.webapp.admin;


import java.io.Serializable;
import java.util.HashMap;


/**
 * <p>The overall data structure representing a <em>tree control</em>
 * that can be rendered by the <code>TreeControlTag</code> custom tag.
 * Each node of the tree is represented by an instance of
 * <code>TreeControlNode</code>.</p>
 *
 * @author Jazmin Jonson
 * @author Craig R. McClanahan
 * @version $Revision: 1.1.1.1 $ $Date: 2002/07/18 16:48:22 $
 */

public class TreeControl implements Serializable {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new instance with no predefined root node.
     */
    public TreeControl() {

        super();
        setRoot(null);

    }


    /**
     * Construct a new instance with the specified root node.
     *
     * @param root The new root node
     */
    public TreeControl(TreeControlNode root) {

        super();
        setRoot(root);

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The collection of nodes that represent this tree, keyed by name.
     */
    protected HashMap registry = new HashMap();


    /**
     * The most recently selected node.
     */
    protected TreeControlNode selected = null;


    // ------------------------------------------------------------- Properties


    /**
     * The root node of the entire tree.
     */
    protected TreeControlNode root = null;

    public TreeControlNode getRoot() {
        return (this.root);
    }

    protected void setRoot(TreeControlNode root) {
        if (this.root != null)
            removeNode(this.root);
        if (root != null)
            addNode(root);
        root.setLast(true);
        this.root = root;
    }


    /**
     * The current displayable "width" of this tree (that is, the maximum
     * depth of the visible part of the tree).
     */
    public int getWidth() {

        if (root == null)
            return (0);
        else
            return (getWidth(root));

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Find and return the <code>TreeControlNode</code> for the specified
     * node name, if it exists; otherwise, return <code>null</code>.
     *
     * @param name Name of the <code>TreeControlNode</code> to be returned
     */
    public TreeControlNode findNode(String name) {

        synchronized (registry) {
            return ((TreeControlNode) registry.get(name));
        }

    }


    /**
     * Mark the specified node as the one-and-only currently selected one,
     * deselecting any previous node that was so marked.
     *
     * @param node Name of the node to mark as selected, or <code>null</code>
     *  if there should be no currently selected node
     */
    public void selectNode(String name) {

        if (selected != null) {
            selected.setSelected(false);
            selected = null;
        }
        selected = findNode(name);
        if (selected != null)
            selected.setSelected(true);

    }


    // -------------------------------------------------------- Package Methods


    /**
     * Register the specified node in our registry of the complete tree.
     *
     * @param node The <code>TreeControlNode</code> to be registered
     *
     * @exception IllegalArgumentException if the name of this node
     *  is not unique
     */
    void addNode(TreeControlNode node) throws IllegalArgumentException {

        synchronized (registry) {
            String name = node.getName();
            if (registry.containsKey(name))
                throw new IllegalArgumentException("Name '" + name +
                                                   "' is not unique");
            node.setTree(this);
            registry.put(name, node);
        }

    }


    /**
     * Calculate the width of the subtree below the specified node.
     *
     * @param node The node for which to calculate the width
     */
    int getWidth(TreeControlNode node) {

        int width = node.getWidth();
        if (!node.isExpanded())
            return (width);
        TreeControlNode children[] = node.findChildren();
        for (int i = 0; i < children.length; i++) {
            int current = getWidth(children[i]);
            if (current > width)
                width = current;
        }
        return (width);

    }


    /**
     * Deregister the specified node, as well as all child nodes of this
     * node, from our registry of the complete tree.  If this node is not
     * present, no action is taken.
     *
     * @param node The <code>TreeControlNode</code> to be deregistered
     */
    void removeNode(TreeControlNode node) {

        synchronized (registry) {
            TreeControlNode children[] = node.findChildren();
            for (int i = 0; i < children.length; i++)
                removeNode(children[i]);
            TreeControlNode parent = node.getParent();
            if (parent != null) {
                parent.removeChild(node);
            }
            node.setParent(null);
            node.setTree(null);
            if (node == this.root) {
                this.root = null;
            }
            registry.remove(node.getName());
        }

    }


}
