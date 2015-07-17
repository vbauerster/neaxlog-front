package bauer.neax.pbxaccess.pages;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tynamo.security.internal.services.LoginContextService;
import org.tynamo.security.services.SecurityService;


@Import(library={"context:/static/azone.js"})
public class Login
{

	private static final Logger log = LoggerFactory.getLogger(Login.class);

    @Inject
    private AlertManager alertManager;

	@Inject
	private Request request;

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

    @InjectComponent
    private Zone result;

    @Property
    private String username;

    @Property
    private String password;

	@Inject
	private SecurityService securityService;

	@Inject
	private LoginContextService loginContextService;


    @Log
    public Object onSubmitFromLoginForm() {

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(result);
		}

		Subject currentUser = securityService.getSubject();

		if (currentUser == null)
		{
			throw new IllegalStateException("Subject can`t be null");
		}

		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		token.setRememberMe(false);

		try {
			currentUser.login(token);
		} catch (LockedAccountException e){
			log.debug("LockedAccountException: " + e.getMessage());
			return null;
		} catch (AuthenticationException e){
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Wrong alias or password!");
			return null;
		}

		//say who they are:
		//print their identifying principal (in this case, a username):
		log.debug("User [" + currentUser.getPrincipal() + "] logged in successfully.");

		return loginContextService.getSuccessPage();
    }

}




