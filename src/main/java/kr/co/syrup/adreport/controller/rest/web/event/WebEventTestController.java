package kr.co.syrup.adreport.controller.rest.web.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.*;
import com.penta.scpdb.ScpDbAgent;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.syrup.adreport.framework.common.annotation.EncryptDataFilter;
import kr.co.syrup.adreport.framework.common.annotation.SetSodarMemberSession;
import kr.co.syrup.adreport.framework.common.annotation.TraceNoFilter;
import kr.co.syrup.adreport.framework.common.annotation.XssFilter;
import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.service.adreport.ApiHelperService;
import kr.co.syrup.adreport.stamp.event.model.StampEventMainModel;
import kr.co.syrup.adreport.stamp.event.model.StampEventPanModel;
import kr.co.syrup.adreport.stamp.event.model.StampEventPanTrModel;
import kr.co.syrup.adreport.stamp.event.service.StampFrontService;
import kr.co.syrup.adreport.stamp.event.service.StampSodarService;
import kr.co.syrup.adreport.survey.go.dto.request.*;
import kr.co.syrup.adreport.survey.go.dto.response.SurveyInfoSelectMobileResDto;
import kr.co.syrup.adreport.survey.go.entity.SurveyExampleEntity;
import kr.co.syrup.adreport.survey.go.entity.SurveyLogAttendEntity;
import kr.co.syrup.adreport.survey.go.entity.SurveySubjectEntity;
import kr.co.syrup.adreport.survey.go.entity.SurveyTargetAgeGenderLimitEntity;
import kr.co.syrup.adreport.survey.go.logic.SurveyGoMobileLogic;
import kr.co.syrup.adreport.survey.go.logic.SurveyGoSodarLogic;
import kr.co.syrup.adreport.survey.go.mybatis.vo.SurveyLogAttendResultResVO;
import kr.co.syrup.adreport.survey.go.mybatis.vo.SurveyTableRawResVO;
import kr.co.syrup.adreport.survey.go.mybatis.vo.SurveyTableRawTitleResVO;
import kr.co.syrup.adreport.survey.go.service.SurveyEntityService;
import kr.co.syrup.adreport.survey.go.service.SurveyGoLogService;
import kr.co.syrup.adreport.survey.go.service.SurveyGoSodarService;
import kr.co.syrup.adreport.survey.go.service.SurveyGoStaticsService;
import kr.co.syrup.adreport.web.event.define.*;
import kr.co.syrup.adreport.web.event.dto.request.*;
import kr.co.syrup.adreport.web.event.dto.response.*;
import kr.co.syrup.adreport.web.event.entity.*;
import kr.co.syrup.adreport.web.event.entity.repository.ArEventNftCouponRepositoryEntityRepository;
import kr.co.syrup.adreport.web.event.entity.repository.EmTranEntityRepository;
import kr.co.syrup.adreport.web.event.logic.*;
import kr.co.syrup.adreport.web.event.mybatis.mapper.TestMapper;
import kr.co.syrup.adreport.web.event.mybatis.vo.EventLogScheduledVO;
import kr.co.syrup.adreport.web.event.mybatis.vo.SurveyConnectionStaticsMapperVO;
import kr.co.syrup.adreport.web.event.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.formula.functions.T;
import org.codehaus.jackson.map.ObjectWriter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.cert.Certificate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@CrossOrigin(value = "*")
@RestController
@RequestMapping(value = "/api/v1/web-event-test")
public class WebEventTestController {

    @Value("${domain.sordar.internal}")
    private String smsSendUrl;

    @Value("${future.sense.api.access.key}")
    private String futureSenseApiAccessKey;

    @Value("${future.sense.api.secret.key}")
    private String futureSenseApiSecretKey;

    @Value("${future.sense.api.host}")
    private String futureSenseApiHost;

    @Value("${bsm.search.domain}")
    private String bsmDomain;

    @Value("${bsm.search.uri}")
    private String bsmUri;

    @Value("${bsm.auth.key}")
    private String bsmAuthKey;

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private ArEventFrontService arEventFrontService;

//    @Autowired
//    private HistoryLogic historyLogic;

    @Autowired
    private SodarApiService sodarApiService;

    @Autowired
    private FutureSenseApiService futureSenseApiService;

    @Autowired
    private OkHttpService okHttpService;

    @Autowired
    private ApiHelperService apiHelperService;

    @Autowired
    private AES256Utils aes256Utils;

    @Autowired
    private SmsService smsService;

    @Autowired
    private LogService logService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private ArEventFrontLogic arEventFrontLogic;

    @Autowired
    private ArEventLogic arEventLogic;

    @Autowired
    private SurveyGoSodarService surveyGoSodarService;

    @Autowired
    private SurveyGoSodarLogic surveyGoSodarLogic;

    @Autowired
    private ArEventNftCouponRepositoryEntityRepository arEventNftCouponRepositoryEntityRepository;

