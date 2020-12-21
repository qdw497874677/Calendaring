package com.qdw.lpnet;

import com.pranav.pojo.Constraint;
import com.qdw.lpnet.ConsGenerater.OF_Type;
import com.qdw.lpnet.NetworkTest.Type;
import com.qdw.lpnet.RandomGeneration.RG_Type;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calendaring {

	
	int no_step;
	double caOfnw;
	int numOfNodePath;
	int numOfRe;
	int minDemand;
	int maxDemand;
	int mixRange;
	int eTime;
	int lTime;
	Type nwtype;
	OF_Type oftype;
	NetworkTest nw;
	Requirement Rs;
	int numOfNode;
	int numOfpaths;
	RG_Type rgtype;
	String paths;
	RoutingAlgorithm RA;

	List<StringBuffer> rs;
	
	public Calendaring(OF_Type oftype) {
		
		this.oftype = oftype;
//		RA = new RoutingAlgorithm(nw);
//		setPaths(RA);
		
	}

	public Calendaring(int no_step, double caOfnw, int numOfNodePath, int numOfRe, int minDemand, int maxDemand,
			int mixRange, int eTime, int lTime, Type nwtype, OF_Type oftype,RG_Type rgtype) {
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
		nw = new NetworkTest(nwtype,no_step,caOfnw);
		Double[][] coflink_input = nw.info_nws.get(0).matrix_topology;
		numOfNode = coflink_input.length;
		RA = new RoutingAlgorithm(nw);
		setPaths(RA);

	}

	public void setPaths(RoutingAlgorithm RA){

		setPaths(RA,0,1);
	}
	public void setPaths(RoutingAlgorithm RA,int i,int numOfNodePath){
		String paths_auto = "";
		try {
			paths_auto = RA.getPath(numOfNode, nwtype, numOfNodePath,Type_RA.minC,i);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//��·������������������·����
		numOfpaths = paths_auto.toString().split(",").length-1;
		System.out.println("·������Ϊ��"+numOfpaths);
		System.out.println(paths_auto);
		paths = paths_auto.toString();

	}


	public Map<String, Integer> VPVB_na(int num , NetworkTest nw, Requirement Rs) {
//		System.out.println("!!");
		StringBuffer sb = new StringBuffer();
//		nw = new NetworkTest(nwtype,no_step,caOfnw);
//		
		RoutingAlgorithm RA = new RoutingAlgorithm(nw);
		int numOfVexs = nw.info_nws.get(0).matrix_topology.length;
		double[][] edges_na;

		
		Rs.getRequirements().sort(Requirement.Comparators.DEMAND);
//		System.out.println(Rs.toString());
		
		Requirement UAAR = new Requirement();
		Requirement AAR = new Requirement();
		
		for(Requirement r:Rs.getRequirements()) {
			int s = r.s;
			int d = r.d;
			double demand = r.demand;
			double redemand = demand;
			boolean isAc = false;
			for(int i=r.earliest_start;i<=r.latest_end;i++) {
				isAc = false;
				edges_na = nw.toDoubleTopo(i);
				double[] path = RA.dijkstra_maxbandwidth(s, d, numOfVexs, edges_na);
				//���·�����ڣ��һ���δ�����������
				if(path[path.length-1] != 0 && redemand!=0) {
					
					double value = -path[path.length-1];
					String path_str = "";
					
					for(int j=0;j<path.length-1;j++){
						path_str += (int)path[j];
						if(j+1!=path.length-1) {
							path_str += "-";
						}
					}
//					System.out.println("path_str:"+path_str+" value:"+value);
					Path path_p = new Path(path_str,nw.info_nws.get(i));
					
					FlowOfR f;
					
					if(redemand-value>=0) {
						f = new FlowOfR(i, path_p, r, 0);
						
						f.setValue(value);
						r.getFlows_allstep().get(i-r.earliest_start).add(f);
						
						redemand -= value;
					}else {
						f = new FlowOfR(i, path_p, r, 0);
						f.setValue(redemand);
						redemand = 0;
						r.getFlows_allstep().get(i-r.earliest_start).add(f);
						
					}
					
					if(redemand == 0) {
						isAc = true;
						break;
					}
				}
			}
			if(isAc) {
				nw.updataTopo_oneR(r);
				
				AAR.getRequirements().add(r);
			}else {
				UAAR.getRequirements().add(r);
//				AAR.getRequirements().remove(index);
				
			}
			
		}
		
		String AAR_PF = "AAR:\r\n" + AAR.printFlows_all();
		System.out.println(AAR_PF);
//		System.out.println("UAAR:");
//		UAAR.printFlows_all();
		DecimalFormat df = new DecimalFormat("0.00");
		int UAAR_size = UAAR.getRequirements().size();
		int AAR_size = AAR.getRequirements().size();
		String result = "VBVP_na:�ɹ�����������:"+AAR_size+"%#ʧ�ܵ���������:"+UAAR_size+"%#������������:"+(UAAR_size+AAR_size)+"%#�ɹ���Ϊ:"+df.format(((double)AAR_size/(UAAR_size + AAR_size))*100)+"%#\r\n";
		Map<String,Integer> map = new HashMap<>();
		map.put("�ɹ�����������",AAR_size);
		map.put("ʧ�ܵ���������",UAAR_size);


//		System.out.println(AAR.toString());
//		System.out.println(nw.printTopo(AAR.getStart(), AAR.getEnd()));
		
		return map;
	}


	public Map<String, Double> LP(int num, NetworkTest nw, Requirement Rs) throws IOException {
		String objectiveFunction;
		List<Constraint> constraints = new ArrayList<Constraint>(0);
		List<String> coefficients = new ArrayList<String>(0);
		String result = "";//������Ҫ����Ľ����������϶��ʵ�飬��ƽ������
		int unknownVariables_input;//δ֪������������·������
		//����Լ�����ɵ�ʱ��������ʼʱ��ͽ���ʱ����Ϊ���в�������������ʱ�������ʱ��
		Rs.getStartEnd();
		int start_time = Rs.earliest_start;
		int end_time = Rs.latest_end;

		StringBuffer sb = new StringBuffer(); 

		sb.append(Rs.toString());
		
				
		List<String> constraint_inputs_test = new ArrayList<String>();
		ConsGenerater cg = new ConsGenerater(nw);
		
		//�򼯺������Լ��
		constraint_inputs_test.addAll(cg.getCons_linkca_new(Rs));
		constraint_inputs_test.addAll(cg.getCons_flows_negative_new(Rs));
		constraint_inputs_test.addAll(cg.getCons_flows_demand_new(Rs));
		

		
		//�����Ż�Ŀ�꺯��
		String OF = new String();
		
		OF = cg.getFun(oftype, Rs, start_time, end_time);

		System.out.println("Ŀ�꺯��:"+OF);
		System.out.println("Ŀ�꺯������:"+OF.split("#")[1]);
		sb.append("Ŀ�꺯��:"+OF+"\r\n");
		sb.append("Ŀ�꺯������:"+OF.split("#")[1]+"\r\n");
		sb.append("��������:"+(constraint_inputs_test.get(0).split(",").length-2)+"\r\n");
		sb.append("Լ������:"+constraint_inputs_test.size()+"\r\n");
		//
		
		
		System.out.println("���ڽ�Լ��ϵ�����뵽LPϵͳ�С�����");
		long startTime1=System.currentTimeMillis();
		int size = constraint_inputs_test.size();
		int tem=1;
		unknownVariables_input = Rs.getNofflow_all();
		System.out.println("getNofflow_all()="+unknownVariables_input);
		for(int i=0;i<constraint_inputs_test.size();i++) {
			
			if((int)(((double)i/size)*100) == tem) {
				
				System.out.print(tem+"%");
				tem+=2;
			}

			Constraint constraint = new Constraint();
			coefficients = new ArrayList<String>(0);
			//�������������ֲ����ó���Ŷ�����˼��ٱ���
			String[] constraint_inputs_split = constraint_inputs_test.get(i).split(",");
			for(int j=0;j<unknownVariables_input;j++) {
				//System.out.println(constraint_inputs[i].split(",")[j]);
				coefficients.add(constraint_inputs_split[j]);
				
			}
			//System.out.println(constraint_inputs[i].split(",")[unknownVariables_input]);
			constraint.setEquality(Integer.parseInt(constraint_inputs_test.get(i).split(",")[unknownVariables_input]));
			constraint.setConstraintValue(Double.parseDouble(constraint_inputs_test.get(i).split(",")[unknownVariables_input+1]));
			constraint.setCoefficient(getString(coefficients));
			constraints.add(constraint);
		}
		//String OF_constraint_inputs[] = new String[]{"-1,-2,-1,-2"};
		coefficients = new ArrayList<String>(0);
		//System.out.println("!!++!!"+unknownVariables_input);
		String[] OF_split = OF.split(",");
		for (int j = 0; j < unknownVariables_input; j++) {
			
			coefficients.add(OF_split[j]);
			//System.out.print(" OFC"+OF_constraint_inputs[0].split(",")[j]);
		}
		objectiveFunction = getString(coefficients);
		System.out.println("������ɣ�");
		long startTime=System.currentTimeMillis();
		

		System.out.println("���ڽ���LP���㡣����");
		System.out.println("��ǰΪ��"+(num+1)+"��ʵ��");
		double var[] = LpUtil.solveLp(constraints, objectiveFunction, unknownVariables_input,2);

		
		long endTime=System.currentTimeMillis();
		System.out.println("LP����ʱ�䣺"+(endTime-startTime)+"ms");
		sb.append("LP����ʱ�䣺"+(endTime-startTime)+"ms"+"\r\n");
		

		Rs.setFlows_value(start_time, end_time, var);
		
		String oldtopo = "��������ǰ\\r\\n" + nw.printTopo(start_time, end_time);
//		System.out.println(oldtopo);
		sb.append(oldtopo);
//		nw.updataTopo_Rs(Rs, start_time, end_time, var);
		//�����������
		nw.updataTopo_Rs(Rs);
		
		String newtopo = "���������\\r\\n" + nw.printTopo(start_time, end_time);
//		System.out.println(newtopo);
		sb.append(newtopo);
		
		String printLinks_flow = nw.printLinks_flow(start_time,end_time);
//		System.out.println(printLinks_flow);
		sb.append(printLinks_flow);
		
		
		
		System.out.println("\r\nĿ�꺯��:"+OF);
		System.out.println("Ŀ�꺯������:"+OF.split("#")[1]);
		sb.append("\r\nĿ�꺯��:"+OF+"\r\n");
		sb.append("Ŀ�꺯������:"+OF.split("#")[1]+"\r\n");
		SaveData.save(sb.toString());
		
		StringBuffer sb_some = new StringBuffer();
		
		sb_some.append(Rs.printFlows_all());
		sb_some.append("Ŀ�꺯��:"+OF+"\r\n");
		sb_some.append("Ŀ�꺯������:"+OF.split("#")[1]+"\r\n");
		
		sb_some.append("ÿ�Խڵ�֮������·������:"+numOfNodePath+"	��·������������������·����:"+numOfpaths+"	����:"+nwtype+"	��·������Ϊ:"+caOfnw+"	��Լ��ϵ�����뵽LPϵͳ��ʱ��:"+(startTime-startTime1)+"ms 	LP����ʱ��:"+(endTime-startTime)+"ms"+"\r\n");
		System.out.println("ÿ�Խڵ�֮������·������:"+numOfNodePath+"	��·������������������·����:"+numOfpaths+"	����:"+nwtype+"	��·������Ϊ:"+caOfnw+"	��Լ��ϵ�����뵽LPϵͳ��ʱ��:"+(startTime-startTime1)+"ms 	LP����ʱ��:"+(endTime-startTime)+"ms");
		
		int TotalDemand = Rs.getTotalDemand();
		double TotalFlowValue = Rs.getTotalFlowValue();
		sb_some.append("�������������ܺ�:"+TotalDemand+"\r\n");
		sb_some.append("����������������ֵ�ܺ�:"+TotalFlowValue+"\r\n");
		System.out.println("���������Ϊ:"+(TotalFlowValue/TotalDemand)*100+"%");
		sb_some.append("���������Ϊ:"+(TotalFlowValue/TotalDemand)*100+"%\r\n");
		sb_some.append("û�д�����ϵ���������Ϊ:"+Rs.getNumOfUnFinish()+"\r\n");
		sb_some.append(Rs.toString());
		SaveData.saveSome(sb_some.toString());
		
		
		DecimalFormat df = new DecimalFormat("0.00");
		result = "LP:û�д�����ϵ���������Ϊ:"+df.format(Rs.getNumOfUnFinish())+"%#���������Ϊ:"+df.format((TotalFlowValue/TotalDemand)*100)+"%#�����������Ϊ:"+df.format(((double)(Rs.getRequirements().size()-Rs.getNumOfUnFinish())/Rs.getRequirements().size())*100);

		Map<String,Double> map = new HashMap<String, Double>();
		map.put("û�д�����ϵ���������", (double) Rs.getNumOfUnFinish());
		map.put("���������",(TotalFlowValue/TotalDemand)*100);
		map.put("�����������",((double)(Rs.getRequirements().size()-Rs.getNumOfUnFinish())/Rs.getRequirements().size())*100);
		map.put("������",(double)TotalDemand);
		map.put("������",TotalFlowValue);
		System.out.println("���������:"+map.get("�����������"));
//		for(String k:nw.info_nws.get(0).pathmap.keySet()) {
//			
//			nw.info_nws.get(0).pathmap.get(k).toString();
//		}
		return map;
	}

	public Map<String, Double> LP_D(int num, NetworkTest nw, Requirement Rs) throws IOException {

		int s = Rs.getStart();
		int e = Rs.getEnd();
		for (int i = s; i <= e; i++) {
			setPaths(RA,i,10);
			nw.info_nws.get(i).setPaths(paths);
		}
		String objectiveFunction;
		List<Constraint> constraints = new ArrayList<Constraint>(0);
		List<String> coefficients = new ArrayList<String>(0);
		int unknownVariables_input;//δ֪������������·������
		//����Լ�����ɵ�ʱ��������ʼʱ��ͽ���ʱ����Ϊ���в�������������ʱ�������ʱ��
		Rs.getStartEnd();
		int start_time = Rs.earliest_start;
		int end_time = Rs.latest_end;
		StringBuffer sb = new StringBuffer();

		sb.append(Rs.toString());
		List<String> constraint_inputs_test = new ArrayList<String>();
		ConsGenerater cg = new ConsGenerater(nw);

		//�򼯺������Լ��
		constraint_inputs_test.addAll(cg.getCons_linkca_new(Rs));
		constraint_inputs_test.addAll(cg.getCons_flows_negative_new(Rs));
		constraint_inputs_test.addAll(cg.getCons_flows_demand_new(Rs));



		//�����Ż�Ŀ�꺯��
		String OF = new String();

		OF = cg.getFun(oftype, Rs, start_time, end_time);

		System.out.println("Ŀ�꺯��:"+OF);
		System.out.println("Ŀ�꺯������:"+OF.split("#")[1]);
		sb.append("Ŀ�꺯��:"+OF+"\r\n");
		sb.append("Ŀ�꺯������:"+OF.split("#")[1]+"\r\n");
		sb.append("��������:"+(constraint_inputs_test.get(0).split(",").length-2)+"\r\n");
		sb.append("Լ������:"+constraint_inputs_test.size()+"\r\n");

		System.out.println("���ڽ�Լ��ϵ�����뵽LPϵͳ�С�����");
		long startTime1=System.currentTimeMillis();
		int size = constraint_inputs_test.size();
		int tem=1;
		unknownVariables_input = Rs.getNofflow_all();
		System.out.println("getNofflow_all()="+unknownVariables_input);
		for(int i=0;i<constraint_inputs_test.size();i++) {
			if((int)(((double)i/size)*100) == tem) {
				System.out.print(tem+"%");
				tem+=2;
			}
			Constraint constraint = new Constraint();
			coefficients = new ArrayList<String>(0);
			//�������������ֲ����ó���Ŷ�����˼��ٱ���
			String[] constraint_inputs_split = constraint_inputs_test.get(i).split(",");
			for(int j=0;j<unknownVariables_input;j++) {
				//System.out.println(constraint_inputs[i].split(",")[j]);
				coefficients.add(constraint_inputs_split[j]);
			}
			//System.out.println(constraint_inputs[i].split(",")[unknownVariables_input]);
			constraint.setEquality(Integer.parseInt(constraint_inputs_test.get(i).split(",")[unknownVariables_input]));
			constraint.setConstraintValue(Double.parseDouble(constraint_inputs_test.get(i).split(",")[unknownVariables_input+1]));
			constraint.setCoefficient(getString(coefficients));
			constraints.add(constraint);
		}
		//String OF_constraint_inputs[] = new String[]{"-1,-2,-1,-2"};
		coefficients = new ArrayList<String>(0);
		//System.out.println("!!++!!"+unknownVariables_input);
		String[] OF_split = OF.split(",");
		for (int j = 0; j < unknownVariables_input; j++) {
			coefficients.add(OF_split[j]);
			//System.out.print(" OFC"+OF_constraint_inputs[0].split(",")[j]);
		}
		objectiveFunction = getString(coefficients);
		System.out.println("������ɣ�");
		long startTime=System.currentTimeMillis();
		System.out.println("���ڽ���LP���㡣����");
		System.out.println("��ǰΪ��"+(num+1)+"��ʵ��");
		double var[] = LpUtil.solveLp(constraints, objectiveFunction, unknownVariables_input,2);

		long endTime=System.currentTimeMillis();
		System.out.println("LP����ʱ�䣺"+(endTime-startTime)+"ms");
		sb.append("LP����ʱ�䣺"+(endTime-startTime)+"ms"+"\r\n");


		Rs.setFlows_value(start_time, end_time, var);

		String oldtopo = "��������ǰ\\r\\n" + nw.printTopo(start_time, end_time);
//		System.out.println(oldtopo);
		sb.append(oldtopo);
		//�����������
		nw.updataTopo_Rs(Rs);

		String newtopo = "���������\\r\\n" + nw.printTopo(start_time, end_time);
//		System.out.println(newtopo);
		sb.append(newtopo);

		String printLinks_flow = nw.printLinks_flow(start_time,end_time);
//		System.out.println(printLinks_flow);
		sb.append(printLinks_flow);

		System.out.println("\r\nĿ�꺯��:"+OF);
		System.out.println("Ŀ�꺯������:"+OF.split("#")[1]);
		sb.append("\r\nĿ�꺯��:"+OF+"\r\n");
		sb.append("Ŀ�꺯������:"+OF.split("#")[1]+"\r\n");
		SaveData.save(sb.toString());

		StringBuffer sb_some = new StringBuffer();

		sb_some.append(Rs.printFlows_all());
		sb_some.append("Ŀ�꺯��:"+OF+"\r\n");
		sb_some.append("Ŀ�꺯������:"+OF.split("#")[1]+"\r\n");

		sb_some.append("ÿ�Խڵ�֮������·������:"+numOfNodePath+"	��·������������������·����:"+numOfpaths+"	����:"+nwtype+"	��·������Ϊ:"+caOfnw+"	��Լ��ϵ�����뵽LPϵͳ��ʱ��:"+(startTime-startTime1)+"ms 	LP����ʱ��:"+(endTime-startTime)+"ms"+"\r\n");
		System.out.println("ÿ�Խڵ�֮������·������:"+numOfNodePath+"	��·������������������·����:"+numOfpaths+"	����:"+nwtype+"	��·������Ϊ:"+caOfnw+"	��Լ��ϵ�����뵽LPϵͳ��ʱ��:"+(startTime-startTime1)+"ms 	LP����ʱ��:"+(endTime-startTime)+"ms");

		int TotalDemand = Rs.getTotalDemand();
		double TotalFlowValue = Rs.getTotalFlowValue();
		sb_some.append("�������������ܺ�:"+TotalDemand+"\r\n");
		sb_some.append("����������������ֵ�ܺ�:"+TotalFlowValue+"\r\n");
		System.out.println("���������Ϊ:"+(TotalFlowValue/TotalDemand)*100+"%");
		sb_some.append("���������Ϊ:"+(TotalFlowValue/TotalDemand)*100+"%\r\n");
		sb_some.append("û�д�����ϵ���������Ϊ:"+Rs.getNumOfUnFinish()+"\r\n");
		sb_some.append(Rs.toString());
		SaveData.saveSome(sb_some.toString());


		Map<String,Double> map = new HashMap<>();
		map.put("û�д�����ϵ���������", (double) Rs.getNumOfUnFinish());
		map.put("���������",(TotalFlowValue/TotalDemand)*100);
		map.put("�����������",((double)(Rs.getRequirements().size()-Rs.getNumOfUnFinish())/Rs.getRequirements().size())*100);
		map.put("������",(double)TotalDemand);
		map.put("������",TotalFlowValue);
//		for(String k:nw.info_nws.get(0).pathmap.keySet()) {
//
//			nw.info_nws.get(0).pathmap.get(k).toString();
//		}
		return map;

	}
	
	
	


	@Override
	public String toString() {
		return "Calendaring [no_step=" + no_step + ", caOfnw=" + caOfnw + ", numOfNodePath=" + numOfNodePath
				+ ", numOfRe=" + numOfRe + ", minDemand=" + minDemand + ", maxDemand=" + maxDemand + ", mixRange="
				+ mixRange + ", eTime=" + eTime + ", lTime=" + lTime + ", nwtype=" + nwtype + ", oftype=" + oftype
				+ ", rgtype=" + rgtype + "]";
	}

	private static String getString(List<String> coefficients) {
		StringBuilder coefficient = new StringBuilder("");
		int size = coefficients.size();
		int k = 0;
		for (String coeff : coefficients) {
			coefficient.append(coeff);
			if (k != size && size != 1) {
				coefficient.append(" ");
				k++;
			}
		}
		return coefficient.toString();
	}
	
	
	public static void main(String[] args) throws IOException {
		Calendaring cal = new Calendaring(20,30.0,10,100,15,40,3,0,20,NetworkTest.Type.size4,ConsGenerater.OF_Type.earliest,RandomGeneration.RG_Type.type1);
//		String s = cal.LP(0);
		
	}
}
