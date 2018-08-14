package socket;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * 从web读取数据
 * @author lenovo
 *
 */

public class URLConnectionTest {

	public static void main(String[] args) {
		try {
			String urlName;
			if (args.length > 0) urlName = args[0];
			else urlName = "http://horstmann.com";
			
			
			URL url = new URL(urlName);
			URLConnection connection = url.openConnection();
			
			connection.connect();
			
			Map<String, List<String>>  headers = connection.getHeaderFields();
			for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
				String key = entry.getKey();
				for (String value : entry.getValue()) {
					System.out.println(key + ":" + value);
				}
			}
			
			System.out.println("--------------");
			System.out.println("getContentType:" + connection.getContentType());
			System.out.println("getContentLength:" + connection.getContentLength());
			System.out.println("getContentEncoding:" + connection.getContentEncoding());
			System.out.println("getDate:" + connection.getDate());
			System.out.println("getExpiration:" + connection.getExpiration());
			System.out.println("getLastModified:" + connection.getLastModified());
			System.out.println("--------------");
			
			String encoding = connection.getContentEncoding();
			
			if (encoding == null) {
				encoding = "UTF-8";
			}
			
			try (Scanner in = new Scanner(connection.getInputStream(),encoding)) {
				for (int n = 1; in.hasNextLine() && n <= 10; n++) {
					System.out.println(in.nextLine());
				}
				if (in.hasNextLine()) {
					System.out.println(". . .");
				}
			} 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
