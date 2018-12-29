import java.util.ArrayList;
import java.util.List;

public class NodeTable {

    private String nodeName;
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    private List<String> evidence;
    private List<Row> rows;   
    private boolean isHandled;
    private boolean isHidden;
    private List<String> hiddens;

    public NodeTable(){
	rows = new ArrayList<Row>();
	evidence = new ArrayList<String>();
	hiddens = new ArrayList<String>();
	isHandled = false;
	isHidden = false;
    }

   public List<String> getHiddens() {
        return hiddens;
    }

    public void setHiddens(List<String> hiddens) {
        this.hiddens = hiddens;
    }

public void addNodeEvidence(String name){evidence.add(name);}

    public void addNodeName(String name){nodeName = name;}

    public List<String> getEvidence(){return evidence;}
    
    public void removeEvidence(String name){evidence.remove(name);}

    public String getNodeName(){return nodeName;}

    public List<Row> getRows(){return rows;}

    public void addRow(Row row){rows.add(row);}
    
    public void setRows(List<Row> rows){ this.rows = rows;};

    public boolean isHidden(){return isHidden;}

    public void setHidden(boolean isHidden){this.isHidden = isHidden;}

    public boolean isHandled(){return isHandled;}

    public void setHandled(boolean isHandled){this.isHandled = isHandled;}

    public void print() {
	System.out.print(nodeName+"| ");
	for(String name : evidence){
	    System.out.print(name+", ");
	}
	if(isHidden){
	    System.out.print("  Hidden");
	}
	if(isHandled){
	    System.out.print(   "isHandled");
	}
	System.out.println();
	for(Row row :rows){
	    row.print();
	}
	System.out.println("------------------------------------");
    }

    public void normalize(Result result) {
	double sum=0;
	for(Row row : rows){
	    sum+=row.getPropbility();
	    result.setAddition(result.getAddition()+1);
	}
	for(Row row : rows){
	    row.setPropbility(row.getPropbility()/sum);
	    result.setMultiplication(result.getMultiplication()+1);
	}
	
    }
}
