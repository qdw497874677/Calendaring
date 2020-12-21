package com.qdw.lpnet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Requirement {
	int rid;
	int s;
	int d;
	int earliest_start;//��ʼʱ϶
	int latest_end;//����ʱ϶
	double demand;
	int nofflow;//ÿ����������������
	int nofflow_all=0;
	int period;//��������
	//List<String> path = new ArrayList<String>();
	//Map<String, Double> path = new HashMap<String, Double>();
	FlowOfR flowOfR;
	List<Path> paths;//��ǰʱ϶������������Ҫ��·������
	List<List<Path>> paths_step;
	List<FlowOfR> flowsOfR;//��ǰʱ϶��ǰ�������
	List<List<FlowOfR>> flowsOfR_allstep;//����ʱ϶�������б�
	List<Requirement> list_requirement;//��������
	List<FlowOfR> flowsOfR_all;//��ǰʱ϶���������������
//	List<List<FlowOfR>> flowsOfR_all_step;
//	NetworkTest nw;
	//List<List<FlowOfR>> flowsOfR
	//String path[][];
	public Requirement() {
		list_requirement = new ArrayList<Requirement>();
		flowsOfR_all = new ArrayList<FlowOfR>();

//		this.nw = nw;
	}
	public Requirement(NetworkTest nwr,String r) {
		String as[] = r.split(",");
		rid = Integer.parseInt(as[0]);
		s = Integer.parseInt(as[1]);
		d = Integer.parseInt(as[2]);
		nofflow = Integer.parseInt(as[3]);
		earliest_start = Integer.parseInt(as[4]);
		latest_end = Integer.parseInt(as[5]);
		demand = Double.parseDouble(as[6]);
		//path = new String[nofpath][2];
		//paths = new ArrayList<Path>();
		flowsOfR = new ArrayList<FlowOfR>();
//		paths_step = new ArrayList<List<Path>>();
		flowsOfR_allstep = new ArrayList<List<FlowOfR>>();

		//δ���ƣ�·��ֻѡ���һ�������е�·��
//		for(int i=earliest_start;i<=latest_end;i++) {
//			//paths = NetworkTest.info_nws.get(0).pathmap.get(String.valueOf(s)+"-"+String.valueOf(d));
//			paths = nwr.getPaths(i, String.valueOf(s), String.valueOf(d));
//			if(paths.size() == 0) {
//				
//				//paths = NetworkTest.info_nws.get(0).pathmap.get(String.valueOf(d)+"-"+String.valueOf(s));
//				paths = nwr.getPaths(i, String.valueOf(d), String.valueOf(s));
//
//			}
//			paths_step.add(paths);
//		}
		
		//������ʱ϶
		int id = 0;
		paths = nwr.getPaths(earliest_start, String.valueOf(s), String.valueOf(d));
		if(paths.size() == 0) {
			
			//paths = NetworkTest.info_nws.get(0).pathmap.get(String.valueOf(d)+"-"+String.valueOf(s));
			paths = nwr.getPaths(earliest_start, String.valueOf(d), String.valueOf(s));

		}
		for(int i=earliest_start;i<=latest_end;i++) {
			flowsOfR = new ArrayList<FlowOfR>();
			id = 0;
			//�����ʱ϶�������
			for(int j=0;j<nofflow && j<paths.size();j++) {	
				flowsOfR.add(new FlowOfR(i,paths.get(j),this,id));
				//System.out.println("!!"+);	
				
				id++;
			}
//			flowsOfR.add(new FlowOfR(i,nwr.info_nws.get(i).pathmap.get("0-0").get(0),earliest_start,latest_end,this,id));
			//�����ʱ϶�����������б����
			flowsOfR_allstep.add(flowsOfR);
			
		}
		flowsOfR = new ArrayList<FlowOfR>();
		flowsOfR.add(new FlowOfR(0,nwr.info_nws.get(0).pathmap.get("0-0").get(0),this,id));
		flowsOfR_allstep.add(flowsOfR);
		
		//û�м���ʱ϶
//		flowsOfR = new ArrayList<FlowOfR>();
//		id = 0;
//		for(int i=0;i<nofflow && i<paths.size();i++) {	
//			flowsOfR.add(new FlowOfR(0,paths.get(i),this,id));
//			//System.out.println("!!"+);	
//		}
		
		//���һ�����������ɱ�ֵ�ܴ�����������
		flowsOfR.add(new FlowOfR(0,nwr.info_nws.get(0).pathmap.get("0-0").get(0),this,id));
		
	}
	
	/*
	 * ����Ҫnw����Ĭ��·���ĸ����������еĵ�������ģ��
	 */
	public Requirement(String r,boolean isonepath) {
		String as[] = r.split(",");
		rid = Integer.parseInt(as[0]);
		s = Integer.parseInt(as[1]);
		d = Integer.parseInt(as[2]);
		nofflow = Integer.parseInt(as[3]);
		earliest_start = Integer.parseInt(as[4]);
		latest_end = Integer.parseInt(as[5]);
		demand = Double.parseDouble(as[6]); 

		flowsOfR = new ArrayList<FlowOfR>();

		flowsOfR_allstep = new ArrayList<List<FlowOfR>>();


		int id = 0;
		
		for(int i=earliest_start;i<=latest_end;i++) {
			flowsOfR = new ArrayList<FlowOfR>();
			id = 0;
			flowsOfR.add(new FlowOfR(i,null,this,id));
			flowsOfR_allstep.add(flowsOfR);
		}
	}
	public void addRequirement(String r,NetworkTest nw) {
		list_requirement.add(new Requirement(nw,r));
	}
	
	public void addRequirement(String r,boolean isonepath) {
		list_requirement.add(new Requirement(r,isonepath));
	}


	//�������л�ȡ·��
	/*
	 * ��ȡ���е�����
	 */
	public List<Requirement> getRe_all() {
		return list_requirement;
	}
	
	/*
	 * ��ȡ�������������ʱ϶����������������
	 */
	public int getNofflow_all() {
		if(nofflow_all == 0) {
//		if(true) {

			for(Requirement r:getRequirements()) {
				nofflow_all += r.getNofflow();
//				System.out.println("!"+r.getNofflow());

				
			}
		}
		return nofflow_all;
	}
	

	/*
	 * ��ȡ��ǰ���������ʱ϶����������������
	 */
	public int getNofflow() {
		if(nofflow_all == 0) {
//		if(true) {
			for(List<FlowOfR> ff:getFlows_allstep()) {
				nofflow_all += ff.size();
				
				
			}
		}
		return nofflow_all;
	}
	
	
	
	/*
	 * ��ȡ��Rs�����翪ʼʱ϶���������ʱ϶
	 */
	public void getStartEnd() {
		earliest_start = getRequirements().get(0).earliest_start;
		latest_end = 0;
		for(Requirement r:getRequirements()) {
			if(r.earliest_start < earliest_start) {
				earliest_start = r.earliest_start;
			}else if(r.latest_end > latest_end) {
				latest_end = r.latest_end;
			}
		}
	}
	
	/*
	 * ��ȡ��Rs�����翪ʼʱ϶
	 */
	public int getStart() {
		getStartEnd();
		return earliest_start;
	}
	
	/*
	 * ��ȡ��Rs���������ʱ϶
	 */
	public int getEnd() {
		getStartEnd();
		return latest_end;
	}
	
	
	

	
	
	/*
	 * ��ȡno_timeʱ϶�ĵ�no_flow����
	 * ����ֵ���� FlowOfR
	 */
	public FlowOfR getFlow_step(int no_time,int no_flow) {
//		if(no_flow <earliest_start || no_time > latest_end) {
//			
//		}
		return flowsOfR_allstep.get(no_time-earliest_start).get(no_flow);
	}
	
	/*
	 * ��ȡno_timeʱ϶��������
	 * ����ֵ���� List<FlowOfR>
	 */
	public List<FlowOfR> getFlows_step(int no_time) {

		return flowsOfR_allstep.get(no_time-earliest_start);
	}
	
	
	/*
	 * ��ȡ��ǰ���������������  ��ÿ��Requirement�����е���
	 * ����ֵ���� List<List<FlowOfR>>
	 */
	public List<List<FlowOfR>> getFlows_allstep() {

		return flowsOfR_allstep;
	}
	
	
	
	
	
	/*
	 * ��ȡ��������,ֻ�����ܵ�Requirement�����е���
	 * ����ֵ���� List<Requirement>
	 */
	public List<Requirement> getRequirements() {

		return list_requirement;
	}
	
	
	/*
	 * ��ӡ������������з�����
	 */
	public String printFlows_all() {
		StringBuffer sb = new StringBuffer();
		DecimalFormat df=new DecimalFormat("0.00");
		String s = "";
		
		for(Requirement r:getRequirements()) {
			s = "����id"+r.rid+"("+r.demand+"): ";
			System.out.print(s);
			sb.append(s);
			for(List<FlowOfR> ff:r.getFlows_allstep()) {
				for(FlowOfR f:ff) {
					
					if(f.value > 0.0) {
						s = " ["+f.timestep+"]:"+f.path.path+"("+df.format(f.value)+") ";
						if(f.path.special == true) {
							s += "#############";
						}
						System.out.print(s);
						sb.append(s);
					}
					
				}
			}
			System.out.println();
			sb.append("\r\n");
		}
		
		return sb.toString();
	}
	
	
	
	public void setFlows_value(int start_time,int end_time,double[] var) {
//		StringBuffer sb = new StringBuffer();
//		DecimalFormat df=new DecimalFormat("0.00");
		
		int i=0;
		for(Requirement r:getRequirements()) {
//			System.out.println("\r\n����"+r.rid);
			for(List<FlowOfR> ff:r.getFlows_allstep()) {
				for(FlowOfR f:ff) {
					f.setValue(var[i]);
//					if(var[i] != 0.0) {
//						System.out.print(   "ʱ϶["+f.timestep+"]����Ϊ��"+df.format(var[i]));
//					}
					i++;
					
				}
			}
		}
		
//		return null;
		
	}
	
	
	
	
	
	public int getTotalDemand() {
		int totaldemand = 0;
		for(Requirement e_r:getRequirements()) {
			totaldemand += e_r.demand;
		}
		System.out.println("�������������ܺ�:"+totaldemand);
		return totaldemand;
		
	}
	public double getTotalFlowValue() {
		double totalflowvalue = 0;
		for(Requirement r:getRequirements()) {
			for(List<FlowOfR> ff:r.getFlows_allstep()) {
				for(FlowOfR f:ff) {
					if(f.path.special == false && f.value > 0.0) {
						totalflowvalue += f.value;
					}
				}
			}
			
		}
		
		
		System.out.println("����������������ֵ�ܺ�:"+totalflowvalue);
		return totalflowvalue;
		
	}
	
	public int getNumOfUnFinish() {
		int unfinish=0;
		for(Requirement r:getRequirements()) {
			double f = r.getFlows_allstep().get(r.getFlows_allstep().size()-1).get(0).value;
			if(f > 0.0) {
				unfinish++;
			}
		}
		
//		System.out.println("û�д�����ϵ���������Ϊ:"+unfinish);
//		System.out.println("�����������Ϊ:"+((double)(list_requirement.size()-unfinish)/list_requirement.size())*100+"%");
		return unfinish;
	}
	
	public void refresh() {
		this.flowsOfR = null;
		this.flowsOfR_allstep = null;
		this.paths = null;
		
	}
	
	/*
	 * ���õ�ǰ�������ڵ�����
	 */
	public void setPeriod(int i) {
		period = i;
	}
	
	/*
	 * �������Ϊ�������ڵ�����
	 */
	public List<Requirement> split(int no_period, int size_period){
		
		List<Requirement> Rs_period = new ArrayList<Requirement>();
		for(int i=0;i<no_period;i++) {
			Rs_period.add(new Requirement());
		}
		int i = 0;
		for(Requirement r:getRequirements()) {
			i = r.earliest_start/size_period;
			r.setPeriod(i);
			Rs_period.get(i).getRequirements().add(r);
		}
		
		
		
		
		
		return Rs_period;
	
		
	}
	
	public static class Comparators{
		public static Comparator<Requirement> DEMAND = new Comparator<Requirement>() {

			@Override
			public int compare(Requirement arg0, Requirement arg1) {
				
				return (int)((arg0.demand - arg1.demand)*100);
			}
			
		};
	}
	
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(Requirement r:getRequirements()) {
			sb.append("Requirement [rid=" + r.rid + ", s=" + r.s + ", d=" + r.d + ", earliest_start=" + r.earliest_start
				+ ", latest_end=" + r.latest_end + ", demand=" + r.demand + ",period="+ r.period +"]\r\n");
		}
		
//		System.out.println(sb);
		return sb.toString();
	}
	
	
	
}
