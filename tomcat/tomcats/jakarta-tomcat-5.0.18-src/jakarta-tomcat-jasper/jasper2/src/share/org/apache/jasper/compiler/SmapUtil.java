/*
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
 */

package org.apache.jasper.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;

/**
 * Contains static utilities for generating SMAP data based on the
 * current version of Jasper.
 * 
 * @author Jayson Falkner
 * @author Shawn Bayern
 * @author Robert Field (inner SDEInstaller class)
 * @author Mark Roth
 * @author Kin-man Chung
 */
public class SmapUtil {

    private static final boolean verbose = false;

    //*********************************************************************
    // Constants

    public static final String SMAP_ENCODING = "UTF-8";

    //*********************************************************************
    // Public entry points

    /**
     * Generates an appropriate SMAP representing the current compilation
     * context.  (JSR-045.)
     *
     * @param ctxt Current compilation context
     * @param pageNodes The current JSP page
     * @return a SMAP for the page
     */
    public static String generateSmap(
        JspCompilationContext ctxt,
        Node.Nodes pageNodes)
        throws IOException {
        // set up our SMAP generator
        SmapGenerator g = new SmapGenerator();

        /** Disable reading of input SMAP because:
            1. There is a bug here: getRealPath() is null if .jsp is in a jar
        	Bugzilla 14660.
            2. Mappings from other sources into .jsp files are not supported.
            TODO: fix 1. if 2. is not true.
        // determine if we have an input SMAP
        String smapPath = inputSmapPath(ctxt.getRealPath(ctxt.getJspFile()));
            File inputSmap = new File(smapPath);
            if (inputSmap.exists()) {
                byte[] embeddedSmap = null;
            byte[] subSmap = SDEInstaller.readWhole(inputSmap);
            String subSmapString = new String(subSmap, SMAP_ENCODING);
            g.addSmap(subSmapString, "JSP");
        }
        **/

        // now, assemble info about our own stratum (JSP) using JspLineMap
        SmapStratum s = new SmapStratum("JSP");

        g.setOutputFileName(unqualify(ctxt.getServletJavaFileName()));
        // Map out Node.Nodes
        evaluateNodes(pageNodes, s, ctxt.getOptions().getMappedFile());
        g.addStratum(s, true);

        if (ctxt.getOptions().isSmapDumped()) {
            File outSmap = new File(ctxt.getClassFileName() + ".smap");
            PrintWriter so =
                new PrintWriter(
                    new OutputStreamWriter(
                        new FileOutputStream(outSmap),
                        SMAP_ENCODING));
            so.print(g.getString());
            so.close();
        }
        return g.getString();
    }

    public static void installSmap(String classFileName, String smap)
        throws IOException {
        if (smap == null) {
            return;
        }

        File outServlet = new File(classFileName);
        SDEInstaller.install(outServlet, smap.getBytes());
    }

    //*********************************************************************
    // Private utilities

    /**
     * Returns an unqualified version of the given file path.
     */
    private static String unqualify(String path) {
        path = path.replace('\\', '/');
        return path.substring(path.lastIndexOf('/') + 1);
    }

    /**
     * Returns a file path corresponding to a potential SMAP input
     * for the given compilation input (JSP file).
     */
    private static String inputSmapPath(String path) {
        return path.substring(0, path.lastIndexOf('.') + 1) + "smap";
    }

    //*********************************************************************
    // Installation logic (from Robert Field, JSR-045 spec lead)
    private static class SDEInstaller {

        static final String nameSDE = "SourceDebugExtension";

        byte[] orig;
        byte[] sdeAttr;
        byte[] gen;

        int origPos = 0;
        int genPos = 0;

        int sdeIndex;

        public static void main(String[] args) throws IOException {
            if (args.length == 2) {
                install(new File(args[0]), new File(args[1]));
            } else if (args.length == 3) {
                install(
                    new File(args[0]),
                    new File(args[1]),
                    new File(args[2]));
            } else {
                System.err.println(
                    "Usage: <command> <input class file> "
                        + "<attribute file> <output class file name>\n"
                        + "<command> <input/output class file> <attribute file>");
            }
        }

