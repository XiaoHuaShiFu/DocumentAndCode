/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/webapps/manager/WEB-INF/classes/org/apache/catalina/manager/StatusTransformer.java,v 1.11 2003/12/23 19:23:10 remm Exp $
 * $Revision: 1.11 $
 * $Date: 2003/12/23 19:23:10 $
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


package org.apache.catalina.manager;

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.compat.JdkCompat;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

/**
 * This is a refactoring of the servlet to externalize
 * the output into a simple class. Although we could
 * use XSLT, that is unnecessarily complex.
 *
 * @author Peter Lin
 * @version $Revision: 1.11 $ $Date: 2003/12/23 19:23:10 $
 */

public class StatusTransformer {


    // --------------------------------------------------------- Public Methods


    public static void setContentType(HttpServletResponse response, 
                                      int mode) {
        if (mode == 0){
            response.setContentType("text/html;charset="+Constants.CHARSET);
        } else if (mode == 1){
            response.setContentType("text/xml;charset="+Constants.CHARSET);
        }
    }


    /**
     * Process a GET request for the specified resource.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet-specified error occurs
     */
    public static void writeHeader(PrintWriter writer, int mode) {
        if (mode == 0){
            // HTML Header Section
            writer.print(Constants.HTML_HEADER_SECTION);
        } else if (mode == 1){
            writer.write(Constants.XML_DECLARATION);
            writer.write
                (Constants.XML_STYLE);
            writer.write("<status>");
        }
    }


    /**
     * Write the header body. XML output doesn't bother
     * to output this stuff, since it's just title.
     * 
     * @param PrintWriter writer
     * @param Object[] args
     * @param int mode
     */
    public static void writeBody(PrintWriter writer, Object[] args, int mode) {
        if (mode == 0){
            writer.print(MessageFormat.format
                         (Constants.BODY_HEADER_SECTION, args));
        }
    }


    /**
     * Write the manager webapp information.
     * 
     * @param PrintWriter writer
     * @param Object[] args
     * @param int mode
     */
    public static void writeManager(PrintWriter writer, Object[] args, 
                                    int mode) {
        if (mode == 0){
            writer.print(MessageFormat.format(Constants.MANAGER_SECTION, args));
        }
    }


    public static void writePageHeading(PrintWriter writer, Object[] args, 
                                        int mode) {
        if (mode == 0){
            writer.print(MessageFormat.format
                         (Constants.SERVER_HEADER_SECTION, args));
        }
    }


    public static void writeServerInfo(PrintWriter writer, Object[] args, 
                                       int mode){
        if (mode == 0){
            writer.print(MessageFormat.format(Constants.SERVER_ROW_SECTION, args));
        }
    }


    /**
     * 
     */
    public static void writeFooter(PrintWriter writer, int mode) {
        if (mode == 0){
            // HTML Tail Section
            writer.print(Constants.HTML_TAIL_SECTION);
        } else if (mode == 1){
            writer.write("</status>");
        }
    }


    /**
     * Write the VM state. Mode 0 will generate HTML.
     * Mode 1 will generate XML.
     */
    public static void writeVMState(PrintWriter writer, int mode)
        throws Exception {

        if (mode == 0){
            writer.print("<h1>JVM</h1>");

            writer.print("<p>");
            writer.print(" Free memory: ");
            writer.print(formatSize
                         (new Long(Runtime.getRuntime().freeMemory()), true));
            writer.print(" Total memory: ");
            writer.print(formatSize
                         (new Long(Runtime.getRuntime().totalMemory()), true));
            writer.print(" Max memory: ");
            writer.print(formatSize
                         (new Long(JdkCompat.getJdkCompat().getMaxMemory()), 
                          true));
            writer.print("</p>");
        } else if (mode == 1){
            writer.write("<jvm>");

            writer.write("<memory");
            writer.write(" free='" + Runtime.getRuntime().freeMemory() + "'");
            writer.write(" total='" + Runtime.getRuntime().totalMemory() + "'");
            writer.write(" max='" + JdkCompat.getJdkCompat().getMaxMemory() + "'/>");

            writer.write("</jvm>");
        }

    }


