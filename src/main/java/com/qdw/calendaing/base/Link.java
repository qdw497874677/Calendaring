package com.qdw.calendaing.base;


import com.qdw.calendaing.base.pathBase.Path;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;

@Data
public class Link {
	@Data
	@AllArgsConstructor
	public static class LinkInfo{
		int timeSlot;
		double capacity;
		double residualCapacity;
		double cost;
	}

	private String id;
	private Node nodeA;
	private Node nodeB;
	private Map<Integer,LinkInfo> linkInfoMap = new LinkedHashMap<>();
	private List<Flow> overlapFlows = new LinkedList<>();
	private Set<Path> overlapPaths = new HashSet<>();

	public Link(Node nodeA, Node nodeB){
		this.id = getLinkId(nodeA.getId(),nodeB.getId());
		this.nodeA = nodeA;
		this.nodeB = nodeB;
	}

	public void addOverlapFlow(Flow flow) {
		overlapFlows.add(flow);
	}

	public void initializeInfo(int slotSize,double capacity,double cost){
		for (int i = 0; i < slotSize; i++) {
			linkInfoMap.put(i, new LinkInfo(i, capacity, capacity, cost));
		}
	}

	public double decrease(double value,int timeSlot){
		LinkInfo linkInfo = linkInfoMap.get(timeSlot);
		linkInfo.residualCapacity -= value;
		return linkInfo.residualCapacity;
	}

	// 添加路过的路径
	public void addPath(Path path){
		overlapPaths.add(path);
	}

	private String getLinkId(int s,int d){
		if (s>d){
			int temp = s;
			s = d;
			d = temp;
		}
		return s+"-"+d;
	}


	@Override
	public String toString() {
		LinkedList<String> list = new LinkedList<>();
		for (Integer timeSlot : linkInfoMap.keySet()) {
			BigDecimal bigDecimal = new BigDecimal(linkInfoMap.get(timeSlot).getResidualCapacity());
			list.add(bigDecimal.setScale(2,BigDecimal.ROUND_FLOOR).toString());
		}
		return "Link { id=" + id + ",["+String.join(",",list)+"] }";
	}

	
	
}
