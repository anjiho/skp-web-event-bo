package kr.co.syrup.adreport.web.event.dto.request;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class ContractAccessPushReqDto implements Serializable {

    private static final long serialVersionUID = -6314155986411260010L;

    private String serviceSolutionId;

    private MarketingInfoReqDto marketingInfo;

    private ContractInfoReqDto contractInfo;

    private String traceNo;


}
