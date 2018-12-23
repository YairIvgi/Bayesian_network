import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *This class executes the three algorithms of the bayesian network.
 *@author Yair Ivgi
 */
public class Algorithms{

    private  List<Node> nodes;

    public Algorithms(List<Node> nodes){
	this.nodes = nodes;
    }

    /**
     * this is the first algorithm
     * @author Yair Ivgi
     * @return double
     * @throws Exception
     */
    public Result calculate1(Query q) throws Exception {
	double v1 = 0,v2 = 0;
	Result result =new Result();
	List<Vnode> evidence =new ArrayList<Vnode>();
	evidence = q.getEvidence();
	v1 = probabilistic(q.getSubject(),evidence,result);
	List<String> values =q.getSubject().getNode().getValues();
	if(values.size() > 1){
	    for(String val : values){
		if(!val.equals(q.getSubject().getValue())){
		    Vnode vn =new Vnode(q.getSubject().getNode(),val);
		    v2 += probabilistic(vn,evidence,result);
		}
	    }
	}
	result.setPropability(v1 *(1/(v2+v1)));
	result.setAddition(result.getAddition()+1);
	return result;
    }

    /**
     * this is the second algorithm
     * @author Yair Ivgi
     * @return double
     */
    public Result calculate2(Query q) {
	Result result =new Result();
	List<Node> qNodes =new ArrayList<Node>();	//all the nodes
	List<Vnode> qRelevant =q.getEvidence();		//all the query nodes
	qNodes.addAll(nodes);						
	qRelevant.add(q.getSubject());				//add the subject to the list of nodes that appear in the query
	qNodes = Node.removeNotRelaventNodes(qRelevant);	//remove all the nodes that are not relevant to the query 		
	Util.setHiddenNodes(qNodes,qRelevant);	 //set all the hidden nodes of the query
	Collections.sort(qNodes,new alphabeticalComparator());  	//sort all the hidden Nodes
	List<NodeTable> tables = new ArrayList<NodeTable>();

	for(Node node : qNodes){
	    NodeTable table =new NodeTable();
	    tables.add(table);
	    buildTable(table,node);
	    table.setHidden(node.isHidden());
	    table.print();
	}	    
	System.out.println("+++++++++++++++++++++++++++++++++++");


		boolean isFinished = false;
		while(!isFinished){
		    isFinished = true;
		    for(NodeTable nt : tables){
			if(nt.isHidden() && !nt.isHandled()){
			    JoinNodesAndEliminate(tables, nt);
			    nt.setHandled(true);
			    isFinished = false;
			    break;
			}
		    }
		}
		//TODO write the method
		//combineTables(tables);
	return result;
    }

    /**
     * This method joins all the appearances of the hidden node in the network by the V.E algorithm
     * @author Yair Ivgi
     * @return void
     */
    private void JoinNodesAndEliminate(List<NodeTable> tables, NodeTable nt) {
	List<NodeTable> outTables = new ArrayList<NodeTable>();
	List<NodeTable> relTables = new ArrayList<NodeTable>();
	for(NodeTable table : tables){
	    //TODO write the function
	    if(!isRelavantTable(table,nt)){
		outTables.add(table);
	    }else{
		relTables.add(table);
	    }
	}
	NodeTable combTable = new NodeTable();
	outTables.add(combTable);

	//TODO build combTable

	//TODO write eliminate
	tables.clear();
	tables.addAll(outTables);
    }

    private boolean isRelavantTable(NodeTable table, NodeTable nt) {
	// TODO Auto-generated method stub
	return false;
    }

    private void buildTable(NodeTable table, Node node) {
	table.addNodeName(node.getName());
	if(!node.getParents().isEmpty()){
	    for(Node parent: node.getParents()){
		table.addNodeName(parent.getName());
	    }
	}
	for(CPT cpt: node.getCPT()){
	    for(ValueP vp: cpt.getValueP()){
		Row row = new Row();
		if(!cpt.getDependenc().isEmpty()){
		    for(Vnode vn :cpt.getDependenc()){
			row.addVnode(vn);
		    }
		}
		row.addVnode(new Vnode(node, vp.getValue()));
		row.setPropbility(vp.getProp());
		table.addRow(row);
	    }
	}
    }

    /**
     * this is the third algorithm
     * @author Yair Ivgi
     * @return double
     * @throws Exception
     */
    public Result calculate3(Query q) {
	Result result =new Result();
	return result;
    }

    /**
     * this method checks what are the nodes that are not in the query and calculate the probabilistic
     * @param vnode
     * @param list
     * @author Yair Ivgi
     * @return double 
     * @throws Exception 
     */
    private double probabilistic(Vnode subject,List<Vnode> list,Result result) throws Exception{
	int multiplication = 0,addition = 0;
	List<Vnode> dependence =new ArrayList<Vnode>();
	dependence.addAll(list);
	dependence.add(subject);   //add the subject to the list 
	List<Node> notInQuery = Util.hiddenNodes(nodes,dependence);
	List<List<Vnode>> permutation = new ArrayList<List<Vnode>>();
	permutation = Util.cartesianProduct(notInQuery);  //the Cartesian product of all the Vnodes 
	double pr=0;
	if(permutation.isEmpty()){
	    permutation.add(dependence);
	}else{
	    for(List<Vnode>  vnode_list : permutation){
		vnode_list.addAll(dependence);		//now we have the complete list of all the permutation 
	    }
	}
	for(List<Vnode> vnode_list : permutation){
	    double val = 1;
	    for(Vnode current_vnode : vnode_list){
		List<Vnode> evidence = Util.getParentFromList(current_vnode, vnode_list);
		double temp= Util.getPropability(current_vnode, evidence);
		if(val == 1){
		    val = temp;
		}else{
		    val *= temp;	
		    multiplication++;
		}
	    }
	    if(pr == 0){
		pr = val;
	    }else{
		pr+=val;
		addition++;	
	    }
	}
	result.setAddition(result.getAddition()+addition);
	result.setMultiplication(result.getMultiplication()+multiplication);
	return pr;
    }

    /**
     *class that implements comparator and compare alphabetically list of nodes
     *@author Yair Ivgi
     */

    class alphabeticalComparator implements Comparator<Node>{
	@Override
	public int compare(Node o1, Node o2) {
	    return o1.getName().compareTo(o2.getName());
	}
    }
}


