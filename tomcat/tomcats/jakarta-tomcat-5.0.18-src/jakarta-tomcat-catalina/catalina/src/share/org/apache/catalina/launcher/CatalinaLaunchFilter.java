/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/catalina/src/share/org/apache/catalina/launcher/CatalinaLaunchFilter.java,v 1.1 2002/08/01 20:29:20 patrickl Exp $
 * $Revision: 1.1 $
 * $Date: 2002/08/01 20:29:20 $
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
 * [Additional notices, if required by prior licensing conditions]
 *
 */


package org.apache.catalina.launcher;


import java.util.ArrayList;
import org.apache.commons.launcher.LaunchCommand;
import org.apache.commons.launcher.LaunchFilter;
import org.apache.tools.ant.BuildException;


/**
 * This class implements the LaunchFilter interface. This class is designed to
 * unconditionally force the "waitforchild" attribute for certain Catalina
 * applications to true.
 *
 * @author Patrick Luby
 */
public class CatalinaLaunchFilter implements LaunchFilter {

    //----------------------------------------------------------- Static Fields

    /**
     * The Catalina bootstrap class name.
     */
    private static String CATALINA_BOOTSTRAP_CLASS_NAME = "org.apache.catalina.startup.Bootstrap";

    //----------------------------------------------------------------- Methods

    /**
     * This method allows dynamic configuration and error checking of the
     * attributes and nested elements in a "launch" task that is launching a
     * Catalina application. This method evaluates the nested command line
     * arguments and, depending on which class is specified in the task's
     * "classname" attribute, may force the application to run
     * in the foreground by forcing the "waitforchild" attribute to "true".
     *
     * @param launchCommand a configured instance of the {@link LaunchTask}
     *  class
     * @throws BuildException if any errors occur
     */
    public void filter(LaunchCommand launchCommand) throws BuildException {

        // Get attributes
        String mainClassName = launchCommand.getClassname();
        boolean waitForChild = launchCommand.getWaitforchild();
        ArrayList argsList = launchCommand.getArgs();
        String[] args = (String[])argsList.toArray(new String[argsList.size()]);

        // Evaluate main class
        if (CatalinaLaunchFilter.CATALINA_BOOTSTRAP_CLASS_NAME.equals(mainClassName)) {
            // If "start" is not the last argument, make "waitforchild" true
            if (args.length == 0 || !"start".equals(args[args.length - 1])) {
                launchCommand.setWaitforchild(true);
                return;
            }

            // If "start" is the last argument, make sure that all of the
            // preceding arguments are OK for running in the background
            for (int i = 0; i < args.length - 1; i++) {
                if ("-config".equals(args[i])) {
                    // Skip next argument since it should be a file
                    if (args.length > i + 1) {
                        i++;
                    } else {
                        launchCommand.setWaitforchild(true);
                        return;
                    }
                } else if ("-debug".equals(args[i])) {
                    // Do nothing
                } else if ("-nonaming".equals(args[i])) {
                    // Do nothing
                } else {
                     launchCommand.setWaitforchild(true);
                     return;
                }
            }
        }

    }

}
