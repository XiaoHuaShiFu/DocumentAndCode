package timer;

import java.awt.Toolkit;
import java.awt.event.*;
import java.util.Date;

/**
 * 实现ActionListener接口的类
 * @author XHSF
 *
 */
class TimePrinter implements ActionListener{
	
	//监听器actionPerformed
	public void actionPerformed(ActionEvent event) {
		System.out.println("At the tone, the time is " + new Date());
		Toolkit.getDefaultToolkit().beep();
	}
	
}
