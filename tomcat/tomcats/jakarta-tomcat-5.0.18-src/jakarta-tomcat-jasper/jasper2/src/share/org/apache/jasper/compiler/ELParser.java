/* ========================================================================= *
 *                                                                           *
 *                 The Apache Software License,  Version 1.1                 *
 *                                                                           *
 *      Copyright (c) 1999, 2000, 2001  The Apache Software Foundation.      *
 *                           All rights reserved.                            *
 *                                                                           *
 * ========================================================================= *
 *                                                                           *
 * Redistribution and use in source and binary forms,  with or without modi- *
 * fication, are permitted provided that the following conditions are met:   *
 *                                                                           *
 * 1. Redistributions of source code  must retain the above copyright notice *
 *    notice, this list of conditions and the following disclaimer.          *
 *                                                                           *
 * 2. Redistributions  in binary  form  must  reproduce the  above copyright *
 *    notice,  this list of conditions  and the following  disclaimer in the *
 *    documentation and/or other materials provided with the distribution.   *
 *                                                                           *
 * 3. The end-user documentation  included with the redistribution,  if any, *
 *    must include the following acknowlegement:                             *
 *                                                                           *
 *       "This product includes  software developed  by the Apache  Software *
 *        Foundation <http://www.apache.org/>."                              *
 *                                                                           *
 *    Alternately, this acknowlegement may appear in the software itself, if *
 *    and wherever such third-party acknowlegements normally appear.         *
 *                                                                           *
 * 4. The names  "The  Jakarta  Project",  "Tomcat",  and  "Apache  Software *
 *    Foundation"  must not be used  to endorse or promote  products derived *
 *    from this  software without  prior  written  permission.  For  written *
 *    permission, please contact <apache@apache.org>.                        *
 *                                                                           *
 * 5. Products derived from this software may not be called "Apache" nor may *
 *    "Apache" appear in their names without prior written permission of the *
 *    Apache Software Foundation.                                            *
 *                                                                           *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES *
 * INCLUDING, BUT NOT LIMITED TO,  THE IMPLIED WARRANTIES OF MERCHANTABILITY *
 * AND FITNESS FOR  A PARTICULAR PURPOSE  ARE DISCLAIMED.  IN NO EVENT SHALL *
 * THE APACHE  SOFTWARE  FOUNDATION OR  ITS CONTRIBUTORS  BE LIABLE  FOR ANY *
 * DIRECT,  INDIRECT,   INCIDENTAL,  SPECIAL,  EXEMPLARY,  OR  CONSEQUENTIAL *
 * DAMAGES (INCLUDING,  BUT NOT LIMITED TO,  PROCUREMENT OF SUBSTITUTE GOODS *
 * OR SERVICES;  LOSS OF USE,  DATA,  OR PROFITS;  OR BUSINESS INTERRUPTION) *
 * HOWEVER CAUSED AND  ON ANY  THEORY  OF  LIABILITY,  WHETHER IN  CONTRACT, *
 * STRICT LIABILITY, OR TORT  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN *
 * ANY  WAY  OUT OF  THE  USE OF  THIS  SOFTWARE,  EVEN  IF  ADVISED  OF THE *
 * POSSIBILITY OF SUCH DAMAGE.                                               *
 *                                                                           *
 * ========================================================================= *
 *                                                                           *
 * This software  consists of voluntary  contributions made  by many indivi- *
 * duals on behalf of the  Apache Software Foundation.  For more information *
 * on the Apache Software Foundation, please see <http://www.apache.org/>.   *
 *                                                                           *
 * ========================================================================= */

package org.apache.jasper.compiler;

/**
 * This class implements a parser for EL expressions.
 *
 * It takes strings of the form xxx${..}yyy${..}zzz etc, and turn it into
 * a ELNode.Nodes.
 *
 * Currently, it only handles text outside ${..} and functions in ${ ..}.
 *
 * @author Kin-man Chung
 */

