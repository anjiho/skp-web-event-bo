package kr.co.syrup.adreport.framework.exception;

/**
 * Created by jino on 2017. 2. 8..
 */
public class NullValueException extends BaseException {
    private static final long serialVersionUID = -6395238743361910743L;

    public NullValueException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public NullValueException(String message) {
        this(message, null);
    }

    public NullValueException(String message, Throwable cause) {
        super(message, cause);
    }

}
