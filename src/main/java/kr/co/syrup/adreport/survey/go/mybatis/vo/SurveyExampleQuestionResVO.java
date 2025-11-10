package kr.co.syrup.adreport.survey.go.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SurveyExampleQuestionResVO implements Serializable {

    private static final long serialVersionUID = -8583711000440418708L;

    private String exampleQuestionAnswer;

    private Integer sort;
}
