package kr.co.syrup.adreport.model.adreport;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Created by emma on 2021. 4. 6..
 */
@Data
@ApiModel(value = "광고 리포팅 GATE 목록 조회 (SF)")
@EqualsAndHashCode(callSuper = false)
public class SFGateDto implements Serializable {
    private static final long serialVersionUID = -1552095414685563343L;

    @ApiModelProperty(value = "이미지URL")
    private String img;

    @ApiModelProperty(value = "리포트명")
    private String reportName;

    @ApiModelProperty(value = "고객에게 보여질 계약처명")
    private String name;

    @ApiModelProperty(value = "광고주 MID")
    @JsonProperty(value = "MID")
    private String mid;

    @ApiModelProperty(value = "등록월")
    private String regDate;

    @ApiModelProperty(value = "통계 년월")
    private String reportDate;

    @ApiModelProperty(value = "시작일")
    private String startDate;

    @ApiModelProperty(value = "종료일")
    private String endDate;

    @ApiModelProperty(value = "encMdn")
    private String encMdn;

}
