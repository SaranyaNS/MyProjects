package com.java;

public class RecordsBean {
	
	private int transReference;
	private double startBalance;
	private double endBalance;
	private double mutation;
	private String accno;
	private String description;

	public int getTransReference() {
		return transReference;
	}
	public void setTransReference(int transReference) {
		this.transReference = transReference;
	}
	public double getStartBalance() {
		return startBalance;
	}
	public void setStartBalance(double startBalance) {
		this.startBalance = startBalance;
	}
	public double getEndBalance() {
		return endBalance;
	}
	public void setEndBalance(double endBalance) {
		this.endBalance = endBalance;
	}
	public double getMutation() {
		return mutation;
	}
	public void setMutation(double mutation) {
		this.mutation = mutation;
	}
	public String getAccno() {
		return accno;
	}
	public void setAccno(String accno) {
		this.accno = accno;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}	
}
