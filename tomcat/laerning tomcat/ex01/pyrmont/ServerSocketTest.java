package ex01.pyrmont;

import java.io.File;
import java.io.IOException;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-09-06 15:56
 */
public class ServerSocketTest {

    public static void main(String[] args) throws IOException {
//        ServerSocket serverSocket = new ServerSocket(8000, 2, InetAddress.getByName("127.0.0.1"));
//        Socket accept = serverSocket.accept();
//        System.out.println(accept);
        System.out.println(System.getProperty("user.dir") + File.separator + "webroot");
    }
}