        static void install(File inClassFile, File attrFile, File outClassFile)
            throws IOException {
            new SDEInstaller(inClassFile, attrFile, outClassFile);
        }

        static void install(File inOutClassFile, File attrFile)
            throws IOException {
            File tmpFile = new File(inOutClassFile.getPath() + "tmp");
            new SDEInstaller(inOutClassFile, attrFile, tmpFile);
            if (!inOutClassFile.delete()) {
                throw new IOException("inOutClassFile.delete() failed");
            }
            if (!tmpFile.renameTo(inOutClassFile)) {
                throw new IOException("tmpFile.renameTo(inOutClassFile) failed");
            }
        }

        static void install(File classFile, byte[] smap) throws IOException {
            File tmpFile = new File(classFile.getPath() + "tmp");
            new SDEInstaller(classFile, smap, tmpFile);
            if (!classFile.delete()) {
                throw new IOException("classFile.delete() failed");
            }
            if (!tmpFile.renameTo(classFile)) {
                throw new IOException("tmpFile.renameTo(classFile) failed");
            }
        }

        SDEInstaller(File inClassFile, byte[] sdeAttr, File outClassFile)
            throws IOException {
            if (!inClassFile.exists()) {
                throw new FileNotFoundException("no such file: " + inClassFile);
            }

            this.sdeAttr = sdeAttr;
            // get the bytes
            orig = readWhole(inClassFile);
            gen = new byte[orig.length + sdeAttr.length + 100];

            // do it
            addSDE();

            // write result
            FileOutputStream outStream = new FileOutputStream(outClassFile);
            outStream.write(gen, 0, genPos);
            outStream.close();
        }

        SDEInstaller(File inClassFile, File attrFile, File outClassFile)
            throws IOException {
            this(inClassFile, readWhole(attrFile), outClassFile);
        }

        static byte[] readWhole(File input) throws IOException {
            FileInputStream inStream = new FileInputStream(input);
            int len = (int)input.length();
            byte[] bytes = new byte[len];
            if (inStream.read(bytes, 0, len) != len) {
                throw new IOException("expected size: " + len);
            }
            inStream.close();
            return bytes;
        }

        void addSDE() throws UnsupportedEncodingException, IOException {
            int i;
            copy(4 + 2 + 2); // magic min/maj version
            int constantPoolCountPos = genPos;
            int constantPoolCount = readU2();
            if (verbose) {
                System.out.println("constant pool count: " + constantPoolCount);
            }
            writeU2(constantPoolCount);

            // copy old constant pool return index of SDE symbol, if found
            sdeIndex = copyConstantPool(constantPoolCount);
            if (sdeIndex < 0) {
                // if "SourceDebugExtension" symbol not there add it
                writeUtf8ForSDE();

                // increment the countantPoolCount
                sdeIndex = constantPoolCount;
                ++constantPoolCount;
                randomAccessWriteU2(constantPoolCountPos, constantPoolCount);

                if (verbose) {
                    System.out.println(
                        "SourceDebugExtension not found, installed at: "
                            + sdeIndex);
                }
            } else {
                if (verbose) {
                    System.out.println(
                        "SourceDebugExtension found at: " + sdeIndex);
                }
            }
            copy(2 + 2 + 2); // access, this, super
            int interfaceCount = readU2();
            writeU2(interfaceCount);
            if (verbose) {
                System.out.println("interfaceCount: " + interfaceCount);
            }
            copy(interfaceCount * 2);
            copyMembers(); // fields
            copyMembers(); // methods
            int attrCountPos = genPos;
            int attrCount = readU2();
            writeU2(attrCount);
            if (verbose) {
                System.out.println("class attrCount: " + attrCount);
            }
            // copy the class attributes, return true if SDE attr found (not copied)
            if (!copyAttrs(attrCount)) {
                // we will be adding SDE and it isn't already counted
                ++attrCount;
                randomAccessWriteU2(attrCountPos, attrCount);
                if (verbose) {
                    System.out.println("class attrCount incremented");
                }
            }
            writeAttrForSDE(sdeIndex);
        }

