package kr.co.syrup.adreport.survey.go.dto.request;

import kr.co.syrup.adreport.survey.go.entity.SurveyExampleQuestionEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubjectExampleSodarReqDto implements Serializable {

    private static final long serialVersionUID = 951791029665514699L;

    private Long surveyExampleId;

    // 보기 문구 입력
    private String exampleTitle;

    // 보기 이미지 URL
    private String exampleImgUrl;

    // 지정이동 설정 (타겟 문항 번호)
    private Integer targetSubjectNumber;

    // 보기 가중치 타입 (자유입력 : FREE, 숫자지정 : ASSIGN ) - 퀴즈형, 분석형 한정
    private String exampleWeightType;

    // 보기 가중치 값
    private String exampleWeightValue;

    // sort order
    private Integer sort;

}
