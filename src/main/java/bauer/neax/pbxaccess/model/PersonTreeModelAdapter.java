package bauer.neax.pbxaccess.model;

import java.util.List;

import org.apache.tapestry5.tree.TreeModelAdapter;





public class PersonTreeModelAdapter implements TreeModelAdapter<PersonNode> {

	public boolean isLeaf(PersonNode node) {
		return node.isLeaf();
	}

	public boolean hasChildren(PersonNode node) {
		return node.hasChildren();
	}

	public List<PersonNode> getChildren(PersonNode node) {
		return node.getChildren();
	}

	public String getLabel(PersonNode node) {
		return node.getParent().getCn();
	}

}
