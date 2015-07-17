package bauer.neax.pbxaccess.mixins;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.mixins.Autocomplete;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONLiteral;
import org.apache.tapestry5.json.JSONObject;

public class MyAutoComplete extends Autocomplete
{
	
	/**
	 * The event to listen for in your component class
	 */
	@Parameter(name = "event", defaultPrefix = BindingConstants.LITERAL, required = true)
	private String event;
	
	@Parameter(name = "context")
	private Object[] context;
	
	@Inject
	private ComponentResources componentResources;	
	
  @Override
  protected void configure(JSONObject config)
  {
    
    String listenerURI = componentResources.createEventLink(event, context).toAbsoluteURI();
    
//    System.out.println("MyAutoComplete: " + listenerURI);
    
    config.put("afterUpdateElement", new JSONLiteral("location.href=\""+listenerURI+"\""));
    
  }
  
}