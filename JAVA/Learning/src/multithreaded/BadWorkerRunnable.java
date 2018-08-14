package multithreaded;

import java.util.*;

import javax.swing.*;

public class BadWorkerRunnable implements Runnable{
	private JComboBox<Integer> combo;
	private Random generator;
	
	public BadWorkerRunnable(JComboBox<Integer> combo) {
		this.combo = combo;
		this.generator = new Random();
	}
	
	public void run() {
		try {
			while (true) {
				int i = Math.abs(generator.nextInt());
				if (i % 2 == 0) {
					combo.insertItemAt(i, 0);
				} else if (combo.getItemCount() > 0) {
					combo.removeItemAt(i % combo.getItemCount());
				}
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {
		}
	}
}
