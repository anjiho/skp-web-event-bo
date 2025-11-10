package kr.co.syrup.adreport.survey.go.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SurveyTargetAgeGenderLimitResDto implements Serializable {

    private static final long serialVersionUID = 3880560803935864447L;

    private Long surveyTargetAgeGenderLimitId;

    private String surveyTargetGender;

    private Integer surveyTargetAge;

    private Integer surveyTargetLimitCount;
}
