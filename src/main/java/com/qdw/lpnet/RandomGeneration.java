package com.qdw.lpnet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RandomGeneration {
	
	public static enum RG_Type{type1,type2};

	public static Requirement generateRequirement(RandomGeneration.RG_Type type, NetworkTest nw, int num_r, int num_node, int minDemand, int maxDemend, int num_nodepath, int earliest_time, int lastest_time, int min_range) {
		Requirement Rs = new Requirement();
		Random random = new Random();
		List<StringBuffer> rs = new ArrayList<StringBuffer>();
		lastest_time += 1;
		for (int i = 0; i < num_r; i++) {
			StringBuffer r = new StringBuffer();
			r.append((i+1)+",");//id
			int s = random.nextInt(num_node);
			r.append(s + ",");//随机源节点
			int d = random.nextInt(num_node);
			while(s == d) {
				d = random.nextInt(num_node);
			}
			r.append(d + ",");//随机目的节点
			r.append(num_nodepath + ",");//节点对路径的最大数量
			int t1 = earliest_time + random.nextInt(lastest_time-min_range);
			int t2 = t1 + min_range + random.nextInt(lastest_time - t1 - min_range);
			while(t1 == t2) {
				t2 = earliest_time + random.nextInt(lastest_time);
			}
			if(t1 < t2) {
				r.append(t1 + "," + t2+",");
			}else {
				r.append(t2 + "," + t1+",");
			}
			int D = 0;
			if(type == RG_Type.type1) {
				D = minDemand + random.nextInt(maxDemend-minDemand);
			}else if(type == RG_Type.type2) {
				D = minDemand + random.nextInt(maxDemend-minDemand);
				D = D*( (t1>t2)?(t1-t2):(t2-t1) );
			}
			
			r.append(D);//随机需求数据量
//			System.out.println(r);
			rs.add(r);
			
		}
		InfoOfRes.setRs(rs);
		for(StringBuffer s:rs) {
			Rs.addRequirement(s.toString(),nw);
		}
		return Rs;
		
	}
	
	
	
	public static Requirement generateRequirement(NetworkTest nw, List<StringBuffer> rs) {
		Requirement Rs = new Requirement();
		for(StringBuffer s:rs) {
			Rs.addRequirement(s.toString(),true);
		}
		return Rs;
	}
	
	
	
	
	
	
}
