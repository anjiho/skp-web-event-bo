package kr.co.syrup.adreport.web.event.mybatis.mapper;

import kr.co.syrup.adreport.web.event.mybatis.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.security.access.method.P;

import java.util.List;
import java.util.Map;

@Mapper
public interface StaticsMapper {

    //페이지접속 개수 누적/기준일
    int selectTotalPageConnectStaticsCount(@Param("eventId") String eventId, @Param("searchDay") String searchDay);

    //AR 참여(참여번호) 개수 누적/기준일
    int selectArAttendStaticsCount(@Param("eventId") String eventId, @Param("searchDay") String searchDay);

    //AR 호출수
    int selectArCallStaticsCount(@Param("eventId") String eventId, @Param("successYn") String successYn, @Param("searchDay") String searchDay);

    //AR 참여버튼 개수 누적/기준일
    int selectAttendButtonStaticsCount(@Param("eventId") String eventId, @Param("searchDay") String searchDay);

    //당첨성공 개수 누적/기준일
    int selectSuccessWinningStaticsCount(@Param("eventId") String eventId, @Param("searchDay") String searchDay);

    //당첨 종류별 개수 누적/기준일
    int selectTotalWinningStaticsCount(@Param("eventId") String eventId, @Param("winningType") String winningType, @Param("searchDay") String searchDay);

    //당첨 종류별 개수 누적/기준일
    int selectWinningStaticsCountByWinningTypeAndEventWinningSort(@Param("eventId") String eventId, @Param("winningType") String winningType, @Param("eventWinningSort") int eventWinningSort, @Param("searchDay") String searchDay);

    int selectWinningStaticsCountByWinningTypeAndEventWinningSortAndGiveAwayStatus(@Param("eventId") String eventId, @Param("eventWinningSort") int eventWinningSort,
                                                                                   @Param("searchDay") String searchDay, @Param("giveAwayStatus") boolean giveAwayStatus);

    //페이지 접속수 시간별 통계
    List<HourlyMapperVO> selectHourlyPageConnectStaticsCount(@Param("eventId") String eventId, @Param("searchDay") String searchDay);

    //페이지 접속수 시간별 통계(개선건)
    List<HourlyMapperVO> selectHourlyPageConnectStaticsCount2(@Param("eventId") String eventId, @Param("searchDay") String searchDay);

    //스탬프 메인 접속수 시간별 통계
    List<HourlyMapperVO> selectStampHourlyPageConnectStaticsCount(@Param("stpId") Integer stpId, @Param("connectType") String connectType, @Param("searchDay") String searchDay);

    //AR 호출수 시간별 통계
    List<HourlyMapperVO> selectHourlyArCallStaticsCount(@Param("eventId") String eventId, @Param("searchDay") String searchDay);

    List<HourlyMapperVO> selectHourlyArCallStaticsCount2(@Param("eventId") String eventId, @Param("searchDay") String searchDay);

    //AR 호출 성공수 시간별 통계
    List<HourlyMapperVO> selectHourlyArCallSuccessStaticsCount(@Param("eventId") String eventId, @Param("searchDay") String searchDay);

    //당첨성공수(type : s), 꽝수(type : f) 시간별 통계
    List<HourlyMapperVO> selectHourlyWinningStaticsCount(@Param("eventId") String eventId, @Param("searchDay") String searchDay, @Param("type") String type, @Param("giveAwayStatus") Boolean giveAwayStatus);

    List<HourlyMapperVO> selectHourlyWinningStaticsCount2(@Param("eventId") String eventId, @Param("searchDay") String searchDay, @Param("type") String type, @Param("giveAwayStatus") Boolean giveAwayStatus);

    List<HourlyMapperVO> selectHourlyWinningStaticsCountByWinningSuccessTable(@Param("eventId") String eventId, @Param("searchDay") String searchDay, @Param("giveAwayStatus") Boolean giveAwayStatus);

    List<HourlyMapperVO> selectStampHourlyWinningStaticsCountByWinningSuccessTable(@Param("stpId") Integer eventId, @Param("searchDay") String searchDay, @Param("giveAwayStatus") Boolean giveAwayStatus);

    //당첨성공수 N
    List<HourlyMapperVO> selectHourlyWinningStaticsCountN(@Param("eventId") String eventId, @Param("searchDay") String searchDay, @Param("type") String type, @Param("giveAwayStatus") Boolean giveAwayStatus, @Param("eventWinningSort") int eventWinningSort);

    //당첨 종류별 시간별 통계
    int selectEventLogWinningCountByArEventWinningId(@Param("eventId") String eventId, @Param("arEventWinningId") int arEventWinningId, @Param("searchDay") String searchDay, @Param("hour") String hour);

    int selectEventLogWinningCount(@Param("eventId") String eventId, @Param("winningType") String winningType, @Param("eventWinningSort") int eventWinningSort,  @Param("searchDay") String searchDay, @Param("hour") String hour);

    //AR 호춣 성공,실패 시간당 개수
    List<HourlyMapperVO> selectEventLogAttendLogByGroupByHour(@Param("eventId") String eventId, @Param("successYn") String successYn, @Param("searchDay") String searchDay);

