import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Network {

	private  Map<String,Node> nodesMap;
	private  List<Node> nodes;
	private  List<Query>  queries;

	public Network(){
		nodes = new ArrayList<Node>();
		queries = new ArrayList<Query>();
		nodesMap = new HashMap<String,Node>();
	}
	
	public  void readFile(String fileName) throws Exception{
		File file  =new File(fileName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			if(!line.startsWith("Network")){
				br.close();
				throw new Exception("Text is not in the correct format\n");
			}
			line = br.readLine();
			line=line.replace("Variables: ", "");
			String tempVar[] = line.split(",");
			for (int i = 0; i < tempVar.length; i++) {
				String name = tempVar[i];
				Node node =new Node(name);
				nodesMap.put(name, node);
				nodes.add(node);
			}
			while(!line.startsWith("Queries")){
				line=br.readLine();
				if(line.startsWith("Var ")){
					String name=line.replace("Var ", "");
					Node node = nodesMap.get(name); 
					if(node == null){
						br.close();
						throw new Exception("Text is not in the correct format\n");
					}
					line=br.readLine();
					node.addValues(line);
					line=br.readLine();
					if(line.startsWith("Parents")){
						String par =  line.replace("Parents: ", "");
						if(!par.startsWith("none")){
							String[] tempParent = par.split(",");
							for(String t:tempParent){
								Node n =nodesMap.get(t);
								if(n == null){
									br.close();
									throw new Exception("The parent is not in the Variables list\n");
								}
								node.addParent(n);	
							}
						}
					}
					line=br.readLine();
					if(line.startsWith("CPT")){
						line =br.readLine();
						while(!line.isEmpty()){
							node.addCPT(line);
							line =br.readLine();
						}
					}
				}
			}
			if(line.startsWith("Queries")){
				line=br.readLine();
				while(line!= null){
					Query query =new Query();
					query.addValue(line, nodesMap);
					queries.add(query);
					line=br.readLine();
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		}

	}

	public void executeQueries() throws Exception{	
		Algorithms a =new Algorithms(nodes);
		for (int i = 0; i < queries.size(); i++) {
			switch (queries.get(i).getAlgoType()) {
				case 1:
					System.out.println("result = "+a.calculate1(queries.get(i)));
					continue;
				case 2:
	//				a.calculate2(queries.get(i));
					continue;
				case 3:
					a.calculate3(queries.get(i));
					continue;
				default:
					continue;
			}
		}
	}
	
	public static void writeFile(){
		
	}

	public  void print(){
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).print();
			System.out.println();
		}
		System.out.println("Queries: ");
		for (int i = 0; i < queries.size(); i++) {
			queries.get(i).print();
			System.out.println();
		}
	}
}
