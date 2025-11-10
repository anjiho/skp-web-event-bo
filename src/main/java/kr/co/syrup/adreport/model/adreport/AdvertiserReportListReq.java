package kr.co.syrup.adreport.model.adreport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Created by emma on 2021. 3. 22..
 */
@Data
@ApiModel(value = "광고주 리포팅 조회 Request")
@EqualsAndHashCode(callSuper = false)
public class AdvertiserReportListReq implements Serializable {

    private static final long serialVersionUID = -6652325728373312471L;

//    @ApiModelProperty(value = "광고주 MID")
//    private String mid;

    @ApiModelProperty(value = "광고주 MDN")
    private String mdn;

    @ApiModelProperty(value = "마케팅 ID")
    private String marketingId;

    @ApiModelProperty(value = "채널 타입 (OP : OCB전단PUSH(0131)-모바일전단 / BLE : BLE광고(0102)-위치광고 / SP : 시럽푸시(0130)-Push광고 / DFP : DFP광고(0129)-배너광고 / SF : Syrup 프렌즈 / AT : 광고 타겟 추출)")
    private String channelType;

    @ApiModelProperty(value = "리포팅 기준일자")
    private String realServiceEndDate;

    @JsonIgnore
    private String solutionCode;

}
