package com.qdw.lpnet;

public class PathInt {
	Integer i;
	public PathInt(int i) {
		// TODO Auto-generated constructor stub
		this.i = i;
	}
	public PathInt(Integer i) {
		// TODO Auto-generated constructor stub
		this.i = i;
	}
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o instanceof PathInt) {  
			PathInt question = (PathInt) o;  
			return this.i.equals(question.i);  
	        }  
		return super.equals(o);  		
//		return super.equals(obj);
	}
	
	public static PathInt[] toPI(int[] a) {
		PathInt[] pi = new PathInt[a.length];
		for(int i=0;i<a.length;i++) {
			pi[i] = new PathInt(a[i]);
		}
		return pi;
	}
	public static PathInt[] toPI(Integer[] a) {
		PathInt[] pi = new PathInt[a.length];
		for(int i=0;i<a.length;i++) {
			pi[i] = new PathInt(a[i]);
		}
		return pi;
	}
	
	public static Integer[] toI(PathInt[] a) {
		Integer[] pi = new Integer[a.length];
		for(int i=0;i<a.length;i++) {
			pi[i] = a[i].i;
		}
		return pi;
	}
	
}
