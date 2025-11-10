package kr.co.syrup.adreport.web.event.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.SecurityUtils;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import kr.co.syrup.adreport.web.event.dto.request.GiveAwayDeliverySaveReqDto;
import lombok.*;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "EVENT_GIVE_AWAY_DELIVERY")
public class EventGiveAwayDeliveryEntity implements Serializable {

    private static final long serialVersionUID = 5680208510541734751L;

    // 인덱스
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "give_away_id")
    // 인덱스
    private Integer giveAwayId;

    // 이벤트 아이디
    private String eventId;

    // 이벤트 당첨 정보 아이디
    @Column(name = "ar_event_winning_id")
    private Integer arEventWinningId;

    // 당첨 타입  값(기프티콘, 기타, 꽝)
    private String winningType;

    // 당첨 상품명
    private String productName;

    // 성명
    private String name;

    // 전화번호
    private String phoneNumber;

    // 우편번호
    private String zipCode;

    // 주소
    private String address;

    // 주소 상세
    private String addressDetail;

    // 경품 비밀번호
    private String giveAwayPassword;

    // 참여번호
    private String attendCode;

    // 경품 수령 여부
    private Boolean isReceive;

    //생년월일
    private String memberBirth;

    //기프티콘 api trId
    private String trId;

    //기프티콘 주문 번호
    private String gifticonOrderNo;

    //기프티콘 주문 결과 코드
    private String gifticonResultCd;

    //EVENT_LOG_WINNING.id
    private Long eventLogWinningId;

    // 포토 추가건 OCB 쿠폰 ID
    @Column(name = "ocb_coupon_id")
    private String ocbCouponId;

    // 포토 추가건 OCB 쿠폰 읽음 여부
    @Column(name = "is_ocb_coupon_read")
    private Boolean isOcbCouponRead;

    // 생성일
    @Column(name = "created_date")
    private Date createdDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = PredicateUtils.isNull(this.createdDate) ? DateUtils.returnNowDate() : this.createdDate;
    }

    public static EventGiveAwayDeliveryEntity saveOf(EventGiveAwayDeliveryEntity reqEntity, String winningType, String productName, String gifticonOrderCd, String trId, String gifticonOrderNo) {
        reqEntity.setWinningType(winningType);
        reqEntity.setProductName(productName);
        reqEntity.setGiveAwayPassword(StringUtils.isEmpty(reqEntity.getGiveAwayPassword()) ? null : SecurityUtils.encryptSHA256(reqEntity.getGiveAwayPassword()));
        reqEntity.setAttendCode(StringUtils.isEmpty(reqEntity.getAttendCode()) ? null : reqEntity.getAttendCode());
        reqEntity.setIsReceive("GIFTICON".equals(winningType) ? true : false);
        reqEntity.setMemberBirth(reqEntity.getMemberBirth());
        reqEntity.setTrId(trId);
        reqEntity.setGifticonOrderNo(gifticonOrderNo);
        reqEntity.setGifticonResultCd(gifticonOrderCd);
        reqEntity.setEventLogWinningId(reqEntity.getEventLogWinningId());
        reqEntity.setCreatedDate(DateUtils.returnNowDate());
        return reqEntity;
    }

    public static EventGiveAwayDeliveryEntity receiptOf(EventGiveAwayDeliveryEntity entity) {
        //entity.setGiveAwayPassword(giveAwayPassword);
        entity.setIsReceive(true);
        return entity;
    }

    //자동당첨 저장일때
    public static EventGiveAwayDeliveryEntity autoSaveOf(EventGiveAwayDeliveryEntity reqEntity, String winningType, String productName) {
        reqEntity.setWinningType(winningType);
        reqEntity.setProductName(productName);
        reqEntity.setGiveAwayPassword("");
        reqEntity.setAttendCode(StringUtils.isEmpty(reqEntity.getAttendCode()) ? null : reqEntity.getAttendCode());
        reqEntity.setIsReceive(false);
        reqEntity.setMemberBirth("");
        reqEntity.setTrId("");
        reqEntity.setGifticonOrderNo("");
        reqEntity.setGifticonResultCd("");
        reqEntity.setEventLogWinningId(reqEntity.getEventLogWinningId());
        reqEntity.setCreatedDate(DateUtils.returnNowDate());
        return reqEntity;
    }
}
