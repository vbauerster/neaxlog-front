package bauer.neax.pbxaccess.pages.adm.pinowner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.DiscardAfter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
//import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.tynamo.security.services.SecurityService;

import bauer.neax.dao.CallsDao;
import bauer.neax.dao.LdapDao;
import bauer.neax.domain.Person;
import bauer.neax.domain.PinOwner;

@Import(library={"context:/static/azone.js"})
public class PinOwnerCreate {
	
	private static final Logger log = LoggerFactory.getLogger(PinOwnerCreate.class);

	@Inject
	private CallsDao callsDao;
	
	@Inject
	private LdapDao ldapDao;
	
	@Inject
	private SecurityService securityService;

//	@Inject
//	private ComponentResources componentResources;

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;	
	
	@Inject
	private Request request;
	
    @Inject
    private AlertManager alertManager;
    
	@InjectPage
	private PinOwnerSearch search;
	
    @Property
    @Persist
	private PinOwner pinOwner;	
	
    @Component
    private Form pinOwnerForm;
    
    @Component
    private TextField pinCode;
    
//    @Component
//    private TextField gin;
    
    @InjectComponent
    private Zone formZone;
    
    @Property
    private String ldapQuery;
    
    
    List<String> onProvideCompletionsFromLdapQuery(String partial){
        
    	List<String> matches = new ArrayList<String>();
    	partial = partial.toLowerCase();
    	
    	for(String alias : callsDao.getAliases(false)){
    		
    		if(alias.toLowerCase().startsWith(partial)){
    			matches.add(alias);
    		}
    	}
    	return matches;
    }	
    
    
    List<PinOwner> onProvideCompletionsFromAlias(String partial){
    
    	List<PinOwner> matches = new ArrayList<PinOwner>();
    	partial = partial.toLowerCase();
    	
    	for(PinOwner po : callsDao.getPinOwners()){
    		
    		if(po.getAlias().toLowerCase().startsWith(partial)){
    			matches.add(po);
    		}
    	}
    	return matches;
    }	
    
    
//    @Log
	void setupRender() {
		
    	if(pinOwner==null)
		  pinOwner = new PinOwner();
	}

    
//	@Log
//	public Object onSelectedFromAliasLink(){
//		
//		log.debug("Alias got from linksubmit: " + pinOwner.getAlias());
//		
////		Person p = ldapDao.getPersonByAlias(pinOwner.getAlias());
//			
//		pinOwner.setEmployeeNumber("12312");
//	
////		pinOwnerForm.recordError("");
//		
////		return componentResources.createEventLink("updateform");
//		return request.isXHR() ? formZone.getBody() : null;
//	}
	
//	@Log
//	public void onPrepareForRender(){
//
////		pinOwner.setEmployeeNumber("12312");
////		pinOwner.setFirstName(p.getGivenName());
////		pinOwner.setLastName("bauer");
////		pinOwner.setMail(p.getMail());
//	
//	}
	
	
//	@Log
	void onValidateFromPinOwnerForm() {

		if (pinOwner == null) {
			log.debug("PinOwner is null");
			pinOwnerForm.recordError("Pin Owner object cannot be null!");
			throw new RuntimeException("Pin Owner object cannot be null!");
		}

		Subject currentUser = securityService.getSubject();
		Person user = (Person) currentUser.getPrincipal();

		pinOwner.setUpdatedBy(user.getAlias());

		
			try {
				callsDao.insPinOwner(pinOwner);
			} catch (DuplicateKeyException dke) {
				
				alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Pin code " + pinOwner.getAuthCode() + " already assigned to "
									+ callsDao.getPinOwner(pinOwner.getAuthCode()));
				pinOwnerForm.recordError(pinCode, "Duplicate key");
			} 
		
	}	
	
//	@Log
//	Object onUpdateForm(String context){
//		
//		pinOwner.setLastName("bauer");
//		pinOwner.setMail("vbauer@slb.com");
//		System.out.println(context);
//		
//		return request.isXHR() ? formZone.getBody() : null;
//	}
	
//	@Log
	@DiscardAfter
	Object onSuccessFromPinOwnerForm() throws MalformedURLException{	
		
		String link = pageRenderLinkSource.createPageRenderLink(PinOwnerSearch.class).toAbsoluteURI();
		
		return new URL(link + ":updateresult");
//		return search;		
	}
	
//	@Log
	public Object onSuccessFromLdapForm() {
		log.debug(ldapQuery);
		if (ldapQuery != null) {

			Person p = ldapDao.getPersonByAlias(ldapQuery);
			
			if(p!=null){
			pinOwner.setAlias(p.getAlias());
//			pinOwner.setEmployeeNumber(p.getEmployeeNumber());
			pinOwner.setFirstName(p.getGivenName());
			pinOwner.setLastName(p.getSurname());
			pinOwner.setMail(p.getMail());
			}
		}
		return request.isXHR() ? formZone.getBody() : null;
	}
	
//	@Log
//	Object onFailure() {
//		return request.isXHR() ? formZone.getBody() : null;
//	}
	
}
