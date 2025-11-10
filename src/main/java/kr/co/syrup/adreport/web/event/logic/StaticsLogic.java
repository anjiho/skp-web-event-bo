package kr.co.syrup.adreport.web.event.logic;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.FunctionUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.stamp.event.model.StampEventMainModel;
import kr.co.syrup.adreport.stamp.event.model.StampEventPanTrModel;
import kr.co.syrup.adreport.stamp.event.service.StampFrontService;
import kr.co.syrup.adreport.stamp.event.service.StampSodarService;
import kr.co.syrup.adreport.survey.go.define.AgeTypeDefine;
import kr.co.syrup.adreport.survey.go.define.GenderTypeDefine;
import kr.co.syrup.adreport.survey.go.service.SurveyGoStaticsService;
import kr.co.syrup.adreport.web.event.define.*;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.dto.response.PerformanceStaticsResDto;
import kr.co.syrup.adreport.web.event.dto.response.StaticsResDto;
import kr.co.syrup.adreport.web.event.entity.ArEventEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventWinningEntity;
import kr.co.syrup.adreport.web.event.entity.WebEventBaseEntity;
import kr.co.syrup.adreport.web.event.mybatis.vo.*;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import kr.co.syrup.adreport.web.event.service.StaticsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Repository
public class StaticsLogic {

    private final int httpSuccessCode = HttpStatus.OK.value();

    @Autowired
    private StaticsService staticsService;

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private SurveyGoStaticsService surveyGoStaticsService;

    @Autowired
    private StampSodarService stampSodarService;

    @Autowired
    private StampFrontService stampFrontService;

