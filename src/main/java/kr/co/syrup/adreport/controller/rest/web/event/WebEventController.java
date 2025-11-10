package kr.co.syrup.adreport.controller.rest.web.event;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.syrup.adreport.framework.common.annotation.RequiredIpPermission;
import kr.co.syrup.adreport.framework.common.annotation.SetSodarMemberSession;
import kr.co.syrup.adreport.framework.common.annotation.TraceNoFilter;
import kr.co.syrup.adreport.framework.common.annotation.XssFilter;
import kr.co.syrup.adreport.framework.utils.GsonUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.web.event.define.CommonSettingsDefine;
import kr.co.syrup.adreport.web.event.define.ErrorCodeDefine;
import kr.co.syrup.adreport.web.event.dto.request.*;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.dto.response.SodaApiResultObjectDto;
import kr.co.syrup.adreport.web.event.entity.CommonSettingsEntity;
import kr.co.syrup.adreport.web.event.logic.ArEventLogic;
import kr.co.syrup.adreport.web.event.logic.ExcelLogic;
import kr.co.syrup.adreport.web.event.logic.SkApiLogic;
import kr.co.syrup.adreport.web.event.logic.XtransLogic;
import kr.co.syrup.adreport.web.event.service.ArEventFrontService;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import kr.co.syrup.adreport.web.event.service.StaticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@CrossOrigin(value = "*")
@Api(value="ar-event", description="AR 이벤트 소다 데이터 컨트롤러")
@RequestMapping(value = "/api/v1/web-event")
public class WebEventController {

    @Autowired
    private ArEventLogic arEventLogic;

    @Autowired
    private ExcelLogic excelLogic;

    @Autowired
    private SkApiLogic skApiLogic;

    @Autowired
    private XtransLogic xtransLogic;

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private StaticsService staticsService;