    @Autowired
    private EmTranEntityRepository emTranEntityRepository;

    @Autowired
    private SurveyEntityService surveyEntityService;

    @Autowired
    private StaticsService staticsService;

    @Autowired
    private StaticsLogic staticsLogic;

    @Autowired
    private SurveyGoLogService surveyGoLogService;

    @Autowired
    private SurveyGoStaticsService surveyGoStaticsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OcbApiService ocbApiService;
    private com.fasterxml.jackson.databind.ObjectMapper mapper;

    @Autowired
    private SurveyGoMobileLogic surveyGoMobileLogic;

    @Autowired
    private TestMapper testMapper;

    @Autowired
    private StampSodarService stampSodarService;

    @Autowired
    private StampFrontService stampFrontService;

    @GetMapping(value = "/test")
    public ResponseEntity<Object> test() {
        String str = aes256Utils.encrypt("경기도 용인시 수지구 성북1로 157 성복경남아너스빌1차");
        String str2 = aes256Utils.decrypt(str);
        log.info(">>> "+ str);
        log.info(">>> "+ str2);
        return null;
    }

    @PostMapping(value = "/test2")
//    @EncryptDataFilter("test,test2")
    @EncryptDataFilter("test,test2,test3|{value1},test5|{value1/value2}")
    public ResponseEntity<Object> test2(@RequestBody TestReqDto reqDto) {
        log.info(">>> " + reqDto.getTest4().getTest5().get(0).getValue1());
        int cnt = logService.getCountEventWinningLogByEventIdAndAttendCodeNotFail("000191", "");
        log.info(">>" + cnt);
        return ResponseEntity.ok(null);
    }

    @PostMapping(value = "/test3")
    public ResponseEntity<Object> test3(@RequestParam(value = "eventId") String eventId) {
        //arEventService.updateArEventNftTokenInfo(10, 680, "token_info_20220616140716615.xlsx");
//        ArEventEntity arEventEntity = arEventService.findArEventByEventId(eventId);
//        log.info(">>" + arEventEntity.toString());
//        WebEventBaseEntity webEventBaseEntity = arEventService.findEventBase(eventId);
//        log.info(">>" + webEventBaseEntity.toString());
//        //log.info(">>" + arEventEntity.getWebEventBaseEntity().getEventTitle());
//        ArEventObjectEntity arEventObjectEntity = arEventService.findArEventObjectById(11);
//        log.info(">>" + arEventObjectEntity.getArEventId());
        return null;
    }

