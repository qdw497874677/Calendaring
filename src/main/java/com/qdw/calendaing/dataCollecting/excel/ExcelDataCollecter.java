package com.qdw.calendaing.dataCollecting.excel;

import com.qdw.calendaing.dataCollecting.DataCollecter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Quandw
 * @Description:
 * @Date: 2021/1/19 0019 20:11
 */
public class ExcelDataCollecter implements DataCollecter {




    @Override
    public boolean addData(String data) {

        XSSFWorkbook workbook = new XSSFWorkbook();
        String[] colNames = {"实验次数","平均用时/ms","平均完成率","平均数据传输率","平均未完成请求传输率"};

        XSSFSheet sheet = workbook.createSheet();
        XSSFFont font = workbook.createFont();
        font.setFontName("simsun");
        font.setBold(true);
        font.setColor(IndexedColors.BLACK.index);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        cellStyle.setFont(font);

        Row titleRow = sheet.createRow(0);
        for (int i = 0; i < colNames.length; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(colNames[i]);
            cell.setCellStyle(cellStyle);
        }

        //构造数据
        List<ExcelModel> dataList = new ArrayList<>();
//        dataList.add(new ExcelModel("张三",12,"13867098765","男"));
//        dataList.add(new ExcelModel("张三1",12,"13867098765","男"));
//        dataList.add(new ExcelModel("张三2",12,"13867098765","男"));
//        dataList.add(new ExcelModel("张三3",12,"13867098765","男"));

        return false;
    }

    private ExcelModel StringToModel(String data){
        ExcelModel excelModel = new ExcelModel();
        String[] split = data.split("\\n");
        for (String s : split) {
            excelModel.setTime(Integer.parseInt(s.split(":")[1]));
            ;
        }

        return null;

    }
}
