package kr.co.syrup.adreport.stamp.event.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;

@Data
public class StampTrLocationAttendReqDto implements Serializable {

    private static final long serialVersionUID = -2343301081255678861L;

    @ApiModelProperty(value = "스탬프 TR 인덱스")
    private Long stpPanTrId;

    @JsonIgnore
    private String stpTrEventId;

    @ApiModelProperty(value = "스탬프 TR 위치 PID")
    private String stpTrPid;

    @ApiModelProperty(value = "참여값")
    private String attendValue;

    @ApiModelProperty(value = "참여종류")
    private String attendType;

    @ApiModelProperty(value = "위도")
    private String latitude;

    @ApiModelProperty(value = "경도")
    private String longitude;
}
