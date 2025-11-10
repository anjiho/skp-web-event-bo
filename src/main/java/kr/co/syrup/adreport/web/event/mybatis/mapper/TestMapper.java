package kr.co.syrup.adreport.web.event.mybatis.mapper;

import kr.co.syrup.adreport.survey.go.entity.SurveyLogAttendEntity;
import kr.co.syrup.adreport.survey.go.mybatis.vo.SurveyLogAttendResultResVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TestMapper {

    @Delete("delete from stamp_event_give_away_delivery where phone_number = #{phoneNumber}")
    void deleteStampEventGiveAwayDelivery(@Param("phoneNumber") String phoneNumber);

    @Delete("delete from stamp_event_log_winning_success where attend_value = #{attendValue} and stp_id = #{stpId}")
    void deleteStampEventLogWinningSuccess(@Param("attendValue") String attendValue, @Param("stpId") int stpId );

    @Delete("delete from stamp_event_log_winning_limit where stp_id = #{stpId}")
    void deleteStampEventLogLimit(@Param("stpId") int stpId );

    @Delete("delete from stamp_event_log_tr where stp_id = #{stpId}")
    void deleteStampEventLogTr(@Param("stpId") int stpId);

    @Delete("delete from event_log_winning_success where event_id = #{eventId} and phone_number = #{phoneNumber}")
    void deleteEventLogWinningSuccess(@Param("phoneNumber") String phoneNumber, @Param("eventId") String eventId);

    @Delete("delete from event_log_winning_limit where ar_event_id = #{arEventId}")
    void deleteEventLogWinningLimit(@Param("arEventId") int arEventId);

    @Delete("delete from event_give_away_delivery where event_id = #{eventId} and phone_number = #{phoneNumber}")
    void deleteEventGiveAwayDelivery(@Param("eventId") String eventId, @Param("phoneNumber") String phoneNumber);

    @Select(" select * from survey_log_attend_result where survey_log_attend_id = #{surveyLogAttendId} ")
    List<SurveyLogAttendResultResVO> getSurveyLogAttendResultBySurveyLogAttendId(@Param("surveyLogAttendId") String surveyLogAttendId);

    @Select(" select * from survey_log_attend where survey_log_attend_id = #{surveyLogAttendId} ")
    SurveyLogAttendEntity getSurveyLogAttendById(@Param("surveyLogAttendId") String surveyLogAttendId);

    @Update("  update survey_log_attend set gender = #{gender}, age = #{age}, is_submit = 1 where survey_log_attend_id = #{surveyLogAttendId} ")
    void updateSurveyLogAttend(SurveyLogAttendEntity surveyLogAttend);

}
