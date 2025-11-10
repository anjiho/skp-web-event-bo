package kr.co.syrup.adreport.web.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.ModelMapperUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.dto.request.EventButtonDto;
import kr.co.syrup.adreport.web.event.session.SodarMemberSession;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "AR_EVENT_BUTTON")
public class ArEventButtonEntity implements Serializable {

    private static final long serialVersionUID = -8800511827035917587L;

    // 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer arEventButtonId;

    // 이벤트 아이디
    private Integer arEventId;

    // 스탬프 메인 인덱스
    private Integer stpId;

    // 버튼 배경색 지정 여부 값(AR_EVENT_CATEGORY)
    private String arButtonBgColorAssignType;

    // 버튼 배경색 지정일떄 RGB, HEX 여부
    private String arButtonBgColorInputType;

    // 버튼 배경색 rgb 값
    private Integer arButtonBgColorRed;

    // 버튼 배경색 rgb 값
    private Integer arButtonBgColorGreen;

    // 버튼 배경색 rgb 값
    private Integer arButtonBgColorBlue;

    // 버튼 배경색 hex 값
    private String arButtonBgColorHex;

    // 버튼색 지정 여부 값
    private String arButtonColorAssignType;

    // 버튼색 지정일떄 RGB, HEX 여부
    private String arButtonColorInputType;

    // 버튼색 rgb 값
    private Integer arButtonColorRed;

    // 버튼색 rgb 값
    private Integer arButtonColorGreen;

    // 버튼색 rgb 값
    private Integer arButtonColorBlue;

    // 버튼색 hex
    private String arButtonColorHex;

    // 버튼 text 색 지정 여부 값
    private String arButtonTextColorAssignType;

    // 버튼 text 색 지정일떄 RGB, HEX 여부
    private String arButtonTextColorInputType;

    // 버튼 text 색 rgb값
    private Integer arButtonTextColorRed;

    // 버튼 text 색 rgb값
    private Integer arButtonTextColorGreen;

    // 버튼 text 색 rgb값
    private Integer arButtonTextColorBlue;

    // 버튼 text 색 hext값
    private String arButtonTextColorHex;

    // 버튼 text 문구 지정
    private String arButtonText;

    // 서베이고 추가건 버튼 유형 지정 (둥글게 : ROUND, 부드럽게 : SOFT, 각지게 : ANGLE)
    private String arButtonShapeType;

    // 생성자
    private String createdBy = PredicateUtils.isNotNull(SodarMemberSession.get()) ? SodarMemberSession.get().getName() : "개발자";;

    // 생성일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;

    // 수정자
    private String lastModifiedBy = PredicateUtils.isNotNull(SodarMemberSession.get()) ? SodarMemberSession.get().getName() : "개발자";;

    // 수정일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date lastModifiedDate;

    /**
     * insert 전 default 값 셋팅
     */
    @PrePersist
    public void prePersist() {
        this.createdDate = PredicateUtils.isNull(this.createdDate) ? DateUtils.returnNowDate() : this.createdDate;
    }

    public static ArEventButtonEntity of(int arEventId, EventButtonDto dto) {
        ArEventButtonEntity entity = ModelMapperUtils.getModelMapper().map(dto, ArEventButtonEntity.class);
        entity.setArEventId(arEventId);
        return entity;
    }

    public static ArEventButtonEntity updateOf(ArEventButtonEntity arEventButtonEntity, EventButtonDto dto) {
        ArEventButtonEntity entity = ModelMapperUtils.getModelMapper().map(dto, ArEventButtonEntity.class);
        entity.setArEventButtonId(arEventButtonEntity.getArEventButtonId());
        entity.setArEventId(arEventButtonEntity.getArEventId());
        entity.setCreatedDate(arEventButtonEntity.getCreatedDate());
        entity.setLastModifiedDate(DateUtils.returnNowDate());
        return entity;
    }

    public static ArEventButtonEntity ofStamp(int stpId, EventButtonDto dto) {
        ArEventButtonEntity entity = ModelMapperUtils.getModelMapper().map(dto, ArEventButtonEntity.class);
        entity.setStpId(stpId);
        return entity;
    }

    public static ArEventButtonEntity ofStampUpdate(ArEventButtonEntity arEventButtonEntity, EventButtonDto dto) {
        ArEventButtonEntity entity = ModelMapperUtils.getModelMapper().map(dto, ArEventButtonEntity.class);
        entity.setArEventButtonId(arEventButtonEntity.getArEventButtonId());
        entity.setStpId(arEventButtonEntity.getStpId());
        entity.setLastModifiedDate(DateUtils.returnNowDate());
        return entity;
    }

}
