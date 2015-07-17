package bauer.neax.pbxaccess.services;

import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.ldap.JndiLdapRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.realm.ldap.LdapUtils;
import org.apache.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author csyperski
 */
public class CWSJndiLdapRealm extends JndiLdapRealm
{   
    private static final Logger log = LoggerFactory.getLogger(CWSJndiLdapRealm.class);
    
    public  static final String DEFAULT_SEARCH_FILTER = "(uid={0})";
    private String searchBase;
    private String searchFilter;
    
    public CWSJndiLdapRealm()
    {
        super();
        searchFilter = DEFAULT_SEARCH_FILTER;
        searchBase = null;
    }
    
    @Override
    public void setUserDnTemplate(String template) throws IllegalArgumentException 
    {
        throw new RuntimeException("This method is not implemented, please use setSeachFilter and setBaseDn");
    }
    
    @Override
    public String getUserDnTemplate() 
    {
        throw new RuntimeException("This method is not implemented, please use getSeachFilter and getBaseDn");
    }
    
    protected String getSearchFilter(String principal) throws IllegalArgumentException, IllegalStateException 
    {
        if (!StringUtils.hasText(principal)) 
        {
            throw new IllegalArgumentException("User principal cannot be null or empty.");
        }
        
        if ( ! searchFilter.contains("{0}") )
        {
            log.warn("You didn't include {0} in your searchFilter, I assume you know what you are doing!");
        }
        return searchFilter.replace("{0}", principal);
    }
    
    @Override
    protected AuthenticationInfo queryForAuthenticationInfo(AuthenticationToken token,
                                                            LdapContextFactory ldapContextFactory)
            throws NamingException {
        
        Object principal = token.getPrincipal();
        Object credentials = token.getCredentials();

        if ( searchBase == null || searchBase.trim().length() == 0 )
        {
            log.error("searchBase must be defined");
            return null;
        }
        
        if ( searchFilter == null || searchFilter.trim().length() == 0 )
        {
            log.error("searchFilter must be defined");
            return null;
        }
        
        if ( ! (principal instanceof String) )
        {
            log.error("principal must be a string");
            return null;
        }

        String filter = getSearchFilter((String)principal);

        log.debug("Using base: {}", searchBase);
        log.debug("Using filter: {}", filter);
        
        LdapContext ctx = null;
        
        SearchControls searchControls = new SearchControls();  
        searchControls.setReturningAttributes(new String[] { "dn" });
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);  
        
        /* We don't know what the search filter will look like
         * So if we get multiple results, we should try them all.
         */
        List<String> possibleDns = new ArrayList<String>();
        
        /* Lets search for the user first */
        try 
        {
            log.debug("Searching for user '{}' through LDAP", principal);
            ctx = ldapContextFactory.getSystemLdapContext();
            NamingEnumeration response = ctx.search(searchBase, filter, searchControls); 
            while (response.hasMoreElements())  
            {  
                possibleDns.add(((SearchResult)response.next()).getNameInNamespace());
            }  
        } 
        finally 
        {
            LdapUtils.closeContext(ctx);
        }
        
        /* Now lets authenticate */
        if ( ! possibleDns.isEmpty() )
        {
            for( String dn : possibleDns )
            {
                ctx = null;
                try 
                {
                    log.debug("Attempting dn '{}' through LDAP", dn);
                    ctx = ldapContextFactory.getLdapContext(dn, credentials);
                    log.debug("Authenticated: {}!", dn);
                    return createAuthenticationInfo(token, principal, credentials, ctx);        // currently uses simple -> change to a subclass thats supports holding the dn?
                } 
                catch ( NamingException e )
                {
                    log.debug("Failed to authenticate for: {}", dn);
                }
                finally 
                {
                    LdapUtils.closeContext(ctx);
                }
            }
        }

        throw new NamingException( "User: '" + (String)principal +  "' not authenticated!" );
    }

    /**
     * @return the searchBase
     */
    public String getSearchBase()
    {
        return searchBase;
    }

    /**
     * @param searchBase the searchBase to set
     */
    public void setSearchBase(String searchBase)
    {
        this.searchBase = searchBase;
    }

    /**
     * @return the searchFilter
     */
    public String getSearchFilter()
    {
        return searchFilter;
    }

    /**
     * @param searchFilter the searchFilter to set
     */
    public void setSearchFilter(String searchFilter)
    {
        this.searchFilter = searchFilter;
    }
}
