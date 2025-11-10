package kr.co.syrup.adreport.survey.go.entity;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
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
@Table(name = "SURVEY_LOG_ATTEND")
public class SurveyLogAttendEntity implements Serializable {

    private static final long serialVersionUID = -30564447357567581L;

    @Id
    @Column(name = "survey_log_attend_id")
    private String surveyLogAttendId;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "ar_event_id")
    private Integer arEventId;

    @Column(name = "gender")
    private String gender;

    @Column(name = "age")
    private Integer age;

    @Column(name = "is_submit")
    private Boolean isSubmit;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "attend_code")
    private String attendCode;

    @Column(name = "give_away_id")
    private Integer giveAwayId;

    @Column(name = "created_date")
    private Date createdDate;

    @PrePersist
    public void prePersist() {
        this.isSubmit = PredicateUtils.isNull(this.isSubmit) ? false : this.isSubmit;
    }


}
