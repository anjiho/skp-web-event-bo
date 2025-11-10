package kr.co.syrup.adreport.controller.rest.survey.go;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kr.co.syrup.adreport.framework.common.annotation.RequiredIpPermission;
import kr.co.syrup.adreport.framework.common.annotation.SetSodarMemberSession;
import kr.co.syrup.adreport.framework.common.annotation.TraceNoFilter;
import kr.co.syrup.adreport.framework.common.annotation.XssFilter;
import kr.co.syrup.adreport.survey.go.logic.SurveyGoSodarLogic;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.logic.ArEventLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(value = "*")
@Api(value="ar-event", description="서베이고 소다 데이터 컨트롤러")
@RequestMapping(value = "/api/v1/survey-go-sodar")
public class SurveyGoSodarController {

    @Autowired
    private SurveyGoSodarLogic surveyGoSodarLogic;

    @Autowired
    private ArEventLogic arEventLogic;

    @XssFilter
    @SetSodarMemberSession
    @TraceNoFilter
    @ApiOperation("서베이고 AR 이벤트 저장")
    @PostMapping(value = "/save")
    public ResponseEntity<ApiResultObjectDto> saveSurveyGoSodarData(@RequestPart(value = "jsonStr") String jsonStr,
                                                                    @RequestPart(value = "excelFile", required = false) MultipartFile excelFile) {
        return ResponseEntity.ok(surveyGoSodarLogic.saveSurveyGoLogic(jsonStr, excelFile));
    }

    @XssFilter
    @SetSodarMemberSession
    @TraceNoFilter
    @ApiOperation("서베이고 AR 이벤트 업데이트")
    @PostMapping(value = "/update")
    public ResponseEntity<ApiResultObjectDto> updateSurveyGoSodarData(@RequestPart(value = "eventId") String eventId,
                                                                      @RequestPart(value = "jsonStr") String jsonStr,
                                                                      @RequestPart(value = "excelFile", required = false) MultipartFile excelFile) {
        return ResponseEntity.ok(surveyGoSodarLogic.updateSurveyGoLogic(eventId, jsonStr, excelFile));
    }

    @SetSodarMemberSession
    @TraceNoFilter
    @ApiOperation("서베이고 계약정보 가져오기")
    @GetMapping(value = "/detail")
    public ResponseEntity<ApiResultObjectDto> getSurveyGoSodarData(@RequestParam(value = "eventId") String eventId) {
        return ResponseEntity.ok(arEventLogic.getArEventDetailLogic(eventId));
    }
}
