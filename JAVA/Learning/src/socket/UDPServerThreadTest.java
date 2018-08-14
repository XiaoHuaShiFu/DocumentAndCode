package socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServerThreadTest {

	public static void main(String[] args) throws SocketException {
		//1.创建服务器端DatagramSocket，指定端口号
		DatagramSocket socket = new DatagramSocket(2018);
		int count = 0;
		System.out.println("服务器端已启动，等待接收数据");
		
		while (true) {
			//2.创建数据报，用于接受客户端发送的数据
			//数据报的数据是保存在字节数组中
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			//3.接受客户端发送的数据，保存在数据报中
			try {
				//在接收到数据之前，处于阻塞状态
				socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			UDPServerThread udpServerThread = new UDPServerThread(socket, data, packet);
			udpServerThread.start();
			
			count++;
			System.out.println("我是服务器，客户端的连接数为：" + count);
		}
	}

}
