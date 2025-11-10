package kr.co.syrup.adreport.survey.go.entity;

import kr.co.syrup.adreport.survey.go.dto.request.SurveySubjectCategoryReqDto;
import lombok.*;
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
@Table(name = "SURVEY_SUBJECT_CATEGORY")
public class SurveySubjectCategoryEntity implements Serializable {

    private static final long serialVersionUID = 3009176720523981036L;

    // index
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_subject_category_id")
    private Long surveySubjectCategoryId;

    @Column(name = "ar_event_id")
    private Integer arEventId;

    // sort order
    @Column(name = "sort")
    private Integer sort;

    // 가중치 최소 (정답율과 함께 사용)
    @Column(name = "weight_min")
    private String weightMin;

    // 가중치 최대 (정답율과 함께 사용)
    @Column(name = "weight_max")
    private String weightMax;

    // 유형 제목 입력
    @Column(name = "category_title")
    private String categoryTitle;

    // 유형 설명 입력 (보조문구)
    @Column(name = "category_sub_text")
    private String categorySubText;

    // 유형 이미지 URL
    @Column(name = "category_img_url")
    private String categoryImgUrl;

    // 버튼 타입 (경품추첨하기 : RAFFLE, 설문종료 : END)
    @Column(name = "category_button_type")
    private String categoryButtonType;

    // 버튼 문구
    @Column(name = "category_button_text")
    private String categoryButtonText;

    // 유형 결과 로딩 문구
    @Column(name = "category_result_text")
    private String categoryResultText;

    @Column(name = "created_by")
    private String createdBy;

    // now()
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    public static SurveySubjectCategoryEntity updateOf(SurveySubjectCategoryReqDto reqDto) {
        SurveySubjectCategoryEntity entity = new SurveySubjectCategoryEntity();
        entity.setSurveySubjectCategoryId(reqDto.getSurveySubjectCategoryId());
        entity.setSort(reqDto.getSort());
        entity.setWeightMin(reqDto.getWeightMin());
        entity.setWeightMax(reqDto.getWeightMax());
        entity.setCategoryTitle(reqDto.getCategoryTitle());
        entity.setCategorySubText(reqDto.getCategorySubText());
        entity.setCategoryImgUrl(reqDto.getCategoryImgUrl());
        entity.setCategoryButtonType(reqDto.getCategoryButtonType());
        entity.setCategoryButtonText(reqDto.getCategoryButtonText());
        return entity;
    }
}
