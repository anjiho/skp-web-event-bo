package kr.co.syrup.adreport.web.event.dto.response;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;

@Setter
@Getter
public class ArEventLogicalResDto implements Serializable {

    private static final long serialVersionUID = 2277502008552966894L;

    private Integer arEventLogicalId;

    // 판 설정  값(판 위치 셀렉트박스)
    private String panPositionType;

    // 판 미션 수
    private Integer panMissionNumber;

    // 브릿지 타입 값
    private String bridgeType;

    // 브릿지 url
    private String bridgeUrl;

    // 브릿지 노출 시간 여부 값(설정 라디오버튼)
    private String bridgeExposureTimeType;

    // 브릿지 노출 시간 값
    private Integer bridgeExposureTimeSecond;

    // 브릿지 화면 방향  값(화면 방향 라디오 코드 값)
    private String bridgeDisplayDirectionType;

    // 브릿지 크기 x
    private String bridgeObjectSizeX;

    // 브릿지 크기 y
    private String bridgeObjectSizeY;

    // 브릿지 크기 z
    private String bridgeObjectSizeZ;

    private String nftWalletImgUrl;

    private String nftWalletPositionX = "0.0";

    private String nftWalletPositionY = "-0.5";

    private String nftWalletPositionZ = "-1.0";

    private String nftWalletSizeX = "0.2";

    private String nftWalletSizeY = "0.2";

    // 브릿지 강제 노출 여부
    private String bridgeForceExposureTimeType;

    // 브릿지 강제 노출 시간 값
    private Integer bridgeForceExposureTimeSecond;
}
