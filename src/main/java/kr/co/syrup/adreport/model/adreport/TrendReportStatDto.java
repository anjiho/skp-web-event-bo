package kr.co.syrup.adreport.model.adreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * Created by emma on 2021. 3. 22..
 */
@Data
@ApiModel(value = "광고주 리포팅 통계 조회 - 트랜드리포트 (단위별)")
@EqualsAndHashCode(callSuper = false)
public class TrendReportStatDto implements Serializable {
    private static final long serialVersionUID = 5694343500811063119L;

    @ApiModelProperty(value = "카테고리 (여성 남성 30대초반 등등..)")
    private List<TrendReportDetailDto> stats;

    @ApiModelProperty(value = "막대그래프 yLabels")
    private List<String> yLabels;
}
