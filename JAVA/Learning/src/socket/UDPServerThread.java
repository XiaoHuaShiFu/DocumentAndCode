package socket;

import java.io.IOException;
import java.net.*;

public class UDPServerThread extends Thread{
	
	DatagramSocket socket = null;
	byte[] data = null;
	DatagramPacket packet = null;
	
	public UDPServerThread(DatagramSocket socket, byte[] data, DatagramPacket packet) {
		this.socket = socket;
		this.data = data;
		this.packet = packet;
	}
	
	public void run(){
		/**
		 * 读取客户端发送的数据
		 */
		//1.读取数据
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
		try {
			socket.send(packet1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
