import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *This class executes the three algorithms of the bayesian network.
 *@author Yair Ivgi
 */
public class Algorithms{

    private  List<Node> nodes;

    public Algorithms(List<Node> nodes){
	this.nodes = nodes;
    }

    /**
     * This is the first algorithm
     * @return double
     * @throws Exception
     */
    public Result calculate1(Query q) throws Exception {
	double v1 = 0,v2 = 0;
	Result result =new Result();
	List<Vnode> evidence =new ArrayList<Vnode>();
	evidence = q.getEvidence();
	v1 = probabilistic(q.getSubject(),evidence,result);
	List<String> values =q.getSubject().getNode().getValues();
	if(values.size() > 1){
	    for(String val : values){
		if(!val.equals(q.getSubject().getValue())){
		    Vnode vn =new Vnode(q.getSubject().getNode(),val);
		    v2 += probabilistic(vn,evidence,result);
		}
	    }
	}
	result.setPropability(v1 *(1/(v2+v1)));
	result.setAddition(result.getAddition()+1);
	return result;
    }

    /**
     * This is the second algorithm
     * @return double
     */
    public Result calculate2(Query q) {
	Result result =new Result();
	List<Node> qNodes =new ArrayList<Node>();	//all the nodes
	List<Vnode> qRelevant =new ArrayList<Vnode>();		//all the query nodes
	qRelevant.addAll(q.getEvidence());
	qRelevant.add(q.getSubject());	//add the subject to the list of nodes that appear in the query				
	for(Node node : Node.removeNotRelaventNodes(qRelevant)){	//remove all the nodes that are not relevant to the query
	    qNodes.add(new Node(node));
	}
	Util.setHiddenNodes(qNodes,qRelevant);	 //set all the hidden nodes of the query
	Collections.sort(qNodes,new alphabeticalComparator());  	//sort all the hidden Nodes
	List<NodeTable> tables = new ArrayList<NodeTable>();
	for(Node node : qNodes){
	    NodeTable table =new NodeTable();
	    tables.add(table);
	    buildTable(table,node,q);		//turns all the nodes to tables, its more comfortable to calculate 
	    table.setHidden(node.isHidden());
	}
	boolean isFinished = false;
	while(!isFinished){
	    isFinished = true;
	    for(NodeTable nt : tables){		//go on the list of tables and sum all the hidden tables appearances 
		if(nt.isHidden()){		   
		    NodeTable combTable = JoinTableList(tables, nt,result,q);
		    eliminate(combTable,nt.getNodeName());				
		    tables.add(combTable);
		    nt.setHandled(true);
		    isFinished = false;
		    break;
		}
	    }
	}
	NodeTable res =new NodeTable();
	if(tables.size()==1){		//if we finished to eliminate all the tables beside the one of the query  
	    res = tables.get(0);
	}else{				// if there are still several  tables but there are no more hidden tables
	    NodeTable combTable =new NodeTable();
	    while(true){
		for(NodeTable nt : tables){	
		    Collections.sort(tables,new factorSize());
		    combTable = JoinTableList(tables, nt,result,q);
		    if(combTable==null){
			break;						
		    }
		    if(combTable.getEvidence().size()>2){		
			eliminate(combTable,nt.getNodeName());
		    }
		    tables.add(combTable);

		    break;
		}
		if(tables.size()==1){		//if we finished to eliminate all the tables beside the one of the query  
		    res = tables.get(0);
		    break;
		}
	    }
	}
	res.normalize(result);
	for(Row row : res.getRows()){
	    if(row.getVnodes().contains(q.getSubject())){
		result.setPropability(row.getPropbility());
	    }
	}
	return result;
    }


