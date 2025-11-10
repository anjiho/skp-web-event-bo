package kr.co.syrup.adreport.web.event.dto.response.api;

import lombok.*;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PicasoCouponListResDto implements Serializable {

    private static final long serialVersionUID = -4539490346590938547L;

    @JsonProperty(value = "couponList")
    private List<CouponInfoResDto> couponList;
}
