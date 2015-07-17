package bauer.neax.domain;

import java.util.Date;

public class ExtAcl {

	private String alias;
	private String addedby;
	private Date evenTime;
	
	public ExtAcl(String alias, String addedby, Date evenTime){
		this.alias=alias;
		this.addedby=addedby;
		this.evenTime=evenTime;
	}
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getAddedby() {
		return addedby;
	}
	public void setAddedby(String addedby) {
		this.addedby = addedby;
	}

	public Date getEvenTime() {
		return evenTime;
	}

	public void setEvenTime(Date evenTime) {
		this.evenTime = evenTime;
	}	
	
}
