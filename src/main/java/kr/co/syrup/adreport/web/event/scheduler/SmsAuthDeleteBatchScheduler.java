package kr.co.syrup.adreport.web.event.scheduler;

import kr.co.syrup.adreport.web.event.service.ArEventService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@Configuration
@Slf4j
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT1M")
public class SmsAuthDeleteBatchScheduler {

    @Autowired
    private ArEventService arEventService;

//    @Scheduled(cron="*/30 * * * * *")
    @Scheduled(cron = "0 0 0 * * *")
    @SchedulerLock(name="smsAuthDeleteSchedulerJobLock",lockAtMostForString = "PT1M", lockAtLeastForString = "PT1M")
    public void runJob() {
        log.info("====================== WEB_EVENT_SMS_AUTH 삭제 스케줄러 시작 ============================");
        arEventService.deleteWebEventSmsAuthAtPrevOneDay();
        log.info("====================== WEB_EVENT_SMS_AUTH 삭제 스케줄러 끝 ============================");

        log.info("====================== EM_TRAN 삭제 스케줄러 시작 ============================");
        arEventService.deleteEmTran();
        log.info("====================== EM_TRAN 삭제 스케줄러 끝 ============================");
    }
}
