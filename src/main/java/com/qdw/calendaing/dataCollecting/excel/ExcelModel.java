package com.qdw.calendaing.dataCollecting.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.sun.rowset.internal.BaseRow;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Quandw
 * @Description:
 * @Date: 2021/1/19 0019 20:25
 */
@Data
public class ExcelModel extends BaseRowModel implements Serializable {

    @ExcelProperty(value = "实验次数" , index = 0)
    private int time;

    @ExcelProperty(value = "平均用时" , index = 1)
    private String avgTime;

    @ExcelProperty(value = "平均完成率" , index = 2)
    private String averageAcceptRate;

    @ExcelProperty(value = "平均数据传输率" , index = 3)
    private String averageThroughputRate;

    @ExcelProperty(value = "平均未完成请求的传输率" , index = 4)
    private String averageReservedRateButRejected;


}
