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
@ApiModel(value = "광고 리포팅 조회 (SF) - statSumInfo")
@EqualsAndHashCode(callSuper = false)
public class SFReportStatSumInfoDto implements Serializable {
    private static final long serialVersionUID = 4075894699142299232L;

    @ApiModelProperty(value = "발급수")
    private String issuedCount;

    @ApiModelProperty(value = "사용건수")
    private String usedCount;

    @ApiModelProperty(value = "신규고객 합계")
    private String newCustomerCount;

    @ApiModelProperty(value = "적립고객 합계")
    private String issuedCustomerCount;
}
