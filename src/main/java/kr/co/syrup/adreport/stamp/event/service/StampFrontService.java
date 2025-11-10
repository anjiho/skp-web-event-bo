package kr.co.syrup.adreport.stamp.event.service;

import kr.co.syrup.adreport.framework.utils.AES256Utils;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.ModelMapperUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.stamp.event.define.StampWinningAttendTypeDefine;
import kr.co.syrup.adreport.stamp.event.model.*;
import kr.co.syrup.adreport.stamp.event.mybatis.mapper.StampFrontMapper;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.*;
import kr.co.syrup.adreport.web.event.dto.request.GiveAwayDeliveryButtonAddInputDto;
import kr.co.syrup.adreport.web.event.dto.response.UserWinningInfoResDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StampFrontService {

    @Autowired
    private AES256Utils aes256Utils;

    @Autowired
    private StampFrontMapper stampFrontMapper;

    @Transactional
    public long saveStampEventGiveAwayDelivery(StampEventGiveAwayDeliveryModel model) {
        return stampFrontMapper.insertStampEventGiveAwayDelivery(model);
    }

    @Cacheable(cacheNames = "findStampPanTrSortByStampPanTrId", keyGenerator = "customKeyGenerator")
    public int findStampPanTrSortByStampPanTrId(Long stpPanTrId) {
        Integer sort = stampFrontMapper.selectStampPanTrSortByStampPanTrId(stpPanTrId);
        if (PredicateUtils.isNull(sort)) {
            return 0;
        }
        return sort;
    }

    @Cacheable(cacheNames = "findStampEventPanTrById", keyGenerator = "customKeyGenerator")
    public StampEventPanTrModel findStampEventPanTrById(Long stpPanTrId) {
        if (PredicateUtils.isNotNull(stpPanTrId)) {
            return stampFrontMapper.selectStampEventPanTrById(stpPanTrId);
        }
        return null;
    }

    @Transactional
    public void updateStampEventGiveAwayDelivery(StampEventGiveAwayDeliveryModel model) {
        try {
            stampFrontMapper.updateStampEventGiveAwayDelivery(model);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void selectInsertStampEventTrLog(String eventId, int arEventWinningId, String attendValue) {
        stampFrontMapper.selectInsertStampEventTrLog(eventId, arEventWinningId, attendValue, Integer.parseInt(DateUtils.getNowYYMMDD()), Integer.parseInt(DateUtils.getNowYYMMDDHH()));
    }

    @Transactional
    public void selectInsertStampEventTrLogAtPid(Long stpPanTrId, String pId, String attendValue) {
        stampFrontMapper.selectInsertStampEventTrLogAtPid(stpPanTrId, pId, attendValue, Integer.parseInt(DateUtils.getNowYYMMDD()), Integer.parseInt(DateUtils.getNowYYMMDDHH()));
    }

    public List<UserWinningInfoResDto> findStampEventGiveAwayDeliveryAtHistory(String eventId, String phoneNumber, String attendCode) {
        List<StampEventGiveAwayDeliveryAtHistoryCheckResVO> list = stampFrontMapper.selectStampEventGiveAwayDeliveryAtHistoryCheck(eventId, phoneNumber, attendCode);
        if (PredicateUtils.isNotNullList(list)) {
            return ModelMapperUtils.convertModelInList(list, UserWinningInfoResDto.class);
        }
        return new ArrayList<UserWinningInfoResDto>();
    }

    @Cacheable(cacheNames = "findEventBaseJoinStampEventMain", keyGenerator = "customKeyGenerator")
    public EventBaseJoinStampEventMainVO findEventBaseJoinStampEventMain(String eventId) {
        return stampFrontMapper.selectEventBaseJoinStampEventMain(eventId);
    }

    @Cacheable(cacheNames = "findStampTrSortAndAttendSortYnByStpTrEventId", keyGenerator = "customKeyGenerator")
    public StampSortAttendSortYnResVO findStampTrSortAndAttendSortYnByStpTrEventId(String stpTrEventId) {
        return stampFrontMapper.selectStampTrSortAndAttendSortYnByStpTrEventId(stpTrEventId);
    }

    @Cacheable(cacheNames = "findFirstEventIdFromStampTrByStpPanId", keyGenerator = "customKeyGenerator")
    public String findFirstEventIdFromStampTrByStpPanId(int stpPanId) {
        return stampFrontMapper.selectFirstEventIdFromStampTrStpPanId(stpPanId);
    }

    public String findFirstEventIdFromStampTrByStpPanIdNoCache(int stpPanId) {
        return stampFrontMapper.selectFirstEventIdFromStampTrStpPanId(stpPanId);
    }

    public List<StampEventGiveAwayDeliveryModel> findStampEventGiveAwayDeliveryByEventId(String eventId, String attendValue, String authCondition){
        return stampFrontMapper.selectStampEventGiveAwayDeliveryByEventId(eventId, attendValue, authCondition);
    }

    public StampEventGiveAwayDeliveryModel findStampEventGiveAwayDeliveryById(long stpGiveAwayId) {
        return stampFrontMapper.selectStampEventGiveAwayDeliveryById(stpGiveAwayId);
    }

    @Cacheable(cacheNames = "findStampEventMainByEventId", keyGenerator = "customKeyGenerator")
    public StampEventMainModel findStampEventMainByEventId(String eventId) {
        return stampFrontMapper.selectStampEventMainByEventId(eventId);
    }

    @Cacheable(cacheNames = "findStampEventMainByEventIdFromWinning", keyGenerator = "customKeyGenerator")
    public StampEventMainModel findStampEventMainByEventIdFromWinning(String eventId) {
        return stampFrontMapper.selectStampEventMainByEventIdFromWinning(eventId);
    }

    @Cacheable(cacheNames = "findStampEventPanByStpId", keyGenerator = "customKeyGenerator")
    public StampEventPanModel findStampEventPanByStpId(int stpId) {
        return stampFrontMapper.selectStampEventPanByStpId(stpId);
    }

    @Transactional
    public void updateStampGiveawayIsReceive(Long stpGiveawayId) {
        stampFrontMapper.updateStampGiveawayIsReceive(stpGiveawayId);
    }

    @Transactional
    public void saveStampEventGiveAwayDeliveryButtonAddList(Long stpGiveAwayId, List<GiveAwayDeliveryButtonAddInputDto>buttonAddInputList) {
        if (PredicateUtils.isNotNullList(buttonAddInputList)) {
            List<StampEventGiveAwayDeliveryButtonAddModel>paramList = ModelMapperUtils.convertModelInList(buttonAddInputList, StampEventGiveAwayDeliveryButtonAddModel.class);
            if (PredicateUtils.isNotNullList(paramList)) {
                paramList.forEach(data -> data.setStpGiveAwayId(stpGiveAwayId));
                try {
                    stampFrontMapper.insertStampEventGiveAwayDeliveryButtonAddList(paramList);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Cacheable(cacheNames = "findNextStampEventPanTrByStpPanIdAndSort", keyGenerator = "customKeyGenerator")
    public StampEventPanTrModel findNextStampEventPanTrByStpPanIdAndSort(int stpPanId, int sort) {
        return stampFrontMapper.selectStampEventPanTrByStpPanIdAnsSort(stpPanId, (sort + 1));
    }

    public StampEventGateCodeModel findStampEventGateCodeByEventIdAndAttendCode(String eventId, String attendCode) {
        return stampFrontMapper.selectStampEventGateCodeByEventIdAndAttendCode(eventId, attendCode);
    }

    public LinkedList<String> findStampEventGateCodeListByEventId(String eventId) {
        return stampFrontMapper.selectStampEventGateCodeList(eventId);
    }

    public long countStampEventGateCodeByEventId(String eventId) {
        return stampFrontMapper.countStampEventGateCodeByEventId(eventId);
    }

    public List<StpPanTrRowNumByWinningVO> findStpPanTrRowNumByWinning(int stpId, String stpAttendAuthCondition, String attendValue) {
        return stampFrontMapper.selectStpPanTrRowNumByWinning(stpId, stpAttendAuthCondition, attendValue);
    }

    @Transactional
    public void usedStampEventGateCode(int stpId, String attendCode) {
        stampFrontMapper.updateStampEventGateCodeIsUseByStpIdAndAttendCode(stpId, attendCode);
    }

    public List<AttendCodeUseVO> findAttendCodeUseListByEventIdAndAttendCode(String eventId, String attendCode) {
        return stampFrontMapper.selectAttendCodeUseListAtStampTr(eventId, attendCode);
    }
}
