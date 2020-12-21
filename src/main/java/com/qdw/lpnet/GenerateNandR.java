package com.qdw.lpnet;

import com.qdw.lpnet.ConsGenerater.OF_Type;
import com.qdw.lpnet.NetworkTest.Type;
import com.qdw.lpnet.RandomGeneration.RG_Type;

import java.io.IOException;
import java.util.List;

public class GenerateNandR {
	//网络时隙个数
	int no_step = 20;
	//网络默认链路总容量
	double caOfnw = 40.0;
	//默认路径数量
	int numOfNodePath = 20;
	//请求数量
	int numOfRe = 50;
	//（type1）最小数据量，（type2）平均每个时隙的最小数据量
	int minDemand = 10;
	//（type1）最大数据量，（type2）平均每个时隙的最大数据量
	int maxDemand = 20;
	//请求的最小时间间隔
	int mixRange = 1;
	//请求的最早随机的开始时隙
	int eTime = 0;
	//请求的最晚随机的开始时隙
	int lTime = 19;
	//网络类型
	NetworkTest.Type nwtype = NetworkTest.Type.size4;
	//目标函数类型
	ConsGenerater.OF_Type oftype = ConsGenerater.OF_Type.earliest;
	//随机请求类型
	RandomGeneration.RG_Type rgtype = RandomGeneration.RG_Type.type2;

	//周期的数量
	int no_period = 4;
	//一个周期包含的时隙
	int size_period = 5;
	
	public GenerateNandR(int no_step, double caOfnw, int numOfNodePath, int numOfRe, int minDemand, int maxDemand,
			int mixRange, int eTime, int lTime, Type nwtype, OF_Type oftype, RG_Type rgtype, int no_period,
			int size_period) {
		super();
		this.no_step = no_step;
		this.caOfnw = caOfnw;
		this.numOfNodePath = numOfNodePath;
		this.numOfRe = numOfRe;
		this.minDemand = minDemand;
		this.maxDemand = maxDemand;
		this.mixRange = mixRange;
		this.eTime = eTime;
		this.lTime = lTime;
		this.nwtype = nwtype;
		this.oftype = oftype;
		this.rgtype = rgtype;
		this.no_period = no_period;
		this.size_period = size_period;
	}
	
	public static NetworkTest generateN(int no_step, double caOfnw, int numOfNodePath,NetworkTest.Type nwtype) {
		NetworkTest nw = new NetworkTest(nwtype,no_step,caOfnw);
		Double[][] coflink_input = nw.info_nws.get(0).matrix_topology;
		int numOfNode = coflink_input.length;
		RoutingAlgorithm RA = new RoutingAlgorithm(nw);
		String paths_auto = "";
		try {
			paths_auto = RA.getPath(numOfNode, nwtype, numOfNodePath,Type_RA.minC);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		int numOfpaths = paths_auto.toString().split(",").length-1;//总路径数量（不包括特殊路径）
		System.out.println("路径数量为："+numOfpaths);
		System.out.println(paths_auto);
		String paths = paths_auto.toString();
		for(int j=0;j<no_step;j++) {
			nw.info_nws.get(j).setPaths(paths);
		}
		
		return nw;
	}
	
	public static Requirement generateR(NetworkTest nw,int numOfRe,int numOfNodePath, int minDemand, int maxDemand,
			int mixRange, int eTime, int lTime,RG_Type rgtype) {
		Double[][] coflink_input = nw.info_nws.get(0).matrix_topology;
		int numOfNode = coflink_input.length;
		Requirement Rs = RandomGeneration.generateRequirement(rgtype,nw,numOfRe, numOfNode, minDemand, maxDemand, numOfNodePath, eTime, lTime,mixRange);
		return Rs;
	}
	
	public static List<Requirement> generateR_p(NetworkTest nw,int numOfRe,int numOfNodePath, int minDemand, int maxDemand,
			int mixRange, int eTime, int lTime,RG_Type rgtype,int no_period,
			int size_period) {
		Double[][] coflink_input = nw.info_nws.get(0).matrix_topology;
		int numOfNode = coflink_input.length;
		Requirement Rs = RandomGeneration.generateRequirement(rgtype,nw,numOfRe, numOfNode, minDemand, maxDemand, numOfNodePath, eTime, lTime,mixRange);
		List<Requirement> Rs_period =  Rs.split(no_period,size_period);
		return Rs_period;
	}
	
	public static List<Requirement> generateR_p(NetworkTest nw,int no_period,int size_period){
		
		
		
		Requirement Rs = RandomGeneration.generateRequirement(nw,InfoOfRes.getRs());
		List<Requirement> Rs_period =  Rs.split(no_period,size_period);
		return Rs_period;
	}
	
	

}
