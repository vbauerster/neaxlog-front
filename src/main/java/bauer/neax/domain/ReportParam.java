package bauer.neax.domain;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


public class ReportParam {
	private int line;
    private Date from;
    private Date till;
    private SortedSet<Person> reporters;
    private List<String> selectedExts;
    private boolean byExt;
    
    public ReportParam(){
    	reporters = new TreeSet<Person>();
    }
    
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	
	public Date getTill() {
		return till;
	}
	public void setTill(Date till) {
		this.till = till;
	}
	
	public SortedSet<Person> getReporters() {
		return reporters;
	}
	public void setReporters(SortedSet<Person> reporters) {
		this.reporters = reporters;
	}

	public List<String> getSelectedExts() {
		return selectedExts;
	}

	public void setSelectedExts(List<String> selectedExts) {
		this.selectedExts = selectedExts;
	}

	public boolean isByExt() {
		return byExt;
	}

	public void setByExt(boolean byExt) {
		this.byExt = byExt;
	}
	
}
