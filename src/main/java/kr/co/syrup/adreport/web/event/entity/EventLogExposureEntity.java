package kr.co.syrup.adreport.web.event.entity;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import lombok.Builder;
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
@Table(name = "EVENT_LOG_EXPOSURE")
public class EventLogExposureEntity implements Serializable {

    private static final long serialVersionUID = 794131970715230445L;

    // 인덱스
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이벤트 아이디
    private String eventId;

    // 이벤트 아이디
    private Integer arEventId;

    // AR_EVENT_OBJECT.id
    private Integer arEventObjectId;

    //오브젝트 순서
    private Integer objectSort;

    // 생성일자(yyyy-mm-dd)
    private String createdDay;

    // 생성 시(hh)
    private String createdHour;

    // 참여자 코드
    private String attendCode;

    // 생성일(yyyy-mm-dd hh:mm:ss)
    private Date createdDate;

    private Integer createdDayImpr;

    private Integer createdHourImpr;

    public static EventLogExposureEntity saveOf(String eventId, int arEventId, int arEventObjectId, int objectSort, String attendCode) {
        EventLogExposureEntity exposureEntity = new EventLogExposureEntity();
        exposureEntity.eventId = eventId;
        exposureEntity.arEventId = arEventId;
        exposureEntity.arEventObjectId = arEventObjectId;
        exposureEntity.objectSort = objectSort;
        exposureEntity.createdDay = DateUtils.getNow("yyyy-MM-dd");
        exposureEntity.createdHour = DateUtils.getNowHour();
        exposureEntity.setAttendCode(attendCode);
        exposureEntity.setCreatedDate(DateUtils.returnNowDate());
        exposureEntity.setCreatedDayImpr(Integer.parseInt(DateUtils.getNowYYMMDD()));
        exposureEntity.setCreatedHourImpr(Integer.parseInt(DateUtils.getNowYYMMDDHH()));
        return exposureEntity;
    }
}
