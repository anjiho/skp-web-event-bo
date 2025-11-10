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
@ApiModel(value = "광고 신청 Request")
@EqualsAndHashCode(callSuper = false)
public class AdvertisementApplyReq implements Serializable {

    private static final long serialVersionUID = 644739762967612795L;

    @JsonIgnore
    private String advertisementApplyId;

    @ApiModelProperty(value = "광고 신청 채널 타입 (OP : OCB전단PUSH(0131)-모바일전단 / BLE : BLE광고(0102)-위치광고 / SP : 시럽푸시(0130)-Push광고 / DFP : DFP광고(0129)-배너광고 / SF : Syrup 프렌즈 / AT : 광고 타겟 추출)")
    private String channelType;

    @ApiModelProperty(value = "광고주 MID")
    private String advertiserMid;

    @ApiModelProperty(value = "계약처명")
    private String contPlcName;

}
