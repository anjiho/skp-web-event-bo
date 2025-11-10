package kr.co.syrup.adreport.stamp.event.logic;

import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.exception.BaseException;
import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.stamp.event.define.*;
import kr.co.syrup.adreport.stamp.event.dto.*;
import kr.co.syrup.adreport.stamp.event.dto.request.*;
import kr.co.syrup.adreport.stamp.event.dto.response.StampGateDetailResDto;
import kr.co.syrup.adreport.stamp.event.dto.response.StampMainInfoResDto;
import kr.co.syrup.adreport.stamp.event.dto.response.StampPanDetailResDto;
import kr.co.syrup.adreport.stamp.event.model.*;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.StampEventLogTrVO;
import kr.co.syrup.adreport.stamp.event.service.StampFrontService;
import kr.co.syrup.adreport.stamp.event.service.StampLogService;
import kr.co.syrup.adreport.stamp.event.service.StampSodarService;
import kr.co.syrup.adreport.web.event.define.ErrorCodeDefine;
import kr.co.syrup.adreport.web.event.define.WinningTypeDefine;
import kr.co.syrup.adreport.web.event.dto.request.EventButtonDto;
import kr.co.syrup.adreport.web.event.dto.request.GiveAwayDeliverySaveReqDto;
import kr.co.syrup.adreport.web.event.dto.request.GiveAwayReceiptReqDto;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.dto.response.ProximityResDto;
import kr.co.syrup.adreport.web.event.entity.*;
import kr.co.syrup.adreport.web.event.logic.SkApiLogic;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class StampFrontLogic {

    @Autowired
    private StampLogService stampLogService;

    @Autowired
    private StampSodarService stampSodarService;

    @Autowired
    private StampFrontService stampFrontService;

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private SkApiLogic skApiLogic;

    @Autowired
    private AES256Utils aes256Utils;

    public ApiResultObjectDto saveStampGiveAwayDeliveryLogic(GiveAwayDeliverySaveReqDto reqDto) {
        int resultCd = HttpStatus.OK.value();
        Map<String, Object>resultMap = new HashMap<>();
        //이벤트ID 없으면 에러처리
        if (PredicateUtils.isNull(reqDto.getEventId())) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_EVENT_ID_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_EVENT_ID_NULL);
        }
        StampEventMainModel stampEventMain = stampSodarService.findStampEventMainByEventId(reqDto.getEventId());
        //stpId 없으면 에러처리
        if (PredicateUtils.isNull(stampEventMain.getStpId())) {
            log.error("stpId is Null");
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GET_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GET_NULL);
        }
        //이미 당첨된 로그 인덱스가 존재하는지 확인
        boolean isSavedIdx = stampLogService.getIsSavedStampEventLogWinningSuccessByIdx(stampEventMain.getStpId(), reqDto.getStpEventLogWinningId());
        //당첨 로그 데이터가 없으면 에러처리
        if (!isSavedIdx) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_STAMP_GIVE_AWAY_IS_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_STAMP_GIVE_AWAY_IS_NULL);
        }

        String eventId         = reqDto.getEventId();
        int arEventWinningId   = reqDto.getArEventWinningId();

        ArEventWinningEntity winningEntity = arEventService.findByArEventWinningById(arEventWinningId);

        //당첨정보가 없으면 에러처리
        if (PredicateUtils.isNull(winningEntity.getArEventWinningId())) {
            log.error("arEventWinning is Null");
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GET_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GET_NULL);
        }

        //추첨형일때만
        if (StampWinningTypeDefine.isWinningRaffle(stampEventMain.getStpWinningType())) {
            //중복당첨 제한 체크 시작
            if (PredicateUtils.isEqualY(stampEventMain.getDuplicateWinningType())) {
                String searchValue = "";
                //참여인증이 핸드폰일때
                if (StampWinningAttendTypeDefine.isMdn(stampEventMain.getStpAttendAuthCondition())) {
                    searchValue = reqDto.getPhoneNumber();
                } else {
                    searchValue = reqDto.getAttendCode();
                }

                boolean isToday = false;
                //전체기한내 일때
                if (PredicateUtils.isEqualZero(stampEventMain.getDuplicateWinningLimitType())) {
                    isToday = true;
                }
                int logCnt = stampLogService.getCountStampGiveAwayDeliveryByEventIdAndAuthConditionAndSearchValueIsToday(eventId, stampEventMain.getStpAttendAuthCondition(), searchValue, isToday);
                if (PredicateUtils.isGreaterThanEqualTo(logCnt, stampEventMain.getDuplicateWinningCount())) {
                    if (isToday) {
                        log.error("중복당첨 제한 > 1일일떄 > 개수 초과!");
                    } else {
                        log.error("중복당첨 제한 > 전체기한내 > 개수 초과!");
                    }
                    throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_LIMIT_WINNING_COUNT.getDesc(), ResultCodeEnum.CUSTOM_ERROR_LIMIT_WINNING_COUNT);
                }
            }
        }

        //기타 상품일때만 추가정보 저장
        if (WinningTypeDefine.isEtcWinning(winningEntity.getWinningType())) {
            //당첨 입력 정보 가져오기
            ArEventWinningButtonEntity winningButtonEntity = arEventService.findArEventWinningButtonById(reqDto.getArEventWinningButtonId());

            if (PredicateUtils.isNull(winningButtonEntity.getDeliveryNameYn()))        winningButtonEntity.setDeliveryNameYn(Boolean.FALSE);
            if (PredicateUtils.isNull(winningButtonEntity.getDeliveryBirthYn()))       winningButtonEntity.setDeliveryBirthYn(Boolean.FALSE);
            if (PredicateUtils.isNull(winningButtonEntity.getDeliveryPhoneNumberYn())) winningButtonEntity.setDeliveryPhoneNumberYn(Boolean.FALSE);
            if (PredicateUtils.isNull(winningButtonEntity.getDeliveryAddressYn()))     winningButtonEntity.setDeliveryAddressYn(Boolean.FALSE);

            if (PredicateUtils.isNull(winningButtonEntity.getArEventWinningButtonId())) {
                log.error("arEventWinningButton is Null");
                throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GET_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GET_NULL);
            }

            if (winningButtonEntity.getDeliveryNameYn()) {
                if (PredicateUtils.isNull(reqDto.getName())) {
                    //이름이 공백 값 또는 null이러 에러 처리
                    resultCd = Integer.parseInt(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_MEMBER_NAME.getCode());
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCd));
                    throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_MEMBER_NAME.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_MEMBER_NAME);
                }
            }
            if (winningButtonEntity.getDeliveryBirthYn()) {
                if (!PredicateUtils.isStrLength(reqDto.getMemberBirth().trim(), 8)) {
                    //생년월일이 8자리가 아니면 에러처리
                    resultCd = Integer.parseInt(ResultCodeEnum.CUSTOM_ERROR_MEMBER_BIRTH_LENGTH.getCode());
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCd));
                    throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_MEMBER_BIRTH_LENGTH.getDesc(), ResultCodeEnum.CUSTOM_ERROR_MEMBER_BIRTH_LENGTH);
                } else {
                    if (EventUtils.isSpecialCharacter(reqDto.getMemberBirth().trim())) {
                        resultCd = Integer.parseInt(ResultCodeEnum.CUSTOM_ERROR_MEMBER_BIRTH_EXITS_SPECIAL_CHARACTER.getCode());
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCd));
                        throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_MEMBER_BIRTH_EXITS_SPECIAL_CHARACTER.getDesc(), ResultCodeEnum.CUSTOM_ERROR_MEMBER_BIRTH_EXITS_SPECIAL_CHARACTER);
                    }
                    if (PredicateUtils.isLowerThan(EventUtils.calculateAgeForKorean(reqDto.getMemberBirth()), 15)) {
                        //만 나이 15세 이하 에러처리
                        resultCd = Integer.parseInt(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_MEMBER_BIRTH_15.getCode());
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCd));
                        throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_MEMBER_BIRTH_15.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_MEMBER_BIRTH_15);
                    }
                }
            }
            if (winningButtonEntity.getDeliveryPhoneNumberYn()) {
//                if (PredicateUtils.isNull(reqDto.getPhoneNumber()) || !EventUtils.isPhoneNumber(aes256Utils.decrypt(reqDto.getPhoneNumber()))) {
                if (PredicateUtils.isNull(reqDto.getPhoneNumber())) {   //[DTWS-594]
                    //핸드폰번호 공백 값 또는 null 또는 핸드폰형식에 안맞으면 에러 처리
                    resultCd = Integer.parseInt(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_PHONE_NUMBER.getCode());
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCd));
                    throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_PHONE_NUMBER.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_PHONE_NUMBER);
                }
            }
            if (winningButtonEntity.getDeliveryAddressYn()) {
                if (PredicateUtils.isNull(reqDto.getAddress()) || PredicateUtils.isNull(reqDto.getAddressDetail()) || PredicateUtils.isNull(reqDto.getZipCode())) {
                    //핸드폰번호 공백 값 또는 null이러 에러 처리
                    resultCd = Integer.parseInt(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_ADDRESS.getCode());
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCd));
                    throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_ADDRESS.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SAVE_ADDRESS);
                }
            }
            try {
                stampFrontService.updateStampEventGiveAwayDelivery(ModelMapperUtils.convertModel(reqDto, StampEventGiveAwayDeliveryModel.class));
            } catch (DataIntegrityViolationException dve) {
                log.error("ConstraintViolationException {} ", dve.getMessage());
                throw new BaseException(ResultCodeEnum.CUSTOM_EVENT_LOG_WINNING_ID_DUPLICATE.getDesc(), ResultCodeEnum.CUSTOM_EVENT_LOG_WINNING_ID_DUPLICATE);
            } catch (Exception e) {
                //SQL에러시
                log.error("Exception {} ", e.getMessage());
                throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SQL_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GIVE_AWAY_SQL_ERROR);
            }

            if (PredicateUtils.isNotNullList(reqDto.getGiveAwayDeliveryButtonAddInputList())) {
                try {
                    stampFrontService.saveStampEventGiveAwayDeliveryButtonAddList(reqDto.getStpGiveAwayId(), reqDto.getGiveAwayDeliveryButtonAddInputList());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            resultMap.put("stpGiveAwayId", reqDto.getStpGiveAwayId());
        }
        return new ApiResultObjectDto().builder().result(resultMap).resultCode(resultCd).build();
    }

    public ApiResultObjectDto attendStampTrLocationLogic(StampTrLocationAttendReqDto reqDto) {
        if (PredicateUtils.isNull(reqDto.getStpPanTrId())) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR);
        }
        //스탬프 TR 조회
        StampEventPanTrModel stampPanTr = stampFrontService.findStampEventPanTrById(reqDto.getStpPanTrId());
        //스탬프 TR 없으면 에러처리
        if (PredicateUtils.isNull(stampPanTr.getStpPanTrId())) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_STAMP_TR_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_STAMP_TR_NULL);
        }
        //스탬프 TR 유형이 아니면 에러처리
        if (PredicateUtils.isNotEqualsStr(stampPanTr.getStpTrType(), StampTrTypeDefine.LOCATION.name())) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_STAMP_TR_TYPE.getDesc(), ResultCodeEnum.CUSTOM_ERROR_STAMP_TR_TYPE);
        }
        //PID 조회
        ProximityResDto proximityResDto = skApiLogic.callProximityApiLogic(reqDto.getStpTrPid(), reqDto.getLatitude(), reqDto.getLongitude());
        //PID 조회 후 tid 가 없으면 에러처리
        if (PredicateUtils.isNull(proximityResDto.getTid())) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_PID_NOT_LOCATION_MATCH.getDesc(), ResultCodeEnum.CUSTOM_ERROR_PID_NOT_LOCATION_MATCH);
        }
        //PID가 현재 위치에 존재하지 않으면 에러처리
        if (PredicateUtils.isEqualN(proximityResDto.getEventExist())) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_PID_NOT_LOCATION_MATCH.getDesc(), ResultCodeEnum.CUSTOM_ERROR_PID_NOT_LOCATION_MATCH);
        }
        //핸드폰번호 또는 참여코드가 없으면 에러처리
        if (PredicateUtils.isNull(reqDto.getAttendValue())) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR);
        }
        if (PredicateUtils.isEqualsStr(reqDto.getAttendType(), StampWinningAttendTypeDefine.MDN.name())) {
            reqDto.setAttendValue(aes256Utils.encrypt(reqDto.getAttendValue()));
        }

        try {
            stampFrontService.selectInsertStampEventTrLogAtPid(reqDto.getStpPanTrId(), reqDto.getStpTrPid(), reqDto.getAttendValue());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BaseException(ResultCodeEnum.SYSTEM_ERROR.getDesc(), ResultCodeEnum.SYSTEM_ERROR);
        }
        return new ApiResultObjectDto().builder()
                .result(reqDto)
                .resultCode(HttpStatus.OK.value())
                .build();
    }

    public ApiResultObjectDto checkStampAttendSortLogic(StampSortCheckReqDto reqDto) {
        Map<String, String>resultMap = new HashMap<>();

        if (PredicateUtils.isNull(reqDto.getStpId())) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR);
        }

        StampEventPanModel stampPan = stampFrontService.findStampEventPanByStpId(reqDto.getStpId());

        String statusCode = "", statusMSg = "";

        if (PredicateUtils.isNotNull(stampPan.getStpId())) {
            if (PredicateUtils.isEqualY(stampPan.getAttendSortSettingYn())) {
                int stampNumber = stampPan.getStpNumber();

                Integer lastSort = stampLogService.findStampTrLogLastSortByStpIdAndAttendValue(reqDto.getStpId(), reqDto.getAttendType(), reqDto.getAttendValue());

                //참여 로그에 하나도 없을때
                if (PredicateUtils.isNull(lastSort)) {
                    //제일 처음 참여하는 상태일때
                    if (PredicateUtils.isEqualNumber(reqDto.getStpTrSort(), 1)) {
                        //참여가능 상태
                        statusCode = "1";
                    } else {
                        //참여불가능 상태
                        statusCode = "0";
                    }
                } else if (PredicateUtils.isEqualNumber(lastSort, stampNumber)) {   //참여로그 번호가 스탬프 개수와 같으면 이미 참여 완료된 상태
                    //참여완료 상태
                    statusCode = "2";
                } else if (PredicateUtils.isGreaterThan(reqDto.getStpTrSort(), stampNumber)) {  //참여시도한 스탬프 번호가 스탬프 개수보다 크면 참여불가능 상태
                    //참여불가능 상태
                    statusCode = "0";
                } else {
                    if (PredicateUtils.isGreaterThanZero(reqDto.getStpTrSort())) {
                        if (PredicateUtils.isNull(lastSort)) {
                            //제일 처음 참여하는 상태일때
                            if (PredicateUtils.isEqualNumber(reqDto.getStpTrSort(), 1)) {
                                //참여가능 상태
                                statusCode = "1";
                            }
                        } else {
                            if (PredicateUtils.isEqualNumber((reqDto.getStpTrSort() + 1), stampNumber)
                                    || PredicateUtils.isEqualNumber(reqDto.getStpTrSort(), stampNumber))
                            {
                                //참여가능 상태
                                statusCode = "1";
                            } else if (PredicateUtils.isEqualNumber(lastSort, stampNumber)) {
                                //현재 이벤트 참여완료 상태
                                statusCode = "2";
                            } else if (PredicateUtils.isLowerThan(lastSort, stampNumber)) {
                                //참여가능 상태
                                statusCode = "1";
                            } else {
                                //참여불가 상태
                                statusCode = "0";
                            }
                        }
                    }
                }
            } else {
                //참여가능 상태
                statusCode = "1";
            }
        }

        if (PredicateUtils.isEqualsStr(statusCode, "0")) {
            statusMSg = "참여불가능";
        }
        if (PredicateUtils.isEqualsStr(statusCode, "1")) {
            statusMSg = "참여가능";
        }
        if (PredicateUtils.isEqualsStr(statusCode, "2")) {
            statusMSg = "참여완료";
        }
        resultMap.put("statusCode", statusCode);
        resultMap.put("statusMsg", statusMSg);

        return new ApiResultObjectDto().builder()
                .result(resultMap)
                .resultCode(HttpStatus.OK.value())
                .build();
    }

    public ApiResultObjectDto receiptStampGiveawayLogic(GiveAwayReceiptReqDto reqDto) {
        if (PredicateUtils.isNull(reqDto.getStpGiveAwayId())) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR);
        }
        Long stpGiveawayId = reqDto.getStpGiveAwayId();
        StampEventGiveAwayDeliveryModel giveAwayDeliveryModel = stampFrontService.findStampEventGiveAwayDeliveryById(stpGiveawayId);
        if (PredicateUtils.isNull(giveAwayDeliveryModel)) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GET_DATA_IS_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GET_DATA_IS_NULL);
        }
        try {
            stampFrontService.updateStampGiveawayIsReceive(stpGiveawayId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new ApiResultObjectDto().builder().resultCode(HttpStatus.OK.value()).build();
    }

    public ApiResultObjectDto validateStampAttendCodeLogic(String eventId, String attendCode) {
        //필수값 없으면 에러
        if (PredicateUtils.isNull(eventId) || PredicateUtils.isNull(attendCode)) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR);
        }
        StampEventGateCodeModel stampAttendCode = stampFrontService.findStampEventGateCodeByEventIdAndAttendCode(eventId, attendCode);
        //조회된 참여코드 없으면 에러처리
        if (PredicateUtils.isNull(stampAttendCode)) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_NOT_ATTEND_CODE.getDesc(), ResultCodeEnum.CUSTOM_ERROR_NOT_ATTEND_CODE);
        }
        try {
            stampFrontService.usedStampEventGateCode(stampAttendCode.getStpId(), stampAttendCode.getAttendCode());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        Map<String, Object>resultMap = new HashMap<>();
        resultMap.put("attendCode", attendCode);
        resultMap.put("isPossibleAttendCode", true);

        return new ApiResultObjectDto().builder().result(resultMap).resultCode(HttpStatus.OK.value()).build();
    }

    public StampGateDetailResDto getGateBaseDetail(StampGateDetailReqDto req){
        StampGateDetailResDto res = new StampGateDetailResDto();

        WebEventBaseEntity webEventBaseEntity = arEventService.findEventBase(req.getEventId());
        res.setEventBaseInfo(ModelMapperUtils.convertModel(webEventBaseEntity, StampEventBaseDto.class));

        StampEventMainModel stampMainModel = stampFrontService.findStampEventMainByEventId(req.getEventId());
        res.setStampMainInfo(ModelMapperUtils.convertModel(stampMainModel, StampEventMainDto.class));

        return res;
    }

    public StampGateDetailResDto getGateDetail(StampGateDetailReqDto req){
        StampGateDetailResDto res = getGateBaseDetail(req);

        if (PredicateUtils.isEqualN(res.getStampMainInfo().getStpMainSettingYn())) {
            return res;
        }

        ArEventButtonEntity arEventButtonEntity = arEventService.findArEventButtonByStpId(res.getStampMainInfo().getStpId());
        res.setStampButtonInfo(ModelMapperUtils.convertModel(arEventButtonEntity, EventButtonDto.class));

        List<ArEventHtmlEntity> arEventHtmlEntityList = arEventService.findByStpIdAndStpPanIdIsNullOrderByHtmlTypeSort(res.getStampMainInfo().getStpId());
        res.setStampHtmlInfo(ModelMapperUtils.convertModelInList(arEventHtmlEntityList, StampEventHtmlDto.class));

        return res;
    }

    public StampPanDetailResDto getPanDetail(StampPanDetailReqDto req){
        StampPanDetailResDto res = new StampPanDetailResDto(getGateBaseDetail(req));

        String eventId = res.getEventBaseInfo().getEventId();
        int panId = 0;

        String attendValue = req.getAttendValue();
        String encryptAttendValue = aes256Utils.encrypt(attendValue);

        // 판 정보 가져오기
        StampEventPanModel stampEventPan = stampFrontService.findStampEventPanByStpId(res.getStampMainInfo().getStpId());
        res.setStampPanInfo(ModelMapperUtils.convertModel(stampEventPan, StampEventPanDto.class));
        panId = res.getStampPanInfo().getStpPanId();

        // TR 리스트 가져오기
        List<StampEventPanTrModel> stampEventPanTrModelList = stampSodarService.findStampEventPanTrListByStpId(stampEventPan.getStpId());
        res.setStampTrList(ModelMapperUtils.convertModelInList(stampEventPanTrModelList, StampEventTrDto.class));

        //  TR 상태 / TR 이미지 찾기 - START
        List<ArEventWinningEntity> arEventWinningList = arEventService.findArEventWinningListByStpId(stampEventPan.getStpId());

        // TR 참여 로그 가져오기
        List<StampEventLogTrVO> stampEventTrLogList = null;
        if (PredicateUtils.isEqualsStr(res.getStampMainInfo().getStpAttendAuthCondition(), StampWinningAttendTypeDefine.MDN.name())) {
            stampEventTrLogList = stampLogService.findStampTrLogByStpPanId(res.getStampMainInfo().getStpAttendSortSettingYn(),
                    stampEventPan.getStpPanId(), res.getStampMainInfo().getStpAttendAuthCondition(), encryptAttendValue);
        } else if (PredicateUtils.isEqualsStr(res.getStampMainInfo().getStpAttendAuthCondition(), StampWinningAttendTypeDefine.ATTEND.name())) {
            stampEventTrLogList = stampLogService.findStampTrLogByStpPanId(res.getStampMainInfo().getStpAttendSortSettingYn(),
                    stampEventPan.getStpPanId(), res.getStampMainInfo().getStpAttendAuthCondition(), attendValue);
        }

        List<StampEventGiveAwayDeliveryModel> giveAwayList = null;
        if (PredicateUtils.isEqualsStr(res.getStampMainInfo().getStpAttendAuthCondition(), StampWinningAttendTypeDefine.MDN.name())) {
            giveAwayList = stampFrontService.findStampEventGiveAwayDeliveryByEventId(eventId, encryptAttendValue, res.getStampMainInfo().getStpAttendAuthCondition());
        } else if (PredicateUtils.isEqualsStr(res.getStampMainInfo().getStpAttendAuthCondition(), StampWinningAttendTypeDefine.ATTEND.name())) {
            giveAwayList = stampFrontService.findStampEventGiveAwayDeliveryByEventId(eventId, attendValue, res.getStampMainInfo().getStpAttendAuthCondition());
        }

        // 1) 이 이벤트와 매핑되어있는 당첨정보 중, 꽝에 대한 당첨 이미지부터 찾음.
        String failImageUrl = "";
        for (int i = 0; i < arEventWinningList.size(); i++) {
            if (PredicateUtils.isEqualsStr(arEventWinningList.get(i).getWinningType(), WinningTypeDefine.꽝.code())) {
                failImageUrl = arEventWinningList.get(i).getWinningImageUrl();
                break;
            }
        }

        res.setTrTotalCnt(res.getStampTrList().size());
        int trStampAfterCnt = 0;
        int trStampAfterNextCnt = 0;

        // 2) TR 루프 시작
        for (int i = 0; i < res.getStampTrList().size(); i++) {
            StampEventTrDto tr = res.getStampTrList().get(i);

            // 2-1) STAMP_BEFORE 로 상태 초기화
            tr.setStampStatus(StampStatusTypeDefine.STAMP_BEFORE.name());
            if (PredicateUtils.isEqualsStr(stampEventPan.getStpImgSettingType(), StampImgSettingTypeDefine.COMMON.name())) {
                tr.setStampDisplayImgUrl(res.getStampTrList().get(0).getStpTrNotAccImgUrl());
            } else {
                tr.setStampDisplayImgUrl(tr.getStpTrNotAccImgUrl());
            }

            ArEventWinningEntity connectWinningInfo = null;
            List<ArEventWinningEntity> connectWinningList = new ArrayList<>();
            if (PredicateUtils.isEqualsStr(res.getStampMainInfo().getStpWinningType(), StampWinningTypeDefine.Y.name())) {
                // 2-2) 내 스탬프와 연결되어있는 당첨 아이템 찾기 (당첨 아이템 리스트. 복수임 - 스탬프 2차)
                for (int j = 0; j < arEventWinningList.size(); j++) {
                    ArEventWinningEntity winningInfo = arEventWinningList.get(j);
                    if (PredicateUtils.isEqualN(res.getStampMainInfo().getStpAttendSortSettingYn())) {
                        // 참여순번이 없는 경우 (TR 맵핑 기준)
                        if (tr.getStpTrSort().equals(winningInfo.getObjectMappingNumber())) {
                            connectWinningList.add(winningInfo);
                        }
                    } else {
                        // 참여순번이 있는 경우
//                        connectWinningList.add(winningInfo);
                        Integer attendSort = -1;
                        for (int k = 0; k < stampEventTrLogList.size(); k++) {
                            if (tr.getStpPanTrId().equals(stampEventTrLogList.get(k).getStpPanTrId())) {
                                attendSort = k + 1;
                                break;
                            }
                        }

                        if (attendSort != -1) {
                            if (attendSort.equals(winningInfo.getObjectMappingNumber())) {
                                connectWinningList.add(winningInfo);
                            }
                        }
                    }
                }

                // 2-3) 당첨 성공 찾기
                if (PredicateUtils.isNotNullList(connectWinningList)) { // 당첨시도가 가능한 스탬프인 경우
                    // 이 TR 에 대한 당첨정보 입력 데이터를 찾음. 이 경우 당첨 성공으로 판단
                    for (int j = 0; j < giveAwayList.size(); j++) {
                        StampEventGiveAwayDeliveryModel giveAwayInfo = giveAwayList.get(j);
                        if (tr.getStpPanTrId().equals(giveAwayInfo.getStpPanTrId())) {
                            trStampAfterCnt = trStampAfterCnt + 1;
                            tr.setStampStatus(StampStatusTypeDefine.GIVEAWAY_SUCCESS.name());
                            for(int k = 0; k < connectWinningList.size(); k++){
                                if(giveAwayInfo.getArEventWinningId().equals(connectWinningList.get(k).getArEventWinningId())){
                                    connectWinningInfo = connectWinningList.get(k);
                                    break; // 정확히 연결된 당첨정보를 찾았으므로 break
                                }
                            }
                            if(PredicateUtils.isNotNull(connectWinningInfo)) {
                                tr.setStampDisplayImgUrl(connectWinningInfo.getWinningImageUrl());
                            }
                            break;  // 당첨 성공 데이터 찾았으므로 break;
                        }
                    }

                    if (PredicateUtils.isEqualsStr(tr.getStampStatus(), StampStatusTypeDefine.GIVEAWAY_SUCCESS.name())) {
                        continue;   // 이 TR 은 상태값을 찾았으므로 다음 TR 로 continue
                    }
                }
            }

            // 2-4) 그 외 스탬프 찍음 (당첨시도불가) / 당첨실패 / 스탬프 찍음 (당첨시도가능) 찾기
            for (int j = 0; j < stampEventTrLogList.size(); j++) {
                StampEventLogTrVO trLog = stampEventTrLogList.get(j);

                if (Objects.equals(tr.getStpPanTrId(), trLog.getStpPanTrId())) {  // stampEventLog 가 있으면, 어떤형태로든 일단 스탬프를 찍은것임 - 2024.04.16 Objects.equals 변경 작성자 : 안지호
                    tr.setStpEventLogTrId(trLog.getStpEventLogTrId());  // 찾은 stpEventLogTrId 를 담아서 내려보내줘야됨.

                    if (PredicateUtils.isNullList(connectWinningList)) {    // 이때 연결된 당첨정보가 없으면 스탬프 찍기는 완료되었으며 당첨시도 불가능한 것으로 판단
                        trStampAfterCnt = trStampAfterCnt + 1;
                        tr.setStampStatus(StampStatusTypeDefine.STAMP_AFTER_END.name());
                        if (PredicateUtils.isEqualsStr(stampEventPan.getStpImgSettingType(), StampImgSettingTypeDefine.COMMON.name())) {
                            tr.setStampDisplayImgUrl(res.getStampTrList().get(0).getStpTrAccImgUrl());
                        }
                        else{
                            tr.setStampDisplayImgUrl(tr.getStpTrAccImgUrl());
                        }

                        break;  // TR 상태를 찾았으므로 break;
                    }

                    if (trLog.getIsClick() > 0) {  // 스탬프를 찍긴 찍었으되, 연결된 당첨은 있는데 당첨정보 입력 데이터가 없고 isClick 이 0 보다 크면 스탬프 찍고난 이후에 추가 클릭이 시도된 것으로 당첨실패 데이터로 간주
                        trStampAfterCnt = trStampAfterCnt + 1;
                        tr.setStampStatus(StampStatusTypeDefine.GIVEAWAY_FAIL.name());
                        tr.setStampDisplayImgUrl(failImageUrl);
                        break;  // TR 상태를 찾았으므로 break;
                    }

                    trStampAfterCnt = trStampAfterCnt + 1;
                    trStampAfterNextCnt = trStampAfterNextCnt + 1;
                    tr.setStampStatus(StampStatusTypeDefine.STAMP_AFTER_NEXT.name());   // 스탬프는 찍혔는데 당첨정보 데이터도 없고 isClick 도 0 인 상태이므로 당첨 시도 가능한 상태로 판단
                    if (PredicateUtils.isEqualsStr(stampEventPan.getStpImgSettingType(), StampImgSettingTypeDefine.COMMON.name())) {
                        tr.setStampDisplayImgUrl(res.getStampTrList().get(0).getStpTrWinningAttendImgUrl());
                    } else {
                        tr.setStampDisplayImgUrl(tr.getStpTrWinningAttendImgUrl());
                    }
                }
            }
        }

        // 참여순번 순서대로 당첨시키는 경우
        if (PredicateUtils.isEqualY(res.getStampMainInfo().getStpAttendSortSettingYn())) {
            int nextWinningAttendRemainingCnt = 0;

            int stampAfterCnt = res.getStampTrList().stream()
                    .filter(tr -> PredicateUtils.isNotEqualsStr(tr.getStampStatus(), StampStatusTypeDefine.STAMP_BEFORE.name()))    // STAMP_BEFORE 가 아닌 count = 스탬프를 찍은 count
                    .collect(Collectors.counting())// 갯수 세기 (Long 으로 반환함)
                    .intValue();    // int 반환

            List<Integer> nextAttendMappingNumberList = arEventWinningList.stream()
                    .filter(info -> info.getObjectMappingNumber() != 0 && info.getObjectMappingNumber() > stampAfterCnt)    // mappingNumber == 0 은 미선택 (꽝) / 현재 스탬프 찍힌 갯수보다 많은 맵핑만 추림
                    .map(ArEventWinningEntity::getObjectMappingNumber)
                    .distinct() // 중복제거
                    .sorted()   // 오름차순 정렬
                    .collect(Collectors.toList());  // 리스트 반환

            int afterAttendMapping = 0;

            if (PredicateUtils.isNotNullList(nextAttendMappingNumberList)) {
                afterAttendMapping = nextAttendMappingNumberList.get(0).intValue(); // 정렬되있으므로 0번째 데이터가 다음에 당첨시도할 순번임
            }

            if (afterAttendMapping != 0) { // afterAttendMapping == 0 인 경우 마지막 순번까지 참여가 끝남
                nextWinningAttendRemainingCnt = afterAttendMapping - stampAfterCnt;
                res.setNextWinningAttendRemainingCnt(nextWinningAttendRemainingCnt);
            }
        }

        res.setTrStampAfterCnt(trStampAfterCnt);
        res.setTrStampAfterNextCnt(trStampAfterNextCnt);
        //  TR 상태 / TR 이미지 찾기 - END

        List<ArEventHtmlEntity> arEventHtmlEntityList = arEventService.findArEventHtmlListByStpPanIdOrderByHtmlTypeSort(panId);
        res.setStampHtmlInfo(ModelMapperUtils.convertModelInList(arEventHtmlEntityList, StampEventHtmlDto.class));

        return res;
    }

    /*
    public StampPanDetailResDto getPanDetailBackup(StampPanDetailReqDto req){
        StampPanDetailResDto res = new StampPanDetailResDto(getGateBaseDetail(req));

        String eventId = res.getEventBaseInfo().getEventId();
        int panId = 0;

        String attendValue = req.getAttendValue();
        String encryptAttendValue = aes256Utils.encrypt(attendValue);

        // 판 정보 가져오기
        StampEventPanModel stampEventPan = stampFrontService.findStampEventPanByStpId(res.getStampMainInfo().getStpId());
        res.setStampPanInfo(ModelMapperUtils.convertModel(stampEventPan, StampEventPanDto.class));
        panId = res.getStampPanInfo().getStpPanId();

        // TR 리스트 가져오기
        List<StampEventPanTrModel> stampEventPanTrModelList = stampSodarService.findStampEventPanTrListByStpId(stampEventPan.getStpId());
        res.setStampTrList(ModelMapperUtils.convertModelInList(stampEventPanTrModelList, StampEventTrDto.class));

        //  TR 상태 / TR 이미지 찾기 - START
        List<ArEventWinningEntity> arEventWinningList = arEventService.findArEventWinningListByStpId(stampEventPan.getStpId());

        List<StampEventLogTrVO> stampEventTrLogList = null;
        if (PredicateUtils.isEqualsStr(res.getStampMainInfo().getStpAttendAuthCondition(), StampWinningAttendTypeDefine.MDN.name())) {
            stampEventTrLogList = stampLogService.findStampTrLogByStpPanId(stampEventPan.getStpPanId(), res.getStampMainInfo().getStpAttendAuthCondition(), encryptAttendValue);
        } else if (PredicateUtils.isEqualsStr(res.getStampMainInfo().getStpAttendAuthCondition(), StampWinningAttendTypeDefine.ATTEND.name())) {
            stampEventTrLogList = stampLogService.findStampTrLogByStpPanId(stampEventPan.getStpPanId(), res.getStampMainInfo().getStpAttendAuthCondition(), attendValue);
        }

        List<StampEventGiveAwayDeliveryModel> giveAwayList = null;
        if (PredicateUtils.isEqualsStr(res.getStampMainInfo().getStpAttendAuthCondition(), StampWinningAttendTypeDefine.MDN.name())) {
            giveAwayList = stampFrontService.findStampEventGiveAwayDeliveryByEventId(eventId, encryptAttendValue, StampWinningAttendTypeDefine.MDN.name());
        } else if (PredicateUtils.isEqualsStr(res.getStampMainInfo().getStpAttendAuthCondition(), StampWinningAttendTypeDefine.ATTEND.name())) {
            giveAwayList = stampFrontService.findStampEventGiveAwayDeliveryByEventId(eventId, attendValue, StampWinningAttendTypeDefine.ATTEND.name());
        }

        // 1) 이 이벤트와 매핑되어있는 당첨정보 중, 꽝에 대한 당첨 이미지부터 찾음.
        String failImageUrl = "";
        for (int i = 0; i < arEventWinningList.size(); i++) {
            if (PredicateUtils.isEqualsStr(arEventWinningList.get(i).getWinningType(), WinningTypeDefine.꽝.code())) {
                failImageUrl = arEventWinningList.get(i).getWinningImageUrl();
                break;
            }
        }

        res.setTrTotalCnt(res.getStampTrList().size());
        int trStampAfterCnt = 0;
        int trStampAfterNextCnt = 0;

        // 2) TR 루프 시작
        for (int i = 0; i < res.getStampTrList().size(); i++) {
            StampEventTrDto tr = res.getStampTrList().get(i);

            // 2-1) STAMP_BEFORE 로 상태 초기화
            tr.setStampStatus(StampStatusTypeDefine.STAMP_BEFORE.name());
            if (PredicateUtils.isEqualsStr(stampEventPan.getStpImgSettingType(), StampImgSettingTypeDefine.COMMON.name())) {
                tr.setStampDisplayImgUrl(res.getStampTrList().get(0).getStpTrNotAccImgUrl());
            } else {
                tr.setStampDisplayImgUrl(tr.getStpTrNotAccImgUrl());
            }

            // 2-2) 내 스탬프와 연결되어있는 당첨 아이템 찾기
            ArEventWinningEntity connectWinningInfo = null;
            for (int j = 0; j < arEventWinningList.size(); j++) {
                ArEventWinningEntity winningInfo = arEventWinningList.get(j);
                if (tr.getStpTrSort().equals(winningInfo.getObjectMappingNumber())) {
                    connectWinningInfo = winningInfo;
                    break;  // 당첨 정보 찾았으므로 break
                }
            }

            // 2-3) 당첨 성공 찾기
            if (PredicateUtils.isNotNull(connectWinningInfo)) { // 당첨시도가 가능한 스탬프인 경우
                // 이 TR 에 대한 당첨정보 입력 데이터를 찾음. 이 경우 당첨 성공으로 판단
                for (int j = 0; j < giveAwayList.size(); j++) {
                    StampEventGiveAwayDeliveryModel giveAwayInfo = giveAwayList.get(j);
                    if (tr.getStpPanTrId().equals(giveAwayInfo.getStpPanTrId())) {
                        trStampAfterCnt = trStampAfterCnt + 1;
                        tr.setStampStatus(StampStatusTypeDefine.GIVEAWAY_SUCCESS.name());
                        tr.setStampDisplayImgUrl(connectWinningInfo.getWinningImageUrl());
                        break;  // 당첨 성공 데이터 찾았으므로 break;
                    }
                }

                if (PredicateUtils.isEqualsStr(tr.getStampStatus(), StampStatusTypeDefine.GIVEAWAY_SUCCESS.name())) {
                    continue;   // 이 TR 은 상태값을 찾았으므로 다음 TR 로 continue
                }
            }

            // 2-4) 그 외 스탬프 찍음 (당첨시도불가) / 당첨실패 / 스탬프 찍음 (당첨시도가능) 찾기
            for (int j = 0; j < stampEventTrLogList.size(); j++) {
                StampEventLogTrVO trLog = stampEventTrLogList.get(j);

                if (Objects.equals(tr.getStpPanTrId(), trLog.getStpPanTrId())) {  // stampEventLog 가 있으면, 어떤형태로든 일단 스탬프를 찍은것임 - 2024.04.16 Objects.equals 변경 작성자 : 안지호
                    tr.setStpEventLogTrId(trLog.getStpEventLogTrId());  // 찾은 stpEventLogTrId 를 담아서 내려보내줘야됨.

                    if (PredicateUtils.isNull(connectWinningInfo)) {    // 이때 연결된 당첨정보가 없으면 스탬프 찍기는 완료되었으며 당첨시도 불가능한 것으로 판단
                        trStampAfterCnt = trStampAfterCnt + 1;
                        tr.setStampStatus(StampStatusTypeDefine.STAMP_AFTER_END.name());
                        if (PredicateUtils.isEqualsStr(stampEventPan.getStpImgSettingType(), StampImgSettingTypeDefine.COMMON.name())) {
                            tr.setStampDisplayImgUrl(res.getStampTrList().get(0).getStpTrAccImgUrl());
                        }
                        else{
                            tr.setStampDisplayImgUrl(tr.getStpTrAccImgUrl());
                        }

                        break;  // TR 상태를 찾았으므로 break;
                    }

                    if (trLog.getIsClick() > 0) {  // 스탬프를 찍긴 찍었으되, 연결된 당첨은 있는데 당첨정보 입력 데이터가 없고 isClick 이 0 보다 크면 스탬프 찍고난 이후에 추가 클릭이 시도된 것으로 당첨실패 데이터로 간주
                        trStampAfterCnt = trStampAfterCnt + 1;
                        tr.setStampStatus(StampStatusTypeDefine.GIVEAWAY_FAIL.name());
                        tr.setStampDisplayImgUrl(failImageUrl);
                        break;  // TR 상태를 찾았으므로 break;
                    }

                    trStampAfterCnt = trStampAfterCnt + 1;
                    trStampAfterNextCnt = trStampAfterNextCnt + 1;
                    tr.setStampStatus(StampStatusTypeDefine.STAMP_AFTER_NEXT.name());   // 스탬프는 찍혔는데 당첨정보 데이터도 없고 isClick 도 0 인 상태이므로 당첨 시도 가능한 상태로 판단
                    if (PredicateUtils.isEqualsStr(stampEventPan.getStpImgSettingType(), StampImgSettingTypeDefine.COMMON.name())) {
                        tr.setStampDisplayImgUrl(res.getStampTrList().get(0).getStpTrWinningAttendImgUrl());
                    } else {
                        tr.setStampDisplayImgUrl(tr.getStpTrWinningAttendImgUrl());
                    }
                }
            }
        }
        res.setTrStampAfterCnt(trStampAfterCnt);
        res.setTrStampAfterNextCnt(trStampAfterNextCnt);
        //  TR 상태 / TR 이미지 찾기 - END

        List<ArEventHtmlEntity> arEventHtmlEntityList = arEventService.findArEventHtmlListByStpPanIdOrderByHtmlTypeSort(panId);
        res.setStampHtmlInfo(ModelMapperUtils.convertModelInList(arEventHtmlEntityList, StampEventHtmlDto.class));

        return res;
    }
     */

    public StampMainInfoResDto getStampMainInfo(StampMainInfoReqDto req) {
        StampMainInfoResDto res = new StampMainInfoResDto();

        StampEventMainModel stampMainModel = stampFrontService.findStampEventMainByEventId(req.getEventId());
        if (PredicateUtils.isEqualY(stampMainModel.getStpMainSettingYn())) {
            res.setStpMainSettingYn(stampMainModel.getStpMainSettingYn());
            return res;
        }

        StampEventPanModel stampEventPan = stampFrontService.findStampEventPanByStpId(stampMainModel.getStpId());

        List<StampEventPanTrModel> stampEventPanTrModelList = stampSodarService.findStampEventPanTrListByStpId(stampEventPan.getStpId());

        for (int i = 0; i < stampEventPanTrModelList.size(); i++) {
            StampEventPanTrModel trInfo = stampEventPanTrModelList.get(i);
            if (PredicateUtils.isEqualsStr(trInfo.getStpTrType(), StampTrTypeDefine.EVENT.name())) {
                res.setStpTrEventId(trInfo.getStpTrEventId());
                break;
            }
        }

        return res;
    }
}
