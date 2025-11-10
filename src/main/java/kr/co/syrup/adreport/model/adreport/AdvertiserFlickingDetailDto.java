package kr.co.syrup.adreport.model.adreport;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class AdvertiserFlickingDetailDto implements Serializable {

    private static final long serialVersionUID = 3508036188776850743L;

    @ApiModelProperty(value = "마케팅ID")
    private String marketingId;

    @ApiModelProperty(value = "광고주 MID")
    private String advertiserMid;

    @ApiModelProperty(value = "마케팅 명")
    private String marketingTitle;

    @ApiModelProperty(value = "고객에게 보여질 계약처명")
    private String contPlcName;

    @ApiModelProperty(value = "솔루션 코드")
    private String solutionCode;

    @ApiModelProperty(value = "서비스 솔루션 ID")
    private String serviceSolutionId;

    @ApiModelProperty(value = "실제 서비스 시작일")
    private String realServiceStartDate;

    @ApiModelProperty(value = "실제 서비스 종료일")
    private String realServiceEndDate;

    @ApiModelProperty(value="광고리포트 마케팅명")
    private String adreportMarketingTitle;

    @ApiModelProperty(value="광고리포트 계약처명")
    private String adreportContPlcName;

    @ApiModelProperty(value="광고리포트 이미지 URL")
    private String adreportImgUrl;

    @ApiModelProperty(value = "채널 타입 (OP : OCB전단PUSH(0131)-모바일전단 / BLE : BLE광고(0102)-위치광고 / SP : 시럽푸시(0130)-Push광고 / DFP : DFP광고(0129)-배너광고 / SF : Syrup 프렌즈 / AT : 광고 타겟 추출)")
    private String channelType;

    @JsonIgnore
    private String createdBy;

    @JsonIgnore
    private String contractNo;

}
