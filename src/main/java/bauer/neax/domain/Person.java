package bauer.neax.domain;

import java.util.List;


public class Person implements Comparable<Person> {
	
//	@Inject
//	private LdapDaoImpl ldapDAO;
	
    private String[] cn;
    private String givenName;
    private String surname;
    private String alias;
    private String mail;
    private String jobTitle;
    private String employeeNumber;
    private String applicationMiFareNo;
    private String city;
    private String employeeType;
    private String ou;
    private String businessCategory;
    private boolean hasPin;
    //this field is get from DB and setup in realm;
    private List<String> permissions;
    private List<String> roles;
    
//    private String[] manager;
     
//    private List<Person> children = new ArrayList<Person>();
//      
// 
//    public Person(){
//    	
//    }
//    
//    public Person(String alias){
//    	Person p = ldapDAO.getPersonByAlias(alias);
//    	
//    	
//    	setChildren(ldapDAO.getReporters(p.getDn()));
//    }
//    

        	
	public void setCn(String[] cn) {
		this.cn = cn;
	}
	public String getCn() {
		return cn[0];
	}
	
	public String getDn() {
	String dn="";
	for(int i=0; i< cn.length; i++){
		if(cn[i].matches(".+\\s\\d+")){
//			System.out.println("Match found at " + i);
			dn = cn[i] + ",ou=" + employeeType + ",o=SLB,c=AN";
			break;
		  }
		}	
	return dn;
	}
	
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getGivenName() {
		return givenName;
	}
	
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	
	public String getAlias() {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getApplicationMiFareNo() {
        return applicationMiFareNo;
    }

    public void setApplicationMiFareNo(String applicationMiFareNo) {
        this.applicationMiFareNo = applicationMiFareNo;
    }

	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }


	public void setCity(String city) {
		this.city = city;
	}
	public String getCity() {
		return city;
	}
	
	public void setEmployeeType(String employeeType) {
		this.employeeType = employeeType;
	}
	public String getEmployeeType() {
		return employeeType;
	}
	
	public void setOu(String ou) {
		this.ou = ou;
	}
	public String getOu() {
		return ou;
	}
	public void setBusinessCategory(String businessCategory) {
		this.businessCategory = businessCategory;
	}
	public String getBusinessCategory() {
		return businessCategory;
	}
	
	@Override
	public String toString() {
		StringBuffer PersonStr = new StringBuffer("Person=[");
		
//		for(int i=0; i< cn.length; i++)	
//		PersonStr.append(" CN=" + cn[i]);
		
		PersonStr.append(" DN=" + getDn());
		PersonStr.append(", First Name=" + givenName);
		PersonStr.append(", Last Name=" + surname);
		PersonStr.append(", Job Title=" + jobTitle);
        PersonStr.append(", Alias=" + alias);
        PersonStr.append(", mail=" + mail);
        PersonStr.append(", GIN=" + employeeNumber);
        PersonStr.append(", applicationMiFareNo=" + applicationMiFareNo);
        PersonStr.append(", ou=" + ou);
        PersonStr.append(", city=" + city);
		PersonStr.append(" ]");
		return PersonStr.toString();
	}

	
//	@Override
	public int compareTo(Person p) {
		// Sorting the list by Common Name
		return getCn().compareTo(p.getCn());
	}
	
//	@Override
	public boolean equals(Person p) {
		return getCn().equals(p.getCn());
	}
	public boolean isHasPin() {
		return hasPin;
	}
	public void setHasPin(boolean hasPin) {
		this.hasPin = hasPin;
	}
	
	public String getCssForPin(){
		
		return hasPin ? "active" : "";
	}
	

    public List<String> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
		
//	public List<Person> getChildren() {
//		return children;
//	}
//
//
//	public void setChildren(List<Person> children) {
//		this.children = children;
//	}
	
}
