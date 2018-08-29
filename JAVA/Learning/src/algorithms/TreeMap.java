package algorithms;


public class TreeMap<K, V> extends RedBlackTree<K, V> {

	/**
	 * 通过key，value对添加结点
	 * 如果该key值存在则覆盖原value值
	 */
	public void add(K key, V value) {
		if (super.getRoot() == null) {
			RBTNode<K, V> root = new RBTNode<>(key, value, "Black");
			super.setRoot(root);
		} else {
			super.put(key, value);
		}
	}
	
	/**
	 * 通过key删除结点
	 * 如果key不存在返回false
	 * 删除成功返回true
	 * @param key
	 */
	public Boolean delete(K key) {
		RBTNode<K, V> p = super.getRBTNode(key);
		if (p == null) 
			return false;
		super.deleteRBTNode(p);
		return true;
	}
	
	/**
	 * 通过key查询value
	 * 如果key值不存在则返回null
	 * @param key
	 * @return
	 */
	public V query(K key) {
		RBTNode<K, V> p = super.getRBTNode(key);
		if (p == null) 
			return null;
		return p.value;
	}
	
	/**
	 * 设置对应key的value值
	 * 如果key不存在，则返回false
	 * @param map
	 * @return
	 */
	public Boolean upadate(K key, V value) {
		RBTNode<K, V> p = super.getRBTNode(key);
		if (p == null) 
			return false;
		super.put(key, value);
		return true;
	}
	
}
