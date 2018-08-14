package model;

import java.util.Date;

public class TransInfo {

	private Integer id;
	private Integer sourceId;
	private String sourceAccount;
	private Integer destinationId;
	private String destinationAccount;
	private double amount;
	private Date createDatetime;
	
	public TransInfo(Integer id, Integer sourceId, String sourceAccount, Integer destinationId,
			String destinationAccount, double amount) {
		super();
		this.id = id;
		this.sourceId = sourceId;
		this.sourceAccount = sourceAccount;
		this.destinationId = destinationId;
		this.destinationAccount = destinationAccount;
		this.amount = amount;
		this.createDatetime = new Date();
	}
	
	public TransInfo(Integer sourceId, String sourceAccount, Integer destinationId,
			String destinationAccount, double amount) {
		super();
		this.sourceId = sourceId;
		this.sourceAccount = sourceAccount;
		this.destinationId = destinationId;
		this.destinationAccount = destinationAccount;
		this.amount = amount;
	}
	
	public TransInfo() {
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceAccount() {
		return sourceAccount;
	}

	public void setSourceAccount(String sourceAccount) {
		this.sourceAccount = sourceAccount;
	}

	public Integer getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(Integer destinationId) {
		this.destinationId = destinationId;
	}

	public String getDestinationAccount() {
		return destinationAccount;
	}

	public void setDestinationAccount(String destinationAccount) {
		this.destinationAccount = destinationAccount;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

	@Override
	public String toString() {
		return "TransInfo [id=" + id + ", sourceId=" + sourceId + ", sourceAccount=" + sourceAccount
				+ ", destinationId=" + destinationId + ", destinationAccount=" + destinationAccount + ", amount="
				+ amount + ", createDatetime=" + createDatetime + "]";
	}

}
