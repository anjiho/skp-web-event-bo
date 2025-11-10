package kr.co.syrup.adreport.web.event.dto.response;

import kr.co.syrup.adreport.web.event.dto.request.NftBannerReqDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceGpsInfoDto implements Serializable {

    private static final long serialVersionUID = -4303178325481774563L;

    private Integer id;

    private Integer sort;

    // 포토 추가건 다비아스명
    private String deviceName;

    // 포토 추가건 좌표 명
    private String gpsName;

    //포토 추가건 업체종류(셀픽, ...)
    private String thirdPartyType;

    // 포토 추가건 위도 좌표
    private String deviceGpsLatitude;

    // 포토 추가건 경도 좌표
    private String deviceGpsLongitude;

}
