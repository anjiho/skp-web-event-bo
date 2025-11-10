package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class EventBaseDto implements Serializable {

    private static final long serialVersionUID = -5323390662975060712L;

    // 이벤트 타이틀
    @NotEmpty(message = "이벤트 제목이 없습니다.")
    private String eventTitle;

    // 계약 인덱스 값
    private String marketingId;

    // 계약상태 값
    private String contractStatus;

    // 이벤트 종류 타입(AR, ROULETTE, SURVEY, STAMP)
    private String eventType;

    // 서비스 시작일
    private String eventStartDate;

    // 서비스 종료일
    private String eventEndDate;

    // 실제 서비스 종료일
    private String realEventEndDate;

    private String qrCodeUrl;

    private String stpConnectYn;

}
