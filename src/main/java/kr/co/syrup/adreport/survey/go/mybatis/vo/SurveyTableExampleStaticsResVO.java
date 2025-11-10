package kr.co.syrup.adreport.survey.go.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SurveyTableExampleStaticsResVO implements Serializable {

    private static final long serialVersionUID = -5387611405286894333L;

    private Integer sort;

    private String exampleTitle;

    private Integer exampleTotalCount;

    private Double percent;
}
