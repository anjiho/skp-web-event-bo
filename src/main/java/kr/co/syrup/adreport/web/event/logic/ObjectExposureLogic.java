package kr.co.syrup.adreport.web.event.logic;

import kr.co.syrup.adreport.framework.common.annotation.LoggingTimeFilter;
import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.web.event.define.EventLogExposureLimitDefine;
import kr.co.syrup.adreport.web.event.define.EventLogicalTypeDefine;
import kr.co.syrup.adreport.web.event.define.ExposureCoordinateTypeDefine;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import kr.co.syrup.adreport.web.event.dto.response.ArEventObjectCacheResDto;
import kr.co.syrup.adreport.web.event.dto.response.ProximityResDto;
import kr.co.syrup.adreport.web.event.entity.ArEventEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventObjectEntity;
import kr.co.syrup.adreport.web.event.mybatis.vo.ArEventByEventIdAtObjectExposureVO;
import kr.co.syrup.adreport.web.event.mybatis.vo.EventLogWinningLimitMapperVO;
import kr.co.syrup.adreport.web.event.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ObjectExposureLogic {
    
    @Autowired
    private SkApiLogic skApiLogic;

    @Autowired
    private LogService logService;

    public List<ArEventObjectEntity> conditionExposureObjectImproveLogic(ArEventByEventIdAtObjectExposureVO arEvent, List<ArEventObjectEntity> orgArEventObjectEntityList, String attendCode, String latitude, String longitude) {
        List<ArEventObjectEntity> arEventObjectEntityList = orgArEventObjectEntityList.stream().collect(Collectors.toList());

        if ( arEvent.getEventLogicalType().equals(EventLogicalTypeDefine.기본형.value()) || arEvent.getEventLogicalType().equals(EventLogicalTypeDefine.브릿지형.value()) ) {
            //log.info("1-1) 타입이 기본형 / 브릿지형인 경우 2번 로직 수행");

            if (PredicateUtils.isNotNullList(arEventObjectEntityList)) {

                //log.info("2) 전체 오브젝트 리스트로 <노출 제어 로직> 을 반복 수행 <노출 제어 로직 - START>");
                for (Iterator<ArEventObjectEntity>it = arEventObjectEntityList.iterator(); it.hasNext();) {
                    ArEventObjectEntity arEventObject = it.next();

                    //log.info("오브젝트 ID {} ", arEventObject.getArEventObjectId());

                    //log.info("============== 확률 체크 선행 시작 =========================");
                    boolean isExposure = false;
                    if (PredicateUtils.isEqualY(arEventObject.getExposurePercentType())) {
                        //log.info("============== 확률 체크 할때 =========================");
                        isExposure = EventUtils.percent(Float.valueOf(arEventObject.getExposurePercent()));
                        if (!isExposure) {
                            //log.info("============== 확률안에 못들어갔을때 목록 삭제 =========================");
                            it.remove();
                            continue;
                        }
                        //log.info("============== 확률안에 들어갔을때 다음 조건 시작 =========================");
                    }

                    if (PredicateUtils.isNull(arEventObject.getAttendCodeExposureType())) arEventObject.setAttendCodeExposureType(StringDefine.N.name());

                    if (PredicateUtils.isEqualN(arEventObject.getExposureControlType()))
                    {
                        //log.info("가) 노출 제어 선택여부가 선택안함인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");

                    } else if (PredicateUtils.isEqualY(arEventObject.getExposureControlType())) {
                        //log.info("나) 노출 제어 선택여부가 선택함인 경우 다) 로직부터 순차 수행");
                        //log.info("다) AR 참여조건이 위치설정조건인지 체크");

                        boolean isLimitArEventIdObjectSort = false, isLimitArEventIdObjectSortAttendCode = false,
                                isLimitArEventIdObjectSortAttendCodeToday = false ,isLimitArEventIdObjectSortHour = false,
                                isLimitArEventIdObjectSortToday =false;

                        List<EventLogWinningLimitMapperVO> exposureLimitList = logService.getEventLogExposureLimitListByArEventId(arEvent.getArEventId());

                        if (!PredicateUtils.isNullList(exposureLimitList)) {
                            //ar_event_object_id + object_sort 기준 전체제한 여부 체크
                            Optional<EventLogWinningLimitMapperVO> arEventIdObjectSortOptional = exposureLimitList.stream()
                                    .filter(limit -> PredicateUtils.isEqualsStr(
                                            limit.getCode(),
                                            StringTools.joinStringsNoSeparator(String.valueOf(arEvent.getArEventId()), String.valueOf(arEventObject.getObjectSort())))
                                    )
                                    .findAny();

                            //ar_event_object_id + object_sort + attend_code 기준 전체제한 여부 체크
                            Optional<EventLogWinningLimitMapperVO> arEventIdObjectSortAttendCodeOptional  = exposureLimitList.stream()
                                    .filter(limit -> PredicateUtils.isEqualsStr(
                                            limit.getCode(),
                                            StringTools.joinStringsNoSeparator(String.valueOf(arEvent.getArEventId()), String.valueOf(arEventObject.getObjectSort()), attendCode))
                                    )
                                    .findAny();

                            //ar_event_object_id + object_sort + attend_code 기준 일일 제한 여부 체크
                            Optional<EventLogWinningLimitMapperVO> arEventIdObjectSortAttendCodeTodayOptional  = exposureLimitList.stream()
                                    .filter(limit -> PredicateUtils.isEqualsStr(
                                            limit.getCode(),
                                            StringTools.joinStringsNoSeparator(
                                                    String.valueOf(arEvent.getArEventId()), String.valueOf(arEventObject.getObjectSort()), attendCode, DateUtils.getNowMMDD()))
                                    )
                                    .findAny();

                            //ar_event_object_id + object_sort 기준 시간 제한 여부 체크
                            Optional<EventLogWinningLimitMapperVO> arEventIdObjectSortHourOptional  = exposureLimitList.stream()
                                    .filter(limit -> PredicateUtils.isEqualsStr(
                                            limit.getCode(),
                                            StringTools.joinStringsNoSeparator(
                                                    String.valueOf(arEvent.getArEventId()), String.valueOf(arEventObject.getObjectSort()), DateUtils.getNowHour()))
                                    )
                                    .findAny();

                            //ar_event_object_id + object_sort 기준 일일 제한 여부 체크
                            Optional<EventLogWinningLimitMapperVO> arEventIdObjectSortTodayOptional  = exposureLimitList.stream()
                                    .filter(limit -> PredicateUtils.isEqualsStr(
                                            limit.getCode(), StringTools.joinStringsNoSeparator(
                                                    String.valueOf(arEvent.getArEventId()), String.valueOf(arEventObject.getObjectSort()), DateUtils.getNowMMDD()))
                                    )
                                    .findAny();

                            if (arEventIdObjectSortOptional.isPresent())                isLimitArEventIdObjectSort = true;
                            if (arEventIdObjectSortAttendCodeOptional.isPresent())      isLimitArEventIdObjectSortAttendCode = true;
                            if (arEventIdObjectSortAttendCodeTodayOptional.isPresent()) isLimitArEventIdObjectSortAttendCodeToday = true;
                            if (arEventIdObjectSortHourOptional.isPresent())            isLimitArEventIdObjectSortHour = true;
                            if (arEventIdObjectSortTodayOptional.isPresent())           isLimitArEventIdObjectSortToday = true;
                        }

                        if (arEvent.getLocationSettingYn()) {
                            //log.info("다-1) AR 참여조건이 위치설정조건인 경우");

                            if (PredicateUtils.isEqualY(arEventObject.getLocationExposureControlType())) {
                                //log.info("다-1-1) 위치 노출 제어가 선택함인 경우 ");
                                //위경도로 pid가 있는지 확인
                                ProximityResDto proximityResDto = new ProximityResDto();
                                if (PredicateUtils.isNotNull(arEventObject.getLocationExposureControlPid())) {
                                    if (PredicateUtils.isEqualsStr(arEventObject.getPidCoordinateType(), ExposureCoordinateTypeDefine.RELATIVE.name())) {
                                        proximityResDto = skApiLogic.callProximityApiLogic(arEventObject.getLocationExposureControlPid(), latitude, longitude);
                                    }
                                    //proximityResDto.setEventExist("Y");
                                }

                                if (proximityResDto.getTid() != null) {
                                    if (PredicateUtils.isEqualN(proximityResDto.getEventExist())) {
                                        //log.info("다-1-1-1) 해당 오브젝트의 위치노출 PID 값을 확인하여 현재 해당 위치가 아닌 경우 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");
                                        it.remove();
                                        continue;
                                    } else if (PredicateUtils.isEqualY(proximityResDto.getEventExist())) {
                                        //log.info("다-1-1-2) 해당 오브젝트의 위치노출 PID 값을 확인하여 현재 해당 위치인 경우 라) 로직을 수행");
                                        //log.info("다-2) AR 참여조건이 위치설정조건가 아닌 경우 라) 로직을 수행");

                                        //log.info("라) 최대 노출 수 체크");
                                        if (PredicateUtils.isEqualN(arEventObject.getMaxExposureType())) {
                                            //log.info("라-1) 최대 노출수가 제한 없음인 경우 마) 로직을 수행");

                                            //log.info("마) 일 노출 수 체크");
                                            if (PredicateUtils.isEqualN(arEventObject.getDayExposureType())) {
                                                //log.info("마-1) 일 노출수가 제한없음인 경우 바) 로직을 수행");

                                                //log.info("바) 시간당 노출 수 체크");
                                                if (PredicateUtils.isEqualN(arEventObject.getHourExposureType())) {
                                                    //log.info("바-1) 시간당 노출수가 제한없음인 경우 사) 로직을 수행");

                                                    //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                    if (arEvent.getArAttendConditionCodeYn()) {
                                                        //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                        if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType()))
                                                        {
                                                            //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                            //log.info("아) 노출확률 체크");
                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                continue;
                                                            }

                                                        } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                            //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                            //제한테이블 조건
                                                            if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                                it.remove();
                                                                continue;
                                                            }

                                                            //전체기한일떄
                                                            if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                                if (!isLimitArEventIdObjectSortAttendCode) {
                                                                    //참여번호당(전체개수) 노출로그 개수
                                                                    int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);

                                                                    if (arEventObject.getAttendCodeExposureCount() <= attendCodeTotalLogCount) {
                                                                        //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                        try {
                                                                            logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                        } catch (DuplicateKeyException e) {
                                                                            log.error("conditionExposureObject error {}" , e.getMessage());
                                                                            it.remove();
                                                                            continue;
                                                                        }
                                                                        it.remove();
                                                                        continue;

                                                                    } else if (arEventObject.getAttendCodeExposureCount() > attendCodeTotalLogCount) {
                                                                        //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                        //log.info("아) 노출확률 체크");
                                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                            continue;
                                                                        }
                                                                    }
                                                                } else {
                                                                    it.remove();
                                                                    continue;
                                                                }
                                                                //1일일떄
                                                            } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                                if (!isLimitArEventIdObjectSortAttendCodeToday) {
                                                                    //참여번호당(1일기준) 노출로그 개수
                                                                    int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);

                                                                    if (arEventObject.getAttendCodeExposureCount() <= attendCodeOneDayLogCount) {
                                                                        //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                        try {
                                                                            logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                        } catch (DuplicateKeyException e) {
                                                                            log.error("conditionExposureObject error {}" , e.getMessage());
                                                                            it.remove();
                                                                            continue;
                                                                        }
                                                                        it.remove();
                                                                        continue;

                                                                    } else if (arEventObject.getAttendCodeExposureCount() > attendCodeOneDayLogCount) {
                                                                        //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                        //log.info("아) 노출확률 체크");
                                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                            continue;
                                                                        }
                                                                    }
                                                                } else {
                                                                    it.remove();
                                                                    continue;
                                                                }
                                                            }
                                                        }

                                                    } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                        //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                        //log.info("아) 노출확률 체크");
                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                            continue;
                                                        }
                                                    }

                                                } else if (PredicateUtils.isEqualY(arEventObject.getHourExposureType())) {

                                                    if (!isLimitArEventIdObjectSortHour) {
                                                        //시간당 노출 로그 개수
                                                        int maxHourExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortCreatedHour(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                                        if (maxHourExposureLogCount < arEventObject.getHourExposureCount()) {
                                                            //log.info("바-2) 시간당 노출수가 노출수 지정이며 노출 건수 미초과시 사) 로직을 수행");

                                                            //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                            if (arEvent.getArAttendConditionCodeYn()) {
                                                                //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                                if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                                    //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                                    //log.info("아) 노출확률 체크");
                                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                        continue;
                                                                    }

                                                                } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                                    //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                                    //제한테이블 조건
                                                                    if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                                        it.remove();
                                                                        continue;
                                                                    }

                                                                    //전체기한일떄
                                                                    if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                                        if (!isLimitArEventIdObjectSortAttendCode) {
                                                                            //참여번호당(전체개수) 노출로그 개수
                                                                            int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                            if (arEventObject.getAttendCodeExposureCount() <= attendCodeTotalLogCount) {
                                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                                try {
                                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                                } catch (DuplicateKeyException e) {
                                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                                    it.remove();
                                                                                    continue;
                                                                                }
                                                                                it.remove();
                                                                                continue;

                                                                            } else if (arEventObject.getAttendCodeExposureCount() > attendCodeTotalLogCount) {
                                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                                //log.info("아) 노출확률 체크");
                                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                                    continue;
                                                                                }
                                                                            }
                                                                        } else {
                                                                            it.remove();
                                                                            continue;
                                                                        }
                                                                        //1일일떄
                                                                    } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                                        if (!isLimitArEventIdObjectSortAttendCodeToday) {
                                                                            //참여번호당(1일기준) 노출로그 개수
                                                                            int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                            if (arEventObject.getAttendCodeExposureCount() <= attendCodeOneDayLogCount) {
                                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                                try {
                                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                                } catch (DuplicateKeyException e) {
                                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                                    it.remove();
                                                                                    continue;
                                                                                }
                                                                                it.remove();
                                                                                continue;

                                                                            } else if (arEventObject.getAttendCodeExposureCount() > attendCodeOneDayLogCount) {
                                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                                //log.info("아) 노출확률 체크");
                                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                                    continue;
                                                                                }
                                                                            }
                                                                        } else {
                                                                            it.remove();
                                                                            continue;
                                                                        }
                                                                    }
                                                                }

                                                            } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                                //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                                //log.info("아) 노출확률 체크");
                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                    continue;
                                                                }
                                                            }

                                                        } else if (PredicateUtils.isGreaterThanEqualTo(maxHourExposureLogCount, arEventObject.getHourExposureCount())) {
                                                            //log.info("바-3) 시간당 노출시가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");
                                                            //log.info("=============================== hour limit ==================================" + arEventObject.getObjectSort());
                                                            try {
                                                                logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), null, EventLogExposureLimitDefine.ID_SORT_HOUR.name());
                                                            } catch (DuplicateKeyException e) {
                                                                log.error("conditionExposureObject error {}" , e.getMessage());
                                                                it.remove();
                                                                continue;
                                                            }
                                                            it.remove();
                                                            continue;
                                                        }
                                                    } else {
                                                        it.remove();
                                                        continue;
                                                    }
                                                }

                                            } else if (PredicateUtils.isEqualY(arEventObject.getDayExposureType())) {
                                                if (isLimitArEventIdObjectSortToday) {
                                                    it.remove();
                                                    continue;
                                                }
                                                // 일노출 로그 개수
                                                int maxDayExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                                if (maxDayExposureLogCount < arEventObject.getDayExposureCount()) {
                                                    //log.info("마-2) 일 노출수가 노출수 지정이며 노출 건수 미초과시 바) 로직을 수행");

                                                    //log.info("바) 시간당 노출 수 체크");
                                                    if (PredicateUtils.isEqualN(arEventObject.getHourExposureType())) {
                                                        //log.info("바-1) 시간당 노출수가 제한없음인 경우 사) 로직을 수행");

                                                        //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                        if (arEvent.getArAttendConditionCodeYn()) {
                                                            //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                            if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                                //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                                //log.info("아) 노출확률 체크");
                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                    continue;
                                                                }

                                                            } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                                //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                                if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                                    it.remove();
                                                                    continue;
                                                                }

                                                                //전체기한일떄
                                                                if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {
                                                                    if (!isLimitArEventIdObjectSortAttendCode) {
                                                                        //참여번호당(전체개수) 노출로그 개수
                                                                        int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                        if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                            //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");
                                                                            try {
                                                                                logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                            }catch (DuplicateKeyException e) {
                                                                                log.error("conditionExposureObject error {}" , e.getMessage());
                                                                                it.remove();
                                                                                continue;
                                                                            }
                                                                            it.remove();
                                                                            continue;

                                                                        } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                                            //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                            //log.info("아) 노출확률 체크");
                                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                                continue;
                                                                            }
                                                                        }
                                                                    } else {
                                                                        it.remove();
                                                                        continue;
                                                                    }
                                                                    //1일일떄
                                                                } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {
                                                                    if (!isLimitArEventIdObjectSortAttendCodeToday) {
                                                                        //참여번호당(1일기준) 노출로그 개수
                                                                        int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                        if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                            //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");
                                                                            try {
                                                                                logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                            } catch (DuplicateKeyException e) {
                                                                                log.error("conditionExposureObject error {}" , e.getMessage());
                                                                                it.remove();
                                                                                continue;
                                                                            }
                                                                            it.remove();
                                                                            continue;

                                                                        } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                                            //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                            //log.info("아) 노출확률 체크");
                                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                                continue;
                                                                            }
                                                                        }
                                                                    } else {
                                                                        it.remove();
                                                                        continue;
                                                                    }
                                                                }
                                                            }

                                                        } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                            //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                            //log.info("아) 노출확률 체크");
                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                continue;
                                                            }
                                                        }

                                                    } else if (PredicateUtils.isEqualY(arEventObject.getHourExposureType())) {
                                                        if (isLimitArEventIdObjectSortHour) {
                                                            it.remove();
                                                            continue;
                                                        }
                                                        //시간당 노출 로그 개수
                                                        int maxHourExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortCreatedHour(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                                        if (PredicateUtils.isGreaterThan(arEventObject.getHourExposureCount(), maxHourExposureLogCount)) {
                                                            //log.info("바-2) 시간당 노출수가 노출수 지정이며 노출 건수 미초과시 사) 로직을 수행");

                                                            //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                            if (arEvent.getArAttendConditionCodeYn()) {
                                                                //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                                if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                                    //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                                    //log.info("아) 노출확률 체크");
                                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                        continue;
                                                                    }
                                                                } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                                    //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                                    if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                                        it.remove();
                                                                        continue;
                                                                    }

                                                                    //전체기한일떄
                                                                    if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {
                                                                        if (!isLimitArEventIdObjectSortAttendCode) {
                                                                            //참여번호당(전체개수) 노출로그 개수
                                                                            int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                            if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");
                                                                                try {
                                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                                } catch (DuplicateKeyException e) {
                                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                                    it.remove();
                                                                                    continue;
                                                                                }
                                                                                it.remove();
                                                                                continue;

                                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                                //log.info("아) 노출확률 체크");
                                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                                    continue;
                                                                                }
                                                                            }
                                                                        } else {
                                                                            it.remove();
                                                                            continue;
                                                                        }
                                                                        //1일일떄
                                                                    } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {
                                                                        if (!isLimitArEventIdObjectSortAttendCodeToday) {
                                                                            //참여번호당(1일기준) 노출로그 개수
                                                                            int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                            if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");
                                                                                try {
                                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                                } catch (DuplicateKeyException e) {
                                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                                    it.remove();
                                                                                    continue;
                                                                                }
                                                                                it.remove();
                                                                                continue;

                                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                                //log.info("아) 노출확률 체크");
                                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                                    continue;
                                                                                }
                                                                            }
                                                                        } else {
                                                                            it.remove();
                                                                            continue;
                                                                        }
                                                                    }
                                                                }

                                                            } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                                //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                                //log.info("아) 노출확률 체크");
                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                    continue;
                                                                }
                                                            }

                                                        } else if (PredicateUtils.isGreaterThanEqualTo(maxHourExposureLogCount, arEventObject.getHourExposureCount())) {
                                                            //log.info("바-3) 시간당 노출시가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");
                                                            try {
                                                                logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), null, EventLogExposureLimitDefine.ID_SORT_HOUR.name());
                                                            } catch (DuplicateKeyException e) {
                                                                log.error("conditionExposureObject error {}" , e.getMessage());
                                                                it.remove();
                                                                continue;
                                                            }
                                                            it.remove();
                                                            continue;
                                                        }
                                                    }

                                                } else if (PredicateUtils.isGreaterThanEqualTo(maxDayExposureLogCount, arEventObject.getDayExposureCount())) {
                                                    //log.info("마-3) 일 노출수가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");
                                                    try {
                                                        logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, null, EventLogExposureLimitDefine.ID_SORT_TODAY.name());
                                                    } catch (DuplicateKeyException e) {
                                                        log.error("conditionExposureObject error {}" , e.getMessage());
                                                        it.remove();
                                                        continue;
                                                    }
                                                    it.remove();
                                                    continue;
                                                }
                                            }

                                        } else if (PredicateUtils.isEqualY(arEventObject.getMaxExposureType())) {
                                            //=========================================> 여기부터 시작 <=======================================================
                                            if (isLimitArEventIdObjectSort || isLimitArEventIdObjectSortToday || isLimitArEventIdObjectSortHour) {
                                                //log.info("노출 제한 테이블 > 최대 당첨 제한 걸렸을때");
                                                it.remove();
                                                continue;
                                            }

                                            if (!isLimitArEventIdObjectSort) {
                                                // 노출로그 총 개수
                                                int maxExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSort(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                                if (PredicateUtils.isGreaterThan(arEventObject.getMaxExposureCount(), maxExposureLogCount)) {
                                                    //log.info("라-2) 최대 노출수가 노출수 지정이며 노출 건수 미초과시 마) 로직을 수행");

                                                    //log.info("마) 일 노출 수 체크");
                                                    if (PredicateUtils.isEqualN(arEventObject.getDayExposureType())) {
                                                        //log.info("마-1) 일 노출수가 제한없음인 경우 바) 로직을 수행");

                                                        //log.info("바) 시간당 노출 수 체크");
                                                        if (PredicateUtils.isEqualN(arEventObject.getHourExposureType())) {
                                                            //log.info("바-1) 시간당 노출수가 제한없음인 경우 사) 로직을 수행");

                                                            //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                            if (arEvent.getArAttendConditionCodeYn()) {
                                                                //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                                if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                                    //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                                    //log.info("아) 노출확률 체크");
                                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                        continue;
                                                                    }

                                                                } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                                    //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                                    if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                                        it.remove();
                                                                        continue;
                                                                    }

                                                                    //전체기한일떄
                                                                    if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {
                                                                        if (isLimitArEventIdObjectSortAttendCode) {
                                                                            it.remove();
                                                                            continue;
                                                                        }
                                                                        //참여번호당(전체개수) 노출로그 개수
                                                                        int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                        if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                            //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                            try {
                                                                                logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                            } catch (DuplicateKeyException e) {
                                                                                log.error("conditionExposureObject error {}" , e.getMessage());
                                                                                it.remove();
                                                                                continue;
                                                                            }
                                                                            it.remove();
                                                                            continue;

                                                                        } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                                            //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                            //log.info("아) 노출확률 체크");
                                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                                continue;
                                                                            }
                                                                        }
                                                                        //1일일떄
                                                                    } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                                        if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                                            it.remove();
                                                                            continue;
                                                                        }
                                                                        //참여번호당(1일기준) 노출로그 개수
                                                                        int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                        if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                            //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                            try {
                                                                                logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                            } catch (DuplicateKeyException e) {
                                                                                log.error("conditionExposureObject error {}" , e.getMessage());
                                                                                it.remove();
                                                                                continue;
                                                                            }
                                                                            it.remove();
                                                                            continue;

                                                                        } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                                            //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                            //log.info("아) 노출확률 체크");
                                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                                continue;
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                            } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                                //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                                //log.info("아) 노출확률 체크");
                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                    continue;
                                                                }
                                                            }

                                                        } else if (PredicateUtils.isEqualY(arEventObject.getHourExposureType())) {

                                                            if (isLimitArEventIdObjectSortHour) {
                                                                it.remove();
                                                                continue;
                                                            }
                                                            //시간당 노출 로그 개수
                                                            int maxHourExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortCreatedHour(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                                            if (PredicateUtils.isGreaterThan(arEventObject.getHourExposureCount(), maxHourExposureLogCount)) {
                                                                //log.info("바-2) 시간당 노출수가 노출수 지정이며 노출 건수 미초과시 사) 로직을 수행");

                                                                //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                                if (arEvent.getArAttendConditionCodeYn()) {
                                                                    //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                                    if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                                        //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                                        //log.info("아) 노출확률 체크");
                                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                            continue;
                                                                        }

                                                                    } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                                        //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                                        if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                                            it.remove();
                                                                            continue;
                                                                        }

                                                                        //전체기한일떄
                                                                        if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                                            if (isLimitArEventIdObjectSortAttendCode) {
                                                                                it.remove();
                                                                                continue;
                                                                            }

                                                                            //참여번호당(전체개수) 노출로그 개수
                                                                            int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                            if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                                try {
                                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                                } catch (DuplicateKeyException e) {
                                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                                    it.remove();
                                                                                    continue;
                                                                                }
                                                                                it.remove();
                                                                                continue;

                                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");
                                                                                //log.info("아) 노출확률 체크");
                                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                                    continue;
                                                                                }
                                                                            }
                                                                            //1일일떄
                                                                        } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {
                                                                            if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                                                it.remove();
                                                                                continue;
                                                                            }

                                                                            //참여번호당(1일기준) 노출로그 개수
                                                                            int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                            if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                                try {
                                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                                } catch (DuplicateKeyException e) {
                                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                                    it.remove();
                                                                                    continue;
                                                                                }
                                                                                it.remove();
                                                                                continue;

                                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                                //log.info("아) 노출확률 체크");
                                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                                    continue;
                                                                                }
                                                                            }
                                                                        }
                                                                    }

                                                                } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                                    //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                                    //log.info("아) 노출확률 체크");
                                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                        continue;
                                                                    }
                                                                }

                                                            } else if (maxHourExposureLogCount >= arEventObject.getHourExposureCount()) {
                                                                //log.info("바-3) 시간당 노출시가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                try {
                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), null, EventLogExposureLimitDefine.ID_SORT_HOUR.name());
                                                                } catch (DuplicateKeyException e) {
                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                    it.remove();
                                                                    continue;
                                                                }
                                                                it.remove();
                                                                continue;
                                                            }
                                                        }

                                                    } else if (PredicateUtils.isEqualY(arEventObject.getDayExposureType())) {

                                                        if (isLimitArEventIdObjectSortToday) {
                                                            it.remove();
                                                            continue;
                                                        }

                                                        // 일노출 로그 개수
                                                        int maxDayExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                                        if (maxDayExposureLogCount < arEventObject.getDayExposureCount()) {
                                                            //log.info("마-2) 일 노출수가 노출수 지정이며 노출 건수 미초과시 바) 로직을 수행");

                                                            //log.info("바) 시간당 노출 수 체크");
                                                            if (PredicateUtils.isEqualN(arEventObject.getHourExposureType())) {
                                                                //log.info("바-1) 시간당 노출수가 제한없음인 경우 사) 로직을 수행");

                                                                //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                                if (arEvent.getArAttendConditionCodeYn()) {
                                                                    //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                                    if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                                        //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                                        //log.info("아) 노출확률 체크");
                                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                            continue;
                                                                        }

                                                                    } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                                        //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                                        if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                                            it.remove();
                                                                            continue;
                                                                        }

                                                                        //전체기한일떄
                                                                        if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                                            if (isLimitArEventIdObjectSortAttendCode) {
                                                                                it.remove();
                                                                                continue;
                                                                            }

                                                                            //참여번호당(전체개수) 노출로그 개수
                                                                            int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                            if (arEventObject.getAttendCodeExposureCount() <= attendCodeTotalLogCount) {
                                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                                try {
                                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                                } catch (DuplicateKeyException e) {
                                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                                    it.remove();
                                                                                    continue;
                                                                                }
                                                                                it.remove();
                                                                                continue;

                                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                                //log.info("아) 노출확률 체크");
                                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                                    continue;
                                                                                }
                                                                            }
                                                                            //1일일떄
                                                                        } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                                            if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                                                it.remove();
                                                                                continue;
                                                                            }

                                                                            //참여번호당(1일기준) 노출로그 개수
                                                                            int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                            if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                                try {
                                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                                } catch (DuplicateKeyException e) {
                                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                                    it.remove();
                                                                                    continue;
                                                                                }
                                                                                it.remove();
                                                                                continue;

                                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                                //log.info("아) 노출확률 체크");
                                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                                    continue;
                                                                                }
                                                                            }
                                                                        }
                                                                    }

                                                                } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                                    //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                                    //log.info("아) 노출확률 체크");
                                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                        continue;
                                                                    }
                                                                }

                                                            } else if (PredicateUtils.isEqualY(arEventObject.getHourExposureType())) {

                                                                if (isLimitArEventIdObjectSortHour) {
                                                                    it.remove();
                                                                    continue;
                                                                }

                                                                //시간당 노출 로그 개수
                                                                int maxHourExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortCreatedHour(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                                                if (PredicateUtils.isGreaterThan(arEventObject.getHourExposureCount(), maxHourExposureLogCount)) {
                                                                    //log.info("바-2) 시간당 노출수가 노출수 지정이며 노출 건수 미초과시 사) 로직을 수행");

                                                                    //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                                    if (arEvent.getArAttendConditionCodeYn()) {
                                                                        //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                                        if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                                            //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                                            //log.info("아) 노출확률 체크");
                                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                                continue;
                                                                            }

                                                                        } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                                            //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                                            //전체기한일떄
                                                                            if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                                                if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                                                    it.remove();
                                                                                    continue;
                                                                                }

                                                                                //참여번호당(전체개수) 노출로그 개수
                                                                                int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                                if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                                    //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                                    try {
                                                                                        logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                                    } catch (DuplicateKeyException e) {
                                                                                        log.error("conditionExposureObject error {}" , e.getMessage());
                                                                                        it.remove();
                                                                                        continue;
                                                                                    }
                                                                                    it.remove();
                                                                                    continue;

                                                                                } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                                                    //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                                    //log.info("아) 노출확률 체크");
                                                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                                        continue;
                                                                                    }
                                                                                }
                                                                                //1일일떄
                                                                            } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                                                if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                                                    it.remove();
                                                                                    continue;
                                                                                }

                                                                                //참여번호당(1일기준) 노출로그 개수
                                                                                int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                                if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                                    //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                                    try {
                                                                                        logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                                    } catch (DuplicateKeyException e) {
                                                                                        log.error("conditionExposureObject error {}" , e.getMessage());
                                                                                        it.remove();
                                                                                        continue;
                                                                                    }
                                                                                    it.remove();
                                                                                    continue;

                                                                                } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                                                    //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                                    //log.info("아) 노출확률 체크");
                                                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");

                                                                                    }
                                                                                }
                                                                            }
                                                                        }

                                                                    } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                                        //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                                        //log.info("아) 노출확률 체크");
                                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                            continue;
                                                                        }
                                                                    }

                                                                } else if (PredicateUtils.isGreaterThanEqualTo(maxHourExposureLogCount, arEventObject.getHourExposureCount())) {
                                                                    //log.info("바-3) 시간당 노출시가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                    try {
                                                                        logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), null, EventLogExposureLimitDefine.ID_SORT_HOUR.name());
                                                                    } catch (DuplicateKeyException e) {
                                                                        log.error("conditionExposureObject error {}" , e.getMessage());
                                                                        it.remove();
                                                                        continue;
                                                                    }
                                                                    it.remove();
                                                                    continue;
                                                                }
                                                            }

                                                        } else if (PredicateUtils.isGreaterThanEqualTo(maxDayExposureLogCount, arEventObject.getDayExposureCount())) {
                                                            //log.info("마-3) 일 노출수가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");
                                                            try {
                                                                logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, null, EventLogExposureLimitDefine.ID_SORT_TODAY.name());
                                                            } catch (DuplicateKeyException e) {
                                                                log.error("conditionExposureObject error {}" , e.getMessage());
                                                                it.remove();
                                                                continue;
                                                            }
                                                            it.remove();
                                                            continue;
                                                        }
                                                    }

                                                } else if (PredicateUtils.isGreaterThanEqualTo(maxExposureLogCount, arEventObject.getMaxExposureCount())) {
                                                    //log.info("라-3) 최대 노출수가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                    try {
                                                        logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, null, EventLogExposureLimitDefine.ID_SORT.name());
                                                    } catch (DuplicateKeyException e) {
                                                        log.error("conditionExposureObject error {}" , e.getMessage());
                                                        it.remove();
                                                        continue;
                                                    }
                                                    it.remove();
                                                    continue;

                                                }   //라-3) 최대 노출수가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트 끝
                                            }   //노출 제한 최대 당첨 조건
                                        }   //라-1) 최대 노출수가 제한 없음인 경우 마) 로직을 수행 끝
                                    }   //다-1-2) 해당 오브젝트의 위치노출 PID 값을 확인하여 현재 해당 위치인 경우 라) 로직을 수행 끝
                                }   // 위경도로 pid가 있는지 끝

                            } else if (PredicateUtils.isEqualN(arEventObject.getLocationExposureControlType())) {
                                //log.info("라) 최대 노출 수 체크");
                                if (PredicateUtils.isEqualN(arEventObject.getMaxExposureType())) {
                                    //log.info("라-1) 최대 노출수가 제한 없음인 경우 마) 로직을 수행");

                                    //log.info("마) 일 노출 수 체크");
                                    if (PredicateUtils.isEqualN(arEventObject.getDayExposureType())) {
                                        //log.info("마-1) 일 노출수가 제한없음인 경우 바) 로직을 수행");

                                        //log.info("바) 시간당 노출 수 체크");
                                        if (PredicateUtils.isEqualN(arEventObject.getHourExposureType())) {
                                            //log.info("바-1) 시간당 노출수가 제한없음인 경우 사) 로직을 수행");

                                            //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                            if (arEvent.getArAttendConditionCodeYn()) {
                                                //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                    //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                    //log.info("아) 노출확률 체크");
                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                        continue;
                                                    }

                                                } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                    //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                    if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                        it.remove();
                                                        continue;
                                                    }

                                                    //전체기한일떄
                                                    if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                        if (isLimitArEventIdObjectSortAttendCode) {
                                                            it.remove();
                                                            continue;
                                                        }

                                                        //참여번호당(전체개수) 노출로그 개수
                                                        int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                        if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                            //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                            try {
                                                                logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                            } catch (DuplicateKeyException e) {
                                                                log.error("conditionExposureObject error {}" , e.getMessage());
                                                                it.remove();
                                                                continue;
                                                            }
                                                            it.remove();
                                                            continue;

                                                        } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                            //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                            //log.info("아) 노출확률 체크");
                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                continue;
                                                            }
                                                        }
                                                        //1일일떄
                                                    } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                        if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                            it.remove();
                                                            continue;
                                                        }

                                                        //참여번호당(1일기준) 노출로그 개수
                                                        int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                        if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                            //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                            try {
                                                                logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                            } catch (DuplicateKeyException e) {
                                                                log.error("conditionExposureObject error {}" , e.getMessage());
                                                                it.remove();
                                                                continue;
                                                            }
                                                            it.remove();
                                                            continue;

                                                        } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                            //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                            //log.info("아) 노출확률 체크");
                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                continue;
                                                            }
                                                        }
                                                    }
                                                }

                                            } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                //log.info("아) 노출확률 체크");
                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                    continue;
                                                }
                                            }

                                        } else if (PredicateUtils.isEqualY(arEventObject.getHourExposureType())) {

                                            if (isLimitArEventIdObjectSortHour) {
                                                it.remove();
                                                continue;
                                            }

                                            //시간당 노출 로그 개수
                                            int maxHourExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortCreatedHour(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                            if (PredicateUtils.isGreaterThan(arEventObject.getHourExposureCount(), maxHourExposureLogCount)) {
                                                //log.info("바-2) 시간당 노출수가 노출수 지정이며 노출 건수 미초과시 사) 로직을 수행");

                                                //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                if (arEvent.getArAttendConditionCodeYn()) {
                                                    //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                    if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                        //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                        //log.info("아) 노출확률 체크");
                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                            continue;
                                                        }
                                                    } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                        //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                        if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                            it.remove();
                                                            continue;
                                                        }

                                                        //전체기한일떄
                                                        if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                            if (isLimitArEventIdObjectSortAttendCode) {
                                                                it.remove();
                                                                continue;
                                                            }

                                                            //참여번호당(전체개수) 노출로그 개수
                                                            int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                            if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                try {
                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                } catch (DuplicateKeyException e) {
                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                    it.remove();
                                                                    continue;
                                                                }
                                                                it.remove();
                                                                continue;

                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                //log.info("아) 노출확률 체크");
                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                    continue;
                                                                }
                                                            }
                                                            //1일일떄
                                                        } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                            if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                                it.remove();
                                                                continue;
                                                            }

                                                            //참여번호당(1일기준) 노출로그 개수
                                                            int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                            if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                try {
                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                } catch (DuplicateKeyException e) {
                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                    it.remove();
                                                                    continue;
                                                                }
                                                                it.remove();
                                                                continue;

                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                //log.info("아) 노출확률 체크");
                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                    continue;
                                                                }
                                                            }
                                                        }
                                                    }

                                                } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                    //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                    //log.info("아) 노출확률 체크");
                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                        continue;
                                                    }
                                                }

                                            } else if (PredicateUtils.isGreaterThanEqualTo(maxHourExposureLogCount, arEventObject.getHourExposureCount())) {
                                                //log.info("바-3) 시간당 노출시가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                try {
                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), null, EventLogExposureLimitDefine.ID_SORT_HOUR.name());
                                                } catch (DuplicateKeyException e) {
                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                    it.remove();
                                                    continue;
                                                }
                                                it.remove();
                                                continue;
                                            }
                                        }

                                    } else if (PredicateUtils.isEqualY(arEventObject.getDayExposureType())) {

                                        if (isLimitArEventIdObjectSortToday) {
                                            it.remove();
                                            continue;
                                        }

                                        // 일노출 로그 개수
                                        int maxDayExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                        if (PredicateUtils.isGreaterThan(arEventObject.getDayExposureCount(), maxDayExposureLogCount)) {
                                            //log.info("마-2) 일 노출수가 노출수 지정이며 노출 건수 미초과시 바) 로직을 수행");

                                            //log.info("바) 시간당 노출 수 체크");
                                            if (PredicateUtils.isEqualN(arEventObject.getHourExposureType())) {
                                                //log.info("바-1) 시간당 노출수가 제한없음인 경우 사) 로직을 수행");

                                                //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                if (arEvent.getArAttendConditionCodeYn()) {
                                                    //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                    if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                        //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                        //log.info("아) 노출확률 체크");
                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                            continue;
                                                        }

                                                    } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                        //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                        if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                            it.remove();
                                                            continue;
                                                        }

                                                        //전체기한일떄
                                                        if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                            if (isLimitArEventIdObjectSortAttendCode) {
                                                                it.remove();
                                                                continue;
                                                            }

                                                            //참여번호당(전체개수) 노출로그 개수
                                                            int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                            if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                try {
                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                } catch (DuplicateKeyException e) {
                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                    it.remove();
                                                                    continue;
                                                                }
                                                                it.remove();
                                                                continue;


                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                //log.info("아) 노출확률 체크");
                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                    continue;
                                                                }
                                                            }
                                                            //1일일떄
                                                        } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                            if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                                it.remove();
                                                                continue;
                                                            }

                                                            //참여번호당(1일기준) 노출로그 개수
                                                            int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                            if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                try {
                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                } catch (DuplicateKeyException e) {
                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                    it.remove();
                                                                    continue;
                                                                }
                                                                it.remove();
                                                                continue;

                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                //log.info("아) 노출확률 체크");
                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                    continue;
                                                                }
                                                            }
                                                        }
                                                    }

                                                } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                    //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                    //log.info("아) 노출확률 체크");
                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                        continue;
                                                    }
                                                }

                                            } else if (PredicateUtils.isEqualY(arEventObject.getHourExposureType())) {

                                                if (isLimitArEventIdObjectSortHour) {
                                                    it.remove();
                                                    continue;
                                                }

                                                //시간당 노출 로그 개수
                                                int maxHourExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortCreatedHour(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                                if (maxHourExposureLogCount < arEventObject.getHourExposureCount()) {
                                                    //log.info("바-2) 시간당 노출수가 노출수 지정이며 노출 건수 미초과시 사) 로직을 수행");

                                                    //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                    if (arEvent.getArAttendConditionCodeYn()) {
                                                        //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                        if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                            //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                            //log.info("아) 노출확률 체크");
                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                continue;
                                                            }
                                                        } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                            //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                            if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                                it.remove();
                                                                continue;
                                                            }

                                                            //전체기한일떄
                                                            if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                                if (isLimitArEventIdObjectSortAttendCode) {
                                                                    it.remove();
                                                                    continue;
                                                                }

                                                                //참여번호당(전체개수) 노출로그 개수
                                                                int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                    //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                    try {
                                                                        logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                    } catch (DuplicateKeyException e) {
                                                                        log.error("conditionExposureObject error {}" , e.getMessage());
                                                                        it.remove();
                                                                        continue;
                                                                    }
                                                                    it.remove();
                                                                    continue;

                                                                } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                                    //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                    //log.info("아) 노출확률 체크");
                                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                        continue;
                                                                    }
                                                                }
                                                                //1일일떄
                                                            } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                                if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                                    it.remove();
                                                                    continue;
                                                                }

                                                                //참여번호당(1일기준) 노출로그 개수
                                                                int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                    //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                    try {
                                                                        logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                    } catch (DuplicateKeyException e) {
                                                                        log.error("conditionExposureObject error {}" , e.getMessage());
                                                                        it.remove();
                                                                        continue;
                                                                    }
                                                                    it.remove();
                                                                    continue;

                                                                } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                                    //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                    //log.info("아) 노출확률 체크");
                                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                        continue;
                                                                    }
                                                                }
                                                            }
                                                        }

                                                    } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                        //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                        //log.info("아) 노출확률 체크");
                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                            continue;
                                                        }
                                                    }

                                                } else if (PredicateUtils.isGreaterThanEqualTo(maxHourExposureLogCount, arEventObject.getHourExposureCount())) {
                                                    //log.info("바-3) 시간당 노출시가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                    try {
                                                        logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), null, EventLogExposureLimitDefine.ID_SORT_HOUR.name());
                                                    } catch (DuplicateKeyException e) {
                                                        log.error("conditionExposureObject error {}" , e.getMessage());
                                                        it.remove();
                                                        continue;
                                                    }
                                                    it.remove();
                                                    continue;
                                                }
                                            }


                                        } else if (PredicateUtils.isGreaterThanEqualTo(maxDayExposureLogCount, arEventObject.getDayExposureCount())) {
                                            //log.info("마-3) 일 노출수가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                            try {
                                                logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, null, EventLogExposureLimitDefine.ID_SORT_TODAY.name());
                                            } catch (DuplicateKeyException e) {
                                                log.error("conditionExposureObject error {}" , e.getMessage());
                                                it.remove();
                                                continue;
                                            }
                                            it.remove();
                                            continue;
                                        }
                                    }

                                } else if (PredicateUtils.isEqualY(arEventObject.getMaxExposureType())) {

                                    if (isLimitArEventIdObjectSort || isLimitArEventIdObjectSortToday || isLimitArEventIdObjectSortHour) {
                                        it.remove();
                                        continue;
                                    }

                                    // 노출로그 총 개수
                                    int maxExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSort(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                    if (maxExposureLogCount < arEventObject.getMaxExposureCount()) {
                                        //log.info("라-2) 최대 노출수가 노출수 지정이며 노출 건수 미초과시 마) 로직을 수행");

                                        //log.info("마) 일 노출 수 체크");
                                        if (PredicateUtils.isEqualN(arEventObject.getDayExposureType())) {
                                            //log.info("마-1) 일 노출수가 제한없음인 경우 바) 로직을 수행");

                                            //log.info("바) 시간당 노출 수 체크");
                                            if (PredicateUtils.isEqualN(arEventObject.getHourExposureType())) {
                                                //log.info("바-1) 시간당 노출수가 제한없음인 경우 사) 로직을 수행");

                                                //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                if (arEvent.getArAttendConditionCodeYn()) {
                                                    //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                    if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                        //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                        //log.info("아) 노출확률 체크");
                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                            continue;
                                                        }

                                                    } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                        //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                        if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                            it.remove();
                                                            continue;
                                                        }

                                                        //전체기한일떄
                                                        if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                            if (isLimitArEventIdObjectSortAttendCode) {
                                                                it.remove();
                                                                continue;
                                                            }

                                                            //참여번호당(전체개수) 노출로그 개수
                                                            int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                            if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                try {
                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                } catch (DuplicateKeyException e) {
                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                    it.remove();
                                                                    continue;
                                                                }
                                                                it.remove();
                                                                continue;

                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                //log.info("아) 노출확률 체크");
                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                    continue;
                                                                }
                                                            }
                                                            //1일일떄
                                                        } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                            if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                                it.remove();
                                                                continue;
                                                            }

                                                            //참여번호당(1일기준) 노출로그 개수
                                                            int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                            if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");
                                                                try {
                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                } catch (DuplicateKeyException e) {
                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                    it.remove();
                                                                    continue;
                                                                }
                                                                it.remove();
                                                                continue;

                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                //log.info("아) 노출확률 체크");
                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                    continue;
                                                                }
                                                            }
                                                        }
                                                    }

                                                } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                    //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                    //log.info("아) 노출확률 체크");
                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                        continue;
                                                    }
                                                }

                                            } else if (PredicateUtils.isEqualY(arEventObject.getHourExposureType())) {

                                                if (isLimitArEventIdObjectSortHour) {
                                                    it.remove();
                                                    continue;
                                                }

                                                //시간당 노출 로그 개수
                                                int maxHourExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortCreatedHour(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                                if (PredicateUtils.isGreaterThan(arEventObject.getHourExposureCount(), maxHourExposureLogCount)) {
                                                    //log.info("바-2) 시간당 노출수가 노출수 지정이며 노출 건수 미초과시 사) 로직을 수행");

                                                    //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                    if (arEvent.getArAttendConditionCodeYn()) {
                                                        //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                        if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                            //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                            //log.info("아) 노출확률 체크");
                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                continue;
                                                            }
                                                        } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                            //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                            //전체기한일떄
                                                            if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                                if (isLimitArEventIdObjectSortAttendCode) {
                                                                    it.remove();
                                                                    continue;
                                                                }

                                                                //참여번호당(전체개수) 노출로그 개수
                                                                int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                    //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                    try {
                                                                        logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                    } catch (DuplicateKeyException e) {
                                                                        log.error("conditionExposureObject error {}" , e.getMessage());
                                                                        it.remove();
                                                                        continue;
                                                                    }
                                                                    it.remove();
                                                                    continue;

                                                                } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                                    //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                    //log.info("아) 노출확률 체크");
                                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                        continue;
                                                                    }
                                                                }
                                                                //1일일떄
                                                            } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                                if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                                    it.remove();
                                                                    continue;
                                                                }

                                                                //참여번호당(1일기준) 노출로그 개수
                                                                int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                    //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                    try {
                                                                        logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                    } catch (DuplicateKeyException e) {
                                                                        log.error("conditionExposureObject error {}" , e.getMessage());
                                                                        it.remove();
                                                                        continue;
                                                                    }
                                                                    it.remove();
                                                                    continue;

                                                                } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                                    //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                    //log.info("아) 노출확률 체크");
                                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                        continue;
                                                                    }
                                                                }
                                                            }
                                                        }

                                                    } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                        //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                        //log.info("아) 노출확률 체크");
                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                            continue;
                                                        }
                                                    }

                                                } else if (PredicateUtils.isGreaterThanEqualTo(maxHourExposureLogCount, arEventObject.getHourExposureCount())) {
                                                    //log.info("바-3) 시간당 노출시가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                    try {
                                                        logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), null, EventLogExposureLimitDefine.ID_SORT_HOUR.name());
                                                    } catch (DuplicateKeyException e) {
                                                        log.error("conditionExposureObject error {}" , e.getMessage());
                                                        it.remove();
                                                        continue;
                                                    }
                                                    it.remove();
                                                    continue;
                                                }
                                            }

                                        } else if (PredicateUtils.isEqualY(arEventObject.getDayExposureType())) {

                                            if (isLimitArEventIdObjectSortToday) {
                                                it.remove();
                                                continue;
                                            }

                                            // 일노출 로그 개수
                                            int maxDayExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                            if (maxDayExposureLogCount < arEventObject.getDayExposureCount()) {
                                                //log.info("마-2) 일 노출수가 노출수 지정이며 노출 건수 미초과시 바) 로직을 수행");

                                                //log.info("바) 시간당 노출 수 체크");
                                                if (PredicateUtils.isEqualN(arEventObject.getHourExposureType())) {
                                                    //log.info("바-1) 시간당 노출수가 제한없음인 경우 사) 로직을 수행");

                                                    //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                    if (arEvent.getArAttendConditionCodeYn()) {
                                                        //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                        if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                            //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                            //log.info("아) 노출확률 체크");
                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                continue;
                                                            }
                                                        } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                            //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                            if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                                it.remove();
                                                                continue;
                                                            }

                                                            //전체기한일떄
                                                            if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                                if (isLimitArEventIdObjectSortAttendCode) {
                                                                    it.remove();
                                                                    continue;
                                                                }

                                                                //참여번호당(전체개수) 노출로그 개수
                                                                int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                    //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                    try {
                                                                        logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                    } catch (DuplicateKeyException e) {
                                                                        log.error("conditionExposureObject error {}" , e.getMessage());
                                                                        it.remove();
                                                                        continue;
                                                                    }
                                                                    it.remove();
                                                                    continue;

                                                                } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                                    //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                    //log.info("아) 노출확률 체크");
                                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                        continue;
                                                                    }
                                                                }
                                                                //1일일떄
                                                            } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                                if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                                    it.remove();
                                                                    continue;
                                                                }

                                                                //참여번호당(1일기준) 노출로그 개수
                                                                int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                    //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                    try {
                                                                        logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                    } catch (DuplicateKeyException e) {
                                                                        log.error("conditionExposureObject error {}" , e.getMessage());
                                                                        it.remove();
                                                                        continue;
                                                                    }
                                                                    it.remove();
                                                                    continue;

                                                                } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                                    //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                    //log.info("아) 노출확률 체크");
                                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                        continue;
                                                                    }
                                                                }
                                                            }
                                                        }

                                                    } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                        //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                        //log.info("아) 노출확률 체크");
                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                            continue;
                                                        }
                                                    }

                                                } else if (PredicateUtils.isEqualY(arEventObject.getHourExposureType())) {

                                                    if (isLimitArEventIdObjectSortHour) {
                                                        it.remove();
                                                        continue;
                                                    }

                                                    //시간당 노출 로그 개수
                                                    int maxHourExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortCreatedHour(arEventObject.getArEventId(), arEventObject.getObjectSort());

                                                    if (PredicateUtils.isGreaterThan(arEventObject.getHourExposureCount(), maxHourExposureLogCount)) {
                                                        //log.info("바-2) 시간당 노출수가 노출수 지정이며 노출 건수 미초과시 사) 로직을 수행");

                                                        //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                        if (arEvent.getArAttendConditionCodeYn()) {
                                                            //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                            if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                                //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                                //log.info("아) 노출확률 체크");
                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                    continue;
                                                                }

                                                            } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                                //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                                if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                                    it.remove();
                                                                    continue;
                                                                }

                                                                //전체기한일떄
                                                                if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                                    if (isLimitArEventIdObjectSortAttendCode) {
                                                                        it.remove();
                                                                        continue;
                                                                    }

                                                                    //참여번호당(전체개수) 노출로그 개수
                                                                    int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                    if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                        //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                        try {
                                                                            logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                        } catch (DuplicateKeyException e) {
                                                                            log.error("conditionExposureObject error {}" , e.getMessage());
                                                                            it.remove();
                                                                            continue;
                                                                        }
                                                                        it.remove();
                                                                        continue;

                                                                    } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                                        //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                        //log.info("아) 노출확률 체크");
                                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                            continue;
                                                                        }
                                                                    }
                                                                    //1일일떄
                                                                } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                                    if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                                        it.remove();
                                                                        continue;
                                                                    }

                                                                    //참여번호당(1일기준) 노출로그 개수
                                                                    int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                    if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                        //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                        try {
                                                                            logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                        } catch (DuplicateKeyException e) {
                                                                            log.error("conditionExposureObject error {}" , e.getMessage());
                                                                            it.remove();
                                                                            continue;
                                                                        }
                                                                        it.remove();
                                                                        continue;

                                                                    } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                                        //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                        //log.info("아) 노출확률 체크");
                                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                            continue;
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                        } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                            //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                            //log.info("아) 노출확률 체크");
                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                continue;
                                                            }
                                                        }

                                                    } else if (PredicateUtils.isGreaterThanEqualTo(maxHourExposureLogCount, arEventObject.getHourExposureCount())) {
                                                        //log.info("바-3) 시간당 노출시가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                        try {
                                                            logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), null, EventLogExposureLimitDefine.ID_SORT_HOUR.name());
                                                        } catch (DuplicateKeyException e) {
                                                            log.error("conditionExposureObject error {}" , e.getMessage());
                                                            it.remove();
                                                            continue;
                                                        }
                                                        it.remove();
                                                        continue;

                                                    }
                                                }


                                            } else if (PredicateUtils.isGreaterThanEqualTo(maxDayExposureLogCount, arEventObject.getDayExposureCount())) {
                                                //log.info("마-3) 일 노출수가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                try {
                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, null, EventLogExposureLimitDefine.ID_SORT_TODAY.name());
                                                } catch (DuplicateKeyException e) {
                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                    it.remove();
                                                    continue;
                                                }
                                                it.remove();
                                                continue;
                                            }
                                        }

                                    } else if (PredicateUtils.isGreaterThanEqualTo(maxExposureLogCount, arEventObject.getMaxExposureCount())) {
                                        //log.info("라-3) 최대 노출수가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                        try {
                                            logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, null, EventLogExposureLimitDefine.ID_SORT.name());
                                        } catch (DuplicateKeyException e) {
                                            log.error("conditionExposureObject error {}" , e.getMessage());
                                            it.remove();
                                            continue;
                                        }
                                        it.remove();
                                        continue;
                                    }
                                }   //최대노출수 끝
                            }
                        }   // 다-1) AR 참여조건이 위치설정조건인 경우 끝

                        /**
                         *
                         */
                        if (!arEvent.getLocationSettingYn()) {
                            //log.info("다-2) AR 참여조건이 위치설정조건가 아닌 경우 라) 로직을 수행");

                            //log.info("라) 최대 노출 수 체크");
                            if (PredicateUtils.isEqualN(arEventObject.getMaxExposureType())) {
                                //log.info("라-1) 최대 노출수가 제한 없음인 경우 마) 로직을 수행");

                                //log.info("마) 일 노출 수 체크");
                                if (PredicateUtils.isEqualN(arEventObject.getDayExposureType())) {
                                    //log.info("마-1) 일 노출수가 제한없음인 경우 바) 로직을 수행");

                                    //log.info("바) 시간당 노출 수 체크");
                                    if (PredicateUtils.isEqualN(arEventObject.getHourExposureType())) {
                                        //log.info("바-1) 시간당 노출수가 제한없음인 경우 사) 로직을 수행");

                                        //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                        if (arEvent.getArAttendConditionCodeYn()) {
                                            //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                            if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                //log.info("아) 노출확률 체크");
                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                    continue;
                                                }
                                            } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                    it.remove();
                                                    continue;
                                                }

                                                //전체기한일떄
                                                if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                    if (isLimitArEventIdObjectSortAttendCode) {
                                                        it.remove();
                                                        continue;
                                                    }

                                                    //참여번호당(전체개수) 노출로그 개수
                                                    int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                    if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                        //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                        try {
                                                            logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                        } catch (DuplicateKeyException e) {
                                                            log.error("conditionExposureObject error {}" , e.getMessage());
                                                            it.remove();
                                                            continue;
                                                        }
                                                        it.remove();
                                                        continue;

                                                    } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                        //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                        //log.info("아) 노출확률 체크");
                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                            continue;
                                                        }
                                                    }
                                                    //1일일떄
                                                } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                    if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                        it.remove();
                                                        continue;
                                                    }

                                                    //참여번호당(1일기준) 노출로그 개수
                                                    int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                    if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                        //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                        try {
                                                            logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                        } catch (DuplicateKeyException e) {
                                                            log.error("conditionExposureObject error {}" , e.getMessage());
                                                            it.remove();
                                                            continue;
                                                        }
                                                        it.remove();
                                                        continue;

                                                    } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                        //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                        //log.info("아) 노출확률 체크");
                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                            continue;
                                                        }
                                                    }
                                                }
                                            }

                                        } else if (!arEvent.getArAttendConditionCodeYn()) {
                                            //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                            //log.info("아) 노출확률 체크");
                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                continue;
                                            }
                                        }

                                    } else if (PredicateUtils.isEqualY(arEventObject.getHourExposureType())) {

                                        if (isLimitArEventIdObjectSortHour) {
                                            it.remove();
                                            continue;
                                        }

                                        //시간당 노출 로그 개수
                                        int maxHourExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortCreatedHour(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                        if (PredicateUtils.isGreaterThan(arEventObject.getHourExposureCount(), maxHourExposureLogCount)) {
                                            //log.info("바-2) 시간당 노출수가 노출수 지정이며 노출 건수 미초과시 사) 로직을 수행");

                                            //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                            if (arEvent.getArAttendConditionCodeYn()) {
                                                //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                    //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                    //log.info("아) 노출확률 체크");
                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                        continue;
                                                    }

                                                } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                    //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                    if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                        it.remove();
                                                        continue;
                                                    }

                                                    //전체기한일떄
                                                    if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                        if (isLimitArEventIdObjectSortAttendCode) {
                                                            it.remove();
                                                            continue;
                                                        }

                                                        //참여번호당(전체개수) 노출로그 개수
                                                        int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                        if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                            //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                            try {
                                                                logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                            } catch (DuplicateKeyException e) {
                                                                log.error("conditionExposureObject error {}" , e.getMessage());
                                                                it.remove();
                                                                continue;
                                                            }
                                                            it.remove();
                                                            continue;



                                                        } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                            //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                            //log.info("아) 노출확률 체크");
                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                continue;
                                                            }
                                                        }
                                                        //1일일떄
                                                    } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                        if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                            it.remove();
                                                            continue;
                                                        }

                                                        //참여번호당(1일기준) 노출로그 개수
                                                        int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                        if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                            //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                            try {
                                                                logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                            } catch (DuplicateKeyException e) {
                                                                log.error("conditionExposureObject error {}" , e.getMessage());
                                                                it.remove();
                                                                continue;
                                                            }
                                                            it.remove();
                                                            continue;

                                                        } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                            //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                            //log.info("아) 노출확률 체크");
                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                continue;
                                                            }
                                                        }
                                                    }
                                                }

                                            } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                //log.info("아) 노출확률 체크");
                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                    continue;
                                                }
                                            }
                                        } else if (PredicateUtils.isGreaterThanEqualTo(maxHourExposureLogCount, arEventObject.getHourExposureCount())) {
                                            //log.info("바-3) 시간당 노출시가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");
                                            it.remove();
                                            continue;
                                        }
                                    }

                                } else if (PredicateUtils.isEqualY(arEventObject.getDayExposureType())) {

                                    if (isLimitArEventIdObjectSortToday) {
                                        it.remove();
                                        continue;
                                    }

                                    // 일노출 로그 개수
                                    int maxDayExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                    if (PredicateUtils.isGreaterThan(arEventObject.getDayExposureCount(), maxDayExposureLogCount)) {
                                        //log.info("마-2) 일 노출수가 노출수 지정이며 노출 건수 미초과시 바) 로직을 수행");

                                        //log.info("바) 시간당 노출 수 체크");
                                        if (PredicateUtils.isEqualN(arEventObject.getHourExposureType())) {
                                            //log.info("바-1) 시간당 노출수가 제한없음인 경우 사) 로직을 수행");

                                            //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                            if (arEvent.getArAttendConditionCodeYn()) {
                                                //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                    //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                    //log.info("아) 노출확률 체크");
                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                        continue;
                                                    }
                                                } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                    //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                    if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                        it.remove();
                                                        continue;
                                                    }

                                                    //전체기한일떄
                                                    if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                        if (isLimitArEventIdObjectSortAttendCode) {
                                                            it.remove();
                                                            continue;
                                                        }

                                                        //참여번호당(전체개수) 노출로그 개수
                                                        int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                        if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                            //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                            try {
                                                                logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                            } catch (DuplicateKeyException e) {
                                                                log.error("conditionExposureObject error {}" , e.getMessage());
                                                                it.remove();
                                                                continue;
                                                            }
                                                            it.remove();
                                                            continue;

                                                        } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                            //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                            //log.info("아) 노출확률 체크");
                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                continue;
                                                            }
                                                        }
                                                        //1일일떄
                                                    } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                        if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                            it.remove();
                                                            continue;
                                                        }

                                                        //참여번호당(1일기준) 노출로그 개수
                                                        int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                        if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                            //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                            try {
                                                                logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                            } catch (DuplicateKeyException e) {
                                                                log.error("conditionExposureObject error {}" , e.getMessage());
                                                                it.remove();
                                                                continue;
                                                            }
                                                            it.remove();
                                                            continue;

                                                        } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                            //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                            //log.info("아) 노출확률 체크");
                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                continue;
                                                            }
                                                        }
                                                    }
                                                }

                                            } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                //log.info("아) 노출확률 체크");
                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                    continue;
                                                }
                                            }

                                        } else if (PredicateUtils.isEqualY(arEventObject.getHourExposureType())) {

                                            if (isLimitArEventIdObjectSortHour) {
                                                it.remove();
                                                continue;
                                            }

                                            //시간당 노출 로그 개수
                                            int maxHourExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortCreatedHour(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                            if (maxHourExposureLogCount < arEventObject.getHourExposureCount()) {
                                                //log.info("바-2) 시간당 노출수가 노출수 지정이며 노출 건수 미초과시 사) 로직을 수행");

                                                //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                if (arEvent.getArAttendConditionCodeYn()) {
                                                    //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                    if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                        //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                        //log.info("아) 노출확률 체크");
                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                            continue;
                                                        }

                                                    } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                        //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                        if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                            it.remove();
                                                            continue;
                                                        }

                                                        //전체기한일떄
                                                        if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                            if (isLimitArEventIdObjectSortAttendCode) {
                                                                it.remove();
                                                                continue;
                                                            }

                                                            //참여번호당(전체개수) 노출로그 개수
                                                            int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                            if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                try {
                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                } catch (DuplicateKeyException e) {
                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                    it.remove();
                                                                    continue;
                                                                }
                                                                it.remove();
                                                                continue;

                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                //log.info("아) 노출확률 체크");
                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                    continue;
                                                                }
                                                            }
                                                            //1일일떄
                                                        } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                            if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                                it.remove();
                                                                continue;
                                                            }

                                                            //참여번호당(1일기준) 노출로그 개수
                                                            int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                            if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                try {
                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                } catch (DuplicateKeyException e) {
                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                    it.remove();
                                                                    continue;
                                                                }
                                                                it.remove();
                                                                continue;

                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                //log.info("아) 노출확률 체크");
                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                    continue;
                                                                }
                                                            }
                                                        }
                                                    }

                                                } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                    //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                    //log.info("아) 노출확률 체크");
                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                        continue;
                                                    }
                                                }

                                            } else if (PredicateUtils.isGreaterThanEqualTo(maxHourExposureLogCount, arEventObject.getHourExposureCount())) {
                                                //log.info("바-3) 시간당 노출시가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                try {
                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), null, EventLogExposureLimitDefine.ID_SORT_HOUR.name());
                                                } catch (DuplicateKeyException e) {
                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                    it.remove();
                                                    continue;
                                                }
                                                it.remove();
                                                continue;
                                            }
                                        }


                                    } else if (PredicateUtils.isGreaterThanEqualTo(maxDayExposureLogCount, arEventObject.getDayExposureCount())) {
                                        //log.info("마-3) 일 노출수가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                        try {
                                            logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, null, EventLogExposureLimitDefine.ID_SORT_TODAY.name());
                                        } catch (DuplicateKeyException e) {
                                            log.error("conditionExposureObject error {}" , e.getMessage());
                                            it.remove();
                                            continue;
                                        }
                                        it.remove();
                                        continue;
                                    }
                                }

                            } else if (PredicateUtils.isEqualY(arEventObject.getMaxExposureType())) {

                                if (isLimitArEventIdObjectSort || isLimitArEventIdObjectSortToday || isLimitArEventIdObjectSortHour) {
                                    it.remove();
                                    continue;
                                }

                                // 노출로그 총 개수
                                int maxExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSort(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                if (maxExposureLogCount < arEventObject.getMaxExposureCount()) {
                                    //log.info("라-2) 최대 노출수가 노출수 지정이며 노출 건수 미초과시 마) 로직을 수행");

                                    //log.info("마) 일 노출 수 체크");
                                    if (PredicateUtils.isEqualN(arEventObject.getDayExposureType())) {
                                        //log.info("마-1) 일 노출수가 제한없음인 경우 바) 로직을 수행");

                                        //log.info("바) 시간당 노출 수 체크");
                                        if (PredicateUtils.isEqualN(arEventObject.getHourExposureType())) {
                                            //log.info("바-1) 시간당 노출수가 제한없음인 경우 사) 로직을 수행");

                                            //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                            if (arEvent.getArAttendConditionCodeYn()) {
                                                //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                    //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                    //log.info("아) 노출확률 체크");
                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                        continue;
                                                    }

                                                } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                    //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                    if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                        it.remove();
                                                        continue;
                                                    }

                                                    //전체기한일떄
                                                    if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                        if (isLimitArEventIdObjectSortAttendCode) {
                                                            it.remove();
                                                            continue;
                                                        }

                                                        //참여번호당(전체개수) 노출로그 개수
                                                        int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                        if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                            //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                            try {
                                                                logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                            } catch (DuplicateKeyException e) {
                                                                log.error("conditionExposureObject error {}" , e.getMessage());
                                                                it.remove();
                                                                continue;
                                                            }
                                                            it.remove();
                                                            continue;

                                                        } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                            //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                            //log.info("아) 노출확률 체크");
                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                continue;
                                                            }
                                                        }
                                                        //1일일떄
                                                    } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                        if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                            it.remove();
                                                            continue;
                                                        }
                                                        //참여번호당(1일기준) 노출로그 개수
                                                        int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                        if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                            //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                            try {
                                                                logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                            } catch (DuplicateKeyException e) {
                                                                log.error("conditionExposureObject error {}" , e.getMessage());
                                                                it.remove();
                                                                continue;
                                                            }
                                                            it.remove();
                                                            continue;


                                                        } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                            //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                            //log.info("아) 노출확률 체크");
                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                continue;
                                                            }
                                                        }
                                                    }
                                                }

                                            } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                //log.info("아) 노출확률 체크");
                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                    continue;
                                                }
                                            }

                                        } else if (PredicateUtils.isEqualY(arEventObject.getHourExposureType())) {
                                            //시간당 노출 로그 개수
                                            int maxHourExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortCreatedHour(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                            if (PredicateUtils.isGreaterThan(arEventObject.getHourExposureCount(), maxHourExposureLogCount)) {
                                                //log.info("바-2) 시간당 노출수가 노출수 지정이며 노출 건수 미초과시 사) 로직을 수행");

                                                //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                if (arEvent.getArAttendConditionCodeYn()) {
                                                    //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                    if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                        //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                        //log.info("아) 노출확률 체크");
                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                            continue;
                                                        }

                                                    } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                        //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                        if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                            it.remove();
                                                            continue;
                                                        }

                                                        //전체기한일떄
                                                        if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                            if (isLimitArEventIdObjectSortAttendCode) {
                                                                it.remove();
                                                                continue;
                                                            }

                                                            //참여번호당(전체개수) 노출로그 개수
                                                            int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                            if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                try {
                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                } catch (DuplicateKeyException e) {
                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                    it.remove();
                                                                    continue;
                                                                }
                                                                it.remove();
                                                                continue;


                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                //log.info("아) 노출확률 체크");
                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                    continue;
                                                                }
                                                            }
                                                            //1일일떄
                                                        } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                            if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                                it.remove();
                                                                continue;
                                                            }
                                                            //참여번호당(1일기준) 노출로그 개수
                                                            int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                            if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                try {
                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                } catch (DuplicateKeyException e) {
                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                    it.remove();
                                                                    continue;
                                                                }
                                                                it.remove();
                                                                continue;

                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                //log.info("아) 노출확률 체크");
                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                    continue;
                                                                }
                                                            }
                                                        }
                                                    }

                                                } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                    //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                    //log.info("아) 노출확률 체크");
                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                        continue;
                                                    }
                                                }

                                            } else if (PredicateUtils.isGreaterThanEqualTo(maxHourExposureLogCount, arEventObject.getHourExposureCount())) {
                                                //log.info("바-3) 시간당 노출시가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");
                                                it.remove();
                                                continue;
                                            }
                                        }

                                    } else if (PredicateUtils.isEqualY(arEventObject.getDayExposureType())) {

                                        if (isLimitArEventIdObjectSortToday) {
                                            it.remove();
                                            continue;
                                        }

                                        // 일노출 로그 개수
                                        int maxDayExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                        if (maxDayExposureLogCount < arEventObject.getDayExposureCount()) {
                                            //log.info("마-2) 일 노출수가 노출수 지정이며 노출 건수 미초과시 바) 로직을 수행");

                                            //log.info("바) 시간당 노출 수 체크");
                                            if (PredicateUtils.isEqualN(arEventObject.getHourExposureType())) {
                                                //log.info("바-1) 시간당 노출수가 제한없음인 경우 사) 로직을 수행");

                                                //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                if (arEvent.getArAttendConditionCodeYn()) {
                                                    //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                    if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                        //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                        //log.info("아) 노출확률 체크");
                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                            continue;
                                                        }
                                                    } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                        //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                        if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                            it.remove();
                                                            continue;
                                                        }

                                                        //전체기한일떄
                                                        if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                            if (isLimitArEventIdObjectSortAttendCode) {
                                                                it.remove();
                                                                continue;
                                                            }

                                                            //참여번호당(전체개수) 노출로그 개수
                                                            int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                            if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                try {
                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                } catch (DuplicateKeyException e) {
                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                    it.remove();
                                                                    continue;
                                                                }
                                                                it.remove();
                                                                continue;

                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                //log.info("아) 노출확률 체크");
                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                    continue;
                                                                }
                                                            }
                                                            //1일일떄
                                                        } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                            if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                                it.remove();
                                                                continue;
                                                            }

                                                            //참여번호당(1일기준) 노출로그 개수
                                                            int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                            if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                try {
                                                                    logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                } catch (DuplicateKeyException e) {
                                                                    log.error("conditionExposureObject error {}" , e.getMessage());
                                                                    it.remove();
                                                                    continue;
                                                                }
                                                                it.remove();
                                                                continue;

                                                            } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                                //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                //log.info("아) 노출확률 체크");
                                                                if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                    //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                    continue;
                                                                }
                                                            }
                                                        }
                                                    }

                                                } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                    //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                    //log.info("아) 노출확률 체크");
                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                        continue;
                                                    }
                                                }

                                            } else if (PredicateUtils.isEqualY(arEventObject.getHourExposureType())) {

                                                if (isLimitArEventIdObjectSortHour) {
                                                    it.remove();
                                                    continue;
                                                }

                                                //시간당 노출 로그 개수
                                                int maxHourExposureLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortCreatedHour(arEventObject.getArEventId(), arEventObject.getObjectSort());
                                                if (PredicateUtils.isGreaterThan(arEventObject.getHourExposureCount(), maxHourExposureLogCount)) {
                                                    //log.info("바-2) 시간당 노출수가 노출수 지정이며 노출 건수 미초과시 사) 로직을 수행");

                                                    //log.info("사) AR 참여조건이 참여번호 인 경우 체크");
                                                    if (arEvent.getArAttendConditionCodeYn()) {
                                                        //log.info("사-1) AR 참여조건이 참여번호인 경우");

                                                        if (PredicateUtils.isEqualN(arEventObject.getAttendCodeExposureType())) {
                                                            //log.info("사-1-1) 참여번호당 노출 수가 제한없음인 경우 아) 로직을 수행");

                                                            //log.info("아) 노출확률 체크");
                                                            if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                continue;
                                                            }

                                                        } else if (PredicateUtils.isEqualY(arEventObject.getAttendCodeExposureType())) {
                                                            //log.info("사-1-2) 참여번호당 노출 수가 노출 수 지정인 경우");

                                                            if (isLimitArEventIdObjectSortAttendCode && isLimitArEventIdObjectSortAttendCodeToday) {
                                                                it.remove();
                                                                continue;
                                                            }

                                                            //전체기한일떄
                                                            if (PredicateUtils.isEqualZero(arEventObject.getAttendCodeLimitType())) {

                                                                if (isLimitArEventIdObjectSortAttendCode) {
                                                                    it.remove();
                                                                    continue;
                                                                }

                                                                //참여번호당(전체개수) 노출로그 개수
                                                                int attendCodeTotalLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                if (PredicateUtils.isGreaterThanEqualTo(attendCodeTotalLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                    //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 전체 기한내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                    try {
                                                                        logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, attendCode, EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                                    } catch (DuplicateKeyException e) {
                                                                        log.error("conditionExposureObject error {}" , e.getMessage());
                                                                        it.remove();
                                                                        continue;
                                                                    }
                                                                    it.remove();
                                                                    continue;

                                                                } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeTotalLogCount)) {
                                                                    //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                    //log.info("아) 노출확률 체크");
                                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                        continue;
                                                                    }
                                                                }
                                                                //1일일떄
                                                            } else if (PredicateUtils.isEqualNumber(arEventObject.getAttendCodeLimitType(), 1)) {

                                                                if (isLimitArEventIdObjectSortAttendCodeToday) {
                                                                    it.remove();
                                                                    continue;
                                                                }
                                                                //참여번호당(1일기준) 노출로그 개수
                                                                int attendCodeOneDayLogCount = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday(arEventObject.getArEventId(), arEventObject.getObjectSort(), attendCode);
                                                                if (PredicateUtils.isGreaterThanEqualTo(attendCodeOneDayLogCount, arEventObject.getAttendCodeExposureCount())) {
                                                                    //log.info("* 기간 제한 조건 확인 후 해당 참여번호당 노출건수가 1일 기간내 내 회수 초과라면 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                                    try {
                                                                        logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), attendCode, EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                                    } catch (DuplicateKeyException e) {
                                                                        log.error("conditionExposureObject error {}" , e.getMessage());
                                                                        it.remove();
                                                                        continue;
                                                                    }
                                                                    it.remove();
                                                                    continue;

                                                                } else if (PredicateUtils.isGreaterThan(arEventObject.getAttendCodeExposureCount(), attendCodeOneDayLogCount)) {
                                                                    //log.info("* 위 조건 불만족하여 노출 가능시 아) 로직을 수행");

                                                                    //log.info("아) 노출확률 체크");
                                                                    if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                                        //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                                        continue;
                                                                    }
                                                                }
                                                            }
                                                        }

                                                    } else if (!arEvent.getArAttendConditionCodeYn()) {
                                                        //log.info("사-2) AR 참여조건이 참여번호가 아닌 경우 아) 로직을 수행");

                                                        //log.info("아) 노출확률 체크");
                                                        if (PredicateUtils.isEqualN(arEventObject.getExposurePercentType())) {
                                                            //log.info("아-1) 노출확률이 전체노출인 경우 -> 해당 오브젝트 노출 처리 후 다음 오브젝트");
                                                            continue;
                                                        }
                                                    }

                                                } else if (PredicateUtils.isGreaterThanEqualTo(maxHourExposureLogCount, arEventObject.getHourExposureCount())) {
                                                    //log.info("바-3) 시간당 노출시가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                                    try {
                                                        logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), null, EventLogExposureLimitDefine.ID_SORT_HOUR.name());
                                                    } catch (DuplicateKeyException e) {
                                                        log.error("conditionExposureObject error {}" , e.getMessage());
                                                        it.remove();
                                                        continue;
                                                    }
                                                    it.remove();
                                                    continue;
                                                }
                                            }


                                        } else if (PredicateUtils.isGreaterThanEqualTo(maxDayExposureLogCount, arEventObject.getDayExposureCount())) {
                                            //log.info("마-3) 일 노출수가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                            try {
                                                logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), DateUtils.getNowMMDD(), null, null, EventLogExposureLimitDefine.ID_SORT_TODAY.name());
                                            } catch (DuplicateKeyException e) {
                                                log.error("conditionExposureObject error {}" , e.getMessage());
                                                it.remove();
                                                continue;
                                            }
                                            it.remove();
                                            continue;
                                        }
                                    }

                                } else if (PredicateUtils.isGreaterThanEqualTo(maxExposureLogCount, arEventObject.getMaxExposureCount())) {
                                    //log.info("라-3) 최대 노출수가 노출수 지정이며 노출 건수 초과시 -> 해당 오브젝트 비노출 처리 후 다음 오브젝트");

                                    try {
                                        logService.saveEventLogExposureLimit(arEvent.getArEventId(), arEventObject.getObjectSort(), null, null, null, EventLogExposureLimitDefine.ID_SORT.name());
                                    } catch (DuplicateKeyException e) {
                                        log.error("conditionExposureObject DuplicateKeyException error {}" , e.getMessage());
                                        it.remove();
                                        continue;
                                    } catch (Exception e) {
                                        log.error("conditionExposureObject Exception error {}" , e.getMessage());
                                        it.remove();
                                        continue;
                                    }
                                    it.remove();
                                    continue;
                                }
                            }   //최대노출수 끝
                        }
                    }
                } //for end
            }
        }
        return arEventObjectEntityList;
    }
}
