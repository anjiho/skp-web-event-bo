package kr.co.syrup.adreport.web.event.dto.response;

import kr.co.syrup.adreport.web.event.entity.ArEventWinningEntity;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WinningResultResDto implements Serializable {

    private static final long serialVersionUID = -2969168842045189925L;
    //이벤트 아이디
    private String eventId;

    //이벤트 종료 여부 ( contractStatus 상태 값 체크)
    private String finishYn;

    //당첨로그정보 테이블 인덱스
    private Long eventLogWinningId;

    private WinningResDto winningInfo;

    private List<WinningButtonResDto> winningButtonInfo;

    private ArEventWinningEntity winningEntity;

    private Long stpGiveAwayId;

    private Boolean isAutoWinning;

}
