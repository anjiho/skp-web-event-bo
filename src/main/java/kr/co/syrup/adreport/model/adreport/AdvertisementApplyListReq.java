package kr.co.syrup.adreport.model.adreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Created by emma on 2021. 4. 6..
 */
@Data
@ApiModel(value = "광고 신청 현황 조회")
@EqualsAndHashCode(callSuper = false)
public class AdvertisementApplyListReq implements Serializable {

    private static final long serialVersionUID = 7078123893983537552L;

    @ApiModelProperty(value = "페이지")
    private int page;

    @ApiModelProperty(value = "크기")
    private int size;

    @ApiModelProperty(value = "채널 타입 (OP : OCB전단PUSH(0131)-모바일전단 / BLE : BLE광고(0102)-위치광고 / SP : 시럽푸시(0130)-Push광고 / DFP : DFP광고(0129)-배너광고 / SF : Syrup 프렌즈 / AT : 광고 타겟 추출)")
    private String channelType;

}
