package kr.co.syrup.adreport.web.event.mybatis.vo;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.web.event.entity.EventLogAttendButtonEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EventLogAttendSaveVO implements Serializable {

    private static final long serialVersionUID = 2807616946667554923L;

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

    public static EventLogAttendSaveVO saveOf(String eventId, int arEventId, String attendCode, String successYn) {
        return new EventLogAttendSaveVO().builder()
                .eventId(eventId)
                .arEventId(arEventId)
                .attendCode(attendCode)
                .successYn(successYn)
                .createdDay(DateUtils.getNow("yyyy-MM-dd"))
                .createdHour(DateUtils.getNowHour())
                .build();

    }

    public static EventLogAttendSaveVO saveOfPhoneNumber(String eventId, int arEventId, String phoneNumber, String successYn) {
        return new EventLogAttendSaveVO().builder()
                .eventId(eventId)
                .arEventId(arEventId)
                .phoneNumber(phoneNumber)
                .successYn(successYn)
                .createdDay(DateUtils.getNow("yyyy-MM-dd"))
                .createdHour(DateUtils.getNowHour())
                .build();

    }
}
