package bauer.neax.pbxaccess.pages;


import java.util.List;

import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.Asset;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.services.javascript.StylesheetLink;
import org.apache.tapestry5.services.javascript.StylesheetOptions;
import org.apache.tapestry5.tree.DefaultTreeModel;
import org.apache.tapestry5.tree.TreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tynamo.security.services.SecurityService;

import bauer.neax.dao.CallsDao;
import bauer.neax.dao.LdapDao;
import bauer.neax.domain.Person;
import bauer.neax.domain.ReportParam;
import bauer.neax.pbxaccess.model.PersonNode;
import bauer.neax.pbxaccess.model.PersonTreeModelAdapter;

@Import(library="context:static/zone-overlay.js", stylesheet = "context:/static/zone-overlay.css")

public class Index {

	private static final Logger log = LoggerFactory.getLogger(Index.class);


	@Inject @Path("context:static/zone-overlay-ie.css")
	private Asset ieCSS;

	@Environmental
	private JavaScriptSupport javaScriptSupport;

	@Inject
	private SecurityService securityService;

	@Inject
	private LdapDao ldapDao;

	@Inject
	private CallsDao callsDao;

	@Inject
	private Request request;

	@InjectComponent
	private Zone selectedZone;

	@InjectComponent
	private Zone treeZone;

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

    @Inject
    private AlertManager alertManager;

    @InjectPage
    private DataGrid dataGridPage;

//    @Property
//    @Persist(PersistenceConstants.FLASH)
//    @Validate("required")
//    private Date from;
//
//    @Property
//    @Persist(PersistenceConstants.FLASH)
//    @Validate("required")
//    private Date to;
//
//    @Property
//    @Persist(PersistenceConstants.FLASH)
//    private int line;

//	@Persist(PersistenceConstants.FLASH)
	@Property
	private PersonNode personNode;

	@Persist
	@Property
	private PersonNode rootNode;

//	@Property
//	@SuppressWarnings("unused")
//	private TreeNode<PersonNode> treeNode;

//	@Persist
//	@Property
//	private SortedSet<Person> reporters;

	@Property
	@SuppressWarnings("unused")
	private Person person;

	@Persist
	private int level;

	@Property
	@Persist
	private ReportParam rParam;


//	 @Log
	 public void setupRender(){

		 if(rootNode==null){
		  rParam = new ReportParam();
		  Subject subj = securityService.getSubject();
		  Person currentUser = (Person) subj.getPrincipal();
		  level = 0;
//		  reporters = new TreeSet<Person>();
		  PersonNode.setCallsDao(callsDao);
		  PersonNode.setLdapDao(ldapDao);
		  rootNode = new PersonNode(currentUser);
//		  rootNode = new PersonNode(ldapDao, callsDao, currentUser);
		 }
	 }


	void afterRender() {
		    javaScriptSupport.importStylesheet(new StylesheetLink(ieCSS, new StylesheetOptions().withCondition("IE")));
		 }


//	@Log
	public Object onSuccess(){
		dataGridPage.setReportParam(rParam);
		return dataGridPage;
	}


	public Person seekPerson(String alias){
		 return treeSeek(rootNode, alias);
	 }


	private Person treeSeek(PersonNode root, String alias) {

		 if(alias.equals(root.getParent().getAlias()))
			 return root.getParent();

		 for(PersonNode child : root.getChildren()){
			 Person match = treeSeek(child, alias);
			 if (match != null)
				 return match;
		 }
		 return null;
	 }


	 void onLeafSelected(String alias){

		 Person slctd = seekPerson(alias);

		 if(!slctd.isHasPin()){
			 alertManager.alert(Duration.TRANSIENT, Severity.WARN, slctd.getCn() + " has no phone pin code!");
		 }else{
//		  System.out.println("Adding person: " + slctd.getDn());
//		  reporters.add(slctd);
		  rParam.getReporters().add(slctd);
		 }

			if (request.isXHR()) {
				ajaxResponseRenderer.addRender(selectedZone);
			}
	 }


	 void onLeafUnSelected(String alias){

//		 reporters.remove(seekPerson(alias));
		 rParam.getReporters().remove(seekPerson(alias));

			if (request.isXHR()) {
				ajaxResponseRenderer.addRender(selectedZone);
			}
	 }


//	 @Log
	 private void seekGrandChild(PersonNode root){

		 List<PersonNode> node = root.getChildren();

		 int i=0;
		 for(PersonNode pn : node){

			 if(pn.isLeaf()){

//				 PersonNode child = new PersonNode(ldapDao, callsDao, pn.getParent());
				 PersonNode child = new PersonNode(pn.getParent());

				 if(child.hasChildren()){
//					 System.out.println("Replacing: " + node.set(i, child));
				  log.debug("Children found for: " + pn.getParent().getCn());
				  node.get(i).setChildren(child.getChildren());
				 }
			 }

			 i++;
		 }
	 }


// Starting lvl=2
//	@Log
	private void seekLeafWithChild(PersonNode root, int lvl) {

		List<PersonNode> node = root.getChildren();

		for (int i = 0; i < node.size(); i++) {

			if (lvl == level) {

				if (node.get(i).hasChildren()) {
//					log.debug("lvl==level");
//					log.debug("lvl={} level={} ", lvl, level);
					log.debug("*****  Children lookup for: {}  *****", node.get(i).getParent().getCn());

					seekGrandChild(node.get(i));

					// dig one leve down
				}

			} else {
				seekLeafWithChild(node.get(i), lvl + 1);
			}
		}
	}

	 void onSubLeaf(){

		 level++;

		 if(level==1)
			 seekGrandChild(rootNode);
		 else
			 seekLeafWithChild(rootNode, 2);


		if (request.isXHR()) {
				ajaxResponseRenderer.addRender(treeZone);
			}
	 }


	public int getLevel(){
		 return level+1;
	 }


// We could return a DateFormat, but instead we'll return a String which DateField will coerce into a DateFormat.
  public String getDateFieldFormat() {
			return "dd/MM/yyyy";
  }



	 public TreeModel<PersonNode> getPersonNodeModel(){

		 ValueEncoder<PersonNode> encoder = new ValueEncoder<PersonNode>(){

			public String toClient(PersonNode node) {
				return node.getParent().getAlias();
			}

			public PersonNode toValue(String alias) {
				// TODO Auto-generated method stub
				return rootNode.seek(alias);
			}

		 };

		 return new DefaultTreeModel<PersonNode>(encoder, new PersonTreeModelAdapter(), rootNode);
	 }

}
