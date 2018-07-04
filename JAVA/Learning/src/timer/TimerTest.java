package timer;

import java.awt.Toolkit;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.Timer;

public class TimerTest {

	public static void main(String[] args) {
		
//		构造一个ActionListener对象
//		ActionListener listener = new TimePrinter();
//		一个计时器，定时执行所传递的对象
//		Timer t = new Timer(1000,listener);
		
		
		
		
		//lambda写法
		//不用指定类型，因为编译器会检测出来
		Timer t = new Timer(1000,event -> {
			System.out.println("The time is " + new Date());
			Toolkit.getDefaultToolkit().beep();
		});
		
		
		
		//开始计时
		t.start();
		JOptionPane.showConfirmDialog(null, "Quit program?");
		System.exit(0);
		

	}

}
