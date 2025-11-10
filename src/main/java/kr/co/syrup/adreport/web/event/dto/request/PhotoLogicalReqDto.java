package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Data
public class PhotoLogicalReqDto implements Serializable {

    private static final long serialVersionUID = 7691805634739390193L;

    //인덱스
    private Integer id;

    // 포토 추가건 튜토리얼 설정여부 (설정안함 : N/ 설정함 : Y)
    private String tutorialYn;


    // 포토 추가건 AR 포토 비율 설정 (기본비율 : BASIC / 확장비율 : EXTENSION)
    private String photoRatioSettingType;

    // 포토 추가건 AR 프레임 설정 여부 (설정안함 : N/ 설정함 : Y)
    private String arFrameSettingYn;

    // 포토 추가건 탭메뉴 추가설정 (설정안함 : N/ 설정함 : Y)
    private String photoTabMenuAddSettingYn;

    // 포토 추가건 탭메뉴 제목
    private String tabMenuTitle;

    // 포토 추가건 AR 필터 설정 (설정안함 : N/ 설정함 : Y)
    private String arFilterSettingYn;

    // 포토 추가건 AR 캐릭터 설정 (설정안함 : N/ 설정함 : Y)
    private String arCharacterSettingYn;

    // 포토 추가건 스티커 설정 (설정안함 : N/ 설정함 : Y)
    private String arStickerSettingYn;

    // 포토 추가건 촬영 결과 이미지 url
    private String filmResultImgUrl;

    // 포토 추가건 해시태그 설정 여부 (설정안함 : N/ 설정함 : Y)
    private String hashTagSettingYn;

    // 포토 추가건 해시태그 값 (콤마 , 로 구분자)
    private List<String> hashTagValue;

    // 포토 추가건 공유하기 동의팝업 설정여부 (설정안함 : N/ 설정함 : Y)
    private String shareAgreePopupSettingYn;

    // 포토 추가건 동의 팝업 문구
    private String agreePopupText;

    // 포토 추가건 자세히 보기 링크 url
    private String agreePopupDetailLinkUrl;

    // 포토 추가건 입력창 기본 문구
    private String agreePopupInputText;

    // 포토 추가건 사진 출력 설정 여부 (설정안함 : N/ 설정함 : Y)
    private String photoPrintSettingYn;

    // 포토 추가건 사진 출력 버튼명
    private String photoPrintButtonText;

    // 포토 추가건 경품 당첨 설정 여부 (설정안함 : N/ 설정함 : Y)
    private String photoGiveAwaySettingYn;

    // 포토 추가건 경품 당첨 버튼명
    private String photoGiveAwayButtonText;

    // 포토 추가건 결과 하단 이미지 설정 여부 (설정안함 : N/ 설정함 : Y)
    private String filmResultFooterImgSettingYn;

    // 포토 추가건 하단 이미지 url
    private String filmResultFooterImgUrl;
}
