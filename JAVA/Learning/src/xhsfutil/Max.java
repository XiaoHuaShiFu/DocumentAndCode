package xhsfutil;

/**
 * 
 * @author XHSF
 *
 */

public class Max {
	
	public static double max(double... values) {
		double largest = Double.NEGATIVE_INFINITY;
		for (double v : values) {
			if (v > largest) {
				largest = v;
			}
		}
		return largest;
	}
	
}