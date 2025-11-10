package kr.co.syrup.adreport.survey.go.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.syrup.adreport.framework.utils.ModelMapperUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.survey.go.define.ExampleTypeDefine;
import kr.co.syrup.adreport.survey.go.dto.request.*;
import kr.co.syrup.adreport.survey.go.entity.*;
import kr.co.syrup.adreport.survey.go.service.SurveyEntityService;
import kr.co.syrup.adreport.survey.go.service.SurveyGoSodarService;
import kr.co.syrup.adreport.web.event.define.ErrorCodeDefine;
import kr.co.syrup.adreport.web.event.dto.request.EventSaveDto;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.logic.ArEventLogic;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import kr.co.syrup.adreport.web.event.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class SurveyGoSodarLogic {

    private final int httpSuccessCode = HttpStatus.OK.value();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArEventLogic arEventLogic;

    @Autowired
    private SurveyGoSodarService surveyGoSodarService;

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private SurveyEntityService surveyEntityService;

    @Autowired
    private CacheService cacheService;

    /**
     * 서베이고 SODAR 데이터 저장하기 로직
     * @param jsonStr
     * @param excelFile
     * @return
     */
    @Transactional
    public ApiResultObjectDto saveSurveyGoLogic(String jsonStr, MultipartFile excelFile) {
        int resultCode = httpSuccessCode;
        Map<String, Object>resultMap = new HashMap<>();

        //AR 이벤트와 같이 쓰는 공통 데이터 저장하기
        ApiResultObjectDto arEventSaveResult = arEventLogic.saveArEventLogic(jsonStr, excelFile);

        //이벤트 기본 저장 시 에러가 발생되었을 때 에러처리
        if (PredicateUtils.isNull(arEventSaveResult)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_SODAR_SAVE_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }

        if (PredicateUtils.isNotNull(arEventSaveResult)) {
            //saveArEventLogic 정상 통신인지 결과 코드 확인
            int arEventResultCd = arEventSaveResult.getResultCode();

            //이벤트 기본 저장 시 결과 코드가 200이 아니면 에러처리
            if (!PredicateUtils.isEqualNumber(arEventResultCd, httpSuccessCode)) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_SODAR_SAVE_ERROR.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
            }
            //이벤트 기본 저장 시 결과 코드가 200이면 서베이고 데이터 저장 시작
            if (PredicateUtils.isEqualNumber(arEventResultCd, httpSuccessCode)) {
                //saveArEventLogic 통신 결과 값이 200 이면 로직 처리
                /*
                 * 서베이고 관련 로직 추가 (문항, 답 관련 데이터)
                 */
                Map<String, Object> saveResultMap = (Map<String, Object>)arEventSaveResult.getResult();
                String eventId = (String)saveResultMap.get("eventId");
                int arEventId = (int)saveResultMap.get("arEventId");

                if (PredicateUtils.isEqualZero(arEventId)) {
                    resultCode = ErrorCodeDefine.CUSTOM_ERROR_AR_EVENT_INFO_NULL.code();
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                }

                if (PredicateUtils.isGreaterThanZero(arEventId)) {
                    resultMap.put("eventId", eventId);
                    resultMap.put("arEventId", arEventId);

                    EventSaveDto sodarSaveReqDto;

                    try {
                        sodarSaveReqDto = objectMapper.readValue(jsonStr, EventSaveDto.class);
                    } catch (JsonProcessingException jpe) {
                        log.error(jpe.getMessage());
                        resultCode = ErrorCodeDefine.JSON_PARSE_EXCEPTION_ERROR.code();
                        return new ApiResultObjectDto().builder()
                                .resultCode(resultCode)
                                .build();
                    }

                    try {
                        //문항 정보 저장 시작
                        if (!PredicateUtils.isNullList(sodarSaveReqDto.getSurveySubjectInfo())) {
                            for (SurveySubjectSodarReqDto subjectSodarReqDto : sodarSaveReqDto.getSurveySubjectInfo()) {
                                //문항 정보 저장
                                SurveySubjectEntity savedSubjectEntity = surveyEntityService.saveSurveySubjectByReqDto(arEventId, subjectSodarReqDto);

                                if (PredicateUtils.isNotNull(savedSubjectEntity)) {
                                    long subjectId = savedSubjectEntity.getSurveySubjectId();

                                    //보기 정보 저장 시작
                                    if (!PredicateUtils.isNullList(subjectSodarReqDto.getExampleInfo())) {
                                        for (SubjectExampleSodarReqDto exampleSodarReqDto : subjectSodarReqDto.getExampleInfo()) {
                                            //보기 정보 저장
                                            surveyEntityService.saveSurveyExampleByReqDto(subjectId, exampleSodarReqDto);
                                        }
                                    }
                                    //보기 정보 저장 끝

                                    //문항 -> 주관식 정보 저장 (주관식일때만)
                                    if (PredicateUtils.isEqualsStr(subjectSodarReqDto.getSubjectExampleType(), ExampleTypeDefine.QUESTION.name())) {
                                        surveyEntityService.saveAllSurveyExampleQuestionByReqDtoList(savedSubjectEntity.getSurveySubjectId(), subjectSodarReqDto.getExampleQuestionInfo());
                                    }

                                    //문항 팝업 이미지 정보 저장 시작
                                    if (!PredicateUtils.isNullList(subjectSodarReqDto.getPopupImageInfo())) {
                                        surveyEntityService.saveAllSurveySubjectPopupImageByReqDto(savedSubjectEntity.getSurveySubjectId(), subjectSodarReqDto.getPopupImageInfo());
                                    }
                                }
                            }
                        }
                        //문항 정보 저장 끝

                        //유형 정보 저장 시작
                        if (!PredicateUtils.isNullList(sodarSaveReqDto.getSurveySubjectCategoryInfo())) {
                            surveyEntityService.saveAllSurveySubjectCategoryByReqDto(arEventId, sodarSaveReqDto.getSurveySubjectCategoryInfo());
                        }
                        //유형 정보 저장 끝

                        //시퀀스 값 저장
                        arEventService.saveSequences(StringTools.joinStringsNoSeparator(eventId, "_", "SURVEY"), 0L);

                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        resultCode = ErrorCodeDefine.IOE_ERROR.code();
                    }
                }
            }
            //서베이고 데이터 저장 끝
        }

        return new ApiResultObjectDto().builder()
                .result(resultMap)
                .resultCode(resultCode)
                .build();
    }

    //@Transactional
    public ApiResultObjectDto updateSurveyGoLogic(String eventId, String jsonStr, MultipartFile excelFile) {
        int resultCode = httpSuccessCode;

        //AR 이벤트와 같이 쓰는 공통 데이터 업데이트
        ApiResultObjectDto arEventUpdateResult = arEventLogic.updateArEventLogic(eventId, jsonStr, excelFile);

        //이벤트 기본 저장 시 에러가 발생되었을 때 에러처리
        if (PredicateUtils.isNull(arEventUpdateResult)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_SODAR_SAVE_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }

        if (PredicateUtils.isNotNull(arEventUpdateResult)) {
            //saveArEventLogic 정상 통신인지 결과 코드 확인
            int arEventResultCd = arEventUpdateResult.getResultCode();

            //이벤트 기본 저장 시 결과 코드가 200이 아니면 에러처리
            if (!PredicateUtils.isEqualNumber(arEventResultCd, httpSuccessCode)) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_SODAR_SAVE_ERROR.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
            }
            //이벤트 수정 시 결과 코드가 200이면 서베이고 데이터 수정 시작 START
            if (PredicateUtils.isEqualNumber(arEventResultCd, httpSuccessCode)) {
                Map<String, Object> saveResultMap = (Map<String, Object>)arEventUpdateResult.getResult();
                int arEventId = (int)saveResultMap.get("arEventId");

                EventSaveDto sodarSaveReqDto = new EventSaveDto();
                try {
                    sodarSaveReqDto = objectMapper.readValue(jsonStr, EventSaveDto.class);
                } catch (Exception e) {
                    resultCode = ErrorCodeDefine.JSON_PARSE_EXCEPTION_ERROR.code();
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                    return new ApiResultObjectDto().builder()
                            .resultCode(resultCode)
                            .build();
                }

                try {
                    //문항 정보 수정 시작
                    if (!PredicateUtils.isNullList(sodarSaveReqDto.getSurveySubjectInfo())) {
                        List<SurveySubjectSodarReqDto> reqSurveySubjectInfo = sodarSaveReqDto.getSurveySubjectInfo();

                        //신규 저장건 선언
                        List<SurveySubjectSodarReqDto> saveSurveySubjectList = new ArrayList<>();
                        //수정건 선언
                        List<SurveySubjectSodarReqDto> updateSurveySubjectList = new ArrayList<>();
                        //저장되어있는 문항 리스트
                        List<SurveySubjectEntity> savedSubjectEntityList = surveyEntityService.findAllSurveySubjectByArEventId(arEventId);

                        //문항 신규/수정/삭제 건 추출작업 시작
                        for (SurveySubjectSodarReqDto dto : reqSurveySubjectInfo) {
                            //신규 저장 건
                            if (PredicateUtils.isNull(dto.getSurveySubjectId())) {
                                saveSurveySubjectList.add(dto);
                            } else {
                                Optional<SurveySubjectEntity> findEntity = savedSubjectEntityList.stream()
                                        .filter(subjectEntity -> Objects.equals(subjectEntity.getSurveySubjectId(), dto.getSurveySubjectId()))
                                        .findFirst();

                                if (findEntity.isPresent()) {
                                    updateSurveySubjectList.add(dto);
                                    savedSubjectEntityList.removeIf(subjectEntity -> Objects.equals(subjectEntity.getSurveySubjectId(), dto.getSurveySubjectId()));
                                } else {
                                    saveSurveySubjectList.add(dto);
                                }
                            }
                        }
                        //문항 신규/수정/삭제 건 추출작업 끝

                        /*
                         * 1. 문항 삭제 로직
                         */
                        if (!PredicateUtils.isNullList(savedSubjectEntityList)) {
                            List<Long> surveySubjectIdList = savedSubjectEntityList.stream().map(SurveySubjectEntity::getSurveySubjectId).collect(Collectors.toList());
                            surveyEntityService.deleteSurveySubjectIndexIn(surveySubjectIdList);
                            surveyEntityService.deleteSurveyExampleBySubjectSubjectIdIn(surveySubjectIdList);
                            surveyEntityService.deleteSurveyExampleQuestionBySurveySubjectIdIn(surveySubjectIdList);
                            surveyEntityService.deleteSurveySubjectPopupImageBySurveySubjectIdIn(surveySubjectIdList);
                        }

                        /*
                         * 2. 문항 신규 저장 로직
                         */
                        if (!PredicateUtils.isNullList(saveSurveySubjectList)) {
                            for (SurveySubjectSodarReqDto subjectSodarReqDto : saveSurveySubjectList) {
                                //2-1. 문항 정보 저장
                                SurveySubjectEntity savedSubjectEntity = surveyEntityService.saveSurveySubjectByReqDto(arEventId, subjectSodarReqDto);
                                if (PredicateUtils.isNotNull(savedSubjectEntity)) {
                                    long subjectId = savedSubjectEntity.getSurveySubjectId();

                                    //2-2. 보기 정보 신규 저장 시작
                                    if (!PredicateUtils.isNullList(subjectSodarReqDto.getExampleInfo())) {
                                        for (SubjectExampleSodarReqDto exampleSodarReqDto : subjectSodarReqDto.getExampleInfo()) {
                                            //보기 정보 저장
                                            surveyEntityService.saveSurveyExampleByReqDto(subjectId, exampleSodarReqDto);
                                        }
                                    }
                                    //보기 정보 저장 끝

                                    //2-3. 문항 -> 주관식 정보 저장 (주관식일때만)
                                    if (PredicateUtils.isEqualsStr(subjectSodarReqDto.getSubjectExampleType(), ExampleTypeDefine.QUESTION.name())) {
                                        surveyEntityService.saveAllSurveyExampleQuestionByReqDtoList(subjectId, subjectSodarReqDto.getExampleQuestionInfo());
                                    }

                                    //문항 팝업 이미지 정보 저장 시작
                                    if (!PredicateUtils.isNullList(subjectSodarReqDto.getPopupImageInfo())) {
                                        surveyEntityService.saveAllSurveySubjectPopupImageByReqDto(savedSubjectEntity.getSurveySubjectId(), subjectSodarReqDto.getPopupImageInfo());
                                    }
                                }
                            }
                        }

                        /*
                         * 3. 문항 수정 로직 시작
                         */
                        if (!PredicateUtils.isNullList(updateSurveySubjectList)) {

                            for (SurveySubjectSodarReqDto subjectSodarReqDto : updateSurveySubjectList) {
                                //3-1. 문항 정보 수정
                                surveyGoSodarService.updateSurveySubject(SurveySubjectEntity.updateOf(subjectSodarReqDto));

                                List<SubjectExampleSodarReqDto> reqSurveyExampleInfo = subjectSodarReqDto.getExampleInfo();

                                //보기 정보 저장 시작
                                if (!PredicateUtils.isNullList(subjectSodarReqDto.getExampleInfo())) {

                                    //신규 저장건 선언
                                    List<SubjectExampleSodarReqDto> saveSurveyExampleList = new ArrayList<>();
                                    //수정건 선언
                                    List<SubjectExampleSodarReqDto> updateSurveyExampleList = new ArrayList<>();
                                    //저장되어있는 문항 리스트
                                    List<SurveyExampleEntity> savedSurveyExampleList = surveyEntityService.findAllSurveyExampleBySurveySubjectId(subjectSodarReqDto.getSurveySubjectId());

                                    //보기 신규/수정/삭제 건 추출작업 시작
                                    for (SubjectExampleSodarReqDto reqDto : reqSurveyExampleInfo) {
                                        if (PredicateUtils.isNull(reqDto.getSurveyExampleId())) {
                                            saveSurveyExampleList.add(reqDto);
                                        } else {
                                            Optional<SurveyExampleEntity> findEntity = savedSurveyExampleList.stream()
                                                    .filter(exampleEntity -> Objects.equals(exampleEntity.getSurveyExampleId(), reqDto.getSurveyExampleId()))
                                                    .findAny();

                                            if (findEntity.isPresent()) {
                                                updateSurveyExampleList.add(reqDto);
                                                savedSurveyExampleList.removeIf(exampleEntity -> Objects.equals(exampleEntity.getSurveyExampleId(), reqDto.getSurveyExampleId()));
                                            } else {
                                                saveSurveyExampleList.add(reqDto);
                                            }
                                        }
                                    }
                                    //보기 신규/수정/삭제 건 추출작업 끝

                                    /*
                                     * 3-1. 보기 삭제 로직
                                     */
                                    if (!PredicateUtils.isNullList(savedSurveyExampleList)) {
                                        surveyEntityService.deleteSurveyExampleIndexIn(
                                                savedSurveyExampleList.stream().map(SurveyExampleEntity::getSurveyExampleId).collect(Collectors.toList())
                                        );
                                    }

                                    /*
                                     * 3-2. 보기 신규 로직
                                     */
                                    //보기 정보 저장 시작
                                    if (!PredicateUtils.isNullList(saveSurveyExampleList)) {
                                        for (SubjectExampleSodarReqDto exampleSodarReqDto : saveSurveyExampleList) {
                                            //보기 정보 저장
                                            surveyEntityService.saveSurveyExampleByReqDto(subjectSodarReqDto.getSurveySubjectId(), exampleSodarReqDto);
                                        }
                                    }
                                    //보기 정보 저장 끝

                                    /*
                                     * 보기 수정 로직
                                     */

                                    for (SubjectExampleSodarReqDto exampleSodarReqDto : updateSurveyExampleList) {
                                        //보기 정보 저장
                                        surveyGoSodarService.updateSurveyExample(SurveyExampleEntity.updateOf(exampleSodarReqDto));
                                    }
                                }
                                //보기 정보 저장 끝

                                // 주관식 정보 수정 (주관식일때만)
                                if (PredicateUtils.isEqualsStr(subjectSodarReqDto.getSubjectExampleType(), ExampleTypeDefine.QUESTION.name())) {
                                    List<SurveyExampleQuestionEntity> savedList = surveyEntityService.findAllSurveyExampleQuestionBySurveySubjectId(subjectSodarReqDto.getSurveySubjectId());

                                    // DTWS-236 주관식 정답 관련 데이터가 있을때만 주관식 정답 데이터 업데이트
                                    if(!PredicateUtils.isNull(subjectSodarReqDto.getExampleQuestionInfo())) {
                                        surveyGoSodarService.updateSurveyExampleQuestionFromSodar(
                                                ModelMapperUtils.convertModelInList(subjectSodarReqDto.getExampleQuestionInfo(), SurveyExampleQuestionEntity.class), savedList, subjectSodarReqDto.getSurveySubjectId()
                                        );
                                    }

                                    //주관식으로 수정할때 보기의 객관식 데이터가 있으면 삭제하기
                                    surveyEntityService.deleteSurveyExampleBySurveySubjectId(subjectSodarReqDto.getSurveySubjectId());

                                } else if (PredicateUtils.isEqualsStr(subjectSodarReqDto.getSubjectExampleType(), ExampleTypeDefine.CHOICE.name())) {
                                    //객관식으로 수정할때 주관식 정답 데이터가 있으면 삭제하기
                                    surveyEntityService.deleteSurveyExampleQuestionBySubjectSubjectId(subjectSodarReqDto.getSurveySubjectId());
                                }

                                //문항 팝업 이미지 정보 수정 시작
                                if (!PredicateUtils.isNullList(subjectSodarReqDto.getPopupImageInfo())) {
                                    List<SurveySubjectPopupImageEntity> savedList = surveyEntityService.findAllSurveySubjectPopupImageBySurveySubjectId(subjectSodarReqDto.getSurveySubjectId());
                                    surveyGoSodarService.updateSurveySubjectPopupImageFromSodar(
                                            ModelMapperUtils.convertModelInList(subjectSodarReqDto.getPopupImageInfo(), SurveySubjectPopupImageEntity.class), savedList, subjectSodarReqDto.getSurveySubjectId()
                                    );
                                }
                            }
                        }
                        //3. 문항 수정 로직 끝
                    }

                    //유형 정보 수정 시작
                    if (!PredicateUtils.isNullList(sodarSaveReqDto.getSurveySubjectCategoryInfo())) {

                        List<SurveySubjectCategoryReqDto> categoryReqDtoList = sodarSaveReqDto.getSurveySubjectCategoryInfo();
                        //신규 저장건 선언
                        List<SurveySubjectCategoryReqDto> saveSurveySubjectCategoryList = new ArrayList<>();
                        //수정건 선언
                        List<SurveySubjectCategoryReqDto> updateSurveySubjectCategoryList = new ArrayList<>();
                        //저장되어있는 문항 유형 리스트
                        List<SurveySubjectCategoryEntity> savedSurveySubjectCategoryList = surveyEntityService.findAllSurveySubjectCategoryByArEventId(arEventId);

                        //유형 신규/수정/삭제 건 추출작업 시작
                        for (SurveySubjectCategoryReqDto dto : categoryReqDtoList) {
                            //신규 저장 건
                            if (PredicateUtils.isNull(dto.getSurveySubjectCategoryId())) {
                                saveSurveySubjectCategoryList.add(dto);
                            } else {
                                Optional<SurveySubjectCategoryEntity> findEntity = savedSurveySubjectCategoryList.stream()
                                        .filter(subjectEntity -> Objects.equals(subjectEntity.getSurveySubjectCategoryId(), dto.getSurveySubjectCategoryId()))
                                        .findAny();

                                if (findEntity.isPresent()) {
                                    updateSurveySubjectCategoryList.add(dto);
                                    savedSurveySubjectCategoryList.removeIf(subjectEntity -> Objects.equals(subjectEntity.getSurveySubjectCategoryId(), dto.getSurveySubjectCategoryId()));
                                }
                            }
                        }
                        //유형 신규/수정/삭제 건 추출작업 끝

                        /*
                         * 삭제
                         */
                        if (!PredicateUtils.isNullList(savedSurveySubjectCategoryList)) {
                            surveyEntityService.deleteSurveySubjectCategoryIndexIn(
                                    savedSurveySubjectCategoryList.stream().map(SurveySubjectCategoryEntity::getSurveySubjectCategoryId).collect(Collectors.toList())
                            );
                        }

                        /*
                         * 신규 저장
                         */
                        if (!PredicateUtils.isNullList(saveSurveySubjectCategoryList)) {
                            surveyEntityService.saveAllSurveySubjectCategoryByReqDto(arEventId, saveSurveySubjectCategoryList);
                        }

                        /*
                         * 수정
                         */
                        if (!PredicateUtils.isNullList(updateSurveySubjectCategoryList)) {
                            for (SurveySubjectCategoryReqDto reqDto : updateSurveySubjectCategoryList) {
                                surveyGoSodarService.updateSurveySubjectCategory(SurveySubjectCategoryEntity.updateOf(reqDto));
                            }
                        }
                    }   //유형 정보 끝
                    //문항 정보 저장 끝
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    resultCode = ErrorCodeDefine.IOE_ERROR.code();
                }
            }
            //이벤트 수정 시 결과 코드가 200이면 서베이고 데이터 수정 시작 END
        }
        //캐시 삭제
        cacheService.clearAllCache();
        //버전 업데이트
        cacheService.updateCacheableInfo();

        if (resultCode != 200) {
            arEventUpdateResult.setResultCode(resultCode);
        }
        return arEventUpdateResult;
    }

    @Transactional
    public ApiResultObjectDto copySurveyGoLogic(String jsonStr, MultipartFile excelFile) {
        int resultCode = httpSuccessCode;
        Map<String, Object>resultMap = new HashMap<>();

        //AR 이벤트와 같이 쓰는 공통 데이터 저장하기
        ApiResultObjectDto arEventSaveResult = arEventLogic.saveArEventLogic(jsonStr, excelFile);

        //이벤트 기본 저장 시 에러가 발생되었을 때 에러처리
        if (PredicateUtils.isNull(arEventSaveResult)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_SODAR_SAVE_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }

        if (PredicateUtils.isNotNull(arEventSaveResult)) {
            //saveArEventLogic 정상 통신인지 결과 코드 확인
            int arEventResultCd = arEventSaveResult.getResultCode();

            //이벤트 기본 저장 시 결과 코드가 200이 아니면 에러처리
            if (!PredicateUtils.isEqualNumber(arEventResultCd, httpSuccessCode)) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_SODAR_SAVE_ERROR.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
            }
            //이벤트 기본 저장 시 결과 코드가 200이면 서베이고 데이터 저장 시작
            if (PredicateUtils.isEqualNumber(arEventResultCd, httpSuccessCode)) {
                //saveArEventLogic 통신 결과 값이 200 이면 로직 처리
                /*
                 * 서베이고 관련 로직 추가 (문항, 답 관련 데이터)
                 */
                Map<String, Object> saveResultMap = (Map<String, Object>)arEventSaveResult.getResult();
                String eventId = (String)saveResultMap.get("eventId");
                int arEventId = (int)saveResultMap.get("arEventId");

                if (PredicateUtils.isEqualZero(arEventId)) {
                    resultCode = ErrorCodeDefine.CUSTOM_ERROR_AR_EVENT_INFO_NULL.code();
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                }

                if (PredicateUtils.isGreaterThanZero(arEventId)) {
                    resultMap.put("eventId", eventId);
                    resultMap.put("arEventId", arEventId);

                    try {
                        EventSaveDto sodarSaveReqDto = objectMapper.readValue(jsonStr, EventSaveDto.class);

                        //문항 정보 저장 시작
                        if (!PredicateUtils.isNullList(sodarSaveReqDto.getSurveySubjectInfo())) {
                            for (SurveySubjectSodarReqDto subjectSodarReqDto : sodarSaveReqDto.getSurveySubjectInfo()) {
                                //문항 정보 저장
                                subjectSodarReqDto.setSurveySubjectId(null);
                                SurveySubjectEntity savedSubjectEntity = surveyEntityService.saveSurveySubjectByReqDto(arEventId, subjectSodarReqDto);

                                if (PredicateUtils.isNotNull(savedSubjectEntity)) {
                                    long subjectId = savedSubjectEntity.getSurveySubjectId();

                                    //보기 정보 저장 시작
                                    if (!PredicateUtils.isNullList(subjectSodarReqDto.getExampleInfo())) {
                                        for (SubjectExampleSodarReqDto exampleSodarReqDto : subjectSodarReqDto.getExampleInfo()) {
                                            //보기 정보 저장
                                            exampleSodarReqDto.setSurveyExampleId(null);
                                            surveyEntityService.saveSurveyExampleByReqDto(subjectId, exampleSodarReqDto);
                                        }
                                    }
                                    //보기 정보 저장 끝

                                    //문항 -> 주관식 정보 저장 (주관식일때만)
                                    if (PredicateUtils.isEqualsStr(subjectSodarReqDto.getSubjectExampleType(), ExampleTypeDefine.QUESTION.name())) {
                                        surveyEntityService.saveAllSurveyExampleQuestionByReqDtoList(savedSubjectEntity.getSurveySubjectId(), subjectSodarReqDto.getExampleQuestionInfo());
                                    }

                                    //문항 팝업 이미지 정보 저장 시작
                                    if (!PredicateUtils.isNullList(subjectSodarReqDto.getPopupImageInfo())) {
                                        surveyEntityService.saveAllSurveySubjectPopupImageByReqDto(savedSubjectEntity.getSurveySubjectId(), subjectSodarReqDto.getPopupImageInfo());
                                    }
                                }
                            }
                        }
                        //문항 정보 저장 끝

                        //유형 정보 저장 시작
                        if (!PredicateUtils.isNullList(sodarSaveReqDto.getSurveySubjectCategoryInfo())) {
                            surveyEntityService.saveAllSurveySubjectCategoryByReqDto(arEventId, sodarSaveReqDto.getSurveySubjectCategoryInfo());
                        }
                        //유형 정보 저장 끝

                        //시퀀스 값 저장
                        arEventService.saveSequences(StringTools.joinStringsNoSeparator(eventId, "_", "SURVEY"), 0L);

                    } catch (Exception e) {
                        resultCode = ErrorCodeDefine.JSON_PARSE_EXCEPTION_ERROR.code();
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                    }
                }
            }
            //서베이고 데이터 저장 끝
        }

        return new ApiResultObjectDto().builder()
                .result(resultMap)
                .resultCode(resultCode)
                .build();
    }

}
