package kr.co.syrup.adreport.survey.go.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SurveyTableRawAnswerResVO implements Serializable {

    private static final long serialVersionUID = 5164183784980069304L;

    private Integer subjectSort;

    private Integer exampleSort;

    private String questionAnswer;

    private String etcOpinionReceiveYn;

    private String subjectExampleType;
}
