package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SavePrintStatusReqDto implements Serializable {
    private static final long serialVersionUID = -2455168401458781293L;

    // 이벤트 아이디
    private String eventId;

    // OCB MBR ID
    private String ocbMbrId;

    // 클라이언트 유니크 키
    private String clientUniqueKey;

    // 출력시도 : TRY / 출력실패 : FAIL / 출력 성공 : SUCCESS
    private String printResultStatus;

    private String traceNo;

}
