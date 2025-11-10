package kr.co.syrup.adreport.web.event.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.exception.BaseException;
import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.stamp.event.service.StampLogService;
import kr.co.syrup.adreport.stamp.event.service.StampSodarService;
import kr.co.syrup.adreport.survey.go.entity.SurveySubjectCategoryEntity;
import kr.co.syrup.adreport.survey.go.entity.SurveySubjectEntity;
import kr.co.syrup.adreport.survey.go.entity.SurveyTargetAgeGenderLimitEntity;
import kr.co.syrup.adreport.survey.go.service.SurveyEntityService;
import kr.co.syrup.adreport.survey.go.service.SurveyGoSodarService;
import kr.co.syrup.adreport.web.event.define.*;
import kr.co.syrup.adreport.web.event.dto.request.*;
import kr.co.syrup.adreport.web.event.dto.request.api.OcbPointSaveReqDto;
import kr.co.syrup.adreport.web.event.dto.response.*;
import kr.co.syrup.adreport.web.event.dto.response.api.OcbPointSaveResDto;
import kr.co.syrup.adreport.web.event.entity.*;
import kr.co.syrup.adreport.web.event.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Slf4j
@Repository
public class ArEventLogic {

    private final int httpSuccessCode = HttpStatus.OK.value();

    @Value("${web.event.domain}")
    private String webEventDomain;

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LogService logService;

    @Autowired
    private SurveyGoSodarService surveyGoSodarService;

    @Autowired
    private SurveyEntityService surveyEntityService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private SodarCommonLogic sodarCommonLogic;

    @Autowired
    private StampSodarService stampSodarService;

