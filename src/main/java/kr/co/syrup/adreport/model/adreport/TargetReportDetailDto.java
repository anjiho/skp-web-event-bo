package kr.co.syrup.adreport.model.adreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Created by emma on 2021. 3. 22..
 */
@Data
@ApiModel(value = "광고주 리포팅 - 광고 타겟고객")
@EqualsAndHashCode(callSuper = false)
public class TargetReportDetailDto implements Serializable {
    private static final long serialVersionUID = 3027552829364668467L;

    @ApiModelProperty(value = "방문 확률 고객 count")
    private String liveAroundCustCnt;

    @ApiModelProperty(value = "광고 확인 고객 count")
    private String adClickCustCnt;

}
