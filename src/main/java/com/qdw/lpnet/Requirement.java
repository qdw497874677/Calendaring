package com.qdw.lpnet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Requirement {
	int rid;
	int s;
	int d;
	int earliest_start;//开始时隙
	int latest_end;//结束时隙
	double demand;
	int nofflow;//每个请求的流最大数量
	int nofflow_all=0;
	int period;//所在周期
	//List<String> path = new ArrayList<String>();
	//Map<String, Double> path = new HashMap<String, Double>();
	FlowOfR flowOfR;
	List<Path> paths;//当前时隙里，这个需求所需要的路径集合
	List<List<Path>> paths_step;
	List<FlowOfR> flowsOfR;//当前时隙当前请求的流
	List<List<FlowOfR>> flowsOfR_allstep;//所有时隙的请求列表
	List<Requirement> list_requirement;//所有请求
	List<FlowOfR> flowsOfR_all;//当前时隙所有请求的所有流
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

		//未完善，路径只选择第一个网络中的路径
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
		
		//加入了时隙
		int id = 0;
		paths = nwr.getPaths(earliest_start, String.valueOf(s), String.valueOf(d));
		if(paths.size() == 0) {
			
			//paths = NetworkTest.info_nws.get(0).pathmap.get(String.valueOf(d)+"-"+String.valueOf(s));
			paths = nwr.getPaths(earliest_start, String.valueOf(d), String.valueOf(s));

		}
		for(int i=earliest_start;i<=latest_end;i++) {
			flowsOfR = new ArrayList<FlowOfR>();
			id = 0;
			//在这个时隙里，加入流
			for(int j=0;j<nofflow && j<paths.size();j++) {	
				flowsOfR.add(new FlowOfR(i,paths.get(j),this,id));
				//System.out.println("!!"+);	
				
				id++;
			}
//			flowsOfR.add(new FlowOfR(i,nwr.info_nws.get(i).pathmap.get("0-0").get(0),earliest_start,latest_end,this,id));
			//把这个时隙的所有流的列表加入
			flowsOfR_allstep.add(flowsOfR);
			
		}
		flowsOfR = new ArrayList<FlowOfR>();
		flowsOfR.add(new FlowOfR(0,nwr.info_nws.get(0).pathmap.get("0-0").get(0),this,id));
		flowsOfR_allstep.add(flowsOfR);
		
		//没有加入时隙
//		flowsOfR = new ArrayList<FlowOfR>();
//		id = 0;
//		for(int i=0;i<nofflow && i<paths.size();i++) {	
//			flowsOfR.add(new FlowOfR(0,paths.get(i),this,id));
//			//System.out.println("!!"+);	
//		}
		
		//添加一条虚拟流，成本值很大，流量无上限
		flowsOfR.add(new FlowOfR(0,nwr.info_nws.get(0).pathmap.get("0-0").get(0),this,id));
		
	}
	
	/*
	 * 不需要nw设置默认路径的高性能网络中的典型请求模型
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


	//从网络中获取路径
	/*
	 * 获取所有的请求
	 */
	public List<Requirement> getRe_all() {
		return list_requirement;
	}
	
	/*
	 * 获取所有请求的所有时隙的所有流的总数量
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
	 * 获取当前请求的所有时隙的所有流的总数量
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
	 * 获取总Rs的最早开始时隙和最晚结束时隙
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
	 * 获取总Rs的最早开始时隙
	 */
	public int getStart() {
		getStartEnd();
		return earliest_start;
	}
	
	/*
	 * 获取总Rs的最晚结束时隙
	 */
	public int getEnd() {
		getStartEnd();
		return latest_end;
	}
	
	
	

	
	
	/*
	 * 获取no_time时隙的第no_flow个流
	 * 返回值类型 FlowOfR
	 */
	public FlowOfR getFlow_step(int no_time,int no_flow) {
//		if(no_flow <earliest_start || no_time > latest_end) {
//			
//		}
		return flowsOfR_allstep.get(no_time-earliest_start).get(no_flow);
	}
	
	/*
	 * 获取no_time时隙的所有流
	 * 返回值类型 List<FlowOfR>
	 */
	public List<FlowOfR> getFlows_step(int no_time) {

		return flowsOfR_allstep.get(no_time-earliest_start);
	}
	
	
	/*
	 * 获取当前请求的所有流集合  在每个Requirement对象中调用
	 * 返回值类型 List<List<FlowOfR>>
	 */
	public List<List<FlowOfR>> getFlows_allstep() {

		return flowsOfR_allstep;
	}
	
	
	
	
	
	/*
	 * 获取所有请求,只能再总的Requirement对象中调用
	 * 返回值类型 List<Requirement>
	 */
	public List<Requirement> getRequirements() {

		return list_requirement;
	}
	
	
	/*
	 * 打印所有请求的所有非零流
	 */
	public String printFlows_all() {
		StringBuffer sb = new StringBuffer();
		DecimalFormat df=new DecimalFormat("0.00");
		String s = "";
		
		for(Requirement r:getRequirements()) {
			s = "请求id"+r.rid+"("+r.demand+"): ";
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
//			System.out.println("\r\n请求："+r.rid);
			for(List<FlowOfR> ff:r.getFlows_allstep()) {
				for(FlowOfR f:ff) {
					f.setValue(var[i]);
//					if(var[i] != 0.0) {
//						System.out.print(   "时隙["+f.timestep+"]带宽为："+df.format(var[i]));
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
		System.out.println("所有请求需求总和:"+totaldemand);
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
		
		
		System.out.println("所有请求分配的流量值总和:"+totalflowvalue);
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
		
//		System.out.println("没有传输完毕的请求数量为:"+unfinish);
//		System.out.println("请求传输完成率为:"+((double)(list_requirement.size()-unfinish)/list_requirement.size())*100+"%");
		return unfinish;
	}
	
	public void refresh() {
		this.flowsOfR = null;
		this.flowsOfR_allstep = null;
		this.paths = null;
		
	}
	
	/*
	 * 设置当前请求所在的周期
	 */
	public void setPeriod(int i) {
		period = i;
	}
	
	/*
	 * 将请求分为几个周期的请求
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
