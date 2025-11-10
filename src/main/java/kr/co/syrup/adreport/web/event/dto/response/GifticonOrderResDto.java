package kr.co.syrup.adreport.web.event.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GifticonOrderResDto implements Serializable {

    private static final long serialVersionUID = 2488066649085541295L;
    //처리결과 코드 (주문처리 코드 - Appendix A 참조, 0000 : 성공)
    @JsonProperty(value = "RESULT_CD")
    private String resultCd;

    //처리결과 메시지(Appendix A 참조)
    @JsonProperty(value = "RESULT_MSG")
    private String resultMsg;

    //기프티콘에서 생성한 연동 ID (캠페인 ID)
    @JsonProperty(value = "CAMP_ID")
    private String campId;

    //고객사 연동 번호 (Unique ID)
    @JsonProperty(value = "TR_ID")
    private String trId;

    @JsonProperty(value = "ORD_INFO")
    private List<GifticonOrderInfoDto> ordInfo;
}
