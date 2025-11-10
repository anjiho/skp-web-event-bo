package kr.co.syrup.adreport.web.event.mybatis.mapper;

import kr.co.syrup.adreport.web.event.mybatis.vo.AttendCodeHistorySearchMapperVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface ArEventOuterMapper {

    @Select(" SELECT now() ")
    Date findNow();

    @Insert(" INSERT INTO common_log_pv (event_name, log_key) VALUES ( #{eventName}, #{logKey} ) ")
    void insertCommonLogPv(@Param("eventName") String eventName, @Param("logKey") String logKey);

    @Insert(" INSERT INTO common_log_person_agree (event_name, agree_id, phone_number) VALUES ( #{eventName}, #{agreeId}, #{phoneNumber} ) ")
    void insertCommonLogPersonAgree(@Param("eventName") String eventName, @Param("agreeId") String agreeId, @Param("phoneNumber") String phoneNumber);

    int countByCommonLogPersonAgreeByEventNameAndAgreeId(@Param("eventName") String eventName, @Param("agreeId") String agreeId, @Param("phoneNumber") String phoneNumber);

    @Select("SELECT A.id, A.attend_code, A.give_away_id, A.winning_type, C.created_date, B.product_name, C.name, C.phone_number" +
            "        FROM event_log_winning A" +
            "                 LEFT OUTER JOIN ar_event_winning B" +
            "                                 ON A.ar_event_winning_id = B.ar_event_winning_id" +
            "                 LEFT OUTER JOIN event_give_away_delivery C" +
            "                                 ON A.give_away_id = C.give_away_id" +
            "        WHERE A.event_id = #{eventId}" +
            "          AND A.attend_code = #{attendCode}" +
            "        ORDER BY C.created_date DESC")
    List<AttendCodeHistorySearchMapperVO> selectAttendCodeHistoryByEventIdAndAttendCode(@Param("eventId") String eventId, @Param("attendCode") String attendCode);

}
