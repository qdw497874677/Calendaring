package com.qdw.TestBed;

import com.qdw.lpnet.*;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class Test {

//    double rate_damend;
//    double rate_re;
//    double rate_su_vbvp;
//    double totalTimeOfLp;
//    double totalTimeOfVPVB;
    StringBuffer sb;
    DecimalFormat df;
    StringBuffer sbF;
    public Test() {
//        this.rate_damend = 0;
//        this.rate_re = 0;
//        this.rate_su_vbvp = 0;
//        this.totalTimeOfLp = 0;
//        this.totalTimeOfVPVB = 0;
        this.sb = new StringBuffer();
        this.sbF = new StringBuffer();
        this.df = new DecimalFormat("0.00");
    }
    public void OfflineLP(Calendaring cal,NetworkTest nw,Requirement Rs,int time) throws IOException {
            double rate_damend=0;
            double rate_re=0;
            double totalTimeOfLp=0;
        for (int i = 0; i < time; i++) {
            long startTime=System.currentTimeMillis();
            Map<String,Double> map_lp = cal.LP(i,nw,Rs);
            long endTime=System.currentTimeMillis();
            double useTime = ((double)endTime-startTime)/1000;
            totalTimeOfLp += useTime;
            double rateD = map_lp.get("���������");
            double rateR = map_lp.get("�����������");
            rate_damend += rateD;
            rate_re += rateR;
            totalTimeOfLp += useTime;
            sb.append("LP:"+" ���������:"+rateD+" �����������:"+rateR+" ��ʱ��"+useTime+"s\r\n");
//            rate_damend += rateD;
//            rate_re += rateR;

        }
        rate_damend /= time;
        rate_re /= time;
        totalTimeOfLp /= time;
        sbF.append("LP����:����:"+time+" ƽ�����������:"+df.format(rate_damend)+"% ƽ�����������:"+df.format(rate_re)+"% ƽ����ʱ:"+totalTimeOfLp+"s\r\n");
//        System.out.println(nw.printTopo(0, 19));
    }
    public void OnlineLP(Calendaring cal,NetworkTest nw,List<Requirement> Rs_period,int time) throws IOException {
        int flag = 0;
        double rate_damend=0;
        double rate_re=0;
        double totalTimeOfLp=0;
        for (int i = 0; i < time; i++) {

            int useTime = 0;
            //LP ����ÿһ��ʱ϶���յ����󣬷ֱ����������
//            int rateR = 0;
//            int rateD = 0;
            double F = 0;
            double D = 0;
            double faR = 0;
            double sumR = 0;
            for(Requirement e_Rs:Rs_period) {
                flag++;
//                for (Requirement requirement : e_Rs.getRequirements()) {
//
//                }
                if(e_Rs.getRequirements().size() == 0) {
                    continue;
                }
                long startTime=System.currentTimeMillis();
                Map<String,Double> map = cal.LP(i,nw,e_Rs);
                long endTime=System.currentTimeMillis();
                useTime += ((double)endTime-startTime)/1000;
//                rate_damend += map.get("���������");
//                rate_re += map.get("�����������");

//                rateD += map.get("������")/map.get("������")*100;
                F += map.get("������");
                D += map.get("������");
//                rateR += ((e_Rs.getRequirements().size()-map.get("û�д�����ϵ���������"))/e_Rs.getRequirements().size())*100;
                sumR += e_Rs.getRequirements().size();
                faR += map.get("û�д�����ϵ���������");
            }
            rate_damend += F/D*100;
            rate_re += ((sumR-faR)/sumR)*100;

            totalTimeOfLp += useTime;
        }
        rate_re /= time;
        rate_damend /= time;
        totalTimeOfLp /= time;
        sbF.append("LP����:����:"+time+" ƽ�����������:"+df.format(rate_damend)+"% ƽ�����������:"+df.format(rate_re)+"% ƽ����ʱ:"+totalTimeOfLp+"s\r\n");
//        System.out.println(flag);
//        for(Requirement e_Rs:Rs_period) {
//            e_Rs.printFlows_all();
//        }
//        System.out.println(nw.printTopo(0, 19));
    }


    public void OfflineLP_D(Calendaring cal,NetworkTest nw,Requirement Rs,int time) throws IOException {
        double rate_damend=0;
        double rate_re=0;
        double totalTimeOfLp=0;
        for (int i = 0; i < time; i++) {
            long startTime=System.currentTimeMillis();
            Map<String,Double> map_lp = cal.LP_D(i,nw,Rs);
            long endTime=System.currentTimeMillis();
            double useTime = ((double)endTime-startTime)/1000;
            totalTimeOfLp += useTime;
            double rateD = map_lp.get("���������");
            double rateR = map_lp.get("�����������");
            rate_damend += rateD;
            rate_re += rateR;
            totalTimeOfLp += useTime;
            sb.append("LP:"+" ���������:"+rateD+" �����������:"+rateR+" ��ʱ��"+useTime+"s\r\n");
//            rate_damend += rateD;
//            rate_re += rateR;

        }
        rate_damend /= time;
        rate_re /= time;
        totalTimeOfLp /= time;
        sbF.append("LP_D����:����:"+time+" ƽ�����������:"+df.format(rate_damend)+"% ƽ�����������:"+df.format(rate_re)+"% ƽ����ʱ:"+totalTimeOfLp+"s\r\n");

    }


    public void OnlineLP_D(Calendaring cal,NetworkTest nw,List<Requirement> Rs_period,int time) throws IOException {
        double rate_damend=0;
        double rate_re=0;
        double totalTimeOfLp=0;
        for (int i = 0; i < time; i++) {
            for(Requirement e_Rs:Rs_period) {
                System.out.println(e_Rs.toString());
            }
            int useTime = 0;
            //LP ����ÿһ��ʱ϶���յ����󣬷ֱ����������
//            int rateR = 0;
//            int rateD = 0;
            double F = 0;
            double D = 0;
            double faR = 0;
            double sumR = 0;
            for(Requirement e_Rs:Rs_period) {
                if(e_Rs.getRequirements().size() == 0) {
                    continue;
                }
                long startTime=System.currentTimeMillis();
                Map<String,Double> map = cal.LP_D(i,nw,e_Rs);
                long endTime=System.currentTimeMillis();
                useTime += ((double)endTime-startTime)/1000;
//                rate_damend += map.get("���������");
//                rate_re += map.get("�����������");

//                rateD += map.get("������")/map.get("������")*100;
                F += map.get("������");
                D += map.get("������");
//                rateR += ((e_Rs.getRequirements().size()-map.get("û�д�����ϵ���������"))/e_Rs.getRequirements().size())*100;
                sumR += e_Rs.getRequirements().size();
                faR += map.get("û�д�����ϵ���������");
            }
            rate_damend += F/D*100;
            rate_re += ((sumR-faR)/sumR)*100;

            totalTimeOfLp += useTime;
        }
        rate_re /= time;
        rate_damend /= time;
        totalTimeOfLp /= time;
        sbF.append("LP_D����:����:"+time+" ƽ�����������:"+df.format(rate_damend)+"% ƽ�����������:"+df.format(rate_re)+"% ƽ����ʱ:"+totalTimeOfLp+"s\r\n");

    }


    public void OfflineVPVB(Calendaring cal,NetworkTest nw,Requirement Rs,int time) throws IOException{
        double rate_su_vbvp=0;
        double totalTimeOfVPVB=0;
        for (int i = 0; i < time; i++) {
            long startTime=System.currentTimeMillis();
            Map<String,Integer> map_VBVP_na = cal.VPVB_na(i,nw,Rs);
            long endTime=System.currentTimeMillis();
            double useTime = ((double)endTime-startTime)/1000;
            int suNum = map_VBVP_na.get("�ɹ�����������");
            int faNum = map_VBVP_na.get("ʧ�ܵ���������");
            sb.append("VBVP_na:"+" �ɹ�����������:"+suNum+" ʧ�ܵ���������:"+faNum+" ��ʱ��"+useTime+"s\r\n");
            rate_su_vbvp += (double) suNum/(suNum+faNum)*100;
            totalTimeOfVPVB += useTime;
        }
        rate_su_vbvp /= time;
        totalTimeOfVPVB /= time;
        sbF.append("VBVP����:����:"+time+" ƽ���ɹ���:"+df.format(rate_su_vbvp)+"% ƽ����ʱ:"+totalTimeOfVPVB+"s\r\n");

    }


    public void OnlineVPVB(Calendaring cal,NetworkTest nw,List<Requirement> Rs_period,int time) throws IOException{
        double rate_su_vbvp=0;
        double totalTimeOfVPVB=0;
        for (int i = 0; i < time; i++) {
            for(Requirement e_Rs:Rs_period) {
                System.out.println(e_Rs.toString());
            }
            int useTime = 0;
            //LP ����ÿһ��ʱ϶���յ����󣬷ֱ����������

            int suNum = 0;
            int faNum = 0;
            for(Requirement e_Rs:Rs_period) {
                if(e_Rs.getRequirements().size() == 0) {
                    continue;
                }
                long startTime=System.currentTimeMillis();
                Map<String,Integer> map_VBVP_na = cal.VPVB_na(i,nw,e_Rs);
                long endTime=System.currentTimeMillis();
                useTime += ((double)endTime-startTime)/1000;
                suNum = map_VBVP_na.get("�ɹ�����������");
                faNum = map_VBVP_na.get("ʧ�ܵ���������");
                sb.append("VBVP_na:"+" �ɹ�����������:"+suNum+" ʧ�ܵ���������:"+faNum+"\r\n");

            }
            rate_su_vbvp += (double) suNum/(suNum+faNum)*100;
            totalTimeOfVPVB += useTime;
        }
        rate_su_vbvp /= time;
        totalTimeOfVPVB /= time;
        sbF.append("VBVP����:����:"+time+" ƽ���ɹ���:"+df.format(rate_su_vbvp)+"% ƽ����ʱ:"+totalTimeOfVPVB+"s\r\n");

    }

    public void printResult(){
        System.out.println(sb);
        System.out.println(sbF);
    }

}
