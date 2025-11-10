package kr.co.syrup.adreport.stamp.event.mybatis.mapper;

import kr.co.syrup.adreport.stamp.event.model.*;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.*;
import kr.co.syrup.adreport.web.event.entity.ArEventWinningEntity;
import org.apache.ibatis.annotations.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Mapper
public interface StampFrontMapper {

    StampEventPanModel selectStpIdStpPanIdByStpPanTrId(@Param("stpPanTrId") Long stpPanTrId);

    ArEventWinningEntity selectMappingArEventWinningByStpPanTrId(@Param("stpPanTrId") Long stpPanTrId);

    Long insertStampEventGiveAwayDelivery(StampEventGiveAwayDeliveryModel model);

    Integer selectStampPanTrSortByStampPanTrId(@Param("stpPanTrId") Long stpPanTrId);

    @Select(" SELECT * FROM stamp_event_pan_tr WHERE stp_pan_tr_id = #{stpPanTrId} ")
    StampEventPanTrModel selectStampEventPanTrById(@Param("stpPanTrId") Long stpPanTrId);

    void updateStampEventGiveAwayDelivery(StampEventGiveAwayDeliveryModel model);

    void selectInsertStampEventTrLog(@Param("eventId") String eventId, @Param("arEventWinningId") int arEventWinningId, @Param("attendValue") String attendValue, @Param("createdDayImpr") Integer createdDayImpr, @Param("createdHourImpr") Integer createdHourImpr);

    void selectInsertStampEventTrLogAtPid(@Param("stpPanTrId") long stpPanTrId, @Param("stpTrPid") String stpTrPid, @Param("attendValue") String attendValue, @Param("createdDayImpr") Integer createdDayImpr, @Param("createdHourImpr") Integer createdHourImpr);

    List<String> selectStpTrEventIdListByEventId(@Param("eventId") String eventId);

    List<StampEventGiveAwayDeliveryAtHistoryCheckResVO> selectStampEventGiveAwayDeliveryAtHistoryCheck(@Param("eventId") String eventId, @Param("phoneNumber") String phoneNumber, @Param("attendCode") String attendCode);

    EventBaseJoinStampEventMainVO selectEventBaseJoinStampEventMain(@Param("eventId") String eventId);

    StampSortAttendSortYnResVO selectStampTrSortAndAttendSortYnByStpTrEventId(@Param("stpTrEventId") String stpTrEventId);

    @Select(" SELECT stp_tr_event_id FROM stamp_event_pan_tr WHERE stp_pan_id = #{stpPanId} ORDER BY stp_tr_sort ASC LIMIT 1 ")
    String selectFirstEventIdFromStampTrStpPanId(@Param("stpPanId") int stpPanId);

    List<StampEventGiveAwayDeliveryModel> selectStampEventGiveAwayDeliveryByEventId(@Param("eventId") String eventId, @Param("attendValue") String attendValue, @Param("authCondition") String authCondition);

    @Select(" SELECT * FROM stamp_event_give_away_delivery WHERE stp_give_away_id = #{stpGiveAwayId} ")
    StampEventGiveAwayDeliveryModel selectStampEventGiveAwayDeliveryById(@Param("stpGiveAwayId") long stpGiveAwayId);

    @Select(" SELECT * FROM stamp_event_main WHERE event_id = #{eventId} ")
    StampEventMainModel selectStampEventMainByEventId(@Param("eventId") String eventId);

    @Select(" SELECT stp_id, stp_winning_type, stp_attend_auth_condition, duplicate_winning_type, duplicate_winning_limit_type, duplicate_winning_count, stp_attend_sort_setting_yn FROM stamp_event_main WHERE event_id = #{eventId} ")
    StampEventMainModel selectStampEventMainByEventIdFromWinning(@Param("eventId") String eventId);

    @Select(" SELECT * FROM stamp_event_pan WHERE stp_id = #{stpId} ")
    StampEventPanModel selectStampEventPanByStpId(@Param("stpId") int stpId);

    @Update(" UPDATE stamp_event_give_away_delivery SET is_receive = 1 WHERE stp_give_away_id = #{stpGiveawayId} ")
    void updateStampGiveawayIsReceive(@Param("stpGiveawayId") long stpGiveawayId);

    void insertStampEventGiveAwayDeliveryButtonAddList(List<StampEventGiveAwayDeliveryButtonAddModel>list);

    @Select(" SELECT * FROM stamp_event_pan_tr WHERE stp_pan_id = #{stpPanId} AND stp_tr_sort = #{sort} ")
    StampEventPanTrModel selectStampEventPanTrByStpPanIdAnsSort(@Param("stpPanId") int stpPanId, @Param("sort") int sort);

    @Select(" SELECT * FROM stamp_event_gate_code WHERE stp_id = (SELECT stp_id FROM stamp_event_main WHERE event_id = #{eventId}) AND attend_code = #{attendCode} ")
    StampEventGateCodeModel selectStampEventGateCodeByEventIdAndAttendCode(@Param("eventId") String eventId, @Param("attendCode") String attendCode);

    @Select(" SELECT attend_code FROM stamp_event_gate_code WHERE stp_id = ( SELECT stp_id FROM stamp_event_main WHERE event_id = #{eventId} ) ")
    LinkedList<String> selectStampEventGateCodeList(@Param("eventId") String eventId);

    @Select(" SELECT count(*) FROM stamp_event_gate_code WHERE stp_id = ( SELECT stp_id FROM stamp_event_main WHERE event_id = #{eventId} ) ")
    long countStampEventGateCodeByEventId(@Param("eventId") String eventId);

    @Select(" SELECT stp_give_away_id FROM stamp_event_give_away_delivery where stp_id = #{stpId} ")
    List<Long> selectStpGiveAwayIdListByStpId(@Param("stpId") int stpId);

    @Delete("delete from stamp_event_give_away_delivery where stp_id = #{stpId}")
    void deleteStampEventGiveAwayDeliveryByStpId(@Param("stpId") int stpId);

    void deleteStampEventGiveAwayDeliveryButtonAddInStpGiveAwayIds(@Param("stpGiveAwayIdList") List<Long>stpGiveAwayIdList);

    List<StpPanTrRowNumByWinningVO> selectStpPanTrRowNumByWinning(@Param("stpId") int stpId, @Param("stpAttendAuthCondition") String stpAttendAuthCondition, @Param("attendValue") String attendValue);

    @Update(" UPDATE stamp_event_gate_code SET is_use = 1 WHERE stp_id = #{stpId} AND attend_code = #{attendCode} ")
    void updateStampEventGateCodeIsUseByStpIdAndAttendCode(@Param("stpId") int stpId, @Param("attendCode") String attendCode);

    List<AttendCodeUseVO> selectAttendCodeUseListAtStampTr(@Param("eventId") String eventId, @Param("attendCode") String attendCode);
}
