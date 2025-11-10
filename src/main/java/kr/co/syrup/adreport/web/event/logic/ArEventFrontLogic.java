package kr.co.syrup.adreport.web.event.logic;

import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.common.annotation.ThreadSleep;
import kr.co.syrup.adreport.framework.exception.BaseException;
import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.stamp.event.define.StampWinningAttendTypeDefine;
import kr.co.syrup.adreport.stamp.event.logic.StampFrontLogic;
import kr.co.syrup.adreport.stamp.event.model.StampEventGiveAwayDeliveryModel;
import kr.co.syrup.adreport.stamp.event.model.StampEventMainModel;
import kr.co.syrup.adreport.stamp.event.model.StampEventPanModel;
import kr.co.syrup.adreport.stamp.event.model.StampEventPanTrModel;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.EventBaseJoinStampEventMainVO;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.StampSortAttendSortYnResVO;
import kr.co.syrup.adreport.stamp.event.service.StampFrontService;
import kr.co.syrup.adreport.stamp.event.service.StampLogService;
import kr.co.syrup.adreport.stamp.event.service.StampSodarService;
import kr.co.syrup.adreport.survey.go.entity.SurveyLogAttendEntity;
import kr.co.syrup.adreport.survey.go.logic.SurveyGoMobileLogic;
import kr.co.syrup.adreport.survey.go.service.SurveyGoLogService;
import kr.co.syrup.adreport.survey.go.service.SurveyGoMobileService;
import kr.co.syrup.adreport.web.event.define.*;
import kr.co.syrup.adreport.web.event.dto.request.*;
import kr.co.syrup.adreport.web.event.dto.response.*;
import kr.co.syrup.adreport.web.event.dto.response.api.CouponInfoResDto;
import kr.co.syrup.adreport.web.event.dto.response.api.OcbPointApiResDto;
import kr.co.syrup.adreport.web.event.dto.response.api.OcbSessionApiResDto;
import kr.co.syrup.adreport.web.event.entity.*;
import kr.co.syrup.adreport.web.event.mybatis.vo.*;
import kr.co.syrup.adreport.web.event.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.StopWatch;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ArEventFrontLogic {

    private final int httpSuccessCode = HttpStatus.OK.value();

    private final String SODAR_IMG_HOST = "https://sodarimg.syrup.co.kr";

    @Value("${web.event.domain}")
    private String webEventDomain;

    @Autowired
    private AES256Utils aes256Utils;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private LogService logService;

    @Autowired
    private ArEventFrontService arEventFrontService;

    @Autowired
    private SkApiLogic skApiLogic;

    @Autowired
    private SmsService smsService;

    @Autowired
    private ObjectExposureLogic exposureLogic;

    @Autowired
    private SurveyGoLogService surveyGoLogService;

    @Autowired
    private SurveyGoMobileService surveyGoMobileService;

    @Autowired
    private ArEventPhotoService arEventPhotoService;

    @Autowired
    private OcbApiService ocbApiService;

    @Autowired
    private SpotService spotService;

    @Autowired
    private AsyncLogic asyncLogic;

    @Autowired
    private StampFrontService stampFrontService;

    @Autowired
    private StampLogService stampLogService;

    @Autowired
    private StampFrontLogic stampFrontLogic;

    @Autowired
    private EventWinning eventWinning;

    @Autowired
    private SurveyGoMobileLogic surveyGoMobileLogic;

    /**
     * 이벤트 메인 페이지 정보
     * @param reqDto
     * @return
     */
    //@ThreadSleep(3000)
    public ApiResultObjectDto getGatePageImproveLogic(WebArGateReqDto reqDto) {
        int resultCode = httpSuccessCode;

        if (StringUtils.isEmpty(reqDto.getEventId())) {
            log.error(ErrorCodeDefine.getLogErrorMessage(ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code()));
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_EVENT_ID_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_EVENT_ID_NULL);
        }

        /**
         * 이벤트 조건에 따른 gate 페이지를 불러오는 로직
         */
        //WEB_EVENT_BASE 정보가져오기
        EventBaseJoinArEventJoinEventButtonVO vo = arEventFrontService.findArEventGagePageInfo(reqDto.getEventId());

        //WEB_EVENT_BASE 정보가 없으면 에러처리
        if (PredicateUtils.isNull(vo)) {
            log.error(ErrorCodeDefine.getLogErrorMessage(ErrorCodeDefine.CUSTOM_ERROR_WEB_EVENT_BASE_NULL.code()));
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_WEB_EVENT_BASE_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_WEB_EVENT_BASE_NULL);
        }

        //이벤트가 현재일 기준 몇일 남았는지 계산
        int diffServiceEndDateTodayCount = DateUtils.differenceTwoDay(
                PredicateUtils.isNotNull(vo.getRealEventEndDate()) ? DateUtils.convertDateToString(vo.getRealEventEndDate(), DateUtils.PATTERN_YYYY_MMD_DD) : DateUtils.convertDateToString(vo.getEventEndDate(), DateUtils.PATTERN_YYYY_MMD_DD),
                DateUtils.getNowDay()
        );

        //서비스진행중이 아니면
        if (PredicateUtils.isNotEqualsStr(ContractStatusDefine.시버스진행.code(), vo.getContractStatus())) {
            //접속 기준일 이벤트 종료일이 60일이 지났으면 접속불가 에러처리
            if (PredicateUtils.isGreaterThan(diffServiceEndDateTodayCount, 60)) {
                log.error(ErrorCodeDefine.getLogErrorMessage(ErrorCodeDefine.CUSTOM_ERROR_NOT_SERVICE_STATUS.code()));
                throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_NOT_SERVICE_STATUS.getDesc(), ResultCodeEnum.CUSTOM_ERROR_NOT_SERVICE_STATUS);
            }
        }

        //이벤트 페이지 HTML 정보 리스트
        List<ArEventHtmlEntity> arEventHtmlEntityList = arEventService.findAllArEventHtmlByArEventId(vo.getArEventId());
        List<ArEventHtmlResDto> htmlResList = new ArrayList<>();
        if (!PredicateUtils.isNullList(arEventHtmlEntityList)) {
            htmlResList = ModelMapperUtils.convertModelInList(arEventHtmlEntityList, ArEventHtmlResDto.class);
            for (ArEventHtmlResDto html : htmlResList) {
                if (PredicateUtils.isEqualsStr(html.getHtmlType(), "BUTTON")) {
                    if (PredicateUtils.isEqualsStr(html.getHtmlButtonType(), HtmlButtonTypeDefine.CONFIRM.name())) {
                        html.setEventBtnName("historyBtn");
                    }
                    if (PredicateUtils.isEqualsStr(html.getHtmlButtonType(), HtmlButtonTypeDefine.NFTREPO.name())) {
                        html.setEventBtnName("nftHistoryBtn");
                    }
                    if (PredicateUtils.isEqualsStr(html.getHtmlButtonType(), HtmlButtonTypeDefine.CPREPO.name())) {
                        html.setEventBtnName("couponHistoryBtn");
                    }
                    if (PredicateUtils.isEqualsStr(html.getHtmlButtonType(), HtmlButtonTypeDefine.PHOTOREPO.name())) {
                        html.setEventBtnName("photoRepositoryBtn");
                    }
                }
            }
        }

        //참여시간 설정 조건
        List<ArEventAttendTimeEntity> eventAttendTimeEntityList = arEventService.findAllArEventAttendTimeByArEventIdProjection(vo.getArEventId());
        //현재 시간 기준 참여가는한 시간상태인지 확인
        boolean isPossibleAttendTime = this.checkPossibleEventAttendTime(eventAttendTimeEntityList);
        if (!isPossibleAttendTime) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_IS_IMPOSSIBLE_ATTEND_TIME.code();;
        }

        //스탬프 연결 이벤트 일때 - 스탬프 순서 참여하는 이벤트 인지여부와 스탬스 순서 값 가져오기
        StampSortAttendSortYnResVO stampSortAttendSortYnResVO = new StampSortAttendSortYnResVO();
        //스탬프형일때
        if (PredicateUtils.isEqualY(vo.getStpConnectYn())) {
            //이벤트ID 기준 스탬프 참여데이터 가져오기
            stampSortAttendSortYnResVO = stampFrontService.findStampTrSortAndAttendSortYnByStpTrEventId(reqDto.getEventId());
            //이벤트ID 기준 스탬프판 정보 가져오기
            StampEventPanModel stampPan = stampFrontService.findStampEventPanByStpId(stampSortAttendSortYnResVO.getStpId());

            if (PredicateUtils.isNotNull(stampPan.getStpId())) {
                //스탬프판 URL
                stampSortAttendSortYnResVO.setStampPanUrl(FileUtils.concatPath( "stamp", "stamp-pan.html?eventId=" + stampSortAttendSortYnResVO.getStampEventId()));
                if (PredicateUtils.isEqualY(stampPan.getAttendSortSettingYn())) {
                    //순서 참여일때 - 첫번째 이벤트 번호 주입
                    stampSortAttendSortYnResVO.setFirstStampTrEventId(stampFrontService.findFirstEventIdFromStampTrByStpPanId(stampSortAttendSortYnResVO.getStpPanId()));
                }
            }
        }

        ArEventGatePageResDto resDto = new ArEventGatePageResDto().builder()
                .eventBaseInfo(vo)
                .eventHtmlInfo(htmlResList)
                .attendCode(reqDto.getAttendCode())
                .diffServiceEndDateTodayCount(diffServiceEndDateTodayCount)
                .stampTrInfo(PredicateUtils.isNotNull(stampSortAttendSortYnResVO.getStpPanTrId()) ? stampSortAttendSortYnResVO : null)
                .build();

        if (PredicateUtils.isEqualNumber(resultCode, httpSuccessCode)) {
            //redirect 가 아닌 기본접속정보일때 로그 저장
            if (!reqDto.getIsRedirect()) {
                //이벤트 접속 로그 저장하기
                CompletableFuture.supplyAsync(() -> asyncLogic.asyncSaveEventLogConnect(EventLogConnectEntity.saveOf(reqDto.getEventId(), resDto.getEventBaseInfo().getArEventId(), reqDto.getAttendCode(), reqDto.getTrackingCode())));
            }
        }

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resDto)
                .build();

    }

    /**
     * AR 페이지로 넘겨줄 참여정보
     * @param eventId
     * @param attendCode
     * @return
     */
    public ApiResultObjectDto getWebArInfoLogic(String eventId, String attendCode, String latitude, String longitude) {
        int resultCode = httpSuccessCode;

        WebArObjectResDto webArObjectResDto = new WebArObjectResDto();

        if (StringUtils.isEmpty(eventId)) {

            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

        } else {
            // 응답값 초기화 - START
            List<ArEventObjectEntity> conditionArEventObjectEntityList = new ArrayList<>();
            ArEventLogicalResDto arEventLogicalResDto = null;
            List<ArEventScanningImageResDto> arEventScanningImageResDtoList = new ArrayList<>();

            PhotoLogicalResDto photoLogicalResDto = null;
            PhotoContentsListReqDto photoContentsListReqDto = null;
            // 응답값 초기화 - END

            //오브젝트 노출 항목 리스트 가져오기
            ArEventByEventIdAtObjectExposureVO arEvent = arEventService.findArEventByEventIdAtObjectExposure(eventId);

            if (PredicateUtils.isNull(arEvent)) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_AR_EVENT_INFO_NULL.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
            } else {
                //참여코드가 있을때 참여코드가 사용가능한 상태인지 예외처리
                if (StringUtils.isNotEmpty(attendCode)) {
                    ApiResultObjectDto resultDto = this.validateAttendCodeLogic(eventId, attendCode);
                    Object resultObj = resultDto.getResult();

                    if (PredicateUtils.isNotNull(resultObj)) {
                        //정상 통신이 아니면 통신 값 리턴
                        if (resultDto.getResultCode() != httpSuccessCode) {
                            //AR호출 실패 로그 저장
                            logService.saveEventLogAttendButton(EventLogAttendSaveVO.saveOf(eventId, arEvent.getArEventId(), attendCode, StringDefine.N.name()));

                            return new ApiResultObjectDto().builder()
                                    .resultCode(resultDto.getResultCode())
                                    .result("")
                                    .build();
                        }
                    }
                }

                boolean isArPhotoType = false;

                if (arEvent.getEventLogicalType().equals(EventLogicalTypeDefine.포토_기본형.value())) {
                    // AR 포토 기본형
                    isArPhotoType = true;

                    ArPhotoLogicalEntity arPhotoLogicalEntity = arEventService.findArPhotoLogicalByArEventId(arEvent.getArEventId());
                    photoLogicalResDto = modelMapper.map(arPhotoLogicalEntity, PhotoLogicalResDto.class);

                    // 해쉬태그가 있는 경우, list 로 변환해서 내려줌
                    if (PredicateUtils.isEqualY(arPhotoLogicalEntity.getHashTagSettingYn())) {
                        String hashTagString = arPhotoLogicalEntity.getHashTagValue();
                        if(!PredicateUtils.isNull(hashTagString)){
                            photoLogicalResDto.setHashTagValue(Arrays.asList(hashTagString.split(",")));
                        }
                    }

                    photoLogicalResDto.setFilmResultImgUrl(EventUtils.replaceUriHost(photoLogicalResDto.getFilmResultImgUrl(), webEventDomain + "/sodarimg")); //AR 구동페이지에 넘겨줄 이미지 URL 변환작업( CORS 이슈 )
                    photoLogicalResDto.setFilmResultFooterImgUrl(EventUtils.replaceUriHost(photoLogicalResDto.getFilmResultFooterImgUrl(), webEventDomain + "/sodarimg")); //AR 구동페이지에 넘겨줄 이미지 URL 변환작업( CORS 이슈 )

                    photoContentsListReqDto = new PhotoContentsListReqDto();

                    if (PredicateUtils.isEqualY(arPhotoLogicalEntity.getArFrameSettingYn())) {
                        // AR 프레임
                        photoContentsListReqDto.setFrameContentsInfo(arEventPhotoService.selectPhotoContentList(arEvent.getArEventId(), PhotoContentTypeDefine.FRAME.name()));
                    }

                    if (PredicateUtils.isEqualY(arPhotoLogicalEntity.getPhotoTabMenuAddSettingYn())) {
                        // AR 탭메뉴
                        photoContentsListReqDto.setTabContentsInfo(arEventPhotoService.selectPhotoContentList(arEvent.getArEventId(), PhotoContentTypeDefine.TAB.name()));
                    }

                    if(PredicateUtils.isEqualY(arPhotoLogicalEntity.getArFilterSettingYn())){
                        // AR 필터
                        photoContentsListReqDto.setFilterContentsInfo(arEventPhotoService.selectPhotoContentList(arEvent.getArEventId(), PhotoContentTypeDefine.FILTER.name()));
                    }

                    if(PredicateUtils.isEqualY(arPhotoLogicalEntity.getArCharacterSettingYn())){
                        // AR 캐릭터
                        photoContentsListReqDto.setCharacterContentsInfo(arEventPhotoService.selectPhotoContentList(arEvent.getArEventId(), PhotoContentTypeDefine.CHARACTER.name()));
                    }

                    if(PredicateUtils.isEqualY(arPhotoLogicalEntity.getArStickerSettingYn())){
                        // AR 스티커
                        photoContentsListReqDto.setStickerContentsInfo(arEventPhotoService.selectPhotoContentList(arEvent.getArEventId(), PhotoContentTypeDefine.STICKER.name()));
                    }
                } else {
                    // 기본 AR
                    List<ArEventObjectEntity> arEventObjectEntityList = arEventService.findAllArEventObjectByArEventIdAtObjectExposure(arEvent.getArEventId());

                    /**
                     * 이벤트 오브젝트 노출제어 조건 시작
                     */
                    if (!arEventObjectEntityList.isEmpty()) {
                        conditionArEventObjectEntityList = exposureLogic.conditionExposureObjectImproveLogic(arEvent, arEventObjectEntityList, attendCode, latitude, longitude);
                    }

                    //조건에 맞는 이벤트 오브젝트가 없을떄
                    if (PredicateUtils.isEqualsStr(EventLogicalTypeDefine.기본형.value(), arEvent.getEventLogicalType())
                            || PredicateUtils.isEqualsStr(EventLogicalTypeDefine.브릿지형.value(), arEvent.getEventLogicalType())) {

                        if (PredicateUtils.isEqualZero(conditionArEventObjectEntityList.size()) || PredicateUtils.isNull(conditionArEventObjectEntityList)) {
                            resultCode = ErrorCodeDefine.CUSTOM_ERROR_CONDITION.code();
                            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                            //AR호출 실패 로그 저장
                            logService.saveEventLogAttendButton(EventLogAttendSaveVO.saveOf(eventId, arEvent.getArEventId(), attendCode, StringDefine.N.name()));
                        }
                    }

                    //arEventObjectResDtoList = new ArrayList<>();
                    //오브젝트가 하나라도 있으면 리턴 값 구현
                    if (PredicateUtils.isNotNullList(conditionArEventObjectEntityList)) {
                        List<EventLogExposureEntity> saveExposureEntityList = new ArrayList<>();
                        //ResDto 변환
                        //arEventObjectResDtoList = ModelMapperUtils.convertModelInList(conditionArEventObjectEntityList, ArEventObjectResDto.class);
                        //AR 구동페이지에 넘겨줄 이미지 URL 변환작업( CORS 이슈 )
                        for (int i=0; i<conditionArEventObjectEntityList.size(); i++) {
                            ArEventObjectEntity objectResDto = conditionArEventObjectEntityList.get(i);

                            if (PredicateUtils.isNotNull(objectResDto.getBridgeUrl())) {
                                if (PredicateUtils.isNotEqualsStr("3D", objectResDto.getObjectSettingType()) && PredicateUtils.isNotEqualsStr("VIDEO", objectResDto.getObjectSettingType())) {
                                    objectResDto.setBridgeUrl(
                                            objectResDto.getBridgeUrl().contains(SODAR_IMG_HOST) ? EventUtils.replaceUriHost(objectResDto.getBridgeUrl(), webEventDomain + "/sodarimg") : objectResDto.getBridgeUrl()

                                    );
                                }
                            }
                            if (PredicateUtils.isNotNull(objectResDto.getObjectSettingUrl())) {
                                objectResDto.setObjectSettingUrl(
                                        objectResDto.getObjectSettingUrl().contains(SODAR_IMG_HOST) ? EventUtils.replaceUriHost(objectResDto.getObjectSettingUrl(), webEventDomain + "/sodarimg") : objectResDto.getObjectSettingUrl()
                                );
                            }
                            if (PredicateUtils.isNotNull(objectResDto.getMissionActiveThumbnailUrl())) {
                                objectResDto.setMissionActiveThumbnailUrl(
                                        objectResDto.getMissionActiveThumbnailUrl().contains(SODAR_IMG_HOST) ? EventUtils.replaceUriHost(objectResDto.getMissionActiveThumbnailUrl(), webEventDomain + "/sodarimg") : objectResDto.getMissionActiveThumbnailUrl()
                                );
                            }
                            if (PredicateUtils.isNotNull(objectResDto.getMissionInactiveThumbnailUrl())) {
                                objectResDto.setMissionInactiveThumbnailUrl(
                                        objectResDto.getMissionInactiveThumbnailUrl().contains(SODAR_IMG_HOST) ? EventUtils.replaceUriHost(objectResDto.getMissionInactiveThumbnailUrl(), webEventDomain + "/sodarimg") : objectResDto.getMissionInactiveThumbnailUrl()
                                );
                            }
                            if (PredicateUtils.isNotNull(objectResDto.getObjectChangeSettingUrl())) {
                                objectResDto.setObjectChangeSettingUrl(
                                        objectResDto.getObjectChangeSettingUrl().contains(SODAR_IMG_HOST) ? EventUtils.replaceUriHost(objectResDto.getObjectChangeSettingUrl(), webEventDomain + "/sodarimg") : objectResDto.getObjectChangeSettingUrl()
                                );
                            }
                            //[DTWS-323] 위치 노출제어 pid 좌표 종류 값이 null 이면 '상대좌표' 값으로 예외처리 - 기존의 null 값 관련 대응
                            if (PredicateUtils.isEqualsStr(arEvent.getEventLogicalType(), EventLogicalTypeDefine.기본형.value())
                                    || PredicateUtils.isEqualsStr(arEvent.getEventLogicalType(), EventLogicalTypeDefine.브릿지형.value())) {
                                if (PredicateUtils.isNull(objectResDto.getPidCoordinateType())) {
                                    objectResDto.setPidCoordinateType(ExposureCoordinateTypeDefine.RELATIVE.name());
                                }
                            }
                            if (PredicateUtils.isEqualY(objectResDto.getExposureControlType())) {
                                saveExposureEntityList.add(EventLogExposureEntity.saveOf(eventId, arEvent.getArEventId(), objectResDto.getArEventObjectId(), objectResDto.getObjectSort(), attendCode));
                            }
                        }

                        //EVENT_LOG_EXPOSURE 로그 저장
                        if (!PredicateUtils.isNullList(saveExposureEntityList)) {
                            CompletableFuture.supplyAsync(() -> logService.asyncSaveAllEventLogExposure(saveExposureEntityList));
                            //logService.saveAllEventLogExposure(saveExposureEntityList);
                        }
                    }
                    //기본형이 아닐때만
                    if (PredicateUtils.isNotEqualsStr(arEvent.getEventLogicalType(), EventLogicalTypeDefine.기본형.value())) {
                        if (PredicateUtils.isNotNull(arEvent.getArEventLogicalId())) {
                            arEventLogicalResDto = arEventService.findArEventLogicalResDtoAtExposureObject(arEvent.getArEventId());
                            if (PredicateUtils.isNull(arEventLogicalResDto.getBridgeExposureTimeSecond())) {
                                arEventLogicalResDto.setBridgeExposureTimeSecond(0);
                            }

                            if (PredicateUtils.isNotNull(arEventLogicalResDto.getBridgeType())) {
                                if (PredicateUtils.isNotEqualsStr("3D", arEventLogicalResDto.getBridgeType()) && PredicateUtils.isNotEqualsStr("VIDEO", arEventLogicalResDto.getBridgeType())) {
                                    arEventLogicalResDto.setBridgeUrl(EventUtils.replaceUriHost(arEventLogicalResDto.getBridgeUrl(), webEventDomain + "/sodarimg"));//AR 구동페이지에 넘겨줄 이미지 URL 변환작업( CORS 이슈 )
                                } else {
                                    arEventLogicalResDto.setBridgeUrl(EventUtils.convertHttpsFromHttp(arEventLogicalResDto.getBridgeUrl()));
                                }
                            }

                            if (PredicateUtils.isNotNull(arEventLogicalResDto.getNftWalletImgUrl())) {
                                arEventLogicalResDto.setNftWalletImgUrl(
                                        arEventLogicalResDto.getNftWalletImgUrl().contains(SODAR_IMG_HOST) ? EventUtils.replaceUriHost(arEventLogicalResDto.getNftWalletImgUrl(), webEventDomain + "/sodarimg") : arEventLogicalResDto.getNftWalletImgUrl()
                                );
                            }
                        }
                    }

                    arEventScanningImageResDtoList = new ArrayList<>();

                    if (PredicateUtils.isEqualsStr(EventLogicalTypeDefine.이미지스캐닝형.value(), arEvent.getEventLogicalType())) {

                        arEventScanningImageResDtoList = arEventService.findAllArEventScanningImageResDtoAtExposureObject(arEvent.getArEventId());

                        if (!PredicateUtils.isNullList(arEventScanningImageResDtoList)) {
                            //AR 구동페이지에 넘겨줄 이미지 URL 변환작업( CORS 이슈 )
                            for (ArEventScanningImageResDto resDto : arEventScanningImageResDtoList) {
                                resDto.setScanningImageUrl(EventUtils.convertHttpsFromHttp(resDto.getScanningImageUrl()));
                                resDto.setActiveThumbnailUrl(EventUtils.replaceUriHost(resDto.getActiveThumbnailUrl(), webEventDomain + "/sodarimg"));
                                resDto.setInactiveThumbnailUrl(EventUtils.replaceUriHost(resDto.getInactiveThumbnailUrl(), webEventDomain + "/sodarimg"));
                            }
                        }
                    }
                }
                //AR호출 성공 로그 저장
                if (PredicateUtils.isEqualN(arEvent.getStpConnectYn())) {
                    if (!arEvent.getArAttendConditionCodeYn() && !arEvent.getAttendConditionMdnYn()) {
                        CompletableFuture.supplyAsync(() -> logService.saveEventLogAttendButtonCompletableFuture(EventLogAttendButtonEntity.saveOf(eventId, arEvent.getArEventId(), attendCode, StringDefine.Y.name())));
                    }
                }
                
                //참여코드 사용개수 업데이트
                if (PredicateUtils.isNotNull(attendCode)) {
                    logService.updateArEventGateCodeUsedCount(eventId, attendCode);
                }

                webArObjectResDto = new WebArObjectResDto().builder()
                        .eventId(eventId)
                        .eventTitle(arEvent.getEventTitle())
                        .eventLogicalType(arEvent.getEventLogicalType())
                        .arBgImage(EventUtils.replaceUriHost(arEvent.getArBgImage(), webEventDomain + "/sodarimg")) //AR 구동페이지에 넘겨줄 이미지 URL 변환작업( CORS 이슈 )
                        .arSkinImage(EventUtils.replaceUriHost(arEvent.getArSkinImage(), webEventDomain + "/sodarimg")) //AR 구동페이지에 넘겨줄 이미지 URL 변환작업( CORS 이슈 )
                        .attendCode(StringUtils.isEmpty(attendCode) ? "" : attendCode)
                        .build();

                if (isArPhotoType) {
                    // AR Photo
                    webArObjectResDto.setLoadingImgYn(arEvent.getLoadingImgYn()); //AR 구동페이지에 넘겨줄 이미지 URL 변환작업( CORS 이슈 )
                    webArObjectResDto.setLoadingImgUrl(EventUtils.replaceUriHost(arEvent.getLoadingImgUrl(), webEventDomain + "/sodarimg")); //AR 구동페이지에 넘겨줄 이미지 URL 변환작업( CORS 이슈 )
                    webArObjectResDto.setPhotoLogicalInfo(photoLogicalResDto);
                    webArObjectResDto.setPhotoContentsInfo(photoContentsListReqDto);
                } else {
                    // 기본 AR
                    webArObjectResDto.setArObjectInfo(ModelMapperUtils.convertModelInList(conditionArEventObjectEntityList, ArEventObjectResDto.class));
                    webArObjectResDto.setArEventLogicalInfo(arEventLogicalResDto);
                    webArObjectResDto.setArScanningImageInfo(arEventScanningImageResDtoList);
                }
            }
        }

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(webArObjectResDto)
                .build();
    }

    /**
     * AR 이벤트 메인페이지에서 참여 코드 검증하기
     * @param eventId
     * @param attendCode
     * @return
     */
    public ApiResultObjectDto validateAttendCodeLogic(String eventId, String attendCode) {
        int resultCode = httpSuccessCode;

        Map<String, Object>resultMap = new HashMap<>();
        if (PredicateUtils.isNull(eventId) || PredicateUtils.isNull(attendCode)) {

            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

        } else {
            //스탬프일때 스탬프 참여코드 확인 로직
            if (StringTools.containsIgnoreCase(eventId, "S")) {
                return stampFrontLogic.validateStampAttendCodeLogic(eventId, attendCode);
            }

            ArEventGateCodeEntity arEventGateCodeEntity = arEventService.findByEventIdAndAttendCode(eventId, attendCode);

            //조회된 참여코드가 없으면 에러처리
            if (PredicateUtils.isNull(arEventGateCodeEntity)) {

                resultCode = ErrorCodeDefine.CUSTOM_ERROR_NOT_ATTEND_CODE.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            } else {

                //참여코드 사용여부(사용된 쿠폰일떄 에러처리)
                if (arEventGateCodeEntity.getUseYn()) {

                    resultCode = ErrorCodeDefine.CUSTOM_ERROR_USED_ATTEND_CODE.code();
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

                } else {
                    /**
                     * AR_EVENT 참여코드별 기간참여 조건 로직 시작
                     */
                    ArEventEntity arEvent = arEventService.findArEventByEventId(eventId);

                    String arAttendTermType = StringDefine.Y.name();  //기간참여조건 타입(제한없음, 기간제한)
                    if (PredicateUtils.isNull(arEvent.getArAttendTermType()) || StringUtils.isEmpty(arEvent.getArAttendTermType()) || !PredicateUtils.isEqualY(arEvent.getArAttendTermType())) {
                        arAttendTermType = StringDefine.N.name();
                    }

                    //참여번호 기간참여 조건이 'Y' 일때
                    if (PredicateUtils.isEqualY(arAttendTermType)) {
                        //참여번호 기간참여 조건이 'Y' 이고 제한일이 1일일떄
                        int arAttendTermLimitType = 0;  //기간참여조건 종류(1일, 이벤트기간내)
                        int arAttendTermLimitCount = 0; //기간참여조건 회수

                        if (PredicateUtils.isNotNull(arEvent.getArAttendTermLimitType())) arAttendTermLimitType = Integer.parseInt(arEvent.getArAttendTermLimitType());
                        if (PredicateUtils.isNotNull(arEvent.getArAttendTermLimitCount())) arAttendTermLimitCount = arEvent.getArAttendTermLimitCount();

                        if (PredicateUtils.isGreaterThanZero(arAttendTermLimitType)) {
                            //조회일 기준 참여코드 개수
                            int todayAttendCodeUsedCnt = logService.getCountEventLogAttendButtonByEventIdAndAttendCodeAndToday(eventId, attendCode);

                            if (PredicateUtils.isGreaterThanEqualTo(todayAttendCodeUsedCnt, arAttendTermLimitCount)) {
                                resultCode = ErrorCodeDefine.CUSTOM_ERROR_ATTEND_COUNT_LIMIT_ATTEND_CODE.code();
                                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                            }

                        } else if (PredicateUtils.isEqualZero(arAttendTermLimitType)) {
                            //참여번호 기간참여 조건이 'Y' 이고 이벤트 기간내 일때
                            //이벤트 아이디 + 참여코드 사용된 개수 가져오기
                            int totalAttendCodeUsedCnt = logService.getCountEventLogAttendButtonByEventId(eventId, attendCode);

                            if (PredicateUtils.isGreaterThanEqualTo(totalAttendCodeUsedCnt, arAttendTermLimitCount)) {
                                resultCode = ErrorCodeDefine.CUSTOM_ERROR_ATTEND_COUNT_LIMIT_ATTEND_CODE.code();
                                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                            }
                        }
                    }

                    if ( PredicateUtils.isEqualNumber(resultCode, httpSuccessCode) ) {
                        //이벤트 참여 버튼 로그 테이블(이벤트 참여버튼 클릭 시 무조건 로그 적재) 저장 기능
                        logService.saveEventLogAttendButton(EventLogAttendSaveVO.saveOf(eventId, arEvent.getArEventId(), attendCode, StringDefine.Y.name()));
                        //참여코드 사용개수 업데이트
                        logService.updateArEventGateCodeUsedCount(eventId, attendCode);

                        resultMap.put("attendCode", arEventGateCodeEntity.getAttendCode());
                        resultMap.put("isPossibleAttendCode", true);
                    }
                }
            }
        }
        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultMap)
                .build();
    }

    public ApiResultObjectDto validatePhoneNumberLogic(String eventId, String phoneNumber) {
        int resultCode = httpSuccessCode;

        Map<String, Object>resultMap = new HashMap<>();
        if (PredicateUtils.isNull(eventId)) {

            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

        } else {
            //이벤트 기본정보 가져오기(테이블 : web_event_base, ar_event)
            ArEventJoinEventBaseVO vo = arEventService.findArEventJoinEventBaseByEventId(eventId);

            String arAttendTermType = StringDefine.Y.name();  //기간참여조건 타입(제한없음, 기간제한)
            if (PredicateUtils.isNull(vo.getArAttendTermType()) || StringUtils.isEmpty(vo.getArAttendTermType()) || !PredicateUtils.isEqualY(vo.getArAttendTermType())) {
                arAttendTermType = StringDefine.N.name();
            }
            int arAttendTermLimitType = 0  //기간참여조건 종류(1일, 이벤트기간내)
                ,arAttendTermLimitCount = 0; //기간참여조건 회수

            if (PredicateUtils.isNotNull(vo.getArAttendTermLimitType())) arAttendTermLimitType = Integer.parseInt(vo.getArAttendTermLimitType());
            if (PredicateUtils.isNotNull(vo.getArAttendTermLimitCount())) arAttendTermLimitCount = vo.getArAttendTermLimitCount();

            //전화번호 참여 제한 여부 체크 시작
            if (vo.getAttendConditionMdnYn()) {
                //기간참여 조건이 'Y' 일때
                if (PredicateUtils.isEqualY(arAttendTermType)) {
                    //기간참여 조건이 'Y' 일때
                    if (PredicateUtils.isGreaterThanZero(arAttendTermLimitType)) {
                        //기간참여 조건이 'Y' 이고 이벤트 1일 일때
                        //event_log_attend_button 테이블의 1일 로그 개수
                        int logCount = logService.gerCountEventLogAttendButtonByEventIdAndPhoneNumberAndIsToday(eventId, phoneNumber, true);

                        if (PredicateUtils.isGreaterThanEqualTo(logCount, arAttendTermLimitCount)) {
                            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_ATTEND_COUNT_LIMIT_PHONE_NUMBER.getDesc(), ResultCodeEnum.CUSTOM_ERROR_ATTEND_COUNT_LIMIT_PHONE_NUMBER);
                        }

                    } else if (PredicateUtils.isEqualZero(arAttendTermLimitType)) {
                        //기간참여 조건이 'Y' 이고 이벤트 기간내 일때
                        //event_log_attend_button 테이블의 총 로그 개수
                        int logCount = logService.gerCountEventLogAttendButtonByEventIdAndPhoneNumberAndIsToday(eventId, phoneNumber, false);

                        if (PredicateUtils.isGreaterThanEqualTo(logCount, arAttendTermLimitCount)) {
                            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_ATTEND_COUNT_LIMIT_PHONE_NUMBER.getDesc(), ResultCodeEnum.CUSTOM_ERROR_ATTEND_COUNT_LIMIT_PHONE_NUMBER);
                        }
                    }
                }
            }
            //전화번호 참여 제한 여부 체크 끝

            if ( PredicateUtils.isEqualNumber(resultCode, httpSuccessCode) ) {
                logService.saveEventLogAttendButton(EventLogAttendSaveVO.saveOfPhoneNumber(eventId, vo.getArEventId(), phoneNumber, StringDefine.Y.name()));
                resultMap.put("isPossiblePhoneNumber", true);
            }
        }
        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultMap)
                .build();
    }

    /**
     * 당첨이력 조회
     * @param eventId
     * @param phoneNumber
     * @return
     */
    public ApiResultObjectDto getWinningInfoLogic(String eventId, String phoneNumber, String attendCode, String stampEventIds) {
        int resultCode = httpSuccessCode;

        boolean isStamp;
        WinningSearchResDto winningSearchResDto = new WinningSearchResDto();
        List<UserWinningInfoResDto> userWinningInfoResDtoList = new ArrayList<>();

        //필수 파라미터 체크
        if (PredicateUtils.isNull(eventId) && PredicateUtils.isNull(phoneNumber)) {
            isStamp = false;

            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

        } else {
            isStamp = StringTools.containsIgnoreCase(eventId, "S");

            //전화번호 검색일때
            if (PredicateUtils.isNotNull(phoneNumber)) {
                if (PredicateUtils.isNotNull(eventId)) {
                    if (PredicateUtils.isNotNull(stampEventIds)) {
                        userWinningInfoResDtoList = spotService.findAllGiveAwayDeliveryByEventIdAndPhoneNumber(stampEventIds, phoneNumber);
                    } else {
                        if (!isStamp) {
                            userWinningInfoResDtoList = arEventFrontService.findAllGiveAwayDeliveryByEventIdAndPhoneNumber(eventId, phoneNumber);
                        } else {
                            userWinningInfoResDtoList = stampFrontService.findStampEventGiveAwayDeliveryAtHistory(eventId, phoneNumber, null);
                        }
                    }
                }
            }
            //참여코드 검색일때
            if (PredicateUtils.isNotNull(attendCode)) {
                if (!isStamp) {
                    userWinningInfoResDtoList = arEventFrontService.findAllGiveAwayDeliveryByEventIdAndAttendCode(eventId, attendCode);
                } else {
                    userWinningInfoResDtoList = stampFrontService.findStampEventGiveAwayDeliveryAtHistory(eventId, null, attendCode);
                }
            }
        }

        if (PredicateUtils.isNotNullList(userWinningInfoResDtoList)) {
            userWinningInfoResDtoList
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(dto -> {

                        ArEventWinningEntity arEventWinning = arEventService.findByArEventWinningById(dto.getArEventWinningId());

                        if (!isStamp) {
                            //응모형일때
                            if (StringUtils.equals(arEventWinning.getSubscriptionYn(), StringDefine.Y.name())) {
                                //응모발표 값이 null 일때 false 예외처리
                                if (PredicateUtils.isNull(arEventWinning.getIsSubscriptionRaffle())) {
                                    arEventWinning.setIsSubscriptionRaffle(false);
                                }
                                //응모 발표했을때
                                if (arEventWinning.getIsSubscriptionRaffle() || PredicateUtils.isNull(arEventWinning.getIsSubscriptionRaffle())) {
                                    EventLogWinningSubscriptionEntity winningSubscriptionEntity = logService.getEventLogWinningSubscriptionByGiveAwayId(dto.getGiveAwayId());
                                    if (PredicateUtils.isNotNull(winningSubscriptionEntity)) {
                                        dto.setReceiveStatusStr(PredicateUtils.isNotNull(winningSubscriptionEntity.getId()) ? "당첨성공" : "당첨실패");
                                    }
                                }
                                //응모 발표전일때
                                if (!arEventWinning.getIsSubscriptionRaffle()) {
                                    dto.setReceiveStatusStr("응모중");
                                }
                            }
                        }

                        if (PredicateUtils.isNull(arEventWinning.getSubscriptionYn())) {
                            arEventWinning.setSubscriptionYn(StringDefine.N.name());
                        }
                        //응모형이 아닐때
                        if (PredicateUtils.isEqualsStr(arEventWinning.getSubscriptionYn(), StringDefine.N.name())) {
                            if (!isStamp) {
                                dto.setReceiveStatusStr(!dto.getIsReceive() ? StringDefine.수령확인.name() : StringDefine.수령완료.name());
                            } else {
                                dto.setReceiveStatusStr(!dto.getIsReceive() ? StringDefine.수령하기.name() : StringDefine.수령완료.name());
                            }
                            dto.setGiveAwayPassword(null);
                        }
                        dto.setSubscriptionYn(PredicateUtils.isNull(arEventWinning.getSubscriptionYn()) ? StringDefine.N.name() : arEventWinning.getSubscriptionYn());
                        dto.setIsSubscriptionWinningPresentation(arEventWinning.getIsSubscriptionWinningPresentation());
                    });

            if (!isStamp) {
                Optional<UserWinningInfoResDto> optional = userWinningInfoResDtoList.stream()
                        .filter(winning -> PredicateUtils.isEqualsStr(winning.getWinningType(), WinningTypeDefine.NFT.code()))
                        .findAny();

                if (optional.isPresent()) {
                    winningSearchResDto.setNftWinningIncludeYn(StringDefine.Y.name());
                }
            }

            Optional<UserWinningInfoResDto> couponOptional = userWinningInfoResDtoList.stream()
                    .filter(winning -> PredicateUtils.isEqualsStr(winning.getWinningType(), WinningTypeDefine.NFT쿠폰.code()))
                    .findAny();

            if (couponOptional.isPresent()) {
                winningSearchResDto.setNftCouponWinningIncludeYn(StringDefine.Y.name());
            }
        }

        if (!isStamp) {
            if (PredicateUtils.isNotNull(eventId) && PredicateUtils.isNull(stampEventIds)) {
                winningSearchResDto.setWinningPasswordYn(arEventService.findArEventByEventId(eventId).getWinningPasswordYn());
            }
        }
        winningSearchResDto.setUserWinningInfoResDtoList(userWinningInfoResDtoList);

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(winningSearchResDto)
                .build();
    }

    /**
     * 이벤트 아이디와 핸드폰번호로 당첨정보 검증(당첨정보 조회 페이지)
     * @param eventId
     * @param phoneNumber
     * @return
     */
    public ApiResultObjectDto validateWinningInfoLogic(String eventId, String phoneNumber) {
        int resultCode = httpSuccessCode;

        boolean isExitsGiveAwayPassword = false;
        //필수 파라미터 체크
        if (StringUtils.isEmpty(eventId) && StringUtils.isEmpty(phoneNumber)) {

            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

        } else {
            //경품 패스워드가 존재하는지 확인
            int giveAwayPasswordCount = arEventFrontService.countGiveAwayDeliveryByEventIdAndPhoneNumberPasswordIsNotNull(eventId, phoneNumber);
            // 한개라도 있으면 true
            if (PredicateUtils.isGreaterThanZero(giveAwayPasswordCount)) {
                isExitsGiveAwayPassword = true;
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("isExitsGiveAwayPassword", isExitsGiveAwayPassword);

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultMap)
                .build();
    }

    /**
     * 경품정보 저장하기
     * @param saveReqDto
     * @return
     */
    public ApiResultObjectDto saveGiveAwayDeliveryLogic(GiveAwayDeliverySaveReqDto saveReqDto) {
        int resultCode = httpSuccessCode;

        int idx = 0;
        int giveAwayLimitCount = 0;

        int eventLogWinningIdCount = logService.getCountByEventLogWinningIdFromGiveAway(saveReqDto.getEventLogWinningId());

        if (PredicateUtils.isNull(saveReqDto.getEventId())) {
            //이벤트 아이디가 없으면 에러처리
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

        } else if (PredicateUtils.isGreaterThanZero(eventLogWinningIdCount)) {
            //당첨 로그 데이터가 중복이면 에러처리
            resultCode = ErrorCodeDefine.CUSTOM_EVENT_LOG_WINNING_ID_DUPLICATE.code();
            log.error("saveGiveAwayDeliveryLogic error {}", ErrorCodeDefine.getLogErrorMessage(resultCode));

        } else {

            ArEventEntity arEvent = arEventService.findArEventByEventId(saveReqDto.getEventId());
            ArEventWinningEntity winningEntity = arEventService.findByArEventWinningById(saveReqDto.getArEventWinningId());

            List<ArEventWinningButtonAddEntity> arEventWinningButtonAddEntityList = null;
            
            //AR_EVENT null이면 에러처리
            if (PredicateUtils.isNull(arEvent)) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_AR_EVENT_INFO_NULL.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
            }
            //당첨 데이터가 NULL이면 에러처리
            if (PredicateUtils.isNull(winningEntity)) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_NULL_WINNING_INFO.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
            }

            //AR_EVENT 정보가 있으면 로직 시작
            if (PredicateUtils.isNotNull(arEvent)) {

                //자동당첨이 아닐때 시작
                if (PredicateUtils.isEqualsStr(winningEntity.getAutoWinningYn(), StringDefine.N.name())) {
                /**
                 * 당첨 입력 정보 기준 입력 값 validation 체크 시작
                 */
                //당첨 입력 정보 가져오기
                ArEventWinningButtonEntity winningButtonEntity = arEventService.findArEventWinningButtonById(saveReqDto.getArEventWinningButtonId());

                if (PredicateUtils.isNull(winningButtonEntity)) {
                    resultCode = Integer.parseInt(ResultCodeEnum.CUSTOM_ERROR_EVENT_WINNING_BUTTON_NULL.getCode());
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                    throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_EVENT_WINNING_BUTTON_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_EVENT_WINNING_BUTTON_NULL);
                }

                log.info("당첨입력 정보 체크 시작");

                if (PredicateUtils.isNull(winningButtonEntity.getDeliveryNameYn())) {
                    winningButtonEntity.setDeliveryNameYn(Boolean.TRUE);
                }
                if (winningButtonEntity.getDeliveryNameYn()) {
                    log.info("당첨입력 정보 체크 - 이름");
                    if (PredicateUtils.isNull(saveReqDto.getName())) {
                        //이름이 공백 값 또는 null이러 에러 처리
                        resultCode = Integer.parseInt(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_MEMBER_NAME.getCode());
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                        throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_MEMBER_NAME.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_MEMBER_NAME);
                    }
                }

                if (PredicateUtils.isNull(winningButtonEntity.getDeliveryBirthYn())) {
                    winningButtonEntity.setDeliveryBirthYn(Boolean.FALSE);
                }
                if (winningButtonEntity.getDeliveryBirthYn()) {
                    log.info("당첨입력 정보 체크 - 생년월일");
                    if (!PredicateUtils.isStrLength(saveReqDto.getMemberBirth().trim(), 8)) {
                        //생년월일이 8자리가 아니면 에러처리
                        resultCode = Integer.parseInt(ResultCodeEnum.CUSTOM_ERROR_MEMBER_BIRTH_LENGTH.getCode());
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                        throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_MEMBER_BIRTH_LENGTH.getDesc(), ResultCodeEnum.CUSTOM_ERROR_MEMBER_BIRTH_LENGTH);
                    } else {
                        log.info("당첨입력 정보 체크 - 생년월일 특수문자 포함여부");
                        if (EventUtils.isSpecialCharacter(saveReqDto.getMemberBirth().trim())) {
                            resultCode = Integer.parseInt(ResultCodeEnum.CUSTOM_ERROR_MEMBER_BIRTH_EXITS_SPECIAL_CHARACTER.getCode());
                            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_MEMBER_BIRTH_EXITS_SPECIAL_CHARACTER.getDesc(), ResultCodeEnum.CUSTOM_ERROR_MEMBER_BIRTH_EXITS_SPECIAL_CHARACTER);
                        }
                        log.info("당첨입력 정보 체크 - 만 나이 15세 이하 체크");
                        if (PredicateUtils.isLowerThan(EventUtils.calculateAgeForKorean(saveReqDto.getMemberBirth()), 15)) {
                            //만 나이 15세 이하 에러처리
                            resultCode = Integer.parseInt(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_MEMBER_BIRTH_15.getCode());
                            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_MEMBER_BIRTH_15.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_MEMBER_BIRTH_15);
                        }
                    }
                }
                log.info("당첨입력 정보 체크 - 핸드폰번호");
                if (PredicateUtils.isNull(winningButtonEntity.getDeliveryPhoneNumberYn())) {
                    winningButtonEntity.setDeliveryPhoneNumberYn(Boolean.FALSE);
                }
                if (winningButtonEntity.getDeliveryPhoneNumberYn()) {
                    if (PredicateUtils.isNull(saveReqDto.getPhoneNumber())) {   //[DTWS-594]휴대폰번호 입력 자릿수 해제 검토
//                    if (PredicateUtils.isNull(saveReqDto.getPhoneNumber()) || !EventUtils.isPhoneNumber(aes256Utils.decrypt(saveReqDto.getPhoneNumber()))) {
                        //핸드폰번호 공백 값 또는 null 또는 핸드폰형식에 안맞으면 에러 처리
                        resultCode = Integer.parseInt(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_PHONE_NUMBER.getCode());
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                        throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_PHONE_NUMBER.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_PHONE_NUMBER);
                    }
                }
                log.info("당첨입력 정보 체크 - 주소");
                if (PredicateUtils.isNull(winningButtonEntity.getDeliveryAddressYn())) {
                    winningButtonEntity.setDeliveryAddressYn(Boolean.FALSE);
                }
                if (winningButtonEntity.getDeliveryAddressYn()) {
                    if (PredicateUtils.isNull(saveReqDto.getAddress()) || PredicateUtils.isNull(saveReqDto.getAddressDetail()) || PredicateUtils.isNull(saveReqDto.getZipCode())) {
                        //핸드폰번호 공백 값 또는 null이러 에러 처리
                        resultCode = Integer.parseInt(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_ADDRESS.getCode());
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                        throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_ADDRESS.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_ADDRESS);
                    }
                }

                // 자동당첨이 아니고, 기타 입력이 있어야 하는 경우 데이터 밸리데이션 체크
                arEventWinningButtonAddEntityList = arEventService.findAllArEventWinningButtonAddByArEventWinningButtonId(winningButtonEntity.getArEventWinningButtonId());
                if (!PredicateUtils.isNullList(arEventWinningButtonAddEntityList)) {
                    if (PredicateUtils.isNullList(saveReqDto.getGiveAwayDeliveryButtonAddInputList())) {
                        resultCode = Integer.parseInt(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_ETC_EMPTY.getCode());
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                        throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_ETC_EMPTY.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_ETC_EMPTY);
                    }

