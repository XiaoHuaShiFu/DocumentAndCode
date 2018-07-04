package xhsfutil;

import java.util.*;

/**
 *实现Comparable<String>接口的类
 * @author XHSF
 *
 */

public class LengthComparator implements Comparator<String>{
	
	public int compare(String first,String second) {
		return first.length() - second.length();
	}
	
}
