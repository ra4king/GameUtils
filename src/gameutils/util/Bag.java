package gameutils.util;

import java.util.ArrayList;

public class Bag<T> extends ArrayList<T> {
	private static final long serialVersionUID = 8096168672390044428L;
	
	public boolean add(T t) {
		checkIfNull(t);
		return super.add(t);
	}
	
	public void add(int idx, T t) {
		checkIfNull(t);
		super.add(idx,t);
	}
	
	public T set(int idx, T t) {
		checkIfNull(t);
		return super.set(idx,t);
	}
	
	public T remove(int idx) {
		checkRange(idx);
		
		return super.set(idx,null);
	}
	
	public boolean remove(Object o) {
		if(o == null)
			return super.remove(o);
		
		int idx = indexOf(o);
		if(idx == -1)
			return false;
		
		remove(idx);
		
		return true;
	}
	
	private void checkRange(int idx) {
		if(idx < 0 || idx >= size())
			throw new IndexOutOfBoundsException("Index: " + idx + ", Size: " + size());
	}
	
	private void checkIfNull(Object o) {
		if(o == null)
			throw new NullPointerException("Object cannot be null.");
	}
}