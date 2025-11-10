package kr.co.syrup.adreport.framework.utils;

import org.apache.poi.ss.usermodel.*;

public class ExcelCellStyle {

    public static CellStyle getCellStyle(Workbook wb, BorderStyle top, BorderStyle bottom, BorderStyle left, BorderStyle right,
                                         short backgroundColorIdx, short fontColorIdx, Boolean isBold) {
        
        CellStyle cellStyle = wb.createCellStyle();

        if (top != null) cellStyle.setBorderTop(top); //테두리 위쪽
        if (bottom != null) cellStyle.setBorderBottom(bottom); //테두리 아래쪽
        if (left != null) cellStyle.setBorderLeft(left); //테두리 왼쪽
        if (right != null) cellStyle.setBorderRight(right); //테두리 오른쪽
        if (backgroundColorIdx > 0) {
            cellStyle.setFillForegroundColor(backgroundColorIdx);  // 배경색
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);	//채우기 적용
        }
        Font font = wb.createFont();

        if (fontColorIdx > 0) font.setColor(fontColorIdx);  //폰트 색
        if (isBold != null) font.setBold(isBold);   //폰트 굵기

        cellStyle.setFont(font);

        return cellStyle;
    }
    
}
