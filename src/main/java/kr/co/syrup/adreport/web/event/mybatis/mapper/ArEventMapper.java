package kr.co.syrup.adreport.web.event.mybatis.mapper;

import kr.co.syrup.adreport.web.event.dto.request.CacheJsonDataListResDto;
import kr.co.syrup.adreport.web.event.dto.response.*;
import kr.co.syrup.adreport.web.event.entity.*;
import kr.co.syrup.adreport.web.event.mybatis.vo.*;
import org.apache.ibatis.annotations.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Mapper
public interface ArEventMapper {

    long selectWebEventSequenceNextval(@Param("seqName") String seqName);

    @Select(" select web_event_sequence_nextval(#{sequence}) as web_event_seq from dual ")
    Long selectWebEventSequenceNextvalBySequence(@Param("sequence") String sequence);

    int selectCountEventGiveAwayDeliveryAndEventIdAndPhoneNumberAndCreatedDate(@Param("eventId") String eventId, @Param("phoneNumber") String phoneNumber, @Param("createdDate") String createdDate);

    int selectCountEventLogExposureByArEventObjectIdAndCreatedDay(@Param("arEventObjectId") int arEventObjectId, @Param("createdDate") String createdDate);

    int selectCountEventLogExposureByArEventIdAndObjectSortAndCreatedDay(@Param("arEventId") int arEventObjectId, @Param("objectSort") int objectSort, @Param("createdDate") String createdDate);

    int selectCountEventLogExposureByArEventIdAndObjectSortAndCreatedDayImpr(@Param("arEventId") int arEventObjectId, @Param("objectSort") int objectSort, @Param("createdDayImpr") int createdDayImpr);

    int selectCountEventLogExposureByArEventObjectIdAndAttendCodeAndCreatedDay(@Param("arEventObjectId") int arEventObjectId, @Param("attendCode") String attendCode, @Param("createdDate") String createdDate);

    int selectCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndCreatedDay(@Param("arEventId") int arEventId, @Param("objectSort") int objectSort, @Param("attendCode") String attendCode, @Param("createdDate") String createdDate);

    int selectCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndCreatedDayImpr(@Param("arEventId") int arEventId, @Param("objectSort") int objectSort, @Param("attendCode") String attendCode, @Param("createdDate") Integer createdDate);

    int selectCountEventLogAttendButtonByEventIdAndAndCreatedDayAndAttendCode(@Param("eventId") String eventId, @Param("attendCode") String attendCode, @Param("createdDate") String createdDate);

    int selectCountEventWinningLogByEventIdAndCreatedDateAndNotFail(@Param("eventId") String eventId, @Param("createdDate") String createdDate, @Param("arEventWinningId") int arEventWinningId);

    int selectCountEventWinningLogByArEventIdAndEventWinningSortNotFail(@Param("arEventId") int arEventId, @Param("eventWinningSort") int eventWinningSort);

    int selectCountEventWinningLogByArEventIdAndCreatedDateAndEventWinningSortNotFail(@Param("arEventId") int arEventId, @Param("createdDay") String createdDay, @Param("eventWinningSort") int eventWinningSort);

    int selectCountEventWinningLogByArEventIdAndCreatedDateAndEventWinningSortNotFail2(@Param("arEventId") int arEventId, @Param("createdDay") String createdDay, @Param("eventWinningSort") int eventWinningSort);

    int selectCountEventWinningLogByArEventIdAndAttendCodeAndCreatedDayNotFail(@Param("arEventId") int arEventId, @Param("attendCode") String attendCode, @Param("createdDay") String createdDay, @Param("arEventWinningId") int arEventWinningId);

    int selectCountEventWinningLogByArEventIdAndAttendCodeAndCreatedDayNotFail2(@Param("arEventId") int arEventId, @Param("attendCode") String attendCode, @Param("createdDay") String createdDay,
                                                                                @Param("arEventWinningId") int arEventWinningId, @Param("phoneNumber") String phoneNumber);

