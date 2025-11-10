package kr.co.syrup.adreport.web.event.service;

import kr.co.syrup.adreport.framework.common.annotation.LoggingTimeFilter;
import kr.co.syrup.adreport.framework.utils.AES256Utils;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.web.event.define.*;
import kr.co.syrup.adreport.web.event.dto.request.EventLogPvReqDto;
import kr.co.syrup.adreport.web.event.dto.response.api.OcbSessionApiResDto;
import kr.co.syrup.adreport.web.event.entity.*;
import kr.co.syrup.adreport.web.event.entity.repository.*;
import kr.co.syrup.adreport.web.event.mybatis.mapper.ArEventMapper;
import kr.co.syrup.adreport.web.event.mybatis.mapper.LogMapper;
import kr.co.syrup.adreport.web.event.mybatis.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class LogService {

    @Autowired
    private ArEventMapper arEventMapper;

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private AES256Utils aes256Utils;

    @Autowired
    private EventLogExposureEntityRepository eventLogExposureEntityRepository;

    @Autowired
    private EventLogAttendButtonEntityRepository eventLogAttendButtonEntityRepository;

    @Autowired
    private EventLogConnectEntityRepository eventLogConnectEntityRepository;

    @Autowired
    private EventGiveAwayDeliveryEntityRepository eventGiveAwayDeliveryEntityRepository;

    @Autowired
    private EventLogWinningSubscriptionRepository eventLogWinningSubscriptionRepository;

    @Autowired
    private EventLogSmsSendEntityRepository eventLogSmsSendEntityRepository;

    @Autowired
    private OcbApiService ocbApiService;

    /**************************************************** SAVE,UPDATE START ****************************************************/

    public CompletableFuture asyncSaveAllEventLogExposure(List<EventLogExposureEntity> exposureEntityList) {
        if (PredicateUtils.isNotNullList(exposureEntityList)) {
            try {
                // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
                eventLogExposureEntityRepository.saveAll(exposureEntityList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * EVENT_LOG_ATTEND_BUTTON 저장
     * @param eventLogAttendSaveVO
     */
    @Transactional
    public void saveEventLogAttendButton(EventLogAttendSaveVO eventLogAttendSaveVO) {
        try {
            //eventLogAttendButtonEntityRepository.save(attendButtonEntity);
            logMapper.saveEventLogAttendButton(eventLogAttendSaveVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public CompletableFuture<Map<String, Object>> saveEventLogAttendButtonCompletableFuture(EventLogAttendButtonEntity attendButtonEntity) {
        try {
            log.info("saveEventLogAttendButtonCompletableFuture");
            eventLogAttendButtonEntityRepository.save(attendButtonEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * EVENT_LOG_CONNECT 저장
     * @param vo
     */
    @Transactional
    public void saveEventLogConnect(EventLogConnectSaveVO vo) {
        try {
            logMapper.saveEventLogConnect(vo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public EventLogWinningEntity  saveEventLogWinningByReturn2(EventLogWinningEntity eventLogWinningEntity, boolean isSuccess) {
        if (PredicateUtils.isNotNull(eventLogWinningEntity.getPhoneNumber())) {
            eventLogWinningEntity.setPhoneNumber(aes256Utils.encrypt(eventLogWinningEntity.getPhoneNumber()));
        }
        try {
            if (isSuccess) {
                arEventMapper.saveEventLogWinningSuccess(eventLogWinningEntity);
            } else {
                arEventMapper.saveEventLogWinning(eventLogWinningEntity);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            return eventLogWinningEntity;
        }
    }

    @Transactional
    public void saveAllEventLogWinningSubscriptionFromEventGiveAwayList(List<EventGiveAwayDeliveryEntity> entityList) {
        if (!PredicateUtils.isNullList(entityList)) {
            List<EventLogWinningSubscriptionEntity> saveList = new ArrayList<>();
            for (EventGiveAwayDeliveryEntity entity : entityList) {
                saveList.add(
                        eventLogWinningSubscriptionRepository.save(EventLogWinningSubscriptionEntity.saveOf(entity.getGiveAwayId(), entity.getArEventWinningId()))
                );
            }
            try {
                // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
                eventLogWinningSubscriptionRepository.saveAll(saveList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void updateEventLogWinningGiveAwayId(Long eventLogWinningId, int giveAwayId) {
        try {
            arEventMapper.updateEventLogWinningSuccessGiveAwayId(eventLogWinningId, giveAwayId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void updateEventGiveAwayDeliveryByGifticonIssuse(int giveAwayId, String trId, String gifticonOrderNo, String gifticonResultCd) {
        try {
            arEventMapper.updateEventGiveAwayDeliveryByGifticonIssuse(giveAwayId, trId, gifticonOrderNo, gifticonResultCd);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void updateEventWinningLogFail(long id, int arEventObjectId, int arEventWinningId, int eventWinningSort) {
        try {
            arEventMapper.updateEventWinningLogFail(id, arEventObjectId, arEventWinningId, eventWinningSort);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void saveEventLogSmsByEventGiveAwayDelivery(EventGiveAwayDeliveryEntity eventGiveAwayDeliveryEntity, String smsContent, String sendDate) {
        if (PredicateUtils.isNotNull(eventGiveAwayDeliveryEntity)) {
            //핸드폰 번호 복호화
            eventGiveAwayDeliveryEntity.setPhoneNumber(aes256Utils.decrypt(eventGiveAwayDeliveryEntity.getPhoneNumber()));
            try {
                eventLogSmsSendEntityRepository.save(EventLogSmsSendEntity.sendOf(eventGiveAwayDeliveryEntity, smsContent, sendDate));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void deleteEventLogExposureByArEventId(int arEventId) {
        try {
            arEventMapper.deleteEventLogExposureByArEventId(arEventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteEventLogExposureLimitByArEventId(int arEventId) {
        try {
            arEventMapper.deleteEventLogExposureLimitByArEventId(arEventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteEventLogConnectByArEventId(int arEventId) {
        try {
            eventLogConnectEntityRepository.deleteByArEventId(arEventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteEventLogPvByArEventId(int arEventId) {
        try {
            logMapper.deleteEventLogPvByArEventId(arEventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteEventLogSmsSendByArEventId(int arEventId) {
        try {
            logMapper.deleteEventLogSmsSendByArEventId(arEventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteEventLogWinningSubscriptionByArEventId(int arEventId) {
        try {
            logMapper.deleteEventLogWinningSubscriptionByArEventId(arEventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteEventLogAttendButtonByArEventId(int arEventId) {
        try {
            eventLogAttendButtonEntityRepository.deleteByArEventId(arEventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteOcbLogPointSaveByEventId(String eventId) {
        try {
            logMapper.deleteOcbLogPointSaveByEventId(eventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deletePhotoLogPrintCountByEventId(String eventId) {
        try {
            logMapper.deletePhotoLogPrintCountByEventId(eventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void saveEventLogWinningLimit(int arEventId, int secondIdx, String createdDay, String createdHour, String attendCode, String limitType) {
        String code = "";
        if (PredicateUtils.isNotNull(attendCode)) {
            secondIdx = 0;
            //getCountEventWinningLogByEventIdAndAttendCodeNotFail
            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_CODE.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(arEventId), attendCode);
            }
            //getCountEventWinningLogByEventIdAndAttendCodeAndTodayNotFail
            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_CODE_TODAY.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(arEventId), attendCode, createdDay);
            }
            //getCountEventWinningLogByArEventIdAndArEventWinningIdAndAttendCodeNotFail
            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_WINNINGID_CODE.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(arEventId), String.valueOf(secondIdx), attendCode);
            }
            //getCountEventWinningLogByArEventIdAndArEventWinningIdAndAttendCodeAndTodayNotFail
            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_WINNINGID_CODE_TODAY.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(arEventId), String.valueOf(secondIdx), attendCode, createdDay);
            }

            //=========================================== 참여번호형 - 전화번호일때 ==============================================================
            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_MDN.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(arEventId), attendCode);
            }

            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_MDN_TODAY.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(arEventId), attendCode, createdDay);
            }

            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_WINNINGID_MDN.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(arEventId), String.valueOf(secondIdx), attendCode);
            }

            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_WINNINGID_MDN_TODAY.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(arEventId), String.valueOf(secondIdx), attendCode, createdDay);
            }
            //=========================================== 참여번호형 - 전화번호일때 ==============================================================

        } else {
            //getCountEventWinningLogByArEventIdAndEventWinningSortAndDayAndHourNotFail
            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_SORT_TODAY_HOUR.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(arEventId), String.valueOf(secondIdx), createdDay, createdHour);
            }
            //getCountEventWinningLogByArEventIdAndEventWinningSortAndTodayAndNotFail
            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_SORT_TODAY.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(arEventId), String.valueOf(secondIdx), createdDay);
            }
            //getCountEventWinningLogByArEventIdAndEventWinningSortNotFail
            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_SORT.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(arEventId), String.valueOf(secondIdx));
            }
        }
        boolean isExitsCode = this.isExitsEventLogWinningLimit(arEventId, code);
        log.debug("isExitsCode >> " + isExitsCode);
        if (!isExitsCode) {
            arEventMapper.saveEventLogWinningLimit(arEventId, code, limitType);
        }
    }

    public List<EventLogWinningLimitMapperVO> selectEventLogWinningLimitListByArEventId(int arEventId) {
        return arEventMapper.selectEventLogWinningLimitByArEventId(arEventId);
    }

    public boolean isExitsEventLogWinningLimit(int arEventId, String code) {
        String savedCode  = arEventMapper.selectEventLogWinningLimitByArEventIdAndCode(arEventId, code);
        if (PredicateUtils.isNull(savedCode) || StringUtils.isEmpty(savedCode)) {
            return false;
        }
        return true;
    }

    @Transactional
    public void saveEventLogExposureLimit(int arEventId, int objectSort, String createdDay, String createdHour, String attendCode, String limitType) {
        String code = "";
        if (PredicateUtils.isNotNull(attendCode)) {
            if (PredicateUtils.isEqualsStr(limitType, EventLogExposureLimitDefine.ID_SORT_CODE.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(arEventId), String.valueOf(objectSort), attendCode);
            }
            if (PredicateUtils.isEqualsStr(limitType, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(arEventId), String.valueOf(objectSort), attendCode, DateUtils.getNowMMDD());
            }
            //[DTWS-220]
            if (PredicateUtils.isEqualsStr(limitType, EventLogExposureLimitDefine.ID_SORT.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(arEventId), String.valueOf(objectSort));
            }
        } else {
            if (PredicateUtils.isEqualsStr(limitType, EventLogExposureLimitDefine.ID_SORT_HOUR.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(arEventId), String.valueOf(objectSort), DateUtils.getNowHour());
            }
            if (PredicateUtils.isEqualsStr(limitType, EventLogExposureLimitDefine.ID_SORT_TODAY.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(arEventId), String.valueOf(objectSort), DateUtils.getNowMMDD());
            }
            //[DTWS-220]
            if (PredicateUtils.isEqualsStr(limitType, EventLogExposureLimitDefine.ID_SORT.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(arEventId), String.valueOf(objectSort));
            }
        }
        log.info("saveEventLogExposureLimit > code :: " + code);
        boolean isExitsCode = this.isExitsEventLogExposureLimit(arEventId, code);
        log.debug("isExitsCode >> " + isExitsCode);
        if (!isExitsCode) {
            try {
                arEventMapper.saveEventLogExposureLimit(arEventId, code, limitType);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                try {
                    arEventMapper.saveEventLogExposureLimit(arEventId, code, limitType);
                } catch (Exception e2) {
                    log.error(e2.getMessage());
                }
            }
        }
    }

    public boolean isExitsEventLogExposureLimit(int arEventId, String code) {
        String savedCode  = arEventMapper.selectEventLogExposureLimitByArEventIdAndCode(arEventId, code);
        if (PredicateUtils.isNull(savedCode) || StringUtils.isEmpty(savedCode)) {
            return false;
        }
        return true;
    }

    @Transactional
    public void deleteEventLogWinningLimitByArEventIdAndCodeAndDesc(int arEventId, String code, String desc) {
        try {
            arEventMapper.deleteEventLogWinningLimitByArEventIdAndCodeAndDesc(arEventId, code, desc);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteEventLogExposureLimitByArEventIdAndCode(int arEventId, String code, String desc) {
        try {
            arEventMapper.deleteEventLogExposureLimitByArEventIdAndCode(arEventId, code, desc);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void saveEventLogScheduled(String eventId, int arEventId, String scheduleType) {
        try {
            arEventMapper.saveEventLogScheduledAtNew(EventLogScheduledVO.saveOf(eventId, arEventId, scheduleType));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void saveEventLogPv(EventLogPvReqDto reqDto, EventLogPvKeyDefine eventLogPvKeyDefine) {
        try {
            if (StringTools.containsIgnoreCase(reqDto.getEventId(), "S")) {
                logMapper.saveEventLogPvBySelectAtStamp(EventLogPvVO.saveOf(reqDto, eventLogPvKeyDefine));
            } else {
                logMapper.saveEventLogPvBySelect(EventLogPvVO.saveOf(reqDto, eventLogPvKeyDefine));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void saveOcbLogPointSave(OcbPointSaveEntity ocbPointSaveEntity, String eventId, String phoneNumber, String pointSaveType, boolean isSuccess, String resultStr, Integer giveAwayId, String requestId, String partnerToken) {
        //OCB 세션 API 콜
        OcbSessionApiResDto ocbSessionInfo = ocbApiService.getOcbSessionApi(partnerToken);
        OcbLogPointSaveVO ocbLogPointSaveVO = new OcbLogPointSaveVO().builder()
                .eventId(eventId)
                .ocbPointSaveId(ocbPointSaveEntity.getId())
                .phoneNumber(aes256Utils.encrypt(phoneNumber))  //[DTWS-133]
                .ocbMbrId(PredicateUtils.isNotNull(ocbSessionInfo.getMbrId()) ? ocbSessionInfo.getMbrId() : null)
                .pointSaveType(pointSaveType)
                .point(ocbPointSaveEntity.getSavePoint())
                .isSuccess(isSuccess)
                .pointSaveResult(resultStr)
                .giveAwayId(giveAwayId)
                .requestId(requestId)
                .build();

        try {
            logMapper.saveOcbLogPointSave(ocbLogPointSaveVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteEventLogWinning(long id, boolean isSuccess) {
        try {
            if (isSuccess) {
                logMapper.deleteEventLogWinningSuccess(id);
            } else {
                logMapper.deleteEventLogWinning(id);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**************************************************** SAVE,UPDATE END ****************************************************/


    /**************************************************** SELECT START ****************************************************/

    @LoggingTimeFilter
    public int getCountEventLogExposureByArEventIdAndObjectSort(int arEventId, int objectSort) {
        int cnt = arEventMapper.selectCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventId, objectSort, "");
        return cnt;
    }

    @LoggingTimeFilter
    public int getCountEventLogExposureByArEventIdAndObjectSortCreatedHour(int arEventId, int objectSort) {
        int cnt = arEventMapper.selectCountEventLogExposureByArEventIdAndObjectSortAndCreatedDayImprAndCreatedHourImpr(arEventId, objectSort, Integer.parseInt(DateUtils.getNowYYMMDD()), Integer.parseInt(DateUtils.getNowYYMMDDHH()));
        return cnt;
    }

    @LoggingTimeFilter
    public int getCountEventLogExposureByArEventIdAndObjectSortAndToday(int arEventId, int objectSort) {
        int cnt = arEventMapper.selectCountEventLogExposureByArEventIdAndObjectSortAndCreatedDayImpr(arEventId, objectSort, Integer.parseInt(DateUtils.getNowYYMMDD()));
        return cnt;
    }

    @LoggingTimeFilter
    public int getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(int arEventId, int objectSort, String attendCode) {
        int cnt = arEventMapper.selectCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventId, objectSort, attendCode);
        return cnt;
    }

    @LoggingTimeFilter
    public int getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(int arEventId, int objectSort, String attendCode) {
        int cnt = arEventMapper.selectCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndCreatedDayImpr(arEventId, objectSort, attendCode, Integer.parseInt(DateUtils.getNowYYMMDD()));
        return cnt;
    }

    /**
     * EVENT_LOG_ATTEND_BUTTON 개수 가져오기
     * @param eventId
     * @param attendCode
     * @return
     */
    public int getCountEventLogAttendButtonByEventId(String eventId, String attendCode) {
        return (int)eventLogAttendButtonEntityRepository.countByEventIdAndAttendCode(eventId, attendCode);
    }

    /**
     * EVENT_LOG_ATTEND_BUTTON 개수 가져오기
     * @param eventId
     * @param attendCode
     * @return
     */
    public int getCountEventLogAttendButtonByEventIdAndAttendCodeAndToday(String eventId, String attendCode) {
        return arEventMapper.selectCountEventLogAttendButtonByEventIdAndAndCreatedDayAndAttendCode(eventId, attendCode, DateUtils.getNow("yyyy-MM-dd"));
    }

    @LoggingTimeFilter
    public void updateArEventGateCodeUsedCount(String eventId, String attendCode) {
        if (StringUtils.isNotEmpty(eventId) && StringUtils.isNotEmpty(attendCode)) {
            arEventMapper.updateArEventGateCodeUsedCount(eventId, attendCode);
        }
    }

    /**
     * EVENT_LOG_WINNING 개수 가져오기
     * @param arEventId
     * @return
     */
    @LoggingTimeFilter
    public int getCountEventWinningLogByArEventIdAndEventWinningSortNotFail(int arEventId, int eventWinningSort) {
        int cnt = arEventMapper.selectCountEventWinningLogByArEventIdAndEventWinningSortNotFail(arEventId, eventWinningSort);
        return cnt;
    }

    @LoggingTimeFilter
    public int getCountEventWinningLogByArEventIdAndEventWinningSortAndTodayAndNotFail2(int arEventId, int eventWinningSort) {
        int cnt = arEventMapper.selectCountEventWinningLogByArEventIdAndCreatedDateAndEventWinningSortNotFail2(arEventId, DateUtils.getNowYYMMDD(), eventWinningSort);
        return cnt;
    }

    @LoggingTimeFilter
    public int getCountEventWinningLogByArEventIdAndEventWinningSortAndDayAndHourNotFail2(int arEventId, int eventWinningSort) {
        int cnt = arEventMapper.selectCountEventWinningLogByArEventIdAndEventWinningSortAndDayAndHourNotFail2(arEventId, eventWinningSort, null, DateUtils.getNowYYMMDDHH());
        return cnt;
    }

    /**
     * EVENT_LOG_WINNING 개수 가져오기
     * @param eventId
     * @param attendCode
     * @return
     */
    @LoggingTimeFilter
    public int getCountEventWinningLogByEventIdAndAttendCodeNotFail(String eventId, String attendCode) {
        int cnt = arEventMapper.selectCountEventLogWinningByEventIdAndAttendCodeAndWinningType(eventId, attendCode, WinningTypeDefine.꽝.code());
        return cnt;
    }

    @LoggingTimeFilter
    public int getCountEventWinningLogByArEventIdAndAttendCodeNotFail(int arEventId, String attendCode) {
        int cnt = arEventMapper.selectCountEventLogWinningByArEventIdAndAttendCodeAndWinningType(arEventId, attendCode);
        return cnt;
    }

    @LoggingTimeFilter
    public int getCountEventWinningLogByArEventIdAndArEventWinningIdAndAttendCodeNotFail(int arEventId, int arEventWinningId, String attendCode, String phoneNumber) {
        if (PredicateUtils.isNotNull(phoneNumber)) {
            phoneNumber = aes256Utils.encrypt(phoneNumber);
        }
        int cnt = arEventMapper.selectCountEventWinningLogByArEventIdAndArEventWinningIdAndAttendCodeNotFail(arEventId, arEventWinningId, attendCode, phoneNumber);
        return cnt;
    }

    @LoggingTimeFilter
    public int getCountEventWinningLogByEventIdAndAttendCodeAndTodayNotFail2(int arEventId, String attendCode) {
        int cnt = arEventMapper.selectCountEventWinningLogByArEventIdAndAttendCodeAndCreatedDayNotFail2(arEventId, attendCode, DateUtils.getNowYYMMDD(), 0, null);
        return cnt;
    }

    @LoggingTimeFilter
    public int getCountEventWinningLogByArEventIdAndArEventWinningIdAndAttendCodeAndTodayNotFail2(int arEventId, int arEventWinningId, String attendCode, String phoneNumber) {

        if (PredicateUtils.isNotNull(phoneNumber)) {
            phoneNumber = aes256Utils.encrypt(phoneNumber);
        }
        int cnt = arEventMapper.selectCountEventWinningLogByArEventIdAndAttendCodeAndCreatedDayNotFail2(arEventId, attendCode, DateUtils.getNowYYMMDD(), arEventWinningId, phoneNumber);
        return cnt;
    }

    /**
     * 경품 입력 기준 기프티콘 발급 개수 조회
     * @param eventId
     * @param arEventWinningId
     * @param phoneNumber
     * @param memberBirth
     * @return
     */
    public int getCountByGiveAwayReceiveGifticon(String eventId, int arEventWinningId, String phoneNumber, String memberBirth) {
        return (int) eventGiveAwayDeliveryEntityRepository.countByEventIdAndArEventWinningIdAndWinningTypeAndPhoneNumberAndMemberBirthAndGifticonResultCd(
                        eventId, arEventWinningId, WinningTypeDefine.기프티콘.code(), phoneNumber.trim(), memberBirth.trim(), "000"
                    );
    }

    public int getCountByEventLogWinningIdFromGiveAway(long eventLogWinningId) {
        return (int) eventGiveAwayDeliveryEntityRepository.countByEventLogWinningId(eventLogWinningId);
    }

    public int getTotalEventLogWinningCountByArEventIdEqualsAndEventWinningSortEqualsAndIdIsLessThan(int arEventId, int eventWinningSort, long id) {
        return arEventMapper.selectCountEventLogWinningAtConcurrency(arEventId, eventWinningSort, id, null, null);
    }

    public int getDayEventLogWinningCountByArEventIdEqualsAndEventWinningSortEqualsAndIdIsLessThan2(int arEventId, int eventWinningSort, long id) {
        return arEventMapper.selectCountEventLogWinningAtConcurrency(arEventId, eventWinningSort, id, DateUtils.getNowYYMMDD(), null);
    }

    public int getHourEventLogWinningCountByArEventIdEqualsAndEventWinningSortEqualsAndIdIsLessThan2(int arEventId, int eventWinningSort, long id) {
        return arEventMapper.selectCountEventLogWinningAtConcurrency2(arEventId, eventWinningSort, id, null, DateUtils.getNowYYMMDDHH());
    }

    public EventLogWinningSubscriptionEntity getEventLogWinningSubscriptionByGiveAwayId(int giveAwayId) {
        return eventLogWinningSubscriptionRepository.findByGiveAwayId(giveAwayId).orElseGet(EventLogWinningSubscriptionEntity::new);
    }

    public List<EventLogWinningLimitMapperVO> getEventLogExposureLimitListByArEventId(int arEventId) {
        return arEventMapper.selectEventLogExposureLimitByArEventId(arEventId);
    }

    public List<EventLogScheduledVO> selectEventLogScheduledByScheduleType(String scheduleType) {
        return arEventMapper.selectEventLogScheduledByScheduleType(scheduleType);
    }

    @LoggingTimeFilter
    public int getCountEventWinningLogByEventIdAndPhoneNumberAndTodayNotFail(int arEventId, String phoneNumber) {
        int cnt = arEventMapper.selectCountEventWinningLogByArEventIdAndPhoneNumberAndCreatedDayNotFail(arEventId, aes256Utils.encrypt(phoneNumber), DateUtils.getNowYYMMDD(), 0);
        return cnt;
    }

    @LoggingTimeFilter
    public int getCountEventWinningLogByEventIdAndPhoneNumberNotFail(int arEventId, String phoneNumber) {
        int cnt = arEventMapper.selectCountEventLogWinningByArEventIdAndPhoneNumberAndWinningType(arEventId, aes256Utils.encrypt(phoneNumber));
        return cnt;
    }

    public int gerCountEventLogAttendButtonByEventIdAndPhoneNumberAndIsToday(String eventId, String phoneNumber, Boolean isToday) {
        return arEventMapper.countEventLogAttendButtonByEventIdAndPhoneNumberAndIsToday(eventId, phoneNumber, isToday);
    }

    public int getCountOcbLogPointSaveByEventId(String eventId, boolean isToday) {
        if (!isToday) {
            return logMapper.selectCountOcbLogPointSaveByEventId(eventId);
        } else {
            return logMapper.selectCountOcbLogPointSaveByEventIdToday(eventId);
        }
    }

    public long getCountOcbLogPointSaveByEventIdAndOcbPointSaveIdAndPhoneNumber(String eventId, int ocbPointSaveId, String phoneNumber, boolean isToday) {
        if (!isToday) {
            return logMapper.selectCountOcbLogPointSaveByEventIdAndOcbPointSaveId(eventId, ocbPointSaveId, aes256Utils.encrypt(phoneNumber));
        } else {
            return logMapper.selectCountOcbLogPointSaveByEventIdAndOcbPointSaveIdAtToday(eventId, ocbPointSaveId, aes256Utils.encrypt(phoneNumber));
        }
    }

    public boolean isOcbLogPointSaveMbrId(String eventId, int ocbPointSaveId, String mbrId, boolean isToday) {
        int cnt = 0;
        if (!isToday) {
            cnt = logMapper.selectCountOcbLogPointSaveByEventIdAndOcbPointSaveIdAndMbrId(eventId, ocbPointSaveId, mbrId);
        } else {
            cnt = logMapper.selectCountOcbLogPointSaveByEventIdAndOcbPointSaveIdAndMbrIdAtToday(eventId, ocbPointSaveId, mbrId);
        }

        if (cnt == 0) {
            return false;
        }
        return true;
    }

    public Long getEventLogWinningSuccessLastIndexHour(int arEventId, int winningSort, int limitCount) {
        return arEventMapper.getEventLogWinningSuccessLastIndex(arEventId, winningSort, limitCount, Integer.parseInt(DateUtils.getNowYYMMDD()), Integer.parseInt(DateUtils.getNowYYMMDDHH()));
    }

    public Long getEventLogWinningSuccessLastIndexDay(int arEventId, int winningSort, int limitCount) {
        return arEventMapper.getEventLogWinningSuccessLastIndex(arEventId, winningSort, limitCount, Integer.parseInt(DateUtils.getNowYYMMDD()), null);
    }

    public Long getEventLogWinningSuccessLastIndex(int arEventId, int winningSort, int limitCount) {
        return arEventMapper.getEventLogWinningSuccessLastIndex(arEventId, winningSort, limitCount, null, null);
    }

    @Transactional
    public void deleteEventLogWinningSuccessLastIndexGreaterThan(int arEventId, int winningSort,long idx) {
        logMapper.deleteEventLogWinningSuccessLastIndexGreaterThan(arEventId, winningSort, idx);
    }

    public int getEventLogWinningSuccessCount(int arEventId, int winningSort) {
        return arEventMapper.getEventLogWinningSuccessCount(arEventId, winningSort, null, null);
    }

    public int getEventLogWinningSuccessCountDay(int arEventId, int winningSort) {
        return arEventMapper.getEventLogWinningSuccessCount(arEventId, winningSort, Integer.parseInt(DateUtils.getNowYYMMDD()), null);
    }

    public int getEventLogWinningSuccessCountHour(int arEventId, int winningSort) {
        return arEventMapper.getEventLogWinningSuccessCount(arEventId, winningSort, Integer.parseInt(DateUtils.getNowYYMMDD()), Integer.parseInt(DateUtils.getNowYYMMDDHH()));
    }

    public List<String> getSurveyEventIdList() {
        return logMapper.selectSurveyEventIdList();
    }
    /**************************************************** SELECT END ****************************************************/
}
