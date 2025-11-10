package kr.co.syrup.adreport.web.event.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class ArEventObjectResDto implements Serializable {

    private static final long serialVersionUID = 133056893007854278L;

    private String clickEventType;

    // 오브젝트 설정 값
    private String objectSettingType;

    // 오브젝트 인덱스
    private Integer arEventObjectId;

    // 오브젝트 순서
    private Integer objectSort;

    // 오브젝트 크기(x)
    private BigDecimal objectSizeX;

    // 오브젝트 크기(y)
    private BigDecimal objectSizeY;

    // 오브젝트 크기(z)
    private BigDecimal objectSizeZ;

    // 오브젝트 위치 지정(x)
    private BigDecimal objectPositionX;

    // 오브젝트 위치 지정(y)
    private BigDecimal objectPositionY;

    // 오브젝트 위치 지정(z)
    private BigDecimal objectPositionZ;

    // 오브젝트 설정 파일 URL
    private String objectSettingUrl;

    // STAY EFFECT 설정  값
    private String stayEffectType;

    // 동영상 재생반복 여부 값
    private String videoPlayRepeatType;

    //터치 관련 데이터 시작
    // 캐치 사운드 설정 값
    private String catchSoundType;

    // 캐치 사운드  값(URL, Library)
    private String catchSoundFile;

    // 오브젝트 change 설정 값(touch관련)
    private String objectChangeSettingType;

    // 오브젝트 change 설정 파일 URL
//    @JsonProperty(value = "objectChangeSettingUrl")
//    private String objectChangeSettingVideoUrl;

    // 오브젝트 change 크기(x)
    private BigDecimal objectChangeSizeX;

    // 오브젝트 change 크기(y)
    private BigDecimal objectChangeSizeY;

    // 오브젝트 change 크기(z)
    private BigDecimal objectChangeSizeZ;
    //터치 관련 데이터 끝

    // 미션클리어형 비활성 썸네일 url
    private String missionInactiveThumbnailUrl;

    // 미션클리어형 활성 썸네일 url
    private String missionActiveThumbnailUrl;

    //AR_EVENT_LOGICAL
    // 판 설정  값(판 위치 셀렉트박스)
    private String panPositionType;

    // 브릿지 타입 값
    private String bridgeType;

    // 브릿지 파일 url
    private String bridgeUrl;

    // 브릿지 노출 시간 값
    private Integer bridgeExposureTimeSecond;

    // 브릿지 화면 방향  값(화면 방향 라디오 코드 값)
    private String bridgeDisplayDirectionType;

    //노출제어 값
    private String exposureControlType;

    // 브릿지 크기 x
    private String bridgeObjectSizeX;

    // 브릿지 크기 y
    private String bridgeObjectSizeY;

    // 브릿지 크기 z
    private String bridgeObjectSizeZ;

    private String objectChangeSettingUrl;

    // 브릿지 강제 노출 여부
    private String bridgeForceExposureTimeType;

    // 브릿지 강제 노출 시간 값
    private Integer bridgeForceExposureTimeSecond;

    // 위치 노출제어 값
    private String locationExposureControlType;

    //  위치 노출제어 pid
    private String locationExposureControlPid;

    //[DTWS-323] 위치 노출제어 pid 좌표 종류(RELATIVE(상대좌표), ABSOLUTE(절대좌표))
    private String pidCoordinateType;

}
