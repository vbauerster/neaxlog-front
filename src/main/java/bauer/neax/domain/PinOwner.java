package bauer.neax.domain;

import java.util.Date;

public class PinOwner {

	private String alias;
	private String firstName;
	private String lastName;
	private String mail;
//	private String employeeNumber;
	private String authCode;
	private boolean notify;
	private Date evenTime;
	private String updatedBy;
	
//	private String whAlias;
	private String whAuthCode;
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}	
//	public String getEmployeeNumber() {
//		return employeeNumber;
//	}
//	public void setEmployeeNumber(String employeeNumber) {
//		this.employeeNumber = employeeNumber;
//	}
	public String getAuthCode() {
		return authCode;
	}
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	public boolean isNotify() {
		return notify;
	}
	public void setNotify(boolean notify) {
		this.notify = notify;
	}
	public Date getEvenTime() {
		return evenTime;
	}
	public void setEvenTime(Date evenTime) {
		this.evenTime = evenTime;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
//	public String getWhAlias() {
//		return whAlias;
//	}
//	public void setWhAlias(String whAlias) {
//		this.whAlias = whAlias;
//	}
	public String getWhAuthCode() {
		return whAuthCode;
	}
	public void setWhAuthCode(String whAuthCode) {
		this.whAuthCode = whAuthCode;
	}
	
	public String toString(){
		return alias;
	}
	
}
