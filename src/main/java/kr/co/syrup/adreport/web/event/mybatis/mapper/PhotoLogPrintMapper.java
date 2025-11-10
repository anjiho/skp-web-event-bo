package kr.co.syrup.adreport.web.event.mybatis.mapper;

import kr.co.syrup.adreport.web.event.mybatis.vo.AttendCodeHistorySearchMapperVO;
import kr.co.syrup.adreport.web.event.mybatis.vo.EventLogPvVO;
import kr.co.syrup.adreport.web.event.mybatis.vo.PhotoLogPrintCountVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PhotoLogPrintMapper {

    @Insert("INSERT INTO photo_log_print_count" +
            "(event_id, client_unique_key, ocb_mbr_id, print_result_status, created_date)" +
            " VALUES (#{eventId}, #{clientUniqueKey}, #{ocbMbrId}, #{printResultStatus}, now())"
    )
    void savePhotoPrintLog(PhotoLogPrintCountVO photoLogPrintCountVO);

    @Select("SELECT COUNT(*) " +
            "FROM photo_log_print_count " +
            "WHERE event_id = #{eventId} " +
            "AND ocb_mbr_id = #{ocbMbrId} " +
            "AND print_result_status = #{printResultStatus}"
    )
    long selectOcbPrintStatusCnt(@Param("eventId") String eventId, @Param("ocbMbrId") String ocbMbrId, @Param("printResultStatus") String printResultStatus);

    @Select("SELECT COUNT(*) " +
            "FROM photo_log_print_count " +
            "WHERE event_id = #{eventId} " +
            "AND client_unique_key = #{clientUniqueKey} " +
            "AND print_result_status = #{printResultStatus}"
    )
    long selectClientPrintStatusCnt(@Param("eventId") String eventId, @Param("clientUniqueKey") String clientUniqueKey, @Param("printResultStatus") String printResultStatus);






}
