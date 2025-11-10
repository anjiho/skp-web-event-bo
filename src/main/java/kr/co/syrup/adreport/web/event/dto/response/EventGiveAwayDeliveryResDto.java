package kr.co.syrup.adreport.web.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventGiveAwayDeliveryResDto implements Serializable {

    private static final long serialVersionUID = -5996964549449594579L;

    // 인덱스
    private Integer giveAwayId;

    // 이벤트 아이디
    private String eventId;

    // 이벤트 당첨 정보 아이디
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
    private String ocbCouponId;

    // 포토 추가건 OCB 쿠폰 읽음 여부
    private Boolean isOcbCouponRead;

    // 생성일
    private Date createdDate;
}
