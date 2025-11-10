package kr.co.syrup.adreport.web.event.entity;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import lombok.*;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "EVENT_LOG_CONNECT")
public class EventLogConnectEntity implements Serializable {

    private static final long serialVersionUID = 7167480618267332824L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 인덱스
    private Long id;

    // 이벤트 아이디
    private String eventId;

    // 이벤트 아이디
    private Integer arEventId;

    // 참여번호 참여 여부
    private Boolean isAttend;

    // 참여자 코드
    private String attendCode;

    //트래킹 코드
    private String trackingCode;

    // 생성일자(yyyy-mm-dd)
    private String createdDay;

    // 생성 시(hh)
    private String createdHour;

    // 생성일(yyyy-mm-dd hh:mm:ss)
    private Date createdDate;

    public static EventLogConnectEntity saveOf(String eventId, int arEventId, String attendCode, String trackingCode) {
        EventLogConnectEntity connectEntity = new EventLogConnectEntity().builder()
                .eventId(eventId)
                .arEventId(arEventId)
                .isAttend(StringUtils.isEmpty(attendCode) ?  false : true)
                .attendCode(attendCode)
                .trackingCode(StringUtils.isEmpty(trackingCode) ? "" : trackingCode)
                .createdDay(DateUtils.getNow("yyyy-MM-dd"))
                .createdHour(DateUtils.getNowHour())
                .createdDate(DateUtils.returnNowDate())
                .build();
        return connectEntity;
    }

}
