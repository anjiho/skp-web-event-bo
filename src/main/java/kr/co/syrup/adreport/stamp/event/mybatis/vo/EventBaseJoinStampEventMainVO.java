package kr.co.syrup.adreport.stamp.event.mybatis.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class EventBaseJoinStampEventMainVO implements Serializable {

    private static final long serialVersionUID = -9075879766127240376L;

    // 이벤트 타이틀
    private String eventTitle;

    // 계약 인덱스 값
    private String marketingId;

    // 계약상태 값
    private String contractStatus;

    // 이벤트 종류 타입(AR, ROULETTE)
    private String eventType;

    // 서비스 시작일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="Asia/Seoul")
    private Date eventStartDate;

    // 서비스 종료일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="Asia/Seoul")
    private Date eventEndDate;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="Asia/Seoul")
    private Date realEventEndDate;

    private String stpConnectYn;

    // 인덱스
    private Integer stpId;

    //이벤트 아이디(WEB_EVENT_BASE.event_id)
    private String eventId;

    // 스탬프 이벤트 명
    private String stpEventTitle;

    // 메인 설정 여부 (설정함, 설정안함)
    private String stpMainSettingYn;

    // 참여 인증 조건(전화번호, 참여번호)
    private String stpAttendAuthCondition;

    // 참여번호 등록 종류(자동생성, 파일등록)
    private String stpAttendCodeRegType;

    // 참여번호 자릿수
    private Integer stpAttendCodeDigit;

    // 참여번호 생성수
    private Integer stpAttendCodeCount;

    // 파일 업로드 시 엑셀 파일명
    private String stpAttendCodeFileName;

    // 참여번호 비매칭시 문구
    private String stpAttendCodeMisTxt;

    // 알림톡 발송 여부(발송함, 발송안함)
    private String alimtokSendYn;

    // 당첨정보(공통)설정 > 중복당첨수 제한 타입
    private String duplicateWinningType;

    // 중복당첨 당첨제한 (전체 : 0 , 1일 : 1)
    private Integer duplicateWinningLimitType;

    // 중복 당첨 당첨제한 회수
    private Integer duplicateWinningCount;

    // 추첨형 스탬프 기점 번호 (1,2,3,~)
    private String winningRaffleStartPoint;

    // 당첨없음, 교환형, 추첨형
    private String stpWinningType;

    // 배경 이미지 URL
    private String stpBgImgUrl;
}
