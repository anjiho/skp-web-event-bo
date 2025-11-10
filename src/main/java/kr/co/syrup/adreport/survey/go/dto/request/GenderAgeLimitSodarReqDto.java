package kr.co.syrup.adreport.survey.go.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GenderAgeLimitSodarReqDto implements Serializable {

    private static final long serialVersionUID = -47970130868413174L;

    private Long surveyTargetAgeGenderLimitId;

    // 성별 (남자 : M, 여자 : F )
    private String surveyTargetGender;

    // 연령
    private Integer surveyTargetAge;

    // 회수
    private Integer surveyTargetLimitCount;
}
