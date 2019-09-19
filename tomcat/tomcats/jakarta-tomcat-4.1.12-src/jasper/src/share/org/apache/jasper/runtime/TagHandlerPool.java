/*
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
 */ 

package org.apache.jasper.runtime;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

/**
 * Pool of tag handlers that can be reused.
 *
 * @author Jan Luehe
 */
public class TagHandlerPool {
    
    private static final int MAX_POOL_SIZE = 5;

    private Tag[] handlers;

    // index of next available tag handler
    private int current;

    /**
     * Constructs a tag handler pool with the default capacity.
     */
    public TagHandlerPool() {
	this(MAX_POOL_SIZE);
    }

    /**
     * Constructs a tag handler pool with the given capacity.
     *
     * @param capacity Tag handler pool capacity
     */
    public TagHandlerPool(int capacity) {
	this.handlers = new Tag[capacity];
	this.current = -1;
    }

    /**
     * Gets the next available tag handler from this tag handler pool,
     * instantiating one if this tag handler pool is empty.
     *
     * @param handlerClass Tag handler class
     *
     * @return Reused or newly instantiated tag handler
     *
     * @throws JspException if a tag handler cannot be instantiated
     */
    public synchronized Tag get(Class handlerClass) throws JspException {
	Tag handler = null;

	if (current >= 0) {
	    handler = handlers[current--];
	} else {
	    try {
		return (Tag) handlerClass.newInstance();
	    } catch (Exception e) {
		throw new JspException(e.getMessage(), e);
	    }
	}

	return handler;
    }

    /**
     * Adds the given tag handler to this tag handler pool, unless this tag
     * handler pool has already reached its capacity, in which case the tag
     * handler's release() method is called.
     *
     * @param handler Tag handler to add to this tag handler pool
     */
    public synchronized void reuse(Tag handler) {
	if (current < (handlers.length - 1))
	    handlers[++current] = handler;
	else
	    handler.release();
    }

    /**
     * Calls the release() method of all available tag handlers in this tag
     * handler pool.
     */
    public synchronized void release() {
	for (int i=current; i>=0; i--) {
	    handlers[i].release();
	}
    }
}

