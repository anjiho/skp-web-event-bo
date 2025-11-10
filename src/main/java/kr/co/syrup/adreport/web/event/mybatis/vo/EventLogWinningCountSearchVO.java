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
public class EventLogWinningCountSearchVO implements Serializable {

    private static final long serialVersionUID = -1493340293008366132L;

    // 이벤트 아이디
    private String eventId = "";

    // AR 이벤트 아이디
    private Integer arEventId = 0;

    private Integer arEventObjectId = 0;

    // 이벤트 당첨 정보 아이디
    private Integer arEventWinningId = 0;

    // 당첨자 정보 설정 넘버
    private Integer eventWinningSort = 0;

    // 당첨 타입  값(기프티콘, 기타, 꽝)
    private String winningType = "";

    // 참여코드
    private String attendCode = "";

    // 생성일자(yyyy-mm-dd)
    private String createdDay = "";

    // 생성 시(hh)
    private String createdHour = "";

    // 생성일(yyyy-mm-dd hh:mm:ss)
    private Date createdDate = null;
}
