package kr.co.syrup.adreport.controller.rest.survey.go;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import kr.co.syrup.adreport.framework.common.annotation.EncryptDataFilter;
import kr.co.syrup.adreport.framework.common.annotation.TraceNoFilter;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.stamp.event.define.StampEventLogConnectTypeDefine;
import kr.co.syrup.adreport.survey.go.dto.request.GenderAgeReqDto;
import kr.co.syrup.adreport.survey.go.dto.request.SurveyInfoSelectMobileReqDto;
import kr.co.syrup.adreport.survey.go.dto.request.SurveyLogAttendResultReqDto;
import kr.co.syrup.adreport.survey.go.dto.request.SurveyResultSaveMobileReqDto;
import kr.co.syrup.adreport.survey.go.dto.response.SurveyInfoSelectMobileResDto;
import kr.co.syrup.adreport.survey.go.dto.response.SurveyResultSaveMobileResDto;
import kr.co.syrup.adreport.survey.go.entity.SurveyLogAttendEntity;
import kr.co.syrup.adreport.survey.go.logic.SurveyGoMobileLogic;
import kr.co.syrup.adreport.survey.go.service.SurveyEntityService;
import kr.co.syrup.adreport.web.event.define.ErrorCodeDefine;
import kr.co.syrup.adreport.web.event.dto.request.WebArGateReqDto;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@CrossOrigin(value = "*")
@Api(value="ar-event", description="서베이고 모바일 데이터 컨트롤러")
@RequestMapping(value = "/api/v1/survey-go-mobile")
public class SurveyGoMobileController {

    private final int httpSuccessCode = HttpStatus.OK.value();

    @Autowired
    private SurveyGoMobileLogic surveyGoMobileLogic;

    @Autowired
    private SurveyEntityService surveyEntityService;

    @TraceNoFilter
    @ApiOperation("서베이고 문항 정보 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 801, message = "EVENT_ID 없음")
    })
    @PostMapping(value = "/detail")
    public ResponseEntity<ApiResultObjectDto> getSurveySubjectData(@RequestBody SurveyInfoSelectMobileReqDto req) {
        int resultCode = httpSuccessCode;
        SurveyInfoSelectMobileResDto res = new SurveyInfoSelectMobileResDto();

        if (PredicateUtils.isNull(req.getEventId())) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            return ResponseEntity.ok(new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .result(res)
                    .build());
        }

        if(PredicateUtils.isNull(req.getSurveyLogAttendId())){
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            return ResponseEntity.ok(new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .result(res)
                    .build());
        }

        SurveyLogAttendEntity surveyLogAttendEntity = surveyEntityService.findSurveyLogAttendBySurveyLogAttendId(req.getSurveyLogAttendId());

        if (PredicateUtils.isNull(surveyLogAttendEntity)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_NOT_ISSUED_SURVEY_LOG_ID.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            return ResponseEntity.ok(new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .result(res)
                    .build());

        }

        if (surveyLogAttendEntity.getIsSubmit() == true) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EXPIRED_SURVEY_LOG_ID.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            return ResponseEntity.ok(new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .result(res)
                    .build());
        }

        res = surveyGoMobileLogic.getSurveySubjectData(req);

        return ResponseEntity.ok(new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(res)
                .build());
    }

    @TraceNoFilter
    @ApiOperation("서베이고 결과 입력")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 801, message = "EVENT_ID 없음"),
            @ApiResponse(code = 803, message = "필수 파라미터가 없음")
    })
    @PostMapping(value = "/answerSave")
    public ResponseEntity<ApiResultObjectDto> saveSurveyResult(@RequestBody SurveyResultSaveMobileReqDto req) {

        int resultCode = httpSuccessCode;
        SurveyResultSaveMobileResDto res = new SurveyResultSaveMobileResDto();

        if (StringUtils.isEmpty(req.getEventId())) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            return ResponseEntity.ok(new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .result(res)
                    .build());
        }

        if (StringUtils.isEmpty(req.getSurveyLogAttendId()) || PredicateUtils.isNullList(req.getAnswerList())) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            return ResponseEntity.ok(new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .result(res)
                    .build());
        }

        res = surveyGoMobileLogic.saveSurveyResult(req);

        return ResponseEntity.ok(new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(res)
                .build());
    }

    @TraceNoFilter
    @EncryptDataFilter("phoneNumber")
    @ApiOperation("서베이고 참여가능 여부 확인")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 801, message = "EVENT_ID 없음"),
            @ApiResponse(code = 819, message = "참여코드 참여횟수 초과"),
            @ApiResponse(code = 854, message = "핸드폰번호 참여횟수 초과")
    })
    @PostMapping(value = "/survey-attend/possible")
    public ResponseEntity<ApiResultObjectDto> checkPossibleSurveyAttend(@RequestBody WebArGateReqDto reqDto) {
        return ResponseEntity.ok(surveyGoMobileLogic.checkPossibleSurveyAttendLogic(reqDto));
    }

    @TraceNoFilter
    @ApiOperation("서베이고 참여가능한 성별/연령대 여부 확인")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 602, message = "설문참여 참여 제한 에러(성별/연령대)"),
            @ApiResponse(code = 801, message = "EVENT_ID 없음"),
            @ApiResponse(code = 803, message = "필수 파라미터가 없음")
    })
    @PostMapping(value = "/survey-attend-target/possible")
    public ResponseEntity<ApiResultObjectDto> checkPossibleSurveyGenderAgeAttend(@RequestBody GenderAgeReqDto reqDto) {
        return ResponseEntity.ok(surveyGoMobileLogic.checkPossibleSurveyGenderAgeAttendLogic(reqDto));
    }

    @TraceNoFilter
    @ApiOperation("서베이고 참여 결과 저장")
    @PostMapping(value = "/survey-attend-result/save")
    public ResponseEntity<ApiResultObjectDto> saveSurveyAttendResult(@RequestBody SurveyLogAttendResultReqDto reqDto) {
        return ResponseEntity.ok(surveyGoMobileLogic.saveSurveyAttendResultLogic(reqDto));
    }
}