    int selectCountEventWinningLogByArEventIdAndEventWinningSortAndDayAndHourNotFail(@Param("arEventId") int arEventId, @Param("eventWinningSort") int eventWinningSort, @Param("createdDay") String createdDay, @Param("createdHour") String createdHour);

    int selectCountEventWinningLogByArEventIdAndEventWinningSortAndDayAndHourNotFail2(@Param("arEventId") int arEventId, @Param("eventWinningSort") int eventWinningSort, @Param("createdDay") String createdDay, @Param("createdHour") String createdHour);

    //int selectCountEventWinningLogByEventIdAndArEventWinningIdAndTodayAndNotFail(@Param("eventId") String eventId, @Param())

    LinkedList<String> selectArEventGateCodeList(@Param("eventId") String eventId);

    long countArEventGateCodeByEventId(@Param("eventId") String eventId);

    List<EventGiveAwayDeliveryEntity> selectEventGiveAwayListByServiceEndAfterSixtyDay(@Param("contractStatus") String contractStatus, @Param("createdDate") String createdDate);

    List<Integer> selectEventIdAndArEventIdAtServiceEnd(@Param("contractStatus") String contractStatus);

    @MapKey("key")
    List<Map<String, Object>> selectEventIdAndArEventIdAtServiceEndAfterSixtyDay(@Param("contractStatusList") List<String> contractStatusList, @Param("createdDate") String createdDate);

    @MapKey("key")
    List<Map<String, Object>> selectStampEventIdAndArEventIdAtServiceEndAfterSixtyDay(@Param("contractStatusList") List<String> contractStatusList, @Param("createdDate") String createdDate);

    List<EventGiveAwayDeliveryEntity> selectSubscriptionList(@Param("searchDate") String searchDate, @Param("arEventWinningId") int arEventWinningId, @Param("scheduleType") String scheduleType);

    List<ArEventWinningEntity> selectSubscriptionWinningInfo(@Param("searchDate") String searchDate, @Param("scheduleType") String scheduleType);

    int selectCountEventLogWinningByEventIdAndAttendCodeAndWinningType(@Param("eventId") String eventId, @Param("attendCode") String attendCode, @Param("winningType") String winningType);

    int selectCountEventLogWinningByArEventIdAndAttendCodeAndWinningType(@Param("arEventId") int arEventId, @Param("attendCode") String attendCode);

    int selectCountEventWinningLogByArEventIdAndArEventWinningIdAndAttendCodeNotFail(@Param("arEventId") int arEventId, @Param("arEventWinningId") int arEventWinningId, @Param("attendCode") String attendCode, @Param("phoneNumber") String phoneNumber);

    int selectCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(@Param("arEventId") int arEventId, @Param("objectSort") int objectSort, @Param("attendCode") String attendCode);

    int selectCountEventLogExposureByArEventIdAndObjectSortAndCreatedDayAndCreatedHour(@Param("arEventId") int arEventId, @Param("objectSort") int objectSort, @Param("createdDay") String createdDay, @Param("createdHour") String createdHour);

    int selectCountEventLogExposureByArEventIdAndObjectSortAndCreatedDayImprAndCreatedHourImpr(@Param("arEventId") int arEventId, @Param("objectSort") int objectSort, @Param("createdDayImpr") int createdDayImpr, @Param("createdHourImpr") int createdHourImpr);

    EventBaseJoinArEventJoinEventButtonVO selectEventBaseJoinArEventJoinEventButton(@Param("eventId") String eventId);

    EventBaseJoinArEventJoinEventButtonVO selectEventBaseJoinArEvent(@Param("eventId") String eventId);

    int selectCountEventLogWinningAtConcurrency(@Param("arEventId") int arEventId, @Param("eventWiningSort") int eventWinningSort, @Param("id") long id, @Param("createdDay") String createdDay, @Param("createdHour") String createdHour);

    int selectCountEventLogWinningAtConcurrency2(@Param("arEventId") int arEventId, @Param("eventWiningSort") int eventWinningSort, @Param("id") long id, @Param("createdDay") String createdDay, @Param("createdHour") String createdHour);

