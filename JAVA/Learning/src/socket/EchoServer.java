package socket;

import java.io.*;
import java.net.*;
import java.util.*;

public class EchoServer {

	public static void main(String[] args) throws IOException {
		try (ServerSocket serverSocket = new ServerSocket(2018)) {
			int count = 0;
			
			while (true) {
				Socket incoming = serverSocket.accept();
				Runnable r= new ThreadedEchoHandler(incoming);
				Thread t = new Thread(r);
				t.start();
				count++;
				System.out.println("Server info: count=" + count + ".");
			}
		}
	}

}
