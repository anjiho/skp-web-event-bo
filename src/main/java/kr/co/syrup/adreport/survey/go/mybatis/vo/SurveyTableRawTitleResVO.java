package kr.co.syrup.adreport.survey.go.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SurveyTableRawTitleResVO implements Serializable {

    private static final long serialVersionUID = 865421137529166872L;

    private Long surveySubjectId;

    private Integer sort;

    private String title;

    private String etcTitle;
}
