import java.util.ArrayList;
import java.util.List;

public class NodeTable {

    private List<String> nodesName;
    private List<Row> rows;   
    private boolean isHandled;
    private boolean isHidden;
    
    public NodeTable(){
	rows = new ArrayList<Row>();
	nodesName = new ArrayList<String>();
	isHandled = false;
	isHidden = false;
    }
    
    public void addNodeName(String name){
	nodesName.add(name);
    }
    
    public List<Row> getRows() {
        return rows;
    }
    
    public void addRow(Row row){
	rows.add(row);
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public boolean isHandled() {
        return isHandled;
    }

    public void setHandled(boolean isHandled) {
        this.isHandled = isHandled;
    }

    public void print() {
	for(String name : nodesName){
	    System.out.print(name+", ");
	}
	if(isHidden){
	    System.out.print("  Hidden");
	}
	System.out.println();
	for(Row row :rows){
	    row.print();
	}
	System.out.println("------------------------------------");
    }

}
