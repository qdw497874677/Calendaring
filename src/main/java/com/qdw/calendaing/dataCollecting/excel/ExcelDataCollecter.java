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
        String[] colNames = {"ʵ�����","ƽ����ʱ/ms","ƽ�������","ƽ�����ݴ�����","ƽ��δ�����������"};

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

        //��������
        List<ExcelModel> dataList = new ArrayList<>();
//        dataList.add(new ExcelModel("����",12,"13867098765","��"));
//        dataList.add(new ExcelModel("����1",12,"13867098765","��"));
//        dataList.add(new ExcelModel("����2",12,"13867098765","��"));
//        dataList.add(new ExcelModel("����3",12,"13867098765","��"));

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
