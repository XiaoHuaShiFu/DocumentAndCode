package human;

import java.io.Serializable;

/**
 * 使用了Serializable序列化接口
 * transient关键字（不进行默认序列化）
 * 自己进行writeObject和readObject序列化和反序列化方法的定义
 * @author XHSF
 *
 */


public class Student extends Person {
	
	/**
	 * 序列化id
	 */
	private static final long serialVersionUID = 8872031215728795692L;
	private String major;
	//关键字transient标志该元素不会被进行默认的序列化,需要手动进行序列化
	private transient int score;
	
	public Student() {
	}
	
	public Student(String name, String major, int score) {
		super(name);
		this.major = major;
		this.score = score;
	}
	
//	public Student(String name, String major) {
//		this.name = name;
//		this.major = major;
//	}
	
	public String getMajor() {
		return this.major;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public String getDescription() {
		return "a student majoring in " + major;
	}
	
	public String toString() {
		return getClass().getName()
			+ "[name=" + this.getName()
			+ ",major=" + this.major
			+ ",score=" + this.score
			+ "]";
	}
	
	/**
	 * 手动序列化
	 * @param s
	 * @throws java.io.IOException
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException{
		s.defaultWriteObject();//能够默认进行序列化的
		s.writeInt(this.score);//自己完成序列化
	}
	
	/**
	 * 手动反序列化
	 * @param s
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException{
		s.defaultReadObject();//能够默认进行反序列化的
		this.score = s.readInt();//自己完成反序列化
	}
	
}
