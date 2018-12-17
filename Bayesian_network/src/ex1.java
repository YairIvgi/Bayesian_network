public class ex1 {

	public static void main(String[] args) {

		Network test = new Network();
		try {
			test.readFile("test_files/input1.txt");
			test.executeQueries();
			test.writeFile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
