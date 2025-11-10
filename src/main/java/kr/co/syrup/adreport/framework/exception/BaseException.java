package kr.co.syrup.adreport.framework.exception;

import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = -800841598125361452L;

    private ResultCodeEnum resultCode;
    private String resultMessage;
    private Object errorData;

    public BaseException() {
        super();
        this.resultMessage = ResultCodeEnum.SYSTEM_ERROR.getDesc();
        this.resultCode = ResultCodeEnum.SYSTEM_ERROR;
    }

    public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.resultMessage = message;
        this.resultCode = ResultCodeEnum.SYSTEM_ERROR;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.resultMessage = message;
        this.resultCode = ResultCodeEnum.SYSTEM_ERROR;
    }

    public BaseException(String message) {
        super(message);
        this.resultMessage = message;
        this.resultCode = ResultCodeEnum.SYSTEM_ERROR;
    }

    public BaseException(Throwable cause) {
        super(cause);
        this.resultCode = ResultCodeEnum.SYSTEM_ERROR;
    }

    public BaseException(String message, ResultCodeEnum errorCode){
        super(message);

        this.resultCode = errorCode;
        this.resultMessage = message;

    }

    public BaseException(String message, ResultCodeEnum errorCode, Object errorData){
        super(message);

        this.resultCode = errorCode;
        this.resultMessage = message;
        this.errorData = errorData;
    }
}
