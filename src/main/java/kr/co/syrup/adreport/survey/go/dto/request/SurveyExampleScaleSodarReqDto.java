package kr.co.syrup.adreport.survey.go.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SurveyExampleScaleSodarReqDto implements Serializable {

    private static final long serialVersionUID = -9158122496065444953L;

    private Long surveyExampleScaleId;

    // 보기 문구
    private String exampleScaleText;

    // sort order
    private Integer sort;
}
