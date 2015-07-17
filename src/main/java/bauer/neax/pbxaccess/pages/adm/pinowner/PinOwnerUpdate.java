package bauer.neax.pbxaccess.pages.adm.pinowner;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.DiscardAfter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.tynamo.security.services.SecurityService;

import bauer.neax.dao.CallsDao;
import bauer.neax.domain.Person;
import bauer.neax.domain.PinOwner;

public class PinOwnerUpdate {
	
	@Inject
	private CallsDao callsDao;
	
	@Inject
	private SecurityService securityService;
	
//	@Inject
//	private ComponentResources componentResources;
	
	@Inject
	private PageRenderLinkSource pageRenderLinkSource;
	
    @Inject
    private AlertManager alertManager;	
	
//	@InjectPage
//	private PinOwnerSearch search;
	
	@Property
	private String pin;
	
    @Property
    @Persist
	private PinOwner pinOwner;

    @Component
    private Form pinOwnerForm;
    
    @Component
    private TextField pinCode;

    
	String onPassivate(){
		return pin;
	}
	
	void onActivate(String pin){		
		this.pin = pin;
	}
	
	
	void setupRender() {
		
		Subject currentUser = securityService.getSubject();
		Person user = (Person) currentUser.getPrincipal();
		
		try{
		pinOwner = callsDao.getPinOwner(pin);
		pinOwner.setWhAuthCode(pinOwner.getAuthCode());
		pinOwner.setUpdatedBy(user.getAlias());
		}catch (EmptyResultDataAccessException e){
		
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Pin " + pin +" does not exist");
		}
		
	}
	
// PinOwnerForm bubbles up the PREPARE_FOR_RENDER event during form render.
//	void onPrepareForRender() {
//		
//	}
		
	void onValidateFromPinOwnerForm(){
		
		if(pinOwner==null){
			pinOwnerForm.recordError("Pin Owner object cannot be null!");
			throw new RuntimeException("Pin Owner object cannot be null!");
		}
		
	
		try{
			callsDao.updPinOwner(pinOwner);		
		}catch(DuplicateKeyException dke){
			
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Pin code " + pinOwner.getAuthCode() + " already used by: "
					+ callsDao.getPinOwner(pinOwner.getAuthCode()));
			
//			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Pin code " + pinOwner.getAuthCode() + " already exists!");
			
			pinOwnerForm.recordError(pinCode, "Duplicate key");
			
//			pinOwnerForm.recordError("Pin code " + pinOwner.getAuthCode() + " already exists!");
//			dke.printStackTrace();
		}		
	
	}
	
	@DiscardAfter
	Object onSuccess() throws MalformedURLException{
	
	 String link = pageRenderLinkSource.createPageRenderLink(PinOwnerSearch.class).toAbsoluteURI();
		
//	 System.out.println(link);
					
//	StringBuilder uri = new StringBuilder(componentResources.createFormEventLink("updateresult").toAbsoluteURI());
//	uri.replace(uri.lastIndexOf("/")+1, uri.lastIndexOf(":"), "search");

	return new URL(link + ":updateresult");
		
	}

}
