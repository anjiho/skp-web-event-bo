package kr.co.syrup.adreport.survey.go.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SurveyTableCategoryStaticsResVO implements Serializable {

    private static final long serialVersionUID = -5983333134955486850L;

    private String title;

    private String weight;

    private Integer achieveCount;
}
