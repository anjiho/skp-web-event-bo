package kr.co.syrup.adreport.stamp.event.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class StampEventTrDto implements Serializable {

    private static final long serialVersionUID = 3512841574392674478L;

    @ApiModelProperty(value = "스탬프 TR ID")
    private Long stpPanTrId;

    @ApiModelProperty(value = "TR 순서")
    private Integer stpTrSort;

    @ApiModelProperty(value = "TR 명")
    private String stpTrTxt;

    @ApiModelProperty(value = "TR 유형 (이벤트, 위치)")
    private String stpTrType;

    @ApiModelProperty(value = "TR 에 연결되어있는 이벤트 ID")
    private String stpTrEventId;

    @ApiModelProperty(value = "TR 에 연결되어있는 위치 PID")
    private String stpTrPid;

    @ApiModelProperty(value = "위치참여 성공시 문구")
    private String stpTrLocationMsgAttend;

    @ApiModelProperty(value = "위치참여 실패시 문구")
    private String stpTrLocationMisMsgAttend;

    @ApiModelProperty(value = "스탬프 미적립 이미지 URL")
    private String stpTrNotAccImgUrl;

    @ApiModelProperty(value = "스탬프 적립완료 이미지 URL")
    private String stpTrAccImgUrl;

    @ApiModelProperty(value = "스탬프 당첨시도 이미지 URL")
    private String stpTrWinningAttendImgUrl;

    @ApiModelProperty(value = "정보제공동의 문구 설정")
    private String infomationProvisionAgreementTextSetting;

    @ApiModelProperty(value = "정보제공동의 문구 - 제공받는자")
    private String infomationProvisionRecipient;

    @ApiModelProperty(value = "정보제공동의 문구 - 위탁업체")
    private String infomationProvisionConsignor;

    @ApiModelProperty(value = "정보제공동의 문구 - 이용목적")
    private String infomationProvisionPurposeUse;

    @ApiModelProperty(value = "스탬프 상태 - STAMP_BEFORE : 스탬프 미적립 / STAMP_AFTER_END : 스탬프 적립완료 및 당첨시도없음 / STAMP_AFTER_NEXT : 스탬프 적립완료 및 당첨시도 필요 / GIVEAWAY_SUCCESS : 당첨성공 / GIVEAWAY_FAIL : 당첨 실패")
    private String stampStatus;

    @ApiModelProperty(value = "스탬프에 표시해야할 이미지")
    private String stampDisplayImgUrl;

    @ApiModelProperty(value = "스탬프가 찍혀있는 경우, 내려갈 log tr id")
    private String stpEventLogTrId;
}