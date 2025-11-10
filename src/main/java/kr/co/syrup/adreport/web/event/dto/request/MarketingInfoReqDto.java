package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class MarketingInfoReqDto implements Serializable {

    private static final long serialVersionUID = 7653860390670449262L;

    private String marketingId;

    private String serviceStartDate;

    private String serviceEndDate;
}
