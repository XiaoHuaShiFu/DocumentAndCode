package ex01.pyrmont;

import java.io.*;
import java.net.Socket;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-06 15:26
 */
public class SocketTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        new SocketTest().test();
    }

    public void test() throws IOException, InterruptedException {
        Socket socket = new Socket("127.0.0.1", 8000);
        OutputStream os = socket.getOutputStream();
        PrintStream out = new PrintStream(os, true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //send an HTTP request to the web server
        out.println("GET /users HTTP/1.1");
        out.println("Host: localhost:8000");
        out.println("Connection: Close");
        out.println();

        //read the response
        StringBuilder sb = new StringBuilder(8096);
        boolean loop = true;
        while (loop) {
            if (in.ready()) {
                int i = 0;
                while (i != -1) {
                    i = in.read();
                    sb.append((char) i);
                }
                loop = false;
            }
            Thread.sleep(50);
        }

        System.out.println(sb.toString());
        socket.close();
    }

}
