package kr.co.syrup.adreport.survey.go.entity;

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
@Table(name = "SURVEY_SUBJECT_POPUP_IMAGE")
public class SurveySubjectPopupImageEntity implements Serializable {

    private static final long serialVersionUID = -838348222810329916L;

    // 인덱스
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_subject_popup_image_id")
    private Long surveySubjectPopupImageId;

    // 문항 인덱스
    @Column(name = "survey_subject_id")
    private Long surveySubjectId;

    // 이미지 url
    @Column(name = "popup_img_url")
    private String popupImgUrl;

    // 순서
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
}
