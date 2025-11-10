package kr.co.syrup.adreport.web.event.scheduler;

import kr.co.syrup.adreport.framework.utils.FileUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.SmsMessageDefine;
import kr.co.syrup.adreport.web.event.define.SmsTranTypeDefine;
import kr.co.syrup.adreport.web.event.define.WinningTypeDefine;
import kr.co.syrup.adreport.web.event.entity.ArEventNftCouponInfoEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventNftTokenInfoEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventWinningEntity;
import kr.co.syrup.adreport.web.event.entity.EventGiveAwayDeliveryEntity;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import kr.co.syrup.adreport.web.event.service.LogService;
import kr.co.syrup.adreport.web.event.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

@Configuration
@Slf4j
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT2M")
public class SubscriptionBatchScheduler {

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private LogService logService;

    @Autowired
    private SmsService smsService;

    @Value("${web.event.domain}")
    private String webEventDomain;

    //@Scheduled(cron="*/30 * * * * *")
    @Scheduled(cron = "0 0 0/1 * * *") // 한시간마다
    @SchedulerLock(name="subscriptionJobSchedulerLock", lockAtMostForString = "PT2M", lockAtLeastForString = "PT2M")
    public void runJob() {
        log.info("====================== subscriptionJob 시작 ============================");
        List<ArEventWinningEntity> itemList = arEventService.findSubscriptionWinningInfoListByYYYY_MM_DD_HH("RAFFLE");
        if (!PredicateUtils.isNullList(itemList)) {
            //당첨정보 목록
            for (ArEventWinningEntity entity : itemList) {
                //경품정보 입력 정보에서 응모가능한 사용자 리스트 가져오기
                List<EventGiveAwayDeliveryEntity> subscriptionUserList = arEventService.findSubscriptionScheduleListByYYYY_MM_DD_HH(entity.getArEventWinningId(), "RAFFLE");
                //랜덤섞기
                if (!PredicateUtils.isNullList(subscriptionUserList)) {
                    Collections.shuffle(subscriptionUserList);
                }
                //응모당첨자 리스트 선언
                List<EventGiveAwayDeliveryEntity> shuffledSubscriptionWinningList = new ArrayList<>();

                if (PredicateUtils.isGreaterThanEqualTo(entity.getSubscriptionWinningNumber(), subscriptionUserList.size())) {
                    for (int i=0; i<subscriptionUserList.size(); i++) {
                        EventGiveAwayDeliveryEntity subscriptionUser = subscriptionUserList.get(i);
                        //응모당첨자 담기
                        shuffledSubscriptionWinningList.add(subscriptionUser);
                        log.info("========================== 응모 당첨자 정보 ::: " + subscriptionUser.toString());


                        if (PredicateUtils.isNull(entity.getIsSubscriptionWinningPresentation())) {
                            entity.setIsSubscriptionWinningPresentation(false);
                        }
                        //응모 발표를 사용으로 셋팅되었을때 SMS 발송자 데이터 추가하기
                        if (entity.getIsSubscriptionWinningPresentation() || PredicateUtils.isNotNull(entity.getSubscriptionWinningPresentationDate())) {
                            if (StringUtils.isNotEmpty(subscriptionUser.getPhoneNumber())) {
                                log.info("========================== 응모 당첨자의 핸드폰 번호가 존재 ==========================");
                                String targetUrl = "";
                                if (PredicateUtils.isEqualsStr(subscriptionUser.getWinningType(), WinningTypeDefine.NFT.code())
                                        || PredicateUtils.isEqualsStr(subscriptionUser.getWinningType(), WinningTypeDefine.NFT쿠폰.code())) {
                                    targetUrl = FileUtils.concatPath("https://", webEventDomain, "web-event", "/nft/nft-history.html?eventId=" + subscriptionUser.getEventId() + "&winningType=" + subscriptionUser.getWinningType());
                                } else {
                                    targetUrl = FileUtils.concatPath("https://", webEventDomain, "web-event", "/event-winning-history.html?eventId=" + subscriptionUser.getEventId());
                                }
                                String smsMessage = SmsMessageDefine.SUBSCRIPTION.content().replace("{productName}", entity.getProductName()).replace("{targetUrl}", targetUrl);
                                //응모 당첨자 SMS 발송 로그 저장
                                logService.saveEventLogSmsByEventGiveAwayDelivery(subscriptionUser, smsMessage, entity.getSubscriptionWinningPresentationDate());
                                //SMS 발송 스케쥴러 테이블에 저장
                                smsService.saveEmTranSms(subscriptionUser.getPhoneNumber(), smsMessage, entity.getSubscriptionWinningPresentationDate(), SmsTranTypeDefine.MMS.key());

                            } else {
                                log.info("========================== 응모 당첨자의 핸드폰 번호가 존재하지 않음 ==========================");
                            }
                        }
                    }
                } else {
                    for (int i=0; i<entity.getSubscriptionWinningNumber(); i++) {
                        EventGiveAwayDeliveryEntity subscriptionUser = subscriptionUserList.get(i);
                        //응모당첨자 담기
                        shuffledSubscriptionWinningList.add(subscriptionUser);
                        log.info("========================== 응모 당첨자 정보 ::: " + subscriptionUser.toString());


                        if (PredicateUtils.isNull(entity.getIsSubscriptionWinningPresentation())) {
                            entity.setIsSubscriptionWinningPresentation(false);
                        }
                        //응모 발표를 사용으로 셋팅되었을때 SMS 발송자 데이터 추가하기
                        if (entity.getIsSubscriptionWinningPresentation() || PredicateUtils.isNotNull(entity.getSubscriptionWinningPresentationDate())) {
                            if (StringUtils.isNotEmpty(subscriptionUser.getPhoneNumber())) {
                                log.info("========================== 응모 당첨자의 핸드폰 번호가 존재 ==========================");
                                String targetUrl = "";
                                if (PredicateUtils.isEqualsStr(subscriptionUser.getWinningType(), WinningTypeDefine.NFT.code())
                                        || PredicateUtils.isEqualsStr(subscriptionUser.getWinningType(), WinningTypeDefine.NFT쿠폰.code())) {
                                    targetUrl = FileUtils.concatPath("https://", webEventDomain, "web-event", "/nft/nft-history.html?eventId=" + subscriptionUser.getEventId() + "&winningType=" + subscriptionUser.getWinningType());
                                } else {
                                    targetUrl = FileUtils.concatPath("https://", webEventDomain, "web-event", "/event-winning-history.html?eventId=" + subscriptionUser.getEventId());
                                }
                                String smsMessage = SmsMessageDefine.SUBSCRIPTION.content().replace("{productName}", entity.getProductName()).replace("{targetUrl}", targetUrl);
                                //응모 당첨자 SMS 발송 로그 저장
                                logService.saveEventLogSmsByEventGiveAwayDelivery(subscriptionUser, smsMessage, entity.getSubscriptionWinningPresentationDate());
                                //SMS 발송 스케쥴러 테이블에 저장
                                smsService.saveEmTranSms(subscriptionUser.getPhoneNumber(), smsMessage, entity.getSubscriptionWinningPresentationDate(), SmsTranTypeDefine.MMS.key());

                            } else {
                                log.info("========================== 응모 당첨자의 핸드폰 번호가 존재하지 않음 ==========================");
                            }
                        }
                    }
                }

                //응모가 NFT 토큰 일때 NFT 보관함 테이블에 저장
                if (StringUtils.equals(entity.getWinningType(), WinningTypeDefine.NFT.code())) {
                    log.info("====================== NFT 토큰 지급 start ======================");
                    //NFT 를 응모당첨자의 NFT 보관함에 넣어준다
                    List<ArEventNftTokenInfoEntity> nftTokenInfoList = arEventService.findAllArEventNftTokenInfoLeftJoinArEventWinningByArEventWinningId(entity.getArEventWinningId());
                    // 응모당첨된 개수, 지급할 NFT 토큰 개수 체크
//                            if (PredicateUtils.isGreaterThan(shuffledSubscriptionWinningList.size(), nftTokenInfoList.size())) {
//                                log.error("subscriptionItemWriter Error :: 지급할 NFT 토크개수 보다 응모당첨된 목록이 많아서 에러!");
//                                throw new CommonException("지급할 NFT 토크개수 보다 응모당첨된 목록이 많아서 에러!");
//                            }
                    //NFT 권한이전 서비스 로직
                    arEventService.transNftToken(shuffledSubscriptionWinningList, nftTokenInfoList);
                    log.info("====================== NFT 토큰 지급 end ======================");
                }

                //응모가 NFT 쿠폰 일때 NFT 쿠폰 지급함에 지급
                if (StringUtils.equals(entity.getWinningType(), WinningTypeDefine.NFT쿠폰.code())) {
                    log.info("====================== NFT 쿠폰 지급 start ======================");
                    //지급가능한 NFT 쿠폰 정보 리스트
                    List<ArEventNftCouponInfoEntity> nftCouponInfoList = arEventService.findAllArEventNftCouponInfoByArEventWinningId(entity.getArEventWinningId());
                    //NFT 쿠폰 권한이전 서비스 로직
                    arEventService.transNftCoupon(shuffledSubscriptionWinningList, nftCouponInfoList);
                    log.info("====================== NFT 쿠폰 지급 end ======================");
                }

                if (PredicateUtils.isEqualsStr(entity.getWinningType(), WinningTypeDefine.OCB쿠폰.code())) {
                    arEventService.transCouponRepositoryByOcbCoupon(shuffledSubscriptionWinningList);
                }

                log.info("====================== 응모당첨자 로그 저장 ======================");
                //응모당첨자 로그 저장
                logService.saveAllEventLogWinningSubscriptionFromEventGiveAwayList(shuffledSubscriptionWinningList);

                log.info("====================== 응모당첨자 로그 끝 ======================");

                log.info("====================== 응모상태 완료로 업데이트 ======================");
                // AR_EVENT_WINNING > 응모 스케쥴링 완료 상태로 업데이트
                arEventService.updateSubscriptionRaffleScheduleDate(entity.getArEventWinningId());
            }
        } else {
            log.info("====================== subscriptionJob 목록 없음 ============================");
        }
        log.info("====================== subscriptionJob 끝 ============================");
    }
}
