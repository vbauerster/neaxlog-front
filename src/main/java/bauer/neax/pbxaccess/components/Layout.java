package bauer.neax.pbxaccess.components;

import bauer.neax.domain.Person;
import bauer.neax.pbxaccess.commons.Menu;
import bauer.neax.pbxaccess.commons.MenuOption;

import java.util.HashMap;

import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.Asset;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.services.javascript.StylesheetLink;
import org.apache.tapestry5.services.javascript.StylesheetOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tynamo.security.services.SecurityService;


@Import(stylesheet = "context:static/style.css")
public class Layout
{
	
	private static final Logger log = LoggerFactory.getLogger(Layout.class);
//	@Inject @Path("context:static/zone-overlay-ie.css")
//	private Asset ieCSS;

//	@Environmental
//	private JavaScriptSupport javaScriptSupport;
	
	private static final String CURRENT_PAGE_CSS_CLASS = "current_page_item";
	
	private static final String CHOSEN_OPTION_CSS_CLASS = "chosenOption";
	
//	private static final String[] ALL_PAGES = {"Index", "Options", "About"};
	
//    @Property
//    private String pageName;
    

    @SuppressWarnings("unused")
    @Property
    @Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
    private String title;
    
    
	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String chosenMenu;
    
    
	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String chosenOption; //chosen tab
 
    
    @SuppressWarnings("unused")
    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String sidebarTitle;
    
    @SuppressWarnings("unused")
    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String topbarTitle;    

    @SuppressWarnings("unused")
    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private Block sidebar;
    
    @SuppressWarnings("unused")
    @Property
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private Block topbar;
    
//    @SuppressWarnings("unused")
//    @Property
//    @Parameter(defaultPrefix = BindingConstants.LITERAL)
//    private Block alertbar;

//    @Inject
//    private ComponentResources resources;

	@Inject
	private SecurityService securityService;
    
//    @Persist
//    private HashMap<String, String> pages;
    

	// Screen fields

	private Menu menu;

	@Property
	private MenuOption menuOption;    
    
//	@Log
//	void setupRender(){
//		
//		pages = new HashMap<String, String>();
//    	pages.put("Index", "Reports");
//    	pages.put("Extension", "Reports");
//    	pages.put("DataGrid", "Reports");
//    	pages.put("Options", "Options");
//    	pages.put("About", "About");
//    	pages.put("adm/pinowner/Search", "Admin");
//    	pages.put("adm/pinowner/Create", "Admin");
//    	pages.put("adm/pinowner/Update", "Admin");
//    	pages.put("adm/ext/Access", "Admin");
//    	
//	
//	}
	
//	void afterRender() {
//	    javaScriptSupport.importStylesheet(new StylesheetLink(ieCSS,
//	                                       new StylesheetOptions().withCondition("IE")));
//	    }	

	
	public String getCssClassForReports(){
		return getCssClassForMenu("Reports");
	}
	public String getCssClassForOptions(){
		return getCssClassForMenu("Options");
	}
	public String getCssClassForAdmin(){
		return getCssClassForMenu("Admin");
	}
	public String getCssClassForAbout(){
		return getCssClassForMenu("About");
	}

	
	@Log
    public String getCssClassForMenu(String menu)
    {
		
//		String currentPage = resources.getPageName();
//		log.debug("currentPage: " + currentPage + " = " + pages.get(currentPage));
		
		log.debug("chosenMenu: " + chosenMenu);
		
		if(chosenMenu != null)
		 if(menu.equals(chosenMenu))
			return CURRENT_PAGE_CSS_CLASS;
		
		
//		if(pages.get(currentPage)!=null)
//			if(pages.get(currentPage).equals(chosenMenu))
//				return CURRENT_PAGE_CSS_CLASS;
//		
//		}
	
	
//		log.debug("pageName: " + pageName);
//		log.debug("currentPage: " + currentPage);
//		
//        if (currentPage.equalsIgnoreCase(pageName))
//        	return CURRENT_PAGE_CSS_CLASS;
//        else
//        	if(pageName.equalsIgnoreCase("Index") && currentPage.equalsIgnoreCase("Extension"))	
//        		return CURRENT_PAGE_CSS_CLASS;
//        else 
//        	if(pageName.equalsIgnoreCase("adm/pinowner/search") && currentPage.startsWith("adm/"))
//        		return CURRENT_PAGE_CSS_CLASS;
//        
//        
        return "";
    }

    
	public String getMenuOptionCSSClass() {
		return menuOption.getLabel().equals(chosenOption) ? CHOSEN_OPTION_CSS_CLASS : "";
	}
    
    
//    public String[] getPageNames()
//    {
//    	
//    	String[] menubar = ALL_PAGES;
//       	
//    	if(securityService.getSubject().hasRole("admin")){
//    		String[] powbar = new String[menubar.length+1];
//    		System.arraycopy(menubar, 0, powbar, 0, menubar.length-1);
//    		menubar=powbar;
//    		menubar[2]="adm/pinowner/Search";
//    		menubar[3]="About";
//    	}
//    	
//    	return menubar;
//    }  
    
    
//    public String getPageValue(String key){
//    	
//     	return pages.get(key);
//    }
    
    
    public Person getUser()
    {
    	Subject currentUser = securityService.getSubject();
    	
    	return (Person) currentUser.getPrincipal();
    	
    }
    

    public boolean isAdminMenu(){
    	
     if(chosenMenu!=null)
    	if(chosenMenu.equals("Admin"))
    		return true;
    	
    	return false;
    	
//    	String currentPage = resources.getPageName();
//    	
//    	if(currentPage.startsWith("adm/"))
//    		return true;
//    	else
//    		return false;    	
    }
   
    
    public boolean isReportsMenu(){
    	
        if(chosenMenu!=null)
        	if(chosenMenu.equals("Reports"))
        		return true;
        	
        	return false;

//    	String currentPage = resources.getPageName();
//    	
//    	if(currentPage.equalsIgnoreCase("Index") || currentPage.equalsIgnoreCase("Extension"))
//    		return true;
//    	else
//    		return false;
    	
    }
 
    
	public Menu getAdmMenu() {

		if (menu == null) {
			menu = new Menu();
			menu.add(new MenuOption("Manage Pin Owners", "adm/pinowner/search"));
			menu.add(new MenuOption("Phone Ext. Access", "adm/ext/access"));
		}

		return menu;
	}  
    
	
	public Menu getMenu() {

		if (menu == null) {
			menu = new Menu();
			menu.add(new MenuOption("By Pin Owner", "Index"));
			menu.add(new MenuOption("By Extension", "Extension"));
		}

		return menu;
	}


    
}
