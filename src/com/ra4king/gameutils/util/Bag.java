package com.ra4king.gameutils.util;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Bag<T> extends ArrayList<T> {
	private static final long serialVersionUID = 8096168672390044428L;
	
	public boolean add(T t) {
		checkIfNull(t);
		modCount--;
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
		return super.set(idx,null);
	}
	
	public boolean remove(Object o) {
		if(o == null) {
			int idx = indexOf(o);
			if(idx == -1)
				return false;
			super.set(idx, get(size()-1));
			super.remove(size()-1);
			return true;
		}
		
		int idx = indexOf(o);
		if(idx == -1)
			return false;
		
		remove(idx);
		
		return remove(o);
	}
	
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private int pos, knownMod = modCount;
			
			public boolean hasNext() {
				boolean hn = false;
				
				for(int a = pos; a < size(); a++) {
					if(get(a) != null) {
						hn = true;
						break;
					}
				}
				
				if(!hn)
					clean();
				
				return hn;
			}
			
			public T next() {
				checkForCoMod();
				
				if(!hasNext())
					throw new NoSuchElementException("reached the end");
				
				T t = null;
				while(hasNext() && (t = get(pos++)) == null);
				
				if(t == null)
					throw new NoSuchElementException("reached the end");
				
				return t;
			}
			
			public void remove() {
				checkForCoMod();
				
				Bag.this.remove(--pos);
				knownMod = modCount;
			}
			
			private void checkForCoMod() {
				if(knownMod != modCount)
					throw new ConcurrentModificationException();
			}
		};
	}
	
	private void clean() {
		while(remove(null));
	}
	
	private void checkIfNull(Object o) {
		if(o == null)
			throw new NullPointerException("Object cannot be null.");
	}
}
