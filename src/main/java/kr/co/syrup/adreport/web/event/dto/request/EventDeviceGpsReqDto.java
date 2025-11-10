package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@NoArgsConstructor
@Data
public class EventDeviceGpsReqDto implements Serializable {

    private static final long serialVersionUID = -4776396606791811428L;

    //인덱스
    private Integer id;

    // 포토 추가건 순서
    private Integer sort;

    //포토 추가건 디바이스 명
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
