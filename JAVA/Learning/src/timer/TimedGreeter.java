package timer;

import javax.swing.Timer;

class TimedGreeter extends Greeter{
	
	public void greet() {
		Timer t = new Timer(1000, System.out::println);
		t.start();
	}
	
}
