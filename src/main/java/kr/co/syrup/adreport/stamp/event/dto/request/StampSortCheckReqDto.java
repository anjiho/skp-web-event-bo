package kr.co.syrup.adreport.stamp.event.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class StampSortCheckReqDto implements Serializable {

    private static final long serialVersionUID = -8012090003282571768L;

    @ApiModelProperty(value = "스탬프 인데스")
    private Integer stpId;

    @ApiModelProperty(value = "스탬프 TR 순서 번호")
    private Integer stpTrSort;

    @ApiModelProperty(value = "참여 종류(참여번호 : ATTEND, 핸드폰번호 : MDN)")
    private String attendType;

    @ApiModelProperty(value = "참여 값(참여번호 or 핸드폰번호)")
    private String attendValue;
}
