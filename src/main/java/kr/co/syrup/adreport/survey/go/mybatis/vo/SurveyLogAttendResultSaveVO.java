package kr.co.syrup.adreport.survey.go.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SurveyLogAttendResultSaveVO implements Serializable {

    private static final long serialVersionUID = -49793958047758742L;

    private String surveyLogAttendId;

    private Long surveySubjectId;

    private Long surveyExampleId;

    private Integer subjectSort;

    private Integer exampleSort;

    private Boolean isAnswer;

    private String questionAnswer;
}
