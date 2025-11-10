package kr.co.syrup.adreport.controller.rest.web.event;

import io.swagger.annotations.ApiOperation;
import kr.co.syrup.adreport.framework.common.annotation.TraceNoFilter;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.ErrorCodeDefine;
import kr.co.syrup.adreport.web.event.dto.request.*;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.dto.response.PhotoPrintCountResDto;
import kr.co.syrup.adreport.web.event.dto.response.PhotoboxDetailResDto;
import kr.co.syrup.adreport.web.event.logic.ArEventPhotoFrontLogic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(value = "*")
@RestController
@RequestMapping(value = "/api/v1/web-event-front-photo")
public class WebEventFrontPhotoController {

    private final int httpSuccessCode = HttpStatus.OK.value();

    @Autowired
    private ArEventPhotoFrontLogic arEventPhotoFrontLogic;

    @TraceNoFilter
    @PostMapping(value = "/photobox/detail")
    @ApiOperation("AR 포토함 정보 조회")
    public ResponseEntity<ApiResultObjectDto> getWebArPhotoDetail(@RequestBody PhotoboxDetailReqDto req) {
        PhotoboxDetailResDto res = new PhotoboxDetailResDto();

        int resultCode = httpSuccessCode;

        if (PredicateUtils.isNull(req.getEventId())) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

            return ResponseEntity.ok(new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .result(res)
                    .build());
        }

        res = arEventPhotoFrontLogic.getWebArPhotoDetail(req);

        return ResponseEntity.ok(new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(res)
                .build());
    }

    @TraceNoFilter
    @PostMapping(value = "/print/selectPrintCount")
    @ApiOperation("사진 출력 횟수 조회")
    public ResponseEntity<ApiResultObjectDto> getPrintCount(@RequestBody PhotoPrintCountReqDto req) {
        PhotoPrintCountResDto res = new PhotoPrintCountResDto();

        int resultCode = httpSuccessCode;

        if (PredicateUtils.isNull(req.getEventId())) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }

        if (PredicateUtils.isNull(req.getClientUniqueKey()) && PredicateUtils.isNull(req.getOcbMbrId())) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }

        if (resultCode != httpSuccessCode) {
            return ResponseEntity.ok(new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .result(res)
                    .build());
        }

        res = arEventPhotoFrontLogic.getSelectPrintCount(req);

        return ResponseEntity.ok(new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(res)
                .build());
    }

    @TraceNoFilter
    @PostMapping(value = "/print/savePrintStatus")
    @ApiOperation("사진 출력 상태 기록")
    public ResponseEntity<ApiResultObjectDto> savePrintStatus(@RequestBody SavePrintStatusReqDto req) {
        int resultCode = httpSuccessCode;

        if (PredicateUtils.isNull(req.getEventId())) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_EVENT_ID_NULL.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }

        if (PredicateUtils.isNull(req.getClientUniqueKey()) && PredicateUtils.isNull(req.getOcbMbrId())) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }

        if (PredicateUtils.isNull(req.getPrintResultStatus())) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }

        if (resultCode != httpSuccessCode) {
            return ResponseEntity.ok(new ApiResultObjectDto().builder()
                    .resultCode(resultCode)
                    .build());
        }

        arEventPhotoFrontLogic.savePrintStatus(req);

        return ResponseEntity.ok(new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .build());
    }


}
