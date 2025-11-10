package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;

@Setter
@Getter
public class EvenScanningImageDto implements Serializable {

    private static final long serialVersionUID = -5400485848443622373L;

    private Integer arEventScanningImageId;

    // 스캐닝 이미지 url
    private String scanningImageUrl;

    // 스캐닝 사운드 선택 타입 값
    private String scanningSoundType;

    // 스캐닝 사운드 데이터
    private String scanningSoundFile;

    // 정렬 순서
    private Integer scanningImageSort;

    // 활성화 썸네일
    private String activeThumbnailUrl;

    // 비활성화 썸네일
    private String inactiveThumbnailUrl;

}
