import java.util.ArrayList;
import java.util.List;
/**
 * This class represent a line in the table of NodeTable class 
 * @author Yair Ivgi
 */
public class Row {
    private List<Vnode> vnodes;
    private double propbility;

    public Row(){
	vnodes = new ArrayList<Vnode>();
	propbility = -1;
    }

    public void addVnode(Vnode vn){vnodes.add(vn);}

    public List<Vnode> getVnodes() {return vnodes;}

    public double getPropbility() {return propbility;}

    public void setPropbility(double propbility) {this.propbility = propbility;}

    public void setVnodes(List<Vnode> vnodes){ this.vnodes = vnodes;}

    public void removeVnode(Vnode v){
	vnodes.remove(v);
    }
}


