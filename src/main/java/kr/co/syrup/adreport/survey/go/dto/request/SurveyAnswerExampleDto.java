package kr.co.syrup.adreport.survey.go.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SurveyAnswerExampleDto implements Serializable {

    private static final long serialVersionUID = 4293032309434476055L;

    // 보기 sort 번호
    private Integer exampleSort;

    // 보기 ID
    private Long surveyExampleId;

}
