package algorithms;

public class RBTNode<K, V> {
	
	public RBTNode<K, V> right;
	public RBTNode<K, V> left;
	public RBTNode<K, V> parent;
	public String color;
	public K key ;
	public V value;
	
	public RBTNode(RBTNode<K, V> parent, K key, V value) {
		this.parent = parent;
		this.key = key;
		this.value = value;
	}
	
	public RBTNode(K key, V value, String color) {
		this.key = key;
		this.value = value;
		this.color = color;
	}
	
	public RBTNode() {
	}

}
