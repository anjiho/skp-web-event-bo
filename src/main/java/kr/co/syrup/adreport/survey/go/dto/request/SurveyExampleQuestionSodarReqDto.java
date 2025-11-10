package kr.co.syrup.adreport.survey.go.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SurveyExampleQuestionSodarReqDto implements Serializable {

    private static final long serialVersionUID = -1341394120787971965L;

    private Long surveyExampleQuestionId;

    // 주관식 정답
    private String exampleQuestionAnswer;

    // sort order
    private Integer sort;

}
