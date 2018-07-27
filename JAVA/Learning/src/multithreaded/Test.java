package multithreaded;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Set<String> words = ConcurrentHashMap.<String>newKeySet();
		words.add("java");
		words.add("java1");
		words.forEach((s) ->{
			System.out.println(s);
		});
		
		String contents = new String(Files.readAllBytes(Paths.get("file\\raf.dat")), StandardCharsets.UTF_8);
		String[] word = contents.split("[|]");
		Arrays.parallelSort(word, Comparator.comparing(String::length));
		System.out.println(Arrays.toString(word));
		
		int[] arr = new int[20];
		Arrays.parallelSetAll(arr, i -> i);
		System.out.println(Arrays.toString(arr));
		
		int[] values = new int[10];
		for (int i = 0; i < values.length; i++) {
			values[i] = i + 1;
		}
		Arrays.parallelPrefix(values, (x, y) -> 
			(x % 2 == 1) ? x * y : y
		);
		System.out.println(Arrays.toString(values));
	}

}
