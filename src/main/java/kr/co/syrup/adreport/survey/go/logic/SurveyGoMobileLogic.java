package kr.co.syrup.adreport.survey.go.logic;

import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.exception.BaseException;
import kr.co.syrup.adreport.framework.utils.AES256Utils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.survey.go.define.ExampleTypeDefine;
import kr.co.syrup.adreport.survey.go.define.GenderTypeDefine;
import kr.co.syrup.adreport.survey.go.define.ImgVideoRegTypeDefine;
import kr.co.syrup.adreport.survey.go.dto.request.*;
import kr.co.syrup.adreport.survey.go.dto.response.SurveyInfoSelectMobileResDto;
import kr.co.syrup.adreport.survey.go.dto.response.SurveyResultSaveMobileResDto;
import kr.co.syrup.adreport.survey.go.entity.*;
import kr.co.syrup.adreport.survey.go.mybatis.vo.SurveyLogAttendResultSaveVO;
import kr.co.syrup.adreport.survey.go.service.SurveyEntityService;
import kr.co.syrup.adreport.survey.go.service.SurveyGoLogService;
import kr.co.syrup.adreport.survey.go.service.SurveyGoMobileService;
import kr.co.syrup.adreport.survey.go.service.SurveyGoStaticsService;
import kr.co.syrup.adreport.web.event.define.ErrorCodeDefine;
import kr.co.syrup.adreport.web.event.define.EventLogicalTypeDefine;
import kr.co.syrup.adreport.web.event.define.EventTypeDefine;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import kr.co.syrup.adreport.web.event.dto.request.WebArGateReqDto;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.entity.ArEventEntity;
import kr.co.syrup.adreport.web.event.entity.WebEventBaseEntity;
import kr.co.syrup.adreport.web.event.mybatis.vo.ArEventJoinEventBaseVO;
import kr.co.syrup.adreport.web.event.service.ArEventFrontService;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import kr.co.syrup.adreport.web.event.service.BatchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class SurveyGoMobileLogic {

    @Autowired
    private SurveyGoMobileService surveyGoMobileService;

    @Autowired
    private SurveyGoLogService surveyGoLogService;

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private ArEventFrontService arEventFrontService;

    @Autowired
    private SurveyEntityService surveyEntityService;

    @Autowired
    private SurveyGoStaticsService surveyGoStaticsService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AES256Utils aes256Utils;

    @Value("${aes.keyvalue.survey.answer}")
    private String surveyAnswerKey;

    /**
     * 서베이고 참여 가능한지 확인 로직
     * @param reqDto
     * @return
     */
    public ApiResultObjectDto checkPossibleSurveyAttendLogic(WebArGateReqDto reqDto) {
        int resultCode = HttpStatus.OK.value();

        Map<String, Object>resultMap = new HashMap<>();

        if (PredicateUtils.isNull(reqDto.getEventId())) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        } else {
            //이벤트 기본정보 가져오기(테이블 : web_event_base, ar_event)
            ArEventJoinEventBaseVO vo = arEventService.findArEventJoinEventBaseByEventId(reqDto.getEventId());

            boolean isSurveyAttend = true;
            if (PredicateUtils.isNotNull(vo)) {
                //서베이고일때
                if (PredicateUtils.isEqualsStr(vo.getEventType(), EventTypeDefine.SURVEY.name())) {

                    String arAttendTermType = StringDefine.Y.name();  //기간참여조건 타입(제한없음, 기간제한)
                    if (PredicateUtils.isNull(vo.getArAttendTermType()) || StringUtils.isEmpty(vo.getArAttendTermType()) || !PredicateUtils.isEqualY(vo.getArAttendTermType())) {
                        arAttendTermType = StringDefine.N.name();
                    }

                    int arAttendTermLimitType = 0  //기간참여조건 종류(1일, 이벤트기간내)
                        ,arAttendTermLimitCount = 0; //기간참여조건 회수

                    //기간참여조건 종류(1일, 이벤트기간내)의 값이 있으면 arAttendTermLimitType 값 주입
                    if (PredicateUtils.isNotNull(vo.getArAttendTermLimitType())) {
                        arAttendTermLimitType = Integer.parseInt(vo.getArAttendTermLimitType());
                    }
                    //기간참여조건 회수의 값이 있으면 arAttendTermLimitCount 값 주입
                    if (PredicateUtils.isNotNull(vo.getArAttendTermLimitCount())) {
                        arAttendTermLimitCount = vo.getArAttendTermLimitCount();
                    }

                    //전화번호 참여 제한 여부 체크 시작
                    if (vo.getAttendConditionMdnYn()) {
                        //참여번호 기간참여 조건이 'Y' 일때
                        if (PredicateUtils.isEqualY(arAttendTermType)) {
                            //참여번호 기간참여 조건이 'Y' 일때
                            if (PredicateUtils.isGreaterThanZero(arAttendTermLimitType)) {
                                //참여번호 기간참여 조건이 'Y' 이고 이벤트 1일 일때
                                //survey_log_attend 테이블의 목표달성수 1일 로그 개수
                                int surveyLogAttendCount = surveyGoLogService.countSurveyLogAttendByEventIdAndPhoneNumberOrAttendCodeAndTodayYn(reqDto.getEventId(), reqDto.getPhoneNumber(), "", StringDefine.Y.name());

                                if (PredicateUtils.isGreaterThanEqualTo(surveyLogAttendCount, arAttendTermLimitCount)) {
                                    throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_ATTEND_COUNT_LIMIT_PHONE_NUMBER.getDesc(), ResultCodeEnum.CUSTOM_ERROR_ATTEND_COUNT_LIMIT_PHONE_NUMBER);
                                }

                            } else if (PredicateUtils.isEqualZero(arAttendTermLimitType)) {
                                //참여번호 기간참여 조건이 'Y' 이고 이벤트 기간내 일때
                                //survey_log_attend 테이블의 목표달성수 전체기한 로그 총 개수
                                int surveyLogAttendCount = surveyGoLogService.countSurveyLogAttendByEventIdAndPhoneNumberOrAttendCodeAndTodayYn(reqDto.getEventId(), reqDto.getPhoneNumber(), "", StringDefine.N.name());

                                if (PredicateUtils.isGreaterThanEqualTo(surveyLogAttendCount, arAttendTermLimitCount)) {
                                    throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_ATTEND_COUNT_LIMIT_PHONE_NUMBER.getDesc(), ResultCodeEnum.CUSTOM_ERROR_ATTEND_COUNT_LIMIT_PHONE_NUMBER);
                                }
                            }
                        }
                    }
                    //전화번호 참여 제한 여부 체크 끝

                    //참여코드 참여 제한 여부 체크 시작
                    if (vo.getArAttendConditionCodeYn()) {
                        //참여번호 기간참여 조건이 'Y' 일때
                        if (PredicateUtils.isEqualY(arAttendTermType)) {
                            //참여번호 기간참여 조건이 'Y' 일때
                            if (PredicateUtils.isGreaterThanZero(arAttendTermLimitType)) {
                                //참여번호 기간참여 조건이 'Y' 이고 이벤트 1일 일때
                                //survey_log_attend 테이블의 목표달성수 1일 로그 개수
                                int surveyLogAttendCount = surveyGoLogService.countSurveyLogAttendByEventIdAndPhoneNumberOrAttendCodeAndTodayYn(reqDto.getEventId(), "", reqDto.getAttendCode(), StringDefine.Y.name());

                                if (PredicateUtils.isGreaterThanEqualTo(surveyLogAttendCount, arAttendTermLimitCount)) {
                                    throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_ATTEND_COUNT_LIMIT_ATTEND_CODE.getDesc(), ResultCodeEnum.CUSTOM_ERROR_ATTEND_COUNT_LIMIT_ATTEND_CODE);
                                }

                            } else if (PredicateUtils.isEqualZero(arAttendTermLimitType)) {
                                //참여번호 기간참여 조건이 'Y' 이고 이벤트 기간내 일때
                                //survey_log_attend 테이블의 목표달성수 전체기한 로그 총 개수
                                int surveyLogAttendCount = surveyGoLogService.countSurveyLogAttendByEventIdAndPhoneNumberOrAttendCodeAndTodayYn(reqDto.getEventId(), "", reqDto.getAttendCode(), StringDefine.N.name());

                                if (PredicateUtils.isGreaterThanEqualTo(surveyLogAttendCount, arAttendTermLimitCount)) {
                                    throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_ATTEND_COUNT_LIMIT_ATTEND_CODE.getDesc(), ResultCodeEnum.CUSTOM_ERROR_ATTEND_COUNT_LIMIT_ATTEND_CODE);
                                }
                            }
                        }
                    }
                    //참여코드 참여 제한 여부 체크 끝
                } else {
                    isSurveyAttend = false;
                }
            } else {
                isSurveyAttend = false;
            }
            //서베이고 참여 가능한 상태이면 > 서베이고 참여 인덱스(surveyLogAttendId) 값 만들기 시작
            if (isSurveyAttend) {
                long sequence = arEventFrontService.findSequenceByName(StringTools.joinStringsNoSeparator(reqDto.getEventId(), "_SURVEY"));
                String surveyLogAttendId = StringTools.joinStringsNoSeparator(String.valueOf(vo.getArEventId()), "_", String.valueOf(sequence));

                //핸드폰 번호가 없으면 공백처리
                if (PredicateUtils.isNull(reqDto.getPhoneNumber())) {
                    reqDto.setPhoneNumber("");
                }
                //참여번호가 없으면 공백처리
                if (PredicateUtils.isNull(reqDto.getAttendCode())) {
                    reqDto.setAttendCode("");
                }

                try {
                    //survey_log_attend 테이블 저장
                    surveyGoLogService.saveSurveyLogAttend(surveyLogAttendId, vo.getEventId(), vo.getArEventId(), reqDto.getPhoneNumber(), reqDto.getAttendCode());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    isSurveyAttend = false;
                }
                //surveyLogAttendId 값 리턴
                resultMap.put("surveyLogAttendId", surveyLogAttendId);
            }
            //서베이고 참여 가능한 상태이면 > 서베이고 참여 인덱스(surveyLogAttendId) 값 만들기 끝

            //서베이고 참여 가능 여부 값 주입
            resultMap.put("isSurveyAttend", isSurveyAttend);
        }
        return new ApiResultObjectDto().builder()
                .result(resultMap)
                .resultCode(resultCode)
                .build();
    }

    /**
     * 설문대상 조건 체크 로직(성별/연령대)
     * @param reqDto
     * @return
     */
    public ApiResultObjectDto checkPossibleSurveyGenderAgeAttendLogic(GenderAgeReqDto reqDto) {
        int resultCode = HttpStatus.OK.value();

        Map<String, Boolean>resultMap = new HashMap<>();
        boolean isSurveyAttend = false;

        if (PredicateUtils.isNull(reqDto)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }
        if (PredicateUtils.isNull(reqDto.getEventId())) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        } else {
            //이벤트 기본정보 가져오기(테이블 : web_event_base, ar_event)
            ArEventJoinEventBaseVO vo = arEventService.findArEventJoinEventBaseByEventId(reqDto.getEventId());

            if (PredicateUtils.isNotNull(vo)) {
                //성,연령별 참여조건이 null이면 false 예외처리
                if (PredicateUtils.isNull(vo.getAttendConditionTargetYn())) {
                    vo.setAttendConditionTargetYn(false);
                }

                //참여조건이 성,연령별 조건이 false 일때
                if (!vo.getAttendConditionTargetYn()) {
                    resultCode = ErrorCodeDefine.CUSTOM_ERROR_NO_ATTEND_TARGET.code();
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                }
                //참여조건이 성,연령별 조건이 true 일때
                if (vo.getAttendConditionTargetYn()) {
                    int limitCount = 0, limitLogCount = 0;
                    ArEventEntity arEvent = arEventService.findArEventByEventId(reqDto.getEventId());

                    log.info("성/연령별 조건 체크 시작");
                    List<SurveyTargetAgeGenderLimitEntity> targetAgeGenderLimitList = surveyGoMobileService.findSurveyTargetAgeGenderLimitListByArEventId(arEvent.getArEventId());
                    if (!PredicateUtils.isNullList(targetAgeGenderLimitList)) {
                        log.info("성별 : 전체 / 연령 : 전체 체크");
                        //1. 성별 - 전체 and 연령 - 전체
                        Optional<SurveyTargetAgeGenderLimitEntity> genderAllAndAgeAllLimit =
                                targetAgeGenderLimitList.stream()
                                .filter(limit -> PredicateUtils.isEqualsStr(limit.getSurveyTargetGender(), GenderTypeDefine.A.name()))
                                .filter(limit -> PredicateUtils.isEqualZero(limit.getSurveyTargetAge()))
                                .findAny();

                        if (genderAllAndAgeAllLimit.isPresent()) {
                            log.info("성별 : 전체 / 연령 : 전체 제한에 걸림");
                            limitCount = genderAllAndAgeAllLimit.get().getSurveyTargetLimitCount();
                            //로그 테이블의 개수 가져오기
                            limitLogCount = surveyGoLogService.countSurveyLogAttendByEventId(reqDto.getEventId(), null, null, true, true);
                        } else {
                            log.info("성별 : 전체 / 연령 : 전체 제한 걸리지 않음");
                        }

                        log.info("성별 : 전체 / 연령 : 조건 체크");
                        //2. 성별 - 전체 and 연령 - 조건
                        Optional<SurveyTargetAgeGenderLimitEntity> genderAllAndAgeCheckLimit =
                                targetAgeGenderLimitList.stream()
                                        .filter(limit -> PredicateUtils.isEqualsStr(limit.getSurveyTargetGender(), GenderTypeDefine.A.name()))
                                        .filter(limit -> PredicateUtils.isEqualNumber(limit.getSurveyTargetAge(), reqDto.getAge()))
                                        .findAny();

                        if (genderAllAndAgeCheckLimit.isPresent()) {
                            log.info("성별 : 전체 / 연령 : 조건 제한에 걸림");
                            limitCount = genderAllAndAgeCheckLimit.get().getSurveyTargetLimitCount();
                            //로그 테이블의 개수 가져오기
                            limitLogCount = surveyGoLogService.countSurveyLogAttendByEventId(reqDto.getEventId(), null, reqDto.getAge(), true, false);
                        } else {
                            log.info("성별 : 전체 / 연령 : 조건 제한 걸리지 않음");
                        }

                        log.info("성별 : 조건 / 연령 : 전체 체크");
                        //3. 성별 - 조건 and 연령 - 전체
                        Optional<SurveyTargetAgeGenderLimitEntity> genderCheckAndAgeAllLimit =
                                targetAgeGenderLimitList.stream()
                                        .filter(limit -> PredicateUtils.isEqualsStr(limit.getSurveyTargetGender(), reqDto.getGender()))
                                        .filter(limit -> PredicateUtils.isEqualZero(limit.getSurveyTargetAge()))
                                        .findAny();

                        if (genderCheckAndAgeAllLimit.isPresent()) {
                            log.info("성별 : 조건 / 연령 : 전체 제한에 걸림");
                            limitCount = genderCheckAndAgeAllLimit.get().getSurveyTargetLimitCount();
                            //로그 테이블의 개수 가져오기
                            limitLogCount = surveyGoLogService.countSurveyLogAttendByEventId(reqDto.getEventId(), reqDto.getGender(), null, false, true);
                        } else {
                            log.info("성별 : 조건 / 연령 : 전체 제한 걸리지 않음");
                        }

                        log.info("성별 : 조건 / 연령 : 조건 체크");
                        //4. 성별 - 조건 and 연령 - 조건
                        Optional<SurveyTargetAgeGenderLimitEntity> genderCheckAndAgeCheckLimit =
                                targetAgeGenderLimitList.stream()
                                        .filter(limit -> PredicateUtils.isEqualsStr(limit.getSurveyTargetGender(), reqDto.getGender()))
                                        .filter(limit -> PredicateUtils.isEqualNumber(limit.getSurveyTargetAge(), reqDto.getAge()))
                                        .findAny();

                        if (genderCheckAndAgeCheckLimit.isPresent()) {
                            log.info("성별 : 조건 / 연령 : 조건 제한에 걸림");
                            limitCount = genderCheckAndAgeCheckLimit.get().getSurveyTargetLimitCount();
                            limitLogCount = surveyGoLogService.countSurveyLogAttendByEventId(reqDto.getEventId(), reqDto.getGender(), reqDto.getAge(), false, false);
                        } else {
                            log.info("성별 : 조건 / 연령 : 조건 제한 걸리지 않음");
                        }
                    }

                    //제한테이블의 셋팅되어있는 개수가 0 이면 '참여불가' 처리
                    if (PredicateUtils.isEqualZero(limitCount)) {
                        //설정된 제한값이 없음으로 참여불가처리
                        resultCode = ErrorCodeDefine.CUSTOM_ERROR_LIMIT_SURVEY_GENDER_AGE.code();
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                    }
                    //제한테이블의 셋팅되어있는 개수가 0 보다 크면 로그 개수 체크 시작
                    if (PredicateUtils.isGreaterThanZero(limitCount)) {
                        //제한테이블의 셋팅되어있는 개수 보다 로그 개수가 크면 '참여불가' 처리
                        if (PredicateUtils.isGreaterThanEqualTo(limitLogCount, limitCount)) {
                            resultCode = ErrorCodeDefine.CUSTOM_ERROR_LIMIT_SURVEY_GENDER_AGE.code();
                            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                        } else {
                            //모든 조건이 통과되어 '참여가능' 처리
                            isSurveyAttend = true;

                            SurveyLogAttendEntity logAttendEntity = new SurveyLogAttendEntity();
                            logAttendEntity.setSurveyLogAttendId(reqDto.getSurveyLogAttendId());
                            logAttendEntity.setGender(reqDto.getGender());
                            logAttendEntity.setAge(reqDto.getAge());

                            surveyGoLogService.updateSurveyLogAttend(logAttendEntity);
                        }
                    }
                }
            }
        }
        resultMap.put("isSurveyAttend", isSurveyAttend);

        return new ApiResultObjectDto().builder()
                .result(resultMap)
                .resultCode(resultCode)
                .build();

    }

    /**
     * 서베이고 참여 결과 로그 저장하기 로직
     * @param reqDto
     * @return
     */
    @Transactional
    public ApiResultObjectDto saveSurveyAttendResultLogic(SurveyLogAttendResultReqDto reqDto) {
        int resultCode = HttpStatus.OK.value();

        if (PredicateUtils.isNull(reqDto)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }
        if (PredicateUtils.isNull(reqDto.getSurveyLogAttendId())) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        } else {
            if (!PredicateUtils.isNullList(reqDto.getSurveyResultInfo())) {
                List<SurveyLogAttendResultSaveVO> saveList = new ArrayList<>();

                //survey_log_attend_result 저장 데이터 만들기 시작
                for (SurveyLogAttendListReqDto attendResult : reqDto.getSurveyResultInfo()) {
                    SurveyLogAttendResultSaveVO saveVO = new SurveyLogAttendResultSaveVO();

                    saveVO.setSurveyLogAttendId(reqDto.getSurveyLogAttendId());
                    saveVO.setSurveySubjectId(attendResult.getSurveySubjectId());
                    saveVO.setSurveyExampleId(attendResult.getSurveyExampleId());
                    saveVO.setSubjectSort(attendResult.getSubjectSort());
                    saveVO.setExampleSort(attendResult.getExampleSort());
                    saveVO.setIsAnswer(attendResult.getIsAnswer());
                    saveVO.setQuestionAnswer(attendResult.getQuestionAnswer());

                    saveList.add(saveVO);
                }
                //survey_log_attend_result 저장 데이터 만들기 끝

                try {
                    //survey_log_attend_result 저장하기
                    surveyGoLogService.saveSurveyLogAttendResultList(saveList);

                    try {
                        SurveyLogAttendEntity logAttendEntity = new SurveyLogAttendEntity();
                        logAttendEntity.setSurveyLogAttendId(reqDto.getSurveyLogAttendId());
                        logAttendEntity.setIsSubmit(true);

                        //정상적으로 로그가 저장되면 survey_log_attend 참여완료로 업데이트
                        surveyGoLogService.updateSurveyLogAttend(logAttendEntity);

                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        resultCode = ErrorCodeDefine.IOE_ERROR.code();
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                    }

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    resultCode = ErrorCodeDefine.IOE_ERROR.code();
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                }

//                CompletableFuture.supplyAsync(() -> this.saveSurveyAnswerStaticsData(reqDto.getSurveyLogAttendId()));
            }
        }
        return new ApiResultObjectDto().builder()
                .result(null)
                .resultCode(resultCode)
                .build();
    }

    public SurveyInfoSelectMobileResDto getSurveySubjectData(SurveyInfoSelectMobileReqDto req){
        SurveyInfoSelectMobileResDto res = new SurveyInfoSelectMobileResDto();

        // 0. webEventBase 조회
        WebEventBaseEntity webEventBase = arEventService.findEventBase(req.getEventId());
        res.setWebEventBaseInfo(webEventBase);

        // 1. arEventInfo 조회
        ArEventEntity arEvent           = arEventService.findArEventByEventIdAtCache(req.getEventId());
        res.setArEventInfo(arEvent);

        // 2. surveySubjectInfo 문항, 보기 정보 조회
        List<SurveySubjectEntity> surveySubjectList = surveyEntityService.findAllSurveySubjectByArEventIdAtCache(arEvent.getArEventId());
        if (PredicateUtils.isNullList(surveySubjectList)) {
            return res;
        }

        // 조회 결과 변환 (문항 테이블 -> 응답 모델)
        List<SurveySubjectInfoMobileDto> resSubjectList = surveySubjectList.stream()
                .map(dto -> modelMapper.map(dto, SurveySubjectInfoMobileDto.class))
                .collect(Collectors.toList());

        resSubjectList.forEach(subject -> {
            String answerAESKey = Long.toString(subject.getSurveySubjectId()) + surveyAnswerKey;
            if (PredicateUtils.isEqualsStr(subject.getSubjectExampleType(), ExampleTypeDefine.CHOICE.name())) {
                // 객관식 답안 암호화
                if(PredicateUtils.isNotNull(subject.getQuizAnswerSort())) {
                    subject.setQuizAnswerSort(aes256Utils.encrypt(subject.getQuizAnswerSort(), answerAESKey));
                }

                // 객관식일때. 주관식은 반환할 보기가 없음
                List<SurveyExampleEntity> exampleEntity = surveyEntityService.findAllSurveyExampleBySurveySubjectIdAtCache(subject.getSurveySubjectId());
                // 조회 결과 변환 (보기 테이블 -> 응답 모델)
                List<SubjectExampleSodarReqDto> resExampleList = exampleEntity.stream()
                        .map(dto -> modelMapper.map(dto, SubjectExampleSodarReqDto.class))
                        .collect(Collectors.toList());

                subject.setExampleInfo(resExampleList);
            } else if (PredicateUtils.isEqualsStr(subject.getSubjectExampleType(), ExampleTypeDefine.QUESTION.name())) {
                // 주관식일때
                // 주관식 정답 조회
                List<SurveyExampleQuestionEntity> questionEntity = surveyEntityService.findAllSurveyExampleQuestionBySurveySubjectIdAtCache(subject.getSurveySubjectId());

                List<SurveyExampleQuestionSodarReqDto> resQuestionAnswerList = questionEntity.stream()
                        .map(dto -> modelMapper.map(dto, SurveyExampleQuestionSodarReqDto.class))
                        .collect(Collectors.toList());

                resQuestionAnswerList.forEach(resQuestionAnswer -> {
                    // 주관식 답안 암호화
                    if(PredicateUtils.isNotNull(resQuestionAnswer.getExampleQuestionAnswer())) {
                        resQuestionAnswer.setExampleQuestionAnswer(aes256Utils.encrypt(resQuestionAnswer.getExampleQuestionAnswer(), answerAESKey));
                    }
                });

                subject.setExampleQuestionAnswer(resQuestionAnswerList);
            }

            if (PredicateUtils.isEqualY(subject.getImgVideoYn())
                    && PredicateUtils.isEqualsStr(subject.getImgVideoRegType(), ImgVideoRegTypeDefine.IMG.name())) {
                // 이미지/영상등록 티압이 이미지일때 팝업이미지를 조회해서 내려줘야됨.
                List<SurveySubjectPopupImageEntity> surveySubjectPopupImageEntity = surveyEntityService.findSurveySubjectPopupImageBySurveySubjectId(subject.getSurveySubjectId());
                // 조회 결과 변환 (문항 팝업 이미지 정보 테이블 -> 응답 모델)
                List<SurveySubjectPopupImageReqDto> popupImageInfo = surveySubjectPopupImageEntity.stream()
                        .map(dto -> modelMapper.map(dto, SurveySubjectPopupImageReqDto.class))
                        .collect(Collectors.toList());

                subject.setPopupImageInfo(popupImageInfo);
            }
        });

        res.setSurveySubjectInfo(resSubjectList);

        return res;
    }

    @Transactional
    public SurveyResultSaveMobileResDto saveSurveyResult(SurveyResultSaveMobileReqDto req) {
        SurveyResultSaveMobileResDto res = new SurveyResultSaveMobileResDto();
        // 1. arEventInfo 조회
        ArEventEntity arEvent = arEventService.findArEventByEventId(req.getEventId());

        if (PredicateUtils.isNullList(req.getAnswerList())) {
            // error 처리 - 응답 리스트가 존재하지 않음
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_MOBILE_SURVEY_PROCESS_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_MOBILE_SURVEY_PROCESS_ERROR);
        }

        // 2. 이벤트에 해당하는 문항 리스트 정보 조회
        List<SurveySubjectEntity> surveySubjectList = surveyEntityService.findAllSurveySubjectByArEventId(arEvent.getArEventId());

        if(req.getAnswerList().size() > surveySubjectList.size()){
            // error 처리 - 문항 정보 개수보다 답변 개수가 많을 수 없음.
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_MOBILE_SURVEY_PROCESS_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_MOBILE_SURVEY_PROCESS_ERROR);
        }

        // 3. 답변 총 개수 res 삽입
        res.setAnswerTotalCount(req.getAnswerList().size());

        List<SurveyLogAttendListReqDto> surveyLogAttendResultList = new ArrayList<>();

        // 4. 타입이 퀴즈형, 분석형인 경우 아래 로직 수행,
        if (EventLogicalTypeDefine.퀴즈형.value().equals(arEvent.getEventLogicalType())
                || EventLogicalTypeDefine.분석형.value().equals(arEvent.getEventLogicalType())) {

            // 4-가. 문항 리스트 문항당 답변 정답 체크 및 가중치 합산
            AtomicInteger answerCount = new AtomicInteger();    // 정답 개수
            AtomicReference<Double> weightCount = new AtomicReference<>((double) 0);    // 가중치

            surveySubjectList.forEach(subject -> {      // 이벤트에 대한 전체 문항을 반복하여 답변 검증 및 가중치 합산 및 유형 산출
                List<SurveyAnswerInfoDto> answerList = req.getAnswerList().stream()
                        .filter(info -> info.getSurveySubjectId().equals(subject.getSurveySubjectId()))
                        .collect(Collectors.toList());  // 현재 문항에 대한 답변 검출

                if (PredicateUtils.isNullList(answerList) || answerList.size() > 1) {
                    // error 처리 - 문항에 대한 답변 정보가 없거나 2개 이상 올라올 수 없음.
                    throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_MOBILE_SURVEY_PROCESS_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_MOBILE_SURVEY_PROCESS_ERROR);
                }

                // 답변 검출
                SurveyAnswerInfoDto answer = answerList.get(0);

                // 로그 삽입 데이터 생성
                SurveyLogAttendListReqDto surveyLogAttendResult = new SurveyLogAttendListReqDto();
                surveyLogAttendResult.setSurveySubjectId(subject.getSurveySubjectId());
                surveyLogAttendResult.setSubjectSort(answer.getSubjectSort());

                // ---- 퀴즈형 - 정답개수 / 분석형 - 가중치 합산 START
                // 현재 문항이 객관식일때
                if (PredicateUtils.isEqualsStr(subject.getSubjectExampleType(), ExampleTypeDefine.CHOICE.name())) {
                    if (PredicateUtils.isNullList(answer.getSurveyExampleList()) || answer.getSurveyExampleList().size() > 1) {
                        // error 처리 - 객관식 답변 리스트에서 답변 리스트가 없을 수 없거나 2개 이상의 보기가 선택될 수 없음 (퀴즈형 / 분석형 한정)
                        throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_MOBILE_SURVEY_PROCESS_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_MOBILE_SURVEY_PROCESS_ERROR);
                    }

                    long surveyExampleId = answer.getSurveyExampleList().get(0).getSurveyExampleId();  // 답변 객관식 보기 번호 검출

                    surveyLogAttendResult.setSurveyExampleId(surveyExampleId);
                    surveyLogAttendResult.setExampleSort(answer.getSurveyExampleList().get(0).getExampleSort());

                    // 답변에 대한 보기 정보 조회
                    SurveyExampleEntity exampleEntity = surveyEntityService.findSurveyExampleBySurveyExampleId(surveyExampleId);

                    if (EventLogicalTypeDefine.분석형.value().equals(arEvent.getEventLogicalType())) { // 분석형일때
                        weightCount.set(weightCount.get() + Double.parseDouble(exampleEntity.getExampleWeightValue())); // 가중치 합산
                        surveyLogAttendResult.setIsAnswer(false);
                    }

                    if (EventLogicalTypeDefine.퀴즈형.value().equals(arEvent.getEventLogicalType())) { // 퀴즈형일때
                        if(exampleEntity.getSort() == subject.getQuizAnswerSort()){     // 정답일때
                            answerCount.getAndIncrement();  // 정답 개수 증가
                            surveyLogAttendResult.setIsAnswer(true);
                        } else {
                            surveyLogAttendResult.setIsAnswer(false);
                        }
                    }
                }

                // 현재 문항이 퀴즈형 - 주관식일때. 분석형은 주관식이 없음.
                if (EventLogicalTypeDefine.퀴즈형.value().equals(arEvent.getEventLogicalType())
                        && PredicateUtils.isEqualsStr(subject.getSubjectExampleType(), ExampleTypeDefine.QUESTION.name())) {
                    // 주관식 정답 리스트 조회
                    List<SurveyExampleQuestionEntity> exampleQuestionList = surveyEntityService.findAllSurveyExampleQuestionBySurveySubjectId(subject.getSurveySubjectId());

                    exampleQuestionList = exampleQuestionList.stream()
                            .filter(exampleQuestion -> exampleQuestion.getExampleQuestionAnswer().equals(answer.getQuestionAnswer()))
                            .collect(Collectors.toList());  // 주관식 정답 일치하는지 검출

                    surveyLogAttendResult.setQuestionAnswer(answer.getQuestionAnswer());

                    if (!PredicateUtils.isNullList(exampleQuestionList)) {  // 주관식 정답 일치한 검출결과가 있을때
                        answerCount.getAndIncrement();  // 정답 개수 증가
                        surveyLogAttendResult.setIsAnswer(true);
                    }
                    else{
                        surveyLogAttendResult.setIsAnswer(false);
                    }
                }

                surveyLogAttendResultList.add(surveyLogAttendResult);
                // ---- 퀴즈형 - 정답개수 / 분석형 - 가중치 합산 END
            });

            // 4-나. 유형 산출
            // 유형 카테고리 리스트 조회
            List<SurveySubjectCategoryEntity> subjectCategoryList = surveyEntityService.findAllSurveySubjectCategoryByArEventIdOrderBySortAsc(arEvent.getArEventId());

            // ---- 퀴즈형 가중치 계산 START
            // 유형이 퀴즈형인 경우.
            if (EventLogicalTypeDefine.퀴즈형.value().equals(arEvent.getEventLogicalType())) {
                // 정답 카운트에 대한 정답율 계산하여 가중치 설정
                weightCount.set((((double) answerCount.get()) / req.getAnswerList().size()) * 100);
                // 퀴즈형인 경우 정답 갯수 반환
                res.setAnswerCount(answerCount.get());
            }
            // ---- 퀴즈형 가중치 계산 END

            subjectCategoryList = subjectCategoryList.stream()
                    .filter(subjectCategory ->
                            (Double.parseDouble(subjectCategory.getWeightMin()) == 0 || (Double.parseDouble(subjectCategory.getWeightMin()) < weightCount.get()))
                                    && Double.parseDouble(subjectCategory.getWeightMax()) >= weightCount.get())
                    .collect(Collectors.toList());  // 가중치에 대한 유형 검출

            if (PredicateUtils.isNullList(subjectCategoryList)) {
                // error 처리 - 유형 산출되지 않음
                throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_MOBILE_SURVEY_PROCESS_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_MOBILE_SURVEY_PROCESS_ERROR);
            }

            // 가중치에 대한 유형 검출
            SurveySubjectCategoryEntity surveySubjectCategoryInfo = subjectCategoryList.get(0);

            // 4-다. 해당하는 유형의 정보 res 에 삽입
            res.setSurveySubjectCategoryId(surveySubjectCategoryInfo.getSurveySubjectCategoryId());
            res.setCategoryTitle(surveySubjectCategoryInfo.getCategoryTitle());
            res.setCategorySubText(surveySubjectCategoryInfo.getCategorySubText());
            res.setCategoryImgUrl(surveySubjectCategoryInfo.getCategoryImgUrl());

            // 4-라. 해당하는 유형의 설문종료 버튼 데이터 res 에 삽입
            res.setSurveyEndButtonType(surveySubjectCategoryInfo.getCategoryButtonType());
            res.setSurveyEndButtonText(surveySubjectCategoryInfo.getCategoryButtonText());
        } else {
            // 5. 타입이 퀴즈형, 분석형이 아닐때 아래 로직 수행
            // 5-가. 설문 종료 버튼 정보 res 에 삽입
            res.setSurveyEndButtonType(arEvent.getSurveyEndButtonType());
            res.setSurveyEndButtonText(arEvent.getSurveyEndButtonText());

            // 5-나. 설문 종료 문구 정보 res 에 삽입 - 대화형 한정
            if (arEvent.getEventLogicalType().equals(EventLogicalTypeDefine.대화형.value())) {
                res.setTalkSurveyEndText(arEvent.getTalkSurveyEndText());
            }

            List<SurveyAnswerInfoDto> answerListClone = new ArrayList<>();
            answerListClone.addAll(req.getAnswerList());

            surveySubjectList.forEach(subject -> {
                List<SurveyAnswerInfoDto> answerList = answerListClone.stream()
                        .filter(info -> info.getSurveySubjectId().equals(subject.getSurveySubjectId()))
                        .collect(Collectors.toList());  // 현재 문항에 대한 답변 검출

                if(PredicateUtils.isNotNull(answerList) && answerList.size() > 0) {
                    SurveyAnswerInfoDto answer = answerList.get(0);

                    if (PredicateUtils.isEqualsStr(subject.getSubjectExampleType(), ExampleTypeDefine.CHOICE.name())) {
                        // 현재 문항이 객관식일때

                        answer.getSurveyExampleList().forEach(answerInfo -> {
                            // 로그 삽입 데이터 생성
                            SurveyLogAttendListReqDto surveyLogAttendResult = new SurveyLogAttendListReqDto();
                            surveyLogAttendResult.setSurveySubjectId(subject.getSurveySubjectId());
                            surveyLogAttendResult.setSubjectSort(answer.getSubjectSort());
                            surveyLogAttendResult.setSurveyExampleId(answerInfo.getSurveyExampleId());
                            surveyLogAttendResult.setExampleSort(answerInfo.getExampleSort());
                            surveyLogAttendResult.setIsAnswer(false);

                            surveyLogAttendResultList.add(surveyLogAttendResult);
                        });

                        if (PredicateUtils.isEqualsStr(subject.getEtcOpinionReceiveYn(), StringDefine.Y.name())
                                && PredicateUtils.isNotNull(answer.getQuestionAnswer())) {
                            // 기타 의견
                            SurveyLogAttendListReqDto surveyLogAttendResult = new SurveyLogAttendListReqDto();
                            surveyLogAttendResult.setSurveySubjectId(subject.getSurveySubjectId());
                            surveyLogAttendResult.setSubjectSort(answer.getSubjectSort());
                            surveyLogAttendResult.setQuestionAnswer(answer.getQuestionAnswer());
                            surveyLogAttendResult.setIsAnswer(false);

                            surveyLogAttendResultList.add(surveyLogAttendResult);
                        }
                    } else if (PredicateUtils.isEqualsStr(subject.getSubjectExampleType(), ExampleTypeDefine.QUESTION.name())) {
                        // 현재 문항이 주관식일때
                        SurveyLogAttendListReqDto surveyLogAttendResult = new SurveyLogAttendListReqDto();
                        surveyLogAttendResult.setSurveySubjectId(subject.getSurveySubjectId());
                        surveyLogAttendResult.setSubjectSort(answer.getSubjectSort());
                        surveyLogAttendResult.setQuestionAnswer(answer.getQuestionAnswer());
                        surveyLogAttendResult.setIsAnswer(false);

                        surveyLogAttendResultList.add(surveyLogAttendResult);
                    }

                    answerListClone.remove(answer);
                }
            });
        }

        // 6. 로그 삽입
        SurveyLogAttendResultReqDto surveyLogAttendReqDto = new SurveyLogAttendResultReqDto();
        surveyLogAttendReqDto.setSurveyLogAttendId(req.getSurveyLogAttendId());
        surveyLogAttendReqDto.setSurveyResultInfo(surveyLogAttendResultList);
        saveSurveyAttendResultLogic(surveyLogAttendReqDto);

        // 7. survey_log_subject_category 로그 삽입 - 작성자 : 안지호, 2023.01.31
        if (PredicateUtils.isNotNull(res.getSurveySubjectCategoryId())) {
            if (PredicateUtils.isGreaterThanZero(res.getSurveySubjectCategoryId().intValue())) {
                surveyGoLogService.saveSurveyLogSubjectCategory(req.getSurveyLogAttendId(), req.getEventId(), arEvent.getArEventId(), res.getSurveySubjectCategoryId());
            }
        }

        return res;
    }

    /**
     * 서베이고 설문 완료 시 ROW 통계 만들기
     * @param surveyLogAttendId
     * @return
     */
    @Transactional
    public void saveSurveyAnswerStaticsData(String surveyLogAttendId) {
        //서베이고 로그로 event_id, ar_event_id 값 가져오기
        Map<String, Object>resultMap = surveyGoMobileService.findArEventIdFromSurveyLogAttendByIdx(surveyLogAttendId);

        String eventId = resultMap.get("event_id").toString();
        Integer arEventId = Integer.parseInt(resultMap.get("ar_event_id").toString());

        //서베이고 설문 제목 리스트 가져오기
        List<String> headerTitleList = surveyGoStaticsService.makeSurveyRawTableTitle(arEventId);

        //서베이고 설문 제목 + 이벤트ID + 설문 인덱스로 저장할 필드 목록 생성
        List<Object>fieldValueList = new ArrayList<>();
        fieldValueList.add("event_id");
        fieldValueList.add("survey_log_attend_id");
        for (int i = 0; i < (headerTitleList.size() - 1); i++) {
            String fieldValue = "answer_" + (i + 1);
            fieldValueList.add(fieldValue);
        }

        //저장할 설문 완료 ROW 목록 만들기
        List<String>answerlist = surveyGoStaticsService.makeSurveyAnswerStaticsData(arEventId, surveyLogAttendId);

        if (PredicateUtils.isNotNull(answerlist)) {
            answerlist.add(0, eventId);
        }
        List<List<String>>makeAnswerList = new ArrayList<>();
        makeAnswerList.add(answerlist);

        //설문 완료 ROW 저장
        batchService.saveBulkSurveyAnswerStatics(fieldValueList, makeAnswerList);

//        return CompletableFuture.completedFuture(resultMap);
    }

}
