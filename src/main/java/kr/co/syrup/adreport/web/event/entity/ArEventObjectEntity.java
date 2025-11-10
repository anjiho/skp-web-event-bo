package kr.co.syrup.adreport.web.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.ModelMapperUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import kr.co.syrup.adreport.web.event.dto.request.EventObjectDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@ToString
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "AR_EVENT_OBJECT")
public class ArEventObjectEntity {

    // 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(name = "object_size_x")
    private String objectSizeX;

    // 오브젝트 크기(y)
    @Column(name = "object_size_y")
    private String objectSizeY;

    // 오브젝트 크기(z)
    @Column(name = "object_size_z")
    private String objectSizeZ;

    // 동영상 재생반복 여부 값
    private String videoPlayRepeatType;

    // 오브젝트 위치지정 값
    private String objectPositionAssignType;

    // 오브젝트 위치 지정(x)
    @Column(name = "object_position_x")
    private String objectPositionX;

    // 오브젝트 위치 지정(y)
    @Column(name = "object_position_y")
    private String objectPositionY;

    // 오브젝트 위치 지정(z)
    @Column(name = "object_position_z")
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
    @Column(name = "object_change_size_x")
    private String objectChangeSizeX;

    // 오브젝트 change 크기(y)
    @Column(name = "object_change_size_y")
    private String objectChangeSizeY;

    // 오브젝트 change 크기(z)
    @Column(name = "object_change_size_z")
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
    @Column(name = "bridge_object_size_x")
    private String bridgeObjectSizeX;

    // 브릿지 크기 y
    @Column(name = "bridge_object_size_y")
    private String bridgeObjectSizeY;

    // 브릿지 크기 z
    @Column(name = "bridge_object_size_z")
    private String bridgeObjectSizeZ;

    private String objectChangeSettingUrl;

    //3D 오브젝트 위치 설정 여부 값 (NFT 추가건)
    @Column(name = "3d_object_position_setting_type", length = 1)
    private String treedObjectPositionSettingType;

    //3D 오브젝트 위치(x) (NFT 추가건)
    @Column(name = "3d_object_position_x", length = 5)
    private String treedObjectPositionX;

    //3D 오브젝트 위치(y) (NFT 추가건)
    @Column(name = "3d_object_position_y", length = 5)
    private String treedObjectPositionY;

    //3D 오브젝트 위치(z) (NFT 추가건)
    @Column(name = "3d_object_position_z", length = 5)
    private String treedObjectPositionZ;

    // 브릿지 강제 노출 여부
    @Column(name = "bridge_force_exposure_time_type", length = 10)
    private String bridgeForceExposureTimeType;

    // 브릿지 강제 노출 시간
    @Column(name = "bridge_force_exposure_time_second")
    private Integer bridgeForceExposureTimeSecond;

    // 생성자
    private String createdBy;

    // 생성일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;

    // 수정자
    private String lastModifiedBy;

    // 수정일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date lastModifiedDate;

    /**
     * insert 전 default 값 셋팅
     */
    @PrePersist
    public void prePersist() {
        this.videoPlayRepeatType = PredicateUtils.isNull(this.videoPlayRepeatType) || StringUtils.isEmpty(this.videoPlayRepeatType) ? StringDefine.N.name() : this.videoPlayRepeatType;
        this.locationExposureControlType = PredicateUtils.isNull(this.locationExposureControlType) || StringUtils.isEmpty(this.locationExposureControlType) ? StringDefine.N.name() : this.locationExposureControlType;

        this.maxExposureType = PredicateUtils.isNull(this.maxExposureType) || StringUtils.isEmpty(this.maxExposureType) ? StringDefine.N.name() : this.maxExposureType;
        this.maxExposureCount = PredicateUtils.isNull(this.maxExposureCount) ? 0 : this.maxExposureCount;

        this.dayExposureType = PredicateUtils.isNull(this.dayExposureType) || StringUtils.isEmpty(this.dayExposureType) ? StringDefine.N.name() : this.dayExposureType;
        this.dayExposureCount = PredicateUtils.isNull(this.dayExposureCount) ? 0 : this.dayExposureCount;

        this.hourExposureType = PredicateUtils.isNull(this.hourExposureType) || StringUtils.isEmpty(this.hourExposureType) ? StringDefine.N.name() : this.hourExposureType;
        this.hourExposureCount = PredicateUtils.isNull(this.hourExposureCount) ? 0 : this.hourExposureCount;

        this.attendCodeExposureType = PredicateUtils.isNull(this.attendCodeExposureType) || StringUtils.isEmpty(this.attendCodeExposureType) ? StringDefine.N.name() : this.attendCodeExposureType;
        this.attendCodeLimitType = PredicateUtils.isNull(this.attendCodeLimitType) ? 0 : this.attendCodeLimitType;
        this.attendCodeExposureCount = PredicateUtils.isNull(this.attendCodeExposureCount) ? 0 : this.attendCodeExposureCount;

        this.exposurePercentType = PredicateUtils.isNull(this.exposurePercentType) || StringUtils.isEmpty(this.exposurePercentType) ? StringDefine.N.name() : this.exposurePercentType;
        this.bridgeExposureTimeType = PredicateUtils.isNull(this.bridgeExposureTimeType) || StringUtils.isEmpty(this.bridgeExposureTimeType) ? StringDefine.N.name() : this.bridgeExposureTimeType;
        this.bridgeExposureTimeSecond = PredicateUtils.isNull(this.bridgeExposureTimeSecond) ? 0 : this.bridgeExposureTimeSecond;

        this.bridgeForceExposureTimeType = PredicateUtils.isNull(this.bridgeForceExposureTimeType) || StringUtils.isEmpty(this.bridgeForceExposureTimeType) ? StringDefine.N.name() : this.bridgeForceExposureTimeType;
        this.bridgeForceExposureTimeSecond = PredicateUtils.isNull(this.bridgeForceExposureTimeSecond) ? 0 : this.bridgeForceExposureTimeSecond;

        this.createdDate = PredicateUtils.isNull(this.createdDate) ? DateUtils.returnNowDate() : this.createdDate;
    }

    public static ArEventObjectEntity of(int arEventId, EventObjectDto dto) {
        ArEventObjectEntity arEventObjectEntity = ModelMapperUtils.getModelMapper().map(dto, ArEventObjectEntity.class);
        arEventObjectEntity.setArEventId(arEventId);
        arEventObjectEntity.setCreatedDate(DateUtils.returnNowDate());
        return arEventObjectEntity;
    }
}
