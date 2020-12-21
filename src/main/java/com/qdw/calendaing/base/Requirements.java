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
		// ���Կ�ʼ�����ʱ϶
		private int readySlot;
		// �������ʱ϶
		private int deadline;
		// ��Ҫ�������������
		private double demand;
		// �Ѿ������������
		private double meetDemand;
		// keyΪʱ϶
		private Map<Integer,List<Flow>> flowsOfR;
		// ������
		private int numOfFlows = 0;
		// �Ƿ����
		private boolean isAccpted;
		// ���ȼ�
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
//					(demand - meetDemand)*(readySlot-curTimeSlot+1) // δ���������
//					/
//					(double) ((deadline-readySlot+1)) // ��ʱ϶��/ʣ��ʱ϶��
//			;


			// �ɹ�������
			// ʱ��Խ�磬�����������Խ�����ȼ�Խ��
//			priority = 0 - (demand - meetDemand) / (double)(deadline - curTimeSlot + 1);
//			priority = - (demand - meetDemand) ;
//			priority = - (demand - meetDemand) / ( (double)(deadline - readySlot + 1)/(curTimeSlot - readySlot + 1) ) ;
//			priority = - (demand - meetDemand) / (1 + (double)(deadline - readySlot + 1)/(curTimeSlot - readySlot + 1) ) ;
			priority = - (demand - meetDemand) / (1 + (double)(deadline - curTimeSlot + 1)/(deadline - readySlot + 1) ) ;

			// ����������
			// ʱ��Խ�ã������������ԽС�����ȼ�Խ��
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
			// ���������Ҫ��
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
	// ���翪ʼʱ϶
	private int earliestSlot;
	// �������ʱ϶
	private int latestSlot;
	//ÿ����������������
	int maxNumOfFlow;
	//������������
	int numOfFlows=0;
	//��������
	int period;
	private int ids = 1;
	private List<Requirement> requirements = new LinkedList<>();
	RequirementConfig requirementConfig;
	//List<String> path = new ArrayList<String>();
	//Map<String, Double> path = new HashMap<String, Double>();
//	FlowOfR flowOfR;
//	List<Path> paths;//��ǰʱ϶������������Ҫ��·������
//	List<List<Path>> paths_step;
//	List<FlowOfR> flowsOfR;//��ǰʱ϶��ǰ�������
//	List<List<FlowOfR>> flowsOfR_allstep;//����ʱ϶�������б�
//	List<Requirements> list_requirement;//��������
//	List<FlowOfR> flowsOfR_all;//��ǰʱ϶���������������
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


	// ���������ʼ���������
	public void initializeFlows(PathConfig pathConfig, Network network){
		int maxNum = pathConfig.getMaxNum();
		int numOfR = requirementConfig.getNumOfR();

		// �������Ϣ
		for (Requirement oneR : requirements) {
			int l = oneR.getReadySlot();
			int r = oneR.getDeadline();

			// ÿ��ʱ϶��Ӷ�Ӧ��������
			for (; l <= r; l++) {
//				System.out.println("S:"+oneR.getSNode().getId()+" D:"+oneR.getDNode().getId());
				List<Path> pathsC = network.getPath(oneR.getSNode().getId(), oneR.getDNode().getId());

				for (int j = 0; j < maxNum && j<pathsC.size(); j++) {
					oneR.addFlow(l,FlowStatus.ZHENGCHANG,pathsC.get(j));
				}
			}

			// �����һ��ʱ϶�����������
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
			sb.append("û������");
		}else {
			for (Requirement requirement : requirements) {
				sb.append(requirement.toString()).append("\n");
			}
		}
		return sb.toString();
	}







}
