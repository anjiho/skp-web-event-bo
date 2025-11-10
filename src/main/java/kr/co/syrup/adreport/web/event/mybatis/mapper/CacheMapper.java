package kr.co.syrup.adreport.web.event.mybatis.mapper;

import kr.co.syrup.adreport.web.event.dto.response.CacheableInfoResDto;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CacheMapper {

    @Update("UPDATE cacheable_info SET sodar_version = sodar_version + 1")
    void updateCacheableInfo();

    @Select("SELECT sodar_version FROM cacheable_info")
    Long selectCacheableInfo();

}
