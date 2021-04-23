package com.qdw.calendaing;

import com.qdw.calendaing.base.Flow;
import com.qdw.calendaing.base.requirement.Requirements;
import com.qdw.calendaing.base.constant.FlowStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @PackageName:com.qdw.calendaing
 * @ClassName: CalendaingResult
 * @Description:
 * @date: 2020/11/26 0026 14:28
 */
@Data
public class CalendaingResult {
    private List<Requirements.Requirement> accepted = new LinkedList<>();
    private List<Requirements.Requirement> rejected = new LinkedList<>();

    private List<Requirements.Requirement> onTime = new LinkedList<>();
    private List<Requirements.Requirement> delay = new LinkedList<>();

    private double sumOfDemand = 0;
    private double sumMeetDemand = 0;
    private long totalTime = 0;

    public void accept(Requirements.Requirement requirement){
        requirement.setAccpted(true);
        accepted.add(requirement);
        sumDemand(requirement);
        sumMeetDemand(requirement);

//        System.out.println("!!!!!!!!!!");
//        System.out.println("requirement.getRealFinishSlot():"+requirement.getRealFinishSlot() + "   requirement.getDeadline():"+requirement.getDeadline());

        if (requirement.getDeadline() < requirement.getRealFinishSlot()){
            delay.add(requirement);
        }else {
            onTime.add(requirement);
        }
    }

    public void reject(Requirements.Requirement requirement){
        requirement.setAccpted(false);
        rejected.add(requirement);
        sumDemand(requirement);
        sumMeetDemand(requirement);
    }


    public void setResultOneTime(Requirements requirements){
        for (Requirements.Requirement requirement : requirements.getRequirements()) {
            if (requirement.isAccpted()){
                accept(requirement);
            }else {
                reject(requirement);
            }
        }
    }


    private void sumDemand(Requirements.Requirement requirement){
        sumOfDemand += requirement.getDemand();
    }
    private void sumMeetDemand(Requirements.Requirement requirement){
        sumMeetDemand += requirement.getMeetDemand();
    }


    public String getAcceptRate(){
        int sum = accepted.size() + rejected.size();
        if (sum==0){
            return "0";
        }
        System.out.println("accepted:"+accepted.size()+" rejected:"+rejected.size());
        BigDecimal bigDecimal = new BigDecimal((double) accepted.size() / sum * 100);
        return bigDecimal.setScale(2,BigDecimal.ROUND_HALF_DOWN)+"%";
    }

    public String getThroughputRate(){
        if (sumOfDemand==0){
            return "0";
        }
        System.out.println("sumMeetDemand:"+sumMeetDemand+" sumOfDemand:"+sumOfDemand);
        BigDecimal bigDecimal = new BigDecimal(sumMeetDemand / sumOfDemand * 100);
        return bigDecimal.setScale(4,BigDecimal.ROUND_HALF_DOWN)+"%";
    }

    public String getThroughput(){
        BigDecimal bigDecimal = new BigDecimal(sumMeetDemand);
        return bigDecimal.setScale(4,BigDecimal.ROUND_HALF_DOWN)+"";
    }

    public String getAverageFinishTime(){

        BigDecimal bigDecimal = new BigDecimal(sumMeetDemand);
        return bigDecimal.setScale(4,BigDecimal.ROUND_HALF_DOWN)+"";
    }

    // 获取单时隙平均跳数
    public String getLinkSteps(){
        double sum = 0;
        for (Requirements.Requirement requirement : accepted) {
            for (List<Flow> flows : requirement.getFlowsOfR().values()) {
                for (Flow flow : flows) {
                    sum += flow.getPath().getPath().size();
                }
            }
        }
        for (Requirements.Requirement requirement : rejected) {
            for (List<Flow> flows : requirement.getFlowsOfR().values()) {
                for (Flow flow : flows) {
                    sum += flow.getPath().getPath().size();
                }
            }
        }
//        return (sum/accepted.get(0).get)+"";
        return "";
    }

