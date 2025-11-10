package kr.co.syrup.adreport.web.event.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ArEventWinningButtonResDto implements Serializable {

    private static final long serialVersionUID = 5139111626459928721L;

    private Integer arEventWinningButtonId;

    // AR_EVENT_WINNING.id
    private Integer arEventWinningId;

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

    // 버튼 액션 타입이 경품배송일때 생년월일 사용여부
    private Boolean deliveryBirthYn;

    // 생성자
    private String createdBy;

    // 생성일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;

    // 수정자
    private String lastModifiedBy;

    // 수정일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date lastModifiedDate;

    private List<ArEventWinningButtonAddResDto> arEventWinningButtonAddInfo;

}
