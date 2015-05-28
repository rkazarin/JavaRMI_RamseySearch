package ramsey;

import java.util.HashSet;

import api.SharedState;

public class SharedTabooList implements SharedState {

	private static final long serialVersionUID = -6356707817281392834L;
	
	private HashSet<Integer> set;
	
	public SharedTabooList() {
		set = new HashSet<>();
	}

	@Override
	public SharedState update(SharedState newState) {
		set.addAll( ((SharedTabooList)newState).set);
		return this;
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
