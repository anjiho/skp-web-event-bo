package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
// SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
@Builder
public class WebArGateReqDto implements Serializable {

    private static final long serialVersionUID = 2208949876429889022L;

    private String eventId;

    private String attendCode;

    @Builder.Default
    private Boolean isRedirect = false;

    private String trackingCode;

    private String phoneNumber;

    private String traceNo;
}
