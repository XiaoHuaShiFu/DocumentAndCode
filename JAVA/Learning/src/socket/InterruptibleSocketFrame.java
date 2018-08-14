package socket;

import java.util.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;

import javax.swing.*;



public class InterruptibleSocketFrame extends JFrame{
	
	/**
	 * serialID
	 */
	private static final long serialVersionUID = 5317574728954252538L;
	private Scanner in;
	private JButton interruptibleButton;
	private JButton blockingButton;
	private JButton cancelButton;
	private JTextArea messages;
	private TestServer server;
	private Thread connectThread;
	
	public InterruptibleSocketFrame() {
		JPanel northPanel = new JPanel();
		add(northPanel, BorderLayout.NORTH);
		
		final int TEXT_ROWS = 20;
		final int TEXT_COLUMNS = 60;
		messages = new JTextArea(TEXT_ROWS, TEXT_COLUMNS);
		add(new JScrollPane(messages));
		
		interruptibleButton = new JButton("Interruptible");
		blockingButton = new JButton("Blocking");
		
		northPanel.add(interruptibleButton);
		northPanel.add(blockingButton);
		
		interruptibleButton.addActionListener(event -> {
			interruptibleButton.setEnabled(false);
			blockingButton.setEnabled(false);
			cancelButton.setEnabled(true);
			connectThread = new Thread(() -> {
				try {
					connectInterruptibly();
				} catch (IOException e) {
					messages.append("\nInterruptibleSocketTest.connectInterruptibly:" + e);
				}
			});
			connectThread.start();
		});
		
		blockingButton.addActionListener(event -> {
			interruptibleButton.setEnabled(false);
			blockingButton.setEnabled(false);
			cancelButton.setEnabled(true);
			connectThread = new Thread(() -> {
				try {
					connectBlocking();
				} catch (IOException e) {
					messages.append("\nInterruptibleSocketTest.connectBlocking:" + e);
				}
			});
			connectThread.start();
		});
		
		cancelButton = new JButton("Cancel");
		cancelButton.setEnabled(false);
		northPanel.add(cancelButton);
		cancelButton.addActionListener(event -> {
			connectThread.interrupt();
			cancelButton.setEnabled(false);
		});
		
		server = new TestServer();
		new Thread(server).start();
		pack();
	}
	
	
	/**
	 * Connects to the test server, using interruptible I/O.
	 */
	public void connectInterruptibly() throws IOException {
		messages.append("Interruptible:\n");
		try (SocketChannel channel = SocketChannel.open(new InetSocketAddress("localhost",2018))) {
			in = new Scanner(channel, "UTF-8");
			while (!Thread.currentThread().isInterrupted()) {
				messages.append("Reading");
				if (in.hasNextLine()) {
					String line = in.nextLine();
					messages.append(line);
					messages.append("\n");
				}
			}
		} finally {
			EventQueue.invokeLater(() -> {
				messages.append("Channel closed\n");
				interruptibleButton.setEnabled(true);
				blockingButton.setEnabled(true);
			});
		}
	}
	
	/**
	 * Connects to the test server, using blocking I/O.
	 */
	public void connectBlocking() throws IOException {
		messages.append("Blocking:\n");
		try (Socket socket = new Socket("localhost",2018)) {
			in = new Scanner(socket.getInputStream(), "UTF-8");
			while (!Thread.currentThread().isInterrupted()) {
				messages.append("Reading");
				if (in.hasNextLine()) {
					String line = in.nextLine();
					messages.append(line);
					messages.append("\n");
				}
			}
		} finally {
			EventQueue.invokeLater(() -> {
				messages.append("Socket closed\n");
				interruptibleButton.setEnabled(true);
				blockingButton.setEnabled(true);
			});
		}
	}
	
	
	/**
	 * A multithreaded server that listens to port 2018 and sends numbers to the client, simulating
	 * a hanging server after 10 numbers.
	 */
	class TestServer implements Runnable {
		public void run() {
			try (ServerSocket s = new ServerSocket(2018)) {
				while (true) {
					Socket incoming = s.accept();
					Runnable r = new TestServerHandler(incoming);
					Thread t = new Thread(r);
					t.start();
				}
			} catch (IOException e) {
				messages.append("\nTestServer.run:" + e);
			}
		}
	}
	
	/**
	 * This class handles the client input for one server socket connection.
	 */
	class TestServerHandler implements Runnable {
		private Socket incoming;
		private int counter;
		
		/**
		 * Constructs a handler
		 */
		public TestServerHandler(Socket incoming) {
			this.incoming = incoming;
		}
		
		public void run() {
			try {
				try {
					OutputStream outStream = incoming.getOutputStream();
					PrintWriter out = new PrintWriter(
							new OutputStreamWriter(outStream, "UTF-8"), true /*antoFlush*/);
					while (counter < 1000) {
						counter++;
						if (counter <= 10) {
							out.println(counter);
						}
						Thread.sleep(100);
					}
				}
				finally {
					incoming.close();
					messages.append("Closing server\n");
				}
			} catch (Exception e) {
				messages.append("\nTestServerHandle.run:" + e);
			}
		}
		
		
	}
	
	
}
