package kr.co.syrup.adreport.stamp.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

//스탬프 PV 로그
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StampEventLogPvModel implements Serializable {

    private static final long serialVersionUID = 6696891227795807237L;

    // 이벤트 아이디
    private String eventId;

    // 스탬프 이벤트 메인 아이디
    private Integer stpId;

    private Long stpPanTrId;

    // page_id
    private String pageId;

    // action_id
    private String actionId;

    // order
    private String pvOrder;

    // type
    private String pvType;

    // code
    private String pvCode;

    // code
    private String browserVersion;

    // code
    private String br;

    // 생성일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;
}
