package kr.co.syrup.adreport.survey.go.entity;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.survey.go.define.ExampleWeightTypeDefine;
import kr.co.syrup.adreport.survey.go.define.ScaleViewTypeDefine;
import kr.co.syrup.adreport.survey.go.dto.request.SubjectExampleSodarReqDto;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
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
@Table(name = "SURVEY_EXAMPLE")
public class SurveyExampleEntity implements Serializable {

    private static final long serialVersionUID = 8418676992969811620L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_example_id")
    private Long surveyExampleId;

    @Column(name = "survey_subject_id")
    private Long surveySubjectId;

    // 보기 문구 입력
    @Column(name = "example_title")
    private String exampleTitle;

    // 보기 이미지 URL
    @Column(name = "example_img_url")
    private String exampleImgUrl;

    // 지정이동 설정 (타겟 문항 번호)
    @Column(name = "target_subject_number")
    private Integer targetSubjectNumber;

    // 보기 가중치 타입 (자유입력 : FREE, 숫자지정 : ASSIGN ) - 퀴즈형, 분석형 한정
    @Column(name = "example_weight_type")
    private String exampleWeightType;

    // 보기 가중치 값
    @Column(name = "example_weight_value")
    private String exampleWeightValue;

    // sort order
    @Column(name = "sort")
    private Integer sort;

    @Column(name = "created_by")
    private String createdBy;

    // now()
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = this.createdDate == null ? DateUtils.returnNowDate() : this.createdDate;
    }

//    public static SurveyExampleEntity saveOf(long surveySubjectId, SubjectExampleSodarReqDto reqDto) {
//        SurveyExampleEntity exampleEntity = new SurveyExampleEntity();
//        exampleEntity.setSurveyExampleId(surveySubjectId);
//        exampleEntity.setExampleTitle(reqDto.getExampleTitle().trim());
//        exampleEntity.setExampleImgUrl(reqDto.getExampleImgUrl());
//        exampleEntity.setScaleViewType(PredicateUtils.isNull(reqDto.getScaleViewType()) ? ScaleViewTypeDefine.BASIC.name() : reqDto.getScaleViewType());
//        exampleEntity.setExampleChoiceAnswerYn(reqDto.getExampleChoiceAnswerYn());
//        exampleEntity.setHintImgYn(reqDto.getHintImgYn());
//        exampleEntity.setHintImgUrl(reqDto.getHintImgUrl());
//        exampleEntity.setAnswerDescImgYn(reqDto.getAnswerDescImgYn());
//        exampleEntity.setAnswerDescImgUrl(reqDto.getAnswerDescImgUrl());
//        exampleEntity.setWrongDescImgYn(reqDto.getWrongDescImgYn());
//        exampleEntity.setWrongDescImgUrl(reqDto.getWrongDescImgUrl());
//        exampleEntity.setTargetSubjectNumber(reqDto.getTargetSubjectNumber());
//        exampleEntity.setExampleWeightType(PredicateUtils.isNull(reqDto.getExampleWeightType()) ? ExampleWeightTypeDefine.FREE.name() : reqDto.getExampleWeightType());
//        exampleEntity.setExampleWeightValue(reqDto.getExampleWeightValue());
//        exampleEntity.setEtcOpinionReceiveYn(reqDto.getEtcOpinionReceiveYn());
//        exampleEntity.setSort(reqDto.getSort());
//        return exampleEntity;
//    }

    public static SurveyExampleEntity updateOf(SubjectExampleSodarReqDto reqDto) {
        SurveyExampleEntity exampleEntity = new SurveyExampleEntity();
        exampleEntity.setSurveyExampleId(reqDto.getSurveyExampleId());
        exampleEntity.setExampleTitle(reqDto.getExampleTitle());
        exampleEntity.setExampleImgUrl(reqDto.getExampleImgUrl());
        exampleEntity.setTargetSubjectNumber(reqDto.getTargetSubjectNumber());
        exampleEntity.setExampleWeightType(reqDto.getExampleWeightType());
        exampleEntity.setExampleWeightValue(reqDto.getExampleWeightValue());
        exampleEntity.setSort(reqDto.getSort());
        return exampleEntity;
    }
}
