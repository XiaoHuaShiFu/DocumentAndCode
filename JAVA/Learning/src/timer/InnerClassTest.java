package timer;

import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import timer.TalkingClock;

public class InnerClassTest {

	public static void main(String[] args) {
		TalkingClock clock = new TalkingClock(1000, true);
		clock.start();
		
		/*//使用内部类的构造器
		  //直接调用TimePrinter内部类的构造器
		TalkingClock clock = new TalkingClock(1000, true);
		TalkingClock.TimePrinter listener = clock.new TimePrinter();
		Timer t = new Timer(1000, listener);
		t.start();*/
		
		//退出程序
		JOptionPane.showMessageDialog(null, "Quit program?");
		System.exit(0);
	}

}
