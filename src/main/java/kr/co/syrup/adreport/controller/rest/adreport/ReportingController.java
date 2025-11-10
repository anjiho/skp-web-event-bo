package kr.co.syrup.adreport.controller.rest.adreport;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.framework.utils.XSSUtils;
import kr.co.syrup.adreport.model.adreport.*;
import kr.co.syrup.adreport.service.adreport.AdvertiserReportService;
import kr.co.syrup.adreport.service.adreport.SyrupFriendsReportService;
import kr.co.syrup.adreport.service.adreport.TrendReportAndTargetStatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 */
@Slf4j
@RestController
@RequestMapping("/api/reporting")
@Api(value="retporting", description="광고 통계 정보 조회")
public class ReportingController {
    @Autowired
    AdvertiserReportService advertiserReportService;

    @Autowired
    TrendReportAndTargetStatService trendReportAndTargetStatService;

    @Autowired
    SyrupFriendsReportService syrupFriendsReportService;

    /**
     * 광고주 리포팅 > 리포트 목록 조회
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "광고주 리포팅 > 리포트 목록 조회", notes = "MMI : , API ID :  , Source Info : ReportingController.reportList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mdn", required = true, dataType = "String", paramType = "query", value = "광고주 MDN"),
            @ApiImplicitParam(name = "marketingId", required = true, dataType = "String", paramType = "query", value = "마케팅ID"),
            @ApiImplicitParam(name = "channelType", required = true, dataType = "String", paramType = "query", value = "채널타입 채널 타입 (OP : OCB전단PUSH(0131)-모바일전단 / BLE : BLE광고(0102)-위치광고 / SP : 시럽푸시(0130)-Push광고 / DFP : DFP광고(0129)-배너광고 / SF : Syrup 프렌즈 / AT : 광고 타겟 추출)")
    })
    @RequestMapping(value = "/reportList", method = { RequestMethod.POST })
    public AdreportResponseWrapDto reportList(@RequestBody AdvertiserReportListReq condition,
                                              @ApiIgnore HttpServletRequest request, //
                                              @ApiIgnore HttpServletResponse response) throws Exception {

        log.debug("광고주 리포팅 > 리포트 목록 조회");

        AdreportResponseWrapDto ret = null;

        if (StringTools.isNull2(condition.getMdn()) == true) {
            ret = new AdreportResponseWrapDto();

            ret.setResultCode(ResultCodeEnum.PARAMETER_ERROR_MDNISNOTNULL.getCode());
            ret.setResultMessage(ResultCodeEnum.PARAMETER_ERROR_MDNISNOTNULL.getDesc());

            return ret;
        }

        if (StringTools.isNull2(condition.getMarketingId()) == true) {
            ret = new AdreportResponseWrapDto();

            ret.setResultCode(ResultCodeEnum.PARAMETER_ERROR_MARKETINGIDISNOTNULL.getCode());
            ret.setResultMessage(ResultCodeEnum.PARAMETER_ERROR_MARKETINGIDISNOTNULL.getDesc());

            return ret;
        }

        try {
            ret = advertiserReportService.getReportList(condition);
        } catch (Exception e) {
            ret = new AdreportResponseWrapDto();
            ret.setResultCode(ResultCodeEnum.SYSTEM_ERROR.getCode());
            ret.setResultMessage(ResultCodeEnum.SYSTEM_ERROR.getDesc());
        }

        return ret;
    }

    /**
     * 광고주 리포팅 > 플리킹 목록 조회
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "광고주 리포팅 > 플리킹 목록 조회", notes = "MMI : , API ID :  , Source Info : ReportingController.flickingList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mdn", required = true, dataType = "String", paramType = "query", value = "광고주 MDN")
    })
    @RequestMapping(value = "/flickingList", method = { RequestMethod.POST })
    public AdreportResponseWrapDto flickingList(@RequestBody @ApiIgnore AdvertiserFlickingListReq condition,
                                                  @ApiIgnore HttpServletRequest request, //
                                                  @ApiIgnore HttpServletResponse response) {

        log.debug("광고주 리포팅 > 플리킹 목록 조회");

        AdreportResponseWrapDto ret = null;

        if (StringTools.isNull2(condition.getMdn()) == true) {
            ret = new AdreportResponseWrapDto();

            ret.setResultCode(ResultCodeEnum.PARAMETER_ERROR_MDNISNOTNULL.getCode());
            ret.setResultMessage(ResultCodeEnum.PARAMETER_ERROR_MDNISNOTNULL.getDesc());

            return ret;
        }

        ret = advertiserReportService.getFlickingContractList(condition);

        return ret;
    }

    /**
     * 광고주 리포팅 > 일별 통계 조회
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "광고주 리포팅 > 일별 통계 조회", notes = "MMI : , API ID :  , Source Info : ReportingController.dayStatsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, dataType = "int", paramType = "query", value = "페이지"),
            @ApiImplicitParam(name = "size", required = true, dataType = "int", paramType = "query", value = "크기"),
            @ApiImplicitParam(name = "marketingId", required = true, dataType = "String", paramType = "query", value = "마케팅ID")
    })
    @RequestMapping(value = "/dayStatsList", method = { RequestMethod.POST })
    public AdreportResponseWrapDto dayStatsList(@RequestBody @ApiIgnore AdvertiserDayStatsReq condition,
                                              @ApiIgnore HttpServletRequest request, //
                                              @ApiIgnore HttpServletResponse response) {

        log.debug("광고주 리포팅 > 일별 통계 조회");

        AdreportResponseWrapDto ret = null;

        if (StringTools.isNull2(condition.getMarketingId()) == true) {
            ret = new AdreportResponseWrapDto();

            ret.setResultCode(ResultCodeEnum.PARAMETER_ERROR_MARKETINGIDISNOTNULL.getCode());
            ret.setResultMessage(ResultCodeEnum.PARAMETER_ERROR_MARKETINGIDISNOTNULL.getDesc());

            return ret;
        }

        if (StringTools.isNull2(condition.getMdn()) == true) {
            ret = new AdreportResponseWrapDto();

            ret.setResultCode(ResultCodeEnum.PARAMETER_ERROR_MDNISNOTNULL.getCode());
            ret.setResultMessage(ResultCodeEnum.PARAMETER_ERROR_MDNISNOTNULL.getDesc());

            return ret;
        }

        ret = advertiserReportService.getDayStats(condition);

        return ret;
    }

    /**
     * 광고주 리포팅 > 월별 통계 조회
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "광고주 리포팅 > 월별 통계 조회", notes = "MMI : , API ID :  , Source Info : ReportingController.monthStatsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, dataType = "int", paramType = "query", value = "페이지"),
            @ApiImplicitParam(name = "size", required = true, dataType = "int", paramType = "query", value = "크기"),
            @ApiImplicitParam(name = "marketingId", required = true, dataType = "String", paramType = "query", value = "마케팅ID"),
            @ApiImplicitParam(name = "mdn", required = true, dataType = "String", paramType = "query", value = "광고주 MDN")
    })
    @RequestMapping(value = "/monthStatsList", method = { RequestMethod.POST })
    public AdreportResponseWrapDto monthStatsList(@RequestBody @ApiIgnore AdvertiserMonthStatsReq condition,
                                                  @ApiIgnore HttpServletRequest request, //
                                                  @ApiIgnore HttpServletResponse response) {

        log.debug("광고주 리포팅 > 월별 통계 조회");


        AdreportResponseWrapDto ret = null;

        if (StringTools.isNull2(condition.getMarketingId()) == true) {
            ret = new AdreportResponseWrapDto();

            ret.setResultCode(ResultCodeEnum.PARAMETER_ERROR_MARKETINGIDISNOTNULL.getCode());
            ret.setResultMessage(ResultCodeEnum.PARAMETER_ERROR_MARKETINGIDISNOTNULL.getDesc());

            return ret;
        }

        if (StringTools.isNull2(condition.getMdn()) == true) {
            ret = new AdreportResponseWrapDto();

            ret.setResultCode(ResultCodeEnum.PARAMETER_ERROR_MDNISNOTNULL.getCode());
            ret.setResultMessage(ResultCodeEnum.PARAMETER_ERROR_MDNISNOTNULL.getDesc());

            return ret;
        }

        ret = advertiserReportService.getMonthStats(condition);

        return ret;
    }

    /**
     * 광고주 리포팅 > 타겟팅 리포트 조회
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "광고주 리포팅 > 타겟팅 리포트 조회", notes = "MMI : , API ID :  , Source Info : ReportingController.targetInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mid", required = true, dataType = "String", paramType = "query", value = "광고주 MID")
    })
    @RequestMapping(value = "/targetInfo", method = { RequestMethod.POST })
    public AdreportResponseWrapDto targetInfo(@RequestBody @ApiIgnore TargetReportDetailReq condition,
                                            @ApiIgnore HttpServletRequest request, //
                                            @ApiIgnore HttpServletResponse response) {

        log.debug("광고주 리포팅 > 타겟팅 리포트 조회 ");

        AdreportResponseWrapDto ret = null;

        if (StringTools.isNull2(condition.getMid()) == true) {
            ret = new AdreportResponseWrapDto();

            ret.setResultCode(ResultCodeEnum.PARAMETER_ERROR_MIDISNOTNULL.getCode());
            ret.setResultMessage(ResultCodeEnum.PARAMETER_ERROR_MIDISNOTNULL.getDesc());

            return ret;
        }

        ret = trendReportAndTargetStatService.getTargetStat(condition);

        return ret;
    }

    /**
     * 광고 신청
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "광고주 리포팅 > 광고 신청", notes = "MMI : , API ID :  , Source Info : ReportingController.apply")
    @ResponseBody
    @RequestMapping(value = "/apply", method = { RequestMethod.POST })
    public AdreportResponseWrapDto apply(@Valid @RequestBody AdvertisementApplyReq condition,
                                         @ApiIgnore HttpServletRequest request,
                                         @ApiIgnore HttpServletResponse response) {
        log.debug("광고주 리포팅 > 광고 신청 ");

        AdreportResponseWrapDto res = null;

        if (StringTools.isNull2(condition.getChannelType()) == true) {
            res.setResultCode(ResultCodeEnum.PARAMETER_ERROR.getCode());
            res.setResultMessage("채널 타입을 입력해주세요");
            return res;

        }
        if (StringTools.isNull2(condition.getAdvertiserMid()) == true) {
            res.setResultCode(ResultCodeEnum.PARAMETER_ERROR.getCode());
            res.setResultMessage("광고주 MID를 입력해주세요");
            return res;
        }

        condition.setChannelType(XSSUtils.stripXSS(condition.getChannelType()));
        condition.setAdvertiserMid(XSSUtils.stripXSS(condition.getAdvertiserMid()));
        condition.setContPlcName(XSSUtils.stripXSS(condition.getContPlcName()));

        res = advertiserReportService.advertisementApply(condition);
        AdvertisementApplyRes detailRes = (AdvertisementApplyRes) res.getData();

        if ("200".equals(detailRes.getCode())) {
            res.setResultCode(ResultCodeEnum.SUCCESS_OK.getCode());
            res.setResultMessage(ResultCodeEnum.SUCCESS_OK.getDesc());
        } else {
            log.error("광고 신청 오류: {}", res.getResultMessage());

            res.setResultCode(ResultCodeEnum.AD_APPLY_ERROR.getCode());
            res.setResultMessage(ResultCodeEnum.AD_APPLY_ERROR.getDesc());
        }

        return res;
    }

    /**
     * 솔루션별 이미지 조회
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "광고주 리포팅 > 솔루션별 이미지 조회", notes = "MMI : , API ID :  , Source Info : ReportingController.solutionImgList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "marketingId", required = true, dataType = "String", paramType = "query", value = "마케팅 ID")
    })
    @ResponseBody
    @RequestMapping(value = "/solutionImgList", method = { RequestMethod.POST })
    public AdreportResponseWrapDto solutionImgList(@RequestBody @ApiIgnore AdvertiserSolutionImgListReq condition,
                                                   @ApiIgnore HttpServletRequest request, //
                                                   @ApiIgnore HttpServletResponse response) {

        log.debug("광고주 리포팅 > 솔루션별 이미지 조회");


        AdreportResponseWrapDto ret = null;

        if (StringTools.isNull2(condition.getMarketingId()) == true) {
            ret = new AdreportResponseWrapDto();

            ret.setResultCode(ResultCodeEnum.PARAMETER_ERROR_MARKETINGIDISNOTNULL.getCode());
            ret.setResultMessage(ResultCodeEnum.PARAMETER_ERROR_MARKETINGIDISNOTNULL.getDesc());

            return ret;
        }

        if (StringTools.isNull2(condition.getMdn()) == true) {
            ret = new AdreportResponseWrapDto();

            ret.setResultCode(ResultCodeEnum.PARAMETER_ERROR_MDNISNOTNULL.getCode());
            ret.setResultMessage(ResultCodeEnum.PARAMETER_ERROR_MDNISNOTNULL.getDesc());

            return ret;
        }

        ret = advertiserReportService.getSolutionImageList(condition);

        return ret;
    }

}
