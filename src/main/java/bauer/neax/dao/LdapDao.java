package bauer.neax.dao;

import java.util.List;

import bauer.neax.domain.Person;


public interface LdapDao {
	
	
    public List<Person> getPersonByCN(String cn);

	public Person getPersonByAlias(String alias);

    public Person getPersonByBadgeKey(String Key);
    
    public Person getPersonByGin(String Key);
    
    public List<Person> getReporters(String dn);
    
	/**
	 * 
	 * @param dn
	 * @return Returns reporters with active pin, checked against callsDao
	 */
	 public List<Person> getReportersWithPin(String dn);
    
	/**
	 * 
	 * @param alias User Name
	 * @param password Password
	 * @param b boolean var args
	 * 1st boolean if true, password check bypassed;
	 * 2nd boolean if true, auth person is not get from ldap, but dummy one inserted
	 */  
    public boolean authenticate(final String alias, final String password, boolean ... b);
    

}
