package kr.co.syrup.adreport.stamp.event.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StampEventGiveAwayDeliveryAtHistoryCheckResVO implements Serializable {

    private static final long serialVersionUID = -3343561206568363582L;

    // 인덱스
    private Long stpGiveAwayId;

    // 이벤트 아이디
    private String eventId;

    private Integer stpId;

    private Long stpPanTrId;

    // 이벤트 당첨 정보 아이디
    private Integer arEventWinningId;

    private Long stpEventLogWinningId;

    // 당첨 타입 값(기프티콘, 기타, 꽝)
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

    // 참여번호
    private String attendCode;

    // 생년월일(8자리)
    private String memberBirth;

    // 경품 수령 여부
    private Boolean isReceive;

    // 기프티콘 api tr_id
    private String trId;

    // 기프티콘 주문 번호
    private String gifticonOrderNo;

    // 기프티콘 결과 값
    private String gifticonResultCd;

    private String winningImageUrl;

    private String winningDate;

}
