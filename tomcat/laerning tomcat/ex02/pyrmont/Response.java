package ex02.pyrmont;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.*;
import java.util.Locale;

/**
 * HTTP Response = Status-Line
 * (( general-header | response-header | entity-header ) CRLF)
 * CRLF
 * [ message-body ]
 * Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
 */
public class Response implements ServletResponse {

    /**
     * 每次读取大小
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * 请求
     */
    private Request request;

    /**
     * 输出流
     */
    private OutputStream output;

    /**
     * 格式化输出流
     */
    private PrintWriter writer;

    /**
     * 错误信息
     */
    private static final String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
            "Content-Type: text/html\r\n" +
            "Content-Length: 23\r\n" +
            "\r\n" +
            "<h1>File Not Found</h1>";

    public Response(OutputStream output) {
        this.output = output;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    /**
     * this method is used to serve static pages
     *
     * @throws IOException .
     */
    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        File file = new File(Constants.WEB_ROOT, request.getUri());
        try (FileInputStream fis = new FileInputStream(file)) {
            int ch = fis.read(bytes, 0, BUFFER_SIZE);
            boolean isFirst = true;
            while (ch != -1) {
                // 根据 HTTP 协议, 输出HTTP头信息
                if (isFirst) {
                    output.write("HTTP/1.0 200 OK\r\n\r\n".getBytes());
                    isFirst = false;
                }
                output.write(bytes, 0, ch);
                ch = fis.read(bytes, 0, BUFFER_SIZE);
            }
        } catch (Exception e) {
            // file not found
            output.write(errorMessage.getBytes());
        }
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    /**
     * autoflush is true, println() will flush,
     * but print() will not.
     * @return PrintWriter
     * @throws IOException .
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(output, true);
    }

    @Override
    public void setCharacterEncoding(String charset) {

    }

    @Override
    public void setContentLength(int len) {

    }

    @Override
    public void setContentType(String type) {

    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }
}