package com.qdw.calendaing.base;

import com.qdw.calendaing.base.constant.FlowStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class Flow {

	private String id;
	private int timeSlot;
	//��־��������ù����´θ��������ǾͲ�������
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

		if (p==null){
			id = r.getId()+"-0-0";
		}else {
			id = r.getId()+"-"+p.getFirst().getId()+"-"+p.getLast().getId();
		}

	}

	public boolean isCover(Link link){
		// �����������Ҳ���Ǿ���
		return status.equals(FlowStatus.XUNI) || path.isCover(link);
	}




	@Override
	public String toString() {
		return "Flow [id=" + id + ", timestep=" + timeSlot +  ", value=" + value + ", Rid=" + thisR.getId() + "]";
	}




	
}