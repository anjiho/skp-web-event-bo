package kr.co.syrup.adreport.web.event.mybatis.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventLogExposureCountSearchVO implements Serializable {

    private static final long serialVersionUID = 936760517394001399L;

    // 이벤트 아이디
    private String eventId = "";

    // 이벤트 아이디
    private Integer arEventId = 0;

    // AR_EVENT_OBJECT.id
    private Integer arEventObjectId = 0;

    //오브젝트 순서
    private Integer objectSort = 0;

    // 생성일자(yyyy-mm-dd)
    private String createdDay = "";

    // 생성 시(hh)
    private String createdHour = "";

    // 참여자 코드
    private String attendCode = "";

    // 생성일(yyyy-mm-dd hh:mm:ss)
    private Date createdDate = null;
}
