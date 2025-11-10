package kr.co.syrup.adreport.survey.go.entity;

import kr.co.syrup.adreport.survey.go.dto.request.SurveyExampleQuestionSodarReqDto;
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
@Table(name = "SURVEY_EXAMPLE_QUESTION")
public class SurveyExampleQuestionEntity implements Serializable {

    private static final long serialVersionUID = -6796400600316706340L;

    // index
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surveyExampleQuestionId;

    private Long surveySubjectId;

    // 주관식 정답
    private String exampleQuestionAnswer;

    // sort order
    private Integer sort;

    private String createdBy;

    // now()
    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;

}
