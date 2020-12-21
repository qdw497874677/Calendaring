package com.qdw.lpnet;

import java.util.ArrayList;
import java.util.List;


public class Link {
	int linkid;
	int nodeA;
	int nodeB;
	String link;
	double capacity;
	double residual_c;
	double cost;
	List<FlowOfR> overlapflow;
	List<Path> overlappath;
	int time;
	
	public Link(int id,double ca,double co,int na,int nb,int time) {
		linkid = id;
		this.time = time;
		capacity = ca;
		cost = co;
		nodeA = na;
		nodeB = nb;
		link = String.valueOf(na)+"-"+String.valueOf(nb);
		
		overlapflow = new ArrayList<FlowOfR>();
		overlappath = new ArrayList<Path>();
	}
	
	public void addFlow(FlowOfR flow) {
		
		overlapflow.add(flow);
	}
	/*
	 * 向链路添加 包含这条链路的路径
	 */
	public void addPath(Path path) {
		
		overlappath.add(path);
	}
	
	/*
	 * 设置链路的剩余带宽
	 */
	public void setRe(double rc) {
		residual_c = rc;
	}

	@Override
	public String toString() {
		return "Link [linkid=" + linkid + ", nodeA=" + nodeA + ", nodeB=" + nodeB + ", link=" + link + ", capacity="
				+ capacity + ", cost=" + cost + "]\r\n";
	}
	
	
}
