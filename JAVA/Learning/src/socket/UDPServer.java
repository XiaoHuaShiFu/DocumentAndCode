package socket;

import java.io.IOException;
import java.net.*;

public class UDPServer {
	
	public static void main(String[] args) throws IOException {
		/**
		 * 接受客户端发送的数据
		 */
		//1.创建服务器端DatagramSocket，指定端口号
		DatagramSocket socket = new DatagramSocket(2018);
		//2.创建数据报，用于接受客户端发送的数据
		//数据报的数据是保存在字节数组中
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		//3.接受客户端发送的数据，保存在数据报中
		System.out.println("服务器端已启动，等待接收数据");
		socket.receive(packet);//在接收到数据之前，处于阻塞状态
		//4.读取数据
		String info = new String(data, 0, packet.getLength());
		System.out.println("我是服务器，客户端说：" + info);
		
		
		/**
		 * 响应客户端发送的数据
		 */
		//1.定义客户端的地址，端口号，要发送的数据
		InetAddress address = packet.getAddress();
		int port = packet.getPort();
		byte[] data1 = "登陆成功！！！".getBytes();
		//2.创建数据报，包含客户端的地址，端口号，要发送的数据
		DatagramPacket packet1 = new DatagramPacket(data1, data1.length, address, port);
		//3.响应客户端
		socket.send(packet1);
		
		
		//最后，关闭资源
		socket.close();
	}
	
}
