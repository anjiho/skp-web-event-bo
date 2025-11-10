package kr.co.syrup.adreport.survey.go.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SurveySubjectInfoMobileDto implements Serializable {

    private static final long serialVersionUID = -7647346500335691405L;

    private Long surveySubjectId;

    // 문항 제목
    private String subjectTitle;

    // 문항 설명 (보조문구)
    private String subjectSubText;

    // 이미지/영상 선택여부 (YN)
    private String imgVideoYn;

    // 이미지/영상 등록 타입 선택(이미지 : IMG , 영상 : VIDEO)
    private String imgVideoRegType;

    // 컨텐츠 URL (이미지 또는 영상)
    private String contentUrl;

    // 보기 유형 설정 (객관식 : CHOICE, 주관식 : QUESTION )
    private String subjectExampleType;

    // 객관식 유형 설정 (가로형 : WIDTH, 세로형 : HEIGHT, 이미지형 : IMG , 척도형 : SCALE, OX 형 : OX, 텍스트형 : TEXT ) - 텍스트형과 이미지형은 대화형 한정
    private String exampleChoiceType;

    // 보기 설정 여부 (YN) - 대화형 한정
    private String talkExampleSettingYn;

    // 복수 정답 선택 여부 (YN)
    private String multipleAnswerYn;

    // 복수 정답 개수
    private Integer multipleAnswerCount;

    // 유형 분류 수 - 퀴즈형, 분석형 한정
    private Integer surveyCategoryTypeCount;

    // 정답 보기 설정 (퀴즈형 > 객관식 - 가로/세로/이미지형)
    private String quizAnswerSort;

    // 보기 개수 설정
    private Integer exampleCount;

    // 기타 의견 받기 여부 (YN)
    private String etcOpinionReceiveYn;

    // 힌트 설정 이미지 여부
    private Boolean hintImgYn;

    // 힌트 설정 이미지 URL
    private String hintImgUrl;

    // 정답 설명 이미지 여부
    private Boolean answerDescImgYn;

    // 정답 설명 이미지 여부 URL
    private String answerDescImgUrl;

    // 정답 설명 이미지 여부
    private Boolean wrongDescImgYn;

    // 정답 설명 이미지 URL
    private String wrongDescImgUrl;

    // 보기 유형 (기본 : BASIC , 표정 : EXPRESSION , 이미지삽입  : IMG ) - 척도형 한정
    private String scaleViewType;

    // 척도형 보기 문구
    private String scaleText;

    // sort order
    private Integer sort;

    // 문항 지정 이동
    private Integer targetSubjectNumber;

    //보기 정보 리스트
    private List<SubjectExampleSodarReqDto> exampleInfo;

    // 문항 팝업 이미지 정보
    private List<SurveySubjectPopupImageReqDto> popupImageInfo;

    // 주관식 답안
    private List<SurveyExampleQuestionSodarReqDto> exampleQuestionAnswer;


}
