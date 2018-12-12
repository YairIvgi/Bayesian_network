
/** 
 *  This class represent the node by the node name and value  
 * @author Yair Ivgi
 */
public class Vnode {

	private Node node;
	private String value;
	
	Vnode(Node node,String value) {
		this.node = node;
		this.value =value;
	}

	public Node getNode() {
		return node;
	}

	public String getValue() {
		return value;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	@Override
	public String toString() {
		return "Vnode [node=" + node.getName() + ", value=" + value + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Vnode)){
			return false;
		}
		Vnode v =(Vnode) o;
		if(v.getValue().equals(getValue()) && v.getNode().getName().equals(getNode().getName())){
			return true;
		}
		return false;
	}
	
}
