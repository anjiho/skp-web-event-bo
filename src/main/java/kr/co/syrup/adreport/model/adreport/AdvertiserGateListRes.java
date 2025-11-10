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
@ApiModel(value = "광고 리포팅 GATE 목록 조회")
@EqualsAndHashCode(callSuper = false)
public class AdvertiserGateListRes implements Serializable {

    private static final long serialVersionUID = -9148537531229769092L;

    @ApiModelProperty(value = "총 count")
    private int totalCnt;

    @ApiModelProperty(value = "size")
    private int size;

    @ApiModelProperty(value = "page")
    private int page;

    @ApiModelProperty(value = "SODAR 조회LIST")
    private List<AdvertiserGateDto> list;

    @ApiModelProperty(value = "SF 조회LIST")
    private List<SFGateDto> list4SF;

    @ApiModelProperty(value = "encMdn")
    private String encMdn;
}
