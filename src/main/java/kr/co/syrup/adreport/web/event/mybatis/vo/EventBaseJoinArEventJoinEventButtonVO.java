package kr.co.syrup.adreport.web.event.mybatis.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventBaseJoinArEventJoinEventButtonVO implements Serializable {

    private static final long serialVersionUID = 8692057732179242946L;

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

    private Integer arEventId;

    // AR 구동 정보(기본형 ~ 이미지스캐닝형)
    private String eventLogicalType;

    // 페이지 접속 팝업(위치설정조건)
    private Boolean locationSettingYn;

    // AR 참여조건(전체)
    private Boolean arAttendConditionAllYn;

    // AR 참여조건(특정위치)
    private Boolean arAttendConditionSpecialLocationYn;

    // AR 참여조건(시간별)
    private Boolean arAttendConditionHourlyYn;

    // AR 참여조건(참여번호)
    private Boolean arAttendConditionCodeYn;

    // 기간참여조건 타입(제한없음, 기간제한)
    private String arAttendTermType;

    // 기간참여조건 종류(1일, 이벤트기간내)
    private String arAttendTermLimitType;

    // 기간참여조건 회수
    private Integer arAttendTermLimitCount;

    // pid
    private String pid;

    // 위치메세지 등록(위치 참여시)
    private String locationMessageAttend;

    // 위치메세지 등록(위치 미 참여시)
    private String locationMessageNotAttend;

    // 시간참여 불가시 메세지
    private String attendHourMisMessage;

    // 참여번호 미 매칭시
    private String attendCodeMisMatchMessage;

    // AR BG 이미지
    private String arBgImage;

    // AR 스킨 이미지
    private String arSkinImage;

    // 당첨정보(공통)설정 > 중복당첨수 제한 타입
    private String duplicateWinningType;

    // 중복당첨 당첨제한 (전체 : 0 , 1일 : 1)
    private Integer duplicateWinningLimitType;

    // 중복 당첨 당첨제한 회수
    private Integer duplicateWinningCount;

    // 당첨 비밀번호 사용 여부(Y, N)
    private String winningPasswordYn;

    // 참여번호 등록 종류
    private String attendCodeRegType;

    // 코드 생성수
    private Integer attendCodeCount;

    // 코드 자릿수
    private Integer attendCodeDigit;

    //정보 제공동의 문구설정(N:설정안함, Y:설정)
    private String informationProvisionAgreementTextSetting;

    //정보 제공동의 문구 - 제공받는 자
    private String informationProvisionRecipient;

    //정보 제공동의 문구 - 위탁업체
    private String informationProvisionConsignor;

    //정보 제공동의 문구 - 이용목적
    private String informationProvisionPurposeUse;

    private Integer arEventButtonId;

    // 버튼 배경색 지정 여부 값(AR_EVENT_CATEGORY)
    private String arButtonBgColorAssignType;

    // 버튼 배경색 지정일떄 RGB, HEX 여부
    private String arButtonBgColorInputType;

    // 버튼 배경색 rgb 값
    private Integer arButtonBgColorRed;

    // 버튼 배경색 rgb 값
    private Integer arButtonBgColorGreen;

    // 버튼 배경색 rgb 값
    private Integer arButtonBgColorBlue;

    // 버튼 배경색 hex 값
    private String arButtonBgColorHex;

    // 버튼색 지정 여부 값
    private String arButtonColorAssignType;

    // 버튼색 지정일떄 RGB, HEX 여부
    private String arButtonColorInputType;

    // 버튼색 rgb 값
    private Integer arButtonColorRed;

    // 버튼색 rgb 값
    private Integer arButtonColorGreen;

    // 버튼색 rgb 값
    private Integer arButtonColorBlue;

    // 버튼색 hex
    private String arButtonColorHex;

    // 버튼 text 색 지정 여부 값
    private String arButtonTextColorAssignType;

    // 버튼 text 색 지정일떄 RGB, HEX 여부
    private String arButtonTextColorInputType;

    // 버튼 text 색 rgb값
    private Integer arButtonTextColorRed;

    // 버튼 text 색 rgb값
    private Integer arButtonTextColorGreen;

    // 버튼 text 색 rgb값
    private Integer arButtonTextColorBlue;

    // 버튼 text 색 hext값
    private String arButtonTextColorHex;

    // 버튼 text 문구 지정
    private String arButtonText;

    // 서베이고 추가건 전화번호 참여제한여부 (true, false)
    private Boolean attendConditionMdnYn;

    // 서베이고 추가건 전화번호 참여 불가 메세지
    private String attendMdnMisMessage;

    // 서베이고 추가건 목표달성수 참여제한여부 (YN)
    private Boolean attendConditionTargetYn;

    // 서베이고 추가건 목표달성수 참여불가시 메세지
    private String attendTargetMisMessage;

    // 서베이고 추가건 문항 이동 유형 설정여부 (이동없음 : 0, 이동지정 : 1)
    private String subjectTargetMoveYn;

    // 서베이고 추가건 아이콘 이미지 URL - 대화형 한정
    private String talkIconImgUrl;

    // 서베이고 추가건 설문 완료 문구 - 대화형 한정
    private String talkSurveyEndText;

    // 서베이고 추가건 설문 완료 버튼 종류 - 기본형, 대화형 ( 경품추천, 설문완료)
    private String surveyEndButtonType;

    // 서베이고 추가건 설문 완료 버튼 문구
    private String surveyEndButtonText;

    // 서베이고 추가건 문항 다시 풀기 설정 YN - 퀴즈형 한정
    private String quizRetryYn;

    // 서베이고 추가건 당첨 조회 기준 설정 - 전화번호, 참여번호( 전화번호 : MDN, 참여번호 : ATTEND )
    private String winningSearchType;

    // 서베이고 추가건 당첨 조회 기준 전화번호 설정 시 SMS 인증여부 사용 YN
    private String smsAuthUseYn;

    // 포토 추가건 이벤트 노출 유형 선택 (오픈브라우저 : OPEN, OCB Web : OCB)
    private String eventExposureType;

    // 포토 추가건 포인트 적립정보 여부 (적립없음 : NONE / 적립 전 참여 : PREV)
    private String ocbPointSaveType;

    //포토 추가건 로딩 이미지 사용여부
    private String loadingImgYn;

    //포토 추가건 로딩 이미지 url
    private String loadingImgUrl;

    private String landingUrl;

    private Long stpPanTrId;
}
