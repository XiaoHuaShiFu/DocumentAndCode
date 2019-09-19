/**
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 */

package jsp2.examples.simpletag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.HashMap;

/**
 * Displays a tile as a single cell in a table.
 */
public class TileSimpleTag extends SimpleTagSupport {
    private String color;
    private String label;

    public void doTag() throws JspException, IOException {
	getJspContext().getOut().write( 
	    "<td width=\"32\" height=\"32\" bgcolor=\"" + this.color + 
	    "\"><font color=\"#ffffff\"><center>" + this.label + 
                "</center></font></td>" );
    }

    public void setColor( String color ) {
        this.color = color;
    }
    
    public void setLabel( String label ) {
        this.label = label;
    }
}
