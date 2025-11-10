package kr.co.syrup.adreport.model.adreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Created by emma on 2021. 4. 6..
 */
@Data
@ApiModel(value = "광고 리포팅 솔루션별 합계 통계 조회")
@EqualsAndHashCode(callSuper = false)
public class AdvertiserSumStatsDto implements Serializable {

    private static final long serialVersionUID = -5313437426288668400L;

    @ApiModelProperty(value = "발송수")
    private String sendCount;

    @ApiModelProperty(value = "수신수")
    private String receiveCount;

    @ApiModelProperty(value = "확인수")
    private String checkCount;

    @ApiModelProperty(value = "버튼수")
    private String buttonCount;

    @ApiModelProperty(value = "노출수")
    private String displayCount;

}
