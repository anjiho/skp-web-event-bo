package kr.co.syrup.adreport.controller.rest.web.event;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kr.co.syrup.adreport.framework.common.annotation.TraceNoFilter;
import kr.co.syrup.adreport.framework.common.annotation.XssFilter;
import kr.co.syrup.adreport.framework.utils.GsonUtils;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.logic.SkApiLogic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(value = "*")
@Api(value="ar-event", description="웹 이벤트 OCB 관련 컨트롤러")
@RequestMapping(value = "/api/v1/web-event-ocb")
public class WebEventOcbController {

    @Autowired
    private SkApiLogic skApiLogic;

    @GetMapping(value = "/session/find/{partnerToken}")
    @ApiOperation("OCB 사용자 정보 가져오기")
    public ResponseEntity<ApiResultObjectDto> getOcbSessionInfo(@PathVariable(value = "partnerToken") String partnerToken) {
        return ResponseEntity.ok(skApiLogic.getOcbSessionLogic(partnerToken));
    }

    @XssFilter
    @TraceNoFilter
    @PostMapping(value = "/point/save")
    @ApiOperation("OCB 포인트 지급")
    public ResponseEntity<ApiResultObjectDto> requestOcbPointSave(@RequestBody String jsonStr) {
        String eventId         = GsonUtils.parseStringJsonStr(jsonStr, "eventId");
        String partnerToken    = GsonUtils.parseStringJsonStr(jsonStr, "partnerToken");

        return ResponseEntity.ok(skApiLogic.requestOcbPointSaveLogic(eventId, partnerToken));
    }
}