    //@RequiredIpPermission
    @SetSodarMemberSession
    @TraceNoFilter
    @GetMapping(value = "/category/all")
    @ApiOperation("이벤트 카테고리 정보 가져오기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryType", value = "카테고리 타입(라디오버튼 : radio, 셀렉트박스 : select)", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "parentCode", value = "부모의 카테고리 코드", dataType = "string", paramType = "query"),
    })
    public ResponseEntity<ApiResultObjectDto> findAllByEventCategory(@RequestParam(value = "categoryType", required = false) String categoryType,
                                                                     @RequestParam(value = "parentCode", required = false) String parentCode) {
        return ResponseEntity.ok(arEventService.findAllEventCategory(categoryType, parentCode));
    }

    //@RequiredIpPermission
    @SetSodarMemberSession
    @GetMapping(value = "/save/request/sample")
    @ApiOperation("AR 이벤트 저장 데이터 샘플")
    public ResponseEntity<ApiResultObjectDto> getEventSaveDtoSample(@RequestBody EventSaveDto eventSaveDto) {
        return ResponseEntity.ok(new ApiResultObjectDto());
    }

    @XssFilter
    //@RequiredIpPermission
    @SetSodarMemberSession
    @TraceNoFilter
    @PostMapping(value = "/save")
    @ApiOperation("AR 이벤트 저장")
    public ResponseEntity<ApiResultObjectDto> saveEvent(@RequestPart(value = "jsonStr") String jsonStr,
                                                        @RequestPart(value = "excelFile", required = false) MultipartFile excelFile) {
        return ResponseEntity.ok(arEventLogic.saveArEventLogic(jsonStr, excelFile));
    }

    @XssFilter
    //@RequiredIpPermission
    @SetSodarMemberSession
    @TraceNoFilter
    @PostMapping(value = "/update")
    @ApiOperation("AR 이벤트 수정")
    public ResponseEntity<ApiResultObjectDto> updateEvent(@RequestPart(value = "eventId") String eventId,
                                                          @RequestPart(value = "jsonStr") String jsonStr,
                                                          @RequestPart(value = "traceNo", required = false) String traceNoStr,
                                                          @RequestPart(value = "excelFile", required = false) MultipartFile excelFile) {
        return ResponseEntity.ok(arEventLogic.updateArEventLogic(eventId, jsonStr, excelFile));
    }

    //@RequiredIpPermission
    @SetSodarMemberSession
    @PostMapping(value = "/excel/verification/attend-code")
    @ApiOperation("엑셀파일 참여코드 검증")
    public ResponseEntity<ApiResultObjectDto> verificationAttendCodeByExcelFile(@RequestPart(value = "excelFile") MultipartFile excelFile,
                                                                                @RequestPart(value = "traceNo", required = false) String traceNoStr) {
        return ResponseEntity.ok(excelLogic.isDuplicateAttendCodeLogic(excelFile, traceNoStr));
    }

    //@RequiredIpPermission
    @SetSodarMemberSession
    @PostMapping(value = "/excel/additional/attend-code")
    @ApiOperation("엑셀파일 참여코드 추가")
    public ResponseEntity<ApiResultObjectDto> validateAttendCodeByAdditionalSave(@RequestPart(value = "excelFile", required = false) MultipartFile excelFile,
                                                                                 @RequestPart(value = "jsonStr") String jsonStr) {
        Map<String, String> resultMap = new HashMap<>();

        String eventId = GsonUtils.parseStringJsonStr(jsonStr, "eventId");
        String commonSettingKey = StringTools.joinStringsNoSeparator(eventId, "_", "addAttendCode");
        CommonSettingsEntity commonSettingsEntity = arEventService.findCommonSettingsBySettingKey(StringTools.joinStringsNoSeparator(eventId, "_", "addAttendCode"));
        if (PredicateUtils.isNull(commonSettingsEntity.getValue())) {
            arEventService.saveCommonSettings(commonSettingKey, "0");
            return ResponseEntity.ok(excelLogic.validateAttendCodeByAdditionalSaveLogic(jsonStr, excelFile, commonSettingKey));
        } else {
            if (PredicateUtils.isNotNull(commonSettingsEntity.getValue())) {
                if (!PredicateUtils.isEqualsStr(commonSettingsEntity.getValue(), "1")) {
                    return ResponseEntity.ok(excelLogic.validateAttendCodeByAdditionalSaveLogic(jsonStr, excelFile, commonSettingKey));
                } else {
                    int resultCode = ErrorCodeDefine.CUSTOM_ERROR_ADD_ATTEND_CODE.code();
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

                    resultMap.put("commonValue", commonSettingsEntity.getValue());
                    resultMap.put("commonDesc", commonSettingsEntity.getCommonSettingsDesc());

                    return ResponseEntity.ok(ApiResultObjectDto.builder().resultCode(resultCode).result(resultMap).build());
                }
            }
        }
        return null;
    }

    //@RequiredIpPermission
    @SetSodarMemberSession
    @TraceNoFilter
    @GetMapping(value = "/detail")
    @ApiOperation("AR 이벤트 상세")
    public ResponseEntity<ApiResultObjectDto> getArEvent(@RequestParam(value = "eventId") String eventId,
                                                         @RequestParam(value = "traceNo", required = false) String traceNo) {
        return ResponseEntity.ok(arEventLogic.getArEventDetailLogic(eventId));
    }

    @RequiredIpPermission
    @PostMapping(value = "/contract/access")
    @ApiOperation("계약 승인 알림 API")
    public ResponseEntity<SodaApiResultObjectDto> accessContract(@RequestBody ContractAccessPushReqDto reqDto) {
        return ResponseEntity.ok(arEventLogic.contractStatusLogic(reqDto, null, null));
    }

    @RequiredIpPermission
    @PostMapping(value = "/contract/modify")
    @ApiOperation("계약 상태 변경 알림 API")
    public ResponseEntity<SodaApiResultObjectDto> modifyContractStatus(@RequestBody ContractModifyReqDto modifyReqDto) {
        return ResponseEntity.ok(arEventLogic.contractStatusLogic(null, modifyReqDto, null));
    }

    @RequiredIpPermission
    @PostMapping(value = "/contract/after")
    @ApiOperation("승인후 계약 변경 알림 API")
    public ResponseEntity<SodaApiResultObjectDto> accessAfterModifyStatus(@RequestBody ContractAfterReqDto afterReqDto) {
        return ResponseEntity.ok(arEventLogic.contractStatusLogic(null, null, afterReqDto));
    }

    @SetSodarMemberSession
    @TraceNoFilter
    @PostMapping(value = "/sms/test-url")
    @ApiOperation("sms 테스트 발송")
    public ResponseEntity<ApiResultObjectDto> callSmsTestUrl(@RequestBody SmsTestUrlReqDto smsTestUrlReqDto) {
        return ResponseEntity.ok(skApiLogic.callSmsTestUrlLogic(smsTestUrlReqDto));
    }

    @SetSodarMemberSession
    @TraceNoFilter
    @PostMapping(value = "/xtrans/upload/give-away-result")
    @ApiOperation("x-trans 당첨결과 엑셀 업로드 전송")
    public ResponseEntity<ApiResultObjectDto> sendXtrans(@RequestBody EventIdReqDto eventIdReqDto) {
        int resultCode = HttpStatus.OK.value();
        Map<String, String> resultMap = new HashMap<>();

        //총개수
        int totalCount = staticsService.countGiveAwayDelivery(eventIdReqDto.getEventId());

        if (totalCount == 0) {
            resultMap.put("resultStatus", "count is 0");
            ApiResultObjectDto resultObjectDto = new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .result(resultMap)
                    .build();
            return ResponseEntity.ok(resultObjectDto);
        }

        if (PredicateUtils.isNull(eventIdReqDto.getLimitCount())) {
            eventIdReqDto.setLimitCount(100);
        }

        CommonSettingsEntity commonSettingsEntity = arEventService.findCommonSettingsBySettingKey(CommonSettingsDefine.GIVE_AWAY_RESULT_LIMIT.name());
        if (PredicateUtils.isNotNull(commonSettingsEntity.getValue())) {
            if (PredicateUtils.isNotEqualsStr(commonSettingsEntity.getValue(), "1")) {
                if (PredicateUtils.isNull(eventIdReqDto.getLimitCount()) || PredicateUtils.isEqualZero(eventIdReqDto.getLimitCount())) {
                    eventIdReqDto.setLimitCount(100);
                }
                CompletableFuture.supplyAsync(() -> xtransLogic.sendSftpGiveAwayExcelLogic(eventIdReqDto.getEventId(), eventIdReqDto.getLimitCount(), CommonSettingsDefine.GIVE_AWAY_RESULT_LIMIT.name()));
            } else {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_LIMIT_EXCEL_DOWNLOAD.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

                resultMap.put("commonValue", commonSettingsEntity.getValue());
                resultMap.put("commonDesc", commonSettingsEntity.getCommonSettingsDesc());
            }
        }
        ApiResultObjectDto resultObjectDto = new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultMap)
                .build();

        return ResponseEntity.ok(resultObjectDto);
    }

    @SetSodarMemberSession
    @TraceNoFilter
    @PostMapping(value = "/xtrans/upload/subscription-winning-result")
    @ApiOperation("x-trans 응모 당첨결과 엑셀 업로드 전송")
    public ResponseEntity<ApiResultObjectDto> sendSubscriptionXtrans(@RequestBody EventIdReqDto eventIdReqDto) {
        int resultCode = HttpStatus.OK.value();
        Map<String, String> resultMap = new HashMap<>();

        if (PredicateUtils.isNull(eventIdReqDto.getLimitCount())) {
            eventIdReqDto.setLimitCount(100);
        }

        CommonSettingsEntity commonSettingsEntity = arEventService.findCommonSettingsBySettingKey(CommonSettingsDefine.SUB_WINNING_RESULT_LIMIT.name());
        if (PredicateUtils.isNotNull(commonSettingsEntity.getValue())) {
            if (PredicateUtils.isNotEqualsStr(commonSettingsEntity.getValue(), "1")) {
                CompletableFuture.supplyAsync(() -> xtransLogic.sendSftpGiveAwayExcelLogic(eventIdReqDto.getEventId(), eventIdReqDto.getLimitCount(), CommonSettingsDefine.GIVE_AWAY_RESULT_LIMIT.name()));
            } else {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_LIMIT_EXCEL_DOWNLOAD.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

                resultMap.put("commonValue", commonSettingsEntity.getValue());
                resultMap.put("commonDesc", commonSettingsEntity.getCommonSettingsDesc());
            }
        }
        ApiResultObjectDto resultObjectDto = new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultMap)
                .build();

        return ResponseEntity.ok(resultObjectDto);
    }

    @TraceNoFilter
    @PostMapping(value = "/xtrans/upload/common-pv-log")
    @ApiOperation("x-trans 공통 PV 로그 엑셀 업로드 전송")
    public ResponseEntity<ApiResultObjectDto> sendXtransCommonPvLog(@RequestBody String jsonStr) {
        return ResponseEntity.ok(xtransLogic.sendSftpCommonPvLogExcelLogic(jsonStr));
    }

    @XssFilter
    @SetSodarMemberSession
    @TraceNoFilter
    @PostMapping(value = "/photo/save")
    @ApiOperation("AR 포토 이벤트 저장")
    public ResponseEntity<ApiResultObjectDto> savePhotoEvent(@RequestPart(value = "jsonStr") String jsonStr,
                                                             @RequestPart(value = "excelFile", required = false) MultipartFile excelFile) {
        return ResponseEntity.ok(arEventLogic.saveArEventLogic(jsonStr, excelFile));
    }

    @XssFilter
    @SetSodarMemberSession
    @TraceNoFilter
    @PostMapping(value = "/photo/update")
    @ApiOperation("AR 포토 이벤트 저장")
    public ResponseEntity<ApiResultObjectDto> updatePhotoEvent(@RequestPart(value = "eventId") String eventId,
                                                               @RequestPart(value = "jsonStr") String jsonStr,
                                                               @RequestPart(value = "traceNo", required = false) String traceNoStr,
                                                               @RequestPart(value = "excelFile", required = false) MultipartFile excelFile) {
        return ResponseEntity.ok(arEventLogic.updateArEventLogic(eventId, jsonStr, excelFile));
    }

    @SetSodarMemberSession
    @TraceNoFilter
    @GetMapping(value = "/photo/detail")
    @ApiOperation("AR 이벤트 상세")
    public ResponseEntity<ApiResultObjectDto> getArPhotoEvent(@RequestParam(value = "eventId") String eventId,
                                                              @RequestParam(value = "traceNo", required = false) String traceNo) {
        return ResponseEntity.ok(arEventLogic.getArEventDetailLogic(eventId));
    }
}
