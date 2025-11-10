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
@ApiModel(value = "광고 리포팅 플리킹 목록 조회")
@EqualsAndHashCode(callSuper = false)
public class AdvertiserFlickingDto implements Serializable {

    private static final long serialVersionUID = -7154006738369638920L;

    @ApiModelProperty(value = "계약 데이터")
    private AdvertiserFlickingDetailDto contractInfo;

    @ApiModelProperty(value = "통계 데이터")
    private AdvertiserSumStatsDto sumStats;


}
