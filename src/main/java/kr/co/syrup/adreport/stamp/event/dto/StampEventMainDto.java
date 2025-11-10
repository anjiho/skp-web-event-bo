package kr.co.syrup.adreport.stamp.event.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class StampEventMainDto implements Serializable {

    private static final long serialVersionUID = 3676199386656877651L;

    @ApiModelProperty(value = "스탬프 ID")
    private Integer stpId;

    @ApiModelProperty(value = "이벤트 ID")
    private String eventId;

    @ApiModelProperty(value = "스탬프 메인 설정 여부")
    private String stpMainSettingYn;

    @ApiModelProperty(value = "스탬프 인증방식 (휴대폰번호 / 전화번호)")
    private String stpAttendAuthCondition;

    @ApiModelProperty(value = "스탬프 참여코드 미매칭시 메세지")
    private String stpAttendCodeMisTxt;

    @ApiModelProperty(value = "스탬프 배경 이미지 url")
    private String stpBgImgUrl;

    @ApiModelProperty(value = "추첨형 당첨 기점 번호 (1,2,3..)")
    private String winningRaffleStartPoint;

    @ApiModelProperty(value = "당첨 타입 (N : 당첨없음, Y : 당첨있음)")
    private String stpWinningType;

    @ApiModelProperty(value = "스탬프 메인 BG 색상 지정 종류")
    private String stpMainBgColorAssignType;

    @ApiModelProperty(value = "스탬프 메인 BG 색상 지정일떄 RGB, HEX 여부)")
    private String stpMainBgColorInputType;

    @ApiModelProperty(value = "스탬프 메인 색상 rgb 값")
    private int stpMainBgColorRed;

    @ApiModelProperty(value = "스탬프 메인 색상 rgb 값")
    private int stpMainBgColorGreen;

    @ApiModelProperty(value = "스탬프 메인 색상 rgb 값")
    private int stpMainBgColorBlue;

    @ApiModelProperty(value = "스탬프 메인 색상 hex 값")
    private String stpMainBgColorHex;

    @ApiModelProperty(value = "당첨 참여순번 설정여부 (N : 설정안함 / Y : 설정함")
    private String stpAttendSortSettingYn;

}
