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
@ApiModel(value = "광고주 리포팅 통계 조회 - 트랜드리포트 통계 상세")
@EqualsAndHashCode(callSuper = false)
public class TrendReportDetailDto implements Serializable {
    private static final long serialVersionUID = -7824103727824010347L;

    @ApiModelProperty(value = "카테고리 (여성 남성 30대초반 등등..)")
    private String reportTypeGrp;

    @ApiModelProperty(value = "통계 count")
    private String uv;

    @ApiModelProperty(value = "통계 퍼센트")
    private String uvPercent;
}
