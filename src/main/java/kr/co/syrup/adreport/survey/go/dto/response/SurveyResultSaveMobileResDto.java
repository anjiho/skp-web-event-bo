package kr.co.syrup.adreport.survey.go.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SurveyResultSaveMobileResDto implements Serializable {
    private static final long serialVersionUID = 5345347397015975313L;

    // 총 답변 개수 - 퀴즈형 한정
    private int answerTotalCount;

    // 이 중 정답 개수 - 퀴즈형 한정
    private int answerCount;

    // 유형 카테고리 ID
    private Long surveySubjectCategoryId = Long.valueOf(-1);

    // 유형 제목 입력
    private String categoryTitle;

    // 유형 설명 입력 (보조문구)
    private String categorySubText;

    // 유형 이미지 URL
    private String categoryImgUrl;

    // 서베이고 추가건 설문 완료 버튼 종류
    private String surveyEndButtonType;

    // 서베이고 추가건 설문 완료 버튼 문구
    private String surveyEndButtonText;

    // 서베이고 추가건 설문 완료 문구 - 대화형 한정
    private String talkSurveyEndText;

}