public class ELParser {

    private Token curToken;	// current token
    private ELNode.Nodes expr;
    private ELNode.Nodes ELexpr;
    private int index;		// Current index of the expression
    private String expression;	// The EL expression
    private boolean escapeBS;	// is '\' an escape char in text outside EL?

    private static final String reservedWords[] = {
        "and", "div", "empty", "eq", "false",
        "ge", "gt", "instanceof", "le", "lt", "mod",
        "ne", "not", "null", "or", "true"};

    public ELParser(String expression) {
	index = 0;
	this.expression = expression;
	expr = new ELNode.Nodes();
    }

    /**
     * Parse an EL expression
     * @param expression The input expression string of the form
     *                   Char* ('${' Char* '}')* Char*
     * @return Parsed EL expression in ELNode.Nodes
     */
    public static ELNode.Nodes parse(String expression) {
	ELParser parser = new ELParser(expression);
	while (parser.hasNextChar()) {
	    String text = parser.skipUntilEL();
	    if (text.length() > 0) {
		parser.expr.add(new ELNode.Text(text));
	    }
	    ELNode.Nodes elexpr = parser.parseEL();
	    if (! elexpr.isEmpty()) {
		parser.expr.add(new ELNode.Root(elexpr));
	    }
	}
	return parser.expr;
    }

    /**
     * Parse an EL expression string '${...}'
     *@return An ELNode.Nodes representing the EL expression
     * TODO: Currently only parsed into functions and text strings.  This
     *       should be rewritten for a full parser.
     */
    private ELNode.Nodes parseEL() {

	StringBuffer buf = new StringBuffer();
	ELexpr = new ELNode.Nodes();
	while (hasNext()) {
	    curToken = nextToken();
	    if (curToken instanceof Char) {
		if (curToken.toChar() == '}') {
		    break;
		}
		buf.append(curToken.toChar());
	    } else {
		// Output whatever is in buffer
		if (buf.length() > 0) {
		    ELexpr.add(new ELNode.ELText(buf.toString()));
		}
		if (!parseFunction()) {
		    ELexpr.add(new ELNode.ELText(curToken.toString()));
		}
	    }
	}
	if (buf.length() > 0) {
	    ELexpr.add(new ELNode.ELText(buf.toString()));
	}

	return ELexpr;
    }

    /**
     * Parse for a function
     * FunctionInvokation ::= (identifier ':')? identifier '('
     *			      (Expression (,Expression)*)? ')'
     * Note: currently we don't parse arguments
     */
    private boolean parseFunction() {
	if (! (curToken instanceof Id) || isELReserved(curToken.toString())) {
	    return false;
	}
	String s1 = null;                 // Function prefix
	String s2 = curToken.toString();  // Function name
	int mark = getIndex();
	if (hasNext()) {
	    Token t = nextToken();
	    if (t.toChar() == ':') {
		if (hasNext()) {
		    Token t2 = nextToken();
		    if (t2 instanceof Id) {
			s1 = s2;
			s2 = t2.toString();
			if (hasNext()) {
			    t = nextToken();
			}
		    }
		}
	    }
	    if (t.toChar() == '(') {
		ELexpr.add(new ELNode.Function(s1, s2));
		return true;
	    }
	}
	setIndex(mark);
	return false;
    }

    /**
     * Test if an id is a reserved word in EL
     */
    private boolean isELReserved(String id) {
        int i = 0;
        int j = reservedWords.length;
        while (i < j) {
            int k = (i+j)/2;
            int result = reservedWords[k].compareTo(id);
            if (result == 0) {
                return true;
            }
            if (result < 0) {
                i = k+1;
            } else {
                j = k;
            }
        }
        return false;
    }