    /**
     * Write connector state.
     */
    public static void writeConnectorState(PrintWriter writer, 
                                           ObjectName tpName, String name,
                                           MBeanServer mBeanServer,
                                           Vector globalRequestProcessors,
                                           Vector requestProcessors,
                                           int mode)
        throws Exception {

        if (mode == 0) {
            writer.print("<h1>");
            writer.print(name);
            writer.print("</h1>");

            writer.print("<p>");
            writer.print(" Max threads: ");
            writer.print(mBeanServer.getAttribute(tpName, "maxThreads"));
            writer.print(" Min spare threads: ");
            writer.print(mBeanServer.getAttribute(tpName, "minSpareThreads"));
            writer.print(" Max spare threads: ");
            writer.print(mBeanServer.getAttribute(tpName, "maxSpareThreads"));
            writer.print(" Current thread count: ");
            writer.print(mBeanServer.getAttribute(tpName, "currentThreadCount"));
            writer.print(" Current thread busy: ");
            writer.print(mBeanServer.getAttribute(tpName, "currentThreadsBusy"));
            
            writer.print("<br>");

            ObjectName grpName = null;

            Enumeration enum = globalRequestProcessors.elements();
            while (enum.hasMoreElements()) {
                ObjectName objectName = (ObjectName) enum.nextElement();
                if (name.equals(objectName.getKeyProperty("name"))) {
                    grpName = objectName;
                }
            }

            if (grpName == null) {
                return;
            }

            writer.print(" Max processing time: ");
            writer.print(formatTime(mBeanServer.getAttribute
                                    (grpName, "maxTime"), false));
            writer.print(" Processing time: ");
            writer.print(formatTime(mBeanServer.getAttribute
                                    (grpName, "processingTime"), true));
            writer.print(" Request count: ");
            writer.print(mBeanServer.getAttribute(grpName, "requestCount"));
            writer.print(" Error count: ");
            writer.print(mBeanServer.getAttribute(grpName, "errorCount"));
            writer.print(" Bytes received: ");
            writer.print(formatSize(mBeanServer.getAttribute
                                    (grpName, "bytesReceived"), true));
            writer.print(" Bytes sent: ");
            writer.print(formatSize(mBeanServer.getAttribute
                                    (grpName, "bytesSent"), true));
            writer.print("</p>");

            writer.print("<table border=\"0\"><tr><th>Stage</th><th>Time</th><th>B Sent</th><th>B Recv</th><th>Client</th><th>VHost</th><th>Request</th></tr>");

            enum = requestProcessors.elements();
            while (enum.hasMoreElements()) {
                ObjectName objectName = (ObjectName) enum.nextElement();
                if (name.equals(objectName.getKeyProperty("worker"))) {
                    writer.print("<tr>");
                    writeProcessorState(writer, objectName, mBeanServer, mode);
                    writer.print("</tr>");
                }
            }

            writer.print("</table>");

            writer.print("<p>");
            writer.print("P: Parse and prepare request S: Service F: Finishing R: Ready K: Keepalive");
            writer.print("</p>");
        } else if (mode == 1){
            writer.write("<connector name='" + name + "'>");

            writer.write("<threadInfo ");
            writer.write(" maxThreads=\"" + mBeanServer.getAttribute(tpName, "maxThreads") + "\"");
            writer.write(" minSpareThreads=\"" + mBeanServer.getAttribute(tpName, "minSpareThreads") + "\"");
            writer.write(" maxSpareThreads=\"" + mBeanServer.getAttribute(tpName, "maxSpareThreads") + "\"");
            writer.write(" currentThreadCount=\"" + mBeanServer.getAttribute(tpName, "currentThreadCount") + "\"");
            writer.write(" currentThreadsBusy=\"" + mBeanServer.getAttribute(tpName, "currentThreadsBusy") + "\"");
            writer.write(" />");

            ObjectName grpName = null;

            Enumeration enum = globalRequestProcessors.elements();
            while (enum.hasMoreElements()) {
                ObjectName objectName = (ObjectName) enum.nextElement();
                if (name.equals(objectName.getKeyProperty("name"))) {
                    grpName = objectName;
                }
            }

            if (grpName != null) {

                writer.write("<requestInfo ");
                writer.write(" maxTime=\"" + mBeanServer.getAttribute(grpName, "maxTime") + "\"");
                writer.write(" processingTime=\"" + mBeanServer.getAttribute(grpName, "processingTime") + "\"");
                writer.write(" requestCount=\"" + mBeanServer.getAttribute(grpName, "requestCount") + "\"");
                writer.write(" errorCount=\"" + mBeanServer.getAttribute(grpName, "errorCount") + "\"");
                writer.write(" bytesReceived=\"" + mBeanServer.getAttribute(grpName, "bytesReceived") + "\"");
                writer.write(" bytesSent=\"" + mBeanServer.getAttribute(grpName, "bytesSent") + "\"");
                writer.write(" />");

                writer.write("<workers>");
                enum = requestProcessors.elements();
                while (enum.hasMoreElements()) {
                    ObjectName objectName = (ObjectName) enum.nextElement();
                    if (name.equals(objectName.getKeyProperty("worker"))) {
                        writeProcessorState(writer, objectName, mBeanServer, mode);
                    }
                }
                writer.write("</workers>");
            }

            writer.write("</connector>");
        }

    }


