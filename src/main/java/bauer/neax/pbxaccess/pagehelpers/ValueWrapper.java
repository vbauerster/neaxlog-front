package bauer.neax.pbxaccess.pagehelpers;

import java.util.ArrayList;  
import java.util.List;  
import java.util.UUID;  
  
public class ValueWrapper {  
    public final String uuid = UUID.randomUUID().toString();  
  
    private String label;  
  
    private String pageName;  
  
    public final List<ValueWrapper> children = new ArrayList<ValueWrapper>();  
  
    public ValueWrapper(String label, String pageName) {  
        if (label == null) {  
            throw new RuntimeException("ValueWrapper.label cannot be null.");  
        }  
  
        this.label = label;  
        this.pageName = pageName;  
  
    }  
  
      
    public ValueWrapper addChild(ValueWrapper child) {  
        children.add(child);  
        return this;  
    }  
  
    public ValueWrapper seek(String uuid) {  
        if (this.uuid.equals(uuid))  
            return this;  
  
        for (ValueWrapper child : children) {  
            ValueWrapper match = child.seek(uuid);  
  
            if (match != null)  
                return match;  
        }  
  
        return null;  
    }  
  
  
    public String getLabel() {  
        return label;  
    }  
  
    public void setLabel(String label) {  
        this.label = label;  
    }  
  
    public String getPageName() {  
        return pageName;  
    }  
  
    public void setPageName(String pageName) {  
        this.pageName = pageName;  
    }  
  
    public boolean isLeaf(){  
        return children.isEmpty();  
    }  
      
    @Override  
    public String toString(){  
        StringBuilder sb = new StringBuilder("ValueWrapper:\n");  
        sb.append("  label = " + getLabel() + ", pageName=" + getPageName());  
        sb.append("  childern=" + children.size());  
          
        return sb.toString();  
    }  
}  