    List<HourlyMapperVO> selectEventLogAttendLogByGroupByHour2(@Param("eventId") String eventId, @Param("successYn") String successYn, @Param("searchDay") String searchDay);

    List<WinningHourlyMapperVO> selectEventLogWinningGroupByHour(@Param("eventId") String eventId, @Param("eventWinningSort") int eventWinningSort, @Param("searchDay") String searchDay);

    List<WinningHourlyMapperVO> selectStampEventLogWinningGroupByHour(@Param("stpId") Integer stpId, @Param("eventWinningSort") int eventWinningSort, @Param("searchDay") String searchDay);

    List<GiveAwayDeliveryListMapperVO> selectGiveAwayDeliveryList(@Param("eventId") String eventId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    List<GiveAwayDeliveryListMapperVO> selectGiveAwayDeliveryList2(@Param("eventId") String eventId, @Param("startDate") String startDate, @Param("endDate") String endDate,
                                                                   @Param("start") int start, @Param("limitCount") int limitCount);

    List<GiveAwayDeliveryListMapperVO> selectStampGiveAwayDeliveryList(@Param("eventId") String eventId, @Param("startDate") String startDate, @Param("endDate") String endDate,
                                                                   @Param("start") int start, @Param("limitCount") int limitCount);

    int countGiveAwayDelivery(@Param("eventId") String eventId);

    @Select("SELECT count(*) FROM stamp_event_give_away_delivery WHERE event_id = #{eventId}")
    int countStampGiveAwayDelivery(@Param("eventId") String eventId);

    List<String> selectPerformanceStaticsList(@Param("eventId") String eventId, @Param("eventWinningSort") int eventWinningSort, @Param("searchDay") String searchDay);

    int selectSubscriptionPerformanceStatics(@Param("arEventId") int arEventId, @Param("arEventWinningId") int arEventWinningId);

    AccumulateStandardCntMapperVO selectNftRepositoryStatics(@Param("eventId") String eventId, @Param("arEventWinningId") int arEventWinningId,
                                                             @Param("searchDay") String searchDay, @Param("searchType") String searchType);

    List<SurveyConnectionStaticsMapperVO> selectSurveyConnectionStaticsGroupByGender(@Param("eventId") String eventId, @Param("searchDay") String searchDay);

    List<SurveyConnectionStaticsMapperVO> selectSurveyConnectionStaticsGroupByAge(@Param("eventId") String eventId, @Param("searchDay") String searchDay);

    SurveyConnectionStaticsMapperVO selectSurveyConnectionStaticsGroupByAge2(@Param("eventId") String eventId, @Param("age") int age, @Param("searchDay") String searchDay, @Param("isSubmit") Boolean isSubmit);

    String selectSurveySuccessSubmit(@Param("eventId") String eventId, @Param("searchDay") String searchDay);

    List<CommonLogPvMapperVO> selectCommonLogPvStatics(@Param("eventName") String eventName, @Param("logKey") String logKey, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select(" SELECT agree_id, created_date, phone_number FROM common_log_person_agree WHERE event_name = #{eventName} ORDER BY created_date ASC ")
    List<CommonLogPersonAgreeMapperVO> selectCommonLogPersonAgreeListByEventName(@Param("eventName") String eventName);

    SurveyConnectionStaticsMapperVO selectSurveyLogByGender(@Param("eventId") String eventId, @Param("gender") String gender, @Param("searchDay") String searchDay, @Param("isSubmit") Boolean isSubmit);

    SurveyConnectionStaticsMapperVO selectSurveyLogByAge(@Param("eventId") String eventId, @Param("age") int age, @Param("searchDay") String searchDay, @Param("isSubmit") Boolean isSubmit);

    PhotoPrintResultMapperVO selectPhotoPrintResultStatics(@Param("eventId") String eventId, @Param("searchDate") String searchDate);

    PhotoBoxStaticsMapperVO selectPhotoBoxStatics(@Param("eventId") String eventId, @Param("searchDate") String searchDate);

    String selectStampTrStatics(@Param("stpId") Integer stpId, @Param("stpPanTrId") Long strPanTrId, @Param("searchDate") String searchDate);

    List<EventGiveAwayDeliveryButtonAddMapperVO> selectWinningButtonAddFileNameListByEventId(@Param("eventId") String eventId);

    List<EventGiveAwayDeliveryButtonAddMapperVO> selectEventGiveAwayDeliveryButtonAddByGiveAwayId(@Param("giveAwayId") int giveAwayId);

    List<EventGiveAwayDeliveryButtonAddMapperVO> selectStampEventGiveAwayDeliveryButtonAddByStpGiveAwayId(@Param("stpGiveAwayId") long stpGiveAwayId);

    List<String> selectStampPerformanceStaticsList(@Param("stpId") Integer stpId, @Param("eventWinningSort") int eventWinningSort, @Param("searchDay") String searchDay);

    int selectTotalPageStampConnectStaticsCount(@Param("stpId") Integer stpId, @Param("connectType") String connectType, @Param("searchDay") String searchDay);

    void saveAnswerList(@Param("titleList") List<Object>titleList, @Param("answerList") List<List<String>>answerList);

    List<Map<String, String>> selectAnswerStaticsListByEventId(@Param("eventId") String eventId, @Param("start") Integer start, @Param("limit") Integer limit);

}
