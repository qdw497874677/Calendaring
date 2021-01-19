package com.qdw.calendaing.base;

import com.qdw.calendaing.base.constant.FlowStatus;
import com.qdw.calendaing.base.pathBase.Path;
import lombok.Data;

@Data
public class Flow {

	private String id;
	private int timeSlot;
	//标志这个流被用过，下次更新网络是就不参与了
	private boolean isUsed;
	private double value;
	private Path path;
	private FlowStatus status;
	private Requirements.Requirement thisR;
	public Flow(int timeSlot, Path p, Requirements.Requirement r, FlowStatus status,double value) {
		this.timeSlot = timeSlot;
		path = p;
		thisR = r;
		this.value = value;
		this.isUsed=false;
		this.status = status;

		if (p==null || p.getPath()==null){
			id = r.getId()+":0->0";
		}else {
			id = r.getId()+":"+p.getFirst().getId()+"->"+p.getLast().getId();
		}

	}

	static public Flow getXUNNIFlow(int timeSlot, Requirements.Requirement requirement){
		return new Flow(timeSlot, null, requirement, FlowStatus.XUNI,0.0);
	}

	public boolean isCover(Link link){
		// 如果是虚拟流也算是经过
		return !status.equals(FlowStatus.XUNI)
				&& path.isCover(link);
	}




	@Override
	public String toString() {
		return "Flow [id=" + id + ", path=" +
				path.getPathStr() +
				", timestep=" + timeSlot +  ", value=" + value + ", Rid=" +

				thisR.getId() + "]";
	}




	
}
