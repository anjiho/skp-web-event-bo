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
@ApiModel(value = "광고 리포팅 조회 (SF) - newCustomerInfo")
@EqualsAndHashCode(callSuper = false)
public class SFReportNewCustomerInfoDto implements Serializable {
    private static final long serialVersionUID = 2064801240962571512L;

    @ApiModelProperty(value = "합계 정보")
    private SFReportStatSumInfoDto statSumInfo;

    @ApiModelProperty(value = "통계 목록")
    private List<SFReportStatListDto> statList;

}
