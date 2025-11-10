package kr.co.syrup.adreport.stamp.event.mybatis.mapper;

import kr.co.syrup.adreport.stamp.event.model.StampEventLogWinningLimitModel;
import kr.co.syrup.adreport.stamp.event.model.StampEventLogWinningModel;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.StampAccWinningProductResVO;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.StampEventLogTrVO;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.StampTrAccYnResVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StampLogMapper {

    @Delete(" DELETE FROM stamp_event_log_winning_limit WHERE stp_id = #{stpId}  AND code LIKE CONCAT(#{code}, '%') AND code_desc = #{desc} ")
    void deleteStampEventLogWinningLimitByStpIdAndCodeAndDesc(@Param("stpId") int stpId, @Param("code") String code, @Param("desc") String desc);

    void insertStampEventLogWinning(StampEventLogWinningModel model);

    void insertStampEventLogWinningSuccess(StampEventLogWinningModel model);

    @Select(" SELECT distinct code FROM stamp_event_log_winning_limit WHERE 1=1 AND stp_id = #{stpId} ")
    List<StampEventLogWinningLimitModel> selectStampEventLogWinningLimitByStpId(@Param("stpId") int stpId);

    int countStampEventLogWinning(StampEventLogWinningModel logWinningModel);

    @Select(" SELECT count(*) FROM stamp_event_log_winning_limit WHERE stp_id = #{stpId} AND code = #{code} AND code_desc = #{codeDesc}")
    int countStampEventLogWinningLimitByStpIdCode(@Param("stpId") int stpId, @Param("code") String code, @Param("codeDesc") String codeDesc);

    @Select("SELECT code FROM stamp_event_log_winning_limit WHERE 1=1 AND stp_id = #{stpId} AND code = #{code}")
    String selectStampEventLogWinningLimitByArEventIdAndCode(@Param("stpId") int stpId, @Param("code") String code);

    void saveStampEventLogWinningLimit(@Param("stpId") int stpId, @Param("code") String code, @Param("codeDesc") String desc);

    int selectCountStampEventLogWinningAtConcurrency(@Param("stpId") int stpId, @Param("eventWiningSort") int eventWinningSort, @Param("id") long id, @Param("createdDayImpr") String createdDayImpr, @Param("createdHourImpr") String createdHourImpr);

    void updateStampEventWinningLogFail(@Param("id") long id);

    @Delete("DELETE FROM stamp_event_log_winning_success WHERE id = #{id}")
    void deleteStampEventWinningLogSuccessById(@Param("id") long id);

    int selectCountStampEventWinningLogSuccessByEventLogWinningId(@Param("stpId") int stpId, @Param("eventLogWinningId") long eventLogWinningId);

    int selectCountStampGiveAwayDeliveryPayedGificon(@Param("eventId") String eventId, @Param("arEventWinningId") int arEventWinningId, @Param("phoneNumber") String phoneNumber);

    int selectCountStampGiveAwayDeliveryByEventIdAndSearchValueAndIsToady(@Param("eventId") String eventId, @Param("authCondition") String authCondition, @Param("searchValue") String searchValue, @Param("isToday") boolean isToday);

    Long selectStampEventLogWinningSuccessLastIndex(@Param("stpId") int stpId, @Param("winningSort") int winningSort, @Param("limitCount") int limitCount, @Param("createdDay") Integer createdDay, @Param("createdHour") Integer createdHour);

    Integer selectStampEventLogWinningSuccessCount(@Param("stpId") int stpId, @Param("winningSort") int winningSort, @Param("createdDay") Integer createdDay, @Param("createdHour") Integer createdHour);

    @Delete("DELETE FROM stamp_event_log_winning_success WHERE stp_id = #{stpId} AND event_winning_sort = #{winningSort} AND id > #{idx}")
    void deleteStampEventLogWinningSuccessLastIndexGreaterThan(@Param("stpId") int stpId, @Param("winningSort") int winningSort, @Param("idx") long idx);

    void insertStampEventLogConnect(@Param("stpId") int stpId, @Param("connectType") String connectType);

    List<String>selectStampAccAttendValueGroupBy(@Param("eventId") String eventId);

    List<StampTrAccYnResVO> selectStampTrAccYn(@Param("stpId") int stpId, @Param("attendValue") String attendValue);

    StampEventLogWinningModel selectStampEventLogWinningSuccessByStpPanTrId(@Param("stpPanTrId") long stpPanTrId, @Param("attendValue") String attendValue);

    int selectCountStampEventGiveAwayDeliveryByWinningId(@Param("stpId") int stpId, @Param("stpPanTrId") long stpPanTrId, @Param("attendAuthCondition") String attendAuthCondition, @Param("attendValue") String attendValue);

    List<StampAccWinningProductResVO> selectCountStampWinningProduct(@Param("stpId") int stpId, @Param("attendAuthCondition") String attendAuthCondition, @Param("attendValue") String attendValue);

    List<StampTrAccYnResVO> selectUnionAllStampTrAndWinningDelivery(@Param("stpId") int stpId, @Param("attendAuthCondition") String attendAuthCondition, @Param("attendValue") String attendValue);

    @Update("UPDATE stamp_event_log_tr SET is_click = #{isClick} WHERE id = #{id} ")
    void updateStampEventLogTrIsClickById(@Param("id") long id, @Param("isClick") boolean isClick);

    Integer selectStampTrLogLastSortByStpId(@Param("stpId") int stpId, @Param("attendType") String attendType, @Param("attendValue") String attendValue);

    List<StampEventLogTrVO> selectStampTrLogByStpPanId(@Param("stpAttendSortSettingYn") String stpAttendSortSettingYn, @Param("stpPanId") int stpPanId, @Param("attendType") String attendType, @Param("attendValue") String attendValue);

    @Delete(" DELETE FROM stamp_event_log_connect WHERE stp_id = #{stpId} ")
    void deleteStampEventLogConnectByStpId(@Param("stpId") int stpId);

    @Delete(" DELETE FROM stamp_event_log_pv WHERE stp_id = #{stpId} ")
    void deleteStampEventLogPvByStpId(@Param("stpId") int stpId);

    @Delete(" DELETE FROM stamp_event_log_tr WHERE stp_id = #{stpId} ")
    void deleteStampEventLogTrByStpId(@Param("stpId") int stpId);

    @Delete(" DELETE FROM stamp_event_log_winning_limit WHERE stp_id = #{stpId} ")
    void deleteStampEventLogWinningLimitByStpId(@Param("stpId") int stpId);

    @Delete(" DELETE FROM stamp_event_log_winning_success WHERE stp_id = #{stpId} ")
    void deleteStampEventLogWinningSuccessByStpId(@Param("stpId") int stpId);
}
