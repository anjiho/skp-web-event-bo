package kr.co.syrup.adreport.web.event.mybatis.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ArEventJoinEventBaseVO {

    // 이벤트 타이틀
    private String eventTitle;

    // 계약 인덱스 값
    private String marketingId;

    // 계약상태 값
    private String contractStatus;

    // 이벤트 종류 타입(AR, ROULETTE)
    private String eventType;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="Asia/Seoul")
    private Date eventStartDate;

    // 서비스 종료일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="Asia/Seoul")
    private Date eventEndDate;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="Asia/Seoul")
    private Date realEventEndDate;

    // QR코드 이미지 URL
    private String qrCodeUrl;

    private Integer arEventId;

    // 이벤트 기본 테이블 아이디
    private String eventId;

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

    //전화번호 참여제한 여부
    private Boolean attendConditionMdnYn;

    //목표달성수 참여제한여부
    private Boolean attendConditionTargetYn;
}
