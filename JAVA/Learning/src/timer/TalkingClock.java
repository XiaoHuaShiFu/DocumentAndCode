package timer;


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;


public class TalkingClock {
	
	private int interval;//间隔
	private boolean beep;//是否开始
	
	public TalkingClock(int interval, boolean beep) {
		this.interval = interval;
		this.beep = beep;
	}

//-------------------------------------------------------------------------------//
	
	/*//开始方法，使用内部类
	public void start() {
		//创建对象后，编译器会把this传递给TimePrinter 
		//->ActionListener listener = new TimePrinter(this);
		ActionListener listener = this.new TimePrinter();
		Timer t = new Timer(interval, listener);
		t.start();
	}*/
	
	
	/*//用内部类实现接口的实现
	public class TimePrinter implements ActionListener{
		
		//自动创建，无需写
		TalkingClock outer;
		
		public TimePrinter(TalkingClock clock) {
			this.outer = clock;
		}
		
		public void actionPerformed(ActionEvent event) {
			System.out.println("At the tone,  the time is " + new Date());
			//可直接访问外围类的数据域，不管是private还是public
			if(TalkingClock.this.beep) Toolkit.getDefaultToolkit().beep();
		}
	}*/
	
//-------------------------------------------------------------------------------//
	
	//开始方法，使用局部内部类
	public void start() {
		//局部内部类，无需指定作用范围，因为只能在这个方法内有效
		class TimePrinter implements ActionListener{
			public void actionPerformed(ActionEvent event) {
				System.out.println("At the tone,  the time is " + new Date());
				if (beep) Toolkit.getDefaultToolkit().beep();
			}
		}
		
		ActionListener listener = new TimePrinter();
		Timer t = new Timer(interval, listener);
		t.start();
	}
	
	/*//开始方法，使用局部内部类，并访问局部变量
	//就无需在TalkingClock的实例域定义变量beep了
	public void start(int interval, boolean beep) {
		//局部内部类，无需指定作用范围，因为只能在这个方法内有效
		class TimePrinter implements ActionListener{
			public void actionPerformed(ActionEvent event) {
				System.out.println("At the tone,  the time is " + new Date());
				if (beep) Toolkit.getDefaultToolkit().beep();
			}
		}
		
		ActionListener listener = new TimePrinter();
		Timer t = new Timer(interval, listener);
		t.start();
	}*/
	
//-------------------------------------------------------------------------------//
	
	/*//开始方法，使用匿名内部类，如果该只需该类的一个对象
	public void start(int interval, boolean beep) {
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("At the tone,  the time is " + new Date());
				if (beep) Toolkit.getDefaultToolkit().beep();
			}
		};
		
		Timer t = new Timer(interval, listener);
		t.start();
	}*/
	
//-------------------------------------------------------------------------------//
	
	//开始方法，使用lambda
	public void start(int interval, boolean beep) {
		Timer t = new Timer(interval, event -> {
			System.out.println("At the tone,  the time is " + new Date());
			if (beep) Toolkit.getDefaultToolkit().beep();
		});
		t.start();
	}
	
}
