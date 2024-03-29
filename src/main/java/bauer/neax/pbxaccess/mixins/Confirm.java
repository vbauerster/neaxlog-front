// Based on http://wiki.apache.org/tapestry/Tapestry5AndJavaScriptExplained

package bauer.neax.pbxaccess.mixins;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

/**
 * A simple mixin for attaching a javascript confirmation box to the onclick event of any component that implements
 * ClientElement.
 * 
 * @author <a href="mailto:chris@thegodcode.net">Chris Lewis</a> Apr 18, 2008
 */
// This annotation tells Tapestry to declare the js file in the page so that the browser will pull it in.
@Import(library = "Confirm.js")
public class Confirm {

	@Parameter(name = "message", value = "Are you sure?", defaultPrefix = BindingConstants.LITERAL)
	private String message;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	@InjectContainer
	private ClientElement clientElement;

	@AfterRender
	public void afterRender() {

		// Tell the Tapestry.Initializer to do the initializing of a Confirm, which it will do when the DOM has been
		// fully loaded.

		JSONObject spec = new JSONObject();
		spec.put("elementId", clientElement.getClientId());
		spec.put("message", message);
		javaScriptSupport.addInitializerCall("confirm", spec);
	}

}
