package bauer.neax.pbxaccess.services;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tynamo.security.services.SecurityService;
//import org.apache.shiro.session.Session;

import bauer.neax.dao.CallsDao;
import bauer.neax.dao.LdapDao;
import bauer.neax.domain.Person;

public class SpringLdapRealm extends AuthorizingRealm {
	
	private static final Logger log = LoggerFactory.getLogger(SpringLdapRealm.class);
	
//	public static final String AUTH_SUBJECT = "AuthSubject";
	
	@Inject
	private LdapDao ldapDao;
	
	@Inject
	private CallsDao callsDao;
	
//	public LdapDao getLdapDAO() {
//		return ldapDAO;
//	}

	@Inject
	private SecurityService securityService;
	
	
	public SpringLdapRealm(){
        //Credentials Matching is not necessary - the LDAP directory will do it automatically:
        setCredentialsMatcher(new AllowAllCredentialsMatcher());
        //Any Object principal and Object credentials may be passed to the LDAP provider, so accept any token:
        //setAuthenticationTokenClass(AuthenticationToken.class);		
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		
		Person loggedUser = (Person) principals.fromRealm(getName()).iterator().next();
		
		if(loggedUser!=null){
			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//			info.addStringPermissions(loggedUser.getPermissions());

//			info.addRoles(loggedUser.getRoles());
			
			for(String role : loggedUser.getRoles()){
				info.addRole(role);
			}			
			
			for(String perm : loggedUser.getPermissions()){
				info.addStringPermission("ext:view:" + perm);
			}
			
		  return info;
		  
		}else
			
		return null;
	}

	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;

		String username = upToken.getUsername();		
		// Null username is invalid
		if (username == null) { throw new AccountException("Null usernames are not allowed by this realm."); }		
		
        log.debug("Authenticating user '{}' through LDAP", username);
        
        String pass = new String(upToken.getPassword());
        
        
       if(ldapDao.authenticate(username, pass)){
    	   
    	   log.info("Authentication success: " + username);
    	   Person loggedUser = ldapDao.getPersonByAlias(username);
    	   
    	   int pinCount = callsDao.getActivePinsCount(username);	
    	   loggedUser.setHasPin(pinCount>0 ? true : false); 
    	   
    	   loggedUser.setPermissions(callsDao.getExtPermissions(username));
    	   
    	   loggedUser.setRoles(callsDao.getUserRoles(username));
    	   
//    	   Session session = securityService.getSubject().getSession();
//    	   session.setAttribute(AUTH_SUBJECT, ldapDAO.getPersonByAlias(username));
    	   return new SimpleAuthenticationInfo(loggedUser, pass, getName()); // the password shall be hashed, if caching the realm
//    	   return null;
       }else{
    	 log.info("Authentication failure: " + username);
    	 return null;
       }
	}

}
