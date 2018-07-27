package multithreaded;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * The frame with ball component and buttons
 * @author lenovo
 *
 */

public class BounceFrame extends JFrame{
	
	/**
	 * serialID
	 */
	private static final long serialVersionUID = 3655191286669445717L;
	private BallComponent comp;
	public static final int STEPS = 1000;
	public static final int DELAY = 3;
	
	/**
	 * Constructs the frame with the component for showing the bouncing ball and Start and Close buttons
	 */
	public BounceFrame() {
		this.setTitle("Bounce");
		this.comp = new BallComponent();
		this.add(this.comp, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		this.addButton(buttonPanel, "Start", event -> addBall());
		this.addButton(buttonPanel, "Close", event -> System.exit(0));
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.pack();
	}
	
	/**
	 * Adds a button to a container.
	 * 
	 */
	public void addButton(Container c, String title, ActionListener listener) {
		JButton button = new JButton(title);
		c.add(button);
		button.addActionListener(listener);
	}
	
	/**
	 * Adds a bouncing ball to the panel and makes it bounce 1000 times.
	 */
	public void addBall() {
		Ball ball = new Ball();
		this.comp.add(ball);
		Runnable r = () -> {
			try {
				for (int i = 1; i < STEPS; i++) {
					ball.move(this.comp.getBounds());
					this.comp.paint(this.comp.getGraphics());
					Thread.sleep(DELAY);
				} 
			} catch (InterruptedException e) {
			}
		};
		Thread t = new Thread(r);
		t.start();
	}
	
}
