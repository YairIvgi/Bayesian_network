import java.text.DecimalFormat;
import java.text.NumberFormat;
/**
 * This class represent the Result of the algorithms
 * @author Yair Ivgi
 *
 */
public class Result {

	private int multiplication;
	private int addition;
	private double propability;
	
	public void setMultiplication(int multiplication) {
		this.multiplication = multiplication;
	}
	
	public void setAddition(int addition) {
		this.addition = addition;
	}
	
	public void setPropability(double propability) {
		this.propability = propability;
	}

	public int getMultiplication() {
		return multiplication;
	}

	public int getAddition() {
		return addition;
	}

	@Override
	public String toString() {
		NumberFormat formatter = new DecimalFormat("#0.00000");
		propability = propability *1.00000;
		return  formatter.format(propability)+","+addition+","+multiplication;
	}	
}