    /**
     * AR 이벤트 저장하기
     * @param jsonStr
     * @return
     */
    @Transactional
    public ApiResultObjectDto saveArEventLogic(String jsonStr, MultipartFile attendCodeExcelFile) {
        int resultCode = httpSuccessCode;

        //json string 인코딩 변경
        EventSaveDto eventSaveDto;
        Map<String, Object>resultMap = new HashMap<>();

        try {
            //json string 인코딩 변경
            eventSaveDto = objectMapper.readValue(jsonStr, EventSaveDto.class);
        } catch (JsonProcessingException jpe) {
            log.error(jpe.getMessage());
            throw new BaseException(ResultCodeEnum.JSON_PARSE_EXCEPTION_ERROR.getDesc(), ResultCodeEnum.JSON_PARSE_EXCEPTION_ERROR);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BaseException(ResultCodeEnum.STRING_ENCODING_EXCEPTION_ERROR.getDesc(), ResultCodeEnum.STRING_ENCODING_EXCEPTION_ERROR);
        }

        //eventSaveDto 있으면 로직 시작
        if (PredicateUtils.isNotNull(eventSaveDto)) {

            /**
             * EVENT_BASE 저장
             */
            int webEventSeq = arEventService.findWebEventSequence("web_event_seq");
            String eventId = arEventService.saveEventBase(WebEventBaseEntity.of(webEventSeq, eventSaveDto.getEventBaseInfo()));

            log.info("eventId {} ", eventId);

            if (PredicateUtils.isNotNull(eventId)) {
                /**
                 * AR_EVENT 저장
                 */
                int arEventId = arEventService.saveEvent(ArEventEntity.of(eventId, eventSaveDto.getArEventInfo()));
                resultMap.put("arEventId", arEventId);

                if (PredicateUtils.isGreaterThanZero(arEventId)) {
                    /**
                     * AR_EVENT_ATTEND_TIME 저장
                     */
                    if (PredicateUtils.isNotNull(eventSaveDto.getArEventInfo().getArEventAttendTimeInfo())) {
                        arEventService.saveAllEventAttendTime(arEventId, ModelMapperUtils.convertModelInList(eventSaveDto.getArEventInfo().getArEventAttendTimeInfo(), ArEventAttendTimeEntity.class));
                    }

                    //서베이고 일때 로직 (SS-20260)
                    if (PredicateUtils.isEqualsStr(eventSaveDto.getEventBaseInfo().getEventType(), EventTypeDefine.SURVEY.name())) {
                        //성/연령별 제한 저장
                        if (!PredicateUtils.isNullList(eventSaveDto.getArEventInfo().getGenderAgeLimitInfo())) {
                            surveyEntityService.saveAllTargetAgeGenderLimit(arEventId, eventSaveDto.getArEventInfo().getGenderAgeLimitInfo());
                        }
                    }

                    /**
                     * AR_EVENT_BUTTON 저장
                     */
                    if (PredicateUtils.isNotNull(eventSaveDto.getArEventButtonInfo())) {
                        arEventService.saveEventButton(ArEventButtonEntity.of(arEventId, eventSaveDto.getArEventButtonInfo()));
                    }

                    /**
                     * AR_EVENT_OBJECT 저장 (이미지스캐닝이 아날떄만)
                     */
                    if (PredicateUtils.isNotEqualsStr(EventLogicalTypeDefine.이미지스캐닝형.value(), eventSaveDto.getArEventInfo().getEventLogicalType())) {
                        if (PredicateUtils.isNotNull(eventSaveDto.getArEventObjectInfo())) {
                            List<ArEventObjectEntity> eventObjectEntityList = ModelMapperUtils.convertModelInList(eventSaveDto.getArEventObjectInfo(), ArEventObjectEntity.class);
                            eventObjectEntityList.forEach(entity -> entity.setArEventId(arEventId));

                            arEventService.saveAllArEventObject(eventObjectEntityList);
                        }
                    }

                    /**
                     * AR_EVENT_SCANNING_IMAGE(이미지스캔형일때만)
                     */
                    if (PredicateUtils.isEqualsStr(EventLogicalTypeDefine.이미지스캐닝형.value(), eventSaveDto.getArEventInfo().getEventLogicalType())) {
                        if (PredicateUtils.isNotNull(eventSaveDto.getArEventScanningImageInfo())) {
                            List<ArEventScanningImageEntity> arEventImageScanningEntityList = ModelMapperUtils.convertModelInList(eventSaveDto.getArEventScanningImageInfo(), ArEventScanningImageEntity.class);
                            arEventImageScanningEntityList.forEach(entity -> entity.setArEventId(arEventId));

                            arEventService.saveAllEventImageScanning(arEventImageScanningEntityList);
                        }
                    }

                    /**
                     * AR_EVENT_LOGICAL 저장
                     */
                    if (PredicateUtils.isNotNull(eventSaveDto.getArEventLogicalInfo())) {
                        arEventService.saveEventLogical(ArEventLogicalEntity.of(arEventId, eventSaveDto.getArEventLogicalInfo()));
                    }

                    //AR포토형일때 - DTWS-70
                    if (PredicateUtils.isEqualsStr(eventSaveDto.getEventBaseInfo().getEventType(), EventTypeDefine.PHOTO.name())) {
                        //AR_PHOTO_LOGICAL - 포토 로지컬 정보 저장
                        if (PredicateUtils.isNotNull(eventSaveDto.getPhotoLogicalInfo())) {
                            ArPhotoLogicalEntity saveArPhotoLogicalEntity = ModelMapperUtils.convertModel(eventSaveDto.getPhotoLogicalInfo(), ArPhotoLogicalEntity.class);
                            //해시태그 설정이 "Y" 일때
                            if (PredicateUtils.isEqualsStr(eventSaveDto.getPhotoLogicalInfo().getHashTagSettingYn(), StringDefine.Y.name())) {
                                //해시태그 값이 있을때 arrayList > string 변환
                                if (PredicateUtils.isNotNull(eventSaveDto.getPhotoLogicalInfo().getHashTagValue())) {
                                    String hashTagString = StringTools.listToString(eventSaveDto.getPhotoLogicalInfo().getHashTagValue(), ",");
                                    saveArPhotoLogicalEntity.setHashTagValue(hashTagString);
                                }
                            }
                            arEventService.saveArPhotoLogical(arEventId, saveArPhotoLogicalEntity);
                        }
                        //AR_PHOTO_CONTENTS - 포토 컨텐츠 정보 저장
                        if (PredicateUtils.isNotNull(eventSaveDto.getPhotoContentsInfo())) {
                            //프레임 컨텐츠 정보 저장
                            if (!PredicateUtils.isNullList(eventSaveDto.getPhotoContentsInfo().getFrameContentsInfo())) {
                                arEventService.saveAllArPhotoContents(arEventId, PhotoContentTypeDefine.FRAME.name(), ModelMapperUtils.convertModelInList(eventSaveDto.getPhotoContentsInfo().getFrameContentsInfo(), ArPhotoContentsEntity.class));
                            }
                            //탭 컨텐츠 정보 저장
                            if (!PredicateUtils.isNullList(eventSaveDto.getPhotoContentsInfo().getTabContentsInfo())) {
                                arEventService.saveAllArPhotoContents(arEventId, PhotoContentTypeDefine.TAB.name(), ModelMapperUtils.convertModelInList(eventSaveDto.getPhotoContentsInfo().getTabContentsInfo(), ArPhotoContentsEntity.class));
                            }
                            //필터 컨텐츠 정보 저장
                            if (!PredicateUtils.isNullList(eventSaveDto.getPhotoContentsInfo().getFilterContentsInfo())) {
                                arEventService.saveAllArPhotoContents(arEventId, PhotoContentTypeDefine.FILTER.name(), ModelMapperUtils.convertModelInList(eventSaveDto.getPhotoContentsInfo().getFilterContentsInfo(), ArPhotoContentsEntity.class));
                            }
                            //캐릭터 컨텐츠 정보 저장
                            if (!PredicateUtils.isNullList(eventSaveDto.getPhotoContentsInfo().getCharacterContentsInfo())) {
                                arEventService.saveAllArPhotoContents(arEventId, PhotoContentTypeDefine.CHARACTER.name(), ModelMapperUtils.convertModelInList(eventSaveDto.getPhotoContentsInfo().getCharacterContentsInfo(), ArPhotoContentsEntity.class));
                            }
                            //스티커 컨텐츠 정보 저장
                            if (!PredicateUtils.isNullList(eventSaveDto.getPhotoContentsInfo().getStickerContentsInfo())) {
                                arEventService.saveAllArPhotoContents(arEventId, PhotoContentTypeDefine.STICKER.name(), ModelMapperUtils.convertModelInList(eventSaveDto.getPhotoContentsInfo().getStickerContentsInfo(), ArPhotoContentsEntity.class));
                            }
                        }
                    }
                }

                /**
                 * AR_EVENT_WINNING, AR_EVENT_WINNING_BUTTON 저장
                 */
                //[DTWS-811]
                if (PredicateUtils.isNotNullList(eventSaveDto.getArEventWinningInfo())) {
                    Optional<EventWinningDto>optional = eventSaveDto.getArEventWinningInfo().stream()
                            .filter(eventWinningInfo -> PredicateUtils.isEqualsStr(eventWinningInfo.getWinningType(), WinningTypeDefine.NFT쿠폰.code()) || PredicateUtils.isEqualsStr(eventWinningInfo.getWinningType(), WinningTypeDefine.NFT.code()))
                            .filter(eventWinningInfo -> PredicateUtils.isNull(eventWinningInfo.getUploadFileSeqNum()))
                            .findAny();

                    if (optional.isPresent()) {
                        throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_SODAR_SAVE_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_SODAR_SAVE_ERROR);
                    }
                }

                try {
                    sodarCommonLogic.saveArEventWinning(eventSaveDto.getArEventWinningInfo(), arEventId, false);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

                /**
                 * AR_EVENT_HTML저장
                 */
                if (!PredicateUtils.isNullList(eventSaveDto.getArEventHtmlInfo())) {
                    //ar_event_html 안의 배너 정보 검증 시작
                    for (EventHtmlDto html : eventSaveDto.getArEventHtmlInfo()) {
                        //htmlType 이 버튼 있을때만 로직
                        if (PredicateUtils.isEqualsStr(html.getHtmlType(), "BUTTON")) {
                            //버튼 종류가 NFT or 쿠폰 or AR포토함이 아닐때 체크
                            if (!PredicateUtils.isEqualsStr(html.getHtmlButtonType(), HtmlButtonTypeDefine.NFTREPO.name())
                                    && !PredicateUtils.isEqualsStr(html.getHtmlButtonType(), HtmlButtonTypeDefine.CPREPO.name())
                                    && !PredicateUtils.isEqualsStr(html.getHtmlButtonType(), HtmlButtonTypeDefine.PHOTOREPO.name())) {
                                //배너정보가 있으면 에러처리
                                if (PredicateUtils.isNotNull(html.getArEventNftBannerInfo())) {
                                    if (PredicateUtils.isGreaterThanZero(html.getArEventNftBannerInfo().size())) {
                                        resultCode = Integer.parseInt(ResultCodeEnum.CUSTOM_EVENT_NFT_BANNER_REG_LIMIT.getCode());
                                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                                        throw new BaseException(ResultCodeEnum.CUSTOM_EVENT_NFT_BANNER_REG_LIMIT.getDesc(), ResultCodeEnum.CUSTOM_EVENT_NFT_BANNER_REG_LIMIT);
                                    }
                                }
                            }
                        }
                    }
                    //ar_event_html 안의 배너 정보 검증 끝

                    //ar_event_html 저장 시작
                    for (EventHtmlDto html : eventSaveDto.getArEventHtmlInfo()) {
                        //ar_event_html 저장
                        ArEventHtmlEntity returnHtmlEntity = arEventService.saveFirstEventHtmlByHtmlId(eventId, arEventId, 0, html, false);
                        //htmlType 이 버튼 있을때만 로직
                        if (PredicateUtils.isEqualsStr(returnHtmlEntity.getHtmlType(), "BUTTON")) {
                            //버튼 종류가 NFT or 쿠폰 or AR포토함 일때만 ar_event_nft_banner 저장
                            if (PredicateUtils.isEqualsStr(returnHtmlEntity.getHtmlButtonType(), HtmlButtonTypeDefine.NFTREPO.name())
                                    || PredicateUtils.isEqualsStr(returnHtmlEntity.getHtmlButtonType(), HtmlButtonTypeDefine.CPREPO.name())
                                    || PredicateUtils.isEqualsStr(returnHtmlEntity.getHtmlButtonType(), HtmlButtonTypeDefine.PHOTOREPO.name())) {
                                arEventService.saveAllArEventNftBanner(arEventId, returnHtmlEntity.getEventHtmlId(), html.getArEventNftBannerInfo(), false);
                            }
                        }
                        //DTWS-70 AR_EVENT_DEVICE_GPS 저장
                        if (!PredicateUtils.isNullList(html.getArEventDeviceGpsInfo())) {
                            arEventService.saveAllArEventDeviceGps(returnHtmlEntity.getEventHtmlId(), ModelMapperUtils.convertModelInList(html.getArEventDeviceGpsInfo(), ArEventDeviceGpsEntity.class));
                        }
                    }
                    //ar_event_html 저장 끝
                }

                if (PredicateUtils.isNotNull(attendCodeExcelFile)) {
                    //참여코드 저장
                    try {
                        sodarCommonLogic.saveAllAttendCode(attendCodeExcelFile, eventId, 0, eventSaveDto.getArEventInfo().getAttendCodeRegType(), eventSaveDto.getArEventInfo().getAttendCodeDigit(), eventSaveDto.getArEventInfo().getAttendCodeCount());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }

                //DTWS-70 OCB_POINT_SAVE 저장
                if (PredicateUtils.isNotNull(eventSaveDto.getOcbPointSaveInfo())) {
                    arEventService.saveOcbPointSave(arEventId, ModelMapperUtils.convertModel(eventSaveDto.getOcbPointSaveInfo(), OcbPointSaveEntity.class));
                }

                //폐기되어야 할 NFT_TOKEN, NFT_COUPON 데이터 삭제
                arEventService.deleteArEventNftCouponInfoByLegacy();
            } //end if (StringUtils.isNotEmpty(eventId))

            resultMap.put("eventId", eventId);

            return new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .result(resultMap)
                    .build();

        } //eventSaveDto 있으면 로직 끝

        return null;
    }

    @Transactional
    public ApiResultObjectDto updateArEventLogic(String eventId, String jsonStr, MultipartFile attendCodeExcelFile) {
        int resultCode = httpSuccessCode;

        EventSaveDto eventSaveDto = new EventSaveDto();
        int arEventId = 0;

        try {
            //jsonString >> EventSaveDto 클래스로 변환
            eventSaveDto = objectMapper.readValue(jsonStr, EventSaveDto.class);

        } catch (JsonProcessingException jpe) {
            log.error(jpe.getMessage());
            throw new BaseException(ResultCodeEnum.JSON_PARSE_EXCEPTION_ERROR.getDesc(), ResultCodeEnum.JSON_PARSE_EXCEPTION_ERROR);
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
            throw new BaseException(ResultCodeEnum.STRING_ENCODING_EXCEPTION_ERROR.getDesc(), ResultCodeEnum.STRING_ENCODING_EXCEPTION_ERROR);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BaseException(ResultCodeEnum.STRING_ENCODING_EXCEPTION_ERROR.getDesc(), ResultCodeEnum.STRING_ENCODING_EXCEPTION_ERROR);
        }

        if (PredicateUtils.isNotNull(eventSaveDto) && StringUtils.isNotEmpty(eventId)) {

            WebEventBaseEntity webEventBase = arEventService.findEventBase(eventId);

            //WEB_EVENT_BASE 없으면 에러처리
            if (PredicateUtils.isNull(webEventBase)) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_WEB_EVENT_BASE_NULL.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
            }

            //WEB_EVENT_BASE 있으면 로직 시작
            if (PredicateUtils.isNotNull(webEventBase)) {
                /**
                 * WEB_EVENT_BASE 수정
                 */
                arEventService.saveEventBase(WebEventBaseEntity.updateOf(webEventBase, eventId, eventSaveDto.getEventBaseInfo()));


                ArEventEntity arEventEntity = arEventService.findArEventByEventId(eventId);
                //AR_EVENT 가 null이면 에러처리
                if (PredicateUtils.isNull(arEventEntity)) {
                    resultCode = ErrorCodeDefine.CUSTOM_ERROR_AR_EVENT_INFO_NULL.code();
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                }
                //AR_EVENT 가 있으면 수정 로직 시작
                if (PredicateUtils.isNotNull(arEventEntity)) {
                    /**
                     * AR_EVENT 수정
                     */
                    //arEventService.saveEvent(ArEventEntity.updateOf(arEventEntity, eventId, eventSaveDto.getArEventInfo()));
                    arEventService.updateArEventByMapper(ArEventEntity.updateOf(arEventEntity, eventId, eventSaveDto.getArEventInfo()));
                    arEventId = arEventEntity.getArEventId();

                    /**
                     * AR_EVENT_ATTEND_TIME 삭제 후 저장
                     */
                    if (PredicateUtils.isNotNull(eventSaveDto.getArEventInfo().getArEventAttendTimeInfo())) {
                        arEventService.deleteArEventAttendTimeByArEventId(arEventEntity.getArEventId());
                        arEventService.saveAllEventAttendTime(arEventEntity.getArEventId(), ModelMapperUtils.convertModelInList(eventSaveDto.getArEventInfo().getArEventAttendTimeInfo(), ArEventAttendTimeEntity.class));
                    } else {
                        arEventService.deleteArEventAttendTimeByArEventId(arEventEntity.getArEventId());
                    }

                    //서베이고 일때 로직 (SS-20260)
                    if (PredicateUtils.isEqualsStr(eventSaveDto.getEventBaseInfo().getEventType(), EventTypeDefine.SURVEY.name())) {
                        //성/연령별 제한 수정 시작
                        if (PredicateUtils.isNotNullList(eventSaveDto.getArEventInfo().getGenderAgeLimitInfo())) {
                            //수정할 성/연령별 제한 데이터
                            List<SurveyTargetAgeGenderLimitEntity> updateEntityList = ModelMapperUtils.convertModelInList(eventSaveDto.getArEventInfo().getGenderAgeLimitInfo(), SurveyTargetAgeGenderLimitEntity.class);
                            //저장되어 있는 성/연령별 제한 데이터
                            List<SurveyTargetAgeGenderLimitEntity> savedEntityList  = surveyEntityService.findSurveyTargetAgeGenderLimitByArEventId(arEventEntity.getArEventId());
                            // 성/연령별 제한 저장, 삭제, 수정
                            surveyGoSodarService.updateSurveyTargetAgeGenderLimitFromSodar(updateEntityList, savedEntityList, arEventEntity.getArEventId());
                        }
                        //성/연령별 제한 수정 끝
                    }

                    /**
                     * AR_EVENT_BUTTON 수정
                     */
                    ArEventButtonEntity arEventButtonEntity = arEventService.findArEventButtonByArEventId(arEventEntity.getArEventId());
                    if (PredicateUtils.isNotNull(arEventButtonEntity)) {
                        arEventService.saveEventButton(ArEventButtonEntity.updateOf(arEventButtonEntity, eventSaveDto.getArEventButtonInfo()));
                    }

                    //AR일때만 수정 시작
                    if (PredicateUtils.isEqualsStr(eventSaveDto.getEventBaseInfo().getEventType(), EventTypeDefine.AR.name())) {
                        /**
                         * AR_EVENT_OBJECT 수정 (이미지스캐닝이 아날떄만)
                         */
                        if (!PredicateUtils.isEqualsStr(EventLogicalTypeDefine.이미지스캐닝형.value(), eventSaveDto.getArEventInfo().getEventLogicalType())) {
                            //AR_EVENT_OBJECT 삭제
                            List<ArEventObjectEntity> eventObjectEntityList = ModelMapperUtils.convertModelInList(eventSaveDto.getArEventObjectInfo(), ArEventObjectEntity.class);

                            eventObjectEntityList.stream()
                                    .filter(Objects::nonNull)
                                    .forEach(objectEntity -> {
                                        objectEntity.setArEventId(arEventEntity.getArEventId());

                                        if (PredicateUtils.isNull(objectEntity.getVideoPlayRepeatType())) {
                                            objectEntity.setVideoPlayRepeatType(StringDefine.N.name());
                                        }
                                        if (PredicateUtils.isNull(objectEntity.getLocationExposureControlType()) || StringUtils.isEmpty(objectEntity.getLocationExposureControlType())) {
                                            objectEntity.setLocationExposureControlType(StringDefine.N.name());
                                        }
                                        if (PredicateUtils.isNull(objectEntity.getMaxExposureType()) || StringUtils.isEmpty(objectEntity.getMaxExposureType())) {
                                            objectEntity.setMaxExposureType(StringDefine.N.name());
                                            objectEntity.setMaxExposureCount(0);
                                        }
                                        if (PredicateUtils.isNull(objectEntity.getDayExposureType()) || StringUtils.isEmpty(objectEntity.getDayExposureType())) {
                                            objectEntity.setDayExposureType(StringDefine.N.name());
                                            objectEntity.setDayExposureCount(0);
                                        }
                                        if (PredicateUtils.isNull(objectEntity.getHourExposureType()) || StringUtils.isEmpty(objectEntity.getHourExposureType())) {
                                            objectEntity.setHourExposureType(StringDefine.N.name());
                                            objectEntity.setHourExposureCount(0);
                                        }
                                        if (PredicateUtils.isNull(objectEntity.getAttendCodeExposureType()) || StringUtils.isEmpty(objectEntity.getAttendCodeExposureType())) {
                                            objectEntity.setAttendCodeExposureType(StringDefine.N.name());
                                            objectEntity.setAttendCodeLimitType(0);
                                        }
                                        if (PredicateUtils.isNull(objectEntity.getLocationExposureControlType()) || StringUtils.isEmpty(objectEntity.getLocationExposureControlType())) {
                                            objectEntity.setLocationExposureControlType(StringDefine.N.name());
                                        }
                                        if (PredicateUtils.isNull(objectEntity.getExposurePercentType()) || StringUtils.isEmpty(objectEntity.getExposurePercentType())) {
                                            objectEntity.setExposurePercentType(StringDefine.N.name());
                                        }
                                        if (PredicateUtils.isNull(objectEntity.getBridgeExposureTimeType()) || StringUtils.isEmpty(objectEntity.getBridgeExposureTimeType())) {
                                            objectEntity.setBridgeExposureTimeType(StringDefine.N.name());
                                        }
                                        if (PredicateUtils.isNull(objectEntity.getBridgeExposureTimeSecond())) {
                                            objectEntity.setBridgeExposureTimeSecond(0);
                                        }
                                        objectEntity.setCreatedDate(DateUtils.returnNowDate());
                                    });

                            //저정되어 있는 오브젝트 리스트 가져오기
                            List<ArEventObjectEntity> exitsArEventObjectEntityList = arEventService.findArEventObjectListByArEventId(arEventEntity.getArEventId());

                            if (PredicateUtils.isNotNullList(exitsArEventObjectEntityList)) {
                                //업데이트 하는 오브젝트 개수와 저장되어있는 오브젝트 개수 체크(저장되어있는 오브젝트 개수가 많으면 업데이트 하는 오브젝트 외 항목은 삭제) - 프론트에서 오브젝트가 삭제 되었을때
                                if (PredicateUtils.isGreaterThanEqualTo(exitsArEventObjectEntityList.size(), eventObjectEntityList.size())) {
                                    //저장할 데이터와 DB에 있는 데이터를 비교해서 같은 데이터는 삭제
                                    eventObjectEntityList.forEach(newObject -> {
                                        if (PredicateUtils.isNotNull(newObject.getArEventObjectId())) {
                                            ArEventObjectEntity savedEntity = arEventService.findArEventObjectById(newObject.getArEventObjectId());
                                            if (PredicateUtils.isNotNull(savedEntity)) {
                                                exitsArEventObjectEntityList.removeIf(data -> data.getArEventObjectId() == savedEntity.getArEventObjectId());
                                            }
                                        }
                                    });
                                    //삭제해아할 이벤트 오브젝트 추출 후 삭제
                                    if (PredicateUtils.isNotNullList(exitsArEventObjectEntityList)) {
                                        exitsArEventObjectEntityList.forEach(removeObject -> {
                                            arEventService.deleteArEventObjectById(removeObject.getArEventObjectId());
                                        });
                                    }
                                } // end if
                            } // end if

                            //노출 제한 테이블 삭제 작업 시작
                            if (PredicateUtils.isNotNullList(eventObjectEntityList)) {
                                for (ArEventObjectEntity objectEntity : eventObjectEntityList) {

                                    if (PredicateUtils.isNotNull(objectEntity.getArEventObjectId())) {

                                        ArEventObjectEntity selectedObjectEntity = arEventService.findArEventObjectById(objectEntity.getArEventObjectId());

                                        if (PredicateUtils.isNotNull(objectEntity)) {

                                            if (PredicateUtils.isNull(objectEntity.getExposureControlType()))
                                                objectEntity.setExposureControlType(StringDefine.N.name());

                                            if (PredicateUtils.isEqualY(objectEntity.getExposureControlType())) {

                                                if (PredicateUtils.isNull(objectEntity.getMaxExposureCount()))
                                                    objectEntity.setMaxExposureCount(0);
                                                if (PredicateUtils.isNull(selectedObjectEntity.getMaxExposureCount()))
                                                    selectedObjectEntity.setMaxExposureCount(0);
                                                //수정할려는 총개수가 과거 개수 보다 크면 삭제 처리
                                                if (PredicateUtils.isGreaterThan(objectEntity.getMaxExposureCount(), selectedObjectEntity.getMaxExposureCount())) {
                                                    logService.deleteEventLogExposureLimitByArEventIdAndCode(arEventEntity.getArEventId(), StringTools.joinStringsNoSeparator(String.valueOf(arEventEntity.getArEventId()), String.valueOf(objectEntity.getObjectSort())), EventLogExposureLimitDefine.ID_SORT.name());
                                                }

                                                if (PredicateUtils.isNull(objectEntity.getDayExposureCount()))
                                                    objectEntity.setDayExposureCount(0);
                                                if (PredicateUtils.isNull(selectedObjectEntity.getDayExposureCount()))
                                                    selectedObjectEntity.setDayExposureCount(0);
                                                //수정할려는 일개수가 과거 개수 보다 크면 삭제 처리
                                                if (PredicateUtils.isGreaterThan(objectEntity.getDayExposureCount(), selectedObjectEntity.getDayExposureCount())) {
                                                    logService.deleteEventLogExposureLimitByArEventIdAndCode(arEventEntity.getArEventId(), StringTools.joinStringsNoSeparator(String.valueOf(arEventEntity.getArEventId()), String.valueOf(objectEntity.getObjectSort())), EventLogExposureLimitDefine.ID_SORT_TODAY.name());
                                                }

                                                if (PredicateUtils.isNull(objectEntity.getHourExposureCount()))
                                                    objectEntity.setHourExposureCount(0);
                                                if (PredicateUtils.isNull(selectedObjectEntity.getHourExposureCount()))
                                                    selectedObjectEntity.setHourExposureCount(0);
                                                //수정할려는 시간개수가 과거 개수 보다 크면 삭제 처리
                                                if (PredicateUtils.isGreaterThan(objectEntity.getHourExposureCount(), selectedObjectEntity.getHourExposureCount())) {
                                                    logService.deleteEventLogExposureLimitByArEventIdAndCode(arEventEntity.getArEventId(), StringTools.joinStringsNoSeparator(String.valueOf(arEventEntity.getArEventId()), String.valueOf(objectEntity.getObjectSort())), EventLogExposureLimitDefine.ID_SORT_HOUR.name());
                                                }
                                            } else {
                                                logService.deleteEventLogExposureLimitByArEventIdAndCode(arEventEntity.getArEventId(), StringTools.joinStringsNoSeparator(String.valueOf(arEventEntity.getArEventId()), String.valueOf(objectEntity.getObjectSort())), EventLogExposureLimitDefine.ID_SORT.name());
                                                logService.deleteEventLogExposureLimitByArEventIdAndCode(arEventEntity.getArEventId(), StringTools.joinStringsNoSeparator(String.valueOf(arEventEntity.getArEventId()), String.valueOf(objectEntity.getObjectSort())), EventLogExposureLimitDefine.ID_SORT_TODAY.name());
                                                logService.deleteEventLogExposureLimitByArEventIdAndCode(arEventEntity.getArEventId(), StringTools.joinStringsNoSeparator(String.valueOf(arEventEntity.getArEventId()), String.valueOf(objectEntity.getObjectSort())), EventLogExposureLimitDefine.ID_SORT_HOUR.name());
                                            }

                                            if (PredicateUtils.isNull(objectEntity.getAttendCodeExposureType()))
                                                objectEntity.setAttendCodeExposureType(StringDefine.N.name());
                                            //참여코드 제한일때
                                            if (PredicateUtils.isEqualY(objectEntity.getAttendCodeExposureType())) {

                                                if (PredicateUtils.isNull(objectEntity.getAttendCodeLimitType()))
                                                    objectEntity.setAttendCodeLimitType(0);
                                                //전체기한내일때
                                                if (PredicateUtils.isEqualZero(objectEntity.getAttendCodeLimitType())) {
                                                    //수정할려는 개수가 과거 개수 보다 크면 삭제 처리
                                                    if (PredicateUtils.isGreaterThan(objectEntity.getAttendCodeExposureCount(), selectedObjectEntity.getAttendCodeExposureCount())) {
                                                        logService.deleteEventLogExposureLimitByArEventIdAndCode(arEventEntity.getArEventId(), StringTools.joinStringsNoSeparator(String.valueOf(arEventEntity.getArEventId()), String.valueOf(objectEntity.getObjectSort())), EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                    }
                                                }
                                                //1일일때
                                                if (PredicateUtils.isGreaterThanZero(objectEntity.getAttendCodeLimitType())) {
                                                    //수정할려는 개수가 과거 개수 보다 크면 삭제 처리
                                                    if (PredicateUtils.isGreaterThan(objectEntity.getAttendCodeExposureCount(), selectedObjectEntity.getAttendCodeExposureCount())) {
                                                        logService.deleteEventLogExposureLimitByArEventIdAndCode(arEventEntity.getArEventId(), StringTools.joinStringsNoSeparator(String.valueOf(arEventEntity.getArEventId()), String.valueOf(objectEntity.getObjectSort())), EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                                    }
                                                }

                                            } else {
                                                //참여코드 제한이 아니면 참여코드 제한 테이블 전체 삭제처리
                                                logService.deleteEventLogExposureLimitByArEventIdAndCode(arEventEntity.getArEventId(), StringTools.joinStringsNoSeparator(String.valueOf(arEventEntity.getArEventId()), String.valueOf(objectEntity.getObjectSort())), EventLogExposureLimitDefine.ID_SORT_CODE.name());
                                                logService.deleteEventLogExposureLimitByArEventIdAndCode(arEventEntity.getArEventId(), StringTools.joinStringsNoSeparator(String.valueOf(arEventEntity.getArEventId()), String.valueOf(objectEntity.getObjectSort())), EventLogExposureLimitDefine.ID_SORT_CODE_TODAY.name());
                                            }
                                        }
                                    }
                                }
                            }
                            //노출 제한 테이블 삭제 작업 끝

                            arEventService.saveAllArEventObject(eventObjectEntityList);

                        } else {
                            /**
                             * AR_EVENT_SCANNING_IMAGE 수정 (이미지스캔형일때만)
                             */
                            if (PredicateUtils.isNotNullList(eventSaveDto.getArEventScanningImageInfo())) {
                                //저장되어 있는 스캐닝 이미지 리스트 가져오기
                                List<ArEventScanningImageEntity> exitsArEventScanningImageList = arEventService.findArEventScanningImageListByArEventId(arEventEntity.getArEventId());
                                if (PredicateUtils.isNotNullList(exitsArEventScanningImageList)) {
                                    //업데이트 하는 이미지 스캐닝 개수와 저장되어있는 이미지 스캐닝 개수 체크(저장되어있는 이미지 스캐닝 개수가 많으면 업데이트 하는 이미지 스캐닝 항목은 삭제) - 프론트에서 이미지 스캐닝이 삭제 되었을때
                                    if (PredicateUtils.isGreaterThanEqualTo(exitsArEventScanningImageList.size(), eventSaveDto.getArEventScanningImageInfo().size())) {
                                        //저장할 데이터와 DB에 있는 데이터를 비교해서 같은 데이터는 삭제
                                        eventSaveDto.getArEventScanningImageInfo().forEach(newObj -> {
                                            if (PredicateUtils.isNotNull(newObj.getArEventScanningImageId())) {
                                                ArEventScanningImageEntity savedImageEntity = arEventService.findArEventScanningImageById(newObj.getArEventScanningImageId());
                                                if (PredicateUtils.isNotNull(savedImageEntity)) {
                                                    exitsArEventScanningImageList.removeIf(data -> data.getArEventScanningImageId() == savedImageEntity.getArEventScanningImageId());
                                                }
                                            }
                                        });
                                        //삭제해야 할 스캐닝 이미지 삭제
                                        if (PredicateUtils.isNotNullList(exitsArEventScanningImageList)) {
                                            exitsArEventScanningImageList.forEach(removeObj -> {
                                                arEventService.deleteArEventScanningImageById(removeObj.getArEventScanningImageId());
                                            });
                                        }
                                    }   // end if
                                }   // end if

                                List<ArEventScanningImageEntity> arEventImageScanningEntityList = ModelMapperUtils.convertModelInList(eventSaveDto.getArEventScanningImageInfo(), ArEventScanningImageEntity.class);

                                arEventImageScanningEntityList
                                        .stream()
                                        .filter(Objects::nonNull)
                                        .forEach(entity -> {
                                            entity.setArEventId(arEventEntity.getArEventId());
                                            entity.setCreatedDate(DateUtils.returnNowDate());
                                        });

                                arEventService.saveAllEventImageScanning(arEventImageScanningEntityList);
                            }
                        }


                        /**
                         * AR_EVENT_LOGICAL 수정
                         */
                        if (PredicateUtils.isNotNull(eventSaveDto.getArEventLogicalInfo())) {
                            ArEventLogicalEntity arEventLogicalEntity = arEventService.findArEventLogicalByArEventId(arEventEntity.getArEventId());
                            //ArEventLogical 데이터가 있으면 UPDATE
                            if (PredicateUtils.isNotNull(arEventLogicalEntity)) {
                                if (PredicateUtils.isNull(eventSaveDto.getArEventLogicalInfo().getBridgeExposureTimeType())) {
                                    arEventLogicalEntity.setBridgeExposureTimeType(StringDefine.Y.name());
                                }
                                if (PredicateUtils.isNull(eventSaveDto.getArEventLogicalInfo().getBridgeExposureTimeSecond())) {
                                    eventSaveDto.getArEventLogicalInfo().setBridgeExposureTimeSecond(0);
                                }
                                arEventService.saveEventLogical(ArEventLogicalEntity.updateOf(arEventLogicalEntity, eventSaveDto.getArEventLogicalInfo()));
                            }
                            //ArEventLogical 데이터가 없으면 INSERT
                            if (PredicateUtils.isNull(arEventLogicalEntity)) {
                                arEventService.saveEventLogical(ArEventLogicalEntity.of(arEventEntity.getArEventId(), eventSaveDto.getArEventLogicalInfo()));
                            }
                        }
                    //AR일때만 수정 끝
                    } else if (PredicateUtils.isEqualsStr(eventSaveDto.getEventBaseInfo().getEventType(), EventTypeDefine.PHOTO.name())) {
                    //AR포토형일때 - DTWS-70
                        //AR_PHOTO_LOGICAL - 포토 로지컬 정보 수정
                        if (PredicateUtils.isNotNull(eventSaveDto.getPhotoLogicalInfo())) {
                            ArPhotoLogicalEntity updateArPhotoLogicalEntity = ModelMapperUtils.convertModel(eventSaveDto.getPhotoLogicalInfo(), ArPhotoLogicalEntity.class);
                            //해시태그 설정이 "Y" 일때
                            if (PredicateUtils.isEqualY(eventSaveDto.getPhotoLogicalInfo().getHashTagSettingYn())) {
                                //해시태그 값이 있을때 arrayList > string 변환
                                if (PredicateUtils.isNotNull(eventSaveDto.getPhotoLogicalInfo().getHashTagValue())) {
                                    String hashTagString = StringTools.listToString(eventSaveDto.getPhotoLogicalInfo().getHashTagValue(), ",");
                                    updateArPhotoLogicalEntity.setHashTagValue(hashTagString);
                                }
                            }
                            arEventService.saveArPhotoLogical(arEventId, updateArPhotoLogicalEntity);
                        }
                        //AR_PHOTO_CONTENTS - 포토 컨텐츠 정보 수정
                        if (PredicateUtils.isNotNull(eventSaveDto.getPhotoContentsInfo())) {
                            //프레임 컨텐츠 정보 수정
                            if (PredicateUtils.isNotNullList(eventSaveDto.getPhotoContentsInfo().getFrameContentsInfo())) {
                                arEventService.saveAllArPhotoContents(arEventId, PhotoContentTypeDefine.FRAME.name(), ModelMapperUtils.convertModelInList(eventSaveDto.getPhotoContentsInfo().getFrameContentsInfo(), ArPhotoContentsEntity.class));
                            }
                            //탭 컨텐츠 정보 수정
                            if (PredicateUtils.isNotNullList(eventSaveDto.getPhotoContentsInfo().getTabContentsInfo())) {
                                arEventService.saveAllArPhotoContents(arEventId, PhotoContentTypeDefine.TAB.name(), ModelMapperUtils.convertModelInList(eventSaveDto.getPhotoContentsInfo().getTabContentsInfo(), ArPhotoContentsEntity.class));
                            }
                            //필터 컨텐츠 정보 수정
                            if (PredicateUtils.isNotNullList(eventSaveDto.getPhotoContentsInfo().getFilterContentsInfo())) {
                                arEventService.saveAllArPhotoContents(arEventId, PhotoContentTypeDefine.FILTER.name(), ModelMapperUtils.convertModelInList(eventSaveDto.getPhotoContentsInfo().getFilterContentsInfo(), ArPhotoContentsEntity.class));
                            }
                            //캐릭터 컨텐츠 정보 수정
                            if (PredicateUtils.isNotNullList(eventSaveDto.getPhotoContentsInfo().getCharacterContentsInfo())) {
                                arEventService.saveAllArPhotoContents(arEventId, PhotoContentTypeDefine.CHARACTER.name(), ModelMapperUtils.convertModelInList(eventSaveDto.getPhotoContentsInfo().getCharacterContentsInfo(), ArPhotoContentsEntity.class));
                            }
                            //스티커 컨텐츠 정보 수정
                            if (PredicateUtils.isNotNullList(eventSaveDto.getPhotoContentsInfo().getStickerContentsInfo())) {
                                arEventService.saveAllArPhotoContents(arEventId, PhotoContentTypeDefine.STICKER.name(), ModelMapperUtils.convertModelInList(eventSaveDto.getPhotoContentsInfo().getStickerContentsInfo(), ArPhotoContentsEntity.class));
                            }
                        }

                    }

                    /**
                     * AR_EVENT_WINNING, AR_EVENT_WINNING_BUTTON 수정
                     */
                    //[DTWS-811]
                    if (PredicateUtils.isNotNullList(eventSaveDto.getArEventWinningInfo())) {

                        Optional<EventWinningDto>optional = eventSaveDto.getArEventWinningInfo().stream()
                                .filter(eventWinningInfo -> PredicateUtils.isEqualsStr(eventWinningInfo.getWinningType(), WinningTypeDefine.NFT쿠폰.code()) || PredicateUtils.isEqualsStr(eventWinningInfo.getWinningType(), WinningTypeDefine.NFT.code()))
                                .filter(eventWinningInfo -> PredicateUtils.isNull(eventWinningInfo.getArEventWinningId()) && PredicateUtils.isNull(eventWinningInfo.getUploadFileSeqNum()))
                                .findAny();

                        if (optional.isPresent()) {
                            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_SODAR_SAVE_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_SODAR_SAVE_ERROR);
                        }
                    }
                    //당첨정보, 당첨버튼정보 수정하기
                    try {
                        sodarCommonLogic.updateArEventWinning(eventSaveDto.getArEventWinningInfo(), arEventId, false);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    /**
                     * AR_EVENT_HTML 저장
                     */
                    try {
                        sodarCommonLogic.updateArEventHtml(eventSaveDto.getArEventHtmlInfo(), eventId, arEventId, 0, false);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }

                    //참여코드 엑셀파일 추출 후 저장하기 시작
                    if (PredicateUtils.isNotNull(attendCodeExcelFile)) {
                        //AR_EVENT_GATE_CODE 삭제
                        arEventService.deleteArEventGateCodeByEventId(eventId);

                        //신규 저장
                        List<ArEventGateCodeEntity> arEventGateCodeEntityList = new ArrayList<>();
                        List<Map<String, Object>> attendCodeList = excelService.extractionAttendCodeByExcelFile(attendCodeExcelFile, "ATTEND");
                        attendCodeList.forEach(attendCodeMap -> {
                            ArEventGateCodeEntity gateCodeEntity = new ArEventGateCodeEntity();
                            gateCodeEntity.setEventId(eventId);
                            gateCodeEntity.setAttendCode(String.valueOf(attendCodeMap.get("A")));
                            gateCodeEntity.setUseYn(false);
                            gateCodeEntity.setUsedCount(0);

                            arEventGateCodeEntityList.add(gateCodeEntity);
                        });
                        arEventService.saveAllArEventGateCode(arEventGateCodeEntityList);
                    }//참여코드 엑셀파일 추출 후 저장하기 끝

                    //DTWS-70 OCB_POINT_SAVE 수정
                    if (PredicateUtils.isNotNull(eventSaveDto.getOcbPointSaveInfo())) {
                        arEventService.saveOcbPointSave(arEventId, ModelMapperUtils.convertModel(eventSaveDto.getOcbPointSaveInfo(), OcbPointSaveEntity.class));
                    }

                }//AR_EVENT 가 있으면 수정 로직 끝

            }//WEB_EVENT_BASE 있으면 로직 끝

            //캐시 삭제
            cacheService.clearAllCache();
            //버전 업데이트
            cacheService.updateCacheableInfo();
            //소다 업데이트할때 내용 저장
            arEventService.saveSodarEventUpdateHistory(eventId, jsonStr);
            //폐기되어야 할 NFT_TOKEN, NFT_COUPON 데이터 삭제
            arEventService.deleteArEventNftCouponInfoByLegacy();

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("eventId", eventId);
            resultMap.put("arEventId", arEventId);

            return new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .result(resultMap)
                    .build();
        }
        return null;
    }

    /**
     * AR 이벤트 상세 정보 가져오기
     * @param eventId
     * @return
     */
    public ApiResultObjectDto getArEventDetailLogic(String eventId) {
        int resultCode = httpSuccessCode;

        ArEventDetailResDto arEventDetailResDto = null;

        //이벤트 아이디가 없으면 에러코드 처리
        if (StringUtils.isEmpty(eventId)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

        } else {
            //EVENT_BASE_INFO 정보 가져오기
            WebEventBaseEntity webEventBaseInfo = arEventService.findEventBaseNoCache(eventId);
            // AR_EVENT 가져오기
            ArEventResDto arEventResDto = arEventService.findArEventByEventIdOfResDto(eventId);

            //AR_EVENT 정보가 없으면 에러코드 처리
            if (PredicateUtils.isNull(arEventResDto)) {

                resultCode = ErrorCodeDefine.CUSTOM_ERROR_AR_EVENT_INFO_NULL.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            } else {

                int arEventId = arEventResDto.getArEventId();
                //AR_EVENT_ATTEND_TIME 정보
                arEventResDto.setArEventAttendTimeInfo(arEventService.findAllArEventAttendTimeByArEventId(arEventId));
                //AR_EVENT_BUTTON 정보
                ArEventButtonEntity arEventButtonEntity = arEventService.findArEventButtonByArEventId(arEventId);

                List<ArEventObjectEntity> arEventObjectEntityList = new ArrayList<>();
                ArEventLogicalEntity arEventLogicalEntity = new ArEventLogicalEntity();
                List<ArEventScanningImageEntity> arEventScanningImageEntityList = new ArrayList<>();

                PhotoLogicalResDto photoLogicalResDto = new PhotoLogicalResDto();
                PhotoContentsListReqDto photoContentsInfo = new PhotoContentsListReqDto();

                //이벤트 종류가 AR일때
                if (PredicateUtils.isEqualsStr(EventTypeDefine.AR.name(), webEventBaseInfo.getEventType())) {
                    //AR_EVENT_OBJECT 정보
                    arEventObjectEntityList = arEventService.findAllArEventObjectByArEventId(arEventId);

                    //AR_EVENT_LOGICAL 가져오기
                    arEventLogicalEntity = arEventService.findArEventLogicalByArEventId(arEventId);
                    if (PredicateUtils.isNotNull(arEventLogicalEntity)) {
                        if (PredicateUtils.isNull(arEventLogicalEntity.getBridgeExposureTimeType()) || PredicateUtils.isEqualN(arEventLogicalEntity.getBridgeExposureTimeType())) {
                            arEventLogicalEntity.setBridgeExposureTimeType(StringDefine.N.name());
                            arEventLogicalEntity.setBridgeExposureTimeSecond(0);
                        }
                    }

                    //오브젝트 종류가 스캐닝이 AR_EVENT_SCANNING_IMAGE 정보 주입하기
                    if (EventLogicalTypeDefine.이미지스캐닝형.value().equals(arEventResDto.getEventLogicalType())) {
                        arEventScanningImageEntityList = arEventService.findAllArEventScanningImageByEventId(arEventId);
                    }
                //이벤트 종류가 포토형일때
                } else if (PredicateUtils.isEqualsStr(EventTypeDefine.PHOTO.name(), webEventBaseInfo.getEventType())) {
                    //AR_PHOTO_LOGICAL 정보 가져오기
                    ArPhotoLogicalEntity arPhotoLogical = arEventService.findArPhotoLogicalByArEventId(arEventId);
                    if (PredicateUtils.isNotNull(arPhotoLogical.getId())) {
                        photoLogicalResDto = ModelMapperUtils.convertModel(arPhotoLogical, PhotoLogicalResDto.class);

                        //해시태그 있으면 해시태그 리스트를 문자열로 변환
                        if (PredicateUtils.isNotNull(arPhotoLogical.getHashTagValue())) {
                            List<String> hashTagValueList = new ArrayList<>();
                            hashTagValueList.addAll(StringTools.stringToList(arPhotoLogical.getHashTagValue(), ","));
                            photoLogicalResDto.setHashTagValue(hashTagValueList);
                        }
                    }

                    //AR_PHOTO_CONTENTS 정보 가져오기
                    List<ArPhotoContentsEntity> list = arEventService.findArPhotoContentsByArEventId(arEventId);
                    if (!PredicateUtils.isNullList(list)) {
                        //트리맵 구조로 그룹핑
                        TreeMap<String, List<ArPhotoContentsEntity>> treeMap = list.stream()
                                .sorted(Comparator.comparing(ArPhotoContentsEntity::getSort))
                                .collect(Collectors.groupingBy(ArPhotoContentsEntity::getPhotoContentType, TreeMap::new, Collectors.toList()));

                        treeMap.forEach((key, value) -> {
                            if (PredicateUtils.isEqualsStr(key, PhotoContentTypeDefine.FRAME.name())) {
                                photoContentsInfo.setFrameContentsInfo(ModelMapperUtils.convertModelInList(value, PhotoContentsReqDto.class));
                            }
                            if (PredicateUtils.isEqualsStr(key, PhotoContentTypeDefine.TAB.name())) {
                                photoContentsInfo.setTabContentsInfo(ModelMapperUtils.convertModelInList(value, PhotoContentsReqDto.class));
                            }
                            if (PredicateUtils.isEqualsStr(key, PhotoContentTypeDefine.FILTER.name())) {
                                photoContentsInfo.setFilterContentsInfo(ModelMapperUtils.convertModelInList(value, PhotoContentsReqDto.class));
                            }
                            if (PredicateUtils.isEqualsStr(key, PhotoContentTypeDefine.CHARACTER.name())) {
                                photoContentsInfo.setCharacterContentsInfo(ModelMapperUtils.convertModelInList(value, PhotoContentsReqDto.class));
                            }
                            if (PredicateUtils.isEqualsStr(key, PhotoContentTypeDefine.STICKER.name())) {
                                photoContentsInfo.setStickerContentsInfo(ModelMapperUtils.convertModelInList(value, PhotoContentsReqDto.class));
                            }
                        });
                    }
                }

                //AR_EVENT_WINNING 정보
                List<ArEventWinningResDto> arEventWinningResDtoList = arEventService.findAllArEventWinningByArEventIdOfResDto(arEventId);

                if (!PredicateUtils.isNullList(arEventWinningResDtoList)) {
                    //AR_EVENT_WINNING_BUTTON 정보 주입
                    arEventWinningResDtoList
                            .stream()
                            .filter(Objects::nonNull)
                            .forEach(resDto -> {
                                resDto.setNftBenefitInfo(
                                        arEventService.findAllArEventNftBenefitByArEventWinningId(resDto.getArEventWinningId())
                                );

                                List<ArEventWinningButtonResDto> arEventWinningButtonEntityList = ModelMapperUtils.convertModelInList(
                                        arEventService.findAllArEventWinningButtonByArEventWinningId(resDto.getArEventWinningId()), ArEventWinningButtonResDto.class
                                );

                                if (!PredicateUtils.isNullList(arEventWinningButtonEntityList)) {
                                    resDto.setArEventWinningButtonInfo(arEventWinningButtonEntityList);
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
//                                        winningButtonResDto.setArEventWinningButtonAddInfo(
//                                                ModelMapperUtils.convertModelInList(
//                                                        arEventService.findAllArEventWinningButtonAddByArEventWinningButtonId(winningButtonResDto.getArEventWinningButtonId()), ArEventWinningButtonAddResDto.class
//                                                )
//                                        );
                                    }
                                }

                                //서베이고 추가 (SS-20260) - 당첨 텍스트 정보 주입
                                resDto.setWinningTextInfo(
                                        arEventService.findAllArEventWinningTextByArWinningId(resDto.getArEventWinningId())
                                );
                                //서베이고 추가 (SS-20260) - 보관함 정보 주입 (쿠폰, NFT 일때 데이터)
                                resDto.setRepositoryButtonInfo(
                                        arEventService.findAllArEventRepositoryButtonByArWinningId(resDto.getArEventWinningId())
                                );
                                //NFT 일때
                                if (PredicateUtils.isEqualsStr(resDto.getWinningType(), WinningTypeDefine.NFT.code())) {
                                    resDto.setNftTokenCount(
                                            arEventService.countArEventNftTokenByArEventWinningId(resDto.getArEventWinningId())
                                    );
//                                    resDto.setNftExcelUploadFileName(
//                                            arEventService.findDistinctNftTokenInfoByArEventWinningId(resDto.getArEventWinningId()).getUploadExcelFileName()
//                                    );
                                }
                                //NFT 쿠폰 일때
                                if (PredicateUtils.isEqualsStr(resDto.getWinningType(), WinningTypeDefine.NFT쿠폰.code())) {
                                    resDto.setNftTokenCount(
                                            arEventService.countArEventNftCouponByArEventWinningId(resDto.getArEventWinningId())
                                    );
//                                    resDto.setNftExcelUploadFileName(
//                                            arEventService.findDistinctNftCouponInfoByArEventWinningId(resDto.getArEventWinningId()).getUploadExcelFileName()
//                                    );
                                }
                                //당첨정보의 OCB 포인트 저장 정보 가져오기
                                if (PredicateUtils.isEqualsStr(resDto.getWinningType(), WinningTypeDefine.OCB포인트.code())) {
                                    OcbPointSaveResDto ocbPointSaveResDto = ModelMapperUtils.convertModel(
                                            arEventService.findOcbPointSaveByArEventIdAndArEventWinningId(arEventId, resDto.getArEventWinningId()), OcbPointSaveResDto.class
                                    );
                                    resDto.setOcbPointSaveInfo(ocbPointSaveResDto);
                                }
                            });
                }
                //AR_EVENT_HTML 정보
                List<ArEventHtmlResDto> arEventHtmlList = arEventService.findAllArEventHtmlResDtoByArEventId(arEventId);
                if (!PredicateUtils.isNullList(arEventHtmlList)) {
                    for (ArEventHtmlResDto htmlInfo : arEventHtmlList) {
                        //AR_EVENT_NFT_BANNER 정보
                        htmlInfo.setArEventNftBannerInfo(arEventService.findArEventNftBannerByEventHtmlId(htmlInfo.getEventHtmlId()));
                        //AR_EVENT_DEVICE_GPS 정보
                        htmlInfo.setArEventDeviceGpsInfo(
                                ModelMapperUtils.convertModelInList(arEventService.findAllArEventDeviceGpsByEventHtmlId(htmlInfo.getEventHtmlId()), EventDeviceGpsResDto.class)
                        );
                    }
                }

                /**
                 * 서베이고 일때 문항, 보기 관련 데이터 가져오기
                 */
                List<SurveySubjectResDto> surveySubjectResDtoList = new ArrayList<>();
                List<SurveySubjectCategoryEntity> surveySubjectCategoryList = new ArrayList<>();

                if (PredicateUtils.isEqualsStr(webEventBaseInfo.getEventType(), EventTypeDefine.SURVEY.name())) {
                    //성/연령별 제한 정보
                    arEventResDto.setGenderAgeLimitInfo(surveyGoSodarService.findSurveyTargetAgeGenderLimitListByArEventId(arEventId));

                    //문항 정보
                    List<SurveySubjectEntity> surveySubjectList = surveyEntityService.findAllSurveySubjectByArEventIdOrderBySortAsc(arEventId);
                    if (!PredicateUtils.isNullList(surveySubjectList)) {

                        for (SurveySubjectEntity subjectEntity : surveySubjectList) {
                            SurveySubjectResDto subjectResDto = new SurveySubjectResDto();
                            long surveySubjectId = subjectEntity.getSurveySubjectId();
                            //문항 정보 주입
                            subjectResDto = ModelMapperUtils.convertModel(subjectEntity, SurveySubjectResDto.class);
                            //문항 - 주관식 정보 주입
                            subjectResDto.setExampleQuestionInfo(
                                    surveyEntityService.findAllSurveyExampleQuestionBySurveySubjectIdOrderBySortAsc(surveySubjectId)
                            );
                            //보기 정보 주입
                            subjectResDto.setExampleInfo(
                                    surveyEntityService.findAllSurveyExampleBySurveySubjectIdOrderBySortAsc(surveySubjectId)
                            );
                            //팝업 이미지 정보 주입
                            subjectResDto.setPopupImageInfo(
                                    surveyEntityService.findAllSurveySubjectPopupImageBySurveySubjectIdOrderBySortAsc(surveySubjectId)
                            );
                            surveySubjectResDtoList.add(subjectResDto);
                        }
                    }
                    //서베이고 카테고리 정보
                    surveySubjectCategoryList = surveyEntityService.findAllSurveySubjectCategoryByArEventIdOrderBySortAsc(arEventId);
                }

                OcbPointSaveResDto ocbPointSaveInfo = ModelMapperUtils.convertModel(
                        arEventService.findOcbPointSaveByArEventIdAndArEventWinningId(arEventId, null), OcbPointSaveResDto.class
                );

                //통계 뷰페이지 url 정보
                String staticsViewUrlInfo = FileUtils.concatPath("https://", webEventDomain, "web-event", "statics.html?eventId=" + eventId);
                //이벤트 메인 뷰페이지 url 정보
                String eventViewUrlInfo = FileUtils.concatPath("https://", webEventDomain, "web-event", "main-preview.html?eventId=" + eventId);
                //이벤트 메인 라이브 url 정보
                String realViewUrlInfo = FileUtils.concatPath("https://", webEventDomain, "web-event", "main.html?eventId=" + eventId);;

                arEventDetailResDto = new ArEventDetailResDto().builder()
                        .eventBaseInfo(webEventBaseInfo) //WEB_EVENT_BASE 정보
                        .arEventInfo(arEventResDto) //AR_EVENT 정보
                        .arEventButtonInfo(arEventButtonEntity) //AR_EVENT_BUTTON
                        .arEventObjectInfo(arEventObjectEntityList) //AR_EVENT_OBJECT
                        .arEventLogicalInfo(arEventLogicalEntity)   //AR_EVENT_LOGICAL
                        .arEventScanningImageInfo(arEventScanningImageEntityList)   //AR_EVENT_SCANNING_IMAGE
                        .arEventWinningInfo(arEventWinningResDtoList)   //AR_EVENT_WINNING
                        .arEventHtmlInfo(arEventHtmlList)   //AR_EVENT_HTML
                        .surveySubjectInfo(surveySubjectResDtoList)
                        .surveySubjectCategoryInfo(surveySubjectCategoryList)
                        .photoLogicalInfo(photoLogicalResDto)
                        .photoContentsInfo(photoContentsInfo)
                        .ocbPointSaveInfo(ocbPointSaveInfo)
                        .previewEventUrlInfo(eventViewUrlInfo)
                        .staticsViewUrlInfo(staticsViewUrlInfo)
                        .realEventUrlInfo(realViewUrlInfo)
                        .build();


            }
        }
        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(arEventDetailResDto)
                .build();
    }

    /**
     * 계약 승인 알림 API, 계약 상태 변경 알림 API, 승인후 계약 변경 API 로직
     * @param reqDto
     * @param modifyReqDto
     * @param afterReqDto
     * @return
     */
    @Transactional
    public SodaApiResultObjectDto contractStatusLogic(ContractAccessPushReqDto reqDto, ContractModifyReqDto modifyReqDto, ContractAfterReqDto afterReqDto) {
        int resultCode = httpSuccessCode;

        WebEventBaseEntity webEventBaseEntity = new WebEventBaseEntity();
        WebEventBaseEntity contractUpdateEntity = new WebEventBaseEntity();

        /**
         * 계약 승인 알림 API 일떄
         */
        if (PredicateUtils.isNotNull(reqDto) && PredicateUtils.isNull(modifyReqDto) && PredicateUtils.isNull(afterReqDto)) {
            log.info("=====================  계약 승인 알림 API ===========================");

            webEventBaseEntity = arEventService.findEventBase(reqDto.getServiceSolutionId());

            if (PredicateUtils.isNotNull(reqDto.getMarketingInfo().getMarketingId())) {
                log.info("marketingId >>>> {}", reqDto.getMarketingInfo().getMarketingId());
            }
            if (PredicateUtils.isNull(reqDto.getMarketingInfo().getMarketingId())) {
                log.info("marketingId is null");
            }

            contractUpdateEntity = WebEventBaseEntity.contractUpdateOf(
                    webEventBaseEntity, reqDto.getServiceSolutionId(), reqDto.getMarketingInfo().getMarketingId(), reqDto.getContractInfo().getContractStatus(), reqDto.getMarketingInfo().getServiceEndDate(), reqDto.getContractInfo().getUserId()
            );

        }
        /**
         *  계약 상태 변경 알림 API 일때
         */
        if (PredicateUtils.isNull(reqDto) && PredicateUtils.isNotNull(modifyReqDto) && PredicateUtils.isNull(afterReqDto)) {
            log.info("=====================  계약 상태 변경 알림 API ===========================");
            webEventBaseEntity = arEventService.findEventBase(modifyReqDto.getServiceSolutionId());

            //계약코드가 06일때 종료일 보다 이전일때만 서비스 종료일 업데이트
            //String realServiceEndDate = "";
            if (StringUtils.equals(modifyReqDto.getContractStatus(), ContractStatusDefine.서비스종료.code())) {
                webEventBaseEntity.setRealEventEndDate(DateUtils.convertDateTimeFormat3(modifyReqDto.getRealServiceEndDate()));
            }
            //[DTWS-406] 스탬프 반려일때 TR 목록 삭제
            if (EventTypeDefine.isStampEvent(webEventBaseEntity.getEventType())) {
                if (PredicateUtils.isEqualsStr(modifyReqDto.getContractStatus(), ContractStatusDefine.반려.code())) {
                    stampSodarService.rejectStampEventPanTrByRel(modifyReqDto.getServiceSolutionId());
                }
            }
            contractUpdateEntity = WebEventBaseEntity.contractUpdateOf(
                    webEventBaseEntity, modifyReqDto.getServiceSolutionId(), modifyReqDto.getMarketingId(), modifyReqDto.getContractStatus(), "", webEventBaseEntity.getLastModifiedBy()
            );

        }
        /**
         *  승인후 계약 변경 알림
         */
        if (PredicateUtils.isNull(reqDto) && PredicateUtils.isNull(modifyReqDto) && PredicateUtils.isNotNull(afterReqDto)) {
            log.info("=====================  승인후 계약 변경 알림 API ===========================");
            webEventBaseEntity = arEventService.findEventBase(afterReqDto.getServiceSolutionId());

            contractUpdateEntity = WebEventBaseEntity.contractAfterOf(
                    webEventBaseEntity, afterReqDto.getServiceSolutionId(), afterReqDto.getMarketingId(), afterReqDto.getMarketingInfo().getServiceEndDate()
            );
        }

        //WEB_EVENT_BASE 예외처리
        if (PredicateUtils.isNull(webEventBaseEntity)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }

        if (PredicateUtils.isNotNull(webEventBaseEntity)) {
            arEventService.saveEventBase(contractUpdateEntity);
        }

        return new SodaApiResultObjectDto().builder().resultCode(resultCode).build();
    }
}
