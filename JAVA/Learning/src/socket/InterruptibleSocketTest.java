package socket;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class InterruptibleSocketTest {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			JFrame frame = new InterruptibleSocketFrame();
			frame.setTitle("InterruptibleSocketTest");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		});

	}

}
