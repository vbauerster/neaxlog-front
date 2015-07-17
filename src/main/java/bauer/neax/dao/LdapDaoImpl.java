/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bauer.neax.dao;

import bauer.neax.domain.Person;

import java.util.List;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.apache.log4j.Logger;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.filter.WhitespaceWildcardsFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.apache.tapestry5.ioc.annotations.Inject;

/**
 *
 * @author Vladimir
 */
public class LdapDaoImpl implements LdapDao{

//	static final Logger log = Logger.getLogger(LdapDaoImpl.class);
	private static final Logger log = LoggerFactory.getLogger(LdapDaoImpl.class);

	private boolean passNoCheck = false;
	private boolean ldapNoCheck = false;

	private LdapTemplate ldapTemplate;

	private CallsDao callsDao;


	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

/**
 *
 * @param callsDao
 *   <bean id="callsDao"	class="bauer.neax.dao.CallsDaoImpl">
		<property name="dataSource" ref="dataSource" />
	    </bean>
 *
 *
 */

	public void setCallsDao(CallsDao callsDao){

		this.callsDao = callsDao;
	}


	/**
	 *
	 * @param alias User Name
	 * @param password Password
	 * @param b boolean var args
	 * 1st boolean if true, password check bypassed;
	 * 2nd boolean if true, auth person is not get from ldap, but dummy one inserted
	 */
	public boolean authenticate(final String alias, final String password, boolean... bargs) {

		for (int i = 0; i < bargs.length; i++) {

			switch (i) {

			case 0:
				passNoCheck = bargs[i];
				break;
			case 1:
				ldapNoCheck = bargs[i];
				break;

			}
		}

		// mock auth for development mode.
		if (passNoCheck)
			return true;

		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("objectclass", "person")).and(new EqualsFilter("alias", alias));
		log.debug("Athentication filter: " + filter.toString());
		return ldapTemplate.authenticate(DistinguishedName.EMPTY_PATH, filter.toString(), password);
	}

public List<Person> getPersonByCN(String cn){
		AndFilter andFilter = new AndFilter();
		andFilter.and(new EqualsFilter("objectclass","person"));

        OrFilter orFilter = new OrFilter();
        orFilter.or(new EqualsFilter("cn", cn));
        orFilter.or(new WhitespaceWildcardsFilter("cn", cn));

        andFilter.and(orFilter);

//        System.out.println("LDAP Query " + andFilter.encode());
        return ldapTemplate.search(DistinguishedName.EMPTY_PATH, andFilter.encode(), getContextMapper());
}

	public Person getPersonByAlias(String alias){

		//mock auth for development mode.
		if(ldapNoCheck){
		  Person p = new Person();
		  p.setAlias(alias);
		  p.setCn(new String[]{"Vladimir Bauer"});
		  p.setEmployeeNumber("02349223");
		  p.setApplicationMiFareNo("02088337");
		  p.setOu("Corporate");
		  p.setBusinessCategory("O610-IT Field Operations");

		  return p;
		}

		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("objectclass","person"));
		filter.and(new EqualsFilter("alias",alias));
//		System.out.println("LDAP Query " + filter.encode());
		List<Person> matches = ldapTemplate.search(DistinguishedName.EMPTY_PATH, filter.encode(), getContextMapper());

		return matches.size() > 0 ? matches.get(0) : null;

   	}

	 public List<Person> getReporters(String dn){

	    AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("objectclass","person"));
		filter.and(new EqualsFilter("manager", "cn=" + dn));
		log.debug("LDAP Query " + filter.encode());

		return ldapTemplate.search(DistinguishedName.EMPTY_PATH, filter.encode(), getContextMapper());
	 }

	/**
	 *
	 * @param dn
	 * @return Returns reporters with active pin, checked against callsDao
	 */
	  public List<Person> getReportersWithPin(String dn){

		List<Person> plist = getReporters(dn);

		if(!plist.isEmpty()){
			for(int i=0; i<plist.size(); i++){
				int pins = callsDao.getActivePinsCount(plist.get(i).getAlias());

				log.debug("{} has {} pins", plist.get(i).getCn(), pins);

				if(pins==0){
//					log.debug("Removing {} as he doesn't have a pin", plist.get(i).getCn());
					System.out.println(String.format("Removing %s as he doesn't have a pin", plist.get(i).getCn()));
					plist.remove(i);
				}
				else
//					log.debug("Pin {} found for {}", pins.get(0), plist.get(i).getCn());
					System.out.println(String.format("%s pin found for %s",  pins, plist.get(i).getCn()));

			}
		}

		return plist;
	}


    public Person getPersonByBadgeKey(String key){

     if(key.length() < 8){
    	 for(int i=0, l=key.length(); i < 8-l; i++){
    		key = "0" + key;
    	 }
     	}

        AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("objectclass","Person"));
		filter.and(new EqualsFilter("applicationMiFareNo", key));
