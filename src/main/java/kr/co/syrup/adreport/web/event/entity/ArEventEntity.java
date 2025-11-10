package kr.co.syrup.adreport.web.event.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.ModelMapperUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import kr.co.syrup.adreport.web.event.dto.request.EventDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@DynamicInsert
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "AR_EVENT")
public class ArEventEntity implements Serializable {

    private static final long serialVersionUID = -3746151205948404693L;

    // 이벤트 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ar_event_id")
    private Integer arEventId;

    @Column(name = "event_id")
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
    @Column(name = "information_provision_agreement_text_setting")
    private String informationProvisionAgreementTextSetting;

    //정보 제공동의 문구 - 제공받는 자
    @Column(name = "information_provision_recipient")
    private String informationProvisionRecipient;

    //정보 제공동의 문구 - 위탁업체
    @Column(name = "information_provision_consignor")
    private String informationProvisionConsignor;

    //정보 제공동의 문구 - 이용목적
    @Column(name = "information_provision_purpose_use")
    private String informationProvisionPurposeUse;

    // 서베이고 추가건 전화번호 참여제한여부 (YN)
    @Column(name = "attend_condition_mdn_yn")
    private Boolean attendConditionMdnYn;

    // 서베이고 추가건 전화번호 참여 불가 메세지
    @Column(name = "attend_mdn_mis_message")
    private String attendMdnMisMessage;

    // 서베이고 추가건 목표달성수 참여제한여부 (YN)
    @Column(name = "attend_condition_target_yn")
    private Boolean attendConditionTargetYn;

    // 서베이고 추가건 목표달성수 참여불가시 메세지
    @Column(name = "attend_target_mis_message")
    private String attendTargetMisMessage;

    // 서베이고 추가건 문항 이동 유형 설정여부 (이동없음 : 0, 이동지정 : 1)
    @Column(name = "subject_target_move_yn")
    private String subjectTargetMoveYn;

    // 서베이고 추가건 아이콘 이미지 URL - 대화형 한정
    @Column(name = "talk_icon_img_url")
    private String talkIconImgUrl;

    // 서베이고 추가건 설문 완료 문구 - 대화형 한정
    @Column(name = "talk_survey_end_text")
    private String talkSurveyEndText;

    // 서베이고 추가건 설문 완료 버튼 종류 - 기본형, 대화형 ( 경품추천, 설문완료)
    @Column(name = "survey_end_button_type")
    private String surveyEndButtonType;

    // 서베이고 추가건 설문 완료 버튼 문구
    @Column(name = "survey_end_button_text")
    private String surveyEndButtonText;

    // 서베이고 추가건 문항 다시 풀기 설정 YN - 퀴즈형 한정
    @Column(name = "quiz_retry_yn")
    private String quizRetryYn;

    // 서베이고 추가건 당첨 조회 기준 설정 - 전화번호, 참여번호( 전화번호 : MDN, 참여번호 : ATTEND )
    @Column(name = "winning_search_type")
    private String winningSearchType;

    // 서베이고 추가건 당첨 조회 기준 전화번호 설정 시 SMS 인증여부 사용 YN
    @Column(name = "sms_auth_use_yn")
    private String smsAuthUseYn;

    // 포토 추가건 이벤트 노출 유형 선택 (오픈브라우저 : OPEN, OCB Web : OCB)
    @Column(name = "event_exposure_type")
    private String eventExposureType;

    // 포토 추가건 포인트 적립정보 여부 (적립없음 : NONE / 적립 전 참여 : PREV)
    @Column(name = "ocb_point_save_type")
    private String ocbPointSaveType;

    //포토 추가건 로딩 이미지 사용여부
    @Column(name = "loading_img_yn")
    private String loadingImgYn;

    //포토 추가건 로딩 이미지 url
    @Column(name = "loading_img_url")
    private String loadingImgUrl;

    // 생성자
    private String createdBy;

    // 생성일
    private Date createdDate;

    // 수정자
    private String lastModifiedBy;

    // 수정일
    private Date lastModifiedDate;

