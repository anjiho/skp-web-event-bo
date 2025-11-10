package kr.co.syrup.adreport.web.event.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.web.event.entity.ArEventEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserWinningInfoResDto implements Serializable {

    private static final long serialVersionUID = 2326636364176005123L;

    private Integer giveAwayId;

    // 이벤트 아이디
    private String eventId;

    //당첨테이블 인덱스
    private Integer arEventWinningId;

    // 당첨 타입  값(기프티콘, 기타, 꽝)
    private String winningType;

    // 상품명
    private String productName;

    // 경품 비밀번호
    private String giveAwayPassword;

    // 경품 수령 여부
    private Boolean isReceive;

    // 경품 수령 여부 한글 명
    private String receiveStatusStr;

    // 당첨 한글 명
    private String winningStr;

    private String subscriptionYn;

    private Boolean isSubscriptionWinningPresentation;

    // 생성일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy.MM.dd", timezone="Asia/Seoul")
    private Date createdDate;

    private ArEventEntity arEventInfo;

    private Long stpGiveAwayId;

    private String winningDate;
}
