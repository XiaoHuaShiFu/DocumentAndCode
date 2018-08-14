package socket;

import java.io.*;
import java.net.*;
import java.util.*;

public class ThreadedEchoHandler implements Runnable{
	
	private Socket incoming;
	
	public ThreadedEchoHandler(Socket incoming) {
		this.incoming = incoming;
	}
	
	public void run() {
		try (InputStream is = incoming.getInputStream();
				OutputStream os = incoming.getOutputStream()) {
			Scanner in = new Scanner(is, "UTF-8");
			PrintWriter out = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
			out.println("Hello Client!");
			boolean done = false;
			while (!done && in.hasNextLine()) {
				String line = in.nextLine();
				System.out.println("Server info: " + line);
				out.println("Echo" + line);
				if (line.trim().equals("BYE")) {
					done = true;
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
