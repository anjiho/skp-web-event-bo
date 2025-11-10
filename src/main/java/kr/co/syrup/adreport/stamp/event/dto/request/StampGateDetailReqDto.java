package kr.co.syrup.adreport.stamp.event.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class StampGateDetailReqDto implements Serializable {

    private static final long serialVersionUID = -2005860530253392497L;

    // 이벤트 ID
    @ApiModelProperty(value = "이벤트 ID")
    private String eventId;
}
