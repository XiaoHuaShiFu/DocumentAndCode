package ex02.pyrmont;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-10 14:03
 */
public class HttpServer1 {

    /**
     * shutdown command
     */
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    /**
     * the shutdown command received
     */
    private boolean shutdown = false;

    /**
     * monitor port
     */
    private int port = 8080;

    /**
     * backlog
     */
    private int backlog = 1;

    /**
     * bind address
     */
    private String address = "127.0.0.1";

    public HttpServer1() {
    }

    public HttpServer1(int port, int backlog, String address) {
        this.port = port;
        this.backlog = backlog;
        this.address = address;
    }

    public static void main(String[] args) {
        HttpServer1 server = new HttpServer1();
        server.await();
    }

    public void await() {
        try (ServerSocket serverSocket = new ServerSocket(port, backlog, InetAddress.getByName(address))) {
            // Loop waiting for a request
            while (!shutdown) {
                InputStream input;
                OutputStream output;
                try (Socket socket = serverSocket.accept()) {
                    input = socket.getInputStream();
                    output = socket.getOutputStream();

                    // create Request object and parse
                    Request request = new Request(input);
                    request.parse();

                    // create Response object
                    Response response = new Response(output);
                    response.setRequest(request);

                    // check if this is a request for a servlet or
                    // a static resource
                    // a request for a servlet begins with "/servlet/"
                    if (request.getUri().startsWith("/servlet/")) {
                        ServletProcessor1 processor = new ServletProcessor1();
                        processor.process(request, response);
                    } else {
                        StaticResourceProcessor processor = new StaticResourceProcessor();
                        processor.process(request, response);
                    }

                    //check if the previous URI is a shutdown command
                    shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
