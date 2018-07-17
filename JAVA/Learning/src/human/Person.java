package human;

/**
 * 
 * @author XHSF
 *
 */

public abstract class Person {
	
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
	
	public String toString() {
		return getClass().getName()
			+ "[name=" + this.name
			+ "]";
	}
	
}
