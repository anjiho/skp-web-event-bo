package kr.co.syrup.adreport.model.adreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Created by emma on 2021. 6. 3..
 */
@Data
@ApiModel(value = "광고 리포트 수신 업데이트")
@EqualsAndHashCode(callSuper = false)
public class AdreportReceiveCheckUpdateReq implements Serializable {

    private static final long serialVersionUID = 5658436202895890110L;

    @ApiModelProperty(value = "채널 타입 (OP : OCB전단PUSH(0131)-모바일전단 / BLE : BLE광고(0102)-위치광고 / SP : 시럽푸시(0130)-Push광고 / DFP : DFP광고(0129)-배너광고 / SF : Syrup 프렌즈 / AT : 광고 타겟 추출)")
    private String channelType;

    @ApiModelProperty(value = "마케팅ID")
    private String marketingId;

    @ApiModelProperty(value = "광고주 MID")
    private String advertiserMid;

    @ApiModelProperty(value = "시럽프렌즈 조회 기준 월")
    private String sfSearchMonth;

    @ApiModelProperty(value = "광고리포트 확인 결과 (0 : 미확인 / 1 : 확인)")
    private String receiveCheckStatus;

}
