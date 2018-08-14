package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;

public class ServerThread extends Thread{
	
	Socket socket = null;
	
	public ServerThread(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		InputStream is = null;
		InputStreamReader isr  = null;
		BufferedReader br = null;
		OutputStream os  = null;
		PrintWriter pw  = null;
		try {
			//1.获取输入流，读取客户端发送的信息
			is = socket.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			String info = null;
			while ((info = br.readLine()) != null) {
				System.out.println("我是服务器，客户端说：" + info);
			}
			InetAddress address = socket.getInetAddress();
			System.out.println("我是服务器，客户端的IP是：" + address.getHostAddress());
			//关闭输入流
			socket.shutdownInput();
			//2.获取输出流，响应客户端的请求
			os = socket.getOutputStream();
			pw = new PrintWriter(os);
			pw.write("登录成功！！！");
			pw.flush();
			socket.shutdownOutput();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (isr != null) {
					isr.close();
				}
				if (os != null) {
					is.close();
				}
				if (is != null) {
					is.close();
				}
				if (pw != null) {
					pw.close();
				}
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
	}
	
}
