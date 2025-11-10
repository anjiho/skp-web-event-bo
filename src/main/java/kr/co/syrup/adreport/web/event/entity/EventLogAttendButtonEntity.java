package kr.co.syrup.adreport.web.event.entity;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
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
@Table(name = "EVENT_LOG_ATTEND_BUTTON")
public class EventLogAttendButtonEntity implements Serializable {

    private static final long serialVersionUID = -8739795864001939353L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 인덱스
    private Long id;

    // 이벤트 아이디
    private String eventId;

    // AR 이벤트 아이디
    private Integer arEventId;

    // 참여자 코드
    private String attendCode;

    //핸드폰 번호
    private String phoneNumber;

    //버튼 성공여부
    private String successYn;

    // 생성일자(yyyy-mm-dd)
    private String createdDay;

    // 생성 시(hh)
    private String createdHour;

    // 생성일(yyyy-mm-dd hh:mm:ss)
    private Date createdDate;

    public static EventLogAttendButtonEntity saveOf(String eventId, int arEventId, String attendCode, String successYn) {
        return new EventLogAttendButtonEntity().builder()
                .eventId(eventId)
                .arEventId(arEventId)
                .attendCode(attendCode)
                .successYn(successYn)
                .createdDay(DateUtils.getNow("yyyy-MM-dd"))
                .createdHour(DateUtils.getNowHour())
                .createdDate(DateUtils.returnNowDate())
                .build();

    }

    public static EventLogAttendButtonEntity saveOfPhoneNumber(String eventId, int arEventId, String phoneNumber, String successYn) {
        return new EventLogAttendButtonEntity().builder()
                .eventId(eventId)
                .arEventId(arEventId)
                .phoneNumber(phoneNumber)
                .successYn(successYn)
                .createdDay(DateUtils.getNow("yyyy-MM-dd"))
                .createdHour(DateUtils.getNowHour())
                .createdDate(DateUtils.returnNowDate())
                .build();

    }
}
