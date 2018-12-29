import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *This class represent a Query for the bayesian network.
 *@author Yair Ivgi
 */
public class Query {
    private Integer algoType;
    private Vnode subject;
    private List<Vnode> evidence;

    public Query(){
	evidence = new ArrayList<Vnode>();
    }

    /**
     * Read the query from a line 
     * @param line
     * @param nodesMap
     */
    public void addValue(String line,Map<String,Node> nodesMap){
	String[] tuple = line.split("\\|");	//split in | 
	if(tuple.length != 2){
	    return;
	}
	String left = tuple[0].replace("P(", "");
	String[] leftV = left.split("=");
	if(leftV.length != 2){
	    return;
	}
	Node node = nodesMap.get(leftV[0]);
	if(node == null){
	    return;
	}
	subject =new Vnode(node,leftV[1]);

	String[] rightV = tuple[1].split(",");
	for(String right :rightV){
	    if(right.contains("=")){
		String[] rr =right.split("=");
		if(rr.length != 2){
		    continue;
		}
		if(rr[1].contains(")")){
		    rr[1] = rr[1].replace(")", "");
		}
		Node nn = nodesMap.get(rr[0]);
		if(nn != null){
		    Vnode vNode =new Vnode(nn,rr[1]);
		    evidence.add(vNode);
		}
	    }else{
		algoType =Integer.parseInt(right);
	    }
	}
    }

    public Integer getAlgoType() {
	return algoType;
    }

    public Vnode getSubject() {
	return subject;
    }

    public List<Vnode> getEvidence() {
	return evidence;
    }
}
