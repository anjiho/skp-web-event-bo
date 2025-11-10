package kr.co.syrup.adreport.framework.controller;

import kr.co.syrup.adreport.framework.common.BaseWrapResponse;
import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.common.annotation.ResponseWrapExclude;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by jino on 2017. 2. 7..
 */
@Controller
public class ErrorController {
    /**
     * web.xml 에서 정의한 404 error 페이지의 Handler.
     * @return
     */
    @RequestMapping("/error/default-exception")
    @ResponseBody
    @ResponseWrapExclude
    public BaseWrapResponse defaultException() {
        BaseWrapResponse res = new BaseWrapResponse();

        res.setResultCode(ResultCodeEnum.SYSTEM_ERROR.getCode());
        res.setResultMessage(ResultCodeEnum.SYSTEM_ERROR.getDesc());

        return res;
    }

    @RequestMapping("/error/pageNotFound")
    @ResponseBody
    @ResponseWrapExclude
    public BaseWrapResponse pageNotFound() {
        BaseWrapResponse res = new BaseWrapResponse();

        res.setResultCode(ResultCodeEnum.PAGE_NOT_FOUND.getCode());
        res.setResultMessage(ResultCodeEnum.PAGE_NOT_FOUND.getDesc());

        return res;
    }

    @RequestMapping("/error/accessDenied")
    @ResponseBody
    @ResponseWrapExclude
    public BaseWrapResponse accessDenied() {
        BaseWrapResponse res = new BaseWrapResponse();

        res.setResultCode(ResultCodeEnum.ACCESS_DENIED.getCode());
        res.setResultMessage(ResultCodeEnum.ACCESS_DENIED.getDesc());

        return res;
    }
}