    /**
     * This method joins all the appearances of the hidden node in the network by the V.E algorithm
     * @param query
     * @return void
     */
    private NodeTable JoinTableList(List<NodeTable> tables, NodeTable nt, Result result, Query q) {		
	List<NodeTable> outTables = new ArrayList<NodeTable>();
	List<NodeTable> relTables = new ArrayList<NodeTable>();
	for(NodeTable table : tables){
	    if(!table.isHandled()){
		if(!isRelevantTable(table,nt)){
		    if(!table.equals(nt)){
			outTables.add(table);
		    }
		}else{
		    relTables.add(table);
		}
	    }
	}
	if(relTables.size()==0){
	    tables.clear();
	    tables.addAll(outTables);
	    return null;
	}
	Collections.sort(relTables,new factorSize());  	//sort all the tables by the factor size
	NodeTable combTable = new NodeTable();
	boolean hiddenCombe =false;		
	List<String> hidNames =new ArrayList<String>();
	if(relTables.size() > 0){
	    combTable = relTables.get(0);
	    if(relTables.get(0).isHidden()){   		//checks if there are hidden in the reltable
		hiddenCombe = true;
		hidNames.add(relTables.get(0).getNodeName());
	    }
	    if(relTables.size() > 1){
		for(int i=1; i<relTables.size(); i++){
		    NodeTable t1 = relTables.get(i);
		    if(t1.isHidden()){   		//checks if there are hidden in the reltable
			hiddenCombe = true;
			hidNames.add(relTables.get(i).getNodeName());
		    }
		    combTable = joinTables(combTable,t1,result);		
		    String name =relTables.get(i).getNodeName();
		    if(name!=null && name.equals(q.getSubject().getNode().getName())){
			continue;
		    }
		    for(String redundant : notRelevant(combTable,nt,outTables,q)){
			eliminate(combTable,redundant);		//eliminate all the nodes that are not relevant anymore;
		    }
		}
	    }
	}
	combTable = joinTables(combTable, nt, result);	
	combTable.setHidden(hiddenCombe);			
	combTable.setHiddens(hidNames); 			
	if(hidNames.size()==1){
	    combTable.setNodeName(hidNames.get(0));		
	}
	tables.clear();
	tables.addAll(outTables);
	return combTable;
    }

    /**
     * Returns a list of all the non relevant tables 
     * @param combTable
     * @param nt
     * @param outTables
     * @param q
     * @return
     */
    private List<String> notRelevant(NodeTable combTable, NodeTable nt, List<NodeTable> outTables, Query q) {
	List<String> redundantVars = new ArrayList<String>();
	List<String> relevantVars =new ArrayList<String>();
	relevantVars.add(q.getSubject().getNode().getName());
	for(NodeTable nodeTable : outTables){
	    for(String s :nodeTable.getEvidence()){
		if(!relevantVars.contains(s)){
		    relevantVars.add(s);
		}
	    }
	    if(nodeTable.getNodeName()!=null){
		if(!relevantVars.contains(nodeTable.getNodeName())){
		    relevantVars.add(nodeTable.getNodeName());
		}
	    }
	}
	if(nt.getNodeName()!=null){
	    if(!relevantVars.contains(nt.getNodeName())){
		relevantVars.add(nt.getNodeName());
	    }
	}
	for(String s : combTable.getEvidence()){
	    if(!relevantVars.contains(s)){
		redundantVars.add(s);
	    }
	}
	return redundantVars;
    }

    /**
     * Canceling node impressions in the table
     * @param combTable
     * @param hiddenName
     */
    private void eliminate(NodeTable combTable, String hiddenName) {
	for(Row row : combTable.getRows()){
	    for(Vnode vn: row.getVnodes()){
		if(vn.getNode().getName().equals(hiddenName)){
		    row.removeVnode(vn);
		    break;
		}
	    }
	}
	List<Row> rows = new ArrayList<Row>();	
	boolean same;
	for(Row row1 : combTable.getRows()){
	    same =false;
	    if(rows.contains(row1)){
		continue;
	    }
	    for(Row row2 : rows){
		if(Util.listEqualsIgnoreOrder(row1.getVnodes(), row2.getVnodes())){
		    row2.setPropbility(row1.getPropbility()+row2.getPropbility());
		    same =true;
		}
	    }
	    if(!same){
		rows.add(row1);
	    }
	    combTable.setRows(rows);
	    combTable.removeEvidence(hiddenName);
	}
    }

