package dataStructures;

import java.time.LocalDate;
import java.util.*;

import human.Employee;
import human.Manager;

public class Test {
	
	public static void main(String[] args) {

		/**
		 * 创建一个集合
		 */
		Collection<String> c = new ArrayList<String>();
		c.add("wjx");
		c.add("wjs");
		c.add("wjt");
		/**
		 * 请求迭代器
		 */
		Iterator<String> iter = c.iterator();
		/**
		 * 用remove删除一个元素
		 */
		iter.next();
		iter.remove();
		/**
		 * 使用forEachRemaining方法实现遍历
		 */
		iter.forEachRemaining(element -> {
			System.out.println(element + "3");
		});
		/**
		 * 使用next实现遍历
		 */
		while (iter.hasNext()) {
			String element = iter.next();
			System.out.println(element + "1");
		}
		/**
		 * 使用for each实现遍历
		 */
		for (String element : c) {
			System.out.println(element + "2");
		}
		
		List<String> list1 = new LinkedList<String>();
		list1.add("wjx");
		list1.add("wjs");
		list1.set(0, "wjc");
		list1.forEach(element -> {
			System.out.println(element);
		});

		//////////////////////////////////////////////////////////////
		/**
		 * 链表
		 */
		List<String> staff1 = new LinkedList<>();
		staff1.add("Amy");
		staff1.add("Bob");
		staff1.add("Carl");
		Iterator<String> iter1 = staff1.iterator();
		String first = (String) iter1.next();
		String second = (String) iter1.next();
		System.out.println(first + second);
		iter1.remove();
		/**
		 * 实现ListIterator接口，可以反向遍历
		 */
		ListIterator<String> iter2 = staff1.listIterator();
		iter2.next();
		iter2.add("Juliet");
		iter2.previous();
		iter2.previous();
		/**
		 * set方法会替换对应结点的值
		 */
		iter2.set("wjx");
		iter2.forEachRemaining(element -> {
			System.out.println(element);
		});
		/**
		 * 获取对应下标的值（效率低，不推荐使用）
		 */
		System.out.println(staff1.get(0));
		/**
		 * 获取当前索引
		 */
		System.out.println(iter2.previousIndex());
		System.out.println(iter2.nextIndex());
		/**
		 * 打印链表方法
		 */
		System.out.println(staff1.toString());
		/**
		 * 移除链表中包含在参数链表中的元素
		 */
		staff1.removeAll(staff1);
		
		//////////////////////////////////////////////////////////////////////
		/**
		 * set类
		 */
		Set<String> words1 = new HashSet<>();
		words1.add("wjx");
		words1.add("wjs");
		words1.add("wjc");
		words1.add("wjt");
		
		Iterator<String> iter3 = words1.iterator();
		iter3.forEachRemaining(element -> {
			System.out.println(element);
		});
		
		///////////////////////////////////////////////////////////////////////////
		/**
		 * 树集TreeSet
		 */
		SortedSet<Item> sorter = new TreeSet<>();
		sorter.add(new Item("wjx",12345));
		sorter.add(new Item("wjs",234));
		sorter.add(new Item("wjt",568));
		sorter.add(new Item("wjc",1257));
		System.out.println(sorter);
		SortedSet<Item> sorter1 = new TreeSet<>();
		/**
		 * 
		 */
		sorter1.addAll(sorter);//深克隆一个sorter对象
		System.out.println(sorter1);
		
		//////////////////////////////////////////////////////////////////////////////////
		/**
		 * 优先级队列priority queue
		 */
		PriorityQueue<LocalDate> pq= new PriorityQueue<>();
		pq.add(LocalDate.of(1906, 12, 9));
		pq.add(LocalDate.of(1998, 6, 10));
		pq.add(LocalDate.of(1998, 9, 17));
		pq.add(LocalDate.of(2000, 7, 15));
		pq.remove();
		System.out.println(pq.toString());
		
		//////////////////////////////////////////////////////////////////////////////////////
		Employee[] staff = new Employee[3];
		staff[0] = new Employee("wjx11",50000,1997,5,9);
		staff[1] = new Employee("wjs",50000,1997,5,9);
		staff[2] = new Employee("wjt222",50000,1996,4,8);
		/**
		 * map数据结果，键/值映射
		 */
		Map<String, Employee> staff2 = new HashMap<>();
		staff2.put("1998-06-10", staff[0]);
		staff2.put("1992-06-10", staff[1]);
		staff2.put("1993-06-10", staff[2]);
		staff2.remove("1998-06-10");
		staff2.size();
		staff2.forEach((key, value) -> {
			System.out.println("key=" + key + ", value=" + value);
		});
		/**
		 * 通过键值获取元素（如果不存在则返回默认值）
		 */
		System.out.println(staff2);
		System.out.println(staff2.getOrDefault("1998-06-11", staff[0]));
		
		/////////////////////////////////////////////////////////////////////////
		/**
		 * map应用-单词计数
		 */
		Map<String, Integer> counts = new HashMap<>();
		String word = "";
		//方法一
		counts.put(word, counts.getOrDefault(word, 0) + 1);
		//方法二
		counts.putIfAbsent(word, 0);
		counts.put(word, counts.get(word));
		//方法三
		counts.merge(word, 1, Integer::sum);
		//counts.merge(word, 1, (a,b) -> a+b);
		
		/////////////////////////////////////////////////////////////////////////////
		/**
		 * 视图view
		 * 有Set<K> keySet()键值
		 * Collection<V> values()值
		 * Set<Map.Entry<K, V>> entrySet() 键/值对
		 */
		Set<String> keys = staff2.keySet();
		Collection<Employee> values = staff2.values();
		System.out.println(values);
		System.out.println(keys);
		keys.remove("1993-06-10");
		/**
		 * 同时访问键/值对
		 * Set<Map.Entry<K, V>> entrySet() 键/值对
		 */
		for (Map.Entry<String, Employee> entry : staff2.entrySet()) {
			String key = entry.getKey();
			Employee value = entry.getValue();
			System.out.println(key + value);
		}
		//此法更高效
		staff2.forEach((key, value) -> System.out.println("key=" + key + ", value=" + value));
		
		///////////////////////////////////////////////////////////////////////
		
		
		
		
	}
	
}
