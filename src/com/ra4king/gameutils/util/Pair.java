package com.ra4king.gameutils.util;

/**
 * @author Roi Atalla
 */
public class Pair<A, B> {
	public A first;
	private B second;
	
	public Pair() {}
	
	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}
}
