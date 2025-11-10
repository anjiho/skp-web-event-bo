package kr.co.syrup.adreport.controller.rest.stamp.event;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kr.co.syrup.adreport.framework.common.annotation.EncryptDataFilter;
import kr.co.syrup.adreport.framework.common.annotation.TraceNoFilter;
import kr.co.syrup.adreport.framework.common.annotation.XssFilter;
import kr.co.syrup.adreport.framework.utils.GsonUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.stamp.event.define.StampEventLogConnectTypeDefine;
import kr.co.syrup.adreport.stamp.event.dto.request.*;
import kr.co.syrup.adreport.stamp.event.dto.response.StampGateDetailResDto;
import kr.co.syrup.adreport.stamp.event.dto.response.StampMainInfoResDto;
import kr.co.syrup.adreport.stamp.event.dto.response.StampPanDetailResDto;
import kr.co.syrup.adreport.stamp.event.logic.StampFrontLogic;
import kr.co.syrup.adreport.stamp.event.logic.StampWinningLogic;
import kr.co.syrup.adreport.stamp.event.service.StampAsyncService;
import kr.co.syrup.adreport.web.event.define.ErrorCodeDefine;
import kr.co.syrup.adreport.web.event.dto.request.EventWinningReqDto;
import kr.co.syrup.adreport.web.event.dto.request.GiveAwayDeliverySaveReqDto;
import kr.co.syrup.adreport.web.event.dto.request.GiveAwayReceiptReqDto;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@CrossOrigin(value = "*")
@Api(value="ar-event", description="스탬프 이벤트 프론트 컨트롤러")
@RequestMapping(value = "/api/v1/stamp-event-front")
public class StampEventFrontController {

    private final int httpSuccessCode = HttpStatus.OK.value();

    @Autowired
    private StampWinningLogic stampWinningLogic;

    @Autowired
    private StampFrontLogic stampFrontLogic;

    @Autowired
    private StampAsyncService stampAsyncService;

    @XssFilter
    @EncryptDataFilter("phoneNumber")
    @ApiOperation("스탬프 당첨 로직 - 2차 개발건")
    @PostMapping(value = "/winning-process")
    public ResponseEntity<ApiResultObjectDto>stampEventWinningImproveLogic(@RequestBody EventWinningReqDto reqDto) {
        return ResponseEntity.ok(stampWinningLogic.improvedProcessStampEventWinning(reqDto));
    }

    @XssFilter
    @EncryptDataFilter("name,address,addressDetail,phoneNumber")
    @ApiOperation("스탬프 경품배송정보 저장하기")
    @PostMapping(value = "/winning-delivery/save")
    public ResponseEntity<ApiResultObjectDto>saveStampEventGiveAwayDelivery(@Valid @RequestBody GiveAwayDeliverySaveReqDto reqDto) {
        return ResponseEntity.ok(stampFrontLogic.saveStampGiveAwayDeliveryLogic(reqDto));
    }

    @ApiOperation("스탬프 TR 위치 참여")
    @PostMapping(value = "/tr/location-attend")
    public ResponseEntity<ApiResultObjectDto>attendStampTrLocation(@Valid @RequestBody StampTrLocationAttendReqDto reqDto) {
        return ResponseEntity.ok(stampFrontLogic.attendStampTrLocationLogic(reqDto));
    }

    @ApiOperation("스탬프 순서 참여일때 참여 가능한지 확인하기")
    @PostMapping(value = "/sort-check")
    public ResponseEntity<ApiResultObjectDto>checkStampAttendSort(@Valid @RequestBody StampSortCheckReqDto reqDto) {
        return ResponseEntity.ok(stampFrontLogic.checkStampAttendSortLogic(reqDto));
    }

    @ApiOperation("스탬프 경품 수령하기")
    @PostMapping(value = "/giveaway/receipt")
    public ResponseEntity<ApiResultObjectDto>receiptStampGiveaway(@RequestBody GiveAwayReceiptReqDto reqDto) {
        return ResponseEntity.ok(stampFrontLogic.receiptStampGiveawayLogic(reqDto));
    }

    @ApiOperation("스탬프 이벤트 게이트 페이지 정보")
    @PostMapping(value = "/gate/detail")
    public ResponseEntity<ApiResultObjectDto> getGateDetail(@Valid @RequestBody StampGateDetailReqDto req) {
        int resultCode = httpSuccessCode;

        if(PredicateUtils.isNull(req) || PredicateUtils.isNull(req.getEventId())){
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PAGE_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            return ResponseEntity.ok(new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .build());
        }

        StampGateDetailResDto res = stampFrontLogic.getGateDetail(req);

        CompletableFuture.supplyAsync(() -> stampAsyncService.asyncInsertStampEventLogConnect(res.getStampMainInfo().getStpId(), StampEventLogConnectTypeDefine.MAIN.name()));

        ApiResultObjectDto result = new ApiResultObjectDto().builder().result(res).resultCode(resultCode).build();
        return ResponseEntity.ok(result);
    }

    @TraceNoFilter
    @ApiOperation("스탬프 판 페이지 정보")
    @PostMapping(value = "/pan/detail")
    public ResponseEntity<ApiResultObjectDto> getPanDetail(@Valid @RequestBody StampPanDetailReqDto req) {
        int resultCode = httpSuccessCode;

        if (PredicateUtils.isNull(req) || PredicateUtils.isNull(req.getEventId())) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PAGE_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            return ResponseEntity.ok(new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .build());
        }

        if (PredicateUtils.isNull(req.getAttendValue())) {

            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
            return ResponseEntity.ok(new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .build());

        }

        StampPanDetailResDto res = stampFrontLogic.getPanDetail(req);

        CompletableFuture.supplyAsync(() -> stampAsyncService.asyncInsertStampEventLogConnect(res.getStampMainInfo().getStpId(), StampEventLogConnectTypeDefine.PAN.name()));

        ApiResultObjectDto result = new ApiResultObjectDto().builder().result(res).resultCode(resultCode).build();
        return ResponseEntity.ok(result);
    }

    @TraceNoFilter
    @ApiOperation("스탬프 메인 페이지 정보")
    @PostMapping(value = "/pan/mainInfo")
    public ResponseEntity<ApiResultObjectDto> getPanDetail(@Valid @RequestBody StampMainInfoReqDto req) {
        int resultCode = httpSuccessCode;

        if (PredicateUtils.isNull(req) || PredicateUtils.isNull(req.getEventId())) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PAGE_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            return ResponseEntity.ok(new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .build());
        }

        StampMainInfoResDto res = stampFrontLogic.getStampMainInfo(req);
        ApiResultObjectDto result = new ApiResultObjectDto().builder().result(res).resultCode(resultCode).build();
        return ResponseEntity.ok(result);
    }

}
