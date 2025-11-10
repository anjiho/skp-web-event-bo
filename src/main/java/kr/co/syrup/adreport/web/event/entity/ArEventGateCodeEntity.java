package kr.co.syrup.adreport.web.event.entity;

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
@Table(name = "AR_EVENT_GATE_CODE")
public class ArEventGateCodeEntity implements Serializable {

    private static final long serialVersionUID = -7340742341545920450L;

    // 이벤트 아이디
    private String eventId;

    // 참여번호 값
    @Id
    private String attendCode;

    // 사용여부
    private Boolean useYn;

    // 사용된 개수
    private int usedCount;

    // 생성자
    private String createdBy;

    // 생성일
    private Date createdDate;

    // 수정자
    private String lastModifiedBy;

    // 수정일
    private Date lastModifiedDate;

}
