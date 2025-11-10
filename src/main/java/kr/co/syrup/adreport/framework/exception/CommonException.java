package kr.co.syrup.adreport.framework.exception;

import kr.co.syrup.adreport.framework.common.ResultCodeEnum;

public class CommonException extends BaseException {
    public CommonException() {
        super();
    }

    public CommonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonException(String message) {
        super(message);
    }

    public CommonException(Throwable cause) {
        super(cause);
    }

    public CommonException(String message, ResultCodeEnum errorCode){
        super(message);
    }

    public CommonException(String message, ResultCodeEnum errorCode, Object errorData){
        super(message);
    }
}
