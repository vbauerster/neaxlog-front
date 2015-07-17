package bauer.neax.pbxaccess.services;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.ldap.JndiLdapRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.realm.ldap.LdapUtils;


public class SlbLdapRealm extends JndiLdapRealm {
	
	static final Logger log = Logger.getLogger(SlbLdapRealm.class);	
	
	public SlbLdapRealm(){
		super();
	}

	
	@Override
	protected AuthenticationInfo queryForAuthenticationInfo(AuthenticationToken token, LdapContextFactory ldapContextFactory)
			throws NamingException {

		Object principal = token.getPrincipal();
		Object credentials = token.getCredentials();

		log.debug("Authenticating user: " + principal);

		principal = getLdapPrincipal(token);

		LdapContext ctx = null;
		try {
			ctx = ldapContextFactory.getLdapContext(principal, credentials);
			// context was opened successfully, which means their credentials
			// were valid. Return the AuthenticationInfo:
			return createAuthenticationInfo(token, principal, credentials, ctx);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}
	
}
