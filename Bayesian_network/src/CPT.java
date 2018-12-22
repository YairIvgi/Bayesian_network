import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CPT {
    private List<Vnode> dependenc;
    private List<ValueP> valueP;
    private List<Node> parents;
    private List<String> values;


    public CPT(List<String> values,List<Node> parents){
	this.parents = parents;
	this.values = values;
	valueP = new ArrayList<ValueP>();
	dependenc = new ArrayList<Vnode>();
    }

    public void addCPT(String line){
	List<String> tempCPT = Arrays.asList(line.split(","));
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
	while(itrString.hasNext()){
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
		DecimalFormat df = new DecimalFormat("##.00000");
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

    public void printCpt(){
	for(Vnode s: dependenc){
	    System.out.print(s.getNode().getName()+" "+s.getValue()+" ");
	}
	System.out.println();
	for(ValueP vp: valueP){
	    System.out.println(vp.getValue()+" "+vp.getProp()+" ");
	}
    }

    /**
     * @author Yair Ivgi 
     * This function returns the probability of the value regarding the CPT table.
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

//    /**
//     * This method join all the CPT tables of the Nodes in the list and then with the hidden Node
//     * @author Yair Ivgi
//     * @return Node
//     */
}