package kr.co.syrup.adreport.web.event.dto.request;

import lombok.*;

import java.io.Serializable;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContractAfterReqDto implements Serializable {

    private static final long serialVersionUID = 1385176898499968076L;

    private String marketingId;

    private MarketingInfoReqDto marketingInfo;

    private String serviceSolutionId;
}