    /**
     * Joins tow tables to generate a new table
     * @param table1
     * @param table2
     * @param result
     * @return NodeTable
     */
    private NodeTable joinTables(NodeTable table1, NodeTable table2, Result result) {
	List<String> resambleColumns =new ArrayList<String>();
	for(Vnode vn1:table1.getRows().get(0).getVnodes()){ 
	    for(Vnode vn2:table2.getRows().get(0).getVnodes()){
		if(vn1.getNode().getName().equals(vn2.getNode().getName())){
		    resambleColumns.add(vn1.getNode().getName());
		    break;
		}
	    }
	}
	NodeTable nodeTable = new NodeTable();
	for(Row combRow : table1.getRows()){
	    List<Row> rList = getRow(combRow,table2,resambleColumns);
	    if(rList.size() > 0){
		for(Row row : rList){
		    joinRow(nodeTable,combRow,row,resambleColumns,result);
		}
	    }
	}
	return nodeTable;
    }

    /**
     * Joins the corresponding rows in the table to combRow
     * @param nodeTable
     * @param combRow
     * @param row
     * @param resambleColumns
     * @param result
     */
    private void joinRow(NodeTable nodeTable, Row combRow, Row row, List<String> resambleColumns, Result result) {
	Row resRow = new Row();
	for(Vnode vn:combRow.getVnodes()){
	    resRow.addVnode(vn);
	    if(!nodeTable.getEvidence().contains(vn.getNode().getName())){
		nodeTable.addNodeEvidence(vn.getNode().getName());
	    }
	}
	for(Vnode vn:row.getVnodes()){
	    if(!resambleColumns.contains(vn.getNode().getName())){
		resRow.addVnode(vn);
		if(!nodeTable.getEvidence().contains(vn.getNode().getName())){
		    nodeTable.addNodeEvidence(vn.getNode().getName());
		}
	    }
	}
	resRow.setPropbility(combRow.getPropbility()*row.getPropbility());
	result.setMultiplication(result.getMultiplication()+1);
	nodeTable.addRow(resRow);
    }

    /**
     * Returns the corresponding rows from the table to the combRow
     * @param combRow
     * @param table
     * @param resambleColumns
     * @return	List<Row>
     */
    private List<Row> getRow(Row combRow, NodeTable table, List<String> resambleColumns) {
	List<Row> rows=new ArrayList<Row>();
	for(Row row : table.getRows()){
	    if(isMatch(row,combRow,resambleColumns)){
		rows.add(row);
	    }
	}
	return rows;
    }

    /**
     * Checks if the two rows are a match to create a new combined row
     * @param row
     * @param combRow
     * @param resambleColumns
     */
    private boolean isMatch(Row row, Row combRow, List<String> resambleColumns) {
	boolean isMatch = true;
	isMatch = false;
	for(Vnode vn1:row.getVnodes()){
	    for(Vnode vn2 : combRow.getVnodes()){
		if(resambleColumns.contains(vn1.getNode().getName())){
		    if(vn1.equals(vn2)){
			isMatch =true;
			break;
		    }
		    if(vn1.getNode().getName().equals(vn2.getNode().getName())){
			return false;
		    }
		}
	    }
	}
	return isMatch;
    }

