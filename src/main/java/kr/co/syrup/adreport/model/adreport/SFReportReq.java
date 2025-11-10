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
@ApiModel(value = "광고주 리포팅 조회 Request (SF)")
@EqualsAndHashCode(callSuper = false)
public class SFReportReq implements Serializable {

    @ApiModelProperty(value = "광고주 MID")
    private String mid;

    @ApiModelProperty(value = "광고주 MDN")
    private String mdn;

    @ApiModelProperty(value = "조회 기준월 - yyyyMM")
    private String reportDate;

    @ApiModelProperty(value = "page")
    private String page = "1";

    @ApiModelProperty(value = "size")
    private String size = "3";
}
