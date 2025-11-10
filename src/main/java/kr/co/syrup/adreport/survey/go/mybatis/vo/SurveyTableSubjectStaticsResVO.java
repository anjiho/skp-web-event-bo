package kr.co.syrup.adreport.survey.go.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SurveyTableSubjectStaticsResVO implements Serializable {

    private static final long serialVersionUID = 7203450525385134891L;

    private Long surveySubjectId;

    private Integer sort;

    private String subjectExampleType;

    private String etcOpinionReceiveYn;

    private Integer quizAnswerSort;

    private String exampleChoiceType;

    private Integer totalCount;
}
