package kr.co.syrup.adreport.model.adreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by emma on 2021. 4. 6..
 */
@Data
@ApiModel(value = "광고 리포팅 솔루션별 월별 통계 조회")
@EqualsAndHashCode(callSuper = false)
public class AdvertiserMonthStatsDto extends AdvertiserSumStatsDto {

    private static final long serialVersionUID = 6295087180499146260L;

    @ApiModelProperty(value = "CTR")
    private String ctr;

    @ApiModelProperty(value = "기준일 - YYYYMMDD")
    private String date;

    @ApiModelProperty(value = "마케팅명")
    private String marketingTitle;

    @ApiModelProperty(value = "광고리포트 마케팅명-계약시 입력한 명칭")
    private String adreportMarketingTitle;

    @ApiModelProperty(value = "고객에게 보여질 계약처명")
    private String contPlcName;

}
