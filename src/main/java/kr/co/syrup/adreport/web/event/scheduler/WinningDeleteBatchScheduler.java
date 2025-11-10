package kr.co.syrup.adreport.web.event.scheduler;

import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.survey.go.service.SurveyGoLogService;
import kr.co.syrup.adreport.web.event.define.EventTypeDefine;
import kr.co.syrup.adreport.web.event.define.OcbPointSaveTypeDefine;
import kr.co.syrup.adreport.web.event.define.ScheduleDefine;
import kr.co.syrup.adreport.web.event.mybatis.vo.EventLogScheduledVO;
import kr.co.syrup.adreport.web.event.service.ArEventFrontService;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import kr.co.syrup.adreport.web.event.service.LogService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration
@Slf4j
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT1M")
public class WinningDeleteBatchScheduler {

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private LogService logService;

    @Autowired
    private ArEventFrontService arEventFrontService;

    @Autowired
    private SurveyGoLogService surveyGoLogService;

    //@Scheduled(cron="*/30 * * * * *")
    @Scheduled(cron = "0 0 3 * * *")
    @SchedulerLock(name="winningDeleteJobSchedulerJobLock",lockAtMostForString = "PT1M", lockAtLeastForString = "PT1M")
    public void runJob() {
        log.info("====================== winningDeleteJob 시작 ============================");
        List<Map<String, Object>> readerList = arEventService.getArEventIdAtEventEndAfterSixtyDay();
        if (!PredicateUtils.isNullList(readerList)) {
            //스케쥴러 로그에 등록되어있는 이벤트가 있는지 확인
            List<EventLogScheduledVO> eventLogScheduleList = logService.selectEventLogScheduledByScheduleType(ScheduleDefine.WINNING_DELETE.name());

            for (Map <String, Object> itemMap : readerList) {
                if (PredicateUtils.isNotNull(itemMap)) {

                    String eventId = itemMap.get("event_id").toString();
                    int arEventId = (int)itemMap.get("ar_event_id");
                    String eventType = itemMap.get("event_type").toString();
                    String obPointSaveType = itemMap.get("ocb_point_save_type").toString();

                    if (!PredicateUtils.isNullList(eventLogScheduleList)) {
                        Optional<EventLogScheduledVO> eventLogScheduleListOptional = eventLogScheduleList.stream().filter(schedule -> StringUtils.equals(schedule.getEventId(), eventId)).findAny();
                        if (eventLogScheduleListOptional.isPresent()) {
                            log.info("======================= 이미 스케쥴러가 된 이벤트이므로 통과 eventId :: {}", eventId);
                            continue;
                        }
                    }

                    log.info("====================== winningDeleteJob eventId :: {}", eventId);
                    log.info("====================== winningDeleteJob arEventId :: {}", arEventId);

                    try {
                        arEventService.deleteEventGiveAwayByEventId(eventId);
                        arEventService.deleteEventLogWinningByEventId(eventId);
                        arEventService.deleteEventLogWinningLimitByArEventId(arEventId);

                        logService.deleteEventLogExposureByArEventId(arEventId);
                        logService.deleteEventLogExposureLimitByArEventId(arEventId);
                        logService.deleteEventLogConnectByArEventId(arEventId);
                        logService.deleteEventLogPvByArEventId(arEventId);
                        logService.deleteEventLogSmsSendByArEventId(arEventId);
                        logService.deleteEventLogWinningSubscriptionByArEventId(arEventId);
                        logService.deleteEventLogAttendButtonByArEventId(arEventId);

                        //sms 인증 목록 삭제
                        arEventFrontService.deleteWebEventSmsAuthByEventId(eventId);

                        //서베이고일때 관련된 로그 삭제
                        if (PredicateUtils.isEqualsStr(eventType, EventTypeDefine.SURVEY.name())) {
                            surveyGoLogService.deleteSurveyLogAttendByArEventId(arEventId);
                            surveyGoLogService.deleteSurveyLogAttendResultByArEventId(arEventId);
                            surveyGoLogService.deleteSurveyLogSubjectCategoryByArEventId(arEventId);

                            arEventService.deleteSequencesByName(StringTools.joinStringsNoSeparator(eventId, "_SURVEY"));
                        }

                        //포토일때 포토프린트 로그 삭제
                        if (PredicateUtils.isEqualsStr(eventType, EventTypeDefine.PHOTO.name())) {
                            logService.deletePhotoLogPrintCountByEventId(eventId);
                        }

                        //OCB 포인트 적립 로그 삭제 - 참여전 적립 또는 당첨형일때
                        logService.deleteOcbLogPointSaveByEventId(eventId);

                        //쿠폰 정보, 쿠폰 지급 저장소 삭제
                        arEventService.deleteArEventCouponRepositoryByEventId(eventId);
                        arEventService.deleteArEventNftCouponInfoByEventIdx(eventType, arEventId);
                    } catch (Exception e) {
                        log.info("======================== WinningDeleteBatchScheduler 에러 발생 ========================");
                        log.error(e.getMessage(), e);
                    } finally {
                        try {
                            logService.saveEventLogScheduled(eventId, arEventId, ScheduleDefine.WINNING_DELETE.name());
                        } catch (Exception e) {
                            log.info("======================== saveEventLogScheduled 에러 발생 한번더 실행 요청 ========================");
                            log.error(e.getMessage(), e);
                            try {
                                logService.saveEventLogScheduled(eventId, arEventId, ScheduleDefine.WINNING_DELETE.name());
                            } catch (Exception e2) {
                                log.info("======================== saveEventLogScheduled 두번 에러 발생 저장 에러 ========================");
                                log.error(e.getMessage(), e);
                            }
                        }
                    }
                }

            }
        } else {
            log.info("====================== winningDeleteJob 항목 없음 ============================");
        }
        arEventService.deleteArEventNftCouponInfoTemp();
        log.info("====================== winningDeleteJob 끝 ============================");
    }
}
