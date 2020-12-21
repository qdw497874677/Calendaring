package com.qdw.lpnet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class NetworkTest {
	public static enum Type{size0,size1,size2,size3,size4};
	Type type;
	List<InfoOfNetwork> info_nws ;
//	List<InfoOfNetwork> info_nws_copy = new ArrayList<>();
	int time_uplink;
	
	//static Double[][] matrix_topology = {};
	static List<Link> links ;
	int start_time;
	int end_time;
	
	
	public NetworkTest(Type type,int no_step,double C) {
		start_time = 0;
		end_time = no_step-1;
		info_nws = new ArrayList<>();
		links = new ArrayList<Link>();
		this.type = type;
		int no_timestep = no_step;
		if(this.type == Type.size0) {
			for(int i=0;i<no_timestep;i++) {
				Double[][] coflink_input = {
						{0.0,C,C,0.0},
						{C,0.0,C,C},
						{C,C,0.0,C},
						{0.0,C,C,0.0}
						};
				info_nws.add(new InfoOfNetwork(coflink_input,i));
			}

		}
		else if(this.type == Type.size1) {
			for(int i=0;i<no_timestep;i++) {
				Double[][] coflink_input = {
						{0.0,C,C,0.0,0.0,0.0},
						{C,0.0,C,C,C,0.0},
						{C,C,0.0,C,0.0,C},
						{0.0,C,C,0.0,C,C},
						{0.0,C,0.0,C,0.0,C},
						{0.0,0.0,C,C,C,0.0}
						};
				info_nws.add(new InfoOfNetwork(coflink_input,i));
			}
		}
		else if(this.type == Type.size2) {
			for(int i=0;i<no_timestep;i++) {
				Double[][] coflink_input = {
						{0.0,C,C,0.0,0.0,0.0,C,0.0},
						{C,0.0,C,C,C,0.0,0.0,0.0},
						{C,C,0.0,C,0.0,C,C,C},
						{0.0,C,C,0.0,C,C,0.0,0.0},
						{0.0,C,0.0,C,0.0,C,0.0,0.0},
						{0.0,0.0,C,C,C,0.0,0.0,C},
						{C,0.0,C,0.0,0.0,0.0,0.0,C},
						{0.0,0.0,C,0.0,0.0,C,C,0.0}
						};
				info_nws.add(new InfoOfNetwork(coflink_input,i));
			}
		}
		else if(this.type == Type.size3) {
			for(int i=0;i<no_timestep;i++) {
				String edge = "0-1,0-2,1-2,1-3,2-3,2-4,3-5,4-5,5-6,4-6,4-7,5-8,6-8,2-7,6-9,7-9,8-9,7-10,7-11,9-10,10-11";
				int numOfnode = 12;
				Double[][] coflink_input = geneTopoByEdge(C, edge, numOfnode);
				info_nws.add(new InfoOfNetwork(coflink_input,i));
			}
		}
		else if(this.type == Type.size4) {
			for(int i=0;i<no_timestep;i++) {
				String edge = "1-3,1-5,3-5,2-3,2-4,5-4,5-12,2-6,4-6,6-7,6-8,12-11,12-7,7-8,7-9,9-11,11-10,10-13,11-16,9-16,7-17,8-22,22-24,22-25,24-25,24-17,24-21,17-21,21-26,17-14,26-19,19-20,26-18,18-20,15-18,14-18,13-15,13-14,16-14,13-16,20-23,23-19";
				int numOfnode = 26;
				Double[][] coflink_input = geneTopoByEdge_1(C, edge, numOfnode);
				info_nws.add(new InfoOfNetwork(coflink_input,i));
			}
		}
		
		
		time_uplink = 0;
		
	}
	
	//获取节点从0开始的
	public static Double[][] geneTopoByEdge(double C,String edge,int numOfnode) {
		String[] es = edge.split(",");
		Double[][] topo = new Double[numOfnode][numOfnode];
		for (int i = 0; i < topo.length; i++) {
			for (int j = 0; j < topo[0].length; j++) {
				topo[i][j] = 0.0;

			}
//			System.out.println();
		}
		for(String e:es) {
			topo[Integer.parseInt(e.split("-")[0])][Integer.parseInt(e.split("-")[1])] = C;
			topo[Integer.parseInt(e.split("-")[1])][Integer.parseInt(e.split("-")[0])] = C;
		}
		return topo;

	}
	
	//获取节点从1开始的
	public static Double[][] geneTopoByEdge_1(double C,String edge,int numOfnode) {
		String[] es = edge.split(",");
		Double[][] topo = new Double[numOfnode][numOfnode];
		for (int i = 0; i < topo.length; i++) {
			for (int j = 0; j < topo[0].length; j++) {
				topo[i][j] = 0.0;
				
			}
//			System.out.println();
		}
		for(String e:es) {
			topo[Integer.parseInt(e.split("-")[0])-1][Integer.parseInt(e.split("-")[1])-1] = C;
			topo[Integer.parseInt(e.split("-")[1])-1][Integer.parseInt(e.split("-")[0])-1] = C;
		}
		return topo;
		
	}
	
	public int[][] toIntTopo(int time){
		Double[][] topo = info_nws.get(time).matrix_topology;
		int[][] inttopo = new int[topo.length][topo[0].length];
		for(int i=0;i<topo.length;i++) {
			for(int j=0;j<topo[0].length;j++) {
				if(topo[i][j] == 0.0) {
					inttopo[i][j] = 0;
				}else {
					inttopo[i][j] = topo[i][j].intValue();
				}
			}
		}
		return inttopo;
	}
	
	public double[][] toDoubleTopo(int time){
		Double[][] topo = info_nws.get(time).matrix_topology;
		double[][] inttopo = new double[topo.length][topo[0].length];
		for(int i=0;i<topo.length;i++) {
			for(int j=0;j<topo[0].length;j++) {
				if(topo[i][j] == 0.0) {
					inttopo[i][j] = 0;
				}else {
					inttopo[i][j] = topo[i][j].doubleValue();
				}
			}
		}
		return inttopo;
	}
	
	
	public void updataTopo(Requirement r, int start_time, int end_time, double[] var) {
		DecimalFormat df=new DecimalFormat("0.00");
		for(int i=0;i<end_time-start_time;i++) {
			Double topo[][] = info_nws.get(i).matrix_topology;
			int j=0;
			for(FlowOfR e_f:r.flowsOfR_all) {
				//System.out.println("var["+j+"]:"+var[j]+"  "+ (var[j] != 0.00));
				if(var[j] != 0.00) {
					for(String key_l:e_f.path.links.keySet()) {
						//System.out.println("!");
						int a = e_f.path.links.get(key_l).nodeA;
						int b = e_f.path.links.get(key_l).nodeB;
						topo[a][b] = topo[a][b] - Double.parseDouble(df.format(var[j]));
						topo[b][a] = topo[b][a] - Double.parseDouble(df.format(var[j]));
					}
				}
				j++;
			}
			
		}
	}
	
	public void updataTopo_Rs(Requirement Rs) {
		DecimalFormat df=new DecimalFormat("0.00");
		int start_time = Rs.getStart();
		int end_time = Rs.getEnd();
		InfoOfNetwork info;
		for(int i=start_time;i<=end_time;i++) {
			info = info_nws.get(i);
			for(Link l:info.links) {
				double total = 0;
				for(FlowOfR f:l.overlapflow) {
					if(!f.isUsed){
						f.setUsed();
						total += Double.parseDouble(df.format(f.value));
					}

				}
				l.residual_c = l.residual_c - total;
				double a = info.matrix_topology[l.nodeA][l.nodeB]-total;
				double b = info.matrix_topology[l.nodeB][l.nodeA]-total;
				info.matrix_topology[l.nodeA][l.nodeB] = a;
				info.matrix_topology[l.nodeB][l.nodeA] = b;

			}
			info.updateLinks();

		}
		
	}
	
	public void updataTopo_oneR(Requirement r) {
		DecimalFormat df=new DecimalFormat("0.00");
		int start_time = r.earliest_start;
		int end_time = r.latest_end;
		InfoOfNetwork info;
		for(List<FlowOfR> ff:r.getFlows_allstep()) {
			for(FlowOfR f:ff) {
				if(f.value > 0.0) {
					info = info_nws.get(f.timestep);
					double value = Double.parseDouble(df.format(f.value));
					for(String l:f.path.links.keySet()) {
						int a = f.path.links.get(l).nodeA;
						int b = f.path.links.get(l).nodeB;
						info.matrix_topology[a][b] = info.matrix_topology[a][b]-value;
						info.matrix_topology[b][a] = info.matrix_topology[b][a]-value;
					}
				}
			}
		}
		
//		for(int i=start_time;i<=end_time;i++) {
//			info = info_nws.get(i);
//			for(Link l:info.links) {
//				double total = 0;
//				for(FlowOfR f:l.overlapflow) {
//					total += Double.valueOf(df.format(f.value));
//				}
//				info.matrix_topology[l.nodeA][l.nodeB] = info.matrix_topology[l.nodeA][l.nodeB]-total;
//				info.matrix_topology[l.nodeB][l.nodeA] = info.matrix_topology[l.nodeB][l.nodeA]-total;
//			}
//		}
		
	}
	
	/*
	 * 获取时隙time上的节点s到节点d之间的路径
	 */
	public List<Path> getPaths(int time,String s,String d){
		//System.out.println("!!@@!!@@"+time);
		List<Path> paths;
		
		if(info_nws.get(time).pathmap.containsKey(s+"-"+d)) {
			paths = info_nws.get(time).pathmap.get(s+"-"+d);
		}else if(info_nws.get(time).pathmap.containsKey(d+"-"+s)){
			paths = info_nws.get(time).pathmap.get(d+"-"+s);
		}else {
			paths = null;
		}
		//List<Path> paths = info_nws.get(time).pathmap.get(s+"-"+d);
		if(paths.size() == 0) {
			paths = info_nws.get(time).pathmap.get(d+"-"+s);
		}
		return paths;
	}
	
	
	
	
	/*
	 * 打印链路中经过的流信息
	 */
	public String printLinks_flow(int time_start,int time_end) {
		StringBuffer sb = new StringBuffer();
		DecimalFormat df=new DecimalFormat("0.00");
		sb.append("链路中经过的流信息~~~~~~~~~~~~~~~~\r\n");
		
		InfoOfNetwork info;
		for(int i=time_start;i<=time_end;i++) {
			info = info_nws.get(i);
			
			sb.append("时隙["+i+"]中的链路\r\n");
//			sb.append("时隙["+i+"]中的链路\r\n");
			for(Link l:info.links) {
				StringBuffer s = new StringBuffer();
				s.append(l.link+" ("+l.capacity+")\r\n");
				int flag = 0;
				for(FlowOfR f:l.overlapflow) {
					if(f.value > 0.0) {
//						sb.append(" ["+f.timestep+"]"+f.path.path+"("+df.format(f.value)+")  ");
						
						s.append(" ["+f.timestep+"]"+f.path.path+"("+df.format(f.value)+")  ");
						flag = 1;
					}
				}
				if(flag == 1) {
					sb.append(s+"\r\n");
				}
				
			}
		}
		
		sb.append("\r\n");
		return sb.toString();
	}
	
	public void setPath(String paths) {
		for(InfoOfNetwork info:info_nws) {
			info.setPaths(paths);
		}
	}

	public void setPath(String paths,int i) {
		info_nws.get(i).setPaths(paths);
		for(InfoOfNetwork info:info_nws) {
			info.setPaths(paths);
		}
	}
	
	/*
	 * 打印time时隙的网络拓扑的邻接矩阵
	 */
	public String printTopo(int starttime,int endtime) {
		StringBuffer sb = new StringBuffer();
		
		for(int i=starttime;i<=endtime;i++) {
			sb.append("时隙"+i+"的网络拓扑邻接矩阵:\r\n");
			sb.append(info_nws.get(i).printMatrix_topology());
		}
		
		return sb.toString();
	}
	
	/*
	 * 打印所有时隙的网络拓扑的邻接矩阵
	 */
	public String printTopo() {
		StringBuffer sb = new StringBuffer();
		for(InfoOfNetwork info:info_nws) {
			sb.append("时隙"+info.time+"的网络拓扑邻接矩阵:\r\n");
			sb.append(info.printMatrix_topology());
		}
		
		return sb.toString();
	}
	
	/*
	 * 打印time时隙的网络的所有链路
	 */
	public String printLinks(int time) {
		String s = info_nws.get(time).links.toString();
		System.out.println(s);
		
		return s;
	}
	
	
	/*
	 * 打印所有时隙的网络的所有链路
	 */
	public String printLinks() {
		StringBuffer sb = new StringBuffer();
		for(InfoOfNetwork info:info_nws) {
			sb.append(info.links.toString()+"\r\n");
		}
		
		System.out.println(sb);
		return sb.toString();
	}
	
	
	
}