    /**
     * insert 전 default 값 셋팅
     */
    @PrePersist
    public void prePersist() {
        this.arAttendTermType = PredicateUtils.isNull(this.arAttendTermType) || StringUtils.isEmpty(this.arAttendTermType) ? StringDefine.N.name() : this.arAttendTermType;
        this.arAttendTermLimitType = PredicateUtils.isNull(this.arAttendTermLimitType) || StringUtils.isEmpty(this.arAttendTermLimitType) ? "0" : this.arAttendTermLimitType;
        this.duplicateWinningLimitType = PredicateUtils.isNull(this.duplicateWinningLimitType) ? 0 : this.duplicateWinningLimitType;
        this.createdDate = PredicateUtils.isNull(this.createdDate) ? DateUtils.returnNowDate() : this.createdDate;
    }

    public static ArEventEntity of(String eventId, EventDto dto) {
        ArEventEntity entity = ModelMapperUtils.getModelMapper().map(dto, ArEventEntity.class);
        entity.setEventId(eventId);
        return entity;
    }

    public static ArEventEntity updateOf(ArEventEntity arEventEntity, String eventId, EventDto dto) {
        ArEventEntity entity = ModelMapperUtils.getModelMapper().map(dto, ArEventEntity.class);
        entity.setArEventId(arEventEntity.getArEventId());
        entity.setEventId(eventId);
        entity.setAttendCodeRegType(PredicateUtils.isNull(dto.getAttendCodeRegType()) ? arEventEntity.getAttendCodeRegType() : null);
        entity.setAttendCodeCount(PredicateUtils.isNull(dto.getAttendCodeCount()) ? arEventEntity.getAttendCodeCount() : null);
        entity.setAttendCodeDigit(PredicateUtils.isNull(dto.getAttendCodeDigit()) ? arEventEntity.getAttendCodeDigit() : null);
        entity.setEventExposureType(PredicateUtils.isNull(dto.getEventExposureType()) ? arEventEntity.getEventExposureType() : dto.getEventExposureType());
        entity.setOcbPointSaveType(PredicateUtils.isNull(dto.getOcbPointSaveType()) ? arEventEntity.getOcbPointSaveType() : dto.getOcbPointSaveType());
        entity.setLoadingImgYn(PredicateUtils.isNull(dto.getLoadingImgYn()) ? arEventEntity.getLoadingImgYn() : dto.getLoadingImgYn());
        entity.setLoadingImgUrl(PredicateUtils.isNull(dto.getLoadingImgUrl()) ? arEventEntity.getLoadingImgUrl() : dto.getLoadingImgUrl());
        entity.setCreatedDate(arEventEntity.getCreatedDate());
        entity.setLastModifiedDate(DateUtils.returnNowDate());
        return entity;
    }

    public static ArEventEntity updateOf(ArEventEntity arEventEntity, int attendCodeCount) {
        ArEventEntity entity = ModelMapperUtils.getModelMapper().map(arEventEntity, ArEventEntity.class);
        entity.setArEventId(arEventEntity.getArEventId());
        entity.setEventId(arEventEntity.getEventId());
        entity.setAttendCodeCount(attendCodeCount);
        entity.setCreatedDate(arEventEntity.getCreatedDate());
        entity.setLastModifiedDate(DateUtils.returnNowDate());
        return entity;
    }

    public static ArEventEntity ofTest(String eventId, String eventLogicalType) {
        ArEventEntity entity = new ArEventEntity();
        entity.setEventId(eventId);
        entity.setEventLogicalType(eventLogicalType);
        entity.setLocationSettingYn(false);
        entity.setArAttendConditionAllYn(false);
        entity.setArAttendConditionSpecialLocationYn(false);
        entity.setArAttendConditionHourlyYn(false);
        entity.setArAttendConditionCodeYn(false);
        entity.setArAttendTermType(null);
        entity.setArBgImage("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaUrLRq79d905baedd05a1f853494d342b893cc.jpg");
        entity.setArSkinImage("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaUrN0ze89460e565de2f6a521a6392dd1f7946.png");
        entity.setDuplicateWinningType(StringDefine.N.name());
        entity.setDuplicateWinningLimitType(0);
        entity.setDuplicateWinningCount(0);
        entity.setWinningPasswordYn(StringDefine.N.name());
        entity.setInformationProvisionAgreementTextSetting(StringDefine.N.name());
        entity.setAttendConditionMdnYn(false);
        entity.setAttendConditionTargetYn(false);
        entity.setWinningSearchType("MDN");
        entity.setSmsAuthUseYn(StringDefine.N.name());
        return entity;
    }
}
