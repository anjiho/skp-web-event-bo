package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PhotoPrintCountReqDto implements Serializable {

    private static final long serialVersionUID = 8601610232656460354L;

    // 이벤트 아이디
    private String eventId;

    // OCB MBR ID
    private String ocbMbrId;

    // 클라이언트 유니크 키
    private String clientUniqueKey;

    private String traceNo;

}
