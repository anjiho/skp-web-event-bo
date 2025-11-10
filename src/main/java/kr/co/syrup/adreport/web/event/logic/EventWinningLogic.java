package kr.co.syrup.adreport.web.event.logic;

import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.exception.BaseException;
import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.stamp.event.model.StampEventMainModel;
import kr.co.syrup.adreport.stamp.event.model.StampEventPanTrModel;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.StampSortAttendSortYnResVO;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.StpPanTrRowNumByWinningVO;
import kr.co.syrup.adreport.stamp.event.service.StampFrontService;
import kr.co.syrup.adreport.stamp.event.service.StampLogService;
import kr.co.syrup.adreport.stamp.event.service.StampSodarService;
import kr.co.syrup.adreport.web.event.define.EventTypeDefine;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import kr.co.syrup.adreport.web.event.define.WinningButtonActionTypeDefine;
import kr.co.syrup.adreport.web.event.define.WinningButtonMoveLocationDefine;
import kr.co.syrup.adreport.web.event.dto.request.EventWinningReqDto;
import kr.co.syrup.adreport.web.event.dto.response.*;
import kr.co.syrup.adreport.web.event.entity.ArEventWinningEntity;
import kr.co.syrup.adreport.web.event.entity.WebEventBaseEntity;
import kr.co.syrup.adreport.web.event.service.ArEventFrontService;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class EventWinningLogic implements EventWinning{

    @Value("${web.event.domain}")
    private String webEventDomain;

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private ArEventFrontService arEventFrontService;

    @Autowired
    private StampLogService stampLogService;

    @Autowired
    private StampSodarService stampSodarService;

    @Autowired
    private StampFrontService stampFrontService;

    @Autowired
    private SkApiLogic skApiLogic;

    @Autowired
    private AES256Utils aes256Utils;

    @Override
    public WinningResultResDto getWinningResultButtonInfo(String eventId, Long eventLogWinningId, Integer eventIndex, String eventType, Boolean isFail) {
        WebEventBaseEntity webEventBase = arEventService.findEventBase(eventId);
        //꽝 당첨정보 가져오기
        LinkedList<ArEventWinningEntity> winningLinkedList = new LinkedList<>();
        if (PredicateUtils.isEqualsStr(EventTypeDefine.STAMP.name(), eventType)) {
            winningLinkedList = arEventService.findArEventWinningListByStpId(eventIndex, isFail);
        } else {
            winningLinkedList = arEventService.findArEventWinningListByArEventIdAndSubscriptionYn(eventIndex, false, isFail);
        }
        //랜덤 섞기
        if (winningLinkedList.size() > 1) {
            Collections.shuffle(winningLinkedList);
        }
        //랜덤중에 첫번쨰 추출
        ArEventWinningEntity winningEntity = winningLinkedList.get(0);

        WinningResDto winningResDto = new WinningResDto().builder()
                .arEventWinningId(winningEntity.getArEventWinningId())
                .winningType(winningEntity.getWinningType())
                .winningImageUrl(EventUtils.replaceUriHost(EventTypeDefine.isStampEvent(eventType) ? winningEntity.getStpWinningPopupImgUrl() : winningEntity.getWinningImageUrl(),webEventDomain + "/sodarimg"))
                .subscriptionYn(winningEntity.getSubscriptionYn())
                .autoWinningYn(winningEntity.getAutoWinningYn())
                .build();

        //당첨정보 버튼 리스트 가져오기
        List<WinningButtonResDto> winningButtonResDtoList = ModelMapperUtils.convertModelInList(arEventService.findWinningButtonResDtoByArEventWinningIdAtWinningProcess(winningEntity.getArEventWinningId(), eventId, webEventBase.getStpConnectYn()), WinningButtonResDto.class);

        WinningResultResDto resultResDto = new WinningResultResDto().builder()
                .eventId(eventId)
                .finishYn( StringDefine.Y.name() )
                .eventLogWinningId(eventLogWinningId)
                .winningInfo(winningResDto)
                .winningButtonInfo(winningButtonResDtoList)
                .winningEntity(winningEntity)
                .build();

        return resultResDto;
    }

    @Override
    public WinningResultResDto getWebEventSuccessWinningResultButtonInfo(String eventId, Long eventLogWinningId, String stpConnectYn, ArEventWinningEntity arEventWinning) {
        WebEventBaseEntity webEventBase = arEventService.findEventBase(eventId);

        WinningResDto winningResDto = new WinningResDto().builder()
                .arEventWinningId(arEventWinning.getArEventWinningId())
                .winningType(arEventWinning.getWinningType())
                .winningImageUrl(EventUtils.replaceUriHost(EventTypeDefine.isStampEvent(webEventBase.getEventType()) ? arEventWinning.getStpWinningPopupImgUrl() : arEventWinning.getWinningImageUrl(),webEventDomain + "/sodarimg"))
                .subscriptionYn(arEventWinning.getSubscriptionYn())
                .autoWinningYn(arEventWinning.getAutoWinningYn())
                .build();

        WinningResultResDto resultResDto = new WinningResultResDto().builder()
                .eventId(eventId)
                .finishYn( StringDefine.Y.name() )
                .eventLogWinningId(eventLogWinningId)
                .winningInfo(winningResDto)
                .winningButtonInfo(arEventService.findWinningButtonResDtoByArEventWinningIdAtWinningProcess(arEventWinning.getArEventWinningId(), eventId, stpConnectYn))
                .winningEntity(arEventWinning)
                .build();

        return resultResDto;
    }

    @Override
    public Long payCoupon(Integer eventIndex, Integer arEventWinningId, Long eventWinningLogId, String eventType) {
        Long couponId = null;
        try {
            boolean isStamp = EventTypeDefine.isStampEvent(eventType);
            couponId = arEventFrontService.saveSelectAvailableArEventCouponByArEventIdAndArEventWinningId(eventIndex, arEventWinningId, eventWinningLogId, isStamp);
        } catch (Exception e) {
            log.error("payCoupon Error {}", e.getMessage());
            log.info("쿠폰 지급시 예외사항 발생!! ::::::: 꽝처리");
        } finally {
            //쿠폰이 정상 발급이되면 쿠폰 상태를 '지급완료' 상태로 업데이트
            //쿠폰 정상지급이 안됬을때
            if (PredicateUtils.isNull(couponId)) {
                try {
                    //지급된 저장소 삭제처리
                    arEventService.deleteArEventNftCouponRepositoryByStampEventWinningLogId(eventWinningLogId);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            } else {
                try {
                    //쿠폰 지급상태 업데이트
                    arEventFrontService.updateCouponIsPayedById(couponId, true);
                } catch (Exception e) {
                    log.error("updateCouponIsPayedById Error {}", e.getMessage());
                }
            }
            return couponId;
        }
    }

    @Override
    public GifticonOrderResDto payGifticon(String eventId, int arEventWinningId, String phoneNumber, String eventType) {
        boolean isPayedGifticon = false;
        if (EventTypeDefine.isStampEvent(eventType)) {
            isPayedGifticon = stampLogService.getIsPayedGifticon(eventId, arEventWinningId, phoneNumber);
        }
        //이미 기프티콘이 지급되었으면 에러처리
        if (isPayedGifticon) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GIFTICON_RECEIVE_COUNT.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GIFTICON_RECEIVE_COUNT);
        }
        GifticonOrderResDto gifticonOrderResDto = new GifticonOrderResDto();
        //기프티콘 지급 시작
        try {
            gifticonOrderResDto = skApiLogic.sendGifticonAtGiveAwayLogic(eventId, arEventWinningId, aes256Utils.decrypt(phoneNumber));
            //기프티콘 발급이 정상적으로 되지 않았을때
            if (PredicateUtils.isNull(gifticonOrderResDto.getResultCd())) {
                gifticonOrderResDto.setResultCd("9999");
                throw new BaseException(ResultCodeEnum.REST_API_CALL_ERROR.getDesc(), ResultCodeEnum.REST_API_CALL_ERROR);
            } else {
                //정상 발급일떄
                if (!PredicateUtils.isEqualsStr(gifticonOrderResDto.getResultCd(), "0000")) {
                    throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GIFTICON_SEND_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GIFTICON_SEND_ERROR);
                }
                log.info("기프티콘 정상 발급! {} ", gifticonOrderResDto);
            }
        } catch (Exception e) {
            gifticonOrderResDto.setResultCd("9999");
            log.error("기프티콘 발급 Error {}", e.getMessage());
        } finally {
            return gifticonOrderResDto;
        }
    }

    @Override
    public void injectStampPanAndStampNextEventUrlPath(String eventId, List<WinningButtonResDto> winningButtonList) {
        if (PredicateUtils.isNullList(winningButtonList)) {
            return;
        }
        StampSortAttendSortYnResVO stampSortAttend = stampFrontService.findStampTrSortAndAttendSortYnByStpTrEventId(eventId);
        //판 인덱스가 없거나, 순차참여가 아니면 리턴
        if (PredicateUtils.isNull(stampSortAttend.getStpPanId())) {
            return;
        }
        //판에 연결되어있는 이벤트 목록 가져오기
        List<StampEventPanTrModel> stampTrList = stampSodarService.findStampEventPanTrListByStpPanId(stampSortAttend.getStpPanId());
        if (PredicateUtils.isNullList(stampTrList)) {
            return;
        }

        for (WinningButtonResDto winningButton : winningButtonList) {
            int stpPanId = stampSortAttend.getStpPanId();
            //마지막 이벤트
            if (PredicateUtils.isEqualNumber(stampSortAttend.getStpTrSort(), stampTrList.size())) {
                //스탬프 판 버튼일때
                if (PredicateUtils.isEqualsStr(winningButton.getButtonActionType(), WinningButtonActionTypeDefine.STMPAN.name())) {
                    winningButton.setMoveLocation(WinningButtonMoveLocationDefine.STMPAN.name());
                    winningButton.setEventId(stampSortAttend.getStampEventId());
                }
            } else {
                //스탬프 판 버튼일때
                if (PredicateUtils.isEqualsStr(winningButton.getButtonActionType(), WinningButtonActionTypeDefine.STMPAN.name())) {
                    winningButton.setMoveLocation(WinningButtonMoveLocationDefine.STMPAN.name());
                    winningButton.setEventId(stampSortAttend.getStampEventId());
                }
                //다음 이벤트 이동 버튼일때
                if (PredicateUtils.isEqualsStr(winningButton.getButtonActionType(), WinningButtonActionTypeDefine.NEXTEVT.name())) {
                    if (PredicateUtils.isLowerThan(stampSortAttend.getStpTrSort(), stampTrList.size())) {
                        StampEventPanTrModel nextStampPanTr = stampFrontService.findNextStampEventPanTrByStpPanIdAndSort(stpPanId, stampSortAttend.getStpTrSort());

                        if (PredicateUtils.isNotNull(nextStampPanTr.getStpPanTrId())) {
                            if (PredicateUtils.isNull(nextStampPanTr.getStpTrEventId()) && PredicateUtils.isNull(nextStampPanTr.getStpTrPid())) {
                                return;
                            } else {
                                //위치기반 이벤트일때 - 스탬프판
                                if (PredicateUtils.isNotNull(nextStampPanTr.getStpTrPid())) {
                                    winningButton.setMoveLocation(WinningButtonMoveLocationDefine.STMPAN.name());
                                    winningButton.setEventId(stampSortAttend.getStampEventId());
                                }
                                //AR, 서베이고 이벤트등 일때
                                if (PredicateUtils.isNotNull(nextStampPanTr.getStpTrEventId())) {
                                    winningButton.setMoveLocation(WinningButtonMoveLocationDefine.NEXTEVT.name());
                                    winningButton.setEventId(nextStampPanTr.getStpTrEventId());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public LinkedList<ArEventWinningEntity> getArEventWinningListByEventIndexAndMappingNumber(int eventIndex, int mappingNumber, String eventType) {
        if (PredicateUtils.isEqualZero(mappingNumber)) {
            return null;
        } else {
            return arEventFrontService.findArEventWinningListByEventIdxAndMappingNumber(eventIndex, mappingNumber, eventType);
        }
    }

    @Override
    public List<StpPanTrRowNumByWinningVO> getStpPanTrRowNumByWinning(int stpId, String attendType, String attendValue) {
        if (PredicateUtils.isEqualZero(stpId) || PredicateUtils.isNull(attendType) || PredicateUtils.isNull(attendValue)) {
            return null;
        } else {
            return stampFrontService.findStpPanTrRowNumByWinning(stpId, attendType, attendValue);
        }
    }

    @Override
    public void injectStampAttendValue(EventWinningReqDto eventWinningReqDto, StampEventMainModel stampEventMainModel) {
        if (StampEventMainModel.isMdnCondition(stampEventMainModel)) {
            if (PredicateUtils.isNull(eventWinningReqDto.getPhoneNumber())) {
                eventWinningReqDto.setAttendValue("");
            } else {
                eventWinningReqDto.setAttendValue(eventWinningReqDto.getPhoneNumber());
            }
        } else {
            if (PredicateUtils.isNull(eventWinningReqDto.getAttendCode())) {
                eventWinningReqDto.setAttendValue("");
            } else {
                eventWinningReqDto.setAttendValue(eventWinningReqDto.getAttendCode());
            }
        }
    }
}
