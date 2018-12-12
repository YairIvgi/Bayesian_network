import java.util.ArrayList;
import java.util.Comparator;
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
	private List<String> joinedNodes;

	public Node(String name){
		this.name=name;
		parents =new ArrayList<Node>();
		values =new ArrayList<String>();
		cpt =new ArrayList<CPT>();
		joinedNodes = new ArrayList<String>();
	}

	public void addJoinedNode(String name){
		joinedNodes.add(name);
	}
	
	public List<String> getJoinedNodes() {
		return joinedNodes;
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

	public void print(){
		System.out.println(name);
		System.out.print("Values: ");
		for(String s : values){
			System.out.print(s+",");
		}
		System.out.println();
		System.out.print("Parents: ");
		for(Node s : parents){
			System.out.print(s.getName()+",");
		}
		System.out.println();
		System.out.println("CPT: ");
		for(CPT c : cpt){
			c.printCpt();
			System.out.println("\n");
		}
	}
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

	void eliminate() {
		// TODO Auto-generated method stub
		
	}
}

/**
 *compare alphabetically two Nodes
 *@author Yair Ivgi
 */

class alphabeticalComparator implements Comparator<Node>{
	@Override
	public int compare(Node o1, Node o2) {
		return o1.getName().compareTo(o2.getName());
	}
}