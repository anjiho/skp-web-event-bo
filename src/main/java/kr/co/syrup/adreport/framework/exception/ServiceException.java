package kr.co.syrup.adreport.framework.exception;

import kr.co.syrup.adreport.framework.common.ResultCodeEnum;

public class ServiceException extends BaseException {

    /**
     *
     */
    private static final long serialVersionUID = 6715612739533906789L;

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message, ResultCodeEnum.SYSTEM_ERROR);
    }

    public ServiceException(String message,ResultCodeEnum errorCode) {
        super(message, errorCode);
    }

//    public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
//        super(message, cause, enableSuppression, writableStackTrace);
//    }

//    public ServiceException(String message, Throwable cause) {
//        super(message, cause);
//    }

//    public ServiceException(String message) {
//        super(message);
//    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(ResultCodeEnum errorCode){super(errorCode.getDesc(), errorCode);}

    public ServiceException(String message, ResultCodeEnum errorCode, Object errorData){super(message, errorCode, errorData);}

    public ServiceException(ResultCodeEnum errorCode, Object errorData){super(errorCode.getDesc(), errorCode,errorData);}

}
