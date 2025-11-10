package kr.co.syrup.adreport.controller.rest.web.event;

import io.swagger.annotations.*;
import kr.co.syrup.adreport.framework.common.annotation.EncryptDataFilter;
import kr.co.syrup.adreport.framework.common.annotation.SetSodarMemberSession;
import kr.co.syrup.adreport.framework.common.annotation.TraceNoFilter;
import kr.co.syrup.adreport.framework.common.annotation.XssFilter;
import kr.co.syrup.adreport.framework.utils.GsonUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.survey.go.logic.SurveyGoMobileLogic;
import kr.co.syrup.adreport.web.event.dto.request.*;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.dto.request.EventLogPvReqDto;
import kr.co.syrup.adreport.web.event.logic.ArEventFrontLogic;
import kr.co.syrup.adreport.web.event.logic.SkApiLogic;
import kr.co.syrup.adreport.web.event.logic.StaticsLogic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@Slf4j
@CrossOrigin(value = "*")
@RestController
@RequestMapping(value = "/api/v1/web-event-front")
public class WebEventFrontController {

    @Autowired
    private ArEventFrontLogic arEventFrontLogic;

    @Autowired
    private StaticsLogic staticsLogic;

    @Autowired
    private SkApiLogic skApiLogic;

    //@TraceNoFilter
    @PostMapping(value = "/gate/detail")
    @ApiOperation("웹 AR 이벤트 게이트 페이지 정보")
    public ResponseEntity<ApiResultObjectDto> getWebArGatePage(@RequestBody WebArGateReqDto reqDto) {
        return ResponseEntity.ok(arEventFrontLogic.getGatePageImproveLogic(reqDto));
    }

    @TraceNoFilter
    @ApiOperation("웹 AR 이벤트 오브젝트 정보(AR 페이지에게 전달할 정보)")
    @PostMapping(value = "/ar-event-meta/detail")
    public ResponseEntity<ApiResultObjectDto> getWebArInfo(@RequestBody ArEventMetaReqDto reqDto) {
        return ResponseEntity.ok(arEventFrontLogic.getWebArInfoLogic(reqDto.getEventId(), reqDto.getAttendCode(), reqDto.getLatitude(), reqDto.getLongitude()));
    }

    @TraceNoFilter
    @ApiOperation("AR 이벤트 페이지 참여 코드 검증하기")
    @PostMapping(value = "/validate/attend-code")
    public ResponseEntity<ApiResultObjectDto> validateAttendCodeByEvent(@RequestBody ValidateAttendCodeReqDto reqDto) {
        return ResponseEntity.ok(arEventFrontLogic.validateAttendCodeLogic(reqDto.getEventId(), reqDto.getAttendCode()));
    }

