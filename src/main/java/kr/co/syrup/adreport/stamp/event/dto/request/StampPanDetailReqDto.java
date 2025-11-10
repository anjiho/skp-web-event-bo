package kr.co.syrup.adreport.stamp.event.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StampPanDetailReqDto extends StampGateDetailReqDto {

    private static final long serialVersionUID = -3650971017973414560L;

    // 인증 번호
    @ApiModelProperty(value = "휴대폰번호 or 참여번호")
    private String attendValue;
}