    List<EventLogWinningLimitMapperVO> selectEventLogWinningLimitByArEventId(@Param("arEventId") int arEventId);

    List<EventLogWinningLimitMapperVO> selectEventLogExposureLimitByArEventId(@Param("arEventId") int arEventId);

    String selectEventLogWinningLimitByArEventIdAndCode(@Param("arEventId") int arEventId, @Param("code") String code);

    @Select(" SELECT code FROM event_log_winning_limit WHERE 1=1 AND ar_event_id = #{arEventId} AND code = #{code} LIMIT 1")
    String selectEventLogExposureLimitByArEventIdAndCode(@Param("arEventId") int arEventId, @Param("code") String code);

    @Select(" SELECT event_id, event_title, event_type FROM WEB_EVENT_BASE WHERE event_id = #{eventId} ")
    WebEventBaseEntity selectWebEventBaseAtExposureObject(@Param("eventId") String eventId);

    @Select(" SELECT * FROM AR_EVENT_LOGICAL WHERE ar_event_id = #{arEventId}")
    ArEventLogicalEntity selectArEventLogicalAtExposureObject(@Param("arEventId") int arEventId);

    @Select(" SELECT scanning_image_url, active_thumbnail_url, inactive_thumbnail_url, scanning_image_sort, scanning_sound_type, scanning_sound_file FROM AR_EVENT_SCANNING_IMAGE WHERE ar_event_id = #{arEventId} ")
    List<ArEventScanningImageEntity> selectArEventScanningImageListByArEventIdAtExposureObject(@Param("arEventId") int arEventId);

    @Select(" SELECT distinct ar_event_id FROM event_log_exposure WHERE ar_event_id = #{arEventId} ")
    Integer selectDistinctEventLogExposureByArEventId(@Param("arEventId") int arEventId);

    @Select(" SELECT distinct ar_event_id FROM event_log_exposure_limit WHERE ar_event_id = #{arEventId} ")
    Integer selectDistinctEventLogExposureLimitByArEventId(@Param("arEventId") int arEventId);

    @Select(" SELECT give_away_id, winning_type FROM event_give_away_delivery WHERE event_id = #{eventId} AND phone_number = #{phoneNumber} ")
    List<EventGiveAwayDeliveryEntity> selectEventGiveAwayDeliveryListAtNftWinning(@Param("eventId") String eventId, @Param("phoneNumber") String phoneNumber);

    @Select(" SELECT A.give_away_id, A.winning_type " +
            " FROM event_give_away_delivery A " +
            " INNER JOIN event_log_winning B " +
            " ON A.give_away_id = B.give_away_id " +
            " WHERE A.event_id = #{eventId} " +
            " AND B.attend_code = #{attendCode} ")
    List<EventGiveAwayDeliveryEntity> selectEventGiveAwayDeliveryListAtNftWinningByAttendCode(@Param("eventId") String eventId, @Param("attendCode") String attendCode);

    @Select(" SELECT * FROM ar_event_nft_banner WHERE ar_nft_banner_id = #{arNftBannerId} ")
    ArEventNftBannerEntity selectArEventNftBannerById(@Param("arNftBannerId") int arNftBannerId);

    List<EventGateCodeAtUsedMapperVO> selectEventGateCodeAtUsed(@Param("eventId") String eventId);

    List<EventGateCodeAtUsedMapperVO> selectStampEventGateCodeAtUsed(@Param("eventId") String eventId);

    @Select(" SELECT contents FROM event_law_info WHERE event_type = #{eventType} AND law_type = #{lawType} AND #{today} BETWEEN start_date AND end_date ")
    String selectEventLawContentsByEventTypeAndLawTypeBetweenStartDateEndDate(@Param("eventType") String eventType, @Param("lawType") String lawType, @Param("today") String today);

    List<HashMap<String, Object>> selectEventLawHyperLinkList(@Param("eventType") String eventType, @Param("lawType") String lawType);

    @Select(" SELECT * FROM event_law_info WHERE idx = #{idx} ")
    EventLawInfoVO selectEventLawContentsByIdx(@Param("idx") Integer idx);

