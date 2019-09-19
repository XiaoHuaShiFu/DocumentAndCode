/**
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 */

package jsp2.examples;

public class FooBean {
    private String bar;
    
    public FooBean() {
        bar = "Initial value";
    }
    
    public String getBar() {
        return this.bar;
    }
    
    public void setBar(String bar) {
        this.bar = bar;
    }
    
}
