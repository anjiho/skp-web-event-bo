package kr.co.syrup.adreport.web.event.service;

import kr.co.syrup.adreport.framework.utils.GsonUtils;
import kr.co.syrup.adreport.framework.utils.ModelMapperUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.CacheTypeDefine;
import kr.co.syrup.adreport.web.event.define.WinningTypeDefine;
import kr.co.syrup.adreport.web.event.dto.response.CacheJsonDataResDto;
import kr.co.syrup.adreport.web.event.entity.ArEventWinningEntity;
import kr.co.syrup.adreport.web.event.mybatis.mapper.ArEventMapper;
import kr.co.syrup.adreport.web.event.mybatis.mapper.CacheMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class CacheService {

    @Autowired
    private CacheMapper cacheMapper;

    @Autowired
    private CacheManager cacheManager;

    @Transactional
    public void updateCacheableInfo() {
        cacheMapper.updateCacheableInfo();
    }

    public Long findCacheableSodarVersion() {
        return cacheMapper.selectCacheableInfo();
    }

    public void clearAllCache() {
        for (CacheTypeDefine define : CacheTypeDefine.values()) {
            cacheManager.getCache(define.getCacheName()).clear();
        }
    }
}
