package kr.co.syrup.adreport.web.event.entity;

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

@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "AR_EVENT_WINNING_BUTTON_ADD")
public class ArEventWinningButtonAddEntity implements Serializable {

    private static final long serialVersionUID = -1931581879646024674L;

    // 포토 추가건 인덱스
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ar_event_winning_button_add_id")
//    private Long arEventWinningButtonAddId;
    private Long id;

    // 포토 추가건
    @Column(name = "ar_event_winning_button_id")
    private Integer arEventWinningButtonId;

    // 포토 추가건 필드 이름
    @Column(name = "field_name")
    private String fieldName;

    // 포토 추가건 필드 타입 (문자형 : CHAR / 숫자형 : INT)
    @Column(name = "field_type")
    private String fieldType;

    // 포토 추가건 필드 길이 (필드타입이 숫자형일때 필수)
    @Column(name = "field_length")
    private Integer fieldLength;

    // 포토 추가건 생성자
    @Column(name = "created_by")
    private String createdBy;

    // 포토 추가건 생성일
    @Column(name = "created_date")
    private Date createdDate;

    // 포토 추가건 수정자
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    // 포토 추가건 수정일
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = DateUtils.returnNowDate();
    }

    public static ArEventWinningButtonAddEntity ofUpdate(ArEventWinningButtonAddEntity findEntity, ArEventWinningButtonAddEntity newEntity) {
        ArEventWinningButtonAddEntity updateEntity = new ArEventWinningButtonAddEntity();
        updateEntity.setId(findEntity.getId());
        updateEntity.setArEventWinningButtonId(findEntity.getArEventWinningButtonId());
        updateEntity.setFieldName(PredicateUtils.isNull(newEntity.getFieldName()) ? findEntity.getFieldName() : newEntity.getFieldName());
        updateEntity.setFieldType(PredicateUtils.isNull(newEntity.getFieldType()) ? findEntity.getFieldType() : newEntity.getFieldType());
        updateEntity.setFieldLength(PredicateUtils.isNull(newEntity.getFieldLength()) ? findEntity.getFieldLength() : newEntity.getFieldLength());
        updateEntity.setCreatedDate(findEntity.getCreatedDate());
        return updateEntity;
    }
}
