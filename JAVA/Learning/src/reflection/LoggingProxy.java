package reflection;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;
import java.util.logging.Logger;

public class LoggingProxy {
	
	/**
	 * 随机数生成变量
	 * @param args
	 */
	public static void main(String[] args) {
		@SuppressWarnings("serial")
		Random genneator = new Random() {
			public double nextDouble() {
				double result = super.nextDouble();
				Logger.getGlobal().info("nextDouble: " + result);
				//获取堆栈轨迹
				Thread.dumpStack();
				//捕获堆栈轨迹到字符串中
				StringWriter out = new StringWriter();
				new Throwable().printStackTrace(new PrintWriter(out));
				String description = out.toString();
				System.out.println(description);
				
				return result;
			}
		};
		System.out.println(genneator.nextDouble());
	}

}
