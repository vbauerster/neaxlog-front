package bauer.neax.pbxaccess.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

import bauer.neax.domain.Calls;

public class JReport {

//	private List<evntEmp> report;
//	private String jasperfile;
	private InputStream jasperIs;
	private HashMap param;
	private JRBeanCollectionDataSource dataSource;
	
//	public JReport (String jasperfile, List<evntEmp> report, HashMap param){
////		this.report = report;
//		this.jasperfile = jasperfile;
//		this.param = param;
//		this.dataSource = new JRBeanCollectionDataSource(report);		
//		
//	}
	

	public JReport (InputStream is, List<Calls> report, HashMap param){
		
		this.jasperIs = is;
		this.param = param;
		this.dataSource = new JRBeanCollectionDataSource(report);		
		
	}	
	
	
	public InputStream generatePDF() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
//	    InputStream reportStream = new FileInputStream("reports/Canteen.jasper");
		
      	try {
//    		JasperRunManager.runReportToPdfFile("reports/Canteen.jasper", new HashMap(), dataSource);		
//    		JasperRunManager.runReportToPdfStream(new FileInputStream(jasperfile), baos, param, dataSource);
      		JasperRunManager.runReportToPdfStream(jasperIs, baos, param, dataSource);
    	} catch (JRException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} 
      	
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        return bais;
	}

	public InputStream generateXLS(String[] sheetnames) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
      	try {
    		
//    		JasperPrint jasperPrint = JasperFillManager.fillReport(new FileInputStream(jasperfile), param, dataSource);
      		
      		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperIs, param, dataSource);
    			
    		JExcelApiExporter xlsExporter = new JExcelApiExporter();
    		
    		xlsExporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
    		xlsExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
    		xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
    		xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
    		xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
    		xlsExporter.setParameter(JRXlsExporterParameter.SHEET_NAMES, sheetnames);
    		
    		xlsExporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, baos);
    		
//    		xlsExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "c:\\reports\\CardholderReader.xls");
//    				System.out.println("Exporting report...");
    		xlsExporter.exportReport();
//    				System.out.println("Done!");
    		
    	} catch (JRException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} 
      	
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        return bais;
	}	
}
