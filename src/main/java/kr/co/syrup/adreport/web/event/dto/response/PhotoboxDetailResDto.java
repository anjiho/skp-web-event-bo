package kr.co.syrup.adreport.web.event.dto.response;

import kr.co.syrup.adreport.web.event.dto.request.NftBannerReqDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PhotoboxDetailResDto implements Serializable {

    private static final long serialVersionUID = 4623745977810326341L;

    // 포토 추가건 디바이스 위치 찾기 버튼 설정 여부 (설정안함 : N / 설정함 : Y)
    private String deviceLocationFindSettingYn;

    // 포토 추가건 디바이스 위치 찾기 버튼 문구
    private String deviceLocationFindButtonText;

    // 포토 추가건 위치 찾기 노출 설정 (지도보기 : MAP / 팝업보기 : POPUP)
    private String locationFindExposureType;

    // 포토 추가건 위치찾기 팝업 이미지 url
    private String locationFindPopupImgUrl;

    // 포토 추가건 무료출력수 제어 (설정안함 : N / 설정함 : Y)
    private String freePrintControlYn;

    // 포토 추가건 고객당 출력 개수
    private Integer freePrintCustomerCount;

    private List<NftBannerReqDto> bannerList;

    private List<DeviceGpsInfoDto> deviceGpsList;
}
