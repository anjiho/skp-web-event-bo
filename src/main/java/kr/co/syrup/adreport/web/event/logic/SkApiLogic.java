package kr.co.syrup.adreport.web.event.logic;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.exception.BaseException;
import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.service.adreport.ApiHelperService;
import kr.co.syrup.adreport.web.event.define.ErrorCodeDefine;
import kr.co.syrup.adreport.web.event.define.GifticonApiPathDefine;
import kr.co.syrup.adreport.web.event.define.OcbPointSaveTypeDefine;
import kr.co.syrup.adreport.web.event.define.SmsMessageDefine;
import kr.co.syrup.adreport.web.event.dto.request.GifticonOrderReqDto;
import kr.co.syrup.adreport.web.event.dto.request.ProximityApiReqDto;
import kr.co.syrup.adreport.web.event.dto.request.SmsTestUrlReqDto;
import kr.co.syrup.adreport.web.event.dto.request.api.ProximityDocentApiReqDto;
import kr.co.syrup.adreport.web.event.dto.response.*;
import kr.co.syrup.adreport.web.event.dto.response.api.OcbPointApiResDto;
import kr.co.syrup.adreport.web.event.dto.response.api.OcbSessionApiResDto;
import kr.co.syrup.adreport.web.event.entity.ArEventEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventWinningEntity;
import kr.co.syrup.adreport.web.event.entity.OcbPointSaveEntity;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import kr.co.syrup.adreport.web.event.service.LogService;
import kr.co.syrup.adreport.web.event.service.OcbApiService;
import kr.co.syrup.adreport.web.event.service.SodarApiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class SkApiLogic {

    private final int httpSuccessCode = HttpStatus.OK.value();

    @Value("${domain.sordar.internal}")
    private String smsSendUrl;

    @Value("${bsm.search.domain}")
    private String bsmDomain;

    @Value("${bsm.search.uri}")
    private String bsmUri;

    @Value("${bsm.search.absolute.uri}")
    private String bsmAbsoluteUri;

    @Value("${bsm.auth.key}")
    private String bsmAuthKey;

    @Value("${gifticon.domain}")
    private String gifticonDomain;

    @Value("${web.event.domain}")
    private String webEventDomain;

    @Autowired
    private ApiHelperService apiHelperService;

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private SodarApiService sodarApiService;

    @Autowired
    private OcbApiService ocbApiService;

    @Autowired
    private LogService logService;

    /**
     * 위치기반 이벤트 조회 api
     * @param eventId
     * @param latitude
     * @param longitude
     * @return
     */
    public ApiResultObjectDto callProximityApi(String eventId, String latitude, String longitude) {
        int resultCode = httpSuccessCode;

        ProximityResDto proximityResDto = null;

        if (StringUtils.isEmpty(eventId)) {

            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

        } else {
            String apiUrl = StringTools.joinStringsNoSeparator(bsmDomain, bsmUri);

            ArEventEntity arEvent = arEventService.findArEventByEventId(eventId);

            //AR_EVENT 정보가 없으면 에러코드
            if (arEvent == null) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_AR_EVENT_INFO_NULL.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
            }

            //AR_EVENT 정보가 있으면 정상 로직 처리
            if (arEvent != null) {
                ProximityApiReqDto condition = ProximityApiReqDto.condition(arEvent.getPid(), latitude, longitude);
                //프록시미티 API 콜
                proximityResDto = apiHelperService.callGetApi(apiUrl, condition, getProximityApiHeaders(eventId), ProximityResDto.class);
            }

        }

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(PredicateUtils.isNull(proximityResDto) ? "resultNull!": proximityResDto)
                .build();
    }

    public ProximityResDto callProximityApiLogic(String pid, String latitude, String longitude) {

        if (StringUtils.isEmpty(pid)) {
            log.error(ErrorCodeDefine.getLogErrorMessage(ErrorCodeDefine.CUSTOM_ERROR_PID_NULL.code()));
            return null;
        } else {
            String apiUrl = StringTools.joinStringsNoSeparator(bsmDomain, bsmUri);

            ProximityApiReqDto condition = ProximityApiReqDto.condition(pid, latitude, longitude);
            return apiHelperService.callGetApi(apiUrl, condition, getProximityApiHeaders(pid), ProximityResDto.class);
        }
    }

    /**
     * 프록시미티 절대좌표 정보 통신 로직
     * @param reqDto
     * @return
     */
    public ApiResultObjectDto callProximityDocentApiLogic(ProximityDocentApiReqDto reqDto) {
        int resultCode = httpSuccessCode;

        //필수값 없으면 에러처리
        if (PredicateUtils.isNull(reqDto.getEventId())) {
            log.error(ErrorCodeDefine.getLogErrorMessage(ErrorCodeDefine.CUSTOM_ERROR_PID_NULL.code()));
            throw new BaseException(ResultCodeEnum.PARAMETER_ERROR.getDesc(), ResultCodeEnum.PARAMETER_ERROR);
        }

        String apiUrl = StringTools.joinStringsNoSeparator(bsmDomain, bsmAbsoluteUri);
        //파라미터 만들기
        ProximityApiReqDto condition = ProximityApiReqDto.conditionByAbsoluteCoordinates(ModelMapperUtils.convertModel(reqDto, ProximityApiReqDto.class));
        //api call
        Object resObj = apiHelperService.callGetApi(apiUrl, condition, getProximityApiHeaders(reqDto.getEventId()), Object.class);

        return new ApiResultObjectDto().builder()
                .result(resObj)
                .resultCode(resultCode)
                .build();
    }

    /**
     * 기프티콘 발송하기 api
     * @param arEventWinningId
     * @param rcvrMdn
     * @return
     */
    @Deprecated
    public ApiResultObjectDto sendGifticonLogic(String eventId, int arEventWinningId, String rcvrMdn) {
        int resultCode = httpSuccessCode;
        //당첨 기본 정보 가져오기
        ArEventWinningEntity arEventWinningEntity = arEventService.findByArEventWinningById(arEventWinningId);
        //reqParam 만들기
        GifticonOrderReqDto condition = GifticonOrderReqDto.condition(
                eventId, arEventWinningEntity.getGifticonCampaignId(), arEventWinningEntity.getGifticonProductCode(), arEventWinningId, rcvrMdn
        );
        //기프티콘 api url 빌더
        String apiUrl = StringTools.joinStringsNoSeparator(gifticonDomain, GifticonApiPathDefine.주문.path());
        //api call
        GifticonOrderResDto gifticonOrderResDto = apiHelperService.callPostApi(apiUrl, condition, GifticonOrderResDto.class);

        return new ApiResultObjectDto().builder()
                .result(gifticonOrderResDto)
                .resultCode(resultCode)
                .build();
    }

    /**
     * 당첨정보 입력 후 기프티콘 지급하기
     * @param eventId
     * @param arEventWinningId
     * @param rcvrMdn
     * @return
     */
    public GifticonOrderResDto sendGifticonAtGiveAwayLogic(String eventId, int arEventWinningId, String rcvrMdn) {
        //당첨 기본 정보 가져오기
        ArEventWinningEntity arEventWinningEntity = arEventService.findByArEventWinningById(arEventWinningId);
        //reqParam 만들기
        GifticonOrderReqDto condition = GifticonOrderReqDto.condition(
                eventId, arEventWinningEntity.getGifticonCampaignId(), arEventWinningEntity.getGifticonProductCode(), arEventWinningId, rcvrMdn
        );
        //기프티콘 api url 빌더
        String apiUrl = StringTools.joinStringsNoSeparator(gifticonDomain, GifticonApiPathDefine.주문.path());
        //api call
        return apiHelperService.callPostApi(apiUrl, condition, GifticonOrderResDto.class);
    }

    /**
     * 테스트 url sms 발송
     * @param reqDto
     * @return
     */
    public ApiResultObjectDto callSmsTestUrlLogic(SmsTestUrlReqDto reqDto) {
        int resultCode = httpSuccessCode;

        Object resultObj = null;
        if (StringUtils.isEmpty(reqDto.getEventId())) {

            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

        }

        if (StringUtils.isNotEmpty(reqDto.getEventId())) {

            String smsTestUrl = FileUtils.concatPath("https://", webEventDomain, "web-event", "main.html?eventId=" + reqDto.getEventId());

            resultObj = sodarApiService.sendSodarSms("/v3/web-event/testSend", reqDto.getPhoneNumber(), smsTestUrl, Object.class);

            log.info("resultObj {} ", resultObj);

            if (PredicateUtils.isNull(resultObj.toString())) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_NULL_TEST_SMS_RESULT.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
            }

            //sms 발송 api 결과가 에러일때
            if (StringTools.containsIgnoreCase(resultObj.toString(),"error")) {

                Gson gson = new Gson();
                String resultStr = gson.toJson(resultObj);
                JsonElement element = gson.fromJson(resultStr, JsonElement.class);
                JsonObject jsonObject = element.getAsJsonObject();
                JsonElement resultElement = jsonObject.get("error");

                JsonObject resultJsonObject = resultElement.getAsJsonObject();
                String id = resultJsonObject.get("id").getAsString();

                //sms 발송 api 에러일때 에러 코드 파싱
                resultCode = Integer.parseInt(id);
                log.error("testSend error {} ", resultCode);
            }

            //sms 발송 api 결과가 정상일때
            if (!StringTools.containsIgnoreCase(resultObj.toString(),"error")) {
                String code = GsonUtils.parseStringJsonStr(resultObj.toString(), "code");

                resultCode = Integer.parseInt(code);
                log.info("testSend {} ", resultCode);
            }
        }

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultObj)
                .build();
    }

    /**
     * 비밀번호 변경 sms 발송
     * @param phoneNumber
     * @param newPassword
     * @return
     */
    @Deprecated
    public boolean callSmsChangePassword(String phoneNumber, String newPassword) {
        if (StringUtils.isEmpty(phoneNumber)) {
            throw new BaseException(ResultCodeEnum.PARAMETER_ERROR.getDesc(), ResultCodeEnum.PARAMETER_ERROR);
        }

        if (StringUtils.isNotEmpty(phoneNumber)) {

            String smsContent = SmsMessageDefine.NEW_PASS.content().replace("{newPass}", newPassword);

            Object resultObj = sodarApiService.sendSodarSms("/v3/web-event/sendSms", phoneNumber, smsContent, Object.class);

            log.info("resultObj {} ", resultObj);

            if (PredicateUtils.isNull(resultObj.toString())) {
                throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_NULL_TEST_SMS_RESULT.getDesc(), ResultCodeEnum.CUSTOM_ERROR_NULL_TEST_SMS_RESULT);
            }

            //sms 발송 api 결과가 에러일때
            if (resultObj.toString().contains("error")) {

                Gson gson = new Gson();
                String resultStr = gson.toJson(resultObj);
                JsonElement element = gson.fromJson(resultStr, JsonElement.class);
                JsonObject jsonObject = element.getAsJsonObject();
                JsonElement resultElement = jsonObject.get("error");

                JsonObject resultJsonObject = resultElement.getAsJsonObject();
                String id = resultJsonObject.get("id").getAsString();

                //sms 발송 api 에러일때 에러 코드 파싱
                log.error("testSend error {} ", resultObj);
                return false;
            }

            //sms 발송 api 결과가 정상일때
            if (!resultObj.toString().contains("error")) {
                String code = GsonUtils.parseStringJsonStr(resultObj.toString(), "code");

                log.info("testSend {} ", resultObj);
                return true;
            }
        }
        return false;
    }

    /**
     * OCB 사용자 정보 (mbrId, mdn, birthday) 가져오기 API
     * @param partnerToken
     * @return
     */
    public ApiResultObjectDto getOcbSessionLogic(String partnerToken) {
        int resultCode = httpSuccessCode;

        //파트너 토큰이 없으면 에러코드 처리
        if (PredicateUtils.isNull(partnerToken)) {
            log.error(ErrorCodeDefine.getLogErrorMessage(ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code()));
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_OCB_SESSION_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_OCB_SESSION_NULL);
        }

        //OCB 회원조회 API 통신
        OcbSessionApiResDto resDto = ocbApiService.getOcbSessionApi(partnerToken);

        //OCB 회원조회 API 통신 결과 값에 mbrId 가 없으면 에러코드 처리
        if (PredicateUtils.isNull(resDto.getMbrId())) {
            log.error(ErrorCodeDefine.getLogErrorMessage(ErrorCodeDefine.CUSTOM_ERROR_OCB_MBR_ID_NULL.code()));
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_OCB_MBR_ID_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_OCB_MBR_ID_NULL);
        }
        return new ApiResultObjectDto().builder()
                .result(resDto)
                .resultCode(resultCode)
                .build();
    }

    /**
     * OCB 포인트 지급 API - 참여전
     * @param eventId
     * @param partnerToken
     * @return
     */
    @Transactional
    public ApiResultObjectDto requestOcbPointSaveLogic(String eventId, String partnerToken) {
        int resultCode = httpSuccessCode;

        OcbPointApiResDto resDto = new OcbPointApiResDto();
        //필수 파라미터가 없으면 에러코드 처리
        if (PredicateUtils.isNull(eventId) && PredicateUtils.isNull(partnerToken)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        } else {
            //OCB 세션 API 콜
            OcbSessionApiResDto ocbSessionInfo = ocbApiService.getOcbSessionApi(partnerToken);

            //OCB 세션값이 없으면 에러 처리
            if (PredicateUtils.isNull(ocbSessionInfo.getMbrId())) {
                log.error(ErrorCodeDefine.getLogErrorMessage(ErrorCodeDefine.CUSTOM_ERROR_PID_NULL.code()));
                throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_OCB_SESSION_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_OCB_SESSION_NULL);
            }
            //ar_event 기본정보 가져오기
            ArEventEntity arEventEntity = arEventService.findArEventByEventId(eventId);

            //포인트 적립정보 여부 확인 - 적립가능한 상태가 아니면 에러코드 처리
            if (PredicateUtils.isEqualsStr(arEventEntity.getOcbPointSaveType(), OcbPointSaveTypeDefine.NONE.name())) {
                log.error(ErrorCodeDefine.getLogErrorMessage(ErrorCodeDefine.CUSTOM_ERROR_OCB_POINT_SAVE_STATUS.code()));
                throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_OCB_POINT_SAVE_STATUS.getDesc(), ResultCodeEnum.CUSTOM_ERROR_OCB_POINT_SAVE_STATUS);
            }

            //OCB 포인트를 지급할 데이터 조회
            OcbPointSaveEntity ocbPointSaveEntity = arEventService.findOcbPointSaveByArEventIdAndArEventWinningId(arEventEntity.getArEventId(), null);
            //OCB 포인트를 지급할 데이터가 없으면 에러처리
            if (PredicateUtils.isNull(ocbPointSaveEntity.getId())) {
                log.error(ErrorCodeDefine.getLogErrorMessage(ErrorCodeDefine.CUSTOM_ERROR_OCB_SAVE_INFO_NULL.code()));
                throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_OCB_SAVE_INFO_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_OCB_SAVE_INFO_NULL);
            }

            boolean isToday = false;
            //1일기준
            if (PredicateUtils.isEqualNumber(ocbPointSaveEntity.getSaveTermType(), 1)) {
               isToday = true;
            }
            //총 로그 개수
            int totalCount = logService.getCountOcbLogPointSaveByEventId(eventId, isToday);

            log.info("getSaveMaxCustomerCount >> " + ocbPointSaveEntity.getSaveMaxCustomerCount());
            log.info("totalCount >> " + totalCount);

            //전체 총개수 지급이 오바되었을때 에러처리
            if (PredicateUtils.isGreaterThanEqualTo(totalCount, ocbPointSaveEntity.getSaveMaxCustomerCount())) {
                log.error(ErrorCodeDefine.getLogErrorMessage(ErrorCodeDefine.CUSTOM_ERROR_OCB_POINT_LIMIT_COUNT.code()));
                throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_OCB_POINT_LIMIT_COUNT.getDesc(), ResultCodeEnum.CUSTOM_ERROR_OCB_POINT_LIMIT_COUNT);
            }

            //이미 지급된 로그 테이블의 개수조회 - 전체기간 또는 1일
            long pointSaveCount = logService.getCountOcbLogPointSaveByEventIdAndOcbPointSaveIdAndPhoneNumber(eventId, ocbPointSaveEntity.getId(), ocbSessionInfo.getMdn(), isToday);

            //셋팅된 개수보다 이미 지급된 로그 개수가 초과되었을때 에러코드 처리
            if (PredicateUtils.isGreaterThanEqualTo((int)pointSaveCount, 1)) {
                log.error(ErrorCodeDefine.getLogErrorMessage(ErrorCodeDefine.CUSTOM_ERROR_OCB_POINT_SAVE_ALREADY.code()));
                throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_OCB_POINT_SAVE_ALREADY.getDesc(), ResultCodeEnum.CUSTOM_ERROR_OCB_POINT_SAVE_ALREADY);
            }

            //mbrId로 포인트 적립된 기록이 있는지 확인 - 전체기간 또는 1일
            boolean isMbrIdPointSaved = logService.isOcbLogPointSaveMbrId(eventId, ocbPointSaveEntity.getId(), ocbSessionInfo.getMbrId(), isToday);

            //mbrId로 포인트 적립된 기록이 있으면 에러코드 처리
            if (isMbrIdPointSaved) {
                log.error(ErrorCodeDefine.getLogErrorMessage(ErrorCodeDefine.CUSTOM_ERROR_OCB_POINT_SAVE_ALREADY.code()));
                throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_OCB_POINT_SAVE_ALREADY.getDesc(), ResultCodeEnum.CUSTOM_ERROR_OCB_POINT_SAVE_ALREADY);
            }

            boolean isSuccess = false;
            String failStr = null;

            //OCB 포인트 지급 API 콜
            resDto = ocbApiService.requestOcbPointSaveApi(ocbPointSaveEntity, partnerToken, eventId);

            //"00" > OCB 포인트 지급 API 정상 지급 코드, 성공여부(isSuccess)를 true 변경
            if (PredicateUtils.isEqualsStr(resDto.getCode(), "00")) {
                //리턴 해줄 지급 포인트 주입
                resDto.setRequestPoint(ocbPointSaveEntity.getSavePoint());
                isSuccess = true;
            } else {
                //OCB 포인트 지급 API 가 실패 코드면 실패 response 문자열 만들기
                failStr = resDto.toString();
            }

            try {
                //OCB 포인트 지급 로그 저장
                logService.saveOcbLogPointSave(ocbPointSaveEntity, eventId, ocbSessionInfo.getMdn(), arEventEntity.getOcbPointSaveType(), isSuccess, failStr, null, resDto.getRequestId(), partnerToken);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                try {
                    //OCB 포인트 지급 exception 발생 후 로그 저장 추가 실행
                    logService.saveOcbLogPointSave(ocbPointSaveEntity, eventId, ocbSessionInfo.getMdn(), arEventEntity.getOcbPointSaveType(), isSuccess, failStr, null, resDto.getRequestId(), partnerToken);
                } catch (Exception e2) {
                    resultCode = ErrorCodeDefine.IOE_ERROR.code();
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                }
            }
        }

        return new ApiResultObjectDto().builder()
                .result(resDto)
                .resultCode(resultCode)
                .build();
    }

    /**
     * 프록시미티 API CALL HEADER 정보
     * @param eventId
     * @return
     */
    private Map<String, String> getProximityApiHeaders(String eventId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", StringTools.joinStringsNoSeparator("SKP-PROX ", bsmAuthKey));
        headers.put("x-skp-client-id", eventId);
        headers.put("Content-Type", "application/json");
        return headers;
    }
}