    @PostMapping(value = "/test4")
    public ResponseEntity<Object> test4(@RequestBody String jsonStr) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ArEventEntity arEventEntity = objectMapper.readValue(jsonStr, ArEventEntity.class);
//        NftTokenTransferReqDto tokenTransferReqDto = new NftTokenTransferReqDto();
//        tokenTransferReqDto.setContractAddress("0x0cc28502567a27613ef4db4f2d7dacec92487fbd");
//        tokenTransferReqDto.setTokenId(146);
//        tokenTransferReqDto.setToWalletAddress("0x00aC80365bb142A89305E7E9C12ABA1519A9EF10");
//        tokenTransferReqDto.setWebhookURL("https://5pbli1qha5.execute-api.ap-northeast-2.amazonaws.com/dev/test/webhook");
        //futureSenseApiService.transferToken(tokenTransferReqDto);
        arEventService.updateArEvent(arEventEntity);
        return null;
        //return ResponseEntity.ok();

    }

    @GetMapping(value = "/test5")
    public ResponseEntity<Object> test5() {
        int cnt = logService.getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode(288, 1, "");
        log.info("cnt " + cnt);
        return ResponseEntity.ok(null);
        //return ResponseEntity.ok();
    }

    @GetMapping(value = "/test6")
    public ResponseEntity<Object> test6() {
//        boolean bl = futureSenseApiService.isSuccessTransferNftToken("118", "00cc28502567a27613ef4db4f2d7dacec92487fbd", "0xbF38EE9AECa49895208c169AaD983CFC5EE18229");
        //logService.getHourEventLogWinningCountByArEventIdEqualsAndEventWinningSortEqualsAndIdIsLessThan(289, 1, 5226);
        logService.saveEventLogWinningLimit(289, 0, null, null, "IS8U", EventLogWinningLimitDefine.ID_CODE.name());
        logService.saveEventLogWinningLimit(289, 0, DateUtils.getNowMMDD(), null, "IS8U", EventLogWinningLimitDefine.ID_CODE_TODAY.name());
        logService.saveEventLogWinningLimit(289, 1, DateUtils.getNowMMDD(), null, null, EventLogWinningLimitDefine.ID_SORT.name());
        return ResponseEntity.ok(null);
        //return ResponseEntity.ok();
    }

    @GetMapping(value = "/test7")
    public ResponseEntity<Object> test7() {
        List<SmsSendReqDto> smsSendReqDtoList = new ArrayList<>();

        String[] number = {"01062585228", "01085585389"};
        for (int i=0; i<number.length; i++) {
            SmsSendReqDto smsSendReqDto = new SmsSendReqDto().builder()
                    .name(number[i])
                    .smsId(makeSmsSendCode(number[i]))
                    .mdn(number[i])
                    .build();

            smsSendReqDtoList.add(smsSendReqDto);
        }

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("sendList", smsSendReqDtoList);
        requestMap.put("contents", "테스트내용 1234");

        SmsSendResDto smsSendResDto = sodarApiService.sendSodarSms("/v3/web-event/bulkSend", requestMap, SmsSendResDto.class);
        return ResponseEntity.ok(smsSendResDto);
    }

    @PostMapping(value = "/aes256/decrypt")
    public @ResponseBody String test8(@RequestParam("encryptStr") String encryptStr) {
        return aes256Utils.decrypt(encryptStr);
    }
    @GetMapping(value = "/test9")
    public ResponseEntity<Object> test9() {
        // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
        ArEventNftCouponRepositoryEntity entity = arEventNftCouponRepositoryEntityRepository.getById(1L);
        log.info("> " + entity);
//        log.info(">> " + entity.getArEventNftCouponInfoEntity().getId());
//        log.info(">> " + entity.getEventGiveAwayDeliveryEntity().getEventId());
        return null;
    }

    @GetMapping(value = "/test10")
    public ResponseEntity<Object> test10() {
        //System.setProperty("jsse.enableSNIExtension", "true");
        //System.setProperty("jsse.enableSNIExtension", "false");
        //String url = bsmDomain + bsmUri + "?eventId=000278&appType=WEB&lat=37.1234&lon=127.1234&radius=150&mradius=0&mid=";
        //Object obj = okHttpService.callGetApi(url, getProximityApiHeaders("000278"), Object.class);

        String apiUrl = bsmDomain + bsmUri;

        ProximityApiReqDto condition = ProximityApiReqDto.condition("000278", "37.1234", "127.1234");
        Object obj = apiHelperService.callGetApi(apiUrl, condition, getProximityApiHeaders("000278"), Object.class);
        ////return apiHelperService.callGetApi(apiUrl, condition, getProximityApiHeaders(pid), ProximityResDto.class);

        //System.setProperty("jsse.enableSNIExtension", "true");
        return ResponseEntity.ok(obj);
        //return ResponseEntity.ok();
    }

    @PostMapping(value = "/encrypt-give-away-delivery")
    public ResponseEntity<Object> encryptField(@RequestParam("isEncrypt") Boolean isEncrypt) {
        //Integer startNum = GsonUtils.parseIntJsonStr(jsonStr, "startNum");
        //Integer endNum = GsonUtils.parseIntJsonStr(jsonStr, "endNum");
        //String isEncrypt = GsonUtils.parseStringJsonStr(jsonStr, "isEncrypt");

        //if (PredicateUtils.isNotNull(startNum)) {
            arEventFrontService.updateEncryptEventGiveAwayDelivery(isEncrypt);
       // }
        return null;
    }

