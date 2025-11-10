package kr.co.syrup.adreport.framework.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelRead {

    protected static final Logger logger = LoggerFactory.getLogger(ExcelRead.class);

    public static List<Map<String, Object>> read(ExcelReadOption excelReadOption) throws Exception {
        /**
         * 엑셀 파일 자체
         * 엑셀 파일을 읽어 들인다.
         * FileUtil.getWorkbook() <-- 파일의 확장자에 따라서 적절하게 가져온다.
         */
        Workbook wb = FileUtils.getWorkbook(excelReadOption.getFilePath());
        /**
         * 엑셀 파일에서 첫번째 시트를 가지고 온다.
         */
        Sheet sheet = wb.getSheetAt(0);

        logger.info("Sheet 이름 : " + wb.getSheetName(0));
        logger.info("데이터가 있는 Sheet의 수 : " + wb.getNumberOfSheets());

        /**
         * sheet에서 유효한(데이터가 있는) 행의 개수를 가져온다.
         */
        int numOfRows = sheet.getPhysicalNumberOfRows();
        int numOfCells = 0;

        Row row = null;
        Cell cell = null;

        String cellName = "";
        Map<String, Object> map = null;
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        for (int rowIndex = excelReadOption.getStartRow()-1; rowIndex < numOfRows; rowIndex++) {
            /**
             * 워크북에서 가져온 시트에서 rowIndex에 해당하는 Row를 가져온다.
             * 하나의 Row는 여러개의 Cell을 가진다.
             */
            row = sheet.getRow(rowIndex);

            if (row != null) {
                /**
                 * 가져온 Row의 Cell의 개수를 구한다.
                 */
                numOfCells = row.getPhysicalNumberOfCells();
                /*
                 * 데이터를 담을 맵 객체 초기화
                 */
                map = new HashMap<String, Object>();
                /*
                 * cell의 수만 큼 반복한다.
                 */
                for (int cellIndex = 0; cellIndex < numOfCells; cellIndex++) {
                    /*
                     * Row에서 CellIndex에 해당하는 Cell을 가져온다.
                     */
                    cell = row.getCell(cellIndex);
                    /*
                     * 현재 Cell의 이름을 가져온다.
                     * Ex> A,B,C,D,.....
                     */
                    cellName = ExcelCellRef.getName(cell, cellIndex);
                    /*
                     * 추출 대상 컬럼인지 확안한다.
                     * 추출 대상 컬럼이 아니라면, for로 다시 올라간다.
                     */
                    if (!excelReadOption.getOutputColumns().contains(cellName)) {
                        continue;
                    }
                    if (StringUtils.isNotEmpty(cell.toString())) {
                   // if (PredicateUtils.isNotNull(cell)) {
                        map.put(cellName, ExcelCellRef.getValue(cell));
                    }
                }
                if (map.size() > 0) {
                    result.add(map);
                }
            }
        }
        return result;
    }

    public static List<Map<String, Object>> readObject(ExcelReadOption excelReadOption) throws Exception {
        /**
         * 엑셀 파일 자체
         * 엑셀 파일을 읽어 들인다.
         * FileUtil.getWorkbook() <-- 파일의 확장자에 따라서 적절하게 가져온다.
         */
        Workbook wb = FileUtils.getWorkbook(excelReadOption.getFilePath());
        /**
         * 엑셀 파일에서 첫번째 시트를 가지고 온다.
         */
        Sheet sheet = wb.getSheetAt(0);

        logger.info("Sheet 이름 : " + wb.getSheetName(0));
        logger.info("데이터가 있는 Sheet의 수 : " + wb.getNumberOfSheets());

        /**
         * sheet에서 유효한(데이터가 있는) 행의 개수를 가져온다.
         */
        int numOfRows = sheet.getPhysicalNumberOfRows();
        int numOfCells = 0;

        Row row = null;
        Cell cell = null;

        String cellName = "";
        Map<String, Object> map = null;
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        for (int rowIndex = excelReadOption.getStartRow()-1; rowIndex < numOfRows; rowIndex++) {
            /**
             * 워크북에서 가져온 시트에서 rowIndex에 해당하는 Row를 가져온다.
             * 하나의 Row는 여러개의 Cell을 가진다.
             */
            row = sheet.getRow(rowIndex);

            if (row != null) {
                /**
                 * 가져온 Row의 Cell의 개수를 구한다.
                 */
                numOfCells = row.getPhysicalNumberOfCells();
                /*
                 * 데이터를 담을 맵 객체 초기화
                 */
                map = new HashMap<String, Object>();
                /*
                 * cell의 수만 큼 반복한다.
                 */
                for (int cellIndex = 0; cellIndex < numOfCells; cellIndex++) {
                    /*
                     * Row에서 CellIndex에 해당하는 Cell을 가져온다.
                     */
                    cell = row.getCell(cellIndex);
                    /*
                     * 현재 Cell의 이름을 가져온다.
                     * Ex> A,B,C,D,.....
                     */
                    cellName = ExcelCellRef.getName(cell, cellIndex);
                    /*
                     * 추출 대상 컬럼인지 확안한다.
                     * 추출 대상 컬럼이 아니라면, for로 다시 올라간다.
                     */
                    if (!excelReadOption.getOutputColumns2().contains(cellName)) {
                        continue;
                    }
                    map.put(cellName, ExcelCellRef.getValue(cell));
                }
                result.add(map);
            }
        }
        return result;
    }
}
