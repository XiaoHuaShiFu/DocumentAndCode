package reflection;


public class StackTraceTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		factorial(3);
	}
	
	public static int factorial(int n) {
		System.out.println("factorial(" + n + "):");
		Throwable t = new Throwable();
		//获得构造这个对象时调用堆栈的跟踪
		StackTraceElement[] frames = t.getStackTrace();
		for (StackTraceElement f : frames) {
			System.out.println(f);
		}
		int r;
		if (n <= 1) r = 1;
		else r = n * factorial(n - 1);
		System.out.println("return " + r);
		return r;
	}
	
}
