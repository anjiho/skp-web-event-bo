package kr.co.syrup.adreport.survey.go.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SurveySubjectCategoryReqDto implements Serializable {

    private static final long serialVersionUID = 7000961484006057464L;

    private Long surveySubjectCategoryId;

    private Long surveySubjectId;

    // sort order
    private Integer sort;

    // 가중치 최소 (정답율과 함께 사용)
    private String weightMin;

    // 가중치 최대 (정답율과 함께 사용)
    private String weightMax;

    // 유형 제목 입력
    private String categoryTitle;

    // 유형 설명 입력 (보조문구)
    private String categorySubText;

    // 유형 이미지 URL
    private String categoryImgUrl;

    // 버튼 타입 (경품추첨하기 : RAFFLE, 설문종료 : END)
    private String categoryButtonType;

    // 유형 결과 로딩 문구
    private String categoryResultText;

    // 버튼 문구
    private String categoryButtonText;
}
