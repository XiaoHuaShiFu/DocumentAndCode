/*
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


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.MBeanInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.Attribute;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.modeler.Registry;
import org.apache.tomcat.util.compat.JdkCompat;

/**
 * This servlet will dump JMX attributes in a simple format
 * and implement proxy services for modeler.
 *
 * @author Costin Manolache
 */
public class JMXProxyServlet extends HttpServlet  {
    // ----------------------------------------------------- Instance Variables

    /**
     * MBean server.
     */
    protected MBeanServer mBeanServer = null;
    protected Registry registry;
    // --------------------------------------------------------- Public Methods


    /**
     * Initialize this servlet.
     */
    public void init() throws ServletException {
        // Retrieve the MBean server
        registry=Registry.getRegistry();
        mBeanServer = Registry.getRegistry().getServer();
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
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {

        response.setContentType("text/plain");

        PrintWriter writer = response.getWriter();
        String qryString= request.getQueryString();

        if( mBeanServer==null ) {
            writer.println("Error - No mbean server");
            return;
        }

        String qry=request.getParameter("set");
        if( qry!= null ) {
            String name=request.getParameter("att");
            String val=request.getParameter("val");

            setAttribute( writer, qry, name, val );
            return;
        }

        qry=request.getParameter("qry");
        if( qry == null ) {
            qry = "*:*";
        }

        listBeans( writer, qry );

    }

    public void setAttribute( PrintWriter writer,
                              String onameStr, String att, String val )
    {
        try {
            ObjectName oname=new ObjectName( onameStr );
            String type=registry.getType(oname, att);
            Object valueObj=registry.convertValue(type, val );
            mBeanServer.setAttribute( oname, new Attribute(att, valueObj));
            writer.println("OK - Attribute set");
        } catch( Exception ex ) {
            writer.println("Error - " + ex.toString());
        }
    }

    public void listBeans( PrintWriter writer, String qry )
    {

        Set names = null;
        try {
            names=mBeanServer.queryNames(new ObjectName(qry), null);
            writer.println("OK - Number of results: " + names.size());
            writer.println();
        } catch (Exception e) {
            writer.println("Error - " + e.toString());
            return;
        }

        Iterator it=names.iterator();
        while( it.hasNext()) {
            ObjectName oname=(ObjectName)it.next();
            writer.println( "Name: " + oname.toString());

            try {
                MBeanInfo minfo=mBeanServer.getMBeanInfo(oname);
                // can't be null - I thinl
                String code=minfo.getClassName();
                if ("org.apache.commons.modeler.BaseModelMBean".equals(code)) {
                    code=(String)mBeanServer.getAttribute(oname, "modelerType");
                }
                writer.println("modelerType: " + code);

                MBeanAttributeInfo attrs[]=minfo.getAttributes();
                Object value=null;

                for( int i=0; i< attrs.length; i++ ) {
                    if( ! attrs[i].isReadable() ) continue;
                    if( ! isSupported( attrs[i].getType() )) continue;
                    String attName=attrs[i].getName();
                    if( attName.indexOf( "=") >=0 ||
                            attName.indexOf( ":") >=0 ||
                            attName.indexOf( " ") >=0 ) {
                        continue;
                    }
            
                    try {
                        value=mBeanServer.getAttribute(oname, attName);
                    } catch( Throwable t) {
                        System.out.println("Error getting attribute " + oname +
                                " " + attName + " " + t.toString());
                        continue;
                    }
                    if( value==null ) continue;
                    if( "modelerType".equals( attName)) continue;
                    String valueString=value.toString();
                    writer.println( attName + ": " + escape(valueString));
                }
            } catch (Exception e) {
                // Ignore
            }
            writer.println();
        }

    }

    public String escape(String value) {
        // The only invalid char is \n
        // We also need to keep the string short and split it with \nSPACE
        // XXX TODO
        int idx=value.indexOf( "\n" );
        if( idx < 0 ) return value;

        int prev=0;
        StringBuffer sb=new StringBuffer();
        while( idx >= 0 ) {
            appendHead(sb, value, prev, idx-1);

            sb.append( "\\n\n ");
            prev=idx+1;
            if( idx==value.length() -1 ) break;
            idx=value.indexOf('\n', idx+1);
        }
        if( prev < value.length() )
            appendHead( sb, value, prev, value.length());
        return sb.toString();
    }

    private void appendHead( StringBuffer sb, String value, int start, int end) {
        int pos=start;
        while( end-pos > 78 ) {
            sb.append( value.substring(pos, pos+78));
            sb.append( "\n ");
            pos=pos+78;
        }
        sb.append( value.substring(pos,end));
    }

    public boolean isSupported( String type ) {
        return true;
    }
}
