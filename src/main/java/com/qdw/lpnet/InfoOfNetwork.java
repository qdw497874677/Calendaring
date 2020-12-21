package com.qdw.lpnet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InfoOfNetwork {
	Double[][] matrix_topology = {};
	//List<Link> links = new ArrayList<Link>();
	Map<String, Link> linkmap;
	List<Link> links;
	List<Path> paths = new ArrayList<Path>();
	Map<String, List<Path>> pathmap;
	List<String> link_key = new ArrayList<String>();
	int time;
	
	
	public InfoOfNetwork(Double[][] matrix,int time) {
		
		//List<Double[][]> temofnetwork = new ArrayList<Double[][]>();
		
		matrix_topology = matrix;
		if(matrix == null) {
			System.out.println("!!!!!!!matrix == null");
		}
		printMatrix_topology();
		linkmap = new HashMap<String, Link>();
		links = new ArrayList<Link>();
		
		//添加网络中的链路
		int flag=0;
		for(int i=0;i<matrix_topology.length-1;i++) {
			for(int j = i+1;j<matrix_topology.length;j++) {
				if(matrix_topology[i][j] != 0) {
					//links.add(new Link(flag, matrix_topology[i][j], 1,i,j));
					Link link = new Link(flag,matrix_topology[i][j], 1,i,j,time);
					linkmap.put(String.valueOf(i)+"-"+String.valueOf(j),link);//1为默认的成本
					links.add(link);
					flag++;
				}
				 
			}
		}
//		for(String key:linkmap.keySet()) {
//			link_key.add(key);
//		}
	}

	public void updateLinks(){
		for (Link link : links) {
			link.capacity = matrix_topology[link.nodeA][link.nodeB];
		}
	}
	
	/*
	 * 
	 * 给网络设置路径
	 * 
	 */
	public void setPaths(String StringPath) {
		pathmap = new HashMap<String, List<Path>>();
		String sp[] = StringPath.split(",");
		String p[];
		
		for(int i=0;i<sp.length;i++) {
			p = sp[i].split("-");
			String t1 = p[0] +"-"+ p[p.length-1];
			String t2 = p[p.length-1] +"-"+ p[0];
			if(pathmap.containsKey(t1) || pathmap.containsKey(t2)) {
				if(pathmap.containsKey(t1)) {
					pathmap.get(t1).add(new Path(sp[i],t1+"#"+String.valueOf(pathmap.get(t1).size()+1),this));
				}else {
					pathmap.get(t2).add(new Path(sp[i],t1+"#"+String.valueOf(pathmap.get(t1).size()+1),this));
				}
				
			}else {
				pathmap.put(t1, new ArrayList<Path>());
				pathmap.get(t1).add(new Path(sp[i],t1+"#"+String.valueOf(1),this));
			}
		}
		
	}
	
	/*
	 * 获取所有链路
	 * 返回值类型 List<Link>
	 */
	public List<Link> getLink_all() {
		
		
		return links;
		
	}
	
	
	public Link getLink(String link) {
		
		
		return linkmap.get(link);
		
	}
	
	public void printPaths() {
		for(String key:pathmap.keySet()) {
			for(Path e:pathmap.get(key)) {
				System.out.println("路径id:"+e.id+"  路径："+e.path);
				System.out.print("包含的链路：");
				for(String key_l:e.links.keySet()) {
					System.out.print(e.links.get(key_l).link+" ");
				}
				System.out.println();
			}
			
		}
	}
	
	public void printLinks() {
		for(String key:linkmap.keySet()) {
			System.out.println("链路id:"+linkmap.get(key).linkid+" 链路："+linkmap.get(key).link+"  链路容量："+linkmap.get(key).capacity);
			System.out.print("包含的路径：");
			for(Path e:linkmap.get(key).overlappath) {
				System.out.print(e.path+" | ");
			}
			System.out.println();
		}
	}
	
	
	
	
	public String printMatrix_topology() {
		DecimalFormat df=new DecimalFormat("0.00");
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<matrix_topology.length;i++) {
			for(int j=0;j<matrix_topology[0].length;j++) {
				sb.append(df.format(matrix_topology[i][j])+"  ");
			}
			sb.append("\r\n");
			
		}
		return sb.toString();
	}
}
