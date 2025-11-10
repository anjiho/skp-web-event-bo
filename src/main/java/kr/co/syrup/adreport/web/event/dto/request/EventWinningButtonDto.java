package kr.co.syrup.adreport.web.event.dto.request;

import kr.co.syrup.adreport.web.event.entity.ArEventWinningButtonAddEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class EventWinningButtonDto implements Serializable {

    private static final long serialVersionUID = -1394896404828219331L;
    
    private Integer arEventWinningButtonId;

    // 버튼 액션 타입
    private String buttonActionType;

    // 버튼 문구
    private String buttonText;

    // 버튼 링크 url
    private String buttonLinkUrl;

    // 순서
    private Integer buttonSort;

    // 버튼 액션 타입이 경품배송일때 성명 사용여부
    private Boolean deliveryNameYn;

    // 버튼 액션 타입이 경품배송일때 전화번호 사용여부
    private Boolean deliveryPhoneNumberYn;

    // 버튼 액션 타입이 경품배송일때 배송주소 사용여부
    private Boolean deliveryAddressYn;

    // 버튼 액션 타입이 경품배송일때 생년월일 사용여부 - 스탬프형일때 추가
    private Boolean deliveryBirthYn;

    private List<EventWinningButtonAddReqDto> arEventWinningButtonAddInfo;

}
