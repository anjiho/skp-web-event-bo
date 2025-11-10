package kr.co.syrup.adreport.framework.utils;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class ExcelCellRef {

	/**
	 * <pre>
	 * 1. Comment : 사용자리스트 엑셀 다운로드
	 * 2. 작성자 : 안지호
	 * 3. 작성일 : 2016. 03. 11
	 * 4. 설명 : Cell에 해당하는 Column Name을 가젼온다(A,B,C..)
     * 만약 Cell이 Null이라면 int cellIndex의 값으로
     * Column Name을 가져온다.
	 * </pre>
	 * @param cell
	 * @param cellIndex
	 * @return
	 */
	public static String getName(Cell cell, int cellIndex) {
		int cellNum = 0;
		if (cell != null) {
			cellNum = cell.getColumnIndex();
		} else {
			cellNum = cellIndex;
		}
		return CellReference.convertNumToColString(cellNum);
	}

	public static String getValue(Cell cell) {
		String value = "";

		if(cell == null) {
            // value = "";
			return value;
        }
		// SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
        switch(cell.getCellType()) {
            case FORMULA :
                value = cell.getCellFormula();
                break;

            case NUMERIC :
				cell.setCellType(CellType.STRING);
				value = cell.getStringCellValue();
                break;
//                value = cell.getNumericCellValue() + "";
//                break;

            case STRING :
                value = cell.getStringCellValue();
                break;

            case BOOLEAN :
                value = cell.getBooleanCellValue() + "";
                break;

            case BLANK :
                value = "";
                break;

            case ERROR :
                value = cell.getErrorCellValue() + "";
                break;
            default:
                value = cell.getStringCellValue();
        }
		return value;
	}

}