    /**
     * Write processor state.
     */
    protected static void writeProcessorState(PrintWriter writer, 
                                              ObjectName pName,
                                              MBeanServer mBeanServer, 
                                              int mode)
        throws Exception {

        Integer stageValue = 
            (Integer) mBeanServer.getAttribute(pName, "stage");
        int stage = stageValue.intValue();
        boolean fullStatus = true;
        boolean showRequest = true;
        String stageStr = null;

        switch (stage) {

        case (1/*org.apache.coyote.Constants.STAGE_PARSE*/):
            stageStr = "P";
            fullStatus = false;
            break;
        case (2/*org.apache.coyote.Constants.STAGE_PREPARE*/):
            stageStr = "P";
            fullStatus = false;
            break;
        case (3/*org.apache.coyote.Constants.STAGE_SERVICE*/):
            stageStr = "S";
            break;
        case (4/*org.apache.coyote.Constants.STAGE_ENDINPUT*/):
            stageStr = "F";
            break;
        case (5/*org.apache.coyote.Constants.STAGE_ENDOUTPUT*/):
            stageStr = "F";
            break;
        case (7/*org.apache.coyote.Constants.STAGE_ENDED*/):
            stageStr = "R";
            fullStatus = false;
            break;
        case (6/*org.apache.coyote.Constants.STAGE_KEEPALIVE*/):
            stageStr = "K";
            fullStatus = true;
            showRequest = false;
            break;
        case (0/*org.apache.coyote.Constants.STAGE_NEW*/):
            stageStr = "R";
            fullStatus = false;
            break;
        default:
            // Unknown stage
            stageStr = "?";
            fullStatus = false;

        }

        if (mode == 0) {
            writer.write("<td><strong>");
            writer.write(stageStr);
            writer.write("</strong></td>");

            if (fullStatus) {
                writer.write("<td>");
                writer.print(formatTime(mBeanServer.getAttribute
                                        (pName, "requestProcessingTime"), false));
                writer.write("</td>");
                writer.write("<td>");
                if (showRequest) {
                    writer.print(formatSize(mBeanServer.getAttribute
                                            (pName, "requestBytesSent"), false));
                } else {
                    writer.write("?");
                }
                writer.write("</td>");
                writer.write("<td>");
                if (showRequest) {
                    writer.print(formatSize(mBeanServer.getAttribute
                                            (pName, "requestBytesReceived"), 
                                            false));
                } else {
                    writer.write("?");
                }
                writer.write("</td>");
                writer.write("<td>");
                writer.print(filter(mBeanServer.getAttribute
                                    (pName, "remoteAddr")));
                writer.write("</td>");
                writer.write("<td nowrap>");
                writer.write(filter(mBeanServer.getAttribute
                                    (pName, "virtualHost")));
                writer.write("</td>");
                writer.write("<td nowrap>");
                if (showRequest) {
                    writer.write(filter(mBeanServer.getAttribute
                                        (pName, "method")));
                    writer.write(" ");
                    writer.write(filter(mBeanServer.getAttribute
                                        (pName, "currentUri")));
                    String queryString = (String) mBeanServer.getAttribute
                        (pName, "currentQueryString");
                    if ((queryString != null) && (!queryString.equals(""))) {
                        writer.write("?");
                        writer.print(queryString);
                    }
                    writer.write(" ");
                    writer.write(filter(mBeanServer.getAttribute
                                        (pName, "protocol")));
                } else {
                    writer.write("?");
                }
                writer.write("</td>");
            } else {
                writer.write("<td>?</td><td>?</td><td>?</td><td>?</td><td>?</td><td>?</td>");
            }
        } else if (mode == 1){
            writer.write("<worker ");
            writer.write(" stage=\"" + stageStr + "\"");

            if (fullStatus) {
                writer.write(" requestProcessingTime=\"" 
                             + mBeanServer.getAttribute
                             (pName, "requestProcessingTime") + "\"");
                writer.write(" requestBytesSent=\"");
                if (showRequest) {
                    writer.write("" + mBeanServer.getAttribute
                                 (pName, "requestBytesSent"));
                } else {
                    writer.write("?");
                }
                writer.write("\"");
                writer.write(" requestBytesReceived=\"");
                if (showRequest) {
                    writer.write("" + mBeanServer.getAttribute
                                 (pName, "requestBytesReceived"));
                } else {
                    writer.write("?");
                }
                writer.write("\"");
                writer.write(" remoteAddr=\"" 
                             + filter(mBeanServer.getAttribute
                                      (pName, "remoteAddr")) + "\"");
                writer.write(" virtualHost=\"" 
                             + filter(mBeanServer.getAttribute
                                      (pName, "virtualHost")) + "\"");

                if (showRequest) {
                    writer.write(" method=\"" 
                                 + filter(mBeanServer.getAttribute
                                          (pName, "method")) + "\"");
                    writer.write(" currentUri=\"" 
                                 + filter(mBeanServer.getAttribute
                                          (pName, "currentUri")) + "\"");

                    String queryString = (String) mBeanServer.getAttribute
                        (pName, "currentQueryString");
                    if ((queryString != null) && (!queryString.equals(""))) {
                        writer.write(" currentQueryString=\"" 
                                     + queryString + "\"");
                    } else {
                        writer.write(" currentQueryString=\"?\"");
                    }
                    writer.write(" protocol=\"" 
                                 + filter(mBeanServer.getAttribute
                                          (pName, "protocol")) + "\"");
                } else {
                    writer.write(" method=\"?\"");
                    writer.write(" currentUri=\"?\"");
                    writer.write(" currentQueryString=\"?\"");
                    writer.write(" protocol=\"?\"");
                }
            } else {
                writer.write(" requestProcessingTime=\"?\"");
                writer.write(" requestBytesSent=\"?\"");
                writer.write(" requestBytesRecieved=\"?\"");
                writer.write(" remoteAddr=\"?\"");
                writer.write(" virtualHost=\"?\"");
                writer.write(" method=\"?\"");
                writer.write(" currentUri=\"?\"");
                writer.write(" currentQueryString=\"?\"");
                writer.write(" protocol=\"?\"");
            }
            writer.write(" />");
        }

    }


