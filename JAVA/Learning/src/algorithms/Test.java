package algorithms;

public class test {
	
	public static void main(String[] args) {
		TreeMap<Integer, String> map = new TreeMap<>();
		map.add(112, "wjx");
		map.add(123, "jws");
		map.add(41, "ds");
		map.add(122, "das");
		map.add(110, "eqw");
		map.upadate(110,"test");
		map.delete(110);
		String name = map.query(110);
		System.out.print(name);
	}
	
}
