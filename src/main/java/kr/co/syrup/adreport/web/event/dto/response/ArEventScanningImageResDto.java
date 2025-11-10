package kr.co.syrup.adreport.web.event.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;

@Setter
@Getter
public class ArEventScanningImageResDto implements Serializable {

    private static final long serialVersionUID = 3993769166032772647L;

    private Integer scanningImageNumber;

    // 스캐닝 이미지 url
    private String scanningImageUrl;

    // 스캐닝 사운드 선택 타입 값
    private String scanningSoundType;

    // 스캐닝 사운드 데이터
    private String scanningSoundFile;

    // 활성화 썸네일
    private String activeThumbnailUrl;

    // 비활성화 썸네일
    private String inactiveThumbnailUrl;

    @JsonProperty(value = "sortNumber")
    private String scanningImageSort;

}
