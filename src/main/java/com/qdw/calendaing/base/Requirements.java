package com.qdw.calendaing.base;

import com.qdw.calendaing.base.config.PathConfig;
import com.qdw.calendaing.base.config.RequirementConfig;
import com.qdw.calendaing.base.constant.FlowStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sun.nio.ch.Net;

import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Requirements implements Cloneable {
	@Data
	static public class 									Requirement implements Cloneable{
		private int id;
		private Node sNode;
		private Node dNode;
		// 可以开始传输的时隙
		private int readySlot;
		// 最晚传输的时隙
		private int deadline;
		// 需要传输的总数据量
		private double demand;
		// 已经满足的数据量
		private double meetDemand;
		// key为时隙
		private Map<Integer,List<Flow>> flowsOfR;
		// 流数量
		private int numOfFlows = 0;
		// 是否接收
		private boolean isAccpted;
		// 优先级
		private double priority;

		public Requirement(int id, Node sNode, Node dNode, int readySlot, int deadline, double demand) {
			this.id = id;
			this.sNode = sNode;
			this.dNode = dNode;
			this.readySlot = readySlot;
			this.deadline = deadline;
			this.demand = demand;
			this.flowsOfR = new HashMap<>();
//			priority = demand/(deadline-readySlot);
//			priority = demand;
			priority = updatePriority(readySlot);
		}

		public boolean containSlot(int timeSlot){
			return (timeSlot>=readySlot && timeSlot<=deadline);
		}

		public double updatePriority(int curTimeSlot){
//			priority =
//					(demand - meetDemand)*(readySlot-curTimeSlot+1) // 未完成数据量
//					/
//					(double) ((deadline-readySlot+1)) // 总时隙数/剩余时隙数
//			;


			// 成功率优先
			// 时间越早，满足的数据量越大，优先级越高
//			priority = 0 - (demand - meetDemand) / (double)(deadline - curTimeSlot + 1);
//			priority = - (demand - meetDemand) ;
//			priority = - (demand - meetDemand) / ( (double)(deadline - readySlot + 1)/(curTimeSlot - readySlot + 1) ) ;
//			priority = - (demand - meetDemand) / (1 + (double)(deadline - readySlot + 1)/(curTimeSlot - readySlot + 1) ) ;
			priority = - (demand - meetDemand) / (1 + (double)(deadline - curTimeSlot + 1)/(deadline - readySlot + 1) ) ;

			// 吞吐量优先
			// 时间越久，满足的数据量越小，优先级越高
//			priority = (demand - meetDemand) / (double)(deadline - curTimeSlot + 1);
//			priority = (double)(curTimeSlot - readySlot + 1);
//			priority = (demand - meetDemand) / (double)(curTimeSlot - readySlot + 1);


			System.out.println("pri:"+priority);
			return priority;
		}

		public Flow addFlow(int timeSlot, FlowStatus flowStatus, Path path){
			return addFlow(timeSlot,flowStatus,path,0.0);
		}

		public Flow addFlow(int timeSlot, FlowStatus flowStatus, Path path,double value){
			Flow flow = new Flow(timeSlot, path, this, flowStatus,value);
			if (flowsOfR.containsKey(timeSlot)){
				flowsOfR.get(timeSlot).add(flow);
			}else {
				List<Flow> list = new LinkedList<>();
				list.add(flow);
				flowsOfR.put(timeSlot,list);
			}
			numOfFlows++;
			// 更新满足的要求
			meetDemand += value;
			return flow;
		}

		public void addDummyFlow(int timeSlot){
			Flow flow = new Flow(timeSlot, null, this, FlowStatus.XUNI,0.0);
			if (flowsOfR.containsKey(timeSlot)){
				flowsOfR.get(timeSlot).add(flow);
			}else {
				List<Flow> list = new LinkedList<>();
				list.add(flow);
				flowsOfR.put(timeSlot,list);
			}
			numOfFlows++;
		}

		public void addDemand(double value){
			meetDemand += value;
		}

		@Override
		public Requirement clone(){

			Requirement clone = null;
			try {
				clone = (Requirement) super.clone();
				clone.setMeetDemand(0);
				clone.setFlowsOfR(new LinkedHashMap<>());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}

			return clone;
		}


	}
	// 最早开始时隙
	private int earliestSlot;
	// 最晚结束时隙
	private int latestSlot;
	//每个请求的流最大数量
	int maxNumOfFlow;
	//所有流的数量
	int numOfFlows=0;
	//所在周期
	int period;
	private int ids = 1;
	private List<Requirement> requirements = new LinkedList<>();
	RequirementConfig requirementConfig;
	//List<String> path = new ArrayList<String>();
	//Map<String, Double> path = new HashMap<String, Double>();
//	FlowOfR flowOfR;
//	List<Path> paths;//当前时隙里，这个需求所需要的路径集合
//	List<List<Path>> paths_step;
//	List<FlowOfR> flowsOfR;//当前时隙当前请求的流
//	List<List<FlowOfR>> flowsOfR_allstep;//所有时隙的请求列表
//	List<Requirements> list_requirement;//所有请求
//	List<FlowOfR> flowsOfR_all;//当前时隙所有请求的所有流
//	List<List<FlowOfR>> flowsOfR_all_step;
//	NetworkTest nw;
	//List<List<FlowOfR>> flowsOfR
	//String path[][];

	public Requirements(RequirementConfig requirementConfig){
		this.requirementConfig = requirementConfig;
		earliestSlot = requirementConfig.getEarliestSlot();
		latestSlot = requirementConfig.getLatestSlot();

	}

	public Requirement addR(Network network, int sId, int dId, int readySlot, int deadline, double demand){
		Requirement requirement = new Requirement(ids++, network.getNode(sId), network.getNode(dId), readySlot, deadline, demand);
		requirements.add(requirement);
		return requirement;
	}

	public void initializeRs(Network network, RequirementProducer requirementProducer){
		int numOfR = requirementConfig.getNumOfR();
		for (int i = 0; i < numOfR; i++) {
			requirements.add(requirementProducer.getOneR(requirementConfig,network));
		}
	}


	// 根据网络初始化请求的流
	public void initializeFlows(PathConfig pathConfig, Network network){
		int maxNum = pathConfig.getMaxNum();
		int numOfR = requirementConfig.getNumOfR();

		// 添加流信息
		for (Requirement oneR : requirements) {
			int l = oneR.getReadySlot();
			int r = oneR.getDeadline();

			// 每个时隙添加对应数量的流
			for (; l <= r; l++) {
//				System.out.println("S:"+oneR.getSNode().getId()+" D:"+oneR.getDNode().getId());
				List<Path> pathsC = network.getPath(oneR.getSNode().getId(), oneR.getDNode().getId());

				for (int j = 0; j < maxNum && j<pathsC.size(); j++) {
					oneR.addFlow(l,FlowStatus.ZHENGCHANG,pathsC.get(j));
				}
			}

			// 在最后一个时隙上添加虚拟流
			oneR.addDummyFlow(r);
			maxNumOfFlow = Math.max(maxNumOfFlow,oneR.getNumOfFlows());
			numOfFlows += oneR.getNumOfFlows();
		}

	}

	public int getFlowsOfAll(){
		int sum = 0;
		for (Requirement requirement : requirements) {
			sum += requirement.getNumOfFlows();
		}
		return sum;
	}

	@Override
	public Requirements clone(){
		Requirements clone = null;
		try {
			clone = (Requirements) super.clone();
			clone.setRequirements(requirements.stream().map(Requirement::clone).collect(Collectors.toList()));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return clone;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		if (requirements.isEmpty()){
			sb.append("没有请求");
		}else {
			for (Requirement requirement : requirements) {
				sb.append(requirement.toString()).append("\n");
			}
		}
		return sb.toString();
	}







}
