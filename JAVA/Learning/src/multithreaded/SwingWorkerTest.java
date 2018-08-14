package multithreaded;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class SwingWorkerTest {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			JFrame frame = new SwingWorkerFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		});

	}

}
