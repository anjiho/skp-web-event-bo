package kr.co.syrup.adreport.web.event.logic;

import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.common.annotation.ThreadSleep;
import kr.co.syrup.adreport.framework.exception.BaseException;
import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.web.event.define.ContractStatusDefine;
import kr.co.syrup.adreport.web.event.define.ErrorCodeDefine;
import kr.co.syrup.adreport.web.event.define.HtmlButtonTypeDefine;
import kr.co.syrup.adreport.web.event.dto.response.ArEventGatePageResDto;
import kr.co.syrup.adreport.web.event.dto.response.ArEventHtmlResDto;
import kr.co.syrup.adreport.web.event.dto.response.CacheJsonDataResDto;
import kr.co.syrup.adreport.web.event.entity.ArEventHtmlEntity;
import kr.co.syrup.adreport.web.event.entity.EventLogConnectEntity;
import kr.co.syrup.adreport.web.event.entity.repository.EventLogConnectEntityRepository;
import kr.co.syrup.adreport.web.event.mybatis.vo.EventBaseJoinArEventJoinEventButtonVO;
import kr.co.syrup.adreport.web.event.service.ArEventFrontService;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import kr.co.syrup.adreport.web.event.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Repository
public class AsyncLogic {

    @Autowired
    private EventLogConnectEntityRepository eventLogConnectEntityRepository;

    /**
     * 이벤트 접속 로그 비동기 저장
     * @param eventLogConnectEntity
     * @return
     */
    @Transactional
    public CompletableFuture<Map<String, Object>> asyncSaveEventLogConnect(EventLogConnectEntity eventLogConnectEntity) {
        try {
            eventLogConnectEntityRepository.save(eventLogConnectEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
