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
@ApiModel(value = "광고 리포팅 조회 (SF) - statInfo")
@EqualsAndHashCode(callSuper = false)
public class SFReportStatListDto implements Serializable {
    private static final long serialVersionUID = -2668557455179676399L;

    @ApiModelProperty(value = "쿠폰명")
    private String couponName;

    @ApiModelProperty(value = "발급수")
    private String issuedCount;

    @ApiModelProperty(value = "사용수")
    private String usedCount;

    @ApiModelProperty(value = "통계월")
    private String reportDate;

    @ApiModelProperty(value = "카운트")
    private String count;

}
