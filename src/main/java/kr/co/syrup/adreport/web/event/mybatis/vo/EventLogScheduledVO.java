package kr.co.syrup.adreport.web.event.mybatis.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventLogScheduledVO implements Serializable {

    private static final long serialVersionUID = 7248752703886418483L;

    private String eventId;

    private Integer arEventId;

    private String scheduleType;

    private Date createdDate;

    public static EventLogScheduledVO saveOf(String eventId, int arEventId, String scheduleType) {
        return new EventLogScheduledVO().builder()
                .eventId(eventId)
                .arEventId(arEventId)
                .scheduleType(scheduleType)
                .build();
    }
}
