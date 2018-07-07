package timer;

public class RepeatTest {

	public static void main(String[] args) {
		
		//重复运行某个操作
		Repeat.repeat(10,() -> System.out.println("hello world"));
		Repeat.repeat(10, i -> System.out.println("Countdown:" + (9 - i)));
		
	}
	
}
