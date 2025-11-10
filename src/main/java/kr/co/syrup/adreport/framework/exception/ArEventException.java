package kr.co.syrup.adreport.framework.exception;

import kr.co.syrup.adreport.web.event.define.ErrorCodeDefine;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper=false)
public class ArEventException extends RuntimeException {

    private static final long serialVersionUID = -1656188798522503941L;
    private ErrorCodeDefine resultCode;
    private String resultMessage;
    private Object errorData;

    public ArEventException() {
        super();
        this.resultMessage = ErrorCodeDefine.SYSTEM_ERROR.msg();
        this.resultCode = ErrorCodeDefine.SYSTEM_ERROR;
    }

    public ArEventException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.resultMessage = message;
        this.resultCode = ErrorCodeDefine.SYSTEM_ERROR;
    }

    public ArEventException(String message, Throwable cause) {
        super(message, cause);
        this.resultMessage = message;
        this.resultCode = ErrorCodeDefine.SYSTEM_ERROR;
    }

    public ArEventException(String message) {
        super(message);
        this.resultMessage = message;
        this.resultCode = ErrorCodeDefine.SYSTEM_ERROR;
    }

    public ArEventException(Throwable cause) {
        super(cause);
        this.resultCode = ErrorCodeDefine.SYSTEM_ERROR;
    }

    public ArEventException(String message, ErrorCodeDefine errorCode){
        super(message);

        this.resultCode = errorCode;
        this.resultMessage = message;

    }

    public ArEventException(String message, ErrorCodeDefine errorCode, Object errorData){
        super(message);

        this.resultCode = errorCode;
        this.resultMessage = message;
        this.errorData = errorData;
    }
}