    @TraceNoFilter
    @EncryptDataFilter("phoneNumber")
    @ApiOperation("AR 이벤트 페이지 핸드폰 번호 검증하기")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 801, message = "EVENT_ID 없음"),
            @ApiResponse(code = 854, message = "핸드폰번호 참여횟수 초과")
    })
    @PostMapping(value = "/validate/phone-number")
    public ResponseEntity<ApiResultObjectDto> validatePhoneNumberByEvent(@RequestBody ValidateAttendCodeReqDto reqDto) {
        return ResponseEntity.ok(arEventFrontLogic.validatePhoneNumberLogic(reqDto.getEventId(), reqDto.getPhoneNumber()));
    }


    @TraceNoFilter
    @EncryptDataFilter("phoneNumber")
    @ApiOperation("당첨이력조회")
    @PostMapping(value = "/winning/search")
    public ResponseEntity<ApiResultObjectDto> getWinningInfo(@RequestBody WinningSearchReqDto reqDto) {
        return ResponseEntity.ok(arEventFrontLogic.getWinningInfoLogic(reqDto.getEventId(), reqDto.getPhoneNumber(), reqDto.getAttendCode(), reqDto.getStampEventIds()));
    }

    @EncryptDataFilter("phoneNumber")
    @TraceNoFilter
    @ApiOperation("당첨정보 검증(당첨비밀번호를 입력해야되는지 검증 기능)")
    @PostMapping(value = "/validate/winning")
    public ResponseEntity<ApiResultObjectDto> validateWinningInfo(@RequestBody SmsTestUrlReqDto reqDto) {
        return ResponseEntity.ok(arEventFrontLogic.validateWinningInfoLogic(reqDto.getEventId(), reqDto.getPhoneNumber()));
    }

    @XssFilter
    @EncryptDataFilter("name,address,addressDetail,phoneNumber")
    @TraceNoFilter
    @ApiOperation("경품배송정보 저장하기")
    @PostMapping(value = "/give-away-delivery/save")
    public ResponseEntity<ApiResultObjectDto> saveEventGiveAwayDelivery(@Valid @RequestBody GiveAwayDeliverySaveReqDto reqDto) {
        return ResponseEntity.ok(arEventFrontLogic.saveGiveAwayDeliveryLogic(reqDto));
    }

    //@TraceNoFilter
    @ApiOperation("당첨 로직")
    @PostMapping(value = "/winning-process")
    public ResponseEntity<ApiResultObjectDto> eventWinningLogic(@RequestBody EventWinningReqDto reqDto) {
        return ResponseEntity.ok(arEventFrontLogic.eventWinningLogic(reqDto));
    }

    @TraceNoFilter
    @ApiOperation("이벤트 통계 (전체 통계 가져오기)")
    @PostMapping(value = "/statics/all")
    public ResponseEntity<ApiResultObjectDto> getStatics(@RequestBody StaticsReqDto reqDto) {
        return ResponseEntity.ok(staticsLogic.getStatics(reqDto.getEventId(), reqDto.getSearchDay()));
    }

    @TraceNoFilter
    @ApiOperation("위치기반 이벤트 조회")
    @PostMapping(value = "/search/proximity")
    public ResponseEntity<ApiResultObjectDto> getSearchProximityInfo(@RequestBody ProximityReqDto reqDto) {
        return ResponseEntity.ok(skApiLogic.callProximityApi(reqDto.getEventId(), reqDto.getLatitude(), reqDto.getLongitude()));
    }

    @TraceNoFilter
    @ApiOperation("당첨버튼 정보 가져오기")
    @GetMapping(value = "/winning-button/detail/{arEventWinningButtonId}")
    public ResponseEntity<ApiResultObjectDto> getWinningButtonDetail(@PathVariable(value = "arEventWinningButtonId") int arEventWinningButtonId) {
        return ResponseEntity.ok(arEventFrontLogic.getWinningButtonDetailLogic(arEventWinningButtonId));
    }

    @TraceNoFilter
    @ApiOperation("경품 수령하기")
    @PostMapping(value = "/receipt/give-away")
    public ResponseEntity<ApiResultObjectDto> receiptGiveAway(@RequestBody GiveAwayReceiptReqDto reqDto) {
        return ResponseEntity.ok(arEventFrontLogic.receiptGiveAwayLogic(reqDto));
    }

    @XssFilter
    @TraceNoFilter
    @ApiOperation("참여버튼 로그 저장하기")
    @PostMapping(value = "/attend-button-log/save")
    public ResponseEntity<ApiResultObjectDto> saveEventLogAttendButton(@RequestBody SaveEventLogAttendButtonReqDto reqDto) {
        return ResponseEntity.ok(arEventFrontLogic.saveEventLogAttendButtonLogic(reqDto.getEventId(), reqDto.getSuccessYn()));
    }

    @XssFilter
    @EncryptDataFilter("receiverPhoneNumber")
    @TraceNoFilter
    @ApiOperation("임시비밀번호 발송하기")
    @PostMapping(value = "/sms/send-temporary-password")
    public ResponseEntity<ApiResultObjectDto> sendSmsTemporaryPassword(@RequestBody EventIdPhoneNumberReqDto reqDto) {
        return ResponseEntity.ok(arEventFrontLogic.sendSmsTemporaryPasswordLogic(reqDto));
    }

    @EncryptDataFilter("userPhoneNumber")
    @TraceNoFilter
    @ApiOperation("NFT 보관함")
    @PostMapping(value = "/nft-repository/search")
    public ResponseEntity<ApiResultObjectDto> getEventNftRepositoryList(@RequestBody EventIdPhoneNumberReqDto reqDto) {
        return ResponseEntity.ok(arEventFrontLogic.getEventNftRepositoryListLogic(reqDto));
    }

    @TraceNoFilter
    @ApiOperation("NFT 보관함 상세정보")
    @GetMapping(value = "/nft-repository/search/{arNftRepositoryId}")
    public ResponseEntity<ApiResultObjectDto> getEventNftRepositoryDetail(@PathVariable("arNftRepositoryId") Long arNftRepositoryId) {
        return ResponseEntity.ok(arEventFrontLogic.getEventNftRepositoryDetailLogic(arNftRepositoryId));
    }

    @XssFilter
    @EncryptDataFilter("userPhoneNumber")
    @TraceNoFilter
    @ApiOperation("NFT 지갑 주소 저장")
    @PostMapping(value = "/nft-wallet-address/save")
    public ResponseEntity<ApiResultObjectDto> saveNftWallet(@Valid @RequestBody NftWalletSaveReqDto reqDto) {
        return ResponseEntity.ok(arEventFrontLogic.saveNftWalletLogic(reqDto));
    }

    @TraceNoFilter
    @ApiOperation("이벤트에 응모형, NFT 당첨형이 있는지 확인")
    @GetMapping(value = "/check/subscription-nft/{eventId}")
    public ResponseEntity<ApiResultObjectDto> getSubscriptionNftYn(@PathVariable("eventId") String eventId) {
        return ResponseEntity.ok(arEventFrontLogic.getSubscriptionNftYnLogic(eventId));
    }

    @TraceNoFilter
    @ApiOperation("쿠폰 보관함 상세정보")
    @GetMapping(value = "/coupon-repository/search/{couponRepositoryId}")
    public ResponseEntity<ApiResultObjectDto> getEventCouponRepositoryDetail(@PathVariable("couponRepositoryId") Long couponRepositoryId) {
        return ResponseEntity.ok(arEventFrontLogic.getEventCouponRepositoryDetailLogic(couponRepositoryId));
    }

    @TraceNoFilter
    @ApiOperation("쿠폰 사용처리 하기")
    @PostMapping(value = "/coupon-repository/use")
    public ResponseEntity<ApiResultObjectDto> useNftCoupon(@RequestBody String jsonStr) {
        long couponRepositoryId = GsonUtils.parseLongFromJsonStr(jsonStr, "couponRepositoryId");
        return ResponseEntity.ok(arEventFrontLogic.useCouponLogic(couponRepositoryId));
    }

    @TraceNoFilter
    @ApiOperation("비밀번호를 받는 이벤트 인지 확인")
    @GetMapping(value = "/check/password-event/{eventId}")
    public ResponseEntity<ApiResultObjectDto> getIsPasswordEvent(@PathVariable("eventId") String evenId) {
        return ResponseEntity.ok(arEventFrontLogic.getCheckPasswordEventLogic(evenId));
    }

    @TraceNoFilter
    @ApiOperation("pv 로그 저장")
    @PostMapping(value = "/pv-log/save")
    public ResponseEntity<ApiResultObjectDto> savePvLog(@RequestBody EventLogPvReqDto resDto) {
        return ResponseEntity.ok(arEventFrontLogic.savePvLogLogic(resDto));
    }

    @ApiOperation("SMS 인증 발송하기")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 524, message = "IOE 에러"),
            @ApiResponse(code = 803, message = "필수 파라미터가 없음"),
            @ApiResponse(code = 849, message = "SMS 인증 발송 개수 초과 (5회)"),
    })
    @TraceNoFilter
    @EncryptDataFilter("phoneNumber")
    @PostMapping(value = "/sms-auth/send")
    public ResponseEntity<ApiResultObjectDto> sendSmsAuth(@RequestBody SmsAuthReqDto reqDto) {
        return ResponseEntity.ok(arEventFrontLogic.sendSmsAuthLogic(reqDto));
    }

    @TraceNoFilter
    @ApiOperation("SMS 코드 인증 하기")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 803, message = "필수 파라미터가 없음"),
            @ApiResponse(code = 850, message = "SMS 인증 코드 에러"),
            @ApiResponse(code = 851, message = "SMS 인증 시간만료(3분초과)"),
    })
    @PostMapping(value = "/sms-code/auth")
    public ResponseEntity<ApiResultObjectDto> authSmsCode(@RequestBody SmsAuthReqDto reqDto) {
        return ResponseEntity.ok(arEventFrontLogic.authSmsCodeLogic(reqDto));
    }

    @TraceNoFilter
    @ApiOperation("이벤트 기본정보 가져오기(webEventBase + ArEvent)")
    @GetMapping(value = "/base-info/{eventId}")
    public ResponseEntity<ApiResultObjectDto> getEventBasicInfo(@PathVariable(value = "eventId") String eventId) {
        return ResponseEntity.ok(arEventFrontLogic.getEventBasicInfoLogic(eventId));
    }

    @ApiOperation("이벤트 법적문구 HTML 가져오기 (개인정보 취급방침 : TERMS, 서비스이용약관 : PRIVACY)")
    @GetMapping(value = "/event-law/event-type/{eventType}/law-type/{lawType}")
    public ResponseEntity<ApiResultObjectDto> getEventLawContents(@PathVariable("eventType") String eventType, @PathVariable("lawType") String lawType) {
        return ResponseEntity.ok(arEventFrontLogic.getEventLawContentsLogic(eventType, lawType));
    }

    @ApiOperation("이벤트 법적문구 HTML 가져오기")
    @GetMapping(value = "/event-law/idx/{idx}")
    public ResponseEntity<ApiResultObjectDto> getEventLawContentsByIdx(@PathVariable("idx") Integer idx) {
        return ResponseEntity.ok(arEventFrontLogic.getEventLawContentsByIdxLogic(idx));
    }

}
