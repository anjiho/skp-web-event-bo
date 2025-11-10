package kr.co.syrup.adreport.web.event.dto.response.api;

import lombok.*;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CouponInfoResDto implements Serializable {

    private static final long serialVersionUID = 3138012811936831340L;

    @JsonProperty(value = "oid")
    private String oid;

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "startDate")
    private String startDate;

    @JsonProperty(value = "endDate")
    private String endDate;

    @JsonProperty(value = "detailInfo")
    private String detailInfo;

    @JsonProperty(value = "status")
    private String status;

    @JsonProperty(value = "regDate")
    private String regDate;

    @JsonProperty(value = "imagePath")
    private String imagePath;

    @JsonProperty(value = "usePlaceName")
    private String usePlaceName;

    @JsonProperty(value = "validPeriodType")
    private String validPeriodType;

    @JsonProperty(value = "validDays")
    private String validDays;

    @JsonProperty(value = "contentType")
    private String contentType;

    @JsonProperty(value = "couponId")
    private String contentId;
}
