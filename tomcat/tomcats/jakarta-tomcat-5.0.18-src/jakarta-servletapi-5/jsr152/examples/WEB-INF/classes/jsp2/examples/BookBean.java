/**
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 */

package jsp2.examples;

public class BookBean {
    private String title;
    private String author;
    private String isbn;
    
    public BookBean( String title, String author, String isbn ) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public String getTitle() {
        return this.title;
    }
    
    public String getAuthor() {
        return this.author;
    }
    
    public String getIsbn() {
        return this.isbn;
    }
    
}