//    @PostMapping(value = "/test12")
//    public ResponseEntity<Object> test12(@RequestParam("eventId") String eventId) {
//        List<EventLogExposureEntity> list = logService.getEventLogExposureByEventId(eventId);
//        batchService.updateEventLogExposureFiled(list);
//        return null;
//    }

    @PostMapping(value = "/test13")
    public ResponseEntity<Object> test13(@RequestParam("eventId") String eventId) {
        logService.saveEventLogScheduled("000001", 1, ScheduleDefine.WINNING_DELETE.name());
        return null;
    }

    @ApiOperation("웹 AR 이벤트 오브젝트 정보(AR 페이지에게 전달할 정보)")
    @PostMapping(value = "/test14")
    public ResponseEntity<ApiResultObjectDto> test14(@RequestBody ArEventMetaReqDto arEventMetaReqDto) {
        EventWinningReqDto reqDto = new EventWinningReqDto();
        reqDto.setEventId("000001");
        reqDto.setAttendCode(null);
        reqDto.setArEventObjectId(1);

        EventLogWinningEntity entity = EventLogWinningEntity.saveOf(reqDto, arEventService.findByArEventWinningById(28));
        EventLogWinningEntity entity1 = logService.saveEventLogWinningByReturn2(entity, false);
        log.info(" id >>> " + entity1.getId() );
        return ResponseEntity.ok(null);
    }

    @PostMapping(value = "/process-full-event-logic")
    public void processingFullEventLogic(
            @RequestParam(value = "eventId") String eventId,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber) {
        final String attendCode = "";
        final String latitude = "";
        final String longitude = "";

        final String name = "안지호";
        //final String phoneNumber = "01012341238";
        final String memberBirth = "19820128";

        //초기화 선언
        ArEventGatePageResDto gatePageResDto = new ArEventGatePageResDto();
        WebArObjectResDto webArObjectResDto =  new WebArObjectResDto();
        EventWinningReqDto winningReqDto =     new EventWinningReqDto();
        int clickArEventObjectId = 0;

        //1. 이벤트 정보 가져오기
        WebArGateReqDto arGateReqDto = new WebArGateReqDto();
        arGateReqDto.setEventId(eventId);

        log.info("================================ 이벤트 화면 진입 시작 이벤트ID :: " + eventId + "================================");
        ApiResultObjectDto apiResultObjectDto1 = arEventFrontLogic.getGatePageImproveLogic(arGateReqDto);

        int resultCode1 = apiResultObjectDto1.getResultCode();
        //1-1. 이벤트 정보 가져오기 정상 통신일때
        if (resultCode1 == HttpStatus.OK.value()) {
            gatePageResDto = (ArEventGatePageResDto) apiResultObjectDto1.getResult();
        } else {
            log.error("1. 이벤트 정보 가져오기 에러 코드 :: {} ", resultCode1);
        }
        log.info("================================ 이벤트 화면 진입 끝 ================================");

        //2. 구동페이지로 진입
        if (PredicateUtils.isNotNull(gatePageResDto)) {
            log.info("================================ 구동페이지 진입 시작 ================================");

            ApiResultObjectDto apiResultObjectDto2 = arEventFrontLogic.getWebArInfoLogic(eventId, attendCode, latitude, longitude);
            int resultCode2 = apiResultObjectDto2.getResultCode();
            //2-1. 구동페이지로 진입이 정상일때
            if (resultCode2 == HttpStatus.OK.value()) {
                webArObjectResDto = (WebArObjectResDto) apiResultObjectDto2.getResult();
                //2-2. AR 오브젝트 파싱
                if (PredicateUtils.isNotNull(webArObjectResDto)) {
                    List<ArEventObjectResDto> arObjectInfo = webArObjectResDto.getArObjectInfo();
                    if (!PredicateUtils.isNullList(arObjectInfo)) {
                        clickArEventObjectId = arObjectInfo.get(0).getArEventObjectId();
                    }
                }
            } else {
                log.error("2. 구동페이지 진입 에러 코드 :: {} ", resultCode2);
            }
        }
        log.info("================================ 구동페이지 진입 끝 ================================");

        String winningType = "";
        long eventLogWinningId = 0L;
        WinningResDto winningInfo = new WinningResDto();
        List<WinningButtonResDto> winningButtonList = new ArrayList<>();

        //3. 당첨로직
        if (clickArEventObjectId > 0) {
            log.info("================================ 당첨로직 시작 objectId ::: " + clickArEventObjectId + "================================");
            winningReqDto.setEventId(eventId);
            winningReqDto.setArEventObjectId(clickArEventObjectId);
            winningReqDto.setAttendCode(attendCode);

            ApiResultObjectDto apiResultObjectDto3 = arEventFrontLogic.eventWinningLogic(winningReqDto);
            int resultCode3 = apiResultObjectDto3.getResultCode();
            if (resultCode3 == HttpStatus.OK.value()) {
                WinningResultResDto winningResultResDto = (WinningResultResDto)apiResultObjectDto3.getResult();
                eventLogWinningId = winningResultResDto.getEventLogWinningId();
                winningInfo = winningResultResDto.getWinningInfo();
                winningButtonList = winningResultResDto.getWinningButtonInfo();
            } else {
                log.error("3. 당첨로직 에러 코드 :: {} ", resultCode3);
            }
        } else {
            log.info("clickArEventObjectId is zero ");
        }
        log.info("================================ 당첨로직 끝 ================================");

        //4. 경품정보 입력 로직
        if (eventLogWinningId > 0L && PredicateUtils.isNotNull(winningInfo)) {
            log.info("================================ 경품정보 입력 시작 ================================");
            if (!StringUtils.equals(winningInfo.getWinningType(), WinningTypeDefine.꽝.code())) {

                log.info("================================ 당첨이라서 경품정보 저장 ================================");

                int arEventWinningButtonId = 0;
                if (!PredicateUtils.isNullList(winningButtonList)) {
                    for (WinningButtonResDto button : winningButtonList) {
                        if (StringUtils.equals(button.getButtonActionType(), "DELIVERY")) {
                            arEventWinningButtonId = button.getArEventWinningButtonId();
                            break;
                        }
                    }
                }

                GiveAwayDeliverySaveReqDto giveAwayDeliverySaveReqDto = new GiveAwayDeliverySaveReqDto();
                giveAwayDeliverySaveReqDto.setEventId(eventId);
                giveAwayDeliverySaveReqDto.setArEventWinningId(winningInfo.getArEventWinningId());
                giveAwayDeliverySaveReqDto.setName(aes256Utils.encrypt(name));
                giveAwayDeliverySaveReqDto.setPhoneNumber(aes256Utils.encrypt(phoneNumber));
                giveAwayDeliverySaveReqDto.setMemberBirth(memberBirth);
                giveAwayDeliverySaveReqDto.setEventLogWinningId(eventLogWinningId);
                giveAwayDeliverySaveReqDto.setArEventWinningButtonId(arEventWinningButtonId);

                ApiResultObjectDto apiResultObjectDto4 = arEventFrontLogic.saveGiveAwayDeliveryLogic(giveAwayDeliverySaveReqDto);
                int resultCode4 = apiResultObjectDto4.getResultCode();
                if (resultCode4 == HttpStatus.OK.value()) {
                    log.info("========== 경품정보 입력 성공 =============");
                } else {
                    log.error("4. 경품정보 입력 로직 에러 코드 :: {} ", resultCode4);
                }
            } else {
                log.info("========== 당첨결과 ::: 꽝 =============");
            }
        }
        log.info("================================ 경품정보 입력 끝 ================================");
    }

    @PostMapping(value = "/test15")
    public ResponseEntity<Object> test15() {
        String str = "VnWh+/pw+jJqeSg1PdH+AA==";
        System.out.println(aes256Utils.decrypt(str));
        return null;
    }

    @XssFilter
    @PostMapping(value = "/survey/test17")
    public ResponseEntity<Object> test17(@RequestBody ArEventMetaReqDto arEventMetaReqDto) {
//        ArEventEntity entity = arEventService.findArEventByEventId(eventId);
//        return ResponseEntity.ok(surveyGoStaticsService.makeSurveyRawTableValue(eventId));
        return null;
    }

    @GetMapping(value = "/survey/test18/{couponCode}")
    public ResponseEntity<Object> test18(@PathVariable("couponCode") String couponCode) {
        return ResponseEntity.ok(ocbApiService.getPicasoCouponInfoApi(couponCode));
    }

    @PostMapping(value = "/copy-data")
    public ResponseEntity<Object> copyEventData(@RequestPart(value = "jsonStr") String jsonStr) throws Exception {
        EventSaveDto eventSaveDto = objectMapper.readValue(jsonStr, EventSaveDto.class);
        log.info(">>>", eventSaveDto);
        if (PredicateUtils.isEqualsStr(eventSaveDto.getEventBaseInfo().getEventType(), EventTypeDefine.SURVEY.name())) {
            surveyGoSodarLogic.copySurveyGoLogic(jsonStr, null);
        }

        if (PredicateUtils.isEqualsStr(eventSaveDto.getEventBaseInfo().getEventType(), EventTypeDefine.AR.name())) {
            arEventLogic.saveArEventLogic(jsonStr, null);
        }
        return null;
    }

    @PostMapping(value = "/survey-logic")
    public ResponseEntity<ApiResultObjectDto> surveyLogic(@RequestParam(value = "eventId") String eventId, //001083
                            @RequestParam(value = "arEventWinningId") Integer arEventWinningId, //3332
                            @RequestParam(value = "arEventWinningButtonId") Integer arEventWinningButtonId,//4295
                            @RequestParam(value = "loopCount", required = false) Integer loopCount) throws Exception {
        CompletableFuture.supplyAsync(() ->this.completableFutureSurveyLogic(eventId, arEventWinningId, arEventWinningButtonId, loopCount));
        ApiResultObjectDto resultObjectDto = new ApiResultObjectDto().builder()
                .resultCode(200)
                .result("")
                .build();

        return ResponseEntity.ok(resultObjectDto);
    }

    private CompletableFuture<Map<String, Object>> completableFutureSurveyLogic(String eventId, int arEventWinningId, int arEventWinningButtonId, int loopCount) {
        for (int k=0; k<loopCount; k++) {
            WebArGateReqDto webArGateReqDto = new WebArGateReqDto().builder()
                    .eventId(eventId)
                    .build();

            ApiResultObjectDto resultObjectDto = surveyGoMobileLogic.checkPossibleSurveyAttendLogic(webArGateReqDto);

            Object resultJsonObj = resultObjectDto.getResult();
            log.info(">>>>>>>>" + resultJsonObj);
            Map<String, Object> resultMap = objectMapper.convertValue(resultJsonObj, Map.class);
            String surveyLogAttendId = resultMap.get("surveyLogAttendId").toString();

            log.info("surveyLogAttendId >>>>>>>>" + surveyLogAttendId);
//        //================================= 서베이고 참여코드 발행 끝 ========================================//

            GenderAgeReqDto genderAgeReqDto = new GenderAgeReqDto();
            genderAgeReqDto.setEventId(eventId);
            genderAgeReqDto.setGender("M");
            genderAgeReqDto.setAge(2);
            surveyGoMobileLogic.checkPossibleSurveyGenderAgeAttendLogic(genderAgeReqDto);

//        //================================= 문항정보 조회 시작 ========================================//
            SurveyInfoSelectMobileReqDto selectMobileReqDto = new SurveyInfoSelectMobileReqDto();
            selectMobileReqDto.setEventId(eventId);
            selectMobileReqDto.setSurveyLogAttendId(surveyLogAttendId);

            SurveyInfoSelectMobileResDto selectMobileResDto = surveyGoMobileLogic.getSurveySubjectData(selectMobileReqDto);
            //================================= 문항정보 조회 시작 ========================================//

//        //================================= 답변 만들기 시작 ========================================//
            List<SurveySubjectInfoMobileDto> subjectList = selectMobileResDto.getSurveySubjectInfo();

            SurveyResultSaveMobileReqDto answerInfo = new SurveyResultSaveMobileReqDto();
            answerInfo.setEventId(eventId);
            answerInfo.setSurveyLogAttendId(surveyLogAttendId);
            List<SurveyAnswerInfoDto> answerList = new ArrayList<>();

            for (int i = 0; i < subjectList.size(); i++) {
                SurveySubjectInfoMobileDto subject = subjectList.get(i);
                SurveyAnswerInfoDto answerInfoDto = new SurveyAnswerInfoDto();

                answerInfoDto.setSurveySubjectId(subject.getSurveySubjectId());
                answerInfoDto.setSubjectSort(subject.getSort());

                List<SurveyAnswerExampleDto> exampleList = new ArrayList<>();
                if (PredicateUtils.isEqualsStr(subject.getMultipleAnswerYn(), "N")) {
                    List<SubjectExampleSodarReqDto> exampleSubjectList = selectMobileResDto.getSurveySubjectInfo().get(i).getExampleInfo();

                    for (SubjectExampleSodarReqDto example : exampleSubjectList) {
//                        if (example.getSort() == 1) {
                            SurveyAnswerExampleDto exampleDto = new SurveyAnswerExampleDto();
                            exampleDto.setExampleSort(1);
                            exampleDto.setSurveyExampleId(example.getSurveyExampleId());

                            exampleList.add(exampleDto);
                            answerInfoDto.setSurveyExampleList(exampleList);
//                            break;
//                        }
                    }
                }
                answerList.add(answerInfoDto);
            }
            answerInfo.setAnswerList(answerList);

            surveyGoMobileLogic.saveSurveyResult(answerInfo);
            //================================= 답변 만들기 끝 ========================================//

            //================================= 당첨 만들기 시작 ========================================//
            EventWinningReqDto winningReqDto = new EventWinningReqDto();
            winningReqDto.setEventId(eventId);
            winningReqDto.setSurveyLogAttendId(surveyLogAttendId);

            Long eventLogWinningId = null;
            WinningResDto winningInfo = new WinningResDto();
            List<WinningButtonResDto> winningButtonList = new ArrayList<>();

            ApiResultObjectDto apiResultObjectDto3 = arEventFrontLogic.eventWinningLogic(winningReqDto);
            int resultCode3 = apiResultObjectDto3.getResultCode();
            if (resultCode3 == HttpStatus.OK.value()) {
                WinningResultResDto winningResultResDto = (WinningResultResDto) apiResultObjectDto3.getResult();
                eventLogWinningId = winningResultResDto.getEventLogWinningId();
                winningInfo = winningResultResDto.getWinningInfo();
                winningButtonList = winningResultResDto.getWinningButtonInfo();
            } else {
                log.error("3. 당첨로직 에러 코드 :: {} ", resultCode3);
            }
            //================================= 당첨 만들기 끝 ========================================//

            //================================= 당첨정보 입력 만들기 시작 ========================================//
            GiveAwayDeliverySaveReqDto giveAwayDeliverySaveReqDto = new GiveAwayDeliverySaveReqDto();
            giveAwayDeliverySaveReqDto.setEventId(eventId);
            giveAwayDeliverySaveReqDto.setArEventWinningId(arEventWinningId);
//            giveAwayDeliverySaveReqDto.setName("테스터");
            giveAwayDeliverySaveReqDto.setPhoneNumber("01062585227");
            giveAwayDeliverySaveReqDto.setMemberBirth("19820128");
            giveAwayDeliverySaveReqDto.setEventLogWinningId(eventLogWinningId);
            giveAwayDeliverySaveReqDto.setArEventWinningButtonId(arEventWinningButtonId);
            giveAwayDeliverySaveReqDto.setSurveyLogAttendId(surveyLogAttendId);

            ApiResultObjectDto apiResultObjectDto4 = arEventFrontLogic.saveGiveAwayDeliveryLogic(giveAwayDeliverySaveReqDto);

            //================================= 당첨정보 입력 만들기 시작 ========================================//

        }
        Map<String, Object>resultMap = new HashMap<>();
        resultMap.put("success", true);
        resultMap.put("resultCode", 200);
        return CompletableFuture.completedFuture(resultMap);
    }

    @PostMapping(value = "/test-aes256")
    public ResponseEntity<Object> testAes256(@RequestParam("str") String str) {
        String resultStr = "";
        Map<String, Object>resultMap = new HashMap<>();

        resultStr = aes256Utils.encrypt(str);
        resultMap.put("encStr", resultStr);
        resultMap.put("decStr", aes256Utils.decrypt(resultStr));

        return ResponseEntity.ok(resultMap);
    }
    @GetMapping(value = "/sodar-ia-member")
    public ResponseEntity<Object> testIAMember(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        return ResponseEntity.ok(sodarApiService.checkSodarLoginMember(cookies));
    }

    @GetMapping(value = "/async-test/{value}")
    public ResponseEntity<ApiResultObjectDto> testAsync(@PathVariable("value") String value) {
        CompletableFuture<ApiResultObjectDto> completableFuture = CompletableFuture.supplyAsync(() -> {
           return this.asyncFunc(value);
        });
        return ResponseEntity.ok(completableFuture.join());
    }

    @GetMapping(value = "/async-test2/{value}")
    public ResponseEntity<ApiResultObjectDto> testAsync2(@PathVariable("value") String value) {
        CompletableFuture<ApiResultObjectDto> completableFuture = CompletableFuture.supplyAsync(() -> {
           return this.asyncFunc(value);
        });
        return ResponseEntity.ok(completableFuture.join());
    }

    @SetSodarMemberSession
    @PostMapping(value = "/delete-all-winning")
    public ResponseEntity<String> deleteAllWinning(@RequestParam(value = "stampEventId") String stampEventId,
                                                    @RequestParam(value = "phoneNumber") String phoneNumber) {
        if (PredicateUtils.isNotNull(phoneNumber)) {
            phoneNumber = aes256Utils.encrypt(phoneNumber);
        }
        StampEventMainModel stampEventMainModel = stampSodarService.findStampEventMainByEventId(stampEventId);
        int stpId = stampEventMainModel.getStpId();
        testMapper.deleteStampEventGiveAwayDelivery(phoneNumber);
        testMapper.deleteStampEventLogTr(stpId);
        testMapper.deleteStampEventLogLimit(stpId);
        testMapper.deleteStampEventLogWinningSuccess(phoneNumber, stpId);

        StampEventPanModel stampEventPanModel = stampSodarService.findStampEventPanByStpId(stpId);
        List<StampEventPanTrModel> trList = stampSodarService.findStampEventPanTrListByStpPanId(stampEventPanModel.getStpPanId());
        if (PredicateUtils.isNotNullList(trList)) {
            for (StampEventPanTrModel trModel : trList) {
                if (PredicateUtils.isNotNull(trModel.getStpTrEventId())) {
                    String eventId = trModel.getStpTrEventId();
                    testMapper.deleteEventLogWinningSuccess(phoneNumber, eventId);
                    testMapper.deleteEventGiveAwayDelivery(eventId, phoneNumber);

                    ArEventEntity arEventEntity = arEventService.findArEventByEventId(eventId);
                    int arEventId = arEventEntity.getArEventId();
                    testMapper.deleteEventLogWinningLimit(arEventId);

                }
            }
        }
        return ResponseEntity.ok("ok");
    }

    @PostMapping(value = "/survey-data-copy")
    public void testSurveyDataCopy(@RequestParam("eventId") String eventId, @RequestParam("surveyLogAttendId") String surveyLogAttendId, @RequestParam("count") int count) {
        Set<String>phoneNumberList = this.makeRandomPhoneNumberList(count);
        for (String phoneNumber : phoneNumberList) {
            WebArGateReqDto webArGateReqDto = new WebArGateReqDto().builder().eventId(eventId).build();
            ApiResultObjectDto resultObjectDto = surveyGoMobileLogic.checkPossibleSurveyAttendLogic(webArGateReqDto);

            Object resultJsonObj = resultObjectDto.getResult();
            Map<String, Object> resultMap = objectMapper.convertValue(resultJsonObj, Map.class);
            String attendSurveyLogAttendId = resultMap.get("surveyLogAttendId").toString();

            SurveyLogAttendEntity surveyLogAttendEntity = testMapper.getSurveyLogAttendById(surveyLogAttendId);
            surveyLogAttendEntity.setSurveyLogAttendId(attendSurveyLogAttendId);
            testMapper.updateSurveyLogAttend(surveyLogAttendEntity);

            List<SurveyLogAttendResultResVO>list = testMapper.getSurveyLogAttendResultBySurveyLogAttendId(surveyLogAttendId);
            list.stream().forEach(data -> data.setSurveyLogAttendId(attendSurveyLogAttendId));

            batchService.saveBulkSurveyLogAttendResult(list);

            long eventLogWinningId = 0L;
            WinningResDto winningInfo = new WinningResDto();
            List<WinningButtonResDto> winningButtonList = new ArrayList<>();

            //================================= 당첨 만들기 시작 ========================================//
            EventWinningReqDto winningReqDto = new EventWinningReqDto();
            winningReqDto.setEventId(eventId);
            winningReqDto.setSurveyLogAttendId(surveyLogAttendId);

            ApiResultObjectDto apiResultObjectDto3 = arEventFrontLogic.eventWinningLogic(winningReqDto);
            int resultCode3 = apiResultObjectDto3.getResultCode();
            if (resultCode3 == HttpStatus.OK.value()) {
                WinningResultResDto winningResultResDto = (WinningResultResDto) apiResultObjectDto3.getResult();
                eventLogWinningId = winningResultResDto.getEventLogWinningId();
                winningInfo = winningResultResDto.getWinningInfo();
                winningButtonList = winningResultResDto.getWinningButtonInfo();
            } else {
                log.error("3. 당첨로직 에러 코드 :: {} ", resultCode3);
            }
            //================================= 당첨 만들기 끝 ========================================//

            //================================= 당첨정보 입력 만들기 시작 ========================================//
            GiveAwayDeliverySaveReqDto giveAwayDeliverySaveReqDto = new GiveAwayDeliverySaveReqDto();
            giveAwayDeliverySaveReqDto.setEventId(eventId);
            giveAwayDeliverySaveReqDto.setArEventWinningId(3332);
//            giveAwayDeliverySaveReqDto.setName("테스터");
            giveAwayDeliverySaveReqDto.setPhoneNumber(aes256Utils.encrypt(phoneNumber));
            giveAwayDeliverySaveReqDto.setMemberBirth("19820128");
            giveAwayDeliverySaveReqDto.setEventLogWinningId(eventLogWinningId);
            giveAwayDeliverySaveReqDto.setArEventWinningButtonId(4295);
            giveAwayDeliverySaveReqDto.setSurveyLogAttendId(attendSurveyLogAttendId);

            ApiResultObjectDto apiResultObjectDto4 = arEventFrontLogic.saveGiveAwayDeliveryLogic(giveAwayDeliverySaveReqDto);

            //================================= 당첨정보 입력 만들기 시작 ========================================//
        }
    }

    @PostMapping(value = "/save-survey-single-data")
    public void testSaveSurveyAnswerStaticsSingleData(@RequestParam("surveyLogAttendId") String surveyLogAttendId) {
        surveyGoMobileLogic.saveSurveyAnswerStaticsData(surveyLogAttendId);
    }

    private ApiResultObjectDto asyncFunc(String value) {
        int resultCode = 200;
        if (value.equals("1")) {
            resultCode = 300;
        }
        ApiResultObjectDto dto = new ApiResultObjectDto().builder().result("null").resultCode(resultCode).build();
        return dto;
    }

    private void checkCharset(String str) {
        String[] charSet = {"utf-8", "euc-kr", "ksc5601", "iso-8859-1", "x-windows-949"};
        for(int i = 0; i<charSet.length; i++){
            for(int j = 0; j<charSet.length; j++){
                try{
                    System.out.println("[" + charSet[i] + "," + charSet[j] + "]" + new String(str.getBytes(charSet[i]), charSet[j]));
                } catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private static String makeSmsSendCode(String receiverPhoneNumber) {
        if (PredicateUtils.isNotNull(receiverPhoneNumber)) {
            StringBuffer sb = new StringBuffer();
            sb.append(DateUtils.getNow(DateUtils.PATTERN_MMDD));
            sb.append(receiverPhoneNumber);
            return sb.toString();
        }
        return null;
    }

    private Map<String, String> getProximityApiHeaders(String eventId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "SKP-PROX " + bsmAuthKey);
        headers.put("x-skp-client-id", eventId);
        headers.put("Content-Type", "application/json");
        return headers;
    }

    private Set<String> makeRandomPhoneNumberList(int count) {
        Set<String> phoneNumbers = new HashSet<>();
        Random random = new Random();

        while (phoneNumbers.size() < count) {
            // 지역 번호 (010)
            String areaCode = "010";

            // 중간 번호 (1000~9999)
            int middleNumber = 1000 + random.nextInt(9000);

            // 마지막 번호 (1000~9999)
            int lastNumber = 1000 + random.nextInt(9000);

            // 핸드폰 번호 조합
            String phoneNumber = String.format("%s%04d%04d", areaCode, middleNumber, lastNumber);

            // Set에 추가 (중복 제거 자동 처리)
            phoneNumbers.add(phoneNumber);
        }
        return phoneNumbers;
    }



}
