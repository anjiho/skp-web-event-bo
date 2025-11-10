package kr.co.syrup.adreport.stamp.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.SecurityUtils;
import kr.co.syrup.adreport.web.event.define.WinningTypeDefine;
import kr.co.syrup.adreport.web.event.dto.request.EventWinningReqDto;
import kr.co.syrup.adreport.web.event.dto.request.GiveAwayDeliverySaveReqDto;
import kr.co.syrup.adreport.web.event.dto.response.GifticonOrderResDto;
import kr.co.syrup.adreport.web.event.entity.ArEventWinningEntity;
import kr.co.syrup.adreport.web.event.entity.EventGiveAwayDeliveryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;

//스탬프 당첨 입력 테이블
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StampEventGiveAwayDeliveryModel implements Serializable {

    private static final long serialVersionUID = 3966457453188544771L;

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

    // 생성일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;

    public static StampEventGiveAwayDeliveryModel ofAutoSave(ArEventWinningEntity winningEntity, EventWinningReqDto reqDto, long stpEventLogWinningId, GifticonOrderResDto gifticonOrderResDto) {
        StampEventGiveAwayDeliveryModel deliveryModel = new StampEventGiveAwayDeliveryModel();
        deliveryModel.setEventId(reqDto.getEventId());
        deliveryModel.setStpId(winningEntity.getStpId());
        deliveryModel.setStpPanTrId(reqDto.getStpPanTrId());
        deliveryModel.setArEventWinningId(winningEntity.getArEventWinningId());
        deliveryModel.setStpEventLogWinningId(stpEventLogWinningId);
        deliveryModel.setWinningType(winningEntity.getWinningType());
        deliveryModel.setProductName(winningEntity.getProductName());
        deliveryModel.setPhoneNumber(PredicateUtils.isNull(reqDto.getPhoneNumber()) ? null : reqDto.getPhoneNumber());
        deliveryModel.setAttendCode(PredicateUtils.isNull(reqDto.getAttendCode()) ? null : reqDto.getAttendCode());
        deliveryModel.setIsReceive(PredicateUtils.isEqualsStr(WinningTypeDefine.기프티콘.code(), winningEntity.getWinningType()) ? true : false);
        deliveryModel.setTrId(PredicateUtils.isNull(gifticonOrderResDto) ? null : gifticonOrderResDto.getTrId());
        deliveryModel.setGifticonOrderNo(PredicateUtils.isNull(gifticonOrderResDto) ? "" :gifticonOrderResDto.getOrdInfo().get(0).getOrderNo());
        deliveryModel.setGifticonResultCd(PredicateUtils.isNull(gifticonOrderResDto) ? "" :gifticonOrderResDto.getResultCd());
        return deliveryModel;
    }

//    public static StampEventGiveAwayDeliveryModel ofSave( StampEventGiveAwayDeliveryModel deliveryModel, ArEventWinningEntity winningEntity) {
//        deliveryModel.setEventId(deliveryModel.getEventId());
//        deliveryModel.setStpId(deliveryModel.getStpId());
//        deliveryModel.setArEventWinningId(deliveryModel.getArEventWinningId());
//        deliveryModel.setStpEventLogWinningId(deliveryModel.getStpGiveAwayId());
//        deliveryModel.setWinningType(winningEntity.getWinningType());
//        deliveryModel.setProductName(winningEntity.getProductName());
//        deliveryModel.setPhoneNumber(deliveryModel.getPhoneNumber());
//        deliveryModel.setAttendCode(StringUtils.isEmpty(deliveryModel.getAttendCode()) ? null : deliveryModel.getAttendCode());
//        deliveryModel.setIsReceive(PredicateUtils.isEqualsStr(WinningTypeDefine.기프티콘.code(), winningEntity.getWinningType()) ? true : false);
//        return deliveryModel;
//    }
}
