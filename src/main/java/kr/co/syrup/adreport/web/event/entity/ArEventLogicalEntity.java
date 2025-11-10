package kr.co.syrup.adreport.web.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.ModelMapperUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import kr.co.syrup.adreport.web.event.dto.request.EventLogicalDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "AR_EVENT_LOGICAL")
public class ArEventLogicalEntity implements Serializable {

    private static final long serialVersionUID = 6145165427038656168L;

    // 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer arEventLogicalId;

    // 이벤트 아이디
    private Integer arEventId;

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
    @Column(name = "bridge_object_size_x")
    private String bridgeObjectSizeX;

    // 브릿지 크기 y
    @Column(name = "bridge_object_size_y")
    private String bridgeObjectSizeY;

    // 브릿지 크기 z
    @Column(name = "bridge_object_size_z")
    private String bridgeObjectSizeZ;

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

    //nft 지갑 이미지 URL (NFT 개발건)
    @Column(name = "nft_wallet_img_url", length = 100)
    private String nftWalletImgUrl;

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
        this.bridgeExposureTimeType = PredicateUtils.isNull(this.bridgeExposureTimeType) || StringUtils.isEmpty(this.bridgeExposureTimeType) ? StringDefine.N.name() : this.bridgeExposureTimeType;
        this.bridgeExposureTimeSecond = PredicateUtils.isNull(this.bridgeExposureTimeSecond) ? 0 : this.bridgeExposureTimeSecond;

        this.bridgeForceExposureTimeType = PredicateUtils.isNull(this.bridgeForceExposureTimeType) || StringUtils.isEmpty(this.bridgeForceExposureTimeType) ? StringDefine.N.name() : this.bridgeForceExposureTimeType;
        this.bridgeForceExposureTimeSecond = PredicateUtils.isNull(this.bridgeForceExposureTimeSecond) ? 0 : this.bridgeForceExposureTimeSecond;

        this.createdDate = this.createdDate == null ? DateUtils.returnNowDate() : this.createdDate;
    }

    public static ArEventLogicalEntity of(int arEventId, EventLogicalDto dto) {
        ArEventLogicalEntity logicalEntity = ModelMapperUtils.getModelMapper().map(dto, ArEventLogicalEntity.class);
        logicalEntity.setArEventId(arEventId);
        return logicalEntity;
    }

    public static ArEventLogicalEntity updateOf(ArEventLogicalEntity arEventLogicalEntity, EventLogicalDto dto) {
        ArEventLogicalEntity logicalEntity = ModelMapperUtils.getModelMapper().map(dto, ArEventLogicalEntity.class);
        logicalEntity.setArEventLogicalId(arEventLogicalEntity.getArEventLogicalId());
        logicalEntity.setArEventId(arEventLogicalEntity.getArEventId());
        logicalEntity.setCreatedDate(arEventLogicalEntity.getCreatedDate());
        logicalEntity.setLastModifiedDate(DateUtils.returnNowDate());
        return logicalEntity;
    }
}
