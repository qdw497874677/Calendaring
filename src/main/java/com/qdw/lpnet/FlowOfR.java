package com.qdw.lpnet;

import java.util.ArrayList;
import java.util.List;

public class FlowOfR {
	boolean isUsed;
	int id;
	int timestep;
//	int earliest_start;
//	int latest_end;
//	double[] value_a;
	double value;
	Path path;
	List<Link> links = new ArrayList<>();
	Requirement thisR;
	int Rid;
	public FlowOfR(int timestep, Path p, Requirement r, int id) {
//		value_a = new double[latest_end-earliest_start];
		this.timestep = timestep;
		path = p;
//		this.earliest_start = earliest_start;
//		this.latest_end = latest_end;
		thisR = r;
		Rid = r.rid;
		value = 0;
		this.id = id;
		this.isUsed=false;
	}
	
	
	
//	public void setValue(double v,int time) {
//		value_a[time] = v;
//	}

	//标志这个流被用过，下次更新网络是就不参与了
	public void setUsed(){
		this.isUsed=true;
	}
	
	public void setValue(double v) {
		value = v;
	}

	@Override
	public String toString() {
		return "FlowOfR [id=" + id + ", timestep=" + timestep + ", earliest_start=" 
				+ ", value=" + value + ", Rid=" + Rid + "]";
	}

	
	
	
	
}