    // 获取虽然还没有完成所有数据的传输预留，但是已经可以预留的部分数据量
    public String getReservedButRejected(){
        double sum = 0;
        for (Requirements.Requirement requirement : rejected) {
            for (List<Flow> flows : requirement.getFlowsOfR().values()) {
                for (Flow flow : flows) {
                    if (flow.getStatus().equals(FlowStatus.ZHENGCHANG)){
                        sum += flow.getValue();
                    }
                }
            }
        }
        BigDecimal bigDecimal = new BigDecimal(sum);
        return bigDecimal.setScale(4,BigDecimal.ROUND_HALF_DOWN)+"";
    }

    // 获取虽然还没有完成所有数据的传输预留，但是已经可以预留的部分数据量
    public String getReservedRateButRejected(){
        double sum = 0;
        double sumDemand = 0;
        for (Requirements.Requirement requirement : rejected) {
            sumDemand += requirement.getDemand();
            for (List<Flow> flows : requirement.getFlowsOfR().values()) {
                for (Flow flow : flows) {
                    if (flow.getStatus().equals(FlowStatus.ZHENGCHANG)){
                        sum += flow.getValue();
                    }
                }
            }
        }
        if (sumDemand == 0){
            return 0+"";
        }
//        System.out.println(sum+"@@@@@@@@@@@@@@");
        BigDecimal bigDecimal = new BigDecimal(sum*100/sumDemand);
        return bigDecimal.setScale(4,BigDecimal.ROUND_HALF_DOWN)+"%";
    }
    // 求批量平均值
    static public String getAverageReservedRateButRejected(Collection<CalendaingResult> collection){

        double sum = 0;
        double sumDemand = 0;
        for (CalendaingResult calendaingResult : collection) {
            for (Requirements.Requirement requirement : calendaingResult.getRejected()) {
                sumDemand += requirement.getDemand();
                for (List<Flow> flows : requirement.getFlowsOfR().values()) {
                    for (Flow flow : flows) {
                        if (flow.getStatus().equals(FlowStatus.ZHENGCHANG)){
                            sum += flow.getValue();
                        }
                    }
                }
            }
        }
        if (sumDemand==0){
            return 0+"%";
        }
        BigDecimal bigDecimal = new BigDecimal(sum*100/sumDemand);
        return bigDecimal.setScale(4,BigDecimal.ROUND_HALF_DOWN)+"%";
    }


    static public String getAverageAcceptRate(Collection<CalendaingResult> collection){
        double sum = 0;
        for (CalendaingResult calendaingResult : collection) {
            double s = calendaingResult.getAccepted().size() + calendaingResult.getRejected().size();
            sum += calendaingResult.getAccepted().size()*100/s;
        }
        return new BigDecimal(sum/collection.size()).setScale(4,BigDecimal.ROUND_HALF_DOWN)+"%";
    }

    static public String getAverageThroughputRate(Collection<CalendaingResult> collection){
        double sum = 0;
        for (CalendaingResult calendaingResult : collection) {
            sum += calendaingResult.getSumMeetDemand() / calendaingResult.getSumOfDemand() * 100;
        }
        BigDecimal bigDecimal = new BigDecimal(sum/collection.size());
        return bigDecimal.setScale(4,BigDecimal.ROUND_HALF_DOWN)+"%";
    }

    static public String getAverageThroughput(Collection<CalendaingResult> collection){
        double sum = 0;
        for (CalendaingResult calendaingResult : collection) {
            sum += calendaingResult.getSumMeetDemand();
        }
        BigDecimal bigDecimal = new BigDecimal(sum/collection.size());
        return bigDecimal.setScale(4,BigDecimal.ROUND_HALF_DOWN)+"";
    }

    // 获取总跳数
    static public String getAllLinkSteps(Collection<CalendaingResult> collection){
        double sum = 0;
        for (CalendaingResult calendaingResult : collection) {

        }
        BigDecimal bigDecimal = new BigDecimal(sum/collection.size());
        return bigDecimal.setScale(4,BigDecimal.ROUND_HALF_DOWN)+"";
    }

