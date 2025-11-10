package kr.co.syrup.adreport.controller.rest.web.event;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kr.co.syrup.adreport.framework.common.annotation.TraceNoFilter;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.logic.ExcelLogic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@CrossOrigin(value = "*")
@Api(value="ar-event", description="A  R 이벤트 파일 업로드 컨트롤러")
@RequestMapping(value = "/api/v1/web-event-file-upload")
public class WebEventFileUploadController {

    @Autowired
    private ExcelLogic excelLogic;

    @TraceNoFilter
    @ApiOperation(value = "임시 NFT 토큰 정보 엑셀 업로드")
    @PostMapping(value = "/excel/temporary/nft-token-info", produces = "application/json;")
    public ResponseEntity<ApiResultObjectDto> uploadArEventNftInfo2(@RequestPart(value = "excelFile") MultipartFile excelFile,
                                                                    @RequestPart(value = "jsonStr") String jsonStr) {
        return ResponseEntity.ok(excelLogic.saveTemporaryNftIdListByExcelLogic(excelFile, jsonStr));
    }

    @TraceNoFilter
    @ApiOperation("NFT 토큰 정보 추가 엑셀 업로드")
    @PostMapping(value = "/excel/add/nft-token-info")
    public ResponseEntity<ApiResultObjectDto> verificationAttendCodeByExcelFile(@RequestPart(value = "excelFile") MultipartFile excelFile,
                                                                                @RequestPart(value = "jsonStr") String jsonStr) {
        //return ResponseEntity.ok(excelLogic.addNftIdListByExcelLogic(excelFile, jsonStr, false));
        return ResponseEntity.ok(excelLogic.addNftIdListByExcelLogicNew(excelFile, jsonStr, false));
    }
}
