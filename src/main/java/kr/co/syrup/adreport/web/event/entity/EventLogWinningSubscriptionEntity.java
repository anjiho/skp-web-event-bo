package kr.co.syrup.adreport.web.event.entity;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "event_log_winning_subscription")
public class EventLogWinningSubscriptionEntity implements Serializable {

    private static final long serialVersionUID = -7467062564861092196L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "ar_event_winning_id", nullable = false)
    private Integer arEventWinningId;

    //이벤트 경품 배송 정보 인덱스
    @Column(name = "give_away_id", nullable = false)
    private Integer giveAwayId;

    //sms 발송여부
    @Column(name = "is_sms_send", nullable = false)
    private Boolean isSmsSend;

    //생성일
    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @PrePersist
    private void prePersist() {
        this.createdDate = PredicateUtils.isNull(this.createdDate) ? DateUtils.returnNowDate() : this.createdDate;
        this.isSmsSend = false;
    }

    public static EventLogWinningSubscriptionEntity saveOf(int giveAwayId, int arEventWinningId) {
         EventLogWinningSubscriptionEntity saveEntity = new EventLogWinningSubscriptionEntity();
         saveEntity.setArEventWinningId(arEventWinningId);
         saveEntity.setGiveAwayId(giveAwayId);
         return saveEntity;
    }

}