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
@ApiModel(value = "광고 리포팅 조회 (SF) - statInfo")
@EqualsAndHashCode(callSuper = false)
public class SFReportStatInfoDto implements Serializable {
    private static final long serialVersionUID = -1420359117581030181L;
    @ApiModelProperty(value = "페이지번")
    private String page;

    @ApiModelProperty(value = "사이즈")
    private String size;

    @ApiModelProperty(value = "전체 항목수")
    private String totalCount;

    @ApiModelProperty(value = "통계 리스트")
    private List<SFReportStatListDto> statList;

}
