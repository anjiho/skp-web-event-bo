package kr.co.syrup.adreport.stamp.event.logic;

import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.exception.BaseException;
import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.stamp.event.define.StampWinningTypeDefine;
import kr.co.syrup.adreport.stamp.event.model.*;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.StampEventLogTrVO;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.StpPanTrRowNumByWinningVO;
import kr.co.syrup.adreport.stamp.event.service.StampFrontService;
import kr.co.syrup.adreport.stamp.event.service.StampLogService;
import kr.co.syrup.adreport.stamp.event.service.StampSodarService;
import kr.co.syrup.adreport.stamp.event.service.StampWinningService;
import kr.co.syrup.adreport.web.event.define.*;
import kr.co.syrup.adreport.web.event.dto.request.EventWinningReqDto;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.dto.response.GifticonOrderResDto;
import kr.co.syrup.adreport.web.event.dto.response.WinningButtonResDto;
import kr.co.syrup.adreport.web.event.dto.response.WinningResultResDto;
import kr.co.syrup.adreport.web.event.entity.ArEventWinningEntity;
import kr.co.syrup.adreport.web.event.logic.EventWinning;
import kr.co.syrup.adreport.web.event.logic.SkApiLogic;
import kr.co.syrup.adreport.web.event.mybatis.vo.EventLogWinningLimitMapperVO;
import kr.co.syrup.adreport.web.event.service.ArEventFrontService;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class StampWinningLogic {

    @Autowired
    private StampLogService stampLogService;

    @Autowired
    private StampWinningService stampWinningService;

    @Autowired
    private StampFrontService stampFrontService;

    @Autowired
    @Lazy
    private EventWinning eventWinning;

    @Autowired
    private ArEventFrontService arEventFrontService;

    @Autowired
    private AES256Utils aes256Utils;

    public ApiResultObjectDto improvedProcessStampEventWinning(EventWinningReqDto winningReqDto) {
        //이벤트 아이디 없으면 에러처리
        if (PredicateUtils.isNull(winningReqDto.getEventId())) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_EVENT_ID_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_EVENT_ID_NULL);
        }

        //스탬프 메인 정보 가져오기
        StampEventMainModel stampMain = stampFrontService.findStampEventMainByEventIdFromWinning(winningReqDto.getEventId());

        if (PredicateUtils.isNull(stampMain.getStpId())) {
            return this.failWinningLogic(winningReqDto, stampMain, false, null);
        } else {
            if (PredicateUtils.isNull(stampMain.getStpWinningType())) {
                stampMain.setStpWinningType(StampWinningTypeDefine.N.name());
            } else {
                //당첨이 교환형일때 > 당첨종류 : Y, 참여순번 설정 : N 변경
                if (PredicateUtils.isEqualsStr(stampMain.getStpWinningType(), StampWinningTypeDefine.EXCHANGE.name())) {
                    stampMain.setStpWinningType(StampWinningTypeDefine.Y.name());
                    stampMain.setStpAttendSortSettingYn(StringDefine.N.name());
                }
            }

            log.info("A) 스탬프 당첨제공유형 타입 체크");

            if (PredicateUtils.isEqualN(stampMain.getStpWinningType())) {
                log.info("A-1) 당첨없음인 경우 - 당첨로직 진행하지 않고 로직 종료 (당첨이 없는데 당첨시도는 꽝처리 필요없이 아예 오류처리 해야됨)");
                return this.failWinningLogic(winningReqDto, stampMain, false, null);
            } else {
                log.info("A-2) 당첨있음인 경우 - B 로직 수행");
                log.info("B) 중복당첨수 제한 체크 - 참여번호, 휴대폰번호 공통");

                //참여값 주입
                eventWinning.injectStampAttendValue(winningReqDto, stampMain);

                //참여값이 없으면 꽝처리
                if (PredicateUtils.isNull(winningReqDto.getAttendValue())) {
                    log.info("참여값이 없어서 꽝처리");
                    return this.failWinningLogic(winningReqDto, stampMain, false, null);
                }

                //주입된 참여값(핸드폰번호 or 참여번호)
                String attendValue = winningReqDto.getAttendValue();

                if (PredicateUtils.isEqualY(stampMain.getDuplicateWinningType())) {
                    log.info("B-1) 중복당첨수 제한 있음");

                    //1일제한여부
                    boolean isToday = PredicateUtils.isEqualNumber(stampMain.getDuplicateWinningLimitType(), 1);

                    //당첨성공 로그 개수 가져오기
                    int stampLogCount = stampLogService.findCountStampEventLogWinning(StampEventLogWinningModel.ofCount(stampMain.getStpId(), 0, stampMain.getStpAttendAuthCondition(), attendValue, !isToday ? null : Integer.parseInt(DateUtils.getNowYYMMDD())));

                    if (PredicateUtils.isGreaterThanEqualTo(stampLogCount, stampMain.getDuplicateWinningCount())) {
                        log.info("* 기존 당첨정보가 있는지 확인 후 당첨정보가 있다면, 기간제한 조건 확인 후 전체기한내 회수 초과라면 꽝처리 후 종료");
                        return this.failWinningLogic(winningReqDto, stampMain, false, null);
                    } else {
                        log.info("* 위 조건 불만족하여 당첨 가능시 C 로직 수행");
                        return this.checkStampWinningAttendSortNumber(winningReqDto, stampMain);
                    }
                } else {
                    log.info("B-2) 중복당첨수제한이 없다면 (N) 이면 C 로직 수행");
                    return this.checkStampWinningAttendSortNumber(winningReqDto, stampMain);
                }
            }
        }
    }

    private ApiResultObjectDto checkStampWinningAttendSortNumber(EventWinningReqDto winningReqDto, StampEventMainModel stampMain) {
        log.info("C) 참여순번 설정 체크");
        if (PredicateUtils.isNull(stampMain.getStpAttendSortSettingYn())) {
            return this.failWinningLogic(winningReqDto, stampMain, false, null);
        } else {
            int mappingNumber = 0;
            if (PredicateUtils.isEqualN(stampMain.getStpAttendSortSettingYn())) {
                log.info("C-1) 참여 순번 설정이 설정안함인 경우 - D 로직 수행");
                log.info("D) 참여순번 설정이 설정안함인 경우");

                if (PredicateUtils.isNotNull(winningReqDto.getStpPanTrId())) {
                    //스탬프 판 TR 순서 가져오기
                    int stampPanTrSort = stampFrontService.findStampPanTrSortByStampPanTrId(winningReqDto.getStpPanTrId());

                    if (PredicateUtils.isGreaterThanZero(stampPanTrSort)) {
                        mappingNumber = stampPanTrSort;
                    } else {
                        return this.failWinningLogic(winningReqDto, stampMain, false, null);
                    }
                } else {
                    return this.failWinningLogic(winningReqDto, stampMain, false, null);
                }
            } else {
                log.info("C-2) 참여 순번 설정이 설정인 경우 - E 로직 수행");
                log.info("E) 참여순번 설정이 설정인 경우  > 스탬프 참여 로그 개수로 참여 순번 일치 체크");
                log.info("E-1) 전체 당첨 배열 중, 현재 당첨시도하는 참여 순번과 일치하는 당첨항목이 있는지 체크");

                List<StpPanTrRowNumByWinningVO> resultList = eventWinning.getStpPanTrRowNumByWinning(stampMain.getStpId(), stampMain.getStpAttendAuthCondition(), winningReqDto.getAttendValue());
                if (PredicateUtils.isNotNullList(resultList)) {
                    Optional<StpPanTrRowNumByWinningVO> optional = resultList.stream().filter(data -> Objects.equals(data.getStpPanTrId(), winningReqDto.getStpPanTrId())).findAny();
                    if (optional.isPresent()) {
                        mappingNumber = optional.get().getRowNum();
                    } else {
                        return this.failWinningLogic(winningReqDto, stampMain, false, null);
                    }
                } else {
                    return this.failWinningLogic(winningReqDto, stampMain, false, null);
                }
            }
            //맵핑되어있는 당첨 정보 목록 가져오기
            LinkedList<ArEventWinningEntity>winningLinkedList = eventWinning.getArEventWinningListByEventIndexAndMappingNumber(stampMain.getStpId(), mappingNumber, EventTypeDefine.STAMP.name());

            if (PredicateUtils.isNullList(winningLinkedList)) {
                log.info("D-1-2) or E-1-2)");
                return this.failWinningLogic(winningReqDto, stampMain, false, null);
            } else {
                log.info("D-1-1) or E-1-1)");
                return this.sequentiallyRaffleWinningLogic(winningReqDto, stampMain, winningLinkedList);
            }
        }
    }

    private ApiResultObjectDto sequentiallyRaffleWinningLogic(EventWinningReqDto reqDto, StampEventMainModel stampMain, LinkedList<ArEventWinningEntity>winningLinkedList) {
        log.info("<당첨 처리 로직 - START>");
        int stpId = stampMain.getStpId();

        //제한테이블 목록 가져오기
        List<StampEventLogWinningLimitModel> winningLimit = stampLogService.findStampEventLogWinningLimitListByStpId(stampMain.getStpId());

        for (int i = 0; i < winningLinkedList.size(); i++) {
            ArEventWinningEntity winningInfo = winningLinkedList.get(i);
            log.info("winningInfo = {}", winningInfo.toString());
            int winningLinkedListSize = (winningLinkedList.size() - 1);

            int totalWinningLogCount = 0, dayWinningLogCount = 0, hourWinningLogCount = 0;

            boolean isFullTotalCount = false, isFullDayCount = false, isFullHourCount = false;

            boolean isLimitHour = false, isLimitDay = false, isLimitTotal = false;

            if (PredicateUtils.isNotNullList(winningLimit)) {
                //ID_SORT_TODAY_HOUR 코드 값 확인
                String hourCode = StringTools.joinStringsNoSeparator(String.valueOf(stpId), String.valueOf(winningInfo.getEventWinningSort()), DateUtils.getNowMMDD(), DateUtils.getNowHour());
                String dayCode = StringTools.joinStringsNoSeparator(String.valueOf(stpId), String.valueOf(winningInfo.getEventWinningSort()), DateUtils.getNowMMDD());
                String totalCode = StringTools.joinStringsNoSeparator(String.valueOf(stpId), String.valueOf(winningInfo.getEventWinningSort()));

                //ID_SORT_TODAY_HOUR 코드로 값이 존재하는지 체크
                Optional<StampEventLogWinningLimitModel> hourLimitOptional = winningLimit.stream().filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), hourCode)).findAny();
                //ID_SORT_TODAY 코드로 값이 존재하는지 체크
                Optional<StampEventLogWinningLimitModel> dayLimitOptional = winningLimit.stream().filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), dayCode)).findAny();
                //ID_SORT 코드로 값이 존재하는지 체크
                Optional<StampEventLogWinningLimitModel> totalLimitOptional = winningLimit.stream().filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), totalCode)).findAny();

                //값이 존재하면 true
                if (hourLimitOptional.isPresent()) {
                    log.info("winning limit 시간제한 걸림");
                    isLimitHour = true;
                    isFullHourCount = true;
                }
                if (dayLimitOptional.isPresent()) {
                    log.info("winning limit 일제한 걸림");
                    isLimitDay = true;
                    isFullDayCount = true;
                }
                if (totalLimitOptional.isPresent()) {
                    log.info("winning limit 전체제한 걸림");
                    isLimitTotal = true;
                    isFullTotalCount = true;
                }
            }
            //시간 당첨 제한 값 확인 끝
            //제한 테입블 값이 전부 true 면 오브젝트로 넘김
            if (isLimitHour && isLimitDay && isLimitTotal) {
                log.info("eventWinningLogic 제한데이터 > 전체, 일, 시간 당첨수량이 전부 MAX 일떄 ");
                //마지막 오브젝트인데 수량이 전부 소진됬을때 꽝처리
                if (i == winningLinkedListSize) {
                    log.info("sequentiallyRaffleWinningLogic 마지막 오브젝트여서 꽝!");
                    //꽝 로직
                    return this.failWinningLogic(reqDto, stampMain, false, null);
                } else if (i < winningLinkedListSize) {
                    log.info("sequentiallyRaffleWinningLogic 다음 오브젝트로 넘김~~ 현재 오브젝트 {} ", winningInfo.getArEventWinningId());
                    continue;
                } else {
                    return this.failWinningLogic(reqDto, stampMain, false, null);
                }
            }

            if (!isLimitDay && !isLimitTotal) {
                //시간당첨 수량 체크
                log.info("가) 시간당 당첨수량 확인");
                if (PredicateUtils.isGreaterThanZero(winningInfo.getHourWinningNumber())) {
                    if (!isFullHourCount) {
                        //로그에 있는 현재 시간당당첨 수량 가져오기
                        StampEventLogWinningModel findStampLogWinningModel = StampEventLogWinningModel.ofCount(stpId, winningInfo.getEventWinningSort(), null, Integer.parseInt(DateUtils.getNowYYMMDDHH()));
                        hourWinningLogCount = stampLogService.findCountStampEventLogWinning(findStampLogWinningModel);
                        log.info("sequentiallyRaffleWinningLogic 로그 시간당당첨 수량 :: {} ", hourWinningLogCount);
                        if (PredicateUtils.isGreaterThanEqualTo(hourWinningLogCount, winningInfo.getHourWinningNumber())) {
                            log.info("sequentiallyRaffleWinningLogic 시간당첨 수량 MAX !");
                            try {
                                stampLogService.saveStampEventLogWinningLimit(stampMain.getStpId(), winningInfo.getEventWinningSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), null, EventLogWinningLimitDefine.ID_SORT_TODAY_HOUR.name());
                            } catch (DuplicateKeyException e) {
                                log.error("saveEventLogWinningLimit error {} ", e);

                                if (i == winningLinkedListSize) {
                                    log.info("sequentiallyRaffleWinningLogic 마지막 오브젝트여서 꽝!");
                                    //꽝 로직
                                    return this.failWinningLogic(reqDto, stampMain, false, null);
                                } else if (i < winningLinkedListSize) {
                                    log.info("가-1) 시간당 당첨수량 초과인 경우 -> 다음 당첨정보");
                                    continue;
                                }
                            } finally {
                                if (i == winningLinkedListSize) {
                                    log.info("sequentiallyRaffleWinningLogic 마지막 오브젝트여서 꽝!");
                                    //꽝 로직
                                    return this.failWinningLogic(reqDto, stampMain, false, null);
                                } else if (i < winningLinkedListSize) {
                                    log.info("다-1) 전체 당첨수량 초과인 경우 -> 다음 당첨정보 ");
                                    continue;
                                }
                            }
                        } else {
                            log.info("가-2) 시간당 당첨수량 미초과인 경우 -> 나) 로직을 수행");
                        }
                    }
                } else {
                    if (i == winningLinkedListSize) {
                        log.info("sequentiallyRaffleWinningLogic 마지막 오브젝트여서 꽝!");
                        //꽝 로직
                        return this.failWinningLogic(reqDto, stampMain, false, null);
                    } else if (i < winningLinkedListSize) {
                        log.info("다-1) 전체 당첨수량 초과인 경우 -> 다음 당첨정보 ");
                        continue;
                    }
                }
            }

            if (!isLimitTotal) {
                //일당첨수량 체크
                log.info("나) 일 당첨수량 확인");
                if (PredicateUtils.isGreaterThanZero(winningInfo.getDayWinningNumber())) {
                    if (!isFullDayCount) {
                        //로그에 있는 일일 기준 당첨 수량 가져오기
                        StampEventLogWinningModel findStampLogWinningModel = StampEventLogWinningModel.ofCount(stpId, winningInfo.getEventWinningSort(), Integer.parseInt(DateUtils.getNowYYMMDD()), null);
                        dayWinningLogCount = stampLogService.findCountStampEventLogWinning(findStampLogWinningModel);
                        log.info("validateWinning 로그 일일 기준 당첨 수량 {}", dayWinningLogCount);

                        if (PredicateUtils.isGreaterThanEqualTo(dayWinningLogCount, winningInfo.getDayWinningNumber())) {
                            log.info("일당첨수량 MAX ! {}", dayWinningLogCount);
                            try {
                                //제한 테이블 ID_SORT_TODAY 값 저장
                                stampLogService.saveStampEventLogWinningLimit(stampMain.getStpId(), winningInfo.getEventWinningSort(), DateUtils.getNowMMDD(), null, null, EventLogWinningLimitDefine.ID_SORT_TODAY.name());
                            } catch (DuplicateKeyException e) {
                                log.error("saveEventLogWinningLimit error {} ", e);
                                if (i == winningLinkedListSize) {
                                    log.info("eventWinningLogic 마지막 오브젝트여서 꽝!");
                                    //꽝 로직
                                    return this.failWinningLogic(reqDto, stampMain, false, null);
                                } else if (i < winningLinkedListSize) {
                                    log.info("나-1) 일 당첨수량 초과인 경우 -> 다음 당첨정보");
                                    continue;
                                }
                            } finally {
                                if (i == winningLinkedListSize) {
                                    log.info("sequentiallyRaffleWinningLogic 마지막 오브젝트여서 꽝!");
                                    //꽝 로직
                                    return this.failWinningLogic(reqDto, stampMain, false, null);
                                } else if (i < winningLinkedListSize) {
                                    log.info("다-1) 전체 당첨수량 초과인 경우 -> 다음 당첨정보 ");
                                    continue;
                                }
                            }
                        } else {
                            log.info("나-2) 일 당첨수량 미초과인 경우 -> 다) 로직을 수행");
                        }
                    }
                } else {
                    if (i == winningLinkedListSize) {
                        log.info("sequentiallyRaffleWinningLogic 마지막 오브젝트여서 꽝!");
                        //꽝 로직
                        return this.failWinningLogic(reqDto, stampMain, false, null);
                    } else if (i < winningLinkedListSize) {
                        log.info("다-1) 전체 당첨수량 초과인 경우 -> 다음 당첨정보 ");
                        continue;
                    }
                }
            }

            //전체당첨수량 수량 체크
            if (PredicateUtils.isGreaterThanZero(winningInfo.getTotalWinningNumber())) {
                log.info("다) 전체 당첨수량 확인");
                if (!isFullTotalCount) {
                    //로그에 있는 전체당첨 수량 가져오기
                    StampEventLogWinningModel findStampLogWinningModel = StampEventLogWinningModel.ofAllCount(stpId, winningInfo.getEventWinningSort());
                    totalWinningLogCount = stampLogService.findCountStampEventLogWinning(findStampLogWinningModel);
                    log.info("sequentiallyRaffleWinningLogic 로그 전체당첨 수량 :: {} ", totalWinningLogCount);

                    if (PredicateUtils.isGreaterThanEqualTo(totalWinningLogCount, winningInfo.getTotalWinningNumber())) {
                        log.info("sequentiallyRaffleWinningLogic 전체수량 MAX ! ");
                        try {
                            //제한 테이블 ID_SORT 값 저장
                            stampLogService.saveStampEventLogWinningLimit(stampMain.getStpId(), winningInfo.getEventWinningSort(), null, null, null, EventLogWinningLimitDefine.ID_SORT.name());
                        } catch (Exception e) {
                            log.error("saveEventLogWinningLimit error {} ", e);
                            if (i == winningLinkedListSize) {
                                log.info("sequentiallyRaffleWinningLogic 마지막 오브젝트여서 꽝!");
                                //꽝 로직
                                return this.failWinningLogic(reqDto, stampMain, false, null);
                            } else if (i < winningLinkedListSize) {
                                log.info("다-1) 전체 당첨수량 초과인 경우 -> 다음 당첨정보 ");
                                continue;
                            }
                        } finally {
                            if (i == winningLinkedListSize) {
                                log.info("sequentiallyRaffleWinningLogic 마지막 오브젝트여서 꽝!");
                                //꽝 로직
                                return this.failWinningLogic(reqDto, stampMain, false, null);
                            } else if (i < winningLinkedListSize) {
                                log.info("다-1) 전체 당첨수량 초과인 경우 -> 다음 당첨정보 ");
                                continue;
                            }
                        }
                    }
                    log.info("다-2) 전체당첨수량 미초과인 경우 -> 라) 로직을 수행 ");
                } else {
                    if (i == winningLinkedListSize) {
                        log.info("sequentiallyRaffleWinningLogic 마지막 오브젝트여서 꽝!");
                        //꽝 로직
                        return this.failWinningLogic(reqDto, stampMain, false, null);
                    } else if (i < winningLinkedListSize) {
                        log.info("다-1) 전체 당첨수량 초과인 경우 -> 다음 당첨정보 ");
                        continue;
                    }
                }
            } else {
                return this.failWinningLogic(reqDto, stampMain, false, null);
            }

            log.info("라) 고객당 당첨제한 확인 -> 참여번호, 휴대폰 번호 공통 ");
            if (PredicateUtils.isEqualN(winningInfo.getUserWinningType())) {
                log.info("라-1) 고객당 당첨제한이 제한없음인 경우 -> 마) 로직을 수행");
                log.info("마) 당첨율 확인하여 당첨여부 결정");
                boolean isWinning = EventUtils.percent(Float.parseFloat(winningInfo.getWinningPercent()));
                if (isWinning) {
                    log.info("마-1) 당첨율 확인하여 당첨여부가 당첨인 경우 -> 당첨처리 후 순차적 반복로직 종료 ");
                    return this.successRaffleWinningLogic(reqDto, stampMain, winningInfo, winningLimit);
                } else {
                    if (i == winningLinkedListSize) {
                        return this.failWinningLogic(reqDto, stampMain, false, null);
                    } else if (i < winningLinkedListSize) {
                        log.info("* 다음 당첨정보");
                        continue;
                    } else {
                        return this.failWinningLogic(reqDto, stampMain, false, null);
                    }
                }
            } else {
                log.info("라-2) 고객당 당첨제한 있음인 경우 - 참여번호, 전화번호 공통");

                String attendValue = reqDto.getAttendValue();
                //참여값이 암호화된 전화번호일때 제한 테이블 확인을 위해 복호화
                if (PredicateUtils.isGreaterThan(attendValue.length(), 20)) {
                    attendValue = aes256Utils.decrypt(attendValue);
                }

                //전체제한 정의
                boolean isToday = false;
                //제한 테이블 검색 코드
                String searchCode = StringTools.joinStringsNoSeparator(String.valueOf(stpId), String.valueOf(winningInfo.getEventWinningSort()), attendValue);
                //제한 테이블 검색 종류
                String searchLimitCodeDesc = EventLogWinningLimitDefine.ID_WINNINGID_MDN.name();
                //당첨 성공 로그 검색 파라미터
                StampEventLogWinningModel findStampLogWinningModel = StampEventLogWinningModel.ofCount(stpId, winningInfo.getEventWinningSort(), stampMain.getStpAttendAuthCondition(), reqDto.getAttendValue());

                //1일제한일때
                if (PredicateUtils.isEqualNumber(winningInfo.getUserWinningLimitType(), 1)) {
                    //전체제한 정의
                    isToday = true;
                    //제한 테이블 검색 코드
                    searchCode = StringTools.joinStringsNoSeparator(String.valueOf(stpId), String.valueOf(winningInfo.getEventWinningSort()), attendValue, DateUtils.getNowMMDD());
                    //제한 테이블 검색 종류
                    searchLimitCodeDesc = EventLogWinningLimitDefine.ID_WINNINGID_MDN_TODAY.name();
                    //당첨 성공 로그 검색 파라미터
                    findStampLogWinningModel = StampEventLogWinningModel.ofCount(stpId, winningInfo.getEventWinningSort(), stampMain.getStpAttendAuthCondition(), reqDto.getAttendValue(), Integer.parseInt(DateUtils.getNowYYMMDD()));
                }

                //당첨제한 테이블 제한이 되어있는지 확인
                boolean isLimit = stampLogService.isStampEventLogWinningLimitByStpIdAndCode(stpId, searchCode, searchLimitCodeDesc);

                //제한테이블 제한이 되어있으면 꽝처리
                if (isLimit) {
                    if (i == winningLinkedListSize) {
                        log.info("고객당 당첨제한 > 전체기한 제한테이블의 데이터가 존재하여 꽝처리");
                        return this.failWinningLogic(reqDto, stampMain, false, null);
                    } else if (i < winningLinkedListSize) {
                        log.info("* 기간제한 조건 확인 후 해당 당첨정보의 당첨횟수가 전체 기한내 회수 초과라면 -> 다음 당첨정보");
                        continue;
                    } else {
                        return this.failWinningLogic(reqDto, stampMain, false, null);
                    }
                } else {
                    //스탬프 당첨 성공 로그 테이블 개수 조회
                    int userWinningCount = stampLogService.findCountStampEventLogWinning(findStampLogWinningModel);

                    //당첨성공 로그 개수 >= 셋팅된 고객당 당첨제한 개수 => 꽝
                    if (PredicateUtils.isGreaterThanEqualTo(userWinningCount, winningInfo.getUserWinningLimitCount())) {
                        if (i == winningLinkedListSize) {
                            try {
                                stampLogService.saveStampEventLogWinningLimit(stampMain.getStpId(), winningInfo.getEventWinningSort(), isToday ? DateUtils.getNowMMDD() : null, null, attendValue, searchLimitCodeDesc);
                            } catch (DuplicateKeyException e) {
                                log.error("saveStampEventLogWinningLimit error {} ", e);
                                return this.failWinningLogic(reqDto, stampMain, false, null);
                            }
                        } else if (i < winningLinkedListSize) {
                            log.info("* 기간제한 조건 확인 후 해당 당첨정보의 당첨횟수가 전체 기한내 회수 초과라면 -> 다음 당첨정보");
                            try {
                                stampLogService.saveStampEventLogWinningLimit(stampMain.getStpId(), winningInfo.getEventWinningSort(), isToday ? DateUtils.getNowMMDD() : null, null, attendValue, searchLimitCodeDesc);
                            } catch (DuplicateKeyException e) {
                                log.error("saveStampEventLogWinningLimit error {} ", e);
                                return this.failWinningLogic(reqDto, stampMain, false, null);
                            } finally {
                                log.info("* 다음 당첨정보");
                                continue;
                            }
                        } else {
                            return this.failWinningLogic(reqDto, stampMain, false, null);
                        }
                    } else {
                        log.info("마) 당첨율 확인하여 당첨여부 결정");
                        boolean isWinning = EventUtils.percent(Float.parseFloat(winningInfo.getWinningPercent()));
                        if (isWinning) {
                            log.info("마-1) 당첨율 확인하여 당첨여부가 당첨인 경우 -> 당첨처리 후 순차적 반복로직 종료 ");
                            return this.successRaffleWinningLogic(reqDto, stampMain, winningInfo, winningLimit);
                        } else {
                            if (i == winningLinkedListSize) {
                                return this.failWinningLogic(reqDto, stampMain, false, null);
                            } else if (i < winningLinkedListSize) {
                                log.info("* 다음 당첨정보");
                                continue;
                            } else {
                                return this.failWinningLogic(reqDto, stampMain, false, null);
                            }
                        }
                    }
                }
            }
        }
        return this.failWinningLogic(reqDto, stampMain, false, null);
    }

    private ApiResultObjectDto successRaffleWinningLogic(EventWinningReqDto reqDto, StampEventMainModel stampMain, ArEventWinningEntity winningEntity, List<StampEventLogWinningLimitModel> winningLimit) {
        int resultCode = HttpStatus.OK.value();

        int stpId = stampMain.getStpId();
        String attendValue = reqDto.getAttendValue();

        long insertedLogId = 0L;

        //stpId 없으면 꽝 처리
        if (PredicateUtils.isNull(winningEntity.getStpId())) {
            return this.failWinningLogic(reqDto, stampMain, true, insertedLogId);
        }

        //성공로그 저장 후 인덱스 리턴
        try {
            insertedLogId = stampLogService.saveStampEventLogWinning(stampMain.getStpId(), reqDto.getStpPanTrId(), stampMain.getStpAttendAuthCondition(), attendValue, stampMain.getStpWinningType(), winningEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        boolean isLimitHour = false;
        boolean isLimitDay = false;
        boolean isLimitTotal = false;

        if (!PredicateUtils.isNullList(winningLimit)) {
            //ID_SORT_TODAY_HOUR 코드 값 확인
            //시간 코드
            String hourCode = StringTools.joinStringsNoSeparator( String.valueOf(stampMain.getStpId()), String.valueOf(winningEntity.getEventWinningSort()), DateUtils.getNowMMDD(), DateUtils.getNowHour() );
            //일 코드
            String dayCode = StringTools.joinStringsNoSeparator( String.valueOf(stampMain.getStpId()), String.valueOf(winningEntity.getEventWinningSort()), DateUtils.getNowMMDD() );
            //전체기간 코드
            String totalCode = StringTools.joinStringsNoSeparator( String.valueOf(stampMain.getStpId()), String.valueOf(winningEntity.getEventWinningSort()) );

            //ID_SORT_TODAY_HOUR 코드로 값이 존재하는지 체크
            Optional<StampEventLogWinningLimitModel> hourLimitOptional = winningLimit.stream().filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), hourCode)).findAny();
            //ID_SORT_TODAY 코드로 값이 존재하는지 체크
            Optional<StampEventLogWinningLimitModel> dayLimitOptional = winningLimit.stream().filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), dayCode)).findAny();
            //ID_SORT 코드로 값이 존재하는지 체크
            Optional<StampEventLogWinningLimitModel> totalLimitOptional = winningLimit.stream().filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), totalCode)).findAny();

            //값이 존재하면 true
            if (hourLimitOptional.isPresent()) {
                isLimitHour = true;
            }
            if (dayLimitOptional.isPresent()) {
                isLimitDay = true;
            }
            if (totalLimitOptional.isPresent()) {
                isLimitTotal = true;
            }
        }

        //시간 당첨 수량 체크
        if (!isLimitHour) {
            if (PredicateUtils.isNull(winningEntity.getHourWinningNumber())) {
                winningEntity.setHourWinningNumber(0);
            }
            if (PredicateUtils.isGreaterThanZero(winningEntity.getHourWinningNumber())) {
                int hourPrevSuccessCnt = stampLogService.getHourStampEventLogWinningCountByStpIdEqualsAndEventWinningSortEqualsAndIdIsLessThan(stpId, winningEntity.getEventWinningSort(), insertedLogId);
                if (PredicateUtils.isGreaterThan((hourPrevSuccessCnt + 1), winningEntity.getHourWinningNumber())) {
                    log.debug("동시성 시간 당첨 수량 초과 로그 ID {} ", insertedLogId);
                    return this.failWinningLogic(reqDto, stampMain, true, insertedLogId);
                }
                //로우 총개수
                int savedHourCnt = stampLogService.getStampEventLogWinningSuccessCountHour(stpId, winningEntity.getEventWinningSort());
                if (savedHourCnt > winningEntity.getHourWinningNumber()) {
                    Long lastIdx = stampLogService.getStampEventLogWinningSuccessLastIndexHour(stpId, winningEntity.getEventWinningSort(), winningEntity.getTotalWinningNumber());
                    if (PredicateUtils.isNotNull(lastIdx)) {
                        stampLogService.deleteStampEventLogWinningSuccessLastIndexGreaterThan(stpId, winningEntity.getEventWinningSort(), lastIdx);
                        return this.failWinningLogic(reqDto, stampMain, true, insertedLogId);
                    }
                }
            }
        }

        //일당첨 수량 체크
        if (!isLimitDay) {
            if (PredicateUtils.isNull(winningEntity.getDayWinningNumber())) {
                winningEntity.setDayWinningNumber(0);
            }
            if (PredicateUtils.isGreaterThanZero(winningEntity.getDayWinningNumber())) {
                int dayPrevSuccessCnt = stampLogService.getDayStampEventLogWinningCountByStpIdEqualsAndEventWinningSortEqualsAndIdIsLessThan(stpId, winningEntity.getEventWinningSort(), insertedLogId);
                if (PredicateUtils.isGreaterThan((dayPrevSuccessCnt + 1), winningEntity.getDayWinningNumber())) {
                    log.debug("동시성 일 당첨 수량 초과 로그 ID {} ", insertedLogId);
                    return this.failWinningLogic(reqDto, stampMain, true, insertedLogId);
                }
                //로우 총개수
                int savedHourCnt = stampLogService.getStampEventLogWinningSuccessCountDay(stpId, winningEntity.getEventWinningSort());
                if (savedHourCnt > winningEntity.getHourWinningNumber()) {
                    Long lastIdx = stampLogService.getStampEventLogWinningSuccessLastIndexDay(stpId, winningEntity.getEventWinningSort(), winningEntity.getTotalWinningNumber());
                    if (PredicateUtils.isNotNull(lastIdx)) {
                        stampLogService.deleteStampEventLogWinningSuccessLastIndexGreaterThan(stpId, winningEntity.getEventWinningSort(), lastIdx);
                        return this.failWinningLogic(reqDto, stampMain, true, insertedLogId);
                    }
                }
            }
        }

        //전체 당첨수량 체크
        if (!isLimitTotal) {
            if (PredicateUtils.isNull(winningEntity.getTotalWinningNumber())) {
                winningEntity.setTotalWinningNumber(0);
            }
            if (PredicateUtils.isGreaterThanZero(winningEntity.getTotalWinningNumber())) {
                int totalPrevSuccessCnt = stampLogService.getTotalStampEventLogWinningCountByStpIdEqualsAndEventWinningSortEqualsAndIdIsLessThan(stpId, winningEntity.getEventWinningSort(), insertedLogId);
                if (PredicateUtils.isGreaterThan((totalPrevSuccessCnt + 1), winningEntity.getTotalWinningNumber())) {
                    log.debug("동시성 전체 당첨 수량 초과 로그 ID {} ", insertedLogId);
                    return this.failWinningLogic(reqDto, stampMain, true, insertedLogId);
                }
                //로우 총개수
                int savedHourCnt = stampLogService.getStampEventLogWinningSuccessCountTotal(stpId, winningEntity.getEventWinningSort());
                if (savedHourCnt > winningEntity.getHourWinningNumber()) {
                    Long lastIdx = stampLogService.getStampEventLogWinningSuccessLastIndex(stpId, winningEntity.getEventWinningSort(), winningEntity.getTotalWinningNumber());
                    if (PredicateUtils.isNotNull(lastIdx)) {
                        stampLogService.deleteStampEventLogWinningSuccessLastIndexGreaterThan(stpId, winningEntity.getEventWinningSort(), lastIdx);
                        return this.failWinningLogic(reqDto, stampMain, true, insertedLogId);
                    }
                }
            }
        }

        //핸드폰번호 또는 참여코드가 없으면 꽝 처리
        if (PredicateUtils.isNull(reqDto.getPhoneNumber()) && PredicateUtils.isNull(reqDto.getAttendCode())) {
            log.error("=================== 자동당첨인데 핸드폰번호, 참여코드가 없어서 꽝처리 ====================> 당첨 인덱스 {} ", winningEntity.getArEventWinningId());
            return this.failWinningLogic(reqDto, stampMain, true, insertedLogId);
        }
        StampEventGiveAwayDeliveryModel giveAwayDeliveryModel = new StampEventGiveAwayDeliveryModel();
        GifticonOrderResDto gifticonOrderResDto = new GifticonOrderResDto();

        Long couponId = null;
        if (PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.기타.code()) || PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.NFT쿠폰.code())) {
            giveAwayDeliveryModel = StampEventGiveAwayDeliveryModel.ofAutoSave(winningEntity, reqDto, insertedLogId,  null);
            if (PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.NFT쿠폰.code())) {
                //쿠폰 지급
                couponId = eventWinning.payCoupon(stampMain.getStpId(), winningEntity.getArEventWinningId(), insertedLogId, EventTypeDefine.STAMP.name());
                //쿠폰이 제대로 지급이 안되면 꽝처리
                if (PredicateUtils.isNull(couponId)) {
                    return this.failWinningLogic(reqDto, stampMain, true, insertedLogId);
                }
            }
        }
        //기프티콘 지급
        if (PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.기프티콘.code())) {
            gifticonOrderResDto = eventWinning.payGifticon(reqDto.getEventId(), winningEntity.getArEventWinningId(), reqDto.getPhoneNumber(), EventTypeDefine.STAMP.name());
            giveAwayDeliveryModel = StampEventGiveAwayDeliveryModel.ofAutoSave(winningEntity, reqDto, insertedLogId, gifticonOrderResDto);
        }

        WinningResultResDto winningResultResDto = eventWinning.getWebEventSuccessWinningResultButtonInfo(reqDto.getEventId(), insertedLogId, "N", winningEntity);

        //자동당첨인지, 당첨정보 입력인지 확인
        if (PredicateUtils.isNotNullList(winningResultResDto.getWinningButtonInfo())) {
            boolean isAutoWinning = true;
            Optional<WinningButtonResDto> optional = winningResultResDto.getWinningButtonInfo().stream().filter(button -> PredicateUtils.isEqualsStr(button.getButtonActionType(), WinningButtonActionTypeDefine.DELIVERY.name())).findAny();
            //버튼 배열중 '당첨정보입력' 버튼이 하나라도 있으면 > 자동당첨이 아님
            if (optional.isPresent()) {
                isAutoWinning = false;
            }
            //리턴 해줄 객체에 자동당첨 여부 주입
            winningResultResDto.setIsAutoWinning(isAutoWinning);
        }

        //경품정보 저장
        try {
            stampFrontService.saveStampEventGiveAwayDelivery(giveAwayDeliveryModel);
            winningResultResDto.setStpGiveAwayId(giveAwayDeliveryModel.getStpGiveAwayId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return this.failWinningLogic(reqDto, stampMain, true, insertedLogId);
        } finally {
            if (PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.NFT쿠폰.code())) {
                try {
                    log.info("NFT쿠폰 :: {}",couponId + " ,getStpGiveAwayId :: {}", winningResultResDto.getStpGiveAwayId());
                    arEventFrontService.updateArEventNftCouponRepositoryStpGiveAwayIdByCouponId(couponId, winningResultResDto.getStpGiveAwayId());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            //스탬프 클릭 상태 업데이트
            if (PredicateUtils.isNotNull(reqDto.getStpEventLogTrId())) {
                try {
                    stampLogService.updateStampEventLogTrIsClick(reqDto.getStpEventLogTrId(), true);
                } catch (Exception e) {
                    log.error("updateStampEventLogTrIsClick Error {}", e);
                }
            }
        }
        return new ApiResultObjectDto().builder()
                .result(winningResultResDto)
                .resultCode(resultCode)
                .build();
    }

    /**
     * 교환형 당첨 결과
     * @param reqDto
     * @param stampMain
     * @param winningEntity
     * @param winningLimit
     * @return
     */
    private ApiResultObjectDto successExchangeWinning(EventWinningReqDto reqDto, StampEventMainModel stampMain, ArEventWinningEntity winningEntity, List<StampEventLogWinningLimitModel> winningLimit) {
        int resultCode = HttpStatus.OK.value();

        //참여 값이 핸드폰번호인지 참여코드인지 분리해서 가져오기
        eventWinning.injectStampAttendValue(reqDto, stampMain);

        String attendValue = reqDto.getAttendValue();

        //성공로그 저장 후 인덱스 리턴
        Long insertedLogId = stampLogService.saveStampEventLogWinning(stampMain.getStpId(), reqDto.getStpPanTrId(), stampMain.getStpAttendAuthCondition(), attendValue, stampMain.getStpWinningType(), winningEntity);

        if (PredicateUtils.isNotNullList(winningLimit)) {
            boolean isLimitTotal = false;
            String totalCode = StringTools.joinStringsNoSeparator(String.valueOf(stampMain.getStpId()), String.valueOf(winningEntity.getEventWinningSort()));
            Optional<StampEventLogWinningLimitModel> totalLimitOptional = winningLimit.stream().filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), totalCode)).findAny();
            if (totalLimitOptional.isPresent()) {
                isLimitTotal = true;
            }

            //전체 당첨수량 체크
            if (!isLimitTotal) {
                if (PredicateUtils.isNull(winningEntity.getTotalWinningNumber())) {
                    winningEntity.setTotalWinningNumber(0);
                }
                if (PredicateUtils.isGreaterThanZero(winningEntity.getTotalWinningNumber())) {  //[SS-20095]오브젝트 맵핑 당첨팝업 오류 - 동시성 체크 :: 당첨이 오브젝트 맵핑일때 당첨 후 일당첨수량, 시간당 당첨수량 0 일때 예외처리 추가. 안지호/2022. 8. 17. 오후 12:36
                    int totalPrevSuccessCnt = stampLogService.getTotalStampEventLogWinningCountByStpIdEqualsAndEventWinningSortEqualsAndIdIsLessThan(stampMain.getStpId(), winningEntity.getEventWinningSort(), insertedLogId);
                    if (PredicateUtils.isGreaterThan((totalPrevSuccessCnt + 1), winningEntity.getTotalWinningNumber())) {
                        log.debug("동시성 전체 당첨 수량 초과 로그 ID {} ", insertedLogId);
                        return this.failWinningLogic(reqDto, stampMain, true, insertedLogId);
                    }
                }
            }
        }

        //핸드폰번호 또는 참여코드가 없으면 꽝 처리
        if (PredicateUtils.isNull(reqDto.getPhoneNumber())
                && PredicateUtils.isNull(reqDto.getAttendCode()))
        {
            log.error("=================== 자동당첨인데 핸드폰번호, 참여코드가 없어서 꽝처리 ====================> 당첨 인덱스 >>> " + winningEntity.getArEventWinningId());
            return this.failWinningLogic(reqDto, stampMain, true, insertedLogId);
        }

        GifticonOrderResDto gifticonOrderResDto = new GifticonOrderResDto();

        Long couponId = null;
        if (PredicateUtils.isNotEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.기타.code())) {
            if (PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.NFT쿠폰.code())) {
                //쿠폰 지급
                couponId = eventWinning.payCoupon(stampMain.getStpId(), winningEntity.getArEventWinningId(), insertedLogId, EventTypeDefine.STAMP.name());
                //쿠폰이 제대로 지급이 안되면 꽝처리
                if (PredicateUtils.isNull(couponId)) {
                    return this.failWinningLogic(reqDto, stampMain, true, insertedLogId);
                }
            }
            //기프티콘 지급
            if (PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.기프티콘.code())) {
                gifticonOrderResDto = eventWinning.payGifticon(reqDto.getEventId(), winningEntity.getArEventWinningId(), reqDto.getPhoneNumber(), EventTypeDefine.STAMP.name());
            }
            //NFT 지급
            if (PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.NFT.code())) {
                //지급가능한 NFT 정보 가져오기
                Long nftTokenId = null;
                try {
                    nftTokenId = arEventFrontService.saveSelectAvailableArEventNftTokenByArEventIdAndArEventWinningId(stampMain.getStpId(), winningEntity.getArEventWinningId(), insertedLogId, true);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    log.info("NFT 지급시 예외사항 발생!! ::::::: 꽝처리");
                    return this.failWinningLogic(reqDto, stampMain, true, insertedLogId);
                } finally {
                    //쿠폰이 정상 발급이되면 쿠폰 상태를 '지급완료' 상태로 업데이트
                    if (PredicateUtils.isNotNull(nftTokenId)) {
                        arEventFrontService.updateNftTokenIsPayedById(nftTokenId);
                    }
                }
            }
        }

        WinningResultResDto winningResultResDto = eventWinning.getWebEventSuccessWinningResultButtonInfo(reqDto.getEventId(), insertedLogId, "N", winningEntity);

        StampEventGiveAwayDeliveryModel giveAwayDeliveryModel = StampEventGiveAwayDeliveryModel.ofAutoSave(winningEntity, reqDto, insertedLogId, gifticonOrderResDto);

        //자동당첨인지, 당첨정보 입력인지 확인
        if (PredicateUtils.isNotNullList(winningResultResDto.getWinningButtonInfo())) {
            boolean isAutoWinning = true;
            Optional<WinningButtonResDto> optional = winningResultResDto.getWinningButtonInfo().stream().filter(button -> PredicateUtils.isEqualsStr(button.getButtonActionType(), WinningButtonActionTypeDefine.DELIVERY.name())).findAny();
            //버튼 배열중 '당첨정보입력' 버튼이 하나라도 있으면 > 자동당첨이 아님
            if (optional.isPresent()) {
                isAutoWinning = false;
            }
            //리턴 해줄 객체에 자동당첨 여부 주입
            winningResultResDto.setIsAutoWinning(isAutoWinning);
        }

        //경품정보 저장
        try {
            stampFrontService.saveStampEventGiveAwayDelivery(giveAwayDeliveryModel);
            long stpGiveAwayId = giveAwayDeliveryModel.getStpGiveAwayId();
            //리턴 해줄 객체에 당첨정보 저장 인덱스값 주입
            winningResultResDto.setStpGiveAwayId(stpGiveAwayId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return this.failWinningLogic(reqDto, stampMain, true, insertedLogId);
        } finally {
            if (PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.NFT쿠폰.code())) {
                try {
                    log.info("NFT쿠폰 =========================> {}", couponId + " ,getStpGiveAwayId ======================> {}", winningResultResDto.getStpGiveAwayId());
                    arEventFrontService.updateArEventNftCouponRepositoryStpGiveAwayIdByCouponId(couponId, winningResultResDto.getStpGiveAwayId());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            //스탬프 클릭 상태 업데이트
            if (PredicateUtils.isNotNull(reqDto.getStpEventLogTrId())) {
                try {
                    stampLogService.updateStampEventLogTrIsClick(reqDto.getStpEventLogTrId(), true);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    try {
                        stampLogService.updateStampEventLogTrIsClick(reqDto.getStpEventLogTrId(), false);
                    } catch (Exception e1) {
                        log.error(e1.getMessage());
                    }
                }
            }
        }

        return new ApiResultObjectDto().builder()
                .result(winningResultResDto)
                .resultCode(resultCode)
                .build();
    }

    private ApiResultObjectDto failWinningLogic(EventWinningReqDto eventWinningReqDto, StampEventMainModel stampEventMain, boolean isConcurrency, Long eventLogWinningId) {
        int resultCode = HttpStatus.OK.value();
        String eventId = eventWinningReqDto.getEventId();

        //리턴해줄 당첨 정보 가쟈오기
        WinningResultResDto winningResultResDto = eventWinning.getWinningResultButtonInfo(eventId, eventLogWinningId, stampEventMain.getStpId(), EventTypeDefine.STAMP.name(), true);

        if (isConcurrency) {
            //당첨 로그 삭제
            stampLogService.deleteStampEventWinningSuccessById(eventLogWinningId);
            log.info("delete fail info 당첨 정보 삭제 : {} ", eventLogWinningId);
        }
        //스탬프 클릭 상태 업데이트
        try {
            stampLogService.updateStampEventLogTrIsClick(eventWinningReqDto.getStpEventLogTrId(), true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                stampLogService.updateStampEventLogTrIsClick(eventWinningReqDto.getStpEventLogTrId(), true);
            } catch (Exception e1) {
                log.error(e1.getMessage());
            }
        }

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(winningResultResDto)
                .build();
    }
}
