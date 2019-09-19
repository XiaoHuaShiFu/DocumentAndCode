/*
 * $Header: /home/cvs/jakarta-tomcat-catalina/webapps/manager/WEB-INF/classes/org/apache/catalina/manager/Constants.java,v 1.8 2003/09/07 18:47:14 remm Exp $
 * $Revision: 1.8 $
 * $Date: 2003/09/07 18:47:14 $
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


public class Constants {

    public static final String Package = "org.apache.catalina.manager";

    public static final String HTML_HEADER_SECTION =
        "<html>\n" +
        "<head>\n" +
        "<style>\n" +
        org.apache.catalina.util.TomcatCSS.TOMCAT_CSS +
        "  table {\n" +
        "    width: 100%;\n" +
        "  }\n" +
        "  td.page-title {\n" +
        "    text-align: center;\n" +
        "    vertical-align: top;\n" +
        "    font-family:sans-serif,Tahoma,Arial;\n" +
        "    font-weight: bold;\n" +
        "    background: white;\n" +
        "    color: black;\n" +
        "  }\n" +
        "  td.title {\n" +
        "    text-align: left;\n" +
        "    vertical-align: top;\n" +
        "    font-family:sans-serif,Tahoma,Arial;\n" +
        "    font-style:italic;\n" +
        "    font-weight: bold;\n" +
        "    background: #D2A41C;\n" +
        "  }\n" +
        "  td.header-left {\n" +
        "    text-align: left;\n" +
        "    vertical-align: top;\n" +
        "    font-family:sans-serif,Tahoma,Arial;\n" +
        "    font-weight: bold;\n" +
        "    background: #FFDC75;\n" +
        "  }\n" +
        "  td.header-center {\n" +
        "    text-align: center;\n" +
        "    vertical-align: top;\n" +
        "    font-family:sans-serif,Tahoma,Arial;\n" +
        "    font-weight: bold;\n" +
        "    background: #FFDC75;\n" +
        "  }\n" +
        "  td.row-left {\n" +
        "    text-align: left;\n" +
        "    vertical-align: middle;\n" +
        "    font-family:sans-serif,Tahoma,Arial;\n" +
        "    color: black;\n" +
        "    background: white;\n" +
        "  }\n" +
        "  td.row-center {\n" +
        "    text-align: center;\n" +
        "    vertical-align: middle;\n" +
        "    font-family:sans-serif,Tahoma,Arial;\n" +
        "    color: black;\n" +
        "    background: white;\n" +
        "  }\n" +
        "  td.row-right {\n" +
        "    text-align: right;\n" +
        "    vertical-align: middle;\n" +
        "    font-family:sans-serif,Tahoma,Arial;\n" +
        "    color: black;\n" +
        "    background: white;\n" +
        "  }\n" +
        "  TH {\n" +
        "    text-align: center;\n" +
        "    vertical-align: top;\n" +
        "    font-family:sans-serif,Tahoma,Arial;\n" +
        "    font-weight: bold;\n" +
        "    background: #FFDC75;\n" +
        "  }\n" +
        "  TD {\n" +
        "    text-align: center;\n" +
        "    vertical-align: middle;\n" +
        "    font-family:sans-serif,Tahoma,Arial;\n" +
        "    color: black;\n" +
        "    background: white;\n" +
        "  }\n" +
        "</style>\n";

    public static final String BODY_HEADER_SECTION =
        "<title>{0}</title>\n" +
        "</head>\n" +
        "\n" +
        "<body bgcolor=\"#FFFFFF\">\n" +
        "\n" +
        "<table cellspacing=\"4\" width=\"100%\" border=\"0\">\n" +
        " <tr>\n" +
        "  <td colspan=\"2\">\n" +
        "   <a href=\"http://jakarta.apache.org/\">\n" +
        "    <img border=\"0\" alt=\"The Jakarta Project\" align=\"left\"\n" +
        "         src=\"{0}/images/jakarta-logo.gif\">\n" +
        "   </a>\n" +
        "   <a href=\"http://jakarta.apache.org/tomcat/\">\n" +
        "    <img border=\"0\" alt=\"The Tomcat Servlet/JSP Container\"\n" +
        "         align=\"right\" src=\"{0}/images/tomcat.gif\">\n" +
        "   </a>\n" +
        "  </td>\n" +
        " </tr>\n" +
        "</table>\n" +
        "<hr size=\"1\" noshade\"\">\n" +
        "<table cellspacing=\"4\" width=\"100%\" border=\"0\">\n" +
        " <tr>\n" +
        "  <td class=\"page-title\" bordercolor=\"#000000\" " +
        "align=\"left\" nowrap>\n" +
        "   <font size=\"+2\">{1}</font>\n" +
        "  </td>\n" +
        " </tr>\n" +
        "</table>\n" +
        "<br>\n" +
        "\n";

    public static final String MESSAGE_SECTION =
        "<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n" +
        " <tr>\n" +
        "  <td class=\"row-left\" width=\"10%\">" +
        "<small><strong>{0}</strong></small>&nbsp;</td>\n" +
        "  <td class=\"row-left\"><pre>{1}</pre></td>\n" +
        " </tr>\n" +
        "</table>\n" +
        "<br>\n" +
        "\n";

    public static final String MANAGER_SECTION =
        "<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n" +
        "<tr>\n" +
        " <td colspan=\"4\" class=\"title\">{0}</td>\n" +
        "</tr>\n" +
        " <tr>\n" +
        "  <td class=\"row-left\"><a href=\"{1}\">{2}</a></td>\n" +
        "  <td class=\"row-center\"><a href=\"{3}\">{4}</a></td>\n" +
        "  <td class=\"row-center\"><a href=\"{5}\">{6}</a></td>\n" +
        "  <td class=\"row-right\"><a href=\"{7}\">{8}</a></td>\n" +
        " </tr>\n" +
        "</table>\n" +
        "<br>\n" +
        "\n";

    public static final String SERVER_HEADER_SECTION =
        "<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n" +
        "<tr>\n" +
        " <td colspan=\"6\" class=\"title\">{0}</td>\n" +
        "</tr>\n" +
        "<tr>\n" +
        " <td class=\"header-center\"><small>{1}</small></td>\n" +
        " <td class=\"header-center\"><small>{2}</small></td>\n" +
        " <td class=\"header-center\"><small>{3}</small></td>\n" +
        " <td class=\"header-center\"><small>{4}</small></td>\n" +
        " <td class=\"header-center\"><small>{5}</small></td>\n" +
        " <td class=\"header-center\"><small>{6}</small></td>\n" +
        "</tr>\n";

    public static final String SERVER_ROW_SECTION =
        "<tr>\n" +
        " <td class=\"row-center\"><small>{0}</small></td>\n" +
        " <td class=\"row-center\"><small>{1}</small></td>\n" +
        " <td class=\"row-center\"><small>{2}</small></td>\n" +
        " <td class=\"row-center\"><small>{3}</small></td>\n" +
        " <td class=\"row-center\"><small>{4}</small></td>\n" +
        " <td class=\"row-center\"><small>{5}</small></td>\n" +
        "</tr>\n" +
        "</table>\n" +
        "<br>\n" +
        "\n";

    public static final String HTML_TAIL_SECTION =
        "<hr size=\"1\" noshade\"\">\n" +
        "<center><font size=\"-1\" color=\"#525D76\">\n" +
        " <em>Copyright &copy; 1999-2003, Apache Software Foundation</em>" +
        "</font></center>\n" +
        "\n" +
        "</body>\n" +
        "</html>";
    public static final String CHARSET="utf-8";

    public static final String XML_DECLARATION =
        "<?xml version=\"1.0\" encoding=\""+CHARSET+"\"?>";
		
    public static final String XML_STYLE =
        "<?xml-stylesheet type=\"text/xsl\" href=\"xform.xsl\" ?>";

}

