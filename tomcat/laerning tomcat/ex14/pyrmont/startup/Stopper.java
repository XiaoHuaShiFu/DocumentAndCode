package ex14.pyrmont.startup;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-17 13:12
 */
public class Stopper {
    public static void main(String[] args) throws IOException {
        int port = 8005;
        Socket socket = new Socket("127.0.0.1", port);
        OutputStream stream = socket.getOutputStream();
        String shutdown = "SHUTDOWN";
        for (int i = 0; i < shutdown.length(); i++) {
            stream.write(shutdown.charAt(i));
        }
        stream.flush();
        stream.close();
        socket.close();
    }
}