    public ApiResultObjectDto getConnectionStaticsLogic(String eventId, Integer stpId, String searchDay) {
        int resultCode = httpSuccessCode;

        Map<String, String>resultMap = new HashMap<>();

        if (PredicateUtils.isNull(eventId) && PredicateUtils.isNull(stpId)) {

            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

        } else {
            WebEventBaseEntity webEventBase = arEventService.findEventBase(eventId);
            String convertSearchDay = DateUtils.convertDateFormat(searchDay);;
            // AR일때
            if (PredicateUtils.isEqualsStr(webEventBase.getEventType(), EventTypeDefine.AR.name())) {
                //페이지접속 누적 개수
                int totalPageConnectionCount = staticsService.selectTotalPageConnectStaticsCount(eventId, null);
                //페이지접속 기준일 개수
                int searchDayPageConnectionCount = staticsService.selectTotalPageConnectStaticsCount(eventId, convertSearchDay);
                //AR참여(참여번호) 누적 개수
                //int totalArAttendStaticsCount = staticsService.selectArAttendStaticsCount(eventId, null);
                //AR참여(참여번호) 기준일 개수
                //int searchDayArAttendStaticsCount = staticsService.selectArAttendStaticsCount(eventId, searchDate);

                //AR호출 총개수(누적)
                int totalArCallCount = staticsService.selectArCallStaticsCount(eventId, null, null);
                //AR호출 총개수(기준일)
                int searchDayArCallCount = staticsService.selectArCallStaticsCount(eventId, null, convertSearchDay);
                //AR호출 성공 총개수(누적)
                int totalArCallSuccessCount = staticsService.selectArCallStaticsCount(eventId, StringDefine.Y.name(), null);
                //AR호출 성공 총개수(기준일)
                int searchDayArCallSuccessCount = staticsService.selectArCallStaticsCount(eventId, StringDefine.Y.name(), convertSearchDay);
                //AR호출 실패 총개수(누적)
                int totalArCallFailCount = staticsService.selectArCallStaticsCount(eventId, StringDefine.N.name(), null);
                //AR호출 실패 총개수(기준일)
                int searchDayArCallFailCount = staticsService.selectArCallStaticsCount(eventId, StringDefine.N.name(), convertSearchDay);

                // 페이지접속 누적/기준일
                resultMap.put("pageConnection", FunctionUtils.concatIntAddSlashString(totalPageConnectionCount, searchDayPageConnectionCount));

                // AR 호출 누적/기준일
                resultMap.put("arCall", FunctionUtils.concatIntAddSlashString(totalArCallCount, searchDayArCallCount));

                // AR 호출성공 누적/기준일
                resultMap.put("arCallSuccess", FunctionUtils.concatIntAddSlashString(totalArCallSuccessCount, searchDayArCallSuccessCount));

                // AR 호출실패 누적/기준일
                resultMap.put("arCallFail", FunctionUtils.concatIntAddSlashString(totalArCallFailCount, searchDayArCallFailCount));
            }

            //서베이고일때
            if (PredicateUtils.isEqualsStr(webEventBase.getEventType(), EventTypeDefine.SURVEY.name())) {
                //페이지접속 누적 개수
                int totalPageConnectionCount = staticsService.selectTotalPageConnectStaticsCount(eventId, null);
                //페이지접속 기준일 개수
                int searchDayPageConnectionCount = staticsService.selectTotalPageConnectStaticsCount(eventId, convertSearchDay);
                //서베이참여 누적/기준일
                String surveyAttendCount = surveyGoStaticsService.findSurveyAttendStatics(eventId, convertSearchDay, null);

                // 페이지접속 누적/기준일
                resultMap.put("pageConnection", FunctionUtils.concatIntAddSlashString(totalPageConnectionCount, searchDayPageConnectionCount));

                //서베이참여 누적/기준일
                resultMap.put("arCall", surveyAttendCount);

                //서베이참여 성공 누적/기준일
                resultMap.put("arCallSuccess", "");

                //서베이참여 실패 누적/기준일
                resultMap.put("arCallFail", "");

                //서베이제출완료 누적/기준일
                resultMap.put("surveySuccessSubmit", surveyGoStaticsService.findSurveyAttendStatics(eventId, convertSearchDay, true));
            }

            //포토일때
            if (PredicateUtils.isEqualsStr(webEventBase.getEventType(), EventTypeDefine.PHOTO.name())) {
                //페이지접속 누적 개수
                int totalPageConnectionCount = staticsService.selectTotalPageConnectStaticsCount(eventId, null);
                //페이지접속 기준일 개수
                int searchDayPageConnectionCount = staticsService.selectTotalPageConnectStaticsCount(eventId, convertSearchDay);

                // 페이지접속 누적/기준일
                resultMap.put("pageConnection", FunctionUtils.concatIntAddSlashString(totalPageConnectionCount, searchDayPageConnectionCount));
            }

            //스탬프일때
            if (EventTypeDefine.isStampEvent(webEventBase.getEventType())) {
                int stampMainAccConnectionCount = staticsService.selectTotalPageStampConnectStaticsCount(stpId, "MAIN", null);
                int stampMainDayConnectionCount = staticsService.selectTotalPageStampConnectStaticsCount(stpId, "MAIN", searchDay);
                //스탬프 메인 페이지접속 누적/기준일
                resultMap.put("stampMainConnectionCount", FunctionUtils.concatIntAddSlashString(stampMainAccConnectionCount, stampMainDayConnectionCount));

                int stampPanAccConnectionCount = staticsService.selectTotalPageStampConnectStaticsCount(stpId, "PAN", null);
                int stampPanDayConnectionCount = staticsService.selectTotalPageStampConnectStaticsCount(stpId, "PAN", searchDay);
                //스탬프 판 접속 누적/기준일
                resultMap.put("stampPanConnectionCount", FunctionUtils.concatIntAddSlashString(stampPanAccConnectionCount, stampPanDayConnectionCount));
            }

        }
        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultMap)
                .build();

    }

    public ApiResultObjectDto getPerformanceStaticsLogic(String eventId, String searchDay, Integer stpId, WebEventBaseEntity eventBase) {
        int resultCode = httpSuccessCode;

        //실적통계 테이블 제목 리스트 선언
        LinkedList<String> tableTitleList = new LinkedList<>();
        //실적통계 캐치기준(당첨성공 로그) 테이블 값 리스트 선언
        LinkedList<String> tableValueList = new LinkedList<>();
        //실적통계 당첨정보 입력기준(당첨성공 후 경품정보 저장까지 저장된 로그) 테이블 값 리스트 선언
        LinkedList<String> tableValueList2 = new LinkedList<>();

        //이벤트 아이디 파라미터 없으면 에러처리
        if (PredicateUtils.isNull(eventId)) {

            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

        } else {
            String searchDate = DateUtils.convertDateFormat(searchDay);
            ArEventEntity arEvent = new ArEventEntity();
            StampEventMainModel stampEvent = new StampEventMainModel();
            boolean isStampEvent = EventTypeDefine.isStampEvent(eventBase.getEventType());

            if (isStampEvent) {
                stampEvent = stampFrontService.findStampEventMainByEventId(eventId);
            } else {
                arEvent = arEventService.findArEventByEventId(eventId);
            }

            //이벤트 상세정보가 없으면 에러처리
            if (PredicateUtils.isNull(arEvent) && PredicateUtils.isNull(stampEvent)) {

                resultCode = ErrorCodeDefine.CUSTOM_ERROR_AR_EVENT_INFO_NULL.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            } else {

                tableTitleList.add(StaticsTableTitleDefine.당첨성공누적기준일.value());

                //당첨성공 당첨정보 리스트 가져오기
                List<String> eventWinningTypeIn = WinningTypeDefine.getWinningTypeListNotInFail();
                LinkedList<ArEventWinningEntity> successWinningInfoList = new LinkedList<ArEventWinningEntity>();
                List<String> performanceStaticsInfoList = new ArrayList<String>();

                //스탬프이벤트
                if (isStampEvent) {
                    successWinningInfoList = arEventService.findArEventWinningListByStpId(stpId, false);
                    performanceStaticsInfoList = staticsService.selectStampPerformanceStaticsList(stpId, 0, searchDate);
                } else {
                //AR이벤트
                    successWinningInfoList = arEventService.findArEventWinningListByArEventIdAndEventWinningTypeIn(arEvent.getArEventId(), eventWinningTypeIn, false);
                    performanceStaticsInfoList = staticsService.selectPerformanceStaticsList(eventId, 0, searchDate);
                }

                if (!PredicateUtils.isNullList(performanceStaticsInfoList)) {
                    tableValueList.add(performanceStaticsInfoList.get(0));
                    tableValueList2.add(performanceStaticsInfoList.get(1));
                }

                if (!PredicateUtils.isNullList(successWinningInfoList)) {
                    //당첨 정보 0 ~ n 개 누적/기준일 개수 주입
                    LinkedList<ArEventWinningEntity> finalSuccessWinningInfoList = successWinningInfoList;
                    IntStream
                            .range(0, successWinningInfoList.size())
                            .forEach(index -> {
                                int eventWinningSort = finalSuccessWinningInfoList.get(index).getEventWinningSort();

                                //제목목록
                                tableTitleList.add(StringTools.joinStringsNoSeparator("당첨", String.valueOf(eventWinningSort), "-", finalSuccessWinningInfoList.get(index).getProductName(), "\r누적/기준일"));

                                List<String> successPerformanceStaticsInfoList = new ArrayList<String>();
                                if (isStampEvent) {
                                    successPerformanceStaticsInfoList = staticsService.selectStampPerformanceStaticsList(stpId, eventWinningSort, searchDate);
                                } else {
                                    successPerformanceStaticsInfoList = staticsService.selectPerformanceStaticsList(eventId, eventWinningSort, searchDate);
                                }

                                if (!PredicateUtils.isNullList(successPerformanceStaticsInfoList)) {
                                    tableValueList.add(successPerformanceStaticsInfoList.get(0));
                                    tableValueList2.add(successPerformanceStaticsInfoList.get(1));
                                }
                            });
                }
            }
        }

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(PerformanceStaticsResDto.builder()
                        .tableTitleInfo(tableTitleList)
                        .tableValueInfo(tableValueList)
                        .tableValueInfo2(tableValueList2)
                        .build())
                .build();

    }

    /**
     * 실적통계 (응모)
     * @param eventId
     * @return
     */
    public ApiResultObjectDto getSubscriptionPerformanceStaticsLogic(String eventId) {
        int resultCode = httpSuccessCode;

        //실적통계 테이블 제목 리스트 선언
        LinkedList<String> tableTitleList = new LinkedList<>();
        LinkedList<String> tableValueList = new LinkedList<>();

        //이벤트 아이디 파라미터 없으면 에러처리
        if (PredicateUtils.isNull(eventId) && StringUtils.isEmpty(eventId)) {

            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

        } else {
            //ar_event 정보 가져오기
            ArEventEntity arEvent = arEventService.findArEventByEventId(eventId);
            //이벤트 상세정보가 없으면 에러처리
            if (PredicateUtils.isNull(arEvent)) {

                resultCode = ErrorCodeDefine.CUSTOM_ERROR_AR_EVENT_INFO_NULL.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            } else {
                //테이블 제목에 표현할 첫번째 항목 값 주입
                tableTitleList.add(0, StaticsTableTitleDefine.전체응모.value());
                tableValueList.add(0, String.valueOf(staticsService.selectSubscriptionPerformanceStaticsCount(arEvent.getArEventId(), 0)));
                //응모성공 종류
                List<String> eventWinningTypeIn = WinningTypeDefine.getWinningTypeListNotInFail();
                //응모성공 당첨정보 리스트 가져오기
                LinkedList<ArEventWinningEntity> successWinningInfoList = arEventService.findArEventWinningListByArEventIdAndEventWinningTypeIn(arEvent.getArEventId(), eventWinningTypeIn, true);

                if (!PredicateUtils.isNullList(successWinningInfoList)) {
                    IntStream
                            .range(0, successWinningInfoList.size())
                            .forEach(index -> {
                                //제목 주입
                                tableTitleList.add(
                                        StringTools.joinStringsNoSeparator (String.valueOf(index + 1), "당첨", String.valueOf(index + 1), "-", successWinningInfoList.get(index).getProductName())
                                );
                                //값 주입
                                tableValueList.add(
                                        StringTools.joinStringsNoSeparator(String.valueOf(index + 1), String.valueOf(staticsService.selectSubscriptionPerformanceStaticsCount(arEvent.getArEventId(), successWinningInfoList.get(index).getArEventWinningId())))
                                );
                            });
                }
            }
        }
        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(PerformanceStaticsResDto.builder()
                        .tableTitleInfo(tableTitleList)
                        .tableValueInfo(tableValueList)
                        .tableValueInfo2(null)
                        .build())
                .build();
    }

    /**
     * NFT 보관함 통계
     * @return
     */
    public ApiResultObjectDto getNftRepositoryStaticsLogic(String eventId, String searchDay) {
        int resultCode = httpSuccessCode;

        //NFT 보관함 통계 테이블 제목 리스트 선언
        LinkedList<String> tableTitleList = new LinkedList<>();
        //NFT 보관함 통계 지갑정보입력 테이블 값 리스트 선언
        LinkedList<String> tableValueList = new LinkedList<>();
        //NFT 보관함 통계 소유권이전완료 테이블 값 리스트 선언
        //LinkedList<String> tableValueList2 = new LinkedList<>();

        //이벤트 아이디 파라미터 없으면 에러처리
        if (StringUtils.isEmpty(eventId)) {

            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

        } else {

            ArEventEntity arEvent = arEventService.findArEventByEventId(eventId);

            //이벤트 상세정보가 없으면 에러처리
            if (PredicateUtils.isNull(arEvent)) {

                resultCode = ErrorCodeDefine.CUSTOM_ERROR_AR_EVENT_INFO_NULL.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            } else {
                //테이블 제목에 표현할 첫번째 항목 값 주입
                AccumulateStandardCntMapperVO walletCnt = staticsService.selectNftRepositoryStatics(eventId, 0, searchDay, "WALLET");

                tableTitleList.add(0, StaticsTableTitleDefine.NFT당첨성공누적기준일.value());
                tableValueList.add(0, FunctionUtils.concatIntAddSlashString(walletCnt.getAccumulateCnt(), walletCnt.getStandardCnt()));

                //당첨성공 당첨정보 리스트 가져오기
                List<String> eventWinningTypeIn = Collections.singletonList(WinningTypeDefine.NFT.code());
                LinkedList<ArEventWinningEntity> successWinningInfoList = arEventService.findArEventWinningListByArEventIdAndEventWinningTypeIn(arEvent.getArEventId(), eventWinningTypeIn, false);

                if (!PredicateUtils.isNullList(successWinningInfoList)) {
                    IntStream
                            .range(0, successWinningInfoList.size())
                            .forEach(idx -> {
                                //제목주입
                                tableTitleList.add(
                                        (idx + 1), "당첨" + (idx + 1) + "-" + successWinningInfoList.get(idx).getProductName()
                                );
                                //값 주입
                                AccumulateStandardCntMapperVO productWalletCnt = staticsService.selectNftRepositoryStatics(eventId, successWinningInfoList.get(idx).getArEventWinningId(), searchDay, "WALLET");
                                tableValueList.add(
                                        (idx + 1), FunctionUtils.concatIntAddSlashString(productWalletCnt.getAccumulateCnt(), productWalletCnt.getStandardCnt())
                                );
                            });
                }
            }
        }

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(PerformanceStaticsResDto.builder()
                        .tableTitleInfo(tableTitleList)
                        .tableValueInfo(tableValueList)
                        //.tableValueInfo2(tableValueList2)
                        .build())
                .build();
    }

    public ApiResultObjectDto getHourlyStaticsLogic(String eventId, String searchDay, Integer stpId, WebEventBaseEntity eventBase) {
        int resultCode = httpSuccessCode;

        HashMap<String, Object> resultMap = new HashMap<>();
        //실적통계 테이블 값 리스트 선언
        LinkedList<LinkedList<String>> tableValueList = new LinkedList<>();
        LinkedList<String> tableSumList = new LinkedList<>();

        //이벤트 아이디 파라미터 없으면 에러처리
        if (PredicateUtils.isNull(eventId)) {

            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

        } else {
            String searchDate = DateUtils.convertDateFormat(searchDay);
            ArEventJoinEventBaseVO baseVO = new ArEventJoinEventBaseVO();
            StampEventMainModel stampEvent = new StampEventMainModel();

            boolean isStampEvent = EventTypeDefine.isStampEvent(eventBase.getEventType());
            if (isStampEvent) {
                stampEvent = stampFrontService.findStampEventMainByEventId(eventId);
            } else {
                baseVO = arEventService.findArEventJoinEventBaseByEventId(eventId);
            }


            //이벤트 상세정보가 없으면 에러처리
            if (PredicateUtils.isNull(baseVO.getArEventId()) && PredicateUtils.isNull(stampEvent.getStpId())) {

                resultCode = ErrorCodeDefine.CUSTOM_ERROR_AR_EVENT_INFO_NULL.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            } else {
                List<String> eventWinningTypeIn = WinningTypeDefine.getWinningTypeListNotInFail();

                //당첨성공 당첨정보 리스트 가져오기
                LinkedList<ArEventWinningEntity> successWinningInfoList = new LinkedList<>();
                if (isStampEvent) {
                    successWinningInfoList = arEventService.findArEventWinningListByStpId(stpId, false);
                } else {
                    successWinningInfoList = arEventService.findArEventWinningListByArEventIdAndEventWinningTypeIn(baseVO.getArEventId(), eventWinningTypeIn, false);
                }

                List<List<WinningHourlyMapperVO>> winningHourlyMapperVoList = new ArrayList<>();
                if (PredicateUtils.isNotNull(successWinningInfoList)) {
                    //당첨 정보 데이터
                    for (ArEventWinningEntity winningEntity : successWinningInfoList) {
                        if (isStampEvent) {
                            winningHourlyMapperVoList.add(staticsService.selectStampEventLogWinningGroupByHour(stpId, winningEntity.getEventWinningSort(), searchDay));
                        } else {
                            winningHourlyMapperVoList.add(staticsService.selectEventLogWinningGroupByHour(eventId, winningEntity.getEventWinningSort(), searchDay));
                        }
                    }
                }

                //24시 문자열 리스트
                List<Integer> timeList = this.getAllTimeStringList();
                //스탬프 메인 시간별 접속 선언
                List<HourlyMapperVO> hourlyStampMainPageConnectGroupByList = new ArrayList<>();
                //스탬프 판 시간별 접속 선언
                List<HourlyMapperVO> hourlyStampPanPageConnectGroupByList = new ArrayList<>();
                //스탬프 이외 시간별 접속 선언
                List<HourlyMapperVO> hourlyPageConnectGroupByList = new ArrayList<>();

                //페이지접속수
                if (isStampEvent) {
                    hourlyStampMainPageConnectGroupByList = staticsService.findStampHourlyPageConnectStaticsCount(stpId, "MAIN", searchDate);
                    hourlyStampPanPageConnectGroupByList  = staticsService.findStampHourlyPageConnectStaticsCount(stpId, "PAN", searchDate);
                } else {
                    hourlyPageConnectGroupByList = staticsService.selectHourlyPageConnectStaticsCount2(eventId, searchDate);
                }


                //AR 호출수
                List<HourlyMapperVO> hourlyArCallGroupByList = new ArrayList<HourlyMapperVO>();
                //AR 호출 성공수
                List<HourlyMapperVO> hourlyArCallSuccessGroupByList = new ArrayList<HourlyMapperVO>();
                //AR 호출 실패수
                List<HourlyMapperVO> hourlyArCallFailGroupByList = new ArrayList<HourlyMapperVO>();

                if (PredicateUtils.isEqualsStr(eventBase.getEventType(), EventTypeDefine.AR.name())) {
                    hourlyArCallGroupByList = staticsService.selectHourlyArCallStaticsCount2(eventId, searchDate);
                    hourlyArCallSuccessGroupByList = staticsService.selectEventLogAttendLogByGroupByHour2(eventId, StringDefine.Y.name(), searchDate);
                    hourlyArCallFailGroupByList = staticsService.selectEventLogAttendLogByGroupByHour2(eventId, StringDefine.N.name(), searchDate);

                } else if (PredicateUtils.isEqualsStr(eventBase.getEventType(), EventTypeDefine.SURVEY.name())) {
                    //서베이참여
                    hourlyArCallGroupByList = surveyGoStaticsService.findHourlySurveyAttendStatics(eventId, searchDate, null);
                    //서베이 제출완료
                    hourlyArCallSuccessGroupByList = surveyGoStaticsService.findHourlySurveyAttendStatics(eventId, searchDate, true);
                }

                List<ArEventWinningEntity> winningList = new ArrayList<>();
                //당첨정보가 있는지 확인
                if (isStampEvent) {
                    winningList = arEventService.findArEventWinningListByStpId(stpId, false);
                } else {
                    winningList = arEventService.findArEventWinningListByArEventId(baseVO.getArEventId());
                }

                //당첨 성공수
                List<HourlyMapperVO> hourlyWinningSuccessGroupByList = new ArrayList<>();
                List<HourlyMapperVO> hourlyWinningSuccessNotReceiveGroupByList = new ArrayList<>();
                if (PredicateUtils.isNotNullList(winningList)) {
                    if (isStampEvent) {
                        hourlyWinningSuccessGroupByList = staticsService.selectStampHourlyWinningStaticsCountByWinningSuccessTable(stpId, searchDate, true);
                        hourlyWinningSuccessNotReceiveGroupByList = staticsService.selectStampHourlyWinningStaticsCountByWinningSuccessTable(stpId, searchDate, null);
                    } else {
                        hourlyWinningSuccessGroupByList = staticsService.selectHourlyWinningStaticsCountByWinningSuccessTable(eventId, searchDate, true);
                        hourlyWinningSuccessNotReceiveGroupByList = staticsService.selectHourlyWinningStaticsCountByWinningSuccessTable(eventId, searchDate, null);
                    }
                }

                LinkedList<String> tableTitleList = new LinkedList<>();

                if (isStampEvent) {
                    tableTitleList.add(StaticsTableTitleDefine.스탬프메인접속누적기준일.value());
                    tableTitleList.add(StaticsTableTitleDefine.스탬프판접속누적기준일.value());
                } else {
                    tableTitleList.add(StaticsTableTitleDefine.페이지접속수.value());
                }

                //AR일떄
                if (PredicateUtils.isEqualsStr(eventBase.getEventType(), EventTypeDefine.AR.name())) {
                    tableTitleList.add(StaticsTableTitleDefine.AR호출수.value());
                    tableTitleList.add(StaticsTableTitleDefine.AR호출성공수.value());
                    tableTitleList.add(StaticsTableTitleDefine.AR호출실패수.value());

                } else if (PredicateUtils.isEqualsStr(eventBase.getEventType(), EventTypeDefine.SURVEY.name())) {
                //서베이고일때
                    tableTitleList.add(StaticsTableTitleDefine.서베이참여.value());
                    tableTitleList.add(StaticsTableTitleDefine.서베이제출완료.value());
                }


                //당첨 정보 데이터
                if (PredicateUtils.isNotNullList(successWinningInfoList)) {
                    //당첨정보가 있을때만
                    if (isStampEvent) {
                        tableTitleList.add(StaticsTableTitleDefine.당첨성공수캐치정보입력.value());
                    } else {
                        tableTitleList.add(StaticsTableTitleDefine.당첨성공수캐치당첨정보.value());
                    }
                    for (ArEventWinningEntity winningEntity : successWinningInfoList) {
                        //제목값 추가
                        if (isStampEvent) {
                            tableTitleList.add(winningEntity.getProductName() + "\r당첨캐치/당첨정보입력");
                        } else {
                            tableTitleList.add(winningEntity.getProductName() + "\r추첨/정보\r입력");
                        }
                    }
                }

                for (Integer time : timeList) {
                    LinkedList<String> hourLinkedList = new LinkedList<>();

                    if (PredicateUtils.isNotNullList(hourlyPageConnectGroupByList)) {
                        for (HourlyMapperVO vo : hourlyPageConnectGroupByList) {
                            if (time == vo.getTime()) {
                                hourLinkedList.add(String.valueOf(vo.getCount()));
                            }
                            if (time == 23) {
                                if (vo.getTime() == 23)
                                    tableSumList.add(String.valueOf(hourlyPageConnectGroupByList.stream().mapToInt(HourlyMapperVO::getCount).sum()));
                            }
                        }
                    }

                    if (PredicateUtils.isNotNullList(hourlyArCallGroupByList)) {
                        for (HourlyMapperVO vo : hourlyArCallGroupByList) {
                            if (time == vo.getTime()) {
                                hourLinkedList.add(String.valueOf(vo.getCount()));
                            }

                            if (time == 23) {
                                if (vo.getTime() == 23)
                                    tableSumList.add(String.valueOf(hourlyArCallGroupByList.stream().mapToInt(HourlyMapperVO::getCount).sum()));
                            }
                        }
                    }

                    if (PredicateUtils.isNotNullList(hourlyArCallSuccessGroupByList)) {
                        for (HourlyMapperVO vo : hourlyArCallSuccessGroupByList) {
                            if (time == vo.getTime()) {
                                hourLinkedList.add(String.valueOf(vo.getCount()));
                            }
                            if (time == 23) {
                                if (vo.getTime() == 23)
                                    tableSumList.add(String.valueOf(hourlyArCallSuccessGroupByList.stream().mapToInt(HourlyMapperVO::getCount).sum()));
                            }
                        }
                    }

                    //AR일때만
                    if (PredicateUtils.isEqualsStr(eventBase.getEventType(), EventTypeDefine.AR.name())) {
                        for (HourlyMapperVO vo : hourlyArCallFailGroupByList) {
                            if (time == vo.getTime()) {
                                hourLinkedList.add(String.valueOf(vo.getCount()));
                            }
                            if (time == 23) {
                                if (vo.getTime() == 23)
                                    tableSumList.add(String.valueOf(hourlyArCallFailGroupByList.stream().mapToInt(HourlyMapperVO::getCount).sum()));
                            }
                        }
                    }

                    //스탬프 이벤트 일때
                    if (isStampEvent) {
                        //스탬프 메인 시간별
                        if (PredicateUtils.isNotNullList(hourlyStampMainPageConnectGroupByList)) {
                            for (HourlyMapperVO vo : hourlyStampMainPageConnectGroupByList) {
                                if (time == vo.getTime()) {
                                    hourLinkedList.add(String.valueOf(vo.getCount()));
                                }
                                if (time == 23) {
                                    if (vo.getTime() == 23)
                                        tableSumList.add(String.valueOf(hourlyStampMainPageConnectGroupByList.stream().mapToInt(HourlyMapperVO::getCount).sum()));
                                }
                            }
                        }
                        //스탬프 판 시간별
                        if (PredicateUtils.isNotNullList(hourlyStampPanPageConnectGroupByList)) {
                            for (HourlyMapperVO vo : hourlyStampPanPageConnectGroupByList) {
                                if (time == vo.getTime()) {
                                    hourLinkedList.add(String.valueOf(vo.getCount()));
                                }
                                if (time == 23) {
                                    if (vo.getTime() == 23)
                                        tableSumList.add(String.valueOf(hourlyStampPanPageConnectGroupByList.stream().mapToInt(HourlyMapperVO::getCount).sum()));
                                }
                            }
                        }
                    }

                    if (PredicateUtils.isNotNullList(hourlyWinningSuccessGroupByList)) {
                        for (int i = 0; i < hourlyWinningSuccessGroupByList.size(); i++) {
                            HourlyMapperVO vo = hourlyWinningSuccessGroupByList.get(i);
                            HourlyMapperVO vo2 = hourlyWinningSuccessNotReceiveGroupByList.get(i);
                            if (time == vo.getTime()) {
                                hourLinkedList.add(vo2.getCount() + "/" + vo.getCount());
                            }
                            if (time == 23) {
                                if (vo.getTime() == 23)
                                    tableSumList.add(String.valueOf(hourlyWinningSuccessNotReceiveGroupByList.stream().mapToInt(HourlyMapperVO::getCount).sum()));
                            }
                        }
                    }

                    if (PredicateUtils.isNotNullList(winningHourlyMapperVoList)) {
                        for (List<WinningHourlyMapperVO> voList : winningHourlyMapperVoList) {
                            for (WinningHourlyMapperVO vo : voList) {
                                if (time == vo.getHour()) {
                                    hourLinkedList.add(vo.getCnt());
                                }
                                if (time == 23) {
                                    if (vo.getHour() == 23)
                                        tableSumList.add(String.valueOf(voList.stream().mapToInt(WinningHourlyMapperVO::getTotalCnt).sum()));
                                }
                            }
                        }
                    }
                    tableValueList.add(hourLinkedList);
                }

                resultMap.put("tableTitleInfo", tableTitleList);
                resultMap.put("tableValueInfo", tableValueList);
                resultMap.put("tableSumInfo", tableSumList);
            }
        }

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultMap)
                .build();
    }

    /**
     * 서베이고 - 접속통계 (목표달성수 기준 - 성별)
     * @param eventId
     * @param searchDay
     * @return
     */
    @Deprecated
    public ApiResultObjectDto getSurveyConnectionGenderStaticsLogic(String eventId, String searchDay) {
        int resultCode = httpSuccessCode;

        LinkedList<String> maleValueList = new LinkedList<>();
        LinkedList<String> feMaleValueList = new LinkedList<>();

        List<String> genderTypeList = Arrays.asList(GenderTypeDefine.M.name(), GenderTypeDefine.F.name());

        List<SurveyConnectionStaticsMapperVO> staticsList = staticsService.selectSurveyConnectionGenderStaticsList(eventId, searchDay);

        if (!PredicateUtils.isNullList(staticsList)) {

            for (String gender : genderTypeList) {

                if (PredicateUtils.isEqualsStr(gender, GenderTypeDefine.M.name())) {
                    maleValueList.add(0, "남성");

                } else if (PredicateUtils.isEqualsStr(gender, GenderTypeDefine.F.name())) {
                    feMaleValueList.add(0, "여성");
                }

                List<SurveyConnectionStaticsMapperVO> list = staticsList
                        .stream()
                        .filter(vo -> PredicateUtils.isNotNull(vo.getGender())).collect(Collectors.toList());

                if (PredicateUtils.isNullList(list)) {
                    if (PredicateUtils.isEqualsStr(gender, GenderTypeDefine.M.name())) {
                        maleValueList.add("0/0");
                        maleValueList.add("0/0");

                    } else if (PredicateUtils.isEqualsStr(gender, GenderTypeDefine.F.name())) {
                        feMaleValueList.add("0/0");
                        feMaleValueList.add("0/0");
                    }
                } else {

                    Optional<SurveyConnectionStaticsMapperVO> findVO = staticsList.stream()
                            .filter(vo -> PredicateUtils.isEqualsStr(vo.getGender(), gender))
                            .findFirst();

                    if (findVO.isPresent()) {
                        if (PredicateUtils.isEqualsStr(gender, GenderTypeDefine.M.name())) {
                            maleValueList.add(findVO.get().getAttendCnt());
                            maleValueList.add(findVO.get().getSuccessCnt());

                        } else if (PredicateUtils.isEqualsStr(gender, GenderTypeDefine.F.name())) {
                            feMaleValueList.add(findVO.get().getAttendCnt());
                            feMaleValueList.add(findVO.get().getSuccessCnt());
                        }
                    } else {
                        if (PredicateUtils.isEqualsStr(gender, GenderTypeDefine.M.name())) {
                            maleValueList.add("0/0");
                            maleValueList.add("0/0");

                        } else if (PredicateUtils.isEqualsStr(gender, GenderTypeDefine.F.name())) {
                            feMaleValueList.add("0/0");
                            feMaleValueList.add("0/0");
                        }
                    }

                }
            }
        }

        if (PredicateUtils.isNullList(staticsList)) {
            for (String gender : genderTypeList) {
                if (PredicateUtils.isEqualsStr(gender, GenderTypeDefine.M.name())) {
                    maleValueList.add(0, "남성");
                } else {
                    feMaleValueList.add(0, "여성");
                }

                maleValueList.add("0/0");
                feMaleValueList.add("0/0");
            }
        }

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("maleValueInfo", maleValueList);
        resultMap.put("feMaleValueInfo", feMaleValueList);

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultMap)
                .build();
    }

    public ApiResultObjectDto getSurveyConnectionGenderStaticsLogic2(String eventId, String searchDay) {
        int resultCode = httpSuccessCode;

        LinkedList<String> maleValueList = new LinkedList<>();
        LinkedList<String> feMaleValueList = new LinkedList<>();

        //서베이참여 누적 개수 (남자)
        SurveyConnectionStaticsMapperVO maleMapperVO = staticsService.selectSurveyLogByGender(eventId, GenderTypeDefine.M.name(), null, null);
        //서베이참여 기준일 개수 (남자)
        SurveyConnectionStaticsMapperVO maleDayMapperVO = staticsService.selectSurveyLogByGender(eventId, GenderTypeDefine.M.name(), searchDay, null);
        String maleAttendCnt = FunctionUtils.concatIntAddSlashString(maleMapperVO.getCnt(), maleDayMapperVO.getCnt());

        //서베이참여 누적 개수 (남자)
        SurveyConnectionStaticsMapperVO maleSubmitMapperVO = staticsService.selectSurveyLogByGender(eventId, GenderTypeDefine.M.name(), null, true);
        //서베이참여 기준일 개수 (남자)
        SurveyConnectionStaticsMapperVO maleSubmitDayMapperVO = staticsService.selectSurveyLogByGender(eventId, GenderTypeDefine.M.name(), searchDay, true);
        String maleSubmitCnt = FunctionUtils.concatIntAddSlashString(maleSubmitMapperVO.getCnt(), maleSubmitDayMapperVO.getCnt());

        //서베이참여 누적 개수 (여자)
        SurveyConnectionStaticsMapperVO feMaleMapperVO = staticsService.selectSurveyLogByGender(eventId, GenderTypeDefine.F.name(), null, null);
        //서베이참여 기준일 개수 (여자)
        SurveyConnectionStaticsMapperVO feMaleDayMapperVO = staticsService.selectSurveyLogByGender(eventId, GenderTypeDefine.F.name(), searchDay, null);
        String feMaleAttendCnt = FunctionUtils.concatIntAddSlashString(feMaleMapperVO.getCnt(), feMaleDayMapperVO.getCnt());

        //서베이참여 누적 개수 (여자)
        SurveyConnectionStaticsMapperVO feMaleSubmitMapperVO = staticsService.selectSurveyLogByGender(eventId, GenderTypeDefine.F.name(), null, true);
        //서베이참여 기준일 개수 (여자)
        SurveyConnectionStaticsMapperVO feMaleSubmitDayMapperVO = staticsService.selectSurveyLogByGender(eventId, GenderTypeDefine.F.name(), searchDay, true);
        String feMaleSubmitCnt = FunctionUtils.concatIntAddSlashString(feMaleSubmitMapperVO.getCnt(), feMaleSubmitDayMapperVO.getCnt());

        List<String> genderTypeList = Arrays.asList(GenderTypeDefine.M.name(), GenderTypeDefine.F.name());
        for (String gender : genderTypeList) {
            if (PredicateUtils.isEqualsStr(gender, GenderTypeDefine.M.name())) {
                maleValueList.add("남성");
                maleValueList.add(maleAttendCnt);
                maleValueList.add(maleSubmitCnt);
            } else if (PredicateUtils.isEqualsStr(gender, GenderTypeDefine.F.name())) {
                feMaleValueList.add("여성");
                feMaleValueList.add(feMaleAttendCnt);
                feMaleValueList.add(feMaleSubmitCnt);
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("maleValueInfo", maleValueList);
        resultMap.put("feMaleValueInfo", feMaleValueList);

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultMap)
                .build();
    }

    /**
     * 서베이고 - 접속통계 (목표달성수 기준 - 연령별)
     * @param eventId
     * @param searchDay
     * @return
     */
    @Deprecated
    public ApiResultObjectDto getSurveyConnectionAgeStaticsLogic(String eventId, String searchDay) {
        int resultCode = httpSuccessCode;

        LinkedList<String> ageList = new LinkedList<>();
        LinkedList<LinkedList<String>> resultLinkedList = new LinkedList<>();

        List<SurveyConnectionStaticsMapperVO> staticsList = staticsService.selectSurveyConnectionAgeStaticsList(eventId, searchDay);

        List<SurveyConnectionStaticsMapperVO> checkList = staticsList
                .stream()
                .filter(vo -> PredicateUtils.isNotNull(vo.getAge())).collect(Collectors.toList());

        if (PredicateUtils.isNotNullList(checkList)) {
            int j = 0;
            for (int i = 1; i < 7; i++) {
                LinkedList<String> ageLinkedList = new LinkedList<String>();
                //연령대 값
                if (i == 1)      ageList.add(AgeTypeDefine.TWENTY_UNDER.getAgeStr());
                else if (i == 2) ageList.add(AgeTypeDefine.TWENTY.getAgeStr());
                else if (i == 3) ageList.add(AgeTypeDefine.THIRTY.getAgeStr());
                else if (i == 4) ageList.add(AgeTypeDefine.FOURTY.getAgeStr());
                else if (i == 5) ageList.add(AgeTypeDefine.FIFTY.getAgeStr());
                else if (i == 6) ageList.add(AgeTypeDefine.SIXTY_MORE.getAgeStr());

                ageLinkedList.add(ageList.get(j));

                String attend = "0/0";  //서베이참여기준 값
                String submit = "0/0";  //서베이참여완료 기준 값

                for (SurveyConnectionStaticsMapperVO vo : staticsList) {
                    if (PredicateUtils.isEqualNumber(vo.getAge(), i)) {
                        attend = vo.getAttendCnt();
                        submit = vo.getSuccessCnt();
                    }
                }
                ageLinkedList.add(attend);
                ageLinkedList.add(submit);

                resultLinkedList.add(ageLinkedList);
                j++;
            }
        } else {
            int j=0;
            for (int i = 1; i < 7; i++) {
                LinkedList<String> ageLinkedList = new LinkedList<String>();
                //연령대 값
                if (i == 1)      ageList.add(AgeTypeDefine.TWENTY_UNDER.getAgeStr());
                else if (i == 2) ageList.add(AgeTypeDefine.TWENTY.getAgeStr());
                else if (i == 3) ageList.add(AgeTypeDefine.THIRTY.getAgeStr());
                else if (i == 4) ageList.add(AgeTypeDefine.FOURTY.getAgeStr());
                else if (i == 5) ageList.add(AgeTypeDefine.FIFTY.getAgeStr());
                else if (i == 6) ageList.add(AgeTypeDefine.SIXTY_MORE.getAgeStr());

                ageLinkedList.add(ageList.get(j));

                String attend = "0/0";  //서베이참여기준 값
                String submit = "0/0";  //서베이참여완료 기준 값

                ageLinkedList.add(attend);
                ageLinkedList.add(submit);

                resultLinkedList.add(ageLinkedList);
                j++;
            }
        }

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("ageInfo", resultLinkedList);

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultMap)
                .build();
    }

    /**
     * 접속통계(목표달성수 기준 - 나이대별)
     * @param eventId
     * @param searchDay
     * @return
     */
    public ApiResultObjectDto getSurveyConnectionAgeStaticsLogic2(String eventId, String searchDay) {
        int resultCode = httpSuccessCode;

        LinkedList<String> ageList = new LinkedList<>();
        LinkedList<LinkedList<String>> resultLinkedList = new LinkedList<>();

        int j = 0;
        for (int i = 1; i < 7; i++) {
            LinkedList<String> ageLinkedList = new LinkedList<String>();
            //연령대 값
            if (i == 1)      ageList.add(AgeTypeDefine.TWENTY_UNDER.getAgeStr());
            else if (i == 2) ageList.add(AgeTypeDefine.TWENTY.getAgeStr());
            else if (i == 3) ageList.add(AgeTypeDefine.THIRTY.getAgeStr());
            else if (i == 4) ageList.add(AgeTypeDefine.FOURTY.getAgeStr());
            else if (i == 5) ageList.add(AgeTypeDefine.FIFTY.getAgeStr());
            else if (i == 6) ageList.add(AgeTypeDefine.SIXTY_MORE.getAgeStr());

            ageLinkedList.add(ageList.get(j));

            String attend = "0/0";  //서베이참여기준 값
            String submit = "0/0";  //서베이참여완료 기준 값

            SurveyConnectionStaticsMapperVO attendVO = staticsService.selectSurveyConnectionAgeStatics(eventId, i, searchDay, null);
            SurveyConnectionStaticsMapperVO successVO = staticsService.selectSurveyConnectionAgeStatics(eventId, i, searchDay, true);

            //참여개수
            if (PredicateUtils.isNotNull(attendVO)) {
                attend = PredicateUtils.isNull(attendVO.getAttendCnt()) ? attend : attendVO.getAttendCnt();
            }
            //성공개수
            if (PredicateUtils.isNotNull(successVO)) {
                submit = PredicateUtils.isNull(successVO.getAttendCnt()) ? submit : successVO.getAttendCnt();
            }
            ageLinkedList.add(attend);
            ageLinkedList.add(submit);

            resultLinkedList.add(ageLinkedList);
            j++;
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("ageInfo", resultLinkedList);


        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultMap)
                .build();
    }

    public ApiResultObjectDto getPhotoPrintResultLogic(String eventId, String searchDay) {
        int resultCode = httpSuccessCode;

        PhotoPrintResultMapperVO vo = staticsService.selectPhotoPrintResultStatics(eventId, searchDay);

        return new ApiResultObjectDto().builder()
                .result(vo)
                .resultCode(resultCode)
                .build();
    }

    /**
     * AR포토 통계 가져오기
     * @param eventId
     * @param searchDay
     * @return
     */
    public ApiResultObjectDto getPhotoBoxStaticsLogic(String eventId, String searchDay) {
        int resultCode = httpSuccessCode;

        PhotoBoxStaticsMapperVO vo = staticsService.selectPhotoBoxStatics(eventId, searchDay);

        return new ApiResultObjectDto().builder()
                .result(vo)
                .resultCode(resultCode)
                .build();
    }

    /**
     * 스탬프 적립 통계 가져오기
     * @param stpId
     * @param searchDay
     * @return
     */
    public ApiResultObjectDto getStampTrStatics(Integer stpId, String searchDay) {
        LinkedList<Map<String, String>> tableValueList = new LinkedList<>();
        //스탬프판 목록
        List<StampEventPanTrModel>stampTrList = stampSodarService.findStampEventPanTrListByStpId(stpId);

        if (PredicateUtils.isNotNullList(stampTrList)) {
            for (StampEventPanTrModel trModel : stampTrList) {
                Map<String, String>resultMap = new HashMap<>();
                String stampTrName = StringTools.joinStringsNoSeparator(String.valueOf(trModel.getStpTrSort()), "번/", trModel.getStpTrTxt());
                resultMap.put("trName", stampTrName);
                //스탬프 판에 맞는 개수 가져오기
                String cnt = staticsService.selectStampTrStatics(stpId, trModel.getStpPanTrId(), searchDay);
                resultMap.put("cnt", cnt);

                tableValueList.add(resultMap);
            }
        }
        return new ApiResultObjectDto().builder()
                .result(tableValueList)
                .resultCode(HttpStatus.OK.value())
                .build();
    }

    /**
     * 모든 종류의 통계를 가져오기
     * @param eventId
     * @param searchDay
     * @return
     */
    public ApiResultObjectDto getStatics(String eventId, String searchDay) {
        int resultCode = httpSuccessCode;
        StaticsResDto resDto = new StaticsResDto();
        //이벤트 아이디 파라미터 없으면 에러처리
        if (StringUtils.isEmpty(eventId)) {

            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

        } else {
            String eventType = null;
            WebEventBaseEntity eventBase = arEventService.findEventBase(eventId);
            if (PredicateUtils.isNotNull(eventBase)) {
                eventType = eventBase.getEventType();
            }
            Integer stpId = null;
            boolean isStampEvent = EventTypeDefine.isStampEvent(eventType);
            if (EventTypeDefine.isStampEvent(eventType)) {
                StampEventMainModel stampEventMain = stampFrontService.findStampEventMainByEventId(eventId);
                stpId = stampEventMain.getStpId();
            }

            resDto = new StaticsResDto().builder()
                    .connectionStatics(this.getConnectionStaticsLogic(eventId, stpId, searchDay))  //접속통계
                    .performanceStatics(this.getPerformanceStaticsLogic(eventId, searchDay, stpId, eventBase))   //실적통계(당첨)
                    .hourlyStatics(this.getHourlyStaticsLogic(eventId, searchDay, stpId, eventBase)) //시간별 통계
                    .performanceSubscriptionStatics(PredicateUtils.isNotEqualsStr(eventType, EventTypeDefine.STAMP.name()) ? this.getSubscriptionPerformanceStaticsLogic(eventId) : null)   //실적통계(응모)
                    .nftRepositoryStatics(PredicateUtils.isNotEqualsStr(eventType, EventTypeDefine.STAMP.name()) ? this.getNftRepositoryStaticsLogic(eventId, searchDay) : null)    //nft보관함 통계
                    .surveyGenderConnectionStatics(PredicateUtils.isEqualsStr(eventType, EventTypeDefine.SURVEY.name()) ? this.getSurveyConnectionGenderStaticsLogic2(eventId, searchDay) : null)    //접속통계(목표달성수 기준 - 성별)
                    .surveyAgeConnectionStatics(PredicateUtils.isEqualsStr(eventType, EventTypeDefine.SURVEY.name()) ? this.getSurveyConnectionAgeStaticsLogic2(eventId, searchDay) : null)    //접속통계(목표달성수 기준 - 나이대별)
                    .photoPrintResultStatics(PredicateUtils.isEqualsStr(eventType, EventTypeDefine.PHOTO.name()) ? this.getPhotoPrintResultLogic(eventId, searchDay) : null)    //AR포토 촬영 결과  통계
                    .photoBoxStatics(PredicateUtils.isEqualsStr(eventType, EventTypeDefine.PHOTO.name()) ? this.getPhotoBoxStaticsLogic(eventId, searchDay) : null) //AR 포토함 통계
                    .stampTrAccStatics(PredicateUtils.isEqualsStr(eventBase.getEventType(), EventTypeDefine.STAMP.name()) ? this.getStampTrStatics(stpId, searchDay) : null) //스탬프 적립 통계
                    .isStampEvent(isStampEvent)
                    .build();

        }
        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resDto)
                .build();
    }

    /**
     * 24시 시각 string list > 00 ~ 23
     * @return
     */
    private List<Integer> getAllTimeStringList() {
        List<Integer> hourList = new ArrayList<>();
        for (int i=0; i<=23; i++) {
            hourList.add(i);
        }
        return hourList;
    }

}
