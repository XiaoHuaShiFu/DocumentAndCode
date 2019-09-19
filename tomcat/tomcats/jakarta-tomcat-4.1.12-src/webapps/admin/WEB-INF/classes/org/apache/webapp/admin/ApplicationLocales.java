/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/ApplicationLocales.java,v 1.1 2001/10/25 16:37:38 craigmcc Exp $
 * $Revision: 1.1 $
 * $Date: 2001/10/25 16:37:38 $
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


package org.apache.webapp.admin;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;


/**
 * Class to hold the Locales supported by this package.
 *
 * @author Patrick Luby
 * @author Craig R. McClanahan
 * @version $Revision: 1.1 $ $Date: 2001/10/25 16:37:38 $
 */

public final class ApplicationLocales {


    // ----------------------------------------------------------- Constructors


    /**
     * Initialize the set of Locales supported by this application.
     *
     * @param servlet ActionServlet we are associated with
     */
    public ApplicationLocales(ActionServlet servlet) {

        super();
        Locale list[] = Locale.getAvailableLocales();
        MessageResources resources = servlet.getResources();
        if (resources == null)
            return;
        String config = resources.getConfig();
        if (config == null)
            return;

        for (int i = 0; i < list.length; i++) {
            ResourceBundle bundle =
                ResourceBundle.getBundle(config, list[i]);
            if (bundle == null)
                continue;
            if (list[i].equals(bundle.getLocale())) {
                localeLabels.add(list[i].getDisplayName());
                localeValues.add(list[i].toString());
                supportedLocales.add(list[i]);
            }
        }

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The set of Locale labels supported by this application.
     */
    protected ArrayList localeLabels = new ArrayList();


    /**
     * The set of Locale values supported by this application.
     */
    protected ArrayList localeValues = new ArrayList();


    /**
     * The set of supported Locales for this application.
     */
    protected ArrayList supportedLocales = new ArrayList();


    // --------------------------------------------------------- Public Methods


    /**
     * Return the set of Locale labels supported by this application.
     */
    public List getLocaleLabels() {

        return (localeLabels);

    }


    /**
     * Return the set of Locale values supported by this application.
     */
    public List getLocaleValues() {

        return (localeValues);

    }


    /**
     * Return the set of Locales supported by this application.
     */
    public List getSupportedLocales() {

        return (supportedLocales);

    }


}
