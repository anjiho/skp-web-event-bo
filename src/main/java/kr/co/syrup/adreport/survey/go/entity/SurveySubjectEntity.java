package kr.co.syrup.adreport.survey.go.entity;

import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.survey.go.dto.request.SurveySubjectSodarReqDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@DynamicInsert
@ToString
@Getter
@Setter
@Entity
@Table(name = "SURVEY_SUBJECT")
public class SurveySubjectEntity implements Serializable {

    // index
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_subject_id")
    private Long surveySubjectId;

    @Column(name = "ar_event_id")
    private Integer arEventId;

    // 문항 제목
    @Column(name = "subject_title")
    private String subjectTitle;

    // 문항 설명 (보조문구)
    @Column(name = "subject_sub_text")
    private String subjectSubText;

    // 이미지/영상 선택여부 (YN)
    @Column(name = "img_video_yn")
    private String imgVideoYn;

    // 이미지/영상 등록 타입 선택(이미지 : IMG , 영상 : VIDEO)
    @Column(name = "img_video_reg_type")
    private String imgVideoRegType;

    // 컨텐츠 URL (이미지 또는 영상)
    @Column(name = "content_url")
    private String contentUrl;

    // 보기 유형 설정 (객관식 : CHOICE, 주관식 : QUESTION )
    @Column(name = "subject_example_type")
    private String subjectExampleType;

    // 객관식 유형 설정 (가로형 : WIDTH, 세로형 : HEIGHT, 이미지형 : IMG , 척도형 : SCALE, OX 형 : OX, 텍스트형 : TEXT ) - 텍스트형과 이미지형은 대화형 한정
    @Column(name = "example_choice_type")
    private String exampleChoiceType;

    // 보기 설정 여부 (YN) - 대화형 한정
    @Column(name = "talk_example_setting_yn")
    private String talkExampleSettingYn;

    // 복수 정답 선택 여부 (YN)
    @Column(name = "multiple_answer_yn")
    private String multipleAnswerYn;

    // 복수 정답 개수
    @Column(name = "multiple_answer_count")
    private Integer multipleAnswerCount;

    // 유형 분류 수 - 퀴즈형, 분석형 한정
    @Column(name = "survey_category_type_count")
    private Integer surveyCategoryTypeCount;

    // 정답 보기 설정 (퀴즈형 > 객관식 - 가로/세로/이미지형)
    @Column(name = "quiz_answer_sort")
    private Integer quizAnswerSort;

    // 보기 개수 설정
    @Column(name = "example_count")
    private Integer exampleCount;

    // 기타 의견 받기 여부 (YN)
    @Column(name = "etc_opinion_receive_yn")
    private String etcOpinionReceiveYn;

    // 힌트 설정 이미지 여부
    @Column(name = "hint_img_yn")
    private Boolean hintImgYn;

    // 힌트 설정 이미지 URL
    @Column(name = "hint_img_url")
    private String hintImgUrl;

    // 정답 설명 이미지 여부
    @Column(name = "answer_desc_img_yn")
    private Boolean answerDescImgYn;

    // 정답 설명 이미지 여부 URL
    @Column(name = "answer_desc_img_url")
    private String answerDescImgUrl;

    // 정답 설명 이미지 여부
    @Column(name = "wrong_desc_img_yn")
    private Boolean wrongDescImgYn;

    // 정답 설명 이미지 URL
    @Column(name = "wrong_desc_img_url")
    private String wrongDescImgUrl;

    // 보기 유형 (기본 : BASIC , 표정 : EXPRESSION , 이미지삽입  : IMG ) - 척도형 한정
    @Column(name = "scale_view_type")
    private String scaleViewType;

    // 척도형 보기 문구
    @Column(name = "scale_text")
    private String scaleText;

    // sort order
    @Column(name = "sort")
    private Integer sort;

    //지정이동 설정 (타겟 문항 번호)
    @Column(name = "target_subject_number")
    private Integer targetSubjectNumber;

    @Column(name = "created_by")
    private String createdBy;

    // now()
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    public static SurveySubjectEntity updateOf(SurveySubjectSodarReqDto reqDto) {
        SurveySubjectEntity entity = new SurveySubjectEntity();
        entity.setSurveySubjectId(reqDto.getSurveySubjectId());
        entity.setSubjectTitle(reqDto.getSubjectTitle());
        entity.setSubjectSubText(reqDto.getSubjectSubText());
        entity.setImgVideoYn(reqDto.getImgVideoYn());
        entity.setImgVideoRegType(reqDto.getImgVideoRegType());
        entity.setContentUrl(reqDto.getContentUrl());
        entity.setSubjectExampleType(reqDto.getSubjectExampleType());
        entity.setExampleChoiceType(reqDto.getExampleChoiceType());
        entity.setTalkExampleSettingYn(reqDto.getTalkExampleSettingYn());
        entity.setMultipleAnswerYn(reqDto.getMultipleAnswerYn());
        entity.setMultipleAnswerCount(reqDto.getMultipleAnswerCount());
        entity.setSurveyCategoryTypeCount(reqDto.getSurveyCategoryTypeCount());
        entity.setQuizAnswerSort(reqDto.getQuizAnswerSort());
        entity.setExampleCount(reqDto.getExampleCount());
        entity.setEtcOpinionReceiveYn(reqDto.getEtcOpinionReceiveYn());
        entity.setHintImgYn(reqDto.getHintImgYn());
        entity.setHintImgUrl(reqDto.getHintImgUrl());
        entity.setAnswerDescImgYn(reqDto.getAnswerDescImgYn());
        entity.setAnswerDescImgUrl(reqDto.getAnswerDescImgUrl());
        entity.setWrongDescImgYn(reqDto.getWrongDescImgYn());
        entity.setWrongDescImgUrl(reqDto.getWrongDescImgUrl());
        entity.setSort(reqDto.getSort());
        entity.setScaleText(reqDto.getScaleText());
        entity.setScaleViewType(reqDto.getScaleViewType());
        entity.setTargetSubjectNumber(PredicateUtils.isNotNull(reqDto.getTargetSubjectNumber()) ? reqDto.getTargetSubjectNumber() : null);
        return entity;
    }

}
