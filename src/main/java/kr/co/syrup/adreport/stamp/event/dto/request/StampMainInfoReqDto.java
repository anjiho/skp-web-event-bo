package kr.co.syrup.adreport.stamp.event.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class StampMainInfoReqDto implements Serializable {

    private static final long serialVersionUID = 4084469410467713018L;

    @ApiModelProperty(value = "이벤트 ID")
    private String eventId;
}
