package ramsey;

import java.io.Serializable;
import java.util.HashSet;

/**
 * A list of flipped edges that prevents revisiting graphs in a given search.
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 */
public class TabooList implements Serializable {

	private static final long serialVersionUID = -6356707817281392834L;
	
	private HashSet<Integer> set;
	
	public TabooList() {
		set = new HashSet<>();
	}

	public void add(int i, int j){
		set.add(hash(i,j));
	}

	public boolean contains(int i, int j){
		return set.contains(hash(i,j));
	}
	
	public int size(){
		return set.size();
	}
	
	protected int hash(int i, int j){
		return (i << 16) | (j & 0XFFFF);
	}

}
