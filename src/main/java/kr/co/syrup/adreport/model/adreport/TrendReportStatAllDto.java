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
@ApiModel(value = "광고주 리포팅 통계 조회 - 트랜드리포트")
@EqualsAndHashCode(callSuper = false)
public class TrendReportStatAllDto implements Serializable {
    private static final long serialVersionUID = 8910553629101323411L;

    @ApiModelProperty(value = "연령별 통계")
    private TrendReportStatDto age;

    @ApiModelProperty(value = "성별 통계")
    private TrendReportStatDto gender;

    @ApiModelProperty(value = "거주지별 통계")
    private TrendReportStatDto live;

    @ApiModelProperty(value = "관심상품별 통계")
    private TrendReportStatDto interest;
}