    void useArEventGateCode(@Param("eventId") String eventId, @Param("attendCode") String attendCode);

    void updateArEventGateCodeUsedCount(@Param("eventId") String eventId, @Param("attendCode") String attendCode);

    void updateEventLogWinningSuccessGiveAwayId(@Param("id") long id, @Param("giveAwayId") int giveAwayId);

    void updateEventGiveAwayDeliveryByGifticonIssuse(@Param("giveAwayId") int giveAwayId, @Param("trId") String trId,
                                                     @Param("gifticonOrderNo") String gifticonOrderNo, @Param("gifticonResultCd") String gifticonResultCd);

    void updateEventWinningLogFail(@Param("id") long id, @Param("arEventObjectId") int arEventObjectId, @Param("arEventWinningId") int arEventWinningId, @Param("eventWinningSort") int eventWinningSort);

    void updateSubscriptionRaffleScheduleDate(@Param("arEventWinningId") int arEventWinningId);

    void updateSubscriptionWinningPresentationScheduleDate(@Param("arEventWinningId") int arEventWinningId);

    void updateArEventNftTokenInfo(@Param("arEventId") int arEventId, @Param("stpId") int stpId, @Param("arEventWinningId") int arEventWinningId, @Param("fileName") String fileName);

    void updatePasswordEventGiveAwayDelivery(@Param("eventId") String eventId, @Param("phoneNumber") String phoneNumber, @Param("newPassword") String newPassword);

    void updateWalletIdFromArEventNftRepository(@Param("arEventNftWalletId") long arEventNftWalletId, @Param("giveAwayIds") List<Integer>giveAwayIds);

    void updateRequestIdOfNftTransfer(@Param("requestId") String requestId, @Param("arNftRepositoryId") long arNftRepositoryId);

    void updateSmsSendLogStatusBySmsCodeList(@Param("smsCodes") List<String>smsCodeList);

    void updateEncryptEventGiveAwayDelivery(@Param("startNum") int startNum, @Param("endNum") int endNum);

    void updateArEventNftCouponInfo(@Param("arEventId") int arEventId, @Param("stpId") int stpId, @Param("arEventWinningId") int arEventWinningId, @Param("fileName") String fileName);

    void updateEmTranSmsBySendMms(@Param("mmsSeq") Integer mmsSeq, @Param("tranPr") Integer tranPr);

    void updateNftCouponIsPayed(@Param("id") long id, @Param("isPayed") boolean isPayed);

    void updateNftTokenIsPayed(@Param("id") long id);

    @Update(" UPDATE ar_event_nft_repository SET give_away_id = #{giveAwayId} WHERE event_winning_log_id = #{eventWinningLogId} ")
    void updateNftRepositoryAtGiveAwayDelivery(@Param("eventWinningLogId") long eventWinningLogId, @Param("giveAwayId") long giveAwayId);

    @Update(" UPDATE ar_event_nft_coupon_repository SET give_away_id = #{giveAwayId} WHERE event_winning_log_id = #{eventWinningLogId} ")
    void updateCouponRepositoryAtGiveAwayDelivery(@Param("eventWinningLogId") long eventWinningLogId, @Param("giveAwayId") long giveAwayId);

    List<EventLogExposureEntity> selectEventLogExposureByEventId(@Param("eventId") String eventId);

    ArEventByIdAtWinningProcessMapperVO selectArEventByIdAtWinningProcess(@Param("eventId") String eventId);

    ArEventByEventIdAtObjectExposureVO selectArEventByEventIdAtObjectExposure(@Param("eventId") String eventId);

    ArEventJoinEventBaseVO selectArEventJoinEventBaseByEventId(@Param("eventId") String eventId);

    ArEventJoinEventBaseVO selectArEventJoinEventBaseByArEventId(@Param("arEventId") int arEventId);

    List<ArEventWinningEntity> selectArEventWinningListByArEventIdAndNotFailAtWinningProcess(@Param("arEventId") int arEventId, @Param("subscriptionYn") String subscriptionYn, @Param("isFail") Boolean isFail);

