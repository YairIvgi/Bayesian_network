public class ex1 {

    public static void main(String[] args) {

	Network test = new Network();
	try {
	    test.readFile("input1.txt");
	    test.executeQueries();

	    test.writeFile();
	} catch (Exception e) {
	  System.err.println("Error: "+e.getMessage());
	}
    }
}
