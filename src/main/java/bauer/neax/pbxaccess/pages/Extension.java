package bauer.neax.pbxaccess.pages;

import java.util.List;

import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.internal.services.StringValueEncoder;
import org.tynamo.security.services.SecurityService;

import bauer.neax.dao.CallsDao;
import bauer.neax.domain.Person;
import bauer.neax.domain.ReportParam;

public class Extension {


	@Inject
	CallsDao callsDao;

	@Inject
	private SecurityService securityService;

//	@Inject
//	private Request request;

//	@Inject
//	private AjaxResponseRenderer ajaxResponseRenderer;

//    @InjectComponent
//    private Zone selectedExt;

    @InjectPage
    private DataGrid dataGridPage;

    @Persist
    @Property
    private List<String> extSet;

//    @Property
//    @Persist(PersistenceConstants.FLASH)
//    private List<String> selectedExts;


    @Property
    private final ValueEncoder<String> encoder = new StringValueEncoder();

//    @Property
//    private String ext;

//    @Property
//    @Persist(PersistenceConstants.FLASH)
//    private String selectedExt;

//    @Property
//    @Persist(PersistenceConstants.FLASH)
//    @Validate("required")
//    private Date from;
//
//    @Property
//    @Persist(PersistenceConstants.FLASH)
//    @Validate("required")
//    private Date to;

//    @Property
//    @Persist(PersistenceConstants.FLASH)
//    private int line;

	@Property
	@Persist
	private ReportParam rParam;

	@Log
    public void setupRender(){
    		rParam = new ReportParam();
    		Subject subj = securityService.getSubject();
    		Person user = (Person) subj.getPrincipal();
    		extSet = callsDao.getExtPermissions(user.getAlias());
    }


//    @Log
//    void onValidateFromCallsByExt(){
//
//    }


//    @Log
    public Object onSuccess(){

    	if(extSet.isEmpty()) return null;

//    	Subject subj = securityService.getSubject();

//    	if(subj.isPermitted("ext:view:" + selectedExts)){
//    		dataGridPage.set(callsDao.getCallsByExt(selectedExts, from, to));
//    	}else{
//    		dataGridPage.set(null);
//    		dataGridPage.setAlert("Report for ext " + selectedExts + " isn't allowed for you!");
//    	}

//		HashMap param = new HashMap(2);
//		param.put("START", from);
//		param.put("END", to);

    	rParam.setByExt(true);
    	dataGridPage.setReportParam(rParam);
//    	dataGridPage.set(callsDao.getCallsByExt(selectedExts, line, from, to));
//    	dataGridPage.setParam(param);
//    	dataGridPage.onActivate("extension");
    	return dataGridPage;
    }


//    public void onValueChanged(){
//
//    	if(request.isXHR()){
//    		ajaxResponseRenderer.addRender(selectedExt);
//    	}
//
//    }

//    public void onExtSelected(String ext){
//
//    	extension = ext;
//
//    	if(request.isXHR()){
//    		ajaxResponseRenderer.addRender(selectedExt);
//    	}
//
//    }

 // We could return a DateFormat, but instead we'll return a String which DateField will coerce into a DateFormat.
    public String getDateFieldFormat() {
  			return "dd/MM/yyyy";
    }

}