        void copyMembers() {
            int count = readU2();
            writeU2(count);
            if (verbose) {
                System.out.println("members count: " + count);
            }
            for (int i = 0; i < count; ++i) {
                copy(6); // access, name, descriptor
                int attrCount = readU2();
                writeU2(attrCount);
                if (verbose) {
                    System.out.println("member attr count: " + attrCount);
                }
                copyAttrs(attrCount);
            }
        }

        boolean copyAttrs(int attrCount) {
            boolean sdeFound = false;
            for (int i = 0; i < attrCount; ++i) {
                int nameIndex = readU2();
                // don't write old SDE
                if (nameIndex == sdeIndex) {
                    sdeFound = true;
                    if (verbose) {
                        System.out.println("SDE attr found");
                    }
                } else {
                    writeU2(nameIndex); // name
                    int len = readU4();
                    writeU4(len);
                    copy(len);
                    if (verbose) {
                        System.out.println("attr len: " + len);
                    }
                }
            }
            return sdeFound;
        }

        void writeAttrForSDE(int index) {
            writeU2(index);
            writeU4(sdeAttr.length);
            for (int i = 0; i < sdeAttr.length; ++i) {
                writeU1(sdeAttr[i]);
            }
        }

        void randomAccessWriteU2(int pos, int val) {
            int savePos = genPos;
            genPos = pos;
            writeU2(val);
            genPos = savePos;
        }

        int readU1() {
            return ((int)orig[origPos++]) & 0xFF;
        }

        int readU2() {
            int res = readU1();
            return (res << 8) + readU1();
        }

        int readU4() {
            int res = readU2();
            return (res << 16) + readU2();
        }

        void writeU1(int val) {
            gen[genPos++] = (byte)val;
        }

        void writeU2(int val) {
            writeU1(val >> 8);
            writeU1(val & 0xFF);
        }

        void writeU4(int val) {
            writeU2(val >> 16);
            writeU2(val & 0xFFFF);
        }

        void copy(int count) {
            for (int i = 0; i < count; ++i) {
                gen[genPos++] = orig[origPos++];
            }
        }

        byte[] readBytes(int count) {
            byte[] bytes = new byte[count];
            for (int i = 0; i < count; ++i) {
                bytes[i] = orig[origPos++];
            }
            return bytes;
        }

        void writeBytes(byte[] bytes) {
            for (int i = 0; i < bytes.length; ++i) {
                gen[genPos++] = bytes[i];
            }
        }

        int copyConstantPool(int constantPoolCount)
            throws UnsupportedEncodingException, IOException {
            int sdeIndex = -1;
            // copy const pool index zero not in class file
            for (int i = 1; i < constantPoolCount; ++i) {
                int tag = readU1();
                writeU1(tag);
                switch (tag) {
                    case 7 : // Class
                    case 8 : // String
                        if (verbose) {
                            System.out.println(i + " copying 2 bytes");
                        }
                        copy(2);
                        break;
                    case 9 : // Field
                    case 10 : // Method
                    case 11 : // InterfaceMethod
                    case 3 : // Integer
                    case 4 : // Float
                    case 12 : // NameAndType
                        if (verbose) {
                            System.out.println(i + " copying 4 bytes");
                        }
                        copy(4);
                        break;
                    case 5 : // Long
                    case 6 : // Double
                        if (verbose) {
                            System.out.println(i + " copying 8 bytes");
                        }
                        copy(8);
                        i++;
                        break;
                    case 1 : // Utf8
                        int len = readU2();
                        writeU2(len);
                        byte[] utf8 = readBytes(len);
                        String str = new String(utf8, "UTF-8");
                        if (verbose) {
                            System.out.println(
                                i + " read class attr -- '" + str + "'");
                        }
                        if (str.equals(nameSDE)) {
                            sdeIndex = i;
                        }
                        writeBytes(utf8);
                        break;
                    default :
                        throw new IOException("unexpected tag: " + tag);
                }
            }
            return sdeIndex;
        }

        void writeUtf8ForSDE() {
            int len = nameSDE.length();
            writeU1(1); // Utf8 tag
            writeU2(len);
            for (int i = 0; i < len; ++i) {
                writeU1(nameSDE.charAt(i));
            }
        }
    }

