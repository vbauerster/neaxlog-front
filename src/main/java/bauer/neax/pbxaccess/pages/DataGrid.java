package bauer.neax.pbxaccess.pages;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.DiscardAfter;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bauer.neax.dao.CallsDao;
import bauer.neax.domain.Calls;
import bauer.neax.domain.ReportParam;
import bauer.neax.pbxaccess.commons.EvenOdd;
import bauer.neax.pbxaccess.services.DFStreamResponse;
import bauer.neax.pbxaccess.services.JReport;

public class DataGrid {
	
	private static final Logger log = LoggerFactory.getLogger(DataGrid.class);
	
	@Inject
	private CallsDao callsDao;
	
    @Inject @Path("context:jasper/pin.jasper")
    @Property
    private Asset jpin;
    
    @Inject @Path("context:jasper/ext.jasper")
    @Property
    private Asset jext; 

    @Inject @Path("context:jasper/xls.jasper")
    @Property
    private Asset jxls;     
    
    @Inject
    private AlertManager alertManager;
    
    @Persist(PersistenceConstants.FLASH)
    private String alert;
		
	@Property
	@Persist
	private List<Calls> calls;
	
//	@SuppressWarnings("rawtypes")
//	@Persist
//	private HashMap param;
	
	@SuppressWarnings("unused")
    @Property
    private Calls callRow;
	

    @Property
    private EvenOdd evenOdd;
	
//	@Property(write = false)
//	private String back;
	
	@Persist
	private ReportParam reportParam;
	
//	void pageLoaded(){
//		evenOdd = new EvenOdd();
//	}   
	
	public void setupRender(){
		
		if(reportParam!=null){
				calls = callsDao.getReport(reportParam);
		}
		
		if(alert!=null)
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, alert);		
	}
	
	
//	String onPassivate() {
//		return back;
//	}
	void onActivate() {
		evenOdd = new EvenOdd();
	}	
	
	
//	void set(List<Calls> callsList){
//		 this.calls = callsList;
////		 this.back = backPage;
//	}
	
//	@SuppressWarnings("rawtypes")
//	void setParam(HashMap param){
//		this.param = param;
//	}
	
	
	public String getDuration(){
		  SimpleDateFormat nTime = new SimpleDateFormat("HH:mm:ss");
		  nTime.setTimeZone(TimeZone.getTimeZone("UTC"));
		  return nTime.format(new Date(callRow.getDuration() * 1000));
	}
	
	
	public String getCallTimeFormat() {
			return "dd/MM/yyyy HH:mm";
	}	  
	 
	void setAlert(String alert) {
		this.alert = alert;
	}
	
	
  @Log
  public StreamResponse onExport(String mime){
  	
  	if(calls == null) return null;
  	 	
	try {
		String filename;		
		Resource jasper;
		
		if(!reportParam.isByExt()){
			filename = "Pin";
			jasper = jpin.getResource();
		}else{
			filename = "Ext";
			jasper = jext.getResource();
		}

		if(mime.endsWith("ms-excel")){
		  jasper = jxls.getResource();
		}
		
		Date from = reportParam.getFrom();
		Date to = reportParam.getTill();
		SimpleDateFormat dmy = new SimpleDateFormat("dd.MM.yyyy");
		filename = filename + "_" + dmy.format(from) + "-" + dmy.format(to);
		
		HashMap param = new HashMap(2);
		param.put("START", from);
		param.put("END", to);		
		
		JReport report = new JReport(jasper.openStream(), calls, param);
		
	  	InputStream is;
	  	
	  	if(mime.endsWith("pdf"))
	  	   is = report.generatePDF();
	  	else
	  	   is = report.generateXLS(new String[]{dmy.format(from) + "-" + dmy.format(to)});
	  	
	  	DFStreamResponse sResponse = new DFStreamResponse(is);
	  	sResponse.setContentType(mime);
	  	sResponse.setFilename(filename);
	  	
	  	return sResponse;
	} catch (IOException e) {	
//		readerForm.recordError("Japser file not found: " + jBReader);
		e.printStackTrace();
		return null;
	}		
  }
  
  
  @DiscardAfter
   String onBack(){
	return reportParam.isByExt() ? "extension" : "index" ;  
   }


public ReportParam getReportParam() {
	return reportParam;
}

public void setReportParam(ReportParam reportParam) {
	this.reportParam = reportParam;
}
	
}
