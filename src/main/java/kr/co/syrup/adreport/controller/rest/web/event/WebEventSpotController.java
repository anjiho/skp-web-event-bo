package kr.co.syrup.adreport.controller.rest.web.event;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.syrup.adreport.survey.go.service.SurveyGoStaticsService;
import kr.co.syrup.adreport.web.event.dto.request.UpsertStampReqDto;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.entity.ArEventEntity;
import kr.co.syrup.adreport.web.event.logic.SpotServiceLogic;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import kr.co.syrup.adreport.web.event.service.BatchService;
import kr.co.syrup.adreport.web.event.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin(value = "*")
@Api(value="ar-event", description="AR 이벤트 스팟성 서비스 관려 컨트롤러")
@RequestMapping(value = "/api/v1/web-event-spot")
public class WebEventSpotController {

    @Autowired
    private SpotServiceLogic spotServiceLogic;

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private SurveyGoStaticsService surveyGoStaticsService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private LogService logService;

    @ApiOperation("스탬프 관련 이벤트 키 조회")
    @GetMapping(value = "/stamp/{searchKey}")
    public ResponseEntity<ApiResultObjectDto> getStampEvent(@PathVariable("searchKey") String searchKey) {
        return ResponseEntity.ok(spotServiceLogic.getStampEventLogic(searchKey));
    }

    @ApiOperation("스탬프 관련 값 insert/update")
    @PostMapping(value = "/stamp/upsert")
    public ResponseEntity<ApiResultObjectDto> upsertStampEventKey(@RequestBody UpsertStampReqDto reqDto) {
        return ResponseEntity.ok(spotServiceLogic.upsertStampEventKeyLogic(reqDto));
    }

    @ApiOperation("설정값 조회")
    @GetMapping(value = "/select/{searchKey}")
    public ResponseEntity<ApiResultObjectDto> getCommonSettings(@PathVariable("searchKey") String searchKey) {
        return ResponseEntity.ok(spotServiceLogic.getStampEventLogic(searchKey));
    }

//    @GetMapping(value = "/process-survey-answer-data")
    public void processSurveyAnswerData() {

        List<String>eventIdList = logService.getSurveyEventIdList();

        for (String eventId : eventIdList) {
            ArEventEntity arEvent = arEventService.findArEventByEventId(eventId);

            List<String> headerTitleList = surveyGoStaticsService.makeSurveyRawTableTitle(arEvent.getArEventId());

            List<Object>fieldValueList = new ArrayList<>();
            fieldValueList.add("event_id");
            fieldValueList.add("survey_log_attend_id");
            for (int i = 0; i < (headerTitleList.size() - 1); i++) {
                String fieldValue = "answer_" + (i + 1);
                fieldValueList.add(fieldValue);
            }

            surveyGoStaticsService.makeSurveyRawTableValue3(eventId, 1000, arEvent.getArEventId(), fieldValueList);

        }
    }
}
