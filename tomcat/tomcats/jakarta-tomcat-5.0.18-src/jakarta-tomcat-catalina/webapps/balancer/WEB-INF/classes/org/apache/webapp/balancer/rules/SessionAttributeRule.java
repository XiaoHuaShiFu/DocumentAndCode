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
package org.apache.webapp.balancer.rules;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * This rule accepts or rejects requests
 * based on the presence of a attribute
 * in the request.
 *
 * @author Yoav Shapira
 */
public class SessionAttributeRule extends BaseRule {
    /**
     * The target attribute name (attribute
     * must be present for match to succeed).
     */
    private String attributeName;

    /**
     * The target attribute value.  This
     * is optional: null means any attribute
     * value is OK for a match.  A non-null
     * value will be matches exactly.
     */
    private Object attributeValue;

    /**
     * Sets the target attribute name.
     *
     * @param theAttributeName The attribute name
     */
    public void setAttributeName(String theAttributeName) {
        if (theAttributeName == null) {
            throw new IllegalArgumentException(
                "attributeName cannot be null.");
        } else {
            attributeName = theAttributeName;
        }
    }

    /**
     * Returns the target attribute name.
     *
     * @return String The target attribute name.
     */
    protected String getAttributeName() {
        return attributeName;
    }

    /**
     * Sets the attribute value, which may be null.
     *
     * @param theAttributeValue The attribute value
     */
    public void setAttributeValue(Object theAttributeValue) {
        attributeValue = theAttributeValue;
    }

    /**
     * Returns the target attribute value,
     * which may be null.
     *
     * @return Object The target attribute value
     */
    protected Object getAttributeValue() {
        return attributeValue;
    }

    /**
     * @see org.apache.webapp.balancer.Rule#matches(HttpServletRequest)
     */
    public boolean matches(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object actualAttributeValue = session.getAttribute(getAttributeName());

        if (actualAttributeValue == null) {
            return (getAttributeValue() == null);
        } else {
            return (actualAttributeValue.equals(getAttributeValue()));
        }
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

        buffer.append("Target attribute name: ");
        buffer.append(getAttributeName());
        buffer.append(" / ");

        buffer.append("Target attribute value: ");
        buffer.append(getAttributeValue());
        buffer.append(" / ");

        buffer.append("Redirect URL: ");
        buffer.append(getRedirectUrl());

        buffer.append("]");

        return buffer.toString();
    }
}


// End of file: SessionAttributeRule.java