//		System.out.println("LDAP Query " + filter.encode());
		List<Person> matches = ldapTemplate.search(DistinguishedName.EMPTY_PATH, filter.encode(), getContextMapper());

		return matches.size() > 0 ? matches.get(0) : null;

//		if(l.isEmpty()){
//			Person p = new Person ();
////			p.setCn("Not Found!");
//			p.setGivenName("Not Found!");
//			p.setApplicationMiFareNo(key);
//			return p;
//		}
//		else
//			return l.get(0);

//		return (Person) ldapTemplate.lookup("cn=Vladimir Bauer  336362", getContextMapper());
    }


    public Person getPersonByGin(String key){

        if(key.length() < 8){
       	 for(int i=0, l=key.length(); i < 8-l; i++){
       		key = "0" + key;
       	 }
        }

        AndFilter filter = new AndFilter();
   		filter.and(new EqualsFilter("objectclass","Person"));
   		filter.and(new EqualsFilter("employeeNumber", key));
//   		System.out.println("LDAP Query " + filter.encode());
   		List<Person> matches = ldapTemplate.search(DistinguishedName.EMPTY_PATH, filter.encode(), getContextMapper());

   		return matches.size() > 0 ? matches.get(0) : null;

//   		if(l.size()==0){
//   			Person p = new Person ();
//   			p.setGivenName("Not Found!");
//   			p.setApplicationMiFareNo(key);
//   			return p;
//   		}
//   		else
//   			return l.get(0);

//   		return (Person) ldapTemplate.lookup("cn=Vladimir Bauer  336362", getContextMapper());
       }

    protected ContextMapper getContextMapper(){
        return new PersonContextMapper();
    }


	public boolean isPassNoCheck() {
		return passNoCheck;
	}

	public void setPassNoCheck(boolean passNoCheck) {
		this.passNoCheck = passNoCheck;
	}

	public boolean isLdapNoCheck() {
		return ldapNoCheck;
	}

	public void setLdapNoCheck(boolean ldapNoCheck) {
		this.ldapNoCheck = ldapNoCheck;
	}

private static class PersonContextMapper extends AbstractContextMapper {

//	private JdbcTemplate jdbcTemplate;
//
//    public void setDataSource(DataSource dataSource) {
//        this.jdbcTemplate = new JdbcTemplate(dataSource);
//    }

//    @Inject
//    private PanelReaderService panelDAO;

      public Object doMapFromContext (DirContextOperations ctx){

          Person p = new Person();

    	  p.setCn(ctx.getStringAttributes("cn"));
          p.setGivenName(ctx.getStringAttribute("givenName"));
          p.setSurname(ctx.getStringAttribute("surname"));
          p.setAlias(ctx.getStringAttribute("alias"));
          p.setMail(ctx.getStringAttribute("mail"));
          p.setJobTitle(ctx.getStringAttribute("jobTitle"));
          p.setEmployeeNumber(ctx.getStringAttribute("employeeNumber"));



          try{
        	  p.setApplicationMiFareNo(ctx.getStringAttribute("applicationMiFareNo"));
        	  Integer.parseInt(p.getApplicationMiFareNo());
          }catch(NumberFormatException nfe){
        	  p.setApplicationMiFareNo("-1");
          }


//          p.setApplicationMiFareNo(
//        		  (ctx.getStringAttribute("applicationMiFareNo")==null) ? "-1" : ctx.getStringAttribute("applicationMiFareNo")
//        		);

          p.setCity(ctx.getStringAttribute("l"));
          p.setEmployeeType(ctx.getStringAttribute("employeeType"));
          p.setOu(ctx.getStringAttribute("ou"));
          p.setBusinessCategory(ctx.getStringAttribute("businessCategory"));
//          System.out.println(ctx.getStringAttributes("manager"));
//          ctx.getStringAttributes("manager");
          return p;
     }

  }
}