    /**
     * Skip until an EL expression ('${') is reached, allowing escape sequences
     * '\\' and '\$'.
     * @return The text string up to the EL expression
     */
    private String skipUntilEL() {
	char prev = 0;
	StringBuffer buf = new StringBuffer();
	while (hasNextChar()) {
	    char ch = nextChar();
	    if (prev == '\\') {
		prev = 0;
		if (ch == '\\') {
		    buf.append('\\');
		    if (!escapeBS)
			prev = '\\';
		} else if (ch == '$') {
		    buf.append('$');
		}
		// else error!
	    } else if (prev == '$') {
		if (ch == '{') {
		    prev = 0;
		    break;
		} 
		buf.append('$');
		buf.append(ch);
	    } else if (ch == '\\' || ch == '$') {
		prev = ch;
	    } else {
		buf.append(ch);
	    }
	}
	if (prev != 0) {
	    buf.append(prev);
	}
	return buf.toString();
    }

    /*
     * @return true if there is something left in EL expression buffer other
     *         than white spaces.
     */
    private boolean hasNext() {
	skipSpaces();
	return hasNextChar();
    }

    /*
     * @return The next token in the EL expression buffer.
     */
    private Token nextToken() {
	skipSpaces();
	if (hasNextChar()) {
	    char ch = nextChar();
	    if (Character.isJavaIdentifierStart(ch)) {
		StringBuffer buf = new StringBuffer();
		buf.append(ch);
		while ((ch = peekChar()) != -1 &&
				Character.isJavaIdentifierPart(ch)) {
		    buf.append(ch);
		    nextChar();
		}
		return new Id(buf.toString());
	    }

	    if (ch == '\'' || ch == '"') {
		return parseQuotedChars(ch);
	    } else {
		// For now...
		return new Char(ch);
	    }
	}
	return null;
    }

    /*
     * Parse a string in single or double quotes, allowing for escape sequences
     * '\\', and ('\"', or "\'")
     */
    private Token parseQuotedChars(char quote) {
	StringBuffer buf = new StringBuffer();
	buf.append(quote);
	while (hasNextChar()) {
	    char ch = nextChar();
	    if (ch == '\\') {
		ch = nextChar();
		if (ch == '\\' || ch == quote) {
		    buf.append(ch);
		}
		// else error!
	    } else if (ch == quote) {
		buf.append(ch);
		break;
	    } else {
		buf.append(ch);
	    }
	}
	return new QuotedString(buf.toString());
    }

    /*
     * A collection of low level parse methods dealing with character in
     * the EL expression buffer.
     */

    private void skipSpaces() {
	while (hasNextChar()) {
	    if (expression.charAt(index) > ' ')
		break;
	    index++;
	}
    }

    private boolean hasNextChar() {
	return index < expression.length();
    }

    private char nextChar() {
	if (index >= expression.length()) {
	    return (char)-1;
	}
	return expression.charAt(index++);
    }

    private char peekChar() {
	if (index >= expression.length()) {
	    return (char)-1;
	}
	return expression.charAt(index);
    }

    private int getIndex() {
	return index;
    }

    private void setIndex(int i) {
	index = i;
    }

    /*
     * Represents a token in EL expression string
     */
    private static class Token {

	char toChar() {
	    return 0;
	}

	public String toString() {
	    return "";
	}
    }

    /*
     * Represents an ID token in EL
     */
    private static class Id extends Token {
	String id;

	Id(String id) {
	    this.id = id;
	}

	public String toString() {
	    return id;
	}
    }

    /*
     * Represents a character token in EL
     */
    private static class Char extends Token {

	private char ch;

	Char(char ch) {
	    this.ch = ch;
	}

	char toChar() {
	    return ch;
	}

	public String toString() {
	    return (new Character(ch)).toString();
	}
    }

    /*
     * Represents a quoted (single or double) string token in EL
     */
    private static class QuotedString extends Token {

	private String value;

	QuotedString(String v) {
	    this.value = v;
	}

	public String toString() {
	    return value;
	}
    }
}

