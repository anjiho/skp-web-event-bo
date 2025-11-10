package kr.co.syrup.adreport.web.event.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GifticonOrderInfoDto implements Serializable {

    private static final long serialVersionUID = -1070426557334171195L;
    //기프티콘 주문번호
    @JsonProperty(value = "ORD_NO")
    private String orderNo;

    //수신자 전화번호, 쿠폰을 받을 전화번호
    @JsonProperty(value = "RCVR_MDN")
    private String rcvrMdn;

    //배송인증번호 (배송지 입력 방식이 RI 일 경우에만 전달)
    @JsonProperty(value = "SHPP_AUTH_NO")
    private String shppAuthNo;

    //배송지 URL (배송지 입력 방식이 RI 일 경우에만 전달)
    @JsonProperty(value = "SHPP_URL")
    private String shppUrl;

    @JsonProperty(value = "CPNO_INFO")
    private List<GifticonInfoDto> cpnoInfo;

}
