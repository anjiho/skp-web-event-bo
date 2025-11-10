package kr.co.syrup.adreport.controller.rest.web.event;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import kr.co.syrup.adreport.framework.common.annotation.TraceNoFilter;
import kr.co.syrup.adreport.stamp.event.service.StampFrontService;
import kr.co.syrup.adreport.web.event.dto.request.CommonLogPersonAgreeReqDto;
import kr.co.syrup.adreport.web.event.dto.request.CommonLogPvReqDto;
import kr.co.syrup.adreport.web.event.dto.request.EventLogPersonAgreeReqDto;
import kr.co.syrup.adreport.web.event.dto.request.ProximityApiReqDto;
import kr.co.syrup.adreport.web.event.dto.request.api.ProximityDocentApiReqDto;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.logic.ArEventOuterLogic;
import kr.co.syrup.adreport.web.event.logic.SkApiLogic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@CrossOrigin(value = "*")
@RestController
@RequestMapping(value = "/api/v1/web-event-open")
public class WebEventOpenController {

    @Autowired
    private ArEventOuterLogic arEventOuterLogic;

    @Autowired
    private SkApiLogic skApiLogic;

    @Autowired
    private StampFrontService stampFrontService;

    //[WEB AR] PRTG 모니터링을 위한 페이지 생성 (SS-20203)
    @GetMapping(value = "/prtg")
    @ApiOperation("PRTG 모니터링을 위한 페이지 생성")
    public Object getPRTGMonitoring() {
        return arEventOuterLogic.getPRTGMonitoringLogic();
    }

    @PostMapping(value = "/common-log-pv/save")
    @ApiOperation("PV 로그 저장하기")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "로그 저장 성공")
    })
    public ApiResultObjectDto saveCommonLogPv(@RequestBody CommonLogPvReqDto reqDto) {
        return arEventOuterLogic.saveCommonLogPvLogic(reqDto);
    }

    @PostMapping(value = "/common-log-person-agree/save")
    @ApiOperation("개인정보 수집 로그 저장하기")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "로그 저장 성공"),
            @ApiResponse(code = 803, message = "동의 할 아이디가 없으면 에러"),
            @ApiResponse(code = 849, message = "개인정보 활용 동의 저장시 아이디 중복 에러")
    })
    public ApiResultObjectDto saveTempPusanExpoPvLog(@Valid @RequestBody CommonLogPersonAgreeReqDto reqDto) {
        return arEventOuterLogic.saveCommonLogPersonAgreeLogic(reqDto);
    }

    @TraceNoFilter
    @PostMapping(value = "/log-person-agree/save")
    @ApiOperation("AR 포토 사진활용 동의 - 개인정보 수집로그 저장하기")
    public ApiResultObjectDto saveLogPersonAgree(@Valid @RequestBody EventLogPersonAgreeReqDto reqDto) {
        CommonLogPersonAgreeReqDto req = new CommonLogPersonAgreeReqDto();

        req.setEventName(reqDto.getEventId());
        req.setAgreeId(reqDto.getAgreeId());
        req.setPhoneNumber(req.getPhoneNumber());

        return arEventOuterLogic.saveCommonLogPersonAgreeLogic(req);
    }

    @TraceNoFilter
    @PostMapping(value = "/proximity-absolute-coordinates/find")
    @ApiOperation("프록시미티 절대좌표 API (AR도슨트 기능 관련)")
    public ResponseEntity<ApiResultObjectDto> findProximityAbsoluteCoordinates(@Valid @RequestBody ProximityDocentApiReqDto reqDto) {
        return ResponseEntity.ok(skApiLogic.callProximityDocentApiLogic(reqDto));
    }

    @TraceNoFilter
    @GetMapping(value = "/attend-code/{attendCode}/event-id/{eventId}")
    @ApiOperation("참여코드 사용여부 히스토리 조회")
    public ResponseEntity<ApiResultObjectDto> getAttendCodeHistory(@PathVariable("attendCode") String attendCode,
                                                                   @PathVariable("eventId") String eventId) {
        return ResponseEntity.ok(arEventOuterLogic.getAttendCodeHistoryLogic(eventId, attendCode));
    }

    @TraceNoFilter
    @GetMapping(value = "/stamp/event-id/{eventId}/attend-code/{attendCode}")
    @ApiOperation("스탬프 참여코드 사용여부 히스토리 조회")
    public ResponseEntity<ApiResultObjectDto> getStampAttendCodeHistory(@PathVariable("eventId") String eventId,
                                                                        @PathVariable("attendCode") String attendCode) {
        return ResponseEntity.ok(arEventOuterLogic.getStampAttendCodeHistoryLogic(eventId, attendCode));
    }
}
