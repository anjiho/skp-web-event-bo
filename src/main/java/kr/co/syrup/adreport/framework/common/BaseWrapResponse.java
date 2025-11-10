package kr.co.syrup.adreport.framework.common;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by jino on 2017. 1. 25..
 */
@Data
public class BaseWrapResponse implements Serializable {
    private static final long serialVersionUID = 4335624804819666230L;

    private String resultMessage;
    private String resultCode;

    private Object data;
}
