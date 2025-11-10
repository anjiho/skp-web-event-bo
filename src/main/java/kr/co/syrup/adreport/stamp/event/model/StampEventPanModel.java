package kr.co.syrup.adreport.stamp.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.session.SodarMemberSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

// 스탬프 판 설정
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StampEventPanModel implements Serializable {

    private static final long serialVersionUID = -6488254148176212038L;

    // 스탬프판 인덱스
    private Integer stpPanId;

    // 스탬프 메인 인덱스
    private Integer stpId;

    // 스탬프판 명
    private String stpPanTitle;

    //스탬프 이벤트명 사용 여부
    private String stpEventTitleUseYn;

    // 스탬프판 테마(기본, 테마1~3)
    private String stpPanTheme;

    // 스탬프 개수
    private Integer stpNumber;

    // 배경 이미지 URL
    private String stpPanBgImgUrl;

    // 스탬프판 이미지 URL
    private String stpPanImgUrl;

    // 참여 순서 제한 여부(순서있음, 순서없음)
    private String attendSortSettingYn;

    // 스탬프 이미지 설정(공통, 개별)
    private String stpImgSettingType;

    private String stpPanTxtColorAssignType;

    private String stpPanTxtColorInputType;

    private Integer stpPanTxtColorRed;

    private Integer stpPanTxtColorGreen;

    private Integer stpPanTxtColorBlue;

    private String stpPanTxtColorHex;

    // 정보 제공동의 문구설정(N:설정안함, Y:설정)
    private String informationProvisionAgreementTextSetting;

    // 정보 제공동의 문구 - 제공받는 자
    private String informationProvisionRecipient;

    // 정보 제공동의 문구 - 위탁업체
    private String informationProvisionConsignor;

    // 정보 제공동의 문구 - 이용목적
    private String informationProvisionPurposeUse;

    //스탬프 판 배경 이미지 1
    private String stpPanBgImgUrl1;

    //스탬프 판 배경 이미지 2
    private String stpPanBgImgUrl2;

    //스탬프판 BG 색상 지정 종류
    private String stpPanBgColorAssignType;

    //스탬프판 BG 지정일떄 (RGB, HEX 여부)
    private String stpPanBgColorInputType;

    //스탬프판 BG 색상 rgb 값
    private Integer stpPanBgColorRed;

    //스탬프판 BG 색상 rgb 값
    private Integer stpPanBgColorGreen;

    //스탬프판 BG 색상 rgb 값
    private Integer stpPanBgColorBlue;

    //스탬프판 BG 색상 hex 값
    private String stpPanBgColorHex;

    //스탬프판 더보기 텍스트 색상 지정 종류
    private String stpPanAddTxtColorAssignType;

    //스탬프판 더보기  텍스트 지정일떄 (RGB, HEX 여부)
    private String stpPanAddTxtColorInputType;

    //스탬프판 더보기 텍스트 색상 rgb 값
    private Integer stpPanAddTxtColorRed;

    //스탬프판 더보기 텍스트 색상 rgb 값
    private Integer stpPanAddTxtColorGreen;

    //스탬프판 더보기 텍스트 색상 rgb 값
    private Integer stpPanAddTxtColorBlue;

    //스탬프판 더보기 텍스트 색상 hex 값
    private String stpPanAddTxtColorHex;

    // 참여 순번 안내 텍스트 - 2차 고도화
    private String attendSortGuideTxt;

    // 참여 순번 종료 텍스트 - 2차 고도화
    private String attendSortEndTxt;

    // 참여 순번 텍스트 색상 지정 여부(BASIC, ASSIGN) - 2차 고도화
    private String attendSortTxtColorAssignType;

    // 참여 순번 텍스트 배경색 지정일떄 RGB, HEX 여부) - 2차 고도화
    private String attendSortTxtColorInputType;

    // 참여 순번 텍스트 컬러 R - 2차 고도화
    private Integer attendSortTxtColorRed;

    // 참여 순번 텍스트 컬러 G - 2차 고도화
    private Integer attendSortTxtColorGreen;

    // 참여 순번 텍스트 컬러 B - 2차 고도화
    private Integer attendSortTxtColorBlue;

    // 참여 순번 텍스트 컬러 HEX - 2차 고도화
    private String attendSortTxtColorHex;

    private String createdBy = PredicateUtils.isNotNull(SodarMemberSession.get()) ? SodarMemberSession.get().getName() : "개발자";

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;

    private String lastModifiedBy = PredicateUtils.isNotNull(SodarMemberSession.get()) ? SodarMemberSession.get().getName() : "개발자";

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date lastModifiedDate;

    public static StampEventPanModel ofSave(StampEventPanModel stampEventPanModel, Integer stpId) {
        if (PredicateUtils.isNotNull(stpId)) {
            stampEventPanModel.setStpId(stpId);
        }
        return stampEventPanModel;
    }
}
