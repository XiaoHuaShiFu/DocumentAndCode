/*
 * $Header: /home/cvs/jakarta-tomcat-jasper/jasper2/src/share/org/apache/jasper/compiler/Localizer.java,v 1.3 2003/09/02 21:39:58 remm Exp $
 * $Revision: 1.3 $
 * $Date: 2003/09/02 21:39:58 $
 *
 * ====================================================================
 * 
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
package org.apache.jasper.compiler;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Class responsible for converting error codes to corresponding localized
 * error messages.
 *
 * @author Jan Luehe
 */
public class Localizer {

    private static final ResourceBundle bundle = ResourceBundle.getBundle(
        "org.apache.jasper.resources.messages");

    /*
     * Returns the localized error message corresponding to the given error
     * code.
     *
     * If the given error code is not defined in the resource bundle for
     * localized error messages, it is used as the error message.
     *
     * @param errCode Error code to localize
     * 
     * @return Localized error message
     */
    public static String getMessage(String errCode) {
	String errMsg = errCode;
	try {
	    errMsg = bundle.getString(errCode);
	} catch (MissingResourceException e) {
	}
	return errMsg;
    }

    /* 
     * Returns the localized error message corresponding to the given error
     * code.
     *
     * If the given error code is not defined in the resource bundle for
     * localized error messages, it is used as the error message.
     *
     * @param errCode Error code to localize
     * @param arg Argument for parametric replacement
     *
     * @return Localized error message
     */
    public static String getMessage(String errCode, String arg) {
	return getMessage(errCode, new Object[] {arg});
    }

    /* 
     * Returns the localized error message corresponding to the given error
     * code.
     *
     * If the given error code is not defined in the resource bundle for
     * localized error messages, it is used as the error message.
     *
     * @param errCode Error code to localize
     * @param arg1 First argument for parametric replacement
     * @param arg2 Second argument for parametric replacement
     *
     * @return Localized error message
     */
    public static String getMessage(String errCode, String arg1, String arg2) {
	return getMessage(errCode, new Object[] {arg1, arg2});
    }
    
    /* 
     * Returns the localized error message corresponding to the given error
     * code.
     *
     * If the given error code is not defined in the resource bundle for
     * localized error messages, it is used as the error message.
     *
     * @param errCode Error code to localize
     * @param arg1 First argument for parametric replacement
     * @param arg2 Second argument for parametric replacement
     * @param arg3 Third argument for parametric replacement
     *
     * @return Localized error message
     */
    public static String getMessage(String errCode, String arg1, String arg2,
				    String arg3) {
	return getMessage(errCode, new Object[] {arg1, arg2, arg3});
    }

    /* 
     * Returns the localized error message corresponding to the given error
     * code.
     *
     * If the given error code is not defined in the resource bundle for
     * localized error messages, it is used as the error message.
     *
     * @param errCode Error code to localize
     * @param arg1 First argument for parametric replacement
     * @param arg2 Second argument for parametric replacement
     * @param arg3 Third argument for parametric replacement
     * @param arg4 Fourth argument for parametric replacement
     *
     * @return Localized error message
     */
    public static String getMessage(String errCode, String arg1, String arg2,
				    String arg3, String arg4) {
	return getMessage(errCode, new Object[] {arg1, arg2, arg3, arg4});
    }

    /*
     * Returns the localized error message corresponding to the given error
     * code.
     *
     * If the given error code is not defined in the resource bundle for
     * localized error messages, it is used as the error message.
     *
     * @param errCode Error code to localize
     * @param args Arguments for parametric replacement
     *
     * @return Localized error message
     */
    public static String getMessage(String errCode, Object[] args) {
	String errMsg = errCode;
	try {
	    errMsg = bundle.getString(errCode);
	    if (args != null) {
		MessageFormat formatter = new MessageFormat(errMsg);
		errMsg = formatter.format(args);
	    }
	} catch (MissingResourceException e) {
	}
	
	return errMsg;
    }
}
