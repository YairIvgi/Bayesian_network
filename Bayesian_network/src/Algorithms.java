import java.util.ArrayList;
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
//		double v1 = 0,v2 = 0;
//		Result result =new Result();
//		List<Vnode> evidence =new ArrayList<Vnode>();
//		evidence = q.getEvidence();
//		v1 = probabilistic(q.getSubject(),evidence,result);
//		List<String> values =q.getSubject().getNode().getValues();
//		if(values.size() > 1){
//			for(String val : values){
//				if(!val.equals(q.getSubject().getValue())){
//					Vnode vn =new Vnode(q.getSubject().getNode(),val);
//					v2 += probabilistic(vn,evidence,result);
//				}
//			}
//		}
//		result.setPropability(v1 *(1/(v2+v1)));
//		result.setAddition(result.getAddition()+1);
//		return result;
		return null;
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
		
//		List<Node> hidden =new ArrayList<Node>();		
//		hidden.addAll(Util.hiddenNodes(qNodes,qRelevant));	 //get all the hidden nodes of the query
//		Collections.sort(hidden,new alphabeticalComparator());  	//sort all the hidden Nodes
//		qNodes.removeAll(hidden);
//		for(Node hid : hidden){
//			//		hid.print();
//			JoinNodes(hid,qNodes);			//join all the nodes that has the hid node in the their evidence
//			qNodes.add(hid);
//			hidden.remove(hid);
//		}
		return result;
	}

	/**
	 * This method joins all the appearances of the hidden node in the network by the V.E algorithm
	 * @author Yair Ivgi
	 * @return void
	 */
	
	private void JoinNodes(Node hid,List<Node> qNodes){
		List<Node> joinedNodes = new ArrayList<Node>();
		for(Node node : qNodes){
			for(Node parent : node.getParents()){
				if(parent.getName().equals(hid.getName())){
					joinedNodes.add(parent);
				}
			}
		}
		qNodes.removeAll(joinedNodes);
		if(joinedNodes.size() > 0){
			hid = CPT.joinCpt(joinedNodes,hid);
		}
		hid.eliminate();				//eliminate the hid variable
	}

	/**
	 * this is the third algorithm
	 * @author Yair Ivgi
	 * @return double
	 * @throws Exception
	 */

	public Result calculate3(Query q) {
		return null;
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

}
