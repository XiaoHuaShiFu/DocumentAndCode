package human;

import java.io.Serializable;

/**
 * 
 * @author XHSF
 *
 */


public class Student extends Person implements Serializable{
	
	/**
	 *序列化id
	 */
	private static final long serialVersionUID = 8872031215728795692L;
	
	private String major;
//	private String name;
	
	public Student() {
	}
	
	public Student(String name, String major) {
		super(name);
		this.major = major;
	}
	
//	public Student(String name, String major) {
//		this.name = name;
//		this.major = major;
//	}
	
	public String getMajor() {
		return this.major;
	}
	
	public String getDescription() {
		return "a student majoring in " + major;
	}
	
	public String toString() {
		return getClass().getName()
			+ "[name=" + this.getName()
			+ ",major=" + this.major
			+ "]";
	}
	
}
