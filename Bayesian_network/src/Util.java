import java.util.ArrayList;
import java.util.List;

/** 
 * This class is a static Util class for a bayesian network
 * @author Yair Ivgi
 * @throws exeptions
 */

public class Util {

	/** 
	 * This function returns the probability of a node and it`s value by the evidence.
	 * @author Yair Ivgi
	 * @throws exeptions
	 */

	public static double getPropability(Vnode subject, List<Vnode> evidence) throws Exception{
		double res = 0;
		if(evidence == null){			//in case that the subject has no parents
			if(subject.getNode().getCPT().size() != 1){
				throw new Exception("CPT is not in the currect size");  
			}
			CPT relaventCpt = subject.getNode().getCPT().get(0);
			String value = subject.getValue();
			res = CPT.getCptProp(relaventCpt,value);
		} else {
			CPT relaventCpt = null;
			for(CPT cpt: subject.getNode().getCPT()){
				if(Util.listEqualsIgnoreOrder(cpt.getDependenc(), evidence)){
					relaventCpt = cpt;
					break;
				} 
			}
			if(relaventCpt == null){
				throw new Exception("evidence not found in the subject CPT table"); 
			}
			res = CPT.getCptProp(relaventCpt, subject.getValue());
		}
		return res;
	}

	

	/** 
	 * This function returns a list of Vnode all the permutation of the nodes and values.
	 * @author Yair Ivgi
	 */

	static List<List<Vnode>> cartesianProduct(List<Node> list){
		List<List<Vnode>> product =  new ArrayList<List<Vnode>>();
		if(list.isEmpty()){
			return product;
		}
		for(Node n : list){
			List<Vnode> vn = new ArrayList<Vnode>();  //list of list of a Vnode with all the permutation of the node and it`s values.
			for(String value : n.getValues()){
				vn.add(new Vnode(n, value));
			}
			product.add(vn);
		}
		List<List<Vnode>> result = new ArrayList<List<Vnode>>();
		result = calculate(product);
		return result;
	}

	/** 
	 * recursive sub function for the Cartesian product
	 * @author Yair Ivgi
	 */

	public static  List<List<Vnode>> calculate(List<List<Vnode>> input) {
		List<List<Vnode>> res = new ArrayList<>();
		if (input.isEmpty()) { // if no more elements to process
			res.add(new ArrayList<>()); // then add empty list and return
			return res;
		} else {
			process(input, res); // we need to calculate the Cartesian product of input and store it in res variable
		}
		return res; 
	}


	/** 
	 * This function returns a list of Vnode all the permutation of the nodes and values.
	 * @author Yair Ivgi
	 */

	private static  void process(List<List<Vnode>> lists, List<List<Vnode>> res) {
		List<Vnode> head = lists.get(0); //take first element of the list
		List<List<Vnode>> tail = calculate(lists.subList(1, lists.size())); //invoke calculate on remaining element, here is recursion

		for (Vnode h : head) { // for each head
			for (List<Vnode> t : tail) { //iterate over the tail
				List<Vnode> tmp = new ArrayList<>(t.size());
				tmp.add(h); // add the head
				tmp.addAll(t); // and current tail element
				res.add(tmp);
			}
		}
	}

	/** 
	 * @author Yair Ivgi
	 * This function checks if the node is in the list.
	 */

	static boolean isExist(Node node,List<Vnode> list){
		for(Vnode v : list){
			if(v.getNode().equals(node)){
				return true;
			}
		}
		return false;
	}

	/** 
	 * This function checks if the lists contain the same elements.
	 * @author Yair Ivgi
	 */

	static boolean listEqualsIgnoreOrder(List<Vnode> list1, List<Vnode> list2) {
		if(list1.size()!= list2.size()){
			return false;
		}
		for (int i = 0; i < list1.size(); i++) {
			Vnode v1=list1.get(i);
			boolean isEqual =false;
			for(Vnode v2 :list2){
				if(v1.equals(v2)){
					isEqual =true;
					break;
				}
			}
			if(!isEqual){
				return false;
			}
		}
		return true;
	}

	/** 
	 * return all the parents with their values .. from Node to Vnode
	 * @author Yair Ivgi
	 */

	public static List<Vnode> getParentFromList(Vnode vnode,List<Vnode> vnode_list){
		List<Vnode> parents_val =new ArrayList<Vnode>();
		List<Node> parents = vnode.getNode().getParents();
		if(parents.isEmpty()){
			return null;
		}
		for(Node n : parents){
			for(Vnode vn : vnode_list){
				if(vn.getNode().equals(n)){
					parents_val.add(vn);
					break;
				}
			}
		}
		return parents_val;	
	}

	/** 
	 * return all the hidden Nodes of the query
	 * @author Yair Ivgi
	 */
	public static List<Node> hiddenNodes(List<Node> nodes,List<Vnode> dependence){
		List<Node> notInQuery =new ArrayList<Node>();
		for(Node node: nodes){
			if(! Util.isExist(node, dependence)){		//add all the nodes that are not in the query
				notInQuery.add(node);
			}
		}
		return notInQuery;
	}

}


