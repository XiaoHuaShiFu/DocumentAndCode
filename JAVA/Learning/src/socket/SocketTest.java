package socket;

import java.io.*;
import java.net.*;
import java.util.*;

public class SocketTest {

	public static void main(String[] args) throws UnknownHostException, IOException {
//		try (Socket s = new Socket("www.baidu.com",13);Scanner in = new Scanner(s.getInputStream(),"UTF-8")){
//			while (in.hasNextLine()) {
//				String line = in.nextLine();
//				System.out.println(line);
//			}
//		}
		///////////////////////////////////////////////////////////////////////////////////////////////
		/**
		 * InetAddress类
		 */
		//获取本机InetAddress实例，可以查看ip地址，主机名
		InetAddress address = InetAddress.getLocalHost();
		System.out.println(address.getHostName() + "|" + address.getHostAddress() 
					+ "|" + Arrays.toString(address.getAddress()) + "|" + address);
		//根据主机名orIP地址获取AnetAddress实例
		InetAddress address1 = InetAddress.getByName("time-a.nist.gov");
		System.out.println(address1);
		
		
		/////////////////////////////////////////////////////////////////////////////////////////
		/**
		 * URL类
		 */
		URL imooc = new URL("https://www.imooc.com");
		//?后面是参数#后面是锚点
		URL url = new URL(imooc,"/index.html?username=tom#test");
		//获取协议，主机名，端口号（如果未指定端口号，则返回-1）,文件的路径
		System.out.println("获取协议" + url.getProtocol());
		System.out.println("获取主机名" + url.getHost());
		System.out.println("获取端口号" + url.getPort());
		System.out.println("获取文件的路径" + url.getPath());
		System.out.println("获取文件名" + url.getFile());
		System.out.println("获取相对路径" + url.getRef());
		System.out.println("查询字符串" + url.getQuery());
		
		URL baidu = new URL("https://www.baidu.com/");
		//获取字节输入流
		InputStream is = baidu.openStream();
		//转换为字符输入流
		InputStreamReader isr = new InputStreamReader(is,"utf-8");
		//添加缓冲
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
		is.close();
		isr.close();
		br.close();
		
	} 

}