    // 按时完成率
    public double getOAR(){
        int b = onTime.size() + delay.size();
        if (b == 0){
            return 0;
        }else {
            BigDecimal bigDecimal = new BigDecimal((double) onTime.size()/b * 100);
            return bigDecimal.setScale(4,BigDecimal.ROUND_HALF_DOWN).doubleValue();
        }
    }

    // 按时完成传输率
    public double getOADR(){
        double a = 0;
        for (Requirements.Requirement requirement : onTime) {
            a += requirement.getDemand();
        }
        double b = 0;
        for (Requirements.Requirement requirement : accepted) {
            b += requirement.getDemand();
        }
        if (b == 0){
            return 0;
        }else {
            BigDecimal bigDecimal = new BigDecimal((double) a/b * 100);
            return bigDecimal.setScale(4,BigDecimal.ROUND_HALF_DOWN).doubleValue();
        }
    }

    // 按时传输数据量
    public double getOTDV(){
        double a = 0;
        for (Requirements.Requirement requirement : onTime) {
            a += requirement.getDemand();
        }
        BigDecimal bigDecimal = new BigDecimal((double) a);
        return bigDecimal.setScale(4,BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }

    // 按时完成传输率
    public static double getAllOADR(Collection<CalendaingResult> collection){
        double sum = 0;
        for (CalendaingResult calendaingResult : collection) {
            sum += calendaingResult.getOADR();
        }
        return sum/collection.size();
    }

    // 最早完成时间
    public double getECT(){
        double sum = 0;
        for (Requirements.Requirement requirement : accepted) {
            sum += requirement.getRealFinishSlot();
        }
        return sum/accepted.size();
    }

    // 带宽消耗率
    public double getBC(){
        double sum = 0;
        for (Requirements.Requirement requirement : accepted) {
            for (List<Flow> flows : requirement.getFlowsOfR().values()) {
                for (Flow flow : flows) {
                    if (flow.getValue()>0 && flow.getStatus().equals(FlowStatus.ZHENGCHANG)){
                        sum += flow.getPath().getPath().size()*flow.getValue();
                    }
                }
            }
        }
        return sum/accepted.size();
    }

    public static double getAllBC(Collection<CalendaingResult> collection){
        double sum = 0;
        for (CalendaingResult calendaingResult : collection) {
            sum += calendaingResult.getBC();
        }
        return sum/collection.size();
    }


    public static double getAllOAR(Collection<CalendaingResult> collection){
        double sum = 0;
        for (CalendaingResult calendaingResult : collection) {
            sum += calendaingResult.getOAR();
        }
        return sum/collection.size();
    }



    // 平均预留延迟率
    public double getARDR(){
        if (delay.size() == 0){
            return 0;
        }else {
            double a = 0;
            double b = 0;
            for (Requirements.Requirement requirement : delay) {
                a += requirement.getRealFinishSlot() - requirement.getDeadline();
                b += requirement.getDeadline() - requirement.getReadySlot();
            }
            BigDecimal bigDecimal = new BigDecimal((double)a/b * 100);
            return bigDecimal.setScale(4,BigDecimal.ROUND_HALF_DOWN).doubleValue();
        }
    }

    public static double getAllARDR(Collection<CalendaingResult> collection){
        double sum = 0;
        for (CalendaingResult calendaingResult : collection) {
            sum += calendaingResult.getARDR();
        }
        return sum/collection.size();
    }

    @Override
    public String toString() {
        StringBuilder sb1 = new StringBuilder();
        for (Requirements.Requirement requirement : accepted) {
            sb1.append(requirement.toString()).append("\n");
        }

        StringBuilder sb2 = new StringBuilder();
        for (Requirements.Requirement requirement : rejected) {
            sb2.append(requirement.toString()).append("\n");
        }

        return "CalendaingResult{\n" +
                "accepted={\n" + sb1 +
                "},"+accepted.size()+", \nrejected={\n" + sb2 +
                "},"+rejected.size()+"\n}";
    }
}