//                    List<GiveAwayDeliveryButtonAddInputDto> isEmptyList = saveReqDto.getGiveAwayDeliveryButtonAddInputList().stream()
//                            .filter(input -> PredicateUtils.isNull(input.getArEventWinningButtonAddId()) || PredicateUtils.isNull(input.getFieldValue())).collect(Collectors.toList());

                    //[DTWS-370] 2023.11.27 안지호 - 아이디값 또는 필드값이 없으면 리스트에서 삭제 처리 변경
                    for (Iterator<GiveAwayDeliveryButtonAddInputDto>it = saveReqDto.getGiveAwayDeliveryButtonAddInputList().iterator(); it.hasNext();) {
                        GiveAwayDeliveryButtonAddInputDto addInputDto = it.next();
                        if (PredicateUtils.isNull(addInputDto.getArEventWinningButtonAddId()) || PredicateUtils.isNull(addInputDto.getFieldValue())) {
                            it.remove();
                        }
                    }

                    if (PredicateUtils.isNullList(saveReqDto.getGiveAwayDeliveryButtonAddInputList())) {
                        log.info("PredicateUtils.isNullList(isEmptyList)");
                        resultCode = Integer.parseInt(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_ETC_EMPTY.getCode());
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                        throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_ETC_EMPTY.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_ETC_EMPTY);
                    }
                }
            }
            //자동당첨이 아닐때 끝

                /**
                 * 당첨 입력 정보 기준 입력 값 validation 체크 끝
                 */

                //참여번호 받는 이벤트가 아니고
                if (!arEvent.getArAttendConditionCodeYn()) {
                    //중복당첨 제한일때 (전화번호로 제한 로그 바라본다)
                    if (PredicateUtils.isEqualY(arEvent.getDuplicateWinningType())) {
                        //중복 당첨 당첨제한 회수
                        int limitCount = arEvent.getDuplicateWinningCount();
                        //전체기한내 일떄
                        if (PredicateUtils.isEqualZero(arEvent.getDuplicateWinningLimitType())) {
                            log.info("saveGiveAwayDeliveryLogic : {} 중복당첨수 제한 >> 전체기한내 일떄");

                            int allTimeLogCount = arEventFrontService.countGiveAwayDeliveryByEventIdAndPhoneNumber(saveReqDto.getEventId(), saveReqDto.getPhoneNumber());
                            //로그 개수가 설정개수보다 많을때
                            if (PredicateUtils.isGreaterThanEqualTo(allTimeLogCount, limitCount)) {
                                log.info("saveGiveAwayDeliveryLogic : {} 중복당첨 제한 > 전체기한내 > 개수 초과!");
                                giveAwayLimitCount = limitCount;
                                //isSave = false;
                                resultCode = ErrorCodeDefine.CUSTOM_ERROR_LIMIT_WINNING_COUNT.code();

                                //PV 로그 저장 [DTWS-399]
//                                EventLogPvReqDto pvReqDto = new EventLogPvReqDto();
//                                pvReqDto.setEventId(saveReqDto.getEventId());
//                                pvReqDto.setPvLogType(EventLogPvKeyDefine.MAIN_ENTERINFO_1.name());
//                                pvReqDto.setOrder(String.valueOf(saveReqDto.getArEventWinningId()));
//                                pvReqDto.setCode("0");
//                                logService.saveEventLogPv(pvReqDto, EventLogPvKeyDefine.getByCode(pvReqDto.getPvLogType()));
                            }
                        }

                        //1일일떄
                        if (PredicateUtils.isEqualNumber(arEvent.getDuplicateWinningLimitType(), 1)) {
                            log.info("saveGiveAwayDeliveryLogic : {} 중복당첨수 제한 >> 1일 일떄");

                            int todayLogCount = arEventFrontService.countGiveAwayDeliveryByEventIdAndPhoneNumberAndToday(saveReqDto.getEventId(), saveReqDto.getPhoneNumber());
                            //로그 개수가 설정개수보다 많을때
                            if (PredicateUtils.isGreaterThanEqualTo(todayLogCount, limitCount)) {
                                log.info("saveGiveAwayDeliveryLogic : {} 중복당첨 제한 > 1일일떄 > 개수 초과!");
                                giveAwayLimitCount = limitCount;
                                //isSave = false;
                                resultCode = ErrorCodeDefine.CUSTOM_ERROR_LIMIT_WINNING_COUNT.code();

                                //PV 로그 저장 [DTWS-399]
//                                EventLogPvReqDto pvReqDto = new EventLogPvReqDto();
//                                pvReqDto.setEventId(saveReqDto.getEventId());
//                                pvReqDto.setPvLogType(EventLogPvKeyDefine.MAIN_ENTERINFO_1.name());
//                                pvReqDto.setOrder(String.valueOf(saveReqDto.getArEventWinningId()));
//                                pvReqDto.setCode("0");
//                                logService.saveEventLogPv(pvReqDto, EventLogPvKeyDefine.getByCode(pvReqDto.getPvLogType()));
                            }
                        }   //1일일떄 끝
                    }   //중복당첨 제한일때 끝
                }   //참여번호 받는 이벤트가 아니고 끝

                //조건을 통과됬을때 저장
                if (resultCode == httpSuccessCode) {
                    //당첨 데이터가 있으면 로직 시작
                    if (PredicateUtils.isNotNull(winningEntity)) {

                        String trId = "";
                        String gifticonOrderNo = "";
                        String gifticonOrderCd = "";

                        try {
                            EventGiveAwayDeliveryEntity entity = new EventGiveAwayDeliveryEntity();
                            //자동당첨이 아닐때
                            if (PredicateUtils.isEqualsStr(winningEntity.getAutoWinningYn(), StringDefine.N.name())) {
                                 entity = EventGiveAwayDeliveryEntity.saveOf(
                                         ModelMapperUtils.convertModel(saveReqDto, EventGiveAwayDeliveryEntity.class), winningEntity.getWinningType(), winningEntity.getProductName(), gifticonOrderCd, trId, gifticonOrderNo
                                );
                            }
                            //자동당첨일때
                            if (PredicateUtils.isEqualsStr(winningEntity.getAutoWinningYn(), StringDefine.Y.name())) {

                                if (PredicateUtils.isNotEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.꽝.code())) {
                                    entity = EventGiveAwayDeliveryEntity.autoSaveOf(
                                            ModelMapperUtils.convertModel(saveReqDto, EventGiveAwayDeliveryEntity.class), winningEntity.getWinningType(), winningEntity.getProductName()
                                    );
                                }
                            }
                            idx = arEventFrontService.saveGiveAwayDelivery(entity);

                            // 자동 당첨이 아니고, 기타 당첨정보 입력이 설정되어있을때, 기타 당첨정보 저장
                            if (PredicateUtils.isEqualsStr(winningEntity.getAutoWinningYn(), StringDefine.N.name()) && !PredicateUtils.isNullList(arEventWinningButtonAddEntityList)) {
                                try {
                                    arEventFrontService.saveAllEventGiveAwayDeliveryButtonAdd(ModelMapperUtils.convertModelInList(saveReqDto.getGiveAwayDeliveryButtonAddInputList(), EventGiveAwayDeliveryButtonAddEntity.class) , idx);
                                } catch (Exception e) {
                                    log.error(e.getMessage(), e);
                                }
                            }
                        } catch (DataIntegrityViolationException dve) { //이벤트 로그 중복 사용 방지 처리 (2022.05.27) - 자자원정대 이벤트 CV 관련 이슈 처리
                            idx = 0;
                            log.error("ConstraintViolationException {} ", dve.getMessage());
                            resultCode = Integer.parseInt(ResultCodeEnum.CUSTOM_EVENT_LOG_WINNING_ID_DUPLICATE.getCode());
                            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                        } catch (Exception e) {
                            //SQL에러시
                            idx = 0;
                            log.error("Exception {} ", e.getMessage());
                            log.warn("RuntimeException for rollback");
                            resultCode = Integer.parseInt(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SQL_ERROR.getCode());
                            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                        }

                        log.info("경품 저장 데이터 Key {} ", idx);
                        //경품정보가 DB에 정상적으로 저장이 되었을때
                        if (idx > 0) {
                            //PV 로그저장 [DTWS-401]
//                            EventLogPvReqDto pvReqDto = new EventLogPvReqDto();
//                            pvReqDto.setEventId(saveReqDto.getEventId());
//                            pvReqDto.setPvLogType(EventLogPvKeyDefine.MAIN_ENTERINFO_1.name());
//                            pvReqDto.setOrder(String.valueOf(saveReqDto.getArEventWinningId()));
//                            pvReqDto.setCode("1");
//                            logService.saveEventLogPv(pvReqDto, EventLogPvKeyDefine.getByCode(EventLogPvKeyDefine.MAIN_ENTERINFO_1.name()));

                            //EVENT_GIVE_AWAY_DELIVERY 의 give_away_id 값 업데이트
                            try {
                                logService.updateEventLogWinningGiveAwayId(saveReqDto.getEventLogWinningId(), idx);
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }

                            //경품이 기프티콘일때 기프티콘 발급 api 연동
                            if (PredicateUtils.isEqualsStr(WinningTypeDefine.기프티콘.code(), winningEntity.getWinningType())) {

                                //수령받은 기프티콘 개수 가져오기(기프티콘 최종발행시 방어 로직으로 해당 전화번호로 해당 이벤트ID에 발급이력이 동일 기프티콘 상품코드에 대한 발급이력이 1회이상 있는지 체크(홍혁의M 요청 사항 / 2022.04.05))
                                int receiveGifticonCount = logService.getCountByGiveAwayReceiveGifticon(saveReqDto.getEventId(), saveReqDto.getArEventWinningId(), saveReqDto.getPhoneNumber(), saveReqDto.getMemberBirth());
                                //기프티콘 개수가 0보다 크면 에러처리
                                if (PredicateUtils.isGreaterThanZero(receiveGifticonCount)) {
                                    resultCode = ErrorCodeDefine.CUSTOM_ERROR_GIFTICON_RECEIVE_COUNT.code();
                                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                                }
                                //기프티콘 개수가 0이면 정상 발급
                                if (PredicateUtils.isEqualZero(receiveGifticonCount)) {
                                    GifticonOrderResDto gifticonOrderResDto = new GifticonOrderResDto();

                                    try {
                                        //기프티콘 API 통신
                                        String rcvrMdn = aes256Utils.decrypt(saveReqDto.getPhoneNumber());  // DTWS-377
                                        gifticonOrderResDto = skApiLogic.sendGifticonAtGiveAwayLogic(saveReqDto.getEventId(), saveReqDto.getArEventWinningId(), rcvrMdn);

                                        //기프티콘 발급이 정상적으로 되지 않았을때
                                        if (PredicateUtils.isNull(gifticonOrderResDto)) {
                                            gifticonOrderCd = "9999";
                                            resultCode = Integer.parseInt(ResultCodeEnum.REST_API_CALL_ERROR.getCode());
                                            log.error("Gificon Api Error {} ", ResultCodeEnum.REST_API_CALL_ERROR.getDesc());
                                        } else {
                                            //기프티콘 발급이 정상적으로 되었을때
                                            gifticonOrderCd = gifticonOrderResDto.getResultCd();
                                            log.info("기프티콘 response {} ", gifticonOrderResDto);

                                            //정상 발급일떄
                                            if (PredicateUtils.isEqualsStr(gifticonOrderCd, "0000")) {
                                                trId = gifticonOrderResDto.getTrId();
                                                gifticonOrderNo = gifticonOrderResDto.getOrdInfo().get(0).getOrderNo();
                                                log.info("기프티콘 정상 발급! {} ", gifticonOrderResDto);
                                            }
                                            //정상 발급이 아니면 에러 처리
                                            if (!PredicateUtils.isEqualsStr(gifticonOrderCd, "0000")) {
                                                resultCode = ErrorCodeDefine.CUSTOM_ERROR_GIFTICON_SEND_ERROR.code();
                                                log.error("Gifticon Api ResultCd Not '0000', ResultCd >> {}", gifticonOrderCd);
                                            }
                                        }

                                    } catch (Exception e) {
                                        gifticonOrderCd = "9999";
                                        log.error("gifticonOrderResDto Error {} ", e.getMessage());
                                    } finally {
                                        try {
                                            logService.updateEventGiveAwayDeliveryByGifticonIssuse(idx, trId, gifticonOrderNo, gifticonOrderCd);
                                        } catch (Exception e) {
                                            log.error(e.getMessage(), e);
                                        }
                                        Map<String, Object> resultMap = new HashMap<>();
                                        resultMap.put("id", idx);   //저장된 로그 시퀀스
                                        resultMap.put("limitCount", giveAwayLimitCount);    //제한에 걸렸을때 제한개수

                                        return new ApiResultObjectDto().builder()
                                                .resultCode(resultCode)
                                                .result(resultMap)
                                                .build();
                                    }
                                }
                            } //end if 기프티콘 지급

                            //NFT 토큰 소유권 이전 로직 시작 SS-20193
                            if (PredicateUtils.isEqualsStr(WinningTypeDefine.NFT.code(), winningEntity.getWinningType())) {
                                //응모형이 아닌 즉시당첨형일때
                                if (PredicateUtils.isEqualsStr(StringDefine.N.name(), winningEntity.getSubscriptionYn())) {
                                    log.info("NFT 저장소 정보 업데이트 :: eventWinningLogId >> " + saveReqDto.getEventLogWinningId() + ", giveAwayId >> " + idx);
                                    arEventFrontService.updateNftRepositoryAtGiveAwayDelivery(saveReqDto.getEventLogWinningId(), idx);
                                }
                            }
                            //NFT 토큰 소유권 이전 로직 끝

                            //응모형이 아닌 즉시당첨형일때 NFT 쿠폰 소유권 이전 로직 시작
                            if (PredicateUtils.isEqualsStr(WinningTypeDefine.NFT쿠폰.code(), winningEntity.getWinningType())
                                    && PredicateUtils.isEqualsStr(StringDefine.N.name(), winningEntity.getSubscriptionYn())) {

                                log.info("쿠폰 저장소 정보 업데이트 :: eventWinningLogId >> " + saveReqDto.getEventLogWinningId() + ", giveAwayId >> " + idx);
                                arEventFrontService.updateCouponRepositoryAtGiveAwayDelivery(saveReqDto.getEventLogWinningId(), idx);
                            }
                            //NFT 쿠폰 소유권 이전 로직 끝
                            //SS-20193

                            //서베이고 일때 - survey_log_attend 테이블의 give_away_id 값 업데이트하기 시작
                            if (PredicateUtils.isNotNull(saveReqDto.getSurveyLogAttendId()) || StringUtils.isNotEmpty(saveReqDto.getSurveyLogAttendId())) {
                                SurveyLogAttendEntity entity = new SurveyLogAttendEntity();
                                entity.setSurveyLogAttendId(saveReqDto.getSurveyLogAttendId());
                                entity.setGiveAwayId(idx);

                                surveyGoLogService.updateSurveyLogAttend(entity);

                                //서베이고 ROW 통계 데이터 가공 후 저장
                                surveyGoMobileLogic.saveSurveyAnswerStaticsData(saveReqDto.getSurveyLogAttendId());
                            }
                            //서베이고 일때 - survey_log_attend 테이블의 give_away_id 값 업데이트하기 끝

                            //OCB 포인트 지급 성공 로그 저장 시작
//                            if (PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.OCB포인트.code())) {
//                                OcbPointSaveEntity ocbPointSaveEntity = arEventService.findOcbPointSaveByArEventIdAndArEventWinningId(arEvent.getArEventId(), winningEntity.getArEventWinningId());
//                                log.info("====================당첨정보 저장 후 OCB포인트 API 통신 로그 저장 시작====================");
//                                log.info("====================giveAwayId ::: " + idx +" ====================");
//                                try {
//                                    logService.saveOcbLogPointSave(ocbPointSaveEntity, arEvent.getEventId(), saveReqDto.getPhoneNumber(), null, arEvent.getOcbPointSaveType(), true, GsonUtils.getJsonStringAsObject(ocbPointApiResDto), idx);
//                                } catch (Exception e) {
//                                    log.error(e.getMessage(), e);
//                                }
//                            }
                            //OCB 포인트 지급 성공 로그 저장 끝

                            if (PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.OCB쿠폰.code())) {
                                try {
                                    arEventFrontService.saveArEventNftCouponRepositoryByOcbCoupon(saveReqDto.getEventLogWinningId(), winningEntity.getOcbCouponId(), idx);
                                } catch (Exception e) {
                                    log.error(e.getMessage(), e);
                                }
                            }
                        }
                    }   //당첨 데이터가 있으면 로직 끝
                } else {
                    if (PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.OCB쿠폰.code())) {
                        arEventService.deleteArEventNftCouponRepositoryByEventWinningLogId(saveReqDto.getEventLogWinningId());
                    }
                }
            }   //AR_EVENT 정보가 있으면 로직 끝
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("id", idx);   //저장된 로그 시퀀스
        resultMap.put("limitCount", giveAwayLimitCount);    //제한에 걸렸을때 제한개수

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultMap)
                .build();
    }

    /**
     * 이벤트 당첨 로직
     * @param reqDto
     * @return
     */
    public ApiResultObjectDto eventWinningLogic(EventWinningReqDto reqDto) {

        log.info("eventWinningLogic : {} 시작");

        if (PredicateUtils.isNotNull(reqDto.getPhoneNumber())) {
            if (PredicateUtils.isEqualsStr(reqDto.getPhoneNumber(), "undefined")) {
                reqDto.setPhoneNumber(null);
            }
        }

        ArEventByIdAtWinningProcessMapperVO arEventEntity = arEventService.findArEventByEventIdAtWinningProcess(reqDto.getEventId());
        //당첨 조건
        LinkedList<ArEventWinningEntity> winningLinkedList = arEventService.findArEventWinningListByArEventIdAndSubscriptionYn(arEventEntity.getArEventId(), false, false);
        //당첨 제한 테이블 리스트 가져오기
        List<EventLogWinningLimitMapperVO> winningLimitList = logService.selectEventLogWinningLimitListByArEventId(arEventEntity.getArEventId());

        /**
         * 당첨조건이 꽝만 있을떄는 무조건 꽝으로 보낸다
         */
        if (PredicateUtils.isNullList(winningLinkedList)) {
            //꽝 로직
            return this.failWinningLogic(reqDto, arEventEntity);
        }
        log.info("eventWinningLogic : {} winningLinkedList 개수 확인");
        //당첨 로직 시작
        if (!winningLinkedList.isEmpty()) {
            /** 중복당첨수 제한 여부 시작 **/

            //중복당첨수 제한없음일때
            if (PredicateUtils.isEqualN(arEventEntity.getDuplicateWinningType())) {
                log.info("eventWinningLogic : {} 중복당첨수 제한이 '제한없음' 일때");
                //검증
                return this.validateWinningLogic( reqDto, arEventEntity, winningLinkedList, winningLimitList );

            } else {
                //중복당첨수 제한없음일때 끝
                log.info("eventWinningLogic : {} 중복당첨수 제한이 '제한' 일때");
                //중복당첨수 제한일때 참여번호 제한 시작
                if (arEventEntity.getArAttendConditionCodeYn()) {

                    boolean isTodayLimit = false, isTotalLimit = false;

                    //당첨 제한 값 확인 시작
                    if (!PredicateUtils.isNullList(winningLimitList)) {
                        //ID_CODE_TODAY 코드 값 확인
                        String todayAttendCode = StringTools.joinStringsNoSeparator(
                                String.valueOf(arEventEntity.getArEventId()), reqDto.getAttendCode(), DateUtils.getNowMMDD()
                        );
                        //ID_CODE 코드 값 확인
                        String totalAttendCode = StringTools.joinStringsNoSeparator(
                                String.valueOf(arEventEntity.getArEventId()), reqDto.getAttendCode()
                        );
                        //ID_CODE_TODAY 코드로 값이 존재하는지 체크
                        Optional<EventLogWinningLimitMapperVO> todayAttendCodeLimitOptional = winningLimitList.stream()
                                .filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), todayAttendCode))
                                .findAny();
                        //ID_CODE 코드로 값이 존재하는지 체크
                        Optional<EventLogWinningLimitMapperVO> totalAttendCodeLimitOptional = winningLimitList.stream()
                                .filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), totalAttendCode))
                                .findAny();
                        //값이 존재하면 true
                        if (todayAttendCodeLimitOptional.isPresent()) {
                            log.info("winning limit 참여코드 일제한 걸림");
                            isTodayLimit = true;
                        }
                        if (totalAttendCodeLimitOptional.isPresent()) {
                            log.info("winning limit 참여코드 전체제한 걸림");
                            isTotalLimit = true;
                        }
                    }
                    //당첨 제한 값 확인 끝

                    log.info("eventWinningLogic : {} arEventEntity.getArAttendConditionCodeYn : true 이면");
                    //중복 당첨 당첨제한 회수
                    int limitCount = arEventEntity.getDuplicateWinningCount();

                    //1일일떄
                    if (PredicateUtils.isEqualNumber(arEventEntity.getDuplicateWinningLimitType(), 1)) {
                        log.info("eventWinningLogic : {} 중복당첨수 제한 >> 1일떄");

                        if (isTodayLimit) {
                            log.info("eventWinningLogic : {} winning limit 참여코드 일제한 걸림 > 꽝!");
                            //꽝 로직
                            return this.failWinningLogic(reqDto, arEventEntity);
                        } else {

                            int todayLogCount = logService.getCountEventWinningLogByEventIdAndAttendCodeAndTodayNotFail2(arEventEntity.getArEventId(), reqDto.getAttendCode());
                            //로그 개수가 설정개수보다 많을때
                            if (PredicateUtils.isGreaterThanEqualTo(todayLogCount, limitCount)) {
                                log.info("eventWinningLogic : {} 꽝!");

                                try {
                                    logService.saveEventLogWinningLimit(arEventEntity.getArEventId(), 0, DateUtils.getNowMMDD(), null, reqDto.getAttendCode(), EventLogWinningLimitDefine.ID_CODE_TODAY.name());
                                } catch (DuplicateKeyException e) {
                                    log.error("saveEventLogWinningLimit error {} ", e.toString());
                                    return this.failWinningLogic(reqDto, arEventEntity);
                                }
                                //꽝 로직
                                return this.failWinningLogic(reqDto, arEventEntity);
                            }
                        }
                    }

                    //전체기한내 일떄
                    if (PredicateUtils.isEqualZero(arEventEntity.getDuplicateWinningLimitType())) {
                        log.info("eventWinningLogic : {} 중복당첨수 제한 >> 전체기한내 일떄");

                        if (isTotalLimit) {
                            log.info("eventWinningLogic : {} winning limit 참여코드 전체제한 걸림 > 꽝!");
                            //꽝 로직
                            return this.failWinningLogic(reqDto, arEventEntity);
                        } else {

                            int allTimeLogCount = logService.getCountEventWinningLogByArEventIdAndAttendCodeNotFail(arEventEntity.getArEventId(), reqDto.getAttendCode());
                            //로그 개수가 설정개수보다 많을때
                            if (PredicateUtils.isGreaterThanEqualTo(allTimeLogCount, limitCount)) {
                                log.info("eventWinningLogic : {} 꽝!");
                                try {
                                    logService.saveEventLogWinningLimit(arEventEntity.getArEventId(), 0, null, null, reqDto.getAttendCode(), EventLogWinningLimitDefine.ID_CODE.name());
                                } catch (DuplicateKeyException e) {
                                    log.error("saveEventLogWinningLimit error {} ", e.toString());
                                    return this.failWinningLogic(reqDto, arEventEntity);
                                }
                                //꽝 로직
                                return this.failWinningLogic(reqDto, arEventEntity);
                            }
                        }
                    }

                    log.info("eventWinningLogic : {} 중복당첨수 제한이 '제한' 일때 통과");
                    //검증
                    return this.validateWinningLogic( reqDto, arEventEntity, winningLinkedList, winningLimitList );
                }
                //중복당첨수 제한일때 끝

                //중복당첨수 제한일때 전화번호 제한 시작
                if (arEventEntity.getAttendConditionMdnYn()) {

                    boolean isTodayLimit = false, isTotalLimit = false;

                    //당첨 제한 값 확인 시작
                    if (PredicateUtils.isNotNullList(winningLimitList)) {
                        //ID_MDN_TODAY 코드 값 확인
                        String todayAttendCode = StringTools.joinStringsNoSeparator(
                                String.valueOf(arEventEntity.getArEventId()), reqDto.getPhoneNumber(), DateUtils.getNowMMDD()
                        );
                        //ID_MDN 코드 값 확인
                        String totalAttendCode = StringTools.joinStringsNoSeparator(
                                String.valueOf(arEventEntity.getArEventId()), reqDto.getPhoneNumber()
                        );
                        //ID_MDN_TODAY 코드로 값이 존재하는지 체크
                        Optional<EventLogWinningLimitMapperVO> todayMdnLimitOptional = winningLimitList.stream()
                                .filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), todayAttendCode))
                                .findAny();
                        //ID_MDN 코드로 값이 존재하는지 체크
                        Optional<EventLogWinningLimitMapperVO> totalMdnLimitOptional = winningLimitList.stream()
                                .filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), totalAttendCode))
                                .findAny();
                        //값이 존재하면 true
                        if (todayMdnLimitOptional.isPresent()) {
                            log.info("winning limit 전화번호 일제한 걸림");
                            isTodayLimit = true;
                        }
                        if (totalMdnLimitOptional.isPresent()) {
                            log.info("winning limit 전화번호 전체제한 걸림");
                            isTotalLimit = true;
                        }
                    }
                    //당첨 제한 값 확인 끝

                    log.info("eventWinningLogic : {} attendConditionMdnYn true 이면");
                    //중복 당첨 당첨제한 회수
                    int limitCount = arEventEntity.getDuplicateWinningCount();

                    //1일일떄
                    if (PredicateUtils.isEqualNumber(arEventEntity.getDuplicateWinningLimitType(), 1)) {
                        log.info("eventWinningLogic : {} 중복당첨수 제한 >> 1일떄");

                        if (isTodayLimit) {
                            log.info("eventWinningLogic : {} winning limit 전화번호 일제한 걸림 > 꽝!");
                            //꽝 로직
                            return this.failWinningLogic(reqDto, arEventEntity);
                        } else {
                            int todayLogCount = logService.getCountEventWinningLogByEventIdAndPhoneNumberAndTodayNotFail(arEventEntity.getArEventId(), reqDto.getPhoneNumber());
                            //로그 개수가 설정개수보다 많을때
                            if (PredicateUtils.isGreaterThanEqualTo(todayLogCount, limitCount)) {
                                log.info("eventWinningLogic : {} 꽝!");

                                try {
                                    logService.saveEventLogWinningLimit(arEventEntity.getArEventId(), 0, DateUtils.getNowMMDD(), null, reqDto.getPhoneNumber(), EventLogWinningLimitDefine.ID_MDN_TODAY.name());
                                } catch (DuplicateKeyException e) {
                                    log.error("saveEventLogWinningLimit error {} ", e.toString());
                                    return this.failWinningLogic(reqDto, arEventEntity);
                                }
                                //꽝 로직
                                return this.failWinningLogic(reqDto, arEventEntity);
                            }
                        }
                    }

                    //전체기한내 일떄
                    if (PredicateUtils.isEqualZero(arEventEntity.getDuplicateWinningLimitType())) {
                        log.info("eventWinningLogic : {} 중복당첨수 제한 >> 전체기한내 일떄");

                        if (isTotalLimit) {
                            log.info("eventWinningLogic : {} winning limit 전화번호 전체제한 걸림 > 꽝!");
                            //꽝 로직
                            return this.failWinningLogic(reqDto, arEventEntity);
                        } else {

                            int allTimeLogCount = logService.getCountEventWinningLogByEventIdAndPhoneNumberNotFail(arEventEntity.getArEventId(), reqDto.getPhoneNumber());
                            //로그 개수가 설정개수보다 많을때
                            if (PredicateUtils.isGreaterThanEqualTo(allTimeLogCount, limitCount)) {
                                log.info("eventWinningLogic : {} 꽝!");
                                try {
                                    logService.saveEventLogWinningLimit(arEventEntity.getArEventId(), 0, null, null, reqDto.getPhoneNumber(), EventLogWinningLimitDefine.ID_MDN.name());
                                } catch (DuplicateKeyException e) {
                                    log.error("saveEventLogWinningLimit error {} ", e.toString());
                                    return this.failWinningLogic(reqDto, arEventEntity);
                                }
                                //꽝 로직
                                return this.failWinningLogic(reqDto, arEventEntity);
                            }
                        }
                    }
                    log.info("eventWinningLogic : {} 중복당첨수 제한이 '제한' 일때 통과");
                    //검증
                    return this.validateWinningLogic( reqDto, arEventEntity, winningLinkedList, winningLimitList );
                }
                //중복당첨수 제한일때 전화번호 제한 끝

                //중복당첨수 제한 > 참여번호 or 전화번호가 아닐떄
                if (!arEventEntity.getArAttendConditionCodeYn() && !arEventEntity.getAttendConditionMdnYn()) {
                    //og.info("eventWinningLogic : {} arEventEntity.getArAttendConditionCodeYn or getAttendConditionMdnYn : false 이면");
                    return this.validateWinningLogic( reqDto, arEventEntity, winningLinkedList, winningLimitList );
                }
            }
        }
        return null;
    }

    /**
     * 당첨 조건 검증 (맵핑일때)
     * @param reqDto
     * @param arEventEntity
     * @return
     */
    @Transactional
    public ApiResultObjectDto validateWinningLogic(EventWinningReqDto reqDto, ArEventByIdAtWinningProcessMapperVO arEventEntity, LinkedList<ArEventWinningEntity>winningLinkedList, List<EventLogWinningLimitMapperVO>winningLimitList) {
        log.info("validateWinning : {} 공통 당첨 조건 시작");

        WebEventBaseEntity webEventBase = arEventService.findEventBase(reqDto.getEventId());
        /**
         * 이벤트 방식이 기본, 브릿지, 드래그앤드랍, 퀴즈형, 분석형일떄 - 오브젝트 맵핑이 있는 타입
         */
        if (EventLogicalTypeDefine.기본형.value().equals(arEventEntity.getEventLogicalType())
                || EventLogicalTypeDefine.브릿지형.value().equals(arEventEntity.getEventLogicalType())
                || EventLogicalTypeDefine.드래그앤드랍.value().equals(arEventEntity.getEventLogicalType())
                || EventLogicalTypeDefine.퀴즈형.value().equals(arEventEntity.getEventLogicalType())
                || EventLogicalTypeDefine.분석형.value().equals(arEventEntity.getEventLogicalType())) {

            int objectNumber = 0;
            //AR 이벤트일때
            if (PredicateUtils.isEqualsStr(webEventBase.getEventType(), EventTypeDefine.AR.name())) {
                //ar_event_object_id 값 없으면 return
                if (PredicateUtils.isNull(reqDto.getArEventObjectId())) {

                    return new ApiResultObjectDto().builder()
                            .resultCode(ErrorCodeDefine.CUSTOM_ERROR_NULL_AR_EVENT_OBJECT_ID.code())
                            .result("")
                            .build();
                }

                //오브젝트 정보 가져오기
                ArEventObjectEntity objectEntity = arEventService.findArEventObjectByIdAtWinningProcess(reqDto.getArEventObjectId(), arEventEntity.getArEventId());
                log.info("objectEntity >> " + objectEntity.toString());

                //오브젝트 번호
                objectNumber = objectEntity.getObjectSort();
            }

            //서베이고 이벤트 일때
            if (PredicateUtils.isEqualsStr(webEventBase.getEventType(), EventTypeDefine.SURVEY.name())) {
                //survey_subject_category_id 값 없으면 return
                if (PredicateUtils.isNull(reqDto.getSurveySubjectCategoryId())) {

                    return new ApiResultObjectDto().builder()
                            .resultCode(ErrorCodeDefine.CUSTOM_ERROR_NULL_SURVEY_SUBJECT_CATEGORY_ID.code())
                            .result("")
                            .build();
                }
                //유형 순서 가져오기
                objectNumber = surveyGoMobileService.findSurveySubjectCategorySortByIndex(reqDto.getSurveySubjectCategoryId());
            }

            //이벤트에 있는 당첨정보중 매핑되어있는 넘버 리스트 가져오기 ( objectMappingType :: Y 조건으로 매핑목록을 셀렉트한다)
            List<Integer> objectMappingNumberList = new ArrayList<>();
            if (PredicateUtils.isNotNullList(winningLinkedList)) {
                for (ArEventWinningEntity winningEntity : winningLinkedList) {
                    if (PredicateUtils.isEqualY(winningEntity.getObjectMappingType())) {
                        objectMappingNumberList.add(winningEntity.getObjectMappingNumber());
                    }
                }
            }

            log.info("validateWinning : {} 오브젝트와 당첨정보가 매핑여부 확인");
            /** 오브젝트 번호와 매핑되어있는 넘버중에 매핑여부 확인 **/
            if (objectMappingNumberList.contains(objectNumber)) {
                log.info("validateWinning : {} 오브젝트와 당첨정보가 매핑 되어있음 >> 매핑조건 시작!");
                /** 매핑되어있는 당첨정보 조회 **/
                ArEventWinningEntity mappingWinningEntity = new ArEventWinningEntity();
                for (ArEventWinningEntity winningEntity : winningLinkedList) {
                    if (PredicateUtils.isEqualsStr(String.valueOf(winningEntity.getArEventId()), String.valueOf(arEventEntity.getArEventId()))) {
                        if (PredicateUtils.isEqualsStr(String.valueOf(winningEntity.getObjectMappingNumber()), String.valueOf(objectNumber))) {
                            mappingWinningEntity = winningEntity;
                            break;
                        }
                    }
                }

                log.info(">>> " + mappingWinningEntity.toString());

                //2022.2.24 기프티콘 라이브 테스트 에러 처리
                if (PredicateUtils.isEqualsStr(WinningTypeDefine.꽝.code(), mappingWinningEntity.getWinningType())) {
                    log.info("맵핑되어있는 꽝일때 {} ", mappingWinningEntity.getWinningType());
                    return this.failWinningLogic(reqDto, arEventEntity);
                }

                boolean isLimitHour = false, isLimitTotal = false;

                if (!PredicateUtils.isNullList(winningLimitList)) {
                    //ID_SORT_TODAY_HOUR 코드 값 확인
                    String hourCode = StringTools.joinStringsNoSeparator(
                            String.valueOf(arEventEntity.getArEventId()), String.valueOf(mappingWinningEntity.getEventWinningSort()), DateUtils.getNowMMDD(), DateUtils.getNowHour()
                    );
                    String totalCode = StringTools.joinStringsNoSeparator(
                            String.valueOf(arEventEntity.getArEventId()), String.valueOf(mappingWinningEntity.getEventWinningSort())
                    );
                    //ID_SORT_TODAY_HOUR 코드로 값이 존재하는지 체크
                    Optional<EventLogWinningLimitMapperVO> hourLimitOptional = winningLimitList.stream()
                            .filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), hourCode))
                            .findAny();
                    //ID_SORT 코드로 값이 존재하는지 체크
                    Optional<EventLogWinningLimitMapperVO> totalLimitOptional = winningLimitList.stream()
                            .filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), totalCode))
                            .findAny();

                    //값이 존재하면 true
                    if (hourLimitOptional.isPresent()) {
                        log.info("winning limit 시간제한 걸림");
                        isLimitHour = true;
                    }
                    if (totalLimitOptional.isPresent()) {
                        log.info("winning limit 전체제한 걸림");
                        isLimitTotal = true;
                    }
                }

                /** 매핑되어있는 당첨정보 조건 시작 **/
                /** 시간설정 조건 시작 **/
                //시간설정일떄
                if (PredicateUtils.isEqualY(mappingWinningEntity.getWinningTimeType())) {
                    log.info("validateWinning : {} 당첨시간설정을 '시간설정' 으로 했을떄 시작");
                    //현재 시간이 시간설정안에 들어가있지 않을때
                    if ( !PredicateUtils.isInTwoSections(mappingWinningEntity.getStartWinningTime(), Integer.parseInt(DateUtils.getNowHour()), (mappingWinningEntity.getEndWinningTime() - 1)) ) {
                        log.info("validateWinning : {} 현재시간이 당첨시간설정에 들어가있지 않아서 꽝!");
                        //꽝 로직
                        return this.failWinningLogic(reqDto, arEventEntity);
                    } else {
                        //당첨 제한 테이블 > 시간제한값이 있을때 꽝처리
                        if (isLimitHour) {
                            log.info("validateWinning : {} 당첨 제한 테이블 > 시간제한값이 있을때 꽝처리");
                            return this.failWinningLogic(reqDto, arEventEntity);
                        } else {
                            //시간당첨 수량 체크
                            if (PredicateUtils.isNull(mappingWinningEntity.getHourWinningNumber())) {
                                mappingWinningEntity.setHourWinningNumber(0);
                            }

                            if (PredicateUtils.isGreaterThanZero(mappingWinningEntity.getHourWinningNumber())) {
                                int hourLogCnt = logService.getCountEventWinningLogByArEventIdAndEventWinningSortAndDayAndHourNotFail2(arEventEntity.getArEventId(), mappingWinningEntity.getEventWinningSort());
                                if (PredicateUtils.isGreaterThanEqualTo(hourLogCnt, mappingWinningEntity.getHourWinningNumber())) {
                                    log.info("eventWinningLogic : {} 시간당첨 수량 MAX ! ");

                                    try {
                                        logService.saveEventLogWinningLimit(arEventEntity.getArEventId(), mappingWinningEntity.getEventWinningSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), null, EventLogWinningLimitDefine.ID_SORT_TODAY_HOUR.name());
                                    } catch (DuplicateKeyException e) {
                                        log.error("saveEventLogWinningLimit error {} ", e.toString());
                                        return this.failWinningLogic(reqDto, arEventEntity);
                                    }
                                    return this.failWinningLogic(reqDto, arEventEntity);
                                }
                            }
                        }
                    }
                    log.info("validateWinning : {} 오브젝트와 당첨정보가 매핑되어있으면 당첨건수 체크중 최대당첨수만 체크!!");
                    /**
                     * 이슈 : 오브젝트와 당첨정보가 매핑되어있으면 당첨건수 체크중 최대당첨수만 체크
                     */
                    /** 최대당첨수 조건 **/
                    //0이면 당첨수 무제한, 0보다 크면 조건 시작
                    if (PredicateUtils.isGreaterThanZero(mappingWinningEntity.getTotalWinningNumber())) {
                        log.info("validateWinning : {} 당첨정보 최대당첨수가 0보다큰 제한할때!!");

                        if (isLimitTotal) {
                            log.info("validateWinning : {} 당첨 제한 테이블 > 전체제한 값이 있을때 꽝처리");
                            return this.failWinningLogic(reqDto, arEventEntity);
                        } else {
                            //로그에 있는 전체당첨 수량 가져오기
                            int totalWinningLogCount = logService.getCountEventWinningLogByArEventIdAndEventWinningSortNotFail(arEventEntity.getArEventId(), mappingWinningEntity.getEventWinningSort());
                            //로그 당첨수량이 세팅되어있는 전체당첨수량보다 많을때
                            if (PredicateUtils.isGreaterThanEqualTo(totalWinningLogCount, mappingWinningEntity.getTotalWinningNumber())) {
                                log.info("validateWinning : {} 최대당첨수가 다 소진 되었을때 꽝!");

                                try {
                                    logService.saveEventLogWinningLimit(arEventEntity.getArEventId(), mappingWinningEntity.getEventWinningSort(), null, null, null, EventLogWinningLimitDefine.ID_SORT.name());
                                } catch (DuplicateKeyException e) {
                                    log.error("saveEventLogWinningLimit error {} ", e.toString());
                                    return this.failWinningLogic(reqDto, arEventEntity);
                                }
                                //꽝 로직
                                return this.failWinningLogic(reqDto, arEventEntity);
                            }
                        }
                    }
                    log.info("validateWinning : {} 매핑 + 시간설정일떄 모든 기준과 당첨률이 통과되어 당첨!!");
                    /*** 당첨 로직 주입 **/
                    return this.successWinningLogic( reqDto, mappingWinningEntity, arEventEntity, winningLimitList, webEventBase );

                }
                //시간설정 아닐떄
                if (PredicateUtils.isEqualN(mappingWinningEntity.getWinningTimeType())) {
                    log.info("validateWinning : {} 매핑 + 시간설정이 아닐떄 조건 시작~");
                    // 시간설정이 아닐때
                    /** 최대당첨수 조건 **/
                    log.info("validateWinning : {} 최대당첨수 조건 시작~");
                    //0이면 당첨수 무제한, 0보다 크면 조건 시작
                    if (PredicateUtils.isGreaterThanZero(mappingWinningEntity.getTotalWinningNumber())) {
                        log.info("validateWinning : {} 최대당첨수량이 0보다 클떄 시작");

                        if (isLimitTotal) {
                            log.info("validateWinning : {} 당첨 제한 테이블 > 전체제한 값이 있을때 꽝처리");
                            return this.failWinningLogic(reqDto, arEventEntity);
                        } else {
                            //로그에 있는 전체당첨 수량 가져오기
                            int totalWinningLogCount = logService.getCountEventWinningLogByArEventIdAndEventWinningSortNotFail(arEventEntity.getArEventId(), mappingWinningEntity.getEventWinningSort());
                            //로그 당첨수량이 세팅되어있는 전체당첨수량보다 많을때
                            if (PredicateUtils.isGreaterThanEqualTo(totalWinningLogCount, mappingWinningEntity.getTotalWinningNumber())) {
                                log.info("validateWinning : {} 로그 당첨수량이 세팅되어있는 전체당첨수량보다 많을때 꽝!");
                                try {
                                    logService.saveEventLogWinningLimit(arEventEntity.getArEventId(), mappingWinningEntity.getEventWinningSort(), null, null, null, EventLogWinningLimitDefine.ID_SORT.name());
                                } catch (DuplicateKeyException e) {
                                    log.error("saveEventLogWinningLimit error {} ", e.toString());
                                    return this.failWinningLogic(reqDto, arEventEntity);
                                }
                                //꽝 로직
                                return this.failWinningLogic(reqDto, arEventEntity);
                            }
                        }
                    }

                    log.info("validateWinning : {} 매핑 + 시간설정아닐떄 모든 기준과 당첨률이 통과되어 당첨!!");
                    /*** 당첨 로직 주입 **/
                    return this.successWinningLogic( reqDto, mappingWinningEntity, arEventEntity, winningLimitList, webEventBase );
                }
            }   //오브젝트 매핑 로직 끝


            /** 오브젝트 번호와 매핑되어있지 않을때 **/
            if (!objectMappingNumberList.contains(objectNumber)) {
                //맵핑되지 않은 당첨목록 선언
                LinkedList<ArEventWinningEntity> notMappingWinningList = new LinkedList<>();
                for (ArEventWinningEntity winningEntity : winningLinkedList) {
                    if (PredicateUtils.isEqualsStr(String.valueOf(winningEntity.getArEventId()), String.valueOf(arEventEntity.getArEventId()))) {
                        if (PredicateUtils.isEqualsStr(winningEntity.getObjectMappingType(), StringDefine.N.name())) {
                            if (!PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.꽝.code())) {
                                notMappingWinningList.add(winningEntity);
                            }
                        }
                    }
                }
                /**
                 * 오브젝트 번호와 매핑되어있는 넘버중에 매핑이 없을 떄 순차로직 매서드 콜
                 */
                //오브젝트 매핑이 되어있지 않은 당첨정보만 넘겨준다
                return this.sequentiallyWinningProcessLogic( reqDto, notMappingWinningList, arEventEntity, winningLimitList );
            }
        }

        /**
         * 미션클리어형, 이미지스캐닝형, 서베이고_기본형, 대화형, AR포토_기본형 일때 당첨로직 - 오브젝트 맵핑이 없는 타입
         */
        if (EventLogicalTypeDefine.미션클리어형.value().equals(arEventEntity.getEventLogicalType())
                || EventLogicalTypeDefine.이미지스캐닝형.value().equals(arEventEntity.getEventLogicalType())
                || EventLogicalTypeDefine.서베이고_기본형.value().equals(arEventEntity.getEventLogicalType())
                || EventLogicalTypeDefine.대화형.value().equals(arEventEntity.getEventLogicalType())
                || EventLogicalTypeDefine.포토_기본형.value().equals(arEventEntity.getEventLogicalType())) {
            //맵핑되지 않은 당첨목록 선언
            LinkedList<ArEventWinningEntity> notMappingWinningList = new LinkedList<>();
            for (ArEventWinningEntity winningEntity : winningLinkedList) {
                if (PredicateUtils.isEqualNumber(winningEntity.getArEventId(), arEventEntity.getArEventId())) {
//                if (PredicateUtils.isEqualsStr(String.valueOf(winningEntity.getArEventId()), String.valueOf(arEventEntity.getArEventId()))) {
                    if (PredicateUtils.isEqualsStr(winningEntity.getObjectMappingType(), StringDefine.N.name())) {
                        if (!PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.꽝.code())) {
                            notMappingWinningList.add(winningEntity);
                        }
                    }
                }
            }

            /**
             * 오브젝트 번호와 매핑되어있는 넘버중에 매핑이 없을 떄
             */
            //오브젝트 매핑이 되어있지 않은 당첨정보만 넘겨준다
            return this.sequentiallyWinningProcessLogic( reqDto, notMappingWinningList, arEventEntity, winningLimitList );
        }
        return null;
    }

    /**
     * 오브젝트 맵핑이 아닌 순차적으로 당첨로직
     */
    @Transactional
    public ApiResultObjectDto sequentiallyWinningProcessLogic(EventWinningReqDto reqDto, LinkedList<ArEventWinningEntity> winningLinkedList, ArEventByIdAtWinningProcessMapperVO arEvent, List<EventLogWinningLimitMapperVO>winningLimitList) {
        log.info("validateWinning : {} 매핑되지 않는 오브젝트 >> 당첨정보 순차적으로 조건 시작 ~~");
        /** 오브젝트 번호와 매핑되어있는 넘버중에 매핑이 없을 떄 **/
        /** 순차적으로 당첨정보를 바라보고 for 시작 **/

        if ( PredicateUtils.isEqualZero(winningLinkedList.size()) || PredicateUtils.isNull(winningLinkedList)) {
            log.info("sequentiallyWinningProcessLogic : {} 순차로직에서 당첨정보가 하나도 없으면 꽝!");
            //꽝 로직
            return failWinningLogic(reqDto, arEvent);
        }

        int i = 1;
        for (ArEventWinningEntity winningEntity : winningLinkedList) {
            log.info("validateWinning : {} 당첨 인덱스 :: " + winningEntity.getArEventWinningId());

            int totalWinningLogCount = 0, dayWinningLogCount = 0, hourWinningLogCount = 0;

            boolean isFullTotalCount = false, isFullDayCount = false, isFullHourCount = false;

            boolean isLimitHour = false, isLimitDay = false, isLimitTotal = false;

            boolean isAttendCodeLimitDay = false, isAttendCodeLimitTotal = false;

            if (!PredicateUtils.isNullList(winningLimitList)) {
                //ID_SORT_TODAY_HOUR 코드 값 확인
                String hourCode = StringTools.joinStringsNoSeparator(
                        String.valueOf(arEvent.getArEventId()), String.valueOf(winningEntity.getEventWinningSort()), DateUtils.getNowMMDD(), DateUtils.getNowHour()
                );
                String dayCode = StringTools.joinStringsNoSeparator(
                        String.valueOf(arEvent.getArEventId()), String.valueOf(winningEntity.getEventWinningSort()), DateUtils.getNowMMDD()
                );
                String totalCode = StringTools.joinStringsNoSeparator(
                        String.valueOf(arEvent.getArEventId()), String.valueOf(winningEntity.getEventWinningSort())
                );
                //ID_SORT_TODAY_HOUR 코드로 값이 존재하는지 체크
                Optional<EventLogWinningLimitMapperVO> hourLimitOptional = winningLimitList.stream()
                                                                                            .filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), hourCode))
                                                                                            .findAny();

                //ID_SORT_TODAY 코드로 값이 존재하는지 체크
                Optional<EventLogWinningLimitMapperVO> dayLimitOptional = winningLimitList.stream()
                                                                                            .filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), dayCode))
                                                                                            .findAny();

                //ID_SORT 코드로 값이 존재하는지 체크
                Optional<EventLogWinningLimitMapperVO> totalLimitOptional = winningLimitList.stream()
                                                                                            .filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), totalCode))
                                                                                            .findAny();
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
                log.info("eventWinningLogic : {} 제한데이터 > 전체, 일, 시간 당첨수량이 전부 MAX 일떄 ");
                //마지막 오브젝트인데 수량이 전부 소진됬을때 꽝처리
                if (i == winningLinkedList.size()) {
                    log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                    //꽝 로직
                    return failWinningLogic(reqDto, arEvent);
                } else if (i < winningLinkedList.size()) {
                    log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                    i++;
                    continue;
                }
            }

            //전부 소진되면 다음 오브젝트로 넘김
            if (isFullTotalCount && isFullDayCount && isFullHourCount) {
                log.info("eventWinningLogic : {} 전체, 일, 시간 당첨수량이 전부 MAX 일떄 ");
                //마지막 오브젝트인데 수량이 전부 소진됬을때 꽝처리
                if (i == winningLinkedList.size()) {
                    log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                    //꽝 로직
                    return failWinningLogic(reqDto, arEvent);
                } else if (i < winningLinkedList.size()) {
                    log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                    i++;
                    continue;
                }
            }

            if (!isLimitDay && !isLimitTotal) {
                //시간당첨 수량 체크
                if (PredicateUtils.isGreaterThanZero(winningEntity.getHourWinningNumber())) {
                    if (!isFullHourCount) {
                        //로그에 있는 현재 시간당당첨 수량 가져오기
                        hourWinningLogCount = logService.getCountEventWinningLogByArEventIdAndEventWinningSortAndDayAndHourNotFail2(arEvent.getArEventId(), winningEntity.getEventWinningSort());
                        log.info("validateWinning : {} 로그 시간당당첨 수량 :: " + hourWinningLogCount);
                        if (PredicateUtils.isGreaterThanEqualTo(hourWinningLogCount, winningEntity.getHourWinningNumber())) {
                            log.info("eventWinningLogic : {} 시간당첨 수량 MAX ! ");
                            isFullHourCount = true;
                            if (!isLimitHour) {
                                try {
                                    logService.saveEventLogWinningLimit(arEvent.getArEventId(), winningEntity.getEventWinningSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), null, EventLogWinningLimitDefine.ID_SORT_TODAY_HOUR.name());
                                } catch (DuplicateKeyException e) {
                                    log.error("saveEventLogWinningLimit error {} ", e.toString());

                                    if (i == winningLinkedList.size()) {
                                        log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                        //꽝 로직
                                        return failWinningLogic(reqDto, arEvent);
                                    } else if (i < winningLinkedList.size()) {
                                        log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                        i++;
                                        continue;
                                    }
                                } finally {
                                    isLimitHour = true;
                                }
                            }
                        }
                    }
                }
            }

            if (!isLimitTotal) {
                //일당첨수량 체크
                if (PredicateUtils.isGreaterThanZero(winningEntity.getDayWinningNumber())) {
                    if (!isFullDayCount) {
                        //로그에 있는 일일 기준 당첨 수량 가져오기
                        dayWinningLogCount = logService.getCountEventWinningLogByArEventIdAndEventWinningSortAndTodayAndNotFail2(arEvent.getArEventId(), winningEntity.getEventWinningSort());
                        log.info("validateWinning : {} 로그 일일 기준 당첨 수량 :: " + dayWinningLogCount);

                        if (PredicateUtils.isGreaterThanEqualTo(dayWinningLogCount, winningEntity.getDayWinningNumber())) {
                            log.info("eventWinningLogic : {} 일당첨수량 MAX ! ");
                            isFullDayCount = true;
                            if (!isLimitDay) {
                                try {
                                    //제한 테이블 ID_SORT_TODAY 값 저장
                                    logService.saveEventLogWinningLimit(arEvent.getArEventId(), winningEntity.getEventWinningSort(), DateUtils.getNowMMDD(), null, null, EventLogWinningLimitDefine.ID_SORT_TODAY.name());
                                } catch (DuplicateKeyException e) {
                                    log.error("saveEventLogWinningLimit error {} ", e.toString());
                                    if (i == winningLinkedList.size()) {
                                        log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                        //꽝 로직
                                        return failWinningLogic(reqDto, arEvent);
                                    } else if (i < winningLinkedList.size()) {
                                        log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                        i++;
                                        continue;
                                    }
                                } finally {
                                    isLimitDay = true;
                                }
                            }
                        }
                    }
                }
            }

            //전체당첨수량 수량 체크
            if (PredicateUtils.isGreaterThanZero(winningEntity.getTotalWinningNumber())) {
                if (!isFullTotalCount) {
                    //로그에 있는 전체당첨 수량 가져오기
                    totalWinningLogCount = logService.getCountEventWinningLogByArEventIdAndEventWinningSortNotFail(arEvent.getArEventId(), winningEntity.getEventWinningSort());
                    log.info("validateWinning : {} 로그 전체당첨 수량 :: " + totalWinningLogCount);

                    if (PredicateUtils.isGreaterThanEqualTo(totalWinningLogCount, winningEntity.getTotalWinningNumber())) {
                        log.info("eventWinningLogic : {} 전체수량 MAX ! ");
                        isFullTotalCount = true;
                        if (!isLimitTotal) {
                            try {
                                //제한 테이블 ID_SORT 값 저장
                                logService.saveEventLogWinningLimit(arEvent.getArEventId(), winningEntity.getEventWinningSort(), null, null, null, EventLogWinningLimitDefine.ID_SORT.name());
                            } catch (Exception e) {
                                log.error("saveEventLogWinningLimit error {} ", e.toString());
                                if (i == winningLinkedList.size()) {
                                    log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                    //꽝 로직
                                    return failWinningLogic(reqDto, arEvent);
                                } else if (i < winningLinkedList.size()) {
                                    log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                    i++;
                                    continue;
                                }
                            } finally {
                                isLimitTotal = true;
                            }
                        }
                    }
                }
            }

            log.info("eventWinningLogic : {} 순차적으로 조건 시간설정 조건 시작~");
            /** 시간설정 조건 시작 **/

            if (PredicateUtils.isNull(winningEntity.getWinningTimeType())) {
                winningEntity.setWinningTimeType(StringDefine.N.name());
            }

            //시간설정일떄
            if (PredicateUtils.isEqualY(winningEntity.getWinningTimeType())) {
                log.info("eventWinningLogic : {} 순차적으로 조건 시간설정이 Y 일떄 ");
                //현재 시간이 시간설정안에 들어가있지 않을때
                if ( !PredicateUtils.isInTwoSections(winningEntity.getStartWinningTime(), Integer.parseInt(DateUtils.getNowHour()), (winningEntity.getEndWinningTime() - 1)) ) {
                    log.info("eventWinningLogic : {} 현재시각이 조건시각안에 안들어 갔을떄 꽝! :: 현재 시각 >> " + DateUtils.getNowHour() + " :: 기준 시각 >> " + winningEntity.getStartWinningTime() + " ~ " + winningEntity.getEndWinningTime());
                    if (i == winningLinkedList.size()) {
                        log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                        //꽝 로직
                        return failWinningLogic(reqDto, arEvent);
                    } else if (i < winningLinkedList.size()) {
                        log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                        i++;
                        continue;
                    }
                }

                log.info("eventWinningLogic : {} 순차적으로 조건 최대당첨수 조건 시작 ~");
                /** 최대당첨수 조건 **/
                if (!isFullTotalCount) {
                    log.info("eventWinningLogic : {} 순차적으로 조건 최대당첨수가 최대가 아닐떄");
                    //0이면 당첨수 무제한, 0보다 크면 조건 시작
                    if (PredicateUtils.isGreaterThanZero(winningEntity.getTotalWinningNumber())) {
                        log.info("eventWinningLogic : {} 순차적으로 조건 최대당첨수가 0보다 크면 개수 체크");
                        //로그 당첨수량이 세팅되어있는 전체당첨수량보다 많을때
                        if (PredicateUtils.isGreaterThanEqualTo(totalWinningLogCount, winningEntity.getTotalWinningNumber())) {

                            if (!isLimitTotal) {
                                isLimitTotal = true;
                            }

                            log.info("eventWinningLogic : {} 순차적으로 조건 로그 당첨수량이 세팅되어있는 전체당첨수량보다 많을때 꽝!");
                            if (i == winningLinkedList.size()) {
                                log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                //꽝 로직
                                return failWinningLogic(reqDto, arEvent);
                            } else if (i < winningLinkedList.size()) {
                                log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                i++;
                                continue;
                            }

                        } else {

                            log.info("eventWinningLogic : {} 순차적으로 조건 일 당첨수 조건 시작 ~");
                            /** 일 당첨수 조건 **/
                            if (!isFullDayCount) {
                                log.info("eventWinningLogic : {} 순차적으로 조건 일 당첨수가 최대가 아닐떄");
                                //0이면 당첨수 무제한, 0보다 크면 조건 시작
                                if (PredicateUtils.isGreaterThanZero(winningEntity.getDayWinningNumber())) {
                                    log.info("eventWinningLogic : {} 순차적으로 조건 일 당첨수가 0보다 크면 개수 체크");
                                    //로그에 있는 일일 기준 당첨 수량 가져오기
                                    if (PredicateUtils.isGreaterThanEqualTo(dayWinningLogCount, winningEntity.getDayWinningNumber())) {

                                        if (!isLimitDay) {
                                            isLimitDay = true;
                                            logService.saveEventLogWinningLimit(arEvent.getArEventId(), winningEntity.getEventWinningSort(), DateUtils.getNowMMDD(), null, null, EventLogWinningLimitDefine.ID_SORT_TODAY.name());
                                        }

                                        log.info("eventWinningLogic : {} 순차적으로 조건 로그에 있는 일일 기준 당첨 수량보다 많을때 꽝!");
                                        if (i == winningLinkedList.size()) {
                                            log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                            //꽝 로직
                                            return failWinningLogic(reqDto, arEvent);
                                        } else if (i < winningLinkedList.size()) {
                                            log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                            i++;
                                            continue;
                                        }

                                    } else {

                                        log.info("eventWinningLogic : {} 순차적으로 조건 시단당 당첨수 조건 시작 ~");
                                        /** 시간당 당첨수량 조건 **/
                                        if (!isFullHourCount) {
                                            log.info("eventWinningLogic : {} 순차적으로 조건 시간당 당첨수가 최대가 아닐떄");
                                            //0이면 당첨수 무제한, 0보다 크면 조건 시작
                                            if (PredicateUtils.isGreaterThanZero(winningEntity.getHourWinningNumber())) {
                                                log.info("eventWinningLogic : {} 순차적으로 조건 시간당 당첨수가 0보다 크면 개수 체크");
                                                //로그에 있는 현재 시간당당첨 수량 가져오기
                                                if (PredicateUtils.isGreaterThanEqualTo(hourWinningLogCount, winningEntity.getHourWinningNumber())) {

                                                    if (!isLimitHour) {
                                                        isLimitHour = true;
                                                    }

                                                    log.info("eventWinningLogic : {} 순차적으로 조건 로그에 있는 시간당 기준 당첨 수량보다 많을때 꽝!");
                                                    if (i == winningLinkedList.size()) {
                                                        log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                                        //꽝 로직
                                                        return failWinningLogic(reqDto, arEvent);
                                                    } else if (i < winningLinkedList.size()) {
                                                        log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                                        i++;
                                                        continue;
                                                    }
                                                }
                                            }
                                        } else {
                                            if (!isLimitHour) {
                                                isLimitHour = true;
                                                //제한 테이블 ID_SORT_TODAY_HOUR 값 저장
                                                logService.saveEventLogWinningLimit(arEvent.getArEventId(), winningEntity.getEventWinningSort(), DateUtils.getNowMMDD(), DateUtils.getNowHour(), null, EventLogWinningLimitDefine.ID_SORT_TODAY_HOUR.name());
                                            }

                                            log.info("eventWinningLogic : {} 시간당 당첨수량 MAX!");
                                            if (i == winningLinkedList.size()) {
                                                log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                                //꽝 로직
                                                return failWinningLogic(reqDto, arEvent);
                                            } else if (i < winningLinkedList.size()) {
                                                log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                                i++;
                                                continue;
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (!isLimitDay) {
                                    isLimitDay = true;
                                    logService.saveEventLogWinningLimit(arEvent.getArEventId(), winningEntity.getEventWinningSort(), DateUtils.getNowMMDD(), null, null, EventLogWinningLimitDefine.ID_SORT_TODAY.name());
                                }
                                log.info("eventWinningLogic : {} 일 당첨수량 MAX!");
                                if (i == winningLinkedList.size()) {
                                    log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                    //꽝 로직
                                    return failWinningLogic(reqDto, arEvent);
                                } else if (i < winningLinkedList.size()) {
                                    log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                    i++;
                                    continue;
                                }
                            }
                        }
                    }
                } else {
                    if (!isLimitTotal) {
                        isLimitTotal = true;
                    }

                    log.info("eventWinningLogic : {} 최대 당첨수량 MAX!");
                    if (i == winningLinkedList.size()) {
                        log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                        //꽝 로직
                        return failWinningLogic(reqDto, arEvent);
                    } else if (i < winningLinkedList.size()) {
                        log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                        i++;
                        continue;
                    }
                }

                log.info("eventWinningLogic : {} 순차적으로 조건 참여번호당, 전화번호당 당첨제한 조건 시작 ~");
                /** 참여번호당 당첨제한 조건 **/
                //기한제한일떄
                if (PredicateUtils.isNotNull(winningEntity.getAttendCodeWinningType())) {

                    if (!PredicateUtils.isNullList(winningLimitList)) {

                        //[DTWS-356]
                        Optional<EventLogWinningLimitMapperVO> dayLimitOptional = Optional.empty(),
                                                               totalLimitOptional = Optional.empty();

                        log.info("참여제한 조건이 > 참여번호일때");
                        if (arEvent.getArAttendConditionCodeYn()) {

                            //ID_WINNINGID_CODE_TODAY 코드 값 확인
                            String dayCode = StringTools.joinStringsNoSeparator(
                                    String.valueOf(arEvent.getArEventId()), String.valueOf(winningEntity.getArEventWinningId()), reqDto.getAttendCode(), DateUtils.getNowMMDD()
                            );
                            //ID_WINNINGID_CODE 코드 값 확인
                            String totalCode = StringTools.joinStringsNoSeparator(
                                    String.valueOf(arEvent.getArEventId()), String.valueOf(winningEntity.getArEventWinningId()), reqDto.getAttendCode()
                            );

                            //ID_WINNINGID_CODE_TODAY 코드로 값이 존재하는지 체크
                            dayLimitOptional = winningLimitList.stream()
                                                                    .filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), dayCode))
                                                                    .findAny();

                            //ID_WINNINGID_CODE 코드로 값이 존재하는지 체크
                            totalLimitOptional = winningLimitList.stream()
                                                                    .filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), totalCode))
                                                                    .findAny();
                        }

                        log.info("참여제한 조건이 > 전화번호일때");
                        if (arEvent.getAttendConditionMdnYn()) {

                            //ID_MDN_TODAY 코드 값 확인
                            String todayAttendCode = StringTools.joinStringsNoSeparator(
                                    String.valueOf(arEvent.getArEventId()), reqDto.getPhoneNumber(), DateUtils.getNowMMDD()
                            );
                            //ID_MDN 코드 값 확인
                            String totalAttendCode = StringTools.joinStringsNoSeparator(
                                    String.valueOf(arEvent.getArEventId()), reqDto.getPhoneNumber()
                            );

                            //ID_MDN_TODAY 코드로 값이 존재하는지 체크
                            dayLimitOptional = winningLimitList.stream()
                                                                    .filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), todayAttendCode))
                                                                    .findAny();

                            //ID_MDN 코드로 값이 존재하는지 체크
                            totalLimitOptional = winningLimitList.stream()
                                                                    .filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), totalAttendCode))
                                                                    .findAny();
                        }

                        //값이 존재하면 true
                        if (dayLimitOptional.isPresent()) {
                            log.info("winning limit 일제한 걸림");
                            isAttendCodeLimitDay = true;
                        }
                        if (totalLimitOptional.isPresent()) {
                            log.info("winning limit 전체제한 걸림");
                            isAttendCodeLimitTotal = true;
                        }
                    }

                    if (PredicateUtils.isEqualY(winningEntity.getAttendCodeWinningType())) {
                        log.info("sequentiallyWinningProcessLogic : {} 순차적으로 조건 참여번호당 당첨제한 조건이 기한제한 Y 일떄 시작 ~");
                        //전체기한내일떄
                        if (PredicateUtils.isEqualZero(winningEntity.getAttendCodeLimitType())) {
                            log.info("sequentiallyWinningProcessLogic : {} 순차적으로 조건 참여번호당 당첨제한 조건이 기한제한이고 전체제한 일떄");

                            if (isAttendCodeLimitTotal) {
                                log.info("sequentiallyWinningProcessLogic : {} 당첨 제한 테이블 > 전체제한 값이 있을때");
                                if (i == winningLinkedList.size()) {
                                    log.info("sequentiallyWinningProcessLogic : {} 마지막 오브젝트여서 꽝!");
                                    //꽝 로직
                                    return failWinningLogic(reqDto, arEvent);
                                } else if (i < winningLinkedList.size()) {
                                    log.info("sequentiallyWinningProcessLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                    i++;
                                    continue;
                                }
                            } else {
                                int allTimeWinningCount = 0;

                                //참여코드, 전화번호 전체 당첨건수 로그 가져오기
                                if (arEvent.getArAttendConditionCodeYn()) {
                                    //참여번호
                                    allTimeWinningCount = logService.getCountEventWinningLogByArEventIdAndArEventWinningIdAndAttendCodeNotFail(arEvent.getArEventId(), winningEntity.getArEventWinningId(), reqDto.getAttendCode(), null);
                                } else if (arEvent.getAttendConditionMdnYn()) {
                                    //전화번호
                                    allTimeWinningCount = logService.getCountEventWinningLogByArEventIdAndArEventWinningIdAndAttendCodeNotFail(arEvent.getArEventId(), winningEntity.getArEventWinningId(), null, reqDto.getPhoneNumber());
                                }

                                if (PredicateUtils.isGreaterThanEqualTo(allTimeWinningCount, winningEntity.getAttendCodeWinningCount())) {
                                    log.info("eventWinningLogic : {} 순차적으로 조건 참여번호당 당첨제한 조건이 기한제한이고 전체제한 일떄 로그개수가 많아서 꽝!");
                                    if (i == winningLinkedList.size()) {
                                        log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                        log.info("winningEntity : {} " + winningEntity.toString());
                                        try {
                                            //참여코드, 전화번호 제한 로그 테이블 값 저장하기
                                            if (arEvent.getArAttendConditionCodeYn()) {
                                                //참여번호
                                                logService.saveEventLogWinningLimit(arEvent.getArEventId(), winningEntity.getArEventWinningId(), null, null, reqDto.getAttendCode(), EventLogWinningLimitDefine.ID_WINNINGID_CODE.name());
                                            } else if (arEvent.getAttendConditionMdnYn()) {
                                                //전화번호
                                                logService.saveEventLogWinningLimit(arEvent.getArEventId(), winningEntity.getArEventWinningId(), null, null, reqDto.getPhoneNumber(), EventLogWinningLimitDefine.ID_WINNINGID_MDN.name());
                                            }
                                        } catch (DuplicateKeyException e) {
                                            log.error("saveEventLogWinningLimit error {} ", e.getMessage());
                                            if (i == winningLinkedList.size()) {
                                                log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                                //꽝 로직
                                                return failWinningLogic(reqDto, arEvent);
                                            } else if (i < winningLinkedList.size()) {
                                                log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                                i++;
                                                continue;
                                            }
                                        }
                                        //꽝 로직
                                        return failWinningLogic(reqDto, arEvent);
                                    } else if (i < winningLinkedList.size()) {
                                        // log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                        i++;
                                        continue;
                                    }
                                }
                            }
                        //1일일떄
                        } else if (winningEntity.getAttendCodeLimitType() == 1) {
                            log.info("eventWinningLogic : {} 순차적으로 조건 참여번호당 당첨제한 조건이 기한제한이고 1일 일떄");

                            if (isAttendCodeLimitDay) {
                                log.info("sequentiallyWinningProcessLogic : {} 당첨 제한 테이블 > 1일제한 값이 있을때");
                                if (i == winningLinkedList.size()) {
                                    log.info("sequentiallyWinningProcessLogic : {} 마지막 오브젝트여서 꽝!");
                                    //꽝 로직
                                    return failWinningLogic(reqDto, arEvent);
                                } else if (i < winningLinkedList.size()) {
                                    log.info("sequentiallyWinningProcessLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                    i++;
                                    continue;
                                }
                            } else {
                                int todayWinningCount = 0;
                                //참여코드 1일 당첨건수 로그 가져오기
                                if (arEvent.getArAttendConditionCodeYn()) {
                                    todayWinningCount = logService.getCountEventWinningLogByArEventIdAndArEventWinningIdAndAttendCodeAndTodayNotFail2(arEvent.getArEventId(), winningEntity.getArEventWinningId(), reqDto.getAttendCode(), null);
                                } else if (arEvent.getAttendConditionMdnYn()) {
                                    todayWinningCount = logService.getCountEventWinningLogByArEventIdAndArEventWinningIdAndAttendCodeAndTodayNotFail2(arEvent.getArEventId(), winningEntity.getArEventWinningId(), null, reqDto.getPhoneNumber());
                                }

                                if (PredicateUtils.isGreaterThanEqualTo(todayWinningCount, winningEntity.getAttendCodeWinningCount())) {
                                    log.info("eventWinningLogic : {} 순차적으로 조건 참여번호당 당첨제한 조건이 기한제한이고 1일일떄 로그개수가 많아서 꽝!");
                                    if (i == winningLinkedList.size()) {
                                        log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");

                                        try {
                                            if (arEvent.getArAttendConditionCodeYn()) {
                                                logService.saveEventLogWinningLimit(arEvent.getArEventId(), winningEntity.getArEventWinningId(), DateUtils.getNowMMDD(), null, reqDto.getAttendCode(), EventLogWinningLimitDefine.ID_WINNINGID_CODE_TODAY.name());
                                            } else if (arEvent.getAttendConditionMdnYn()) {
                                                logService.saveEventLogWinningLimit(arEvent.getArEventId(), winningEntity.getArEventWinningId(), DateUtils.getNowMMDD(), null, reqDto.getPhoneNumber(), EventLogWinningLimitDefine.ID_WINNINGID_MDN_TODAY.name());
                                            }
                                        } catch (DuplicateKeyException e) {
                                            log.error("saveEventLogWinningLimit error {} ", e.toString());
                                            if (i == winningLinkedList.size()) {
                                                log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                                //꽝 로직
                                                return failWinningLogic(reqDto, arEvent);
                                            } else if (i < winningLinkedList.size()) {
                                                log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                                i++;
                                                continue;
                                            }
                                        }
                                        //꽝 로직
                                        return failWinningLogic(reqDto, arEvent);
                                    } else if (i < winningLinkedList.size()) {
                                        log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                        i++;
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                }

                log.info("eventWinningLogic : {} 기본 조건을 통과되서 당첨률 시작~");

                /** 당첨률 계산**/
                log.info("eventWinningLogic : {} 당첨률 :: < " + winningEntity.getWinningPercent() + " >");
                boolean isWin = EventUtils.percent(Float.valueOf(winningEntity.getWinningPercent()));

                if (!isWin) {
                    log.info("eventWinningLogic : {} 셋팅된 당첨률안에 못들거가서 꽝!");
                    //꽝 로직
                    //continue;
                    if (i == winningLinkedList.size()) {
                        log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                        //꽝 로직
                        return failWinningLogic(reqDto, arEvent);
                    } else if (i < winningLinkedList.size()) {
                        log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                        i++;
                        continue;
                    }
                }
                if (isWin) {
                    log.info("eventWinningLogic : {} 매핑 + 시간설정아닐떄 모든 기준과 당첨률이 통과되어 당첨!!");
                    /*** 당첨 로직 주입 **/
                    return this.successWinningLogic( reqDto, winningEntity, arEvent, winningLimitList, null );
                }

            }
            // end if PredicateUtils.isEqualY(winningEntity.getWinningTimeType())

            if (PredicateUtils.isEqualN(winningEntity.getWinningTimeType())) {

                log.info("eventWinningLogic : {} 순차적으로 조건 시간설정이 아닐때 조건 시작 ~");
                // 시간설정이 아닐때
                /** 최대당첨수 조건 **/
                if (!isFullTotalCount) {
                    log.info("eventWinningLogic : {} 순차적으로 조건 최대당첨수 조건 시작 ~");
                    //0이면 당첨수 무제한, 0보다 크면 조건 시작
                    if (PredicateUtils.isGreaterThanZero(winningEntity.getTotalWinningNumber())) {
                        log.info("eventWinningLogic : {} 순차적으로 조건 최대당첨수 셋팅 값이 0보다 크기때문에 조건 시작 ~");
                        //로그에 있는 전체당첨 수량 가져오기
                        // 로그 당첨수량이 세팅되어있는 전체당첨수량보다 많을때
                        if (PredicateUtils.isGreaterThanEqualTo(totalWinningLogCount, winningEntity.getTotalWinningNumber())) {

                            log.info("eventWinningLogic : {} 순차적으로 조건 세팅되어있는 전체당첨수량이 로그 당첨수량보다 많을때 꽝");
                            if (i == winningLinkedList.size()) {
                                log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                //꽝 로직
                                return failWinningLogic(reqDto, arEvent);
                            } else if (i < winningLinkedList.size()) {
                                log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                i++;
                                continue;
                            }

                        } else {

                            /** 일 당첨수 조건 **/
                            if (!isFullDayCount) {
                                log.info("eventWinningLogic : {} 순차적으로 조건 일 당첨수 조건 시작 ~");
                                //0이면 당첨수 무제한, 0보다 크면 조건 시작
                                if (PredicateUtils.isGreaterThanZero(winningEntity.getDayWinningNumber())) {
                                    log.info("eventWinningLogic : {} 순차적으로 조건 일 당첨수 셋팅 값이 0보다 크기때문에 조건 시작 ~");
                                    //로그에 있는 일일 기준 당첨 수량 가져오기
                                    if (PredicateUtils.isGreaterThanEqualTo(dayWinningLogCount, winningEntity.getDayWinningNumber())) {
                                        log.info("eventWinningLogic : {} 순차적으로 조건 세팅되어있는 일 당첨수량이 로그 당첨수량보다 많을때 꽝");
                                        if (i == winningLinkedList.size()) {
                                            log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                            //꽝 로직
                                            return failWinningLogic(reqDto, arEvent);
                                        } else if (i < winningLinkedList.size()) {
                                            log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                            i++;
                                            continue;
                                        }
                                    } else {
                                        /** 시간당 당첨수량 조건 **/
                                        if (!isFullHourCount) {
                                            log.info("eventWinningLogic : {} 순차적으로 조건 시간당 당첨수 조건 시작 ~");
                                            //0이면 당첨수 무제한, 0보다 크면 조건 시작
                                            if (PredicateUtils.isGreaterThanZero(winningEntity.getHourWinningNumber())) {
                                                log.info("eventWinningLogic : {} 순차적으로 조건 시간당 당첨수 셋팅 값이 0보다 크기때문에 조건 시작 ~");
                                                //로그에 있는 현재 시간당당첨 수량 가져오기
                                                if (PredicateUtils.isGreaterThanEqualTo(hourWinningLogCount, winningEntity.getHourWinningNumber())) {

                                                    if (!isLimitHour) {
                                                        isLimitHour = true;
                                                    }

                                                    log.info("eventWinningLogic : {} 순차적으로 조건 세팅되어있는 시간당 당첨수량이 로그 당첨수량보다 많을때 꽝");
                                                    if (i == winningLinkedList.size()) {
                                                        log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                                        //꽝 로직
                                                        return failWinningLogic(reqDto, arEvent);
                                                    } else if (i < winningLinkedList.size()) {
                                                        log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                                        i++;
                                                        continue;
                                                    }
                                                }
                                            }
                                        } else {
//                                            if (!isLimitHour) {
//                                                isLimitHour = true;
//                                            }

                                            log.info("eventWinningLogic : {} 시간당 당첨수량 MAX!");
                                            if (i == winningLinkedList.size()) {
                                                log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                                //꽝 로직
                                                return failWinningLogic(reqDto, arEvent);
                                            } else if (i < winningLinkedList.size()) {
                                                log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                                i++;
                                                continue;
                                            }
                                        }
                                    }
                                }
                            } else {
                                log.info("eventWinningLogic : {} 일 당첨수량 MAX!");
                                if (i == winningLinkedList.size()) {
                                    log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                    //꽝 로직
                                    return failWinningLogic(reqDto, arEvent);
                                    //break;
                                } else if (i < winningLinkedList.size()) {
                                    log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                    i++;
                                    continue;
                                }
                            }
                        }
                    }
                } else {
                    log.info("eventWinningLogic : {} 최대 당첨수량 MAX!");
                    if (i == winningLinkedList.size()) {
                        log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                        //꽝 로직
                        return failWinningLogic(reqDto, arEvent);
                    } else if (i < winningLinkedList.size()) {
                        log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                        i++;
                        continue;
                    }
                }

                // log.info("eventWinningLogic : {} 순차적으로 조건 시간제한 아닐때 참여번호당 당첨제한 조건 시작 ~");
                /** 참여번호당 당첨제한 조건 **/
                //기한제한일떄
                if (PredicateUtils.isNotNull(winningEntity.getAttendCodeWinningType())) {
                    if (PredicateUtils.isEqualY(winningEntity.getAttendCodeWinningType())) {
                        log.info("eventWinningLogic : {} 순차적으로 조건 시간제한 아닐때 기한제한일때 조건 시작 ~");
                        //전체기한내일떄
                        if (PredicateUtils.isEqualZero(winningEntity.getAttendCodeLimitType())) {
                            log.info("eventWinningLogic : {} 순차적으로 조건 시간제한 아닐때 기한제한이고 전체기한내 일때 조건 시작 ~");

                            if (isAttendCodeLimitTotal) {
                                log.info("sequentiallyWinningProcessLogic : {} 당첨 제한 테이블 > 전체제한 값이 있을때");
                                if (i == winningLinkedList.size()) {
                                    log.info("sequentiallyWinningProcessLogic : {} 마지막 오브젝트여서 꽝!");
                                    //꽝 로직
                                    return failWinningLogic(reqDto, arEvent);
                                } else if (i < winningLinkedList.size()) {
                                    log.info("sequentiallyWinningProcessLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                    i++;
                                    continue;
                                }
                            } else {
                                //참여코드 전체 당첨건수 로그 가져오기
                                int allTimeWinningCount = 0;
                                //참여번호일때
                                if (arEvent.getArAttendConditionCodeYn()) {
                                    log.info("참여번호일때~");
                                    allTimeWinningCount = logService.getCountEventWinningLogByArEventIdAndArEventWinningIdAndAttendCodeNotFail(arEvent.getArEventId(), winningEntity.getArEventWinningId(), reqDto.getAttendCode(), null);
                                } else if (arEvent.getAttendConditionMdnYn()) {
                                    log.info("전화번호일때~");
                                    allTimeWinningCount = logService.getCountEventWinningLogByArEventIdAndArEventWinningIdAndAttendCodeNotFail(arEvent.getArEventId(), winningEntity.getArEventWinningId(), null, reqDto.getPhoneNumber());
                                }

                                if (PredicateUtils.isGreaterThanEqualTo(allTimeWinningCount, winningEntity.getAttendCodeWinningCount())) {
                                    log.info("eventWinningLogic : {} 순차적으로 조건 시간제한 아닐때 기한제한이고 전체기한내 일때 꽝!");
                                    if (i == winningLinkedList.size()) {
                                        log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                        log.info("winningEntity :: " + winningEntity.toString());
                                        try {
                                            if (arEvent.getArAttendConditionCodeYn()) {
                                                log.info("참여번호일때~");
                                                logService.saveEventLogWinningLimit(arEvent.getArEventId(), winningEntity.getArEventWinningId(), null, null, reqDto.getAttendCode(), EventLogWinningLimitDefine.ID_WINNINGID_CODE.name());
                                            } else if (arEvent.getAttendConditionMdnYn()) {
                                                log.info("전화번호일때~");
                                                logService.saveEventLogWinningLimit(arEvent.getArEventId(), winningEntity.getArEventWinningId(), null, null, reqDto.getPhoneNumber(), EventLogWinningLimitDefine.ID_WINNINGID_MDN.name());
                                            }

                                        } catch (DuplicateKeyException e) {
                                            log.error("saveEventLogWinningLimit error {} ", e.toString());
                                            if (i == winningLinkedList.size()) {
                                                log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                                //꽝 로직
                                                return failWinningLogic(reqDto, arEvent);
                                            } else if (i < winningLinkedList.size()) {
                                                log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                                i++;
                                                continue;
                                            }
                                        }
                                        //꽝 로직
                                        return failWinningLogic(reqDto, arEvent);
                                    } else if (i < winningLinkedList.size()) {
                                        log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                        i++;
                                        continue;
                                    }
                                }
                            }
                            //1일일떄
                        } else if (winningEntity.getAttendCodeLimitType() == 1) {
                            log.info("eventWinningLogic : {} 순차적으로 조건 시간제한 아닐때 기한제한이고 1일 일때 조건 시작 ~");

                            if (isAttendCodeLimitDay) {
                                log.info("sequentiallyWinningProcessLogic : {} 당첨 제한 테이블 > 1일제한 값이 있을때");
                                if (i == winningLinkedList.size()) {
                                    log.info("sequentiallyWinningProcessLogic : {} 마지막 오브젝트여서 꽝!");
                                    //꽝 로직
                                    return failWinningLogic(reqDto, arEvent);
                                } else if (i < winningLinkedList.size()) {
                                    log.info("sequentiallyWinningProcessLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                    i++;
                                    continue;
                                }
                                //꽝 로직
                                return failWinningLogic(reqDto, arEvent);
                            } else {
                                int todayWinningCount = 0;
                                //참여번호일때
                                if (arEvent.getArAttendConditionCodeYn()) {
                                    log.info("참여번호일때~");
                                    todayWinningCount = logService.getCountEventWinningLogByArEventIdAndArEventWinningIdAndAttendCodeAndTodayNotFail2(arEvent.getArEventId(), winningEntity.getArEventWinningId(), reqDto.getAttendCode(), null);
                                } else if (arEvent.getAttendConditionMdnYn()) {
                                    log.info("전화번호일때~");
                                    todayWinningCount = logService.getCountEventWinningLogByArEventIdAndArEventWinningIdAndAttendCodeAndTodayNotFail2(arEvent.getArEventId(), winningEntity.getArEventWinningId(), null, reqDto.getPhoneNumber());
                                }

                                if (PredicateUtils.isGreaterThanEqualTo(todayWinningCount, winningEntity.getAttendCodeWinningCount())) {
                                    log.info("eventWinningLogic : {} 순차적으로 조건 시간제한 아닐때 기한제한이고 1일 일때 꽝!");
                                    if (i == winningLinkedList.size()) {
                                        log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");

                                        try {
                                            if (arEvent.getArAttendConditionCodeYn()) {
                                                logService.saveEventLogWinningLimit(arEvent.getArEventId(), winningEntity.getArEventWinningId(), DateUtils.getNowMMDD(), null, reqDto.getAttendCode(), EventLogWinningLimitDefine.ID_WINNINGID_CODE_TODAY.name());
                                            } else if (arEvent.getAttendConditionMdnYn()) {
                                                logService.saveEventLogWinningLimit(arEvent.getArEventId(), winningEntity.getArEventWinningId(), DateUtils.getNowMMDD(), null, reqDto.getPhoneNumber(), EventLogWinningLimitDefine.ID_WINNINGID_MDN_TODAY.name());
                                            }

                                        } catch (DuplicateKeyException e) {
                                            log.error("saveEventLogWinningLimit error {} ", e.toString());
                                            if (i == winningLinkedList.size()) {
                                                log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                                                //꽝 로직
                                                return failWinningLogic(reqDto, arEvent);
                                            } else if (i < winningLinkedList.size()) {
                                                log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                                i++;
                                                continue;
                                            }
                                        }
                                        //꽝 로직
                                        return failWinningLogic(reqDto, arEvent);
                                    } else if (i < winningLinkedList.size()) {
                                        log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                                        i++;
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                }

                log.info("eventWinningLogic : {} 기본 조건을 통과되서 당첨률 시작~");
                /** 당첨률 계산**/
                log.info("eventWinningLogic : {} 당첨률 :: < " + winningEntity.getWinningPercent() + " >");
                boolean isWin = EventUtils.percent(Float.valueOf(winningEntity.getWinningPercent()));
                if (!isWin) {
                    log.info("eventWinningLogic : {} 셋팅된 당첨률안에 못들거가서 꽝!");
                    if (i == winningLinkedList.size()) {
                        log.info("eventWinningLogic : {} 마지막 오브젝트여서 꽝!");
                        //꽝 로직
                        return failWinningLogic(reqDto, arEvent);
                    } else if (i < winningLinkedList.size()) {
                        log.info("eventWinningLogic : {} 다음 오브젝트로 넘김~~ 현재 오브젝트 :: " + winningEntity.getArEventWinningId());
                        i++;
                        continue;
                    }
                }

                if (isWin) {
                    log.info("eventWinningLogic : {} 매핑 + 시간설정아닐떄 모든 기준과 당첨률이 통과되어 당첨!!");
                    /*** 당첨 로직 주입 **/
                    return this.successWinningLogic( reqDto, winningEntity, arEvent, winningLimitList, null );
                }
                i++;
            }
        }   //for문 끝
        return null;
    }


    /**
     * 꽝 로직
     * @return
     */
    public ApiResultObjectDto failWinningLogic(EventWinningReqDto reqDto, ArEventByIdAtWinningProcessMapperVO arEvent) {
        int resultCode = httpSuccessCode;

        String eventId = reqDto.getEventId();

        WinningResultResDto resultResDto = eventWinning.getWinningResultButtonInfo(eventId, 0l, arEvent.getArEventId(), EventTypeDefine.AR.name(), true);

        log.info("failWinningLogic : {} 꽝 정보 >> " + resultResDto);

        if (PredicateUtils.isNotNull(reqDto.getSurveyLogAttendId())) {
            //서베이고 ROW 통계 데이터 가공 후 저장
            surveyGoMobileLogic.saveSurveyAnswerStaticsData(reqDto.getSurveyLogAttendId());
        }

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultResDto)
                .build();
    }

    /**
     * 당첨된 후 당첨 메서드에서 조건 통과 못할 시 event_log_winning 의 값을 FAIL 로 업데이트
     * @param reqDto
     * @param eventLogWinningId
     * @return
     */
    public ApiResultObjectDto failWinningUpdateLogic(EventWinningReqDto reqDto, long eventLogWinningId, ArEventByIdAtWinningProcessMapperVO arEventEntity) {
        int resultCode = httpSuccessCode;

        String eventId = reqDto.getEventId();

        //ar_event_object_id 가 null 일때 예외처리
        if (PredicateUtils.isNull(reqDto.getArEventObjectId())) {
            reqDto.setArEventObjectId(0);
        }

        //당첨 성공 로그 테이블 삭제
        logService.deleteEventLogWinning(eventLogWinningId, true);

        WinningResultResDto resultResDto = eventWinning.getWinningResultButtonInfo(eventId, 0l, arEventEntity.getArEventId(), EventTypeDefine.AR.name(), true);

        log.info("failWinningUpdateLogic : {} 꽝 정보 >> " + resultResDto);

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultResDto)
                .build();
    }

    /**
     * 당첨 로직
     * @param reqDto
     * @param winningEntity
     * @param arEventEntity
     * @return
     */
    public ApiResultObjectDto successWinningLogic(EventWinningReqDto reqDto, ArEventWinningEntity winningEntity, ArEventByIdAtWinningProcessMapperVO arEventEntity, List<EventLogWinningLimitMapperVO>winningLimitList, WebEventBaseEntity webEventBase) {
        int resultCode = httpSuccessCode;

        //당첨 로그 저장 후 저장된 로우 값 리턴
        EventLogWinningEntity eventLogWinningEntity = logService.saveEventLogWinningByReturn2(EventLogWinningEntity.saveOf(reqDto, winningEntity), true);

        //당첨 로그가 있으면 개수 체크(동시성에 관련된 예외처리) 시작
        if (PredicateUtils.isNotNull(eventLogWinningEntity)) {

            boolean isLimitHour = false;
            boolean isLimitDay = false;
            boolean isLimitTotal = false;

            if (!PredicateUtils.isNullList(winningLimitList)) {
                //ID_SORT_TODAY_HOUR 코드 값 확인
                String hourCode = StringTools.joinStringsNoSeparator(
                        String.valueOf(arEventEntity.getArEventId()), String.valueOf(winningEntity.getEventWinningSort()), DateUtils.getNowMMDD(), DateUtils.getNowHour()
                );
                String dayCode = StringTools.joinStringsNoSeparator(
                        String.valueOf(arEventEntity.getArEventId()), String.valueOf(winningEntity.getEventWinningSort()), DateUtils.getNowMMDD()
                );
                String totalCode = StringTools.joinStringsNoSeparator(
                        String.valueOf(arEventEntity.getArEventId()), String.valueOf(winningEntity.getEventWinningSort())
                );
                //ID_SORT_TODAY_HOUR 코드로 값이 존재하는지 체크
                Optional<EventLogWinningLimitMapperVO> hourLimitOptional = winningLimitList.stream()
                        .filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), hourCode))
                        .findAny();
                //ID_SORT_TODAY 코드로 값이 존재하는지 체크
                Optional<EventLogWinningLimitMapperVO> dayLimitOptional = winningLimitList.stream()
                        .filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), dayCode))
                        .findAny();

                //ID_SORT 코드로 값이 존재하는지 체크
                Optional<EventLogWinningLimitMapperVO> totalLimitOptional = winningLimitList.stream()
                        .filter(limit -> PredicateUtils.isEqualsStr(limit.getCode(), totalCode))
                        .findAny();
                //값이 존재하면 true
                if (hourLimitOptional.isPresent()) isLimitHour = true;
                if (dayLimitOptional.isPresent()) isLimitDay = true;
                if (totalLimitOptional.isPresent()) isLimitTotal = true;

            }

            //참여번호, 전화번호가 아닐때
            if (PredicateUtils.isNull(reqDto.getAttendCode()) || PredicateUtils.isNull(reqDto.getPhoneNumber())) {
                //시간 당첨 수량 체크
                if (!isLimitHour) {
                    if (PredicateUtils.isNull(winningEntity.getHourWinningNumber()))
                        winningEntity.setHourWinningNumber(0);
                    if (PredicateUtils.isGreaterThanZero(winningEntity.getHourWinningNumber())) {   //[SS-20095]오브젝트 맵핑 당첨팝업 오류 - 동시성 체크 :: 당첨이 오브젝트 맵핑일때 당첨 후 일당첨수량, 시간당 당첨수량 0 일때 예외처리 추가. 안지호/2022. 8. 17. 오후 12:36
                        int hourPrevSuccessCnt = logService.getHourEventLogWinningCountByArEventIdEqualsAndEventWinningSortEqualsAndIdIsLessThan2(eventLogWinningEntity.getArEventId(), eventLogWinningEntity.getEventWinningSort(), eventLogWinningEntity.getId());
                        if (PredicateUtils.isGreaterThan((hourPrevSuccessCnt + 1), winningEntity.getHourWinningNumber())) {
                            log.debug("동시성 시간 당첨 수량 초과 로그 ID {} ", eventLogWinningEntity.getId());
                            return failWinningUpdateLogic(reqDto, eventLogWinningEntity.getId(), arEventEntity);
                        }
                        //로우 총개수
                        int savedHourCnt = logService.getEventLogWinningSuccessCountHour(arEventEntity.getArEventId(), winningEntity.getEventWinningSort());
                        if (savedHourCnt > winningEntity.getHourWinningNumber()) {
                            Long lastIdx = logService.getEventLogWinningSuccessLastIndexHour(arEventEntity.getArEventId(), winningEntity.getEventWinningSort(), winningEntity.getTotalWinningNumber());
                            if (PredicateUtils.isNotNull(lastIdx)) {
                                logService.deleteEventLogWinningSuccessLastIndexGreaterThan(arEventEntity.getArEventId(), winningEntity.getEventWinningSort(), lastIdx);
                                return failWinningUpdateLogic(reqDto, eventLogWinningEntity.getId(), arEventEntity);
                            }
                        }
                    }
                }

                //일당첨 수량 체크
                if (!isLimitDay) {
                    if (PredicateUtils.isNull(winningEntity.getDayWinningNumber()))
                        winningEntity.setDayWinningNumber(0);
                    if (PredicateUtils.isGreaterThanZero(winningEntity.getDayWinningNumber())) {    //[SS-20095]오브젝트 맵핑 당첨팝업 오류 - 동시성 체크 :: 당첨이 오브젝트 맵핑일때 당첨 후 일당첨수량, 시간당 당첨수량 0 일때 예외처리 추가. 안지호/2022. 8. 17. 오후 12:36
                        int dayPrevSuccessCnt = logService.getDayEventLogWinningCountByArEventIdEqualsAndEventWinningSortEqualsAndIdIsLessThan2(eventLogWinningEntity.getArEventId(), eventLogWinningEntity.getEventWinningSort(), eventLogWinningEntity.getId());
                        if (PredicateUtils.isGreaterThan((dayPrevSuccessCnt + 1), winningEntity.getDayWinningNumber())) {
                            log.debug("동시성 일 당첨 수량 초과 로그 ID {} ", eventLogWinningEntity.getId());
                            return failWinningUpdateLogic(reqDto, eventLogWinningEntity.getId(), arEventEntity);
                        }
                        //로우 총개수
                        int savedHourCnt = logService.getEventLogWinningSuccessCountDay(arEventEntity.getArEventId(), winningEntity.getEventWinningSort());
                        if (savedHourCnt > winningEntity.getHourWinningNumber()) {
                            Long lastIdx = logService.getEventLogWinningSuccessLastIndexDay(arEventEntity.getArEventId(), winningEntity.getEventWinningSort(), winningEntity.getTotalWinningNumber());
                            if (PredicateUtils.isNotNull(lastIdx)) {
                                logService.deleteEventLogWinningSuccessLastIndexGreaterThan(arEventEntity.getArEventId(), winningEntity.getEventWinningSort(), lastIdx);
                                return failWinningUpdateLogic(reqDto, eventLogWinningEntity.getId(), arEventEntity);
                            }
                        }
                    }
                }

                //전체 당첨수량 체크
                if (!isLimitTotal) {
                    if (PredicateUtils.isNull(winningEntity.getTotalWinningNumber()))
                        winningEntity.setTotalWinningNumber(0);
                    if (PredicateUtils.isGreaterThanZero(winningEntity.getTotalWinningNumber())) {  //[SS-20095]오브젝트 맵핑 당첨팝업 오류 - 동시성 체크 :: 당첨이 오브젝트 맵핑일때 당첨 후 일당첨수량, 시간당 당첨수량 0 일때 예외처리 추가. 안지호/2022. 8. 17. 오후 12:36
                        int totalPrevSuccessCnt = logService.getTotalEventLogWinningCountByArEventIdEqualsAndEventWinningSortEqualsAndIdIsLessThan(eventLogWinningEntity.getArEventId(), eventLogWinningEntity.getEventWinningSort(), eventLogWinningEntity.getId());
                        if (PredicateUtils.isGreaterThan((totalPrevSuccessCnt + 1), winningEntity.getTotalWinningNumber())) {
                            log.debug("동시성 전체 당첨 수량 초과 로그 ID {} ", eventLogWinningEntity.getId());
                            return failWinningUpdateLogic(reqDto, eventLogWinningEntity.getId(), arEventEntity);
                        }
                        //로우 총개수
                        int savedHourCnt = logService.getEventLogWinningSuccessCount(arEventEntity.getArEventId(), winningEntity.getEventWinningSort());
                        if (savedHourCnt > winningEntity.getHourWinningNumber()) {
                            Long lastIdx = logService.getEventLogWinningSuccessLastIndex(arEventEntity.getArEventId(), winningEntity.getEventWinningSort(), winningEntity.getTotalWinningNumber());
                            if (PredicateUtils.isNotNull(lastIdx)) {
                                logService.deleteEventLogWinningSuccessLastIndexGreaterThan(arEventEntity.getArEventId(), winningEntity.getEventWinningSort(), lastIdx);
                                return failWinningUpdateLogic(reqDto, eventLogWinningEntity.getId(), arEventEntity);
                            }
                        }
                    }
                }
            }
        }
        //당첨 로그가 있으면 개수 체크(동시성에 관련된 예외처리) 끝

        if (PredicateUtils.isNull(webEventBase)) {
            webEventBase = arEventService.findEventBase(reqDto.getEventId());
        }

        //[SS-20193]
        //NFT, 쿠폰, OCB쿠폰, OCB포인트 지급 로직 시작
        if (PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.NFT쿠폰.code()) || PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.NFT.code())
                || PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.OCB쿠폰.code()) || PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.OCB포인트.code())) {
            if (PredicateUtils.isEqualsStr(StringDefine.N.name(), winningEntity.getSubscriptionYn())) {

                //쿠폰일떄
                if (PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.NFT쿠폰.code())) {
                    Long couponId = null;
                    try {
                        //쿠폰 선택 후 저장
                        couponId = arEventFrontService.saveSelectAvailableArEventCouponByArEventIdAndArEventWinningId(arEventEntity.getArEventId(), winningEntity.getArEventWinningId(), eventLogWinningEntity.getId(), false);
                        if (PredicateUtils.isNotNull(couponId)) {
                            arEventFrontService.updateCouponIsPayedById(couponId, true);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage() + " ::: couponId >>> " + couponId);
                        log.info("쿠폰 지급시 예외사항 발생!! ::::::: 꽝처리");
                        //쿠폰이 정상 발급이되면 쿠폰 상태를 '지급완료' 상태로 업데이트
                        try {
                            arEventService.deleteArEventNftCouponRepositoryByEventWinningLogId(eventLogWinningEntity.getId());
                            if (PredicateUtils.isNotNull(couponId)) {
                                arEventFrontService.updateCouponIsPayedById(couponId, false);
                            }
                        } catch (Exception e2) {
                            log.error(e2.getMessage());
                        }
                        return failWinningUpdateLogic(reqDto, eventLogWinningEntity.getId(), arEventEntity);
                    }
                }

                //NFT일떄
                if (PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.NFT.code())) {
                    //지급가능한 NFT 정보 가져오기
                    Long nftTokenId = null;
                    try {
                        nftTokenId = arEventFrontService.saveSelectAvailableArEventNftTokenByArEventIdAndArEventWinningId(arEventEntity.getArEventId(), winningEntity.getArEventWinningId(), eventLogWinningEntity.getId(), false);
                    } catch (Exception e) {
                        log.error(e.getMessage() + " ::: nftTokenId >>> " + nftTokenId);
                        log.info("NFT 지급시 예외사항 발생!! ::::::: 꽝처리");
                        return failWinningUpdateLogic(reqDto, eventLogWinningEntity.getId(), arEventEntity);
                    } finally {
                        //쿠폰이 정상 발급이되면 쿠폰 상태를 '지급완료' 상태로 업데이트
                        if (PredicateUtils.isNotNull(nftTokenId)) {
                            arEventFrontService.updateNftTokenIsPayedById(nftTokenId);
                        }
                    }
                }
            }
        }
        //[SS-20193]

        //자동 당첨일때 시작 - 서베이고 기능 추가건
        if (PredicateUtils.isEqualsStr(winningEntity.getAutoWinningYn(), StringDefine.Y.name())) {
            log.info("====================== 자동당첨일때 로직 시작 ======================");

            //자동당첨일때 핸드폰번호가 없으면 꽝 처리
            if (PredicateUtils.isNull(reqDto.getPhoneNumber()) && PredicateUtils.isNull(reqDto.getAttendCode())) {
                log.error("=================== 자동당첨인데 핸드폰번호, 참여코드가 없어서 꽝처리 ====================> 당첨 인덱스 >>> " + winningEntity.getArEventWinningId());
                return failWinningUpdateLogic(reqDto, eventLogWinningEntity.getId(), arEventEntity);
            }

            GiveAwayDeliverySaveReqDto saveReqDto = new GiveAwayDeliverySaveReqDto().builder()
                            .eventId(reqDto.getEventId())
                            .arEventWinningId(winningEntity.getArEventWinningId())
                            .eventLogWinningId(eventLogWinningEntity.getId())
                            .name(PredicateUtils.isNull(reqDto.getName()) ? "" : aes256Utils.encrypt(reqDto.getName()))
                            .phoneNumber(PredicateUtils.isNull(reqDto.getPhoneNumber()) ? "" : aes256Utils.encrypt(reqDto.getPhoneNumber()))
                            .attendCode(PredicateUtils.isNull(reqDto.getAttendCode()) ? "" : reqDto.getAttendCode())
                            .build();

            //서베이고일때 서베이고 참여 로그 값 주입 하기
            if (PredicateUtils.isEqualsStr(webEventBase.getEventType(), EventTypeDefine.SURVEY.name())) {
                saveReqDto.setSurveyLogAttendId(reqDto.getSurveyLogAttendId());
            }
            log.info("====================== 자동당첨 값 ======================> " + saveReqDto.toString());

            //OcbPointApiResDto ocbPointApiResDto = new OcbPointApiResDto();
            log.info("====================== 당첨정보 자동 입력 콜 시작 :: saveGiveAwayDeliveryLogic ======================");
            //당첨정보 저장 로직 콜
            int giveAwayId = 0;
            int giveAwayResultCode = 0;
            try {
                //당첨정보 입력
                ApiResultObjectDto giveAwayResult = this.saveGiveAwayDeliveryLogic(saveReqDto);
                Map<String, Object> resultMap = ModelMapperUtils.convertModel(giveAwayResult.getResult(), Map.class);
                //당첨정보 입력 결과 코드
                giveAwayResultCode = giveAwayResult.getResultCode();
                log.info(">>>>>>>>>>>>> giveAwayResultCode :: {}", giveAwayResultCode);
                //당첨정보 입력 결과 인덱스
                giveAwayId = (int)resultMap.get("id");
                log.info(">>>>>>>>>>>>> giveAwayId :: {}",giveAwayId);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                //꽝으로 리턴
                return failWinningUpdateLogic(reqDto, eventLogWinningEntity.getId(), arEventEntity);
            } finally {
                //당첨정보 자동입력 에러 시 당첨정보 삭제
                if (giveAwayResultCode != 200) {
                    if (giveAwayId > 0) {
                        arEventFrontService.deleteEventGiveAwayByGiveAwayId(giveAwayId);
                    }
                    //꽝으로 리턴
                    return failWinningUpdateLogic(reqDto, eventLogWinningEntity.getId(), arEventEntity);
                } else {
                    //노출방식이 OCB > 서베이고 > 자동당첨 > OCB포인트 > OCB 포인트지급 시작
                    if (PredicateUtils.isEqualsStr(arEventEntity.getEventExposureType(), EventExposureTypeDefine.OCB.name())
                            && EventLogicalTypeDefine.isSurveyEvent(arEventEntity.getEventLogicalType())
                            && PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.OCB포인트.code()))
                    {
                        log.info("노출방식이 OCB > 서베이고 > 자동당첨 > OCB포인트 > OCB 포인트지급 시작");
                        OcbPointSaveEntity ocbPointSaveEntity = arEventService.findOcbPointSaveByArEventIdAndArEventWinningId(arEventEntity.getArEventId(), winningEntity.getArEventWinningId());

                        if (PredicateUtils.isNull(ocbPointSaveEntity)) {
                            log.info("ocbPointSaveEntity null!! ::::::: 꽝처리");
                            return failWinningUpdateLogic(reqDto, eventLogWinningEntity.getId(), arEventEntity);
                        } else {
                            try {
                                //OCB 포인트 API
                                log.info("=============================== OCB 포인트 지급 API ===============================");
                                OcbPointApiResDto ocbPointApiResDto = ocbApiService.requestOcbPointSaveApi(ocbPointSaveEntity, reqDto.getPartnerToken(), reqDto.getEventId());
                                log.info("ocbPointSaveEntity ::: " + GsonUtils.getJsonStringAsObject(ocbPointSaveEntity));
                                if (PredicateUtils.isNull(ocbPointApiResDto.getCode()) || PredicateUtils.isNotEqualsStr(ocbPointApiResDto.getCode(), "00")) {
                                    if (PredicateUtils.isNull(ocbPointApiResDto.getCode())) {
                                        ocbPointApiResDto.setCode("null");
                                    }
                                    log.info("OCB포인트 지급 결과 예외사항 발생!! - OCB 포인트 API 통신에러 꽝처리 에러코드 ::::::: " + ocbPointApiResDto.getCode());
                                    //OCB 포인트 지급 실패 로그 저장
                                    try {
                                        logService.saveOcbLogPointSave(ocbPointSaveEntity, reqDto.getEventId(), aes256Utils.decrypt(saveReqDto.getPhoneNumber()), arEventEntity.getOcbPointSaveType(), false, GsonUtils.getJsonStringAsObject(ocbPointApiResDto), null, ocbPointApiResDto.getRequestId(), reqDto.getPartnerToken());
                                    } catch (Exception e) {
                                        log.info("OCB포인트 로그저장 시 에러 발생!");
                                        log.error(e.getMessage(), e);
                                        return failWinningUpdateLogic(reqDto, eventLogWinningEntity.getId(), arEventEntity);
                                    }
                                } else {
                                    try {
                                        //[DTWS-133]
                                        //OCB 포인트 지급 로그 저장
                                        logService.saveOcbLogPointSave(ocbPointSaveEntity, reqDto.getEventId(), aes256Utils.decrypt(saveReqDto.getPhoneNumber()), arEventEntity.getOcbPointSaveType(), true, null, giveAwayId, ocbPointApiResDto.getRequestId(), reqDto.getPartnerToken());
                                    } catch (Exception e) {
                                        log.info("OCB 포인트 지급 로그 저장 에러 발생!");
                                        log.error(e.getMessage(), e);
                                        return failWinningUpdateLogic(reqDto, eventLogWinningEntity.getId(), arEventEntity);
                                    }
                                }
                            } catch (Exception e) {
                                log.info("OCB포인트 API 통신 에러 발생!! ::::::: 꽝처리");
                                log.error(e.getMessage(), e);
                                arEventFrontService.deleteEventGiveAwayByGiveAwayId(giveAwayId);
                                return failWinningUpdateLogic(reqDto, eventLogWinningEntity.getId(), arEventEntity);
                            }
                        }
                    }
                    //스탬프 당첨일때 일때 스탬프 적립 로그 저장하기 - [DTWS-368]
                    if (PredicateUtils.isEqualsStr(winningEntity.getWinningType(), WinningTypeDefine.스탬프.code())) {
                        String attendValue = "";
                        if (PredicateUtils.isNotNull(reqDto.getPhoneNumber())) {
                            attendValue = aes256Utils.encrypt(reqDto.getPhoneNumber());
                        }
                        if (PredicateUtils.isNotNull(reqDto.getAttendCode())) {
                            attendValue = reqDto.getAttendCode();
                        }

                        try {
                            //스탬프 TR 로그 저장
                            stampFrontService.selectInsertStampEventTrLog(reqDto.getEventId(), winningEntity.getArEventWinningId(), attendValue);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            arEventFrontService.deleteEventGiveAwayByGiveAwayId(giveAwayId);
                            return failWinningUpdateLogic(reqDto, eventLogWinningEntity.getId(), arEventEntity);
                        }
                    }
                }
            }
            log.info("====================== 당첨정보 자동 입력 콜 끝 :: saveGiveAwayDeliveryLogic ======================");
        }
        //자동 당첨일때 끝 - 서베이고 기능 추가건

        WinningResultResDto resultResDto = eventWinning.getWebEventSuccessWinningResultButtonInfo(reqDto.getEventId(), eventLogWinningEntity.getId(), webEventBase.getStpConnectYn(), winningEntity);

        log.info("successWinningLogic : {} 당첨 정보 >> " + resultResDto.toString());

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultResDto)
                .build();

    }

    public ApiResultObjectDto getWinningButtonDetailLogic(int arEventWinningButtonId) {
        String subscriptionRaffleDay = "";
        String subscriptionRaffleTime = "";

        //AR_EVENT_WINNING_BUTTON 정보
        ArEventWinningButtonEntity buttonEntity = arEventService.findArEventWinningButtonById(arEventWinningButtonId);
        //AR_EVENT_WINNING 정보
        ArEventWinningEntity winningEntity = arEventService.findByArEventWinningById(buttonEntity.getArEventWinningId());
        // AR_EVENT_WINNING_BUTTON_ADD 정보
        List<ArEventWinningButtonAddEntity> buttonAddEntity = arEventService.findAllArEventWinningButtonAddByArEventWinningButtonId(buttonEntity.getArEventWinningButtonId());

        //응모 당첨일 관련 데이터 정리 시작
        if (PredicateUtils.isNotNull(winningEntity)) {
            //arEventWinningEntity > arEventWinningResDto 변환
            ArEventWinningResDto winningResDto = ModelMapperUtils.convertModel(winningEntity, ArEventWinningResDto.class);

            if (PredicateUtils.isNotNull(winningResDto)) {
                if (PredicateUtils.isNotNull(winningResDto.getSubscriptionRaffleDate())) {
                    Map<String, String> disuniteSubscriptionRaffleDate = DateUtils.disuniteDateHourFromYYYYMMDDHHMMSS(winningResDto.getSubscriptionRaffleDate());
                    if (PredicateUtils.isNotNull(disuniteSubscriptionRaffleDate)) {
                        subscriptionRaffleDay = disuniteSubscriptionRaffleDate.get("date");
                        subscriptionRaffleTime = disuniteSubscriptionRaffleDate.get("hour");
                    }
                }
            }
        }
        //AR_EVENT 정보
        ArEventEntity arEventEntity = new ArEventEntity();
        //스탬프 판 정보
        StampEventPanModel stampEventPan = new StampEventPanModel();
        //AR, 서베이고 이벤트일때
        if (PredicateUtils.isNotNull(winningEntity.getArEventId())) {
            arEventEntity = arEventService.findArEventById(arEventService.findByArEventWinningById(buttonEntity.getArEventWinningId()).getArEventId());
        }
        //스탬프 이벤트일때
        if (PredicateUtils.isNotNull(winningEntity.getStpId())) {
            stampEventPan = stampFrontService.findStampEventPanByStpId(winningEntity.getStpId());
        }

        GiveAwayResDto resDto = new GiveAwayResDto().builder()
                .winningPasswordYn(arEventEntity.getWinningPasswordYn())
                .winningType(winningEntity.getWinningType())
                .subscriptionRaffleDay(subscriptionRaffleDay)
                .subscriptionRaffleTime(subscriptionRaffleTime)
                .buttonInfo(buttonEntity)
                .arEventInfo(arEventEntity)
                .arEventWinningInfo(winningEntity)
                .arEventWinningButtonAddList(buttonAddEntity)
                .stampEventPanInfo(stampEventPan)
                .build();

        return new ApiResultObjectDto().builder()
                .resultCode(httpSuccessCode)
                .result(resDto)
                .build();

    }

    /**
     *
     * @param eventId
     * @param successYn
     * @return
     */
    public ApiResultObjectDto saveEventLogAttendButtonLogic(String eventId, String successYn) {
        int resultCode = httpSuccessCode;
        Map<String, String>resultMap = new HashMap<>();

        if (PredicateUtils.isNull(eventId) || PredicateUtils.isNull(successYn)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        } else {
            ArEventEntity arEvent = arEventService.findArEventByEventId(eventId);
            if (PredicateUtils.isNull(arEvent)) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_AR_EVENT_INFO_NULL.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
            }
            if (PredicateUtils.isNotNull(arEvent)) {
                //AR호출 로그 저장
                logService.saveEventLogAttendButton(EventLogAttendSaveVO.saveOf(eventId, arEvent.getArEventId(), "", successYn));
                resultMap.put("eventId", eventId);
                resultMap.put("successYn", successYn);
            }
        }

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultMap)
                .build();
    }

    /**
     * 경품 수령하기 로직
     * @param reqDto
     * @return
     */
    public ApiResultObjectDto receiptGiveAwayLogic(GiveAwayReceiptReqDto reqDto) {
        int resultCode = httpSuccessCode;

        //경품정보가 있는지 확인
        EventGiveAwayDeliveryEntity deliveryEntity = arEventFrontService.findEventGiveAwayById(reqDto.getGiveAwayId());
        //경품정보가 없으면 에러코드 처리
        if (PredicateUtils.isNull(deliveryEntity)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_NULL_EVENT_GIVE_AWAY_DELIVERY.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }
        //경품정보가 있으면 로직 시작
        if (PredicateUtils.isNotNull(deliveryEntity)) {
            //이미 수령된 상태면 예외처리
            if (deliveryEntity.getIsReceive()) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_ALREADY_RECEIPT_GIVE_AWAY.code();
            } else {
                //수령처리
                try {
                    arEventFrontService.updateEventGiveAwayDeliveryIsReceiveByGiveAwayId(reqDto.getGiveAwayId(), true);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("giveAwayId" , String.valueOf(reqDto.getGiveAwayId()));

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultMap)
                .build();
    }

    /**
     * 임시비밀번호 SMS 발송
     * @param reqDto
     * @return
     */
    public ApiResultObjectDto sendSmsTemporaryPasswordLogic(EventIdPhoneNumberReqDto reqDto) {
        int resultCode = httpSuccessCode;
        boolean isSuccess = true;

        Map<String, Object>resultMap = new HashMap<>();

        //전화번호가 없으면 에러처리
        if (PredicateUtils.isNull(reqDto.getReceiverPhoneNumber())) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }

        if (PredicateUtils.isNotNull(reqDto.getReceiverPhoneNumber())) {
            //변경될 비밀번호
            String newPassword = RandomPasswordUtil.generatePassword();

            try {
                //EM_TRAN 테이블에 SMS발송 내용 저장
                smsService.saveEmTranSms(aes256Utils.decrypt(reqDto.getReceiverPhoneNumber()), SmsMessageDefine.NEW_PASS.content().replace("{newPass}", newPassword), "", SmsTranTypeDefine.SMS.key());
                //경품 저장 정보에 해당 이벤트의 비밀번호를 임시비밀번호로 업데이트
                arEventFrontService.updatePasswordEventGiveAwayDelivery(reqDto.getEventId(), reqDto.getReceiverPhoneNumber(), newPassword);

                resultMap.put("receiverPhoneNumber", aes256Utils.decrypt(reqDto.getReceiverPhoneNumber()));

            } catch (Exception e) {
                isSuccess = false;
                log.error(e.getMessage(), e);
            } finally {
                resultMap.put("isSuccess", isSuccess);
            }
        }
        return new ApiResultObjectDto().builder()
                .result(resultMap)
                .resultCode(resultCode)
                .build();
    }

    /**
     * NFT 보관함 리스트
     * @param reqDto
     * @return
     */
    public ApiResultObjectDto getEventNftRepositoryListLogic(EventIdPhoneNumberReqDto reqDto) {
        int resultCode = httpSuccessCode;
        NftRepositoryResDto nftRepositoryResDto = new NftRepositoryResDto();
        ArEventNftWalletEntity nftWalletInfo = new ArEventNftWalletEntity();
        List<ArEventNftBannerEntity> bannerList = new ArrayList<>();
        String winningType = null;

        //필수 파라미터 없으면 에러처리
        if (PredicateUtils.isNull(reqDto.getEventId())) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }

        if (PredicateUtils.isNotNull(reqDto.getEventId())) {

            boolean isStamp = StringTools.containsIgnoreCase(reqDto.getEventId(), "S");

            EventBaseJoinArEventJoinEventButtonVO eventVO = new EventBaseJoinArEventJoinEventButtonVO();
            EventBaseJoinStampEventMainVO stampEventVO = new EventBaseJoinStampEventMainVO();

            if (!isStamp) {
                eventVO = arEventFrontService.findArEventBaseInfoByEventId(reqDto.getEventId());
            } else {
                stampEventVO = stampFrontService.findEventBaseJoinStampEventMain(reqDto.getEventId());
            }

            if (PredicateUtils.isNull(eventVO) && PredicateUtils.isNull(stampEventVO)) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_AR_EVENT_INFO_NULL.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
            }

            List<ArEventNftRepositoryResDto> nftWinningInfoList = new ArrayList<>();
            List<ArEventNftCouponRepositoryEntity> couponWinningInfoList = new ArrayList<>();
            List<CouponDetailResDto>newCouponWinningInfoList = new ArrayList<>();

            List<UserWinningInfoResDto> winningInfoList = new ArrayList<>();
            if (PredicateUtils.isNotNull(reqDto.getUserPhoneNumber())) {
                if (PredicateUtils.isNotNull(reqDto.getEventId()) && PredicateUtils.isNotNull(reqDto.getUserPhoneNumber())) {
                    //이벤트ID + 전화번호로 당첨목록 가져오기
                    if (!isStamp) {
                        winningInfoList = ModelMapperUtils.convertModelInList(
                                arEventFrontService.findEventGiveAwayDeliveryListAtNftWinning(reqDto.getEventId(), reqDto.getUserPhoneNumber()), UserWinningInfoResDto.class
                        );
                    } else {
                        winningInfoList = ModelMapperUtils.convertModelInList(
                                stampFrontService.findStampEventGiveAwayDeliveryAtHistory(reqDto.getEventId(), reqDto.getUserPhoneNumber(), null), UserWinningInfoResDto.class
                        );
                    }
                }
                //[DTWS-149] 스탬프 관련 기능 추가
                if (PredicateUtils.isNotNull(reqDto.getEventId()) && PredicateUtils.isNotNull(reqDto.getStampEventIds())) {
                    //스탬프형일때 이벤트 IN 절 조회
                    winningInfoList = spotService.findEventGiveAwayDeliveryListAsStampEvent(reqDto.getStampEventIds(), reqDto.getUserPhoneNumber(), null);
                }
            }
            if (PredicateUtils.isNotNull(reqDto.getAttendCode())) {
                if (PredicateUtils.isNotNull(reqDto.getEventId()) && PredicateUtils.isNotNull(reqDto.getAttendCode())) {
                    //이벤트ID + 참여코드로 당첨목록 가져오기
                    if (!isStamp) {
                        winningInfoList = ModelMapperUtils.convertModelInList(
                                arEventFrontService.findEventGiveAwayDeliveryListAtNftWinningByAttendCode(reqDto.getEventId(), reqDto.getAttendCode()), UserWinningInfoResDto.class
                        );
                    } else {
                        winningInfoList = ModelMapperUtils.convertModelInList(
                                stampFrontService.findStampEventGiveAwayDeliveryAtHistory(reqDto.getEventId(), null, reqDto.getAttendCode()), UserWinningInfoResDto.class
                        );
                    }
                }
                //[DTWS-149] 스탬프 관련 기능 추가
                if (PredicateUtils.isNotNull(reqDto.getEventId()) && PredicateUtils.isNotNull(reqDto.getStampEventIds())) {
                    //스탬프형일때 이벤트 IN 절 조회
                    winningInfoList = spotService.findEventGiveAwayDeliveryListAsStampEvent(reqDto.getStampEventIds(), null, reqDto.getAttendCode());
                }
            }

            if (PredicateUtils.isNotNullList(winningInfoList)) {
                //NFT 일때
                if (!isStamp) {
                    if (PredicateUtils.isEqualsStr(reqDto.getWinningType(), WinningTypeDefine.NFT.code())) {

                        //당첨목록에서 당첨ID 목록 추출
                        List<Integer> giveAwayIdList = winningInfoList.stream()
                                .filter(winningInfo -> PredicateUtils.isEqualsStr(winningInfo.getWinningType(), WinningTypeDefine.NFT.code()))
                                .map(UserWinningInfoResDto::getGiveAwayId)
                                .collect(Collectors.toList());

                        //NFT 저장소에 당첨ID 리스트 조회
                        nftWinningInfoList = ModelMapperUtils.convertModelInList(arEventFrontService.findArEventNftRepositoryByGiveAwayIdIn(giveAwayIdList), ArEventNftRepositoryResDto.class);

                        if (PredicateUtils.isNotNullList(nftWinningInfoList)) {
                            nftWinningInfoList.forEach(nftWinningInfo -> {
                                //NFT 정보 주입
                                nftWinningInfo.setNftTokenInfo(arEventFrontService.findArEventNftTokenInfoEntityById(nftWinningInfo.getArEventNftTokenInfoId()));
                            });
                        }
                        //지갑정보 주입
                        nftWalletInfo = arEventFrontService.findArEventNftWalletByPhoneNumber(reqDto.getUserPhoneNumber());
                    }
                }

                //NFT 쿠폰 일때
                if (PredicateUtils.isEqualsStr(reqDto.getWinningType(), WinningTypeDefine.NFT쿠폰.code())) {

                    //쿠폰 저장소에 당첨ID 리스트 조회
                    if (!isStamp) {
                        //당첨목록에서 당첨ID 목록 추출
                        List<Integer> giveAwayIdList = winningInfoList.stream()
                                .filter(winningInfo ->
                                        PredicateUtils.isEqualsStr(winningInfo.getWinningType(), WinningTypeDefine.NFT쿠폰.code())
                                                || PredicateUtils.isEqualsStr(winningInfo.getWinningType(), WinningTypeDefine.OCB쿠폰.code())
                                )
                                .map(UserWinningInfoResDto::getGiveAwayId)
                                .collect(Collectors.toList());

                        couponWinningInfoList = arEventFrontService.findArEventNftCouponRepositoryByGiveAwayIdIn(giveAwayIdList);
                    } else {
                        //당첨목록에서 당첨ID 목록 추출
                        List<Long> stpGiveAwayIdList = winningInfoList.stream()
                                .filter(winningInfo ->
                                        PredicateUtils.isEqualsStr(winningInfo.getWinningType(), WinningTypeDefine.NFT쿠폰.code()) || PredicateUtils.isEqualsStr(winningInfo.getWinningType(), WinningTypeDefine.OCB쿠폰.code()))
                                .map(UserWinningInfoResDto::getStpGiveAwayId)
                                .collect(Collectors.toList());

                        couponWinningInfoList = arEventFrontService.findArEventNftCouponRepositoryByStpGiveAwayIdIn(stpGiveAwayIdList);
                        winningType = winningInfoList.get(0).getWinningType();
                    }

                    if (PredicateUtils.isNotNullList(couponWinningInfoList)) {
                        for (ArEventNftCouponRepositoryEntity couponWinningInfo : couponWinningInfoList) {
                            CouponDetailResDto detailResDto = ModelMapperUtils.convertModel(couponWinningInfo, CouponDetailResDto.class);
                            if (!isStamp) {
                                EventGiveAwayDeliveryEntity giveAwayDelivery = arEventFrontService.findEventGiveAwayById(couponWinningInfo.getGiveAwayId());
                                if (PredicateUtils.isNotNull(giveAwayDelivery.getGiveAwayId())) {
                                    detailResDto.setEventGiveAwayDeliveryEntity(ModelMapperUtils.convertModel(giveAwayDelivery, EventGiveAwayDeliveryResDto.class));
                                    winningType = giveAwayDelivery.getWinningType();
                                }
                                ArEventNftCouponInfoResDto arEventNftCouponInfoEntity = new ArEventNftCouponInfoResDto();
                                if (PredicateUtils.isNotNull(couponWinningInfo.getNftCouponInfoId())) {
                                    arEventNftCouponInfoEntity = arEventFrontService.findArEventNftCouponInfJoinArEventWinningById(couponWinningInfo.getNftCouponInfoId());
                                }
                                if (PredicateUtils.isNotNull(arEventNftCouponInfoEntity.getId())) {
                                    detailResDto.setArEventNftCouponInfoEntity(ModelMapperUtils.convertModel(arEventNftCouponInfoEntity, ArEventNftCouponInfoResDto.class));
                                }
                            } else {
                                detailResDto.setStampEventGiveAwayDelivery(stampFrontService.findStampEventGiveAwayDeliveryById(couponWinningInfo.getStpGiveAwayId()));
                                detailResDto.setArEventNftCouponInfoEntity(ModelMapperUtils.convertModel(arEventFrontService.findArEventNftCouponInfJoinArEventWinningById(couponWinningInfo.getNftCouponInfoId()), ArEventNftCouponInfoResDto.class));
                            }
                            //DTWS-70 - OCB 쿠폰 연동
                            if (PredicateUtils.isEqualsStr(winningType, WinningTypeDefine.OCB쿠폰.code())) {
                                CouponInfoResDto ocbCoupon = ocbApiService.getPicasoCouponInfoApi(couponWinningInfo.getOcbCouponId());

                                if (PredicateUtils.isNotNull(ocbCoupon.getOid())) {
                                    //시럽쿠폰 이미지 URL주입
                                    detailResDto.setOcbCouponImgUrl(ocbCoupon.getImagePath());
                                }
                            }
                            //DTWS-70 - OCB 쿠폰 연동 끝
                            newCouponWinningInfoList.add(detailResDto);
                        }
                    }
                }

                if (!isStamp) {
                    bannerList = arEventService.findAllArEventBannerByArEventId(eventVO.getArEventId());
                } else {
                    bannerList = arEventService.findAllArEventBannerByStpId(stampEventVO.getStpId());
                }
            }
            nftRepositoryResDto = nftRepositoryResDto.builder()
                    .nftWalletInfo(nftWalletInfo)
                    .nftRepositoryInfo(nftWinningInfoList)
                    .nftBannerInfo(PredicateUtils.isNotNullList(bannerList) ? bannerList : null)
                    .couponRepositoryInfo(newCouponWinningInfoList)
                    .diffServiceEndDateTodayCount(DateUtils.differenceTwoDay(
                            !isStamp ? PredicateUtils.isNotNull(eventVO.getRealEventEndDate()) ? DateUtils.convertDateToString(eventVO.getRealEventEndDate(), DateUtils.PATTERN_YYYY_MMD_DD) : DateUtils.convertDateToString(eventVO.getEventEndDate(), DateUtils.PATTERN_YYYY_MMD_DD)
                                    : PredicateUtils.isNotNull(stampEventVO.getRealEventEndDate()) ? DateUtils.convertDateToString(stampEventVO.getRealEventEndDate(), DateUtils.PATTERN_YYYY_MMD_DD) : DateUtils.convertDateToString(stampEventVO.getEventEndDate(), DateUtils.PATTERN_YYYY_MMD_DD),
                            DateUtils.getNowDay()
                    )).build();
        }
        return new ApiResultObjectDto().builder()
                .result(nftRepositoryResDto)
                .resultCode(resultCode)
                .build();
    }

    /**
     * NFT 보관함 상세정보
     * @param arNftRepositoryId
     * @return
     */
    public ApiResultObjectDto getEventNftRepositoryDetailLogic(long arNftRepositoryId) {
        int resultCode = httpSuccessCode;
        ArEventNftRepositoryResDto repositoryResDto = new ArEventNftRepositoryResDto();

        if (PredicateUtils.isEqualZero((int)arNftRepositoryId)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }

        if (PredicateUtils.isGreaterThanZero((int)arNftRepositoryId)) {
            //AR_EVENT_NFT_REPOSITORY 테이블 조회
            repositoryResDto = ModelMapperUtils.convertModel(arEventFrontService.findArEventNftRepositoryById(arNftRepositoryId), ArEventNftRepositoryResDto.class);

            //AR_EVENT_NFT_REPOSITORY 정보가 있으면
            if (PredicateUtils.isNotNull(repositoryResDto)) {

                //AR_EVENT_NFT_TOKEN_INFO 정보 조회 후 주입
                ArEventNftTokenInfoEntity nftTokenInfo = arEventFrontService.findArEventNftTokenInfoEntityById(repositoryResDto.getArEventNftTokenInfoId());
                repositoryResDto.setNftTokenInfo(nftTokenInfo);

                //AR_EVENT_NFT_TOKEN_INFO 정보가 있으면
                if (PredicateUtils.isNotNull(nftTokenInfo)) {
                    //AR_EVENT_NFT_BENEFIT 정보 주입
                    repositoryResDto.setNftBenefitInfo(arEventService.findAllArEventNftBenefitByArEventWinningId(nftTokenInfo.getArEventWinningId()));
                }
            }
        }
        return new ApiResultObjectDto().builder()
                .result(repositoryResDto)
                .resultCode(resultCode)
                .build();
    }

    /**
     * NFT 지갑주소 저장하기
     * @param reqDto
     * @return
     */
    public ApiResultObjectDto saveNftWalletLogic(NftWalletSaveReqDto reqDto) {
        int resultCode = httpSuccessCode;
        Map<String, Object>resultMap = new HashMap<>();

        String nftWalletId = reqDto.getNftWalletId();
        String eventId = reqDto.getEventId();
        String userPhoneNumber = reqDto.getUserPhoneNumber();
        String walletAddress = reqDto.getWalletAddress();

        if (PredicateUtils.isNull(eventId) || PredicateUtils.isNull(userPhoneNumber) || PredicateUtils.isNull(walletAddress)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        } else {

            ArEventByIdAtWinningProcessMapperVO arEventEntity = arEventService.findArEventByEventIdAtWinningProcess(eventId);
            //ar_event 정보가 없으면 에러처리
            if (PredicateUtils.isNull(arEventEntity)) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_AR_EVENT_INFO_NULL.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
            }
            //ar_event 정보가 있으면
            if (PredicateUtils.isNotNull(arEventEntity)) {
                //nftWalletId 가 있으면 수정
                if (StringUtils.isNotEmpty(nftWalletId) && PredicateUtils.isGreaterThanZero(Integer.parseInt(nftWalletId))) {
                    ArEventNftWalletEntity savedNftWallet = arEventFrontService.findArEventNftWalletById(Long.parseLong(nftWalletId));
                    if (PredicateUtils.isNotNull(savedNftWallet)) {
                        arEventFrontService.saveArEventNftWallet(ArEventNftWalletEntity.updateOf(savedNftWallet, walletAddress));
                    }
                }
                //nftWalletId 없으면 신규 저장 로직
                if (StringUtils.isEmpty(nftWalletId) || PredicateUtils.isEqualZero(Integer.parseInt(nftWalletId))) {
                    //이벤트에 지갑주소가 등록되어있는지 개수 확인
                    int walletCount = arEventFrontService.countNftWalletByArEventIdAndUserPhoneNumber(arEventEntity.getArEventId(), userPhoneNumber, NftWalletTypeDefine.KAS.name());
                    //지갑주소가 하나라도 있으면 에러처리
                    if (PredicateUtils.isGreaterThanZero(walletCount)) {
                        resultCode = ErrorCodeDefine.CUSTOM_ERROR_DUPLICATE_NFT_WALLET_ADDRESS.code();
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                    }
                    //지갑주소가 없으면 저장 로직
                    if (PredicateUtils.isEqualZero(walletCount)) {
                        arEventFrontService.saveArEventNftWallet(ArEventNftWalletEntity.saveOf(arEventEntity.getArEventId(), userPhoneNumber, walletAddress));
                        //저장된 지갑정보 조회
                        ArEventNftWalletEntity savedWalletInfo = arEventFrontService.findArEventNftWalletEntityByWalletAddressAndWalletTypeAndArEventId(walletAddress, NftWalletTypeDefine.KAS.name(), arEventEntity.getArEventId());
                        //이벤트ID + 전화번호로 당첨목록 가져오기
                        List<UserWinningInfoResDto> winningInfoList = arEventFrontService.findAllGiveAwayDeliveryByEventIdAndPhoneNumber(eventId, userPhoneNumber);

                        if (!PredicateUtils.isNullList(winningInfoList)) {
                            //당첨목록에서 당첨ID 목록 추출
                            List<Integer> giveAwayIdList = winningInfoList.stream()
                                    .filter(winningInfo -> PredicateUtils.isEqualsStr(winningInfo.getWinningType(), WinningTypeDefine.NFT.code()))
                                    .map(UserWinningInfoResDto::getGiveAwayId)
                                    .collect(Collectors.toList());

                            if (PredicateUtils.isNotNull(savedWalletInfo)) {
                                //저장소에 있는 NFT의 지갑수소 맵핑 업데이트
                                arEventFrontService.updateArEventNftWalletIdFromNftRepositoryByGiveAwayIds(savedWalletInfo.getId(), giveAwayIdList);
                            }
                        }

                        resultMap.put("userPhoneNumber", userPhoneNumber);
                    }
                }
            }
        }
        return new ApiResultObjectDto().builder()
                .result(resultMap)
                .resultCode(resultCode)
                .build();
    }

    /**
     * 이벤트에 응모형, NFT 당첨형이 있는지 확인 로직
     * @param eventId
     * @return
     */
    public ApiResultObjectDto getSubscriptionNftYnLogic(String eventId) {
        int resultCode = httpSuccessCode;
        Map<String, Object> resultMap = new HashMap<>();

        if (PredicateUtils.isNull(eventId)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        } else {
            WebEventBaseEntity webEventBase = arEventService.findEventBase(eventId);
            resultMap.put("eventType", webEventBase.getEventType());

            List<ArEventWinningEntity> arEventWinningEntityList = new ArrayList<>();
            //당첨정보 목록 가져오기
            if (PredicateUtils.isEqualsStr(webEventBase.getEventType(), EventTypeDefine.STAMP.name())) {
                arEventWinningEntityList = arEventService.findArEventWinningListByStpId(stampFrontService.findStampEventMainByEventId(eventId).getStpId());
            } else {
                arEventWinningEntityList = arEventService.findArEventWinningListByArEventId(arEventService.findArEventByEventId(eventId).getArEventId());
            }


            if (PredicateUtils.isNotNullList(arEventWinningEntityList)) {
                List<ArEventWinningEntity> filterdWinningList = arEventWinningEntityList
                        .stream()
                        .filter(eventWinning -> PredicateUtils.isNotNull(eventWinning.getSubscriptionYn()))
                        .collect(Collectors.toList());

                if (PredicateUtils.isNotNullList(filterdWinningList)) {
                    //응모형인지 확인 시작
                    Optional<ArEventWinningEntity> subscriptionOptional = arEventWinningEntityList
                            .stream()
                            .filter(eventWinning -> PredicateUtils.isEqualsStr(eventWinning.getSubscriptionYn(), StringDefine.Y.name()))
                            .findAny();

                    //응모형이면
                    if (subscriptionOptional.isPresent()) {
                        resultMap.put("isSubscription", true);
                    }
                    //응모형이 아니면
                    if (!subscriptionOptional.isPresent()) {
                        resultMap.put("isSubscription", false);
                    }
                    //응모형인지 확인 끝
                }

                //NFT 인지 확인 시작
                Optional<ArEventWinningEntity> nftOptional = arEventWinningEntityList
                        .stream()
                        .filter(eventWinning -> PredicateUtils.isEqualsStr(eventWinning.getWinningType(), WinningTypeDefine.NFT.code()))
                        .findAny();

                //NFT이면
                if (nftOptional.isPresent()) {
                    resultMap.put("isNft", true);
                }
                //NFT아니면
                if (!nftOptional.isPresent()) {
                    resultMap.put("isNft", false);
                }
                //NFT인지 확인 끝
            }
        }
        return new ApiResultObjectDto().builder()
                .result(resultMap)
                .resultCode(resultCode)
                .build();
    }

    /**
     * 쿠폰 사용처리하기 로직
     * @param couponRepositoryId
     * @return
     */
    public ApiResultObjectDto useCouponLogic(final Long couponRepositoryId) {
        int resultCode = httpSuccessCode;
        Map<String, Object>resultMap = new HashMap<>();

        resultMap.put("repositoryId", couponRepositoryId);
        //파라미터 없으면 에러처리
        if (PredicateUtils.isNull(couponRepositoryId)) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR);
        }
        //쿠폰 저장소의 상세 정보 가져오기
        ArEventNftCouponRepositoryEntity couponRepositoryEntity = arEventFrontService.findArEventNftCouponRepositoryEntityById(couponRepositoryId);
        //쿠폰 저장소의 값이 없으면 에러처리
        if (PredicateUtils.isNull(couponRepositoryEntity)) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GET_DATA_IS_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GET_DATA_IS_NULL);
        }
        //이미 사용완료된 쿠폰이면 에러처리
        if (couponRepositoryEntity.getIsUse()) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_ALREADY_RECEIPT_GIVE_AWAY.getDesc(), ResultCodeEnum.CUSTOM_ERROR_ALREADY_RECEIPT_GIVE_AWAY);
        }
        //쿠폰을 사용처리하기
        try {
            arEventFrontService.saveArEventNftCouponRepository(ArEventNftCouponRepositoryEntity.useOf(couponRepositoryEntity));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            //PV 로그 저장
            Map<String, Object> couponMap = new HashMap<>();
            //AR 이벤트 일때
            if (PredicateUtils.isNotNull(couponRepositoryEntity.getGiveAwayId())) {
                if (couponRepositoryEntity.getGiveAwayId() > 0) {
                    couponMap = arEventFrontService.findEventIdArEventWinningIdByCouponRepositoryId(couponRepositoryId);
                }
            }
            //스탬프 이벤트 일때
            if (PredicateUtils.isNotNull(couponRepositoryEntity.getStpGiveAwayId())) {
                if (couponRepositoryEntity.getStpGiveAwayId() > 0L) {
                    couponMap = arEventFrontService.findEventIdArEventWinningIdByCouponRepositoryIdAtStamp(couponRepositoryId);
                }
            }

            Object eventId           = couponMap.get("event_id");
            Object arEventWinningId  = couponMap.get("ar_event_winning_id");

            //PV 로그 저장
            if (PredicateUtils.isNotNull(eventId) && PredicateUtils.isNotNull(arEventWinningId)) {
                EventLogPvReqDto pvReqDto = new EventLogPvReqDto();
                pvReqDto.setEventId(String.valueOf(eventId));
                pvReqDto.setPvLogType(EventLogPvKeyDefine.MAIN_LOCKER_LIST_DETAIL_POPUP_1.name());
                pvReqDto.setOrder(String.valueOf(arEventWinningId));

                try {
                    logService.saveEventLogPv(pvReqDto, EventLogPvKeyDefine.getByCode(pvReqDto.getPvLogType()));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return new ApiResultObjectDto().builder()
                .result(resultMap)
                .resultCode(resultCode)
                .build();
    }

    /**
     * 쿠폰 보관함 상세 정보 로직
     * @param couponRepositoryId
     * @return
     */
    public ApiResultObjectDto getEventCouponRepositoryDetailLogic(long couponRepositoryId) {
        int resultCode = httpSuccessCode;
        CouponRepositoryResDto couponRepositoryResDto = new CouponRepositoryResDto();

        //쿠폰 저장 인덱스 번호가 없으면 에러처리
        if (PredicateUtils.isEqualZero((int)couponRepositoryId)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }

        if (PredicateUtils.isGreaterThanZero((int)couponRepositoryId)) {
            //쿠폰 상제 정보 가져오기
            CouponDetailInfoMapVO vo = arEventFrontService.findCouponDetailInfo(couponRepositoryId);
            if (PredicateUtils.isNotNull(vo.getNftCouponId())) {
                couponRepositoryResDto.setCouponDetailInfo(vo);
                //쿠폰 혜택정보 가져오기
                couponRepositoryResDto.setBenefitInfo(arEventService.findAllArEventNftBenefitByArEventWinningId(vo.getArEventWinningId()));
            }
        }
        return new ApiResultObjectDto().builder()
                .result(couponRepositoryResDto)
                .resultCode(resultCode)
                .build();
    }

    /**
     * 경품 정보 입력 시 패스워드를 받는 이벤트 인지 확인 로직
     * @param eventId
     * @return
     */
    public ApiResultObjectDto getCheckPasswordEventLogic(String eventId) {
        int resultCode = httpSuccessCode;

        Map<String, Boolean>resultMap = new HashMap<>();

        if (PredicateUtils.isNull(eventId)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        } else {
            ArEventEntity arEventEntity = arEventService.findArEventByEventId(eventId);
            if (PredicateUtils.isNotNull(arEventEntity)) {
                //패스워드를 받는 이벤트 일때
                if (PredicateUtils.isEqualsStr(arEventEntity.getWinningPasswordYn(), StringDefine.Y.name())) {
                    resultMap.put("isPassword", true);
                }
                //패스워드를 받는 이벤트가 아닐때
                if (PredicateUtils.isEqualsStr(arEventEntity.getWinningPasswordYn(), StringDefine.N.name()) || PredicateUtils.isNull(arEventEntity.getWinningPasswordYn())) {
                    resultMap.put("isPassword", false);
                }
            }
        }
        return new ApiResultObjectDto().builder()
                .result(resultMap)
                .resultCode(resultCode)
                .build();
    }

    /**
     * PV 로그 저장 로직
     * @param reqDto
     * @return
     */
    public ApiResultObjectDto savePvLogLogic(EventLogPvReqDto reqDto) {

        if (PredicateUtils.isNotNull(reqDto)) {
            if (StringUtils.isNotEmpty(reqDto.getPvLogType())) {
                logService.saveEventLogPv(reqDto, EventLogPvKeyDefine.getByCode(reqDto.getPvLogType()));
            }
        }
        return new ApiResultObjectDto().builder()
                .result(reqDto)
                .resultCode(httpSuccessCode)
                .build();
    }

    /**
     * SMS 인증 문자 발송하기
     * @param reqDto
     * @return
     */
    public ApiResultObjectDto sendSmsAuthLogic(SmsAuthReqDto reqDto) {
        int resultCode = httpSuccessCode;

        Map<String, String>resultMap = new HashMap<>();

        //파라미터 객체가 null 이면 에러처리
        if (PredicateUtils.isNull(reqDto)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        } else {
            //필수 파라미터가 없으면 에러처리
            if (PredicateUtils.isNull(reqDto.getEventId()) && PredicateUtils.isNull(reqDto.getPhoneNumber()) && PredicateUtils.isNull(reqDto.getAuthMenuType())) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
            } else {
                //sms 인증 발송 (1일) 개수 가져오기
                int sendCount = arEventFrontService.countSendWebEventSmsAuthByToday(reqDto.getPhoneNumber());
                //sms 인증 발송 개수가 3회가 넘으면 에러처리
                if (PredicateUtils.isGreaterThanEqualTo(sendCount, 3)) {
                    resultCode = ErrorCodeDefine.CUSTOM_ERROR_LIMIT_SMS_AUTH_SEND.code();
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

                } else {
                    WebEventBaseEntity webEventBase = arEventService.findEventBase(reqDto.getEventId());
                    try {
                        //SMS 인증 발송 난수 발생 후 저장
                        WebEventSmsAuthEntity savedSmsAuthEntity = arEventFrontService.saveWebEventSmsAuth(reqDto.getEventId(), reqDto.getPhoneNumber(), reqDto.getAuthMenuType());
                        if (PredicateUtils.isNotNull(savedSmsAuthEntity)) {
                            String smsMessage = "";
                            //메인페이지 일때
                            if (PredicateUtils.isEqualsStr(reqDto.getAuthMenuType(), SmsAuthMenuDefine.MAIN_ATTEND.name())) {
                                // AR 이벤트일때
                                if (PredicateUtils.isEqualsStr(webEventBase.getEventType(), EventTypeDefine.AR.name()) || PredicateUtils.isEqualsStr(webEventBase.getEventType(), EventTypeDefine.STAMP.name())) {
                                    smsMessage = SmsMessageDefine.MAIN_SMS_AUTH_AR.content().replace("{smsCode}", savedSmsAuthEntity.getAuthCode());
                                }
                                // 서베이고 이벤트일때
                                if (PredicateUtils.isEqualsStr(webEventBase.getEventType(), EventTypeDefine.SURVEY.name())) {
                                    smsMessage = SmsMessageDefine.MAIN_SMS_AUTH_SURVEY.content().replace("{smsCode}", savedSmsAuthEntity.getAuthCode());
                                }
                            }
                            //당첨조회 페이지 일때
                            if (PredicateUtils.isEqualsStr(reqDto.getAuthMenuType(), SmsAuthMenuDefine.WINNING_SEARCH.name())) {
                                smsMessage = SmsMessageDefine.WINNING_SEARCH_AUTH.content().replace("{smsCode}", savedSmsAuthEntity.getAuthCode());
                            }

                            try {
                                smsService.saveEmTranSms(aes256Utils.decrypt(reqDto.getPhoneNumber()), smsMessage, null, SmsTranTypeDefine.SMS.key());
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                                try {
                                    smsService.saveEmTranSms(aes256Utils.decrypt(reqDto.getPhoneNumber()), smsMessage, null, SmsTranTypeDefine.SMS.key());
                                } catch (Exception e2) {
                                    log.error(e2.getMessage());
                                    resultCode = ErrorCodeDefine.IOE_ERROR.code();
                                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                                }
                            }
                            //결과값 반환
                            resultMap.put("smsAuthCode", savedSmsAuthEntity.getSmsAuthCode());
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        resultCode = ErrorCodeDefine.IOE_ERROR.code();
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                    }
                }
            }
        }
        return new ApiResultObjectDto().builder()
                .result(resultMap)
                .resultCode(resultCode)
                .build();
    }

    /**
     * SMS 인증 코드 인증하기
     * @param reqDto
     * @return
     */
    public ApiResultObjectDto authSmsCodeLogic(SmsAuthReqDto reqDto) {
        int resultCode = httpSuccessCode;

        Map<String, Object>resultMap = new HashMap<>();

        if (PredicateUtils.isNull(reqDto)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        } else {
            WebEventSmsAuthEntity smsAuthEntity = new WebEventSmsAuthEntity();
            smsAuthEntity.setSmsAuthCode(reqDto.getSmsAuthCode());  //sms인증 pk 값
            smsAuthEntity.setAuthCode(reqDto.getAuthCode());    //문자로 발송받은 코드 값
            int sendCount = arEventFrontService.countWebEventSmsAuth(smsAuthEntity);
            //없으면
            if (PredicateUtils.isEqualNumber(sendCount, 0)) {
                //인증번호가 틀렸습니다 에러처리
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_SMS_AUTH_CODE.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
            }
            //있을때
            if (PredicateUtils.isEqualNumber(sendCount, 1)) {
                smsAuthEntity.setAuthExpireDate(DateUtils.returnNowDate());
                int expiredDateCount = arEventFrontService.countWebEventSmsAuth(smsAuthEntity);

                //인증시간 만료 에러처리
                if (PredicateUtils.isEqualNumber(expiredDateCount, 0)) {
                    resultCode = ErrorCodeDefine.CUSTOM_ERROR_EXPIRED_SMS_AUTH_TIME.code();
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                }
                //전부 통과되어 인증완료 처리
                if (PredicateUtils.isEqualNumber(expiredDateCount, 1)) {
                    WebEventSmsAuthEntity findEntity = arEventFrontService.findWebEventSmsAuthBySmsAuthCode(reqDto.getSmsAuthCode());
                    arEventFrontService.deleteWebEventSmsAuth(findEntity);

                    resultMap.put("smsAuthCode", reqDto.getSmsAuthCode());
                }
            }
        }
        return new ApiResultObjectDto().builder()
                .result(resultMap)
                .resultCode(resultCode)
                .build();
    }

    public ApiResultObjectDto getEventBasicInfoLogic(String eventId) {
        int resultCode = httpSuccessCode;

        EventBaseJoinArEventJoinEventButtonVO vo = new EventBaseJoinArEventJoinEventButtonVO();
        if (PredicateUtils.isNull(eventId)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        } else {
            vo = arEventFrontService.findArEventBaseInfoByEventId(eventId);
        }
        return new ApiResultObjectDto().builder()
                .result(vo)
                .resultCode(resultCode)
                .build();
    }

    public ApiResultObjectDto getEventLawContentsLogic(String eventType, String lawType) {
        String contents = arEventFrontService.findEventLawContentsByEventTypeAndLawType(eventType, lawType);
        List<HashMap<String, Object>> hyperMap = arEventFrontService.findEventLawHyperLinkList(eventType, lawType);

        EventLawContentsResDto resDto = new EventLawContentsResDto();
        resDto.setContents(contents);
        resDto.setHyperLinkInfo(hyperMap);

        return new ApiResultObjectDto().builder()
                .result(resDto)
                .resultCode(httpSuccessCode)
                .build();
    }


    public ApiResultObjectDto getEventLawContentsByIdxLogic(Integer idx) {
        EventLawInfoVO eventLawInfoVO = arEventFrontService.findEventLawContentsByIdx(idx);
        return new ApiResultObjectDto().builder()
                .result(eventLawInfoVO)
                .resultCode(httpSuccessCode)
                .build();
    }



    /**
     * 이벤트가 참여가능한 시간인지 체크
     * @param eventAttendTimeEntityList
     * @return
     */
    private boolean checkPossibleEventAttendTime(List<ArEventAttendTimeEntity> eventAttendTimeEntityList) {
        if (!PredicateUtils.isNullList(eventAttendTimeEntityList)) {
            for (ArEventAttendTimeEntity timeEntity : eventAttendTimeEntityList) {
                boolean isSection = PredicateUtils.isInTwoSections(timeEntity.getAttendStartHour(), Integer.parseInt(DateUtils.getNowHour()), (timeEntity.getAttendEndHour() - 1));
                //시간 리스트중에 하나라도 포함되어 있지않으면 참여불가능
                if (!isSection) {
                    return false;
                } else {
                //시간 리스트중에 하나라도 포함되어 있으면 참여가능
                    return true;
                }
            }
        }
        return true;
    }
}
