
public class ex1 {

	public static void main(String[] args) {

		Network test = new Network();
		try {
			test.readFile("input.txt");
			test.executeQueries();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
