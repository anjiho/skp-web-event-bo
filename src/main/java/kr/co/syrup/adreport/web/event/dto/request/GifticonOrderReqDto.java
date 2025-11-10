package kr.co.syrup.adreport.web.event.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GifticonOrderReqDto implements Serializable {

    private static final long serialVersionUID = 8744343737728192212L;

    //기프티콘에서 생성한 연동 ID (캠페인 ID)
    @JsonProperty(value = "CAMP_ID")
    @Size(max = 8)
    private String campId;

    //고객사 연동 번호 (Unique ID)
    @JsonProperty(value = "TR_ID")
    @Size(max = 40)
    private String trId;

    //상품ID(소다 상품코드)
    @JsonProperty(value = "PROD_ID")
    @Size(max = 8)
    private String prodId;

    //상품수량
    @JsonProperty(value = "PROD_QTY")
    @Size(max = 2)
    private String prodQty;

    //수신자 전화번호, 쿠폰을 받을 전화번호
    @JsonProperty(value = "RCVR_INFO")
    @Size(max = 11)
    private Map<String, String> rcvrInfo;

    //암호화 여부 Y/ - Default : N
    @JsonProperty(value = "ENC_YN")
    @Size(max = 1)
    private String encYn;

    //발신자 번호
    @JsonProperty(value = "CALLBACK")
    @Size(max = 11)
    private String callBack;

    //쿠폰 발송 형태
    //- CAB01 : SMS
    //- CAB03 : MMS
    //- CAB06 : 자체발송 (발송안함)
    //- CAB07 : PUSH (기프티콘)
    @JsonProperty(value = "SEND_TYPE")
    @Size(max = 5)
    private String sendType;

    public static GifticonOrderReqDto condition(String eventId, String campId, String prodId, int arEventWinningId, String receivePhoneNumber) {
        Map<String, String> rcvrInfo = new HashMap<>();
        rcvrInfo.put("RCVR_MDN", receivePhoneNumber);

        return new GifticonOrderReqDto().builder()
                .campId(campId)
                .trId(getTrId(eventId, campId, arEventWinningId))
                .prodId(prodId)
                .prodQty("1")
                .rcvrInfo(rcvrInfo)
                .encYn("N")
                .callBack("01061192025")
                .sendType("CAB03")
                .build();
    }

    public static GifticonOrderReqDto testCondition(String eventId, String campId, String prodId, int arEventWinningId, String receivePhoneNumber) {
        Map<String, String> rcvrInfo = new HashMap<>();
        rcvrInfo.put("RCVR_MDN", receivePhoneNumber);

        return new GifticonOrderReqDto().builder()
                .campId("M1001658")
                .trId(getTrId(eventId, campId, arEventWinningId))
                .prodId("S0073267")
                .prodQty("1")
                .rcvrInfo(rcvrInfo)
                .encYn("N")
                .callBack("01012341234")
                .sendType("CAB01")
                .build();
    }

    private static String getTrId(String eventId, String campId, int arEventWinningId) {
        StringBuilder sb = new StringBuilder();
        sb.append(DateUtils.returnNowDateByYyyymmddhhmmss());
        sb.append(campId);
        sb.append("E");
        sb.append(eventId);
        sb.append("W");
        sb.append(arEventWinningId);
        return sb.toString();
    }
}
