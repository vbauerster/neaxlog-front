package bauer.neax.pbxaccess.pages.adm.ext;

import bauer.neax.dao.CallsDao;
import bauer.neax.domain.ExtAcl;
import bauer.neax.domain.Person;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tynamo.security.services.SecurityService;

public class ExtAccess {
	
	private static final Logger log = LoggerFactory.getLogger(ExtAccess.class);
	
	@Inject
	CallsDao callsDao;
	
	@Inject
	private SecurityService securityService;
	
    @Inject
    private AlertManager alertManager;	
	
	@Inject
	private Request request;
	
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	
    @InjectComponent
    private Zone usersZone;
    
	@Property
	@SuppressWarnings("unused")
	private List<ExtAcl> extAclist;
	
	@Property
	@SuppressWarnings("unused")
	private ExtAcl extAcl;

    @Persist
    @Property
    private List<String> extSet;
    
    @Property
    @Persist(PersistenceConstants.FLASH)
    private String extension; 
    
    @Property
    @Persist(PersistenceConstants.FLASH)
    private String alias;
    
       
    public void setupRender(){
    	
    	if(extSet==null){		
    		extSet = callsDao.getDistinctExts();
    	}else if(extension!=null){
    		extAclist = callsDao.getExtAcl(extension);
    	}
    	
    }
    
    public void onSuccess(){
    	
    	Map<String, Object> inputs = new HashMap<String, Object>(3);
    	
    	inputs.put("ext", Integer.parseInt(extension));
    	inputs.put("alias", alias);
    	inputs.put("addedby", getLoggedUser().getAlias());
    	
    	log.debug("****  Inserting ExtAcl: " + inputs);
    
    	if(!callsDao.insertExtAcl(inputs)){
    		alertManager.alert(Duration.TRANSIENT, Severity.ERROR, alias + " not inserted!");
    	}
    	
    }
    
    
    @Log
    public void onValueChanged(String ext){
    	   	
    	extAclist = callsDao.getExtAcl(ext);
    	
    	if(request.isXHR()){
    		ajaxResponseRenderer.addRender(usersZone);
    	}
    }
    
    
    @Log
    public void onDelete(String alias){
    	
    	log.debug("****  Deleting {} from ext {}", alias, extension);
    	
    	try{
    	 int i = callsDao.delExtAcl(Integer.parseInt(extension), alias);
    	 log.debug("{} rows deleted by delExtAcl", i);
    	}catch (Exception e){
    		alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Error deleting " + alias + " from ext: " + extension);
    	}
    	
    }
    
	private Person getLoggedUser(){
		Subject subj = securityService.getSubject();
		return (Person) subj.getPrincipal();	
	}

}
