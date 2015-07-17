package bauer.neax.domain;

import java.util.Date;

public class Calls {
	
	
	private Date callTime;
	private String line;
	private int ext;
	private String calledNumber;
	private long duration; //duration of call in seconds
	private String authCode;
	private String commonName;
	
	
	
	public Date getCallTime() {
		return callTime;
	}
	public void setCallTime(Date callTime) {
		this.callTime = callTime;
	}	
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}
	public int getExt() {
		return ext;
	}
	public void setExt(int ext) {
		this.ext = ext;
	}
	public String getCalledNumber() {
		return calledNumber;
	}
	public void setCalledNumber(String calledNumber) {
		this.calledNumber = calledNumber;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getAuthCode() {
		return authCode;
	}
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	public String getCommonName() {
		return commonName;
	}
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

}
