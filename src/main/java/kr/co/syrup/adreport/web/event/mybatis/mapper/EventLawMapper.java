package kr.co.syrup.adreport.web.event.mybatis.mapper;

import kr.co.syrup.adreport.web.event.mybatis.vo.EventLawInfoVO;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface EventLawMapper {

    @Insert( "INSERT INTO event_law_info (law_type, contents, start_date, end_date, created_by, version) VALUES (#{lawType}, #{contents}, #{startDate}, #{endDate}, #{createdBy}, #{version})" )
    void insertEventLawInfo(@Param("lawType") String lawType, @Param("contents") String contents, @Param("startDate") Date startDate,
                            @Param("endDate") Date endDate, @Param("createdBy") String createdBy, @Param("version") String version);

    void updateEventLawInfo(@Param("idx") int idx, @Param("lawType") String lawType, @Param("contents") String contents,
                            @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("createdBy") String createdBy);

    List<EventLawInfoVO> selectEventLawInfoListByType(@Param("lawType") String lawType);

    @Delete(" DELETE FROM event_law_info WHERE idx = #{idx} ")
    void deleteEventLawInfoById(int idx);

    @Select("SELECT idx FROM event_law_info WHERE idx order by idx desc limit 1")
    Integer selectLastIdx();

    @Select("SELECT version FROM event_law_info WHERE law_type = #{lawType} order by version desc limit 1")
    Double selectLastVersionByLawType(String lawType);
}
