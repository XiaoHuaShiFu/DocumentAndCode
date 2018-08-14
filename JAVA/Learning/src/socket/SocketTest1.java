package socket;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;
import java.util.*;

public class SocketTest1 {

	public static void main(String[] args) throws UnknownHostException, IOException {
		try (Socket socket = new Socket("localhost", 2018);
				Scanner in = new Scanner(socket.getInputStream(),"UTF-8")){
			socket.setSoTimeout(10000);
			InetAddress address = InetAddress.getByName("time-a.nist.gov");
			byte[] addressBytes = address.getAddress();
			System.out.println(Arrays.toString(addressBytes) + InetAddress.getLocalHost());
			while (in.hasNextLine()) {
				String line = in.nextLine();
				System.out.println(line);
			}
		} catch (InterruptedIOException e) {
			e.printStackTrace();
		}
		
//		try (Socket socket = new Socket();
//				Scanner in = new Scanner(socket.getInputStream(),"UTF-8")){
//			socket.connect(new InetSocketAddress("localhost", 2018), 10000);
//			while (in.hasNextLine()) {
//				String line = in.nextLine();
//				System.out.println(line);
//			}
//		} catch (InterruptedIOException e) {
//			e.printStackTrace();
//		}
		
	} 

}
