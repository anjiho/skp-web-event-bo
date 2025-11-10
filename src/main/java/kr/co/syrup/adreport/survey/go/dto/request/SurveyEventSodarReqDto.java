package kr.co.syrup.adreport.survey.go.dto.request;

import kr.co.syrup.adreport.web.event.dto.request.EventAttendTimeDto;
import kr.co.syrup.adreport.web.event.dto.request.EventRepositoryButtonReqDto;
import kr.co.syrup.adreport.web.event.dto.request.EventWinningTextReqDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SurveyEventSodarReqDto implements Serializable {

    private static final long serialVersionUID = -6132674494591214017L;

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

    // BG 이미지
    private String arBgImage;

    // 당첨정보(공통)설정 > 중복당첨수 제한 타입
    private String duplicateWinningType;

    // 중복당첨 당첨제한 (전체 : 0 , 1일 : 1)
    private Integer duplicateWinningLimitType;

    // 중복 당첨 당첨제한 회수
    private Integer duplicateWinningCount;

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

    // 서베이고 추가건 전화번호 참여제한여부 (YN)
    private Boolean attendConditionMdnYn;

    // 서베이고 추가건 전화번호 참여 불가 메세지
    private String attendMdnMisMessage;

    // 서베이고 추가건 목표달성수 참여제한여부 (YN)
    private Boolean attendConditionTargetYn;

    // 서베이고 추가건 목표달성수 참여불가시 메세지
    private String attendTargetMisMessage;

    // 서베이고 추가건 문항 이동 유형 설정여부 (이동없음 : N, 이동지정 : Y)
    private String subjectTargetMoveYn;

    // 서베이고 추가건 아이콘 이미지 URL - 대화형 한정
    private String talkIconImgUrl;

    // 서베이고 추가건 설문 완료 문구 - 대화형 한정
    private String talkSurveyEndText;

    // 서베이고 추가건 설문 완료 버튼 종류 - 기본형, 대화형 ( 경품추천, 설문완료 )
    private String surveyEndButtonType;

    // 서베이고 추가건 설문 완료 버튼 문구
    private String surveyEndButtonText;

    // 서베이고 추가건 문항 다시 풀기 설정 YN - 퀴즈형 한정
    private String quizRetryYn;

    //참여시간 정보
    private List<EventAttendTimeDto> arEventAttendTimeInfo;

    //성/연령조건 설정 정보
    private List<GenderAgeLimitSodarReqDto> genderAgeLimitInfo;

    //당첨정보 텍스트 정보
    private List<EventWinningTextReqDto> winningTextInfo;

    //보관함 버튼 정보
    private List<EventRepositoryButtonReqDto> repositoryButtonInfo;


}
