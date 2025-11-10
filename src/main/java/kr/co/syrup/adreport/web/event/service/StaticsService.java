package kr.co.syrup.adreport.web.event.service;

import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.web.event.mybatis.mapper.StaticsMapper;
import kr.co.syrup.adreport.web.event.mybatis.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StaticsService {

    @Autowired
    private StaticsMapper staticsMapper;

    /**
     * 페이지접속 개수 누적/기준일 가져오기
     * @param eventId
     * @param searchDay
     * @return
     */
    public int selectTotalPageConnectStaticsCount(String eventId, String searchDay) {
        return staticsMapper.selectTotalPageConnectStaticsCount(eventId, searchDay);
    }

    public int selectTotalPageStampConnectStaticsCount(int stpId, String connectType, String searchDay) {
        return staticsMapper.selectTotalPageStampConnectStaticsCount(stpId, connectType, searchDay);
    }

    public List<String> selectPerformanceStaticsList(String eventId, int eventWinningSort, String searchDay) {
        return staticsMapper.selectPerformanceStaticsList(eventId, eventWinningSort, searchDay);
    }

    public List<String> selectStampPerformanceStaticsList(int stpId, int eventWinningSort, String searchDay) {
        return staticsMapper.selectStampPerformanceStaticsList(stpId, eventWinningSort, searchDay);
    }

    public int selectSubscriptionPerformanceStaticsCount(int arEventId, int arEventWinningId) {
        if (arEventId > 0) {
            return staticsMapper.selectSubscriptionPerformanceStatics(arEventId, arEventWinningId);
        }
        return 0;
    }

    public List<HourlyMapperVO> selectHourlyPageConnectStaticsCount2(String eventId, String searchDay) {
        return staticsMapper.selectHourlyPageConnectStaticsCount2(eventId, searchDay);
    }

    public List<HourlyMapperVO> findStampHourlyPageConnectStaticsCount(Integer stpId, String connectType, String searchDay) {
        return staticsMapper.selectStampHourlyPageConnectStaticsCount(stpId, connectType, searchDay);
    }

    public List<HourlyMapperVO> selectHourlyArCallStaticsCount2(String eventId, String searchDay) {
        return staticsMapper.selectHourlyArCallStaticsCount2(eventId, searchDay);
    }

    public List<HourlyMapperVO> selectHourlyWinningStaticsCount2(String eventId, String searchDay, String type, Boolean giveAwayStatus) {
        return staticsMapper.selectHourlyWinningStaticsCount2(eventId, searchDay, type, giveAwayStatus);
    }

    public List<HourlyMapperVO> selectHourlyWinningStaticsCountByWinningSuccessTable(String eventId, String searchDay, Boolean giveAwayStatus) {
        return staticsMapper.selectHourlyWinningStaticsCountByWinningSuccessTable(eventId, searchDay, giveAwayStatus);
    }

    public List<HourlyMapperVO> selectStampHourlyWinningStaticsCountByWinningSuccessTable(int stpId, String searchDay, Boolean giveAwayStatus) {
        return staticsMapper.selectStampHourlyWinningStaticsCountByWinningSuccessTable(stpId, searchDay, giveAwayStatus);
    }

    /**
     * AR 호출수 가져오기
     * @param eventId
     * @param successYn
     * @param searchDay
     * @return
     */
    public int selectArCallStaticsCount(String eventId, String successYn, String searchDay) {
        return staticsMapper.selectArCallStaticsCount(eventId, successYn, searchDay);
    }

    public List<HourlyMapperVO> selectEventLogAttendLogByGroupByHour2(String eventId, String successYn, String searchDay) {
        return staticsMapper.selectEventLogAttendLogByGroupByHour2(eventId, successYn, searchDay);
    }

    public List<WinningHourlyMapperVO> selectEventLogWinningGroupByHour(String eventId, int eventWinningSort, String searchDay) {
        return staticsMapper.selectEventLogWinningGroupByHour(eventId, eventWinningSort, searchDay);
    }

    public List<WinningHourlyMapperVO> selectStampEventLogWinningGroupByHour(int stpId, int eventWinningSort, String searchDay) {
        return staticsMapper.selectStampEventLogWinningGroupByHour(stpId, eventWinningSort, searchDay);
    }

    public List<GiveAwayDeliveryListMapperVO> selectGiveAwayDeliveryList(String eventId, String startDate, String endDate) {
        return staticsMapper.selectGiveAwayDeliveryList(eventId, startDate, endDate);
    }

    public List<GiveAwayDeliveryListMapperVO> selectGiveAwayDeliveryLimitList(String eventId, String startDate, String endDate, int start, int limitCount) {
        if (StringTools.containsIgnoreCase(eventId, "S")) {
            return staticsMapper.selectStampGiveAwayDeliveryList(eventId, startDate, endDate, start, limitCount);
        } else {
            return staticsMapper.selectGiveAwayDeliveryList2(eventId, startDate, endDate, start, limitCount);
        }
    }

    public int countGiveAwayDelivery(String eventId) {
        if (StringTools.containsIgnoreCase(eventId, "S")) {
            return staticsMapper.countStampGiveAwayDelivery(eventId);
        } else {
            return staticsMapper.countGiveAwayDelivery(eventId);
        }
    }

    public AccumulateStandardCntMapperVO selectNftRepositoryStatics(String eventId, int arEventWinningId, String searchDay, String searchType) {
        return staticsMapper.selectNftRepositoryStatics(eventId, arEventWinningId, searchDay, searchType);
    }

    public List<SurveyConnectionStaticsMapperVO> selectSurveyConnectionGenderStaticsList(String eventId, String searchDay) {
        return staticsMapper.selectSurveyConnectionStaticsGroupByGender(eventId, searchDay);
    }

    public List<SurveyConnectionStaticsMapperVO> selectSurveyConnectionAgeStaticsList(String eventId, String searchDay) {
        return staticsMapper.selectSurveyConnectionStaticsGroupByAge(eventId, searchDay);
    }

    public SurveyConnectionStaticsMapperVO selectSurveyConnectionAgeStatics(String eventId, int age, String searchDay, Boolean isSubmit) {
        return staticsMapper.selectSurveyConnectionStaticsGroupByAge2(eventId, age, searchDay, isSubmit);
    }

    public List<CommonLogPvMapperVO> selectCommonLogPvList(String eventName, String logKey, String startDate, String endDate) {
        return staticsMapper.selectCommonLogPvStatics(eventName, logKey, startDate, endDate);
    }

    public List<CommonLogPersonAgreeMapperVO> findCommonLogPersonAgreeListByEventName(String eventName) {
        if (PredicateUtils.isNotNull(eventName)) {
            return staticsMapper.selectCommonLogPersonAgreeListByEventName(eventName);
        }
        return null;
    }

    public SurveyConnectionStaticsMapperVO selectSurveyLogByGender(String eventId, String gender, String searchDay, Boolean isSubmit) {
        SurveyConnectionStaticsMapperVO vo = staticsMapper.selectSurveyLogByGender(eventId, gender, searchDay, isSubmit);
        vo.setGender(gender);
        return vo;
    }

    public PhotoPrintResultMapperVO selectPhotoPrintResultStatics(String eventId, String searchDate) {
        return staticsMapper.selectPhotoPrintResultStatics(eventId, searchDate);
    }

    public PhotoBoxStaticsMapperVO selectPhotoBoxStatics(String eventId, String searchDate) {
        return staticsMapper.selectPhotoBoxStatics(eventId, searchDate);
    }

    public String selectStampTrStatics(int stpId, Long stpPanTrId, String searchDate) {
        return staticsMapper.selectStampTrStatics(stpId, stpPanTrId, searchDate);
    }

    public List<EventGiveAwayDeliveryButtonAddMapperVO> findWinningButtonAddFileNameListByEventId(String eventId) {
        return staticsMapper.selectWinningButtonAddFileNameListByEventId(eventId);
    }

    public List<EventGiveAwayDeliveryButtonAddMapperVO> findEventGiveAwayDeliveryButtonAddByGiveAwayId(int giveAwayId) {
        return staticsMapper.selectEventGiveAwayDeliveryButtonAddByGiveAwayId(giveAwayId);
    }

    public List<EventGiveAwayDeliveryButtonAddMapperVO> findStampEventGiveAwayDeliveryButtonAddByStpGiveAwayId(Long stpGiveAwayId) {
        return staticsMapper.selectStampEventGiveAwayDeliveryButtonAddByStpGiveAwayId(stpGiveAwayId);
    }
}
