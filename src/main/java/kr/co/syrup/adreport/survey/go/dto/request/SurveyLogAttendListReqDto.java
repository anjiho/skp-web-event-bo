package kr.co.syrup.adreport.survey.go.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SurveyLogAttendListReqDto implements Serializable {

    private static final long serialVersionUID = -2682119682916230622L;

    private Long surveySubjectId;

    private Long surveyExampleId;

    private Integer subjectSort;

    private Integer exampleSort;

    private Boolean isAnswer;

    private String questionAnswer;
}
