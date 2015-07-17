package bauer.neax.pbxaccess.pages;

import java.util.List;

import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.chenillekit.tapestry.core.components.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tynamo.security.services.SecurityService;

import bauer.neax.dao.CallsDao;
import bauer.neax.domain.Person;
import bauer.neax.pbxaccess.commons.EvenOdd;

public class Options {
	
	private static final Logger log = LoggerFactory.getLogger(Options.class);
	
	@Inject
	private CallsDao callsDao;
	
	@Inject
	private SecurityService securityService;
	
	@Inject
	private Request request;
	
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	
    @InjectComponent
    private Zone pinZone;
	
    @Property
    @Persist(PersistenceConstants.FLASH)
    private List<String> pins;
    
    @Property
    private String pin;
	
	@Property
	@Persist(PersistenceConstants.FLASH)
	private boolean notify;
	

	private Person getLoggedUser(){
		Subject subj = securityService.getSubject();
		return (Person) subj.getPrincipal();	
	}
	
	
	void setupRender(){

		Person currentUser = getLoggedUser();		
		
		if(currentUser.isHasPin()){
			notify = callsDao.isNotifyEnabled(currentUser.getAlias());
		}
		
	}
	
	void onSuccess(){

		Person currentUser = getLoggedUser();
		int i = callsDao.setNotify(notify, currentUser.getAlias());		
		log.debug("updated rows: " + i);
	}

	@Log
	public void onShowPin(){
		Person currentUser = getLoggedUser();
		pins = callsDao.getActivePins(currentUser.getAlias());
		
		if(request.isXHR()){
			ajaxResponseRenderer.addRender(pinZone);
		}
	}
	
	public boolean isNotHasPin(){
		
		return !getLoggedUser().isHasPin();
	}
}
