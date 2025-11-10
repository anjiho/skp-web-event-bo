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
@ApiModel(value = "광고 리포팅 조회 (SF) - Coupon Info")
@EqualsAndHashCode(callSuper = false)
public class SFReportCouponInfoDto implements Serializable {
    private static final long serialVersionUID = 1755470406295868996L;

    @ApiModelProperty(value = "합계 정보")
    private SFReportStatSumInfoDto statSumInfo;

    @ApiModelProperty(value = "통계 목록")
    private SFReportStatInfoDto statInfo;

}
