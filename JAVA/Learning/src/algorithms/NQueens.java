package algorithms;

public class NQueens {
	
	private static int count = 0;
	private static int[] a;
	
	public static int queues(int queens) {
		a = new int[queens];
		search(0);
		return count;
	}
	
	private static void search(int k) {
		if (k == a.length) {
			count++;
//			System.out.println(Arrays.toString(a));
		} else {
			for (int i = 0; i < a.length; i++) {
				a[k] = i;
				if (check(k)) {
					 search(k + 1);
				}
			}
		}
	}
	
	private static boolean check(int k) {
		for (int i = 0; i < k; i++) {
			if (Math.abs(a[k] - a[i]) == k - i || a[k] == a[i]) {
				return false;
			}
		}
		return true;
	}
	
}
