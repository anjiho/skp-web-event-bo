package kr.co.syrup.adreport.web.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.framework.utils.DateUtils;
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
@Table(name = "AR_EVENT_ATTEND_TIME")
public class ArEventAttendTimeEntity implements Serializable {

    private static final long serialVersionUID = 3965652421517951911L;
    // 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer arEventAttendTimeId;

    // AR 이벤트 아이디
    private Integer arEventId;

    // 참여시간 설정(시작)
    private Integer attendStartHour;

    // 참여시간 설정(종료)
    private Integer attendEndHour;

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
        this.createdDate = this.createdDate == null ? DateUtils.returnNowDate() : this.createdDate;
    }
}
