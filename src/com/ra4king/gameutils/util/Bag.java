package com.ra4king.gameutils.util;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Bag<T> extends ArrayList<T> {
	private static final long serialVersionUID = 8096168672390044428L;
	
	private volatile int iteratorCount;
	
	@Override
	public boolean add(T t) {
		checkIfNull(t);
		modCount--;
		return super.add(t);
	}
	
	@Override
	public void add(int idx, T t) {
		checkIfNull(t);
		super.add(idx,t);
	}
	
	@Override
	public T set(int idx, T t) {
		checkIfNull(t);
		return super.set(idx,t);
	}
	
	@Override
	public T remove(int idx) {
		return super.set(idx,null);
	}
	
	@Override
	public boolean remove(Object o) {
		if(o == null) {
			int idx = indexOf(null);
			if(idx == -1)
				return false;
			super.set(idx, get(size()-1));
			super.remove(size()-1);
			return true;
		}
		
		int idx;
		boolean found = false;
		
		while((idx = indexOf(o)) != -1) {
			remove(idx);
			found = true;
		}
		
		return found;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private int pos, knownMod = modCount;
			
			{
				iteratorCount++;
			}
			
			@Override
			public boolean hasNext() {
				boolean hn = false;
				
				for(int a = pos; a < size(); a++) {
					if(get(a) != null) {
						hn = true;
						break;
					}
				}
				
				if(!hn) {
					iteratorCount--;
					if(iteratorCount == 0)
						clean();
				}
				
				return hn;
			}
			
			private T nextItem() {
				checkForCoMod();
				
				if(!hasNext())
					throw new NoSuchElementException("reached the end");
				
				T t = null;
				while((t = get(pos++)) == null);
				
				return t;
			}
			
			@Override
			public T next() {
				T t = nextItem();
				if(t == null)
					System.out.println("IT'S NULL!!!");
				return t;
			}
			
			@Override
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
	
	public void clean() {
		while(remove(null));
	}
	
	private void checkIfNull(Object o) {
		if(o == null)
			throw new NullPointerException("Object cannot be null.");
	}
}
