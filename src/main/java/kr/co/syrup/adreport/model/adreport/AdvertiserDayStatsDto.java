package kr.co.syrup.adreport.model.adreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by emma on 2021. 4. 6..
 */
@Data
@ApiModel(value = "광고 리포팅 솔루션별 일자 통계 조회")
@EqualsAndHashCode(callSuper = false)
public class AdvertiserDayStatsDto extends AdvertiserSumStatsDto {

    private static final long serialVersionUID = -8208119940598379957L;

    @ApiModelProperty(value = "CTR")
    private String ctr;

    @ApiModelProperty(value = "기준일 - YYYYMMDD")
    private String date;

    @ApiModelProperty(value = "page")
    private String page;

    @ApiModelProperty(value = "size")
    private String size;

}
