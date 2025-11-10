package kr.co.syrup.adreport.model.adreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * Created by emma on 2021. 4. 6..
 */
@Data
@ApiModel(value = "광고 리포팅 솔루션별 월별 통계 조회")
@EqualsAndHashCode(callSuper = false)
public class AdvertiserMonthStatsRes implements Serializable {

    private static final long serialVersionUID = 3362873480058456855L;

    @ApiModelProperty(value = "totalCnt")
    private int totalCnt;

    @ApiModelProperty(value = "page")
    private int page;

    @ApiModelProperty(value = "size")
    private int size;

    @ApiModelProperty(value = "통계 list")
    private List<AdvertiserMonthStatsDto> list;

}
