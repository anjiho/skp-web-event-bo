package kr.co.syrup.adreport.stamp.event.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class StampEventPanDto implements Serializable {
    private static final long serialVersionUID = -728668823509539577L;

    @ApiModelProperty(value = "스탬프 판 ID")
    private Integer stpPanId;

    @ApiModelProperty(value = "스탬프 판 타이틀명")
    private String stpPanTitle;

    @ApiModelProperty(value = "스탬프 판 테마")
    private String stpPanTheme;

    @ApiModelProperty(value = "스탬프 총 개수")
    private String stpNumber;

    @ApiModelProperty(value = "스탬프 판 이미지 url")
    private String stpPanImgUrl;

    @ApiModelProperty(value = "참여순서 제한여부 (Y : 지정함 / N : 지정안함)")
    private String attendSortSettingYn;

    @ApiModelProperty(value = "스탬프 이미지 설정 여부 (공통, 개별)")
    private String stpImgSettingType;

    @ApiModelProperty(value = "스탬프 판 텍스트 컬러 커스텀 여부")
    private String stpPanTxtColorAssignType;

    @ApiModelProperty(value = "스탬프 판 텍스트 컬러 타입")
    private String stpPanTxtColorInputType;

    @ApiModelProperty(value = "스탬프 판 텍스트 컬러 R")
    private Integer stpPanTxtColorRed;

    @ApiModelProperty(value = "스탬프 판 텍스트 컬러 G")
    private Integer stpPanTxtColorGreen;

    @ApiModelProperty(value = "스탬프 판 텍스트 컬러 B")
    private Integer stpPanTxtColorBlue;

    @ApiModelProperty(value = "스탬프 판 텍스트 컬러 HEX")
    private String stpPanTxtColorHex;

    @ApiModelProperty(value = "스탬프 판 배경 이미지 1")
    private String stpPanBgImgUrl1;

    @ApiModelProperty(value = "스탬프 판 배경 이미지 2")
    private String stpPanBgImgUrl2;

    @ApiModelProperty(value = "스탬프판 BG 색상 지정 종류")
    private String stpPanBgColorAssignType;

    @ApiModelProperty(value = "스탬프판 BG 지정일떄 RGB, HEX 여부")
    private String stpPanBgColorInputType;

    @ApiModelProperty(value = "스탬프판 BG 색상 rgb 값")
    private int stpPanBgColorRed;

    @ApiModelProperty(value = "스탬프판 BG 색상 rgb 값")
    private int stpPanBgColorGreen;

    @ApiModelProperty(value = "스탬프판 BG 색상 rgb 값")
    private int stpPanBgColorBlue;

    @ApiModelProperty(value = "스탬프판 BG 색상 hex 값")
    private String stpPanBgColorHex;

    @ApiModelProperty(value = "스탬프판 더보기 텍스트 색상 지정 종류")
    private String stpPanAddTxtColorAssignType;

    @ApiModelProperty(value = "스탬프판 더보기  텍스트 지정일떄 RGB, HEX 여부)")
    private String stpPanAddTxtColorInputType;

    @ApiModelProperty(value = "스탬프판 더보기 텍스트 색상 rgb 값")
    private int stpPanAddTxtColorRed;

    @ApiModelProperty(value = "스탬프판 더보기 텍스트 색상 rgb 값")
    private int stpPanAddTxtColorGreen;

    @ApiModelProperty(value = "스탬프판 더보기 텍스트 색상 rgb 값")
    private int stpPanAddTxtColorBlue;

    @ApiModelProperty(value = "스탬프판 더보기 텍스트 색상 hex 값")
    private String stpPanAddTxtColorHex;

    @ApiModelProperty(value = "참여 순번 안내 텍스트")
    private String attendSortGuideTxt;

    @ApiModelProperty(value = "참여 순번 종료 텍스트")
    private String attendSortEndTxt;

    @ApiModelProperty(value = "참여 순번 텍스트 색상 지정 여부(BASIC, ASSIGN)")
    private String attendSortTxtColorAssignType;

    @ApiModelProperty(value = "참여 순번 텍스트 배경색 지정일떄 RGB, HEX 여부)")
    private String attendSortTxtColorInputType;

    @ApiModelProperty(value = "참여 순번 텍스트 컬러 R")
    private String attendSortTxtColorRed;

    @ApiModelProperty(value = "참여 순번 텍스트 컬러 G")
    private String attendSortTxtColorGreen;

    @ApiModelProperty(value = "참여 순번 텍스트 컬러 B")
    private String attendSortTxtColorBlue;

    @ApiModelProperty(value = "참여 순번 텍스트 컬러 HEX")
    private String attendSortTxtColorHex;

}