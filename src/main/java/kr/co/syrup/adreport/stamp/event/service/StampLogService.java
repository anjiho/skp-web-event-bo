package kr.co.syrup.adreport.stamp.event.service;

import kr.co.syrup.adreport.framework.utils.AES256Utils;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.stamp.event.define.StampWinningAttendTypeDefine;
import kr.co.syrup.adreport.stamp.event.model.*;
import kr.co.syrup.adreport.stamp.event.mybatis.mapper.StampFrontMapper;
import kr.co.syrup.adreport.stamp.event.mybatis.mapper.StampLogMapper;
import kr.co.syrup.adreport.stamp.event.mybatis.mapper.StampSodarMapper;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.StampAccWinningProductResVO;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.StampEventLogTrVO;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.StampTrAccYnResVO;
import kr.co.syrup.adreport.web.event.define.EventLogWinningLimitDefine;
import kr.co.syrup.adreport.web.event.entity.ArEventWinningEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class StampLogService {

    @Autowired
    private StampLogMapper stampLogMapper;

    @Autowired
    private StampFrontMapper stampFrontMapper;

    @Autowired
    private StampSodarMapper stampSodarMapper;

    @Autowired
    private AES256Utils aes256Utils;

    @Transactional
    public void deleteStampEventLogWinningLimitByStpIdAndCode(int stpId, String code, String desc) {
        stampLogMapper.deleteStampEventLogWinningLimitByStpIdAndCodeAndDesc(stpId, code, desc);
    }

    /**
     * stamp_event_log_winning_exchange 로그 저장하기
     * @param winningEntity
     * @param attendType
     * @param attendValue
     */
    @Transactional
    public Long saveStampEventLogWinning(int stpId, long stpPanTrId, String attendType, String attendValue, String stampWinningType, ArEventWinningEntity winningEntity) {
        StampEventLogWinningModel model = new StampEventLogWinningModel().builder()
                .stpId(stpId)
                .stpPanTrId(stpPanTrId)
                .arEventWinningId(winningEntity.getArEventWinningId())
                .eventWinningSort(winningEntity.getEventWinningSort())
                .winningType(winningEntity.getWinningType())
                .attendType(attendType)
                .attendValue(attendValue)
                .stpWinningType(stampWinningType)
                .build();

        //성공일때
        stampLogMapper.insertStampEventLogWinningSuccess(model);
        return model.getId();
    }

    public List<StampEventLogWinningLimitModel> findStampEventLogWinningLimitListByStpId(int stpId) {
        return stampLogMapper.selectStampEventLogWinningLimitByStpId(stpId);
    }

    public int findCountStampEventLogWinning(StampEventLogWinningModel winningModel) {
        return stampLogMapper.countStampEventLogWinning(winningModel);
    }

    public boolean isStampEventLogWinningLimitByStpIdAndCode(int stpId, String code, String codeDesc) {
        int cnt = stampLogMapper.countStampEventLogWinningLimitByStpIdCode(stpId, code, codeDesc);
        if (cnt > 0) {
            return true;
        }
        return false;
    }

    @Transactional
    public void saveStampEventLogWinningLimit(int stpId, int secondIdx, String createdDay, String createdHour, String attendValue, String limitType) {
        String code = "";
        if (PredicateUtils.isNotNull(attendValue)) {
            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_CODE.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(stpId), attendValue);
            }
            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_CODE_TODAY.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(stpId), attendValue, createdDay);
            }
            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_WINNINGID_CODE.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(stpId), String.valueOf(secondIdx), attendValue);
            }
            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_WINNINGID_CODE_TODAY.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(stpId), String.valueOf(secondIdx), attendValue, createdDay);
            }

            //=========================================== 참여번호형 - 전화번호일때 ==============================================================
            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_MDN.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(stpId), attendValue);
            }

            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_MDN_TODAY.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(stpId), attendValue, createdDay);
            }

            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_WINNINGID_MDN.name())) {
                if (attendValue.length() > 20) {
                    attendValue = aes256Utils.decrypt(attendValue);
                }
                code = StringTools.joinStringsNoSeparator(String.valueOf(stpId), String.valueOf(secondIdx), attendValue);
            }

            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_WINNINGID_MDN_TODAY.name())) {
                if (attendValue.length() > 20) {
                    attendValue = aes256Utils.decrypt(attendValue);
                }
                code = StringTools.joinStringsNoSeparator(String.valueOf(stpId), String.valueOf(secondIdx), attendValue, createdDay);
            }
            //=========================================== 참여번호형 - 전화번호일때 ==============================================================

        } else {
            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_SORT_TODAY_HOUR.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(stpId), String.valueOf(secondIdx), createdDay, createdHour);
            }
            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_SORT_TODAY.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(stpId), String.valueOf(secondIdx), createdDay);
            }
            if (PredicateUtils.isEqualsStr(limitType, EventLogWinningLimitDefine.ID_SORT.name())) {
                code = StringTools.joinStringsNoSeparator(String.valueOf(stpId), String.valueOf(secondIdx));
            }
        }
        boolean isExitsCode = this.isExitsEventLogWinningLimit(stpId, code);
        log.debug("isExitsCode >> " + isExitsCode);
        if (!isExitsCode) {
            stampLogMapper.saveStampEventLogWinningLimit(stpId, code, limitType);
        }
    }

    private boolean isExitsEventLogWinningLimit(int stpId, String code) {
        String savedCode  = stampLogMapper.selectStampEventLogWinningLimitByArEventIdAndCode(stpId, code);
        if (PredicateUtils.isNull(savedCode) || StringUtils.isEmpty(savedCode)) {
            return false;
        }
        return true;
    }

    public int getTotalStampEventLogWinningCountByStpIdEqualsAndEventWinningSortEqualsAndIdIsLessThan(int stpId, int eventWinningSort, long id) {
        return stampLogMapper.selectCountStampEventLogWinningAtConcurrency(stpId, eventWinningSort, id, null, null);
    }

    public int getDayStampEventLogWinningCountByStpIdEqualsAndEventWinningSortEqualsAndIdIsLessThan(int stpId, int eventWinningSort, long id) {
        return stampLogMapper.selectCountStampEventLogWinningAtConcurrency(stpId, eventWinningSort, id, DateUtils.getNowYYMMDD(), null);
    }

    public int getHourStampEventLogWinningCountByStpIdEqualsAndEventWinningSortEqualsAndIdIsLessThan(int stpId, int eventWinningSort, long id) {
        return stampLogMapper.selectCountStampEventLogWinningAtConcurrency(stpId, eventWinningSort, id, null, DateUtils.getNowYYMMDDHH());
    }

    @Transactional
    public void deleteStampEventWinningSuccessById(long id) {
        stampLogMapper.deleteStampEventWinningLogSuccessById(id);
    }

    public boolean getIsSavedStampEventLogWinningSuccessByIdx(int stpId, long eventLogWinningId) {
        int cnt = stampLogMapper.selectCountStampEventWinningLogSuccessByEventLogWinningId(stpId, eventLogWinningId);
        if (PredicateUtils.isEqualZero(cnt)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean getIsPayedGifticon(String eventId, int arEventWinningId, String phoneNumber) {
        int cnt = stampLogMapper.selectCountStampGiveAwayDeliveryPayedGificon(eventId, arEventWinningId, phoneNumber);
        if (cnt == 0) {
            return false;
        }
        return true;
    }

    public int getCountStampGiveAwayDeliveryByEventIdAndAuthConditionAndSearchValueIsToday(String eventId, String authCondition, String searchValue, boolean isToday) {
        return stampLogMapper.selectCountStampGiveAwayDeliveryByEventIdAndSearchValueAndIsToady(eventId, authCondition, searchValue, isToday);
    }

    public int getStampEventLogWinningSuccessCountTotal(int stpId, int winningSort) {
        return stampLogMapper.selectStampEventLogWinningSuccessCount(stpId, winningSort, null, null);
    }

    public int getStampEventLogWinningSuccessCountDay(int stpId, int winningSort) {
        return stampLogMapper.selectStampEventLogWinningSuccessCount(stpId, winningSort, Integer.parseInt(DateUtils.getNowYYMMDD()), null);
    }

    public int getStampEventLogWinningSuccessCountHour(int stpId, int winningSort) {
        return stampLogMapper.selectStampEventLogWinningSuccessCount(stpId, winningSort, Integer.parseInt(DateUtils.getNowYYMMDD()), Integer.parseInt(DateUtils.getNowYYMMDDHH()));
    }

    public Long getStampEventLogWinningSuccessLastIndexHour(int stpId, int winningSort, int limitCount) {
        return stampLogMapper.selectStampEventLogWinningSuccessLastIndex(stpId, winningSort, limitCount, Integer.parseInt(DateUtils.getNowYYMMDD()), Integer.parseInt(DateUtils.getNowYYMMDDHH()));
    }

    public Long getStampEventLogWinningSuccessLastIndexDay(int stpId, int winningSort, int limitCount) {
        return stampLogMapper.selectStampEventLogWinningSuccessLastIndex(stpId, winningSort, limitCount, Integer.parseInt(DateUtils.getNowYYMMDD()), null);
    }

    public Long getStampEventLogWinningSuccessLastIndex(int stpId, int winningSort, int limitCount) {
        return stampLogMapper.selectStampEventLogWinningSuccessLastIndex(stpId, winningSort, limitCount, null, null);
    }

    @Transactional
    public void deleteStampEventLogWinningSuccessLastIndexGreaterThan(int stpId, int winningSort,long idx) {
        stampLogMapper.deleteStampEventLogWinningSuccessLastIndexGreaterThan(stpId, winningSort, idx);
    }

    public List<String> getStampAccUserListByEventId(String eventId) {
        return stampLogMapper.selectStampAccAttendValueGroupBy(eventId);
    }

    public List<StampTrAccYnResVO> getStampTrAccYnByAttendValue(int stpId, String attendValue) {
        return stampLogMapper.selectStampTrAccYn(stpId, attendValue);
    }

    public List<StampAccWinningProductResVO> getCountStampWinningProduct(int stpId, String attendAuthCondition, String attendValue) {
        return stampLogMapper.selectCountStampWinningProduct(stpId, attendAuthCondition, attendValue);
    }

    public List<StampTrAccYnResVO> getUnionAllStampTrAndWinningDelivery(int stpId, String attendAuthCondition, String attendValue) {
        return stampLogMapper.selectUnionAllStampTrAndWinningDelivery(stpId, attendAuthCondition, attendValue);
    }

    @Transactional
    public void updateStampEventLogTrIsClick(long id, boolean isClick) {
        stampLogMapper.updateStampEventLogTrIsClickById(id, isClick);
    }

    public Integer findStampTrLogLastSortByStpIdAndAttendValue(int stpId, String attendType, String attendValue) {
        if (PredicateUtils.isEqualsStr(attendType, StampWinningAttendTypeDefine.MDN.name())) {
            attendValue = aes256Utils.encrypt(attendValue);
        }
        return stampLogMapper.selectStampTrLogLastSortByStpId(stpId, attendType, attendValue);
    }

    public List<StampEventLogTrVO> findStampTrLogByStpPanId(String stpAttendSortSettingYn, int stpPanId, String attendType, String attendValue) {
        return stampLogMapper.selectStampTrLogByStpPanId(stpAttendSortSettingYn, stpPanId, attendType, attendValue);
    }

    @Transactional
    public void deleteStampEventGiveAwayByStpId(int stpId) {
        stampFrontMapper.deleteStampEventGiveAwayDeliveryByStpId(stpId);
    }

    @Transactional
    public void deleteStampEventGiveAwayButtonAddByStpId(int stpId) {
        List<Long> stpGiveAwayIdList = stampFrontMapper.selectStpGiveAwayIdListByStpId(stpId);
        if (PredicateUtils.isNotNullList(stpGiveAwayIdList)) {
            stampFrontMapper.deleteStampEventGiveAwayDeliveryButtonAddInStpGiveAwayIds(stpGiveAwayIdList);
        }
    }

    @Transactional
    public void deleteStampEventLogConnectByStpId(int stpId) {
        stampLogMapper.deleteStampEventLogConnectByStpId(stpId);
    }

    @Transactional
    public void deleteStampEventLogPvByStpId(int stpId) {
        stampLogMapper.deleteStampEventLogPvByStpId(stpId);
    }

    @Transactional
    public void deleteStampEventLogTrByStpId(int stpId) {
        stampLogMapper.deleteStampEventLogTrByStpId(stpId);
    }

    @Transactional
    public void deleteStampEventLogWinningLimitByStpId(int stpId) {
        stampLogMapper.deleteStampEventLogWinningLimitByStpId(stpId);
    }

    @Transactional
    public void deleteStampEventLogWinningSuccessByStpId(int stpId) {
        stampLogMapper.deleteStampEventLogWinningSuccessByStpId(stpId);
    }


}
