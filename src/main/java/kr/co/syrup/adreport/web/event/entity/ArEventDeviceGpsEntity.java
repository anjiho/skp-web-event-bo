package kr.co.syrup.adreport.web.event.entity;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "AR_EVENT_DEVICE_GPS")
public class ArEventDeviceGpsEntity implements Serializable {

    private static final long serialVersionUID = 5120834218838464377L;

    // 포토 추가건 인덱스
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_gps_id")
    private Integer id;

    // 포토 추가건
    @Column(name = "event_html_id")
    private Integer eventHtmlId;

    // 포토 추가건 순서
    @Column(name = "sort")
    private Integer sort;

    // 포토 추가건 디바이스 명
    @Column(name = "device_name")
    private String deviceName;

    // 포토 추가건 좌표 명
    @Column(name = "gps_name")
    private String gpsName;

    //포토 추가건 업체종류(셀픽, ...)
    @Column(name = "third_party_type")
    private String thirdPartyType;

    // 포토 추가건 위도 좌표
    @Column(name = "device_gps_latitude")
    private String deviceGpsLatitude;

    // 포토 추가건 경도 좌표
    @Column(name = "device_gps_longitude")
    private String deviceGpsLongitude;

    // 포토 추가건 생성자
    @Column(name = "created_by")
    private String createdBy;

    // 포토 추가건 생성일
    @Column(name = "created_date")
    private Date createdDate;

    // 포토 추가건 수정자
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    // 포토 추가건 수정일
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = DateUtils.returnNowDate();
    }

    public static ArEventDeviceGpsEntity ofUpdate(int eventHtmlId, ArEventDeviceGpsEntity findEntity, ArEventDeviceGpsEntity newEntity) {
        ArEventDeviceGpsEntity updateEntity = new ArEventDeviceGpsEntity();
        updateEntity.setId(findEntity.getId());
        updateEntity.setEventHtmlId(eventHtmlId);
        updateEntity.setSort(PredicateUtils.isNull(newEntity.getSort()) ? findEntity.getSort() : newEntity.getSort());
        updateEntity.setDeviceName(PredicateUtils.isNull(newEntity.getDeviceName()) ? findEntity.getDeviceName() : newEntity.getDeviceName());
        updateEntity.setGpsName(PredicateUtils.isNull(newEntity.getGpsName()) ? findEntity.getGpsName() : newEntity.getGpsName());
        updateEntity.setThirdPartyType(PredicateUtils.isNull(newEntity.getThirdPartyType()) ? findEntity.getThirdPartyType() : newEntity.getThirdPartyType());
        updateEntity.setDeviceGpsLatitude(PredicateUtils.isNull(newEntity.getDeviceGpsLatitude()) ? findEntity.getDeviceGpsLatitude() : newEntity.getDeviceGpsLatitude());
        updateEntity.setDeviceGpsLongitude(PredicateUtils.isNull(newEntity.getDeviceGpsLongitude()) ? findEntity.getDeviceGpsLongitude() : newEntity.getDeviceGpsLongitude());
        updateEntity.setCreatedBy(findEntity.getCreatedBy());
        updateEntity.setCreatedDate(findEntity.getCreatedDate());
        updateEntity.setLastModifiedBy(updateEntity.getLastModifiedBy());
        return updateEntity;
    }
}
