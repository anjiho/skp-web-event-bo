package kr.co.syrup.adreport.framework.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResultValueResponse implements Serializable {
    private static final long serialVersionUID = -2146778107741031044L;

    public Object result;

    public ResultValueResponse(Object result) {
        this.result = result;
    }
}
