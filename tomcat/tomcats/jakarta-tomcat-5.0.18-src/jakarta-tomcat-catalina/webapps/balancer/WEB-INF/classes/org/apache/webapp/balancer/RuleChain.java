/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
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
 * Portions of this software are based upon public domain software
 * originally written at the National Center for Supercomputing Applications,
 * University of Illinois, Urbana-Champaign.
 */
package org.apache.webapp.balancer;

import java.net.URL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * A RuleChain is a list of rules
 * considered in order.  The first
 * rule to succeed stops the evaluation
 * of rules.
 *
 * @author Yoav Shapira
 */
public class RuleChain {
    /**
     * The list of rules to evaluate.
     */
    private List rules;

    /**
     * Constructor.
     */
    public RuleChain() {
        rules = new ArrayList();
    }

    /**
     * Returns the list of rules
     * to evaluate.
     *
     * @return List
     */
    protected List getRules() {
        return rules;
    }

    /**
     * Returns an iterator over
     * the list of rules to evaluate.
     *
     * @return Iterator
     */
    protected Iterator getRuleIterator() {
        return getRules().iterator();
    }

    /**
     * Adds a rule to evaluate.
     *
     * @param theRule The rule to add
     */
    public void addRule(Rule theRule) {
        if (theRule == null) {
            throw new IllegalArgumentException("The rule cannot be null.");
        } else {
            getRules().add(theRule);
        }
    }

    /**
     * Evaluates the given request to see if
     * any of the rules matches.  Returns the
     * redirect URL for the first matching
     * rule.  Returns null if no rules match
     * the request.
     *
     * @param request The request
     * @return URL The first matching rule URL
     * @see Rule#matches(HttpServletRequest)
     */
    public URL evaluate(HttpServletRequest request) {
        Iterator iter = getRuleIterator();

        Rule currentRule = null;
        boolean currentMatches = false;

        while (iter.hasNext()) {
            currentRule = (Rule) iter.next();
            currentMatches = currentRule.matches(request);

            if (currentMatches) {
                try {
                    return new URL(currentRule.getRedirectUrl());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return null;
    }

    /**
     * Returns a String representation of this object.
     *
     * @return String
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[");
        buffer.append(getClass().getName());
        buffer.append(": ");

        Iterator iter = getRuleIterator();
        Rule currentRule = null;

        while (iter.hasNext()) {
            currentRule = (Rule) iter.next();
            buffer.append(currentRule);

            if (iter.hasNext()) {
                buffer.append(", ");
            }
        }

        buffer.append("]");

        return buffer.toString();
    }
}


// End of file: RuleChain.java
