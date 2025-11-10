package kr.co.syrup.adreport.stamp.event.service;

import kr.co.syrup.adreport.stamp.event.mybatis.mapper.StampFrontMapper;
import kr.co.syrup.adreport.web.event.entity.ArEventWinningEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StampWinningService {

    @Autowired
    private StampFrontMapper stampFrontMapper;

    @Cacheable(cacheNames = "findMappingArEventWinningByStpPanTrId", keyGenerator = "customKeyGenerator")
    public ArEventWinningEntity findMappingArEventWinningByStpPanTrId(Long stpPanTrId) {
        return stampFrontMapper.selectMappingArEventWinningByStpPanTrId(stpPanTrId);
    }
}