    public static void evaluateNodes(
        Node.Nodes nodes,
        SmapStratum s,
        boolean breakAtLF) {
        try {
            nodes.visit(new SmapGenVisitor(s, breakAtLF));
        } catch (JasperException ex) {
        }
        s.optimizeLineSection();
    }

    static class SmapGenVisitor extends Node.Visitor {

        private SmapStratum smap;
        private boolean breakAtLF;

        SmapGenVisitor(SmapStratum s, boolean breakAtLF) {
            this.smap = s;
            this.breakAtLF = breakAtLF;
        }

        public void visit(Node.Declaration n) throws JasperException {
            doSmapText(n);
        }

        public void visit(Node.Expression n) throws JasperException {
            doSmapText(n);
        }

        public void visit(Node.Scriptlet n) throws JasperException {
            doSmapText(n);
        }

        public void visit(Node.IncludeAction n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.ForwardAction n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.GetProperty n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.SetProperty n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.UseBean n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.PlugIn n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.CustomTag n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.UninterpretedTag n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.JspElement n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.JspText n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.NamedAttribute n) throws JasperException {
            visitBody(n);
        }

        public void visit(Node.JspBody n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.InvokeAction n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.DoBodyAction n) throws JasperException {
            doSmap(n);
            visitBody(n);
        }

        public void visit(Node.ELExpression n) throws JasperException {
            doSmap(n);
        }

        public void visit(Node.TemplateText n) throws JasperException {
            Mark mark = n.getStart();
            if (mark == null) {
                return;
            }

            //Add the file information
            String fileName = mark.getFile();
            smap.addFile(unqualify(fileName), fileName);

            //Add a LineInfo that corresponds to the beginning of this node
            int iInputStartLine = mark.getLineNumber();
            int iOutputStartLine = n.getBeginJavaLine();
            int iOutputLineIncrement = breakAtLF? 1: 0;
            smap.addLineData(iInputStartLine, fileName, 1, iOutputStartLine, 
                             iOutputLineIncrement);

            // Output additional mappings in the text
            java.util.ArrayList extraSmap = n.getExtraSmap();

            if (extraSmap != null) {
                for (int i = 0; i < extraSmap.size(); i++) {
                    iOutputStartLine += iOutputLineIncrement;
                    smap.addLineData(
                        iInputStartLine+((Integer)extraSmap.get(i)).intValue(),
                        fileName,
                        1,
                        iOutputStartLine,
                        iOutputLineIncrement);
                }
            }
        }

        private void doSmap(
            Node n,
            int inLineCount,
            int outIncrement,
            int skippedLines) {
            Mark mark = n.getStart();
            if (mark == null) {
                return;
            }

            String unqualifiedName = unqualify(mark.getFile());
            smap.addFile(unqualifiedName, mark.getFile());
            smap.addLineData(
                mark.getLineNumber() + skippedLines,
                mark.getFile(),
                inLineCount - skippedLines,
                n.getBeginJavaLine() + skippedLines,
                outIncrement);
        }

        private void doSmap(Node n) {
            doSmap(n, 1, n.getEndJavaLine() - n.getBeginJavaLine(), 0);
        }

        private void doSmapText(Node n) {
            String text = n.getText();
            int index = 0;
            int next = 0;
            int lineCount = 1;
            int skippedLines = 0;
            boolean slashStarSeen = false;
            boolean beginning = true;

            // Count lines inside text, but skipping comment lines at the
            // beginning of the text.
            while ((next = text.indexOf('\n', index)) > -1) {
                if (beginning) {
                    String line = text.substring(index, next).trim();
                    if (!slashStarSeen && line.startsWith("/*")) {
                        slashStarSeen = true;
                    }
                    if (slashStarSeen) {
                        skippedLines++;
                        int endIndex = line.indexOf("*/");
                        if (endIndex >= 0) {
                            // End of /* */ comment
                            slashStarSeen = false;
                            if (endIndex < line.length() - 2) {
                                // Some executable code after comment
                                skippedLines--;
                                beginning = false;
                            }
                        }
                    } else if (line.length() == 0 || line.startsWith("//")) {
                        skippedLines++;
                    } else {
                        beginning = false;
                    }
                }
                lineCount++;
                index = next + 1;
            }

            doSmap(n, lineCount, 1, skippedLines);
        }
    }
}
