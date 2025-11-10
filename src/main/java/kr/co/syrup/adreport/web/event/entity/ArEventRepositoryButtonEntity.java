package kr.co.syrup.adreport.web.event.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@DynamicInsert
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "AR_EVENT_REPOSITORY_BUTTON")
public class ArEventRepositoryButtonEntity implements Serializable {

    private static final long serialVersionUID = -1107464424847871934L;

    // index
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ar_event_repository_button_id")
    private Long arEventRepositoryButtonId;

    @Column(name = "ar_event_winning_id")
    private Integer arEventWinningId;

    // 버튼 타입 (사용확인 : CONFIRM, URL 접속 : URL)
    @Column(name = "repository_button_type")
    private String repositoryButtonType;

    // 버튼 문구
    @Column(name = "repository_button_text")
    private String repositoryButtonText;

    // 버튼 이동 URL
    @Column(name = "repository_button_target_url")
    private String repositoryButtonTargetUrl;

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
}