    List<ArEventWinningEntity> selectArEventWinningListByStpId(@Param("stpId") int stpId, @Param("isFail") Boolean isFail);

    ArEventObjectEntity selectArEventObjectByIdAtWinningProcess(@Param("arEventObjectId") int arEventObjectId);

    List<ArEventWinningButtonEntity> selectArEventWinningButtonListByArEventWinningIdAtWinningProcess(@Param("arEventWinningId") int arEventWinningId);

    List<ArEventObjectEntity> selectArEventObjectByArEventIdAtObjectExposure(@Param("arEventId") int arEventId);

    EventLogWinningEntity selectLastEventLogWinning();

    int countWebEventSmsAuth(WebEventSmsAuthEntity smsAuthEntity);

    @Select(" SELECT count(sms_auth_code) " +
            "        FROM web_event_sms_auth " +
            "        WHERE phone_number = #{phoneNumber} " +
            "        AND  DATE(created_date) = DATE(now()) ")
    int countSendWebEventSmsAuthByToday(@Param("phoneNumber") String phoneNumber);

    int selectCountEventWinningLogByArEventIdAndPhoneNumberAndCreatedDayNotFail(@Param("arEventId") int arEventId, @Param("phoneNumber") String phoneNumber, @Param("createdDay") String createdDay, @Param("arEventWinningId") int arEventWinningId);

    int selectCountEventLogWinningByArEventIdAndPhoneNumberAndWinningType(@Param("arEventId") int arEventId, @Param("phoneNumber") String phoneNumber);

    @Select(" SELECT event_id, phone_number, auth_menu_type, auth_code FROM web_event_sms_auth WHERE sms_auth_code = #{smsAuthCode} ")
    WebEventSmsAuthEntity findWebEventSmsAuthBySmsAuthCode(@Param("smsAuthCode") String smsAuthCode);

    @Select(" SELECT * FROM ar_event_nft_coupon_info WHERE is_payed = 0 AND ar_event_id = #{arEventId} LIMIT 1")
    ArEventNftCouponInfoEntity selectAvailableArEventCouponByArEventId(@Param("arEventId") int arEventId);

    @Select(" SELECT * FROM ar_event_nft_token_info WHERE is_payed = 0 AND ar_event_id = #{arEventId} LIMIT 1")
    ArEventNftTokenInfoEntity selectAvailableArEventNftTokenByArEventId(@Param("arEventId") int arEventId);

    @Select(" SELECT nft_coupon_info_id FROM ar_event_nft_coupon_repository WHERE ar_nft_coupon_repository_id = #{arNftCouponRepositoryId} ")
    Long selectCouponIdFromArEventNftCouponRepositoryById(@Param("arNftCouponRepositoryId") long arNftCouponRepositoryId);

    @Select(" SELECT ar_event_nft_token_info_id FROM ar_event_nft_repository WHERE ar_nft_repository_id = #{arNftRepositoryId} ")
    Long selectNftTokenIdFromArEventNftTokenRepositoryById(@Param("arNftRepositoryId") long arNftRepositoryId);

    @Select(" SELECT event_id, ar_event_id FROM event_log_scheduled WHERE schedule_type = #{scheduleType} ")
    List<EventLogScheduledVO> selectEventLogScheduledByScheduleType(@Param("scheduleType") String scheduleType);

    @Select(" SELECT ifnull(sum(survey_target_limit_count), 0) as sumCnt FROM survey_target_age_gender_limit WHERE ar_event_id = #{arEventId} ")
    int selectSumSurveyTargetAgeGenderLimitByArEventId(@Param("arEventId") int arEventId);

    @Select(" SELECT give_away_id, event_id, ar_event_winning_id, winning_type, product_name, is_receive, created_date FROM event_give_away_delivery \n" +
            "WHERE event_id = #{eventId} " +
            "AND give_away_id IN ( " +
            "  SELECT give_away_id FROM event_log_winning WHERE event_id = #{eventId} AND winning_type != 'FAIL' AND attend_code = #{attendCode} " +
            ") ")
    List<UserWinningInfoResDto> selectGiveAwayDeliveryByEventIdAndAttendCode(@Param("eventId") String eventId, @Param("attendCode") String attendCode);

