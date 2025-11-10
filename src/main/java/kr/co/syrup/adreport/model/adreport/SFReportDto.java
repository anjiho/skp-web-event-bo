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
@ApiModel(value = "광고 리포팅 조회 (SF)")
@EqualsAndHashCode(callSuper = false)
public class SFReportDto implements Serializable {
    private static final long serialVersionUID = -4833790046855195022L;

    @ApiModelProperty(value = "MID INFO")
    private SFReportMidInfoDto midInfo;

    @ApiModelProperty(value = "salesInfo")
    private SFReportSalesInfoDto salesInfo;
}
