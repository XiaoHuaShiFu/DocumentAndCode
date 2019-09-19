/**
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 */

package jsp2.examples.simpletag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * SimpleTag handler that prints "Hello, world!"
 */
public class HelloWorldSimpleTag extends SimpleTagSupport {
    public void doTag() throws JspException, IOException {
	getJspContext().getOut().write( "Hello, world!" );
    }
}
