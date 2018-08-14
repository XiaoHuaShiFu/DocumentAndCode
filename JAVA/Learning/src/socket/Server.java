package socket;

import java.io.*;
import java.net.*;

public class Server {

	public static void main(String[] args) throws IOException {
		try {
			//1.创建一个服务器端socket，即ServerSocket，并绑定指定出端口，并监听此窗口
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(2018);
			int count = 0;
			System.out.println("服务器即将启动，等待客户端的连接");
			//循环监听，等待客户端的连接
			while (true) {
				//2.调用accept()方法开始监听，等待客户端的连接
				Socket socket = serverSocket.accept();
				//3.创建一个新的线程
				ServerThread serverThread = new ServerThread(socket);
				serverThread.start();
				//记录连接的客户端数量
				count++;
				System.out.println("客户端数量为：" + count);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
//		//3.获取输入流，读取客户端发送的信息
//		InputStream is = socket.getInputStream();
//		InputStreamReader isr = new InputStreamReader(is);
//		BufferedReader br = new BufferedReader(isr);
//		String info = null;
//		while ((info = br.readLine()) != null) {
//			System.out.println("我是服务器，客户端说：" + info);
//		}
//		//关闭输入流
//		socket.shutdownInput();
//		//4.获取输出流，响应客户端的请求
//		OutputStream os = socket.getOutputStream();
//		PrintWriter pw = new PrintWriter(os);
//		pw.write("登录成功！！！");
//		pw.flush();
//		socket.shutdownOutput();
//		//5.关闭相关资源
//		br.close();
//		isr.close();
//		is.close();
//		os.close();
//		pw.close();
//		socket.close();
		
	}
	
}
