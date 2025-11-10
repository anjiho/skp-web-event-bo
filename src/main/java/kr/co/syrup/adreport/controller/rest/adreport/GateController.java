package kr.co.syrup.adreport.controller.rest.adreport;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.model.adreport.AdreportResponseWrapDto;
import kr.co.syrup.adreport.model.adreport.AdvertiserGateListReq;
import kr.co.syrup.adreport.service.adreport.AdvertiserReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/api/gate")
@Api(value="gate", description="gate 목록 조회")
public class GateController {

    @Autowired
    AdvertiserReportService advertiserReportService;

    @ApiOperation(value = "광고주 리포팅 > GATE 목록 조회", notes = "MMI : , API ID :  , Source Info : GateController.gateList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mdn", required = true, dataType = "String", paramType = "query", value = "광고주 MDN"),
            @ApiImplicitParam(name = "page", required = true, dataType = "String", paramType = "query", value = "페이지번호"),
            @ApiImplicitParam(name = "size", required = true, dataType = "int", paramType = "query", value = "조회 크기")
    })
    @RequestMapping(value="/gateList", method={RequestMethod.POST})
    public AdreportResponseWrapDto gateList(@RequestBody AdvertiserGateListReq condition,
                                            @ApiIgnore HttpServletRequest request, //
                                            @ApiIgnore HttpServletResponse response) throws Exception {


        log.debug("광고주 리포팅 > GATE 목록 조회");

        AdreportResponseWrapDto ret = null;

        if (StringTools.isNull2(condition.getMdn()) == true) {
            ret = new AdreportResponseWrapDto();

            ret.setResultCode(ResultCodeEnum.PARAMETER_ERROR_MDNISNOTNULL.getCode());
            ret.setResultMessage(ResultCodeEnum.PARAMETER_ERROR_MDNISNOTNULL.getDesc());

            return ret;
        }

        condition.setMdn(condition.getMdn().trim());
        condition.setMdn(condition.getMdn().replaceAll("-", ""));

        ret = advertiserReportService.getGateContractList(condition);

        return ret;
    }
}
