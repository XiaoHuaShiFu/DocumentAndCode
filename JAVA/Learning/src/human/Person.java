package human;

import java.io.Externalizable;
import java.io.Serializable;

/**
 * 抽象类实现了序列化接口Serializable，子类就不需要再去实现序列化接口了 
 * @author lenovo
 *
 */


public abstract class Person implements Serializable{
	
	/**
	 *序列化id
	 */
	private static final long serialVersionUID = 4749100007361634614L;
	private String name;
	public abstract String getDescription();
	
	public Person() {
	}
	
	public Person(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return getClass().getName()
			+ "[name=" + this.name
			+ "]";
	}
	
}
