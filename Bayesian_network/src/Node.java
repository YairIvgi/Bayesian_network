import java.util.ArrayList;
import java.util.List;

/**
 *This class represent a general node in a bayesian network.
 *@author Yair Ivgi
 */

public class Node{
    private String name;
    private List<Node> parents;	
    private List<String> values;
    private List<CPT> cpt;
    private boolean isHidden;

    public void setHidden(boolean isHidden) {
	this.isHidden = isHidden;
    }

    public boolean isHidden() {
	return isHidden;
    }

    public Node(String name){
	this.name=name;
	parents =new ArrayList<Node>();
	values =new ArrayList<String>();
	cpt =new ArrayList<CPT>();
    }

    public Node(Node node){
	this.name=node.name;
	this.isHidden = node.isHidden;
	parents =new ArrayList<Node>();
	for(Node n: node.getParents()){
	    parents.add(new Node(n));
	}
	values =new ArrayList<String>();
	for(String s : node.getValues()){
	    values.add(new String(s));
	}
	cpt =new ArrayList<CPT>();
	for(CPT cpt : node.getCPT()){
	    this.cpt.add(new CPT(cpt));
	}
    }

    public List<Node> getParents(){ return parents;} 

    public List<String> getValues(){ return values;}

    public List<CPT> getCPT(){ return cpt;}

    public String getName(){ return name;}

    public void addValues(String line){
	String val = line.replace("Values: ", "");
	String[] tempVals =val.split(",");
	for (int i = 0; i < tempVals.length; i++) {
	    values.add(tempVals[i].trim());
	}
    }

    public void addParent(Node node){
	parents.add(node);
    }

    public void addCPT(String line){
	CPT c = new CPT(values,parents);
	c.addCPT(line);
	cpt.add(c);
    }

    //    public void print(){
    //	System.out.println(name);
    //	System.out.print("Values: ");
    //	for(String s : values){
    //	    System.out.print(s+",");
    //	}
    //	System.out.println();
    //	System.out.print("Parents: ");
    //	for(Node s : parents){
    //	    System.out.print(s.getName()+",");
    //	}
    //	System.out.println();
    //	System.out.println("CPT: ");
    //	for(CPT c : cpt){
    //	    c.printCpt();
    //	    System.out.println("\n");
    //	}
    //    }

    public void print2(){
	System.out.println(name);
    }


    /**
     * This method checks if two Nodes are equal
     * @author Yair Ivgi
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
	if (!(o instanceof Node)){
	    return false;
	}
	Node n =(Node) o;
	if(n.getName().equals(getName())){
	    return true;
	}
	return false;
    }

    /**
     * This method removes the non relevant nodes to the specific query from the bayesian network   
     * @author Yair Ivgi
     * @return List<Node>
     */
    static List<Node> removeNotRelaventNodes(List<Vnode> query) {
	List<Node> qList = new ArrayList<Node>();
	List<Node> parents = new ArrayList<Node>();
	for(Vnode vn : query){
	    qList.add(vn.getNode());
	}
	for(Node n : qList){
	    List<Node> nodes = new ArrayList<Node>();
	    parents.addAll(n.recursive(nodes));
	}
	qList.addAll(parents);
	return getDistinctList(qList);
    }

    /**
     * This method recurse on the ancestors of a specific node and returns them   
     * @author Yair Ivgi
     * @return List<Node>
     */
    private List<Node> recursive(List<Node> nodes){
	if(! (this.getParents().size()==0)){
	    for(Node n : this.getParents()){
		nodes.add(n);
		n.recursive(nodes);
	    }
	}
	return nodes;
    }

    /**
     * This method returns a distinct list from a list of nodes    
     * @author Yair Ivgi
     * @return List<Node>
     */
    private static List<Node> getDistinctList(List<Node> list){
	List<Node> listDistinct = new ArrayList<>();
	for(Node i : list){
	    if( !listDistinct.contains(i)){
		listDistinct.add(i); }
	}
	return listDistinct;
    }
}