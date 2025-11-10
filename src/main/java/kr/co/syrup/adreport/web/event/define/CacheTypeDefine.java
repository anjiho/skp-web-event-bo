package kr.co.syrup.adreport.web.event.define;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CacheTypeDefine {

    FIND_AR_EVENT_BY_ID("findArEventById", 60 * 60 * 24, 1),
    FIND_WEB_EVENT_BASE("findEventBase", 60 * 60 * 24, 1),
    FIND_AR_EVENT_BY_WINNING("findArEventByEventIdAtWinningProcess", 60 * 60 * 24, 1),
    FIND_AR_EVENT_BY_OBJ_EXPOSURE("findArEventByEventIdAtObjectExposure", 60 * 60 * 24, 1),
    FIND_WINNING("findArEventWinningListByArEventIdAndSubscriptionYn", 60 * 60 * 24, 1),
    FIND_FAIL_WINNING("findFailArEventWinningListByArEventIdAndSubscriptionYn", 60 * 60 * 24, 1),
    FIND_OBJECT_BY_IDX("findArEventObjectByIdAtWinningProcess", 60 * 60 * 24, 1),
    FIND_ALL_OBJ_EXPOSURE("findAllArEventObjectByArEventIdAtObjectExposure", 60 * 60 * 24, 1),
    FIND_AR_EVENT_LOGICAL_EXPOSURE("findArEventLogicalResDtoAtExposureObject", 60 * 60 * 24, 1),
    FIND_WINNING_BTN_WINNING_PROCESS("findArEventWinningButtonListByArEventWinningIdAtWinningProcess", 60 * 60 * 24, 1),
    FIND_AR_EVENT_WINNING_BY_STP_ID("findArEventWinningListByStpId", 60 * 60 * 24, 1),
    FIND_STP_MAIN_BY_ID("findStampEventMainByEventId", 60 * 60 * 24, 1),
    FIND_AR_EVENT_WINNING_BY_ID("findByArEventWinningById", 60 * 60 * 24, 1),
    FIND_AR_EVENT_WINNING_BTN_BY_ID("findArEventWinningButtonById", 60 * 60 * 24, 1),
    FIND_MAPPING_WINNING_BY_STP_PAN_TR_ID("findMappingArEventWinningByStpPanTrId", 60 * 60 * 24, 1),
    FIND_STAMP_EVENT_PAN_BY_STP_ID("findStampEventPanByStpId", 60 * 60 * 24, 1),
    FIND_STAMP_EVENT_PAN_TR_LIST_BY_STP_PAN_ID("findStampEventPanTrListByStpPanId", 60 * 60 * 24, 1),
    FIND_STAMP_EVENT_PAN_TR_LIST_BY_STP_ID("findStampEventPanTrListByStpId", 60 * 60 * 24, 1),
    FIND_ALL_ATTEND_TIME_BY_AR_EVENT_ID("findAllArEventAttendTimeByArEventIdProjection", 60 * 60 * 24, 1),
    FIND_ALL_EVENT_HTML_BY_AR_EVENT_ID("findAllArEventHtmlByArEventId", 60 * 60 * 24, 1),
    FIND_AR_EVENT_GATE_PAGE_INFO("findArEventGagePageInfo", 60 * 60 * 24, 1),
    FIND_STAMP_TR_SORT_ATTEND_SORT_YN_BY_STP_TR_EVENT_ID("findStampTrSortAndAttendSortYnByStpTrEventId", 60 * 60 * 24, 1),
    FIND_STAMP_EVENT_MAIN_BY_ID("findStampEventMainById", 60 * 60 * 24, 1),
    FIND_FIRST_EVENT_ID_FROM_STAMP_TR_BY_STP_PAN_ID("findFirstEventIdFromStampTrByStpPanId",60 * 60 * 24, 1),
    FIND_STP_TR_EVENT_ID_AT_NEXT_ATTEND("findStpTrEventIdAtNextAttend",60 * 60 * 24, 1),
    FIND_AR_EVENT_WINNING_LIST_BY_STP_ID("findArEventWinningListByStpId", 60 * 60 * 24, 1),
    FIND_AR_EVENT_HTML_LIST_BY_STP_PAN_ID("findArEventHtmlListByStpPanId", 60 * 60 * 24, 1),
    FIND_AR_EVENT_HTML_LIST_BY_STP_ID("findArEventHtmlListByStpId", 60 * 60 * 24, 1),
    FIND_STAMP_PAN_TR_SORT_BY_STAMP_PAN_TR_ID("findStampPanTrSortByStampPanTrId", 60 * 60 * 24, 1),
    FIND_STAMP_PAN_TR_BY_ID("findStampEventPanTrById", 60 * 60 * 24, 1),
    FIND_AR_EVENT_NFT_COUPON_INFO_BY_ID("findArEventNftCouponInfoEntityById", 60 * 60 * 24, 1),
    FIND_AR_EVENT_NFT_BENEFIT_BY_AR_EVENT_WINNING_ID("findAllArEventNftBenefitByArEventWinningId", 60 * 60 * 24, 1),
    FIND_WINNING_BUTTON_ADD_BY_AR_EVENT_WINNING_BUTTON_ID("findAllArEventWinningButtonAddByArEventWinningButtonId", 60 * 60 * 24, 1),
    FINE_NEXT_STAMP_EVENT_PAN_TR_BY_STP_PAN_ID_AND_SORT("findNextStampEventPanTrByStpPanIdAndSort", 60 * 60 * 24, 1),
    FIND_STAMP_EVENT_MAIN_BY_EVENT_ID_FROM_WINNING("findStampEventMainByEventIdFromWinning", 60 * 60 * 24, 1),
    FIND_AR_EVENT_HTML_LIST_BY_STP_ID_ORDER_BY_HTML_TYPE_SORT("findByStpIdAndStpPanIdIsNullOrderByHtmlTypeSort", 60 * 60 * 24, 1),
    FIND_AR_EVENT_HTML_LIST_BY_STP_PAN_ID_ORDER_BY_HTML_TYPE_SORT("findArEventHtmlListByStpPanIdOrderByHtmlTypeSort", 60 * 60 * 24, 1),
    FIND_AR_EVENT_BASE_INFO_BY_AR_EVENT_ID("findArEventBaseInfoByEventId", 60 * 60 * 24, 1),
    FIND_EVENT_BASE_JOIN_STAMP_EVENT_MAIN("findEventBaseJoinStampEventMain", 60 * 60 * 24, 1),
    FIND_ALL_AR_EVENT_BANNER_BY_AR_EVENT_ID("findAllArEventBannerByArEventId", 60 * 60 * 24, 1),
    FIND_ALL_AR_EVENT_BANNER_BY_STP_ID("findAllArEventBannerByStpId", 60 * 60 * 24, 1),
    FIND_AR_EVENT_WINNING_LIST_BY_EVENT_IDX_AND_MAPPING_NUMBER("findArEventWinningListByEventIdxAndMappingNumber", 60 * 60 * 24, 1),
    FIND_WINNING_BUTTON_RESDTO_BY_AR_EVENT_WINNING_ID_AT_WINNING_PROCESS("findWinningButtonResDtoByArEventWinningIdAtWinningProcess", 60 * 60 * 24, 1),
    FIND_AR_EVENT_BY_EVENT_ID_AT_CACHE("findArEventByEventIdAtCache", 60 * 60 * 24, 1),
    FIND_ALL_SURVEY_SUBJECT_BY_AR_EVENT_ID_AT_CACHE("findAllSurveySubjectByArEventIdAtCache", 60 * 60 * 24, 1),
    FIND_ALL_SURVEY_EXAMPLE_BY_SURVEY_SUBJECT_ID_AT_CACHE("findAllSurveyExampleBySurveySubjectIdAtCache", 60 * 60 * 24, 1),
    FIND_ALL_SURVEY_EXAMPLE_QUESTION_BY_SURVEY_SUBJECT_ID_AT_CACHE("findAllSurveyExampleQuestionBySurveySubjectIdAtCache", 60 * 60 * 24, 1),
    FIND_SURVEY_SUBJECT_POPUP_IMAGE_BY_SURVEY_SUBJECT_ID("findSurveySubjectPopupImageBySurveySubjectId", 60 * 60 * 24, 1),



    ;

    private final String cacheName;     // 캐시 이름
    private final int expireAfterWrite; // 만료시간
    private final int maximumSize;      // 최대 갯수

}
