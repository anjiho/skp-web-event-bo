package kr.co.syrup.adreport.framework.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExcelReadOption {

    // 엑셀파일 경로
    private String filePath;

    // 추출할 컬럼명 목록
    private List<String> outputColumns;

    private List<Object> outputColumns2;

    //추출을 시작할 행 번호
    private int startRow;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<String> getOutputColumns() {
        List<String> temp = new ArrayList<String>();
        temp.addAll(outputColumns);
        return temp;
    }

    public List<Object> getOutputColumns2() {
        List<Object> temp = new ArrayList<Object>();
        temp.addAll(outputColumns2);
        return temp;
    }

    public void setOutputColumns(List<String> outputColumns) {
        List<String> temp = new ArrayList<String>();
        temp.addAll(outputColumns);
        this.outputColumns = temp;
    }

    public void setOutputColumns(String... outputColumns) {
        if (this.outputColumns == null) {
            this.outputColumns = new ArrayList<String>();
        }

        for (String outputColumn : outputColumns) {
            this.outputColumns.add(outputColumn);
        }
    }

    public void setOutputColumns2(Object... outputColumns) {
        if (this.outputColumns2 == null) {
            this.outputColumns2 = new ArrayList<Object>();
        }

        for (Object outputColumn : outputColumns) {
            this.outputColumns2.add(outputColumn);
        }
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }
}
