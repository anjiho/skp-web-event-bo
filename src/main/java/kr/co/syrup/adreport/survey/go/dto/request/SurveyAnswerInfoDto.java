package kr.co.syrup.adreport.survey.go.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SurveyAnswerInfoDto implements Serializable {
    private static final long serialVersionUID = 7486857791017704549L;

    // 문항 sort 번호
    private Integer subjectSort;

    // 문항 ID
    private Long surveySubjectId;

    // 객관식 정답 (복수 가능)
    private List<SurveyAnswerExampleDto> surveyExampleList;

    // 주관식 정답 (단수) / 객관식인 경우 기타 의견
    private String questionAnswer;


}
