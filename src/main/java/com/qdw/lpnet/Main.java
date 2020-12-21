/**
 * 
 */
package com.qdw.lpnet;

import com.qdw.TestBed.Test;
import com.qdw.utils.PropertiesUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;


/**
 * @author chikodipranav@gmail.com
 *
 */
public class Main {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {

		PropertiesUtil pu = new PropertiesUtil();

		try {
//			DecimalFormat df = new DecimalFormat("0.00");
//			StringBuffer sb = new StringBuffer();
			double rate_damend = 0;
			double rate_re = 0;
			double rate_su_vbvp = 0;
			double totalTimeOfLp = 0;
			double totalTimeOfVPVB = 0;
			//分配流量实验 次数
//			int time = 1;
			int time = pu.getValuesInt("time");

			//网络时隙个数
//			int no_step = 20;
			int no_step = pu.getValuesInt("noOfStep");
			//网络默认链路总容量
//			double caOfnw = 40.0;
			double caOfnw = pu.getValuesDouble("caOfnw");
			//默认路径数量
//			int numOfNodePath = 20;
			int numOfNodePath = pu.getValuesInt("numOfNodePath");
			//请求数量
//			int numOfRe = 60;
			int numOfRe = pu.getValuesInt("numOfRe");
			//（type1）最小数据量，（type2）平均每个时隙的最小数据量
//			int minDemand = 10;
			int minDemand = pu.getValuesInt("minDemand");
			//（type1）最大数据量，（type2）平均每个时隙的最大数据量
//			int maxDemand = 20;
			int maxDemand = pu.getValuesInt("maxDemand");
			//请求的最小时间间隔
//			int mixRange = 1;
			int mixRange = pu.getValuesInt("mixRange");
			//请求的最早随机的开始时隙
//			int eTime = 0;
			int eTime = pu.getValuesInt("eTime");
			//请求的最晚随机的开始时隙
//			int lTime = 19;
			int lTime = pu.getValuesInt("lTime");
			//网络类型
			NetworkTest.Type nwtype = NetworkTest.Type.size4;
			//目标函数类型
			ConsGenerater.OF_Type oftype = ConsGenerater.OF_Type.earliest;
			//随机请求类型
			RandomGeneration.RG_Type rgtype = RandomGeneration.RG_Type.type2;
			
			
			//是否是在线的
//			boolean period = true;
			boolean period = pu.getValuesBoolean("isOnline");
			boolean onlineLP = pu.getValuesBoolean("onlineLP");
			boolean onlineVPVB = pu.getValuesBoolean("onlineVPVB");
			boolean offlineLP = pu.getValuesBoolean("offlineLP");
			boolean offlineVPVB = pu.getValuesBoolean("offlineVPVB");

			boolean onlineLP_D = pu.getValuesBoolean("onlineLP_D");
			boolean offlineLP_D = pu.getValuesBoolean("offlineLP_D");
			int no_period = 4;
			//周期包含的时隙数量
			int size_period = 5;
			//是否测试
			boolean LP = true;
			boolean VBVP = true;

			//创建两组网络和请求
			NetworkTest nw = GenerateNandR.generateN(no_step, caOfnw, numOfNodePath, nwtype);
			Requirement Rs = GenerateNandR.generateR(nw, numOfRe, numOfNodePath, minDemand, maxDemand, mixRange, eTime, lTime, rgtype);

//			NetworkTest nwLP = GenerateNandR.generateN(no_step, caOfnw, numOfNodePath, nwtype);
//			NetworkTest nwVPVB = GenerateNandR.generateN(no_step, caOfnw, numOfNodePath, nwtype);
//			Requirement RsLP = RandomGeneration.generateRequirement(nwLP,InfoOfRes.getRs());
//			Requirement RsVPVB = RandomGeneration.generateRequirement(nwVPVB,InfoOfRes.getRs());
//			List<Requirement> Rs_pLP = Rs_pLP = Rs.split(no_period,size_period);
//			List<Requirement> Rs_pVPVB = Rs_pVPVB = Rs.split(no_period,size_period);

			Calendaring cal = new Calendaring(oftype);
			//离线
			Test test = new Test();
			if (onlineLP){
				NetworkTest nwLP = GenerateNandR.generateN(no_step, caOfnw, numOfNodePath, nwtype);
				List<Requirement> Rs_pLP = GenerateNandR.generateR(nw, numOfRe, numOfNodePath, minDemand, maxDemand, mixRange, eTime, lTime, rgtype).split(no_period,size_period);
				test.OnlineLP(cal,nwLP,Rs_pLP,time);
			}
			if (onlineVPVB){
				NetworkTest nwVPVB = GenerateNandR.generateN(no_step, caOfnw, numOfNodePath, nwtype);
				List<Requirement> Rs_pVPVB = Rs_pVPVB = Rs.split(no_period,size_period);
				test.OnlineVPVB(cal,nwVPVB,Rs_pVPVB,time);
			}
			if (offlineLP){
				NetworkTest nwLP = GenerateNandR.generateN(no_step, caOfnw, numOfNodePath, nwtype);
				Requirement RsLP = GenerateNandR.generateR(nw, numOfRe, numOfNodePath, minDemand, maxDemand, mixRange, eTime, lTime, rgtype);
				test.OfflineLP(cal,nwLP,RsLP,time);
			}
			if (offlineVPVB){
				NetworkTest nwVPVB = GenerateNandR.generateN(no_step, caOfnw, numOfNodePath, nwtype);
				Requirement RsVPVB = RandomGeneration.generateRequirement(nwVPVB,InfoOfRes.getRs());
				test.OfflineVPVB(cal,nwVPVB,RsVPVB,time);
			}
			if (onlineLP_D){
				NetworkTest nwLP = GenerateNandR.generateN(no_step, caOfnw, numOfNodePath, nwtype);
				List<Requirement> Rs_pLP = Rs.split(no_period,size_period);
				test.OnlineLP_D(cal,nwLP,Rs_pLP,time);
			}
			if (offlineLP_D){
				NetworkTest nwLP = GenerateNandR.generateN(no_step, caOfnw, numOfNodePath, nwtype);
				Requirement RsLP = RandomGeneration.generateRequirement(nwLP,InfoOfRes.getRs());
				test.OfflineLP_D(cal,nwLP,RsLP,time);
			}

			test.printResult();



			//离线情况
//			if(!period) {
//				Calendaring cal = new Calendaring(oftype);
//				for (int i = 0; i < time; i++) {
//
//					NetworkTest nw = GenerateNandR.generateN(no_step, caOfnw, numOfNodePath, nwtype);
//					Requirement Rs = GenerateNandR.generateR(nw, numOfRe, numOfNodePath, minDemand, maxDemand, mixRange, eTime, lTime, rgtype);
//					long startTime=System.currentTimeMillis();
//					Map<String,Double> map_lp = cal.LP(i,nw,Rs);
//					long endTime=System.currentTimeMillis();
//					double useTime = ((double)endTime-startTime)/1000;
//					totalTimeOfLp += useTime;
//					double rateD = map_lp.get("需求完成率");
//					double rateR = map_lp.get("请求传输完成率");
//					rate_damend += rateD;
//					rate_re += rateR;
//					sb.append("LP:"+" 需求完成率:"+rateD+" 请求传输完成率:"+rateR+" 用时："+useTime+"s\r\n");
//					//VBVP
//					nw = new NetworkTest(nwtype,no_step,caOfnw);
//					Rs = RandomGeneration.generateRequirement(nw,InfoOfRes.getRs());
//					startTime=System.currentTimeMillis();
//					Map<String,Integer> map_VBVP_na = cal.VBVP_na(i,nw,Rs);
//					endTime=System.currentTimeMillis();
//					useTime = ((double)endTime-startTime)/1000;
//					totalTimeOfVPVB += useTime;
//					int suNum = map_VBVP_na.get("成功的请求数量");
//					int faNum = map_VBVP_na.get("失败的请求数量");
//					sb.append("VBVP_na:"+" 成功的请求数量:"+suNum+" 失败的请求数量:"+faNum+" 用时："+useTime+"s\r\n");
//					rate_su_vbvp += (double) suNum/(suNum+faNum)*100;
//
//				}
//				rate_damend /= time;
//				rate_re /= time;
//				rate_su_vbvp /= time;
//				totalTimeOfLp /= time;
//				totalTimeOfVPVB /= time;
//
////				rate_damend /= time*(no_period);
////				rate_re /= time*(no_period);
////				rate_su_vbvp /= time*(no_period);
//
//
////				sb.append("LP:次数:"+time+" 平均需求完成率:"+df.format(rate_damend)+"% 平均请求完成率:"+df.format(rate_re)+"% 平均用时:"+totalTimeOfLp+"s\r\n"+
////						"VBVP_na:次数:"+time+" 平均成功率:"+df.format(rate_su_vbvp)+"% 平均用时:"+totalTimeOfVPVB+"s");
////				System.out.println(VBVP);
//				System.out.println(sb);
//				SaveData.save_finally(sb.toString());
//
//				//在线情况
//			}else {
//
//				Calendaring cal = new Calendaring(oftype);
//				for (int i = 0; i < time; i++) {
//					StringBuffer sss = new StringBuffer();
//					//生成网络
//					NetworkTest nw = GenerateNandR.generateN(no_step, caOfnw, numOfNodePath, nwtype);
//					//生成每个时隙传来的请求的集合
//					List<Requirement> Rs_period = GenerateNandR.generateR_p(nw, numOfRe, numOfNodePath, minDemand, maxDemand, mixRange, eTime, lTime, rgtype, no_period, size_period);
//					for(Requirement e_Rs:Rs_period) {
//						System.out.println(e_Rs.toString());
//					}
//					int useTime = 0;
//					//LP 对于每一个时隙接收的请求，分别做分配计算
//					for(Requirement e_Rs:Rs_period) {
//						if(e_Rs.getRequirements().size() == 0) {
//							continue;
//						}
//						long startTime=System.currentTimeMillis();
//						Map<String,Double> map = cal.LP(i,nw,e_Rs);
//						long endTime=System.currentTimeMillis();
//						useTime += ((double)endTime-startTime)/1000;
//						rate_damend += map.get("需求完成率");
//						rate_re += map.get("请求传输完成率");
//
//						for(String key:map.keySet()){
//							sb.append(key).append(":").append(df.format(map.get(key)));
//						}
//						sb.append("\r\n");
//					}
//					totalTimeOfLp += useTime;
//
//					//VBVP
//					useTime = 0;
//					nw = new NetworkTest(nwtype,no_step,caOfnw);
//
//					Rs_period = GenerateNandR.generateR_p(nw, no_period, size_period);
//					for(Requirement e_Rs:Rs_period) {
//						long startTime=System.currentTimeMillis();
//						Map<String,Integer> map_VBVP_na = cal.VBVP_na(i,nw,e_Rs);
//						long endTime=System.currentTimeMillis();
//						useTime += ((double)endTime-startTime)/1000;
//						int suNum = map_VBVP_na.get("成功的请求数量");
//						int faNum = map_VBVP_na.get("失败的请求数量");
//						sb.append("VBVP_na:"+" 成功的请求数量:"+suNum+" 失败的请求数量:"+faNum+"\r\n");
//						rate_su_vbvp += (double) suNum/(suNum+faNum)*100;
//
//					}
//					totalTimeOfVPVB += useTime;
//
//				}

				
//				rate_damend /= time*(no_period);
//				rate_re /= time*(no_period);
//				rate_su_vbvp /= time*(no_period);
//				totalTimeOfLp /= time;
//				totalTimeOfVPVB /= time;
//
//
////				sb.append("LP:次数:").append(time).append(" 平均需求完成率:").append(df.format(rate_damend)).append("% 平均请求完成率:").append(df.format(rate_re)).append("% 平均用时:"+totalTimeOfLp+"s\r\n");
////				sb.append("VBVP_na:次数:").append(time).append(" 平均成功率:").append(df.format(rate_su_vbvp)).append("% 平均用时:"+totalTimeOfVPVB+'s');
////				System.out.println(sb);
//				SaveData.save_finally(sb.toString());
//
//				System.out.println(period);
//			}
//			sb.append("LP:次数:"+time+" 平均需求完成率:"+df.format(rate_damend)+"% 平均请求完成率:"+df.format(rate_re)+"% 平均用时:"+totalTimeOfLp+"s\r\n"+
//					"VBVP_na:次数:"+time+" 平均成功率:"+df.format(rate_su_vbvp)+"% 平均用时:"+totalTimeOfVPVB+"s");
//			System.out.println(sb);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}





	
}
