package bauer.neax.pbxaccess.pages.adm.pinowner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
//import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tynamo.security.services.SecurityService;

import bauer.neax.dao.CallsDao;
import bauer.neax.domain.Person;
import bauer.neax.domain.PinOwner;

@Import(library={"context:/static/azone.js"})
public class PinOwnerSearch {
	
	private static final Logger log = LoggerFactory.getLogger(PinOwnerSearch.class);
			
	@Inject
	private CallsDao callsDao;
	
	@Inject
	private Request request;

//	@Inject
//	private ComponentResources componentResources;
	
	@Inject
	private SecurityService securityService;
	
    @Inject
    private AlertManager alertManager;	
	
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	
    @InjectComponent
    private Zone result;
	
	@Property
	@Persist
	private String query;

//	@Persist
//	private String lastQuery;
	
	@Property
	@Persist	
	private List<PinOwner> pinOwners;
	
	@SuppressWarnings("unused")
    @Property	
	private PinOwner pinOwner;

	
	
//	@Log
//	String onPassivate(){
//		lastQuery = query;
//		return query;
//	}
	
	
//	@Log
//	Object onActivate(){
//		
//		if(query!=null){
//			pinOwners = callsDao.seekPinOwners(query);
//    	    return result.getBody();
//		}else{
//			return null;
//		}
//	}
	
    List<String> onProvideCompletionsFromQuery(String partial){
        
    	List<String> matches = new ArrayList<String>();
    	partial = partial.toLowerCase();
    	
    	for(String alias : callsDao.getAliases(true)){
    		
    		if(alias.toLowerCase().startsWith(partial)){
    			matches.add(alias);
    		}
    	}
    	return matches;
    }		
	
//	@Log
	public void onSuccessFromSearchForm(){
		
		
		query = query.replaceAll("\\*", "%");
		
		log.debug("Query: " + query);
		
		pinOwners = callsDao.seekPinOwners(query);
		
    	if(request.isXHR()){
    		ajaxResponseRenderer.addRender(result);
    	}		
	}
	
//	@Log
	public Object onDelete(String pin){
		
		Subject currentUser = securityService.getSubject();
		Person user = (Person) currentUser.getPrincipal();
		
		Map<String, String> argMap = new HashMap<String, String>(3);
		
		argMap.put("authCode", pin);
		argMap.put("updatedBy", user.getAlias());
		argMap.put("alias", "");
		
		Map<String,Object> out = callsDao.deactivatePin(argMap); 
		if (out!=null) {
//			log.info("Successfully deactivated pin: " + pin + "=" + out.get("alias"));
			alertManager.alert(Duration.TRANSIENT, Severity.INFO, 
					"Successfully deactivated pin: " + pin + "=" + out.get("alias"));
			
			pinOwners = callsDao.seekPinOwners(query);
			return result;
			
		} else {
//			log.error("Error deactivating the pin: " + pin);
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Error deactivating the pin: " + pin);
			return null;
		}		
	}
	
	
	public Object onUpdateResult(){
		
		if(query!=null)
		pinOwners = callsDao.seekPinOwners(query);
//		return result;
		return request.isXHR() ? result.getBody() : null;
	}
	

}
