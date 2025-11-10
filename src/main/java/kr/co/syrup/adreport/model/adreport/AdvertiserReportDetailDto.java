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
@ApiModel(value = "광고주 리포팅 조회 Response")
@EqualsAndHashCode(callSuper = false)
public class AdvertiserReportDetailDto implements Serializable {

    private static final long serialVersionUID = -2327662691081221953L;

    @ApiModelProperty(value = "솔루션 계약 정보")
    private AdvertiserReportContractDetailDto contractInfo;

    @ApiModelProperty(value = "통계 - 합계")
    private AdvertiserSumStatsDto sumStats;

    @ApiModelProperty(value = "통계 - 일자별")
    private AdvertiserDayStatsRes dayStats;

    @ApiModelProperty(value = "통계 - 월별")
    private AdvertiserMonthStatsRes monthStats;

    @ApiModelProperty(value = "트랜드 리포트 - 전체")
    private TrendReportStatAllDto trendReportStatAll;

}
