package kr.co.syrup.adreport.controller.rest.web.event;

import io.swagger.annotations.ApiOperation;
import kr.co.syrup.adreport.framework.common.annotation.SetSodarMemberSession;
import kr.co.syrup.adreport.framework.utils.GsonUtils;
import kr.co.syrup.adreport.framework.utils.ModelMapperUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.logic.EventLawLogic;
import kr.co.syrup.adreport.web.event.mybatis.vo.EventLawInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(value = "*")
@RestController
@RequestMapping(value = "/api/v1/web-event-law")
public class WebEventLawController {

    @Autowired
    private EventLawLogic eventLawLogic;

    @SetSodarMemberSession
    @GetMapping(value = "/law-type/{lawType}")
    @ApiOperation("법적문구 리스트 가져오기 (전체 : ALL, 개인정보 취급방침 : TERMS, 서비스이용약관 : PRIVACY)")
    public ResponseEntity<ApiResultObjectDto> getEventLawInfoList(@PathVariable("lawType") String lawType) {
        return ResponseEntity.ok(eventLawLogic.getEventLawInfoListLogic(lawType));
    }

    @SetSodarMemberSession
    @PostMapping(value = "/save")
    @ApiOperation("법적문구 저장")
    public ResponseEntity<ApiResultObjectDto> saveEventLawInfo(@RequestBody EventLawInfoVO reqVO) {
        return ResponseEntity.ok(eventLawLogic.saveEventLawInfoLogic(reqVO.getLawType(), reqVO.getContents(), reqVO.getStartDate(), reqVO.getEndDate()));
    }

    @SetSodarMemberSession
    @PostMapping(value = "/update")
    @ApiOperation("법적문구 수정")
    public ResponseEntity<ApiResultObjectDto> updateEventLawInfo(@RequestBody EventLawInfoVO reqVO) {
        return ResponseEntity.ok(eventLawLogic.updateEventLawInfoLogic(reqVO.getIdx(), reqVO.getLawType(), reqVO.getContents(), reqVO.getStartDate(), reqVO.getEndDate()));
    }

    @SetSodarMemberSession
    @PostMapping(value = "/delete")
    @ApiOperation("법적문구 삭제")
    public ResponseEntity<ApiResultObjectDto> deleteEventLawInfo(@RequestBody String jsonStr) {
        Integer idx = GsonUtils.parseIntJsonStr(jsonStr, "idx");
        return ResponseEntity.ok(eventLawLogic.deleteEventLawInfoLogic(idx));
    }
}
