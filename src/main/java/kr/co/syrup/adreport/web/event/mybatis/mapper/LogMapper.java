package kr.co.syrup.adreport.web.event.mybatis.mapper;

import kr.co.syrup.adreport.web.event.entity.EventLogAttendButtonEntity;
import kr.co.syrup.adreport.web.event.mybatis.vo.EventLogAttendSaveVO;
import kr.co.syrup.adreport.web.event.mybatis.vo.EventLogConnectSaveVO;
import kr.co.syrup.adreport.web.event.mybatis.vo.EventLogPvVO;
import kr.co.syrup.adreport.web.event.mybatis.vo.OcbLogPointSaveVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LogMapper {

    @Insert(" INSERT INTO event_log_pv" +
            "( event_id, ar_event_id, page_id, action_id, pv_order, pv_type, pv_code, browser_version, br, created_date )" +
            "VALUES ( #{eventId}, #{arEventId}, #{pageId}, #{actionId}, #{order}, #{type}, #{code}, #{browserVersion}, #{br}, now() ) ")
    void saveEventLogPv(EventLogPvVO eventLogPvVO);

    @Insert(" INSERT INTO event_log_pv" +
            "( event_id, ar_event_id, page_id, action_id, pv_order, pv_type, pv_code, browser_version, br, created_date )" +
            " SELECT #{eventId}, ar_event_id, #{pageId}, #{actionId}, #{order}, #{type}, #{code}, #{browserVersion}, #{br}, now() " +
            " FROM ar_event WHERE event_id = #{eventId} " )
    void saveEventLogPvBySelect(EventLogPvVO eventLogPvVO);

    @Insert(" INSERT INTO event_log_pv" +
            "( event_id, ar_event_id, page_id, action_id, pv_order, pv_type, pv_code, browser_version, br, created_date )" +
            " SELECT #{eventId}, stp_id, #{pageId}, #{actionId}, #{order}, #{type}, #{code}, #{browserVersion}, #{br}, now() " +
            " FROM stamp_event_main WHERE event_id = #{eventId} " )
    void saveEventLogPvBySelectAtStamp(EventLogPvVO eventLogPvVO);

    @Insert("INSERT INTO ocb_log_point_save " +
            "( event_id, ocb_point_save_id, phone_number, ocb_mbr_id, point_save_type, point, is_success, point_save_result, give_away_id, request_id ) " +
            "VALUES ( #{eventId}, #{ocbPointSaveId}, #{phoneNumber}, #{ocbMbrId}, #{pointSaveType}, #{point}, #{isSuccess}, #{pointSaveResult}, #{giveAwayId}, #{requestId} ) ")
    void saveOcbLogPointSave(OcbLogPointSaveVO ocbLogPointSaveVO);

    @Select(" SELECT count(*) FROM ocb_log_point_save WHERE event_id = #{eventId} AND is_success = 1")
    int selectCountOcbLogPointSaveByEventId(@Param("eventId") String eventId);

    @Select(" SELECT count(*) FROM ocb_log_point_save WHERE event_id = #{eventId} AND DATE(created_date) = DATE(now()) AND is_success = 1")
    int selectCountOcbLogPointSaveByEventIdToday(@Param("eventId") String eventId);

    @Select(" SELECT count(*) FROM ocb_log_point_save WHERE event_id = #{eventId} AND ocb_point_save_id = #{ocbPointSaveId} AND phone_number = #{phoneNumber} AND is_success = 1")
    long selectCountOcbLogPointSaveByEventIdAndOcbPointSaveId(@Param("eventId") String eventId, @Param("ocbPointSaveId") int ocbPointSaveId, @Param("phoneNumber") String phoneNumber);

    @Select(" SELECT count(*) FROM ocb_log_point_save WHERE event_id = #{eventId} AND ocb_point_save_id = #{ocbPointSaveId} AND phone_number = #{phoneNumber} AND DATE(created_date) = DATE(now()) AND is_success = 1 ")
    long selectCountOcbLogPointSaveByEventIdAndOcbPointSaveIdAtToday(@Param("eventId") String eventId, @Param("ocbPointSaveId") int ocbPointSaveId, @Param("phoneNumber") String phoneNumber);

    @Select(" SELECT count(*) FROM ocb_log_point_save WHERE event_id = #{eventId} AND ocb_point_save_id = #{ocbPointSaveId} AND ocb_mbr_id = #{ocbMbrId} AND is_success = 1 ")
    int selectCountOcbLogPointSaveByEventIdAndOcbPointSaveIdAndMbrId(@Param("eventId") String eventId, @Param("ocbPointSaveId") int ocbPointSaveId, @Param("ocbMbrId") String mbrId);

    @Select(" SELECT count(*) FROM ocb_log_point_save WHERE event_id = #{eventId} AND ocb_point_save_id = #{ocbPointSaveId} AND ocb_mbr_id = #{ocbMbrId} AND is_success = 1 AND DATE(created_date) = DATE(now()) ")
    int selectCountOcbLogPointSaveByEventIdAndOcbPointSaveIdAndMbrIdAtToday(@Param("eventId") String eventId, @Param("ocbPointSaveId") int ocbPointSaveId, @Param("ocbMbrId") String mbrId);

    @Delete(" DELETE FROM event_log_pv WHERE ar_event_id = #{arEventId}")
    void deleteEventLogPvByArEventId(@Param("arEventId") int arEventId);

    @Delete(" DELETE FROM event_log_sms_send WHERE ar_event_winning_id IN ( SELECT ar_event_winning_id FROM ar_event_winning WHERE ar_event_id = #{arEventId}) ")
    void deleteEventLogSmsSendByArEventId(@Param("arEventId") int arEventId);

    @Delete(" DELETE FROM event_log_winning_subscription WHERE ar_event_winning_id IN ( SELECT ar_event_winning_id FROM ar_event_winning WHERE ar_event_id = #{arEventId}) ")
    void deleteEventLogWinningSubscriptionByArEventId(@Param("arEventId") int arEventId);

    @Delete(" DELETE FROM ocb_log_point_save WHERE event_id = #{eventId} ")
    void deleteOcbLogPointSaveByEventId(@Param("eventId") String eventId);

    @Delete(" DELETE FROM photo_log_print_count WHERE event_id = #{eventId} ")
    void deletePhotoLogPrintCountByEventId(@Param("eventId") String eventId);

    @Delete("DELETE FROM event_log_winning_success WHERE id = #{id}")
    void deleteEventLogWinningSuccess(@Param("id") long id);

    @Delete("DELETE FROM event_log_winning WHERE id = #{id}")
    void deleteEventLogWinning(@Param("id") long id);

    @Delete("DELETE FROM event_log_winning_success WHERE ar_event_id = #{arEventId} AND winning_sort = #{winningSort} AND id > #{idx}")
    void deleteEventLogWinningSuccessLastIndexGreaterThan(@Param("arEventId") int arEventId, @Param("winningSort") int winningSort, @Param("idx") long idx);

    @Insert("INSERT INTO event_log_connect (event_id, ar_event_id, is_attend, attend_code, tracking_code, created_hour, created_day) VALUES (#{eventId}, #{arEventId}, #{isAttend}, #{attendCode}, #{trackingCode}, #{createdHour}, #{createdDay})")
    void saveEventLogConnect(EventLogConnectSaveVO vo);

    @Insert("INSERT INTO event_log_attend_button (event_id, ar_event_id, attend_code, phone_number, success_yn, created_day, created_hour) VALUES (#{eventId}, #{arEventId}, #{attendCode}, #{phoneNumber}, #{successYn}, #{createdDay}, #{createdHour})")
    void saveEventLogAttendButton(EventLogAttendSaveVO vo);

    @Select(" SELECT event_id FROM survey_log_attend GROUP BY event_id ")
    List<String> selectSurveyEventIdList();
}
