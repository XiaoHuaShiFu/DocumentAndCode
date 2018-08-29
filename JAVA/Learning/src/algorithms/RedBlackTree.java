package algorithms;


public class RedBlackTree<K, V> {

	private int size = 0;
	private RBTNode<K, V> root;
	private static final String RED = "Red";
	private static final String BLACK = "Black";

	/**
	 * 左旋
	 * @param p
	 */
	private void rotateLeft(RBTNode<K, V> p) {
		if (p != null) {
			RBTNode<K, V> r = p.right;
			p.right = r.left;
			if (r.left != null)
				r.left.parent = p;
			r.parent = p.parent;
			if (p.parent == null)
				root = r;
			else if (p.parent.left == p)
				p.parent.left = r;
			else
				p.parent.right = r;
			r.left = p;
			p.parent = r;
		}
	}

	/**
	 * 右旋
	 * 
	 * @param p
	 */
	private void rotateRight(RBTNode<K, V> p) {
		if (p != null) {
			RBTNode<K, V> l = p.left;
			p.left = l.right;
			if (l.right != null)
				l.right.parent = p;
			l.parent = p.parent;
			if (p.parent == null)
				root = l;
			else if (p.parent.left == p)
				p.parent.left = l;
			else
				p.parent.right = l;
			l.right = p;
			p.parent = l;
		}
	}
	
	/**
	 * 指定key返回对应value
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final RBTNode<K, V> getRBTNode(Object key) {
		if (key == null)
			throw new NullPointerException();
		Comparable<? super K> k = (Comparable<? super K>) key;//使用元素的自然顺序
		RBTNode<K, V> p = root;
		while (p != null) {
			int cmp = k.compareTo(p.key);
			if (cmp < 0)
				p = p.left;
			else if (cmp > 0)
				p = p.right;
			else
				return p; 
		}
		return null;
	}

	/**
	 * 指定key的value添加到tree里
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public V put(K key, V value) {
		int cmp;
		RBTNode<K, V> parent;
		RBTNode<K, V> t = root;
		if (key == null)
			throw new NullPointerException();
		Comparable<? super K> k = (Comparable<? super K>) key;
		do {
			parent = t;
			cmp = k.compareTo(t.key);
			if (cmp < 0)
				t = t.left;
			else if (cmp > 0)
				t = t.right;
			else
				return t.value = value;
		} while (t != null);
		RBTNode<K, V> e = new RBTNode<K, V>(parent, key, value);
		if (cmp < 0)
			parent.left = e;
		else
			parent.right = e;
		fixAfterInsertion(e);
		size++;
		return null;
	}

	/**
	 * 调整插入结点后的tree
	 * 
	 * @param x
	 */
	private void fixAfterInsertion(RBTNode<K, V> x) {
		x.color = RED;
		while (x != null && x != root && x.parent.color == RED) {
			if (x.parent == x.parent.parent.left) {
				RBTNode<K, V> y = x.parent.parent.right;
				if (y.color == RED) {
					x.parent.color = BLACK;
					y.color = BLACK;
					x.parent.parent.color = RED;
					x = x.parent.parent;
				} else {
					if (x.parent.right == x) {
						x = x.parent;
						rotateLeft(x);
					}
					x.parent.color = BLACK;
					x.parent.parent.color = RED;
					rotateRight(x.parent.parent);
				}
			} else {
				RBTNode<K, V> y = x.parent.parent.left;
				if (y.color == RED) {
					x.parent.color = BLACK;
					y.color = BLACK;
					x.parent.parent.color = RED;
					x = x.parent.parent;
				} else {
					if (x.parent.left == x) {
						x = x.parent;
						rotateRight(x);
					}
					x.parent.color = BLACK;
					x.parent.parent.color = RED;
					rotateLeft(x.parent.parent);
				}
			}
		}
		root.color = BLACK;
	}

	/**
	 * 查找后继结点
	 * 
	 * @param t
	 * @return
	 */
	private RBTNode<K, V> successor(RBTNode<K, V> t) {
		if (t == null)
			return null;
		else if (t.right != null) {
			RBTNode<K, V> p = t;
			while (p.left != null)
				p = p.left;
			return p;
		} else {
			RBTNode<K, V> p = t.parent;
			RBTNode<K, V> ch = t;
			while (p != null && ch == p.right) {
				ch = p;
				p = p.parent;
			}
			return p;
		}
	}

	/**
	 * 删除结点
	 * 
	 * @param p
	 */
	public void deleteRBTNode(RBTNode<K, V> p) {
		size--;
		// 1.删除点p的左右子树都非空。
		if (p.left != null && p.right != null) {
			RBTNode<K, V> s = successor(p);
			p.key = s.key;
			p.value = s.value;
			p = s;
		}
		RBTNode<K, V> replacement = (p.left != null ? p.left : p.right);
		// 2. 删除点p只有一棵子树非空。
		if (replacement != null) {
			replacement.parent = p.parent;
			if (p.parent == null)
				root = replacement;
			else if (p.parent.left == p)
				p.parent.left = replacement;
			else
				p.parent.right = replacement;
			p.left = p.right = p.parent = null;
			if (p.color == BLACK)
				fixAfterDeletion(replacement);
		} else if (p.parent == null) {
			root = null;
		} else {
			// 3. 删除点p的左右子树都为空
			if (p.color == BLACK)
				fixAfterDeletion(p);
			if (p.parent != null) {
				if (p == p.parent.left)
					p.parent.left = null;
				else if (p == p.parent.right)
					p.parent.right = null;
				p.parent = null;
			}
		}
	}

	/**
	 * 调整删除结点后的tree
	 * 
	 * @param x
	 */
	private void fixAfterDeletion(RBTNode<K, V> x) {
		while (x != root && x.color == BLACK) {
			if (x.parent.left == x) {
				RBTNode<K, V> sib = x.parent.right;
				if (sib.color == RED) {
					sib.color = BLACK;
					x.parent.color = RED;
					rotateLeft(x.parent);
					sib = x.parent.right;
				}
				if (sib.left.color == BLACK && sib.right.color == BLACK) {
					sib.color = RED;
					x = x.parent;
				} else {
					if (sib.right.color == BLACK) {
						sib.color = RED;
						sib.left.color = BLACK;
						rotateRight(sib);
						sib = x.parent.right;
					}
					sib.color = x.parent.color;
					x.parent.color = BLACK;
					sib.right.color = BLACK;
					rotateLeft(x.parent);
					x = root;
				}
			} else {
				RBTNode<K, V> sib = x.parent.left;
				if (sib.color == RED) {
					sib.color = BLACK;
					x.parent.color = RED;
					rotateRight(x.parent);
					sib = x.parent.left;
				}
				if (sib.left.color == BLACK && sib.right.color == BLACK) {
					sib.color = RED;
					x = x.parent;
				} else {
					if (sib.left.color == BLACK) {
						sib.color = RED;
						sib.right.color = BLACK;
						rotateLeft(sib);
						sib = x.parent.left;
					}
					sib.color = x.parent.color;
					x.parent.color = BLACK;
					sib.left.color = BLACK;
					rotateRight(x.parent);
					x = root;
				}
			}
		}
		x.color = BLACK;
	}

	/**
	 * 得到tree的节点数
	 * 
	 * @return
	 */
	public int getSize() {
		return size;
	}

	/**
	 * 得到tree的root
	 */
	public RBTNode<K, V> getRoot() {
		return root;
	}

	/**
	 * 设置tree的root
	 * 
	 * @param root
	 */
	public void setRoot(RBTNode<K, V> root) {
		this.root = root;
	}

}
