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
@ApiModel(value = "광고 리포팅 조회 (SF) - midInfo")
@EqualsAndHashCode(callSuper = false)
public class SFReportMidInfoDto implements Serializable {
    private static final long serialVersionUID = 7053161312468539407L;

    @ApiModelProperty(value = "고객에게 보여질 계약처명")
    private String name;

    @ApiModelProperty(value = "시작일시")
    private String startDate;

    @ApiModelProperty(value = "종료일시")
    private String endDate;

    @ApiModelProperty(value = "누적적립고객수 누적 고객수")
    private String totalCustomer;

    @ApiModelProperty(value = "누적 적립건수")
    private String totalPointCount;

    @ApiModelProperty(value = "누적 쿠폰발급수")
    private String totalIssuedCount;

    @ApiModelProperty(value = "누적 쿠폰 사용수")
    private String totalUsedCount;
}
