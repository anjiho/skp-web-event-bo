package kr.co.syrup.adreport.survey.go.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@ToString
@Getter
@Setter
@Entity
@Table(name = "SURVEY_TARGET_AGE_GENDER_LIMIT")
public class SurveyTargetAgeGenderLimitEntity implements Serializable {

    private static final long serialVersionUID = 2884555022163249715L;

    // index
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_target_age_gender_limit_id")
    private Long surveyTargetAgeGenderLimitId;

    // AR 이벤트 아이디
    @Column(name = "ar_event_id")
    private Integer arEventId;

    // 성별 (남자 : M, 여자 : F )
    @Column(name = "survey_target_gender")
    private String surveyTargetGender;

    // 연령
    @Column(name = "survey_target_age")
    private Integer surveyTargetAge;

    // 회수
    @Column(name = "survey_target_limit_count")
    private Integer surveyTargetLimitCount;

    @Column(name = "created_by")
    private String createdBy;

    // now()
    @Column(name = "created_date")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date lastModifiedDate;

}
