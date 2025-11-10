package kr.co.syrup.adreport.web.event.entity;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "COMMON_SETTINGS")
public class CommonSettingsEntity implements Serializable {

    private static final long serialVersionUID = -1850267604236794628L;
    
    // 포토 추가건
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "common_settings_id", nullable = false)
    private Integer id;

    // 포토 추가건
    private String settingKey;

    // 포토 추가건
    private String value;

    // 포토 추가건 공통 설정 설명
    private String commonSettingsDesc;

    // 포토 추가건
    private Date createdDate;

    // 포토 추가건
    private Date lastModifiedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = PredicateUtils.isNull(this.createdDate) ? DateUtils.returnNowDate() : this.createdDate;
    }

    public static CommonSettingsEntity ofUpdate(CommonSettingsEntity findEntity, String value) {
        CommonSettingsEntity newEntity = new CommonSettingsEntity();
        newEntity.setId(findEntity.getId());
        newEntity.setSettingKey(findEntity.getSettingKey());
        newEntity.setValue(value);
        newEntity.setCommonSettingsDesc(findEntity.getCommonSettingsDesc());
        newEntity.setCreatedDate(findEntity.getCreatedDate());
        newEntity.setLastModifiedDate(DateUtils.returnNowDate());
        return newEntity;
    }
}