    int countEventLogAttendButtonByEventIdAndPhoneNumberAndIsToday(@Param("eventId") String eventId, @Param("phoneNumber") String phoneNumber, @Param("todayYn") Boolean isToday);

    void saveEventLogWinningLimit(@Param("arEventId") int arEventId, @Param("code") String code, @Param("codeDesc") String desc);

    void saveEventLogWinning(EventLogWinningEntity entity);

    void saveEventLogWinningSuccess(EventLogWinningEntity entity);

    void deleteEventLogWinningLimitByCode(@Param("arEventId") int arEventId, @Param("code") String code);

    void saveEventLogExposureLimit(@Param("arEventId") int arEventId, @Param("code") String code, @Param("codeDesc") String desc);

    void saveArEventNftRepository(ArEventNftRepositoryEntity arEventNftRepositoryEntity);

    @Insert(" INSERT INTO ar_event_nft_coupon_repository" +
            " ( nft_coupon_info_id, event_winning_log_id )" +
            " SELECT id, #{eventWinningLogId}  FROM ar_event_nft_coupon_info WHERE is_payed = 0 AND ar_event_id = #{arEventId} AND ar_event_winning_id = #{arEventWinningId} LIMIT 1 ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void saveSelectAvailableArEventCouponByArEventIdAndArEventWinningId(CouponSaveReqMapperVO couponSaveReqMapperVO);

    @Insert(" INSERT INTO ar_event_nft_coupon_repository" +
            " ( nft_coupon_info_id, stamp_event_winning_log_id )" +
            " SELECT id, #{stampEventWinningLogId}  FROM ar_event_nft_coupon_info WHERE is_payed = 0 AND stp_id = #{stpId} AND ar_event_winning_id = #{arEventWinningId} LIMIT 1 ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void saveSelectAvailableArEventCouponByStpIdAndArEventWinningId(CouponSaveReqMapperVO couponSaveReqMapperVO);

    @Insert(" INSERT INTO ar_event_nft_coupon_repository" +
            " ( event_winning_log_id, ocb_coupon_id, give_away_id )" +
            " VALUES ( #{eventWinningLogId}, #{ocbCouponId}, #{giveAwayId} ) ")
    void saveArEventNftCouponRepository(CouponSaveReqMapperVO couponSaveReqMapperVO);

    @Insert(" INSERT INTO ar_event_nft_repository" +
            " ( ar_event_nft_token_info_id, event_winning_log_id )" +
            " SELECT id, #{eventWinningLogId}  FROM ar_event_nft_token_info WHERE is_payed = 0 AND ar_event_id = #{arEventId} AND ar_event_winning_id = #{arEventWinningId} LIMIT 1 ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void saveSelectAvailableArEventNftTokenByArEventIdAndArEventWinningId(CouponSaveReqMapperVO couponSaveReqMapperVO);

    void deleteEventLogExposureByArEventId(@Param("arEventId") int arEventId);

    void deleteEventLogExposureLimitByArEventId(@Param("arEventId") int arEventId);

    @Delete(" DELETE FROM event_log_winning_limit WHERE ar_event_id = #{arEventId}  AND code LIKE CONCAT(#{code}, '%') AND code_desc = #{desc} ")
    void deleteEventLogWinningLimitByArEventIdAndCodeAndDesc(@Param("arEventId") int arEventId, @Param("code") String code, @Param("desc") String desc);

    @Delete(" DELETE FROM event_log_exposure_limit WHERE ar_event_id = #{arEventId} AND code LIKE CONCAT(#{code}, '%') AND code_desc = #{desc} ")
    void deleteEventLogExposureLimitByArEventIdAndCode(@Param("arEventId") int arEventId, @Param("code") String code, @Param("desc") String desc);

    @Delete(" DELETE FROM event_give_away_delivery WHERE event_id = #{eventId} ")
    void deleteEventGiveAwayByEventId(@Param("eventId") String eventId);

    @Delete(" DELETE FROM event_log_winning WHERE event_id = #{eventId} ")
    void deleteEventLogWinningByEventId(@Param("eventId") String eventId);

    @Delete(" DELETE FROM event_log_winning_limit WHERE ar_event_id = #{arEventId} ")
    void deleteEventLogWinningLimitByArEventId(@Param("arEventId") int arEventId);

    @Insert(" INSERT INTO event_log_scheduled" +
            "( event_id, ar_event_id, schedule_type )" +
            "VALUES ( #{eventId}, #{arEventId}, #{scheduleType} )" )
    void saveEventLogScheduledAtNew(EventLogScheduledVO eventLogScheduledVO);

    @Insert(" INSERT INTO sequences (name, currval) VALUES (#{name}, #{currval})")
    void saveSequences(@Param("name") String name, @Param("currval") long currval);

    void updateArEventWinningText(ArEventWinningTextEntity arEventWinningTextEntity);

    void updateArEventRepositoryButton(ArEventRepositoryButtonEntity arEventRepositoryButtonEntity);

    @Update(" UPDATE web_event_sms_auth SET is_auth = 1 WHERE sms_auth_code = #{smsAuthCode} ")
    void updateUsedSmsAuth(@Param("smsAuthCode") String smsAuthCode);

    @Delete(" DELETE FROM web_event_sms_auth WHERE event_id = #{eventId} AND phone_number = #{phoneNumber} AND auth_menu_type = #{authMenuType} ")
    void deleteWebEventSmsAuth(WebEventSmsAuthEntity smsAuthEntity);

    @Delete(" DELETE FROM web_event_sms_auth WHERE event_id = #{eventId} ")
    void deleteWebEventSmsAuthByEventId(@Param("eventId") String eventId);

    @Delete(" DELETE FROM web_event_sms_auth where DATE(created_date) = DATE(DATE_ADD(now(), interval -1 day)) ")
    void deleteWebEventSmsAuthAtPrevOneDay();

    @Delete(" DELETE FROM sequences WHERE name = #{name} ")
    void deleteSequencesByName(@Param("name") String name);

    List<EventGiveAwayDeliveryEntity> selectEventGiveAwayDeliveryListAsStampEvent(@Param("eventIdList") List<String> eventIdList, @Param("phoneNumber") String phoneNumber, @Param("attendCode") String attendCode);

    void insertEventGiveAwayDelivery(EventGiveAwayDeliveryEntity entity);

    Long getEventLogWinningSuccessLastIndex(@Param("arEventId") int arEventId, @Param("winningSort") int winningSort, @Param("limitCount") int limitCount, @Param("createdDay") Integer createdDay, @Param("createdHour") Integer createdHour);

    Integer getEventLogWinningSuccessCount(@Param("arEventId") int arEventId, @Param("winningSort") int winningSort, @Param("createdDay") Integer createdDay, @Param("createdHour") Integer createdHour);

    void saveEventGiveAwayDeliveryButtonAddList(@Param("list") List<EventGiveAwayDeliveryButtonAddEntity> entityList);

    CouponDetailInfoMapVO selectCouponDetailInfo(@Param("id") long id);

    Map<String, Object> selectEventIdArEventWinningIdByCouponRepositoryId(@Param("id") long id);

    Map<String, Object> selectEventIdArEventWinningIdByCouponRepositoryIdAtStamp(@Param("id") long id);

    @Update(" UPDATE event_give_away_delivery SET is_receive = #{isReceive} WHERE give_away_id = #{giveAwayId} ")
    void updateEventGiveAwayDeliveryIsReceiveByGiveAwayId(@Param("giveAwayId") int giveAwayId, @Param("isReceive") boolean isReceive);

    void updateArEvent(ArEventUpdateVO vo);

    ArEventNftCouponInfoResDto selectArEventNftCouponInfJoinArEventWinningById(@Param("id") long id);

    ArEventCouponDetailInfoResDto selectArEventNftCouponRepositoryEntityJoinArEventWInningById(@Param("id") long id);

    String selectEventIdByArEventWinningId(@Param("arEventWinningId") int arEventWinningId);

    @Select(" SELECT ar_event_winning_id, stp_id, product_name, object_mapping_type, object_mapping_number, winning_type, winning_time_type,start_winning_time, end_winning_time, hour_winning_number, total_winning_number, event_winning_sort, day_winning_number, attend_code_winning_type, attend_code_limit_type, attend_code_winning_count,winning_percent, winning_image_url, subscription_yn, auto_winning_yn, ocb_coupon_id, user_winning_type, user_winning_limit_type, user_winning_limit_count, stp_winning_popup_img_url FROM ar_event_winning WHERE winning_type != 'FAIL' AND stp_id = #{stpId} AND object_mapping_number = #{mappingNumber} order by event_winning_sort asc ")
    LinkedList<ArEventWinningEntity> selectArEventWinningListByStpIdAndMappingNumber(@Param("stpId") int stpId, @Param("mappingNumber") int mappingNumber);

    @Select(" SELECT ar_event_winning_id, ar_event_id, product_name, object_mapping_type, object_mapping_number, winning_type, winning_time_type,start_winning_time, end_winning_time, hour_winning_number, total_winning_number, event_winning_sort, day_winning_number, attend_code_winning_type, attend_code_limit_type, attend_code_winning_count,winning_percent, winning_image_url, subscription_yn, auto_winning_yn, ocb_coupon_id, user_winning_type, user_winning_limit_type, user_winning_limit_count FROM ar_event_winning WHERE winning_type != 'FAIL' AND ar_event_id = #{arEventId} AND object_mapping_number = #{mappingNumber} order by event_winning_sort asc ")
    LinkedList<ArEventWinningEntity> selectArEventWinningListByArEventIdAndMappingNumber(@Param("arEventId") int arEventId, @Param("mappingNumber") int mappingNumber);

    LinkedList<ArEventWinningEntity> selectArEventWinningListByStpIdAndStampAttendSortLogCount(@Param("stpId") int stpId, @Param("winningAttemptOrder") int winningAttemptOrder, @Param("attendValue") String attendValue, @Param("attendType") String attendType);

    @Delete(" DELETE FROM em_tran WHERE date(tran_date) < adddate(now(), -7) ")
    void deleteEmTran();

    @Update(" UPDATE ar_event_nft_coupon_repository SET stp_give_away_id = #{stpGiveAwayId} WHERE nft_coupon_info_id = #{couponId}")
    void updateArEventNftCouponRepositoryStpGiveAwayIdByCouponId(@Param("couponId") Long couponId, @Param("stpGiveAwayId") Long stpGiveAwayId);

    void insertSodarEventUpdateHistory(@Param("eventId") String eventId, @Param("jsonStr") String jsonStr, @Param("createdBy") String createdBy);

    void insertArEventNftCouponByTemp(@Param("arEventId") int arEventId, @Param("stpId") int stpId, @Param("arEventWinningId") long arEventWinningId, @Param("uploadFileName") String uploadFileName, @Param("tempSeq") long tempSeq);

    void insertArEventNftTokenByTemp(@Param("arEventId") int arEventId, @Param("stpId") int stpId, @Param("arEventWinningId") long arEventWinningId, @Param("uploadFileName") String uploadFileName, @Param("tempSeq") long tempSeq);

    void deleteArEventNftCouponInfoTempBySeq(@Param("tempSeq") long tempSeq);

    void deleteArEventNftCouponInfoByLegacy();

    void deleteArEventCouponRepositoryByEventId(@Param("eventId") String eventId);

    @Delete(" DELETE FROM ar_event_nft_coupon_info_temp WHERE temp_seq > 0 ")
    void deleteArEventNftCouponInfoTemp();

    void deleteArEventNftCouponInfoByEventIdx(@Param("isStampEvent") Boolean isStampEvent, @Param("eventIdx") int eventIdx);
}
