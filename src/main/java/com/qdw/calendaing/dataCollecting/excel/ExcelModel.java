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

    @ExcelProperty(value = "ʵ�����" , index = 0)
    private int time;

    @ExcelProperty(value = "ƽ����ʱ" , index = 1)
    private String avgTime;

    @ExcelProperty(value = "ƽ�������" , index = 2)
    private String averageAcceptRate;

    @ExcelProperty(value = "ƽ�����ݴ�����" , index = 3)
    private String averageThroughputRate;

    @ExcelProperty(value = "ƽ��δ�������Ĵ�����" , index = 4)
    private String averageReservedRateButRejected;


}
