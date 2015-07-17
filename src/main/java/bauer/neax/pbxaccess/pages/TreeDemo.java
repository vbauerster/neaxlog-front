package bauer.neax.pbxaccess.pages;

import org.apache.tapestry5.PersistenceConstants;  
import org.apache.tapestry5.ValueEncoder;  
import org.apache.tapestry5.annotations.InjectComponent;  
import org.apache.tapestry5.annotations.Persist;  
import org.apache.tapestry5.annotations.Property;  
import org.apache.tapestry5.corelib.components.Tree;  
import org.apache.tapestry5.tree.DefaultTreeModel;  
import org.apache.tapestry5.tree.TreeModel;  
  
import bauer.neax.pbxaccess.pagehelpers.ValueWrapper;  
import bauer.neax.pbxaccess.pagehelpers.ValueWrapperTreeModelAdapter;  
  
/** 
 * This Tree example is based on the TreeDemo example in Tapestry 5.3.0. 
 *  
 */  
public class TreeDemo {  
    @InjectComponent  
    private Tree tree;  
  
    @SuppressWarnings("unused")  
    @Persist(PersistenceConstants.FLASH)  
    @Property  
    private ValueWrapper valueWrapper;  
  
    private static final ValueWrapper rootNode = new ValueWrapper("Root", null);  
  
    static {  
        // Build the data structure for the tree  
        ValueWrapper coreNode = new ValueWrapper("Core components", null);  
        coreNode.addChild(new ValueWrapper("About", "about"));  
        coreNode.addChild(new ValueWrapper("Contact", "contact"));  
        coreNode.addChild(new ValueWrapper("Index", "index"));  
  
        ValueWrapper custNode = new ValueWrapper("Custom components", "");  
        ValueWrapper colorPickerNode = new ValueWrapper("ColorPicker", null);  
        ValueWrapper dynamicSelectNode = new ValueWrapper("DynamicSelect", null);  
        ValueWrapper tabNode= new ValueWrapper("TabDemo", "LdapTree");  
        custNode.addChild(colorPickerNode).addChild(dynamicSelectNode).addChild(tabNode);  
          
//        ValueWrapper techNode = new ValueWrapper("Techniques", "");  
//        techNode.addChild(new ValueWrapper("Parent Child Window", "test/parentWindow"));  
//        techNode.addChild(new ValueWrapper("Sum", "test/sum"));  
          
      
  
        rootNode.addChild(coreNode).addChild(custNode);  
  
    }  
  
    public TreeModel<ValueWrapper> getValueWrapperModel() {  
        ValueEncoder<ValueWrapper> encoder = new ValueEncoder<ValueWrapper>() {  
            public String toClient(ValueWrapper value) {  
                return value.uuid;  
            }  
  
            public ValueWrapper toValue(String clientValue) {  
                return rootNode.seek(clientValue);  
            }  
        };  
  
        return new DefaultTreeModel<ValueWrapper>(encoder,  
                new ValueWrapperTreeModelAdapter(), rootNode.children);  
    }  
  
    void onActionFromClear() {  
        tree.clearExpansions();  
    }  
  
  
}  