import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
/**
 * This class represent the CPT values of a node in a bayesian network 
 * @author Yair Ivgi
 *
 */
public class CPT {
    private List<Vnode> dependenc;
    private List<ValueP> valueP;
    private List<Node> parents;
    private List<String> values;

    /**
     * constructor
     * @param values
     * @param parents
     */
    public CPT(List<String> values,List<Node> parents){
	this.parents = parents;
	this.values = values;
	valueP = new ArrayList<ValueP>();
	dependenc = new ArrayList<Vnode>();
    }

    /**
     * CPT copy constructor 
     * @param cpt
     */
    public CPT(CPT cpt){
	this.parents = cpt.parents;
	this.values = cpt.values;
	valueP = new ArrayList<ValueP>();
	for(ValueP vp : cpt.getValueP()){
	    valueP.add(new ValueP(vp));
	}
	dependenc = new ArrayList<Vnode>();
	for(Vnode vn : cpt.getDependenc()){
	    dependenc.add(new Vnode(vn));
	}
    }

    /**
     * This method adds CPT line to the Node CPT list
     * @author Yair Ivgi
     */
    public void addCPT(String line){
	List<String> tempCPT = Arrays.asList(line.split(","));	//split the parents with ','
	Iterator<String> itrString = tempCPT.iterator();
	Iterator<Node> itrNode =parents.iterator();
	String word = null;
	while(itrString.hasNext()){
	    word = itrString.next();
	    if(word.startsWith("=")){
		break;
	    }
	    if(!itrNode.hasNext()){
		return; 
	    }
	    dependenc.add(new Vnode(itrNode.next(), word));
	}
	while(itrString.hasNext()){		//Phrase to value and probability using '='
	    if(word.startsWith("=")){
		String val =word.replace("=", "");
		word = itrString.next();
		double prop = Double.parseDouble(word);
		valueP.add(new ValueP(val, prop));
		if(itrString.hasNext()){
		    word = itrString.next();
		}
	    }
	}
	for(String val : values){	//add the valueP that is not in the original CPT table
	    double adverse =0;
	    boolean found = false;
	    for(ValueP vp : getValueP()){
		if(vp.getValue().equals(val)){
		    found = true;
		    break;
		}else{
		    adverse += vp.getProp();
		}
	    }
	    if(!found){
		DecimalFormat df = new DecimalFormat("##.00000");	//change the probability to the required format
		valueP.add(new ValueP(val, Double.valueOf(df.format(1-adverse))));
	    }
	}
    }

    public List<Vnode> getDependenc() {
	return dependenc;
    }

    public List<ValueP> getValueP() {
	return valueP;
    }

    /**
     * This function returns the probability of the value regarding the CPT table.
     * @author Yair Ivgi 
     */

    static double getCptProp(CPT cpt,String value){	
	double result=0;
	for(ValueP vp : cpt.getValueP()){
	    if(vp.getValue().equals(value)){
		result = vp.getProp();
		break;	
	    }
	}
	return result;
    }
}