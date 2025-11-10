package kr.co.syrup.adreport.web.event.entity;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "EVENT_LOG_SMS_SEND")
public class EventLogSmsSendEntity implements Serializable {

    private static final long serialVersionUID = -2678548929217454435L;

    // 아이디
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 아이디
    private Long id;

    @Column(name = "give_away_id")
    private Integer giveAwayId;

    @Column(name = "ar_event_winning_id")
    private Integer arEventWinningId;

    //수신자 핸드폰번호
    @Column(name = "receiver_phone_number")
    private String receiverPhoneNumber;
    //내용
    @Column(name = "sms_contents")
    private String smsContents;

    //발송 시간
    @Column(name = "send_date")
    private Date sendDate;

    //성공여부
    @Column(name = "is_success")
    private Boolean isSuccess;

    //생성일
    @Column(name = "created_date")
    private Date createdDate;

    @PrePersist
    public void prePersist() {
        this.isSuccess = true;
        this.createdDate = this.createdDate == null ? DateUtils.returnNowDate() : this.createdDate;
    }

    public static EventLogSmsSendEntity sendOf(String receiverPhoneNumber, String smsContents) {
        EventLogSmsSendEntity smsSendEntity = new EventLogSmsSendEntity();
        smsSendEntity.setReceiverPhoneNumber(receiverPhoneNumber.trim());
        smsSendEntity.setSmsContents(smsContents);
        return smsSendEntity;
    }

    public static EventLogSmsSendEntity sendOf(EventGiveAwayDeliveryEntity entity, String smsContents, String sendDate) {
        EventLogSmsSendEntity smsSendEntity = new EventLogSmsSendEntity();
        smsSendEntity.setGiveAwayId(entity.getGiveAwayId());
        smsSendEntity.setArEventWinningId(entity.getArEventWinningId());
        smsSendEntity.setReceiverPhoneNumber(entity.getPhoneNumber().trim());
        smsSendEntity.setSmsContents(smsContents);
        smsSendEntity.setSendDate(DateUtils.convertDateTimeFormat(sendDate));
        return smsSendEntity;
    }

    private static String makeSmsSendCode(String receiverPhoneNumber) {
        if (PredicateUtils.isNotNull(receiverPhoneNumber)) {
            StringBuffer sb = new StringBuffer();
            sb.append(DateUtils.getNow(DateUtils.PATTERN_MMDD));
            sb.append(receiverPhoneNumber);
            return sb.toString();
        }
        return null;
    }
}
