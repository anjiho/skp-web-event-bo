package kr.co.syrup.adreport.web.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventDeviceGpsResDto implements Serializable {

    private static final long serialVersionUID = -6738380865210684523L;

    //인덱스
    private Integer id;

    // 포토 추가건 순서
    private Integer sort;

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
