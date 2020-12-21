package com.qdw.lpnet;

import java.util.HashMap;
import java.util.Map;

public class Path {
	String id;
	String path;
	int source_node;
	int destination_node;
	//List<FlowOfR> flowsOfR;
	//double b;
	//List<Link> links ;
	Map<String, Link> links;
	double cost;
	boolean special;
	
	
	public Path(String p, String id, InfoOfNetwork infonw) {
		cost = 0;
		path = p;
		//this.b = this.b;
		this.id = id;
		source_node = Integer.parseInt(p.split("-")[0]);
		destination_node = Integer.parseInt(p.split("-")[p.split("-").length-1]);
		if(source_node == destination_node) {
			special = true;
		}else{
			special = false;
		}
		
		//覆盖的链路
		//links = new ArrayList<>();
		links = new HashMap();
		String pa[] = path.split("-");
		for(int i=1;i<pa.length;i++) {
			
			if(infonw.linkmap.containsKey(pa[i-1]+"-"+pa[i])) {
				links.put(pa[i-1]+"-"+pa[i],infonw.linkmap.get(pa[i-1]+"-"+pa[i]));
				infonw.linkmap.get(pa[i-1]+"-"+pa[i]).addPath(this);
			}else if(infonw.linkmap.containsKey(pa[i]+"-"+pa[i-1])) {
				links.put(pa[i]+"-"+pa[i-1],infonw.linkmap.get(pa[i]+"-"+pa[i-1]));
				infonw.linkmap.get(pa[i]+"-"+pa[i-1]).addPath(this);
			}
			
		}
	}
	
	public Path(String p, InfoOfNetwork infonw) {
		cost = 0;
		path = p;
		//this.b = this.b;
		
		source_node = Integer.parseInt(p.split("-")[0]);
		destination_node = Integer.parseInt(p.split("-")[p.split("-").length-1]);
		links = new HashMap();
		String pa[] = path.split("-");
		for(int i=1;i<pa.length;i++) {
			
			if(infonw.linkmap.containsKey(pa[i-1]+"-"+pa[i])) {
				links.put(pa[i-1]+"-"+pa[i],infonw.linkmap.get(pa[i-1]+"-"+pa[i]));
				infonw.linkmap.get(pa[i-1]+"-"+pa[i]).addPath(this);
			}else if(infonw.linkmap.containsKey(pa[i]+"-"+pa[i-1])) {
				links.put(pa[i]+"-"+pa[i-1],infonw.linkmap.get(pa[i]+"-"+pa[i-1]));
				infonw.linkmap.get(pa[i]+"-"+pa[i-1]).addPath(this);
			}
			
		}
		
	}
	
	public double getCost() {

		if(source_node == destination_node) {//虚拟路径的成本
			return 1000;
		}
		double total_cost = 0;
		for(String key_l:links.keySet()) {
			total_cost += links.get(key_l).cost;
		}
		cost = total_cost;
		return cost;
	}
	
	public double getCost(int i) {

		
		double total_cost = 0;
		if(special == true) {
			cost = 10000 + i;
			return cost;
		}
		for(String key_l:links.keySet()) {
			total_cost += links.get(key_l).cost;
		}
		
		
		cost = total_cost;
		return total_cost;
	}
	
	public double setCost(double c) {
		cost = c;
		return cost;
	}
	
//	public void setPath() {
//		String p[] = path.split("-");
//		for(int i=1;i<p.length;i++) {
//			
//		}
//	}
	
//	public void addflow(FlowOfR flow) {
//		flowsOfR.add(flow);
//	}
	//路径中是否覆盖指定链路
	public boolean isCovered(String link) {
		String a = link.split("-")[0];
		String b = link.split("-")[1];
		String p[] = path.split("-");
		for(int i=0;i<path.split("-").length-1;i++) {
			if((p[i].equals(a)&&p[i+1].equals(b))||(p[i].equals(b)&&p[i+1].equals(a))) {
				return true;
			}
		}
		return false;
	}
	
//	public boolean isCovered(Link link) {
//		String a = String.valueOf(link.nodeA);
//		String b = String.valueOf(link.nodeB);
//		String p[] = path.split("-");
//		for(int i=0;i<path.split("-").length-1;i++) {
//			if((p[i].equals(a)&&p[i+1].equals(b))||(p[i].equals(b)&&p[i+1].equals(a))) {
//				return true;
//			}
//		}
//		return false;
//	}
	
	public boolean isCovered(Link link) {
		String a = String.valueOf(link.nodeA);
		String b = String.valueOf(link.nodeB);
		String p[] = path.split("-");
		for(int i=0;i<path.split("-").length-1;i++) {
			if(links.containsKey(a+"-"+b) || links.containsKey(b+"-"+a)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "Path [id=" + id + ", path=" + path 
				  + ", cost=" + cost + ", special=" + special + "]";
	}
	
	
}
