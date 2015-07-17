package bauer.neax.pbxaccess.pages;

import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.tynamo.security.services.SecurityService;

import bauer.neax.domain.Person;

//@RequiresAuthentication
public class About
{

	@Inject
	private SecurityService securityService;

    @Inject
    private Request request;

	public String getStatus() {
		return securityService.isAuthenticated() ? "Authenticated" : "Not Authenticated";
	}

	public String getInfo(){
		Subject currentUser = securityService.getSubject();

		Person user =  (Person) currentUser.getPrincipal();

		return user.toString();

	}

}
