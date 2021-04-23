package com.qdw.calendaing.base.requirement;

import com.alibaba.fastjson.JSONObject;
import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.NetContext;
import com.qdw.calendaing.base.Network;
import com.qdw.calendaing.base.Node;
import com.qdw.calendaing.base.config.PathConfig;
import com.qdw.calendaing.base.config.RequirementConfig;
import com.qdw.calendaing.base.constant.FlowStatus;
import com.qdw.calendaing.base.pathBase.Path;
import com.qdw.calendaing.base.requirementBase.priority.MaxCS_PM;
import com.qdw.calendaing.base.requirementBase.priority.PriorityModifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qdw49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Requirements implements Cloneable {
	@Data
	static public class Requirement implements Cloneable{
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
		private boolean isAccpted = false;
		// ���ȼ�
		private Double priority;
		// ���ƴ���
		private double maxBdw;
		// ���ȼ��޸���
		private PriorityModifier priorityModifier;
		// ����ʵ�ʵĽ�ֹʱ��
		private int realFinishSlot;
		// Requirements
		private Requirements mather;
		public Requirement(int id, Node sNode, Node dNode, int readySlot, int deadline, double demand) {
			this.id = id;
			this.sNode = sNode;
			this.dNode = dNode;
			this.readySlot = readySlot;
			this.deadline = deadline;
			this.demand = demand;
			this.flowsOfR = new LinkedHashMap<>();
//			priority = demand/(deadline-readySlot);
//			priority = demand;
			this.maxBdw = -1;
			this.priorityModifier = new MaxCS_PM();
//			priority = updatePriority(readySlot,priorityModifier);
		}
		public Requirement(int id, Node sNode, Node dNode, int readySlot, int deadline, double demand, int maxBdw) {
			this.id = id;
			this.sNode = sNode;
			this.dNode = dNode;
			this.readySlot = readySlot;
			this.deadline = deadline;
			this.demand = demand;
			this.flowsOfR = new LinkedHashMap<>();
//			priority = demand/(deadline-readySlot);
//			priority = demand;
			this.maxBdw = maxBdw;
			this.priorityModifier = new MaxCS_PM();
//			priority = updatePriority(readySlot,priorityModifier);
		}
		public Requirement(int id, Node sNode, Node dNode, int readySlot, int deadline, double demand,double maxBdw,PriorityModifier priorityModifier) {
			this.id = id;
			this.sNode = sNode;
			this.dNode = dNode;
			this.readySlot = readySlot;
			this.deadline = deadline;
			this.demand = demand;
			this.flowsOfR = new LinkedHashMap<>();
//			priority = demand/(deadline-readySlot);
//			priority = demand;
			this.priorityModifier = priorityModifier;
//			priority = updatePriority(readySlot,priorityModifier);
			this.maxBdw = maxBdw;
		}

		public List<Flow> getFlowsOfR(int slot){
			if (flowsOfR.containsKey(slot)){
				return flowsOfR.get(slot);
			}else {
				mather.addFlowsForOneSlot(this, slot);
				return flowsOfR.get(slot);
			}
	}

		public void setRealFinishSlot(int lastSlot){
			this.realFinishSlot = lastSlot;
		}
		public void setRealFinishSlot(){
			if (this.isAccpted){
				int max = 0;
				if (flowsOfR!=null){
					for (Integer key : flowsOfR.keySet()) {
						List<Flow> flows = flowsOfR.get(key);
						if (flows!=null || flows.size()==0){
							continue;
						}
						double temp = 0;
						for (Flow flow : flows) {
							if (flow.getStatus().equals(FlowStatus.XUNI)){
								continue;
							}
							temp += flow.getValue();
						}
						if (temp>0){
							max = Math.max(max, key);
						}
					}
				}
				this.realFinishSlot = max;
			}
		}

		public boolean containSlot(int timeSlot){
			return (timeSlot>=readySlot && timeSlot<=deadline);
		}

		public double updatePriority(int curTimeSlot, PriorityModifier priorityModifier){
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
//			priority = - (demand - meetDemand) / (1 + (double)(deadline - curTimeSlot + 1)/(deadline - readySlot + 1) ) ;

//			if (deadline == curTimeSlot){
//				priority += 100;
//				System.out.println("@#$#@@#$@#$@#$@#$#@@#$@#$");
//			}

			// ����������
			// ʱ��Խ�ã������������ԽС�����ȼ�Խ��
//			priority = (demand - meetDemand) / (double)(deadline - curTimeSlot + 1);
//			priority = (double)(curTimeSlot - readySlot + 1);
//			priority = (demand - meetDemand) / (double)(curTimeSlot - readySlot + 1);
//			priority = (demand - meetDemand) / (1 + (double)(deadline - curTimeSlot + 1)/(deadline - readySlot + 1) );


			priority = priorityModifier.updatePriority(curTimeSlot,this);

//			System.out.println("id:"+this.id+" slot:"+curTimeSlot+" pri:"+priority);
			return priority;
		}

		public Flow addFlow(int timeSlot, FlowStatus flowStatus, Path path){
			if (path==null){
				return null;
			}
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
			// ���������Ҫ��
			meetDemand += value;
			numOfFlows++;
			return flow;
		}

		public void addDummyFlow(int timeSlot){
			Flow flow = Flow.getXUNNIFlow(timeSlot,this);
			if (flowsOfR.containsKey(timeSlot)){
				flowsOfR.get(timeSlot).add(flow);
			}else {
				List<Flow> list = new LinkedList<>();
				list.add(flow);
				flowsOfR.put(timeSlot,list);
			}
		}

		public void addDemand(double value){
			meetDemand += value;
		}

		@Override
		public Requirement clone(){

			Requirement clone = null;
			try {
				clone = (Requirement) super.clone();
				// ��ʼ�������������
				clone.setMeetDemand(0);
				clone.setAccpted(false);
				clone.setFlowsOfR(new LinkedHashMap<>());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return clone;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Requirement that = (Requirement) o;
			return id == that.id &&
					readySlot == that.readySlot &&
					deadline == that.deadline &&
					Double.compare(that.demand, demand) == 0 &&
					Double.compare(that.meetDemand, meetDemand) == 0 &&
					Objects.equals(sNode, that.sNode) &&
					Objects.equals(dNode, that.dNode);
		}

		@Override
		public int hashCode() {
			return Objects.hash(id, sNode, dNode, readySlot, deadline, demand, meetDemand);
		}

		private String getNonzeroFlow(){
			StringBuilder res = new StringBuilder();
			for (List<Flow> flows : flowsOfR.values()) {
				if (flows.size()>0){
					res.append(" [").append(flows.get(0).getTimeSlot()).append("]");
				}
				res.append("{");
				for (Flow flow : flows) {
					if (flow.getStatus().equals(FlowStatus.ZHENGCHANG) && flow.getValue()>0.0){
						res.append(flow.getPath().getPathStr()).append(":").append(flow.getValue()).append(" ");
					}
				}
				res.append("}");
			}
			return res.toString();
		}

		@Override
		public String toString() {
			return "Requirement{" +
					"id=" + id +
					", sNode=" + sNode +
					", dNode=" + dNode +
					", readySlot=" + readySlot +
					", deadline=" + deadline +
					", readFinishSlot=" + realFinishSlot +
					", demand=" + demand +
					", meetDemand=" + meetDemand +
					", maxBdw=" + maxBdw +
					", numOfFlows=" + getNumOfFlows() +
					", nonZeroFlows=" + getNonzeroFlow() +
					", isAccpted=" + isAccpted +
					", priority=" + priority +

					'}';
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

	// ��ȡ���е���
	public List<Flow> getAllFlows(){
		List<Flow> flows = new LinkedList<>();
		for (Requirements.Requirement requirement : requirements) {
			for (List<Flow> flow : requirement.getFlowsOfR().values()) {
				flows.addAll(flow);
			}
		}
		return flows;
	}

	static public JSONObject getJson(int sNode,int dNode,int readySlot,int deadline,double demand,double maxBdw){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id",sNode);
		jsonObject.put("sNode",sNode);
		jsonObject.put("dNode",dNode);
		jsonObject.put("readySlot",readySlot);
		jsonObject.put("deadline",deadline);
		jsonObject.put("demand",demand);
		jsonObject.put("maxBdw",maxBdw);
		return jsonObject;
	}

	static public List<JSONObject> reqsToJson(Requirements requirements){
		List<JSONObject> list = new LinkedList<>();
		for (Requirement requirement : requirements.getRequirements()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id",requirement.getId());
			jsonObject.put("sNode",requirement.getSNode());
			jsonObject.put("dNode",requirement.getDNode());
			jsonObject.put("readySlot",requirement.getReadySlot());
			jsonObject.put("deadline",requirement.getDeadline());
			jsonObject.put("demand",requirement.getDemand());
			jsonObject.put("maxBdw",requirement.getMaxBdw());
			list.add(jsonObject);
		}
		return list;
	}

	static public Requirements createReqs(List<JSONObject> list, RequirementConfig requirementConfig, Network network){
		Requirements requirements = new Requirements(requirementConfig);
		for (JSONObject jsonObject : list) {
			jsonObject.get("");
			requirements.addR(network,
					(int)jsonObject.get("id"),
					(int)jsonObject.get("sNode"),
					(int)jsonObject.get("dNode"),
					(int)jsonObject.get("readySlot"),
					(int)jsonObject.get("deadline"),
					(double)jsonObject.get("demand"),
					(double)jsonObject.get("maxBdw"),
					requirementConfig);
		}
		return requirements;
	}

	public Requirement addR(Network network,int id,int sId, int dId, int readySlot, int deadline, double demand,double maxBdw,RequirementConfig requirementConfig){
		Requirement requirement = new Requirement(id, network.getNode(sId), network.getNode(dId), readySlot, deadline, demand,maxBdw,requirementConfig.getPriorityModifier());
		addRequirementToList(requirement);
		return requirement;
	}

	public Requirement addR(Network network, int sId, int dId, int readySlot, int deadline, double demand,double maxBdw,RequirementConfig requirementConfig){
		Requirement requirement = new Requirement(ids++, network.getNode(sId), network.getNode(dId), readySlot, deadline, demand,maxBdw,requirementConfig.getPriorityModifier());
		addRequirementToList(requirement);
		return requirement;
	}

	public void initializeRs(Network network){
		int numOfR = requirementConfig.getNumOfR();
		for (int i = 0; i < numOfR; i++) {
			addRequirementToList(requirementConfig.getRequirementProducer().getOneR(requirementConfig,network));

		}

//		System.out.println();
	}

	public void addRequirementToList(Requirement requirement){
		requirement.setMather(this);
		requirements.add(requirement);

	}


	// ���������ʼ���������
	public void initializeFlows(PathConfig pathConfig, Network network,boolean needXUNI){
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
				addFlowsForOneSlot(oneR, l, pathsC, maxNum);
//				for (int j = 0; j < maxNum && j<pathsC.size(); j++) {
//					oneR.addFlow(l,FlowStatus.ZHENGCHANG,pathsC.get(j));
//				}
			}
			if (needXUNI){
				// �����һ��ʱ϶�����������
				oneR.addDummyFlow(r);
			}

			maxNumOfFlow = Math.max(maxNumOfFlow,oneR.getNumOfFlows());
		}

	}

	public void addFlowsForOneSlot(Requirement requirement, int slot, List<Path> pathsC,int maxNum){
		for (int j = 0; j < maxNum && j<pathsC.size(); j++) {
			requirement.addFlow(slot ,FlowStatus.ZHENGCHANG, pathsC.get(j));
		}
	}
	public void addFlowsForOneSlot(Requirement requirement, int slot){
		List<Path> pathsC = NetContext.getNetContext().getNetwork().getPath(requirement.getSNode().getId(), requirement.getDNode().getId());
		int maxNum = NetContext.getNetContext().getPathConfig().getMaxNum();
		addFlowsForOneSlot(requirement, slot, pathsC, maxNum);
	}

	public int getFlowsOfAll(){
		int sum = 0;
		for (Requirement requirement : requirements) {
			for (List<Flow> flows : requirement.getFlowsOfR().values()) {
//				System.out.println(sum);
				sum += flows.size();
			}
		}
		numOfFlows = sum;
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
