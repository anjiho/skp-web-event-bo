package kr.co.syrup.adreport.web.event.dto.response.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PicasoCouponInfoApiResDto implements Serializable {

    private static final long serialVersionUID = 777013766707457824L;

    @JsonProperty(value = "resCode1")
    private String resCode1;

    @JsonProperty(value = "detailMsg")
    private String detailMsg;

    @JsonProperty(value = "traceNo")
    private String traceNo;

    @JsonProperty(value = "responseData")
    private PicasoCouponListResDto responseData;
}
