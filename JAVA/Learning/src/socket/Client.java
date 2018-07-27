package socket;

import java.io.*;
import java.net.*;

public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException {
		//1.创建客户端socket，指定服务器地址和端口
		Socket socket = new Socket("localhost",2018);
		//2.获取输出流，向服务器端发送信息
		OutputStream os = socket.getOutputStream();//字节输出流
		PrintWriter pw = new PrintWriter(os);//打印流
		pw.write("username:admin;password:123;gfc");
		pw.flush();
		//关闭输出流
		socket.shutdownOutput();
		//3.获取输入流，读取服务器端的响应信息
		InputStream is = socket.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String info = null;
		while ((info = br.readLine()) != null) {
			System.out.println("我是客户端，服务器说：" + info);
		}
		socket.shutdownInput();
		//4.关闭相关资源
		is.close();
		isr.close();
		br.close();
		os.close();
		pw.close();
		socket.close();
	}

}
