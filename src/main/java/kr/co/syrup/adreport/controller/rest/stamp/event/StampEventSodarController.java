package kr.co.syrup.adreport.controller.rest.stamp.event;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kr.co.syrup.adreport.framework.common.annotation.SetSodarMemberSession;
import kr.co.syrup.adreport.framework.common.annotation.TraceNoFilter;
import kr.co.syrup.adreport.framework.common.annotation.XssFilter;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.stamp.event.dto.request.WebEventStampTypeListReqDto;
import kr.co.syrup.adreport.stamp.event.dto.response.WebEventStampTypeListResDto;
import kr.co.syrup.adreport.stamp.event.logic.StampSodarLogic;
import kr.co.syrup.adreport.web.event.define.ErrorCodeDefine;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@CrossOrigin(value = "*")
@Api(value="ar-event", description="스탬프 이벤트 소다 데이터 컨트롤러")
@RequestMapping(value = "/api/v1/stamp-event-sodar")
public class StampEventSodarController {

    private final int httpSuccessCode = HttpStatus.OK.value();

    @Autowired
    private StampSodarLogic stampSodarLogic;

    @XssFilter
    @SetSodarMemberSession
    @TraceNoFilter
    @PostMapping(value = "/save")
    @ApiOperation("스탬프 이벤트 저장")
    public ResponseEntity<ApiResultObjectDto> saveStampEvent(@RequestPart(value = "jsonStr") String jsonStr,
                                                             @RequestPart(value = "excelFile", required = false) MultipartFile excelFile) {
        return ResponseEntity.ok(stampSodarLogic.saveSodarStampLogic(jsonStr, excelFile));
    }

    @XssFilter
    @SetSodarMemberSession
    @TraceNoFilter
    @PostMapping(value = "/update")
    @ApiOperation("스탬프 이벤트 수정")
    public ResponseEntity<ApiResultObjectDto> updateStampEvent(@RequestPart(value = "eventId") String eventId,
                                                               @RequestPart(value = "jsonStr") String jsonStr,
                                                               @RequestPart(value = "traceNo", required = false) String traceNoStr,
                                                               @RequestPart(value = "excelFile", required = false) MultipartFile excelFile) {
        return ResponseEntity.ok(stampSodarLogic.updateSodarStampLogic(eventId, jsonStr, excelFile));
    }

    @SetSodarMemberSession
    @TraceNoFilter
    @GetMapping(value = "/get/{eventId}")
    @ApiOperation("스탬프 이벤트 상세")
    public ResponseEntity<ApiResultObjectDto> getStampEvent(@PathVariable(value = "eventId") String eventId) {
        return ResponseEntity.ok(stampSodarLogic.getStampEventLogic(eventId));
    }

    @SetSodarMemberSession
    @TraceNoFilter
    @PostMapping(value = "/web-event-list")
    @ApiOperation("웹 이벤트 목록 조회")
    public ResponseEntity<ApiResultObjectDto> getWebEventStampTypeList(@RequestBody WebEventStampTypeListReqDto req) {
        int resultCode = httpSuccessCode;
        WebEventStampTypeListResDto res = new WebEventStampTypeListResDto();

        if(PredicateUtils.isNull(req) || PredicateUtils.isNull(req.getPage())){
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PAGE_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            return ResponseEntity.ok(new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .result(res)
                    .build());
        }

        if(PredicateUtils.isNull(req.getSize())){
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PAGE_SIZE_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            return ResponseEntity.ok(new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .result(res)
                    .build());
        }

        return ResponseEntity.ok(stampSodarLogic.getEventStampTypeList(req));
    }
}