    /**
     * Checks if the table of the nt is relevant to the calculation of the hidden nt table
     * @param table
     * @param nt
     * @return boolean
     */
    private boolean isRelevantTable(NodeTable table, NodeTable nt) {
	boolean res =false;
	if(table.equals(nt)){
	    return res;
	}
	if(table.getEvidence().contains(nt.getNodeName())){
	    res = true;
	}else{
	    for(String name : nt.getHiddens()){									
		if(table.getEvidence().contains(name)){
		    res = true;
		}
	    }
	}
	return res;
    }
    /**
     * This method turns the node into table for more comfortable calculation 
     * @param table
     * @param node
     * @param query
     */
    private void buildTable(NodeTable table, Node node, Query q) {
	boolean isbuildAll =false;
	String name =null; 
	if(node.getName().equals(q.getSubject().getNode().getName()) || node.isHidden()){
	    isbuildAll = true;
	}else{
	    for(Vnode vn :q.getEvidence()){
		if(vn.getNode().getName().equals(node.getName())){
		    name=vn.getValue();
		}
	    }
	}
	table.addNodeName(node.getName());
	for(Node parent: node.getParents()){
	    table.addNodeEvidence(parent.getName());
	}
	for(CPT cpt: node.getCPT()){
	    for(ValueP vp: cpt.getValueP()){
		boolean isAdd = isbuildAll;
		if(!isAdd){
		    if(vp.getValue().equals(name)){
			Row row = new Row();
			for(Vnode vn :cpt.getDependenc()){
			    row.addVnode(vn);
			}
			row.addVnode(new Vnode(node, name));
			row.setPropbility(vp.getProp());
			table.addRow(row);
		    }
		}
		if(isAdd){
		    Row row = new Row();
		    for(Vnode vn :cpt.getDependenc()){
			row.addVnode(vn);
		    }
		    row.addVnode(new Vnode(node, vp.getValue()));
		    row.setPropbility(vp.getProp());
		    table.addRow(row);
		}
	    }
	}
    }

    /**
     * This is the third algorithm
     * @return double
     * @throws Exception
     */
    public Result calculate3(Query q) {
	Result result =new Result();
	return result;
    }

    /**
     * This method checks what are the nodes that are not in the query and calculate the probabilistic
     * @return double 
     * @throws Exception 
     */
    private double probabilistic(Vnode subject,List<Vnode> list,Result result) throws Exception{
	int multiplication = 0,addition = 0;
	List<Vnode> dependence =new ArrayList<Vnode>();
	dependence.addAll(list);
	dependence.add(subject);   //add the subject to the list 
	List<Node> notInQuery = Util.hiddenNodes(nodes,dependence);
	List<List<Vnode>> permutation = new ArrayList<List<Vnode>>();
	permutation = Util.cartesianProduct(notInQuery);  //the Cartesian product of all the Vnodes 
	double pr=0;
	if(permutation.isEmpty()){
	    permutation.add(dependence);
	}else{
	    for(List<Vnode>  vnode_list : permutation){
		vnode_list.addAll(dependence);		//now we have the complete list of all the permutation 
	    }
	}
	for(List<Vnode> vnode_list : permutation){
	    double val = 1;
	    for(Vnode current_vnode : vnode_list){
		List<Vnode> evidence = Util.getParentFromList(current_vnode, vnode_list);
		double temp= Util.getPropability(current_vnode, evidence);
		if(val == 1){
		    val = temp;
		}else{
		    val *= temp;	
		    multiplication++;
		}
	    }
	    if(pr == 0){
		pr = val;
	    }else{
		pr+=val;
		addition++;	
	    }
	}
	result.setAddition(result.getAddition()+addition);
	result.setMultiplication(result.getMultiplication()+multiplication);
	return pr;
    }

    /**
     *class that implements comparator and compare alphabetically list of nodes
     */
    class alphabeticalComparator implements Comparator<Node>{
	@Override
	public int compare(Node o1, Node o2) {
	    return o1.getName().compareTo(o2.getName());
	}
    }

    /**
     * class that implements comparator and compare using the factor size of the table 
     */
    class factorSize implements Comparator<NodeTable>{
	@Override
	public int compare(NodeTable o1, NodeTable o2) {	   
	    return o1.getRows().size() - o2.getRows().size();
	}
    }
}