    /**
     * Write applications state.
     */
    public static void writeDetailedState(PrintWriter writer,
                                          MBeanServer mBeanServer, int mode)
        throws Exception {

        if (mode == 0){
            ObjectName queryHosts = new ObjectName("*:j2eeType=WebModule,*");
            Set hostsON = mBeanServer.queryNames(queryHosts, null);

            // Navigation menu
            writer.print("<h1>");
            writer.print("Application list");
            writer.print("</h1>");

            writer.print("<p>");
            int count = 0;
            Iterator iterator = hostsON.iterator();
            while (iterator.hasNext()) {
                ObjectName contextON = (ObjectName) iterator.next();
                String webModuleName = contextON.getKeyProperty("name");
                if (webModuleName.startsWith("//")) {
                    webModuleName = webModuleName.substring(2);
                }
                int slash = webModuleName.indexOf("/");
                if (slash == -1) {
                    count++;
                    continue;
                }

                writer.print("<a href=\"#" + (count++) + ".0\">");
                writer.print(webModuleName);
                writer.print("</a>");
                if (iterator.hasNext()) {
                    writer.print("<br>");
                }

            }
            writer.print("</p>");

            // Webapp list
            count = 0;
            iterator = hostsON.iterator();
            while (iterator.hasNext()) {
                ObjectName contextON = (ObjectName) iterator.next();
                writer.print("<a class=\"A.name\" name=\"" 
                             + (count++) + ".0\">");
                writeContext(writer, contextON, mBeanServer, mode);
            }

        } else if (mode == 1){
            // for now we don't write out the Detailed state in XML
        }

    }


