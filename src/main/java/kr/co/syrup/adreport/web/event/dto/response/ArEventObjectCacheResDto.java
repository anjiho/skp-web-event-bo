package kr.co.syrup.adreport.web.event.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;

@Data
public class ArEventObjectCacheResDto implements Serializable {

    private static final long serialVersionUID = -497778216723601008L;

    private Integer arEventObjectId;

    // 이벤트 아이디
    private Integer arEventId;

    // 오브젝트 순서
    private Integer objectSort;

    // 오브젝트 설정 값
    private String objectSettingType;

    // 오브젝트 설정 파일 URL
    private String objectSettingUrl;

    // 오브젝트 크기(x)
    private String objectSizeX;

    // 오브젝트 크기(y)
    private String objectSizeY;

    // 오브젝트 크기(z)
    private String objectSizeZ;

    // 동영상 재생반복 여부 값
    private String videoPlayRepeatType;

    // 오브젝트 위치지정 값
    private String objectPositionAssignType;

    // 오브젝트 위치 지정(x)
    private String objectPositionX;

    // 오브젝트 위치 지정(y)
    private String objectPositionY;

    // 오브젝트 위치 지정(z)
    private String objectPositionZ;

    // STAY EFFECT 설정  값
    private String stayEffectType;

    // 클릭 이벤트 설정  값
    private String clickEventType;

    // 오브젝트 change 설정 값
    private String objectChangeSettingType;

    // 오브젝트 change 설정 파일 URL
    private String objectChangeSettingVideoUrl;

    // 오브젝트 change 크기(x)
    private String objectChangeSizeX;

    // 오브젝트 change 크기(y)
    private String objectChangeSizeY;

    // 오브젝트 change 크기(z)
    private String objectChangeSizeZ;

    // 캐치 사운드 설정 값
    private String catchSoundType;

    // 캐치 사운드  값(URL, Library)
    private String catchSoundFile;

    // 노출제어 값
    private String exposureControlType;

    // 위치 노출제어 값
    private String locationExposureControlType;

    //  위치 노출제어 pid
    private String locationExposureControlPid;

    //[DTWS-323] 위치 노출제어 pid 좌표 종류(RELATIVE(상대좌표), ABSOLUTE(절대좌표))
    private String pidCoordinateType;

    // 최대 노출 여부 값
    private String maxExposureType;

    // 최대 노출 수
    private Integer maxExposureCount;

    // 일 노출 여부  값
    private String dayExposureType;

    // 일 노출 수
    private Integer dayExposureCount;

    // 시간당 노출 여부 값
    private String hourExposureType;

    // 시간당 노출 수
    private Integer hourExposureCount;

    // 참여번호당 노출수 타입 값
    private String attendCodeExposureType;

    // 참여번호당 노출수 지정시 타입(0:전체기한내, 1일)
    private Integer attendCodeLimitType;

    // 참여번호당 노출수
    private Integer attendCodeExposureCount;

    // 노출 확률 여부 값
    private String exposurePercentType;

    // 노출 확률 %(0.01 ~ 100)
    private String exposurePercent;

    // 브릿지 타입 값
    private String bridgeType;

    // 브릿지 파일 url
    private String bridgeUrl;

    // 브릿지 노출 시간 여부 값(설정 라디오버튼)
    private String bridgeExposureTimeType;

    // 브릿지 노출 시간 값
    private Integer bridgeExposureTimeSecond;

    // 브릿지 화면 방향  값(화면 방향 라디오 코드 값)
    private String bridgeDisplayDirectionType;

    // 미션클리어형 비활성 썸네일 url
    private String missionInactiveThumbnailUrl;

    // 미션클리어형 활성 썸네일 url
    private String missionActiveThumbnailUrl;

    // 브릿지 크기 x
    private String bridgeObjectSizeX;

    // 브릿지 크기 y
    private String bridgeObjectSizeY;

    // 브릿지 크기 z
    private String bridgeObjectSizeZ;

    private String objectChangeSettingUrl;

    //3D 오브젝트 위치 설정 여부 값 (NFT 추가건)
    private String treedObjectPositionSettingType;

    //3D 오브젝트 위치(x) (NFT 추가건)
    private String treedObjectPositionX;

    //3D 오브젝트 위치(y) (NFT 추가건)
    private String treedObjectPositionY;

    //3D 오브젝트 위치(z) (NFT 추가건)
    private String treedObjectPositionZ;

    // 브릿지 강제 노출 여부
    private String bridgeForceExposureTimeType;

    // 브릿지 강제 노출 시간
    private Integer bridgeForceExposureTimeSecond;
}
