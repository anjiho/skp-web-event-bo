package kr.co.syrup.adreport.stamp.event.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.exception.BaseException;
import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.stamp.event.dto.WebEventBaseDto;
import kr.co.syrup.adreport.stamp.event.dto.request.StampEventSaveReqDto;
import kr.co.syrup.adreport.stamp.event.dto.request.WebEventStampTypeListReqDto;
import kr.co.syrup.adreport.stamp.event.dto.response.StampEventSodarResDto;
import kr.co.syrup.adreport.stamp.event.dto.response.WebEventStampTypeListResDto;
import kr.co.syrup.adreport.stamp.event.model.StampEventGateCodeModel;
import kr.co.syrup.adreport.stamp.event.model.StampEventMainModel;
import kr.co.syrup.adreport.stamp.event.model.StampEventPanModel;
import kr.co.syrup.adreport.stamp.event.service.StampFrontService;
import kr.co.syrup.adreport.stamp.event.service.StampLogService;
import kr.co.syrup.adreport.stamp.event.service.StampSodarService;
import kr.co.syrup.adreport.survey.go.mybatis.vo.SurveyTableExampleStaticsResVO;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import kr.co.syrup.adreport.web.event.define.WinningTypeDefine;
import kr.co.syrup.adreport.web.event.dto.request.EventHtmlDto;
import kr.co.syrup.adreport.web.event.dto.response.*;
import kr.co.syrup.adreport.web.event.dto.response.api.OcbPointSaveResDto;
import kr.co.syrup.adreport.web.event.entity.*;
import kr.co.syrup.adreport.web.event.logic.SodarCommonLogic;
import kr.co.syrup.adreport.web.event.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class StampSodarLogic {

    @Value("${web.event.domain}")
    private String webEventDomain;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private StampSodarService stampSodarService;

    @Autowired
    private StampFrontService stampFrontService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private SodarCommonLogic sodarCommonLogic;

    public ApiResultObjectDto saveSodarStampLogic(String jsonStr, MultipartFile attendCodeExcelFile) {
        int resultCode = HttpStatus.OK.value();

        if (PredicateUtils.isNull(attendCodeExcelFile)) {
            log.error("attendCodeExcelFile is null");
        } else {
            log.info("attendCodeExcelFile >>> " + attendCodeExcelFile.getOriginalFilename());
        }
        StampEventSaveReqDto stampEventSaveDto;
        try {
             stampEventSaveDto = objectMapper.readValue(jsonStr, StampEventSaveReqDto.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BaseException(ResultCodeEnum.STRING_ENCODING_EXCEPTION_ERROR.getDesc(), ResultCodeEnum.STRING_ENCODING_EXCEPTION_ERROR);
        }

        //이벤트 아이디 DB에 등록된 내용이 있는지 확인
        if (PredicateUtils.isNotNullList(stampEventSaveDto.getStampTrInfo())) {
            List<String> requestTrEventIdList = stampEventSaveDto.getStampTrInfo().stream().filter(data -> PredicateUtils.isNotNull(data.getStpTrEventId())).map(e -> e.getStpTrEventId()).collect(Collectors.toCollection(ArrayList::new));

            if (PredicateUtils.isNotNullList(requestTrEventIdList)) {
                List<String> savedTrEventIdList = stampSodarService.findAllStpTrEventIdList();

                boolean isDuplicates = StringTools.hasDuplicates(requestTrEventIdList, savedTrEventIdList);
                //새로 등록할 스탬프 TR의 eventId 와 등록되어있는 스탬프 TR의 eventId 중에 하나라도 중복되면 에러
                if (isDuplicates) {
                    throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_DUPLICATE_VALUE.getDesc(), ResultCodeEnum.CUSTOM_ERROR_DUPLICATE_VALUE);
                }
            }
        }

        String eventId = null;
        //WEB_EVENT_BASE 저장
        try {
            eventId = arEventService.saveEventBase(WebEventBaseEntity.ofStamp(arEventService.findWebEventSequence("stamp_event_seq"), stampEventSaveDto.getEventBaseInfo()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        //이벤트ID 가 정상적으로 저장이 안되면 에러처리
        if (PredicateUtils.isNull(eventId)) {
            log.error("eventId is null");
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_SODAR_SAVE_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_SODAR_SAVE_ERROR);
        }

        //STAMP_EVENT_MAIN 저장
        Integer stpId = null;
        try {
             stpId = stampSodarService.upsertStampEventMain(stampEventSaveDto.getStampMainInfo(), eventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (PredicateUtils.isNull(stpId)) {
            log.error("stpId is null");
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_SODAR_SAVE_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_SODAR_SAVE_ERROR);
        }

        //STAMP_EVENT_PAN 저장
        Integer stpPanId = null;
        try {
            stpPanId = stampSodarService.upsertStampEventPan(StampEventPanModel.ofSave(stampEventSaveDto.getStampPanInfo(), stpId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (PredicateUtils.isNull(stpPanId)) {
            log.error("stpPanId is null");
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_SODAR_SAVE_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_SODAR_SAVE_ERROR);
        }
        //스탬프판 HTML 저장
        if (PredicateUtils.isNotNullList(stampEventSaveDto.getStampPanHtmlInfo())) {
            for (EventHtmlDto html : stampEventSaveDto.getStampPanHtmlInfo()) {
                try {
                    arEventService.saveFirstEventHtmlByHtmlId(eventId, stpId, stpPanId, html, true);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        //STAMP_EVENT_PAN_TR 저장
        if (PredicateUtils.isNotNull(stampEventSaveDto.getStampTrInfo())) {
            try {
                stampSodarService.upsertDeleteListStampEventPanTr(stampEventSaveDto.getStampTrInfo(), stpPanId);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        //STAMP_ALIMTOK, STAMP_ALIMTOK_BUTTON 저장
        if (PredicateUtils.isNotNull(stampEventSaveDto.getStampAlimtokInfo())) {
            try {
                stampSodarService.upsertDeleteListStampAlimtok(stampEventSaveDto.getStampAlimtokInfo(), stpId);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        //AR_EVENT_BUTTON 저장
        if (PredicateUtils.isNotNull(stampEventSaveDto.getArEventButtonInfo())) {
            try {
                arEventService.saveEventButton(ArEventButtonEntity.ofStamp(stpId, stampEventSaveDto.getArEventButtonInfo()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        //당첨정보, 당첨버튼정보 저장하기
        try {
            sodarCommonLogic.saveArEventWinning(stampEventSaveDto.getArEventWinningInfo(), stpId, true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        /**
         * AR_EVENT_HTML저장
         */
        if (PredicateUtils.isNotNullList(stampEventSaveDto.getArEventHtmlInfo())) {
            //ar_event_html 저장 시작
            for (EventHtmlDto html : stampEventSaveDto.getArEventHtmlInfo()) {
                //ar_event_html 저장
                try {
                    arEventService.saveFirstEventHtmlByHtmlId(eventId, stpId, 0, html, true);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            //ar_event_html 저장 끝
        }

        //참여코드 저장
        try {
            String attendCodeRegType = null;
            int strDigit = 0, codeCnt = 0;
            if (PredicateUtils.isNotNull(stampEventSaveDto.getStampMainInfo().getStpAttendCodeRegType())) {
                attendCodeRegType = stampEventSaveDto.getStampMainInfo().getStpAttendCodeRegType();

                if (PredicateUtils.isNotNull(stampEventSaveDto.getStampMainInfo().getStpAttendCodeDigit())) {
                    strDigit = stampEventSaveDto.getStampMainInfo().getStpAttendCodeDigit();
                }
                if (PredicateUtils.isNotNull(stampEventSaveDto.getStampMainInfo().getStpAttendCodeCount())) {
                    codeCnt = stampEventSaveDto.getStampMainInfo().getStpAttendCodeCount();
                }
            }
            sodarCommonLogic.saveAllAttendCode(attendCodeExcelFile, eventId, stpId, attendCodeRegType, strDigit, codeCnt);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        Map<String, String>resultMap = new HashMap<>();
        resultMap.put("eventId", eventId);

        return new ApiResultObjectDto().builder()
                .result(resultMap)
                .resultCode(resultCode)
                .build();
    }

    public ApiResultObjectDto updateSodarStampLogic(String eventId, String jsonStr, MultipartFile attendCodeExcelFile) {
        int resultCode = HttpStatus.OK.value();

        //이벤트ID 가 없으면 에러처리
        if (PredicateUtils.isNull(eventId)) {
            log.error("eventId is null");
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_EVENT_ID_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_EVENT_ID_NULL);
        }

        StampEventSaveReqDto stampEventSaveDto = new StampEventSaveReqDto();
        try {
            stampEventSaveDto = objectMapper.readValue(jsonStr, StampEventSaveReqDto.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BaseException(ResultCodeEnum.STRING_ENCODING_EXCEPTION_ERROR.getDesc(), ResultCodeEnum.STRING_ENCODING_EXCEPTION_ERROR);
        }

        //스탬프TR 이벤트 아이디 DB에 등록된 내용이 있는지 확인
        if (PredicateUtils.isNotNullList(stampEventSaveDto.getStampTrInfo())) {
            List<String> requestTrEventIdList = stampEventSaveDto.getStampTrInfo().stream().filter(data -> PredicateUtils.isNotNull(data.getStpTrEventId())).map(e -> e.getStpTrEventId()).collect(Collectors.toCollection(ArrayList::new));

            if (PredicateUtils.isNotNullList(requestTrEventIdList)) {
                List<String> savedTrEventIdList = stampSodarService.findStpTrEventIdListByStpTrEventIdNotIn(requestTrEventIdList);

                boolean isDuplicates = StringTools.hasDuplicates(requestTrEventIdList, savedTrEventIdList);
                //새로 등록할 스탬프 TR의 eventId 와 등록되어있는 스탬프 TR의 eventId 중에 하나라도 중복되면 에러
                if (isDuplicates) {
                    throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_DUPLICATE_VALUE.getDesc(), ResultCodeEnum.CUSTOM_ERROR_DUPLICATE_VALUE);
                }
            }
        }

        WebEventBaseEntity findWebEventBase = arEventService.findEventBase(eventId);
        //WEB_EVENT_BASE 수정
        if (PredicateUtils.isNotNull(stampEventSaveDto.getEventBaseInfo())) {
            try {
                arEventService.saveEventBase(WebEventBaseEntity.updateOf(findWebEventBase, eventId, stampEventSaveDto.getEventBaseInfo()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        //stpId 없으면 에러처리
        if (PredicateUtils.isNull(stampEventSaveDto.getStampMainInfo().getStpId())) {
            log.error("stpId is null");
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_SODAR_SAVE_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_SODAR_SAVE_ERROR);
        }

        int stpId = stampEventSaveDto.getStampMainInfo().getStpId();
        //STAMP_EVENT_MAIN 수정
        if (PredicateUtils.isNotNull(stampEventSaveDto.getStampMainInfo())) {
            try {
                stampSodarService.upsertStampEventMain(stampEventSaveDto.getStampMainInfo(), eventId);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        //stpPanId 없으면 에러처리
        if (PredicateUtils.isNull(stampEventSaveDto.getStampPanInfo().getStpPanId())) {
            log.error("stpPanId is null");
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_SODAR_SAVE_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_SODAR_SAVE_ERROR);
        }

        int stpPanId = stampEventSaveDto.getStampPanInfo().getStpPanId();

        //STAMP_EVENT_PAN 수정
        if (PredicateUtils.isNotNull(stampEventSaveDto.getStampPanInfo())) {
            try {
                stampSodarService.upsertStampEventPan(StampEventPanModel.ofSave(stampEventSaveDto.getStampPanInfo(), stpId));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        /**
         * STAMP_EVENT_PAN_HTML 수정
         */
        if (PredicateUtils.isNotNullList(stampEventSaveDto.getStampPanHtmlInfo())) {
            //저장되어있는 AR_EVENT_HTML 목록 가져오기
            List<ArEventHtmlEntity> savedHtmlList = arEventService.findArEventHtmlListByStpIdAndStpPanId(stpId, stpPanId);

            //AR_EVENT_HTML 수정 시작
            for (EventHtmlDto htmlDto : stampEventSaveDto.getStampPanHtmlInfo()) {
                try {
                    ArEventHtmlEntity returnHtmlEntity = arEventService.saveFirstEventHtmlByHtmlId(eventId, stpId, stpPanId, htmlDto, true);
                    //저장이 되면 목록에서 삭제
                    savedHtmlList.removeIf(html -> Objects.equals(html.getEventHtmlId(), returnHtmlEntity.getEventHtmlId()));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            //삭제목록은 삭제
            if (PredicateUtils.isNotNullList(savedHtmlList)) {
                try {
                    arEventService.deleteAllArEventHtml(savedHtmlList.stream().map(ArEventHtmlEntity::getEventHtmlId).collect(Collectors.toList()));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            //ar_event_html 수정 끝
        } else {
            // DTWS-567 - 소다에서 올라오는 AR_EVENT_HTML 목록이 없는 경우, 전체 삭제된 것이므로 삭제처리해야됨.
            //저장되어있는 AR_EVENT_HTML 목록 가져오기
            List<ArEventHtmlEntity> savedHtmlList = arEventService.findArEventHtmlListByStpIdAndStpPanId(stpId, stpPanId);

            if (PredicateUtils.isNotNullList(savedHtmlList)) {
                arEventService.deleteAllArEventHtml(savedHtmlList.stream().map(ArEventHtmlEntity::getEventHtmlId).collect(Collectors.toList()));
            }
        }

        //STAMP_EVENT_PAN_TR 수정
        if (PredicateUtils.isNotNull(stampEventSaveDto.getStampTrInfo())) {
            try {
                stampSodarService.upsertDeleteListStampEventPanTr(stampEventSaveDto.getStampTrInfo(), stpPanId);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        //STAMP_ALIMTOK, STAMP_ALIMTOK_BUTTON 저장
        if (PredicateUtils.isNotNull(stampEventSaveDto.getStampAlimtokInfo())) {
            try {
                stampSodarService.upsertDeleteListStampAlimtok(stampEventSaveDto.getStampAlimtokInfo(), stpId);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        //AR_EVENT_BUTTON 저장
        if (PredicateUtils.isNotNull(stampEventSaveDto.getArEventButtonInfo())) {
            ArEventButtonEntity findArEventButtonEntity = arEventService.findArEventButtonByStpId(stpId);
            try {
                arEventService.saveEventButton(ArEventButtonEntity.ofStampUpdate(findArEventButtonEntity, stampEventSaveDto.getArEventButtonInfo()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        //당첨정보, 당첨버튼정보 저장하기
        /**
         * AR_EVENT_WINNING, AR_EVENT_WINNING_BUTTON 수정
         */
        //당첨정보, 당첨버튼정보 수정하기
        try {
            sodarCommonLogic.updateArEventWinning(stampEventSaveDto.getArEventWinningInfo(), stpId, true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        /**
         * AR_EVENT_HTML 저장
         */
        try {
            sodarCommonLogic.updateArEventHtml(stampEventSaveDto.getArEventHtmlInfo(), eventId, stpId, 0, true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (PredicateUtils.isNotNull(attendCodeExcelFile)) {
            //STAMP_EVENT_GATE_CODE 삭제
            try {
                stampSodarService.deleteStampEventGateCodeByStpId(stpId);
                sodarCommonLogic.saveAllAttendCode(attendCodeExcelFile, eventId, stpId, stampEventSaveDto.getStampMainInfo().getStpAttendCodeRegType(),
                                                        stampEventSaveDto.getStampMainInfo().getStpAttendCodeDigit(), stampEventSaveDto.getStampMainInfo().getStpAttendCodeCount());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        //캐시 삭제
        cacheService.clearAllCache();
        //버전 업데이트
        cacheService.updateCacheableInfo();
        //소다 업데이트할때 내용 저장
        arEventService.saveSodarEventUpdateHistory(eventId, jsonStr);

        Map<String, Object>resultMap = new HashMap<>();
        resultMap.put("eventId", eventId);
        resultMap.put("stpId", stpId);

        return new ApiResultObjectDto().builder()
                .result(resultMap)
                .resultCode(resultCode)
                .build();

    }

    public ApiResultObjectDto getStampEventLogic(String eventId) {
        int resultCode = HttpStatus.OK.value();

        StampEventSodarResDto resDto = new StampEventSodarResDto();
        if (PredicateUtils.isNull(eventId)) {
            log.error("eventId is null");
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_EVENT_ID_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_EVENT_ID_NULL);
        }
        resDto.setEventBaseInfo(arEventService.findEventBase(eventId));

        //스탬프 메인
        StampEventMainModel stampEventMain = stampSodarService.findStampEventMainByEventId(eventId);
        resDto.setStampMainInfo(stampEventMain);

        //스탬프 판
        int stpId= stampEventMain.getStpId();
        StampEventPanModel stampEventPan = stampSodarService.findStampEventPanByStpId(stpId);
        resDto.setStampPanInfo(stampEventPan);

        int stpPanId = stampEventPan.getStpPanId();
        //스탬프 판 HTML
        resDto.setStampPanHtmlInfo(ModelMapperUtils.convertModelInList(arEventService.findArEventHtmlListNoCacheByStpPanId(stpPanId), ArEventHtmlResDto.class));
        //스탬프 TR 목록
        resDto.setStampTrInfo(stampSodarService.findStampEventPanTrListNoCacheByStpId(stpPanId));
        //알림톡 목록
        resDto.setStampAlimtokInfo(stampSodarService.findStampAlimtokInfoByStpId(stpId));
        //스탬프 메인 버튼 목록
        resDto.setArEventButtonInfo(arEventService.findArEventButtonByStpId(stpId));
        //스탬프 당첨 목록
        List<ArEventWinningResDto> winningList = ModelMapperUtils.convertModelInList(arEventService.findArEventWinningListNoCacheByStpId(stpId), ArEventWinningResDto.class);
        resDto.setArEventWinningInfo(winningList);

        if (PredicateUtils.isNotNullList(winningList)) {
            //AR_EVENT_WINNING_BUTTON 정보 주입
            winningList
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(winningResDto -> {
                        winningResDto.setNftBenefitInfo(
                                arEventService.findAllArEventNftBenefitByArEventWinningId(winningResDto.getArEventWinningId())
                        );

                        List<ArEventWinningButtonResDto> arEventWinningButtonEntityList = ModelMapperUtils.convertModelInList(
                                arEventService.findAllArEventWinningButtonByArEventWinningId(winningResDto.getArEventWinningId()), ArEventWinningButtonResDto.class
                        );

                        if (PredicateUtils.isNotNullList(arEventWinningButtonEntityList)) {
                            winningResDto.setArEventWinningButtonInfo(arEventWinningButtonEntityList);
                            for (ArEventWinningButtonResDto winningButtonResDto : arEventWinningButtonEntityList) {
                                List<ArEventWinningButtonAddResDto> winningButtonAddResDtoList = new ArrayList<>();
                                List<ArEventWinningButtonAddEntity> winningButtonAddEntityList = arEventService.findAllArEventWinningButtonAddByArEventWinningButtonId(winningButtonResDto.getArEventWinningButtonId());
                                for (ArEventWinningButtonAddEntity buttonAddEntity : winningButtonAddEntityList) {
                                    ArEventWinningButtonAddResDto buttonAddResDto = new ArEventWinningButtonAddResDto();
                                    buttonAddResDto.setId(buttonAddEntity.getId());
                                    buttonAddResDto.setFieldName(buttonAddEntity.getFieldName());
                                    buttonAddResDto.setFieldLength(buttonAddEntity.getFieldLength());
                                    buttonAddResDto.setFieldType(buttonAddEntity.getFieldType());

                                    winningButtonAddResDtoList.add(buttonAddResDto);
                                }
                                winningButtonResDto.setArEventWinningButtonAddInfo(winningButtonAddResDtoList);
                            }
                        }

                        //서베이고 추가 (SS-20260) - 당첨 텍스트 정보 주입
                        winningResDto.setWinningTextInfo(
                                arEventService.findAllArEventWinningTextByArWinningId(winningResDto.getArEventWinningId())
                        );
                        //서베이고 추가 (SS-20260) - 보관함 정보 주입 (쿠폰, NFT 일때 데이터)
                        winningResDto.setRepositoryButtonInfo(
                                arEventService.findAllArEventRepositoryButtonByArWinningId(winningResDto.getArEventWinningId())
                        );
                        //NFT 쿠폰 일때
                        if (PredicateUtils.isEqualsStr(winningResDto.getWinningType(), WinningTypeDefine.NFT쿠폰.code())) {
                            winningResDto.setNftTokenCount(
                                    arEventService.countArEventNftCouponByArEventWinningId(winningResDto.getArEventWinningId())
                            );
                            winningResDto.setNftExcelUploadFileName(
                                    arEventService.findDistinctNftCouponInfoByArEventWinningId(winningResDto.getArEventWinningId()).getUploadExcelFileName()
                            );
                        }
                    });
        }
        //스탬프 메인 HTML 정보
        resDto.setArEventHtmlInfo(ModelMapperUtils.convertModelInList(arEventService.findArEventHtmlListNoCacheByStpId(stpId), ArEventHtmlResDto.class));

        //통계 뷰페이지 url 정보
        String staticsViewUrlInfo = FileUtils.concatPath("https://", webEventDomain, "web-event", "statics.html?eventId=" + eventId);
        //이벤트 메인 뷰페이지 url 정보
        String eventViewUrlInfo = FileUtils.concatPath("https://", webEventDomain, "stamp", "main-preview.html?eventId=" + eventId);
        //스탬프 메인 url 정보
        String realViewUrlInfo = FileUtils.concatPath("https://", webEventDomain, "stamp", "main.html?eventId=" + eventId);
        //스탬프 판 url 정보
        String stampPanUrlInfo = FileUtils.concatPath("https://", webEventDomain, "stamp", "stamp-pan.html?eventId=" + eventId);

        String firstEventId = stampFrontService.findFirstEventIdFromStampTrByStpPanIdNoCache(stampEventPan.getStpPanId());
        //하위 첫번째 이벤트 정보
        String stampTrFirstEventUrlInfo = FileUtils.concatPath("https://", webEventDomain, "web-event", "main.html?eventId=" + firstEventId);
        //하위 첫번째 이벤트 정보
        String stampTrFirstEventPreviewUrlInfo = FileUtils.concatPath("https://", webEventDomain, "web-event", "main-preview.html?eventId=" + firstEventId);

        if (PredicateUtils.isEqualY(stampEventMain.getStpMainSettingYn())) {
            resDto.setStampMainUrlInfo(realViewUrlInfo);
        }
        resDto.setPreviewEventUrlInfo(eventViewUrlInfo);
        resDto.setStampPanUrlInfo(stampPanUrlInfo);
        resDto.setStampFirstEventUrlInfo(stampTrFirstEventUrlInfo);
        resDto.setStaticsViewUrlInfo(staticsViewUrlInfo);
        resDto.setStampFirstEventPreviewUrlInfo(stampTrFirstEventPreviewUrlInfo);

        return new ApiResultObjectDto().builder()
                .result(resDto)
                .resultCode(resultCode)
                .build();
    }

    public ApiResultObjectDto getEventStampTypeList(WebEventStampTypeListReqDto req) {
        int resultCode = HttpStatus.OK.value();

        WebEventStampTypeListResDto res = new WebEventStampTypeListResDto();

        List<WebEventBaseDto> eventBaseList = stampSodarService.findWebEventStampTypeList(req);
        long totalCnt = stampSodarService.findWebEventStampTypeListCount(req);

        res.setPage(req.getPage());
        res.setTotalCnt(totalCnt);
        res.setEventList(eventBaseList);
        res.setSize(eventBaseList.size());

        return new ApiResultObjectDto().builder()
                .result(res)
                .resultCode(resultCode)
                .build();
    }
}
