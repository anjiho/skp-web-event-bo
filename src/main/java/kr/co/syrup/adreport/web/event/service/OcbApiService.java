package kr.co.syrup.adreport.web.event.service;

import kr.co.syrup.adreport.framework.config.properties.ProfileProperties;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.SecurityUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.service.adreport.ApiHelperService;
import kr.co.syrup.adreport.web.event.dto.request.api.OcbCouponSearchReqDto;
import kr.co.syrup.adreport.web.event.dto.response.api.CouponInfoResDto;
import kr.co.syrup.adreport.web.event.dto.response.api.OcbPointApiResDto;
import kr.co.syrup.adreport.web.event.dto.response.api.OcbSessionApiResDto;
import kr.co.syrup.adreport.web.event.dto.response.api.PicasoCouponInfoApiResDto;
import kr.co.syrup.adreport.web.event.entity.OcbPointSaveEntity;
import kr.co.syrup.adreport.web.event.entity.repository.OcbPointSaveEntityRepository;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class OcbApiService {

    @Value("${ocb.session.token.api.url}")
    private String ocbSessionTokenApiUrl;

    @Value("${ocb.point.api.url}")
    private String ocbPointApiUrl;

    @Value("${ocb.session.token.sid}")
    private String ocbSessionTokenSid;

    @Value("${ocb.session.token.salt}")
    private String ocbSessionTokenSalt;

    @Value("${ocb.session.token.siteid}")
    private String ocbSessionTokenSiteId;

    @Value("${spring.profiles}")
    private String profile;

    @Value("${picaso.syrup.api.url}")
    private String picasoSyrupApiUrl;

    @Autowired
    private ApiHelperService apiHelperService;

    @Autowired
    private OkHttpService okHttpService;

    /**
     * OCB 파트너 토큰 조회 API
     * @param partnerToken
     * @return
     */
    public OcbSessionApiResDto getOcbSessionApi(String partnerToken) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("partner_token", this.makeOcbPartnerToken(partnerToken));

        return apiHelperService.callGetApi(ocbSessionTokenApiUrl, condition, this.getOcbSessionTokenApiHeaders(""), OcbSessionApiResDto.class);
    }

    /**
     * OCB 파트너 토큰으로 포인트 지급 API
     * @param ocbPointSaveInfo
     * @param partnerToken
     * @param eventId
     * @return
     */
    public OcbPointApiResDto requestOcbPointSaveApi(OcbPointSaveEntity ocbPointSaveInfo, String partnerToken, String eventId) {
        String saveType = "real_time";
        if (ProfileProperties.isProd()) {
            saveType = "queue";
        }
        String requestId = StringTools.joinStringsNoSeparator(DateUtils.returnNowDateByPattern(DateUtils.PATTERN_YYYYMMDDD), eventId, RandomStringUtils.randomNumeric(5));
        log.info("ocb_request_id >>>>>>>>>>>> " + requestId);

        RequestBody body = new MultipartBody.Builder()
                                                .setType(MultipartBody.FORM)
                                                .addFormDataPart("partner_token", this.makeOcbPartnerToken(partnerToken))

                                                //적립 요청 포인트
                                                .addFormDataPart("point", String.valueOf(ocbPointSaveInfo.getSavePoint()))

                                                //Nxmile 적립 가맹점 번호(사업 담당자 문의)
                                                .addFormDataPart("biz_no", ocbPointSaveInfo.getOcbPointSaveCode())

                                                //Nxmile 적립 사업자 번호(사업 담당자 문의)
                                                .addFormDataPart("saup_no", ocbPointSaveInfo.getBusinessNumber())

                                                //포인트 적립 처리 요청 방식, 다음 세가지 중에서 한가지 방식으로 전달해야 한다.
                                                //"real_time": 실시간 적립(개발/알파 테스트 용도)
                                                //"queue": API 요청시 Queue 적재를 통한 지연 적립
                                                //"point_inquery" : 포인트 조회 요청 시점에 적립 실행("내지갑" 화면 진입시)
                                                .addFormDataPart("save_type", saveType)

                                                //적립 요청 채널 구분값 (최대 10Byte 이하)
                                                .addFormDataPart("request_channel", ocbSessionTokenSiteId)

                                                //제휴사에서 생성하는 유니크 TR ID
                                                //API Timeout 등의 적립 오류 발생시, 동일 request_id로 요청하면 재적립 처리(동일 회원, 동일 포인트 인 경우)
                                                // 특정 주기 별로 삭제 관리 예정이므로 가급적 "YYYYYMMDD"을 prefix로 생성 필요함
                                                .addFormDataPart("request_id", requestId)
                                                .build();
        OcbPointApiResDto resDto = okHttpService.callPostParamApi(ocbPointApiUrl, body, this.getOcbSessionTokenApiHeaders(""), OcbPointApiResDto.class);
        resDto.setRequestId(requestId);

        return resDto;
    }

    public CompletableFuture<OcbPointApiResDto> requestAsyncOcbPointSaveApi(OcbPointSaveEntity ocbPointSaveInfo, String partnerToken, String eventId) {
        String saveType = "real_time";
        if (ProfileProperties.isProd()) {
            saveType = "queue";
        }
        String requestId = StringTools.joinStringsNoSeparator(DateUtils.returnNowDateByPattern(DateUtils.PATTERN_YYYYMMDDD), "_", eventId + "_", partnerToken);

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("partner_token", this.makeOcbPartnerToken(partnerToken))

                //적립 요청 포인트
                .addFormDataPart("point", String.valueOf(ocbPointSaveInfo.getSavePoint()))

                //Nxmile 적립 가맹점 번호(사업 담당자 문의)
                .addFormDataPart("biz_no", ocbPointSaveInfo.getOcbPointSaveCode())

                //Nxmile 적립 사업자 번호(사업 담당자 문의)
                .addFormDataPart("saup_no", ocbPointSaveInfo.getBusinessNumber())

                //포인트 적립 처리 요청 방식, 다음 세가지 중에서 한가지 방식으로 전달해야 한다.
                //"real_time": 실시간 적립(개발/알파 테스트 용도)
                //"queue": API 요청시 Queue 적재를 통한 지연 적립
                //"point_inquery" : 포인트 조회 요청 시점에 적립 실행("내지갑" 화면 진입시)
                .addFormDataPart("save_type", saveType)

                //적립 요청 채널 구분값 (최대 10Byte 이하)
                .addFormDataPart("request_channel", ocbSessionTokenSiteId)

                //제휴사에서 생성하는 유니크 TR ID
                //API Timeout 등의 적립 오류 발생시, 동일 request_id로 요청하면 재적립 처리(동일 회원, 동일 포인트 인 경우)
                // 특정 주기 별로 삭제 관리 예정이므로 가급적 "YYYYYMMDD"을 prefix로 생성 필요함
                .addFormDataPart("request_id", requestId)
                .build();
        OcbPointApiResDto ocbPointApiResDto = okHttpService.callPostParamApi(ocbPointApiUrl, body, this.getOcbSessionTokenApiHeaders(""), OcbPointApiResDto.class);
        CompletableFuture<OcbPointApiResDto> future = CompletableFuture.completedFuture(ocbPointApiResDto);
        return future;
    }

    /**
     * 피카소 쿠폰정보 가져오기 API
     * @param counponCode
     * @return
     */
    public CouponInfoResDto getPicasoCouponInfoApi(String counponCode) {
        CouponInfoResDto couponInfoResDto = new CouponInfoResDto();

        if (PredicateUtils.isEqualsStr(profile, "local")) {
            //로컬 테스트용 쿠폰 코드
            counponCode = "10000450157";
        }
        //API 조회 body값 생성
        List<Map<String, Object>> paramSearchList = new ArrayList<>();
        Map<String, Object>paramSearchMap = new HashMap<>();
        paramSearchMap.put("searchKey", "002");
        paramSearchMap.put("searchValueList", Arrays.asList(counponCode));
        paramSearchList.add(paramSearchMap);

        //쿠폰 조회 request 생성
        OcbCouponSearchReqDto couponSearchReqDto = OcbCouponSearchReqDto.ofRequest(paramSearchList);

        //쿠폰 조회 API 콜
        PicasoCouponInfoApiResDto resDto = apiHelperService.callPostApi(picasoSyrupApiUrl, couponSearchReqDto, null, PicasoCouponInfoApiResDto.class);

        if (PredicateUtils.isNotNull(resDto)) {
            //"000" 정상통신
            if (PredicateUtils.isEqualsStr(resDto.getResCode1(), "000")) {
                //쿠폰 목록이 있을때
                if (!PredicateUtils.isNullList(resDto.getResponseData().getCouponList())) {
                    couponInfoResDto = resDto.getResponseData().getCouponList().get(0);
                }
            }
        }
        return couponInfoResDto;
    }

    //OCB API 파트너 토큰 값 만들기
    private String makeOcbPartnerToken(String partnerToken) {
        long currentTimestamp = System.currentTimeMillis();
        String hashStr = SecurityUtils.encryptSHA256(partnerToken + ":" + currentTimestamp + ":" + ocbSessionTokenSalt);
        return StringTools.joinStringsNoSeparator(partnerToken, ":", String.valueOf(currentTimestamp), ":", hashStr);
    }

    //OCB API default header 정의
    private Map<String, String> getOcbSessionTokenApiHeaders(String device) {
        Map<String, String> headers = new HashMap<>();
        headers.put("x-ocb-agent", "ocb_6.7.0,ios-7.1;accept=json;crypted=0");
        headers.put("x-ocb-crypted-sid", ocbSessionTokenSid);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }
}
