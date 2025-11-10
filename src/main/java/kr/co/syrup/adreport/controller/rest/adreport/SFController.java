package kr.co.syrup.adreport.controller.rest.adreport;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.common.annotation.RequiredIpPermission;
import kr.co.syrup.adreport.framework.common.annotation.SetSodarMemberSession;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.model.adreport.*;
import kr.co.syrup.adreport.service.adreport.AdvertiserReportService;
import kr.co.syrup.adreport.service.adreport.SyrupFriendsReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 */
@Slf4j
@RestController
@RequestMapping("/api/sf")
@Api(value="sf", description="시럽프렌즈 조회")
public class SFController {
    @Autowired
    SyrupFriendsReportService syrupFriendsReportService;

    @Autowired
    AdvertiserReportService advertiserReportService;

    /**
     * 광고주 리포팅 > 리포트 목록 조회 (SF)
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "광고주 리포팅 > 리포트 조회 (SF)", notes = "MMI : , API ID : ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mid", required = true, dataType = "String", paramType = "query", value = "광고주 MID"),
            @ApiImplicitParam(name = "reportDate", required = true, dataType = "String", paramType = "query", value = "조회 기준월 - yyyyMM")
    })
    @RequestMapping(value = "/report", method = { RequestMethod.POST })
    public AdreportResponseWrapDto report4SF(@RequestBody @ApiIgnore SFReportReq condition,
                                             @ApiIgnore HttpServletRequest request, //
                                             @ApiIgnore HttpServletResponse response) throws Exception {

        log.debug("광고주 리포팅 > 리포트 조회 (SF)");

        AdreportResponseWrapDto ret = new AdreportResponseWrapDto();

        if (StringTools.isNull2(condition.getMid()) == true) {
            ret.setResultCode(ResultCodeEnum.PARAMETER_ERROR_MIDISNOTNULL.getCode());
            ret.setResultMessage(ResultCodeEnum.PARAMETER_ERROR_MIDISNOTNULL.getDesc());
            return ret;
        }

        if (StringTools.isNull2(condition.getReportDate()) == true) {
            ret.setResultCode(ResultCodeEnum.PARAMETER_ERROR_REPORTDATEISNOTNULL.getCode());
            ret.setResultMessage(ResultCodeEnum.PARAMETER_ERROR_REPORTDATEISNOTNULL.getDesc());
            return ret;
        }

        SFReportDto data = syrupFriendsReportService.getReport4SF(condition);
        ret.setData(data);

        ret.setResultCode(ResultCodeEnum.SUCCESS_OK.getCode());
        ret.setResultMessage(ResultCodeEnum.SUCCESS_OK.getDesc());

        try {
            AdreportReceiveCheckUpdateReq adreportReceiveCheckUpdateReq = new AdreportReceiveCheckUpdateReq();
            adreportReceiveCheckUpdateReq.setChannelType("SF");
            adreportReceiveCheckUpdateReq.setAdvertiserMid(condition.getMid());
            adreportReceiveCheckUpdateReq.setSfSearchMonth(condition.getReportDate());
            adreportReceiveCheckUpdateReq.setReceiveCheckStatus("1");

            advertiserReportService.receiveCheckUpdate(adreportReceiveCheckUpdateReq);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return ret;
    }

    /**
     * 광고주 리포팅 > 리포트 목록 조회 (SF)
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "광고주 리포팅 > 리포트 조회 > 쿠폰더보기", notes = "MMI : , API ID :  ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mid", required = true, dataType = "String", paramType = "query", value = "광고주 mid")
            , @ApiImplicitParam(name = "reportDate", required = true, dataType = "String", paramType = "query", value = "조회 기준월 yyyyMM")
            , @ApiImplicitParam(name = "page", required = true, dataType = "String", paramType = "query", value = "page 번")
            , @ApiImplicitParam(name = "size", required = true, dataType = "String", paramType = "query", value = "page당 사이즈")
    })
    @RequestMapping(value = "/couponMore", method = { RequestMethod.POST })
    public AdreportResponseWrapDto couponMore(@RequestBody @ApiIgnore SFReportReq condition,
                                             @ApiIgnore HttpServletRequest request, //
                                             @ApiIgnore HttpServletResponse response) throws Exception {

        log.debug("광고주 리포팅 > 리포트 조회 > 쿠폰더보기");

        AdreportResponseWrapDto ret = new AdreportResponseWrapDto();

        if (StringTools.isNull2(condition.getMid()) == true) {
            ret.setResultCode(ResultCodeEnum.PARAMETER_ERROR_MIDISNOTNULL.getCode());
            ret.setResultMessage(ResultCodeEnum.PARAMETER_ERROR_MIDISNOTNULL.getDesc());
            return ret;
        }

        if (StringTools.isNull2(condition.getReportDate()) == true) {
            ret.setResultCode(ResultCodeEnum.PARAMETER_ERROR_REPORTDATEISNOTNULL.getCode());
            ret.setResultMessage(ResultCodeEnum.PARAMETER_ERROR_REPORTDATEISNOTNULL.getDesc());
            return ret;
        }

        SFReportCouponInfoDto data = syrupFriendsReportService.couponMore(condition);
        ret.setData(data);

        ret.setResultCode(ResultCodeEnum.SUCCESS_OK.getCode());
        ret.setResultMessage(ResultCodeEnum.SUCCESS_OK.getDesc());

        return ret;
    }

    /**
     * 광고주 리포팅 > 리포트 목록 조회 (SF)
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "광고주 리포팅 > 시럽프렌즈 SMS 발송대상 조회", notes = "MMI : , API ID :  ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reportDate", required = true, dataType = "String", paramType = "query", value = "조회기준월 yyyyMM")
    })
    //@RequiredIpPermission
    @SetSodarMemberSession
    @RequestMapping(value = "/getSendTargetList", method = { RequestMethod.POST })
    public AdreportResponseWrapDto getSendTargetList(@RequestBody SFReportReq condition,
                                                     @ApiIgnore HttpServletRequest request, //
                                                     @ApiIgnore HttpServletResponse response) throws Exception {

        log.debug("광고주 리포팅 > 시럽프렌즈 SMS 발송대상 조회");

        AdreportResponseWrapDto ret = new AdreportResponseWrapDto();

        if (StringTools.isNull2(condition.getReportDate()) == true) {
            ret.setResultCode(ResultCodeEnum.PARAMETER_ERROR_REPORTDATEISNOTNULL.getCode());
            ret.setResultMessage(ResultCodeEnum.PARAMETER_ERROR_REPORTDATEISNOTNULL.getDesc());
            return ret;
        }

        List data = syrupFriendsReportService.getSendTargetList(condition);
        ret.setData(data);

        ret.setResultCode(ResultCodeEnum.SUCCESS_OK.getCode());
        ret.setResultMessage(ResultCodeEnum.SUCCESS_OK.getDesc());

        return ret;
    }

}
