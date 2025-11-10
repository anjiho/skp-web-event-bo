package kr.co.syrup.adreport.stamp.event.service;

import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.stamp.event.mybatis.mapper.StampLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class StampAsyncService {

    @Autowired
    private StampLogMapper stampLogMapper;

    /**
     * 스탬프 접속 로그 저장
     * @param stpId
     * @param connectType > 메인 : MAIN, 판 : PAN
     * @return
     */
    @Transactional
    public CompletableFuture<Boolean> asyncInsertStampEventLogConnect(int stpId, String connectType) {
        stampLogMapper.insertStampEventLogConnect(stpId, PredicateUtils.isNull(connectType) ? "MAIN" : connectType);
        return CompletableFuture.completedFuture(true);
    }
}
