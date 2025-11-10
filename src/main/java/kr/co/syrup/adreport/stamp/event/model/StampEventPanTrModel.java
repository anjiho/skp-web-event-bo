package kr.co.syrup.adreport.stamp.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.session.SodarMemberSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

// 스탬프판 상세 설정
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StampEventPanTrModel implements Serializable {

    private static final long serialVersionUID = -6756826558160626306L;

    // 스탬프판 상세 인덱스
    private Long stpPanTrId;

    // 스탬프판 인덱스
    private Integer stpPanId;

    // 스탬프 TR 순서
    private Integer stpTrSort;

    // 스탬프 TR 명
    private String stpTrTxt;

    // 스탬프 TR 유형(이벤트, 위치)
    private String stpTrType;

    // TR 이벤트 ID
    private String stpTrEventId;

    // TR 위치 PID
    private String stpTrPid;

    // 위치 참여시 문구
    private String stpTrLocationMsgAttend;

    // 위치 미참여시 문구
    private String stpTrLocationMisMsgAttend;

    // 스탬프 미적립 이미지 URL
    private String stpTrNotAccImgUrl;

    // 스탬프 적립 완료 이미지 URL
    private String stpTrAccImgUrl;

    // 스탬프 당첨 시도 이미지 URL
    private String stpTrWinningAttendImgUrl;

    private String createdBy = PredicateUtils.isNotNull(SodarMemberSession.get()) ? SodarMemberSession.get().getName() : "개발자";

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;

    private String lastModifiedBy = PredicateUtils.isNotNull(SodarMemberSession.get()) ? SodarMemberSession.get().getName() : "개발자";

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date lastModifiedDate;

}
