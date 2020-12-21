package com.qdw.lpnet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ConsGenerater {
	
	Requirement R;
	List<Requirement> Rs;
	NetworkTest nw;
	//InfoOfNetwork info_nw;
	//List<InfoOfNetwork> info_nws;
	//int noflink = NetworkTest.info_nws.get(0).links.size();
	static enum OF_Type{
		default_of,
		min_cost,//最小成本，尽可能完成，需求量越小的越不容易完成
		max_throughput,
		max_finishrate,//最大成功率，不能完成的尽量不占用流量
		max_utilization,
		earliest
		
	}
	public ConsGenerater(NetworkTest nw) {
		this.nw = nw;
		
	}
	public String getFun(OF_Type type, Requirement Rs, int start_time, int end_time) throws IOException {
		System.out.println("正在生成目标函数。。。");
		String s = "";
		switch (type) {
		case min_cost:
			s = getFun_optimize_min_cost(Rs, start_time, end_time);
			s = s + "#" + "min_cost";
			break;

		case default_of:
			s = getFun_optimize(Rs, start_time, end_time);
			s = s + "#" + "default_of";
			break;
		case max_finishrate:
			s = getFun_optimize_max_finishrate(Rs, start_time, end_time);
			s = s + "#" + "max_finishrate";
			break;
		case earliest:
			s = getFun_optimize_earliest(Rs, start_time, end_time);
			s = s + "#" + "earliest";
			break;
		
		}
		
		
		System.out.println("生成完成！");
		return s;
	}
	
	
	/*
	 * q添加对每个链路的总容量约束
	 */
//	public List<String> getCons_linkca(Requirement Rs,int start_time,int end_time) {
//		System.out.println("正在生成每个链路的总容量约束。。。");
//		List<String> cons_linkca = new ArrayList<String>();
//
//		InfoOfNetwork info_nw = nw.info_nws.get(0);
//		int no_links = info_nw.linkmap.size();
//		int no_cons_onestep = Rs.getNofflow_all();
//		System.out.println("no_cons_onestep"+no_cons_onestep);
//		int col = Rs.getNofflow_all()*(end_time-start_time)+2;//列
//		
//		int row = nw.info_nws.get(start_time).linkmap.size()*(end_time-start_time);//行
//		double cons[][] = new double[row][col];
//		int current_time=start_time;
//		
//		for(int i=0;i<cons.length;i++) {
//			int f=0;
//			InfoOfNetwork current_nw = nw.info_nws.get(current_time);
//			String cur_link_key = current_nw.link_key.get((i+1)%current_nw.linkmap.size());
//			current_time = (i)/no_links;
//			for(int j=no_cons_onestep*(current_time-start_time);j<no_cons_onestep*(current_time-start_time)+no_cons_onestep;j++) {
//				if(Rs.getFlows_all().get(f).path.links.containsKey(cur_link_key)) {
//					cons[i][j] = 1;
//				}
//				else {
//					cons[i][j] = 0;
//				}
//				f++;
//				
//				
//			}
//			cons[i][col-2] = 1;
//			cons[i][col-1] = current_nw.linkmap.get(cur_link_key).capacity;
//			
//		}
//		
//		for(int i=0;i<cons.length;i++) {
//			StringBuffer sb = new StringBuffer();
//			for(int j=0;j<cons[0].length;j++) {
//				if(j != cons[0].length-1) {
//					sb.append((int)cons[i][j]+",");
//				}else {
//					sb.append(cons[i][j]);
//				}
//				
//			}
//			//System.out.println(sb);
//			cons_linkca.add(sb.toString());
//		}
//		
////		System.out.println("测试");
////		for(int i=0;i<cons.length;i++) {
////			for(int j=0;j<cons[0].length;j++) {
////				System.out.print(cons[i][j]+" ");
////			}
////			System.out.println();
////		}
//		System.out.println("生成完成！");	
//		return cons_linkca;
//		
//	}
	
	
	/*
	 * q添加对每个链路的总容量约束
	 */
	public List<String> getCons_linkca_new(Requirement Rs) {
		System.out.println("正在生成每个链路的总容量约束。。。");
		List<String> cons_linkca = new ArrayList<String>();

		InfoOfNetwork info_nw;
		int col = Rs.getNofflow_all()+2;//列
		
//		int row = nw.info_nws.get(start_time).linkmap.size()*(end_time-start_time);//行
//		double cons[][] = new double[row][col];
		
		int row = 0;
		int start_time = Rs.getStart();
		int end_time = Rs.getEnd();
		for(int i=start_time;i<=end_time;i++) {
			row += nw.info_nws.get(i).links.size();
//			System.out.println(nw.info_nws.get(i).links.size());
		}
		double cons[][] = new double[row][col];
		System.out.println("Rs.getNofflow_all()="+Rs.getNofflow_all()+" start_time="+start_time+" end_time="+end_time+" row="+row+" col="+col);
		int i=0,j=0;
		
		
//		while(i < row) {
		for(int k=Rs.earliest_start;k<=Rs.latest_end;k++) {	
			info_nw = nw.info_nws.get(k);
			for(Link l:info_nw.links) {
				j=0;
//				System.out.println(l.toString());
				for(Requirement r:Rs.getRequirements()) {
					
					for(List<FlowOfR> ff:r.getFlows_allstep()) {

						for(FlowOfR f:ff) {
//							if(r.earliest_start > l.time || r.latest_end < l.time) {
							//如果这个流的所在时隙 和 当前考虑链路的时隙是同一个时隙,且不是特殊流
							if(f.timestep == l.time && f.path.special == false) {
								if(f.path.isCovered(l)) {
									cons[i][j] = 1;
									l.addFlow(f);
								}
							}
							
							j++;
						}
					}
//					System.out.println("j="+j);
				}
				cons[i][col-2] = 1;
				cons[i][col-1] = l.capacity;
//				System.out.println("!!! j="+j);
				i++;
			}
			
		}

		
		for(i=0;i<cons.length;i++) {
			StringBuffer sb = new StringBuffer();
			for(j=0;j<cons[0].length;j++) {
				if(j != cons[0].length-1) {
					sb.append((int)cons[i][j]+",");
				}else {
					sb.append(cons[i][j]);
				}
				
			}
//			System.out.println(sb);
			cons_linkca.add(sb.toString());
		}
//		for(String e_s:cons_linkca) {
//			System.out.println("\""+e_s+"\",");
//		}
		
		
		System.out.println("生成完成！");	
		return cons_linkca;
		
	}
	
	public List<String> getCons_flows_negative_new(Requirement Rs){
		System.out.println("正在生成每个流的非负约束。。。");
		List<String> cons_flows_negative = new ArrayList<String>();



		int row = Rs.getNofflow_all();//行
		int col = Rs.getNofflow_all()+2;//列
		
		int cons[][] = new int[row][col];

		System.out.println("row="+row);
		System.out.println("col="+col);
		
		int i=0;
		for(Requirement r:Rs.getRequirements()) {
			for(List<FlowOfR> ff:r.getFlows_allstep()) {
				for(FlowOfR f:ff) {
					cons[i][i] = 1;
					cons[i][col-2] = 2;
					i++;
				}
			}
		}
		
		
		
		for(i=0;i<cons.length;i++) {
			StringBuffer sb = new StringBuffer();
			
			for(int j=0;j<cons[0].length-1;j++) {
				sb.append(cons[i][j]+",");
			}
			sb.append(cons[i][cons[0].length-1]);
			cons_flows_negative.add(sb.toString());
		}
		
//		for(String e_s:cons_flows_negative) {
//			System.out.println("\""+e_s+"\",");
//		}
		
		System.out.println("生成完成！");
		return cons_flows_negative;
	}
	
	
//	public List<String> getCons_flows_negative(Requirement Rs,int start_time,int end_time){
//		System.out.println("正在生成每个流的非负约束。。。");
//		List<String> cons_flows_negative = new ArrayList<String>();
//
//		
//		
//		int current_time=start_time;
//		InfoOfNetwork info_nw = nw.info_nws.get(current_time);
//		int no_links = info_nw.linkmap.size();
//		int no_cons_onestep = Rs.getNofflow_all();
//		int col = Rs.getNofflow_all()*(end_time-start_time)+2;//列
//		int row = Rs.getNofflow_all()*(end_time-start_time);//行
//		int cons[][] = new int[row][col];
//		int flowid = 0;
//		//System.out.println(Rs.flowsOfR_all.size());
//		System.out.println("row="+row);
//		System.out.println("col="+col);
//		InfoOfNetwork current_nw;
//		for(int i=0;i<row;i++) {
//			
//			//System.out.println(flowid);
//			current_nw = nw.info_nws.get(current_time);
//			
//			cons[i][flowid+(current_time-start_time)*no_cons_onestep] = 1;
//			if(Rs.flowsOfR_all.get(flowid).earliest_start <= current_time && Rs.flowsOfR_all.get(flowid).latest_end > current_time) {//如果这个请求在当前时隙有需求
//				cons[i][col-2] = 2;
//			}else {
//				cons[i][col-2] = 3;
//			}
//			
//			cons[i][col-1] = 0;
//			flowid = (flowid+1)%no_cons_onestep;
//			current_time = (i+1)/no_cons_onestep;
//		}
//		for(int i=0;i<cons.length;i++) {
//			StringBuffer sb = new StringBuffer();
//			
//			for(int j=0;j<cons[0].length-1;j++) {
//				sb.append(cons[i][j]+",");
//			}
//			sb.append(cons[i][cons[0].length-1]);
//			cons_flows_negative.add(sb.toString());
//		}
//		
//			
//		
//		System.out.println("生成完成！");
//		return cons_flows_negative;
//	}
	
	
	
	public List<String> getCons_flows_demand_new(Requirement Rs){
		System.out.println("正在生成每个请求的需求约束。。。");
		List<String> cons_flows_demand = new ArrayList<String>();

		int row = Rs.getRequirements().size();//行
		int col = Rs.getNofflow_all()+2;//列
		double cons[][] = new double[row][col];
		System.out.println("row="+row+" col="+col);
		
		int i=0,j=0;
		
		for(Requirement r:Rs.getRequirements()) {
			
			for(List<FlowOfR> ff:r.getFlows_allstep()) {
				for(FlowOfR f:ff) {
					cons[i][j] = 1;
					j++;
				}
			}
			cons[i][col-2] = 3;
			cons[i][col-1] = r.demand;
			i++;
			
		}
		
		
		
		for(i=0;i<cons.length;i++) {
			StringBuffer sb = new StringBuffer();
			
			for(j=0;j<cons[0].length-1;j++) {
				sb.append((int)cons[i][j]+",");
			}
			sb.append(cons[i][cons[0].length-1]);
			cons_flows_demand.add(sb.toString());
			//System.out.println(sb);
		}
		
//		for(String e_s:cons_flows_demand) {
//			System.out.println("\""+e_s+"\",");
//		}
		
		System.out.println("生成完成");
		return cons_flows_demand;
		
	}
	
	public List<String> getCons_flows_demand(Requirement Rs, int start_time, int end_time){
		System.out.println("正在生成每个请求的需求约束。。。");
		List<String> cons_flows_demand = new ArrayList<String>();

		int current_time=start_time;
		InfoOfNetwork info_nw = nw.info_nws.get(current_time);
		
		int no_cons_onestep = Rs.getNofflow_all();
		int col = Rs.getNofflow_all()*(end_time-start_time)+2;//列
		int row = Rs.list_requirement.size();//行
		double cons[][] = new double[row][col];
		
		//System.out.println(Rs.flowsOfR_all.size());
		int f=0;
		for(int i=0;i<row;i++) {
			Requirement r = Rs.list_requirement.get(i);
			//System.out.println(flowid);
			InfoOfNetwork current_nw = nw.info_nws.get(current_time);
			f = f % no_cons_onestep;
			for(int j=0;j<end_time-start_time;j++) {
				for(int k=j*no_cons_onestep+f;k<j*no_cons_onestep+f+Rs.list_requirement.get(i).flowsOfR.size();k++){
					cons[i][k] = 1;
				}

					
			}
			
			f += Rs.list_requirement.get(i).flowsOfR.size();
			cons[i][col-2] = 3;
			cons[i][col-1] = Rs.list_requirement.get(i).demand;
			current_time = (i)/no_cons_onestep;
		}
		
		for(int i=0;i<cons.length;i++) {
			StringBuffer sb = new StringBuffer();
			
			for(int j=0;j<cons[0].length-1;j++) {
				sb.append((int)cons[i][j]+",");
			}
			sb.append(cons[i][cons[0].length-1]);
			cons_flows_demand.add(sb.toString());
			//System.out.println(sb);
		}
		
		System.out.println("生成完成");
		return cons_flows_demand;
		
	}
	
	
	public String getFun_optimize(Requirement Rs, int start_time, int end_time){
		StringBuffer sb = new StringBuffer();
		for(Requirement r:Rs.getRequirements()) {
			for(List<FlowOfR> ff:r.getFlows_allstep()) {
				for(FlowOfR f:ff) {
					sb.append(f.path.getCost()+",");
				}
			}
		}
		
		
		return sb.toString();
		
	}
	
	public String getFun_optimize_min_cost(Requirement Rs, int start_time, int end_time) throws IOException{
		StringBuffer sb = new StringBuffer();
		int para = 1;
		for(Requirement r:Rs.getRequirements()) {
			for(List<FlowOfR> ff:r.getFlows_allstep()) {
				for(FlowOfR f:ff) {
					if(f.path.special == true) {
						para = (int) (r.demand)*1000;

						sb.append(f.path.getCost(para)+",");
					}else {

						sb.append(f.path.getCost()+",");
					}
				}
			}
		}
		
		sb.replace(sb.length()-1, sb.length(), "");

		return sb.toString();
		
	}
	
	
	public String getFun_optimize_max_finishrate(Requirement Rs, int start_time, int end_time) throws IOException{
		StringBuffer sb = new StringBuffer();
		int para = 1;
		for(Requirement r:Rs.getRequirements()) {
			for(List<FlowOfR> ff:r.getFlows_allstep()) {
				for(FlowOfR f:ff) {
					if(f.path.special == true) {
						para = 10000/(int) (r.demand);

						sb.append(f.path.getCost(para)+",");
					}else {

						sb.append(f.path.getCost()+",");
					}
				}
			}
		}
		
		
	
		sb.replace(sb.length()-1, sb.length(), "");
		//SaveData.save(sbb.toString());
		//System.out.println("!!!"+sb);
		return sb.toString();
		
	}
	
	
	public String getFun_optimize_earliest(Requirement Rs, int start_time, int end_time) throws IOException{
		StringBuffer sb = new StringBuffer();
		
		int i=0;
		int para = 1;
		for(Requirement r:Rs.getRequirements()) {
			for(List<FlowOfR> ff:r.getFlows_allstep()) {
				for(FlowOfR f:ff) {
					if(f.path.special == true) {
						para = 10000/(int) (r.demand);
						
						sb.append(f.path.getCost(para)+",");
					}else {
						i = f.timestep - r.earliest_start;
						double cost = f.path.getCost();
						sb.append((cost*(i+1))+",");
					}
				}
			}
		}
		
		
		
		
//		for(int i=0;i<end_time-start_time;i++) {
//			for(FlowOfR e_f:Rs.getFlows_all()) {
//				if(e_f.path.special == true) {
//					//System.out.println("!!! 成本为"+e_f.path.getCost());
//					//sbb.append("!!! 成本为"+e_f.path.getClass()+"\r\n");
//					para = 10000/(int) (e_f.thisR.demand);
//					//sb.append("-"+e_f.path.setCost((int)(100+1000*(1/e_f.thisR.demand)))+",");
////					sb.append("-"+e_f.path.getCost(para)+",");
//					sb.append(e_f.path.getCost(para)+",");
//
//				}else {
//
//					double cost = e_f.path.getCost();
//					sb.append((cost*(i+1))+",");
//				}
//				
//			}
//			
//		}
		sb.replace(sb.length()-1, sb.length(), "");

		return sb.toString();
		
	}
	

}
