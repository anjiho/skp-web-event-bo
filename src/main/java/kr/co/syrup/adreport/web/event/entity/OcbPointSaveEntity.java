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
@Table(name = "OCB_POINT_SAVE")
public class OcbPointSaveEntity implements Serializable {

    private static final long serialVersionUID = -4499557189044736662L;

    // 포토 추가건 인덱스
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ocb_point_save_id")
    private Integer id;

    // 포토 추가건 AR 이벤트 아이디
    @Column(name = "ar_event_id")
    private Integer arEventId;

    // 포토 추가건 AR 이벤트 당첨 아이디
    @Column(name = "ar_event_winning_id")
    private Integer arEventWinningId;

    // 포토 추가건 OCB 포인트 적립코드
    @Column(name = "ocb_point_save_code")
    private String ocbPointSaveCode;

    // 포토 추가건 사업자번호
    @Column(name = "business_number")
    private String businessNumber;

    // 포토 추가건 적립 기간 (이벤트 기간내 / 1일)
    @Column(name = "save_term_type")
    private Integer saveTermType;

    // 포토 추가건 최대적립고객수
    @Column(name = "save_max_customer_count")
    private Integer saveMaxCustomerCount;

    //포토 추가건 기타 선택 여부 (YN)
    @Column(name = "is_etc")
    private Boolean isEtc;

    // 포토 추가건 적립금액
    @Column(name = "save_point")
    private Integer savePoint;

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

    public static OcbPointSaveEntity ofUpdate(OcbPointSaveEntity findEntity, OcbPointSaveEntity newEntity) {
        OcbPointSaveEntity updateEntity = new OcbPointSaveEntity();
        updateEntity.setId(findEntity.getId());
        updateEntity.setArEventId(findEntity.getArEventId());
        updateEntity.setArEventWinningId(PredicateUtils.isNull(newEntity.getArEventWinningId()) ? findEntity.getArEventWinningId() : newEntity.getArEventWinningId());
        updateEntity.setOcbPointSaveCode(PredicateUtils.isNull(newEntity.getOcbPointSaveCode()) ? findEntity.getOcbPointSaveCode() : newEntity.getOcbPointSaveCode());
        updateEntity.setBusinessNumber(PredicateUtils.isNull(newEntity.getBusinessNumber()) ? findEntity.getBusinessNumber() : newEntity.getBusinessNumber());
        updateEntity.setSaveTermType(PredicateUtils.isNull(newEntity.getSaveTermType()) ? findEntity.getSaveTermType() : newEntity.getSaveTermType());
        updateEntity.setSaveMaxCustomerCount(PredicateUtils.isNull(newEntity.getSaveMaxCustomerCount()) ? findEntity.getSaveMaxCustomerCount() : newEntity.getSaveMaxCustomerCount());
        updateEntity.setIsEtc(PredicateUtils.isNull(newEntity.getIsEtc()) ? findEntity.getIsEtc() : newEntity.getIsEtc());
        updateEntity.setSavePoint(PredicateUtils.isNull(newEntity.getSavePoint()) ? findEntity.getSavePoint() : newEntity.getSavePoint());
        updateEntity.setCreatedDate(findEntity.getCreatedDate());
        return updateEntity;
    }
}
