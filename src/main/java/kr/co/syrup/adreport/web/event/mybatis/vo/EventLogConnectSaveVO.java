package kr.co.syrup.adreport.web.event.mybatis.vo;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.web.event.entity.EventLogConnectEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EventLogConnectSaveVO implements Serializable {

    private static final long serialVersionUID = -2528716387512124783L;

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

    public static EventLogConnectSaveVO saveOf(String eventId, int arEventId, String attendCode, String trackingCode) {
        EventLogConnectSaveVO connectEntity = new EventLogConnectSaveVO().builder()
                .eventId(eventId)
                .arEventId(arEventId)
                .isAttend(StringUtils.isEmpty(attendCode) ?  false : true)
                .attendCode(attendCode)
                .trackingCode(StringUtils.isEmpty(trackingCode) ? "" : trackingCode)
                .createdDay(DateUtils.getNow("yyyy-MM-dd"))
                .createdHour(DateUtils.getNowHour())
                .build();
        return connectEntity;
    }
}
