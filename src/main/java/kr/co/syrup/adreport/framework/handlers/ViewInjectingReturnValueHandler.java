package kr.co.syrup.adreport.framework.handlers;

import kr.co.syrup.adreport.framework.common.BaseWrapResponse;
import kr.co.syrup.adreport.framework.common.CommonConstant;
import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.common.ResultValueResponse;
import kr.co.syrup.adreport.framework.common.annotation.ResponseWrapExclude;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Created by jino on 2017. 1. 25..
 */
public class ViewInjectingReturnValueHandler implements HandlerMethodReturnValueHandler {

    private final HandlerMethodReturnValueHandler delegate;

    public ViewInjectingReturnValueHandler(HandlerMethodReturnValueHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return delegate.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue,
                                  MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {

        if (!returnType.hasMethodAnnotation(ResponseWrapExclude.class)) {
            returnValue = wrapResult(returnValue, returnType, mavContainer);
        }

        delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }

    private Object wrapResult(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer) {
        BaseWrapResponse response = new BaseWrapResponse();

        response.setResultCode(ResultCodeEnum.SUCCESS_OK.getCode());
        response.setResultMessage(CommonConstant.SUCCESS);
        /**
         * 리턴 벨류가 Boolean,String,Integer,Long,Float 경우
         * ResultValueResponse 사용하여 "result"로 출력되도록 함
         */
        if(returnValue instanceof Boolean || returnValue instanceof String
                || returnValue instanceof Integer || returnValue instanceof Long || returnValue instanceof Float){
            response.setData(new ResultValueResponse(returnValue));
        }else {
            response.setData(returnValue);
        }
        return response;
    }
}
