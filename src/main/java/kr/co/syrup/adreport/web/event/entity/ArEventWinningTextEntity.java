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
@Table(name = "AR_EVENT_WINNING_TEXT")
public class ArEventWinningTextEntity implements Serializable {

    private static final long serialVersionUID = 9015981947286895923L;

    // index
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ar_event_winning_text_id")
    private Long arEventWinningTextId;

    @Column(name = "ar_event_winning_id")
    private Integer arEventWinningId;

    // 컨텐츠 문구
    @Column(name = "winning_text")
    private String winningText;

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
