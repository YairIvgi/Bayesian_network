/** 
 *  This class represent the node by the node name and it`s probability 
 * @author Yair Ivgi
 */
public class ValueP {

    private String value;
    private double propbility;

    public ValueP(String value,double propbility){
	this.value = value;
	this.propbility =propbility;
    }

    public ValueP(ValueP vp) {
	this.value = vp.value;
	this.propbility =vp.propbility;
    }

    public String getValue() {
	return value;
    }

    public double getProp() {
	return propbility;
    }


}

