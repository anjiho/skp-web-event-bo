package kr.co.syrup.adreport.web.event.scheduler;

import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.stamp.event.service.StampLogService;
import kr.co.syrup.adreport.web.event.define.EventTypeDefine;
import kr.co.syrup.adreport.web.event.define.ScheduleDefine;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import kr.co.syrup.adreport.web.event.service.LogService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT1M")
public class StampWinningDeleteBatchScheduler {

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private LogService logService;

    @Autowired
    private StampLogService stampLogService;

    @Scheduled(cron = "0 0 4 * * *")
    @SchedulerLock(name="stampWinningDeleteJobSchedulerJobLock",lockAtMostForString = "PT1M", lockAtLeastForString = "PT1M")
    public void runJob() {
        log.info("====================== stampWinningDeleteJob 시작 ============================");
        List<Map<String, Object>> readerList = arEventService.getStampEventAtEventEndAfterSixtyDay();
        if (PredicateUtils.isNotNullList(readerList)) {

            for (Map <String, Object> itemMap : readerList) {
                if (PredicateUtils.isNotNull(itemMap)) {

                    String eventId = "";
                    if (PredicateUtils.isNotNull(itemMap.get("event_id"))) {
                        eventId = (String) itemMap.get("event_id");
                    }

                    int stpId = 0;
                    if (PredicateUtils.isNotNull(itemMap.get("stp_id"))) {
                        stpId = (int)itemMap.get("stp_id");
                    }

                    if (PredicateUtils.isNotNull(eventId) && PredicateUtils.isNotNull(stpId)) {
                        log.info("====================== stampWinningDeleteJob stpId :: {} ", stpId);

                        try {
                            stampLogService.deleteStampEventGiveAwayByStpId(stpId);
                            stampLogService.deleteStampEventGiveAwayButtonAddByStpId(stpId);
                            stampLogService.deleteStampEventLogConnectByStpId(stpId);
                            stampLogService.deleteStampEventLogPvByStpId(stpId);
                            stampLogService.deleteStampEventLogTrByStpId(stpId);
                            stampLogService.deleteStampEventLogWinningLimitByStpId(stpId);
                            stampLogService.deleteStampEventLogWinningSuccessByStpId(stpId);

                            //쿠폰 정보, 쿠폰 지급 저장소 삭제
                            arEventService.deleteArEventCouponRepositoryByEventId(eventId);
                            arEventService.deleteArEventNftCouponInfoByEventIdx(EventTypeDefine.STAMP.name(), stpId);
                        } catch (Exception e) {
                            log.info("======================== StampWinningDeleteBatchScheduler 에러 발생 ========================");
                            log.error(e.getMessage(), e);
                        } finally {
                            try {
                                logService.saveEventLogScheduled(eventId, stpId, ScheduleDefine.STAMP_WINNING_DELETE.name());
                            } catch (Exception e) {
                                log.info("======================== saveEventLogScheduled 에러 발생 한번더 실행 요청 ========================");
                                log.error(e.getMessage(), e);
                                try {
                                    logService.saveEventLogScheduled(eventId, stpId, ScheduleDefine.STAMP_WINNING_DELETE.name());
                                } catch (Exception e2) {
                                    log.info("======================== saveEventLogScheduled 두번 에러 발생 저장 에러 ========================");
                                    log.error(e.getMessage(), e);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            log.info("====================== stampWinningDeleteJob 항목 없음 ============================");
        }
        log.info("====================== stampWinningDeleteJob 끝 ============================");
    }
}
