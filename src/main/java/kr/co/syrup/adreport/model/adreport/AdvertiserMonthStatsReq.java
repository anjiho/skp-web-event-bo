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
@ApiModel(value = "광고 리포팅 솔루션별 월별 통계 조회")
@EqualsAndHashCode(callSuper = false)
public class AdvertiserMonthStatsReq implements Serializable {

    private static final long serialVersionUID = 3205564722894747648L;

    @ApiModelProperty(value = "페이지")
    private int page = 1;

    @ApiModelProperty(value = "크기")
    private int size = 3;

    @ApiModelProperty(value = "마케팅 ID")
    private String marketingId;

    @ApiModelProperty(value = "광고주 MDN")
    private String mdn;
}
