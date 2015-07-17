package bauer.neax.pbxaccess.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import bauer.neax.dao.CallsDao;
import bauer.neax.dao.LdapDao;
import bauer.neax.domain.Person;


public class CopyOfPersonNode{

//	@InjectService(value = "LdapDaoImpl")
//	private LdapDao ldapDao;	
//	@InjectService("SpringLdapRealm") SpringLdapRealm ldapRealm;
	

	private Person parent;
	private List<CopyOfPersonNode> children = new ArrayList<CopyOfPersonNode>();
	
//	private int level;
	
	
	
//	private PersonNode(Builder builder){
//		// private Constructor can only be called from Builder
//		
//		this.ldapDAO = builder.ldapDAO;
//	}
	
//	public void setLdapDao(LdapDao ldapDao) {
//		
////		<bean id="PersonNode"	class="bauer.neax.pbxaccess.model.PersonNode">
////			<property name="ldapDao" ref="ldapDao" />
////		 </bean>   
//		this.ldapDao = ldapDao;
//	}
	
	public CopyOfPersonNode(LdapDao ldapDao, CallsDao callsDao, Person parent, int lvl) {

		if (parent == null)
			throw new RuntimeException("Person object cannot be null!");
		
		this.parent = parent;

		if (lvl != 0) {

			List<Person> reporters = ldapDao.getReporters(parent.getDn());

			if (!reporters.isEmpty()) {
				
				lvl--;
				Collections.sort(reporters);
	
				for (int i = 0; i < reporters.size(); i++) {
					int pinCount = callsDao.getActivePinsCount(reporters.get(i).getAlias());

					reporters.get(i).setHasPin(pinCount > 0 ? true : false);
					
					addChild(new CopyOfPersonNode(ldapDao, callsDao, reporters.get(i), lvl));
				}				
			}
		}
	}	

	public CopyOfPersonNode(LdapDao ldapDao, CallsDao callsDao, Person parent){
		
		this(ldapDao, callsDao, parent, 1);

	}

	
	public CopyOfPersonNode addChild(CopyOfPersonNode child){
		
		children.add(child);
		
		return this;
	}
	
	
	public CopyOfPersonNode seek(String alias){
		
		if (parent.getAlias().equals(alias))		
			return this;
		
		for(CopyOfPersonNode node : children){
			
			if(alias.equals(node.getParent().getAlias()))
			
				return node;
		}
		
		return null;
//		throw new RuntimeException("Invalid alias returned from client: " + alias);
	}
	
	
	public Person getParent() {
		return parent;
	}

	public void setParent(Person person) {
		this.parent = person;
	}

	public List<CopyOfPersonNode> getChildren() {
		return children;
	}

	public void setChildren(List<CopyOfPersonNode> children) {
		this.children = children;
	}
	
	public boolean isLeaf(){
	
		return children.isEmpty();
		
	}
	
	public boolean hasChildren(){
		
		return !children.isEmpty(); 
	}

//	public int getLevel() {
//		return level;
//	}
//
//
//	public void setLevel(int level) {
//		this.level = level;
//	}	


	
//	public static class Builder{
//		
//		private final LdapDaoImpl ldapDAO;
//		
//		public Builder(LdapDaoImpl ldapDAO){
//			
//			this.ldapDAO = ldapDAO;
//		}
//		
//		
//		public PersonNode build() {			
//			return new PersonNode(this);			
//		}
//		
//	}
	
	
	
}