    /**
     * Write context state.
     */
    protected static void writeContext(PrintWriter writer, 
                                       ObjectName objectName,
                                       MBeanServer mBeanServer, int mode)
        throws Exception {

        if (mode == 0){
            String webModuleName = objectName.getKeyProperty("name");
            String name = webModuleName;
            if (name == null) {
                return;
            }
            
            String hostName = null;
            String contextName = null;
            if (name.startsWith("//")) {
                name = name.substring(2);
            }
            int slash = name.indexOf("/");
            if (slash != -1) {
                hostName = name.substring(0, slash);
                contextName = name.substring(slash);
            } else {
                return;
            }

            ObjectName queryManager = new ObjectName
                (objectName.getDomain() + ":type=Manager,path=" + contextName 
                 + ",host=" + hostName + ",*");
            Set managersON = mBeanServer.queryNames(queryManager, null);
            ObjectName managerON = null;
            Iterator iterator2 = managersON.iterator();
            while (iterator2.hasNext()) {
                managerON = (ObjectName) iterator2.next();
            }

            // Special case for the root context
            if (contextName.equals("/")) {
                contextName = "";
            }

            writer.print("<h1>");
            writer.print(name);
            writer.print("</h1>");
            writer.print("</a>");

            writer.print("<p>");
            writer.print(" Startup time: ");
            writer.print(formatTime(mBeanServer.getAttribute
                                    (objectName, "startupTime"), false));
            writer.print(" TLD scan time: ");
            writer.print(formatTime(mBeanServer.getAttribute
                                    (objectName, "tldScanTime"), false));
            if (managerON != null) {
                writeManager(writer, managerON, mBeanServer, mode);
            }
            writer.print("</p>");

            String onStr = objectName.getDomain() 
                + ":j2eeType=Servlet,WebModule=" + webModuleName + ",*";
            ObjectName servletObjectName = new ObjectName(onStr);
            Set set = mBeanServer.queryMBeans(servletObjectName, null);
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                ObjectInstance oi = (ObjectInstance) iterator.next();
                writeWrapper(writer, oi.getObjectName(), mBeanServer, mode);
            }

        } else if (mode == 1){
            // for now we don't write out the context in XML
        }

    }


    /**
     * Write detailed information about a manager.
     */
    public static void writeManager(PrintWriter writer, ObjectName objectName,
                                    MBeanServer mBeanServer, int mode)
        throws Exception {

        if (mode == 0) {
            writer.print("<br>");
            writer.print(" Active sessions: ");
            writer.print(mBeanServer.getAttribute
                         (objectName, "activeSessions"));
            writer.print(" Session count: ");
            writer.print(mBeanServer.getAttribute
                         (objectName, "sessionCounter"));
            writer.print(" Max active sessions: ");
            writer.print(mBeanServer.getAttribute(objectName, "maxActive"));
            writer.print(" Rejected session creations: ");
            writer.print(mBeanServer.getAttribute
                         (objectName, "rejectedSessions"));
            writer.print(" Expired sessions: ");
            writer.print(mBeanServer.getAttribute
                         (objectName, "expiredSessions"));
            writer.print(" Processing time: ");
            writer.print(formatTime(mBeanServer.getAttribute
                                    (objectName, "processingTime"), false));
        } else if (mode == 1) {
            // for now we don't write out the wrapper details
        }

    }


    /**
     * Write detailed information about a wrapper.
     */
    public static void writeWrapper(PrintWriter writer, ObjectName objectName,
                                    MBeanServer mBeanServer, int mode)
        throws Exception {

        if (mode == 0) {
            String servletName = objectName.getKeyProperty("name");
            
            String[] mappings = (String[]) 
                mBeanServer.invoke(objectName, "findMappings", null, null);
            
            writer.print("<h2>");
            writer.print(servletName);
            if ((mappings != null) && (mappings.length > 0)) {
                writer.print(" [ ");
                for (int i = 0; i < mappings.length; i++) {
                    writer.print(mappings[i]);
                    if (i < mappings.length - 1) {
                        writer.print(" , ");
                    }
                }
                writer.print(" ] ");
            }
            writer.print("</h2>");
            
            writer.print("<p>");
            writer.print(" Processing time: ");
            writer.print(formatTime(mBeanServer.getAttribute
                                    (objectName, "processingTime"), true));
            writer.print(" Max time: ");
            writer.print(formatTime(mBeanServer.getAttribute
                                    (objectName, "maxTime"), false));
            writer.print(" Request count: ");
            writer.print(mBeanServer.getAttribute(objectName, "requestCount"));
            writer.print(" Error count: ");
            writer.print(mBeanServer.getAttribute(objectName, "errorCount"));
            writer.print(" Load time: ");
            writer.print(formatTime(mBeanServer.getAttribute
                                    (objectName, "loadTime"), false));
            writer.print(" Classloading time: ");
            writer.print(formatTime(mBeanServer.getAttribute
                                    (objectName, "classLoadTime"), false));
            writer.print("</p>");
        } else if (mode == 1){
            // for now we don't write out the wrapper details
        }

    }


    /**
     * Filter the specified message string for characters that are sensitive
     * in HTML.  This avoids potential attacks caused by including JavaScript
     * codes in the request URL that is often reported in error messages.
     *
     * @param message The message string to be filtered
     */
    public static String filter(Object obj) {

        if (obj == null)
            return ("?");
        String message = obj.toString();

        char content[] = new char[message.length()];
        message.getChars(0, message.length(), content, 0);
        StringBuffer result = new StringBuffer(content.length + 50);
        for (int i = 0; i < content.length; i++) {
            switch (content[i]) {
            case '<':
                result.append("&lt;");
                break;
            case '>':
                result.append("&gt;");
                break;
            case '&':
                result.append("&amp;");
                break;
            case '"':
                result.append("&quot;");
                break;
            default:
                result.append(content[i]);
            }
        }
        return (result.toString());

    }


    /**
     * Display the given size in bytes, either as KB or MB.
     *
     * @param mb true to display megabytes, false for kilobytes
     */
    public static String formatSize(Object obj, boolean mb) {

        long bytes = -1L;

        if (obj instanceof Long) {
            bytes = ((Long) obj).longValue();
        } else if (obj instanceof Integer) {
            bytes = ((Integer) obj).intValue();
        }

        if (mb) {
            long mbytes = bytes / (1024 * 1024);
            long rest = 
                ((bytes - (mbytes * (1024 * 1024))) * 100) / (1024 * 1024);
            return (mbytes + "." + ((rest < 10) ? "0" : "") + rest + " MB");
        } else {
            return ((bytes / 1024) + " KB");
        }

    }


    /**
     * Display the given time in ms, either as ms or s.
     *
     * @param seconds true to display seconds, false for milliseconds
     */
    public static String formatTime(Object obj, boolean seconds) {

        long time = -1L;

        if (obj instanceof Long) {
            time = ((Long) obj).longValue();
        } else if (obj instanceof Integer) {
            time = ((Integer) obj).intValue();
        }

        if (seconds) {
            return ((time / 1000) + " s");
        } else {
            return (time + " ms");
        }
    }

}
