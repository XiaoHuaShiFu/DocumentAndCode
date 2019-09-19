/**
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 */

package jsp2.examples.simpletag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;

/**
 * SimpleTag handler that echoes all its attributes 
 */
public class EchoAttributesTag 
    extends SimpleTagSupport
    implements DynamicAttributes
{
    private ArrayList keys = new ArrayList();
    private ArrayList values = new ArrayList();

    public void doTag() throws JspException, IOException {
	JspWriter out = getJspContext().getOut();
	for( int i = 0; i < keys.size(); i++ ) {
	    String key = (String)keys.get( i );
	    Object value = values.get( i );
	    out.println( "<li>" + key + " = " + value + "</li>" );
        }
    }

    public void setDynamicAttribute( String uri, String localName, 
	Object value ) 
	throws JspException
    {
	keys.add( localName );
	values.add( value );
    }
}
