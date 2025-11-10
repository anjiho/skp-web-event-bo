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
@ApiModel(value = "광고 리포팅 조회 (SF) - Sales Info")
@EqualsAndHashCode(callSuper = false)
public class SFReportSalesInfoDto implements Serializable {
    private static final long serialVersionUID = 8914461368966701133L;

    @ApiModelProperty(value = "리포팅 조회 기준월")
    private String reportDate;

    @ApiModelProperty(value = "쿠폰 실적 정보")
    private SFReportCouponInfoDto couponInfo;

    @ApiModelProperty(value = "신규 고객 정보 (salesInfo.reportDate 의 해당월 기준 SUM, 통계 목록)")
    private SFReportNewCustomerInfoDto newCustomerInfo;

    @ApiModelProperty(value = "적립 고객 정보 (salesInfo.reportDate 의 해당월 기준 SUM, 통계 목록)")
    private SFReportIssuedCustomerInfoDto issuedCustomerInfo;
}
