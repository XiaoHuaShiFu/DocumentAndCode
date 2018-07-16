package dataStructures;

import java.util.Objects;

public class Item implements Comparable<Item>{
	
	private String description;
	private int partNumber;
	
	public Item(String description, int partNumber) {
		this.description = description;
		this.partNumber = partNumber;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public int getPartNumber() {
		return this.partNumber;
	}
	
	public String toString() {
		return "[description=" + this.description + ", partNumber=" + this.partNumber + "]";
	}
	
	public boolean equals(Object otherObject) {
		if (this == otherObject) return true;
		if (otherObject == null) return false;
		if (this.getClass() != otherObject.getClass()) return false;
		Item other = (Item) otherObject;
		return Objects.equals(this.description, other.description) && this.partNumber == other.partNumber;
	}
	
	public int hashCode() {
		return Objects.hash(this.description, this.partNumber);
	}
	
	public int compareTo(Item other) {
		int diff = Integer.compare(this.partNumber, other.partNumber);
		return diff != 0 ? diff : this.description.compareTo(other.description);
	}
	
}
