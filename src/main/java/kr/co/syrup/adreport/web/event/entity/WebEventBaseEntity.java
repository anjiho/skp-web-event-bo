package kr.co.syrup.adreport.web.event.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import kr.co.syrup.adreport.web.event.dto.request.EventBaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "WEB_EVENT_BASE")
public class WebEventBaseEntity implements Serializable {

    private static final long serialVersionUID = -5569975245365192952L;
    // 인데스
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "event_id")
    //이벤트 아이디
    private String eventId;

    // 이벤트 타이틀
    private String eventTitle;

    // 계약 인덱스 값
    private String marketingId;

    // 계약상태 값
    private String contractStatus;

    // 이벤트 종류 타입(AR, ROULETTE)
    private String eventType;

    // 서비스 시작일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="Asia/Seoul")
    private Date eventStartDate;

    // 서비스 종료일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="Asia/Seoul")
    private Date eventEndDate;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="Asia/Seoul")
    private Date realEventEndDate;

    // QR코드 이미지 URL
    private String qrCodeUrl;

    // 스탬프 연결 여부(연결함, 연결안함)
    private String stpConnectYn;

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
        this.createdDate = PredicateUtils.isNull(this.createdDate) ? DateUtils.returnNowDate() : this.createdDate;
        this.contractStatus = PredicateUtils.isNull(this.contractStatus) || StringUtils.isEmpty(this.contractStatus) ? "00" : this.contractStatus;
        //this.eventType = PredicateUtils.isNull(this.eventType) || StringUtils.isEmpty(this.eventType) ? StringDefine.AR.name() : this.eventType;
    }

    public static WebEventBaseEntity of(int webEventSeq, EventBaseDto dto) {
        WebEventBaseEntity entity = new WebEventBaseEntity();
        entity.setEventId(webEventSeq == 0 ? "000001" : generateEventId(String.valueOf(webEventSeq)));
        entity.setEventTitle(dto.getEventTitle());
        entity.setMarketingId(dto.getMarketingId());
        entity.setEventType(dto.getEventType());
        entity.setEventStartDate(DateUtils.convertDateTimeFormat3(dto.getEventStartDate()));
        entity.setEventEndDate(DateUtils.convertDateTimeFormat3(dto.getEventEndDate()));
        entity.setStpConnectYn(dto.getStpConnectYn());
        entity.setQrCodeUrl(dto.getQrCodeUrl());
        return entity;
    }

    public static WebEventBaseEntity updateOf(WebEventBaseEntity webEventBaseEntity, String eventId, EventBaseDto dto) {
        WebEventBaseEntity entity = new WebEventBaseEntity();
        entity.setId(webEventBaseEntity.getId());
        entity.setEventId(eventId);
        entity.setEventTitle(dto.getEventTitle());
        entity.setMarketingId(dto.getMarketingId());
        entity.setContractStatus(webEventBaseEntity.getContractStatus());
        entity.setEventType(dto.getEventType());
        entity.setEventStartDate( PredicateUtils.isNull(dto.getEventStartDate()) ? webEventBaseEntity.getEventStartDate() : DateUtils.convertDateTimeFormat3(dto.getEventStartDate()));
        entity.setEventEndDate( PredicateUtils.isNull(dto.getEventEndDate()) ? webEventBaseEntity.getEventEndDate() : DateUtils.convertDateTimeFormat3(dto.getEventEndDate()) );
        entity.setRealEventEndDate(StringUtils.isNotEmpty(dto.getRealEventEndDate()) ? DateUtils.convertDateTimeFormat2(dto.getRealEventEndDate()) : null);
        entity.setStpConnectYn(PredicateUtils.isNotNull(dto.getStpConnectYn()) ? dto.getStpConnectYn() : "N");
        entity.setQrCodeUrl(dto.getQrCodeUrl());
        entity.setCreatedDate(webEventBaseEntity.getCreatedDate());
        entity.setLastModifiedDate(DateUtils.returnNowDate());
        return entity;
    }

    public static WebEventBaseEntity contractUpdateOf(WebEventBaseEntity webEventBaseEntity, String eventId, String marketingId, String contractStatus, String eventEndDate, String lastModifiedBy) {
        WebEventBaseEntity entity = new WebEventBaseEntity();
        entity.setId(webEventBaseEntity.getId());
        entity.setEventId(eventId);
        entity.setEventTitle(webEventBaseEntity.getEventTitle());
        entity.setMarketingId(marketingId);
        entity.setContractStatus(contractStatus);
        entity.setEventType(webEventBaseEntity.getEventType());
        entity.setEventStartDate(webEventBaseEntity.getEventStartDate());
        entity.setEventEndDate(StringUtils.isEmpty(eventEndDate) ? webEventBaseEntity.getEventEndDate() : DateUtils.convertDateTimeFormat3(eventEndDate));
        entity.setRealEventEndDate(webEventBaseEntity.getRealEventEndDate());
        entity.setQrCodeUrl(webEventBaseEntity.getQrCodeUrl());
        entity.setStpConnectYn(webEventBaseEntity.getStpConnectYn());
        entity.setCreatedDate(webEventBaseEntity.getCreatedDate());
        entity.setLastModifiedBy(lastModifiedBy);
        entity.setLastModifiedDate(DateUtils.returnNowDate());
        return entity;
    }

    public static WebEventBaseEntity contractAfterOf(WebEventBaseEntity webEventBaseEntity, String eventId, String marketingId, String serviceEndDate) {
        WebEventBaseEntity entity = new WebEventBaseEntity();
        entity.setId(webEventBaseEntity.getId());
        entity.setEventId(eventId);
        entity.setEventTitle(webEventBaseEntity.getEventTitle());
        entity.setMarketingId(marketingId);
        entity.setContractStatus(webEventBaseEntity.getContractStatus());
        entity.setEventType(webEventBaseEntity.getEventType());
        entity.setEventStartDate(webEventBaseEntity.getEventStartDate());
        entity.setEventEndDate(DateUtils.convertDateTimeFormat2(DateUtils.convertDateFormat(serviceEndDate)));
        entity.setRealEventEndDate(webEventBaseEntity.getRealEventEndDate());
        entity.setQrCodeUrl(webEventBaseEntity.getQrCodeUrl());
        entity.setStpConnectYn(webEventBaseEntity.getStpConnectYn());
        entity.setCreatedDate(webEventBaseEntity.getCreatedDate());
        entity.setLastModifiedBy(webEventBaseEntity.getLastModifiedBy());
        entity.setLastModifiedDate(DateUtils.returnNowDate());
        return entity;
    }

    public static WebEventBaseEntity ofStamp(int webEventSeq, EventBaseDto dto) {
        WebEventBaseEntity entity = new WebEventBaseEntity();
        entity.setEventId(webEventSeq == 0 ? "S000001" : generateEventIdByStamp(String.valueOf(webEventSeq)));
        entity.setEventTitle(dto.getEventTitle());
        entity.setMarketingId(dto.getMarketingId());
        entity.setEventType(dto.getEventType());
        entity.setEventStartDate(DateUtils.convertDateTimeFormat3(dto.getEventStartDate()));
        entity.setEventEndDate(DateUtils.convertDateTimeFormat3(dto.getEventEndDate()));
        entity.setStpConnectYn(dto.getStpConnectYn());
        entity.setQrCodeUrl(dto.getQrCodeUrl());
        return entity;
    }

    public static String generateEventId(String prevEventId) {
        String generateCode = "";
        if (!"".equals(prevEventId)) {
            generateCode = String.format("%06d", Integer.parseInt(prevEventId) + 1);
        } else {
            generateCode = "000001";
        }
        return generateCode;
    }

    public static String generateEventIdByStamp(String prevEventId) {
        String generateCode = "";
        if (PredicateUtils.isNotNull(prevEventId)) {
            generateCode = String.format("%06d", Integer.parseInt(prevEventId) + 1);
        } else {
            generateCode = "000001";
        }
        return StringTools.joinStringsNoSeparator("S", generateCode);
    }
}
