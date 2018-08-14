package socket;

import java.io.IOException;
import java.net.*;

public class UDPClient {
	
	public static void main(String[] args) throws IOException {
		/**
		 * 向服务器端发送数据
		 */
		//1.定义服务器的地址，端口号，数据
		InetAddress address = InetAddress.getByName("localhost");
		int port = 2018;
		byte[] data = "username:xhsf;password:123456;".getBytes();
		//2.创建数据报，包含服务器的地址，端口号，要发送的数据
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
		//3.创建DatagramSocket
		DatagramSocket socket = new DatagramSocket();
		//4.向服务器端发送数据报
		socket.send(packet);
		
		/**
		 * 接收服务器端的数据
		 */
		//1.创建数据报，用于保存服务器端发送的数据
		byte[] data1 = new byte[1024];
		DatagramPacket packet1 = new DatagramPacket(data1, data1.length);
		//2.接受服务器端发送的数据
		socket.receive(packet1);
		//3.读取数据
		String apply = new String(data1, 0, packet1.getLength());
		System.out.println("我是客户端，服务器说：" + apply);
		
		
		//最后，关闭资源
		socket.close();
	}
	
}
