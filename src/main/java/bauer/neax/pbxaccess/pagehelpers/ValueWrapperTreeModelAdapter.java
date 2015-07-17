package bauer.neax.pbxaccess.pagehelpers;

import java.util.List;  

import org.apache.tapestry5.tree.TreeModelAdapter;  
  
public class ValueWrapperTreeModelAdapter implements TreeModelAdapter<ValueWrapper>  
{  
    public boolean isLeaf(ValueWrapper value)  
    {  
        return value.children.isEmpty();  
    }  
  
    public boolean hasChildren(ValueWrapper value)  
    {  
        return !value.children.isEmpty();  
    }  
  
    public List<ValueWrapper> getChildren(ValueWrapper value)  
    {  
        return value.children;  
    }  
  
    public String getLabel(ValueWrapper value)  
    {  
        return value.getLabel();  
    }  
}  
  