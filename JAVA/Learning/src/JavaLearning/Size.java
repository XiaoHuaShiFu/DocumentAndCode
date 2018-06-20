package JavaLearning;

/**
 * 
 * @author XHSF
 *
 */

public enum Size {

	SMALL("S"), MEDIUM("M"), LARGE("L"), EXTRA_LARGE("XL");

	private String abbreviation;

	private Size(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getAbbreviation() {
		return this.abbreviation;
	}

}
