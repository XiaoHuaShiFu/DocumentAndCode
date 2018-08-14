package multithreaded;

import java.awt.*;

import javax.swing.*;

public class SwingThreadTest {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			JFrame frame = new SwingThreadFrame();
			frame.setTitle("SwingThreadTest");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		});

	}
	

}
