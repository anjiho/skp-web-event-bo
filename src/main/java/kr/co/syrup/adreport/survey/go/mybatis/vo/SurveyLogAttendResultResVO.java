package kr.co.syrup.adreport.survey.go.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SurveyLogAttendResultResVO implements Serializable {

    private static final long serialVersionUID = -3113091831034804133L;

    private String surveyLogAttendId;

    private Long surveySubjectId;

    private Long surveyExampleId;

    private Integer subjectSort;

    private Integer exampleSort;

    private Boolean isAnswer;

    private String questionAnswer;

    private String createdDate;

    private String exampleTitle;
}
